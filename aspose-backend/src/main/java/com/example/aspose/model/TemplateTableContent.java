package com.example.aspose.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class TemplateTableContent extends TemplateContent {
    @JsonProperty("headers")
    private List<String> headers;

    // The "content" field for tables is an array of arrays.
    @JsonProperty("content")
    private List<List<String>> content;
}
