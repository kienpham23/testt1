package com.example.baitapt1.controller;


import com.example.baitapt1.dto.ProductDTO;

import com.example.baitapt1.dto.ProductReponDTO;
import com.example.baitapt1.dto.ProductSearchRequest;

import com.example.baitapt1.mapper.ProductMapper;
import com.example.baitapt1.repository.ProductRepository;
import com.example.baitapt1.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RestController
@RequestMapping("/api/product")
public class ProductCotroller {
    @Autowired
    ProductService productService;
    @Autowired
    ProductRepository productRepository;
    @Autowired
    ProductMapper productMapper;
    @PostMapping
    public ResponseEntity<ProductReponDTO> createProduct(@ModelAttribute @Valid ProductDTO dto) {
        ProductReponDTO result = productService.createProduct(dto, "admin");
        return ResponseEntity.ok(result);
    }
    @GetMapping("/search")
    public ResponseEntity<?> searchProducts(
            @ModelAttribute ProductSearchRequest searchRequest,
            @PageableDefault(page = 0, size = 10)
            Pageable pageable) {

        Map<String, Object> result = productService.searchProducts(
                searchRequest.getName(),
                searchRequest.getProductCode(),
                searchRequest.getCreatedFrom(),
                searchRequest.getCreatedTo(),
                searchRequest.getCategoryId(),
                pageable
        );

        return ResponseEntity.ok(result);
    }
    @GetMapping("/export")
    public ResponseEntity<byte[]> exportExcel(@ModelAttribute ProductSearchRequest searchRequest) {
        return productService.exportProductsToExcel(searchRequest);
    }
    @PutMapping("/{id}")
    public ResponseEntity<ProductReponDTO> updateProduct(@PathVariable Long id,
                                                         @Valid @ModelAttribute ProductDTO dto) {
        return ResponseEntity.ok(productService.updateProduct(id, dto, dto.getUpdatedBy()));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id) {
        productService.softDeleteProduct(id, "admin");
        return ResponseEntity.ok().build();
    }


}
