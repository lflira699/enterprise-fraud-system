package com.efs.modules.catalog.service;

import com.efs.modules.catalog.dto.NotificationTemplateRequest;
import com.efs.modules.catalog.dto.NotificationTemplateResponse;
import com.efs.modules.catalog.entity.NotificationTemplate;
import com.efs.modules.catalog.mapper.NotificationTemplateMapper;
import com.efs.modules.catalog.repository.NotificationTemplateRepository;
import com.efs.shared.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class NotificationTemplateService
        implements NotificationTemplateServiceInterface {

    private final NotificationTemplateRepository
            notificationTemplateRepository;

    private final NotificationTemplateMapper
            notificationTemplateMapper;

    public NotificationTemplateService(
            NotificationTemplateRepository notificationTemplateRepository,
            NotificationTemplateMapper notificationTemplateMapper) {

        this.notificationTemplateRepository =
                notificationTemplateRepository;

        this.notificationTemplateMapper =
                notificationTemplateMapper;
    }

    @Override
    public NotificationTemplateResponse createNotificationTemplate(
            NotificationTemplateRequest request) {

        NotificationTemplate notificationTemplate =
                notificationTemplateMapper.toEntity(
                        request
                );

        NotificationTemplate savedNotificationTemplate =
                notificationTemplateRepository.save(
                        notificationTemplate
                );

        return notificationTemplateMapper.toResponse(
                savedNotificationTemplate
        );
    }

    @Override
    @Transactional(readOnly = true)
    public NotificationTemplateResponse getNotificationTemplateById(
            UUID notificationTemplateId) {

        NotificationTemplate notificationTemplate =
                notificationTemplateRepository
                        .findById(notificationTemplateId)
                        .orElseThrow(
                                () ->
                                        new ResourceNotFoundException(
                                                "Notification template not found: "
                                                        + notificationTemplateId
                                        )
                        );

        return notificationTemplateMapper.toResponse(
                notificationTemplate
        );
    }

    @Override
    @Transactional(readOnly = true)
    public NotificationTemplateResponse getNotificationTemplateByScope(
            UUID organizationId,
            UUID tenantId,
            String templateCode,
            String channel,
            UUID languageId) {

        NotificationTemplate notificationTemplate =
                notificationTemplateRepository
                        .findByOrganizationIdAndTenantIdAndTemplateCodeAndChannelAndLanguageId(
                                organizationId,
                                tenantId,
                                templateCode,
                                channel,
                                languageId
                        )
                        .orElseThrow(
                                () ->
                                        new ResourceNotFoundException(
                                                "Notification template not found for requested scope"
                                        )
                        );

        return notificationTemplateMapper.toResponse(
                notificationTemplate
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationTemplateResponse>
            getNotificationTemplatesByScope(
                    UUID organizationId,
                    UUID tenantId) {

        return notificationTemplateRepository
                .findByOrganizationIdAndTenantIdOrderByTemplateNameAsc(
                        organizationId,
                        tenantId
                )
                .stream()
                .map(notificationTemplateMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationTemplateResponse>
            getNotificationTemplatesByScopeAndStatus(
                    UUID organizationId,
                    UUID tenantId,
                    String status) {

        return notificationTemplateRepository
                .findByOrganizationIdAndTenantIdAndStatusOrderByTemplateNameAsc(
                        organizationId,
                        tenantId,
                        status
                )
                .stream()
                .map(notificationTemplateMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationTemplateResponse>
            getNotificationTemplatesByCode(
                    String templateCode) {

        return notificationTemplateRepository
                .findByTemplateCodeOrderByTemplateNameAsc(
                        templateCode
                )
                .stream()
                .map(notificationTemplateMapper::toResponse)
                .toList();
    }
}