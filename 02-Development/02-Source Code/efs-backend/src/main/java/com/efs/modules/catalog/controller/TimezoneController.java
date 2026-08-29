package com.efs.modules.catalog.controller;

import com.efs.modules.catalog.dto.TimezoneRequest;
import com.efs.modules.catalog.dto.TimezoneResponse;
import com.efs.modules.catalog.service.TimezoneServiceInterface;
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
@RequestMapping("/api/v1/timezones")
public class TimezoneController {

    private final TimezoneServiceInterface timezoneService;

    public TimezoneController(
            TimezoneServiceInterface timezoneService) {

        this.timezoneService =
                timezoneService;
    }

    @PostMapping
    public ResponseEntity<TimezoneResponse> createTimezone(
            @Valid @RequestBody TimezoneRequest request) {

        TimezoneResponse response =
                timezoneService.createTimezone(
                        request
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/{timezoneId}")
    public ResponseEntity<TimezoneResponse> getTimezoneById(
            @PathVariable UUID timezoneId) {

        return ResponseEntity.ok(
                timezoneService.getTimezoneById(
                        timezoneId
                )
        );
    }

    @GetMapping("/code/{timezoneCode}")
    public ResponseEntity<TimezoneResponse> getTimezoneByTimezoneCode(
            @PathVariable String timezoneCode) {

        return ResponseEntity.ok(
                timezoneService.getTimezoneByTimezoneCode(
                        timezoneCode
                )
        );
    }

    @GetMapping
    public ResponseEntity<List<TimezoneResponse>> getTimezones(
            @RequestParam(required = false) String status) {

        if (status != null) {
            return ResponseEntity.ok(
                    timezoneService.getTimezonesByStatus(
                            status
                    )
            );
        }

        return ResponseEntity.ok(
                timezoneService.getAllTimezones()
        );
    }
}