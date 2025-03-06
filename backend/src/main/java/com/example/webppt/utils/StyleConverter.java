package com.example.webppt.utils;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

@Component
public class StyleConverter {

    /**
     * Converts PowerPoint style properties to CSS style properties
     * 
     * @param pptStyle The original PowerPoint style properties map
     * @return A map containing CSS style properties
     */
    public static Map<String, String> toCssStyle(Map<String, Object> pptStyle) {
        if (pptStyle == null) {
            return new HashMap<>();
        }

        Map<String, String> cssStyle = new HashMap<>();

        // Text font properties
        handleFontProperties(pptStyle, cssStyle);

        // Text alignment properties
        handleTextAlignment(pptStyle, cssStyle);

        // Text spacing and insets
        handleTextSpacingAndInsets(pptStyle, cssStyle);

        // Text wrapping and overflow properties
        handleTextWrappingAndOverflow(pptStyle, cssStyle);

        // Text direction (horizontal/vertical)
        handleTextDirection(pptStyle, cssStyle);

        // List and bullet properties
        handleListAndBulletProperties(pptStyle, cssStyle);

        // Character formatting (sub/superscript, textCap)
        handleCharacterFormatting(pptStyle, cssStyle);

        // Table-specific styles
        handleTableStyles(pptStyle, cssStyle);

        // Additional styles
        handleOpacity(pptStyle, cssStyle);
        handleBorders(pptStyle, cssStyle);
        handleTextStroke(pptStyle, cssStyle);
        handleTransforms(pptStyle, cssStyle);
        handleColumns(pptStyle, cssStyle);
        handleWordSpacing(pptStyle, cssStyle);
        handleListPosition(pptStyle, cssStyle);
        handleTextOverflow(pptStyle, cssStyle);
        handleFontStretch(pptStyle, cssStyle);
        handleHyphenation(pptStyle, cssStyle);

        return cssStyle;
    }

    /**
     * Handles font-related styling properties
     */
    private static void handleFontProperties(Map<String, Object> pptStyle, Map<String, String> cssStyle) {
        // Font family
        if (pptStyle.containsKey("fontFamily")) {
            String fontFamily = String.valueOf(pptStyle.get("fontFamily"));
            cssStyle.put("font-family", "'" + fontFamily + "', sans-serif");
        } else if (pptStyle.containsKey("defaultFontFamily")) {
            String defaultFontFamily = String.valueOf(pptStyle.get("defaultFontFamily"));
            cssStyle.put("font-family", "'" + defaultFontFamily + "', sans-serif");
        }

        // Font size
        if (pptStyle.containsKey("fontSize")) {
            Object fontSizeObj = pptStyle.get("fontSize");
            if (fontSizeObj instanceof Number) {
                double fontSize = ((Number) fontSizeObj).doubleValue();
                cssStyle.put("font-size", fontSize + "pt");
            }
        } else if (pptStyle.containsKey("defaultFontSize")) {
            Object defaultFontSizeObj = pptStyle.get("defaultFontSize");
            if (defaultFontSizeObj instanceof Number) {
                double defaultFontSize = ((Number) defaultFontSizeObj).doubleValue();
                cssStyle.put("font-size", defaultFontSize + "pt");
            }
        }

        // Font color
        if (pptStyle.containsKey("fontColor")) {
            String fontColor = String.valueOf(pptStyle.get("fontColor"));
            cssStyle.put("color", fontColor);
        }

        // Font weight (bold)
        if (pptStyle.containsKey("bold")) {
            boolean isBold = (boolean) pptStyle.getOrDefault("bold", false);
            cssStyle.put("font-weight", isBold ? "bold" : "normal");
        }

        // Font style (italic)
        if (pptStyle.containsKey("italic")) {
            boolean isItalic = (boolean) pptStyle.getOrDefault("italic", false);
            cssStyle.put("font-style", isItalic ? "italic" : "normal");
        }

        // Character spacing
        if (pptStyle.containsKey("characterSpacing")) {
            Object spacingObj = pptStyle.get("characterSpacing");
            if (spacingObj instanceof Number) {
                double spacing = ((Number) spacingObj).doubleValue();
                // Convert to em for CSS
                double spacingEm = spacing / 100.0;
                cssStyle.put("letter-spacing", spacingEm + "em");
            }
        }

        // Text decorations (underline, strikethrough)
        StringBuilder textDecoration = new StringBuilder();

        if (pptStyle.containsKey("underlined") && (boolean) pptStyle.getOrDefault("underlined", false)) {
            textDecoration.append("underline ");
        }

        if (pptStyle.containsKey("strikethrough") && (boolean) pptStyle.getOrDefault("strikethrough", false)) {
            textDecoration.append("line-through ");
        }

        if (textDecoration.length() > 0) {
            cssStyle.put("text-decoration", textDecoration.toString().trim());
        } else {
            cssStyle.put("text-decoration", "none");
        }

        // Highlight color (background color for text)
        if (pptStyle.containsKey("highlightColor")) {
            String highlightColor = String.valueOf(pptStyle.get("highlightColor"));
            cssStyle.put("background-color", highlightColor);
        }
    }

