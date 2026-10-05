package com.efs.modules.risk.controller;

import com.efs.modules.risk.dto.EventRiskAssessmentRequest;
import com.efs.modules.risk.dto.EventRiskAssessmentResponse;
import com.efs.modules.risk.service.EventRiskAssessmentServiceInterface;
import com.efs.shared.security.SecurityContext;
import com.efs.shared.security.SecurityContextProvider;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/event-risk-assessments")
public class EventRiskAssessmentController {

    private final EventRiskAssessmentServiceInterface
            eventRiskAssessmentService;

    private final SecurityContextProvider
            securityContextProvider;

    public EventRiskAssessmentController(
            EventRiskAssessmentServiceInterface eventRiskAssessmentService,
            SecurityContextProvider securityContextProvider) {

        this.eventRiskAssessmentService =
                eventRiskAssessmentService;

        this.securityContextProvider =
                securityContextProvider;
    }

    @PostMapping
    public ResponseEntity<EventRiskAssessmentResponse>
    createEventRiskAssessment(
            @Valid @RequestBody
            EventRiskAssessmentRequest request) {

        EventRiskAssessmentResponse response =
                eventRiskAssessmentService
                        .assess(
                                request
                        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/{eventRiskAssessmentId}")
    public ResponseEntity<EventRiskAssessmentResponse>
    getEventRiskAssessmentById(
            @PathVariable
            UUID eventRiskAssessmentId) {

        SecurityContext securityContext =
                securityContextProvider
                        .getCurrentContext();

        try {

            return ResponseEntity.ok(
                    eventRiskAssessmentService
                            .getEventRiskAssessmentById(
                                    eventRiskAssessmentId,
                                    securityContext
                            )
            );
        }
        catch (AccessDeniedException exception) {

            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .build();
        }
    }

    @GetMapping("/fraud-event/{fraudEventId}")
    public ResponseEntity<List<EventRiskAssessmentResponse>>
    getEventRiskAssessmentsByFraudEventId(
            @PathVariable
            UUID fraudEventId) {

        SecurityContext securityContext =
                securityContextProvider
                        .getCurrentContext();

        try {

            return ResponseEntity.ok(
                    eventRiskAssessmentService
                            .getEventRiskAssessmentsByFraudEventId(
                                    fraudEventId,
                                    securityContext
                            )
            );
        }
        catch (AccessDeniedException exception) {

            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .build();
        }
    }
}