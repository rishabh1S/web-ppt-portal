package com.example.webppt.utils;

import java.awt.Color;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;

import org.apache.poi.sl.usermodel.PaintStyle;
import org.apache.poi.sl.usermodel.PaintStyle.SolidPaint;
import org.springframework.stereotype.Component;

@Component
public class StyleExtractionUtils {
    private ColorUtils colorUtils = new ColorUtils();

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
        name = name.replaceAll("^(get|is)", "");
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

    public void extractStyles(Object styleObject, Map<String, Object> styleMap) {
        try {
            for (Method method : styleObject.getClass().getMethods()) {
                if (isStyleGetter(method)) {
                    String styleKey = getStyleKey(method);
                    Object value = method.invoke(styleObject);

                    if (value != null && isSimpleType(value)) {
                        styleMap.put(styleKey, convertSpecialTypes(value));
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Style extraction error: " + e.getMessage());
        }
    }
}
