package com.example.webppt.service;

import com.example.webppt.model.SlideElement;
import com.example.webppt.utils.SlideElementUtils;
import com.example.webppt.utils.StyleApplicationUtils;
import org.apache.poi.sl.usermodel.ShapeType;
import org.apache.poi.xslf.usermodel.XSLFAutoShape;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.springframework.stereotype.Service;

@Service
public class ShapeGeneration {

    public void addAutoShape(XSLFSlide slide, SlideElement element) {
        // Create an auto shape on the slide
        XSLFAutoShape shape = slide.createAutoShape();
        // Apply position and size from the element's properties
        SlideElementUtils.applyPositionAndSize(shape, element);

        // Set shape type if provided (e.g. "rect")
        Object shapeTypeObj = element.getStyle().get("shapeType");
        if (shapeTypeObj != null) {
            try {
                String shapeTypeStr = shapeTypeObj.toString();
                // Convert the string (e.g. "rect") to the corresponding POI ShapeType enum.
                ShapeType poiShapeType = ShapeType.valueOf(shapeTypeStr.toUpperCase());
                shape.setShapeType(poiShapeType);
            } catch (Exception e) {
                System.err.println("Error setting shape type: " + e.getMessage());
            }
        }

        // (Optional) If your shape content contains a custom SVG path, you might
        // consider
        // using a freeform shape instead and parsing that path. For now, we assume an
        // auto shape is sufficient.

        // Finally, apply all other style properties using your reflective style
        // utility.
        StyleApplicationUtils.applyStyles(shape, element.getStyle());
    }
}
