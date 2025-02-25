package com.example.webppt.service;

import com.example.webppt.model.*;
import com.example.webppt.utils.ColorUtils;
import com.example.webppt.utils.SlideElementUtils;
import com.example.webppt.utils.SvgUtils;
import org.apache.poi.xslf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service
public class PresentationGenerationService {

    @Autowired
    FileStorageService fileStorageService;
    @Autowired
    SlideElementUtils slideElementUtils;
    @Autowired
    ColorUtils colorUtils;
    @Autowired
    SvgUtils svgUtils;
    @Autowired
    TextGeneration textGeneration;
    @Autowired
    ImageGeneration imageGeneration;
    @Autowired
    ShapeGeneration shapeGeneration;

    public byte[] generatePresentation(Presentation presentation) throws IOException {
        try (XMLSlideShow ppt = new XMLSlideShow()) {
            // Set slide dimensions from original
            Dimension pageSize = new Dimension(
                    (int) presentation.getWidth(),
                    (int) presentation.getHeight());
            ppt.setPageSize(pageSize);

            // Process slides
            for (Slide dbSlide : presentation.getSlides()) {
                XSLFSlide pptSlide = ppt.createSlide();
                processSlideElements(pptSlide, dbSlide.getElements());
            }

            // Write to byte array
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ppt.write(out);
            return out.toByteArray();
        }
    }

    private void processSlideElements(XSLFSlide slide, List<SlideElement> elements) {
        for (SlideElement element : elements) {
            try {
                switch (element.getType()) {
                    case TEXT:
                        textGeneration.addTextShape(slide, element);
                        break;
                    case IMAGE:
                        imageGeneration.addImage(slide, element);
                        break;
                    case SHAPE:
                        // shapeGeneration.addAutoShape(slide, element);
                        break;
                    case TABLE:
                        // addTable(slide, element);
                        break;
                }
            } catch (Exception e) {
                System.err.println("Error processing element " + element.getId() + ": " + e.getMessage());
            }
        }
    }
}