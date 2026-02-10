package com.example.ondc.repository;

import com.example.ondc.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    Optional<Inventory> findByProductIdAndOutletId(Long productId, Long outletId);
    List<Inventory> findByOutletId(Long outletId);
    List<Inventory> findByProductId(Long productId);

    @Query("SELECT i FROM Inventory i WHERE (i.totalStock - i.reservedStock) <= i.reorderLevel")
    List<Inventory> findLowStockInventory();

    @Query("SELECT i FROM Inventory i WHERE i.outlet.vendor.id = :vendorId AND (i.totalStock - i.reservedStock) <= i.reorderLevel")
    List<Inventory> findLowStockByVendorId(@Param("vendorId") Long vendorId);

    List<Inventory> findByOutletVendorId(Long vendorId);
}
