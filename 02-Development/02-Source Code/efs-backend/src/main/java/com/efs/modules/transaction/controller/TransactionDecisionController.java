package com.efs.modules.transaction.controller;

import com.efs.modules.transaction.dto.TransactionDecisionRequest;
import com.efs.modules.transaction.dto.TransactionDecisionResponse;
import com.efs.modules.transaction.service.TransactionChildAccessServiceInterface;
import com.efs.modules.transaction.service.TransactionDecisionServiceInterface;
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
public class TransactionDecisionController {

    private final TransactionDecisionServiceInterface
            transactionDecisionService;

    private final TransactionChildAccessServiceInterface
            transactionChildAccessService;

    private final SecurityContextProvider
            securityContextProvider;

    public TransactionDecisionController(
            TransactionDecisionServiceInterface transactionDecisionService,
            TransactionChildAccessServiceInterface
                    transactionChildAccessService,
            SecurityContextProvider securityContextProvider) {

        this.transactionDecisionService =
                transactionDecisionService;

        this.transactionChildAccessService =
                transactionChildAccessService;

        this.securityContextProvider =
                securityContextProvider;
    }

    @PostMapping("/{transactionId}/decisions")
    public ResponseEntity<TransactionDecisionResponse> createDecision(
            @PathVariable UUID transactionId,
            @Valid @RequestBody TransactionDecisionRequest request) {

        SecurityContext securityContext =
                securityContextProvider
                        .getCurrentContext();

        TransactionDecisionResponse response =
                transactionChildAccessService
                        .create(
                                securityContext,
                                transactionId,
                                request,
                                transactionDecisionService::createDecision
                        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/decisions/{decisionId}")
    public ResponseEntity<TransactionDecisionResponse> getDecisionById(
            @PathVariable UUID decisionId) {

        SecurityContext securityContext =
                securityContextProvider
                        .getCurrentContext();

        return ResponseEntity.ok(
                transactionChildAccessService
                        .getById(
                                securityContext,
                                decisionId,
                                transactionDecisionService::getDecisionById,
                                TransactionDecisionResponse::getTransactionId,
                                "Transaction decision not found: "
                                        + decisionId
                        )
        );
    }

    @GetMapping("/{transactionId}/decisions")
    public ResponseEntity<List<TransactionDecisionResponse>>
    getDecisionsByTransactionId(
            @PathVariable UUID transactionId) {

        SecurityContext securityContext =
                securityContextProvider
                        .getCurrentContext();

        return ResponseEntity.ok(
                transactionChildAccessService
                        .getByTransactionId(
                                securityContext,
                                transactionId,
                                transactionDecisionService
                                        ::getDecisionsByTransactionId
                        )
        );
    }

    @GetMapping("/decisions/type/{decisionType}")
    public ResponseEntity<List<TransactionDecisionResponse>>
    getDecisionsByType(
            @PathVariable String decisionType) {

        SecurityContext securityContext =
                securityContextProvider
                        .getCurrentContext();

        return ResponseEntity.ok(
                transactionChildAccessService
                        .filterVisible(
                                securityContext,
                                () ->
                                        transactionDecisionService
                                                .getDecisionsByType(
                                                        decisionType
                                                ),
                                TransactionDecisionResponse
                                        ::getTransactionId
                        )
        );
    }

    @GetMapping("/decisions/source/{decisionSource}")
    public ResponseEntity<List<TransactionDecisionResponse>>
    getDecisionsBySource(
            @PathVariable String decisionSource) {

        SecurityContext securityContext =
                securityContextProvider
                        .getCurrentContext();

        return ResponseEntity.ok(
                transactionChildAccessService
                        .filterVisible(
                                securityContext,
                                () ->
                                        transactionDecisionService
                                                .getDecisionsBySource(
                                                        decisionSource
                                                ),
                                TransactionDecisionResponse
                                        ::getTransactionId
                        )
        );
    }

    @GetMapping("/decisions/final/{finalDecision}")
    public ResponseEntity<List<TransactionDecisionResponse>>
    getDecisionsByFinalStatus(
            @PathVariable Boolean finalDecision) {

        SecurityContext securityContext =
                securityContextProvider
                        .getCurrentContext();

        return ResponseEntity.ok(
                transactionChildAccessService
                        .filterVisible(
                                securityContext,
                                () ->
                                        transactionDecisionService
                                                .getDecisionsByFinalStatus(
                                                        finalDecision
                                                ),
                                TransactionDecisionResponse
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
