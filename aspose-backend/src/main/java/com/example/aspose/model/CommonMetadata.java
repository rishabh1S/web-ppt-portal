package com.example.aspose.model;

import lombok.Data;

import java.util.List;

@Data
public class CommonMetadata {
    private TemplateTextContent title;
    private TemplateTextContent csi;
    private TemplateTextContent footer;
    private List<TemplateTextContent> legalEntity;
    private TemplateTextContent presenters;
}
