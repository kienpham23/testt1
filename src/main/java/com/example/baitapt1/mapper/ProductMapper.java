package com.example.baitapt1.mapper;


import com.example.baitapt1.dto.*;
import com.example.baitapt1.entity.Category;
import com.example.baitapt1.entity.Product;
import com.example.baitapt1.entity.ProductCategory;
import com.example.baitapt1.entity.ProductImage;
import org.mapstruct.*;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface ProductMapper {
        @Mapping(target = "id", ignore = true)
        @Mapping(target = "status", ignore = true)
        @Mapping(target = "createdBy", ignore = true)
        @Mapping(target = "createdDate", ignore = true)
        @Mapping(target = "modifiedBy", ignore = true)
        @Mapping(target = "modifiedDate", ignore = true)
        @Mapping(target = "productCategory", ignore = true)
        @Mapping(target = "productsimage", ignore = true)
        Product toEntity(ProductDTO dto);

        @Mapping(source = "product.productCategory", target = "category")
        @Mapping(source = "product.productsimage", target = "images")
        ProductReponDTO toResponse(Product product);


        // map danh sách category
        default List<CategoryProDTO> map(List<ProductCategory> productCategories) {
            return productCategories.stream()
                    .map(pc -> new CategoryProDTO(
                            pc.getCategory().getId(),
                            pc.getCategory().getName()
                    ))
                    .collect(Collectors.toList());
        }

        // map danh sách ảnh
        default List<ImageProductDTO> mapImages(List<ProductImage> productImages) {
            return productImages.stream()
                    .map(img -> new ImageProductDTO(
                            img.getName(),
                            img.getUrl(),
                            img.getUuid()
                    ))
                    .collect(Collectors.toList());
        }
    }



