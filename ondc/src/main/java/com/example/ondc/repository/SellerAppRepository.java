package com.example.ondc.repository;

import com.example.ondc.entity.SellerApp;
import com.example.ondc.enums.SellerAppStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SellerAppRepository extends JpaRepository<SellerApp, Long> {
    List<SellerApp> findByStatus(SellerAppStatus status);
    List<SellerApp> findByNameContainingIgnoreCase(String name);
}
