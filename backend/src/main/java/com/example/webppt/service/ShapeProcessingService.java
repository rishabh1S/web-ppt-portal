package com.example.webppt.service;

import com.example.webppt.model.Presentation;
import com.example.webppt.model.Slide;
import com.example.webppt.model.SlideElement;
import com.example.webppt.model.ElementType;
import com.example.webppt.utils.ColorUtils;
import com.example.webppt.utils.SlideElementUtils;
import com.example.webppt.utils.SvgUtils;

import org.apache.poi.xslf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

@Service
public class ShapeProcessingService {
    @Autowired
    SvgUtils svgUtils;
    @Autowired
    ColorUtils colorUtils;
    @Autowired
    TextProcessingService textProcessingService;
    @Autowired
    private SlideElementUtils slideElementUtils;

    public void processGroupShape(XSLFGroupShape group, Slide dbSlide, Presentation presentation) {
        for (XSLFShape shape : group.getShapes()) {
            if (shape instanceof XSLFGroupShape) {
                processGroupShape((XSLFGroupShape) shape, dbSlide, presentation);
            } else if (shape instanceof XSLFAutoShape) {
                // Process auto-shapes in the group
                XSLFAutoShape autoShape = (XSLFAutoShape) shape;
                SlideElement shapeElement = processAutoShape(autoShape, presentation);
                shapeElement.setSlide(dbSlide);
                dbSlide.getElements().add(shapeElement);

                if (!autoShape.getText().isEmpty()) {
                    SlideElement textElement = textProcessingService.processTextShape(autoShape, presentation);
                    textElement.setSlide(dbSlide);
                    dbSlide.getElements().add(textElement);
                }
            }
        }
    }

    public SlideElement processAutoShape(XSLFAutoShape autoShape, Presentation presentation) {
        SlideElement element = slideElementUtils.createSlideElement(ElementType.SHAPE, autoShape, presentation);

        // Generate SVG path data
        String svgPath = svgUtils.generateSVGPath(autoShape);
        element.setContent(svgPath);

        // Extract style properties
        Map<String, Object> style = new HashMap<>();

        // Fill color
        Color fillColor = autoShape.getFillColor();
        style.put("fillColor", fillColor != null ? colorUtils.toHexColor(fillColor) : "transparent");

        // Stroke color
        Color strokeColor = autoShape.getLineColor();
        style.put("strokeColor", strokeColor != null ? colorUtils.toHexColor(strokeColor) : "transparent");
        style.put("strokeWidth", autoShape.getLineWidth());

        element.setStyle(style);

        return element;
    }
}
