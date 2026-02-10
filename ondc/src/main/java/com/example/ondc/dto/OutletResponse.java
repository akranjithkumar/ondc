package com.example.ondc.dto;

import com.example.ondc.enums.OutletType;
import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class OutletResponse {
    private Long id;
    private String name;
    private OutletType type;
    private String address;
    private Double latitude;
    private Double longitude;
    private String pincode;
    private Boolean isActive;
    private Integer currentLoad;
    private Integer maxCapacity;
    private Long vendorId;
    private String vendorName;
    private LocalDateTime createdAt;
}
