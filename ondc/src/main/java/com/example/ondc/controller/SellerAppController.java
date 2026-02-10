package com.example.ondc.controller;

import com.example.ondc.dto.SellerAppRequest;
import com.example.ondc.dto.SellerAppResponse;
import com.example.ondc.service.SellerAppService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/seller-apps")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class SellerAppController {

    private final SellerAppService sellerAppService;

    @GetMapping
    public ResponseEntity<List<SellerAppResponse>> getAllSellerApps() {
        return ResponseEntity.ok(sellerAppService.getAllSellerApps());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SellerAppResponse> getSellerAppById(@PathVariable Long id) {
        return ResponseEntity.ok(sellerAppService.getSellerAppById(id));
    }

    @GetMapping("/healthy")
    public ResponseEntity<List<SellerAppResponse>> getHealthyApps() {
        return ResponseEntity.ok(sellerAppService.getHealthyApps());
    }

    @GetMapping("/{id}/health")
    public ResponseEntity<SellerAppResponse> checkHealth(@PathVariable Long id) {
        return ResponseEntity.ok(sellerAppService.checkHealth(id));
    }

    @PostMapping
    public ResponseEntity<SellerAppResponse> createSellerApp(@Valid @RequestBody SellerAppRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(sellerAppService.createSellerApp(request));
    }
}
