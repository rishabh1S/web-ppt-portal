package com.example.aspose.service;

import com.aspose.slides.Html5Options;
import com.aspose.slides.ISlide;
import com.aspose.slides.License;
import com.aspose.slides.Presentation;
import com.aspose.slides.SaveFormat;
import com.example.aspose.model.Slide;
import com.example.aspose.repository.PresentationRepository;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;

@Service
public class PresentationService {
    @Autowired
    PresentationRepository presentationRepo;
    @Autowired
    FileStorageService fileStorageService;
    @Value("${file.upload-dir}")
    private String uploadDir;

    static {
        try {
            License license = new License();
            license.setLicense("src/main/resources/Aspose.SlidesforJava.lic");
            System.out.println("Aspose.Slides license applied successfully.");
        } catch (Exception e) {
            System.err.println("Failed to load Aspose.Slides license: " + e.getMessage());
        }
    }

    @Transactional
    public com.example.aspose.model.Presentation processPresentation(MultipartFile file) throws IOException {
        // 1. Save the original file
        String filePath = fileStorageService.storeFile(file);

        // 2. Load the PPTX file using Aspose.Slides
        Presentation asposePresentation = new Presentation(file.getInputStream());
        try {
            com.example.aspose.model.Presentation dbPresentation = new com.example.aspose.model.Presentation();
            dbPresentation.setOriginalFilePath(filePath);
            dbPresentation.setName(file.getOriginalFilename());
            dbPresentation.setWidth(asposePresentation.getSlideSize().getSize().getWidth());
            dbPresentation.setHeight(asposePresentation.getSlideSize().getSize().getHeight());

            if (asposePresentation.getSlides().size() == 0) {
                throw new IOException("The PPTX file contains no slides.");
            }

            // Get slide dimensions
            float slideWidth = (float) asposePresentation.getSlideSize().getSize().getWidth();
            float slideHeight = (float) asposePresentation.getSlideSize().getSize().getHeight();

            int slideNumber = 1;
            for (ISlide asposeSlide : asposePresentation.getSlides()) {
                // Generate a unique folder for this presentation
                String presentationFolder = uploadDir + "/" + dbPresentation.getName() + "_slides";
                Files.createDirectories(Paths.get(presentationFolder));

                // Generate a unique HTML file path
                String htmlFileName = "slide_" + slideNumber + ".html";
                String htmlFilePath = Paths.get(presentationFolder, htmlFileName).toString();

                // Create a temporary presentation with just the current slide
                Presentation tempPresentation = new Presentation();
                try {
                    tempPresentation.getSlides().removeAt(0);
                    tempPresentation.getSlides().addClone(asposeSlide);
                    // Save as HTML5
                    Html5Options options = new Html5Options();
                    tempPresentation.save(htmlFilePath, SaveFormat.Html5, options);
                    inlineResources(htmlFilePath, presentationFolder, slideWidth, slideHeight);
                } finally {
                    tempPresentation.dispose();
                }

                // Read the HTML content from the file
                String htmlContent = new String(Files.readAllBytes(Paths.get(htmlFilePath)), StandardCharsets.UTF_8);

                Slide dbSlide = new Slide();
                dbSlide.setSlideNumber(slideNumber++);
                dbSlide.setHtmlContent(htmlContent);
                dbSlide.setPresentation(dbPresentation);
                dbPresentation.getSlides().add(dbSlide);
            }

            return presentationRepo.save(dbPresentation);
        } finally {
            asposePresentation.dispose();
        }
    }

    private void inlineResources(String htmlFilePath, String presentationFolder, float slideWidth, float slideHeight)
            throws IOException {
        Path htmlPath = Paths.get(htmlFilePath);
        String htmlContent = new String(Files.readAllBytes(htmlPath), StandardCharsets.UTF_8);

        Document doc = Jsoup.parse(htmlContent, StandardCharsets.UTF_8.name());

        // 1. Inline CSS files
        for (Element link : doc.select("link[rel=stylesheet]")) {
            String cssHref = link.attr("href");
            Path cssPath = htmlPath.getParent().resolve(cssHref);
            if (Files.exists(cssPath)) {
                String css = new String(Files.readAllBytes(cssPath), StandardCharsets.UTF_8);
                Element style = new Element(Tag.valueOf("style"), "");
                style.text(css);
                link.replaceWith(style);
                Files.deleteIfExists(cssPath);
            }
        }

        // 2. Modify .slide CSS class and styles for tr in all <style> blocks
        for (Element style : doc.select("style")) {
            String modifiedCss = style.html()
                    .replaceAll(
                            "\\.slide\\s*\\{([^}]*)\\}",
                            String.format(
                                    ".slide { width: %.2fpx; height: %.2fpx; overflow: hidden; position: relative; }",
                                    slideWidth, slideHeight))
                    + "\n tr { line-height: 0.5; }";
            style.html(modifiedCss);
        }

        // 3. Remove unwanted inline styles
        for (Element element : doc.select("[style]")) {
            String inlineStyle = element.attr("style");
            inlineStyle = inlineStyle.replaceAll("white-space:\\s*pre-wrap;", "");
            element.attr("style", inlineStyle);
        }

        // 4. Add contenteditable to text-containing elements outside tables
        for (Element element : doc.select("span, p, div")) {
            if (!element.ownText().trim().isEmpty()) {
                element.attr("contenteditable", "true");
            }
        }

        // 5. Handle table cells: Mark innermost elements even if empty
        for (Element td : doc.select("td")) {
            // Find the deepest span/p/div within the <td>
            Elements innermostElements = td.select("span, p, div").select("*:not(:has(span, p, div))");
            for (Element el : innermostElements) {
                el.attr("contenteditable", "true");
            }

            // If no innermost elements exist, mark the <td> itself
            if (innermostElements.isEmpty()) {
                td.attr("contenteditable", "true");
            }
        }

        // 6. Save the modified HTML
        Files.write(htmlPath, doc.outerHtml().getBytes(StandardCharsets.UTF_8));
    }
}