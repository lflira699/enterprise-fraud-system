package com.efs.modules.detection.controller;

import com.efs.modules.detection.dto.CorrelationEventRequest;
import com.efs.modules.detection.dto.CorrelationEventResponse;
import com.efs.modules.detection.service.CorrelationEventServiceInterface;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/detection/correlation-events")
public class CorrelationEventController {

    private final CorrelationEventServiceInterface correlationEventService;

    public CorrelationEventController(
            CorrelationEventServiceInterface correlationEventService) {

        this.correlationEventService = correlationEventService;
    }

    @PostMapping
    public ResponseEntity<CorrelationEventResponse>
    createCorrelationEvent(
            @Valid @RequestBody CorrelationEventRequest request) {

        CorrelationEventResponse response =
                correlationEventService.createCorrelationEvent(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/{correlationEventId}")
    public ResponseEntity<CorrelationEventResponse>
    getCorrelationEventById(
            @PathVariable UUID correlationEventId) {

        return ResponseEntity.ok(
                correlationEventService.getCorrelationEventById(
                        correlationEventId
                )
        );
    }

    @GetMapping("/correlation/{correlationId}")
    public ResponseEntity<List<CorrelationEventResponse>>
    getEventsByCorrelation(
            @PathVariable UUID correlationId) {

        return ResponseEntity.ok(
                correlationEventService.getEventsByCorrelation(
                        correlationId
                )
        );
    }

    @GetMapping("/event/{eventId}")
    public ResponseEntity<List<CorrelationEventResponse>>
    getCorrelationsByEvent(
            @PathVariable UUID eventId) {

        return ResponseEntity.ok(
                correlationEventService.getCorrelationsByEvent(
                        eventId
                )
        );
    }

    @GetMapping("/role/{eventRole}")
    public ResponseEntity<List<CorrelationEventResponse>>
    getEventsByRole(
            @PathVariable String eventRole) {

        return ResponseEntity.ok(
                correlationEventService.getEventsByRole(
                        eventRole
                )
        );
    }
}