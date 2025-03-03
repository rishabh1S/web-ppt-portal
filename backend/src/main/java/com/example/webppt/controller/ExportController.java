package com.example.webppt.controller;

import com.aspose.slides.*;
import com.example.webppt.repository.PresentationRepository;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.util.UUID;

@RestController
@RequestMapping("/api/presentations")
public class ExportController {


    @Autowired
    PresentationRepository presentationRepo;
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(ExportController.class);

    @GetMapping("/{id}/export")
    public ResponseEntity<ByteArrayResource> exportPresentation(@PathVariable UUID id) {
        logger.info("Starting export for presentation ID: {}", id);

        // Fetch presentation from database
        com.example.webppt.model.Presentation dbPresentation = presentationRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Presentation not found"));

        logger.info("Total Slides Retrieved: {}", dbPresentation.getSlides().size());

        Presentation ppt = new Presentation();
        ppt.getSlides().removeAt(0); // Remove default empty slide

        try {
            for (com.example.webppt.model.Slide dbSlide : dbPresentation.getSlides()) {
                ISlide slide = ppt.getSlides().addEmptySlide(ppt.getLayoutSlides().get_Item(0));

                if (slide == null) {
                    logger.warn("Slide {} could not be created", dbSlide.getSlideNumber());
                    continue;
                }

                String rawHtml = dbSlide.getHtmlContent();
                logger.info("Original HTML for Slide {}: {}", dbSlide.getSlideNumber(), rawHtml);

                // Convert SVG-based text to standard HTML
                String cleanedHtml = extractSvgTextAsHtml(rawHtml);
                logger.info("Cleaned HTML for Slide {}: {}", dbSlide.getSlideNumber(), cleanedHtml);

                // Add a shape and insert the cleaned HTML content
                IAutoShape htmlShape = slide.getShapes().addAutoShape(ShapeType.Rectangle, 50, 50, 700, 500);
                htmlShape.addTextFrame("");

                ITextFrame textFrame = htmlShape.getTextFrame();
                if (textFrame != null) {
                    textFrame.getParagraphs().clear(); // Clear default empty paragraph
                    textFrame.getParagraphs().addFromHtml(cleanedHtml);

                    logger.info("HTML successfully added to slide {}", dbSlide.getSlideNumber());
                } else {
                    logger.error("TextFrame is null for slide {}", dbSlide.getSlideNumber());
                }
            }

            // Save PPTX file
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ppt.save(outputStream, SaveFormat.Pptx);
            ppt.dispose(); // Free Aspose resources

            ByteArrayResource resource = new ByteArrayResource(outputStream.toByteArray());

            logger.info("Final PPTX file size: {} bytes", outputStream.toByteArray().length);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=presentation.pptx")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);

        } catch (Exception e) {
            logger.error("Error exporting presentation: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(null);
        }
    }

    // ✅ Extracts text from SVG and converts it to valid HTML
    private String extractSvgTextAsHtml(String htmlContent) {
        Document doc = Jsoup.parse(htmlContent);

        StringBuilder extractedHtml = new StringBuilder();
        Elements textElements = doc.select("svg text"); // Select all <text> elements inside <svg>

        for (Element textElement : textElements) {
            String fontFamily = textElement.attr("font-family");
            String fontSize = textElement.attr("font-size");
            String color = textElement.attr("fill");

            // Extract text content from <tspan>
            StringBuilder textContent = new StringBuilder();
            for (Element tspan : textElement.select("tspan")) {
                textContent.append(tspan.text()).append(" ");
            }

            // Convert extracted SVG text to valid HTML
            extractedHtml.append("<p style='font-family:").append(fontFamily)
                         .append("; font-size:").append(fontSize)
                         .append("; color:").append(color)
                         .append(";'>")
                         .append(textContent.toString().trim())
                         .append("</p>");
        }

        return extractedHtml.toString();
    }
}

