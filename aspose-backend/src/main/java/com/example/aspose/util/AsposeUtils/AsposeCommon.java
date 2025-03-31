package com.example.aspose.util.AsposeUtils;

import com.aspose.slides.TextAlignment;
import java.awt.Color;

public class AsposeCommon {

    public static int getTextAlignment(String alignmentStr) {
        switch (alignmentStr.toLowerCase()) {
            case "center":
                return TextAlignment.Center;
            case "right":
                return TextAlignment.Right;
            case "justified":
                return TextAlignment.Justify;
            default:
                return TextAlignment.Left;
        }
    }

    public static Color parseColor(String colorStr) {
        String[] components = colorStr.replaceAll("[rgb()\\s]", "").split(",");
        return new Color(
                Integer.parseInt(components[0]),
                Integer.parseInt(components[1]),
                Integer.parseInt(components[2]));
    }
}
