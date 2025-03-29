package com.example.aspose.service;

import org.springframework.stereotype.Service;

import com.aspose.slides.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.awt.Color;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

@Service
public class TemplateService {
        private final ObjectMapper objectMapper = new ObjectMapper();

        public void createStandardSlide(Presentation pres, String metadataPath) {
                // Remove default slide
                pres.getSlides().removeAt(0);

                try {
                        // Read metadata from JSON file
                        Map<String, Object> metadata = readMetadataFromFile(
                                        metadataPath);

                        // Create a blank slide
                        ISlide slide = pres.getSlides().addEmptySlide(
                                        pres.getLayoutSlides().getByType(SlideLayoutType.Blank));

                        // Set slide dimensions
                        int slideWidth = (int) metadata.get("width");
                        int slideHeight = (int) metadata.get("height");
                        pres.getSlideSize().setSize(slideWidth, slideHeight, SlideSizeScaleType.DoNotScale);

                        // Add slide content based on metadata
                        addSlideContent(slide, metadata);

                } catch (Exception e) {
                        throw new RuntimeException("Error creating standard slide", e);
                }
        }

        private Map<String, Object> readMetadataFromFile(String fileName) {
                try (InputStream input = getClass().getResourceAsStream("/metadata/" + fileName)) {
                        return objectMapper.readValue(input, new TypeReference<Map<String, Object>>() {
                        });
                } catch (IOException e) {
                        throw new RuntimeException("Error reading metadata file: " + fileName, e);
                }
        }

        private void addSlideContent(ISlide slide, Map<String, Object> metadata) throws Exception {
                // Retrieve template content
                List<Map<String, Object>> templateContent = (List<Map<String, Object>>) metadata
                                .get("stdTemplateContent");

                for (Map<String, Object> object : templateContent) {
                        String objectType = (String) object.get("objectType");

                        switch (objectType) {
                                case "text":
                                        addTextShape(slide, object);
                                        break;
                                case "table":
                                        addTableShape(slide, object);
                                        break;
                        }
                }
        }

        private void addTextShape(ISlide slide, Map<String, Object> textObject) {
                // Retrieve text object properties
                int x = (int) textObject.get("x");
                int y = (int) textObject.get("y");
                int width = (int) textObject.get("width");
                int height = (int) textObject.get("height");
                String content = (String) textObject.get("content");

                // Create text shape
                IAutoShape textShape = slide.getShapes().addAutoShape(
                                ShapeType.Rectangle, x, y, width, height);
                textShape.getFillFormat().setFillType(FillType.NoFill);
                textShape.getLineFormat().setWidth(0);
                textShape.getLineFormat().getFillFormat().setFillType(FillType.NoFill);

                // Configure text properties from metadata
                Map<String, Object> styles = (Map<String, Object>) textObject.get("styles");

                // Set text
                textShape.getTextFrame().setText(content);

                // Configure font
                IPortionFormat portionFormat = textShape.getTextFrame().getParagraphs().get_Item(0).getPortions()
                                .get_Item(0).getPortionFormat();
                portionFormat.setLatinFont(new FontData((String) styles.get("fontFamily")));
                portionFormat.setFontHeight(Integer.parseInt(((String) styles.get("fontSize")).replace("pt", "")));

                // Set text color
                String colorStr = (String) styles.get("color");
                portionFormat.getFillFormat().setFillType(FillType.Solid);
                portionFormat.getFillFormat().getSolidFillColor().setColor(parseColor(colorStr));

                // Configure paragraph alignment
                textShape.getTextFrame().getParagraphs().get_Item(0).getParagraphFormat().setAlignment(
                                getTextAlignment((String) styles.get("textAlign")));

                // Set background color if exists
                if (styles.containsKey("backgroundColor")) {
                        textShape.getFillFormat().setFillType(FillType.Solid);
                        textShape.getFillFormat().getSolidFillColor()
                                        .setColor(parseColor((String) styles.get("backgroundColor")));
                }
        }

