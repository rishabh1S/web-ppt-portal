package com.example.webppt.controller;

import com.example.webppt.model.Slide;
import com.example.webppt.model.SlideElement;
import com.example.webppt.repository.SlideRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/elements")
public class ElementController {


    @Autowired
    private SlideRepository slideRepository;

    @PatchMapping("/{id}")
public ResponseEntity<Slide> updateSlide(@PathVariable UUID id, @RequestBody Slide updatedSlide) {
    return slideRepository.findById(id).map(existingSlide -> {
        existingSlide.setSlideNumber(updatedSlide.getSlideNumber());

        // Remove elements no longer in the updatedSlide
        existingSlide.getElements().removeIf(existingElement -> 
            updatedSlide.getElements().stream().noneMatch(e -> e.getId().equals(existingElement.getId()))
        );

        // Update or add new elements
        for (SlideElement newElement : updatedSlide.getElements()) {
            if (newElement.getId() == null) {
                newElement.setId(UUID.randomUUID()); // Ensure new elements get an ID
                newElement.setSlide(existingSlide);
                existingSlide.getElements().add(newElement);
            } else {
                for (SlideElement existingElement : existingSlide.getElements()) {
                    if (existingElement.getId().equals(newElement.getId())) {
                        // Update properties
                        existingElement.setContent(newElement.getContent());
                        existingElement.setX(newElement.getX());
                        existingElement.setY(newElement.getY());
                        existingElement.setWidth(newElement.getWidth());
                        existingElement.setHeight(newElement.getHeight());
                        existingElement.setStyle(newElement.getStyle());
                    }
                }
            }
        }

        Slide savedSlide = slideRepository.save(existingSlide);
        return ResponseEntity.ok(savedSlide);
    }).orElse(ResponseEntity.notFound().build());
}


}

