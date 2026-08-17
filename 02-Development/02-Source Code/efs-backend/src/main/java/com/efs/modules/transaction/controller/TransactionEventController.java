package com.efs.modules.transaction.controller;

import com.efs.modules.transaction.dto.TransactionEventRequest;
import com.efs.modules.transaction.dto.TransactionEventResponse;
import com.efs.modules.transaction.service.TransactionEventServiceInterface;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/transactions")
public class TransactionEventController {

    private final TransactionEventServiceInterface transactionEventService;

    public TransactionEventController(
            TransactionEventServiceInterface transactionEventService) {

        this.transactionEventService = transactionEventService;
    }

    @PostMapping("/{transactionId}/events")
    public ResponseEntity<TransactionEventResponse> createEvent(
            @PathVariable UUID transactionId,
            @Valid @RequestBody TransactionEventRequest request) {

        TransactionEventResponse response =
                transactionEventService.createEvent(
                        transactionId,
                        request
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/events/{eventId}")
    public ResponseEntity<TransactionEventResponse> getEventById(
            @PathVariable UUID eventId) {

        return ResponseEntity.ok(
                transactionEventService.getEventById(eventId)
        );
    }

    @GetMapping("/{transactionId}/events")
    public ResponseEntity<List<TransactionEventResponse>>
    getEventsByTransactionId(
            @PathVariable UUID transactionId) {

        return ResponseEntity.ok(
                transactionEventService
                        .getEventsByTransactionId(transactionId)
        );
    }

    @GetMapping("/events/type/{eventType}")
    public ResponseEntity<List<TransactionEventResponse>>
    getEventsByType(
            @PathVariable String eventType) {

        return ResponseEntity.ok(
                transactionEventService.getEventsByType(eventType)
        );
    }

    @GetMapping("/events/component/{componentName}")
    public ResponseEntity<List<TransactionEventResponse>>
    getEventsByComponentName(
            @PathVariable String componentName) {

        return ResponseEntity.ok(
                transactionEventService
                        .getEventsByComponentName(componentName)
        );
    }

    @GetMapping("/events/correlation/{correlationId}")
    public ResponseEntity<List<TransactionEventResponse>>
    getEventsByCorrelationId(
            @PathVariable UUID correlationId) {

        return ResponseEntity.ok(
                transactionEventService
                        .getEventsByCorrelationId(correlationId)
        );
    }
}