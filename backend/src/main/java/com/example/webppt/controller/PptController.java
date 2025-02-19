package com.example.webppt.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.example.webppt.model.SlideData;
import com.example.webppt.repository.PptDataRepository;
import com.example.webppt.service.PptService;
import java.io.IOException;

@RestController
@RequestMapping("/api/ppt")
public class PptController {

    @Autowired
    private PptService pptService;

    @Autowired
    private PptDataRepository pptDataRepository;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadPpt(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty() || !file.getOriginalFilename().endsWith(".pptx")) {
            return ResponseEntity.badRequest().body("Invalid file format");
        }
        try {
            pptService.savePptData(file);
            return ResponseEntity.ok("PPT successfully uploaded and saved");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

    // @GetMapping("/{pptId}/slides")
    // public ResponseEntity<List<SlideData>> getSlidesByPptId(@PathVariable Long
    // pptId) {
    // return pptDataRepository.findById(pptId)
    // .map(ppt -> ResponseEntity.ok(ppt.getSlides()))
    // .orElseGet(() -> ResponseEntity.notFound().build());
    // }
}