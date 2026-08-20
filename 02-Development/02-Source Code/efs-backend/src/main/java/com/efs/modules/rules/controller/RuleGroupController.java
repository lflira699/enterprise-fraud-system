package com.efs.modules.rules.controller;

import com.efs.modules.rules.dto.RuleGroupRequest;
import com.efs.modules.rules.dto.RuleGroupResponse;
import com.efs.modules.rules.service.RuleGroupServiceInterface;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/rules/groups")
public class RuleGroupController {

    private final RuleGroupServiceInterface ruleGroupService;

    public RuleGroupController(
            RuleGroupServiceInterface ruleGroupService) {

        this.ruleGroupService = ruleGroupService;
    }

    @PostMapping
    public ResponseEntity<RuleGroupResponse> createRuleGroup(
            @Valid @RequestBody RuleGroupRequest request) {

        RuleGroupResponse response =
                ruleGroupService.createRuleGroup(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/{ruleGroupId}")
    public ResponseEntity<RuleGroupResponse> getRuleGroupById(
            @PathVariable UUID ruleGroupId) {

        return ResponseEntity.ok(
                ruleGroupService
                        .getRuleGroupById(ruleGroupId)
        );
    }

    @GetMapping("/code/{groupCode}")
    public ResponseEntity<RuleGroupResponse> getRuleGroupByCode(
            @PathVariable String groupCode) {

        return ResponseEntity.ok(
                ruleGroupService
                        .getRuleGroupByCode(groupCode)
        );
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<RuleGroupResponse>>
    getRuleGroupsByStatus(
            @PathVariable String status) {

        return ResponseEntity.ok(
                ruleGroupService
                        .getRuleGroupsByStatus(status)
        );
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<RuleGroupResponse>>
    getRuleGroupsByCategory(
            @PathVariable String category) {

        return ResponseEntity.ok(
                ruleGroupService
                        .getRuleGroupsByCategory(category)
        );
    }
}