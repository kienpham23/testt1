package com.example.baitapt1.dto;

import java.time.LocalDateTime;

public interface ProductRepoDTO { Long getId();
    String getName();
    String getProductCode();
    Double getPrice();
    Long getQuantity();
    LocalDateTime getCreatedDate();
    LocalDateTime getModifiedDate();
    String getCategories();
    String getImages();
}
