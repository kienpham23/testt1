package com.example.baitapt1.dto;


import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;


import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {
    @NotBlank(message = "{product.name.notblank}")
    @Size(max = 100, message = "{product.name.size.max}")
    private String name;

    @NotBlank(message = "{product.code.notblank}")
    @Size(max = 50, message = "{product.code.size.max}")
    private String productCode;

    @Size(max = 200, message = "{product.description.size.max}")
    private String description;

    @NotNull(message = "{product.price.notnull}")
    @PositiveOrZero(message = "{product.price.positive}")
    private double price;

    @NotNull(message = "{product.quantity.notnull}")
    @Min(value = 0, message = "{product.quantity.min}")
    private Long quantity;

    @NotEmpty(message = "{product.categoryIds.notempty}")
    private List<Long> categoryIds;

    private List<MultipartFile> image;



}
