package com.example.ondc_buyer.model;

import java.io.Serializable;

public class OrderItem implements Serializable {
    private Long id;
    private Long productId;
    private String productName;
    private Integer requestedQty;
    private Integer fulfilledQty;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    public Integer getRequestedQty() { return requestedQty; }
    public void setRequestedQty(Integer requestedQty) { this.requestedQty = requestedQty; }
    public Integer getFulfilledQty() { return fulfilledQty; }
    public void setFulfilledQty(Integer fulfilledQty) { this.fulfilledQty = fulfilledQty; }
}
