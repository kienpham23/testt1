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

        @NotBlank(message = "{category.name.notblank}")
        @Size(max = 100, message = "{category.name.size}")
        private String name;

        @NotBlank(message = "{category.code.notblank}")
        @Size(max = 50, message = "{category.code.size}")
        private String categorycode;

        @Size(max = 200, message = "{category.description.size}")
        private String description;

        private MultipartFile[] image;
        private List<String> keepImageUuids;
}

