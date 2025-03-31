package com.example.aspose.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import lombok.Data;

import java.util.Map;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "objectType", visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = TemplateTextContent.class, name = "text"),
        @JsonSubTypes.Type(value = TemplateTableContent.class, name = "table")
})

@Data
public abstract class TemplateContent {
    private int objectId;
    private String objectType;
    private String objectRole;
    private int x;
    private int y;
    private int height;
    private int width;
    private Map<String, Object> styles;
}
