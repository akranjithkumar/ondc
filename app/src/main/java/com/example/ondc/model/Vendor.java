package com.example.ondc.model;

import com.google.gson.annotations.SerializedName;

public class Vendor {
    @SerializedName("id")
    private Long id;
    @SerializedName("name")
    private String name;
    @SerializedName("email")
    private String email;
    @SerializedName("phone")
    private String phone;
    @SerializedName("businessName")
    private String businessName;
    @SerializedName("address")
    private String address;
    @SerializedName("rating")
    private Double rating;
    @SerializedName("totalOrders")
    private Integer totalOrders;
    @SerializedName("successfulOrders")
    private Integer successfulOrders;
    @SerializedName("cancelledOrders")
    private Integer cancelledOrders;
    @SerializedName("isActive")
    private Boolean isActive;
    @SerializedName("createdAt")
    private String createdAt;

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getBusinessName() { return businessName; }
    public String getAddress() { return address; }
    public Double getRating() { return rating; }
    public Integer getTotalOrders() { return totalOrders; }
    public Integer getSuccessfulOrders() { return successfulOrders; }
    public Integer getCancelledOrders() { return cancelledOrders; }
    public Boolean getIsActive() { return isActive; }
    public String getCreatedAt() { return createdAt; }
}
