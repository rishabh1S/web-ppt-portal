package com.example.webppt.utils;

import org.apache.poi.xslf.usermodel.*;
import org.springframework.stereotype.Component;

import java.awt.geom.*;
import org.apache.poi.sl.usermodel.ShapeType;

@Component
public class SvgUtils {

    public String generateSVGPath(XSLFAutoShape autoShape) {
        if (autoShape instanceof XSLFFreeformShape) {
            return convertFreeformShapeToSVG((XSLFFreeformShape) autoShape);
        } else {
            return getPresetShapeSVGPath(autoShape.getShapeType());
        }
    }

    public String convertFreeformShapeToSVG(XSLFFreeformShape freeformShape) {
        Path2D path = freeformShape.getPath();
        java.awt.geom.Rectangle2D anchor = freeformShape.getAnchor();

        AffineTransform transform = new AffineTransform();
        transform.translate(-anchor.getX(), -anchor.getY());
        transform.scale(100 / anchor.getWidth(), 100 / anchor.getHeight());

        PathIterator iterator = path.getPathIterator(transform);
        return convertPathIteratorToSVG(iterator);
    }

    public String convertPathIteratorToSVG(PathIterator iterator) {
        StringBuilder svgPath = new StringBuilder();
        double[] coords = new double[6];
        while (!iterator.isDone()) {
            int segmentType = iterator.currentSegment(coords);
            switch (segmentType) {
                case PathIterator.SEG_MOVETO:
                    svgPath.append(String.format("M %.2f,%.2f ", coords[0], coords[1]));
                    break;
                case PathIterator.SEG_LINETO:
                    svgPath.append(String.format("L %.2f,%.2f ", coords[0], coords[1]));
                    break;
                case PathIterator.SEG_QUADTO:
                    svgPath.append(String.format("Q %.2f,%.2f %.2f,%.2f ",
                            coords[0], coords[1], coords[2], coords[3]));
                    break;
                case PathIterator.SEG_CUBICTO:
                    svgPath.append(String.format("C %.2f,%.2f %.2f,%.2f %.2f,%.2f ",
                            coords[0], coords[1], coords[2], coords[3], coords[4], coords[5]));
                    break;
                case PathIterator.SEG_CLOSE:
                    svgPath.append("Z ");
                    break;
                default:
                    break;
            }
            iterator.next();
        }
        return svgPath.toString().trim();
    }

    public String getPresetShapeSVGPath(ShapeType shapeType) {
        if (shapeType == null) {
            return "M 0 0 H 100 V 100 H 0 Z";
        }
        switch (shapeType) {
            case RECT:
                return "M 0 0 H 100 V 100 H 0 Z";
            case ELLIPSE:
                return "M 50,0 A 50,50 0 1 1 50,100 A 50,50 0 1 1 50,0";
            case TRIANGLE:
                return "M 50 0 L 100 100 L 0 100 Z";
            case RIGHT_ARROW:
                return "M 0 50 L 70 50 L 70 30 L 100 50 L 70 70 L 70 50 Z";
            case LEFT_ARROW:
                return "M 100 50 L 30 50 L 30 30 L 0 50 L 30 70 L 30 50 Z";
            case DIAMOND:
                return "M 50 0 L 100 50 L 50 100 L 0 50 Z";
            case HEXAGON:
                return "M 50 0 L 100 25 L 100 75 L 50 100 L 0 75 L 0 25 Z";
            case PENTAGON:
                return "M 50 0 L 100 38 L 82 100 L 18 100 L 0 38 Z";
            case LINE:
                return "M 0 50 L 100 50";
            default:
                return "M 0 0 H 100 V 100 H 0 Z";
        }
    }
}
