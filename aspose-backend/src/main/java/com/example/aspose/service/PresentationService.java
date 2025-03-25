package com.example.aspose.service;

import com.aspose.slides.IAutoShape;
import com.aspose.slides.ICell;
import com.aspose.slides.IColorFormat;
import com.aspose.slides.IGroupShape;
import com.aspose.slides.IParagraph;
import com.aspose.slides.IPictureFrame;
import com.aspose.slides.IPortion;
import com.aspose.slides.IPortionFormat;
import com.aspose.slides.IRow;
import com.aspose.slides.IShape;
import com.aspose.slides.ISlide;
import com.aspose.slides.ITable;
import com.aspose.slides.License;
import com.aspose.slides.NullableBool;
import com.aspose.slides.Presentation;
import com.example.aspose.model.ElementType;
import com.example.aspose.model.Slide;
import com.example.aspose.model.SlideElement;
import com.example.aspose.repository.PresentationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        // 1. Save original file
        String filePath = fileStorageService.storeFile(file);
        // 2. Set file path and name
        com.example.aspose.model.Presentation presentation = new com.example.aspose.model.Presentation();
        presentation.setName(file.getOriginalFilename());
        presentation.setOriginalFilePath(filePath);

        // 3. Process presentation
        Presentation asposePres = null;
        try {
            asposePres = new Presentation(file.getInputStream());
            // Set presentation dimensions
            presentation.setWidth(asposePres.getSlideSize().getSize().getWidth());
            presentation.setHeight(asposePres.getSlideSize().getSize().getHeight());

            // Process slides
            for (ISlide asposeSlide : asposePres.getSlides()) {
                Slide slideEntity = processSlide(asposeSlide, presentation);
                presentation.getSlides().add(slideEntity);
            }
        } finally {
            if (asposePres != null) {
                asposePres.dispose();
            }
        }

        // 4. Save presentation
        return presentationRepo.save(presentation);
    }

    private Slide processSlide(ISlide asposeSlide, com.example.aspose.model.Presentation presentation) {
        Slide slide = new Slide();
        slide.setSlideNumber(asposeSlide.getSlideNumber());
        slide.setPresentation(presentation);

        // Process shapes recursively (including groups)
        for (IShape shape : asposeSlide.getShapes()) {
            processShapeRecursive(shape, slide);
        }
        return slide;
    }

    private void processShapeRecursive(IShape shape, Slide slide) {
        if (shape instanceof IGroupShape) {
            IGroupShape groupShape = (IGroupShape) shape;
            // Recursively process each child in the group
            for (IShape child : groupShape.getShapes()) {
                processShapeRecursive(child, slide);
            }
        } else {
            SlideElement element = processShape(shape);
            if (element != null) {
                element.setSlide(slide);
                slide.getElements().add(element);
            }
        }
    }

    /**
     * Processes a non-group shape and returns a SlideElement.
     */
    private SlideElement processShape(IShape shape) {
        SlideElement element = new SlideElement();
        element.setX(shape.getX());
        element.setY(shape.getY());
        element.setWidth(shape.getWidth());
        element.setHeight(shape.getHeight());

        Map<String, Object> content = new HashMap<>();
        Map<String, Object> style = new HashMap<>();

        if (shape instanceof IAutoShape) {
            processAutoShape((IAutoShape) shape, element, content, style);
        } else if (shape instanceof ITable) {
            processTable((ITable) shape, element, content, style);
        } else if (shape instanceof IPictureFrame) {
            processImage((IPictureFrame) shape, element, content, style);
        } else {
            // For other shapes, mark as generic shape
            element.setType(ElementType.SHAPE);
            content.put("shapeType", shape.getClass().getSimpleName());
        }

        element.setContent(content);
        element.setStyle(style);
        return element;
    }

    private void processAutoShape(IAutoShape autoShape, SlideElement element,
            Map<String, Object> content, Map<String, Object> style) {
        // Mark as a text box if text exists; otherwise, treat as generic shape.
        if (autoShape.getTextFrame() != null && autoShape.getTextFrame().getText() != null) {
            element.setType(ElementType.TEXT_BOX);
            content.put("text", autoShape.getTextFrame().getText());

            // Extract text styles if available
            if (autoShape.getTextFrame().getParagraphs().getCount() > 0) {
                IParagraph firstParagraph = autoShape.getTextFrame().getParagraphs().get_Item(0);
                if (firstParagraph.getPortions().getCount() > 0) {
                    IPortion firstPortion = firstParagraph.getPortions().get_Item(0);
                    IPortionFormat format = firstPortion.getPortionFormat();

                    style.put("fontSize", format.getFontHeight());
                    style.put("fontColor", colorToString(format.getFillFormat().getSolidFillColor()));
                    style.put("fontFamily", format.getLatinFont().getFontName());
                    style.put("bold", format.getFontBold() == NullableBool.True);
                    style.put("italic", format.getFontItalic() == NullableBool.True);
                }
            }
        } else {
            // If no text is present, simply mark the type as shape.
            element.setType(ElementType.SHAPE);
        }

        // Shape appearance
        style.put("fillColor", colorToString(autoShape.getFillFormat().getSolidFillColor()));
        style.put("borderColor", colorToString(autoShape.getLineFormat().getFillFormat().getSolidFillColor()));
        style.put("borderWidth", autoShape.getLineFormat().getWidth());
    }

    private void processTable(ITable table, SlideElement element,
            Map<String, Object> content, Map<String, Object> style) {
        element.setType(ElementType.TABLE);
        List<Map<String, Object>> rows = new ArrayList<>();

        for (IRow row : table.getRows()) {
            List<Map<String, Object>> cells = new ArrayList<>();
            for (ICell cell : row) {
                Map<String, Object> cellData = new HashMap<>();
                if (cell.getTextFrame() != null) {
                    cellData.put("text", cell.getTextFrame().getText());
                } else {
                    cellData.put("text", "");
                }
                cellData.put("rowSpan", cell.getRowSpan());
                cellData.put("colSpan", cell.getColSpan());
                cells.add(cellData);
            }
            rows.add(Map.of("cells", cells));
        }
        content.put("rows", rows);
    }

    private void processImage(IPictureFrame pictureFrame, SlideElement element,
            Map<String, Object> content, Map<String, Object> style) {
        element.setType(ElementType.IMAGE);
        try {
            byte[] imageData = pictureFrame.getPictureFormat().getPicture().getImage().getBinaryData();
            String imagePath = fileStorageService.storeImage(imageData);
            content.put("url", imagePath);
        } catch (Exception e) {
            content.put("error", "Failed to process image: " + e.getMessage());
        }
    }

    private String colorToString(IColorFormat colorFormat) {
        return String.format("#%02x%02x%02x",
                colorFormat.getR(),
                colorFormat.getG(),
                colorFormat.getB());
    }
}
