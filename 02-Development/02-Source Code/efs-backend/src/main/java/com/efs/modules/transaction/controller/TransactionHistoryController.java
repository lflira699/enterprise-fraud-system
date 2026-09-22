package com.efs.modules.transaction.controller;

import com.efs.modules.administration.dto.UserAccountReference;
import com.efs.modules.transaction.dto.TransactionHistoryRequest;
import com.efs.modules.transaction.dto.TransactionHistoryResponse;
import com.efs.modules.transaction.service.TransactionChildAccessServiceInterface;
import com.efs.modules.transaction.service.TransactionHistoryServiceInterface;
import com.efs.modules.transaction.service.TransactionScopeAuthorizationServiceInterface;
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
public class TransactionHistoryController {

    private final TransactionHistoryServiceInterface
            transactionHistoryService;

    private final TransactionChildAccessServiceInterface
            transactionChildAccessService;

    private final TransactionScopeAuthorizationServiceInterface
            transactionScopeAuthorizationService;

    private final SecurityContextProvider
            securityContextProvider;

    public TransactionHistoryController(
            TransactionHistoryServiceInterface transactionHistoryService,
            TransactionChildAccessServiceInterface
                    transactionChildAccessService,
            TransactionScopeAuthorizationServiceInterface
                    transactionScopeAuthorizationService,
            SecurityContextProvider securityContextProvider) {

        this.transactionHistoryService =
                transactionHistoryService;

        this.transactionChildAccessService =
                transactionChildAccessService;

        this.transactionScopeAuthorizationService =
                transactionScopeAuthorizationService;

        this.securityContextProvider =
                securityContextProvider;
    }

    @PostMapping("/{transactionId}/history")
    public ResponseEntity<TransactionHistoryResponse> createHistory(
            @PathVariable UUID transactionId,
            @Valid @RequestBody TransactionHistoryRequest request) {

        SecurityContext securityContext =
                securityContextProvider
                        .getCurrentContext();

        TransactionHistoryResponse response =
                transactionChildAccessService
                        .create(
                                securityContext,
                                transactionId,
                                request,
                                transactionHistoryService::createHistory
                        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/history/{historyId}")
    public ResponseEntity<TransactionHistoryResponse> getHistoryById(
            @PathVariable UUID historyId) {

        SecurityContext securityContext =
                securityContextProvider
                        .getCurrentContext();

        return ResponseEntity.ok(
                transactionChildAccessService
                        .getById(
                                securityContext,
                                historyId,
                                transactionHistoryService::getHistoryById,
                                TransactionHistoryResponse::getTransactionId,
                                "Transaction history not found: "
                                        + historyId
                        )
        );
    }

    @GetMapping("/{transactionId}/history")
    public ResponseEntity<List<TransactionHistoryResponse>>
    getHistoryByTransactionId(
            @PathVariable UUID transactionId) {

        SecurityContext securityContext =
                securityContextProvider
                        .getCurrentContext();

        return ResponseEntity.ok(
                transactionChildAccessService
                        .getByTransactionId(
                                securityContext,
                                transactionId,
                                transactionHistoryService
                                        ::getHistoryByTransactionId
                        )
        );
    }

    @GetMapping("/{transactionId}/history/version/{versionNumber}")
    public ResponseEntity<TransactionHistoryResponse>
    getHistoryByTransactionIdAndVersionNumber(
            @PathVariable UUID transactionId,
            @PathVariable Integer versionNumber) {

        SecurityContext securityContext =
                securityContextProvider
                        .getCurrentContext();

        UserAccountReference actor =
                transactionScopeAuthorizationService
                        .authorize(
                                securityContext,
                                "transaction.view"
                        );

        String notFoundMessage =
                "Transaction history not found for transaction "
                        + transactionId
                        + " and version "
                        + versionNumber;

        transactionScopeAuthorizationService
                .requireVisibleTransaction(
                        transactionId,
                        actor,
                        notFoundMessage
                );

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

        SecurityContext securityContext =
                securityContextProvider
                        .getCurrentContext();

        return ResponseEntity.ok(
                transactionChildAccessService
                        .filterVisible(
                                securityContext,
                                () ->
                                        transactionHistoryService
                                                .getHistoryByChangedBy(
                                                        changedBy
                                                ),
                                TransactionHistoryResponse
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
