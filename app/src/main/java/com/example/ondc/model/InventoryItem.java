package com.example.ondc.model;

import com.google.gson.annotations.SerializedName;

public class InventoryItem {
    @SerializedName("id")
    private Long id;
    @SerializedName("productId")
    private Long productId;
    @SerializedName("productName")
    private String productName;
    @SerializedName("productSku")
    private String productSku;
    @SerializedName("outletId")
    private Long outletId;
    @SerializedName("outletName")
    private String outletName;
    @SerializedName("totalStock")
    private Integer totalStock;
    @SerializedName("reservedStock")
    private Integer reservedStock;
    @SerializedName("availableStock")
    private Integer availableStock;
    @SerializedName("reorderLevel")
    private Integer reorderLevel;
    @SerializedName("isLowStock")
    private Boolean isLowStock;
    @SerializedName("lastSyncedAt")
    private String lastSyncedAt;

    public Long getId() { return id; }
    public Long getProductId() { return productId; }
    public String getProductName() { return productName; }
    public String getProductSku() { return productSku; }
    public Long getOutletId() { return outletId; }
    public String getOutletName() { return outletName; }
    public Integer getTotalStock() { return totalStock; }
    public Integer getReservedStock() { return reservedStock; }
    public Integer getAvailableStock() { return availableStock; }
    public Integer getReorderLevel() { return reorderLevel; }
    public Boolean getIsLowStock() { return isLowStock; }
    public String getLastSyncedAt() { return lastSyncedAt; }

    /**
     * Returns stock level as a percentage of total stock relative to reorder level.
     * Used for progress bar display.
     */
    public int getStockPercentage() {
        if (totalStock == null || totalStock == 0) return 0;
        int available = availableStock != null ? availableStock : 0;
        return Math.min(100, (available * 100) / totalStock);
    }

    /**
     * Returns the health status: 0 = critical, 1 = low, 2 = healthy
     */
    public int getStockHealthLevel() {
        if (availableStock == null || reorderLevel == null) return 2;
        if (availableStock <= reorderLevel / 2) return 0; // critical
        if (availableStock <= reorderLevel) return 1; // low
        return 2; // healthy
    }
}
