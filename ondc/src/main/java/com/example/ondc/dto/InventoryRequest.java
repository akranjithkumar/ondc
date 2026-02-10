package com.example.ondc.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class InventoryRequest {
    @NotNull(message = "Product ID is required")
    private Long productId;

    @NotNull(message = "Outlet ID is required")
    private Long outletId;

    @NotNull(message = "Total stock is required")
    @PositiveOrZero(message = "Total stock cannot be negative")
    private Integer totalStock;

    private Integer reorderLevel;
}
