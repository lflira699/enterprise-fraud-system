package com.efs.modules.rules.controller;

import com.efs.modules.rules.dto.RuleParameterRequest;
import com.efs.modules.rules.dto.RuleParameterResponse;
import com.efs.modules.rules.service.RuleParameterServiceInterface;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/rules")
public class RuleParameterController {

    private final RuleParameterServiceInterface ruleParameterService;

    public RuleParameterController(
            RuleParameterServiceInterface ruleParameterService) {

        this.ruleParameterService = ruleParameterService;
    }

    @PostMapping("/versions/{ruleVersionId}/parameters")
    public ResponseEntity<RuleParameterResponse> createRuleParameter(
            @PathVariable UUID ruleVersionId,
            @Valid @RequestBody RuleParameterRequest request) {

        RuleParameterResponse response =
                ruleParameterService.createRuleParameter(
                        ruleVersionId,
                        request
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/parameters/{parameterId}")
    public ResponseEntity<RuleParameterResponse> getRuleParameterById(
            @PathVariable UUID parameterId) {

        return ResponseEntity.ok(
                ruleParameterService
                        .getRuleParameterById(parameterId)
        );
    }

    @GetMapping("/versions/{ruleVersionId}/parameters")
    public ResponseEntity<List<RuleParameterResponse>>
    getRuleParametersByRuleVersionId(
            @PathVariable UUID ruleVersionId) {

        return ResponseEntity.ok(
                ruleParameterService
                        .getRuleParametersByRuleVersionId(ruleVersionId)
        );
    }

    @GetMapping("/parameters/name/{parameterName}")
    public ResponseEntity<List<RuleParameterResponse>>
    getRuleParametersByName(
            @PathVariable String parameterName) {

        return ResponseEntity.ok(
                ruleParameterService
                        .getRuleParametersByName(parameterName)
        );
    }
}