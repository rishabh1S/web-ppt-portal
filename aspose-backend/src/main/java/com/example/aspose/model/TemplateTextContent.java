package com.example.aspose.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class TemplateTextContent extends TemplateContent {
    @JsonProperty("content")
    private String content;
}
