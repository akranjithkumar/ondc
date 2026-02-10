package com.example.ondc.service;

import com.example.ondc.dto.SellerAppRequest;
import com.example.ondc.dto.SellerAppResponse;
import com.example.ondc.entity.SellerApp;
import com.example.ondc.enums.SellerAppStatus;
import com.example.ondc.exception.ResourceNotFoundException;
import com.example.ondc.repository.SellerAppRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SellerAppService {

    private final SellerAppRepository sellerAppRepository;

    public List<SellerAppResponse> getAllSellerApps() {
        return sellerAppRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public SellerAppResponse getSellerAppById(Long id) {
        return toResponse(findSellerAppById(id));
    }

    @Transactional
    public SellerAppResponse createSellerApp(SellerAppRequest request) {
        SellerApp app = SellerApp.builder()
                .name(request.getName())
                .apiEndpoint(request.getApiEndpoint())
                .apiKey(request.getApiKey())
                .build();
        return toResponse(sellerAppRepository.save(app));
    }

    /**
     * Health check simulation — in production, this would ping the actual API endpoint.
     * For MVP, we simulate health status based on random response times.
     */
    @Transactional
    public SellerAppResponse checkHealth(Long id) {
        SellerApp app = findSellerAppById(id);

        // Simulate health check
        long responseTime = (long) (Math.random() * 500); // 0-500ms
        app.setResponseTimeMs(responseTime);
        app.setTotalRequests(app.getTotalRequests() + 1);
        app.setLastHealthCheck(LocalDateTime.now());

        if (responseTime > 400) {
            app.setStatus(SellerAppStatus.DOWN);
            app.setFailedRequests(app.getFailedRequests() + 1);
        } else if (responseTime > 200) {
            app.setStatus(SellerAppStatus.DEGRADED);
        } else {
            app.setStatus(SellerAppStatus.ACTIVE);
        }

        // Calculate uptime
        if (app.getTotalRequests() > 0) {
            double uptime = ((double)(app.getTotalRequests() - app.getFailedRequests())
                             / app.getTotalRequests()) * 100;
            app.setUptimePercentage(Math.round(uptime * 100.0) / 100.0);
        }

        return toResponse(sellerAppRepository.save(app));
    }

    public List<SellerAppResponse> getHealthyApps() {
        return sellerAppRepository.findByStatus(SellerAppStatus.ACTIVE).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public SellerApp findSellerAppById(Long id) {
        return sellerAppRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Seller app not found with id: " + id));
    }

    private SellerAppResponse toResponse(SellerApp app) {
        return SellerAppResponse.builder()
                .id(app.getId())
                .name(app.getName())
                .apiEndpoint(app.getApiEndpoint())
                .status(app.getStatus())
                .responseTimeMs(app.getResponseTimeMs())
                .uptimePercentage(app.getUptimePercentage())
                .totalRequests(app.getTotalRequests())
                .failedRequests(app.getFailedRequests())
                .lastHealthCheck(app.getLastHealthCheck())
                .createdAt(app.getCreatedAt())
                .build();
    }
}
