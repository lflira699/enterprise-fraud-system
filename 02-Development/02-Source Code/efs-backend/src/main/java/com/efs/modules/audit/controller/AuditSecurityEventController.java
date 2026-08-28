package com.efs.modules.audit.controller;

import com.efs.modules.audit.dto.AuditSecurityEventRequest;
import com.efs.modules.audit.dto.AuditSecurityEventResponse;
import com.efs.modules.audit.service.AuditSecurityEventServiceInterface;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/audit/security-events")
public class AuditSecurityEventController {

    private final AuditSecurityEventServiceInterface auditSecurityEventService;

    public AuditSecurityEventController(
            AuditSecurityEventServiceInterface auditSecurityEventService) {

        this.auditSecurityEventService =
                auditSecurityEventService;
    }

    @PostMapping
    public ResponseEntity<AuditSecurityEventResponse>
    createAuditSecurityEvent(
            @Valid @RequestBody AuditSecurityEventRequest request) {

        AuditSecurityEventResponse response =
                auditSecurityEventService
                        .createAuditSecurityEvent(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/{securityEventId}")
    public ResponseEntity<AuditSecurityEventResponse>
    getAuditSecurityEventById(
            @PathVariable UUID securityEventId) {

        return ResponseEntity.ok(
                auditSecurityEventService
                        .getAuditSecurityEventById(
                                securityEventId
                        )
        );
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<AuditSecurityEventResponse>>
    getAuditSecurityEventsByUserId(
            @PathVariable UUID userId) {

        return ResponseEntity.ok(
                auditSecurityEventService
                        .getAuditSecurityEventsByUserId(
                                userId
                        )
        );
    }

    @GetMapping("/organization/{organizationId}")
    public ResponseEntity<List<AuditSecurityEventResponse>>
    getAuditSecurityEventsByOrganizationId(
            @PathVariable UUID organizationId) {

        return ResponseEntity.ok(
                auditSecurityEventService
                        .getAuditSecurityEventsByOrganizationId(
                                organizationId
                        )
        );
    }

    @GetMapping("/audit-event/{auditEventId}")
    public ResponseEntity<List<AuditSecurityEventResponse>>
    getAuditSecurityEventsByAuditEventId(
            @PathVariable UUID auditEventId) {

        return ResponseEntity.ok(
                auditSecurityEventService
                        .getAuditSecurityEventsByAuditEventId(
                                auditEventId
                        )
        );
    }

    @GetMapping("/severity/{severity}")
    public ResponseEntity<List<AuditSecurityEventResponse>>
    getAuditSecurityEventsBySeverity(
            @PathVariable String severity) {

        return ResponseEntity.ok(
                auditSecurityEventService
                        .getAuditSecurityEventsBySeverity(
                                severity
                        )
        );
    }

    @GetMapping("/category/{eventCategory}")
    public ResponseEntity<List<AuditSecurityEventResponse>>
    getAuditSecurityEventsByEventCategory(
            @PathVariable String eventCategory) {

        return ResponseEntity.ok(
                auditSecurityEventService
                        .getAuditSecurityEventsByEventCategory(
                                eventCategory
                        )
        );
    }
}