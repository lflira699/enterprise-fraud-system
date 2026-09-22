package com.efs.modules.transaction.controller;

import com.efs.modules.transaction.dto.TransactionEventRequest;
import com.efs.modules.transaction.dto.TransactionEventResponse;
import com.efs.modules.transaction.service.TransactionChildAccessServiceInterface;
import com.efs.modules.transaction.service.TransactionEventServiceInterface;
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
public class TransactionEventController {

    private final TransactionEventServiceInterface
            transactionEventService;

    private final TransactionChildAccessServiceInterface
            transactionChildAccessService;

    private final SecurityContextProvider
            securityContextProvider;

    public TransactionEventController(
            TransactionEventServiceInterface transactionEventService,
            TransactionChildAccessServiceInterface
                    transactionChildAccessService,
            SecurityContextProvider securityContextProvider) {

        this.transactionEventService =
                transactionEventService;

        this.transactionChildAccessService =
                transactionChildAccessService;

        this.securityContextProvider =
                securityContextProvider;
    }

    @PostMapping("/{transactionId}/events")
    public ResponseEntity<TransactionEventResponse> createEvent(
            @PathVariable UUID transactionId,
            @Valid @RequestBody TransactionEventRequest request) {

        SecurityContext securityContext =
                securityContextProvider
                        .getCurrentContext();

        TransactionEventResponse response =
                transactionChildAccessService
                        .create(
                                securityContext,
                                transactionId,
                                request,
                                transactionEventService::createEvent
                        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/events/{eventId}")
    public ResponseEntity<TransactionEventResponse> getEventById(
            @PathVariable UUID eventId) {

        SecurityContext securityContext =
                securityContextProvider
                        .getCurrentContext();

        return ResponseEntity.ok(
                transactionChildAccessService
                        .getById(
                                securityContext,
                                eventId,
                                transactionEventService::getEventById,
                                TransactionEventResponse::getTransactionId,
                                "Transaction event not found: "
                                        + eventId
                        )
        );
    }

    @GetMapping("/{transactionId}/events")
    public ResponseEntity<List<TransactionEventResponse>>
    getEventsByTransactionId(
            @PathVariable UUID transactionId) {

        SecurityContext securityContext =
                securityContextProvider
                        .getCurrentContext();

        return ResponseEntity.ok(
                transactionChildAccessService
                        .getByTransactionId(
                                securityContext,
                                transactionId,
                                transactionEventService
                                        ::getEventsByTransactionId
                        )
        );
    }

    @GetMapping("/events/type/{eventType}")
    public ResponseEntity<List<TransactionEventResponse>>
    getEventsByType(
            @PathVariable String eventType) {

        SecurityContext securityContext =
                securityContextProvider
                        .getCurrentContext();

        return ResponseEntity.ok(
                transactionChildAccessService
                        .filterVisible(
                                securityContext,
                                () ->
                                        transactionEventService
                                                .getEventsByType(
                                                        eventType
                                                ),
                                TransactionEventResponse
                                        ::getTransactionId
                        )
        );
    }

    @GetMapping("/events/component/{componentName}")
    public ResponseEntity<List<TransactionEventResponse>>
    getEventsByComponentName(
            @PathVariable String componentName) {

        SecurityContext securityContext =
                securityContextProvider
                        .getCurrentContext();

        return ResponseEntity.ok(
                transactionChildAccessService
                        .filterVisible(
                                securityContext,
                                () ->
                                        transactionEventService
                                                .getEventsByComponentName(
                                                        componentName
                                                ),
                                TransactionEventResponse
                                        ::getTransactionId
                        )
        );
    }

    @GetMapping("/events/correlation/{correlationId}")
    public ResponseEntity<List<TransactionEventResponse>>
    getEventsByCorrelationId(
            @PathVariable UUID correlationId) {

        SecurityContext securityContext =
                securityContextProvider
                        .getCurrentContext();

        return ResponseEntity.ok(
                transactionChildAccessService
                        .filterVisible(
                                securityContext,
                                () ->
                                        transactionEventService
                                                .getEventsByCorrelationId(
                                                        correlationId
                                                ),
                                TransactionEventResponse
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
