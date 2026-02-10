package com.example.ondc.dto;

import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class VendorResponse {
    private Long id;
    private String name;
    private String email;
    private String phone;
    private String businessName;
    private String address;
    private Double rating;
    private Integer totalOrders;
    private Integer successfulOrders;
    private Integer cancelledOrders;
    private Boolean isActive;
    private LocalDateTime createdAt;
}
