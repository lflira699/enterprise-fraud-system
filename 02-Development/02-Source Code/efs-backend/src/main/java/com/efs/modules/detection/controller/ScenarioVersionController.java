package com.efs.modules.detection.controller;

import com.efs.modules.detection.dto.ScenarioVersionRequest;
import com.efs.modules.detection.dto.ScenarioVersionResponse;
import com.efs.modules.detection.service.ScenarioVersionServiceInterface;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/detection/scenario-versions")
public class ScenarioVersionController {

    private final ScenarioVersionServiceInterface scenarioVersionService;

    public ScenarioVersionController(
            ScenarioVersionServiceInterface scenarioVersionService) {

        this.scenarioVersionService = scenarioVersionService;
    }

    @PostMapping
    public ResponseEntity<ScenarioVersionResponse> createScenarioVersion(
            @Valid @RequestBody ScenarioVersionRequest request) {

        ScenarioVersionResponse response =
                scenarioVersionService.createScenarioVersion(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/{scenarioVersionId}")
    public ResponseEntity<ScenarioVersionResponse> getScenarioVersionById(
            @PathVariable UUID scenarioVersionId) {

        return ResponseEntity.ok(
                scenarioVersionService
                        .getScenarioVersionById(scenarioVersionId)
        );
    }

    @GetMapping("/scenario/{scenarioId}")
    public ResponseEntity<List<ScenarioVersionResponse>>
    getScenarioVersionsByScenario(
            @PathVariable UUID scenarioId) {

        return ResponseEntity.ok(
                scenarioVersionService
                        .getScenarioVersionsByScenario(scenarioId)
        );
    }

    @GetMapping("/scenario/{scenarioId}/version/{versionNumber}")
    public ResponseEntity<ScenarioVersionResponse>
    getScenarioVersionByNumber(
            @PathVariable UUID scenarioId,
            @PathVariable Integer versionNumber) {

        return ResponseEntity.ok(
                scenarioVersionService
                        .getScenarioVersionByNumber(
                                scenarioId,
                                versionNumber
                        )
        );
    }

    @GetMapping("/status/{versionStatus}")
    public ResponseEntity<List<ScenarioVersionResponse>>
    getScenarioVersionsByStatus(
            @PathVariable String versionStatus) {

        return ResponseEntity.ok(
                scenarioVersionService
                        .getScenarioVersionsByStatus(versionStatus)
        );
    }

    @GetMapping("/activation-mode/{activationMode}")
    public ResponseEntity<List<ScenarioVersionResponse>>
    getScenarioVersionsByActivationMode(
            @PathVariable String activationMode) {

        return ResponseEntity.ok(
                scenarioVersionService
                        .getScenarioVersionsByActivationMode(
                                activationMode
                        )
        );
    }
}