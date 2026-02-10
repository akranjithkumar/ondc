package com.example.ondc.dto;

import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class OrderItemResponse {
    private Long id;
    private Long productId;
    private String productName;
    private String productSku;
    private Integer requestedQty;
    private Integer fulfilledQty;
    private Double unitPrice;
    private Double lineTotal;
}
