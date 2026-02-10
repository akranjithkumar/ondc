package com.example.ondc.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "inventory",
       uniqueConstraints = @UniqueConstraint(columnNames = {"product_id", "outlet_id"}))
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "outlet_id", nullable = false)
    private Outlet outlet;

    @Column(nullable = false)
    @Builder.Default
    private Integer totalStock = 0;

    @Column(nullable = false)
    @Builder.Default
    private Integer reservedStock = 0;

    @Builder.Default
    private Integer reorderLevel = 10;

    @Builder.Default
    private LocalDateTime lastSyncedAt = LocalDateTime.now();

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime updatedAt;

    /**
     * Available stock = total - reserved.
     * This is the inventory authority — single source of truth.
     */
    public Integer getAvailableStock() {
        return totalStock - reservedStock;
    }

    /**
     * Check if stock is below reorder level.
     */
    public boolean isLowStock() {
        return getAvailableStock() <= reorderLevel;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
        lastSyncedAt = LocalDateTime.now();
    }
}
