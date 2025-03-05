package com.example.webppt.utils;

import org.apache.poi.xslf.usermodel.*;
import org.springframework.stereotype.Component;

import com.example.webppt.model.ShapeSVGPath;

import java.awt.geom.*;

@Component
public class SvgUtils {

    public static String generateSVGPath(XSLFAutoShape autoShape) {
        if (autoShape instanceof XSLFFreeformShape) {
            return convertFreeformShapeToSVG((XSLFFreeformShape) autoShape);
        } else {
            return ShapeSVGPath.getSVGPathForShapeType(autoShape.getShapeType());
        }
    }

    private static String convertFreeformShapeToSVG(XSLFFreeformShape freeformShape) {
        Path2D path = freeformShape.getPath();
        Rectangle2D pathBounds = path.getBounds2D();

        AffineTransform transform = new AffineTransform();

        double scaleX = 100.0 / pathBounds.getWidth();
        double scaleY = 100.0 / pathBounds.getHeight();

        double scale = Math.min(scaleX, scaleY);

        transform.translate(-pathBounds.getX(), -pathBounds.getY());

        transform.scale(scale, scale);

        double centerOffsetX = (100 - (pathBounds.getWidth() * scale)) / 2;
        double centerOffsetY = (100 - (pathBounds.getHeight() * scale)) / 2;
        transform.translate(centerOffsetX / scale, centerOffsetY / scale);

        PathIterator iterator = path.getPathIterator(transform, 0.02);
        return convertPathIteratorToSVG(iterator);
    }

    private static String convertPathIteratorToSVG(PathIterator iterator) {
        StringBuilder svgPath = new StringBuilder();
        double[] coords = new double[6];

        while (!iterator.isDone()) {
            int segmentType = iterator.currentSegment(coords);
            switch (segmentType) {
                case PathIterator.SEG_MOVETO:
                    svgPath.append(String.format("M %.8f,%.8f ", coords[0], coords[1]));
                    break;
                case PathIterator.SEG_LINETO:
                    svgPath.append(String.format("L %.8f,%.8f ", coords[0], coords[1]));
                    break;
                case PathIterator.SEG_QUADTO:
                    svgPath.append(String.format("Q %.8f,%.8f %.8f,%.8f ",
                            coords[0], coords[1], coords[2], coords[3]));
                    break;
                case PathIterator.SEG_CUBICTO:
                    svgPath.append(String.format("C %.8f,%.8f %.8f,%.8f %.8f,%.8f ",
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
}
