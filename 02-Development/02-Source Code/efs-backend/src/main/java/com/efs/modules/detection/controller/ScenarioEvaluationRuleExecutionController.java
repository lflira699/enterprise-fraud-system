package com.efs.modules.detection.controller;

import com.efs.modules.detection.dto.ScenarioEvaluationRuleExecutionRequest;
import com.efs.modules.detection.dto.ScenarioEvaluationRuleExecutionResponse;
import com.efs.modules.detection.service.ScenarioEvaluationAccessServiceInterface;
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
@RequestMapping("/api/v1/detection/scenario-evaluation-rule-executions")
public class ScenarioEvaluationRuleExecutionController {

    private final ScenarioEvaluationAccessServiceInterface
            scenarioEvaluationAccessService;

    private final SecurityContextProvider
            securityContextProvider;

    public ScenarioEvaluationRuleExecutionController(
            ScenarioEvaluationAccessServiceInterface
                    scenarioEvaluationAccessService,
            SecurityContextProvider securityContextProvider) {

        this.scenarioEvaluationAccessService =
                scenarioEvaluationAccessService;

        this.securityContextProvider =
                securityContextProvider;
    }

    @PostMapping
    public ResponseEntity<ScenarioEvaluationRuleExecutionResponse>
    createScenarioEvaluationRuleExecution(
            @Valid
            @RequestBody
            ScenarioEvaluationRuleExecutionRequest request) {

        ScenarioEvaluationRuleExecutionResponse response =
                scenarioEvaluationAccessService
                        .createScenarioEvaluationRuleExecution(
                                request,
                                currentContext()
                        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/{evaluationRuleExecutionId}")
    public ResponseEntity<ScenarioEvaluationRuleExecutionResponse>
    getScenarioEvaluationRuleExecutionById(
            @PathVariable UUID evaluationRuleExecutionId) {

        return ResponseEntity.ok(
                scenarioEvaluationAccessService
                        .getScenarioEvaluationRuleExecutionById(
                                evaluationRuleExecutionId,
                                currentContext()
                        )
        );
    }

    @GetMapping("/evaluation/{evaluationId}")
    public ResponseEntity<List<ScenarioEvaluationRuleExecutionResponse>>
    getRuleExecutionsByEvaluation(
            @PathVariable UUID evaluationId) {

        return ResponseEntity.ok(
                scenarioEvaluationAccessService
                        .getRuleExecutionsByEvaluation(
                                evaluationId,
                                currentContext()
                        )
        );
    }

    @GetMapping("/execution/{executionId}")
    public ResponseEntity<List<ScenarioEvaluationRuleExecutionResponse>>
    getEvaluationsByRuleExecution(
            @PathVariable UUID executionId) {

        return ResponseEntity.ok(
                scenarioEvaluationAccessService
                        .getEvaluationsByRuleExecution(
                                executionId,
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