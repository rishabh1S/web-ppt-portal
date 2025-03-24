package com.example.aspose.service;

import com.aspose.slides.License;
import com.example.aspose.repository.PresentationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class PresentationService {
    @Autowired
    PresentationRepository presentationRepo;
    @Autowired
    FileStorageService fileStorageService;
    @Value("${file.upload-dir}")
    private String uploadDir;

    static {
        try {
            License license = new License();
            license.setLicense("src/main/resources/Aspose.SlidesforJava.lic");
            System.out.println("Aspose.Slides license applied successfully.");
        } catch (Exception e) {
            System.err.println("Failed to load Aspose.Slides license: " + e.getMessage());
        }
    }

    @Transactional
    public com.example.aspose.model.Presentation processPresentation(MultipartFile file) throws IOException {
        return null;
    }
}