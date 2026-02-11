package com.example.ondc.model;

import com.google.gson.annotations.SerializedName;

public class OrderItem {
    @SerializedName("id")
    private Long id;
    @SerializedName("productId")
    private Long productId;
    @SerializedName("productName")
    private String productName;
    @SerializedName("productSku")
    private String productSku;
    @SerializedName("requestedQty")
    private Integer requestedQty;
    @SerializedName("fulfilledQty")
    private Integer fulfilledQty;
    @SerializedName("unitPrice")
    private Double unitPrice;
    @SerializedName("lineTotal")
    private Double lineTotal;

    public Long getId() { return id; }
    public Long getProductId() { return productId; }
    public String getProductName() { return productName; }
    public String getProductSku() { return productSku; }
    public Integer getRequestedQty() { return requestedQty; }
    public Integer getFulfilledQty() { return fulfilledQty; }
    public Double getUnitPrice() { return unitPrice; }
    public Double getLineTotal() { return lineTotal; }
}
