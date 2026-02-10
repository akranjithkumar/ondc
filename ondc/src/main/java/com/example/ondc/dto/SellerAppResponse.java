package com.example.ondc.dto;

import com.example.ondc.enums.SellerAppStatus;
import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class SellerAppResponse {
    private Long id;
    private String name;
    private String apiEndpoint;
    private SellerAppStatus status;
    private Long responseTimeMs;
    private Double uptimePercentage;
    private Integer totalRequests;
    private Integer failedRequests;
    private LocalDateTime lastHealthCheck;
    private LocalDateTime createdAt;
}
