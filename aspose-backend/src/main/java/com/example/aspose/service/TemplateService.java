package com.example.aspose.service;

import com.aspose.slides.*;
import com.example.aspose.model.CommonMetadata;
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
import java.util.List;

@Service
public class TemplateService {

        @Value("classpath:/templates/blank.pptx")
        private Resource blankTemplate;
        private final ObjectMapper objectMapper = new ObjectMapper();
        private final String metadataPath = "/metadata/";
        private final String commonMetadataFile = "CommonMetadata.json";

        public void createStandardSlide(Presentation pres, String slideMetadataPath) {
                // Remove the default slide
                pres.getSlides().removeAt(0);

                try (InputStream templateStream = blankTemplate.getInputStream()) {
                        // Read common metadata and slide-specific metadata
                        CommonMetadata commonMetadata = readMetadataFromFile(commonMetadataFile,
                                        CommonMetadata.class);
                        TemplateMetadata slideMetadata = readMetadataFromFile(slideMetadataPath,
                                        TemplateMetadata.class);

                        Presentation templatePres = new Presentation(templateStream);
                        ISlide templateSlide = templatePres.getSlides().get_Item(0);
                        ISlide slide = pres.getSlides().addClone(templateSlide);

                        int slideWidth = slideMetadata.getWidth();
                        int slideHeight = slideMetadata.getHeight();
                        pres.getSlideSize().setSize(slideWidth, slideHeight, SlideSizeScaleType.EnsureFit);
                        addCommonContent(slide, commonMetadata);
                        addSlideContent(slide, slideMetadata);
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

        private void addSlideContent(ISlide slide, TemplateMetadata metadata) throws Exception {
                for (TemplateContent contentObj : metadata.getStdTemplateContent()) {
                        switch (contentObj.getObjectType()) {
                                case "text":
                                        AsposeText.addTextShape(slide, (TemplateTextContent) contentObj);
                                        break;
                                case "table":
                                        AsposeTable.addTableShape(slide, (TemplateTableContent) contentObj);
                                        break;
                                default:
                                        throw new IllegalArgumentException(
                                                        "Unknown objectType: " + contentObj.getObjectType());
                        }
                }
        }

        private void addCommonContent(ISlide slide, CommonMetadata commonMetadata) throws Exception {
                AsposeText.addTextShape(slide, commonMetadata.getTitle());
                AsposeText.addTextShape(slide, commonMetadata.getCsi());
                AsposeText.addTextShape(slide, commonMetadata.getFooter());

                List<TemplateTextContent> legalEntities = commonMetadata.getLegalEntity();
                for (TemplateTextContent legalEntity : legalEntities) {
                        AsposeText.addTextShape(slide, legalEntity);
                }
                AsposeText.addTextShape(slide, commonMetadata.getPresenters());
        }
}
