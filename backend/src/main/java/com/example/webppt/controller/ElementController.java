package com.example.webppt.controller;

import com.example.webppt.model.Slide;
import com.example.webppt.model.SlideElement;
import com.example.webppt.repository.SlideRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/elements")
public class ElementController {

    @Autowired
    private SlideRepository slideRepository;


    @PatchMapping("/batch-update")
public ResponseEntity<List<Slide>> updateSlides(@RequestBody List<Slide> updatedSlides) {
    List<Slide> savedSlides = new ArrayList<>();
    for (Slide updatedSlide : updatedSlides) {
        slideRepository.findById(updatedSlide.getId()).ifPresent(existingSlide -> {
            // Update slide properties
            existingSlide.setSlideNumber(updatedSlide.getSlideNumber());

            // Remove elements no longer in the updatedSlide
            existingSlide.getElements().removeIf(existingElement -> 
                updatedSlide.getElements().stream().noneMatch(e -> e.getId().equals(existingElement.getId()))
            );

            // Update or add new elements
            for (SlideElement newElement : updatedSlide.getElements()) {
                if (newElement.getId() == null) {
                    newElement.setId(UUID.randomUUID());
                    newElement.setSlide(existingSlide);
                    existingSlide.getElements().add(newElement);
                } else {
                    existingSlide.getElements().stream()
                        .filter(existingElement -> existingElement.getId().equals(newElement.getId()))
                        .findFirst()
                        .ifPresent(existingElement -> {
                            // Update properties
                            existingElement.setContent(newElement.getContent());
                            existingElement.setX(newElement.getX());
                            existingElement.setY(newElement.getY());
                            existingElement.setWidth(newElement.getWidth());
                            existingElement.setHeight(newElement.getHeight());
                            existingElement.setStyle(newElement.getStyle());
                        });
                }
            }

            savedSlides.add(slideRepository.save(existingSlide));
        });
    }
    return ResponseEntity.ok(savedSlides);
}
}
