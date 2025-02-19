package com.example.webppt.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.webppt.model.PptData;
import com.example.webppt.model.SlideData;
import com.example.webppt.repository.PptDataRepository;
import java.io.IOException;
import org.apache.poi.xslf.usermodel.*;

import java.util.*;

@Service
public class PptService {
    @Autowired
    private PptDataRepository pptDataRepository;

    public void savePptData(MultipartFile file) throws IOException {
        PptData pptData = new PptData();
        List<SlideData> slideList = new ArrayList<>();

        try (XMLSlideShow ppt = new XMLSlideShow(file.getInputStream())) {
            for (XSLFSlide slide : ppt.getSlides()) {
                SlideData slideData = new SlideData();
                List<Map<String, Object>> textElements = new ArrayList<>();
                List<Map<String, Object>> imageElements = new ArrayList<>();

                for (XSLFShape shape : slide.getShapes()) {
                    if (shape instanceof XSLFTextShape textShape) {
                        for (XSLFTextParagraph paragraph : textShape.getTextParagraphs()) {
                            for (XSLFTextRun textRun : paragraph.getTextRuns()) {
                                Map<String, Object> textInfo = new HashMap<>();
                                textInfo.put("content", textRun.getRawText());
                                textInfo.put("fontSize", textRun.getFontSize());
                                textInfo.put("fontColor",
                                        textRun.getFontColor() != null ? textRun.getFontColor().toString() : "#000000");
                                textInfo.put("fontFamily", textRun.getFontFamily());
                                textElements.add(textInfo);
                            }
                        }
                    }
                    if (shape instanceof XSLFPictureShape pictureShape) {
                        Map<String, Object> imageInfo = new HashMap<>();
                        byte[] imgBytes = pictureShape.getPictureData().getData();
                        imageInfo.put("imageBase64", Base64.getEncoder().encodeToString(imgBytes));
                        imageElements.add(imageInfo);
                    }
                }

                slideData.setTexts(textElements);
                slideData.setImages(imageElements);
                slideList.add(slideData);
            }
        }

        pptData.setSlides(slideList);
        pptDataRepository.save(pptData);
    }
}