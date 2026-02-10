package com.example.ondc.service;

import com.example.ondc.dto.OutletRequest;
import com.example.ondc.dto.OutletResponse;
import com.example.ondc.entity.Outlet;
import com.example.ondc.entity.Vendor;
import com.example.ondc.exception.ResourceNotFoundException;
import com.example.ondc.repository.OutletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OutletService {

    private final OutletRepository outletRepository;
    private final VendorService vendorService;

    public List<OutletResponse> getAllOutlets() {
        return outletRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<OutletResponse> getOutletsByVendor(Long vendorId) {
        return outletRepository.findByVendorId(vendorId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public OutletResponse getOutletById(Long id) {
        return toResponse(findOutletById(id));
    }

    @Transactional
    public OutletResponse createOutlet(OutletRequest request) {
        Vendor vendor = vendorService.findVendorById(request.getVendorId());
        Outlet outlet = Outlet.builder()
                .name(request.getName())
                .type(request.getType())
                .address(request.getAddress())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .pincode(request.getPincode())
                .maxCapacity(request.getMaxCapacity() != null ? request.getMaxCapacity() : 100)
                .vendor(vendor)
                .build();
        return toResponse(outletRepository.save(outlet));
    }

    @Transactional
    public OutletResponse updateOutlet(Long id, OutletRequest request) {
        Outlet outlet = findOutletById(id);
        outlet.setName(request.getName());
        outlet.setType(request.getType());
        outlet.setAddress(request.getAddress());
        outlet.setLatitude(request.getLatitude());
        outlet.setLongitude(request.getLongitude());
        outlet.setPincode(request.getPincode());
        if (request.getMaxCapacity() != null) {
            outlet.setMaxCapacity(request.getMaxCapacity());
        }
        return toResponse(outletRepository.save(outlet));
    }

    /**
     * Find optimal outlet based on:
     * 1. Distance to delivery pincode (exact match preferred)
     * 2. Current load vs capacity (prefer less loaded)
     * 3. Active status
     */
    public OutletResponse findOptimalOutlet(Long vendorId, String deliveryPincode) {
        List<Outlet> activeOutlets = outletRepository.findByVendorIdAndIsActiveTrue(vendorId);
        if (activeOutlets.isEmpty()) {
            throw new ResourceNotFoundException("No active outlets found for vendor: " + vendorId);
        }

        return activeOutlets.stream()
                .sorted(Comparator
                        // Prefer same pincode
                        .comparingInt((Outlet o) -> o.getPincode().equals(deliveryPincode) ? 0 : 1)
                        // Then prefer less loaded outlets (load ratio)
                        .thenComparingDouble(o -> (double) o.getCurrentLoad() / o.getMaxCapacity())
                )
                .findFirst()
                .map(this::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("No suitable outlet found"));
    }

    @Transactional
    public void incrementLoad(Long outletId) {
        Outlet outlet = findOutletById(outletId);
        outlet.setCurrentLoad(outlet.getCurrentLoad() + 1);
        outletRepository.save(outlet);
    }

    public Outlet findOutletById(Long id) {
        return outletRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Outlet not found with id: " + id));
    }

    private OutletResponse toResponse(Outlet outlet) {
        return OutletResponse.builder()
                .id(outlet.getId())
                .name(outlet.getName())
                .type(outlet.getType())
                .address(outlet.getAddress())
                .latitude(outlet.getLatitude())
                .longitude(outlet.getLongitude())
                .pincode(outlet.getPincode())
                .isActive(outlet.getIsActive())
                .currentLoad(outlet.getCurrentLoad())
                .maxCapacity(outlet.getMaxCapacity())
                .vendorId(outlet.getVendor().getId())
                .vendorName(outlet.getVendor().getName())
                .createdAt(outlet.getCreatedAt())
                .build();
    }
}
