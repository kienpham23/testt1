package com.example.baitapt1.service;



import com.example.baitapt1.dto.ProductDTO;
import com.example.baitapt1.dto.ProductRepoDTO;
import com.example.baitapt1.dto.ProductReponDTO;
import com.example.baitapt1.entity.*;
import com.example.baitapt1.mapper.ProductMapper;

import com.example.baitapt1.repository.CategoryRepository;
import com.example.baitapt1.repository.ProductCategoryRepository;
import com.example.baitapt1.repository.ProductImageRepository;
import com.example.baitapt1.repository.ProductRepository;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;

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
    @Transactional
    public ProductReponDTO createProduct(ProductDTO dto, String createdBy) {
        // Map các field cơ bản
        Product product = productMapper.toEntity(dto);
        product.setStatus("1");
        product.setCreatedBy(createdBy);
        product.setCreatedDate(LocalDateTime.now());

        // Lưu tạm Product
        product = productRepository.save(product);

        // Gán Category
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
        for (MultipartFile file : dto.getImage()) {
            String uuid = UUID.randomUUID().toString();
            String url = fileStorageService.save(file, uuid);

            ProductImage img = new ProductImage();
            img.setProduct(product);
            img.setUuid(uuid);
            img.setName(file.getOriginalFilename());
            img.setUrl(url);
            images.add(img);
        }
        productImageRepository.saveAll(images);

        // set vào entity
        product.setProductCategory(productCategories);
        product.setProductsimage(images);

        return productMapper.toResponse(product);
    }
    public Map<String, Object> searchProducts(String name, String productCode,
                                              LocalDateTime createdFrom, LocalDateTime createdTo,
                                              Long categoryId, int page, int size) {

        int offset = page * size;
        List<ProductRepoDTO> results = productRepository.searchProducts(
                name, productCode, createdFrom, createdTo, categoryId, size, offset
        );
        if (results.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "product.notfound");
        }




        Map<String, Object> response = new HashMap<>();
        response.put("data", results);
        response.put("total", results.size());

        Map<String, Object> pagination = new HashMap<>();
        pagination.put("currentPage", page);
        pagination.put("pageSize", size);
        pagination.put("hasNext", results.size() == size);
        pagination.put("hasPrevious", page > 0);

        response.put("pagination", pagination);
        return response;
    }
    public ResponseEntity<byte[]> exportProductsToExcel(String name, String productCode,
                                                        LocalDateTime createdFrom, LocalDateTime createdTo,
                                                        Long categoryId) {

        List<ProductRepoDTO> products = productRepository.searchProducts(name, productCode, createdFrom, createdTo, categoryId, 10_000, 0);

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Products");

            // Header
            Row header = sheet.createRow(0);
            String[] columns = {"ID", "Tên", "Mã", "Giá", "Số lượng", "Ngày tạo", "Ngày sửa", "Danh mục"};
            for (int i = 0; i < columns.length; i++) {
                Cell cell = header.createCell(i);
                cell.setCellValue(columns[i]);
            }

            // Dữ liệu
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
            throw new RuntimeException("pc.export.failed" , e);
        }
    }
    @Transactional
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


        if (dto.getImage() != null && !dto.getImage().isEmpty()) {

            productImageRepository.deactivateOldImages(product.getId());


            List<ProductImage> newImages = dto.getImage().stream().map(file -> {
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
            }).collect(Collectors.toList());

            productImageRepository.saveAll(newImages);
            product.setProductsimage(newImages);
        }


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



