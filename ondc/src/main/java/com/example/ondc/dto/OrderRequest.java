package com.example.ondc.dto;

import com.example.ondc.enums.OrderPriority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.util.List;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class OrderRequest {
    @NotBlank(message = "ONDC Order ID is required")
    private String ondcOrderId;

    @NotNull(message = "Vendor ID is required")
    private Long vendorId;

    private Long outletId;
    private Long sellerAppId;

    private OrderPriority priority;

    private String customerName;
    private String customerPhone;
    private String deliveryAddress;
    private String deliveryPincode;

    @NotEmpty(message = "Order must have at least one item")
    private List<OrderItemRequest> items;
}
