package com.example.ondc.dto;

import lombok.*;
import java.util.List;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class DashboardResponse {
    private Long vendorId;
    private String vendorName;

    // Order stats
    private Long totalOrders;
    private Long pendingOrders;
    private Long acceptedOrders;
    private Long fulfilledOrders;
    private Long cancelledOrders;

    // Inventory stats
    private Integer totalProducts;
    private Integer lowStockItems;
    private List<InventoryResponse> lowStockAlerts;

    // Outlet stats
    private Integer totalOutlets;
    private Integer activeOutlets;

    // Seller app stats
    private Integer totalSellerApps;
    private Integer healthySellerApps;

    // Performance
    private Double vendorRating;
    private Double fulfillmentRate;
}
