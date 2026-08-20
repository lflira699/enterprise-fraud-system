package com.efs.modules.rules.controller;

import com.efs.modules.rules.dto.RulePolicyRequest;
import com.efs.modules.rules.dto.RulePolicyResponse;
import com.efs.modules.rules.service.RulePolicyServiceInterface;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/rules/policies")
public class RulePolicyController {

    private final RulePolicyServiceInterface rulePolicyService;

    public RulePolicyController(
            RulePolicyServiceInterface rulePolicyService) {

        this.rulePolicyService = rulePolicyService;
    }

    @PostMapping
    public ResponseEntity<RulePolicyResponse> createRulePolicy(
            @Valid @RequestBody RulePolicyRequest request) {

        RulePolicyResponse response =
                rulePolicyService.createRulePolicy(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/{policyId}")
    public ResponseEntity<RulePolicyResponse> getRulePolicyById(
            @PathVariable UUID policyId) {

        return ResponseEntity.ok(
                rulePolicyService
                        .getRulePolicyById(policyId)
        );
    }

    @GetMapping("/code/{policyCode}")
    public ResponseEntity<RulePolicyResponse> getRulePolicyByCode(
            @PathVariable String policyCode) {

        return ResponseEntity.ok(
                rulePolicyService
                        .getRulePolicyByCode(policyCode)
        );
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<RulePolicyResponse>>
    getRulePoliciesByStatus(
            @PathVariable String status) {

        return ResponseEntity.ok(
                rulePolicyService
                        .getRulePoliciesByStatus(status)
        );
    }

    @GetMapping("/organization/{organizationId}")
    public ResponseEntity<List<RulePolicyResponse>>
    getRulePoliciesByOrganizationId(
            @PathVariable UUID organizationId) {

        return ResponseEntity.ok(
                rulePolicyService
                        .getRulePoliciesByOrganizationId(organizationId)
        );
    }

    @GetMapping("/tenant/{tenantId}")
    public ResponseEntity<List<RulePolicyResponse>>
    getRulePoliciesByTenantId(
            @PathVariable UUID tenantId) {

        return ResponseEntity.ok(
                rulePolicyService
                        .getRulePoliciesByTenantId(tenantId)
        );
    }

    @GetMapping("/type/{policyType}")
    public ResponseEntity<List<RulePolicyResponse>>
    getRulePoliciesByType(
            @PathVariable String policyType) {

        return ResponseEntity.ok(
                rulePolicyService
                        .getRulePoliciesByType(policyType)
        );
    }
}