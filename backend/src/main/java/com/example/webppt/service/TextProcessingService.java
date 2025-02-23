package com.example.webppt.service;

import com.example.webppt.model.*;
import com.example.webppt.utils.ColorUtils;
import com.example.webppt.utils.SlideElementUtils;

import org.apache.poi.xslf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

import org.apache.poi.sl.usermodel.PaintStyle;
import org.apache.poi.sl.usermodel.PaintStyle.SolidPaint;

import java.awt.Color;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

@Service
public class TextProcessingService {
    @Autowired
    private SlideElementUtils slideElementUtils;
    @Autowired
    private ColorUtils colorUtils;

    public SlideElement processTextShape(XSLFShape shape, Presentation presentation) {
        SlideElement element = slideElementUtils.createSlideElement(ElementType.TEXT, shape, presentation);

        // Extract text content
        XSLFTextShape textShape = getTextShape(shape);
        element.setContent(textShape != null ? textShape.getText() : "");

        Map<String, Object> style = new HashMap<>();
        if (textShape != null) {
            textShape.getTextParagraphs().forEach(paragraph -> {
                paragraph.getTextRuns().forEach(textRun -> {
                    extractStyles(textRun, style);
                });
            });

            // Then process paragraph styles
            textShape.getTextParagraphs().forEach(paragraph -> {
                extractStyles(paragraph, style);
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

    private void extractStyles(Object styleObject, Map<String, Object> styleMap) {
        try {
            for (Method method : styleObject.getClass().getMethods()) {
                if (isStyleGetter(method)) {
                    String styleKey = getStyleKey(method);
                    Object value = method.invoke(styleObject);

                    if (value != null && isSimpleType(value)) {
                        value = convertSpecialTypes(value);
                        styleMap.put(styleKey, value);
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Error extracting styles: " + e.getMessage());
        }
    }

    private boolean isSimpleType(Object value) {
        return value instanceof String ||
                value instanceof Number ||
                value instanceof Boolean ||
                value instanceof Color ||
                value instanceof SolidPaint ||
                value instanceof Enum;
    }

    private boolean isStyleGetter(Method method) {
        int mod = method.getModifiers();
        return Modifier.isPublic(mod) &&
                !Modifier.isStatic(mod) &&
                (method.getName().startsWith("get") || method.getName().startsWith("is")) &&
                method.getParameterCount() == 0 &&
                !method.getName().equals("getClass") &&
                !method.getName().equals("getTextParagraphs") &&
                !method.getName().equals("getTextRuns");
    }

    private String getStyleKey(Method method) {
        String name = method.getName();
        if (name.startsWith("get")) {
            name = name.substring(3);
        } else if (name.startsWith("is")) {
            name = name.substring(2);
        }
        return Character.toLowerCase(name.charAt(0)) + name.substring(1);
    }

    private Object convertSpecialTypes(Object value) {
        if (value instanceof Color) {
            return colorUtils.toHexColor((Color) value);
        }
        if (value instanceof PaintStyle) {
            return colorUtils.extractPaintColor((PaintStyle) value);
        }
        if (value instanceof Enum) {
            return ((Enum<?>) value).name().toLowerCase();
        }
        return value;
    }
}
