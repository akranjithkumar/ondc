package com.example.ondc.repository;

import com.example.ondc.entity.Order;
import com.example.ondc.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findByOndcOrderId(String ondcOrderId);
    List<Order> findByVendorId(Long vendorId);
    List<Order> findByVendorIdAndStatus(Long vendorId, OrderStatus status);
    List<Order> findByOutletId(Long outletId);
    List<Order> findBySellerAppId(Long sellerAppId);

    @Query("SELECT o FROM Order o WHERE o.vendor.id = :vendorId ORDER BY " +
           "CASE o.priority WHEN 'CRITICAL' THEN 0 WHEN 'HIGH' THEN 1 WHEN 'MEDIUM' THEN 2 ELSE 3 END, " +
           "o.createdAt ASC")
    List<Order> findByVendorIdOrderByPriority(@Param("vendorId") Long vendorId);

    @Query("SELECT COUNT(o) FROM Order o WHERE o.vendor.id = :vendorId AND o.status = :status")
    Long countByVendorIdAndStatus(@Param("vendorId") Long vendorId, @Param("status") OrderStatus status);
}
