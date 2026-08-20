package com.efs.modules.detection.controller;

import com.efs.modules.detection.dto.TimelineEventRequest;
import com.efs.modules.detection.dto.TimelineEventResponse;
import com.efs.modules.detection.service.TimelineEventServiceInterface;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/detection/timeline-events")
public class TimelineEventController {

    private final TimelineEventServiceInterface timelineEventService;

    public TimelineEventController(
            TimelineEventServiceInterface timelineEventService) {

        this.timelineEventService = timelineEventService;
    }

    @PostMapping
    public ResponseEntity<TimelineEventResponse>
    createTimelineEvent(
            @Valid @RequestBody TimelineEventRequest request) {

        TimelineEventResponse response =
                timelineEventService.createTimelineEvent(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/{timelineEventId}")
    public ResponseEntity<TimelineEventResponse>
    getTimelineEventById(
            @PathVariable UUID timelineEventId) {

        return ResponseEntity.ok(
                timelineEventService.getTimelineEventById(
                        timelineEventId
                )
        );
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<TimelineEventResponse>>
    getEventsByCustomer(
            @PathVariable UUID customerId) {

        return ResponseEntity.ok(
                timelineEventService.getEventsByCustomer(
                        customerId
                )
        );
    }

    @GetMapping("/transaction/{transactionId}")
    public ResponseEntity<List<TimelineEventResponse>>
    getEventsByTransaction(
            @PathVariable UUID transactionId) {

        return ResponseEntity.ok(
                timelineEventService.getEventsByTransaction(
                        transactionId
                )
        );
    }

    @GetMapping("/correlation/{correlationId}")
    public ResponseEntity<List<TimelineEventResponse>>
    getEventsByCorrelation(
            @PathVariable UUID correlationId) {

        return ResponseEntity.ok(
                timelineEventService.getEventsByCorrelation(
                        correlationId
                )
        );
    }

    @GetMapping("/type/{eventType}")
    public ResponseEntity<List<TimelineEventResponse>>
    getEventsByType(
            @PathVariable String eventType) {

        return ResponseEntity.ok(
                timelineEventService.getEventsByType(eventType)
        );
    }

    @GetMapping("/source/{eventSource}")
    public ResponseEntity<List<TimelineEventResponse>>
    getEventsBySource(
            @PathVariable String eventSource) {

        return ResponseEntity.ok(
                timelineEventService.getEventsBySource(eventSource)
        );
    }
}