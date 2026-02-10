package com.example.ondc.dto;

import lombok.*;
import java.util.List;
import java.util.Map;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class PartialFulfillmentRequest {
    /**
     * Map of productId -> fulfilledQty for each order item.
     * Only items listed here will be fulfilled; rest remain unfulfilled.
     */
    private Map<Long, Integer> itemFulfillments;
}
