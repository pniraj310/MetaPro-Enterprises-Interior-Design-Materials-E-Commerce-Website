package com.metapro.controller;

import com.metapro.model.BusinessSettings;
import com.metapro.service.BusinessSettingsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/settings")
public class BusinessSettingsController {

    private final BusinessSettingsService settingsService;

    public BusinessSettingsController(BusinessSettingsService settingsService) {
        this.settingsService = settingsService;
    }

    @GetMapping
    public ResponseEntity<BusinessSettings> getSettings() {
        return ResponseEntity.ok(settingsService.getSettings());
    }

    @PutMapping
    public ResponseEntity<BusinessSettings> updateSettings(@RequestBody BusinessSettings settings) {
        return ResponseEntity.ok(settingsService.updateSettings(settings));
    }
}
