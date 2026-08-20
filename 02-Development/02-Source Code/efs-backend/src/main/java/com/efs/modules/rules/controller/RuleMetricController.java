package com.efs.modules.rules.controller;

import com.efs.modules.rules.dto.RuleMetricRequest;
import com.efs.modules.rules.dto.RuleMetricResponse;
import com.efs.modules.rules.service.RuleMetricServiceInterface;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/rules/metrics")
public class RuleMetricController {

    private final RuleMetricServiceInterface ruleMetricService;

    public RuleMetricController(
            RuleMetricServiceInterface ruleMetricService) {

        this.ruleMetricService = ruleMetricService;
    }

    @PostMapping
    public ResponseEntity<RuleMetricResponse> createRuleMetric(
            @Valid @RequestBody RuleMetricRequest request) {

        RuleMetricResponse response =
                ruleMetricService.createRuleMetric(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/{metricId}")
    public ResponseEntity<RuleMetricResponse> getRuleMetricById(
            @PathVariable UUID metricId) {

        return ResponseEntity.ok(
                ruleMetricService
                        .getRuleMetricById(metricId)
        );
    }

    @GetMapping("/rule/{ruleId}")
    public ResponseEntity<List<RuleMetricResponse>>
    getRuleMetricsByRuleId(
            @PathVariable UUID ruleId) {

        return ResponseEntity.ok(
                ruleMetricService
                        .getRuleMetricsByRuleId(ruleId)
        );
    }

    @GetMapping("/version/{ruleVersionId}")
    public ResponseEntity<List<RuleMetricResponse>>
    getRuleMetricsByRuleVersionId(
            @PathVariable UUID ruleVersionId) {

        return ResponseEntity.ok(
                ruleMetricService
                        .getRuleMetricsByRuleVersionId(ruleVersionId)
        );
    }

    @GetMapping("/date/{metricDate}")
    public ResponseEntity<List<RuleMetricResponse>>
    getRuleMetricsByDate(
            @PathVariable LocalDate metricDate) {

        return ResponseEntity.ok(
                ruleMetricService
                        .getRuleMetricsByDate(metricDate)
        );
    }
}