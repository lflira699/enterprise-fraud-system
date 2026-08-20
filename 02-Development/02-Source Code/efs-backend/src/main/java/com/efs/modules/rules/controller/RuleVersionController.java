package com.efs.modules.rules.controller;

import com.efs.modules.rules.dto.RuleVersionRequest;
import com.efs.modules.rules.dto.RuleVersionResponse;
import com.efs.modules.rules.service.RuleVersionServiceInterface;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/rules")
public class RuleVersionController {

    private final RuleVersionServiceInterface ruleVersionService;

    public RuleVersionController(
            RuleVersionServiceInterface ruleVersionService) {

        this.ruleVersionService = ruleVersionService;
    }

    @PostMapping("/{ruleId}/versions")
    public ResponseEntity<RuleVersionResponse> createRuleVersion(
            @PathVariable UUID ruleId,
            @Valid @RequestBody RuleVersionRequest request) {

        RuleVersionResponse response =
                ruleVersionService.createRuleVersion(
                        ruleId,
                        request
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/versions/{ruleVersionId}")
    public ResponseEntity<RuleVersionResponse> getRuleVersionById(
            @PathVariable UUID ruleVersionId) {

        return ResponseEntity.ok(
                ruleVersionService
                        .getRuleVersionById(ruleVersionId)
        );
    }

    @GetMapping("/{ruleId}/versions")
    public ResponseEntity<List<RuleVersionResponse>>
    getRuleVersionsByRuleId(
            @PathVariable UUID ruleId) {

        return ResponseEntity.ok(
                ruleVersionService
                        .getRuleVersionsByRuleId(ruleId)
        );
    }

    @GetMapping("/{ruleId}/versions/{versionNumber}")
    public ResponseEntity<RuleVersionResponse>
    getRuleVersionByRuleIdAndVersionNumber(
            @PathVariable UUID ruleId,
            @PathVariable Integer versionNumber) {

        return ResponseEntity.ok(
                ruleVersionService
                        .getRuleVersionByRuleIdAndVersionNumber(
                                ruleId,
                                versionNumber
                        )
        );
    }

    @GetMapping("/versions/status/{publicationStatus}")
    public ResponseEntity<List<RuleVersionResponse>>
    getRuleVersionsByPublicationStatus(
            @PathVariable String publicationStatus) {

        return ResponseEntity.ok(
                ruleVersionService
                        .getRuleVersionsByPublicationStatus(
                                publicationStatus
                        )
        );
    }
}