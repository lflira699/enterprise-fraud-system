package com.efs.modules.transaction.controller;

import com.efs.modules.transaction.dto.TransactionStatusHistoryRequest;
import com.efs.modules.transaction.dto.TransactionStatusHistoryResponse;
import com.efs.modules.transaction.service.TransactionChildAccessServiceInterface;
import com.efs.modules.transaction.service.TransactionStatusHistoryServiceInterface;
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
public class TransactionStatusHistoryController {

    private final TransactionStatusHistoryServiceInterface
            transactionStatusHistoryService;

    private final TransactionChildAccessServiceInterface
            transactionChildAccessService;

    private final SecurityContextProvider
            securityContextProvider;

    public TransactionStatusHistoryController(
            TransactionStatusHistoryServiceInterface
                    transactionStatusHistoryService,
            TransactionChildAccessServiceInterface
                    transactionChildAccessService,
            SecurityContextProvider securityContextProvider) {

        this.transactionStatusHistoryService =
                transactionStatusHistoryService;

        this.transactionChildAccessService =
                transactionChildAccessService;

        this.securityContextProvider =
                securityContextProvider;
    }

    @PostMapping("/{transactionId}/status-history")
    public ResponseEntity<TransactionStatusHistoryResponse>
    createStatusHistory(
            @PathVariable UUID transactionId,
            @Valid @RequestBody TransactionStatusHistoryRequest request) {

        SecurityContext securityContext =
                securityContextProvider
                        .getCurrentContext();

        TransactionStatusHistoryResponse response =
                transactionChildAccessService
                        .create(
                                securityContext,
                                transactionId,
                                request,
                                transactionStatusHistoryService
                                        ::createStatusHistory
                        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/status-history/{historyId}")
    public ResponseEntity<TransactionStatusHistoryResponse>
    getStatusHistoryById(
            @PathVariable UUID historyId) {

        SecurityContext securityContext =
                securityContextProvider
                        .getCurrentContext();

        return ResponseEntity.ok(
                transactionChildAccessService
                        .getById(
                                securityContext,
                                historyId,
                                transactionStatusHistoryService
                                        ::getStatusHistoryById,
                                TransactionStatusHistoryResponse
                                        ::getTransactionId,
                                "Transaction status history not found: "
                                        + historyId
                        )
        );
    }

    @GetMapping("/{transactionId}/status-history")
    public ResponseEntity<List<TransactionStatusHistoryResponse>>
    getStatusHistoryByTransactionId(
            @PathVariable UUID transactionId) {

        SecurityContext securityContext =
                securityContextProvider
                        .getCurrentContext();

        return ResponseEntity.ok(
                transactionChildAccessService
                        .getByTransactionId(
                                securityContext,
                                transactionId,
                                transactionStatusHistoryService
                                        ::getStatusHistoryByTransactionId
                        )
        );
    }

    @GetMapping("/status-history/status/{currentStatus}")
    public ResponseEntity<List<TransactionStatusHistoryResponse>>
    getStatusHistoryByCurrentStatus(
            @PathVariable String currentStatus) {

        SecurityContext securityContext =
                securityContextProvider
                        .getCurrentContext();

        return ResponseEntity.ok(
                transactionChildAccessService
                        .filterVisible(
                                securityContext,
                                () ->
                                        transactionStatusHistoryService
                                                .getStatusHistoryByCurrentStatus(
                                                        currentStatus
                                                ),
                                TransactionStatusHistoryResponse
                                        ::getTransactionId
                        )
        );
    }

    @GetMapping("/status-history/changed-by/{changedBy}")
    public ResponseEntity<List<TransactionStatusHistoryResponse>>
    getStatusHistoryByChangedBy(
            @PathVariable UUID changedBy) {

        SecurityContext securityContext =
                securityContextProvider
                        .getCurrentContext();

        return ResponseEntity.ok(
                transactionChildAccessService
                        .filterVisible(
                                securityContext,
                                () ->
                                        transactionStatusHistoryService
                                                .getStatusHistoryByChangedBy(
                                                        changedBy
                                                ),
                                TransactionStatusHistoryResponse
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
