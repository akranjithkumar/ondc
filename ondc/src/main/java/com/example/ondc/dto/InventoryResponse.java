package com.example.ondc.dto;

import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class InventoryResponse {
    private Long id;
    private Long productId;
    private String productName;
    private String productSku;
    private Long outletId;
    private String outletName;
    private Integer totalStock;
    private Integer reservedStock;
    private Integer availableStock;
    private Integer reorderLevel;
    private Boolean isLowStock;
    private LocalDateTime lastSyncedAt;
}
