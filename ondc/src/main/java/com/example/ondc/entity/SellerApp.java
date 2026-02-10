package com.example.ondc.entity;

import com.example.ondc.enums.SellerAppStatus;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "seller_apps")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class SellerApp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String apiEndpoint;

    private String apiKey;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private SellerAppStatus status = SellerAppStatus.ACTIVE;

    @Builder.Default
    private Long responseTimeMs = 0L;

    @Builder.Default
    private Double uptimePercentage = 100.0;

    @Builder.Default
    private Integer totalRequests = 0;

    @Builder.Default
    private Integer failedRequests = 0;

    private LocalDateTime lastHealthCheck;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime updatedAt;

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
