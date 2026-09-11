package com.efs.modules.notification.repository;

import com.efs.modules.notification.entity.Notification;
import com.efs.modules.notification.entity.NotificationRecipientDelivery;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest
@Transactional
class NotificationPersistenceIntegrationTest {

    private static final UUID ORGANIZATION_ID =
            UUID.fromString(
                    "15115115-1151-4151-8151-151151151151"
            );

    private static final UUID TENANT_ID =
            UUID.fromString(
                    "15215215-2152-4152-8152-152152152152"
            );

    private static final UUID RECIPIENT_USER_ID =
            UUID.fromString(
                    "15315315-3153-4153-8153-153153153153"
            );

    private static final UUID SOURCE_ENTITY_ID =
            UUID.fromString(
                    "15415415-4154-4154-8154-154154154154"
            );

    private static final UUID CORRELATION_ID =
            UUID.fromString(
                    "15515515-5155-4155-8155-155155155155"
            );

    @Autowired
    private NotificationRepository
            notificationRepository;

    @Autowired
    private NotificationRecipientDeliveryRepository
            notificationRecipientDeliveryRepository;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {

        insertOrganization();
        insertTenant();
        insertRecipientUser();
    }

    @Test
    void shouldPersistNotificationAggregateRootWithJsonPayloadAndDefaults() {

        Notification notification =
                new Notification();

        notification.setOrganizationId(
                ORGANIZATION_ID
        );

        notification.setTenantId(
                TENANT_ID
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
                SOURCE_ENTITY_ID
        );

        notification.setCorrelationId(
                CORRELATION_ID
        );

        notification.setNotificationPayload(
                Map.of(
                        "caseId",
                        SOURCE_ENTITY_ID.toString(),
                        "status",
                        "CLOSED"
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

        Notification persisted =
                notificationRepository
                        .findById(
                                notificationId
                        )
                        .orElseThrow();

        assertEquals(
                ORGANIZATION_ID,
                persisted.getOrganizationId()
        );

        assertEquals(
                TENANT_ID,
                persisted.getTenantId()
        );

        assertEquals(
                "CASE_STATUS_CHANGED",
                persisted.getNotificationType()
        );

        assertNull(
                persisted.getNotificationTemplateId()
        );

        assertEquals(
                "CASE",
                persisted.getSourceComponent()
        );

        assertEquals(
                "CASE",
                persisted.getSourceEntityType()
        );

        assertEquals(
                SOURCE_ENTITY_ID,
                persisted.getSourceEntityId()
        );

        assertEquals(
                CORRELATION_ID,
                persisted.getCorrelationId()
        );

        assertEquals(
                SOURCE_ENTITY_ID.toString(),
                persisted
                        .getNotificationPayload()
                        .get("caseId")
        );

        assertEquals(
                "CLOSED",
                persisted
                        .getNotificationPayload()
                        .get("status")
        );

        assertEquals(
                "PENDING",
                persisted.getNotificationStatus()
        );

        assertNotNull(
                persisted.getCreatedAt()
        );

        assertNull(
                persisted.getProcessedAt()
        );
    }

    @Test
    void shouldPersistAndRetrieveRecipientDeliveriesInCreationOrder() {

        Notification notification =
                new Notification();

        notification.setOrganizationId(
                ORGANIZATION_ID
        );

        notification.setTenantId(
                TENANT_ID
        );

        notification.setNotificationType(
                "CASE_STATUS_CHANGED"
        );

        notification.setSourceComponent(
                "CASE"
        );

        notification.setSourceEntityType(
                "CASE"
        );

        notification.setSourceEntityId(
                SOURCE_ENTITY_ID
        );

        notification.setCorrelationId(
                CORRELATION_ID
        );

        notification.setNotificationPayload(
                Map.of(
                        "caseId",
                        SOURCE_ENTITY_ID.toString()
                )
        );

        Notification savedNotification =
                notificationRepository.save(
                        notification
                );

        entityManager.flush();

        UUID notificationId =
                savedNotification
                        .getNotificationId();

        assertNotNull(
                notificationId
        );

        LocalDateTime firstCreatedAt =
                LocalDateTime.now()
                        .truncatedTo(ChronoUnit.MICROS)
                        .minusSeconds(2);

        LocalDateTime secondCreatedAt =
                firstCreatedAt.plusSeconds(1);

        NotificationRecipientDelivery
                firstDelivery =
                new NotificationRecipientDelivery();

        firstDelivery.setNotificationId(
                notificationId
        );

        firstDelivery.setRecipientUserId(
                RECIPIENT_USER_ID
        );

        firstDelivery.setChannel(
                "EMAIL"
        );

        firstDelivery.setCreatedAt(
                firstCreatedAt
        );

        NotificationRecipientDelivery
                secondDelivery =
                new NotificationRecipientDelivery();

        secondDelivery.setNotificationId(
                notificationId
        );

        secondDelivery.setRecipientUserId(
                RECIPIENT_USER_ID
        );

        secondDelivery.setChannel(
                "SMS"
        );

        secondDelivery.setCreatedAt(
                secondCreatedAt
        );

        notificationRecipientDeliveryRepository
                .save(
                        secondDelivery
                );

        notificationRecipientDeliveryRepository
                .save(
                        firstDelivery
                );

        entityManager.flush();

        assertNotNull(
                firstDelivery
                        .getNotificationDeliveryId()
        );

        assertNotNull(
                secondDelivery
                        .getNotificationDeliveryId()
        );

        assertEquals(
                7,
                firstDelivery
                        .getNotificationDeliveryId()
                        .version()
        );

        assertEquals(
                7,
                secondDelivery
                        .getNotificationDeliveryId()
                        .version()
        );

        entityManager.clear();

        List<NotificationRecipientDelivery>
                deliveries =
                notificationRecipientDeliveryRepository
                        .findByNotificationIdOrderByCreatedAtAsc(
                                notificationId
                        );

        assertEquals(
                2,
                deliveries.size()
        );

        NotificationRecipientDelivery
                persistedFirst =
                deliveries.get(0);

        NotificationRecipientDelivery
                persistedSecond =
                deliveries.get(1);

        assertEquals(
                notificationId,
                persistedFirst.getNotificationId()
        );

        assertEquals(
                notificationId,
                persistedSecond.getNotificationId()
        );

        assertEquals(
                RECIPIENT_USER_ID,
                persistedFirst.getRecipientUserId()
        );

        assertEquals(
                RECIPIENT_USER_ID,
                persistedSecond.getRecipientUserId()
        );

        assertEquals(
                "EMAIL",
                persistedFirst.getChannel()
        );

        assertEquals(
                "SMS",
                persistedSecond.getChannel()
        );

        assertEquals(
                "PENDING",
                persistedFirst.getDeliveryStatus()
        );

        assertEquals(
                "PENDING",
                persistedSecond.getDeliveryStatus()
        );

        assertNull(
                persistedFirst.getProcessedAt()
        );

        assertNull(
                persistedSecond.getProcessedAt()
        );

        assertEquals(
                firstCreatedAt,
                persistedFirst.getCreatedAt()
        );

        assertEquals(
                secondCreatedAt,
                persistedSecond.getCreatedAt()
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
                ORGANIZATION_ID,
                "EFS-NOTIFICATION-PERSISTENCE-ORG",
                "EFS Notification Persistence Test Organization",
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
                TENANT_ID,
                ORGANIZATION_ID,
                "EFS-NOTIFICATION-PERSISTENCE-TENANT",
                "EFS Notification Persistence Test Tenant",
                "ACTIVE",
                "TEST"
        );
    }

    private void insertRecipientUser() {

        jdbcTemplate.update(
                """
                INSERT INTO administration.user_account (
                    user_id,
                    organization_id,
                    tenant_id,
                    username,
                    full_name,
                    email,
                    authentication_provider,
                    mfa_enabled,
                    account_status,
                    failed_login_attempts
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """,
                RECIPIENT_USER_ID,
                ORGANIZATION_ID,
                TENANT_ID,
                "efs.notification.persistence",
                "EFS Notification Persistence Test User",
                "efs.notification.persistence@example.com",
                "LOCAL",
                false,
                "ACTIVE",
                0
        );
    }
}