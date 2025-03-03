package com.example.webppt.service;

import com.example.webppt.model.*;
import com.example.webppt.utils.*;

import org.apache.poi.xslf.usermodel.*;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class TableProcessingService {

    public SlideElement processTable(XSLFTable table, Presentation presentation) {
        SlideElement element = SlideElementUtils.createSlideElement(ElementType.TABLE, table, presentation);
        element.setStyle(Optional.ofNullable(element.getStyle()).orElseGet(HashMap::new));

        List<List<String>> tableHeader = new ArrayList<>(), tableData = new ArrayList<>();
        List<List<Map<String, Object>>> headerCellStyles = new ArrayList<>(), cellStyles = new ArrayList<>();

        boolean isHeader = true;
        for (XSLFTableRow row : table.getRows()) {
            List<String> rowContent = new ArrayList<>();
            List<Map<String, Object>> rowStyles = new ArrayList<>();

            row.getCells().forEach(cell -> {
                Map<String, Object> cellStyle = extractCellStyle(cell);
                rowContent.add(cell.getText());
                rowStyles.add(cellStyle);
            });

            if (isHeader) {
                tableHeader.add(rowContent);
                headerCellStyles.add(rowStyles);
                isHeader = false;
            } else {
                tableData.add(rowContent);
                cellStyles.add(rowStyles);
            }
        }

        element.setContent(Map.of("tableHeader", tableHeader, "tableData", tableData));
        element.getStyle().put("headerCellStyles", headerCellStyles);
        element.getStyle().put("cellStyles", cellStyles);
        return element;
    }

    private Map<String, Object> extractCellStyle(XSLFTableCell cell) {
        Map<String, Object> styleMap = new HashMap<>();
        StyleExtractionUtils.extractStyles(cell, styleMap);
        cell.getTextParagraphs().forEach(paragraph -> {
            StyleExtractionUtils.extractStyles(paragraph, styleMap);
            paragraph.getTextRuns().forEach(textRun -> StyleExtractionUtils.extractStyles(textRun, styleMap));
        });
        Optional.ofNullable(cell.getBorderColor(XSLFTableCell.BorderEdge.bottom))
                .ifPresent(borderColor -> styleMap.put("borderColor", ColorUtils.toHexColor(borderColor)));
        return styleMap;
    }

}
