package com.efs.modules.transaction.controller;

import com.efs.modules.transaction.dto.TransactionParticipantRequest;
import com.efs.modules.transaction.dto.TransactionParticipantResponse;
import com.efs.modules.transaction.service.TransactionParticipantServiceInterface;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/transactions")
public class TransactionParticipantController {

    private final TransactionParticipantServiceInterface
            transactionParticipantService;

    public TransactionParticipantController(
            TransactionParticipantServiceInterface
                    transactionParticipantService) {

        this.transactionParticipantService =
                transactionParticipantService;
    }

    @PostMapping("/{transactionId}/participants")
    public ResponseEntity<TransactionParticipantResponse>
    createParticipant(
            @PathVariable UUID transactionId,
            @Valid @RequestBody
            TransactionParticipantRequest request) {

        TransactionParticipantResponse response =
                transactionParticipantService.createParticipant(
                        transactionId,
                        request
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/participants/{participantId}")
    public ResponseEntity<TransactionParticipantResponse>
    getParticipantById(
            @PathVariable UUID participantId) {

        return ResponseEntity.ok(
                transactionParticipantService
                        .getParticipantById(participantId)
        );
    }

    @GetMapping("/{transactionId}/participants")
    public ResponseEntity<List<TransactionParticipantResponse>>
    getParticipantsByTransactionId(
            @PathVariable UUID transactionId) {

        return ResponseEntity.ok(
                transactionParticipantService
                        .getParticipantsByTransactionId(
                                transactionId
                        )
        );
    }

    @GetMapping("/participants/customer/{customerId}")
    public ResponseEntity<List<TransactionParticipantResponse>>
    getParticipantsByCustomerId(
            @PathVariable UUID customerId) {

        return ResponseEntity.ok(
                transactionParticipantService
                        .getParticipantsByCustomerId(
                                customerId
                        )
        );
    }
}