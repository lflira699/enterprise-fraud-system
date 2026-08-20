package com.efs.modules.rules.controller;

import com.efs.modules.rules.dto.RuleHistoryRequest;
import com.efs.modules.rules.dto.RuleHistoryResponse;
import com.efs.modules.rules.service.RuleHistoryServiceInterface;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/rules/history")
public class RuleHistoryController {

    private final RuleHistoryServiceInterface ruleHistoryService;

    public RuleHistoryController(
            RuleHistoryServiceInterface ruleHistoryService) {

        this.ruleHistoryService = ruleHistoryService;
    }

    @PostMapping
    public ResponseEntity<RuleHistoryResponse> createRuleHistory(
            @Valid @RequestBody RuleHistoryRequest request) {

        RuleHistoryResponse response =
                ruleHistoryService.createRuleHistory(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/{historyId}")
    public ResponseEntity<RuleHistoryResponse> getRuleHistoryById(
            @PathVariable UUID historyId) {

        return ResponseEntity.ok(
                ruleHistoryService
                        .getRuleHistoryById(historyId)
        );
    }

    @GetMapping("/entity/{entityType}/{entityId}")
    public ResponseEntity<List<RuleHistoryResponse>>
    getRuleHistoriesByEntity(
            @PathVariable String entityType,
            @PathVariable UUID entityId) {

        return ResponseEntity.ok(
                ruleHistoryService
                        .getRuleHistoriesByEntity(
                                entityType,
                                entityId
                        )
        );
    }

    @GetMapping("/changed-by/{changedBy}")
    public ResponseEntity<List<RuleHistoryResponse>>
    getRuleHistoriesByChangedBy(
            @PathVariable UUID changedBy) {

        return ResponseEntity.ok(
                ruleHistoryService
                        .getRuleHistoriesByChangedBy(
                                changedBy
                        )
        );
    }

    @GetMapping("/operation/{operationType}")
    public ResponseEntity<List<RuleHistoryResponse>>
    getRuleHistoriesByOperationType(
            @PathVariable String operationType) {

        return ResponseEntity.ok(
                ruleHistoryService
                        .getRuleHistoriesByOperationType(
                                operationType
                        )
        );
    }

    @GetMapping("/correlation/{correlationId}")
    public ResponseEntity<List<RuleHistoryResponse>>
    getRuleHistoriesByCorrelationId(
            @PathVariable UUID correlationId) {

        return ResponseEntity.ok(
                ruleHistoryService
                        .getRuleHistoriesByCorrelationId(
                                correlationId
                        )
        );
    }
}