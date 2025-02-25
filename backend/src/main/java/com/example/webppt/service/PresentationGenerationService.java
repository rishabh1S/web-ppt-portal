package com.example.webppt.service;

import com.example.webppt.model.*;
import com.example.webppt.utils.ColorUtils;
import com.example.webppt.utils.SlideElementUtils;
import com.example.webppt.utils.SvgUtils;

import org.apache.poi.sl.usermodel.PictureData;
import org.apache.poi.sl.usermodel.TableCell.BorderEdge;
import org.apache.poi.xslf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
public class PresentationGenerationService {

    @Autowired
    FileStorageService fileStorageService;
    @Autowired
    SlideElementUtils slideElementUtils;
    @Autowired
    ColorUtils colorUtils;
    @Autowired
    SvgUtils svgUtils;

    public byte[] generatePresentation(Presentation presentation) throws IOException {
        try (XMLSlideShow ppt = new XMLSlideShow()) {
            // Set slide dimensions from original
            Dimension pageSize = new Dimension(
                    (int) presentation.getWidth(),
                    (int) presentation.getHeight());
            ppt.setPageSize(pageSize);

            // Process slides
            for (Slide dbSlide : presentation.getSlides()) {
                XSLFSlide pptSlide = ppt.createSlide();
                processSlideElements(pptSlide, dbSlide.getElements());
            }

            // Write to byte array
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ppt.write(out);
            return out.toByteArray();
        }
    }

    private void processSlideElements(XSLFSlide slide, List<SlideElement> elements) {
        for (SlideElement element : elements) {
            try {
                switch (element.getType()) {
                    case TEXT:
                        addTextShape(slide, element);
                        break;
                    case IMAGE:
                        addImage(slide, element);
                        break;
                    case SHAPE:
                        addAutoShape(slide, element);
                        break;
                    case TABLE:
                        addTable(slide, element);
                        break;
                }
            } catch (Exception e) {
                System.err.println("Error processing element " + element.getId() + ": " + e.getMessage());
            }
        }
    }

    private void addTextShape(XSLFSlide slide, SlideElement element) {
        XSLFTextShape textShape = slide.createTextBox();
        applyPositionAndSize(textShape, element);

        // Set text content
        String text = (String) element.getContent().get("text");
        XSLFTextParagraph p = textShape.addNewTextParagraph();
        XSLFTextRun run = p.addNewTextRun();
        run.setText(text);

        // Apply styles
        applyTextStyles(run, element.getStyle());
    }

    private void addImage(XSLFSlide slide, SlideElement element) throws IOException {
        String imagePath = (String) element.getContent().get("url");
        byte[] imageData = fileStorageService.loadImage(imagePath);

        XSLFPictureShape picture = slide.createPicture(
                slide.getSlideShow().addPicture(
                        new ByteArrayInputStream(imageData),
                        getPictureType(imagePath)));

        applyPositionAndSize(picture, element);
    }

    private void addAutoShape(XSLFSlide slide, SlideElement element) {
        // Create freeform shape for custom paths
        XSLFFreeformShape shape = slide.createFreeform();
        applyPositionAndSize(shape, element);

        // Apply shape styles
        Map<String, Object> style = element.getStyle();
        shape.setFillColor(colorUtils.parseColor((String) style.get("fillColor")));
        shape.setLineColor(colorUtils.parseColor((String) style.get("strokeColor")));
        shape.setLineWidth(((Number) style.get("strokeWidth")).doubleValue());

        // Set shape geometry
        String svgPath = (String) element.getContent().get("svgPath");
        Path2D path = svgUtils.svgPathToPath2D(svgPath);
        shape.setPath(path);
    }

    private void addTable(XSLFSlide slide, SlideElement element) {
        XSLFTable table = slide.createTable();
        applyPositionAndSize(table, element);

        // Table structure
        List<List<String>> header = (List<List<String>>) element.getContent().get("tableHeader");
        List<List<String>> data = (List<List<String>>) element.getContent().get("tableData");

        // Create table rows and cells
        createTableRows(table, header, data);

        // Apply table styles
        applyTableStyles(table, element.getStyle());
    }

