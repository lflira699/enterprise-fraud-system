package com.efs.modules.detection.controller;

import com.efs.modules.detection.dto.ScenarioEvaluationRequest;
import com.efs.modules.detection.dto.ScenarioEvaluationResponse;
import com.efs.modules.detection.service.ScenarioEvaluationServiceInterface;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/detection/scenario-evaluations")
public class ScenarioEvaluationController {

    private final ScenarioEvaluationServiceInterface scenarioEvaluationService;

    public ScenarioEvaluationController(
            ScenarioEvaluationServiceInterface scenarioEvaluationService) {

        this.scenarioEvaluationService = scenarioEvaluationService;
    }

    @PostMapping
    public ResponseEntity<ScenarioEvaluationResponse> createScenarioEvaluation(
            @Valid @RequestBody ScenarioEvaluationRequest request) {

        ScenarioEvaluationResponse response =
                scenarioEvaluationService.createScenarioEvaluation(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/{evaluationId}")
    public ResponseEntity<ScenarioEvaluationResponse>
    getScenarioEvaluationById(
            @PathVariable UUID evaluationId) {

        return ResponseEntity.ok(
                scenarioEvaluationService
                        .getScenarioEvaluationById(evaluationId)
        );
    }

    @GetMapping("/scenario/{scenarioId}")
    public ResponseEntity<List<ScenarioEvaluationResponse>>
    getEvaluationsByScenario(
            @PathVariable UUID scenarioId) {

        return ResponseEntity.ok(
                scenarioEvaluationService
                        .getEvaluationsByScenario(scenarioId)
        );
    }

    @GetMapping("/scenario-version/{scenarioVersionId}")
    public ResponseEntity<List<ScenarioEvaluationResponse>>
    getEvaluationsByScenarioVersion(
            @PathVariable UUID scenarioVersionId) {

        return ResponseEntity.ok(
                scenarioEvaluationService
                        .getEvaluationsByScenarioVersion(
                                scenarioVersionId
                        )
        );
    }

    @GetMapping("/transaction/{transactionId}")
    public ResponseEntity<List<ScenarioEvaluationResponse>>
    getEvaluationsByTransaction(
            @PathVariable UUID transactionId) {

        return ResponseEntity.ok(
                scenarioEvaluationService
                        .getEvaluationsByTransaction(transactionId)
        );
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<ScenarioEvaluationResponse>>
    getEvaluationsByCustomer(
            @PathVariable UUID customerId) {

        return ResponseEntity.ok(
                scenarioEvaluationService
                        .getEvaluationsByCustomer(customerId)
        );
    }

    @GetMapping("/status/{evaluationStatus}")
    public ResponseEntity<List<ScenarioEvaluationResponse>>
    getEvaluationsByStatus(
            @PathVariable String evaluationStatus) {

        return ResponseEntity.ok(
                scenarioEvaluationService
                        .getEvaluationsByStatus(evaluationStatus)
        );
    }

    @GetMapping("/matched/{matched}")
    public ResponseEntity<List<ScenarioEvaluationResponse>>
    getEvaluationsByMatched(
            @PathVariable Boolean matched) {

        return ResponseEntity.ok(
                scenarioEvaluationService
                        .getEvaluationsByMatched(matched)
        );
    }
}