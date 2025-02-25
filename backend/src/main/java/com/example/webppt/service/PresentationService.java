package com.example.webppt.service;

import com.example.webppt.model.*;
import com.example.webppt.repository.PresentationRepository;
import com.example.webppt.utils.ColorUtils;

import org.apache.poi.xslf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.awt.Dimension;
import java.io.IOException;
import java.util.UUID;

@Service
public class PresentationService {
    @Autowired
    PresentationRepository presentationRepo;
    @Autowired
    FileStorageService fileStorageService;
    @Autowired
    SlideProcessingService slideProcessingService;
    @Autowired
    ColorUtils colorUtils;
    @Autowired
    PresentationGenerationService generationService;

    @Transactional
    public Presentation processPresentation(MultipartFile file) throws IOException {
        // 1. Save original file
        String filePath = fileStorageService.storeFile(file);

        // 2. Parse PPTX using Apache POI
        try (XMLSlideShow slideShow = new XMLSlideShow(file.getInputStream())) {
            Presentation presentation = new Presentation();
            presentation.setOriginalFilePath(filePath);

            // Get slide dimensions
            Dimension pageSize = slideShow.getPageSize();
            presentation.setWidth(pageSize.getWidth()); // Width in points
            presentation.setHeight(pageSize.getHeight()); // Height in points

            if (slideShow.getSlides().isEmpty()) {
                throw new IOException("The PPTX file contains no slides.");
            }

            // Process slides
            for (XSLFSlide slide : slideShow.getSlides()) {
                Slide dbSlide = slideProcessingService.processSlide(slide, presentation);
                presentation.getSlides().add(dbSlide);
            }

            return presentationRepo.save(presentation);
        }
    }

    public byte[] generatePresentation(UUID presentationId) throws IOException {
        Presentation presentation = presentationRepo.findById(presentationId)
                .orElseThrow(() -> new RuntimeException("Presentation not found"));
        return generationService.generatePresentation(presentation);
    }
}