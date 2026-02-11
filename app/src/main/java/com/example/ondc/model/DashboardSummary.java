package com.example.ondc.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class DashboardSummary {
    @SerializedName("vendorId")
    private Long vendorId;
    @SerializedName("vendorName")
    private String vendorName;

    // Order stats
    @SerializedName("totalOrders")
    private Long totalOrders;
    @SerializedName("pendingOrders")
    private Long pendingOrders;
    @SerializedName("acceptedOrders")
    private Long acceptedOrders;
    @SerializedName("fulfilledOrders")
    private Long fulfilledOrders;
    @SerializedName("cancelledOrders")
    private Long cancelledOrders;

    // Inventory stats
    @SerializedName("totalProducts")
    private Integer totalProducts;
    @SerializedName("lowStockItems")
    private Integer lowStockItems;
    @SerializedName("lowStockAlerts")
    private List<InventoryItem> lowStockAlerts;

    // Outlet stats
    @SerializedName("totalOutlets")
    private Integer totalOutlets;
    @SerializedName("activeOutlets")
    private Integer activeOutlets;

    // Seller app stats
    @SerializedName("totalSellerApps")
    private Integer totalSellerApps;
    @SerializedName("healthySellerApps")
    private Integer healthySellerApps;

    // Performance
    @SerializedName("vendorRating")
    private Double vendorRating;
    @SerializedName("fulfillmentRate")
    private Double fulfillmentRate;

    // Getters
    public Long getVendorId() { return vendorId; }
    public String getVendorName() { return vendorName; }
    public Long getTotalOrders() { return totalOrders; }
    public Long getPendingOrders() { return pendingOrders; }
    public Long getAcceptedOrders() { return acceptedOrders; }
    public Long getFulfilledOrders() { return fulfilledOrders; }
    public Long getCancelledOrders() { return cancelledOrders; }
    public Integer getTotalProducts() { return totalProducts; }
    public Integer getLowStockItems() { return lowStockItems; }
    public List<InventoryItem> getLowStockAlerts() { return lowStockAlerts; }
    public Integer getTotalOutlets() { return totalOutlets; }
    public Integer getActiveOutlets() { return activeOutlets; }
    public Integer getTotalSellerApps() { return totalSellerApps; }
    public Integer getHealthySellerApps() { return healthySellerApps; }
    public Double getVendorRating() { return vendorRating; }
    public Double getFulfillmentRate() { return fulfillmentRate; }

    /**
     * Formatted fulfillment rate as percentage string like "85.5%"
     */
    public String getFormattedFulfillmentRate() {
        if (fulfillmentRate == null) return "0%";
        return String.format("%.1f%%", fulfillmentRate);
    }

    /**
     * Formatted vendor rating like "4.5 ★"
     */
    public String getFormattedRating() {
        if (vendorRating == null) return "N/A";
        return String.format("%.1f ★", vendorRating);
    }
}
