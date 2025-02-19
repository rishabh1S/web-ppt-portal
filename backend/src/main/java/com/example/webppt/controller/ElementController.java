package com.example.webppt.controller;

import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.webppt.model.SlideElement;
import com.example.webppt.repository.SlideElementRepository;

@RestController
@RequestMapping("/api/elements")
public class ElementController {
    @Autowired
    SlideElementRepository elementRepo;

    @PatchMapping("/{id}")
    public ResponseEntity<SlideElement> updateText(
            @PathVariable UUID id,
            @RequestBody Map<String, String> update) {
        return elementRepo.findById(id)
                .map(element -> {
                    element.setContent(update.get("content"));
                    return ResponseEntity.ok(elementRepo.save(element));
                })
                .orElse(ResponseEntity.notFound().build());
    }
}