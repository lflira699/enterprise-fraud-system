package com.efs.modules.transaction.controller;

import com.efs.modules.transaction.dto.TransactionParticipantRequest;
import com.efs.modules.transaction.dto.TransactionParticipantResponse;
import com.efs.modules.transaction.service.TransactionChildAccessServiceInterface;
import com.efs.modules.transaction.service.TransactionParticipantServiceInterface;
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
public class TransactionParticipantController {

    private final TransactionParticipantServiceInterface
            transactionParticipantService;

    private final TransactionChildAccessServiceInterface
            transactionChildAccessService;

    private final SecurityContextProvider
            securityContextProvider;

    public TransactionParticipantController(
            TransactionParticipantServiceInterface
                    transactionParticipantService,
            TransactionChildAccessServiceInterface
                    transactionChildAccessService,
            SecurityContextProvider securityContextProvider) {

        this.transactionParticipantService =
                transactionParticipantService;

        this.transactionChildAccessService =
                transactionChildAccessService;

        this.securityContextProvider =
                securityContextProvider;
    }

    @PostMapping("/{transactionId}/participants")
    public ResponseEntity<TransactionParticipantResponse>
    createParticipant(
            @PathVariable UUID transactionId,
            @Valid @RequestBody
            TransactionParticipantRequest request) {

        SecurityContext securityContext =
                securityContextProvider
                        .getCurrentContext();

        TransactionParticipantResponse response =
                transactionChildAccessService
                        .create(
                                securityContext,
                                transactionId,
                                request,
                                transactionParticipantService
                                        ::createParticipant
                        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/participants/{participantId}")
    public ResponseEntity<TransactionParticipantResponse>
    getParticipantById(
            @PathVariable UUID participantId) {

        SecurityContext securityContext =
                securityContextProvider
                        .getCurrentContext();

        return ResponseEntity.ok(
                transactionChildAccessService
                        .getById(
                                securityContext,
                                participantId,
                                transactionParticipantService
                                        ::getParticipantById,
                                TransactionParticipantResponse
                                        ::getTransactionId,
                                "Transaction participant not found: "
                                        + participantId
                        )
        );
    }

    @GetMapping("/{transactionId}/participants")
    public ResponseEntity<List<TransactionParticipantResponse>>
    getParticipantsByTransactionId(
            @PathVariable UUID transactionId) {

        SecurityContext securityContext =
                securityContextProvider
                        .getCurrentContext();

        return ResponseEntity.ok(
                transactionChildAccessService
                        .getByTransactionId(
                                securityContext,
                                transactionId,
                                transactionParticipantService
                                        ::getParticipantsByTransactionId
                        )
        );
    }

    @GetMapping("/participants/customer/{customerId}")
    public ResponseEntity<List<TransactionParticipantResponse>>
    getParticipantsByCustomerId(
            @PathVariable UUID customerId) {

        SecurityContext securityContext =
                securityContextProvider
                        .getCurrentContext();

        return ResponseEntity.ok(
                transactionChildAccessService
                        .filterVisible(
                                securityContext,
                                () ->
                                        transactionParticipantService
                                                .getParticipantsByCustomerId(
                                                        customerId
                                                ),
                                TransactionParticipantResponse
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
