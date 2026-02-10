package com.example.ondc.controller;

import com.example.ondc.dto.OutletRequest;
import com.example.ondc.dto.OutletResponse;
import com.example.ondc.service.OutletService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/outlets")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class OutletController {

    private final OutletService outletService;

    @GetMapping
    public ResponseEntity<List<OutletResponse>> getAllOutlets() {
        return ResponseEntity.ok(outletService.getAllOutlets());
    }

    @GetMapping("/{id}")
    public ResponseEntity<OutletResponse> getOutletById(@PathVariable Long id) {
        return ResponseEntity.ok(outletService.getOutletById(id));
    }

    @GetMapping("/vendor/{vendorId}")
    public ResponseEntity<List<OutletResponse>> getOutletsByVendor(@PathVariable Long vendorId) {
        return ResponseEntity.ok(outletService.getOutletsByVendor(vendorId));
    }

    @GetMapping("/optimal")
    public ResponseEntity<OutletResponse> findOptimalOutlet(
            @RequestParam Long vendorId, @RequestParam String deliveryPincode) {
        return ResponseEntity.ok(outletService.findOptimalOutlet(vendorId, deliveryPincode));
    }

    @PostMapping
    public ResponseEntity<OutletResponse> createOutlet(@Valid @RequestBody OutletRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(outletService.createOutlet(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<OutletResponse> updateOutlet(
            @PathVariable Long id, @Valid @RequestBody OutletRequest request) {
        return ResponseEntity.ok(outletService.updateOutlet(id, request));
    }
}
