package com.efs.modules.audit.controller;

import com.efs.modules.audit.dto.AuditHistoryRequest;
import com.efs.modules.audit.dto.AuditHistoryResponse;
import com.efs.modules.audit.service.AuditHistoryServiceInterface;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/audit/history")
public class AuditHistoryController {

    private final AuditHistoryServiceInterface auditHistoryService;

    public AuditHistoryController(
            AuditHistoryServiceInterface auditHistoryService) {

        this.auditHistoryService =
                auditHistoryService;
    }

    @PostMapping
    public ResponseEntity<AuditHistoryResponse>
    createAuditHistory(
            @Valid @RequestBody AuditHistoryRequest request) {

        AuditHistoryResponse response =
                auditHistoryService.createAuditHistory(
                        request
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/{historyId}")
    public ResponseEntity<AuditHistoryResponse>
    getAuditHistoryById(
            @PathVariable UUID historyId) {

        return ResponseEntity.ok(
                auditHistoryService.getAuditHistoryById(
                        historyId
                )
        );
    }

    @GetMapping("/source")
    public ResponseEntity<AuditHistoryResponse>
    getAuditHistoryBySource(
            @RequestParam String sourceTable,
            @RequestParam UUID sourceRecordId) {

        return ResponseEntity.ok(
                auditHistoryService.getAuditHistoryBySource(
                        sourceTable,
                        sourceRecordId
                )
        );
    }

    @GetMapping("/organization/{organizationId}")
    public ResponseEntity<List<AuditHistoryResponse>>
    getAuditHistoryByOrganizationId(
            @PathVariable UUID organizationId) {

        return ResponseEntity.ok(
                auditHistoryService.getAuditHistoryByOrganizationId(
                        organizationId
                )
        );
    }

    @GetMapping("/tenant/{tenantId}")
    public ResponseEntity<List<AuditHistoryResponse>>
    getAuditHistoryByTenantId(
            @PathVariable UUID tenantId) {

        return ResponseEntity.ok(
                auditHistoryService.getAuditHistoryByTenantId(
                        tenantId
                )
        );
    }

    @GetMapping("/correlation/{correlationId}")
    public ResponseEntity<List<AuditHistoryResponse>>
    getAuditHistoryByCorrelationId(
            @PathVariable UUID correlationId) {

        return ResponseEntity.ok(
                auditHistoryService.getAuditHistoryByCorrelationId(
                        correlationId
                )
        );
    }

    @GetMapping("/source-table")
    public ResponseEntity<List<AuditHistoryResponse>>
    getAuditHistoryBySourceTable(
            @RequestParam String sourceTable) {

        return ResponseEntity.ok(
                auditHistoryService.getAuditHistoryBySourceTable(
                        sourceTable
                )
        );
    }
}