package com.example.baitapt1.repository;

import com.example.baitapt1.dto.ProductRepoDTO;

import com.example.baitapt1.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;



import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findByProductCode(String productCode);

    @Query(value = """
        SELECT 
            p.id, p.name, p.product_code AS productCode, p.price, p.quantity, 
            p.created_date AS createdDate, p.modified_date AS modifiedDate,
            GROUP_CONCAT(DISTINCT c.name SEPARATOR ', ') AS categories,
            JSON_ARRAYAGG(JSON_OBJECT('name', i.name, 'url', i.url, 'uuid', i.uuid)) AS images
        FROM product p
        LEFT JOIN product_category pc ON p.id = pc.product_id
        LEFT JOIN category c ON pc.category_id = c.id
        LEFT JOIN product_image i ON p.id = i.product_id
        WHERE p.status = 1
          AND (:name IS NULL OR p.name LIKE CONCAT('%', :name, '%'))
          AND (:productCode IS NULL OR p.product_code = :productCode)
          AND (:createdFrom IS NULL OR p.created_date >= :createdFrom)
          AND (:createdTo IS NULL OR p.created_date <= :createdTo)
          AND (:categoryId IS NULL OR c.id = :categoryId)
        GROUP BY p.id
        LIMIT :limit OFFSET :offset
        """, nativeQuery = true)
    List<ProductRepoDTO> searchProducts(
            @Param("name") String name,
            @Param("productCode") String productCode,
            @Param("createdFrom") LocalDateTime createdFrom,
            @Param("createdTo") LocalDateTime createdTo,
            @Param("categoryId") Long categoryId,
            @Param("limit") int limit,
            @Param("offset") int offset
    );
    Optional<Product> findByIdAndStatus(long id, String status);
    boolean existsByProductCodeAndIdNot(String productCode, long id);

    @Modifying
    @Query("UPDATE Product p SET p.name = :name, p.productCode=:productCode, p.description = :description,p.price= :price, p.quantity=:quantity, p.modifiedDate = CURRENT_TIMESTAMP WHERE p.id= :id AND p.status = '1'")
    int updateProduct( @Param("id") Long id,
                       @Param("name") String name,
                       @Param("productCode") String productCode,
                       @Param("description") String description,
                       @Param("price") double price,
                       @Param ("quantity") Long quantity);

    @Modifying
    @Query("UPDATE Product p SET p.status = '0', p.modifiedDate = CURRENT_TIMESTAMP, p.modifiedBy = :updatedBy WHERE p.id = :id AND p.status = '1'")
    Integer softDelete(@Param("id") Long id, @Param("updatedBy") String updatedBy);






}
