package com.example.webppt.utils;

import java.awt.Color;

import org.apache.poi.sl.usermodel.PaintStyle;
import org.apache.poi.sl.usermodel.PaintStyle.SolidPaint;
import org.apache.poi.xslf.usermodel.XSLFTextRun;
import org.springframework.stereotype.Component;

@Component
public class ColorUtils {

    public static String toHexColor(Color color) {
        return String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
    }

    public static String extractFontColor(XSLFTextRun textRun) {
        PaintStyle paintStyle = textRun.getFontColor();
        if (paintStyle instanceof SolidPaint) {
            SolidPaint solidPaint = (SolidPaint) paintStyle;
            Color awtColor = solidPaint.getSolidColor().getColor();
            if (awtColor != null) {
                return toHexColor(awtColor);
            }
        }
        return "#000000";
    }

    public static String extractPaintColor(PaintStyle paintStyle) {
        if (paintStyle instanceof SolidPaint) {
            Color color = ((SolidPaint) paintStyle).getSolidColor().getColor();
            return toHexColor(color);
        }
        return null;
    }

    public static Color parseColor(String hexColor) {
        if (hexColor == null || hexColor.isEmpty())
            return null;
        try {
            return Color.decode(hexColor.startsWith("#") ? hexColor : "#" + hexColor);
        } catch (NumberFormatException e) {
            return Color.BLACK;
        }
    }
}
