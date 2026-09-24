package com.efs.modules.audit.controller;

import com.efs.modules.audit.dto.AuditEventRequest;
import com.efs.modules.audit.dto.AuditEventResponse;
import com.efs.modules.audit.service.AuditEventServiceInterface;
import com.efs.modules.audit.service.AuditLogReviewServiceInterface;
import com.efs.shared.pagination.PageResponse;
import com.efs.shared.security.SecurityContext;
import com.efs.shared.security.SecurityContextProvider;
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
    private final AuditLogReviewServiceInterface
            auditLogReviewService;
    private final SecurityContextProvider
            securityContextProvider;

    public AuditEventController(
            AuditEventServiceInterface auditEventService,
            AuditLogReviewServiceInterface auditLogReviewService,
            SecurityContextProvider securityContextProvider) {

        this.auditEventService =
                auditEventService;

        this.auditLogReviewService =
                auditLogReviewService;

        this.securityContextProvider =
                securityContextProvider;
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

    @GetMapping
    public ResponseEntity<PageResponse<AuditEventResponse>>
    searchAuditEvents(
            @RequestParam(required = false) UUID userId,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to,
            @RequestParam(required = false) String entityType,
            @RequestParam(required = false) UUID entityId,
            @RequestParam(required = false) String action,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "25") int size,
            @RequestParam(
                    defaultValue = "eventTimestamp"
            ) String sort,
            @RequestParam(
                    defaultValue = "DESC"
            ) String direction) {

        SecurityContext securityContext =
                securityContextProvider
                        .getCurrentContext();

        return ResponseEntity.ok(
                auditLogReviewService
                        .searchAuditEvents(
                                userId,
                                from,
                                to,
                                entityType,
                                entityId,
                                action,
                                page,
                                size,
                                sort,
                                direction,
                                securityContext
                        )
        );
    }

    @GetMapping("/{auditEventId}")
    public ResponseEntity<AuditEventResponse> getAuditEventById(
            @PathVariable UUID auditEventId) {

        SecurityContext securityContext =
                securityContextProvider
                        .getCurrentContext();

        return ResponseEntity.ok(
                auditLogReviewService
                        .getAuditEventById(
                                auditEventId,
                                securityContext
                        )
        );
    }

    @GetMapping("/type/{eventType}")
    public ResponseEntity<List<AuditEventResponse>>
    getAuditEventsByEventType(
            @PathVariable String eventType) {

        SecurityContext securityContext =
                securityContextProvider
                        .getCurrentContext();

        return ResponseEntity.ok(
                auditLogReviewService
                        .getAuditEventsByEventType(
                                eventType,
                                securityContext
                        )
        );
    }

    @GetMapping("/entity/{entityType}/{entityId}")
    public ResponseEntity<List<AuditEventResponse>>
    getAuditEventsByEntity(
            @PathVariable String entityType,
            @PathVariable UUID entityId) {

        SecurityContext securityContext =
                securityContextProvider
                        .getCurrentContext();

        return ResponseEntity.ok(
                auditLogReviewService
                        .getAuditEventsByEntity(
                                entityType,
                                entityId,
                                securityContext
                        )
        );
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<AuditEventResponse>>
    getAuditEventsByUserId(
            @PathVariable UUID userId) {

        SecurityContext securityContext =
                securityContextProvider
                        .getCurrentContext();

        return ResponseEntity.ok(
                auditLogReviewService
                        .getAuditEventsByUserId(
                                userId,
                                securityContext
                        )
        );
    }

    @GetMapping("/organization/{organizationId}")
    public ResponseEntity<List<AuditEventResponse>>
    getAuditEventsByOrganizationId(
            @PathVariable UUID organizationId) {

        SecurityContext securityContext =
                securityContextProvider
                        .getCurrentContext();

        return ResponseEntity.ok(
                auditLogReviewService
                        .getAuditEventsByOrganizationId(
                                organizationId,
                                securityContext
                        )
        );
    }

    @GetMapping("/correlation/{correlationId}")
    public ResponseEntity<List<AuditEventResponse>>
    getAuditEventsByCorrelationId(
            @PathVariable UUID correlationId) {

        SecurityContext securityContext =
                securityContextProvider
                        .getCurrentContext();

        return ResponseEntity.ok(
                auditLogReviewService
                        .getAuditEventsByCorrelationId(
                                correlationId,
                                securityContext
                        )
        );
    }
}