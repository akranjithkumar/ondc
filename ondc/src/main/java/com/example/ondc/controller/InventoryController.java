package com.example.ondc.controller;

import com.example.ondc.dto.InventoryRequest;
import com.example.ondc.dto.InventoryResponse;
import com.example.ondc.service.InventoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping("/outlet/{outletId}")
    public ResponseEntity<List<InventoryResponse>> getInventoryByOutlet(@PathVariable Long outletId) {
        return ResponseEntity.ok(inventoryService.getInventoryByOutlet(outletId));
    }

    @GetMapping("/vendor/{vendorId}")
    public ResponseEntity<List<InventoryResponse>> getInventoryByVendor(@PathVariable Long vendorId) {
        return ResponseEntity.ok(inventoryService.getInventoryByVendor(vendorId));
    }

    @GetMapping("/check")
    public ResponseEntity<InventoryResponse> getInventory(
            @RequestParam Long productId, @RequestParam Long outletId) {
        return ResponseEntity.ok(inventoryService.getInventory(productId, outletId));
    }

    @PostMapping
    public ResponseEntity<InventoryResponse> createOrUpdateInventory(
            @Valid @RequestBody InventoryRequest request) {
        return ResponseEntity.ok(inventoryService.createOrUpdateInventory(request));
    }

    @PostMapping("/reserve")
    public ResponseEntity<Map<String, String>> reserveStock(
            @RequestParam Long productId, @RequestParam Long outletId, @RequestParam int quantity) {
        inventoryService.reserveStock(productId, outletId, quantity);
        return ResponseEntity.ok(Map.of("message", "Stock reserved successfully"));
    }

    @PostMapping("/release")
    public ResponseEntity<Map<String, String>> releaseReservation(
            @RequestParam Long productId, @RequestParam Long outletId, @RequestParam int quantity) {
        inventoryService.releaseReservation(productId, outletId, quantity);
        return ResponseEntity.ok(Map.of("message", "Reservation released successfully"));
    }

    @GetMapping("/low-stock")
    public ResponseEntity<List<InventoryResponse>> getLowStockAlerts() {
        return ResponseEntity.ok(inventoryService.getLowStockAlerts());
    }

    @GetMapping("/low-stock/vendor/{vendorId}")
    public ResponseEntity<List<InventoryResponse>> getLowStockByVendor(@PathVariable Long vendorId) {
        return ResponseEntity.ok(inventoryService.getLowStockByVendor(vendorId));
    }

    @PostMapping("/sync/{vendorId}")
    public ResponseEntity<List<InventoryResponse>> syncInventory(@PathVariable Long vendorId) {
        return ResponseEntity.ok(inventoryService.syncInventory(vendorId));
    }
}
