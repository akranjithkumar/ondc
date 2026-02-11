package com.example.ondc_buyer.model;

import java.io.Serializable;

public class OrderItemRequest implements Serializable {
    private Long productId;
    private Integer requestedQty;

    public OrderItemRequest() {}

    public OrderItemRequest(Long productId, Integer requestedQty) {
        this.productId = productId;
        this.requestedQty = requestedQty;
    }

    // Getters and Setters
    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }
    public Integer getRequestedQty() { return requestedQty; }
    public void setRequestedQty(Integer requestedQty) { this.requestedQty = requestedQty; }
}
