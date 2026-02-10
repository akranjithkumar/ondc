package com.example.ondc.service;

import com.example.ondc.dto.InventoryRequest;
import com.example.ondc.dto.InventoryResponse;
import com.example.ondc.entity.Inventory;
import com.example.ondc.entity.Outlet;
import com.example.ondc.entity.Product;
import com.example.ondc.exception.InsufficientStockException;
import com.example.ondc.exception.ResourceNotFoundException;
import com.example.ondc.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final ProductService productService;
    private final OutletService outletService;

    public List<InventoryResponse> getInventoryByOutlet(Long outletId) {
        return inventoryRepository.findByOutletId(outletId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<InventoryResponse> getInventoryByVendor(Long vendorId) {
        return inventoryRepository.findByOutletVendorId(vendorId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public InventoryResponse getInventory(Long productId, Long outletId) {
        Inventory inv = inventoryRepository.findByProductIdAndOutletId(productId, outletId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Inventory not found for product " + productId + " at outlet " + outletId));
        return toResponse(inv);
    }

    @Transactional
    public InventoryResponse createOrUpdateInventory(InventoryRequest request) {
        Product product = productService.findProductById(request.getProductId());
        Outlet outlet = outletService.findOutletById(request.getOutletId());

        Inventory inventory = inventoryRepository
                .findByProductIdAndOutletId(request.getProductId(), request.getOutletId())
                .orElse(Inventory.builder()
                        .product(product)
                        .outlet(outlet)
                        .build());

        inventory.setTotalStock(request.getTotalStock());
        if (request.getReorderLevel() != null) {
            inventory.setReorderLevel(request.getReorderLevel());
        }
        inventory.setLastSyncedAt(LocalDateTime.now());

        return toResponse(inventoryRepository.save(inventory));
    }

    /**
     * Reserve stock for an order — the core anti-overselling mechanism.
     * This is called when an order is accepted.
     */
    @Transactional
    public void reserveStock(Long productId, Long outletId, int quantity) {
        Inventory inventory = inventoryRepository.findByProductIdAndOutletId(productId, outletId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Inventory not found for product " + productId + " at outlet " + outletId));

        if (inventory.getAvailableStock() < quantity) {
            throw new InsufficientStockException(
                    "Insufficient stock for product " + productId +
                    ". Available: " + inventory.getAvailableStock() +
                    ", Requested: " + quantity);
        }

        inventory.setReservedStock(inventory.getReservedStock() + quantity);
        inventoryRepository.save(inventory);
    }

    /**
     * Release reserved stock — called when an order is rejected or cancelled.
     */
    @Transactional
    public void releaseReservation(Long productId, Long outletId, int quantity) {
        Inventory inventory = inventoryRepository.findByProductIdAndOutletId(productId, outletId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Inventory not found for product " + productId + " at outlet " + outletId));

        int newReserved = Math.max(0, inventory.getReservedStock() - quantity);
        inventory.setReservedStock(newReserved);
        inventoryRepository.save(inventory);
    }

    /**
     * Deduct stock after fulfillment — reduces both total and reserved stock.
     */
    @Transactional
    public void deductStock(Long productId, Long outletId, int quantity) {
        Inventory inventory = inventoryRepository.findByProductIdAndOutletId(productId, outletId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Inventory not found for product " + productId + " at outlet " + outletId));

        inventory.setTotalStock(Math.max(0, inventory.getTotalStock() - quantity));
        inventory.setReservedStock(Math.max(0, inventory.getReservedStock() - quantity));
        inventoryRepository.save(inventory);
    }

    /**
     * Get low-stock alerts — predictive replenishment.
     */
    public List<InventoryResponse> getLowStockAlerts() {
        return inventoryRepository.findLowStockInventory().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<InventoryResponse> getLowStockByVendor(Long vendorId) {
        return inventoryRepository.findLowStockByVendorId(vendorId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Sync inventory across all seller apps — broadcasts current stock levels.
     */
    @Transactional
    public List<InventoryResponse> syncInventory(Long vendorId) {
        List<Inventory> inventories = inventoryRepository.findByOutletVendorId(vendorId);
        inventories.forEach(inv -> inv.setLastSyncedAt(LocalDateTime.now()));
        inventoryRepository.saveAll(inventories);
        return inventories.stream().map(this::toResponse).collect(Collectors.toList());
    }

    /**
     * Check if sufficient stock is available for given product at outlet.
     */
    public boolean hasAvailableStock(Long productId, Long outletId, int quantity) {
        return inventoryRepository.findByProductIdAndOutletId(productId, outletId)
                .map(inv -> inv.getAvailableStock() >= quantity)
                .orElse(false);
    }

    private InventoryResponse toResponse(Inventory inventory) {
        return InventoryResponse.builder()
                .id(inventory.getId())
                .productId(inventory.getProduct().getId())
                .productName(inventory.getProduct().getName())
                .productSku(inventory.getProduct().getSku())
                .outletId(inventory.getOutlet().getId())
                .outletName(inventory.getOutlet().getName())
                .totalStock(inventory.getTotalStock())
                .reservedStock(inventory.getReservedStock())
                .availableStock(inventory.getAvailableStock())
                .reorderLevel(inventory.getReorderLevel())
                .isLowStock(inventory.isLowStock())
                .lastSyncedAt(inventory.getLastSyncedAt())
                .build();
    }
}
