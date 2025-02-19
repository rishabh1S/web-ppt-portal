package com.example.webppt.service;

import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFPictureShape;
import org.apache.poi.xslf.usermodel.XSLFShape;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.apache.poi.xslf.usermodel.XSLFTextShape;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PptService {
    public Map<String, Object> extractPptData(MultipartFile file) throws IOException {
        Map<String, Object> data = new HashMap<>();
        List<String> texts = new ArrayList<>();
        List<byte[]> images = new ArrayList<>();
        // Open the PPTX file using Apache POI
        try (XMLSlideShow ppt = new XMLSlideShow(file.getInputStream())) {
            // Loop through each slide
            for (XSLFSlide slide : ppt.getSlides()) {
                StringBuilder slideText = new StringBuilder();
                // Loop through each shape on the slide
                for (XSLFShape shape : slide.getShapes()) {
                    // If the shape contains text, extract it
                    if (shape instanceof XSLFTextShape) {
                        slideText.append(((XSLFTextShape) shape).getText()).append(" ");
                    }
                    // If the shape is a picture, extract the image data
                    if (shape instanceof XSLFPictureShape) {
                        images.add(((XSLFPictureShape) shape).getPictureData().getData());
                    }
                }
                texts.add(slideText.toString().trim());
            }
        }
        data.put("texts", texts);
        data.put("images", images);
        return data;
    }
}