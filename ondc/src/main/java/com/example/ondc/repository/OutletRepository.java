package com.example.ondc.repository;

import com.example.ondc.entity.Outlet;
import com.example.ondc.enums.OutletType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface OutletRepository extends JpaRepository<Outlet, Long> {
    List<Outlet> findByVendorId(Long vendorId);
    List<Outlet> findByVendorIdAndIsActiveTrue(Long vendorId);
    List<Outlet> findByVendorIdAndType(Long vendorId, OutletType type);
    List<Outlet> findByPincode(String pincode);
}
