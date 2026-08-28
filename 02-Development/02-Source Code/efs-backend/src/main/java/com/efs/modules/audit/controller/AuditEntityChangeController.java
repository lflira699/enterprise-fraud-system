package com.efs.modules.audit.controller;

import com.efs.modules.audit.dto.AuditEntityChangeRequest;
import com.efs.modules.audit.dto.AuditEntityChangeResponse;
import com.efs.modules.audit.service.AuditEntityChangeServiceInterface;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/audit/entity-changes")
public class AuditEntityChangeController {

    private final AuditEntityChangeServiceInterface auditEntityChangeService;

    public AuditEntityChangeController(
            AuditEntityChangeServiceInterface auditEntityChangeService) {

        this.auditEntityChangeService =
                auditEntityChangeService;
    }

    @PostMapping
    public ResponseEntity<AuditEntityChangeResponse>
    createAuditEntityChange(
            @Valid @RequestBody AuditEntityChangeRequest request) {

        AuditEntityChangeResponse response =
                auditEntityChangeService
                        .createAuditEntityChange(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/{changeId}")
    public ResponseEntity<AuditEntityChangeResponse>
    getAuditEntityChangeById(
            @PathVariable UUID changeId) {

        return ResponseEntity.ok(
                auditEntityChangeService
                        .getAuditEntityChangeById(changeId)
        );
    }

    @GetMapping("/event/{auditEventId}")
    public ResponseEntity<List<AuditEntityChangeResponse>>
    getAuditEntityChangesByAuditEventId(
            @PathVariable UUID auditEventId) {

        return ResponseEntity.ok(
                auditEntityChangeService
                        .getAuditEntityChangesByAuditEventId(
                                auditEventId
                        )
        );
    }

    @GetMapping("/entity/{entityType}/{entityId}")
    public ResponseEntity<List<AuditEntityChangeResponse>>
    getAuditEntityChangesByEntity(
            @PathVariable String entityType,
            @PathVariable UUID entityId) {

        return ResponseEntity.ok(
                auditEntityChangeService
                        .getAuditEntityChangesByEntity(
                                entityType,
                                entityId
                        )
        );
    }

    @GetMapping("/operation/{operation}")
    public ResponseEntity<List<AuditEntityChangeResponse>>
    getAuditEntityChangesByOperation(
            @PathVariable String operation) {

        return ResponseEntity.ok(
                auditEntityChangeService
                        .getAuditEntityChangesByOperation(
                                operation
                        )
        );
    }
}