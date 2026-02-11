package com.example.ondc.model;

import com.google.gson.annotations.SerializedName;

public class SellerApp {
    @SerializedName("id")
    private Long id;
    @SerializedName("name")
    private String name;
    @SerializedName("apiEndpoint")
    private String apiEndpoint;
    @SerializedName("status")
    private String status;
    @SerializedName("responseTimeMs")
    private Long responseTimeMs;
    @SerializedName("uptimePercentage")
    private Double uptimePercentage;
    @SerializedName("totalRequests")
    private Integer totalRequests;
    @SerializedName("failedRequests")
    private Integer failedRequests;
    @SerializedName("lastHealthCheck")
    private String lastHealthCheck;
    @SerializedName("createdAt")
    private String createdAt;

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getApiEndpoint() { return apiEndpoint; }
    public String getStatus() { return status; }
    public Long getResponseTimeMs() { return responseTimeMs; }
    public Double getUptimePercentage() { return uptimePercentage; }
    public Integer getTotalRequests() { return totalRequests; }
    public Integer getFailedRequests() { return failedRequests; }
    public String getLastHealthCheck() { return lastHealthCheck; }
    public String getCreatedAt() { return createdAt; }

    public boolean isHealthy() {
        return "HEALTHY".equalsIgnoreCase(status);
    }

    public String getDisplayStatus() {
        if (status == null) return "Unknown";
        switch (status) {
            case "HEALTHY": return "Healthy";
            case "UNHEALTHY": return "Unhealthy";
            case "DEGRADED": return "Degraded";
            default: return status;
        }
    }
}
