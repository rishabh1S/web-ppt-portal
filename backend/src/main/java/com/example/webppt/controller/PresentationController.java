package com.example.webppt.controller;

import java.io.IOException;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.example.webppt.model.Presentation;
import com.example.webppt.repository.PresentationRepository;
import com.example.webppt.service.PresentationService;

@RestController
@RequestMapping("/api/presentations")
public class PresentationController {

    @Autowired
    PresentationService presentationService;

    @Autowired
    PresentationRepository presentationRepo;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Presentation> uploadPresentation(@RequestParam("file") MultipartFile file) {
        try {
            Presentation presentation = presentationService.processPresentation(file);
            return ResponseEntity.status(200).body(presentation);
        } catch (IOException e) {
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Presentation> getPresentation(@PathVariable UUID id) {
        return presentationRepo.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<ByteArrayResource> downloadPresentation(@PathVariable UUID id) {
        try {
            byte[] pptBytes = presentationService.generatePresentation(id);
            ByteArrayResource resource = new ByteArrayResource(pptBytes);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=presentation.pptx")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .contentLength(pptBytes.length)
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }
}