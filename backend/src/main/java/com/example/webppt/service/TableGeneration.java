package com.example.webppt.service;

import com.example.webppt.model.SlideElement;
import org.apache.poi.xslf.usermodel.*;
import org.springframework.stereotype.Service;
import java.awt.geom.Rectangle2D;
import java.util.List;
import java.util.Map;
import com.example.webppt.utils.StyleApplicationUtils;

@Service
public class TableGeneration {

    @SuppressWarnings("unchecked")
    public void addTable(XSLFSlide slide, SlideElement element) {
        // Get table data from content
        Map<String, Object> content = element.getContent();
        Map<String, Object> style = element.getStyle();

        if (content == null || style == null) {
            return;
        }

        // Extract header and data from content
        List<List<String>> tableHeader = (List<List<String>>) content.get("tableHeader");
        List<List<String>> tableData = (List<List<String>>) content.get("tableData");

        if (tableHeader == null || tableData == null || tableHeader.isEmpty()) {
            return;
        }

        // Create table
        XSLFTable table = slide.createTable();

        // Calculate the table position in points (not percentages)
        double slideWidth = slide.getSlideShow().getPageSize().getWidth();
        double slideHeight = slide.getSlideShow().getPageSize().getHeight();
        double xPosition = element.getX() * slideWidth / 100;
        double yPosition = element.getY() * slideHeight / 100;
        double tableWidth = element.getWidth() * slideWidth / 100;
        double tableHeight = element.getHeight() * slideHeight / 100;

        table.setAnchor(new Rectangle2D.Double(xPosition, yPosition, tableWidth, tableHeight));

        // Add header rows
        addTableRows(table, tableHeader, true, style);

        // Add data rows
        addTableRows(table, tableData, false, style);

        // Calculate total number of columns
        int numCols = Math.max(
                tableHeader.isEmpty() ? 0 : tableHeader.get(0).size(),
                tableData.isEmpty() ? 0 : tableData.get(0).size());

        // Set equal width for all columns
        if (numCols > 0) {
            double colWidth = tableWidth / numCols;
            for (int i = 0; i < numCols; i++) {
                table.setColumnWidth(i, colWidth);
            }
        }

        // Set equal row heights
        int totalRows = table.getNumberOfRows();
        if (totalRows > 0) {
            double rowHeight = tableHeight / totalRows;
            for (int i = 0; i < totalRows; i++) {
                table.setRowHeight(i, rowHeight);
            }
        }

        // Update the cell anchors to ensure proper dimensions
        table.updateCellAnchor();
    }

    @SuppressWarnings("unchecked")
    private void addTableRows(XSLFTable table, List<List<String>> rows, boolean isHeader, Map<String, Object> style) {
        List<List<Map<String, Object>>> cellStyles = isHeader
                ? (List<List<Map<String, Object>>>) style.get("headerCellStyles")
                : (List<List<Map<String, Object>>>) style.get("cellStyles");

        for (int i = 0; i < rows.size(); i++) {
            List<String> rowContent = rows.get(i);
            XSLFTableRow tableRow = table.addRow();

            for (int j = 0; j < rowContent.size(); j++) {
                XSLFTableCell cell = tableRow.addCell();
                String cellText = rowContent.get(j);

                // Clear default text and create a new text run
                cell.clearText();
                XSLFTextParagraph paragraph = cell.addNewTextParagraph();
                XSLFTextRun textRun = paragraph.addNewTextRun();
                textRun.setText(cellText);

                // Apply cell styles if available
                if (cellStyles != null && i < cellStyles.size() && j < cellStyles.get(i).size()) {
                    Map<String, Object> styles = cellStyles.get(i).get(j);

                    // Apply cell-level styles (e.g., fillColor)
                    StyleApplicationUtils.applyStyles(cell, styles);

                    // Apply text-level styles (e.g., bold, fontSize)
                    StyleApplicationUtils.applyStyles(textRun, styles);

                    // Apply paragraph-level styles
                    StyleApplicationUtils.applyStyles(paragraph, styles);
                }
            }
        }
    }
}
