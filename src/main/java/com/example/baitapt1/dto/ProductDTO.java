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
    @NotBlank(message = "Tên sản phẩm không được để trống")
    @Size(max = 100, message = "Tên sản phẩm tối đa 100 ký tự")
    private String name ;
    @NotBlank(message = "Mã sản phẩm không được để trống")
    @Size(max=50)
    private String  productCode;
    @Size(max = 200, message = "Mô tả sản phẩm tối đa 200 ký tự")
    private String description;

    @NotNull(message = "Giá không được để trống")
    @PositiveOrZero(message = "Giá phải >= 0")
    private double price;

    @NotNull(message = "Số lượng không được để trống")
    @Min(value = 0,message = "Số lượng phải >= 0")
    private Long quantity;

    @NotEmpty(message = "Danh sách loại sản phẩm không được để trống")
    private List<Long> categoryIds;

    private List<MultipartFile> image;


}
