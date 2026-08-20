package com.efs.modules.detection.controller;

import com.efs.modules.detection.dto.RelationshipAnalysisRequest;
import com.efs.modules.detection.dto.RelationshipAnalysisResponse;
import com.efs.modules.detection.service.RelationshipAnalysisServiceInterface;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/detection/relationship-analyses")
public class RelationshipAnalysisController {

    private final RelationshipAnalysisServiceInterface relationshipAnalysisService;

    public RelationshipAnalysisController(
            RelationshipAnalysisServiceInterface relationshipAnalysisService) {

        this.relationshipAnalysisService =
                relationshipAnalysisService;
    }

    @PostMapping
    public ResponseEntity<RelationshipAnalysisResponse>
    createRelationshipAnalysis(
            @Valid @RequestBody RelationshipAnalysisRequest request) {

        RelationshipAnalysisResponse response =
                relationshipAnalysisService.createRelationshipAnalysis(
                        request
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/{relationshipAnalysisId}")
    public ResponseEntity<RelationshipAnalysisResponse>
    getRelationshipAnalysisById(
            @PathVariable UUID relationshipAnalysisId) {

        return ResponseEntity.ok(
                relationshipAnalysisService
                        .getRelationshipAnalysisById(
                                relationshipAnalysisId
                        )
        );
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<RelationshipAnalysisResponse>>
    getAnalysesByCustomer(
            @PathVariable UUID customerId) {

        return ResponseEntity.ok(
                relationshipAnalysisService
                        .getAnalysesByCustomer(customerId)
        );
    }

    @GetMapping("/transaction/{transactionId}")
    public ResponseEntity<List<RelationshipAnalysisResponse>>
    getAnalysesByTransaction(
            @PathVariable UUID transactionId) {

        return ResponseEntity.ok(
                relationshipAnalysisService
                        .getAnalysesByTransaction(transactionId)
        );
    }

    @GetMapping("/correlation/{correlationId}")
    public ResponseEntity<List<RelationshipAnalysisResponse>>
    getAnalysesByCorrelation(
            @PathVariable UUID correlationId) {

        return ResponseEntity.ok(
                relationshipAnalysisService
                        .getAnalysesByCorrelation(correlationId)
        );
    }

    @GetMapping("/type/{relationshipType}")
    public ResponseEntity<List<RelationshipAnalysisResponse>>
    getAnalysesByType(
            @PathVariable String relationshipType) {

        return ResponseEntity.ok(
                relationshipAnalysisService
                        .getAnalysesByType(relationshipType)
        );
    }

    @GetMapping("/source/{sourceEntityKey}")
    public ResponseEntity<List<RelationshipAnalysisResponse>>
    getAnalysesBySource(
            @PathVariable String sourceEntityKey) {

        return ResponseEntity.ok(
                relationshipAnalysisService
                        .getAnalysesBySource(sourceEntityKey)
        );
    }

    @GetMapping("/target/{targetEntityKey}")
    public ResponseEntity<List<RelationshipAnalysisResponse>>
    getAnalysesByTarget(
            @PathVariable String targetEntityKey) {

        return ResponseEntity.ok(
                relationshipAnalysisService
                        .getAnalysesByTarget(targetEntityKey)
        );
    }

    @GetMapping("/status/{analysisStatus}")
    public ResponseEntity<List<RelationshipAnalysisResponse>>
    getAnalysesByStatus(
            @PathVariable String analysisStatus) {

        return ResponseEntity.ok(
                relationshipAnalysisService
                        .getAnalysesByStatus(analysisStatus)
        );
    }
}