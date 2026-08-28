package com.efs.modules.transaction.service;

import com.efs.modules.transaction.dto.TransactionHistoryRequest;
import com.efs.modules.transaction.dto.TransactionHistoryResponse;
import com.efs.shared.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
class TransactionHistoryServiceIntegrationTest {

    private static final UUID CUSTOMER_ID =
            UUID.fromString("82000000-0000-0000-0000-000000000001");

    private static final UUID TRANSACTION_ID =
            UUID.fromString("82000000-0000-0000-0000-000000000002");

    private static final UUID ORGANIZATION_ID =
            UUID.fromString("82000000-0000-0000-0000-000000000003");

    private static final UUID CREATED_BY =
            UUID.fromString("82000000-0000-0000-0000-000000000004");

    private static final UUID CHANGED_BY =
            UUID.fromString("82000000-0000-0000-0000-000000000005");

    @Autowired
    private TransactionHistoryServiceInterface service;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {

        insertOrganization();
        insertUserAccount(CREATED_BY, "efs-v82-created-by");
        insertUserAccount(CHANGED_BY, "efs-v82-changed-by");
        insertCustomer();
        insertTransaction();
    }

    @Test
    void shouldCreateTransactionHistory() {

        TransactionHistoryRequest request =
                createRequest(
                        1,
                        "Initial transaction snapshot",
                        CHANGED_BY,
                        "RECEIVED",
                        "PENDING",
                        new BigDecimal("100.00")
                );

        TransactionHistoryResponse response =
                service.createHistory(
                        TRANSACTION_ID,
                        request
                );

        assertNotNull(response);
        assertNotNull(response.getHistoryId());
        assertEquals(
                TRANSACTION_ID,
                response.getTransactionId()
        );
        assertEquals(
                1,
                response.getVersionNumber()
        );
        assertEquals(
                "Initial transaction snapshot",
                response.getChangeReason()
        );
        assertEquals(
                CHANGED_BY,
                response.getChangedBy()
        );
        assertNotNull(response.getChangedAt());
        assertNotNull(response.getSnapshotJson());

        assertEquals(
                "RECEIVED",
                response.getSnapshotJson().get("transactionStatus")
        );

        assertEquals(
                "PENDING",
                response.getSnapshotJson().get("finalDecision")
        );

        Long count =
                jdbcTemplate.queryForObject(
                        """
                        SELECT COUNT(*)
                        FROM transaction.transaction_history
                        WHERE history_id = ?
                        """,
                        Long.class,
                        response.getHistoryId()
                );

        assertEquals(1L, count);
    }

    @Test
    void shouldGetTransactionHistoryById() {

        TransactionHistoryResponse created =
                service.createHistory(
                        TRANSACTION_ID,
                        createRequest(
                                1,
                                "Version one",
                                CHANGED_BY,
                                "RECEIVED",
                                "PENDING",
                                new BigDecimal("100.00")
                        )
                );

        TransactionHistoryResponse response =
                service.getHistoryById(
                        created.getHistoryId()
                );

        assertNotNull(response);
        assertEquals(
                created.getHistoryId(),
                response.getHistoryId()
        );
        assertEquals(
                TRANSACTION_ID,
                response.getTransactionId()
        );
        assertEquals(
                1,
                response.getVersionNumber()
        );
        assertEquals(
                "Version one",
                response.getChangeReason()
        );
    }

    @Test
    void shouldGetTransactionHistoryByTransactionIdAndVersionNumber() {

        service.createHistory(
                TRANSACTION_ID,
                createRequest(
                        1,
                        "Version one",
                        CHANGED_BY,
                        "RECEIVED",
                        "PENDING",
                        new BigDecimal("100.00")
                )
        );

        service.createHistory(
                TRANSACTION_ID,
                createRequest(
                        2,
                        "Version two",
                        CHANGED_BY,
                        "REVIEW",
                        "PENDING",
                        new BigDecimal("100.00")
                )
        );

        TransactionHistoryResponse response =
                service.getHistoryByTransactionIdAndVersionNumber(
                        TRANSACTION_ID,
                        2
                );

        assertNotNull(response);
        assertEquals(
                TRANSACTION_ID,
                response.getTransactionId()
        );
        assertEquals(
                2,
                response.getVersionNumber()
        );
        assertEquals(
                "Version two",
                response.getChangeReason()
        );
        assertEquals(
                "REVIEW",
                response.getSnapshotJson()
                        .get("transactionStatus")
        );
    }

