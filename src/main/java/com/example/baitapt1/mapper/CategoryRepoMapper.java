package com.example.baitapt1.mapper;

import com.example.baitapt1.dto.CategoryRepoDTO;
import com.example.baitapt1.dto.ImageCategoryDTO;
import com.example.baitapt1.entity.Category;
import com.example.baitapt1.entity.CategoryImage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


import java.util.List;

@Mapper(componentModel = "spring")
public interface CategoryRepoMapper {
    @Mapping(source = "id", target = "id")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "categorycode", target = "categorycode")
    @Mapping(source = "description", target = "description")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "categoryImage", target = "images")
    @Mapping(source = "createDate", target = "createDate")
    @Mapping(source = "createdBy", target = "createdBy")

    CategoryRepoDTO toDTO(Category category);


     List<CategoryRepoDTO> toDTOs(List<Category> categories);



    List<ImageCategoryDTO> toImageDTOs(List<CategoryImage> images);
}
