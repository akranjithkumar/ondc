package com.example.ondc.repository;

import com.example.ondc.entity.Vendor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

@Repository
public interface VendorRepository extends JpaRepository<Vendor, Long> {
    Optional<Vendor> findByEmail(String email);
    List<Vendor> findByIsActiveTrue();
    List<Vendor> findByBusinessNameContainingIgnoreCase(String businessName);
}
