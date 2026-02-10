package com.example.ondc.dto;

import com.example.ondc.enums.OutletType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class OutletRequest {
    @NotBlank(message = "Name is required")
    private String name;

    @NotNull(message = "Outlet type is required")
    private OutletType type;

    private String address;
    private Double latitude;
    private Double longitude;

    @NotBlank(message = "Pincode is required")
    private String pincode;

    @NotNull(message = "Vendor ID is required")
    private Long vendorId;

    private Integer maxCapacity;
}
