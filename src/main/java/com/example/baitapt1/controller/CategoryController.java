package com.example.baitapt1.controller;

import com.example.baitapt1.dto.CategoryDTO;
import com.example.baitapt1.dto.CategoryRepoDTO;

import com.example.baitapt1.service.CategoryService;
import org.springframework.core.io.Resource;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/category")
public class CategoryController {
    @Autowired
    CategoryService categoryService;
    // Thêm mới category
    @PostMapping
    public ResponseEntity<CategoryRepoDTO> createCategory(
            @Valid @ModelAttribute CategoryDTO dto,
            @RequestParam("image") List<MultipartFile> image,
            @RequestParam(defaultValue = "admin") String createdBy
    ) {
        CategoryRepoDTO result = categoryService.Categorycode(dto, image, createdBy);
        return ResponseEntity.ok(result);
    }
    @PutMapping("/{categorycode}")
    public ResponseEntity<CategoryRepoDTO> updateCategory(
            @PathVariable("categorycode") String categorycode,
            @ModelAttribute CategoryDTO dto) {

        CategoryRepoDTO response = categoryService.updateCategoryByCode(categorycode, dto);
        return ResponseEntity.ok(response);
    }


    @GetMapping("/search")
    public ResponseEntity<?> search(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String categorycode,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSSSSS") LocalDateTime createdFrom,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSSSSS") LocalDateTime createdTo,
            Pageable pageable) {

        Map<String, Object> response = categoryService.searchCategories(
                name, categorycode, createdFrom, createdTo, pageable
        );
        return ResponseEntity.ok(response);
    }
    @GetMapping("/export")
    public ResponseEntity<byte[]> exportCategories(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String categorycode,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime createdFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime createdTo
    ) {
        return categoryService.exportCategoriesToExcel(name, categorycode, createdFrom, createdTo);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> softDelete(@PathVariable Long id) {
        categoryService.softDelete(id);
        return ResponseEntity.ok("Xóa mềm thành công");
 }
}
