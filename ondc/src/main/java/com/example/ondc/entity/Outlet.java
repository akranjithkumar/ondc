package com.example.ondc.entity;

import com.example.ondc.enums.OutletType;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "outlets")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Outlet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OutletType type;

    @Column(length = 500)
    private String address;

    private Double latitude;

    private Double longitude;

    @Column(nullable = false)
    private String pincode;

    @Builder.Default
    private Boolean isActive = true;

    @Builder.Default
    private Integer currentLoad = 0;

    @Builder.Default
    private Integer maxCapacity = 100;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vendor_id", nullable = false)
    private Vendor vendor;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime updatedAt;

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