        private void addTableShape(ISlide slide, Map<String, Object> tableObject) {
                // Retrieve table properties
                float x = ((Number) tableObject.get("x")).floatValue();
                float y = ((Number) tableObject.get("y")).floatValue();
                double width = ((Number) tableObject.get("width")).doubleValue();
                double height = ((Number) tableObject.get("height")).doubleValue();
                List<String> headers = (List<String>) tableObject.get("headers");
                int colCount = headers.size();

                // Create evenly distributed column widths
                double[] columnWidths = new double[colCount];
                for (int i = 0; i < colCount; i++) {
                        columnWidths[i] = width / colCount;
                }

                // Process the table's body content.
                // If the content array is empty, we create one body row for styling.
                List<List<String>> content = (List<List<String>>) tableObject.get("content");
                int bodyRows = (content == null || content.isEmpty()) ? 1 : content.size();
                int totalRows = 1 + bodyRows; // 1 header row plus body rows

                // Create evenly distributed row heights
                double[] rowHeights = new double[totalRows];
                for (int i = 0; i < totalRows; i++) {
                        rowHeights[i] = height / totalRows;
                }

                // Create the table with header and body rows
                ITable table = slide.getShapes().addTable(x, y, columnWidths, rowHeights);

                // Set header row cells with header text
                for (int col = 0; col < colCount; col++) {
                        table.get_Item(col, 0).getTextFrame().setText(headers.get(col));
                }

                // Fill body rows
                // If no content provided, cells will be empty but still rendered.
                if (content != null && !content.isEmpty()) {
                        for (int r = 0; r < content.size(); r++) {
                                List<String> rowContent = content.get(r);
                                for (int col = 0; col < colCount; col++) {
                                        // Set cell text if available; otherwise, leave it empty.
                                        String cellText = (rowContent.size() > col) ? rowContent.get(col) : "";
                                        table.get_Item(col, r + 1).getTextFrame().setText(cellText);
                                }
                        }
                } else {
                        // Create an empty body row with empty strings
                        for (int col = 0; col < colCount; col++) {
                                table.get_Item(col, 1).getTextFrame().setText("");
                        }
                }

                // Configure table styles (borders, fonts, etc.)
                Map<String, Object> styles = (Map<String, Object>) tableObject.get("styles");
                configureTableStyles(table, styles);
        }

