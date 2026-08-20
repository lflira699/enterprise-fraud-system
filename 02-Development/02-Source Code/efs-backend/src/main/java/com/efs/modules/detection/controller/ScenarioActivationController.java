package com.efs.modules.detection.controller;

import com.efs.modules.detection.dto.ScenarioActivationRequest;
import com.efs.modules.detection.dto.ScenarioActivationResponse;
import com.efs.modules.detection.service.ScenarioActivationServiceInterface;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/detection/scenario-activations")
public class ScenarioActivationController {

    private final ScenarioActivationServiceInterface scenarioActivationService;

    public ScenarioActivationController(
            ScenarioActivationServiceInterface scenarioActivationService) {

        this.scenarioActivationService = scenarioActivationService;
    }

    @PostMapping
    public ResponseEntity<ScenarioActivationResponse> createScenarioActivation(
            @Valid @RequestBody ScenarioActivationRequest request) {

        ScenarioActivationResponse response =
                scenarioActivationService.createScenarioActivation(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/{activationId}")
    public ResponseEntity<ScenarioActivationResponse> getScenarioActivationById(
            @PathVariable UUID activationId) {

        return ResponseEntity.ok(
                scenarioActivationService
                        .getScenarioActivationById(activationId)
        );
    }

    @GetMapping("/scenario/{scenarioId}")
    public ResponseEntity<List<ScenarioActivationResponse>>
    getActivationsByScenario(
            @PathVariable UUID scenarioId) {

        return ResponseEntity.ok(
                scenarioActivationService
                        .getActivationsByScenario(scenarioId)
        );
    }

    @GetMapping("/scenario-version/{scenarioVersionId}")
    public ResponseEntity<List<ScenarioActivationResponse>>
    getActivationsByScenarioVersion(
            @PathVariable UUID scenarioVersionId) {

        return ResponseEntity.ok(
                scenarioActivationService
                        .getActivationsByScenarioVersion(
                                scenarioVersionId
                        )
        );
    }

    @GetMapping("/transaction/{transactionId}")
    public ResponseEntity<List<ScenarioActivationResponse>>
    getActivationsByTransaction(
            @PathVariable UUID transactionId) {

        return ResponseEntity.ok(
                scenarioActivationService
                        .getActivationsByTransaction(transactionId)
        );
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<ScenarioActivationResponse>>
    getActivationsByCustomer(
            @PathVariable UUID customerId) {

        return ResponseEntity.ok(
                scenarioActivationService
                        .getActivationsByCustomer(customerId)
        );
    }

    @GetMapping("/status/{activationStatus}")
    public ResponseEntity<List<ScenarioActivationResponse>>
    getActivationsByStatus(
            @PathVariable String activationStatus) {

        return ResponseEntity.ok(
                scenarioActivationService
                        .getActivationsByStatus(activationStatus)
        );
    }

    @GetMapping("/severity/{severity}")
    public ResponseEntity<List<ScenarioActivationResponse>>
    getActivationsBySeverity(
            @PathVariable String severity) {

        return ResponseEntity.ok(
                scenarioActivationService
                        .getActivationsBySeverity(severity)
        );
    }
}