    @Test
    void shouldGetTransactionHistoryByTransactionIdOrderedByVersionDescending() {

        service.createHistory(
                TRANSACTION_ID,
                createRequest(
                        1,
                        "Version one",
                        CHANGED_BY,
                        "RECEIVED",
                        "PENDING",
                        new BigDecimal("100.00")
                )
        );

        service.createHistory(
                TRANSACTION_ID,
                createRequest(
                        2,
                        "Version two",
                        CHANGED_BY,
                        "REVIEW",
                        "PENDING",
                        new BigDecimal("100.00")
                )
        );

        service.createHistory(
                TRANSACTION_ID,
                createRequest(
                        3,
                        "Version three",
                        CHANGED_BY,
                        "COMPLETED",
                        "APPROVED",
                        new BigDecimal("100.00")
                )
        );

        List<TransactionHistoryResponse> responses =
                service.getHistoryByTransactionId(
                        TRANSACTION_ID
                );

        assertNotNull(responses);
        assertEquals(3, responses.size());

        assertEquals(
                3,
                responses.get(0).getVersionNumber()
        );

        assertEquals(
                2,
                responses.get(1).getVersionNumber()
        );

        assertEquals(
                1,
                responses.get(2).getVersionNumber()
        );
    }

    @Test
    void shouldGetTransactionHistoryByChangedBy() {

        service.createHistory(
                TRANSACTION_ID,
                createRequest(
                        1,
                        "Version one",
                        CHANGED_BY,
                        "RECEIVED",
                        "PENDING",
                        new BigDecimal("100.00")
                )
        );

        service.createHistory(
                TRANSACTION_ID,
                createRequest(
                        2,
                        "Version two",
                        CHANGED_BY,
                        "REVIEW",
                        "PENDING",
                        new BigDecimal("100.00")
                )
        );

        List<TransactionHistoryResponse> responses =
                service.getHistoryByChangedBy(
                        CHANGED_BY
                );

        assertNotNull(responses);
        assertEquals(2, responses.size());

        assertTrue(
                responses.stream()
                        .allMatch(
                                history ->
                                        CHANGED_BY.equals(
                                                history.getChangedBy()
                                        )
                        )
        );
    }

    @Test
    void shouldPreserveSnapshotJson() {

        TransactionHistoryRequest request =
                createRequest(
                        1,
                        "Snapshot verification",
                        CHANGED_BY,
                        "REVIEW",
                        "PENDING",
                        new BigDecimal("250.75")
                );

        TransactionHistoryResponse created =
                service.createHistory(
                        TRANSACTION_ID,
                        request
                );

        TransactionHistoryResponse response =
                service.getHistoryById(
                        created.getHistoryId()
                );

        Map<String, Object> snapshot =
                response.getSnapshotJson();

        assertNotNull(snapshot);
        assertFalse(snapshot.isEmpty());

        assertEquals(
                "REVIEW",
                snapshot.get("transactionStatus")
        );

        assertEquals(
                "PENDING",
                snapshot.get("finalDecision")
        );

        assertEquals(
                "GTQ",
                snapshot.get("currencyCode")
        );

        assertEquals(
                "TEST",
                snapshot.get("transactionType")
        );
    }

