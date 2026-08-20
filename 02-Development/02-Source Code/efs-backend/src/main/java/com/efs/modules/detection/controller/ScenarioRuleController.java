package com.efs.modules.detection.controller;

import com.efs.modules.detection.dto.ScenarioRuleRequest;
import com.efs.modules.detection.dto.ScenarioRuleResponse;
import com.efs.modules.detection.service.ScenarioRuleServiceInterface;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/detection/scenario-rules")
public class ScenarioRuleController {

    private final ScenarioRuleServiceInterface scenarioRuleService;

    public ScenarioRuleController(
            ScenarioRuleServiceInterface scenarioRuleService) {

        this.scenarioRuleService = scenarioRuleService;
    }

    @PostMapping
    public ResponseEntity<ScenarioRuleResponse> createScenarioRule(
            @Valid @RequestBody ScenarioRuleRequest request) {

        ScenarioRuleResponse response =
                scenarioRuleService.createScenarioRule(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/{scenarioRuleId}")
    public ResponseEntity<ScenarioRuleResponse> getScenarioRuleById(
            @PathVariable UUID scenarioRuleId) {

        return ResponseEntity.ok(
                scenarioRuleService.getScenarioRuleById(
                        scenarioRuleId
                )
        );
    }

    @GetMapping("/scenario-version/{scenarioVersionId}")
    public ResponseEntity<List<ScenarioRuleResponse>>
    getScenarioRulesByScenarioVersion(
            @PathVariable UUID scenarioVersionId) {

        return ResponseEntity.ok(
                scenarioRuleService
                        .getScenarioRulesByScenarioVersion(
                                scenarioVersionId
                        )
        );
    }

    @GetMapping("/rule/{ruleId}")
    public ResponseEntity<List<ScenarioRuleResponse>>
    getScenarioRulesByRule(
            @PathVariable UUID ruleId) {

        return ResponseEntity.ok(
                scenarioRuleService
                        .getScenarioRulesByRule(ruleId)
        );
    }

    @GetMapping("/scenario-version/{scenarioVersionId}/required")
    public ResponseEntity<List<ScenarioRuleResponse>>
    getRequiredScenarioRules(
            @PathVariable UUID scenarioVersionId) {

        return ResponseEntity.ok(
                scenarioRuleService
                        .getRequiredScenarioRules(
                                scenarioVersionId
                        )
        );
    }
}