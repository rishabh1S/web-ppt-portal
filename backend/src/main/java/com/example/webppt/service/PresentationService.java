package com.example.webppt.service;

import com.example.webppt.model.Presentation;
import com.example.webppt.model.Slide;
import com.example.webppt.model.SlideElement;
import com.example.webppt.model.ElementType;
import com.example.webppt.repository.PresentationRepository;
import com.google.gson.Gson;

import org.apache.poi.sl.usermodel.PaintStyle;
import org.apache.poi.sl.usermodel.PaintStyle.SolidPaint;
import org.apache.poi.xslf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.awt.Dimension;
import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.awt.geom.Path2D;
import org.apache.poi.sl.usermodel.ShapeType;

@Service
public class PresentationService {
    @Autowired
    PresentationRepository presentationRepo;
    @Autowired
    FileStorageService fileStorageService;

    @Transactional
    public Presentation processPresentation(MultipartFile file) throws IOException {
        // 1. Save original file
        String filePath = fileStorageService.storeFile(file);

        // 2. Parse PPTX using Apache POI
        try (XMLSlideShow slideShow = new XMLSlideShow(file.getInputStream())) {
            Presentation presentation = new Presentation();
            presentation.setOriginalFilePath(filePath);

            // Get slide dimensions
            Dimension pageSize = slideShow.getPageSize();
            presentation.setWidth(pageSize.getWidth()); // Width in points
            presentation.setHeight(pageSize.getHeight()); // Height in points

            if (slideShow.getSlides().isEmpty()) {
                throw new IOException("The PPTX file contains no slides.");
            }

            // Process slides
            for (XSLFSlide slide : slideShow.getSlides()) {
                Slide dbSlide = processSlide(slide, presentation);
                presentation.getSlides().add(dbSlide);
            }

            return presentationRepo.save(presentation);
        }
    }

    private Slide processSlide(XSLFSlide slide, Presentation presentation) throws IOException {
        Slide dbSlide = new Slide();
        dbSlide.setSlideNumber(slide.getSlideNumber());
        dbSlide.setPresentation(presentation);

        for (XSLFShape shape : slide.getShapes()) {
            if (shape instanceof XSLFGroupShape) {
                // Process grouped shapes recursively
                processGroupShape((XSLFGroupShape) shape, dbSlide, presentation);
            } else if (shape instanceof XSLFTable) {
                // Handle TABLE
                SlideElement element = processTable((XSLFTable) shape, presentation);
                element.setSlide(dbSlide);
                dbSlide.getElements().add(element);
            } else if (shape instanceof XSLFPictureShape) {
                // Handle IMAGE
                SlideElement element = processImage((XSLFPictureShape) shape, presentation);
                element.setSlide(dbSlide);
                dbSlide.getElements().add(element);
            } else if (shape instanceof XSLFAutoShape) {
                // Handle SHAPE (rectangles, arrows, etc.)
                XSLFAutoShape autoShape = (XSLFAutoShape) shape;

                // Create a SHAPE element for the shape itself
                SlideElement shapeElement = processAutoShape(autoShape, presentation);
                shapeElement.setSlide(dbSlide);
                dbSlide.getElements().add(shapeElement);

                // If the shape contains text, create a TEXT element for the text
                if (!autoShape.getText().isEmpty()) {
                    SlideElement textElement = processTextShape(autoShape, presentation);
                    textElement.setSlide(dbSlide);
                    dbSlide.getElements().add(textElement);
                }
            } else if (shape instanceof XSLFTextShape) {
                // Handle pure TEXT boxes (no shape)
                SlideElement element = processTextShape((XSLFTextShape) shape, presentation);
                element.setSlide(dbSlide);
                dbSlide.getElements().add(element);
            } else {
                // Log unsupported shapes
                System.out.println("Unsupported shape type: " + shape.getClass().getSimpleName());
            }
        }
        return dbSlide;
    }

    private void processGroupShape(XSLFGroupShape group, Slide dbSlide, Presentation presentation) {
        for (XSLFShape shape : group.getShapes()) {
            if (shape instanceof XSLFGroupShape) {
                processGroupShape((XSLFGroupShape) shape, dbSlide, presentation);
            } else if (shape instanceof XSLFAutoShape) {
                // Process auto-shapes in the group
                XSLFAutoShape autoShape = (XSLFAutoShape) shape;
                SlideElement shapeElement = processAutoShape(autoShape, presentation);
                shapeElement.setSlide(dbSlide);
                dbSlide.getElements().add(shapeElement);

                if (!autoShape.getText().isEmpty()) {
                    SlideElement textElement = processTextShape(autoShape, presentation);
                    textElement.setSlide(dbSlide);
                    dbSlide.getElements().add(textElement);
                }
            }
        }
    }

    private SlideElement processAutoShape(XSLFAutoShape autoShape, Presentation presentation) {
        SlideElement element = createSlideElement(ElementType.SHAPE, autoShape, presentation);

        // Generate SVG path data
        String svgPath = generateSVGPath(autoShape);
        element.setContent(svgPath);

        // Extract style properties
        Map<String, Object> style = new HashMap<>();

        // Fill color
        Color fillColor = autoShape.getFillColor();
        style.put("fillColor", fillColor != null ? toHexColor(fillColor) : "transparent");

        // Stroke color
        Color strokeColor = autoShape.getLineColor();
        style.put("strokeColor", strokeColor != null ? toHexColor(strokeColor) : "transparent");
        style.put("strokeWidth", autoShape.getLineWidth());

        element.setStyle(style);

        return element;
    }

