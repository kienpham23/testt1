package com.example.baitapt1.service;

import com.example.baitapt1.dto.CategoryDTO;
import com.example.baitapt1.dto.CategoryRepoDTO;
import com.example.baitapt1.dto.ImageCategoryDTO;
import com.example.baitapt1.entity.Category;
import com.example.baitapt1.entity.CategoryImage;
import com.example.baitapt1.mapper.CategoryRepoMapper;
import com.example.baitapt1.repository.CategoryImageRepository;
import com.example.baitapt1.repository.CategoryRepository;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private CategoryRepoMapper categoryRepoMapper;
    @Autowired
    private CategoryImageRepository categoryImageRepository;
    @Autowired
    private FileStorageService fileStorageService;




    @Transactional
    public CategoryRepoDTO Categorycode(CategoryDTO dto, List<MultipartFile>images, String createdBy) {
        if(categoryRepository.existsByCategorycode(dto.getCategorycode())) {
            throw new RuntimeException("Categorycode bị trùng");
        }
        Category category = new Category();
        category.setName(dto.getName());
        category.setCategorycode(dto.getCategorycode());
        category.setDescription(dto.getDescription());
        category.setCreatedBy("admin");


        categoryRepository.save(category);


        List<CategoryImage> categoryImages = new ArrayList<>();
        for (MultipartFile file : images) {
            String uuid = UUID.randomUUID().toString();
            String url = fileStorageService.save(file, uuid);

            CategoryImage img = new CategoryImage();
            img.setName(category.getName());
            img.setUrl(url);
            img.setUuid(uuid);
            img.setCategory(category);
            categoryImages.add(img);
        }

        // 5. Lưu ảnh
        categoryImageRepository.saveAll(categoryImages);

        // 6. Set ảnh lại vào category (nếu dùng fetch)
        category.setCategoryImage(categoryImages);

        // 7. Trả về DTO
        return categoryRepoMapper.toDTO(category);


    }
    //Tìm kiếm

    public Map<String, Object> searchCategories(String name, String categorycode,
                                                LocalDateTime createdFrom, LocalDateTime createdTo,
                                                Pageable pageable) {

        Page<Category> page = categoryRepository.searchCategories(name, categorycode, createdFrom, createdTo, pageable);

        if (page.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "category.notfound");
        }

        List<Long> categoryIds = page.getContent().stream()
                .map(Category::getId)
                .collect(Collectors.toList());

        List<CategoryImage> images = categoryImageRepository.findByCategoryIdIn(categoryIds);

        Map<Long, List<CategoryImage>> imageMap = images.stream()
                .collect(Collectors.groupingBy(CategoryImage::getCategoryId));

        List<CategoryRepoDTO> data = page.getContent().stream().map(category -> {
            List<CategoryImage> categoryImages = imageMap.getOrDefault(category.getId(), new ArrayList<>());
            List<ImageCategoryDTO> imageDTOs = categoryImages.stream()
                    .map(img -> new ImageCategoryDTO(img.getName(), img.getUrl(), img.getUuid()))
                    .collect(Collectors.toList());

            return new CategoryRepoDTO(
                    category.getId(),
                    category.getName(),
                    category.getCategorycode(),
                    category.getDescription(),
                    category.getStatus(),
                    imageDTOs,
                    category.getCreateDate(),
                    category.getCreatedBy()
            );
        }).collect(Collectors.toList());

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("data", data);

        Map<String, Object> pagination = new HashMap<>();
        pagination.put("currentPage", page.getNumber() + 1);
        pagination.put("pageSize", page.getSize());
        pagination.put("totalElements", page.getTotalElements());
        pagination.put("totalPages", page.getTotalPages());
        pagination.put("hasNext", page.hasNext());
        pagination.put("hasPrevious", page.hasPrevious());

        response.put("pagination", pagination);
        return response;
    }

    @Transactional
    public CategoryRepoDTO updateCategoryByCode(String categorycode, CategoryDTO dto) {

        if (dto.getName().length() > 100 || dto.getDescription().length() > 200) {
            throw new RuntimeException("C.length.exceeded");
        }


        Category category = categoryRepository.findByCategoryCodeWithImages(categorycode)
                .filter(c -> "1".equals(c.getStatus()))
                .orElseThrow(() -> new RuntimeException("category.notfound.or.deleted"));


        category.setName(dto.getName());
        category.setDescription(dto.getDescription());
        category.setCreatedBy("admin");

        categoryRepository.save(category);


        categoryImageRepository.deactivateOldImages(category.getId());


        if (dto.getImage() != null && !dto.getImage().isEmpty()) {
            List<CategoryImage> newImages = dto.getImage().stream().map(file -> {
                String uuid = UUID.randomUUID().toString();
                String url = fileStorageService.save(file, uuid);

                CategoryImage img = new CategoryImage();
                img.setName(file.getOriginalFilename());
                img.setUrl(url);
                img.setUuid(uuid);
                img.setStatus("1");
                img.setCategory(category);
                return img;
            }).collect(Collectors.toList());

            categoryImageRepository.saveAll(newImages);


            category.setCategoryImage(newImages);
        }

        return categoryRepoMapper.toDTO(category);
    }
    public ResponseEntity<byte[]> exportCategoriesToExcel(String name, String categorycode,
                                                          LocalDateTime createdFrom, LocalDateTime createdTo) {


        Pageable pageable = PageRequest.of(0, 10_000);
        List<Category> categories = categoryRepository
                .searchCategories(name, categorycode, createdFrom, createdTo, pageable)
                .getContent();

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Categories");

            // Header
            Row header = sheet.createRow(0);
            String[] columns = {"ID", "Tên", "Mã", "Mô tả", "Ngày tạo", "Ngày sửa", "Người tạo", "Người sửa"};
            for (int i = 0; i < columns.length; i++) {
                Cell cell = header.createCell(i);
                cell.setCellValue(columns[i]);
            }


            int rowIdx = 1;
            for (Category c : categories) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(c.getId());
                row.createCell(1).setCellValue(c.getName());
                row.createCell(2).setCellValue(c.getCategorycode());
                row.createCell(3).setCellValue(c.getDescription() != null ? c.getDescription() : "");
                row.createCell(4).setCellValue(c.getCreateDate() != null ? c.getCreateDate().toString() : "");
                row.createCell(5).setCellValue(c.getModifiedDate() != null ? c.getModifiedDate().toString() : "");
                row.createCell(6).setCellValue(c.getCreatedBy() != null ? c.getCreatedBy() : "");
                row.createCell(7).setCellValue(c.getModifiedBy() != null ? c.getModifiedBy() : "");
            }

            workbook.write(out);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=categories.xlsx")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(out.toByteArray());

        } catch (Exception e) {
            throw new RuntimeException("pc.export.failed", e);
        }
    }


    @Transactional
    public void softDelete(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "category.notfound.with.id"));

        category.setStatus("0");
        category.setModifiedDate(LocalDateTime.now());

        categoryRepository.save(category);

    }

}


