package com.example.ondc.model;

import com.google.gson.annotations.SerializedName;

public class OrderItemRequest {
    @SerializedName("productId")
    private Long productId;
    @SerializedName("requestedQty")
    private Integer requestedQty;

    public OrderItemRequest(Long productId, Integer requestedQty) {
        this.productId = productId;
        this.requestedQty = requestedQty;
    }

    public Long getProductId() { return productId; }
    public Integer getRequestedQty() { return requestedQty; }
}
