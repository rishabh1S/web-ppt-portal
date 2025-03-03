package com.example.webppt.service;

import com.example.webppt.model.*;
import com.example.webppt.utils.ColorUtils;
import com.example.webppt.utils.SlideElementUtils;
import org.apache.poi.xslf.usermodel.*;
import org.springframework.stereotype.Service;
import java.util.Map;

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
        // Apply styles
        applyTextStyles(run, element.getStyle());
    }

    private void applyTextStyles(XSLFTextRun run, Map<String, Object> styles) {
        if (styles.containsKey("fontSize")) {
            run.setFontSize(((Number) styles.get("fontSize")).doubleValue());
        }
        if (styles.containsKey("fontColor")) {
            run.setFontColor(ColorUtils.parseColor((String) styles.get("fontColor")));
        }
    }
}
