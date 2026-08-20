package com.efs.modules.rules.controller;

import com.efs.modules.rules.dto.RuleActionRequest;
import com.efs.modules.rules.dto.RuleActionResponse;
import com.efs.modules.rules.service.RuleActionServiceInterface;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/rules")
public class RuleActionController {

    private final RuleActionServiceInterface ruleActionService;

    public RuleActionController(
            RuleActionServiceInterface ruleActionService) {

        this.ruleActionService = ruleActionService;
    }

    @PostMapping("/versions/{ruleVersionId}/actions")
    public ResponseEntity<RuleActionResponse> createRuleAction(
            @PathVariable UUID ruleVersionId,
            @Valid @RequestBody RuleActionRequest request) {

        RuleActionResponse response =
                ruleActionService.createRuleAction(
                        ruleVersionId,
                        request
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/actions/{actionId}")
    public ResponseEntity<RuleActionResponse> getRuleActionById(
            @PathVariable UUID actionId) {

        return ResponseEntity.ok(
                ruleActionService
                        .getRuleActionById(actionId)
        );
    }

    @GetMapping("/versions/{ruleVersionId}/actions")
    public ResponseEntity<List<RuleActionResponse>>
    getRuleActionsByRuleVersionId(
            @PathVariable UUID ruleVersionId) {

        return ResponseEntity.ok(
                ruleActionService
                        .getRuleActionsByRuleVersionId(ruleVersionId)
        );
    }

    @GetMapping("/actions/type/{actionType}")
    public ResponseEntity<List<RuleActionResponse>>
    getRuleActionsByType(
            @PathVariable String actionType) {

        return ResponseEntity.ok(
                ruleActionService
                        .getRuleActionsByType(actionType)
        );
    }
}