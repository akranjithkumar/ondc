package com.example.ondc.dto;

import com.example.ondc.enums.FulfillmentType;
import com.example.ondc.enums.OrderPriority;
import com.example.ondc.enums.OrderStatus;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class OrderResponse {
    private Long id;
    private String ondcOrderId;
    private OrderStatus status;
    private OrderPriority priority;
    private FulfillmentType fulfillmentType;
    private Double totalAmount;
    private String customerName;
    private String customerPhone;
    private String deliveryAddress;
    private String deliveryPincode;
    private Long vendorId;
    private String vendorName;
    private Long outletId;
    private String outletName;
    private Long sellerAppId;
    private String sellerAppName;
    private String rejectionReason;
    private List<OrderItemResponse> items;
    private LocalDateTime createdAt;
    private LocalDateTime acceptedAt;
    private LocalDateTime fulfilledAt;
}
