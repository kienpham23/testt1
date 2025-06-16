package com.example.baitapt1.repository;
import com.example.baitapt1.entity.CategoryImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface CategoryImageRepository extends JpaRepository<CategoryImage, Integer> {


    List<CategoryImage> findByCategoryIdIn(List<Long> categoryIds);
    @Modifying
    @Query("UPDATE CategoryImage ci SET ci.status = '0' WHERE ci.category.id = :categoryId AND ci.status = '1'")
    void deactivateOldImages(@Param("categoryId") Long categoryId);

}
