package com.example.ondc.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class Order {
    @SerializedName("id")
    private Long id;
    @SerializedName("ondcOrderId")
    private String ondcOrderId;
    @SerializedName("status")
    private String status;
    @SerializedName("priority")
    private String priority;
    @SerializedName("fulfillmentType")
    private String fulfillmentType;
    @SerializedName("totalAmount")
    private Double totalAmount;
    @SerializedName("customerName")
    private String customerName;
    @SerializedName("customerPhone")
    private String customerPhone;
    @SerializedName("deliveryAddress")
    private String deliveryAddress;
    @SerializedName("deliveryPincode")
    private String deliveryPincode;
    @SerializedName("vendorId")
    private Long vendorId;
    @SerializedName("vendorName")
    private String vendorName;
    @SerializedName("outletId")
    private Long outletId;
    @SerializedName("outletName")
    private String outletName;
    @SerializedName("sellerAppId")
    private Long sellerAppId;
    @SerializedName("sellerAppName")
    private String sellerAppName;
    @SerializedName("rejectionReason")
    private String rejectionReason;
    @SerializedName("items")
    private List<OrderItem> items;
    @SerializedName("createdAt")
    private String createdAt;
    @SerializedName("acceptedAt")
    private String acceptedAt;
    @SerializedName("fulfilledAt")
    private String fulfilledAt;

    public Long getId() { return id; }
    public String getOndcOrderId() { return ondcOrderId; }
    public String getStatus() { return status; }
    public String getPriority() { return priority; }
    public String getFulfillmentType() { return fulfillmentType; }
    public Double getTotalAmount() { return totalAmount; }
    public String getCustomerName() { return customerName; }
    public String getCustomerPhone() { return customerPhone; }
    public String getDeliveryAddress() { return deliveryAddress; }
    public String getDeliveryPincode() { return deliveryPincode; }
    public Long getVendorId() { return vendorId; }
    public String getVendorName() { return vendorName; }
    public Long getOutletId() { return outletId; }
    public String getOutletName() { return outletName; }
    public Long getSellerAppId() { return sellerAppId; }
    public String getSellerAppName() { return sellerAppName; }
    public String getRejectionReason() { return rejectionReason; }
    public List<OrderItem> getItems() { return items; }
    public String getCreatedAt() { return createdAt; }
    public String getAcceptedAt() { return acceptedAt; }
    public String getFulfilledAt() { return fulfilledAt; }

    /**
     * Returns a display-friendly status text.
     */
    public String getDisplayStatus() {
        if (status == null) return "Unknown";
        switch (status) {
            case "PENDING": return "Pending";
            case "ACCEPTED": return "Accepted";
            case "REJECTED": return "Rejected";
            case "PARTIALLY_FULFILLED": return "Partial";
            case "FULFILLED": return "Fulfilled";
            case "CANCELLED": return "Cancelled";
            default: return status;
        }
    }

    /**
     * Returns items summary string like "3 items"
     */
    public String getItemsSummary() {
        if (items == null || items.isEmpty()) return "No items";
        int count = items.size();
        return count + (count == 1 ? " item" : " items");
    }
}
