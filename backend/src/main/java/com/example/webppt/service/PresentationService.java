package com.example.webppt.service;

import com.aspose.slides.IAutoShape;
import com.aspose.slides.ISlide;
import com.aspose.slides.Presentation;
import com.aspose.slides.SaveFormat;
import com.aspose.slides.ShapeType;
import com.example.webppt.model.Slide;
import com.example.webppt.repository.PresentationRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Service
public class PresentationService {
    @Autowired
    PresentationRepository presentationRepo;
    @Autowired
    FileStorageService fileStorageService;

    @Transactional
    public com.example.webppt.model.Presentation processPresentation(MultipartFile file) throws IOException {
        // 1. Save the original file
        String filePath = fileStorageService.storeFile(file);

        // 2. Load the PPTX file using Aspose.Slides
        Presentation asposePresentation = null;
        try (InputStream inputStream = file.getInputStream()) {
            asposePresentation = new Presentation(inputStream);

            // 3. Create a new model.Presentation entity
            com.example.webppt.model.Presentation dbPresentation = new com.example.webppt.model.Presentation();
            dbPresentation.setOriginalFilePath(filePath);
            dbPresentation.setName(file.getOriginalFilename());
            dbPresentation.setWidth(asposePresentation.getSlideSize().getSize().getWidth());
            dbPresentation.setHeight(asposePresentation.getSlideSize().getSize().getHeight());

            if (asposePresentation.getSlides().size() == 0) {
                throw new IOException("The PPTX file contains no slides.");
            }

            // 4. Convert each slide to HTML
            int slideNumber = 1;
            for (ISlide asposeSlide : asposePresentation.getSlides()) {
                try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                    // Create a temporary presentation with only one slide
                    Presentation tempPresentation = new Presentation();
                    try {
                        tempPresentation.getSlides().removeAt(0); // Remove default empty slide
                        tempPresentation.getSlides().addClone(asposeSlide); // Add only the current slide
                        tempPresentation.save(outputStream, SaveFormat.Html);
                    } finally {
                        tempPresentation.dispose(); // Close the temporary Aspose.Presentation
                    }
                    // Store extracted HTML as a string
                    String htmlContent = outputStream.toString("UTF-8");

                    // Create a new Slide entity and store the HTML
                    Slide dbSlide = new Slide();
                    dbSlide.setSlideNumber(slideNumber++);
                    dbSlide.setHtmlContent(htmlContent);
                    dbSlide.setPresentation(dbPresentation);
                    dbPresentation.getSlides().add(dbSlide);
                }
            }
            // 5. Save the presentation along with its slides
            return presentationRepo.save(dbPresentation);
        } finally {
            if (asposePresentation != null) {
                asposePresentation.dispose(); // Close the main Aspose.Presentation
            }
        }
    }

    public byte[] generatePPTFromHtml(UUID id) {
    com.example.webppt.model.Presentation dbPresentation = presentationRepo.findById(id)
            .orElseThrow(() -> new RuntimeException("Presentation not found"));

    try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
        Presentation ppt = new Presentation(); // Create a new Aspose PPTX

        for (Slide dbSlide : dbPresentation.getSlides()) {
            ISlide slide = ppt.getSlides().addEmptySlide(ppt.getLayoutSlides().get_Item(0));

            // Add a text box to hold the HTML content
            IAutoShape textBox = slide.getShapes().addAutoShape(ShapeType.Rectangle, 50, 50, 600, 400);
            textBox.getTextFrame().setText(stripHtmlTags(dbSlide.getHtmlContent()));
        }

        ppt.save(outputStream, SaveFormat.Pptx);
        return outputStream.toByteArray();
    } catch (IOException e) {
        throw new RuntimeException("Failed to generate PPT", e);
    }
}

/**
 * Helper method to remove HTML tags from the content.
 */
private String stripHtmlTags(String htmlContent) {
    return htmlContent.replaceAll("<[^>]*>", ""); // Basic HTML stripping
}


}