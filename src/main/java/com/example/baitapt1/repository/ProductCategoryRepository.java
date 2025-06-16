package com.example.baitapt1.repository;


import com.example.baitapt1.entity.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface ProductCategoryRepository extends JpaRepository<ProductCategory, Integer> {
    @Modifying
    @Transactional
    @Query(value = """
        DELETE FROM product_category 
        WHERE product_id = :productId 
        AND category_id NOT IN (:newCategoryIds)
        """, nativeQuery = true)
    void deleteOldCategories(@Param("productId") Long productId, @Param("newCategoryIds") List<Long> newCategoryIds);

    // Kiểm tra liên kết đã tồn tại hay chưa
    @Query(value = """
        SELECT COUNT(*) FROM product_category 
        WHERE product_id = :productId 
        AND category_id = :categoryId
        """, nativeQuery = true)
    int existsByProductAndCategory(@Param("productId") Long productId, @Param("categoryId") Long categoryId);
}







