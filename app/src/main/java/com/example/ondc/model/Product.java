package com.example.ondc.model;

import com.google.gson.annotations.SerializedName;

public class Product {
    @SerializedName("id")
    private Long id;
    @SerializedName("name")
    private String name;
    @SerializedName("sku")
    private String sku;
    @SerializedName("category")
    private String category;
    @SerializedName("price")
    private Double price;
    @SerializedName("vendorId")
    private Long vendorId;
    @SerializedName("vendorName")
    private String vendorName;

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getSku() { return sku; }
    public String getCategory() { return category; }
    public Double getPrice() { return price; }
    public Long getVendorId() { return vendorId; }
    public String getVendorName() { return vendorName; }
}
