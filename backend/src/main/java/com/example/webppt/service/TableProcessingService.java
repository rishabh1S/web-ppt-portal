package com.example.webppt.service;

import com.example.webppt.model.*;
import com.example.webppt.utils.ColorUtils;
import com.example.webppt.utils.SlideElementUtils;
import com.google.gson.Gson;

import org.apache.poi.xslf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.util.*;

@Service
public class TableProcessingService {
    @Autowired
    private SlideElementUtils slideElementUtils;
    @Autowired
    ColorUtils colorUtils;

    public SlideElement processTable(XSLFTable table, Presentation presentation) {
        SlideElement element = slideElementUtils.createSlideElement(ElementType.TABLE, table, presentation);

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
                cellStyle.put("backgroundColor",
                        backgroundColor != null ? colorUtils.toHexColor(backgroundColor) : "transparent");

                // Extract border colors (example for bottom border)
                Color borderColor = cell.getBorderColor(XSLFTableCell.BorderEdge.bottom);
                cellStyle.put("borderColor", borderColor != null ? colorUtils.toHexColor(borderColor) : "transparent");

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
}
