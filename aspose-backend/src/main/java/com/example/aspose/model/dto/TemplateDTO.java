package com.example.aspose.model.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TemplateDTO {
    private Long standardTemplateId;
    private String name;
    private String originalFilePath;
    private double width;
    private double height;
    private String slideTitle;
    private boolean isCSI;
    private String[] legalEntities;
    private String[] presenters;
    private List<TemplateObjectDTO> stdObjectContent;
}

