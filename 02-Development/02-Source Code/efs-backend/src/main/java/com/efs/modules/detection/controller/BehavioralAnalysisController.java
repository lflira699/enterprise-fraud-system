package com.efs.modules.detection.controller;

import com.efs.modules.detection.dto.BehavioralAnalysisRequest;
import com.efs.modules.detection.dto.BehavioralAnalysisResponse;
import com.efs.modules.detection.service.BehavioralAnalysisServiceInterface;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/detection/behavioral-analyses")
public class BehavioralAnalysisController {

    private final BehavioralAnalysisServiceInterface behavioralAnalysisService;

    public BehavioralAnalysisController(
            BehavioralAnalysisServiceInterface behavioralAnalysisService) {

        this.behavioralAnalysisService = behavioralAnalysisService;
    }

    @PostMapping
    public ResponseEntity<BehavioralAnalysisResponse>
    createBehavioralAnalysis(
            @Valid @RequestBody BehavioralAnalysisRequest request) {

        BehavioralAnalysisResponse response =
                behavioralAnalysisService.createBehavioralAnalysis(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/{behavioralAnalysisId}")
    public ResponseEntity<BehavioralAnalysisResponse>
    getBehavioralAnalysisById(
            @PathVariable UUID behavioralAnalysisId) {

        return ResponseEntity.ok(
                behavioralAnalysisService.getBehavioralAnalysisById(
                        behavioralAnalysisId
                )
        );
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<BehavioralAnalysisResponse>>
    getAnalysesByCustomer(
            @PathVariable UUID customerId) {

        return ResponseEntity.ok(
                behavioralAnalysisService.getAnalysesByCustomer(
                        customerId
                )
        );
    }

    @GetMapping("/transaction/{transactionId}")
    public ResponseEntity<List<BehavioralAnalysisResponse>>
    getAnalysesByTransaction(
            @PathVariable UUID transactionId) {

        return ResponseEntity.ok(
                behavioralAnalysisService.getAnalysesByTransaction(
                        transactionId
                )
        );
    }

    @GetMapping("/correlation/{correlationId}")
    public ResponseEntity<List<BehavioralAnalysisResponse>>
    getAnalysesByCorrelation(
            @PathVariable UUID correlationId) {

        return ResponseEntity.ok(
                behavioralAnalysisService.getAnalysesByCorrelation(
                        correlationId
                )
        );
    }

    @GetMapping("/status/{analysisStatus}")
    public ResponseEntity<List<BehavioralAnalysisResponse>>
    getAnalysesByStatus(
            @PathVariable String analysisStatus) {

        return ResponseEntity.ok(
                behavioralAnalysisService.getAnalysesByStatus(
                        analysisStatus
                )
        );
    }
}