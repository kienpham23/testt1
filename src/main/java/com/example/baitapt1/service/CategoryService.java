package com.example.baitapt1.service;

import com.example.baitapt1.dto.CategoryDTO;
import com.example.baitapt1.dto.CategoryRepoDTO;
import com.example.baitapt1.dto.CategorySearchRequest;
import com.example.baitapt1.dto.ImageCategoryDTO;
import com.example.baitapt1.entity.Category;
import com.example.baitapt1.entity.CategoryImage;
import com.example.baitapt1.mapper.CategoryRepoMapper;
import com.example.baitapt1.repository.CategoryImageRepository;
import com.example.baitapt1.repository.CategoryRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
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
public class CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private CategoryRepoMapper categoryRepoMapper;
    @Autowired
    private CategoryImageRepository categoryImageRepository;
    @Autowired
    private FileStorageService fileStorageService;
    @Autowired
    private MessageSource messageSource;

    // Giữ nguyên method tạo mới (có thể sửa để dùng DTO đúng nếu muốn)
    @Transactional
    public CategoryRepoDTO Categorycode(CategoryDTO dto) {
        if (categoryRepository.existsByCategorycode(dto.getCategorycode())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "category.code.exists");
        }
        Category category = new Category();
        category.setName(dto.getName());
        category.setCategorycode(dto.getCategorycode());
        category.setDescription(dto.getDescription());
        category.setCreatedBy("admin");

        categoryRepository.save(category); // cần lưu trước để lấy id liên kết ảnh

        List<CategoryImage> categoryImages = new ArrayList<>();
        if (dto.getImage() != null && dto.getImage().length > 0) {
            for (MultipartFile file : dto.getImage()) {
                if (file != null && !file.isEmpty()) {
                    String uuid = UUID.randomUUID().toString();
                    String url = fileStorageService.save(file, uuid);

                    CategoryImage img = new CategoryImage();
                    img.setName(file.getOriginalFilename());
                    img.setUrl(url);
                    img.setUuid(uuid);
                    img.setCategory(category);
                    categoryImages.add(img);
                }
            }
            categoryImageRepository.saveAll(categoryImages);
        category.setCategoryImage(categoryImages);
        }

        return categoryRepoMapper.toDTO(category);
    }


    // Tìm kiếm theo DTO gom param + phân trang
    public Map<String, Object> searchCategories(CategorySearchRequest searchRequest, Pageable pageable) {
        Page<Category> page = categoryRepository.searchCategories(
                searchRequest.getName(),
                searchRequest.getCategorycode(),
                searchRequest.getCreatedFrom(),
                searchRequest.getCreatedTo(),
                pageable
        );

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

        Map<String, Object> pagination = new LinkedHashMap<>();
        pagination.put("currentPage", page.getNumber() );
        pagination.put("pageSize", page.getSize());
        pagination.put("totalElements", page.getTotalElements());
        pagination.put("totalPages", page.getTotalPages());
        pagination.put("hasNext", page.hasNext());
        pagination.put("hasPrevious", page.hasPrevious());

        response.put("pagination", pagination);
        return response;
    }


    public ResponseEntity<byte[]> exportCategoriesToExcel(CategorySearchRequest searchRequest) {

        List<Category> categories = categoryRepository.searchCategories(
                searchRequest.getName(),
                searchRequest.getCategorycode(),
                searchRequest.getCreatedFrom(),
                searchRequest.getCreatedTo()
        );

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Categories");

            String[] columns = {"ID", "Tên", "Mã", "Mô tả", "Ngày tạo", "Ngày sửa", "Người tạo", "Người sửa"};
            Row header = sheet.createRow(0);
            for (int i = 0; i < columns.length; i++) {
                header.createCell(i).setCellValue(columns[i]);
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

    // Cập nhật category theo categorycode
    @Transactional
    public CategoryRepoDTO updateCategoryByCode(String categorycode, CategoryDTO dto) {
        Category category = categoryRepository.findByCategoryCodeWithImages(categorycode)
                .filter(c -> "1".equals(c.getStatus()))
                .orElseThrow(() -> new RuntimeException("category.notfound.or.deleted"));

        category.setName(dto.getName());
        category.setDescription(dto.getDescription());
        category.setModifiedBy("admin");
        category.setModifiedDate(LocalDateTime.now());
        categoryRepository.save(category);

        List<CategoryImage> currentImages = categoryImageRepository.findByCategoryIdAndStatus(category.getId(), "1");
        Set<String> keepUuids = new HashSet<>(dto.getKeepImageUuids() != null ? dto.getKeepImageUuids() : List.of());

        // Vô hiệu hóa ảnh không giữ lại
        List<CategoryImage> toDeactivate = currentImages.stream()
                .filter(img -> !keepUuids.contains(img.getUuid()))
                .toList();
        toDeactivate.forEach(img -> img.setStatus("0"));
        categoryImageRepository.saveAll(toDeactivate);

        // Lưu ảnh mới nếu có
        if (dto.getImage() != null && dto.getImage().length > 0) {
            List<CategoryImage> newImages = Arrays.stream(dto.getImage())
                    .filter(file -> file != null && !file.isEmpty())
                    .map(file -> {
                        String uuid = UUID.randomUUID().toString();
                        String url = fileStorageService.save(file, uuid);

                        CategoryImage img = new CategoryImage();
                        img.setName(file.getOriginalFilename());
                        img.setUrl(url);
                        img.setUuid(uuid);
                        img.setStatus("1");
                        img.setCategory(category);
                        return img;
                    })
                    .toList();

            categoryImageRepository.saveAll(newImages);
        }


        List<CategoryImage> updatedImages = categoryImageRepository.findByCategoryIdAndStatus(category.getId(), "1");
        category.setCategoryImage(updatedImages);

        return categoryRepoMapper.toDTO(category);
    }

    // Xóa mềm theo id
    @Transactional
    public void softDelete(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND, "category.notfound.with.id"));

        category.setStatus("0");
        category.setModifiedDate(LocalDateTime.now());

        categoryRepository.save(category);
    }
}
