package com.example.ondc.service;

import com.example.ondc.dto.*;
import com.example.ondc.entity.*;
import com.example.ondc.enums.*;
import com.example.ondc.exception.*;
import com.example.ondc.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final VendorService vendorService;
    private final OutletService outletService;
    private final SellerAppService sellerAppService;
    private final ProductService productService;
    private final InventoryService inventoryService;

    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public OrderResponse getOrderById(Long id) {
        return toResponse(findOrderById(id));
    }

    public List<OrderResponse> getOrdersByVendor(Long vendorId) {
        return orderRepository.findByVendorId(vendorId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get orders prioritized by the decision engine.
     * CRITICAL > HIGH > MEDIUM > LOW, then by creation time (oldest first).
     */
    public List<OrderResponse> getPrioritizedOrders(Long vendorId) {
        return orderRepository.findByVendorIdOrderByPriority(vendorId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<OrderResponse> getOrdersByStatus(Long vendorId, OrderStatus status) {
        return orderRepository.findByVendorIdAndStatus(vendorId, status).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Create a new order — auto-assigns optimal outlet if not specified.
     */
    @Transactional
    public OrderResponse createOrder(OrderRequest request) {
        Vendor vendor = vendorService.findVendorById(request.getVendorId());

        // Auto-route to optimal outlet if not specified
        Outlet outlet = null;
        if (request.getOutletId() != null) {
            outlet = outletService.findOutletById(request.getOutletId());
        } else if (request.getDeliveryPincode() != null) {
            OutletResponse optimal = outletService.findOptimalOutlet(
                    request.getVendorId(), request.getDeliveryPincode());
            outlet = outletService.findOutletById(optimal.getId());
        }

        SellerApp sellerApp = null;
        if (request.getSellerAppId() != null) {
            sellerApp = sellerAppService.findSellerAppById(request.getSellerAppId());
        }

        // Build order
        Order order = Order.builder()
                .ondcOrderId(request.getOndcOrderId())
                .vendor(vendor)
                .outlet(outlet)
                .sellerApp(sellerApp)
                .priority(request.getPriority() != null ? request.getPriority() : OrderPriority.MEDIUM)
                .customerName(request.getCustomerName())
                .customerPhone(request.getCustomerPhone())
                .deliveryAddress(request.getDeliveryAddress())
                .deliveryPincode(request.getDeliveryPincode())
                .totalAmount(0.0)
                .build();

        // Add order items and calculate total
        double total = 0.0;
        for (OrderItemRequest itemReq : request.getItems()) {
            Product product = productService.findProductById(itemReq.getProductId());
            OrderItem item = OrderItem.builder()
                    .order(order)
                    .product(product)
                    .requestedQty(itemReq.getRequestedQty())
                    .unitPrice(product.getPrice())
                    .build();
            order.getItems().add(item);
            total += product.getPrice() * itemReq.getRequestedQty();
        }
        order.setTotalAmount(total);

        return toResponse(orderRepository.save(order));
    }

    /**
     * Accept an order — reserves inventory across all items.
     */
    @Transactional
    public OrderResponse acceptOrder(Long orderId) {
        Order order = findOrderById(orderId);
        validateOrderStatus(order, OrderStatus.PENDING, "accept");

        if (order.getOutlet() == null) {
            throw new InvalidOperationException("Order must have an outlet assigned before acceptance");
        }

        // Reserve inventory for all items
        for (OrderItem item : order.getItems()) {
            inventoryService.reserveStock(
                    item.getProduct().getId(),
                    order.getOutlet().getId(),
                    item.getRequestedQty());
            item.setFulfilledQty(item.getRequestedQty());
        }

        order.setStatus(OrderStatus.ACCEPTED);
        order.setFulfillmentType(FulfillmentType.FULL);
        order.setAcceptedAt(LocalDateTime.now());
        outletService.incrementLoad(order.getOutlet().getId());
        vendorService.updateVendorStats(order.getVendor().getId(), true);

        return toResponse(orderRepository.save(order));
    }

    /**
     * Reject an order with reason.
     */
    @Transactional
    public OrderResponse rejectOrder(Long orderId, String reason) {
        Order order = findOrderById(orderId);
        validateOrderStatus(order, OrderStatus.PENDING, "reject");

        order.setStatus(OrderStatus.REJECTED);
        order.setRejectionReason(reason);
        vendorService.updateVendorStats(order.getVendor().getId(), false);

        return toResponse(orderRepository.save(order));
    }

    /**
     * Partially fulfill an order — vendor decides which items and quantities to fulfill.
     */
    @Transactional
    public OrderResponse partialFulfill(Long orderId, PartialFulfillmentRequest request) {
        Order order = findOrderById(orderId);
        validateOrderStatus(order, OrderStatus.PENDING, "partially fulfill");

        if (order.getOutlet() == null) {
            throw new InvalidOperationException("Order must have an outlet assigned");
        }

        Map<Long, Integer> fulfillments = request.getItemFulfillments();

        for (OrderItem item : order.getItems()) {
            Integer fulfillQty = fulfillments.get(item.getProduct().getId());
            if (fulfillQty != null && fulfillQty > 0) {
                int qty = Math.min(fulfillQty, item.getRequestedQty());
                inventoryService.reserveStock(
                        item.getProduct().getId(),
                        order.getOutlet().getId(),
                        qty);
                item.setFulfilledQty(qty);
            }
        }

        order.setStatus(OrderStatus.PARTIALLY_FULFILLED);
        order.setFulfillmentType(FulfillmentType.PARTIAL);
        order.setAcceptedAt(LocalDateTime.now());

        // Recalculate total based on fulfilled quantities
        double fulfilledTotal = order.getItems().stream()
                .mapToDouble(item -> item.getFulfilledQty() * item.getUnitPrice())
                .sum();
        order.setTotalAmount(fulfilledTotal);

        outletService.incrementLoad(order.getOutlet().getId());
        vendorService.updateVendorStats(order.getVendor().getId(), true);

        return toResponse(orderRepository.save(order));
    }

    private Order findOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
    }

    private void validateOrderStatus(Order order, OrderStatus expected, String action) {
        if (order.getStatus() != expected) {
            throw new InvalidOperationException(
                    "Cannot " + action + " order. Current status: " + order.getStatus() +
                    ", Expected: " + expected);
        }
    }

    private OrderResponse toResponse(Order order) {
        List<OrderItemResponse> itemResponses = order.getItems().stream()
                .map(item -> OrderItemResponse.builder()
                        .id(item.getId())
                        .productId(item.getProduct().getId())
                        .productName(item.getProduct().getName())
                        .productSku(item.getProduct().getSku())
                        .requestedQty(item.getRequestedQty())
                        .fulfilledQty(item.getFulfilledQty())
                        .unitPrice(item.getUnitPrice())
                        .lineTotal(item.getLineTotal())
                        .build())
                .collect(Collectors.toList());

        return OrderResponse.builder()
                .id(order.getId())
                .ondcOrderId(order.getOndcOrderId())
                .status(order.getStatus())
                .priority(order.getPriority())
                .fulfillmentType(order.getFulfillmentType())
                .totalAmount(order.getTotalAmount())
                .customerName(order.getCustomerName())
                .customerPhone(order.getCustomerPhone())
                .deliveryAddress(order.getDeliveryAddress())
                .deliveryPincode(order.getDeliveryPincode())
                .vendorId(order.getVendor().getId())
                .vendorName(order.getVendor().getName())
                .outletId(order.getOutlet() != null ? order.getOutlet().getId() : null)
                .outletName(order.getOutlet() != null ? order.getOutlet().getName() : null)
                .sellerAppId(order.getSellerApp() != null ? order.getSellerApp().getId() : null)
                .sellerAppName(order.getSellerApp() != null ? order.getSellerApp().getName() : null)
                .rejectionReason(order.getRejectionReason())
                .items(itemResponses)
                .createdAt(order.getCreatedAt())
                .acceptedAt(order.getAcceptedAt())
                .fulfilledAt(order.getFulfilledAt())
                .build();
    }
}
