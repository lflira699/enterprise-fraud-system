package com.efs.modules.administration.controller;

import com.efs.modules.administration.dto.ConfigurationChangeRequestCreateRequest;
import com.efs.modules.administration.dto.ConfigurationChangeRequestResponse;
import com.efs.modules.administration.dto.ConfigurationEffectiveResponse;
import com.efs.modules.administration.dto.ConfigurationRejectRequest;
import com.efs.modules.administration.dto.ConfigurationVersionResponse;
import com.efs.modules.administration.service.ConfigurationGovernanceServiceInterface;
import com.efs.shared.security.SecurityContext;
import com.efs.shared.security.SecurityContextProvider;
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
@RequestMapping("/api/v1/admin/configuration")
public class ConfigurationAdministrationController {

    private final ConfigurationGovernanceServiceInterface
            configurationGovernanceService;

    private final SecurityContextProvider
            securityContextProvider;

    public ConfigurationAdministrationController(
            ConfigurationGovernanceServiceInterface
                    configurationGovernanceService,
            SecurityContextProvider
                    securityContextProvider) {

        this.configurationGovernanceService =
                configurationGovernanceService;

        this.securityContextProvider =
                securityContextProvider;
    }

    @GetMapping("/effective")
    public ResponseEntity<List<ConfigurationEffectiveResponse>>
    getEffectiveConfigurations(
            @RequestParam(required = false)
            UUID tenantId) {

        SecurityContext securityContext =
                securityContextProvider
                        .getCurrentContext();

        return ResponseEntity.ok(
                configurationGovernanceService
                        .getEffectiveConfigurations(
                                tenantId,
                                securityContext
                        )
        );
    }

    @GetMapping("/effective/{configurationKey}")
    public ResponseEntity<ConfigurationEffectiveResponse>
    getEffectiveConfiguration(
            @PathVariable
            String configurationKey,
            @RequestParam(required = false)
            UUID tenantId) {

        SecurityContext securityContext =
                securityContextProvider
                        .getCurrentContext();

        return ResponseEntity.ok(
                configurationGovernanceService
                        .getEffectiveConfiguration(
                                configurationKey,
                                tenantId,
                                securityContext
                        )
        );
    }

    @PostMapping("/change-requests")
    public ResponseEntity<ConfigurationChangeRequestResponse>
    createChangeRequest(
            @Valid
            @RequestBody
            ConfigurationChangeRequestCreateRequest request) {

        SecurityContext securityContext =
                securityContextProvider
                        .getCurrentContext();

        ConfigurationChangeRequestResponse response =
                configurationGovernanceService
                        .createChangeRequest(
                                request,
                                securityContext
                        );

        return ResponseEntity
                .status(
                        HttpStatus.CREATED
                )
                .body(
                        response
                );
    }

    @GetMapping("/change-requests/{changeRequestId}")
    public ResponseEntity<ConfigurationChangeRequestResponse>
    getChangeRequest(
            @PathVariable
            UUID changeRequestId) {

        SecurityContext securityContext =
                securityContextProvider
                        .getCurrentContext();

        return ResponseEntity.ok(
                configurationGovernanceService
                        .getChangeRequest(
                                changeRequestId,
                                securityContext
                        )
        );
    }

    @PostMapping(
            "/change-requests/{changeRequestId}/approve"
    )
    public ResponseEntity<ConfigurationChangeRequestResponse>
    approveChangeRequest(
            @PathVariable
            UUID changeRequestId) {

        SecurityContext securityContext =
                securityContextProvider
                        .getCurrentContext();

        return ResponseEntity.ok(
                configurationGovernanceService
                        .approveChangeRequest(
                                changeRequestId,
                                securityContext
                        )
        );
    }

    @PostMapping(
            "/change-requests/{changeRequestId}/reject"
    )
    public ResponseEntity<ConfigurationChangeRequestResponse>
    rejectChangeRequest(
            @PathVariable
            UUID changeRequestId,
            @Valid
            @RequestBody
            ConfigurationRejectRequest request) {

        SecurityContext securityContext =
                securityContextProvider
                        .getCurrentContext();

        return ResponseEntity.ok(
                configurationGovernanceService
                        .rejectChangeRequest(
                                changeRequestId,
                                request.getRejectionReason(),
                                securityContext
                        )
        );
    }

    @PostMapping(
            "/change-requests/{changeRequestId}/publish"
    )
    public ResponseEntity<ConfigurationChangeRequestResponse>
    publishChangeRequest(
            @PathVariable
            UUID changeRequestId) {

        SecurityContext securityContext =
                securityContextProvider
                        .getCurrentContext();

        return ResponseEntity.ok(
                configurationGovernanceService
                        .publishChangeRequest(
                                changeRequestId,
                                securityContext
                        )
        );
    }

    @GetMapping("/{configurationKey}/versions")
    public ResponseEntity<List<ConfigurationVersionResponse>>
    getAppliedVersions(
            @PathVariable
            String configurationKey,
            @RequestParam(required = false)
            UUID tenantId) {

        SecurityContext securityContext =
                securityContextProvider
                        .getCurrentContext();

        return ResponseEntity.ok(
                configurationGovernanceService
                        .getAppliedVersions(
                                configurationKey,
                                tenantId,
                                securityContext
                        )
        );
    }
}