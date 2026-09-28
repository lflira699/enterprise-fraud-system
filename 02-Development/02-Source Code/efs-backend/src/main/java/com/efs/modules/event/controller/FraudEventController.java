package com.efs.modules.event.controller;

import com.efs.modules.event.dto.FraudEventRequest;
import com.efs.modules.event.dto.FraudEventResponse;
import com.efs.modules.event.service.FraudEventServiceInterface;
import com.efs.shared.pagination.PageResponse;
import com.efs.shared.security.SecurityContextProvider;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/events")
public class FraudEventController {

    private final FraudEventServiceInterface fraudEventService;
    private final SecurityContextProvider securityContextProvider;

    public FraudEventController(
            FraudEventServiceInterface fraudEventService,
            SecurityContextProvider securityContextProvider) {

        this.fraudEventService = fraudEventService;
        this.securityContextProvider = securityContextProvider;
    }

    @PostMapping
    public ResponseEntity<FraudEventResponse> registerEvent(
            @Valid @RequestBody FraudEventRequest request) {

        FraudEventResponse response =
                fraudEventService.registerEvent(
                        request,
                        securityContextProvider.getCurrentContext());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/{eventId}")
    public ResponseEntity<FraudEventResponse> getEvent(
            @PathVariable UUID eventId) {

        FraudEventResponse response =
                fraudEventService.getEvent(
                        eventId,
                        securityContextProvider.getCurrentContext());

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<PageResponse<FraudEventResponse>> searchEvents(
            @RequestParam(required = false)
            UUID transactionId,

            @RequestParam(required = false)
            String eventType,

            @RequestParam(required = false)
            String sourceType,

            @RequestParam(required = false)
            String sourceReference,

            @RequestParam(required = false)
            UUID correlationId,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime occurredFrom,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime occurredTo,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime receivedFrom,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime receivedTo,

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "25")
            int size,

            @RequestParam(defaultValue = "occurredAt")
            String sort,

            @RequestParam(defaultValue = "DESC")
            String direction) {

        PageResponse<FraudEventResponse> response =
                fraudEventService.searchEvents(
                        transactionId,
                        eventType,
                        sourceType,
                        sourceReference,
                        correlationId,
                        occurredFrom,
                        occurredTo,
                        receivedFrom,
                        receivedTo,
                        page,
                        size,
                        sort,
                        direction,
                        securityContextProvider.getCurrentContext());

        return ResponseEntity.ok(response);
    }
}