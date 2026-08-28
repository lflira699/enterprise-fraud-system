package com.efs.modules.audit.controller;

import com.efs.modules.audit.dto.AuditLoginRequest;
import com.efs.modules.audit.dto.AuditLoginResponse;
import com.efs.modules.audit.service.AuditLoginServiceInterface;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/audit/logins")
public class AuditLoginController {

    private final AuditLoginServiceInterface auditLoginService;

    public AuditLoginController(
            AuditLoginServiceInterface auditLoginService) {

        this.auditLoginService =
                auditLoginService;
    }

    @PostMapping
    public ResponseEntity<AuditLoginResponse> createAuditLogin(
            @Valid @RequestBody AuditLoginRequest request) {

        AuditLoginResponse response =
                auditLoginService.createAuditLogin(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/{loginId}")
    public ResponseEntity<AuditLoginResponse> getAuditLoginById(
            @PathVariable UUID loginId) {

        return ResponseEntity.ok(
                auditLoginService.getAuditLoginById(
                        loginId
                )
        );
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<AuditLoginResponse>>
    getAuditLoginsByUserId(
            @PathVariable UUID userId) {

        return ResponseEntity.ok(
                auditLoginService.getAuditLoginsByUserId(
                        userId
                )
        );
    }

    @GetMapping("/result/{loginResult}")
    public ResponseEntity<List<AuditLoginResponse>>
    getAuditLoginsByLoginResult(
            @PathVariable String loginResult) {

        return ResponseEntity.ok(
                auditLoginService.getAuditLoginsByLoginResult(
                        loginResult
                )
        );
    }

    @GetMapping("/ip/{ipAddress}")
    public ResponseEntity<List<AuditLoginResponse>>
    getAuditLoginsByIpAddress(
            @PathVariable String ipAddress) {

        return ResponseEntity.ok(
                auditLoginService.getAuditLoginsByIpAddress(
                        ipAddress
                )
        );
    }

    @GetMapping("/authentication-method/{authenticationMethod}")
    public ResponseEntity<List<AuditLoginResponse>>
    getAuditLoginsByAuthenticationMethod(
            @PathVariable String authenticationMethod) {

        return ResponseEntity.ok(
                auditLoginService
                        .getAuditLoginsByAuthenticationMethod(
                                authenticationMethod
                        )
        );
    }
}