    private String generateSVGPath(XSLFAutoShape autoShape) {
        if (autoShape instanceof XSLFFreeformShape) {
            return convertFreeformShapeToSVG((XSLFFreeformShape) autoShape);
        } else {
            return getPresetShapeSVGPath(autoShape.getShapeType());
        }
    }

    private String convertFreeformShapeToSVG(XSLFFreeformShape freeformShape) {
        Path2D path = freeformShape.getPath();
        java.awt.geom.Rectangle2D anchor = freeformShape.getAnchor();

        AffineTransform transform = new AffineTransform();
        transform.translate(-anchor.getX(), -anchor.getY());
        transform.scale(100 / anchor.getWidth(), 100 / anchor.getHeight());

        PathIterator iterator = path.getPathIterator(transform);
        return convertPathIteratorToSVG(iterator);
    }

    private String convertPathIteratorToSVG(PathIterator iterator) {
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

    private String getPresetShapeSVGPath(ShapeType shapeType) {
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

    private SlideElement processTable(XSLFTable table, Presentation presentation) {
        SlideElement element = createSlideElement(ElementType.TABLE, table, presentation);

        // Extract table structure and content
        List<List<String>> tableContent = new ArrayList<>();
        Map<String, Object> tableStyle = new HashMap<>();

        // Store cell styles in a nested structure
        List<List<Map<String, Object>>> cellStyles = new ArrayList<>();

        for (XSLFTableRow row : table.getRows()) {
            List<String> rowContent = new ArrayList<>();
            List<Map<String, Object>> rowStyles = new ArrayList<>();

            for (XSLFTableCell cell : row.getCells()) {
                // Extract cell text content
                rowContent.add(cell.getText());

                // Extract cell styling
                Map<String, Object> cellStyle = new HashMap<>();
                Color backgroundColor = cell.getFillColor();
                cellStyle.put("backgroundColor", backgroundColor != null ? toHexColor(backgroundColor) : "transparent");

                // Extract border colors (example for bottom border)
                Color borderColor = cell.getBorderColor(XSLFTableCell.BorderEdge.bottom);
                cellStyle.put("borderColor", borderColor != null ? toHexColor(borderColor) : "transparent");

                // Add cell style to row styles
                rowStyles.add(cellStyle);
            }

            // Add row content and styles to the table
            tableContent.add(rowContent);
            cellStyles.add(rowStyles);
        }

        // Set table content in the `content` field
        element.setContent(new Gson().toJson(tableContent));

        // Set table styles in the `style` field
        tableStyle.put("cellStyles", cellStyles);
        element.setStyle(tableStyle);

        return element;
    }

    private SlideElement processTextShape(XSLFShape shape, Presentation presentation) {
        SlideElement element = createSlideElement(ElementType.TEXT, shape, presentation);

        // Extract text content
        String text = "";
        if (shape instanceof XSLFTextShape) {
            text = ((XSLFTextShape) shape).getText();
        } else if (shape instanceof XSLFAutoShape) {
            text = ((XSLFAutoShape) shape).getText();
        }
        element.setContent(text);

        // Font styling
        Map<String, Object> style = new HashMap<>();
        if (shape instanceof XSLFTextShape) {
            XSLFTextShape textShape = (XSLFTextShape) shape;
            if (!textShape.getTextParagraphs().isEmpty()) {
                XSLFTextParagraph paragraph = textShape.getTextParagraphs().get(0);
                if (!paragraph.getTextRuns().isEmpty()) {
                    XSLFTextRun textRun = paragraph.getTextRuns().get(0);
                    style.put("fontSize", textRun.getFontSize());
                    style.put("color", extractFontColor(textRun));
                }
            }
        }
        element.setStyle(style);

        return element;
    }

    private SlideElement processImage(XSLFPictureShape pictureShape, Presentation presentation) throws IOException {
        SlideElement element = createSlideElement(ElementType.IMAGE, pictureShape, presentation);

        // Save image to storage and get the file path
        String imagePath = fileStorageService.storeImage(pictureShape.getPictureData().getData());
        element.setContent(imagePath);

        return element;
    }

    private SlideElement createSlideElement(ElementType type, XSLFShape shape, Presentation presentation) {
        SlideElement element = new SlideElement();
        element.setType(type);

        // Set position and size
        setPositionAndSize(element, shape, presentation);

        return element;
    }

    private void setPositionAndSize(SlideElement element, XSLFShape shape, Presentation presentation) {
        java.awt.geom.Rectangle2D anchor = shape.getAnchor();
        element.setX(convertPointsToPercentage(anchor.getX(), presentation.getWidth()));
        element.setY(convertPointsToPercentage(anchor.getY(), presentation.getHeight()));
        element.setWidth(convertPointsToPercentage(anchor.getWidth(), presentation.getWidth()));
        element.setHeight(convertPointsToPercentage(anchor.getHeight(), presentation.getHeight()));
    }

    private String extractFontColor(XSLFTextRun textRun) {
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

    private double convertPointsToPercentage(double points, double totalPoints) {
        return (points / totalPoints) * 100;
    }

    private String toHexColor(Color color) {
        return String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
    }
}