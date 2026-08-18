package com.efs.modules.transaction.controller;

import com.efs.modules.transaction.dto.TransactionStatusHistoryRequest;
import com.efs.modules.transaction.dto.TransactionStatusHistoryResponse;
import com.efs.modules.transaction.service.TransactionStatusHistoryServiceInterface;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/transactions")
public class TransactionStatusHistoryController {

    private final TransactionStatusHistoryServiceInterface transactionStatusHistoryService;

    public TransactionStatusHistoryController(
            TransactionStatusHistoryServiceInterface transactionStatusHistoryService) {

        this.transactionStatusHistoryService = transactionStatusHistoryService;
    }

    @PostMapping("/{transactionId}/status-history")
    public ResponseEntity<TransactionStatusHistoryResponse> createStatusHistory(
            @PathVariable UUID transactionId,
            @Valid @RequestBody TransactionStatusHistoryRequest request) {

        TransactionStatusHistoryResponse response =
                transactionStatusHistoryService.createStatusHistory(
                        transactionId,
                        request
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/status-history/{historyId}")
    public ResponseEntity<TransactionStatusHistoryResponse> getStatusHistoryById(
            @PathVariable UUID historyId) {

        return ResponseEntity.ok(
                transactionStatusHistoryService
                        .getStatusHistoryById(historyId)
        );
    }

    @GetMapping("/{transactionId}/status-history")
    public ResponseEntity<List<TransactionStatusHistoryResponse>>
    getStatusHistoryByTransactionId(
            @PathVariable UUID transactionId) {

        return ResponseEntity.ok(
                transactionStatusHistoryService
                        .getStatusHistoryByTransactionId(transactionId)
        );
    }

    @GetMapping("/status-history/status/{currentStatus}")
    public ResponseEntity<List<TransactionStatusHistoryResponse>>
    getStatusHistoryByCurrentStatus(
            @PathVariable String currentStatus) {

        return ResponseEntity.ok(
                transactionStatusHistoryService
                        .getStatusHistoryByCurrentStatus(currentStatus)
        );
    }

    @GetMapping("/status-history/changed-by/{changedBy}")
    public ResponseEntity<List<TransactionStatusHistoryResponse>>
    getStatusHistoryByChangedBy(
            @PathVariable UUID changedBy) {

        return ResponseEntity.ok(
                transactionStatusHistoryService
                        .getStatusHistoryByChangedBy(changedBy)
        );
    }
}