package com.example.baitapt1.controller;

import com.example.baitapt1.dto.CategoryDTO;
import com.example.baitapt1.dto.CategoryRepoDTO;

import com.example.baitapt1.dto.CategorySearchRequest;
import com.example.baitapt1.service.CategoryService;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;


import org.springframework.data.domain.Pageable;

import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;



import java.util.Map;

@RestController
@RequestMapping("/api/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;


    @PostMapping
    public ResponseEntity<CategoryRepoDTO> createCategory(@ModelAttribute @Valid CategoryDTO dto) {
        CategoryRepoDTO result = categoryService.Categorycode(dto);
        return ResponseEntity.ok(result);
    }


    // Cập nhật category theo categorycode
    @PutMapping("/{categorycode}")
    public ResponseEntity<CategoryRepoDTO> updateCategory(
            @PathVariable String categorycode,
            @ModelAttribute CategoryDTO dto) {
        CategoryRepoDTO response = categoryService.updateCategoryByCode(categorycode, dto);
        return ResponseEntity.ok(response);
    }


    @GetMapping("/search")
    public ResponseEntity<?> searchCategories(
            @ModelAttribute CategorySearchRequest searchRequest,
            @PageableDefault(page = 0, size = 10)
            Pageable pageable) {
        Map<String, Object> response = categoryService.searchCategories(searchRequest, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/export")
    public ResponseEntity<byte[]> exportCategories(
            @ModelAttribute CategorySearchRequest searchRequest) {
        return categoryService.exportCategoriesToExcel(searchRequest);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<String> softDelete(@PathVariable Long id) {
        categoryService.softDelete(id);
        return ResponseEntity.ok("Xóa mềm thành công");
    }
}


