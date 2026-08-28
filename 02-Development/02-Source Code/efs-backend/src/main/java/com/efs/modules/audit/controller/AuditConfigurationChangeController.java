package com.efs.modules.audit.controller;

import com.efs.modules.audit.dto.AuditConfigurationChangeRequest;
import com.efs.modules.audit.dto.AuditConfigurationChangeResponse;
import com.efs.modules.audit.service.AuditConfigurationChangeServiceInterface;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/audit/configuration-changes")
public class AuditConfigurationChangeController {

    private final AuditConfigurationChangeServiceInterface
            auditConfigurationChangeService;

    public AuditConfigurationChangeController(
            AuditConfigurationChangeServiceInterface
                    auditConfigurationChangeService) {

        this.auditConfigurationChangeService =
                auditConfigurationChangeService;
    }

    @PostMapping
    public ResponseEntity<AuditConfigurationChangeResponse>
    createAuditConfigurationChange(
            @Valid @RequestBody
            AuditConfigurationChangeRequest request) {

        AuditConfigurationChangeResponse response =
                auditConfigurationChangeService
                        .createAuditConfigurationChange(
                                request
                        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/{configurationChangeId}")
    public ResponseEntity<AuditConfigurationChangeResponse>
    getAuditConfigurationChangeById(
            @PathVariable UUID configurationChangeId) {

        return ResponseEntity.ok(
                auditConfigurationChangeService
                        .getAuditConfigurationChangeById(
                                configurationChangeId
                        )
        );
    }

    @GetMapping("/audit-event/{auditEventId}")
    public ResponseEntity<List<AuditConfigurationChangeResponse>>
    getAuditConfigurationChangesByAuditEventId(
            @PathVariable UUID auditEventId) {

        return ResponseEntity.ok(
                auditConfigurationChangeService
                        .getAuditConfigurationChangesByAuditEventId(
                                auditEventId
                        )
        );
    }

    @GetMapping("/configuration-key")
    public ResponseEntity<List<AuditConfigurationChangeResponse>>
    getAuditConfigurationChangesByConfigurationKey(
            @RequestParam String configurationKey) {

        return ResponseEntity.ok(
                auditConfigurationChangeService
                        .getAuditConfigurationChangesByConfigurationKey(
                                configurationKey
                        )
        );
    }

    @GetMapping("/changed-by/{changedBy}")
    public ResponseEntity<List<AuditConfigurationChangeResponse>>
    getAuditConfigurationChangesByChangedBy(
            @PathVariable UUID changedBy) {

        return ResponseEntity.ok(
                auditConfigurationChangeService
                        .getAuditConfigurationChangesByChangedBy(
                                changedBy
                        )
        );
    }
}