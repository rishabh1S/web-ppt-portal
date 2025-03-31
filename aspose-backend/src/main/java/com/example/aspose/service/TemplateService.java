package com.example.aspose.service;

import com.aspose.slides.*;
import com.example.aspose.model.CommonMetadata;
import com.example.aspose.model.Presenter;
import com.example.aspose.model.SlideContent;
import com.example.aspose.model.TemplateMetadata;
import com.example.aspose.model.TemplateContent;
import com.example.aspose.model.TemplateTextContent;
import com.example.aspose.model.TemplateTableContent;
import com.example.aspose.util.AsposeUtils.AsposeText;
import com.example.aspose.util.AsposeUtils.AsposeTable;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class TemplateService {

        @Value("classpath:/templates/blank.pptx")
        private Resource blankTemplate;
        private final ObjectMapper objectMapper = new ObjectMapper();
        private final String metadataPath = "/metadata/";
        private final String commonMetadataFile = "CommonMetadata.json";
        private final String slideContentFile = "/metadata/SlideContent.json";

        public void createStandardSlide(Presentation pres, String slideMetadataPath) {
                // Remove the default slide
                pres.getSlides().removeAt(0);

                try (InputStream templateStream = blankTemplate.getInputStream()) {
                        // Read common metadata and slide-specific metadata
                        CommonMetadata commonMetadata = readMetadataFromFile(commonMetadataFile,
                                        CommonMetadata.class);
                        TemplateMetadata slideMetadata = readMetadataFromFile(slideMetadataPath,
                                        TemplateMetadata.class);

                        SlideContent slideContent = readSlideContent();

                        Presentation templatePres = new Presentation(templateStream);
                        ISlide templateSlide = templatePres.getSlides().get_Item(0);
                        ISlide slide = pres.getSlides().addClone(templateSlide);

                        int slideWidth = slideMetadata.getWidth();
                        int slideHeight = slideMetadata.getHeight();
                        pres.getSlideSize().setSize(slideWidth, slideHeight, SlideSizeScaleType.EnsureFit);
                        addCommonContent(slide, commonMetadata, slideContent);
                        addSlideContent(slide, slideMetadata, slideContent);
                } catch (IOException e) {
                        throw new RuntimeException("Error loading blank template from resources", e);
                } catch (Exception e) {
                        throw new RuntimeException("Error creating standard slide", e);
                }
        }

        private <T> T readMetadataFromFile(String fileName, Class<T> clazz) {
                try (InputStream input = getClass().getResourceAsStream(metadataPath + fileName)) {
                        if (input == null) {
                                throw new IOException("Metadata file not found: " + fileName);
                        }
                        return objectMapper.readValue(input, clazz);
                } catch (IOException e) {
                        throw new RuntimeException("Error reading metadata file: " + fileName, e);
                }
        }

        private SlideContent readSlideContent() {
                try (InputStream input = getClass().getResourceAsStream(slideContentFile)) {
                        if (input == null) {
                                throw new IOException("Slide content file not found: " + slideContentFile);
                        }
                        return objectMapper.readValue(input, SlideContent.class);
                } catch (IOException e) {
                        throw new RuntimeException("Error reading slide content file", e);
                }
        }

        private void addSlideContent(ISlide slide, TemplateMetadata metadata, SlideContent slideContent)
                        throws Exception {
                Map<String, List<String>> stdSlideContent = slideContent.getStdSlideContent();
                for (TemplateContent contentObj : metadata.getStdTemplateContent()) {
                        switch (contentObj.getObjectType()) {
                                case "text":
                                        AsposeText.addTextShape(slide, (TemplateTextContent) contentObj);
                                        break;
                                case "table":
                                        TemplateTableContent tableContent = (TemplateTableContent) contentObj;
                                        int objectId = tableContent.getObjectId();
                                        String tableId = "table " + objectId;

                                        if (stdSlideContent != null && stdSlideContent.containsKey(tableId)) {
                                                List<String> cellContents = stdSlideContent.get(tableId);

                                                List<List<String>> formattedContent = new ArrayList<>();

                                                int colCount = tableContent.getHeaders().size();

                                                int totalCells = cellContents.size();
                                                int rowCount = (int) Math.ceil((double) totalCells / colCount);

                                                for (int row = 0; row < rowCount; row++) {
                                                        List<String> rowContent = new ArrayList<>();
                                                        for (int col = 0; col < colCount; col++) {
                                                                int cellIndex = row * colCount + col;
                                                                if (cellIndex < totalCells) {
                                                                        rowContent.add(cellContents.get(cellIndex));
                                                                } else {
                                                                        rowContent.add("");

                                                                }
                                                        }
                                                        formattedContent.add(rowContent);
                                                }
                                                tableContent.setContent(formattedContent);
                                        }

                                        AsposeTable.addTableShape(slide, tableContent);
                                        break;
                                default:
                                        throw new IllegalArgumentException(
                                                        "Unknown objectType: " + contentObj.getObjectType());
                        }
                }
        }

        private void addCommonContent(ISlide slide, CommonMetadata commonMetadata, SlideContent slideContent)
                        throws Exception {
                TemplateTextContent title = commonMetadata.getTitle();
                title.setContent(slideContent.getSlideTitle());
                AsposeText.addTextShape(slide, title);

                List<TemplateTextContent> legalEntities = commonMetadata.getLegalEntity();
                List<String> apiLegalEntities = slideContent.getLegalEntity();
                for (int i = 0; i < legalEntities.size(); i++) {
                        if (i < apiLegalEntities.size()) {
                                legalEntities.get(i).setContent(apiLegalEntities.get(i));
                                AsposeText.addTextShape(slide, legalEntities.get(i));
                        }
                }

                TemplateTextContent presenterField = commonMetadata.getPresenters();
                StringBuilder presenterNames = new StringBuilder();
                for (Presenter presenter : slideContent.getPresenter()) {
                        presenterNames.append(presenter.getUserFirstName())
                                        .append(" ")
                                        .append(presenter.getUserLastName())
                                        .append(", ");
                }
                if (presenterNames.length() > 0) {
                        presenterNames.setLength(presenterNames.length() - 2);
                }
                presenterField.setContent(presenterNames.toString());
                AsposeText.addTextShape(slide, presenterField);

                if ("Y".equalsIgnoreCase(slideContent.getIsCSI())) {
                        TemplateTextContent csiContent = commonMetadata.getCsi();
                        csiContent.setContent("Confidential Security Information (CSI)");
                        AsposeText.addTextShape(slide, csiContent);
                }
                TemplateTextContent footerContent = commonMetadata.getFooter();
                footerContent.setContent(slideContent.getFooter());
                AsposeText.addTextShape(slide, footerContent);
        }

}
