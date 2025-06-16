package com.example.baitapt1.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;


import java.util.List;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CategoryDTO {

        @NotBlank
        @Size(max = 100)
        private String name;
        @NotBlank
        @Size(max = 50)
        private String categorycode;
        @Size(max = 200)
        private String description;
        private List<MultipartFile> image;

}
