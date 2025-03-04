package com.example.webppt.service;

import com.example.webppt.model.*;
import com.example.webppt.utils.SlideElementUtils;
import com.example.webppt.utils.StyleApplicationUtils;

import org.apache.poi.xslf.usermodel.*;
import org.springframework.stereotype.Service;

@Service
public class TextGeneration {

    public void addTextShape(XSLFSlide slide, SlideElement element) {
        XSLFTextShape textShape = slide.createTextBox();
        SlideElementUtils.applyPositionAndSize(textShape, element);

        // Set text content
        String text = (String) element.getContent().get("text");
        XSLFTextParagraph p = textShape.addNewTextParagraph();
        XSLFTextRun run = p.addNewTextRun();
        run.setText(text);
        // Apply all styles from the style map at once
        StyleApplicationUtils.applyStyles(run, element.getStyle());
    }
}