        private void configureTableStyles(ITable table, Map<String, Object> styles) {
                // Apply border style to the whole table using the simplified border metadata.
                if (styles.containsKey("border")) {
                        Map<String, Object> borderMap = (Map<String, Object>) styles.get("border");
                        // Assuming width is provided as a numeric value (e.g., 1 for 1pt)
                        float borderWidth = ((Number) borderMap.get("width")).floatValue();
                        Color borderColor = parseColor((String) borderMap.get("color"));
                        applyBorderToTable(table, borderWidth, borderColor);
                }

                // Configure header style
                Map<String, Object> headerStyle = (Map<String, Object>) styles.get("header");
                if (headerStyle != null) {
                        for (int col = 0; col < table.getColumns().size(); col++) {
                                // Set header background color
                                if (headerStyle.containsKey("backgroundColor")) {
                                        table.getRows().get_Item(0).get_Item(col)
                                                        .getCellFormat().getFillFormat().setFillType(FillType.Solid);
                                        table.getRows().get_Item(0).get_Item(col)
                                                        .getCellFormat().getFillFormat().getSolidFillColor()
                                                        .setColor(parseColor(
                                                                        (String) headerStyle.get("backgroundColor")));
                                }

                                ITextFrame headerTextFrame = table.getRows().get_Item(0).get_Item(col).getTextFrame();
                                IPortionFormat headerPortionFormat = headerTextFrame.getParagraphs().get_Item(0)
                                                .getPortions().get_Item(0).getPortionFormat();

                                headerPortionFormat.setLatinFont(new FontData((String) headerStyle.get("fontFamily")));
                                headerPortionFormat.setFontHeight(
                                                Integer.parseInt(((String) headerStyle.get("fontSize")).replace("pt",
                                                                "")));
                                headerPortionFormat.setFontBold(
                                                (byte) (Boolean.TRUE.equals(headerStyle.get("fontWeight")) ? 1 : 0));
                                headerPortionFormat.getFillFormat().setFillType(FillType.Solid);
                                headerPortionFormat.getFillFormat().getSolidFillColor()
                                                .setColor(parseColor((String) headerStyle.get("color")));

                                // Set header text alignment if specified
                                if (headerStyle.containsKey("textAlign")) {
                                        String alignment = (String) headerStyle.get("textAlign");
                                        headerTextFrame.getParagraphs().get_Item(0).getParagraphFormat()
                                                        .setAlignment(getTextAlignment(alignment));
                                }
                        }
                }

                // Configure body style
                Map<String, Object> bodyStyle = (Map<String, Object>) styles.get("body");
                if (bodyStyle != null) {
                        for (int row = 1; row < table.getRows().size(); row++) {
                                for (int col = 0; col < table.getColumns().size(); col++) {
                                        ITextFrame bodyTextFrame = table.getRows().get_Item(row).get_Item(col)
                                                        .getTextFrame();
                                        IPortionFormat bodyPortionFormat = bodyTextFrame.getParagraphs().get_Item(0)
                                                        .getPortions().get_Item(0).getPortionFormat();

                                        bodyPortionFormat.setLatinFont(
                                                        new FontData((String) bodyStyle.get("fontFamily")));
                                        bodyPortionFormat.setFontHeight(
                                                        Integer.parseInt(((String) bodyStyle.get("fontSize"))
                                                                        .replace("pt", "")));
                                        bodyPortionFormat.setFontBold(
                                                        (byte) (Boolean.TRUE.equals(bodyStyle.get("fontWeight")) ? 1
                                                                        : 0));
                                        bodyPortionFormat.getFillFormat().setFillType(FillType.Solid);
                                        bodyPortionFormat.getFillFormat().getSolidFillColor()
                                                        .setColor(parseColor((String) bodyStyle.get("color")));

                                        // Set body text alignment if specified
                                        if (bodyStyle.containsKey("textAlign")) {
                                                String alignment = (String) bodyStyle.get("textAlign");
                                                bodyTextFrame.getParagraphs().get_Item(0).getParagraphFormat()
                                                                .setAlignment(getTextAlignment(alignment));
                                        }
                                }
                        }
                }
        }

        private void applyBorderToTable(ITable table, float borderWidth, Color borderColor) {
                for (IRow row : table.getRows()) {
                        for (int i = 0; i < row.size(); i++) {
                                ICell cell = row.get_Item(i);
                                // Top border
                                cell.getCellFormat().getBorderTop().getFillFormat().setFillType(FillType.Solid);
                                cell.getCellFormat().getBorderTop().getFillFormat().getSolidFillColor()
                                                .setColor(borderColor);
                                cell.getCellFormat().getBorderTop().setWidth(borderWidth);
                                // Bottom border
                                cell.getCellFormat().getBorderBottom().getFillFormat().setFillType(FillType.Solid);
                                cell.getCellFormat().getBorderBottom().getFillFormat().getSolidFillColor()
                                                .setColor(borderColor);
                                cell.getCellFormat().getBorderBottom().setWidth(borderWidth);
                                // Left border
                                cell.getCellFormat().getBorderLeft().getFillFormat().setFillType(FillType.Solid);
                                cell.getCellFormat().getBorderLeft().getFillFormat().getSolidFillColor()
                                                .setColor(borderColor);
                                cell.getCellFormat().getBorderLeft().setWidth(borderWidth);
                                // Right border
                                cell.getCellFormat().getBorderRight().getFillFormat().setFillType(FillType.Solid);
                                cell.getCellFormat().getBorderRight().getFillFormat().getSolidFillColor()
                                                .setColor(borderColor);
                                cell.getCellFormat().getBorderRight().setWidth(borderWidth);
                        }
                }
        }

        private int getTextAlignment(String alignmentStr) {
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

        private Color parseColor(String colorStr) {
                // Remove 'rgb(' and ')' and split into components
                String[] components = colorStr.replaceAll("[rgb()\\s]", "").split(",");
                return new Color(
                                Integer.parseInt(components[0]),
                                Integer.parseInt(components[1]),
                                Integer.parseInt(components[2]));
        }
}
