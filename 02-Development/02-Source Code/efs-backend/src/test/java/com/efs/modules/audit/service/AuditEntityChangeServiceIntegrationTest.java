package com.efs.modules.audit.service;

import com.efs.modules.audit.dto.AuditEntityChangeRequest;
import com.efs.modules.audit.dto.AuditEntityChangeResponse;
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

@SpringBootTest
@Transactional
class AuditEntityChangeServiceIntegrationTest {

    private static final UUID AUDIT_EVENT_ID =
            UUID.fromString(
                    "73737373-7373-7373-7373-737373737373"
            );

    private static final UUID ENTITY_ID =
            UUID.fromString(
                    "74747474-7474-7474-7474-747474747474"
            );

    @Autowired
    private AuditEntityChangeServiceInterface service;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {

        insertAuditEvent(
                AUDIT_EVENT_ID
        );
    }

    @Test
    void shouldCreateAndRetrieveAuditEntityChangeById() {

        AuditEntityChangeResponse created =
                service.createAuditEntityChange(
                        buildRequest(
                                AUDIT_EVENT_ID,
                                "RULE",
                                ENTITY_ID,
                                "UPDATE",
                                Map.of(
                                        "status",
                                        "DRAFT"
                                ),
                                Map.of(
                                        "status",
                                        "ACTIVE"
                                )
                        )
                );

        assertNotNull(
                created.getChangeId()
        );

        assertEquals(
                AUDIT_EVENT_ID,
                created.getAuditEventId()
        );

        assertEquals(
                "RULE",
                created.getEntityType()
        );

        assertEquals(
                ENTITY_ID,
                created.getEntityId()
        );

        assertEquals(
                "UPDATE",
                created.getOperation()
        );

        assertEquals(
                "DRAFT",
                created.getPreviousValue().get(
                        "status"
                )
        );

        assertEquals(
                "ACTIVE",
                created.getCurrentValue().get(
                        "status"
                )
        );

        assertNotNull(
                created.getChangedAt()
        );

        AuditEntityChangeResponse retrieved =
                service.getAuditEntityChangeById(
                        created.getChangeId()
                );

        assertEquals(
                created.getChangeId(),
                retrieved.getChangeId()
        );
    }

    @Test
    void shouldPreserveJsonPreviousAndCurrentValues() {

        AuditEntityChangeResponse created =
                service.createAuditEntityChange(
                        buildRequest(
                                AUDIT_EVENT_ID,
                                "RULE",
                                ENTITY_ID,
                                "UPDATE",
                                Map.of(
                                        "status", "DRAFT",
                                        "priority", 1,
                                        "enabled", false
                                ),
                                Map.of(
                                        "status", "ACTIVE",
                                        "priority", 2,
                                        "enabled", true
                                )
                        )
                );

        assertNotNull(
                created.getPreviousValue()
        );

        assertNotNull(
                created.getCurrentValue()
        );

        assertEquals(
                "DRAFT",
                created.getPreviousValue().get(
                        "status"
                )
        );

        assertEquals(
                1,
                created.getPreviousValue().get(
                        "priority"
                )
        );

        assertEquals(
                Boolean.FALSE,
                created.getPreviousValue().get(
                        "enabled"
                )
        );

        assertEquals(
                "ACTIVE",
                created.getCurrentValue().get(
                        "status"
                )
        );

        assertEquals(
                2,
                created.getCurrentValue().get(
                        "priority"
                )
        );

        assertEquals(
                Boolean.TRUE,
                created.getCurrentValue().get(
                        "enabled"
                )
        );
    }

    @Test
    void shouldAllowPreviousValueToBeNull() {

        AuditEntityChangeResponse created =
                service.createAuditEntityChange(
                        buildRequest(
                                AUDIT_EVENT_ID,
                                "RULE",
                                ENTITY_ID,
                                "CREATE",
                                null,
                                Map.of(
                                        "status",
                                        "DRAFT"
                                )
                        )
                );

        assertNotNull(
                created.getChangeId()
        );

        assertNull(
                created.getPreviousValue()
        );

        assertNotNull(
                created.getCurrentValue()
        );

        assertEquals(
                "CREATE",
                created.getOperation()
        );
    }