    /**
     * Handles text alignment properties
     */
    private static void handleTextAlignment(Map<String, Object> pptStyle, Map<String, String> cssStyle) {
        // Horizontal text alignment
        if (pptStyle.containsKey("textAlign")) {
            String textAlign = String.valueOf(pptStyle.get("textAlign"));
            cssStyle.put("text-align", textAlign);
        } else if (pptStyle.containsKey("horizontalCentered")) {
            boolean centered = (boolean) pptStyle.getOrDefault("horizontalCentered", false);
            cssStyle.put("text-align", centered ? "center" : "left");
        }

        // Vertical alignment
        if (pptStyle.containsKey("verticalAlignment")) {
            String verticalAlign = String.valueOf(pptStyle.get("verticalAlignment"));
            switch (verticalAlign) {
                case "top":
                    cssStyle.put("display", "flex");
                    cssStyle.put("flex-direction", "column");
                    cssStyle.put("justify-content", "flex-start");
                    break;
                case "middle":
                    cssStyle.put("display", "flex");
                    cssStyle.put("flex-direction", "column");
                    cssStyle.put("justify-content", "center");
                    break;
                case "bottom":
                    cssStyle.put("display", "flex");
                    cssStyle.put("flex-direction", "column");
                    cssStyle.put("justify-content", "flex-end");
                    break;
                default:
                    // Default to top alignment
                    cssStyle.put("display", "flex");
                    cssStyle.put("flex-direction", "column");
                    cssStyle.put("justify-content", "flex-start");
            }
        }
    }

    /**
     * Handles text spacing and insets/padding
     */
    private static void handleTextSpacingAndInsets(Map<String, Object> pptStyle, Map<String, String> cssStyle) {
        // Text indentation
        if (pptStyle.containsKey("indent")) {
            Object indentObj = pptStyle.get("indent");
            if (indentObj instanceof Number) {
                double indent = ((Number) indentObj).doubleValue();
                cssStyle.put("text-indent", indent + "px");
            }
        }

        // Indent level (for lists)
        if (pptStyle.containsKey("indentLevel")) {
            Object indentLevelObj = pptStyle.get("indentLevel");
            if (indentLevelObj instanceof Number) {
                int indentLevel = ((Number) indentLevelObj).intValue();
                // Apply additional left padding based on indent level
                double leftPadding = indentLevel * 20; // 20px per level
                cssStyle.put("padding-left", leftPadding + "px");
            }
        }

        // Margins
        if (pptStyle.containsKey("leftMargin")) {
            Object leftMarginObj = pptStyle.get("leftMargin");
            if (leftMarginObj instanceof Number) {
                double leftMargin = ((Number) leftMarginObj).doubleValue();
                cssStyle.put("margin-left", leftMargin + "px");
            }
        }

        if (pptStyle.containsKey("rightMargin")) {
            Object rightMarginObj = pptStyle.get("rightMargin");
            if (rightMarginObj instanceof Number) {
                double rightMargin = ((Number) rightMarginObj).doubleValue();
                cssStyle.put("margin-right", rightMargin + "px");
            }
        }

        // Spacing before/after paragraphs
        if (pptStyle.containsKey("spaceBefore")) {
            Object spaceBeforeObj = pptStyle.get("spaceBefore");
            if (spaceBeforeObj instanceof Number) {
                double spaceBefore = ((Number) spaceBeforeObj).doubleValue();
                cssStyle.put("margin-top", spaceBefore + "px");
            }
        }

        if (pptStyle.containsKey("spacingAfter")) {
            Object spacingAfterObj = pptStyle.get("spacingAfter");
            if (spacingAfterObj instanceof Number) {
                double spacingAfter = ((Number) spacingAfterObj).doubleValue();
                cssStyle.put("margin-bottom", spacingAfter + "px");
            }
        }

        // Line spacing
        if (pptStyle.containsKey("lineSpacing")) {
            Object lineSpacingObj = pptStyle.get("lineSpacing");
            if (lineSpacingObj instanceof Number) {
                double lineSpacing = ((Number) lineSpacingObj).doubleValue();
                // Convert to appropriate CSS line-height
                // Use percentage for line height, approximating PPT behavior
                double lineHeightPercent = (lineSpacing / 72.0) * 100; // Normalize to percentage
                cssStyle.put("line-height", lineHeightPercent + "%");
            }
        }

        // Insets (padding)
        if (pptStyle.containsKey("topInset")) {
            Object topInsetObj = pptStyle.get("topInset");
            if (topInsetObj instanceof Number) {
                double topInset = ((Number) topInsetObj).doubleValue();
                cssStyle.put("padding-top", topInset + "px");
            }
        }

        if (pptStyle.containsKey("rightInset")) {
            Object rightInsetObj = pptStyle.get("rightInset");
            if (rightInsetObj instanceof Number) {
                double rightInset = ((Number) rightInsetObj).doubleValue();
                cssStyle.put("padding-right", rightInset + "px");
            }
        }

        if (pptStyle.containsKey("bottomInset")) {
            Object bottomInsetObj = pptStyle.get("bottomInset");
            if (bottomInsetObj instanceof Number) {
                double bottomInset = ((Number) bottomInsetObj).doubleValue();
                cssStyle.put("padding-bottom", bottomInset + "px");
            }
        }

        if (pptStyle.containsKey("leftInset")) {
            Object leftInsetObj = pptStyle.get("leftInset");
            if (leftInsetObj instanceof Number) {
                double leftInset = ((Number) leftInsetObj).doubleValue();
                cssStyle.put("padding-left", leftInset + "px");
            }
        }
    }

