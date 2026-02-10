package com.example.ondc.repository;

import com.example.ondc.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByVendorId(Long vendorId);
    Optional<Product> findBySku(String sku);
    List<Product> findByCategoryIgnoreCase(String category);
    List<Product> findByVendorIdAndIsActiveTrue(Long vendorId);
}
