package com.efs.modules.rules.service;

import com.efs.modules.rules.dto.RuleMetricRequest;
import com.efs.modules.rules.dto.RuleMetricResponse;
import com.efs.shared.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class RuleMetricServiceIntegrationTest {

    private static final UUID RULE_ID =
            UUID.fromString(
                    "32323232-3232-3232-3232-323232323232"
            );

    private static final LocalDate METRIC_DATE =
            LocalDate.of(2026, 8, 22);

    @Autowired
    private RuleMetricServiceInterface service;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {

        insertRule();
    }

    @Test
    void shouldCreateAndRetrieveRuleMetricById() {

        RuleMetricResponse created =
                service.createRuleMetric(
                        buildRequest(
                                METRIC_DATE,
                                100L,
                                25L,
                                10L,
                                5L,
                                2L,
                                "12.50",
                                "15000.75",
                                "GTQ"
                        )
                );

        assertNotNull(
                created.getMetricId()
        );

        assertEquals(
                RULE_ID,
                created.getRuleId()
        );

        assertNull(
                created.getRuleVersionId()
        );

        assertEquals(
                METRIC_DATE,
                created.getMetricDate()
        );

        assertEquals(
                Long.valueOf(100L),
                created.getExecutionCount()
        );

        assertEquals(
                Long.valueOf(25L),
                created.getMatchCount()
        );

        assertEquals(
                Long.valueOf(10L),
                created.getConfirmedFraudCount()
        );

        assertEquals(
                Long.valueOf(5L),
                created.getFalsePositiveCount()
        );

        assertEquals(
                Long.valueOf(2L),
                created.getFalseNegativeCount()
        );

        assertEquals(
                0,
                new BigDecimal("12.50")
                        .compareTo(created.getAverageExecutionMs())
        );

        assertEquals(
                0,
                new BigDecimal("15000.75")
                        .compareTo(created.getPreventedAmount())
        );

        assertEquals(
                "GTQ",
                created.getCurrencyCode()
        );

        assertNotNull(
                created.getCalculatedAt()
        );

        RuleMetricResponse retrieved =
                service.getRuleMetricById(
                        created.getMetricId()
                );

        assertEquals(
                created.getMetricId(),
                retrieved.getMetricId()
        );

        assertEquals(
                "GTQ",
                retrieved.getCurrencyCode()
        );
    }

    @Test
    void shouldReturnMetricsByRuleIdOrderedByMetricDateDescending() {

        service.createRuleMetric(
                buildRequest(
                        LocalDate.of(2026, 8, 20),
                        50L,
                        10L,
                        4L,
                        2L,
                        1L,
                        "10.00",
                        "5000.00",
                        "GTQ"
                )
        );

        service.createRuleMetric(
                buildRequest(
                        LocalDate.of(2026, 8, 22),
                        80L,
                        20L,
                        8L,
                        3L,
                        1L,
                        "11.00",
                        "9000.00",
                        "GTQ"
                )
        );

        List<RuleMetricResponse> metrics =
                service.getRuleMetricsByRuleId(
                        RULE_ID
                );

        assertEquals(
                2,
                metrics.size()
        );

        assertEquals(
                LocalDate.of(2026, 8, 22),
                metrics.get(0).getMetricDate()
        );

        assertEquals(
                LocalDate.of(2026, 8, 20),
                metrics.get(1).getMetricDate()
        );
    }

    @Test
    void shouldReturnMetricsByDate() {

        service.createRuleMetric(
                buildRequest(
                        METRIC_DATE,
                        120L,
                        30L,
                        12L,
                        6L,
                        3L,
                        "13.25",
                        "20000.00",
                        "USD"
                )
        );

        List<RuleMetricResponse> metrics =
                service.getRuleMetricsByDate(
                        METRIC_DATE
                );

        assertEquals(
                1,
                metrics.size()
        );

        assertEquals(
                METRIC_DATE,
                metrics.get(0).getMetricDate()
        );

        assertEquals(
                RULE_ID,
                metrics.get(0).getRuleId()
        );

        assertEquals(
                "USD",
                metrics.get(0).getCurrencyCode()
        );
    }

    @Test
    void shouldAllowOptionalFalseNegativeCountAndCurrencyCode() {

        RuleMetricResponse created =
                service.createRuleMetric(
                        buildRequest(
                                METRIC_DATE,
                                25L,
                                5L,
                                2L,
                                1L,
                                null,
                                "8.50",
                                "2500.00",
                                null
                        )
                );

        assertNotNull(
                created.getMetricId()
        );

        assertNull(
                created.getFalseNegativeCount()
        );

        assertNull(
                created.getCurrencyCode()
        );
    }

    @Test
    void shouldReturnEmptyListForUnknownRuleVersionId() {

        List<RuleMetricResponse> metrics =
                service.getRuleMetricsByRuleVersionId(
                        UUID.randomUUID()
                );

        assertNotNull(
                metrics
        );

        assertEquals(
                0,
                metrics.size()
        );
    }

    @Test
    void shouldRejectUnknownMetricId() {

        UUID unknownMetricId =
                UUID.randomUUID();

        assertThrows(
                ResourceNotFoundException.class,
                () -> service.getRuleMetricById(
                        unknownMetricId
                )
        );
    }

    private RuleMetricRequest buildRequest(
            LocalDate metricDate,
            Long executionCount,
            Long matchCount,
            Long confirmedFraudCount,
            Long falsePositiveCount,
            Long falseNegativeCount,
            String averageExecutionMs,
            String preventedAmount,
            String currencyCode) {

        RuleMetricRequest request =
                new RuleMetricRequest();

        request.setRuleId(
                RULE_ID
        );

        request.setRuleVersionId(
                null
        );

        request.setMetricDate(
                metricDate
        );

        request.setExecutionCount(
                executionCount
        );

        request.setMatchCount(
                matchCount
        );

        request.setConfirmedFraudCount(
                confirmedFraudCount
        );

        request.setFalsePositiveCount(
                falsePositiveCount
        );

        request.setFalseNegativeCount(
                falseNegativeCount
        );

        request.setAverageExecutionMs(
                new BigDecimal(averageExecutionMs)
        );

        request.setPreventedAmount(
                new BigDecimal(preventedAmount)
        );

        request.setCurrencyCode(
                currencyCode
        );

        return request;
    }

    private void insertRule() {

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
                RULE_ID,
                "RULE-METRIC-001",
                "Rule Metric Integration Test",
                "Rule used by V57 integration tests",
                "TRANSACTION",
                "HIGH",
                (short) 1,
                "FRAUD_RULES",
                1,
                "ACTIVE"
        );
    }
}