package com.efs.modules.rules.controller;

import com.efs.modules.rules.dto.RuleExecutionRequest;
import com.efs.modules.rules.dto.RuleExecutionResponse;
import com.efs.modules.rules.service.RuleExecutionServiceInterface;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/rules/executions")
public class RuleExecutionController {

    private final RuleExecutionServiceInterface ruleExecutionService;

    public RuleExecutionController(
            RuleExecutionServiceInterface ruleExecutionService) {

        this.ruleExecutionService = ruleExecutionService;
    }

    @PostMapping
    public ResponseEntity<RuleExecutionResponse> createRuleExecution(
            @Valid @RequestBody RuleExecutionRequest request) {

        RuleExecutionResponse response =
                ruleExecutionService.createRuleExecution(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/{executionId}")
    public ResponseEntity<RuleExecutionResponse> getRuleExecutionById(
            @PathVariable UUID executionId) {

        return ResponseEntity.ok(
                ruleExecutionService
                        .getRuleExecutionById(executionId)
        );
    }

    @GetMapping("/rule/{ruleId}")
    public ResponseEntity<List<RuleExecutionResponse>>
    getRuleExecutionsByRuleId(
            @PathVariable UUID ruleId) {

        return ResponseEntity.ok(
                ruleExecutionService
                        .getRuleExecutionsByRuleId(ruleId)
        );
    }

    @GetMapping("/version/{ruleVersionId}")
    public ResponseEntity<List<RuleExecutionResponse>>
    getRuleExecutionsByRuleVersionId(
            @PathVariable UUID ruleVersionId) {

        return ResponseEntity.ok(
                ruleExecutionService
                        .getRuleExecutionsByRuleVersionId(ruleVersionId)
        );
    }

    @GetMapping("/policy/{policyId}")
    public ResponseEntity<List<RuleExecutionResponse>>
    getRuleExecutionsByPolicyId(
            @PathVariable UUID policyId) {

        return ResponseEntity.ok(
                ruleExecutionService
                        .getRuleExecutionsByPolicyId(policyId)
        );
    }

    @GetMapping("/transaction/{transactionId}")
    public ResponseEntity<List<RuleExecutionResponse>>
    getRuleExecutionsByTransactionId(
            @PathVariable UUID transactionId) {

        return ResponseEntity.ok(
                ruleExecutionService
                        .getRuleExecutionsByTransactionId(transactionId)
        );
    }

    @GetMapping("/status/{executionStatus}")
    public ResponseEntity<List<RuleExecutionResponse>>
    getRuleExecutionsByStatus(
            @PathVariable String executionStatus) {

        return ResponseEntity.ok(
                ruleExecutionService
                        .getRuleExecutionsByStatus(executionStatus)
        );
    }
}