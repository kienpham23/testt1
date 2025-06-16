package com.example.baitapt1.repository;

import com.example.baitapt1.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository <Category, Long> {

    @Query("SELECT DISTINCT c FROM Category c LEFT JOIN FETCH c.categoryImage WHERE c.categorycode = :categoryCode")
    Optional<Category> findByCategoryCodeWithImages(@Param("categoryCode") String categoryCode);
    boolean existsByCategorycode(String categorycode);
    @Query("SELECT c FROM Category c WHERE " +
            "(:name IS NULL OR c.name LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
            "(:categorycode IS NULL OR c.categorycode = :categorycode) AND " +
            "(:createdFrom IS NULL OR c.createDate >= :createdFrom) AND " +
            "(:createdTo IS NULL OR c.createDate <= :createdTo) AND " +
            "c.status = '1'")


    Page<Category> searchCategories(@Param("name") String name,
                                    @Param("categorycode") String categorycode,
                                    @Param("createdFrom") LocalDateTime createdFrom,
                                    @Param("createdTo") LocalDateTime createdTo,
                                    Pageable pageable);
    Optional<Category> findByIdAndStatus(long id, String status);

    boolean existsBycategorycodeAndIdNot(String categorycode, long id);
    @Modifying
    @Query("UPDATE Category c SET c.name = :name,  c.description = :description, c.modifiedDate = CURRENT_TIMESTAMP WHERE c.categorycode = :categorycode AND c.status = '1'")
    int updateCategory(@Param("name") String name,
                       @Param("categorycode") String newcode,
                       @Param("description") String description);


    boolean existsByIdAndStatus(Long categoryId, String number);

}



