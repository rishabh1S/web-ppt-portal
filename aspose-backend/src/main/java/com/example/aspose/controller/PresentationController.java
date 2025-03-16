package com.example.aspose.controller;

import java.io.IOException;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.example.aspose.model.Presentation;
import com.example.aspose.repository.PresentationRepository;
import com.example.aspose.service.PresentationService;

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
        return presentationRepo.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }
}
