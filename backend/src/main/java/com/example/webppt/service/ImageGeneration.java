package com.example.webppt.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.apache.poi.sl.usermodel.PictureData;
import org.apache.poi.xslf.usermodel.XSLFPictureShape;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.webppt.model.SlideElement;
import com.example.webppt.utils.SlideElementUtils;

@Service
public class ImageGeneration {
    @Autowired
    FileStorageService fileStorageService;

    public void addImage(XSLFSlide slide, SlideElement element) throws IOException {
        String imagePath = (String) element.getContent().get("url");
        byte[] imageData = fileStorageService.loadImage(imagePath);

        XSLFPictureShape picture = slide.createPicture(
                slide.getSlideShow().addPicture(new ByteArrayInputStream(imageData), PictureData.PictureType.PNG));

        SlideElementUtils.applyPositionAndSize(picture, element);
    }
}
