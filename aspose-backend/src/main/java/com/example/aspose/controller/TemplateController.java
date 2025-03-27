package com.example.aspose.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aspose.slides.Presentation;
import com.aspose.slides.SaveFormat;
import com.example.aspose.service.StandardTemplateService;
import com.example.aspose.util.AsposeLicense;

import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/standard-slide")
public class TemplateController {
    @Autowired
    private StandardTemplateService standardTemplateService;

    @GetMapping
    public void generateOverviewSlide(HttpServletResponse response) throws IOException {
        AsposeLicense.setLicense();
        response.setContentType("application/vnd.openxmlformats-officedocument.presentationml.presentation");
        response.setHeader("Content-Disposition", "attachment; filename=presentation.pptx");

        Presentation pres = null;
        try {
            pres = new Presentation();
            standardTemplateService.createStandardSlide(pres);
            pres.save(response.getOutputStream(), SaveFormat.Pptx);
        } finally {
            if (pres != null) {
                pres.dispose();
            }
        }
    }
}
