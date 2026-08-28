package com.efs.modules.audit.service;

import com.efs.modules.audit.dto.AuditConfigurationChangeRequest;
import com.efs.modules.audit.dto.AuditConfigurationChangeResponse;
import com.efs.shared.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
class AuditConfigurationChangeServiceIntegrationTest {

    private static final UUID ORGANIZATION_ID =
            UUID.fromString(
                    "91919191-9191-9191-9191-919191919191"
            );

    private static final UUID USER_ID =
            UUID.fromString(
                    "92929292-9292-9292-9292-929292929292"
            );

    private static final UUID AUDIT_EVENT_ID =
            UUID.fromString(
                    "93939393-9393-9393-9393-939393939393"
            );

    @Autowired
    private AuditConfigurationChangeServiceInterface
            auditConfigurationChangeService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {

        insertOrganization();
        insertUser();
        insertAuditEvent();
    }

    @Test
    void shouldCreateAndRetrieveAuditConfigurationChangeById() {

        AuditConfigurationChangeRequest request =
                new AuditConfigurationChangeRequest();

        request.setAuditEventId(
                AUDIT_EVENT_ID
        );

        request.setConfigurationKey(
                "fraud.rules.threshold"
        );

        request.setPreviousValue(
                Map.of(
                        "value",
                        70
                )
        );

        request.setCurrentValue(
                Map.of(
                        "value",
                        80
                )
        );

        request.setChangedBy(
                USER_ID
        );

        request.setChangeReason(
                "Controlled configuration update"
        );

        AuditConfigurationChangeResponse created =
                auditConfigurationChangeService
                        .createAuditConfigurationChange(
                                request
                        );

        assertNotNull(
                created
        );

        assertNotNull(
                created.getConfigurationChangeId()
        );

        assertEquals(
                AUDIT_EVENT_ID,
                created.getAuditEventId()
        );

        assertEquals(
                "fraud.rules.threshold",
                created.getConfigurationKey()
        );

        assertEquals(
                70,
                created.getPreviousValue().get("value")
        );

        assertEquals(
                80,
                created.getCurrentValue().get("value")
        );

        assertEquals(
                USER_ID,
                created.getChangedBy()
        );

        assertEquals(
                "Controlled configuration update",
                created.getChangeReason()
        );

        assertNotNull(
                created.getChangedAt()
        );

        AuditConfigurationChangeResponse retrieved =
                auditConfigurationChangeService
                        .getAuditConfigurationChangeById(
                                created.getConfigurationChangeId()
                        );

        assertEquals(
                created.getConfigurationChangeId(),
                retrieved.getConfigurationChangeId()
        );
    }

    @Test
    void shouldAllowOptionalValuesToBeNull() {

        AuditConfigurationChangeRequest request =
                new AuditConfigurationChangeRequest();

        request.setAuditEventId(
                AUDIT_EVENT_ID
        );

        request.setConfigurationKey(
                "fraud.feature.toggle"
        );

        request.setChangedBy(
                USER_ID
        );

        AuditConfigurationChangeResponse created =
                auditConfigurationChangeService
                        .createAuditConfigurationChange(
                                request
                        );

        assertNotNull(
                created.getConfigurationChangeId()
        );

        assertNull(
                created.getPreviousValue()
        );

        assertNull(
                created.getCurrentValue()
        );

        assertNull(
                created.getChangeReason()
        );

        assertNotNull(
                created.getChangedAt()
        );
    }

    @Test
    void shouldReturnConfigurationChangesByAuditEventId() {

        insertConfigurationChange(
                UUID.randomUUID(),
                AUDIT_EVENT_ID,
                "fraud.rules.threshold",
                USER_ID
        );

        insertConfigurationChange(
                UUID.randomUUID(),
                AUDIT_EVENT_ID,
                "fraud.feature.toggle",
                USER_ID
        );

        List<AuditConfigurationChangeResponse> results =
                auditConfigurationChangeService
                        .getAuditConfigurationChangesByAuditEventId(
                                AUDIT_EVENT_ID
                        );

        assertEquals(
                2,
                results.size()
        );

        assertTrue(
                results.stream()
                        .allMatch(
                                result ->
                                        AUDIT_EVENT_ID.equals(
                                                result.getAuditEventId()
                                        )
                        )
        );
    }

