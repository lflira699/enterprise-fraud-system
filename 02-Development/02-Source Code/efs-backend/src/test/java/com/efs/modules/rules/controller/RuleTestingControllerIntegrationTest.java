package com.efs.modules.rules.controller;

import com.efs.modules.customer.entity.Customer;
import com.efs.modules.customer.repository.CustomerRepository;
import com.efs.modules.transaction.entity.Transaction;
import com.efs.modules.transaction.repository.TransactionRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class RuleTestingControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Test
    void shouldExecuteControlledTransactionRuleTest()
            throws Exception {

        UUID organizationId =
                UUID.randomUUID();

        UUID executedBy =
                UUID.randomUUID();

        UUID correlationId =
                UUID.randomUUID();

        insertOrganization(
                organizationId
        );

        insertUser(
                organizationId,
                executedBy
        );

        UUID ruleId =
                insertRule(
                        "RULE-TEST-HTTP-001"
                );

        UUID ruleVersionId =
                insertRuleVersion(
                        ruleId,
                        executedBy
                );

        insertCondition(
                ruleVersionId,
                "transaction.amount",
                "GREATER_THAN",
                "{\"value\":5000}"
        );

        UUID transactionId =
                insertTransaction(
                        organizationId,
                        executedBy,
                        new BigDecimal(
                                "7500.00"
                        )
                );

        String datasetReference =
                "transaction://"
                        + transactionId;

        String requestBody =
                """
                {
                    "simulationName":
                        "UC-025 HTTP transaction test",
                    "datasetReference":
                        "%s",
                    "executedBy":
                        "%s",
                    "correlationId":
                        "%s"
                }
                """.formatted(
                        datasetReference,
                        executedBy,
                        correlationId
                );

        mockMvc.perform(
                        post(
                                "/api/v1/rules/{ruleId}/versions/{ruleVersionId}/test",
                                ruleId,
                                ruleVersionId
                        )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        requestBody
                                )
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath(
                                "$.simulationId"
                        ).exists()
                )
                .andExpect(
                        jsonPath(
                                "$.simulationName"
                        ).value(
                                "UC-025 HTTP transaction test"
                        )
                )
                .andExpect(
                        jsonPath(
                                "$.entityType"
                        ).value(
                                "RULE_VERSION"
                        )
                )
                .andExpect(
                        jsonPath(
                                "$.entityId"
                        ).value(
                                ruleVersionId.toString()
                        )
                )
                .andExpect(
                        jsonPath(
                                "$.datasetReference"
                        ).value(
                                datasetReference
                        )
                )
                .andExpect(
                        jsonPath(
                                "$.sampleSize"
                        ).value(
                                1
                        )
                )
                .andExpect(
                        jsonPath(
                                "$.matchCount"
                        ).value(
                                1
                        )
                )
                .andExpect(
                        jsonPath(
                                "$.approveCount"
                        ).value(
                                0
                        )
                )
                .andExpect(
                        jsonPath(
                                "$.rejectCount"
                        ).value(
                                0
                        )
                )
                .andExpect(
                        jsonPath(
                                "$.reviewCount"
                        ).value(
                                0
                        )
                )
                .andExpect(
                        jsonPath(
                                "$.simulationStatus"
                        ).value(
                                "COMPLETED"
                        )
                );

        Number simulationCount =
                jdbcTemplate.queryForObject(
                        """
                        SELECT COUNT(*)
                        FROM rules.rule_simulation
                        WHERE entity_type = 'RULE_VERSION'
                          AND entity_id = ?
                          AND dataset_reference = ?
                          AND sample_size = 1
                          AND match_count = 1
                          AND approve_count = 0
                          AND reject_count = 0
                          AND review_count = 0
                          AND simulation_status = 'COMPLETED'
                          AND executed_by = ?
                        """,
                        Number.class,
                        ruleVersionId,
                        datasetReference,
                        executedBy
                );

        assertEquals(
                1L,
                simulationCount.longValue()
        );

        Number auditCount =
                jdbcTemplate.queryForObject(
                        """
                        SELECT COUNT(*)
                        FROM audit.audit_event
                        WHERE event_type = 'RULE_TEST_EXECUTED'
                          AND entity_type = 'RULE'
                          AND entity_id = ?
                          AND action = 'TEST'
                          AND source_component = 'RULE_ENGINE'
                          AND user_id = ?
                          AND correlation_id = ?
                          AND event_result = 'SUCCESS'
                        """,
                        Number.class,
                        ruleId,
                        executedBy,
                        correlationId
                );

        assertEquals(
                1L,
                auditCount.longValue()
        );

        Number historyCount =
                jdbcTemplate.queryForObject(
                        """
                        SELECT COUNT(*)
                        FROM rules.rule_history
                        WHERE entity_id = ?
                        """,
                        Number.class,
                        ruleId
                );

        assertEquals(
                0L,
                historyCount.longValue()
        );
    }

    @Test
    void shouldRejectBlankSimulationName()
            throws Exception {

        String requestBody =
                """
                {
                    "simulationName": "   ",
                    "datasetReference":
                        "transaction://00000000-0000-0000-0000-000000000001",
                    "executedBy":
                        "00000000-0000-0000-0000-000000000002"
                }
                """;

        mockMvc.perform(
                        post(
                                "/api/v1/rules/{ruleId}/versions/{ruleVersionId}/test",
                                UUID.randomUUID(),
                                UUID.randomUUID()
                        )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        requestBody
                                )
                )
                .andExpect(
                        status().isBadRequest()
                );
    }

    @Test
    void shouldRejectMissingActor()
            throws Exception {

        String requestBody =
                """
                {
                    "simulationName":
                        "Missing actor test",
                    "datasetReference":
                        "transaction://00000000-0000-0000-0000-000000000001"
                }
                """;

        mockMvc.perform(
                        post(
                                "/api/v1/rules/{ruleId}/versions/{ruleVersionId}/test",
                                UUID.randomUUID(),
                                UUID.randomUUID()
                        )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        requestBody
                                )
                )
                .andExpect(
                        status().isBadRequest()
                );
    }

    @Test
    void shouldRejectMalformedTransactionDatasetReference()
            throws Exception {

        UUID organizationId =
                UUID.randomUUID();

        UUID executedBy =
                UUID.randomUUID();

        insertOrganization(
                organizationId
        );

        insertUser(
                organizationId,
                executedBy
        );

        UUID ruleId =
                insertRule(
                        "RULE-TEST-HTTP-MALFORMED"
                );

        UUID ruleVersionId =
                insertRuleVersion(
                        ruleId,
                        executedBy
                );

        insertCondition(
                ruleVersionId,
                "transaction.amount",
                "GREATER_THAN",
                "{\"value\":5000}"
        );

        String requestBody =
                """
                {
                    "simulationName": "Malformed dataset test",
                    "datasetReference": "transaction://not-a-uuid",
                    "executedBy": "%s"
                }
                """.formatted(
                        executedBy
                );

        mockMvc.perform(
                        post(
                                "/api/v1/rules/{ruleId}/versions/{ruleVersionId}/test",
                                ruleId,
                                ruleVersionId
                        )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        requestBody
                                )
                )
                .andExpect(
                        status().isUnprocessableEntity()
                );
    }

    @Test
    void shouldReturnNotFoundForUnknownTransactionDataset()
            throws Exception {

        UUID organizationId =
                UUID.randomUUID();

        UUID executedBy =
                UUID.randomUUID();

        insertOrganization(
                organizationId
        );

        insertUser(
                organizationId,
                executedBy
        );

        UUID ruleId =
                insertRule(
                        "RULE-TEST-HTTP-UNKNOWN-DATASET"
                );

        UUID ruleVersionId =
                insertRuleVersion(
                        ruleId,
                        executedBy
                );

        insertCondition(
                ruleVersionId,
                "transaction.amount",
                "GREATER_THAN",
                "{\"value\":5000}"
        );

        UUID unknownTransactionId =
                UUID.randomUUID();

        String requestBody =
                """
                {
                    "simulationName": "Unknown transaction test",
                    "datasetReference": "transaction://%s",
                    "executedBy": "%s"
                }
                """.formatted(
                        unknownTransactionId,
                        executedBy
                );

        mockMvc.perform(
                        post(
                                "/api/v1/rules/{ruleId}/versions/{ruleVersionId}/test",
                                ruleId,
                                ruleVersionId
                        )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        requestBody
                                )
                )
                .andExpect(
                        status().isNotFound()
                );
    }

    private UUID insertTransaction(
            UUID organizationId,
            UUID executedBy,
            BigDecimal amount) {

        LocalDateTime now =
                LocalDateTime.now();

        Customer customer =
                new Customer();

        customer.setCustomerNumber(
                "UC025-"
                        + UUID.randomUUID()
        );

        customer.setCustomerType(
                "INDIVIDUAL"
        );

        customer.setFirstName(
                "UC025"
        );

        customer.setLastName(
                "Testing"
        );

        customer.setRiskLevel(
                "LOW"
        );

        customer.setRiskScore(
                BigDecimal.ZERO
        );

        customer.setCustomerStatus(
                "ACTIVE"
        );

        customer.setCreatedAt(
                now
        );

        customer.setUpdatedAt(
                now
        );

        customer.setRecordStatus(
                "ACTIVE"
        );

        customer.setRecordVersion(
                0
        );

        Customer savedCustomer =
                customerRepository.saveAndFlush(
                        customer
                );

        Transaction transaction =
                new Transaction();

        transaction.setTransactionReference(
                "UC025-TXN-"
                        + UUID.randomUUID()
        );

        transaction.setCustomerId(
                savedCustomer.getCustomerId()
        );

        transaction.setOrganizationId(
                organizationId
        );

        transaction.setTransactionType(
                "PAYMENT"
        );

        transaction.setTransactionSubtype(
                "CARD"
        );

        transaction.setAmount(
                amount
        );

        transaction.setCurrencyCode(
                "GTQ"
        );

        transaction.setTransactionDatetime(
                now
        );

        transaction.setTransactionStatus(
                "RECEIVED"
        );

        transaction.setFinalDecision(
                "PENDING"
        );

        transaction.setFraudScore(
                BigDecimal.ZERO
        );

        transaction.setCreatedAt(
                now
        );

        transaction.setUpdatedAt(
                now
        );

        transaction.setCreatedBy(
                executedBy
        );

        transaction.setRecordVersion(
                0
        );

        return transactionRepository
                .saveAndFlush(
                        transaction
                )
                .getTransactionId();
    }

    private UUID insertRule(
            String ruleCode) {

        UUID ruleId =
                UUID.randomUUID();

        jdbcTemplate.update(
                """
                INSERT INTO rules.rule (
                    rule_id,
                    rule_code,
                    rule_name,
                    description,
                    category,
                    severity,
                    priority,
                    owner_team,
                    current_version,
                    status,
                    created_at,
                    updated_at
                )
                VALUES (
                    ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,
                    CURRENT_TIMESTAMP,
                    CURRENT_TIMESTAMP
                )
                """,
                ruleId,
                ruleCode,
                "UC-025 HTTP Rule",
                "Rule used by UC-025 HTTP integration test",
                "TRANSACTION",
                "HIGH",
                (short) 1,
                "FRAUD_RULES",
                1,
                "INACTIVE"
        );

        return ruleId;
    }

    private UUID insertRuleVersion(
            UUID ruleId,
            UUID createdBy) {

        UUID ruleVersionId =
                UUID.randomUUID();

        jdbcTemplate.update(
                """
                INSERT INTO rules.rule_version (
                    rule_version_id,
                    rule_id,
                    version_number,
                    rule_name,
                    description,
                    category,
                    severity,
                    priority,
                    owner_team,
                    effective_from,
                    effective_to,
                    publication_status,
                    change_summary,
                    created_by,
                    approved_by,
                    created_at
                )
                VALUES (
                    ?, ?, ?, ?, ?, ?, ?, ?, ?,
                    CURRENT_TIMESTAMP,
                    ?, ?, ?, ?, ?,
                    CURRENT_TIMESTAMP
                )
                """,
                ruleVersionId,
                ruleId,
                1,
                "UC-025 HTTP Rule Version",
                "DRAFT version used for controlled testing",
                "TRANSACTION",
                "HIGH",
                (short) 1,
                "FRAUD_RULES",
                null,
                "DRAFT",
                "UC-025 controlled test",
                createdBy,
                null
        );

        return ruleVersionId;
    }

    private void insertCondition(
            UUID ruleVersionId,
            String attributeName,
            String comparisonOperator,
            String comparisonValueJson) {

        jdbcTemplate.update(
                """
                INSERT INTO rules.rule_condition (
                    condition_id,
                    rule_version_id,
                    condition_order,
                    attribute_name,
                    comparison_operator,
                    comparison_value,
                    logical_operator,
                    is_required
                )
                VALUES (
                    uuidv7(),
                    ?,
                    1,
                    ?,
                    ?,
                    CAST(? AS jsonb),
                    NULL,
                    TRUE
                )
                """,
                ruleVersionId,
                attributeName,
                comparisonOperator,
                comparisonValueJson
        );
    }

    private void insertOrganization(
            UUID organizationId) {

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
                "UC025-" + organizationId,
                "UC-025 HTTP Test Organization",
                "GT",
                "America/Guatemala",
                "ACTIVE"
        );
    }

    private void insertUser(
            UUID organizationId,
            UUID userId) {

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
                userId,
                organizationId,
                "uc025." + userId,
                "UC-025 Rule Administrator",
                userId + "@uc025.example.com",
                "LOCAL",
                false,
                "ACTIVE",
                0
        );
    }
}
