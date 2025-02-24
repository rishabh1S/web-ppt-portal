package com.example.webppt.service;

import com.example.webppt.model.*;
import com.example.webppt.utils.*;
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
    @Autowired
    StyleExtractionUtils styleExtractionUtils;

    public SlideElement processTable(XSLFTable table, Presentation presentation) {
        SlideElement element = slideElementUtils.createSlideElement(ElementType.TABLE, table, presentation);

        if (element.getStyle() == null) {
            element.setStyle(new HashMap<>());
        }

        List<List<String>> tableContent = new ArrayList<>();
        List<List<Map<String, Object>>> cellStyles = new ArrayList<>();

        for (XSLFTableRow row : table.getRows()) {
            List<String> rowContent = new ArrayList<>();
            List<Map<String, Object>> rowStyles = new ArrayList<>();

            for (XSLFTableCell cell : row.getCells()) {
                Map<String, Object> cellStyle = new HashMap<>();

                // Extract cell-level styles
                styleExtractionUtils.extractStyles(cell, cellStyle);

                // Extract text styles from cell content
                extractTextStyles(cell, cellStyle);

                // Manual border color fallback
                addBorderColorFallback(cell, cellStyle);

                rowContent.add(cell.getText());
                rowStyles.add(cellStyle);
            }

            tableContent.add(rowContent);
            cellStyles.add(rowStyles);
        }

        element.setContent(new Gson().toJson(tableContent));
        element.getStyle().put("cellStyles", cellStyles);

        return element;
    }

    private void addBorderColorFallback(XSLFTableCell cell, Map<String, Object> styleMap) {
        Color borderColor = cell.getBorderColor(XSLFTableCell.BorderEdge.bottom);
        if (borderColor != null && !styleMap.containsKey("borderColor")) {
            styleMap.put("borderColor", colorUtils.toHexColor(borderColor));
        }
    }

    private void extractTextStyles(XSLFTableCell cell, Map<String, Object> styleMap) {
        cell.getTextParagraphs().forEach(paragraph -> {
            styleExtractionUtils.extractStyles(paragraph, styleMap);
            paragraph.getTextRuns().forEach(textRun -> styleExtractionUtils.extractStyles(textRun, styleMap));
        });
    }

}
