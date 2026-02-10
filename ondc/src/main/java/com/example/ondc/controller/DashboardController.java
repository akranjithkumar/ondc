package com.example.ondc.controller;

import com.example.ondc.dto.DashboardResponse;
import com.example.ondc.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/summary/{vendorId}")
    public ResponseEntity<DashboardResponse> getVendorDashboard(@PathVariable Long vendorId) {
        return ResponseEntity.ok(dashboardService.getVendorDashboard(vendorId));
    }
}
