package com.efs.modules.rules.service;

import com.efs.modules.rules.dto.RuleParameterRequest;
import com.efs.modules.rules.dto.RuleParameterResponse;
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
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class RuleParameterServiceIntegrationTest {

    private static final UUID ORGANIZATION_ID =
            UUID.fromString(
                    "dddddddd-dddd-dddd-dddd-dddddddddddd"
            );

    private static final UUID CREATED_BY =
            UUID.fromString(
                    "eeeeeeee-eeee-eeee-eeee-eeeeeeeeeeee"
            );

    @Autowired
    private RuleParameterServiceInterface service;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {

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
                "EFS-RULE-PARAMETER-TEST-ORG",
                "EFS Rule Parameter Test Organization",
                "GT",
                "America/Guatemala",
                "ACTIVE"
        );

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
                CREATED_BY,
                ORGANIZATION_ID,
                "efs.rule.parameter.test",
                "EFS Rule Parameter Test User",
                "efs.rule.parameter.test@example.com",
                "LOCAL",
                false,
                "ACTIVE",
                0
        );
    }

    @Test
    void shouldCreateAndRetrieveRuleParameterById() {

        UUID ruleVersionId =
                createRuleVersion(
                        "RULE-PARAMETER-001"
                );

        RuleParameterResponse created =
                service.createRuleParameter(
                        ruleVersionId,
                        buildRequest(
                                "transaction_threshold",
                                "DECIMAL",
                                Map.of(
                                        "value",
                                        10000
                                ),
                                false,
                                "value > 0"
                        )
                );

        assertNotNull(
                created.getParameterId()
        );

        assertEquals(
                ruleVersionId,
                created.getRuleVersionId()
        );

        assertEquals(
                "transaction_threshold",
                created.getParameterName()
        );

        assertEquals(
                "DECIMAL",
                created.getParameterType()
        );

        assertEquals(
                10000,
                created.getParameterValue().get("value")
        );

        assertEquals(
                Boolean.FALSE,
                created.getIsSensitive()
        );

        assertEquals(
                "value > 0",
                created.getValidationExpression()
        );

        RuleParameterResponse retrieved =
                service.getRuleParameterById(
                        created.getParameterId()
                );

        assertEquals(
                created.getParameterId(),
                retrieved.getParameterId()
        );
    }

    @Test
    void shouldReturnParametersByRuleVersionId() {

        UUID ruleVersionId =
                createRuleVersion(
                        "RULE-PARAMETER-002"
                );

        service.createRuleParameter(
                ruleVersionId,
                buildRequest(
                        "minimum_amount",
                        "DECIMAL",
                        Map.of(
                                "value",
                                5000
                        ),
                        false,
                        null
                )
        );

        service.createRuleParameter(
                ruleVersionId,
                buildRequest(
                        "risk_threshold",
                        "INTEGER",
                        Map.of(
                                "value",
                                80
                        ),
                        false,
                        null
                )
        );

        List<RuleParameterResponse> parameters =
                service.getRuleParametersByRuleVersionId(
                        ruleVersionId
                );

        assertEquals(
                2,
                parameters.size()
        );
    }

    @Test
    void shouldReturnParametersByName() {

        UUID firstRuleVersionId =
                createRuleVersion(
                        "RULE-PARAMETER-003"
                );

        UUID secondRuleVersionId =
                createRuleVersion(
                        "RULE-PARAMETER-004"
                );

        service.createRuleParameter(
                firstRuleVersionId,
                buildRequest(
                        "risk_threshold",
                        "INTEGER",
                        Map.of(
                                "value",
                                80
                        ),
                        false,
                        null
                )
        );

        service.createRuleParameter(
                secondRuleVersionId,
                buildRequest(
                        "risk_threshold",
                        "INTEGER",
                        Map.of(
                                "value",
                                90
                        ),
                        false,
                        null
                )
        );

        List<RuleParameterResponse> parameters =
                service.getRuleParametersByName(
                        "risk_threshold"
                );

        assertEquals(
                2,
                parameters.size()
        );

        assertEquals(
                "risk_threshold",
                parameters.get(0).getParameterName()
        );

        assertEquals(
                "risk_threshold",
                parameters.get(1).getParameterName()
        );
    }

    @Test
    void shouldPreserveJsonParameterValue() {

        UUID ruleVersionId =
                createRuleVersion(
                        "RULE-PARAMETER-005"
                );

        Map<String, Object> parameterValue =
                Map.of(
                        "minimum",
                        1000,
                        "maximum",
                        10000,
                        "currency",
                        "GTQ"
                );

        RuleParameterResponse created =
                service.createRuleParameter(
                        ruleVersionId,
                        buildRequest(
                                "amount_range",
                                "RANGE",
                                parameterValue,
                                false,
                                null
                        )
                );

        assertEquals(
                1000,
                created.getParameterValue().get("minimum")
        );

        assertEquals(
                10000,
                created.getParameterValue().get("maximum")
        );

        assertEquals(
                "GTQ",
                created.getParameterValue().get("currency")
        );
    }

    @Test
    void shouldPreserveSensitiveIndicator() {

        UUID ruleVersionId =
                createRuleVersion(
                        "RULE-PARAMETER-006"
                );

        RuleParameterResponse created =
                service.createRuleParameter(
                        ruleVersionId,
                        buildRequest(
                                "protected_parameter",
                                "STRING",
                                Map.of(
                                        "value",
                                        "protected-value"
                                ),
                                true,
                                null
                        )
                );

        assertEquals(
                Boolean.TRUE,
                created.getIsSensitive()
        );
    }

    @Test
    void shouldAllowNullValidationExpression() {

        UUID ruleVersionId =
                createRuleVersion(
                        "RULE-PARAMETER-007"
                );

        RuleParameterResponse created =
                service.createRuleParameter(
                        ruleVersionId,
                        buildRequest(
                                "velocity_limit",
                                "INTEGER",
                                Map.of(
                                        "value",
                                        5
                                ),
                                false,
                                null
                        )
                );

        assertEquals(
                null,
                created.getValidationExpression()
        );
    }

    @Test
    void shouldRejectCreationForUnknownRuleVersion() {

        UUID unknownRuleVersionId =
                UUID.randomUUID();

        RuleParameterRequest request =
                buildRequest(
                        "risk_threshold",
                        "INTEGER",
                        Map.of(
                                "value",
                                80
                        ),
                        false,
                        null
                );

        assertThrows(
                ResourceNotFoundException.class,
                () -> service.createRuleParameter(
                        unknownRuleVersionId,
                        request
                )
        );
    }

    @Test
    void shouldRejectUnknownParameterId() {

        UUID unknownParameterId =
                UUID.randomUUID();

        assertThrows(
                ResourceNotFoundException.class,
                () -> service.getRuleParameterById(
                        unknownParameterId
                )
        );
    }

    @Test
    void shouldRejectUnknownRuleVersionWhenListingParameters() {

        UUID unknownRuleVersionId =
                UUID.randomUUID();

        assertThrows(
                ResourceNotFoundException.class,
                () -> service.getRuleParametersByRuleVersionId(
                        unknownRuleVersionId
                )
        );
    }

    private UUID createRuleVersion(
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
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
                """,
                ruleId,
                ruleCode,
                "Rule Parameter Integration Test",
                "Rule used by V53 integration tests",
                "TRANSACTION",
                "HIGH",
                (short) 1,
                "FRAUD_RULES",
                1,
                "ACTIVE"
        );

        UUID ruleVersionId =
                UUID.randomUUID();

        jdbcTemplate.update(
                """
                INSERT INTO rules.rule_version (
                    rule_version_id,
                    rule_id,
                    version_number,
                    effective_from,
                    effective_to,
                    publication_status,
                    change_summary,
                    created_by,
                    approved_by,
                    created_at
                )
                VALUES (?, ?, ?, CURRENT_TIMESTAMP, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP)
                """,
                ruleVersionId,
                ruleId,
                1,
                null,
                "DRAFT",
                "Initial rule version",
                CREATED_BY,
                null
        );

        return ruleVersionId;
    }

    private RuleParameterRequest buildRequest(
            String parameterName,
            String parameterType,
            Map<String, Object> parameterValue,
            boolean isSensitive,
            String validationExpression) {

        RuleParameterRequest request =
                new RuleParameterRequest();

        request.setParameterName(
                parameterName
        );

        request.setParameterType(
                parameterType
        );

        request.setParameterValue(
                parameterValue
        );

        request.setIsSensitive(
                isSensitive
        );

        request.setValidationExpression(
                validationExpression
        );

        return request;
    }
}