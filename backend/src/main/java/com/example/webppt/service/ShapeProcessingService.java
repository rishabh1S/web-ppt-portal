package com.example.webppt.service;

import com.example.webppt.model.Presentation;
import com.example.webppt.model.SlideElement;
import com.example.webppt.model.ElementType;
import com.example.webppt.utils.ColorUtils;
import com.example.webppt.utils.SlideElementUtils;
import com.example.webppt.utils.StyleExtractionUtils;
import com.example.webppt.utils.SvgUtils;

import org.apache.poi.xslf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    SlideElementUtils slideElementUtils;
    @Autowired
    StyleExtractionUtils styleExtractionUtils;

    public SlideElement processAutoShape(XSLFAutoShape autoShape, Presentation presentation) {
        SlideElement element = slideElementUtils.createSlideElement(ElementType.SHAPE, autoShape, presentation);

        // Generate SVG path data
        String svgPath = svgUtils.generateSVGPath(autoShape);
        element.setContent(Map.of("svgPath", svgPath));

        // Extract style properties
        Map<String, Object> style = new HashMap<>();

        styleExtractionUtils.extractStyles(autoShape, style);

        element.setStyle(style);

        return element;
    }
}
