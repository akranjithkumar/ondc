package com.example.ondc.dto;

import com.example.ondc.enums.SellerAppStatus;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class SellerAppRequest {
    @NotBlank(message = "Seller app name is required")
    private String name;

    @NotBlank(message = "API endpoint is required")
    private String apiEndpoint;

    private String apiKey;
}
