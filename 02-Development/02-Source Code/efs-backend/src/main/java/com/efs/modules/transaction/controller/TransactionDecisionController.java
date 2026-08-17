package com.efs.modules.transaction.controller;

import com.efs.modules.transaction.dto.TransactionDecisionRequest;
import com.efs.modules.transaction.dto.TransactionDecisionResponse;
import com.efs.modules.transaction.service.TransactionDecisionServiceInterface;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/transactions")
public class TransactionDecisionController {

    private final TransactionDecisionServiceInterface
            transactionDecisionService;

    public TransactionDecisionController(
            TransactionDecisionServiceInterface transactionDecisionService) {

        this.transactionDecisionService =
                transactionDecisionService;
    }

    @PostMapping("/{transactionId}/decisions")
    public ResponseEntity<TransactionDecisionResponse> createDecision(
            @PathVariable UUID transactionId,
            @Valid @RequestBody TransactionDecisionRequest request) {

        TransactionDecisionResponse response =
                transactionDecisionService.createDecision(
                        transactionId,
                        request
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/decisions/{decisionId}")
    public ResponseEntity<TransactionDecisionResponse> getDecisionById(
            @PathVariable UUID decisionId) {

        return ResponseEntity.ok(
                transactionDecisionService.getDecisionById(decisionId)
        );
    }

    @GetMapping("/{transactionId}/decisions")
    public ResponseEntity<List<TransactionDecisionResponse>>
    getDecisionsByTransactionId(
            @PathVariable UUID transactionId) {

        return ResponseEntity.ok(
                transactionDecisionService
                        .getDecisionsByTransactionId(transactionId)
        );
    }

    @GetMapping("/decisions/type/{decisionType}")
    public ResponseEntity<List<TransactionDecisionResponse>>
    getDecisionsByType(
            @PathVariable String decisionType) {

        return ResponseEntity.ok(
                transactionDecisionService
                        .getDecisionsByType(decisionType)
        );
    }

    @GetMapping("/decisions/source/{decisionSource}")
    public ResponseEntity<List<TransactionDecisionResponse>>
    getDecisionsBySource(
            @PathVariable String decisionSource) {

        return ResponseEntity.ok(
                transactionDecisionService
                        .getDecisionsBySource(decisionSource)
        );
    }

    @GetMapping("/decisions/final/{finalDecision}")
    public ResponseEntity<List<TransactionDecisionResponse>>
    getDecisionsByFinalStatus(
            @PathVariable Boolean finalDecision) {

        return ResponseEntity.ok(
                transactionDecisionService
                        .getDecisionsByFinalStatus(finalDecision)
        );
    }
}