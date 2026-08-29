package com.efs.modules.catalog.controller;

import com.efs.modules.catalog.dto.LanguageRequest;
import com.efs.modules.catalog.dto.LanguageResponse;
import com.efs.modules.catalog.service.LanguageServiceInterface;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/languages")
public class LanguageController {

    private final LanguageServiceInterface languageService;

    public LanguageController(
            LanguageServiceInterface languageService) {

        this.languageService =
                languageService;
    }

    @PostMapping
    public ResponseEntity<LanguageResponse> createLanguage(
            @Valid @RequestBody LanguageRequest request) {

        LanguageResponse response =
                languageService.createLanguage(
                        request
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/{languageId}")
    public ResponseEntity<LanguageResponse> getLanguageById(
            @PathVariable UUID languageId) {

        return ResponseEntity.ok(
                languageService.getLanguageById(
                        languageId
                )
        );
    }

    @GetMapping("/code/{languageCode}")
    public ResponseEntity<LanguageResponse> getLanguageByLanguageCode(
            @PathVariable String languageCode) {

        return ResponseEntity.ok(
                languageService.getLanguageByLanguageCode(
                        languageCode
                )
        );
    }

    @GetMapping("/alpha3/{alpha3Code}")
    public ResponseEntity<LanguageResponse> getLanguageByAlpha3Code(
            @PathVariable String alpha3Code) {

        return ResponseEntity.ok(
                languageService.getLanguageByAlpha3Code(
                        alpha3Code
                )
        );
    }

    @GetMapping
    public ResponseEntity<List<LanguageResponse>> getLanguages(
            @RequestParam(required = false) String status) {

        if (status != null) {
            return ResponseEntity.ok(
                    languageService.getLanguagesByStatus(
                            status
                    )
            );
        }

        return ResponseEntity.ok(
                languageService.getAllLanguages()
        );
    }
}