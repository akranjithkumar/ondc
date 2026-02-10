package com.example.ondc.dto;

import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class ProductResponse {
    private Long id;
    private String name;
    private String sku;
    private String category;
    private String description;
    private Double price;
    private String unit;
    private Boolean isActive;
    private Long vendorId;
    private LocalDateTime createdAt;
}
