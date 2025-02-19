package com.example.webppt.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.example.webppt.services.PptService;
import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/ppt")
public class PptController {
   @Autowired
   private PptService pptService;  // Injecting the PPT service
   // Endpoint to upload a PPTX file and extract its data
   @PostMapping("/upload")
   public ResponseEntity<Map<String, Object>> uploadPpt(@RequestParam("file") MultipartFile file) {
       // Check if the file is empty or not a PPTX file
       if (file.isEmpty() || !file.getOriginalFilename().endsWith(".pptx")) {
           return new ResponseEntity<>(Map.of("error", "Invalid file format"), HttpStatus.BAD_REQUEST);
       }
       try {
           // Call the service to extract data from the PPTX file
           Map<String, Object> extractedData = pptService.extractPptData(file);
           return new ResponseEntity<>(extractedData, HttpStatus.OK);
       } catch (IOException e) {
           // Return an error response if something goes wrong during extraction
           return new ResponseEntity<>(Map.of("error", e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
       }
   }
}