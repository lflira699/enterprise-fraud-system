package com.efs.modules.detection.controller;

import com.efs.modules.detection.dto.ScenarioEvaluationRequest;
import com.efs.modules.detection.dto.ScenarioEvaluationResponse;
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
@RequestMapping("/api/v1/detection/scenario-evaluations")
public class ScenarioEvaluationController {

    private final ScenarioEvaluationAccessServiceInterface
            scenarioEvaluationAccessService;

    private final SecurityContextProvider
            securityContextProvider;

    public ScenarioEvaluationController(
            ScenarioEvaluationAccessServiceInterface
                    scenarioEvaluationAccessService,
            SecurityContextProvider securityContextProvider) {

        this.scenarioEvaluationAccessService =
                scenarioEvaluationAccessService;

        this.securityContextProvider =
                securityContextProvider;
    }

    @PostMapping
    public ResponseEntity<ScenarioEvaluationResponse>
    createScenarioEvaluation(
            @Valid @RequestBody ScenarioEvaluationRequest request) {

        ScenarioEvaluationResponse response =
                scenarioEvaluationAccessService
                        .createScenarioEvaluation(
                                request,
                                currentContext()
                        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/{evaluationId}")
    public ResponseEntity<ScenarioEvaluationResponse>
    getScenarioEvaluationById(
            @PathVariable UUID evaluationId) {

        return ResponseEntity.ok(
                scenarioEvaluationAccessService
                        .getScenarioEvaluationById(
                                evaluationId,
                                currentContext()
                        )
        );
    }

    @GetMapping("/scenario/{scenarioId}")
    public ResponseEntity<List<ScenarioEvaluationResponse>>
    getEvaluationsByScenario(
            @PathVariable UUID scenarioId) {

        return ResponseEntity.ok(
                scenarioEvaluationAccessService
                        .getEvaluationsByScenario(
                                scenarioId,
                                currentContext()
                        )
        );
    }

    @GetMapping("/scenario-version/{scenarioVersionId}")
    public ResponseEntity<List<ScenarioEvaluationResponse>>
    getEvaluationsByScenarioVersion(
            @PathVariable UUID scenarioVersionId) {

        return ResponseEntity.ok(
                scenarioEvaluationAccessService
                        .getEvaluationsByScenarioVersion(
                                scenarioVersionId,
                                currentContext()
                        )
        );
    }

    @GetMapping("/transaction/{transactionId}")
    public ResponseEntity<List<ScenarioEvaluationResponse>>
    getEvaluationsByTransaction(
            @PathVariable UUID transactionId) {

        return ResponseEntity.ok(
                scenarioEvaluationAccessService
                        .getEvaluationsByTransaction(
                                transactionId,
                                currentContext()
                        )
        );
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<ScenarioEvaluationResponse>>
    getEvaluationsByCustomer(
            @PathVariable UUID customerId) {

        return ResponseEntity.ok(
                scenarioEvaluationAccessService
                        .getEvaluationsByCustomer(
                                customerId,
                                currentContext()
                        )
        );
    }

    @GetMapping("/status/{evaluationStatus}")
    public ResponseEntity<List<ScenarioEvaluationResponse>>
    getEvaluationsByStatus(
            @PathVariable String evaluationStatus) {

        return ResponseEntity.ok(
                scenarioEvaluationAccessService
                        .getEvaluationsByStatus(
                                evaluationStatus,
                                currentContext()
                        )
        );
    }

    @GetMapping("/matched/{matched}")
    public ResponseEntity<List<ScenarioEvaluationResponse>>
    getEvaluationsByMatched(
            @PathVariable Boolean matched) {

        return ResponseEntity.ok(
                scenarioEvaluationAccessService
                        .getEvaluationsByMatched(
                                matched,
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