package com.efs.modules.detection.controller;

import com.efs.modules.detection.dto.ScenarioEvaluationRuleExecutionRequest;
import com.efs.modules.detection.dto.ScenarioEvaluationRuleExecutionResponse;
import com.efs.modules.detection.service.ScenarioEvaluationRuleExecutionServiceInterface;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/detection/scenario-evaluation-rule-executions")
public class ScenarioEvaluationRuleExecutionController {

    private final ScenarioEvaluationRuleExecutionServiceInterface service;

    public ScenarioEvaluationRuleExecutionController(
            ScenarioEvaluationRuleExecutionServiceInterface service) {

        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ScenarioEvaluationRuleExecutionResponse>
    createScenarioEvaluationRuleExecution(
            @Valid
            @RequestBody
            ScenarioEvaluationRuleExecutionRequest request) {

        ScenarioEvaluationRuleExecutionResponse response =
                service.createScenarioEvaluationRuleExecution(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/{evaluationRuleExecutionId}")
    public ResponseEntity<ScenarioEvaluationRuleExecutionResponse>
    getScenarioEvaluationRuleExecutionById(
            @PathVariable UUID evaluationRuleExecutionId) {

        return ResponseEntity.ok(
                service.getScenarioEvaluationRuleExecutionById(
                        evaluationRuleExecutionId
                )
        );
    }

    @GetMapping("/evaluation/{evaluationId}")
    public ResponseEntity<List<ScenarioEvaluationRuleExecutionResponse>>
    getRuleExecutionsByEvaluation(
            @PathVariable UUID evaluationId) {

        return ResponseEntity.ok(
                service.getRuleExecutionsByEvaluation(
                        evaluationId
                )
        );
    }

    @GetMapping("/execution/{executionId}")
    public ResponseEntity<List<ScenarioEvaluationRuleExecutionResponse>>
    getEvaluationsByRuleExecution(
            @PathVariable UUID executionId) {

        return ResponseEntity.ok(
                service.getEvaluationsByRuleExecution(
                        executionId
                )
        );
    }
}