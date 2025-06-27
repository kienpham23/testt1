package com.example.baitapt1.service;



import com.example.baitapt1.dto.ProductDTO;
import com.example.baitapt1.dto.ProductRepoDTO;
import com.example.baitapt1.dto.ProductReponDTO;
import com.example.baitapt1.dto.ProductSearchRequest;
import com.example.baitapt1.entity.*;
import com.example.baitapt1.mapper.ProductMapper;

import com.example.baitapt1.repository.CategoryRepository;
import com.example.baitapt1.repository.ProductCategoryRepository;
import com.example.baitapt1.repository.ProductImageRepository;
import com.example.baitapt1.repository.ProductRepository;


import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.ByteArrayOutputStream;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private ProductImageRepository productImageRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private ProductCategoryRepository productCategoryRepository;
    @Transactional(rollbackFor = Exception.class)
    public ProductReponDTO createProduct(ProductDTO dto, String createdBy) {

        Product product = productMapper.toEntity(dto);
        product.setStatus("1");
        product.setCreatedBy(createdBy);
        product.setCreatedDate(LocalDateTime.now());


        product = productRepository.save(product);


        List<Category> categories = categoryRepository.findAllById(dto.getCategoryIds());
        List<ProductCategory> productCategories = new ArrayList<>();
        for (Category cat : categories) {
            ProductCategory pc = new ProductCategory();
            pc.setProduct(product);
            pc.setCategory(cat);
            pc.setCreatedBy(createdBy);
            productCategories.add(pc);
        }
        productCategoryRepository.saveAll(productCategories);


        List<ProductImage> images = new ArrayList<>();
        if (dto.getImage() != null && dto.getImage().length > 0) {
            for (MultipartFile file : dto.getImage()) {
                if (!file.isEmpty()) {
                    String uuid = UUID.randomUUID().toString();
                    String url = fileStorageService.save(file, uuid);

                    ProductImage img = new ProductImage();
                    img.setProduct(product);
                    img.setUuid(uuid);
                    img.setName(file.getOriginalFilename());
                    img.setUrl(url);
                    images.add(img);
                }
            }
            productImageRepository.saveAll(images);
        }




        product.setProductCategory(productCategories);
        product.setProductsimage(images);

        return productMapper.toResponse(product);
    }
    public Map<String, Object> searchProducts(String name, String productCode,
                                              LocalDateTime createdFrom, LocalDateTime createdTo,
                                              Long categoryId, Pageable pageable) {

        Page<ProductRepoDTO> page = productRepository.searchProducts(
                name, productCode, createdFrom, createdTo, categoryId, pageable
        );
        System.out.println("Trang hiện tại: " + page.getNumber());
        System.out.println("Tổng số trang: " + page.getTotalPages());
        System.out.println("Tổng số bản ghi: " + page.getTotalElements());
        System.out.println("Có trang kế tiếp không? " + page.hasNext());

        if (page.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "product.notfound");
        }

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("data", page.getContent());

        Map<String, Object> pagination = new LinkedHashMap<>();
        pagination.put("currentPage", page.getNumber());
        pagination.put("pageSize", page.getSize());
        pagination.put("totalElements", page.getTotalElements());
        pagination.put("totalPages", page.getTotalPages());
        pagination.put("hasNext", page.hasNext());
        pagination.put("hasPrevious", page.hasPrevious());

        response.put("pagination", pagination);
        return response;
    }

    public ResponseEntity<byte[]> exportProductsToExcel(ProductSearchRequest searchRequest) {
        // Lấy toàn bộ danh sách sản phẩm không phân trang (export full)
        List<ProductRepoDTO> products = productRepository.exportProductsToExcel(
                searchRequest.getName(),
                searchRequest.getProductCode(),
                searchRequest.getCreatedFrom(),
                searchRequest.getCreatedTo(),
                searchRequest.getCategoryId()
        );

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Products");

            String[] columns = {"ID", "Tên", "Mã", "Giá", "Số lượng", "Ngày tạo", "Ngày sửa", "Danh mục"};
            Row header = sheet.createRow(0);
            for (int i = 0; i < columns.length; i++) {
                header.createCell(i).setCellValue(columns[i]);
            }

            int rowIdx = 1;
            for (ProductRepoDTO dto : products) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(dto.getId());
                row.createCell(1).setCellValue(dto.getName());
                row.createCell(2).setCellValue(dto.getProductCode());
                row.createCell(3).setCellValue(dto.getPrice());
                row.createCell(4).setCellValue(dto.getQuantity());
                row.createCell(5).setCellValue(dto.getCreatedDate() != null ? dto.getCreatedDate().toString() : "");
                row.createCell(6).setCellValue(dto.getModifiedDate() != null ? dto.getModifiedDate().toString() : "");
                row.createCell(7).setCellValue(dto.getCategories());
            }

            workbook.write(out);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=products.xlsx")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(out.toByteArray());

        } catch (Exception e) {
            throw new RuntimeException("product.export.failed", e);
        }
    }
    @Transactional(rollbackFor = Exception.class)
    public ProductReponDTO updateProduct(Long id, ProductDTO dto, String updatedBy) {

        Product product = productRepository.findByIdAndStatus(id, "1")
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "product.notfound"));

        if (productRepository.existsByProductCodeAndIdNot(dto.getProductCode(), id)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "product.code.exists");
        }

        product.setName(dto.getName());
        product.setProductCode(dto.getProductCode());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setQuantity(dto.getQuantity());
        product.setModifiedBy(updatedBy);
        product.setModifiedDate(LocalDateTime.now());
        productRepository.save(product);

        if (dto.getCategoryIds() != null && !dto.getCategoryIds().isEmpty()) {
            List<Long> newCategoryIds = dto.getCategoryIds();

            productCategoryRepository.deleteOldCategories(id, newCategoryIds);

            for (Long categoryId : newCategoryIds) {
                boolean categoryExists = categoryRepository.existsByIdAndStatus(categoryId, "1");
                boolean linkExists = productCategoryRepository.existsByProductAndCategory(id, categoryId) > 0;

                if (categoryExists && !linkExists) {
                    ProductCategory pc = new ProductCategory();
                    pc.setProduct(product);
                    pc.setCategory(new Category(categoryId));
                    pc.setCreatedBy(updatedBy);
                    pc.setCreateDate(LocalDateTime.now());
                    productCategoryRepository.save(pc);
                }
            }
        }


        List<ProductImage> currentImages = productImageRepository.findByProductIdAndStatus(product.getId(), "1");
        Set<String> keepUuids = new HashSet<>(dto.getKeepImageUuids() != null ? dto.getKeepImageUuids() : List.of());


        List<ProductImage> toDeactivate = currentImages.stream()
                .filter(img -> !keepUuids.contains(img.getUuid()))
                .toList();

        toDeactivate.forEach(img -> img.setStatus("0"));
        productImageRepository.saveAll(toDeactivate);

        if (dto.getImage() != null && dto.getImage().length > 0) {
            List<ProductImage> newImages = Arrays.stream(dto.getImage())
                    .filter(file -> file != null && !file.isEmpty())
                    .map(file -> {
                        String uuid = UUID.randomUUID().toString();
                        String url = fileStorageService.save(file, uuid);

                        ProductImage img = new ProductImage();
                        img.setName(file.getOriginalFilename());
                        img.setUrl(url);
                        img.setUuid(uuid);
                        img.setStatus("1");
                        img.setProduct(product);
                        img.setCreateDate(LocalDateTime.now());
                        img.setCreatedBy(updatedBy);
                        return img;
                    })
                    .collect(Collectors.toList());

            productImageRepository.saveAll(newImages);
        }



        List<ProductImage> updatedImages = productImageRepository.findByProductIdAndStatus(product.getId(), "1");
        product.setProductsimage(updatedImages);

        return productMapper.toResponse(product);
    }

    @Transactional
    public void softDeleteProduct(Long id, String updatedBy) {
        Integer updated = productRepository.softDelete(id, updatedBy);

        if (updated == 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "product.notfound.with.id");
        }


    }
}



