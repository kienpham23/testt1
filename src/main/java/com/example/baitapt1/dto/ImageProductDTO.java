package com.example.baitapt1.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImageProductDTO {
    @NotBlank
    @Size(max =100)
    private String name;
    private String url;
    private String uuid;
}
