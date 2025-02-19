package com.example.webppt.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageService {
    @Value("${file.upload-dir}")
    private String uploadDir;

    public String storeFile(MultipartFile file) throws IOException {
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path targetPath = Paths.get(uploadDir).resolve(fileName);
        Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
        return targetPath.toString();
    }

    public String storeImage(byte[] imageData) throws IOException {
        String fileName = UUID.randomUUID() + ".png"; // Save as PNG
        Path targetPath = Paths.get(uploadDir).resolve(fileName);
        Files.write(targetPath, imageData);
        return targetPath.toString();
    }
}