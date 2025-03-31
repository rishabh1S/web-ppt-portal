package com.example.aspose.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aspose.slides.Presentation;
import com.aspose.slides.SaveFormat;
import com.example.aspose.service.TemplateService;
import com.example.aspose.util.AsposeLicense;

import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/standard-slide")
public class TemplateController {
    @Autowired
    private TemplateService templateService;

    @GetMapping("/significant")
    public void generateSignificantSlide(HttpServletResponse response) throws IOException {
        AsposeLicense.setLicense();
        response.setContentType("application/vnd.openxmlformats-officedocument.presentationml.presentation");
        response.setHeader("Content-Disposition", "attachment; filename=significant.pptx");

        Presentation pres = null;
        try {
            pres = new Presentation();
            templateService.createStandardSlide(pres,
                    "SignificantMetadata.json");
            pres.save(response.getOutputStream(), SaveFormat.Pptx);
        } finally {
            if (pres != null) {
                pres.dispose();
            }
        }
    }

    @GetMapping("/escalation")
    public void generateEscalationSlide(HttpServletResponse response) throws IOException {
        AsposeLicense.setLicense();
        response.setContentType("application/vnd.openxmlformats-officedocument.presentationml.presentation");
        response.setHeader("Content-Disposition", "attachment; filename=escalation.pptx");

        Presentation pres = null;
        try {
            pres = new Presentation();
            templateService.createStandardSlide(pres,
                    "EscalationMetadata.json");
            pres.save(response.getOutputStream(), SaveFormat.Pptx);
        } finally {
            if (pres != null) {
                pres.dispose();
            }
        }
    }

    @GetMapping("/risk")
    public void generateRiskSlide(HttpServletResponse response) throws IOException {
        AsposeLicense.setLicense();
        response.setContentType("application/vnd.openxmlformats-officedocument.presentationml.presentation");
        response.setHeader("Content-Disposition", "attachment; filename=risk.pptx");

        Presentation pres = null;
        try {
            pres = new Presentation();
            templateService.createStandardSlide(pres,
                    "RiskMetadata.json");
            pres.save(response.getOutputStream(), SaveFormat.Pptx);
        } finally {
            if (pres != null) {
                pres.dispose();
            }
        }
    }
}
