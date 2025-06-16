package com.example.baitapt1.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CategoryRepoDTO {
    private long id;
    private String name;
    private String categorycode;
    private String description;
    private String status;
    private List<ImageCategoryDTO> images;
    private LocalDateTime createDate;
    private String createdBy;



}

