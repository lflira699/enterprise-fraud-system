package com.efs.modules.detection.controller;

import com.efs.modules.detection.dto.CorrelationRequest;
import com.efs.modules.detection.dto.CorrelationResponse;
import com.efs.modules.detection.service.CorrelationServiceInterface;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/detection/correlations")
public class CorrelationController {

    private final CorrelationServiceInterface correlationService;

    public CorrelationController(
            CorrelationServiceInterface correlationService) {

        this.correlationService = correlationService;
    }

    @PostMapping
    public ResponseEntity<CorrelationResponse> createCorrelation(
            @Valid @RequestBody CorrelationRequest request) {

        CorrelationResponse response =
                correlationService.createCorrelation(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/{correlationId}")
    public ResponseEntity<CorrelationResponse> getCorrelationById(
            @PathVariable UUID correlationId) {

        return ResponseEntity.ok(
                correlationService.getCorrelationById(
                        correlationId
                )
        );
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<CorrelationResponse>>
    getCorrelationsByCustomer(
            @PathVariable UUID customerId) {

        return ResponseEntity.ok(
                correlationService.getCorrelationsByCustomer(
                        customerId
                )
        );
    }

    @GetMapping("/transaction/{transactionId}")
    public ResponseEntity<List<CorrelationResponse>>
    getCorrelationsByTransaction(
            @PathVariable UUID transactionId) {

        return ResponseEntity.ok(
                correlationService.getCorrelationsByTransaction(
                        transactionId
                )
        );
    }

    @GetMapping("/key/{correlationKey}")
    public ResponseEntity<List<CorrelationResponse>>
    getCorrelationsByKey(
            @PathVariable String correlationKey) {

        return ResponseEntity.ok(
                correlationService.getCorrelationsByKey(
                        correlationKey
                )
        );
    }

    @GetMapping("/type/{correlationType}")
    public ResponseEntity<List<CorrelationResponse>>
    getCorrelationsByType(
            @PathVariable String correlationType) {

        return ResponseEntity.ok(
                correlationService.getCorrelationsByType(
                        correlationType
                )
        );
    }

    @GetMapping("/status/{correlationStatus}")
    public ResponseEntity<List<CorrelationResponse>>
    getCorrelationsByStatus(
            @PathVariable String correlationStatus) {

        return ResponseEntity.ok(
                correlationService.getCorrelationsByStatus(
                        correlationStatus
                )
        );
    }
}