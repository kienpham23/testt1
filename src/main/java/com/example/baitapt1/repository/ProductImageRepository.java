package com.example.baitapt1.repository;

import com.example.baitapt1.entity.Product;
import com.example.baitapt1.entity.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductImageRepository extends JpaRepository<ProductImage, Integer> {
    @Modifying
    @Query("UPDATE ProductImage pi SET pi.status = '0' WHERE pi.product.id = :productId AND pi.status = '1'")
    void deactivateOldImages(@Param("productId") Long productId);
}

