package com.efs.modules.transaction.controller;

import com.efs.modules.transaction.dto.TransactionScoreRequest;
import com.efs.modules.transaction.dto.TransactionScoreResponse;
import com.efs.modules.transaction.service.TransactionChildAccessServiceInterface;
import com.efs.modules.transaction.service.TransactionScoreServiceInterface;
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
public class TransactionScoreController {

    private final TransactionScoreServiceInterface
            transactionScoreService;

    private final TransactionChildAccessServiceInterface
            transactionChildAccessService;

    private final SecurityContextProvider
            securityContextProvider;

    public TransactionScoreController(
            TransactionScoreServiceInterface transactionScoreService,
            TransactionChildAccessServiceInterface
                    transactionChildAccessService,
            SecurityContextProvider securityContextProvider) {

        this.transactionScoreService =
                transactionScoreService;

        this.transactionChildAccessService =
                transactionChildAccessService;

        this.securityContextProvider =
                securityContextProvider;
    }

    @PostMapping("/{transactionId}/scores")
    public ResponseEntity<TransactionScoreResponse> createScore(
            @PathVariable UUID transactionId,
            @Valid @RequestBody TransactionScoreRequest request) {

        SecurityContext securityContext =
                securityContextProvider
                        .getCurrentContext();

        TransactionScoreResponse response =
                transactionChildAccessService
                        .create(
                                securityContext,
                                transactionId,
                                request,
                                transactionScoreService
                                        ::createScore
                        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/scores/{scoreId}")
    public ResponseEntity<TransactionScoreResponse> getScoreById(
            @PathVariable UUID scoreId) {

        SecurityContext securityContext =
                securityContextProvider
                        .getCurrentContext();

        return ResponseEntity.ok(
                transactionChildAccessService
                        .getById(
                                securityContext,
                                scoreId,
                                transactionScoreService
                                        ::getScoreById,
                                TransactionScoreResponse
                                        ::getTransactionId,
                                "Transaction score not found: "
                                        + scoreId
                        )
        );
    }

    @GetMapping("/{transactionId}/scores")
    public ResponseEntity<List<TransactionScoreResponse>>
    getScoresByTransactionId(
            @PathVariable UUID transactionId) {

        SecurityContext securityContext =
                securityContextProvider
                        .getCurrentContext();

        return ResponseEntity.ok(
                transactionChildAccessService
                        .getByTransactionId(
                                securityContext,
                                transactionId,
                                transactionScoreService
                                        ::getScoresByTransactionId
                        )
        );
    }

    @GetMapping("/scores/type/{scoreType}")
    public ResponseEntity<List<TransactionScoreResponse>>
    getScoresByType(
            @PathVariable String scoreType) {

        SecurityContext securityContext =
                securityContextProvider
                        .getCurrentContext();

        return ResponseEntity.ok(
                transactionChildAccessService
                        .filterVisible(
                                securityContext,
                                () ->
                                        transactionScoreService
                                                .getScoresByType(
                                                        scoreType
                                                ),
                                TransactionScoreResponse
                                        ::getTransactionId
                        )
        );
    }

    @GetMapping("/scores/model/{scoringModel}")
    public ResponseEntity<List<TransactionScoreResponse>>
    getScoresByScoringModel(
            @PathVariable String scoringModel) {

        SecurityContext securityContext =
                securityContextProvider
                        .getCurrentContext();

        return ResponseEntity.ok(
                transactionChildAccessService
                        .filterVisible(
                                securityContext,
                                () ->
                                        transactionScoreService
                                                .getScoresByScoringModel(
                                                        scoringModel
                                                ),
                                TransactionScoreResponse
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