    /**
     * Handles text wrapping and overflow behavior
     */
    private static void handleTextWrappingAndOverflow(Map<String, Object> pptStyle, Map<String, String> cssStyle) {
        if (pptStyle.containsKey("wordWrap")) {
            boolean wordWrap = (boolean) pptStyle.getOrDefault("wordWrap", true);
            cssStyle.put("white-space", wordWrap ? "normal" : "nowrap");
            if (wordWrap) {
                cssStyle.put("overflow-wrap", "break-word");
            }
        }

        // Handle text autofit
        if (pptStyle.containsKey("textAutofit")) {
            String textAutofit = String.valueOf(pptStyle.get("textAutofit"));
            if ("shrinkTextOnOverflow".equals(textAutofit)) {
                cssStyle.put("font-size", "min(1em, max(0.5em, (100% - 1em)))");
            }
        }
    }

    /**
     * Handles text direction (horizontal/vertical)
     */
    private static void handleTextDirection(Map<String, Object> pptStyle, Map<String, String> cssStyle) {
        if (pptStyle.containsKey("textDirection")) {
            String textDirection = String.valueOf(pptStyle.get("textDirection"));
            if ("vertical".equals(textDirection)) {
                cssStyle.put("writing-mode", "vertical-rl");
            }
            if ("rtl".equals(pptStyle.get("textDirection"))) {
                cssStyle.put("direction", "rtl");
                cssStyle.put("text-align", "right");
            }
        }
    }

    /**
     * Handles list and bullet properties
     */
    private static void handleListAndBulletProperties(Map<String, Object> pptStyle, Map<String, String> cssStyle) {
        boolean isBullet = (boolean) pptStyle.getOrDefault("bullet", false);

        if (isBullet) {
            // Set list-style properties
            String listStyleType = "disc"; // Default

            // Check for auto-numbering scheme
            if (pptStyle.containsKey("autoNumberingScheme")) {
                String scheme = String.valueOf(pptStyle.get("autoNumberingScheme"));
                switch (scheme) {
                    case "arabicperiod":
                        listStyleType = "decimal";
                        break;
                    case "alphalcperiod":
                        listStyleType = "lower-alpha";
                        break;
                    case "alphaucperiod":
                        listStyleType = "upper-alpha";
                        break;
                    case "romanlcperiod":
                        listStyleType = "lower-roman";
                        break;
                    case "romanucperiod":
                        listStyleType = "upper-roman";
                        break;
                    default:
                        listStyleType = "disc";
                }
            }

            cssStyle.put("list-style-type", listStyleType);
            cssStyle.put("display", "list-item");

            // Custom bullet styling
            if (pptStyle.containsKey("bulletFont") && pptStyle.containsKey("bulletFontColor")) {
                String bulletFont = String.valueOf(pptStyle.get("bulletFont"));
                String bulletColor = String.valueOf(pptStyle.get("bulletFontColor"));

                // For custom bullets, we would typically need to use ::before in CSS
                // Since we can't directly generate ::before in a style map, we'll add
                // properties
                // that can be used to generate the appropriate CSS later
                cssStyle.put("--bullet-font", "'" + bulletFont + "', sans-serif");
                cssStyle.put("--bullet-color", bulletColor);
            }

            // Handle tab size for lists
            if (pptStyle.containsKey("defaultTabSize")) {
                Object tabSizeObj = pptStyle.get("defaultTabSize");
                if (tabSizeObj instanceof Number) {
                    double tabSize = ((Number) tabSizeObj).doubleValue();
                    cssStyle.put("tab-size", String.valueOf(tabSize / 10)); // Convert to a reasonable CSS value
                }
            }
        }
    }

