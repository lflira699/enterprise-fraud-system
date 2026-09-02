package com.efs.modules.rules.controller;

import com.efs.modules.rules.dto.RuleRequest;
import com.efs.modules.rules.dto.RuleResponse;
import com.efs.modules.rules.service.RuleServiceInterface;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/rules")
public class RuleController {

    private final RuleServiceInterface ruleService;

    public RuleController(
            RuleServiceInterface ruleService) {

        this.ruleService = ruleService;
    }

    @PostMapping
    public ResponseEntity<RuleResponse> createRule(
            @Valid @RequestBody RuleRequest request) {

        RuleResponse response =
                ruleService.createRule(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping
    public ResponseEntity<List<RuleResponse>> getRules() {

        return ResponseEntity.ok(
                ruleService.getRules()
        );
    }

    @GetMapping("/{ruleId}")
    public ResponseEntity<RuleResponse> getRuleById(
            @PathVariable UUID ruleId) {

        return ResponseEntity.ok(
                ruleService.getRuleById(ruleId)
        );
    }

    @GetMapping("/code/{ruleCode}")
    public ResponseEntity<RuleResponse> getRuleByCode(
            @PathVariable String ruleCode) {

        return ResponseEntity.ok(
                ruleService.getRuleByCode(ruleCode)
        );
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<RuleResponse>> getRulesByStatus(
            @PathVariable String status) {

        return ResponseEntity.ok(
                ruleService.getRulesByStatus(status)
        );
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<RuleResponse>> getRulesByCategory(
            @PathVariable String category) {

        return ResponseEntity.ok(
                ruleService.getRulesByCategory(category)
        );
    }

    @GetMapping("/severity/{severity}")
    public ResponseEntity<List<RuleResponse>> getRulesBySeverity(
            @PathVariable String severity) {

        return ResponseEntity.ok(
                ruleService.getRulesBySeverity(severity)
        );
    }
}