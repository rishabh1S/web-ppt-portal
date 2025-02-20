package com.example.webppt.service;

import com.example.webppt.model.Presentation;
import com.example.webppt.model.Slide;
import com.example.webppt.model.SlideElement;
import com.example.webppt.model.ElementType;
import com.example.webppt.repository.PresentationRepository;

import org.apache.poi.sl.usermodel.PaintStyle;
import org.apache.poi.sl.usermodel.PaintStyle.SolidPaint;
import org.apache.poi.ss.usermodel.Color;
import org.apache.poi.xslf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.awt.Dimension;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class PresentationService {
    @Autowired
    PresentationRepository presentationRepo;
    @Autowired
    FileStorageService fileStorageService;

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

            // Process slides
            for (XSLFSlide slide : slideShow.getSlides()) {
                Slide dbSlide = processSlide(slide, presentation);
                presentation.getSlides().add(dbSlide);
            }

            return presentationRepo.save(presentation);
        }
    }

    private Slide processSlide(XSLFSlide slide, Presentation presentation) throws IOException {
        Slide dbSlide = new Slide();
        dbSlide.setSlideNumber(slide.getSlideNumber());
        dbSlide.setPresentation(presentation);

        // Extract elements (text, images)
        for (XSLFShape shape : slide.getShapes()) {
            if (shape instanceof XSLFTextShape) {
                SlideElement element = processTextShape((XSLFTextShape) shape, presentation);
                element.setSlide(dbSlide);
                dbSlide.getElements().add(element);
            } else if (shape instanceof XSLFPictureShape) {
                SlideElement element = processImage((XSLFPictureShape) shape, presentation);
                element.setSlide(dbSlide);
                dbSlide.getElements().add(element);
            }
        }
        return dbSlide;
    }

    // private SlideElement processTextShape(XSLFTextShape textShape, Presentation presentation) {
    //     SlideElement element = new SlideElement();
    //     element.setType(ElementType.TEXT);
    //     element.setContent(textShape.getText());

    //     // Convert points to percentages
    //     element.setX(convertPointsToPercentage(textShape.getAnchor().getX(), presentation.getWidth()));
    //     element.setY(convertPointsToPercentage(textShape.getAnchor().getY(), presentation.getHeight()));
    //     element.setWidth(convertPointsToPercentage(textShape.getAnchor().getWidth(), presentation.getWidth()));
    //     element.setHeight(convertPointsToPercentage(textShape.getAnchor().getHeight(), presentation.getHeight()));

    //     // Populate style
    //     Map<String, Object> style = new HashMap<>();
    //     style.put("fontSize", textShape.getTextParagraphs().get(0).getTextRuns().get(0).getFontSize());
    //     style.put("color", textShape.getTextParagraphs().get(0).getTextRuns().get(0).getFontColor().toString());
    //     element.setStyle(style);

    //     return element;
    // }

    private SlideElement processTextShape(XSLFTextShape textShape, Presentation presentation) {
        SlideElement element = new SlideElement();
        element.setType(ElementType.TEXT);
        element.setContent(textShape.getText());
    
        // Convert points to percentages
        element.setX(convertPointsToPercentage(textShape.getAnchor().getX(), presentation.getWidth()));
        element.setY(convertPointsToPercentage(textShape.getAnchor().getY(), presentation.getHeight()));
        element.setWidth(convertPointsToPercentage(textShape.getAnchor().getWidth(), presentation.getWidth()));
        element.setHeight(convertPointsToPercentage(textShape.getAnchor().getHeight(), presentation.getHeight()));
    
        // Populate style
        Map<String, Object> style = new HashMap<>();
        XSLFTextRun textRun = textShape.getTextParagraphs().get(0).getTextRuns().get(0);
    
        style.put("fontSize", textRun.getFontSize());
    
        // Extract the exact font color
        String fontColor = extractFontColor(textRun);
        style.put("color", fontColor);
    
        element.setStyle(style);
        return element;
    }

    private String extractFontColor(XSLFTextRun textRun) {
        PaintStyle paintStyle = textRun.getFontColor();
    
        if (paintStyle instanceof SolidPaint solidPaint) {
            java.awt.Color awtColor = solidPaint.getSolidColor().getColor(); // Correct java.awt.Color usage
            if (awtColor != null) {
                return String.format("#%02x%02x%02x", awtColor.getRed(), awtColor.getGreen(), awtColor.getBlue());
            }
        }
    
        return "#000000"; // Default to black if extraction fails
    }
    

    private SlideElement processImage(XSLFPictureShape pictureShape, Presentation presentation) throws IOException {
        SlideElement element = new SlideElement();
        element.setType(ElementType.IMAGE);

        // Save image to storage and get the file path
        String imagePath = fileStorageService.storeImage(pictureShape.getPictureData().getData());
        element.setContent(imagePath);

        // Convert points to percentages
        element.setX(convertPointsToPercentage(pictureShape.getAnchor().getX(), presentation.getWidth()));
        element.setY(convertPointsToPercentage(pictureShape.getAnchor().getY(), presentation.getHeight()));
        element.setWidth(convertPointsToPercentage(pictureShape.getAnchor().getWidth(), presentation.getWidth()));
        element.setHeight(convertPointsToPercentage(pictureShape.getAnchor().getHeight(), presentation.getHeight()));
        return element;
    }

    private double convertPointsToPercentage(double points, double totalPoints) {
        return (points / totalPoints) * 100;
    }
}