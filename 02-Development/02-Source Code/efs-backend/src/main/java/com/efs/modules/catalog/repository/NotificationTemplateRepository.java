package com.efs.modules.catalog.repository;

import com.efs.modules.catalog.entity.NotificationTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface NotificationTemplateRepository
        extends JpaRepository<NotificationTemplate, UUID> {

    Optional<NotificationTemplate>
            findByOrganizationIdAndTenantIdAndTemplateCodeAndChannelAndLanguageId(
                    UUID organizationId,
                    UUID tenantId,
                    String templateCode,
                    String channel,
                    UUID languageId
            );

    List<NotificationTemplate>
            findByOrganizationIdAndTenantIdOrderByTemplateNameAsc(
                    UUID organizationId,
                    UUID tenantId
            );

    List<NotificationTemplate>
            findByOrganizationIdAndTenantIdAndStatusOrderByTemplateNameAsc(
                    UUID organizationId,
                    UUID tenantId,
                    String status
            );

    List<NotificationTemplate>
            findByTemplateCodeOrderByTemplateNameAsc(
                    String templateCode
            );
}