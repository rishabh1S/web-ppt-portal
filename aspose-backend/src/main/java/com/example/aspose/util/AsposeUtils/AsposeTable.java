package com.example.aspose.util.AsposeUtils;

import com.aspose.slides.*;
import com.example.aspose.model.TemplateTableContent;
import java.awt.Color;
import java.util.List;
import java.util.Map;

public class AsposeTable {

    public static void addTableShape(ISlide slide, TemplateTableContent tableContent) {
        float x = tableContent.getX();
        float y = tableContent.getY();
        double width = tableContent.getWidth();
        double height = tableContent.getHeight();
        List<String> headers = tableContent.getHeaders();
        int colCount = headers.size();

        // Create evenly distributed column widths
        double[] columnWidths = new double[colCount];
        for (int i = 0; i < colCount; i++) {
            columnWidths[i] = width / colCount;
        }

        // Process table body content
        List<List<String>> content = tableContent.getContent();
        int bodyRows = (content == null || content.isEmpty()) ? 1 : content.size();
        int totalRows = 1 + bodyRows; // 1 header row plus body rows

        // First, create the table with initial equal row heights
        // We'll adjust them after setting content
        double initialRowHeight = 0;
        double[] initialRowHeights = new double[totalRows];
        for (int i = 0; i < totalRows; i++) {
            initialRowHeights[i] = initialRowHeight;
        }

        // Create the table with initial row heights
        ITable table = slide.getShapes().addTable(x, y, columnWidths, initialRowHeights);

        // Set header row cells with header text
        for (int col = 0; col < colCount; col++) {
            table.get_Item(col, 0).getTextFrame().setText(headers.get(col));
        }

        // Fill body rows
        if (content != null && !content.isEmpty()) {
            for (int r = 0; r < content.size(); r++) {
                List<String> rowContent = content.get(r);
                for (int col = 0; col < colCount; col++) {
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

        // Configure table styles (borders, fonts, etc.) using the POJO's styles map
        Map<String, Object> styles = tableContent.getStyles();
        configureTableStyles(table, styles);

        // Now adjust row heights based on content
        adjustRowHeightsBasedOnContent(table, height, totalRows);
    }

    /**
     * Adjusts row heights based on content with header row being sized to its
     * content
     * and remaining space distributed proportionally among body rows
     */
    private static void adjustRowHeightsBasedOnContent(ITable table, double totalTableHeight, int totalRows) {
        if (totalRows <= 1)
            return;

        try {
            // First, make the header row auto-fit to its content
            table.get_Item(0, 0).getTextFrame().getTextFrameFormat().setAutofitType(TextAutofitType.Normal);
            for (int col = 0; col < table.getColumns().size(); col++) {
                table.get_Item(col, 0).getTextFrame().getTextFrameFormat().setAutofitType(TextAutofitType.Normal);
            }

            // Get the actual height of the header row after autofit
            double headerRowHeight = table.getRows().get_Item(0).getHeight();

            // Calculate remaining height for body rows
            double remainingHeight = totalTableHeight - headerRowHeight;
            double bodyRowHeight = remainingHeight / (totalRows - 1); // Divide by number of body rows

            // Set body row heights
            for (int i = 1; i < totalRows; i++) {
                table.getRows().get_Item(i).setMinimalHeight(bodyRowHeight);
            }
        } catch (Exception e) {
            // Log any issues with row height adjustment
            System.err.println("Error adjusting row heights: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private static void configureTableStyles(ITable table, Map<String, Object> styles) {
        if (styles.containsKey("border")) {
            Map<String, Object> borderMap = (Map<String, Object>) styles.get("border");
            float borderWidth = ((Number) borderMap.get("width")).floatValue();
            Color borderColor = AsposeCommon.parseColor((String) borderMap.get("color"));
            applyBorderToTable(table, borderWidth, borderColor);
        }

        Map<String, Object> headerStyle = (Map<String, Object>) styles.get("header");
        if (headerStyle != null) {
            for (int col = 0; col < table.getColumns().size(); col++) {
                // Set header background color if provided
                if (headerStyle.containsKey("backgroundColor")) {
                    table.getRows().get_Item(0).get_Item(col)
                            .getCellFormat().getFillFormat().setFillType(FillType.Solid);
                    table.getRows().get_Item(0).get_Item(col)
                            .getCellFormat().getFillFormat().getSolidFillColor()
                            .setColor(AsposeCommon.parseColor((String) headerStyle.get("backgroundColor")));
                }

                ITextFrame headerTextFrame = table.getRows().get_Item(0).get_Item(col).getTextFrame();

                IPortionFormat headerPortionFormat = headerTextFrame.getParagraphs().get_Item(0)
                        .getPortions().get_Item(0).getPortionFormat();

                headerPortionFormat.setLatinFont(new FontData((String) headerStyle.get("fontFamily")));
                headerPortionFormat.setFontHeight(
                        Integer.parseInt(((String) headerStyle.get("fontSize")).replace("pt", "")));
                headerPortionFormat.setFontBold(
                        (byte) (Boolean.TRUE.equals(headerStyle.get("isBold")) ? 1 : 0));
                headerPortionFormat.getFillFormat().setFillType(FillType.Solid);
                headerPortionFormat.getFillFormat().getSolidFillColor()
                        .setColor(AsposeCommon.parseColor((String) headerStyle.get("color")));

                // Set header text alignment if specified
                if (headerStyle.containsKey("textAlign")) {
                    String alignment = (String) headerStyle.get("textAlign");
                    headerTextFrame.getParagraphs().get_Item(0).getParagraphFormat()
                            .setAlignment(AsposeCommon.getTextAlignment(alignment));
                }
            }
        }

        Map<String, Object> bodyStyle = (Map<String, Object>) styles.get("body");
        if (bodyStyle != null) {
            for (int row = 1; row < table.getRows().size(); row++) {
                for (int col = 0; col < table.getColumns().size(); col++) {
                    ICell cell = table.getRows().get_Item(row).get_Item(col);
                    cell.getCellFormat().getFillFormat().setFillType(FillType.NoFill);
                    ITextFrame bodyTextFrame = table.getRows().get_Item(row).get_Item(col).getTextFrame();
                    IPortionFormat bodyPortionFormat = bodyTextFrame.getParagraphs().get_Item(0)
                            .getPortions().get_Item(0).getPortionFormat();

                    bodyPortionFormat.setLatinFont(new FontData((String) bodyStyle.get("fontFamily")));
                    bodyPortionFormat.setFontHeight(
                            Integer.parseInt(((String) bodyStyle.get("fontSize")).replace("pt", "")));
                    bodyPortionFormat.setFontBold(
                            (byte) (Boolean.TRUE.equals(bodyStyle.get("isBold")) ? 1 : 0));
                    bodyPortionFormat.getFillFormat().setFillType(FillType.Solid);
                    bodyPortionFormat.getFillFormat().getSolidFillColor()
                            .setColor(AsposeCommon.parseColor((String) bodyStyle.get("color")));

                    // Set body text alignment if specified
                    if (bodyStyle.containsKey("textAlign")) {
                        String alignment = (String) bodyStyle.get("textAlign");
                        bodyTextFrame.getParagraphs().get_Item(0).getParagraphFormat()
                                .setAlignment(AsposeCommon.getTextAlignment(alignment));
                    }
                }
            }
        }
    }

    private static void applyBorderToTable(ITable table, float borderWidth, Color borderColor) {
        for (IRow row : table.getRows()) {
            for (int i = 0; i < row.size(); i++) {
                ICell cell = row.get_Item(i);
                // Top border
                cell.getCellFormat().getBorderTop().getFillFormat().setFillType(FillType.Solid);
                cell.getCellFormat().getBorderTop().getFillFormat().getSolidFillColor().setColor(borderColor);
                cell.getCellFormat().getBorderTop().setWidth(borderWidth);
                // Bottom border
                cell.getCellFormat().getBorderBottom().getFillFormat().setFillType(FillType.Solid);
                cell.getCellFormat().getBorderBottom().getFillFormat().getSolidFillColor().setColor(borderColor);
                cell.getCellFormat().getBorderBottom().setWidth(borderWidth);
                // Left border
                cell.getCellFormat().getBorderLeft().getFillFormat().setFillType(FillType.Solid);
                cell.getCellFormat().getBorderLeft().getFillFormat().getSolidFillColor().setColor(borderColor);
                cell.getCellFormat().getBorderLeft().setWidth(borderWidth);
                // Right border
                cell.getCellFormat().getBorderRight().getFillFormat().setFillType(FillType.Solid);
                cell.getCellFormat().getBorderRight().getFillFormat().getSolidFillColor().setColor(borderColor);
                cell.getCellFormat().getBorderRight().setWidth(borderWidth);
            }
        }
    }
}
