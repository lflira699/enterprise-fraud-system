package com.efs.modules.detection.controller;

import com.efs.modules.detection.dto.RelationshipAnalysisRequest;
import com.efs.modules.detection.dto.RelationshipAnalysisResponse;
import com.efs.modules.detection.service.RelationshipAnalysisAccessServiceInterface;
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
@RequestMapping("/api/v1/detection/relationship-analyses")
public class RelationshipAnalysisController {

    private final RelationshipAnalysisAccessServiceInterface
            relationshipAnalysisAccessService;

    private final SecurityContextProvider
            securityContextProvider;

    public RelationshipAnalysisController(
            RelationshipAnalysisAccessServiceInterface relationshipAnalysisAccessService,
            SecurityContextProvider securityContextProvider) {

        this.relationshipAnalysisAccessService =
                relationshipAnalysisAccessService;

        this.securityContextProvider =
                securityContextProvider;
    }

    @PostMapping
    public ResponseEntity<RelationshipAnalysisResponse>
    createRelationshipAnalysis(
            @Valid @RequestBody RelationshipAnalysisRequest request) {

        RelationshipAnalysisResponse response =
                relationshipAnalysisAccessService
                        .createRelationshipAnalysis(
                                request,
                                currentContext()
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
                relationshipAnalysisAccessService
                        .getRelationshipAnalysisById(
                                relationshipAnalysisId,
                                currentContext()
                        )
        );
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<RelationshipAnalysisResponse>>
    getAnalysesByCustomer(
            @PathVariable UUID customerId) {

        return ResponseEntity.ok(
                relationshipAnalysisAccessService
                        .getAnalysesByCustomer(
                                customerId,
                                currentContext()
                        )
        );
    }

    @GetMapping("/transaction/{transactionId}")
    public ResponseEntity<List<RelationshipAnalysisResponse>>
    getAnalysesByTransaction(
            @PathVariable UUID transactionId) {

        return ResponseEntity.ok(
                relationshipAnalysisAccessService
                        .getAnalysesByTransaction(
                                transactionId,
                                currentContext()
                        )
        );
    }

    @GetMapping("/correlation/{correlationId}")
    public ResponseEntity<List<RelationshipAnalysisResponse>>
    getAnalysesByCorrelation(
            @PathVariable UUID correlationId) {

        return ResponseEntity.ok(
                relationshipAnalysisAccessService
                        .getAnalysesByCorrelation(
                                correlationId,
                                currentContext()
                        )
        );
    }

    @GetMapping("/type/{relationshipType}")
    public ResponseEntity<List<RelationshipAnalysisResponse>>
    getAnalysesByType(
            @PathVariable String relationshipType) {

        return ResponseEntity.ok(
                relationshipAnalysisAccessService
                        .getAnalysesByType(
                                relationshipType,
                                currentContext()
                        )
        );
    }

    @GetMapping("/source/{sourceEntityKey}")
    public ResponseEntity<List<RelationshipAnalysisResponse>>
    getAnalysesBySource(
            @PathVariable String sourceEntityKey) {

        return ResponseEntity.ok(
                relationshipAnalysisAccessService
                        .getAnalysesBySource(
                                sourceEntityKey,
                                currentContext()
                        )
        );
    }

    @GetMapping("/target/{targetEntityKey}")
    public ResponseEntity<List<RelationshipAnalysisResponse>>
    getAnalysesByTarget(
            @PathVariable String targetEntityKey) {

        return ResponseEntity.ok(
                relationshipAnalysisAccessService
                        .getAnalysesByTarget(
                                targetEntityKey,
                                currentContext()
                        )
        );
    }

    @GetMapping("/status/{analysisStatus}")
    public ResponseEntity<List<RelationshipAnalysisResponse>>
    getAnalysesByStatus(
            @PathVariable String analysisStatus) {

        return ResponseEntity.ok(
                relationshipAnalysisAccessService
                        .getAnalysesByStatus(
                                analysisStatus,
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