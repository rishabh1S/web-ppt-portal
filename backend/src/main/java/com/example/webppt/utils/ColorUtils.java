package com.example.webppt.utils;

import java.awt.Color;

import org.apache.poi.sl.usermodel.PaintStyle;
import org.apache.poi.sl.usermodel.PaintStyle.SolidPaint;
import org.apache.poi.xslf.usermodel.XSLFTextRun;
import org.springframework.stereotype.Component;

@Component
public class ColorUtils {

    public String toHexColor(Color color) {
        return String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
    }

    public String extractFontColor(XSLFTextRun textRun) {
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

    public String extractPaintColor(PaintStyle paintStyle) {
        if (paintStyle instanceof SolidPaint) {
            Color color = ((SolidPaint) paintStyle).getSolidColor().getColor();
            return toHexColor(color);
        }
        return null;
    }
}
