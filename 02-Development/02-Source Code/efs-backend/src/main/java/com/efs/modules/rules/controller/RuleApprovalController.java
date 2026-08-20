package com.efs.modules.rules.controller;

import com.efs.modules.rules.dto.RuleApprovalRequest;
import com.efs.modules.rules.dto.RuleApprovalResponse;
import com.efs.modules.rules.service.RuleApprovalServiceInterface;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/rules/approvals")
public class RuleApprovalController {

    private final RuleApprovalServiceInterface ruleApprovalService;

    public RuleApprovalController(
            RuleApprovalServiceInterface ruleApprovalService) {

        this.ruleApprovalService = ruleApprovalService;
    }

    @PostMapping
    public ResponseEntity<RuleApprovalResponse> createRuleApproval(
            @Valid @RequestBody RuleApprovalRequest request) {

        RuleApprovalResponse response =
                ruleApprovalService.createRuleApproval(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/{approvalId}")
    public ResponseEntity<RuleApprovalResponse> getRuleApprovalById(
            @PathVariable UUID approvalId) {

        return ResponseEntity.ok(
                ruleApprovalService
                        .getRuleApprovalById(approvalId)
        );
    }

    @GetMapping("/entity/{entityType}/{entityId}")
    public ResponseEntity<List<RuleApprovalResponse>>
    getRuleApprovalsByEntity(
            @PathVariable String entityType,
            @PathVariable UUID entityId) {

        return ResponseEntity.ok(
                ruleApprovalService
                        .getRuleApprovalsByEntity(
                                entityType,
                                entityId
                        )
        );
    }

    @GetMapping("/status/{approvalStatus}")
    public ResponseEntity<List<RuleApprovalResponse>>
    getRuleApprovalsByStatus(
            @PathVariable String approvalStatus) {

        return ResponseEntity.ok(
                ruleApprovalService
                        .getRuleApprovalsByStatus(
                                approvalStatus
                        )
        );
    }

    @GetMapping("/submitted-by/{submittedBy}")
    public ResponseEntity<List<RuleApprovalResponse>>
    getRuleApprovalsBySubmittedBy(
            @PathVariable UUID submittedBy) {

        return ResponseEntity.ok(
                ruleApprovalService
                        .getRuleApprovalsBySubmittedBy(
                                submittedBy
                        )
        );
    }

    @GetMapping("/reviewed-by/{reviewedBy}")
    public ResponseEntity<List<RuleApprovalResponse>>
    getRuleApprovalsByReviewedBy(
            @PathVariable UUID reviewedBy) {

        return ResponseEntity.ok(
                ruleApprovalService
                        .getRuleApprovalsByReviewedBy(
                                reviewedBy
                        )
        );
    }
}