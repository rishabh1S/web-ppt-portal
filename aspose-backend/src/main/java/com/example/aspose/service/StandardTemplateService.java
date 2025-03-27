package com.example.aspose.service;

import org.springframework.stereotype.Service;

import com.aspose.slides.ISlide;
import com.aspose.slides.Presentation;
import com.aspose.slides.SlideLayoutType;
import com.example.aspose.util.TemplateWriter;

@Service
public class StandardTemplateService {

        public void createStandardSlide(Presentation pres) {
                // Create a new presentation
                pres.getSlides().removeAt(0);
                ISlide slide = pres.getSlides().addEmptySlide(
                                pres.getLayoutSlides().getByType(SlideLayoutType.Blank));
                // Add static fields
                TemplateWriter.addStaticFields(slide);

                // Add dynamic objects
                TemplateWriter.addDynamicContent(slide);
        }

}
