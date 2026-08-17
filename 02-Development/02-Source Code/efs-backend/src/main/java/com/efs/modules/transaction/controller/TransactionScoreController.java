package com.efs.modules.transaction.controller;

import com.efs.modules.transaction.dto.TransactionScoreRequest;
import com.efs.modules.transaction.dto.TransactionScoreResponse;
import com.efs.modules.transaction.service.TransactionScoreServiceInterface;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/transactions")
public class TransactionScoreController {

    private final TransactionScoreServiceInterface
            transactionScoreService;

    public TransactionScoreController(
            TransactionScoreServiceInterface transactionScoreService) {

        this.transactionScoreService =
                transactionScoreService;
    }

    @PostMapping("/{transactionId}/scores")
    public ResponseEntity<TransactionScoreResponse> createScore(
            @PathVariable UUID transactionId,
            @Valid @RequestBody TransactionScoreRequest request) {

        TransactionScoreResponse response =
                transactionScoreService.createScore(
                        transactionId,
                        request
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/scores/{scoreId}")
    public ResponseEntity<TransactionScoreResponse> getScoreById(
            @PathVariable UUID scoreId) {

        return ResponseEntity.ok(
                transactionScoreService.getScoreById(scoreId)
        );
    }

    @GetMapping("/{transactionId}/scores")
    public ResponseEntity<List<TransactionScoreResponse>>
    getScoresByTransactionId(
            @PathVariable UUID transactionId) {

        return ResponseEntity.ok(
                transactionScoreService
                        .getScoresByTransactionId(transactionId)
        );
    }

    @GetMapping("/scores/type/{scoreType}")
    public ResponseEntity<List<TransactionScoreResponse>>
    getScoresByType(
            @PathVariable String scoreType) {

        return ResponseEntity.ok(
                transactionScoreService
                        .getScoresByType(scoreType)
        );
    }

    @GetMapping("/scores/model/{scoringModel}")
    public ResponseEntity<List<TransactionScoreResponse>>
    getScoresByScoringModel(
            @PathVariable String scoringModel) {

        return ResponseEntity.ok(
                transactionScoreService
                        .getScoresByScoringModel(scoringModel)
        );
    }
}