package com.efs.modules.risk.controller;

import com.efs.modules.risk.dto.RiskAssessmentRequest;
import com.efs.modules.risk.dto.RiskAssessmentResponse;
import com.efs.modules.risk.service.RiskAssessmentServiceInterface;
import com.efs.shared.pagination.PageResponse;
import com.efs.shared.security.SecurityContext;
import com.efs.shared.security.SecurityContextProvider;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
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
@RequestMapping("/api/v1/risk-assessments")
public class RiskAssessmentController {

    private final RiskAssessmentServiceInterface
            riskAssessmentService;

    private final SecurityContextProvider
            securityContextProvider;

    public RiskAssessmentController(
            RiskAssessmentServiceInterface riskAssessmentService,
            SecurityContextProvider securityContextProvider) {

        this.riskAssessmentService =
                riskAssessmentService;

        this.securityContextProvider =
                securityContextProvider;
    }

    @PostMapping
    public ResponseEntity<RiskAssessmentResponse>
    createRiskAssessment(
            @Valid @RequestBody RiskAssessmentRequest request) {

        RiskAssessmentResponse response =
                riskAssessmentService
                        .createRiskAssessment(
                                request
                        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/{riskAssessmentId}")
    public ResponseEntity<RiskAssessmentResponse>
    getRiskAssessmentById(
            @PathVariable UUID riskAssessmentId) {

        SecurityContext securityContext =
                securityContextProvider
                        .getCurrentContext();

        try {

            return ResponseEntity.ok(
                    riskAssessmentService
                            .getRiskAssessmentById(
                                    riskAssessmentId,
                                    securityContext
                            )
            );
        }
        catch (AccessDeniedException exception) {

            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .build();
        }
    }

    @GetMapping("/transaction/{transactionId}")
    public ResponseEntity<List<RiskAssessmentResponse>>
    getAssessmentsByTransaction(
            @PathVariable UUID transactionId) {

        SecurityContext securityContext =
                securityContextProvider
                        .getCurrentContext();

        try {

            return ResponseEntity.ok(
                    riskAssessmentService
                            .getAssessmentsByTransaction(
                                    transactionId,
                                    securityContext
                            )
            );
        }
        catch (AccessDeniedException exception) {

            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .build();
        }
    }

    @GetMapping("/transaction/{transactionId}/latest")
    public ResponseEntity<RiskAssessmentResponse>
    getLatestAssessmentByTransaction(
            @PathVariable UUID transactionId) {

        SecurityContext securityContext =
                securityContextProvider
                        .getCurrentContext();

        try {

            return ResponseEntity.ok(
                    riskAssessmentService
                            .getLatestAssessmentByTransaction(
                                    transactionId,
                                    securityContext
                            )
            );
        }
        catch (AccessDeniedException exception) {

            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .build();
        }
    }

    @GetMapping("/transaction/{transactionId}/type/{assessmentType}")
    public ResponseEntity<List<RiskAssessmentResponse>>
    getAssessmentsByTransactionAndType(
            @PathVariable UUID transactionId,
            @PathVariable String assessmentType) {

        SecurityContext securityContext =
                securityContextProvider
                        .getCurrentContext();

        try {

            return ResponseEntity.ok(
                    riskAssessmentService
                            .getAssessmentsByTransactionAndType(
                                    transactionId,
                                    assessmentType,
                                    securityContext
                            )
            );
        }
        catch (AccessDeniedException exception) {

            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .build();
        }
    }

    @GetMapping
    public ResponseEntity<PageResponse<RiskAssessmentResponse>>
    searchAssessments(
            @RequestParam(required = false) String riskLevel,
            @RequestParam(required = false) String assessmentResult,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "25") int size,
            @RequestParam(defaultValue = "assessmentTimestamp") String sort,
            @RequestParam(defaultValue = "DESC") String direction) {

        SecurityContext securityContext =
                securityContextProvider
                        .getCurrentContext();

        try {

            return ResponseEntity.ok(
                    riskAssessmentService
                            .searchAssessments(
                                    riskLevel,
                                    assessmentResult,
                                    page,
                                    size,
                                    sort,
                                    direction,
                                    securityContext
                            )
            );
        }
        catch (AccessDeniedException exception) {

            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .build();
        }
    }
}