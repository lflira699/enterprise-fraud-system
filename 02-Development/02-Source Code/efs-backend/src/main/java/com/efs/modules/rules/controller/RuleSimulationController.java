package com.efs.modules.rules.controller;

import com.efs.modules.rules.dto.RuleSimulationRequest;
import com.efs.modules.rules.dto.RuleSimulationResponse;
import com.efs.modules.rules.service.RuleSimulationServiceInterface;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/rules/simulations")
public class RuleSimulationController {

    private final RuleSimulationServiceInterface ruleSimulationService;

    public RuleSimulationController(
            RuleSimulationServiceInterface ruleSimulationService) {

        this.ruleSimulationService = ruleSimulationService;
    }

    @PostMapping
    public ResponseEntity<RuleSimulationResponse> createRuleSimulation(
            @Valid @RequestBody RuleSimulationRequest request) {

        RuleSimulationResponse response =
                ruleSimulationService.createRuleSimulation(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/{simulationId}")
    public ResponseEntity<RuleSimulationResponse> getRuleSimulationById(
            @PathVariable UUID simulationId) {

        return ResponseEntity.ok(
                ruleSimulationService
                        .getRuleSimulationById(simulationId)
        );
    }

    @GetMapping("/entity/{entityType}/{entityId}")
    public ResponseEntity<List<RuleSimulationResponse>>
    getRuleSimulationsByEntity(
            @PathVariable String entityType,
            @PathVariable UUID entityId) {

        return ResponseEntity.ok(
                ruleSimulationService
                        .getRuleSimulationsByEntity(
                                entityType,
                                entityId
                        )
        );
    }

    @GetMapping("/status/{simulationStatus}")
    public ResponseEntity<List<RuleSimulationResponse>>
    getRuleSimulationsByStatus(
            @PathVariable String simulationStatus) {

        return ResponseEntity.ok(
                ruleSimulationService
                        .getRuleSimulationsByStatus(
                                simulationStatus
                        )
        );
    }

    @GetMapping("/executed-by/{executedBy}")
    public ResponseEntity<List<RuleSimulationResponse>>
    getRuleSimulationsByExecutedBy(
            @PathVariable UUID executedBy) {

        return ResponseEntity.ok(
                ruleSimulationService
                        .getRuleSimulationsByExecutedBy(
                                executedBy
                        )
        );
    }
}