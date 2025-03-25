package com.example.aspose.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

@Service
public class FileStorageService {
    @Value("${file.upload-dir}")
    private String uploadDir;

    public FileStorageService(@Value("${file.upload-dir}") String uploadDir) {
        this.uploadDir = uploadDir;
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            try {
                Files.createDirectories(uploadPath);
            } catch (IOException e) {
                throw new RuntimeException("Could not create upload directory: " + uploadDir, e);
            }
        }
    }

    public String storeFile(MultipartFile file) throws IOException {
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path targetPath = Paths.get(uploadDir).resolve(fileName);
        Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
        return targetPath.toString();
    }

    public String storeImage(byte[] imageData) throws IOException {
        String fileName = UUID.randomUUID() + ".png";
        Path targetPath = Paths.get(uploadDir).resolve(fileName);
        Files.write(targetPath, imageData);
        return targetPath.toString();
    }

    public byte[] loadImage(String filePath) throws IOException {
        Path path = Paths.get(uploadDir).resolve(Paths.get(filePath).getFileName());
        return Files.readAllBytes(path);
    }
}
