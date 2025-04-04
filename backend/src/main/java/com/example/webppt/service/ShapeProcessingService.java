package com.example.webppt.service;

import com.example.webppt.model.Presentation;
import com.example.webppt.model.SlideElement;
import com.example.webppt.model.ElementType;
import com.example.webppt.utils.SlideElementUtils;
import com.example.webppt.utils.StyleExtractionUtils;
import com.example.webppt.utils.SvgUtils;

import org.apache.poi.xslf.usermodel.*;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class ShapeProcessingService {

    public SlideElement processAutoShape(XSLFAutoShape autoShape, Presentation presentation) {
        SlideElement element = SlideElementUtils.createSlideElement(ElementType.SHAPE, autoShape, presentation);

        // Generate SVG path data
        String svgPath = SvgUtils.generateSVGPath(autoShape);
        element.setContent(Map.of("svgPath", svgPath));

        // Extract style properties
        Map<String, Object> style = new HashMap<>();

        StyleExtractionUtils.extractStyles(autoShape, style);

        element.setStyle(style);

        return element;
    }
}
