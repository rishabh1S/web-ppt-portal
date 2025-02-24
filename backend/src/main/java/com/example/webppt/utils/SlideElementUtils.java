package com.example.webppt.utils;

import com.example.webppt.model.*;
import org.apache.poi.xslf.usermodel.XSLFShape;
import org.springframework.stereotype.Component;
import java.awt.geom.Rectangle2D;

@Component
public class SlideElementUtils {

    public SlideElement createSlideElement(ElementType type, XSLFShape shape, Presentation presentation) {
        SlideElement element = new SlideElement();
        element.setType(type);
        setPositionAndSize(element, shape, presentation);
        return element;
    }

    public void setPositionAndSize(SlideElement element, XSLFShape shape, Presentation presentation) {
        Rectangle2D anchor = shape.getAnchor();
        element.setX(convertPointsToPercentage(anchor.getX(), presentation.getWidth()));
        element.setY(convertPointsToPercentage(anchor.getY(), presentation.getHeight()));
        element.setWidth(convertPointsToPercentage(anchor.getWidth(), presentation.getWidth()));
        element.setHeight(convertPointsToPercentage(anchor.getHeight(), presentation.getHeight()));
    }

    private double convertPointsToPercentage(double points, double totalPoints) {
        return (points / totalPoints) * 100;
    }
}