package com.efs.modules.detection.controller;

import com.efs.modules.detection.dto.ScenarioActivationRequest;
import com.efs.modules.detection.dto.ScenarioActivationResponse;
import com.efs.modules.detection.service.ScenarioActivationAccessServiceInterface;
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
@RequestMapping("/api/v1/detection/scenario-activations")
public class ScenarioActivationController {

    private final ScenarioActivationAccessServiceInterface
            scenarioActivationAccessService;

    private final SecurityContextProvider
            securityContextProvider;

    public ScenarioActivationController(
            ScenarioActivationAccessServiceInterface
                    scenarioActivationAccessService,
            SecurityContextProvider securityContextProvider) {

        this.scenarioActivationAccessService =
                scenarioActivationAccessService;

        this.securityContextProvider =
                securityContextProvider;
    }

    @PostMapping
    public ResponseEntity<ScenarioActivationResponse>
    createScenarioActivation(
            @Valid @RequestBody ScenarioActivationRequest request) {

        ScenarioActivationResponse response =
                scenarioActivationAccessService
                        .createScenarioActivation(
                                request,
                                currentContext()
                        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/{activationId}")
    public ResponseEntity<ScenarioActivationResponse>
    getScenarioActivationById(
            @PathVariable UUID activationId) {

        return ResponseEntity.ok(
                scenarioActivationAccessService
                        .getScenarioActivationById(
                                activationId,
                                currentContext()
                        )
        );
    }

    @GetMapping("/scenario/{scenarioId}")
    public ResponseEntity<List<ScenarioActivationResponse>>
    getActivationsByScenario(
            @PathVariable UUID scenarioId) {

        return ResponseEntity.ok(
                scenarioActivationAccessService
                        .getActivationsByScenario(
                                scenarioId,
                                currentContext()
                        )
        );
    }

    @GetMapping("/scenario-version/{scenarioVersionId}")
    public ResponseEntity<List<ScenarioActivationResponse>>
    getActivationsByScenarioVersion(
            @PathVariable UUID scenarioVersionId) {

        return ResponseEntity.ok(
                scenarioActivationAccessService
                        .getActivationsByScenarioVersion(
                                scenarioVersionId,
                                currentContext()
                        )
        );
    }

    @GetMapping("/transaction/{transactionId}")
    public ResponseEntity<List<ScenarioActivationResponse>>
    getActivationsByTransaction(
            @PathVariable UUID transactionId) {

        return ResponseEntity.ok(
                scenarioActivationAccessService
                        .getActivationsByTransaction(
                                transactionId,
                                currentContext()
                        )
        );
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<ScenarioActivationResponse>>
    getActivationsByCustomer(
            @PathVariable UUID customerId) {

        return ResponseEntity.ok(
                scenarioActivationAccessService
                        .getActivationsByCustomer(
                                customerId,
                                currentContext()
                        )
        );
    }

    @GetMapping("/status/{activationStatus}")
    public ResponseEntity<List<ScenarioActivationResponse>>
    getActivationsByStatus(
            @PathVariable String activationStatus) {

        return ResponseEntity.ok(
                scenarioActivationAccessService
                        .getActivationsByStatus(
                                activationStatus,
                                currentContext()
                        )
        );
    }

    @GetMapping("/severity/{severity}")
    public ResponseEntity<List<ScenarioActivationResponse>>
    getActivationsBySeverity(
            @PathVariable String severity) {

        return ResponseEntity.ok(
                scenarioActivationAccessService
                        .getActivationsBySeverity(
                                severity,
                                currentContext()
                        )
        );
    }

    private SecurityContext currentContext() {

        return securityContextProvider
                .getCurrentContext();
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Void> handleAccessDenied() {

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .build();
    }
}