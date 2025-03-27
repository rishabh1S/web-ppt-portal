package com.example.aspose.model.dto;

import java.util.Map;

import com.example.aspose.model.enums.ObjectType;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TemplateObjectDTO {
    private Long objectId;
    private ObjectType objectType;
    private double x;
    private double y;
    private double width;
    private double height;
    private Map<String, Object> content;
    private Map<String, Object> style;
}
