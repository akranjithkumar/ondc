package com.example.ondc.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class OrderRequest {
    @SerializedName("ondcOrderId")
    private String ondcOrderId;
    @SerializedName("vendorId")
    private Long vendorId;
    @SerializedName("outletId")
    private Long outletId;
    @SerializedName("sellerAppId")
    private Long sellerAppId;
    @SerializedName("priority")
    private String priority;
    @SerializedName("customerName")
    private String customerName;
    @SerializedName("customerPhone")
    private String customerPhone;
    @SerializedName("deliveryAddress")
    private String deliveryAddress;
    @SerializedName("deliveryPincode")
    private String deliveryPincode;
    @SerializedName("items")
    private List<OrderItemRequest> items;

    // Builder-style setters
    public OrderRequest setOndcOrderId(String ondcOrderId) { this.ondcOrderId = ondcOrderId; return this; }
    public OrderRequest setVendorId(Long vendorId) { this.vendorId = vendorId; return this; }
    public OrderRequest setOutletId(Long outletId) { this.outletId = outletId; return this; }
    public OrderRequest setSellerAppId(Long sellerAppId) { this.sellerAppId = sellerAppId; return this; }
    public OrderRequest setPriority(String priority) { this.priority = priority; return this; }
    public OrderRequest setCustomerName(String customerName) { this.customerName = customerName; return this; }
    public OrderRequest setCustomerPhone(String customerPhone) { this.customerPhone = customerPhone; return this; }
    public OrderRequest setDeliveryAddress(String deliveryAddress) { this.deliveryAddress = deliveryAddress; return this; }
    public OrderRequest setDeliveryPincode(String deliveryPincode) { this.deliveryPincode = deliveryPincode; return this; }
    public OrderRequest setItems(List<OrderItemRequest> items) { this.items = items; return this; }
}