    /**
     * Handles character formatting like superscript, subscript, text capitalization
     */
    private static void handleCharacterFormatting(Map<String, Object> pptStyle, Map<String, String> cssStyle) {
        // Subscript/Superscript
        boolean isSubscript = (boolean) pptStyle.getOrDefault("subscript", false);
        boolean isSuperscript = (boolean) pptStyle.getOrDefault("superscript", false);

        if (isSubscript) {
            cssStyle.put("vertical-align", "sub");
            cssStyle.put("font-size", "smaller");
        } else if (isSuperscript) {
            cssStyle.put("vertical-align", "super");
            cssStyle.put("font-size", "smaller");
        }

        // Text capitalization
        if (pptStyle.containsKey("textCap")) {
            String textCap = String.valueOf(pptStyle.get("textCap"));
            switch (textCap) {
                case "all":
                    cssStyle.put("text-transform", "uppercase");
                    break;
                case "small":
                    cssStyle.put("text-transform", "lowercase");
                    break;
                case "firstChar":
                    cssStyle.put("text-transform", "capitalize");
                    break;
                case "none":
                default:
                    cssStyle.put("text-transform", "none");
            }
        }

        // Additional font properties based on pitchAndFamily value
        if (pptStyle.containsKey("pitchAndFamily")) {
            Object pitchObj = pptStyle.get("pitchAndFamily");
            if (pitchObj instanceof Number) {
                int pitchValue = ((Number) pitchObj).intValue();

                // Interpret the pitch and family value
                // This is a bit of a simplification as pitchAndFamily is a complex Windows
                // concept
                boolean isFixedPitch = (pitchValue & 1) == 1;

                if (isFixedPitch) {
                    cssStyle.put("font-family", "monospace, " + cssStyle.getOrDefault("font-family", "sans-serif"));
                }
            }
        }
    }

    /**
     * Handles table-specific styling properties
     */
    private static void handleTableStyles(Map<String, Object> pptStyle, Map<String, String> cssStyle) {
        // Border color
        if (pptStyle.containsKey("borderColor")) {
            String borderColor = String.valueOf(pptStyle.get("borderColor"));
            cssStyle.put("border-color", borderColor);
        }

        // Fill color
        if (pptStyle.containsKey("fillColor")) {
            String fillColor = String.valueOf(pptStyle.get("fillColor"));
            cssStyle.put("background-color", fillColor);
        }

        // Line spacing
        if (pptStyle.containsKey("lineSpacing")) {
            Object lineSpacingObj = pptStyle.get("lineSpacing");
            if (lineSpacingObj instanceof Number) {
                double lineSpacing = ((Number) lineSpacingObj).doubleValue();
                // Convert to appropriate CSS line-height
                double lineHeightPercent = (lineSpacing / 100.0) * 100; // Normalize to percentage
                cssStyle.put("line-height", lineHeightPercent + "%");
            }
        }

        // Indent level
        if (pptStyle.containsKey("indentLevel")) {
            Object indentLevelObj = pptStyle.get("indentLevel");
            if (indentLevelObj instanceof Number) {
                int indentLevel = ((Number) indentLevelObj).intValue();
                // Apply additional left padding based on indent level
                double leftPadding = indentLevel * 20; // 20px per level
                cssStyle.put("padding-left", leftPadding + "px");
            }
        }

        // Insets (padding)
        if (pptStyle.containsKey("topInset")) {
            Object topInsetObj = pptStyle.get("topInset");
            if (topInsetObj instanceof Number) {
                double topInset = ((Number) topInsetObj).doubleValue();
                cssStyle.put("padding-top", topInset + "px");
            }
        }

        if (pptStyle.containsKey("rightInset")) {
            Object rightInsetObj = pptStyle.get("rightInset");
            if (rightInsetObj instanceof Number) {
                double rightInset = ((Number) rightInsetObj).doubleValue();
                cssStyle.put("padding-right", rightInset + "px");
            }
        }

        if (pptStyle.containsKey("bottomInset")) {
            Object bottomInsetObj = pptStyle.get("bottomInset");
            if (bottomInsetObj instanceof Number) {
                double bottomInset = ((Number) bottomInsetObj).doubleValue();
                cssStyle.put("padding-bottom", bottomInset + "px");
            }
        }

        if (pptStyle.containsKey("leftInset")) {
            Object leftInsetObj = pptStyle.get("leftInset");
            if (leftInsetObj instanceof Number) {
                double leftInset = ((Number) leftInsetObj).doubleValue();
                cssStyle.put("padding-left", leftInset + "px");
            }
        }
    }

