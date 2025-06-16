package com.example.baitapt1.controller;


import com.example.baitapt1.dto.ProductDTO;
import com.example.baitapt1.dto.ProductRepoDTO;
import com.example.baitapt1.dto.ProductReponDTO;
import com.example.baitapt1.entity.Product;
import com.example.baitapt1.mapper.ProductMapper;
import com.example.baitapt1.repository.ProductRepository;
import com.example.baitapt1.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Arrays;
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
    public ResponseEntity<ProductReponDTO> createProduct(
            @Valid @ModelAttribute ProductDTO productDTO,
            @RequestParam String createdBy) {

        ProductReponDTO result = productService.createProduct(productDTO, createdBy);
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }
    @GetMapping("/search")
    public ResponseEntity<?> searchProducts(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String productCode,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime createdFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime createdTo,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Map<String, Object> response = productService.searchProducts(
                name, productCode, createdFrom, createdTo, categoryId, page, size
        );
        return ResponseEntity.ok(response);
    }
    @GetMapping("/export")
    public ResponseEntity<byte[]> exportExcel(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String productCode,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime createdFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime createdTo,
            @RequestParam(required = false) Long categoryId
    ) {
        return productService.exportProductsToExcel(name, productCode, createdFrom, createdTo, categoryId);
    }
    @PutMapping("/{id}")
    public ResponseEntity<ProductReponDTO> updateProduct(
            @PathVariable Long id,
            @ModelAttribute ProductDTO dto,
            @RequestHeader(name = "updatedBy", defaultValue = "admin") String updatedBy
    ) {
        ProductReponDTO updatedProduct = productService.updateProduct(id, dto, updatedBy);
        return ResponseEntity.ok(updatedProduct);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id) {
        productService.softDeleteProduct(id, "admin"); // bạn truyền user hiện tại nếu có
        return ResponseEntity.ok().build();
    }


}
