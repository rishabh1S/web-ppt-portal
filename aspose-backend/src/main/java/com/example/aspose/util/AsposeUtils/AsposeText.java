package com.example.aspose.util.AsposeUtils;

import java.util.Map;

import com.aspose.slides.*;
import com.example.aspose.model.TemplateTextContent;

public class AsposeText {

        public static void addTextShape(ISlide slide, TemplateTextContent textContent) {
                // Retrieve text object properties from the POJO
                int x = textContent.getX();
                int y = textContent.getY();
                int width = textContent.getWidth();
                int height = textContent.getHeight();
                String content = textContent.getContent();

                // Create text shape
                IAutoShape textShape = slide.getShapes().addAutoShape(
                                ShapeType.Rectangle, x, y, width, height);
                textShape.getFillFormat().setFillType(FillType.NoFill);
                textShape.getLineFormat().setWidth(0);
                textShape.getLineFormat().getFillFormat().setFillType(FillType.NoFill);

                // Set text content
                textShape.getTextFrame().setText(content);

                Map<String, Object> styles = (Map<String, Object>) textContent.getStyles();
                IPortionFormat portionFormat = textShape.getTextFrame().getParagraphs().get_Item(0)
                                .getPortions().get_Item(0).getPortionFormat();
                portionFormat.setLatinFont(new FontData((String) styles.get("fontFamily")));
                portionFormat.setFontHeight(Integer.parseInt(((String) styles.get("fontSize")).replace("pt", "")));

                // Set text color
                String colorStr = (String) styles.get("color");
                portionFormat.getFillFormat().setFillType(FillType.Solid);
                portionFormat.getFillFormat().getSolidFillColor().setColor(AsposeCommon.parseColor(colorStr));

                // Configure paragraph alignment
                textShape.getTextFrame().getParagraphs().get_Item(0).getParagraphFormat()
                                .setAlignment(AsposeCommon.getTextAlignment((String) styles.get("textAlign")));

                // Set background color if exists
                if (styles.containsKey("backgroundColor")) {
                        textShape.getFillFormat().setFillType(FillType.Solid);
                        textShape.getFillFormat().getSolidFillColor()
                                        .setColor(AsposeCommon.parseColor((String) styles.get("backgroundColor")));
                }
        }
}
