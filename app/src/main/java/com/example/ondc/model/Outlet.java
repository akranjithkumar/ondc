package com.example.ondc.model;

import com.google.gson.annotations.SerializedName;

public class Outlet {
    @SerializedName("id")
    private Long id;
    @SerializedName("name")
    private String name;
    @SerializedName("type")
    private String type;
    @SerializedName("address")
    private String address;
    @SerializedName("latitude")
    private Double latitude;
    @SerializedName("longitude")
    private Double longitude;
    @SerializedName("pincode")
    private String pincode;
    @SerializedName("isActive")
    private Boolean isActive;
    @SerializedName("currentLoad")
    private Integer currentLoad;
    @SerializedName("maxCapacity")
    private Integer maxCapacity;
    @SerializedName("vendorId")
    private Long vendorId;
    @SerializedName("vendorName")
    private String vendorName;
    @SerializedName("createdAt")
    private String createdAt;

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getType() { return type; }
    public String getAddress() { return address; }
    public Double getLatitude() { return latitude; }
    public Double getLongitude() { return longitude; }
    public String getPincode() { return pincode; }
    public Boolean getIsActive() { return isActive; }
    public Integer getCurrentLoad() { return currentLoad; }
    public Integer getMaxCapacity() { return maxCapacity; }
    public Long getVendorId() { return vendorId; }
    public String getVendorName() { return vendorName; }
    public String getCreatedAt() { return createdAt; }
}
