package com.efs.modules.notification.repository;

import com.efs.modules.notification.entity.Notification;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
class NotificationClaimLockIntegrationTest {

    private final UUID organizationId =
            UUID.randomUUID();

    private final UUID tenantId =
            UUID.randomUUID();

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {

        insertOrganization();
        insertTenant();
    }

    @Test
    void shouldLoadPendingNotificationWithPessimisticWriteLock() {

        UUID sourceEntityId =
                UUID.randomUUID();

        Notification notification =
                new Notification();

        notification.setOrganizationId(
                organizationId
        );

        notification.setTenantId(
                tenantId
        );

        notification.setNotificationType(
                "CASE_STATUS_CHANGED"
        );

        notification.setNotificationTemplateId(
                null
        );

        notification.setSourceComponent(
                "CASE"
        );

        notification.setSourceEntityType(
                "CASE"
        );

        notification.setSourceEntityId(
                sourceEntityId
        );

        notification.setCorrelationId(
                UUID.randomUUID()
        );

        notification.setNotificationPayload(
                Map.of(
                        "caseId",
                        sourceEntityId.toString()
                )
        );

        Notification saved =
                notificationRepository.save(
                        notification
                );

        entityManager.flush();

        UUID notificationId =
                saved.getNotificationId();

        assertNotNull(
                notificationId
        );

        assertEquals(
                7,
                notificationId.version()
        );

        entityManager.clear();

        Optional<Notification> locked =
                notificationRepository
                        .findByNotificationIdForUpdate(
                                notificationId
                        );

        assertTrue(
                locked.isPresent()
        );

        assertEquals(
                notificationId,
                locked.orElseThrow()
                        .getNotificationId()
        );

        assertEquals(
                "PENDING",
                locked.orElseThrow()
                        .getNotificationStatus()
        );
    }

    @Test
    void shouldReturnEmptyWhenNotificationDoesNotExist() {

        Optional<Notification> locked =
                notificationRepository
                        .findByNotificationIdForUpdate(
                                UUID.randomUUID()
                        );

        assertTrue(
                locked.isEmpty()
        );
    }

    private void insertOrganization() {

        jdbcTemplate.update(
                """
                INSERT INTO administration.organization (
                    organization_id,
                    organization_code,
                    legal_name,
                    country_code,
                    timezone,
                    status
                )
                VALUES (?, ?, ?, ?, ?, ?)
                """,
                organizationId,
                "NCLM-ORG-"
                        + organizationId
                                .toString()
                                .substring(0, 8),
                "EFS Notification Claim Test Organization",
                "GT",
                "America/Guatemala",
                "ACTIVE"
        );
    }

    private void insertTenant() {

        jdbcTemplate.update(
                """
                INSERT INTO administration.tenant (
                    tenant_id,
                    organization_id,
                    tenant_code,
                    tenant_name,
                    status,
                    environment
                )
                VALUES (?, ?, ?, ?, ?, ?)
                """,
                tenantId,
                organizationId,
                "NCLM-TEN-"
                        + tenantId
                                .toString()
                                .substring(0, 8),
                "EFS Notification Claim Test Tenant",
                "ACTIVE",
                "TEST"
        );
    }
}