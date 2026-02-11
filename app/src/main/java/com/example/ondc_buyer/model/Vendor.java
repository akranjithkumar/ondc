package com.example.ondc_buyer.model;

import java.io.Serializable;

public class Vendor implements Serializable {
    private Long id;
    private String name;
    private String address;
    private String contactPhone;
    private String contactEmail;
    private Double rating;
    private Boolean active;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getContactPhone() { return contactPhone; }
    public void setContactPhone(String contactPhone) { this.contactPhone = contactPhone; }
    public String getContactEmail() { return contactEmail; }
    public void setContactEmail(String contactEmail) { this.contactEmail = contactEmail; }
    public Double getRating() { return rating; }
    public void setRating(Double rating) { this.rating = rating; }
    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }
}
