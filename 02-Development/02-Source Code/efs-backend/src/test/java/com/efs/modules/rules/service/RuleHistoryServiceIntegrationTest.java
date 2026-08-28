package com.efs.modules.rules.service;

import com.efs.modules.rules.dto.RuleHistoryRequest;
import com.efs.modules.rules.dto.RuleHistoryResponse;
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
class RuleHistoryServiceIntegrationTest {

    private static final UUID ORGANIZATION_ID =
            UUID.fromString(
                    "51515151-5151-5151-5151-515151515151"
            );

    private static final UUID USER_ID =
            UUID.fromString(
                    "52525252-5252-5252-5252-525252525252"
            );

    private static final UUID ENTITY_ID =
            UUID.fromString(
                    "53535353-5353-5353-5353-535353535353"
            );

    private static final UUID CORRELATION_ID =
            UUID.fromString(
                    "54545454-5454-5454-5454-545454545454"
            );

    @Autowired
    private RuleHistoryServiceInterface service;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {

        insertOrganization();
        insertUser();
    }

    @Test
    void shouldCreateAndRetrieveRuleHistoryById() {

        Map<String, Object> previousValue =
                Map.of(
                        "status", "DRAFT",
                        "priority", 1
                );

        Map<String, Object> currentValue =
                Map.of(
                        "status", "ACTIVE",
                        "priority", 2
                );

        RuleHistoryResponse created =
                service.createRuleHistory(
                        buildRequest(
                                "RULE",
                                ENTITY_ID,
                                "UPDATE",
                                previousValue,
                                currentValue,
                                "Rule activated",
                                USER_ID,
                                CORRELATION_ID
                        )
                );

        assertNotNull(
                created.getHistoryId()
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
                created.getOperationType()
        );

        assertEquals(
                "DRAFT",
                created.getPreviousValue().get("status")
        );

        assertEquals(
                "ACTIVE",
                created.getCurrentValue().get("status")
        );

        assertEquals(
                "Rule activated",
                created.getChangeReason()
        );

        assertEquals(
                USER_ID,
                created.getChangedBy()
        );

        assertNotNull(
                created.getChangedAt()
        );

        assertEquals(
                CORRELATION_ID,
                created.getCorrelationId()
        );

        RuleHistoryResponse retrieved =
                service.getRuleHistoryById(
                        created.getHistoryId()
                );

        assertEquals(
                created.getHistoryId(),
                retrieved.getHistoryId()
        );
    }

    @Test
    void shouldPreserveJsonPreviousAndCurrentValues() {

        Map<String, Object> previousValue =
                Map.of(
                        "severity", "MEDIUM",
                        "enabled", false
                );

        Map<String, Object> currentValue =
                Map.of(
                        "severity", "HIGH",
                        "enabled", true
                );

        RuleHistoryResponse created =
                service.createRuleHistory(
                        buildRequest(
                                "RULE",
                                ENTITY_ID,
                                "UPDATE",
                                previousValue,
                                currentValue,
                                "Severity updated",
                                USER_ID,
                                CORRELATION_ID
                        )
                );

        assertNotNull(
                created.getPreviousValue()
        );

        assertNotNull(
                created.getCurrentValue()
        );

        assertEquals(
                "MEDIUM",
                created.getPreviousValue().get("severity")
        );

        assertEquals(
                Boolean.FALSE,
                created.getPreviousValue().get("enabled")
        );

        assertEquals(
                "HIGH",
                created.getCurrentValue().get("severity")
        );

        assertEquals(
                Boolean.TRUE,
                created.getCurrentValue().get("enabled")
        );
    }

    @Test
    void shouldAllowOptionalHistoryValuesToBeNull() {

        RuleHistoryResponse created =
                service.createRuleHistory(
                        buildRequest(
                                "RULE",
                                ENTITY_ID,
                                "CREATE",
                                null,
                                Map.of(
                                        "status",
                                        "DRAFT"
                                ),
                                null,
                                USER_ID,
                                null
                        )
                );

        assertNotNull(
                created.getHistoryId()
        );

        assertNull(
                created.getPreviousValue()
        );

        assertNotNull(
                created.getCurrentValue()
        );

        assertNull(
                created.getChangeReason()
        );

        assertNull(
                created.getCorrelationId()
        );
    }

    @Test
    void shouldReturnRuleHistoriesByEntityOrderedByChangedAtDescending() {

        RuleHistoryResponse first =
                service.createRuleHistory(
                        buildRequest(
                                "RULE",
                                ENTITY_ID,
                                "CREATE",
                                null,
                                Map.of(
                                        "status",
                                        "DRAFT"
                                ),
                                "Rule created",
                                USER_ID,
                                CORRELATION_ID
                        )
                );

        sleepBriefly();

        RuleHistoryResponse second =
                service.createRuleHistory(
                        buildRequest(
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
                                ),
                                "Rule activated",
                                USER_ID,
                                CORRELATION_ID
                        )
                );

