package com.example.webppt.service;

import com.example.webppt.model.*;
import com.example.webppt.utils.SlideElementUtils;

import org.apache.poi.xslf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;

@Service
public class ImageProcessingService {
    @Autowired
    FileStorageService fileStorageService;

    public SlideElement processImage(XSLFPictureShape pictureShape, Presentation presentation) throws IOException {
        SlideElement element = SlideElementUtils.createSlideElement(ElementType.IMAGE, pictureShape, presentation);

        // Save image to storage and get the file path
        String imagePath = fileStorageService.storeImage(pictureShape.getPictureData().getData());
        element.setContent(Map.of("url", imagePath));

        return element;
    }

}