    private static void handleOpacity(Map<String, Object> pptStyle, Map<String, String> cssStyle) {
        if (pptStyle.containsKey("opacity")) {
            double opacity = ((Number) pptStyle.get("opacity")).doubleValue();
            cssStyle.put("opacity", String.valueOf(opacity));
        }
    }

    private static void handleBorders(Map<String, Object> pptStyle, Map<String, String> cssStyle) {
        // Border width and style
        if (pptStyle.containsKey("borderWidth")) {
            cssStyle.put("border-width", pptStyle.get("borderWidth") + "px");
        }
        if (pptStyle.containsKey("borderStyle")) {
            cssStyle.put("border-style", String.valueOf(pptStyle.get("borderStyle")));
        }
        // Border radius
        if (pptStyle.containsKey("cornerRadius")) {
            cssStyle.put("border-radius", pptStyle.get("cornerRadius") + "px");
        }
    }

    private static void handleTextStroke(Map<String, Object> pptStyle, Map<String, String> cssStyle) {
        if (pptStyle.containsKey("textStrokeWidth") && pptStyle.containsKey("textStrokeColor")) {
            String width = pptStyle.get("textStrokeWidth") + "px";
            String color = String.valueOf(pptStyle.get("textStrokeColor"));
            cssStyle.put("-webkit-text-stroke", width + " " + color);
        }
    }

    private static void handleTransforms(Map<String, Object> pptStyle, Map<String, String> cssStyle) {
        if (pptStyle.containsKey("rotation")) {
            double rotation = ((Number) pptStyle.get("rotation")).doubleValue();
            cssStyle.put("transform", "rotate(" + rotation + "deg)");
        }
    }

    private static void handleColumns(Map<String, Object> pptStyle, Map<String, String> cssStyle) {
        if (pptStyle.containsKey("columnCount")) {
            cssStyle.put("column-count", String.valueOf(pptStyle.get("columnCount")));
        }
        if (pptStyle.containsKey("columnGap")) {
            cssStyle.put("column-gap", pptStyle.get("columnGap") + "px");
        }
    }

    private static void handleWordSpacing(Map<String, Object> pptStyle, Map<String, String> cssStyle) {
        if (pptStyle.containsKey("wordSpacing")) {
            cssStyle.put("word-spacing", pptStyle.get("wordSpacing") + "px");
        }
    }

    private static void handleListPosition(Map<String, Object> pptStyle, Map<String, String> cssStyle) {
        if (pptStyle.containsKey("bulletPosition")) {
            cssStyle.put("list-style-position", String.valueOf(pptStyle.get("bulletPosition")));
        }
    }

    private static void handleTextOverflow(Map<String, Object> pptStyle, Map<String, String> cssStyle) {
        if (pptStyle.containsKey("textOverflow")) {
            String overflow = String.valueOf(pptStyle.get("textOverflow"));
            if ("ellipsis".equals(overflow)) {
                cssStyle.put("text-overflow", "ellipsis");
                cssStyle.put("overflow", "hidden");
                cssStyle.put("white-space", "nowrap");
            }
        }
    }

    private static void handleFontStretch(Map<String, Object> pptStyle, Map<String, String> cssStyle) {
        if (pptStyle.containsKey("fontStretch")) {
            cssStyle.put("font-stretch", String.valueOf(pptStyle.get("fontStretch")));
        }
    }

    private static void handleHyphenation(Map<String, Object> pptStyle, Map<String, String> cssStyle) {
        if (pptStyle.containsKey("autoHyphenate") && (Boolean) pptStyle.get("autoHyphenate")) {
            cssStyle.put("hyphens", "auto");
        }
    }
}