    @Test
    void shouldReturnAuditEntityChangesByAuditEventId() {

        service.createAuditEntityChange(
                buildRequest(
                        AUDIT_EVENT_ID,
                        "RULE",
                        UUID.randomUUID(),
                        "CREATE",
                        null,
                        Map.of(
                                "status",
                                "DRAFT"
                        )
                )
        );

        service.createAuditEntityChange(
                buildRequest(
                        AUDIT_EVENT_ID,
                        "POLICY",
                        UUID.randomUUID(),
                        "UPDATE",
                        Map.of(
                                "status",
                                "DRAFT"
                        ),
                        Map.of(
                                "status",
                                "ACTIVE"
                        )
                )
        );

        List<AuditEntityChangeResponse> changes =
                service.getAuditEntityChangesByAuditEventId(
                        AUDIT_EVENT_ID
                );

        assertEquals(
                2,
                changes.size()
        );

        assertEquals(
                AUDIT_EVENT_ID,
                changes.get(0).getAuditEventId()
        );

        assertEquals(
                AUDIT_EVENT_ID,
                changes.get(1).getAuditEventId()
        );
    }

    @Test
    void shouldReturnAuditEntityChangesByEntity() {

        service.createAuditEntityChange(
                buildRequest(
                        AUDIT_EVENT_ID,
                        "RULE",
                        ENTITY_ID,
                        "CREATE",
                        null,
                        Map.of(
                                "status",
                                "DRAFT"
                        )
                )
        );

        service.createAuditEntityChange(
                buildRequest(
                        AUDIT_EVENT_ID,
                        "RULE",
                        ENTITY_ID,
                        "UPDATE",
                        Map.of(
                                "status",
                                "DRAFT"
                        ),
                        Map.of(
                                "status",
                                "ACTIVE"
                        )
                )
        );

        List<AuditEntityChangeResponse> changes =
                service.getAuditEntityChangesByEntity(
                        "RULE",
                        ENTITY_ID
                );

        assertEquals(
                2,
                changes.size()
        );

        assertEquals(
                ENTITY_ID,
                changes.get(0).getEntityId()
        );

        assertEquals(
                ENTITY_ID,
                changes.get(1).getEntityId()
        );
    }

    @Test
    void shouldReturnAuditEntityChangesByOperation() {

        service.createAuditEntityChange(
                buildRequest(
                        AUDIT_EVENT_ID,
                        "RULE",
                        UUID.randomUUID(),
                        "UPDATE",
                        Map.of(
                                "status",
                                "DRAFT"
                        ),
                        Map.of(
                                "status",
                                "ACTIVE"
                        )
                )
        );

        service.createAuditEntityChange(
                buildRequest(
                        AUDIT_EVENT_ID,
                        "POLICY",
                        UUID.randomUUID(),
                        "UPDATE",
                        Map.of(
                                "priority",
                                1
                        ),
                        Map.of(
                                "priority",
                                2
                        )
                )
        );

        List<AuditEntityChangeResponse> changes =
                service.getAuditEntityChangesByOperation(
                        "UPDATE"
                );

        assertEquals(
                2,
                changes.size()
        );

        assertEquals(
                "UPDATE",
                changes.get(0).getOperation()
        );

        assertEquals(
                "UPDATE",
                changes.get(1).getOperation()
        );
    }

    @Test
    void shouldRejectUnknownAuditEntityChangeId() {

        UUID unknownChangeId =
                UUID.randomUUID();

        assertThrows(
                ResourceNotFoundException.class,
                () -> service.getAuditEntityChangeById(
                        unknownChangeId
                )
        );
    }

    @Test
    void shouldRejectUnknownAuditEventIdWhenCreatingChange() {

        UUID unknownAuditEventId =
                UUID.randomUUID();

        AuditEntityChangeRequest request =
                buildRequest(
                        unknownAuditEventId,
                        "RULE",
                        ENTITY_ID,
                        "UPDATE",
                        Map.of(
                                "status",
                                "DRAFT"
                        ),
                        Map.of(
                                "status",
                                "ACTIVE"
                        )
                );

        assertThrows(
                ResourceNotFoundException.class,
                () -> service.createAuditEntityChange(
                        request
                )
        );
    }

    private AuditEntityChangeRequest buildRequest(
            UUID auditEventId,
            String entityType,
            UUID entityId,
            String operation,
            Map<String, Object> previousValue,
            Map<String, Object> currentValue) {

        AuditEntityChangeRequest request =
                new AuditEntityChangeRequest();

        request.setAuditEventId(
                auditEventId
        );

        request.setEntityType(
                entityType
        );

        request.setEntityId(
                entityId
        );

        request.setOperation(
                operation
        );

        request.setPreviousValue(
                previousValue
        );

        request.setCurrentValue(
                currentValue
        );

        return request;
    }

    private void insertAuditEvent(
            UUID auditEventId) {

        jdbcTemplate.update(
                """
                INSERT INTO audit.audit_event (
                    audit_event_id,
                    event_timestamp,
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
                    ?
                )
                """,
                auditEventId,
                "ENTITY_CHANGE_TEST",
                "UPDATE",
                "AUDIT",
                "SUCCESS"
        );
    }
}