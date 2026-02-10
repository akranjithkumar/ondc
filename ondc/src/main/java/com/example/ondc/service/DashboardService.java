package com.example.ondc.service;

import com.example.ondc.dto.DashboardResponse;
import com.example.ondc.dto.InventoryResponse;
import com.example.ondc.enums.OrderStatus;
import com.example.ondc.enums.SellerAppStatus;
import com.example.ondc.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final VendorService vendorService;
    private final OrderRepository orderRepository;
    private final InventoryService inventoryService;
    private final OutletRepository outletRepository;
    private final SellerAppRepository sellerAppRepository;
    private final ProductRepository productRepository;

    public DashboardResponse getVendorDashboard(Long vendorId) {
        var vendor = vendorService.findVendorById(vendorId);

        Long totalOrders = (long) orderRepository.findByVendorId(vendorId).size();
        Long pendingOrders = orderRepository.countByVendorIdAndStatus(vendorId, OrderStatus.PENDING);
        Long acceptedOrders = orderRepository.countByVendorIdAndStatus(vendorId, OrderStatus.ACCEPTED);
        Long fulfilledOrders = orderRepository.countByVendorIdAndStatus(vendorId, OrderStatus.FULFILLED);
        Long cancelledOrders = orderRepository.countByVendorIdAndStatus(vendorId, OrderStatus.CANCELLED);

        List<InventoryResponse> lowStockAlerts = inventoryService.getLowStockByVendor(vendorId);

        var outlets = outletRepository.findByVendorId(vendorId);
        int activeOutlets = (int) outlets.stream().filter(o -> o.getIsActive()).count();

        var sellerApps = sellerAppRepository.findAll();
        int healthyApps = (int) sellerApps.stream()
                .filter(a -> a.getStatus() == SellerAppStatus.ACTIVE).count();

        int totalProducts = productRepository.findByVendorId(vendorId).size();

        double fulfillmentRate = totalOrders > 0
                ? (double)(acceptedOrders + fulfilledOrders) / totalOrders * 100
                : 0.0;

        return DashboardResponse.builder()
                .vendorId(vendor.getId())
                .vendorName(vendor.getName())
                .totalOrders(totalOrders)
                .pendingOrders(pendingOrders)
                .acceptedOrders(acceptedOrders)
                .fulfilledOrders(fulfilledOrders)
                .cancelledOrders(cancelledOrders)
                .totalProducts(totalProducts)
                .lowStockItems(lowStockAlerts.size())
                .lowStockAlerts(lowStockAlerts)
                .totalOutlets(outlets.size())
                .activeOutlets(activeOutlets)
                .totalSellerApps(sellerApps.size())
                .healthySellerApps(healthyApps)
                .vendorRating(vendor.getRating())
                .fulfillmentRate(Math.round(fulfillmentRate * 100.0) / 100.0)
                .build();
    }
}
