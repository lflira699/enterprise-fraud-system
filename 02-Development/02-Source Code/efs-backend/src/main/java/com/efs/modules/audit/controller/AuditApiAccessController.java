package com.efs.modules.audit.controller;

import com.efs.modules.audit.dto.AuditApiAccessRequest;
import com.efs.modules.audit.dto.AuditApiAccessResponse;
import com.efs.modules.audit.service.AuditApiAccessServiceInterface;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/audit/api-access")
public class AuditApiAccessController {

    private final AuditApiAccessServiceInterface auditApiAccessService;

    public AuditApiAccessController(
            AuditApiAccessServiceInterface auditApiAccessService) {

        this.auditApiAccessService = auditApiAccessService;
    }

    @PostMapping
    public ResponseEntity<AuditApiAccessResponse> createAuditApiAccess(
            @Valid @RequestBody AuditApiAccessRequest request) {

        AuditApiAccessResponse response =
                auditApiAccessService.createAuditApiAccess(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/{apiAccessId}")
    public ResponseEntity<AuditApiAccessResponse> getAuditApiAccessById(
            @PathVariable UUID apiAccessId) {

        return ResponseEntity.ok(
                auditApiAccessService.getAuditApiAccessById(
                        apiAccessId
                )
        );
    }

    @GetMapping("/client/{apiClientId}")
    public ResponseEntity<List<AuditApiAccessResponse>>
            getAuditApiAccessesByApiClientId(
                    @PathVariable UUID apiClientId) {

        return ResponseEntity.ok(
                auditApiAccessService.getAuditApiAccessesByApiClientId(
                        apiClientId
                )
        );
    }

    @GetMapping("/endpoint")
    public ResponseEntity<List<AuditApiAccessResponse>>
            getAuditApiAccessesByEndpoint(
                    @RequestParam String endpoint) {

        return ResponseEntity.ok(
                auditApiAccessService.getAuditApiAccessesByEndpoint(
                        endpoint
                )
        );
    }

    @GetMapping("/response-code/{responseCode}")
    public ResponseEntity<List<AuditApiAccessResponse>>
            getAuditApiAccessesByResponseCode(
                    @PathVariable Integer responseCode) {

        return ResponseEntity.ok(
                auditApiAccessService.getAuditApiAccessesByResponseCode(
                        responseCode
                )
        );
    }

    @GetMapping("/correlation/{correlationId}")
    public ResponseEntity<List<AuditApiAccessResponse>>
            getAuditApiAccessesByCorrelationId(
                    @PathVariable UUID correlationId) {

        return ResponseEntity.ok(
                auditApiAccessService.getAuditApiAccessesByCorrelationId(
                        correlationId
                )
        );
    }
}