package com.example.webppt.utils;

import com.example.webppt.model.*;
import org.apache.poi.xslf.usermodel.XSLFShape;
import org.apache.poi.xslf.usermodel.XSLFSimpleShape;
import org.springframework.stereotype.Component;

import java.awt.Dimension;
import java.awt.geom.Rectangle2D;

@Component
public class SlideElementUtils {

    public static SlideElement createSlideElement(ElementType type, XSLFShape shape, Presentation presentation) {
        SlideElement element = new SlideElement();
        element.setType(type);
        setPositionAndSize(element, shape, presentation);
        return element;
    }

    public static void setPositionAndSize(SlideElement element, XSLFShape shape, Presentation presentation) {
        Rectangle2D anchor = shape.getAnchor();
        element.setX(convertPointsToPercentage(anchor.getX(), presentation.getWidth()));
        element.setY(convertPointsToPercentage(anchor.getY(), presentation.getHeight()));
        element.setWidth(convertPointsToPercentage(anchor.getWidth(), presentation.getWidth()));
        element.setHeight(convertPointsToPercentage(anchor.getHeight(), presentation.getHeight()));
    }

    private static double convertPointsToPercentage(double points, double totalPoints) {
        return (points / totalPoints) * 100;
    }

    public static void applyPositionAndSize(XSLFShape shape, SlideElement element) {
        Dimension pageSize = shape.getSheet().getSlideShow().getPageSize();
        Rectangle2D anchor = new Rectangle2D.Double(
                percentageToPoints(element.getX(), pageSize.getWidth()),
                percentageToPoints(element.getY(), pageSize.getHeight()),
                percentageToPoints(element.getWidth(), pageSize.getWidth()),
                percentageToPoints(element.getHeight(), pageSize.getHeight()));
        ((XSLFSimpleShape) shape).setAnchor(anchor);
    }

    private static double percentageToPoints(double percentage, double totalPoints) {
        return (percentage / 100.0) * totalPoints;
    }
}