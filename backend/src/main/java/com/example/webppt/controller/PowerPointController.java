package com.example.webppt.controller; 

import com.aspose.slides.*;
import org.springframework.web.bind.annotation.*;
import java.io.*;

@RestController
@RequestMapping("/ppt")
public class PowerPointController {

    private static final String PPTX_FILE_PATH = "Users/sidkonnar/Downloads/MVP for Content Creator (1)"; // Update this path

    @GetMapping("/extract-html/{slideIndex}")
    public String extractHtml(@PathVariable int slideIndex) {
        try {
            // Load the PowerPoint file
            Presentation presentation = new Presentation(PPTX_FILE_PATH);

            // Ensure slide index is valid
            if (slideIndex < 0 || slideIndex >= presentation.getSlides().size()) {
                return "Invalid slide index!";
            }

            // Create a new presentation to store only the selected slide
            Presentation singleSlidePresentation = new Presentation();
            singleSlidePresentation.getSlides().removeAt(0); // Remove default empty slide
            ISlide slide = presentation.getSlides().get_Item(slideIndex);
            singleSlidePresentation.getSlides().addClone(slide);

            // Save the extracted slide as an HTML file
            File tempFile = File.createTempFile("slide_html_", ".html");
            singleSlidePresentation.save(tempFile.getAbsolutePath(), SaveFormat.Html);

            // Read and return the HTML content
            return new String(java.nio.file.Files.readAllBytes(tempFile.toPath()));
        } catch (Exception e) {
            return "Error extracting HTML: " + e.getMessage();
        }
    }
}
