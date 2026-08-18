package com.efs.modules.transaction.controller;

import com.efs.modules.transaction.dto.TransactionHistoryRequest;
import com.efs.modules.transaction.dto.TransactionHistoryResponse;
import com.efs.modules.transaction.service.TransactionHistoryServiceInterface;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/transactions")
public class TransactionHistoryController {

    private final TransactionHistoryServiceInterface transactionHistoryService;

    public TransactionHistoryController(
            TransactionHistoryServiceInterface transactionHistoryService) {

        this.transactionHistoryService = transactionHistoryService;
    }

    @PostMapping("/{transactionId}/history")
    public ResponseEntity<TransactionHistoryResponse> createHistory(
            @PathVariable UUID transactionId,
            @Valid @RequestBody TransactionHistoryRequest request) {

        TransactionHistoryResponse response =
                transactionHistoryService.createHistory(
                        transactionId,
                        request
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/history/{historyId}")
    public ResponseEntity<TransactionHistoryResponse> getHistoryById(
            @PathVariable UUID historyId) {

        return ResponseEntity.ok(
                transactionHistoryService
                        .getHistoryById(historyId)
        );
    }

    @GetMapping("/{transactionId}/history")
    public ResponseEntity<List<TransactionHistoryResponse>>
    getHistoryByTransactionId(
            @PathVariable UUID transactionId) {

        return ResponseEntity.ok(
                transactionHistoryService
                        .getHistoryByTransactionId(transactionId)
        );
    }

    @GetMapping("/{transactionId}/history/version/{versionNumber}")
    public ResponseEntity<TransactionHistoryResponse>
    getHistoryByTransactionIdAndVersionNumber(
            @PathVariable UUID transactionId,
            @PathVariable Integer versionNumber) {

        return ResponseEntity.ok(
                transactionHistoryService
                        .getHistoryByTransactionIdAndVersionNumber(
                                transactionId,
                                versionNumber
                        )
        );
    }

    @GetMapping("/history/changed-by/{changedBy}")
    public ResponseEntity<List<TransactionHistoryResponse>>
    getHistoryByChangedBy(
            @PathVariable UUID changedBy) {

        return ResponseEntity.ok(
                transactionHistoryService
                        .getHistoryByChangedBy(changedBy)
        );
    }
}