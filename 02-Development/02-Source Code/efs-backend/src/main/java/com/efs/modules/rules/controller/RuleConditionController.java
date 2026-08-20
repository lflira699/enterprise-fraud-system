package com.efs.modules.rules.controller;

import com.efs.modules.rules.dto.RuleConditionRequest;
import com.efs.modules.rules.dto.RuleConditionResponse;
import com.efs.modules.rules.service.RuleConditionServiceInterface;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/rules")
public class RuleConditionController {

    private final RuleConditionServiceInterface ruleConditionService;

    public RuleConditionController(
            RuleConditionServiceInterface ruleConditionService) {

        this.ruleConditionService = ruleConditionService;
    }

    @PostMapping("/versions/{ruleVersionId}/conditions")
    public ResponseEntity<RuleConditionResponse> createRuleCondition(
            @PathVariable UUID ruleVersionId,
            @Valid @RequestBody RuleConditionRequest request) {

        RuleConditionResponse response =
                ruleConditionService.createRuleCondition(
                        ruleVersionId,
                        request
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/conditions/{conditionId}")
    public ResponseEntity<RuleConditionResponse> getRuleConditionById(
            @PathVariable UUID conditionId) {

        return ResponseEntity.ok(
                ruleConditionService
                        .getRuleConditionById(conditionId)
        );
    }

    @GetMapping("/versions/{ruleVersionId}/conditions")
    public ResponseEntity<List<RuleConditionResponse>>
    getRuleConditionsByRuleVersionId(
            @PathVariable UUID ruleVersionId) {

        return ResponseEntity.ok(
                ruleConditionService
                        .getRuleConditionsByRuleVersionId(ruleVersionId)
        );
    }

    @GetMapping("/conditions/attribute/{attributeName}")
    public ResponseEntity<List<RuleConditionResponse>>
    getRuleConditionsByAttributeName(
            @PathVariable String attributeName) {

        return ResponseEntity.ok(
                ruleConditionService
                        .getRuleConditionsByAttributeName(attributeName)
        );
    }
}