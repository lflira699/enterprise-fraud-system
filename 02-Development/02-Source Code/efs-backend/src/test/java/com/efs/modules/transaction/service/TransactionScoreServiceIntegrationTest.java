package com.efs.modules.transaction.service;

import com.efs.modules.transaction.dto.TransactionScoreRequest;
import com.efs.modules.transaction.dto.TransactionScoreResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@Transactional
class TransactionScoreServiceIntegrationTest {

    private static final UUID CUSTOMER_ID =
            UUID.fromString(
                    "77777777-7777-7777-7777-777777777777"
            );

    private static final UUID TRANSACTION_ID =
            UUID.fromString(
                    "88888888-8888-8888-8888-888888888888"
            );

    private static final UUID ORGANIZATION_ID =
            UUID.fromString(
                    "99999999-9999-9999-9999-999999999999"
            );

    private static final UUID CREATED_BY =
            UUID.fromString(
                    "aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa"
            );

    @Autowired
    private TransactionScoreServiceInterface service;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {

        jdbcTemplate.update(
                """
                INSERT INTO customer.customer (
                    customer_id,
                    customer_number,
                    customer_type,
                    risk_level,
                    risk_score,
                    customer_status,
                    record_status,
                    record_version
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """,
                CUSTOMER_ID,
                "EFS-SCORE-TEST-CUSTOMER",
                "INDIVIDUAL",
                "LOW",
                BigDecimal.ZERO,
                "ACTIVE",
                "ACTIVE",
                1
        );

        jdbcTemplate.update(
                """
                INSERT INTO transaction.transaction (
                    transaction_id,
                    transaction_reference,
                    customer_id,
                    organization_id,
                    transaction_type,
                    amount,
                    currency_code,
                    transaction_status,
                    final_decision,
                    fraud_score,
                    created_by,
                    record_version
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """,
                TRANSACTION_ID,
                "EFS-SCORE-TEST-TRANSACTION",
                CUSTOMER_ID,
                ORGANIZATION_ID,
                "TEST",
                new BigDecimal("100.00"),
                "GTQ",
                "RECEIVED",
                "PENDING",
                BigDecimal.ZERO,
                CREATED_BY,
                1
        );
    }

    @Test
    void shouldCreateAndRetrieveTransactionScore() {

        TransactionScoreRequest request =
                buildRequest(
                        "RULES",
                        new BigDecimal("25.00"),
                        new BigDecimal("40.00"),
                        "EFS-RISK",
                        "1.0"
                );

        TransactionScoreResponse created =
                service.createScore(
                        TRANSACTION_ID,
                        request
                );

        assertNotNull(created.getScoreId());

        assertEquals(
                TRANSACTION_ID,
                created.getTransactionId()
        );

        assertEquals(
                "RULES",
                created.getScoreType()
        );

        assertEquals(
                new BigDecimal("25.00"),
                created.getScoreValue()
        );

        assertEquals(
                new BigDecimal("40.00"),
                created.getScoreWeight()
        );

        assertNotNull(
                created.getCalculatedAt()
        );

        TransactionScoreResponse retrieved =
                service.getScoreById(
                        created.getScoreId()
                );

        assertEquals(
                created.getScoreId(),
                retrieved.getScoreId()
        );
    }

    @Test
    void shouldReturnScoresByTransaction() {

        service.createScore(
                TRANSACTION_ID,
                buildRequest(
                        "RULES",
                        new BigDecimal("20.00"),
                        new BigDecimal("40.00"),
                        "EFS-RISK",
                        "1.0"
                )
        );

        service.createScore(
                TRANSACTION_ID,
                buildRequest(
                        "BEHAVIORAL",
                        new BigDecimal("30.00"),
                        new BigDecimal("30.00"),
                        "EFS-RISK",
                        "1.0"
                )
        );

        List<TransactionScoreResponse> scores =
                service.getScoresByTransactionId(
                        TRANSACTION_ID
                );

        assertEquals(
                2,
                scores.size()
        );
    }

    @Test
    void shouldFilterScoresByTypeAndModel() {

        service.createScore(
                TRANSACTION_ID,
                buildRequest(
                        "RULES",
                        new BigDecimal("20.00"),
                        new BigDecimal("40.00"),
                        "EFS-RISK",
                        "1.0"
                )
        );

        service.createScore(
                TRANSACTION_ID,
                buildRequest(
                        "BEHAVIORAL",
                        new BigDecimal("30.00"),
                        new BigDecimal("30.00"),
                        "EFS-RISK",
                        "1.0"
                )
        );

        List<TransactionScoreResponse> byType =
                service.getScoresByType(
                        "RULES"
                );

        assertEquals(
                1,
                byType.size()
        );

        List<TransactionScoreResponse> byModel =
                service.getScoresByScoringModel(
                        "EFS-RISK"
                );

        assertEquals(
                2,
                byModel.size()
        );
    }

    private TransactionScoreRequest buildRequest(
            String scoreType,
            BigDecimal scoreValue,
            BigDecimal scoreWeight,
            String scoringModel,
            String modelVersion
    ) {

        TransactionScoreRequest request =
                new TransactionScoreRequest();

        request.setScoreType(
                scoreType
        );

        request.setScoreValue(
                scoreValue
        );

        request.setScoreWeight(
                scoreWeight
        );

        request.setScoringModel(
                scoringModel
        );

        request.setModelVersion(
                modelVersion
        );

        return request;
    }
}