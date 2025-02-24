package com.example.webppt.service;

import java.io.IOException;

import org.apache.poi.xslf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.webppt.model.*;

@Service
public class SlideProcessingService {
    @Autowired
    ShapeProcessingService shapeProcessingService;
    @Autowired
    TextProcessingService textProcessingService;
    @Autowired
    ImageProcessingService imageProcessingService;
    @Autowired
    TableProcessingService tableProcessingService;

    public Slide processSlide(XSLFSlide slide, Presentation presentation) throws IOException {
        Slide dbSlide = new Slide();
        dbSlide.setSlideNumber(slide.getSlideNumber());
        dbSlide.setPresentation(presentation);

        for (XSLFShape shape : slide.getShapes()) {
            if (shape instanceof XSLFTable) {
                SlideElement element = tableProcessingService.processTable((XSLFTable) shape, presentation);
                element.setSlide(dbSlide);
                dbSlide.getElements().add(element);
            } else if (shape instanceof XSLFPictureShape) {
                SlideElement element = imageProcessingService.processImage((XSLFPictureShape) shape, presentation);
                element.setSlide(dbSlide);
                dbSlide.getElements().add(element);
            } else if (shape instanceof XSLFAutoShape) {
                XSLFAutoShape autoShape = (XSLFAutoShape) shape;
                SlideElement shapeElement = shapeProcessingService.processAutoShape(autoShape, presentation);
                shapeElement.setSlide(dbSlide);
                dbSlide.getElements().add(shapeElement);

                if (!autoShape.getText().isEmpty()) {
                    SlideElement textElement = textProcessingService.processTextShape(autoShape, presentation);
                    textElement.setSlide(dbSlide);
                    dbSlide.getElements().add(textElement);
                }
            } else if (shape instanceof XSLFTextShape) {
                SlideElement element = textProcessingService.processTextShape((XSLFTextShape) shape, presentation);
                element.setSlide(dbSlide);
                dbSlide.getElements().add(element);
            }
        }
        return dbSlide;
    }
}
