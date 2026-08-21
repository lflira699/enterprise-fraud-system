package com.efs.modules.decision.controller;

import com.efs.modules.decision.dto.DecisionEvaluationRequest;
import com.efs.modules.decision.dto.DecisionEvaluationResponse;
import com.efs.modules.decision.service.DecisionEvaluationServiceInterface;
import com.efs.modules.decision.service.DecisionExecutionServiceInterface;
import com.efs.modules.transaction.dto.TransactionDecisionResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/decisions")
public class DecisionController {

    private final DecisionEvaluationServiceInterface
            decisionEvaluationService;

    private final DecisionExecutionServiceInterface
            decisionExecutionService;

    public DecisionController(
            DecisionEvaluationServiceInterface decisionEvaluationService,
            DecisionExecutionServiceInterface decisionExecutionService) {

        this.decisionEvaluationService =
                decisionEvaluationService;

        this.decisionExecutionService =
                decisionExecutionService;
    }

    @PostMapping("/evaluate")
    public ResponseEntity<DecisionEvaluationResponse>
    evaluateDecision(
            @Valid @RequestBody DecisionEvaluationRequest request) {

        DecisionEvaluationResponse response =
                decisionEvaluationService.evaluateDecision(
                        request
                );

        return ResponseEntity.ok(
                response
        );
    }

    @PostMapping("/execute")
    public ResponseEntity<TransactionDecisionResponse>
    evaluateAndPersistDecision(
            @Valid @RequestBody DecisionEvaluationRequest request) {

        TransactionDecisionResponse response =
                decisionExecutionService.evaluateAndPersistDecision(
                        request
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }
}