    @Test
    void shouldThrowWhenCreatingHistoryForMissingTransaction() {

        UUID missingTransactionId =
                UUID.fromString(
                        "82000000-0000-0000-0000-000000000099"
                );

        TransactionHistoryRequest request =
                createRequest(
                        1,
                        "Missing transaction",
                        CHANGED_BY,
                        "RECEIVED",
                        "PENDING",
                        new BigDecimal("100.00")
                );

        ResourceNotFoundException exception =
                assertThrows(
                        ResourceNotFoundException.class,
                        () ->
                                service.createHistory(
                                        missingTransactionId,
                                        request
                                )
                );

        assertTrue(
                exception.getMessage()
                        .contains(
                                "Transaction not found: "
                                        + missingTransactionId
                        )
        );
    }

    @Test
    void shouldThrowWhenHistoryIdDoesNotExist() {

        UUID missingHistoryId =
                UUID.fromString(
                        "82000000-0000-0000-0000-000000000098"
                );

        ResourceNotFoundException exception =
                assertThrows(
                        ResourceNotFoundException.class,
                        () ->
                                service.getHistoryById(
                                        missingHistoryId
                                )
                );

        assertTrue(
                exception.getMessage()
                        .contains(
                                "Transaction history not found: "
                                        + missingHistoryId
                        )
        );
    }

    @Test
    void shouldThrowWhenTransactionVersionDoesNotExist() {

        service.createHistory(
                TRANSACTION_ID,
                createRequest(
                        1,
                        "Version one",
                        CHANGED_BY,
                        "RECEIVED",
                        "PENDING",
                        new BigDecimal("100.00")
                )
        );

        ResourceNotFoundException exception =
                assertThrows(
                        ResourceNotFoundException.class,
                        () ->
                                service.getHistoryByTransactionIdAndVersionNumber(
                                        TRANSACTION_ID,
                                        99
                                )
                );

        assertTrue(
                exception.getMessage()
                        .contains(
                                "Transaction history not found for transaction "
                                        + TRANSACTION_ID
                                        + " and version 99"
                        )
        );
    }

    @Test
    void shouldThrowWhenGettingHistoryForMissingTransaction() {

        UUID missingTransactionId =
                UUID.fromString(
                        "82000000-0000-0000-0000-000000000097"
                );

        ResourceNotFoundException exception =
                assertThrows(
                        ResourceNotFoundException.class,
                        () ->
                                service.getHistoryByTransactionId(
                                        missingTransactionId
                                )
                );

        assertTrue(
                exception.getMessage()
                        .contains(
                                "Transaction not found: "
                                        + missingTransactionId
                        )
        );
    }

    private TransactionHistoryRequest createRequest(
            Integer versionNumber,
            String changeReason,
            UUID changedBy,
            String transactionStatus,
            String finalDecision,
            BigDecimal amount) {

        Map<String, Object> snapshot =
                new LinkedHashMap<>();

        snapshot.put(
                "transactionReference",
                "EFS-V82-TRANSACTION"
        );

        snapshot.put(
                "transactionType",
                "TEST"
        );

        snapshot.put(
                "amount",
                amount
        );

        snapshot.put(
                "currencyCode",
                "GTQ"
        );

        snapshot.put(
                "transactionStatus",
                transactionStatus
        );

        snapshot.put(
                "finalDecision",
                finalDecision
        );

        TransactionHistoryRequest request =
                new TransactionHistoryRequest();

        request.setVersionNumber(versionNumber);
        request.setSnapshotJson(snapshot);
        request.setChangeReason(changeReason);
        request.setChangedBy(changedBy);

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
                "EFS-V82-ORG",
                "EFS V82 Organization",
                "GT",
                "America/Guatemala",
                "ACTIVE"
        );
    }

    private void insertUserAccount(
            UUID userId,
            String username) {

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
                    account_status
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """,
                userId,
                ORGANIZATION_ID,
                username,
                "EFS V82 Test User",
                username + "@efs.test",
                "LOCAL",
                false,
                "ACTIVE"
        );
    }

    private void insertCustomer() {

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
                "EFS-V82-CUSTOMER",
                "INDIVIDUAL",
                "LOW",
                BigDecimal.ZERO,
                "ACTIVE",
                "ACTIVE",
                1
        );
    }

    private void insertTransaction() {

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
                "EFS-V82-TRANSACTION",
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
}