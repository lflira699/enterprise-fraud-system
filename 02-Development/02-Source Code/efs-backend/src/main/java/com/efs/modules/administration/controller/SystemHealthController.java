package com.efs.modules.administration.controller;

import com.efs.modules.administration.dto.SystemHealthResponse;
import com.efs.modules.administration.service.SystemHealthServiceInterface;
import com.efs.shared.security.SecurityContext;
import com.efs.shared.security.SecurityContextProvider;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/health")
public class SystemHealthController {

    private final SystemHealthServiceInterface
            systemHealthService;

    private final SecurityContextProvider
            securityContextProvider;

    public SystemHealthController(
            SystemHealthServiceInterface systemHealthService,
            SecurityContextProvider securityContextProvider) {

        this.systemHealthService =
                systemHealthService;

        this.securityContextProvider =
                securityContextProvider;
    }

    @GetMapping
    public ResponseEntity<SystemHealthResponse>
    getSystemHealth() {

        SecurityContext securityContext =
                securityContextProvider
                        .getCurrentContext();

        return ResponseEntity.ok(
                systemHealthService
                        .getSystemHealth(
                                securityContext
                        )
        );
    }
}