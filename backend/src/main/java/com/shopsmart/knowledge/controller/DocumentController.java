package com.shopsmart.knowledge.controller;

import com.shopsmart.knowledge.dto.DocumentUploadResponse;
import com.shopsmart.knowledge.service.DocumentIngestionService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/documents")
public class DocumentController {

    private final DocumentIngestionService ingestionService;

    public DocumentController(DocumentIngestionService ingestionService) {
        this.ingestionService = ingestionService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<DocumentUploadResponse> uploadDocument(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "version", required = false, defaultValue = "1.0") String version,
            @RequestParam(value = "effectiveDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate effectiveDate,
            @RequestParam(value = "category", required = false, defaultValue = "General") String category,
            @RequestParam(value = "region", required = false, defaultValue = "GLOBAL") String region) {

        DocumentUploadResponse response = ingestionService.ingestDocument(file, version, effectiveDate, category, region);
        return ResponseEntity.ok(response);
    }
}
