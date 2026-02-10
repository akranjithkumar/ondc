package com.example.ondc.service;

import com.example.ondc.dto.VendorRequest;
import com.example.ondc.dto.VendorResponse;
import com.example.ondc.entity.Vendor;
import com.example.ondc.exception.ResourceNotFoundException;
import com.example.ondc.repository.VendorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VendorService {

    private final VendorRepository vendorRepository;

    public List<VendorResponse> getAllVendors() {
        return vendorRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public VendorResponse getVendorById(Long id) {
        return toResponse(findVendorById(id));
    }

    @Transactional
    public VendorResponse createVendor(VendorRequest request) {
        Vendor vendor = Vendor.builder()
                .name(request.getName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .businessName(request.getBusinessName())
                .address(request.getAddress())
                .build();
        return toResponse(vendorRepository.save(vendor));
    }

    @Transactional
    public VendorResponse updateVendor(Long id, VendorRequest request) {
        Vendor vendor = findVendorById(id);
        vendor.setName(request.getName());
        vendor.setEmail(request.getEmail());
        vendor.setPhone(request.getPhone());
        vendor.setBusinessName(request.getBusinessName());
        vendor.setAddress(request.getAddress());
        return toResponse(vendorRepository.save(vendor));
    }

    @Transactional
    public void updateVendorStats(Long vendorId, boolean orderSuccessful) {
        Vendor vendor = findVendorById(vendorId);
        vendor.setTotalOrders(vendor.getTotalOrders() + 1);
        if (orderSuccessful) {
            vendor.setSuccessfulOrders(vendor.getSuccessfulOrders() + 1);
        } else {
            vendor.setCancelledOrders(vendor.getCancelledOrders() + 1);
        }
        // Calculate rating based on fulfillment rate
        if (vendor.getTotalOrders() > 0) {
            double rate = (double) vendor.getSuccessfulOrders() / vendor.getTotalOrders();
            vendor.setRating(Math.round(rate * 50) / 10.0); // Scale to 0-5
        }
        vendorRepository.save(vendor);
    }

    public Vendor findVendorById(Long id) {
        return vendorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vendor not found with id: " + id));
    }

    private VendorResponse toResponse(Vendor vendor) {
        return VendorResponse.builder()
                .id(vendor.getId())
                .name(vendor.getName())
                .email(vendor.getEmail())
                .phone(vendor.getPhone())
                .businessName(vendor.getBusinessName())
                .address(vendor.getAddress())
                .rating(vendor.getRating())
                .totalOrders(vendor.getTotalOrders())
                .successfulOrders(vendor.getSuccessfulOrders())
                .cancelledOrders(vendor.getCancelledOrders())
                .isActive(vendor.getIsActive())
                .createdAt(vendor.getCreatedAt())
                .build();
    }
}
