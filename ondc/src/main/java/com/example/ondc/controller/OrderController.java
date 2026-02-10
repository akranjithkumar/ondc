package com.example.ondc.controller;

import com.example.ondc.dto.*;
import com.example.ondc.enums.OrderStatus;
import com.example.ondc.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class OrderController {

    private final OrderService orderService;

    @GetMapping
    public ResponseEntity<List<OrderResponse>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getOrderById(id));
    }

    @GetMapping("/vendor/{vendorId}")
    public ResponseEntity<List<OrderResponse>> getOrdersByVendor(@PathVariable Long vendorId) {
        return ResponseEntity.ok(orderService.getOrdersByVendor(vendorId));
    }

    @GetMapping("/vendor/{vendorId}/prioritized")
    public ResponseEntity<List<OrderResponse>> getPrioritizedOrders(@PathVariable Long vendorId) {
        return ResponseEntity.ok(orderService.getPrioritizedOrders(vendorId));
    }

    @GetMapping("/vendor/{vendorId}/status/{status}")
    public ResponseEntity<List<OrderResponse>> getOrdersByStatus(
            @PathVariable Long vendorId, @PathVariable OrderStatus status) {
        return ResponseEntity.ok(orderService.getOrdersByStatus(vendorId, status));
    }

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody OrderRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.createOrder(request));
    }

    @PutMapping("/{id}/accept")
    public ResponseEntity<OrderResponse> acceptOrder(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.acceptOrder(id));
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<OrderResponse> rejectOrder(
            @PathVariable Long id, @RequestBody Map<String, String> body) {
        String reason = body.getOrDefault("reason", "No reason provided");
        return ResponseEntity.ok(orderService.rejectOrder(id, reason));
    }

    @PutMapping("/{id}/partial-fulfill")
    public ResponseEntity<OrderResponse> partialFulfill(
            @PathVariable Long id, @Valid @RequestBody PartialFulfillmentRequest request) {
        return ResponseEntity.ok(orderService.partialFulfill(id, request));
    }
}
