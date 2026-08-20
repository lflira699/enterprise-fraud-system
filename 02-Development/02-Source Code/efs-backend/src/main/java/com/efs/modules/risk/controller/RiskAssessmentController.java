package com.efs.modules.risk.controller;

import com.efs.modules.risk.dto.RiskAssessmentRequest;
import com.efs.modules.risk.dto.RiskAssessmentResponse;
import com.efs.modules.risk.service.RiskAssessmentServiceInterface;
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
@RequestMapping("/api/v1/risk-assessments")
public class RiskAssessmentController {

    private final RiskAssessmentServiceInterface riskAssessmentService;

    public RiskAssessmentController(
            RiskAssessmentServiceInterface riskAssessmentService) {

        this.riskAssessmentService = riskAssessmentService;
    }

    @PostMapping
    public ResponseEntity<RiskAssessmentResponse> createRiskAssessment(
            @Valid @RequestBody RiskAssessmentRequest request) {

        RiskAssessmentResponse response =
                riskAssessmentService.createRiskAssessment(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/{riskAssessmentId}")
    public ResponseEntity<RiskAssessmentResponse> getRiskAssessmentById(
            @PathVariable UUID riskAssessmentId) {

        return ResponseEntity.ok(
                riskAssessmentService.getRiskAssessmentById(
                        riskAssessmentId
                )
        );
    }

    @GetMapping("/transaction/{transactionId}")
    public ResponseEntity<List<RiskAssessmentResponse>>
    getAssessmentsByTransaction(
            @PathVariable UUID transactionId) {

        return ResponseEntity.ok(
                riskAssessmentService.getAssessmentsByTransaction(
                        transactionId
                )
        );
    }

    @GetMapping("/transaction/{transactionId}/latest")
    public ResponseEntity<RiskAssessmentResponse>
    getLatestAssessmentByTransaction(
            @PathVariable UUID transactionId) {

        return ResponseEntity.ok(
                riskAssessmentService.getLatestAssessmentByTransaction(
                        transactionId
                )
        );
    }

    @GetMapping("/transaction/{transactionId}/type/{assessmentType}")
    public ResponseEntity<List<RiskAssessmentResponse>>
    getAssessmentsByTransactionAndType(
            @PathVariable UUID transactionId,
            @PathVariable String assessmentType) {

        return ResponseEntity.ok(
                riskAssessmentService.getAssessmentsByTransactionAndType(
                        transactionId,
                        assessmentType
                )
        );
    }

    @GetMapping
    public ResponseEntity<List<RiskAssessmentResponse>> searchAssessments(
            @RequestParam(required = false) String riskLevel,
            @RequestParam(required = false) String assessmentResult) {

        if (riskLevel != null) {
            return ResponseEntity.ok(
                    riskAssessmentService.getAssessmentsByRiskLevel(
                            riskLevel
                    )
            );
        }

        if (assessmentResult != null) {
            return ResponseEntity.ok(
                    riskAssessmentService.getAssessmentsByResult(
                            assessmentResult
                    )
            );
        }

        return ResponseEntity.ok(List.of());
    }
}