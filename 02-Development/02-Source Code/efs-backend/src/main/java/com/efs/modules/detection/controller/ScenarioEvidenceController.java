package com.efs.modules.detection.controller;

import com.efs.modules.detection.dto.ScenarioEvidenceRequest;
import com.efs.modules.detection.dto.ScenarioEvidenceResponse;
import com.efs.modules.detection.service.ScenarioEvidenceServiceInterface;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/detection/scenario-evidence")
public class ScenarioEvidenceController {

    private final ScenarioEvidenceServiceInterface scenarioEvidenceService;

    public ScenarioEvidenceController(
            ScenarioEvidenceServiceInterface scenarioEvidenceService) {

        this.scenarioEvidenceService = scenarioEvidenceService;
    }

    @PostMapping
    public ResponseEntity<ScenarioEvidenceResponse> createScenarioEvidence(
            @Valid @RequestBody ScenarioEvidenceRequest request) {

        ScenarioEvidenceResponse response =
                scenarioEvidenceService.createScenarioEvidence(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/{evidenceId}")
    public ResponseEntity<ScenarioEvidenceResponse> getScenarioEvidenceById(
            @PathVariable UUID evidenceId) {

        return ResponseEntity.ok(
                scenarioEvidenceService.getScenarioEvidenceById(
                        evidenceId
                )
        );
    }

    @GetMapping("/scenario-version/{scenarioVersionId}")
    public ResponseEntity<List<ScenarioEvidenceResponse>>
    getEvidenceByScenarioVersion(
            @PathVariable UUID scenarioVersionId) {

        return ResponseEntity.ok(
                scenarioEvidenceService
                        .getEvidenceByScenarioVersion(
                                scenarioVersionId
                        )
        );
    }

    @GetMapping("/type/{evidenceType}")
    public ResponseEntity<List<ScenarioEvidenceResponse>>
    getEvidenceByType(
            @PathVariable String evidenceType) {

        return ResponseEntity.ok(
                scenarioEvidenceService
                        .getEvidenceByType(evidenceType)
        );
    }

    @GetMapping("/source-type/{sourceType}")
    public ResponseEntity<List<ScenarioEvidenceResponse>>
    getEvidenceBySourceType(
            @PathVariable String sourceType) {

        return ResponseEntity.ok(
                scenarioEvidenceService
                        .getEvidenceBySourceType(sourceType)
        );
    }
}