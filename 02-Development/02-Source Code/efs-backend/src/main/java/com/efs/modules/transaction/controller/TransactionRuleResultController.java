package com.efs.modules.transaction.controller;

import com.efs.modules.transaction.dto.TransactionRuleResultRequest;
import com.efs.modules.transaction.dto.TransactionRuleResultResponse;
import com.efs.modules.transaction.service.TransactionChildAccessServiceInterface;
import com.efs.modules.transaction.service.TransactionRuleResultServiceInterface;
import com.efs.shared.security.SecurityContext;
import com.efs.shared.security.SecurityContextProvider;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/transactions")
public class TransactionRuleResultController {

    private final TransactionRuleResultServiceInterface
            transactionRuleResultService;

    private final TransactionChildAccessServiceInterface
            transactionChildAccessService;

    private final SecurityContextProvider
            securityContextProvider;

    public TransactionRuleResultController(
            TransactionRuleResultServiceInterface
                    transactionRuleResultService,
            TransactionChildAccessServiceInterface
                    transactionChildAccessService,
            SecurityContextProvider securityContextProvider) {

        this.transactionRuleResultService =
                transactionRuleResultService;

        this.transactionChildAccessService =
                transactionChildAccessService;

        this.securityContextProvider =
                securityContextProvider;
    }

    @PostMapping("/{transactionId}/rule-results")
    public ResponseEntity<TransactionRuleResultResponse>
    createRuleResult(
            @PathVariable UUID transactionId,
            @Valid @RequestBody
            TransactionRuleResultRequest request) {

        SecurityContext securityContext =
                securityContextProvider
                        .getCurrentContext();

        TransactionRuleResultResponse response =
                transactionChildAccessService
                        .create(
                                securityContext,
                                transactionId,
                                request,
                                transactionRuleResultService
                                        ::createRuleResult
                        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/rule-results/{ruleResultId}")
    public ResponseEntity<TransactionRuleResultResponse>
    getRuleResultById(
            @PathVariable UUID ruleResultId) {

        SecurityContext securityContext =
                securityContextProvider
                        .getCurrentContext();

        return ResponseEntity.ok(
                transactionChildAccessService
                        .getById(
                                securityContext,
                                ruleResultId,
                                transactionRuleResultService
                                        ::getRuleResultById,
                                TransactionRuleResultResponse
                                        ::getTransactionId,
                                "Transaction rule result not found: "
                                        + ruleResultId
                        )
        );
    }

    @GetMapping("/{transactionId}/rule-results")
    public ResponseEntity<List<TransactionRuleResultResponse>>
    getRuleResultsByTransactionId(
            @PathVariable UUID transactionId) {

        SecurityContext securityContext =
                securityContextProvider
                        .getCurrentContext();

        return ResponseEntity.ok(
                transactionChildAccessService
                        .getByTransactionId(
                                securityContext,
                                transactionId,
                                transactionRuleResultService
                                        ::getRuleResultsByTransactionId
                        )
        );
    }

    @GetMapping("/rule-results/rule/{ruleId}")
    public ResponseEntity<List<TransactionRuleResultResponse>>
    getRuleResultsByRuleId(
            @PathVariable UUID ruleId) {

        SecurityContext securityContext =
                securityContextProvider
                        .getCurrentContext();

        return ResponseEntity.ok(
                transactionChildAccessService
                        .filterVisible(
                                securityContext,
                                () ->
                                        transactionRuleResultService
                                                .getRuleResultsByRuleId(
                                                        ruleId
                                                ),
                                TransactionRuleResultResponse
                                        ::getTransactionId
                        )
        );
    }

    @GetMapping("/rule-results/result/{evaluationResult}")
    public ResponseEntity<List<TransactionRuleResultResponse>>
    getRuleResultsByEvaluationResult(
            @PathVariable String evaluationResult) {

        SecurityContext securityContext =
                securityContextProvider
                        .getCurrentContext();

        return ResponseEntity.ok(
                transactionChildAccessService
                        .filterVisible(
                                securityContext,
                                () ->
                                        transactionRuleResultService
                                                .getRuleResultsByEvaluationResult(
                                                        evaluationResult
                                                ),
                                TransactionRuleResultResponse
                                        ::getTransactionId
                        )
        );
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Void> handleAccessDenied(
            AccessDeniedException exception) {

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .build();
    }
}
