package com.efs.modules.catalog.controller;

import com.efs.modules.catalog.dto.NotificationTemplateRequest;
import com.efs.modules.catalog.dto.NotificationTemplateResponse;
import com.efs.modules.catalog.dto.NotificationTemplateUpdateRequest;
import com.efs.modules.catalog.service.NotificationTemplateServiceInterface;
import com.efs.shared.security.SecurityContext;
import com.efs.shared.security.SecurityContextProvider;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/notification-templates")
public class NotificationTemplateController {

    private final NotificationTemplateServiceInterface
            notificationTemplateService;

    private final SecurityContextProvider
            securityContextProvider;

    public NotificationTemplateController(
            NotificationTemplateServiceInterface notificationTemplateService,
            SecurityContextProvider securityContextProvider) {

        this.notificationTemplateService =
                notificationTemplateService;

        this.securityContextProvider =
                securityContextProvider;
    }

    @PostMapping
    public ResponseEntity<NotificationTemplateResponse>
            createNotificationTemplate(
                    @Valid @RequestBody
                    NotificationTemplateRequest request) {

        NotificationTemplateResponse response =
                notificationTemplateService
                        .createNotificationTemplate(
                                request
                        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PatchMapping("/{notificationTemplateId}")
    public ResponseEntity<NotificationTemplateResponse>
            updateNotificationTemplate(
                    @PathVariable
                    UUID notificationTemplateId,
                    @Valid @RequestBody
                    NotificationTemplateUpdateRequest request) {

        SecurityContext securityContext =
                securityContextProvider
                        .getCurrentContext();

        return ResponseEntity.ok(
                notificationTemplateService
                        .updateNotificationTemplate(
                                notificationTemplateId,
                                request,
                                securityContext
                        )
        );
    }

    @GetMapping("/{notificationTemplateId}")
    public ResponseEntity<NotificationTemplateResponse>
            getNotificationTemplateById(
                    @PathVariable
                    UUID notificationTemplateId) {

        return ResponseEntity.ok(
                notificationTemplateService
                        .getNotificationTemplateById(
                                notificationTemplateId
                        )
        );
    }

    @GetMapping("/scope")
    public ResponseEntity<NotificationTemplateResponse>
            getNotificationTemplateByScope(
                    @RequestParam UUID organizationId,
                    @RequestParam UUID tenantId,
                    @RequestParam String templateCode,
                    @RequestParam String channel,
                    @RequestParam UUID languageId) {

        return ResponseEntity.ok(
                notificationTemplateService
                        .getNotificationTemplateByScope(
                                organizationId,
                                tenantId,
                                templateCode,
                                channel,
                                languageId
                        )
        );
    }

    @GetMapping
    public ResponseEntity<List<NotificationTemplateResponse>>
            getNotificationTemplatesByScope(
                    @RequestParam UUID organizationId,
                    @RequestParam UUID tenantId,
                    @RequestParam(required = false)
                    String status) {

        if (status != null) {

            return ResponseEntity.ok(
                    notificationTemplateService
                            .getNotificationTemplatesByScopeAndStatus(
                                    organizationId,
                                    tenantId,
                                    status
                            )
            );
        }

        return ResponseEntity.ok(
                notificationTemplateService
                        .getNotificationTemplatesByScope(
                                organizationId,
                                tenantId
                        )
        );
    }

    @GetMapping("/code/{templateCode}")
    public ResponseEntity<List<NotificationTemplateResponse>>
            getNotificationTemplatesByCode(
                    @PathVariable String templateCode) {

        return ResponseEntity.ok(
                notificationTemplateService
                        .getNotificationTemplatesByCode(
                                templateCode
                        )
        );
    }
}