    @Test
    void shouldReturnConfigurationChangesByConfigurationKey() {

        insertConfigurationChange(
                UUID.randomUUID(),
                AUDIT_EVENT_ID,
                "fraud.rules.threshold",
                USER_ID
        );

        insertConfigurationChange(
                UUID.randomUUID(),
                AUDIT_EVENT_ID,
                "fraud.rules.threshold",
                USER_ID
        );

        List<AuditConfigurationChangeResponse> results =
                auditConfigurationChangeService
                        .getAuditConfigurationChangesByConfigurationKey(
                                "fraud.rules.threshold"
                        );

        assertEquals(
                2,
                results.size()
        );

        assertTrue(
                results.stream()
                        .allMatch(
                                result ->
                                        "fraud.rules.threshold".equals(
                                                result.getConfigurationKey()
                                        )
                        )
        );
    }

    @Test
    void shouldReturnConfigurationChangesByChangedBy() {

        insertConfigurationChange(
                UUID.randomUUID(),
                AUDIT_EVENT_ID,
                "fraud.rules.threshold",
                USER_ID
        );

        insertConfigurationChange(
                UUID.randomUUID(),
                AUDIT_EVENT_ID,
                "fraud.feature.toggle",
                USER_ID
        );

        List<AuditConfigurationChangeResponse> results =
                auditConfigurationChangeService
                        .getAuditConfigurationChangesByChangedBy(
                                USER_ID
                        );

        assertEquals(
                2,
                results.size()
        );

        assertTrue(
                results.stream()
                        .allMatch(
                                result ->
                                        USER_ID.equals(
                                                result.getChangedBy()
                                        )
                        )
        );
    }

    @Test
    void shouldRejectUnknownConfigurationChangeId() {

        UUID unknownConfigurationChangeId =
                UUID.randomUUID();

        assertThrows(
                ResourceNotFoundException.class,
                () ->
                        auditConfigurationChangeService
                                .getAuditConfigurationChangeById(
                                        unknownConfigurationChangeId
                                )
        );
    }

    private void insertConfigurationChange(
            UUID configurationChangeId,
            UUID auditEventId,
            String configurationKey,
            UUID changedBy) {

        jdbcTemplate.update(
                """
                INSERT INTO audit.audit_configuration_change (
                    configuration_change_id,
                    audit_event_id,
                    configuration_key,
                    previous_value,
                    current_value,
                    changed_by,
                    change_reason,
                    changed_at
                )
                VALUES (
                    ?,
                    ?,
                    ?,
                    CAST(? AS jsonb),
                    CAST(? AS jsonb),
                    ?,
                    ?,
                    clock_timestamp()
                )
                """,
                configurationChangeId,
                auditEventId,
                configurationKey,
                "{\"value\":70}",
                "{\"value\":80}",
                changedBy,
                "Integration test change"
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
                "EFS-AUDIT-CONFIG-ORG",
                "EFS Audit Configuration Organization",
                "GT",
                "America/Guatemala",
                "ACTIVE"
        );
    }

    private void insertUser() {

        jdbcTemplate.update(
                """
                INSERT INTO administration.user_account (
                    user_id,
                    organization_id,
                    username,
                    full_name,
                    email,
                    authentication_provider,
                    mfa_enabled,
                    account_status,
                    failed_login_attempts
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """,
                USER_ID,
                ORGANIZATION_ID,
                "efs.audit.configuration",
                "EFS Audit Configuration User",
                "efs.audit.configuration@example.com",
                "LOCAL",
                false,
                "ACTIVE",
                0
        );
    }

    private void insertAuditEvent() {

        jdbcTemplate.update(
                """
                INSERT INTO audit.audit_event (
                    audit_event_id,
                    event_timestamp,
                    organization_id,
                    user_id,
                    event_type,
                    action,
                    source_component,
                    event_result
                )
                VALUES (
                    ?,
                    clock_timestamp(),
                    ?,
                    ?,
                    ?,
                    ?,
                    ?,
                    ?
                )
                """,
                AUDIT_EVENT_ID,
                ORGANIZATION_ID,
                USER_ID,
                "CONFIGURATION_CHANGE_TEST",
                "UPDATE_CONFIGURATION",
                "AUDIT",
                "SUCCESS"
        );
    }
}