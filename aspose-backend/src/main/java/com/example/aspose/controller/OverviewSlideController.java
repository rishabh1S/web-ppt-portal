package com.example.aspose.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aspose.slides.Presentation;
import com.aspose.slides.SaveFormat;
import com.example.aspose.service.OverviewSlideService;

import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/overview-slide")
public class OverviewSlideController {
    @Autowired
    private OverviewSlideService overviewSlideService;

    @GetMapping
    public void generateOverviewSlide(HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.presentationml.presentation");
        response.setHeader("Content-Disposition", "attachment; filename=overview.pptx");

        Presentation pres = null;
        try {
            pres = new Presentation();
            overviewSlideService.createOverviewSlide(pres);
            pres.save(response.getOutputStream(), SaveFormat.Pptx);
        } finally {
            if (pres != null) {
                pres.dispose();
            }
        }
    }
}
