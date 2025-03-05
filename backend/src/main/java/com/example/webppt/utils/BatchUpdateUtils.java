package com.example.webppt.utils;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.example.webppt.exceptions.ResourceNotFoundException;
import com.example.webppt.model.Presentation;
import com.example.webppt.model.Slide;
import com.example.webppt.model.SlideElement;
import com.example.webppt.model.DTO.ElementUpdateDTO;

@Component
public class BatchUpdateUtils {

    public static Slide validateSlideExists(Presentation presentation, UUID slideId) {
        return presentation.getSlides().stream()
                .filter(s -> s.getId().equals(slideId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Slide not found"));
    }

    public static SlideElement validateElementExists(Slide slide, UUID elementId) {
        return slide.getElements().stream()
                .filter(e -> e.getId().equals(elementId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Element not found"));
    }

    public static void applyElementUpdates(SlideElement element, ElementUpdateDTO update) {
        if (update.getType() != null)
            element.setType(update.getType());
        if (update.getContent() != null)
            element.setContent(update.getContent());
        if (update.getX() != null)
            element.setX(update.getX());
        if (update.getY() != null)
            element.setY(update.getY());
        if (update.getWidth() != null)
            element.setWidth(update.getWidth());
        if (update.getHeight() != null)
            element.setHeight(update.getHeight());
        if (update.getStyle() != null)
            element.setStyle(update.getStyle());
    }
}
