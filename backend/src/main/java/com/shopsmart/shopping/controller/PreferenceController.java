package com.shopsmart.shopping.controller;

import com.shopsmart.shopping.dto.PreferenceDto;
import com.shopsmart.shopping.service.PreferenceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/preferences")
public class PreferenceController {

    private final PreferenceService preferenceService;

    public PreferenceController(PreferenceService preferenceService) {
        this.preferenceService = preferenceService;
    }

    @GetMapping
    public ResponseEntity<PreferenceDto> getPreferences() {
        return ResponseEntity.ok(preferenceService.getPreferences());
    }

    @PutMapping
    public ResponseEntity<PreferenceDto> updatePreferences(@RequestBody PreferenceDto request) {
        return ResponseEntity.ok(preferenceService.updatePreferences(request));
    }
}
