package com.example.baitapt1.dto;



import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductReponDTO {
    private long id;
    private String name;
    private String description;
    private double price;
    private String  productCode;
    private long quantity;
    private String status;
    private List<CategoryProDTO> category;
    private List<ImageProductDTO> images;
    private LocalDateTime createdDate;
    private String createdBy;
}
