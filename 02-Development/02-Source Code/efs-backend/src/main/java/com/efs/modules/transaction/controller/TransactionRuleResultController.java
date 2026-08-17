package com.efs.modules.transaction.controller;

import com.efs.modules.transaction.dto.TransactionRuleResultRequest;
import com.efs.modules.transaction.dto.TransactionRuleResultResponse;
import com.efs.modules.transaction.service.TransactionRuleResultServiceInterface;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/transactions")
public class TransactionRuleResultController {

    private final TransactionRuleResultServiceInterface
            transactionRuleResultService;

    public TransactionRuleResultController(
            TransactionRuleResultServiceInterface
                    transactionRuleResultService) {

        this.transactionRuleResultService =
                transactionRuleResultService;
    }

    @PostMapping("/{transactionId}/rule-results")
    public ResponseEntity<TransactionRuleResultResponse>
    createRuleResult(
            @PathVariable UUID transactionId,
            @Valid @RequestBody
            TransactionRuleResultRequest request) {

        TransactionRuleResultResponse response =
                transactionRuleResultService.createRuleResult(
                        transactionId,
                        request
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/rule-results/{ruleResultId}")
    public ResponseEntity<TransactionRuleResultResponse>
    getRuleResultById(
            @PathVariable UUID ruleResultId) {

        return ResponseEntity.ok(
                transactionRuleResultService
                        .getRuleResultById(ruleResultId)
        );
    }

    @GetMapping("/{transactionId}/rule-results")
    public ResponseEntity<List<TransactionRuleResultResponse>>
    getRuleResultsByTransactionId(
            @PathVariable UUID transactionId) {

        return ResponseEntity.ok(
                transactionRuleResultService
                        .getRuleResultsByTransactionId(
                                transactionId
                        )
        );
    }

    @GetMapping("/rule-results/rule/{ruleId}")
    public ResponseEntity<List<TransactionRuleResultResponse>>
    getRuleResultsByRuleId(
            @PathVariable UUID ruleId) {

        return ResponseEntity.ok(
                transactionRuleResultService
                        .getRuleResultsByRuleId(ruleId)
        );
    }

    @GetMapping("/rule-results/result/{evaluationResult}")
    public ResponseEntity<List<TransactionRuleResultResponse>>
    getRuleResultsByEvaluationResult(
            @PathVariable String evaluationResult) {

        return ResponseEntity.ok(
                transactionRuleResultService
                        .getRuleResultsByEvaluationResult(
                                evaluationResult
                        )
        );
    }
}