        List<RuleHistoryResponse> histories =
                service.getRuleHistoriesByEntity(
                        "RULE",
                        ENTITY_ID
                );

        assertEquals(
                2,
                histories.size()
        );

        assertEquals(
                second.getHistoryId(),
                histories.get(0).getHistoryId()
        );

        assertEquals(
                first.getHistoryId(),
                histories.get(1).getHistoryId()
        );
    }

    @Test
    void shouldReturnRuleHistoriesByChangedBy() {

        service.createRuleHistory(
                buildRequest(
                        "RULE",
                        UUID.randomUUID(),
                        "CREATE",
                        null,
                        Map.of(
                                "status",
                                "DRAFT"
                        ),
                        "Created",
                        USER_ID,
                        UUID.randomUUID()
                )
        );

        service.createRuleHistory(
                buildRequest(
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
                        ),
                        "Updated",
                        USER_ID,
                        UUID.randomUUID()
                )
        );

        List<RuleHistoryResponse> histories =
                service.getRuleHistoriesByChangedBy(
                        USER_ID
                );

        assertEquals(
                2,
                histories.size()
        );

        assertEquals(
                USER_ID,
                histories.get(0).getChangedBy()
        );

        assertEquals(
                USER_ID,
                histories.get(1).getChangedBy()
        );
    }

    @Test
    void shouldReturnRuleHistoriesByOperationType() {

        service.createRuleHistory(
                buildRequest(
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
                        ),
                        "Updated rule",
                        USER_ID,
                        UUID.randomUUID()
                )
        );

        service.createRuleHistory(
                buildRequest(
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
                        ),
                        "Updated policy",
                        USER_ID,
                        UUID.randomUUID()
                )
        );

        List<RuleHistoryResponse> histories =
                service.getRuleHistoriesByOperationType(
                        "UPDATE"
                );

        assertEquals(
                2,
                histories.size()
        );

        assertEquals(
                "UPDATE",
                histories.get(0).getOperationType()
        );

        assertEquals(
                "UPDATE",
                histories.get(1).getOperationType()
        );
    }

    @Test
    void shouldReturnRuleHistoriesByCorrelationId() {

        service.createRuleHistory(
                buildRequest(
                        "RULE",
                        UUID.randomUUID(),
                        "CREATE",
                        null,
                        Map.of(
                                "status",
                                "DRAFT"
                        ),
                        "Created",
                        USER_ID,
                        CORRELATION_ID
                )
        );

        service.createRuleHistory(
                buildRequest(
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
                        ),
                        "Updated",
                        USER_ID,
                        CORRELATION_ID
                )
        );

        List<RuleHistoryResponse> histories =
                service.getRuleHistoriesByCorrelationId(
                        CORRELATION_ID
                );

        assertEquals(
                2,
                histories.size()
        );

        assertEquals(
                CORRELATION_ID,
                histories.get(0).getCorrelationId()
        );

        assertEquals(
                CORRELATION_ID,
                histories.get(1).getCorrelationId()
        );
    }

    @Test
    void shouldRejectUnknownHistoryId() {

        UUID unknownHistoryId =
                UUID.randomUUID();

        assertThrows(
                ResourceNotFoundException.class,
                () -> service.getRuleHistoryById(
                        unknownHistoryId
                )
        );
    }

    private RuleHistoryRequest buildRequest(
            String entityType,
            UUID entityId,
            String operationType,
            Map<String, Object> previousValue,
            Map<String, Object> currentValue,
            String changeReason,
            UUID changedBy,
            UUID correlationId) {

        RuleHistoryRequest request =
                new RuleHistoryRequest();

        request.setEntityType(
                entityType
        );

        request.setEntityId(
                entityId
        );

        request.setOperationType(
                operationType
        );

        request.setPreviousValue(
                previousValue
        );

        request.setCurrentValue(
                currentValue
        );

        request.setChangeReason(
                changeReason
        );

        request.setChangedBy(
                changedBy
        );

        request.setCorrelationId(
                correlationId
        );

        return request;
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
                "EFS-RULE-HISTORY-TEST-ORG",
                "EFS Rule History Test Organization",
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
                "efs.rule.history.test",
                "EFS Rule History Test User",
                "efs.rule.history.test@example.com",
                "LOCAL",
                false,
                "ACTIVE",
                0
        );
    }

    private void sleepBriefly() {

        try {
            Thread.sleep(5);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
        }
    }
}