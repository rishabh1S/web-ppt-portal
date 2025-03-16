package com.example.aspose.service;

import com.aspose.slides.ISlide;
import com.aspose.slides.License;
import com.aspose.slides.Presentation;
import com.aspose.slides.SaveFormat;
import com.example.aspose.model.Slide;
import com.example.aspose.repository.PresentationRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
public class PresentationService {
    @Autowired
    PresentationRepository presentationRepo;
    @Autowired
    FileStorageService fileStorageService;

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
        // 1. Save the original file
        String filePath = fileStorageService.storeFile(file);

        // 2. Load the PPTX file using Aspose.Slides
        Presentation asposePresentation = new Presentation(file.getInputStream()); // No try-with-resources
        try {
            com.example.aspose.model.Presentation dbPresentation = new com.example.aspose.model.Presentation();
            dbPresentation.setOriginalFilePath(filePath);
            dbPresentation.setName(file.getOriginalFilename());
            dbPresentation.setWidth(asposePresentation.getSlideSize().getSize().getWidth());
            dbPresentation.setHeight(asposePresentation.getSlideSize().getSize().getHeight());

            if (asposePresentation.getSlides().size() == 0) {
                throw new IOException("The PPTX file contains no slides.");
            }

            int slideNumber = 1;
            for (ISlide asposeSlide : asposePresentation.getSlides()) {
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                Presentation tempPresentation = new Presentation();
                try {
                    tempPresentation.getSlides().removeAt(0);
                    tempPresentation.getSlides().addClone(asposeSlide);
                    tempPresentation.save(outputStream, SaveFormat.Html5);
                } finally {
                    tempPresentation.dispose();
                }

                String htmlContent = outputStream.toString("UTF-8");

                Slide dbSlide = new Slide();
                dbSlide.setSlideNumber(slideNumber++);
                dbSlide.setHtmlContent(htmlContent);
                dbSlide.setPresentation(dbPresentation);
                dbPresentation.getSlides().add(dbSlide);
            }

            return presentationRepo.save(dbPresentation);
        } finally {
            asposePresentation.dispose();
        }
    }
}