    // Helper methods
    private void applyPositionAndSize(XSLFShape shape, SlideElement element) {
        Dimension pageSize = shape.getSheet().getSlideShow().getPageSize();
        Rectangle2D anchor = new Rectangle2D.Double(
                percentageToPoints(element.getX(), pageSize.getWidth()),
                percentageToPoints(element.getY(), pageSize.getHeight()),
                percentageToPoints(element.getWidth(), pageSize.getWidth()),
                percentageToPoints(element.getHeight(), pageSize.getHeight()));
        ((XSLFSimpleShape) shape).setAnchor(anchor);
    }

    private double percentageToPoints(double percentage, double totalPoints) {
        return (percentage / 100.0) * totalPoints;
    }

    private PictureData.PictureType getPictureType(String fileName) {
        String ext = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        switch (ext) {
            case "png":
                return PictureData.PictureType.PNG;
            case "jpg":
            case "jpeg":
                return PictureData.PictureType.JPEG;
            default:
                return PictureData.PictureType.PICT;
        }
    }

    private void applyTextStyles(XSLFTextRun run, Map<String, Object> styles) {
        if (styles.containsKey("fontSize")) {
            run.setFontSize(((Number) styles.get("fontSize")).doubleValue());
        }
        if (styles.containsKey("color")) {
            run.setFontColor(colorUtils.parseColor((String) styles.get("color")));
        }
    }

    private void createTableRows(XSLFTable table, List<List<String>> header, List<List<String>> data) {
        // Create header rows
        for (List<String> headerRow : header) {
            XSLFTableRow row = table.addRow();
            for (String cellText : headerRow) {
                XSLFTableCell cell = row.addCell();
                cell.setText(cellText);
            }
        }

        // Create data rows
        for (List<String> dataRow : data) {
            XSLFTableRow row = table.addRow();
            for (String cellText : dataRow) {
                XSLFTableCell cell = row.addCell();
                cell.setText(cellText);
            }
        }
    }

    private void applyTableStyles(XSLFTable table, Map<String, Object> styles) {
        List<List<Map<String, Object>>> headerStyles = (List<List<Map<String, Object>>>) styles.get("headerCellStyles");
        List<List<Map<String, Object>>> cellStyles = (List<List<Map<String, Object>>>) styles.get("cellStyles");

        // Apply header styles
        int rowIdx = 0;
        for (XSLFTableRow row : table.getRows()) {
            if (rowIdx >= headerStyles.size())
                break;

            List<Map<String, Object>> rowStyle = headerStyles.get(rowIdx);
            int cellIdx = 0;
            for (XSLFTableCell cell : row.getCells()) {
                if (cellIdx >= rowStyle.size())
                    break;
                applyCellStyle(cell, rowStyle.get(cellIdx));
                cellIdx++;
            }
            rowIdx++;
        }

        // Apply data cell styles
        int dataStartRow = headerStyles.size();
        int styleRowIdx = 0;
        for (int i = dataStartRow; i < table.getRows().size(); i++) {
            if (styleRowIdx >= cellStyles.size())
                break;

            XSLFTableRow row = table.getRows().get(i);
            List<Map<String, Object>> rowStyle = cellStyles.get(styleRowIdx);
            int cellIdx = 0;
            for (XSLFTableCell cell : row.getCells()) {
                if (cellIdx >= rowStyle.size())
                    break;
                applyCellStyle(cell, rowStyle.get(cellIdx));
                cellIdx++;
            }
            styleRowIdx++;
        }
    }

    private void applyCellStyle(XSLFTableCell cell, Map<String, Object> style) {
        // Apply background color
        if (style.containsKey("fillColor")) {
            cell.setFillColor(colorUtils.parseColor((String) style.get("fillColor")));
        }

        // Apply border
        if (style.containsKey("borderColor")) {
            Color borderColor = colorUtils.parseColor((String) style.get("borderColor"));
            cell.setBorderColor(BorderEdge.top, borderColor);
            cell.setBorderColor(BorderEdge.bottom, borderColor);
            cell.setBorderColor(BorderEdge.left, borderColor);
            cell.setBorderColor(BorderEdge.right, borderColor);
        }

        // Apply text styles
        for (XSLFTextParagraph p : cell.getTextParagraphs()) {
            for (XSLFTextRun run : p.getTextRuns()) {
                if (style.containsKey("fontSize")) {
                    run.setFontSize(((Number) style.get("fontSize")).doubleValue());
                }
                if (style.containsKey("color")) {
                    run.setFontColor(colorUtils.parseColor((String) style.get("color")));
                }
            }
        }
    }
}