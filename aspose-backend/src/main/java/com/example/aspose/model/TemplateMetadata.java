package com.example.aspose.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

import java.util.List;

@Data
public class TemplateMetadata {
    @JsonProperty("stdTemplateId")
    private int stdTemplateId;

    @JsonProperty("slideTitle")
    private String slideTitle;

    @JsonProperty("legalEntity")
    private List<String> legalEntity;

    @JsonProperty("presenters")
    private List<String> presenters;

    @JsonProperty("isCSI")
    private boolean isCSI;

    @JsonProperty("height")
    private int height;

    @JsonProperty("width")
    private int width;

    @JsonProperty("stdTemplateContent")
    private List<TemplateContent> stdTemplateContent;

}
