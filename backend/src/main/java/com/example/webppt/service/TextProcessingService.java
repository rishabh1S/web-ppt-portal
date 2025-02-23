package com.example.webppt.service;

import com.example.webppt.model.*;
import com.example.webppt.utils.ColorUtils;
import com.example.webppt.utils.SlideElementUtils;

import org.apache.poi.xslf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class TextProcessingService {
    @Autowired
    private SlideElementUtils slideElementUtils;
    @Autowired
    ColorUtils colorUtils;

    SlideElement processTextShape(XSLFShape shape, Presentation presentation) {
        SlideElement element = slideElementUtils.createSlideElement(ElementType.TEXT, shape, presentation);

        // Extract text content
        String text = "";
        if (shape instanceof XSLFTextShape) {
            text = ((XSLFTextShape) shape).getText();
        } else if (shape instanceof XSLFAutoShape) {
            text = ((XSLFAutoShape) shape).getText();
        }
        element.setContent(text);

        // Font styling
        Map<String, Object> style = new HashMap<>();
        if (shape instanceof XSLFTextShape) {
            XSLFTextShape textShape = (XSLFTextShape) shape;
            if (!textShape.getTextParagraphs().isEmpty()) {
                XSLFTextParagraph paragraph = textShape.getTextParagraphs().get(0);
                if (!paragraph.getTextRuns().isEmpty()) {
                    XSLFTextRun textRun = paragraph.getTextRuns().get(0);
                    style.put("fontSize", textRun.getFontSize());
                    style.put("color", colorUtils.extractFontColor(textRun));
                }
            }
        }
        element.setStyle(style);

        return element;
    }
}
