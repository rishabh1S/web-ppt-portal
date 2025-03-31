package com.example.aspose.model;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SlideContent {

    private int slideId;
    private String slideTitle;
    private List<String> legalEntity;
    private List<Presenter> presenter;
    private String footer;
    private String isCSI;
    private Map<String, List<String>> stdSlideContent;
}
