package com.efs.modules.audit.controller;

import com.efs.modules.audit.dto.AuditEventRequest;
import com.efs.modules.audit.dto.AuditEventResponse;
import com.efs.modules.audit.service.AuditEventServiceInterface;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/audit/events")
public class AuditEventController {

    private final AuditEventServiceInterface auditEventService;

    public AuditEventController(
            AuditEventServiceInterface auditEventService) {

        this.auditEventService = auditEventService;
    }

    @PostMapping
    public ResponseEntity<AuditEventResponse> createAuditEvent(
            @Valid @RequestBody AuditEventRequest request) {

        AuditEventResponse response =
                auditEventService.createAuditEvent(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/{auditEventId}")
    public ResponseEntity<AuditEventResponse> getAuditEventById(
            @PathVariable UUID auditEventId) {

        return ResponseEntity.ok(
                auditEventService.getAuditEventById(
                        auditEventId
                )
        );
    }

    @GetMapping("/type/{eventType}")
    public ResponseEntity<List<AuditEventResponse>>
    getAuditEventsByEventType(
            @PathVariable String eventType) {

        return ResponseEntity.ok(
                auditEventService.getAuditEventsByEventType(
                        eventType
                )
        );
    }

    @GetMapping("/entity/{entityType}/{entityId}")
    public ResponseEntity<List<AuditEventResponse>>
    getAuditEventsByEntity(
            @PathVariable String entityType,
            @PathVariable UUID entityId) {

        return ResponseEntity.ok(
                auditEventService.getAuditEventsByEntity(
                        entityType,
                        entityId
                )
        );
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<AuditEventResponse>>
    getAuditEventsByUserId(
            @PathVariable UUID userId) {

        return ResponseEntity.ok(
                auditEventService.getAuditEventsByUserId(
                        userId
                )
        );
    }

    @GetMapping("/organization/{organizationId}")
    public ResponseEntity<List<AuditEventResponse>>
    getAuditEventsByOrganizationId(
            @PathVariable UUID organizationId) {

        return ResponseEntity.ok(
                auditEventService.getAuditEventsByOrganizationId(
                        organizationId
                )
        );
    }

    @GetMapping("/correlation/{correlationId}")
    public ResponseEntity<List<AuditEventResponse>>
    getAuditEventsByCorrelationId(
            @PathVariable UUID correlationId) {

        return ResponseEntity.ok(
                auditEventService.getAuditEventsByCorrelationId(
                        correlationId
                )
        );
    }
}