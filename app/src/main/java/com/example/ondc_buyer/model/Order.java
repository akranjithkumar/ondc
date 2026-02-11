package com.example.ondc_buyer.model;

import java.io.Serializable;
import java.util.List;

public class Order implements Serializable {
    private Long id;
    private String ondcOrderId; // e.g., "ONDC-123"
    private Long vendorId;
    private String vendorName;
    private String priority; // HIGH, MEDIUM, LOW
    private String status;   // PENDING, ACCEPTED, REJECTED, PARTIAL_FULFILLED
    private String reason;   // Rejection reason
    private String customerName;
    private String customerPhone;
    private String deliveryAddress;
    private String deliveryPincode;
    private String createdAt;
    private List<OrderItem> items;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getOndcOrderId() { return ondcOrderId; }
    public void setOndcOrderId(String ondcOrderId) { this.ondcOrderId = ondcOrderId; }
    public Long getVendorId() { return vendorId; }
    public void setVendorId(Long vendorId) { this.vendorId = vendorId; }
    public String getVendorName() { return vendorName; }
    public void setVendorName(String vendorName) { this.vendorName = vendorName; }
    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public String getCustomerPhone() { return customerPhone; }
    public void setCustomerPhone(String customerPhone) { this.customerPhone = customerPhone; }
    public String getDeliveryAddress() { return deliveryAddress; }
    public void setDeliveryAddress(String deliveryAddress) { this.deliveryAddress = deliveryAddress; }
    public String getDeliveryPincode() { return deliveryPincode; }
    public void setDeliveryPincode(String deliveryPincode) { this.deliveryPincode = deliveryPincode; }
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    public List<OrderItem> getItems() { return items; }
    public void setItems(List<OrderItem> items) { this.items = items; }
}
