package com.efs.modules.detection.controller;

import com.efs.modules.detection.dto.NetworkAnalysisRequest;
import com.efs.modules.detection.dto.NetworkAnalysisResponse;
import com.efs.modules.detection.service.NetworkAnalysisAccessServiceInterface;
import com.efs.shared.security.SecurityContext;
import com.efs.shared.security.SecurityContextProvider;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/detection/network-analyses")
public class NetworkAnalysisController {

    private final NetworkAnalysisAccessServiceInterface
            networkAnalysisAccessService;

    private final SecurityContextProvider
            securityContextProvider;

    public NetworkAnalysisController(
            NetworkAnalysisAccessServiceInterface networkAnalysisAccessService,
            SecurityContextProvider securityContextProvider) {

        this.networkAnalysisAccessService =
                networkAnalysisAccessService;

        this.securityContextProvider =
                securityContextProvider;
    }

    @PostMapping
    public ResponseEntity<NetworkAnalysisResponse>
    createNetworkAnalysis(
            @Valid @RequestBody NetworkAnalysisRequest request) {

        NetworkAnalysisResponse response =
                networkAnalysisAccessService
                        .createNetworkAnalysis(
                                request,
                                currentContext()
                        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/{networkAnalysisId}")
    public ResponseEntity<NetworkAnalysisResponse>
    getNetworkAnalysisById(
            @PathVariable UUID networkAnalysisId) {

        return ResponseEntity.ok(
                networkAnalysisAccessService
                        .getNetworkAnalysisById(
                                networkAnalysisId,
                                currentContext()
                        )
        );
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<NetworkAnalysisResponse>>
    getAnalysesByCustomer(
            @PathVariable UUID customerId) {

        return ResponseEntity.ok(
                networkAnalysisAccessService
                        .getAnalysesByCustomer(
                                customerId,
                                currentContext()
                        )
        );
    }

    @GetMapping("/transaction/{transactionId}")
    public ResponseEntity<List<NetworkAnalysisResponse>>
    getAnalysesByTransaction(
            @PathVariable UUID transactionId) {

        return ResponseEntity.ok(
                networkAnalysisAccessService
                        .getAnalysesByTransaction(
                                transactionId,
                                currentContext()
                        )
        );
    }

    @GetMapping("/correlation/{correlationId}")
    public ResponseEntity<List<NetworkAnalysisResponse>>
    getAnalysesByCorrelation(
            @PathVariable UUID correlationId) {

        return ResponseEntity.ok(
                networkAnalysisAccessService
                        .getAnalysesByCorrelation(
                                correlationId,
                                currentContext()
                        )
        );
    }

    @GetMapping("/type/{networkType}")
    public ResponseEntity<List<NetworkAnalysisResponse>>
    getAnalysesByType(
            @PathVariable String networkType) {

        return ResponseEntity.ok(
                networkAnalysisAccessService
                        .getAnalysesByType(
                                networkType,
                                currentContext()
                        )
        );
    }

    @GetMapping("/status/{analysisStatus}")
    public ResponseEntity<List<NetworkAnalysisResponse>>
    getAnalysesByStatus(
            @PathVariable String analysisStatus) {

        return ResponseEntity.ok(
                networkAnalysisAccessService
                        .getAnalysesByStatus(
                                analysisStatus,
                                currentContext()
                        )
        );
    }

    @GetMapping("/key/{networkKey}")
    public ResponseEntity<List<NetworkAnalysisResponse>>
    getAnalysesByKey(
            @PathVariable String networkKey) {

        return ResponseEntity.ok(
                networkAnalysisAccessService
                        .getAnalysesByKey(
                                networkKey,
                                currentContext()
                        )
        );
    }

    private SecurityContext currentContext() {

        return securityContextProvider
                .getCurrentContext();
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Void> handleAccessDenied(
            AccessDeniedException exception) {

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .build();
    }
}