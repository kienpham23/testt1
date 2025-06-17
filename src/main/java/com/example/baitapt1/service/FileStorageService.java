package com.example.baitapt1.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
@Service
public class FileStorageService {
    private final String uploadDir = "D:/uploads";

    public String save(MultipartFile file, String uuid) {
        try {
            String extension = Objects.requireNonNull(file.getOriginalFilename())
                    .substring(file.getOriginalFilename().lastIndexOf("."));
            String fileName = uuid + extension;
            Path filePath = Paths.get(uploadDir).resolve(fileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            return "/files/" + fileName;
        } catch (IOException e) {
            throw new RuntimeException("Lỗi lưu file " + file.getOriginalFilename(), e);
        }
    }
}
