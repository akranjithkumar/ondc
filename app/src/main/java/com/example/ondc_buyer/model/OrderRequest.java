package com.example.ondc_buyer.model;

import java.io.Serializable;
import java.util.List;

public class OrderRequest implements Serializable {
    private String ondcOrderId;
    private Long vendorId;
    private Long sellerAppId;
    private String priority; // "HIGH", "MEDIUM", "LOW"
    private String customerName;
    private String customerPhone;
    private String deliveryAddress;
    private String deliveryPincode;
    private List<OrderItemRequest> items;

    // Getters and Setters
    public String getOndcOrderId() { return ondcOrderId; }
    public void setOndcOrderId(String ondcOrderId) { this.ondcOrderId = ondcOrderId; }
    public Long getVendorId() { return vendorId; }
    public void setVendorId(Long vendorId) { this.vendorId = vendorId; }
    public Long getSellerAppId() { return sellerAppId; }
    public void setSellerAppId(Long sellerAppId) { this.sellerAppId = sellerAppId; }
    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public String getCustomerPhone() { return customerPhone; }
    public void setCustomerPhone(String customerPhone) { this.customerPhone = customerPhone; }
    public String getDeliveryAddress() { return deliveryAddress; }
    public void setDeliveryAddress(String deliveryAddress) { this.deliveryAddress = deliveryAddress; }
    public String getDeliveryPincode() { return deliveryPincode; }
    public void setDeliveryPincode(String deliveryPincode) { this.deliveryPincode = deliveryPincode; }
    public List<OrderItemRequest> getItems() { return items; }
    public void setItems(List<OrderItemRequest> items) { this.items = items; }
}
