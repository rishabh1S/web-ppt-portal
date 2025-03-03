package com.example.webppt.service;

import com.example.webppt.model.*;
import com.example.webppt.utils.SlideElementUtils;
import com.example.webppt.utils.StyleExtractionUtils;

import org.apache.poi.xslf.usermodel.*;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class TextProcessingService {

    public SlideElement processTextShape(XSLFShape shape, Presentation presentation) {
        SlideElement element = SlideElementUtils.createSlideElement(ElementType.TEXT, shape, presentation);

        // Extract text content
        XSLFTextShape textShape = getTextShape(shape);
        element.setContent(Map.of("text", textShape != null ? textShape.getText() : ""));

        Map<String, Object> style = new HashMap<>();
        if (textShape != null) {
            textShape.getTextParagraphs().forEach(paragraph -> {
                StyleExtractionUtils.extractStyles(paragraph, style);
                paragraph.getTextRuns().forEach(textRun -> StyleExtractionUtils.extractStyles(textRun, style));
            });
        }

        element.setStyle(style);
        return element;
    }

    private XSLFTextShape getTextShape(XSLFShape shape) {
        if (shape instanceof XSLFTextShape)
            return (XSLFTextShape) shape;
        if (shape instanceof XSLFAutoShape)
            return (XSLFAutoShape) shape;
        return null;
    }
}
