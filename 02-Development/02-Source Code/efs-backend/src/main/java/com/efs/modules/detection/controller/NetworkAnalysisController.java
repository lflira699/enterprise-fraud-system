package com.efs.modules.detection.controller;

import com.efs.modules.detection.dto.NetworkAnalysisRequest;
import com.efs.modules.detection.dto.NetworkAnalysisResponse;
import com.efs.modules.detection.service.NetworkAnalysisServiceInterface;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/detection/network-analyses")
public class NetworkAnalysisController {

    private final NetworkAnalysisServiceInterface networkAnalysisService;

    public NetworkAnalysisController(
            NetworkAnalysisServiceInterface networkAnalysisService) {

        this.networkAnalysisService = networkAnalysisService;
    }

    @PostMapping
    public ResponseEntity<NetworkAnalysisResponse>
    createNetworkAnalysis(
            @Valid @RequestBody NetworkAnalysisRequest request) {

        NetworkAnalysisResponse response =
                networkAnalysisService.createNetworkAnalysis(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/{networkAnalysisId}")
    public ResponseEntity<NetworkAnalysisResponse>
    getNetworkAnalysisById(
            @PathVariable UUID networkAnalysisId) {

        return ResponseEntity.ok(
                networkAnalysisService.getNetworkAnalysisById(
                        networkAnalysisId
                )
        );
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<NetworkAnalysisResponse>>
    getAnalysesByCustomer(
            @PathVariable UUID customerId) {

        return ResponseEntity.ok(
                networkAnalysisService.getAnalysesByCustomer(
                        customerId
                )
        );
    }

    @GetMapping("/transaction/{transactionId}")
    public ResponseEntity<List<NetworkAnalysisResponse>>
    getAnalysesByTransaction(
            @PathVariable UUID transactionId) {

        return ResponseEntity.ok(
                networkAnalysisService.getAnalysesByTransaction(
                        transactionId
                )
        );
    }

    @GetMapping("/correlation/{correlationId}")
    public ResponseEntity<List<NetworkAnalysisResponse>>
    getAnalysesByCorrelation(
            @PathVariable UUID correlationId) {

        return ResponseEntity.ok(
                networkAnalysisService.getAnalysesByCorrelation(
                        correlationId
                )
        );
    }

    @GetMapping("/type/{networkType}")
    public ResponseEntity<List<NetworkAnalysisResponse>>
    getAnalysesByType(
            @PathVariable String networkType) {

        return ResponseEntity.ok(
                networkAnalysisService.getAnalysesByType(
                        networkType
                )
        );
    }

    @GetMapping("/status/{analysisStatus}")
    public ResponseEntity<List<NetworkAnalysisResponse>>
    getAnalysesByStatus(
            @PathVariable String analysisStatus) {

        return ResponseEntity.ok(
                networkAnalysisService.getAnalysesByStatus(
                        analysisStatus
                )
        );
    }

    @GetMapping("/key/{networkKey}")
    public ResponseEntity<List<NetworkAnalysisResponse>>
    getAnalysesByKey(
            @PathVariable String networkKey) {

        return ResponseEntity.ok(
                networkAnalysisService.getAnalysesByKey(
                        networkKey
                )
        );
    }
}