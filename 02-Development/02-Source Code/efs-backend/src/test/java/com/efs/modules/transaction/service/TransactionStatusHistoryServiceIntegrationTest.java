package com.efs.modules.transaction.service;

import com.efs.modules.transaction.dto.TransactionStatusHistoryRequest;
import com.efs.modules.transaction.dto.TransactionStatusHistoryResponse;
import com.efs.shared.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class TransactionStatusHistoryServiceIntegrationTest {

    @Autowired
    private TransactionStatusHistoryServiceInterface
            transactionStatusHistoryService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void shouldCreateAndRetrieveStatusHistoryById() {

        UUID organizationId =
                createOrganization();

        UUID changedBy =
                createUserAccount(organizationId);

        UUID transactionId =
                createTransaction(organizationId);

        TransactionStatusHistoryRequest request =
                new TransactionStatusHistoryRequest();

        request.setPreviousStatus("RECEIVED");
        request.setCurrentStatus("UNDER_REVIEW");
        request.setChangeReason(
                "Transaction moved to investigation"
        );
        request.setChangedBy(changedBy);

        TransactionStatusHistoryResponse created =
                transactionStatusHistoryService
                        .createStatusHistory(
                                transactionId,
                                request
                        );

        assertNotNull(created);
        assertNotNull(created.getHistoryId());

        assertEquals(
                transactionId,
                created.getTransactionId()
        );

        assertEquals(
                "RECEIVED",
                created.getPreviousStatus()
        );

        assertEquals(
                "UNDER_REVIEW",
                created.getCurrentStatus()
        );

        assertEquals(
                "Transaction moved to investigation",
                created.getChangeReason()
        );

        assertEquals(
                changedBy,
                created.getChangedBy()
        );

        assertNotNull(
                created.getChangedAt()
        );

        TransactionStatusHistoryResponse retrieved =
                transactionStatusHistoryService
                        .getStatusHistoryById(
                                created.getHistoryId()
                        );

        assertEquals(
                created.getHistoryId(),
                retrieved.getHistoryId()
        );
    }

    @Test
    void shouldPreserveProvidedChangedAt() {

        UUID organizationId =
                createOrganization();

        UUID changedBy =
                createUserAccount(organizationId);

        UUID transactionId =
                createTransaction(organizationId);

        LocalDateTime changedAt =
                LocalDateTime.of(
                        2026,
                        8,
                        26,
                        8,
                        0,
                        0
                );

        TransactionStatusHistoryRequest request =
                new TransactionStatusHistoryRequest();

        request.setPreviousStatus("RECEIVED");
        request.setCurrentStatus("APPROVED");
        request.setChangeReason(
                "Transaction approved"
        );
        request.setChangedBy(changedBy);
        request.setChangedAt(changedAt);

        TransactionStatusHistoryResponse created =
                transactionStatusHistoryService
                        .createStatusHistory(
                                transactionId,
                                request
                        );

        assertEquals(
                changedAt,
                created.getChangedAt()
        );
    }

    @Test
    void shouldReturnStatusHistoryByTransactionOrderedByChangedAtDesc() {

        UUID organizationId =
                createOrganization();

        UUID changedBy =
                createUserAccount(organizationId);

        UUID transactionId =
                createTransaction(organizationId);

        insertStatusHistory(
                UUID.randomUUID(),
                transactionId,
                "RECEIVED",
                "UNDER_REVIEW",
                "First status change",
                changedBy,
                LocalDateTime.of(
                        2026,
                        8,
                        26,
                        8,
                        0
                )
        );

        insertStatusHistory(
                UUID.randomUUID(),
                transactionId,
                "UNDER_REVIEW",
                "APPROVED",
                "Second status change",
                changedBy,
                LocalDateTime.of(
                        2026,
                        8,
                        26,
                        9,
                        0
                )
        );

        List<TransactionStatusHistoryResponse> results =
                transactionStatusHistoryService
                        .getStatusHistoryByTransactionId(
                                transactionId
                        );

        assertEquals(
                2,
                results.size()
        );

        assertEquals(
                "APPROVED",
                results.get(0).getCurrentStatus()
        );

        assertEquals(
                "UNDER_REVIEW",
                results.get(1).getCurrentStatus()
        );
    }

    @Test
    void shouldReturnStatusHistoryByCurrentStatusOrderedByChangedAtDesc() {

        UUID organizationId =
                createOrganization();

        UUID changedBy =
                createUserAccount(organizationId);

        UUID transactionIdOne =
                createTransaction(organizationId);

        UUID transactionIdTwo =
                createTransaction(organizationId);

        insertStatusHistory(
                UUID.randomUUID(),
                transactionIdOne,
                "RECEIVED",
                "UNDER_REVIEW",
                "First review",
                changedBy,
                LocalDateTime.of(
                        2026,
                        8,
                        26,
                        8,
                        0
                )
        );

        insertStatusHistory(
                UUID.randomUUID(),
                transactionIdTwo,
                "RECEIVED",
                "UNDER_REVIEW",
                "Second review",
                changedBy,
                LocalDateTime.of(
                        2026,
                        8,
                        26,
                        9,
                        0
                )
        );

        List<TransactionStatusHistoryResponse> results =
                transactionStatusHistoryService
                        .getStatusHistoryByCurrentStatus(
                                "UNDER_REVIEW"
                        );

        assertEquals(
                2,
                results.size()
        );

        assertEquals(
                transactionIdTwo,
                results.get(0).getTransactionId()
        );

        assertEquals(
                transactionIdOne,
                results.get(1).getTransactionId()
        );
    }

    @Test
    void shouldReturnStatusHistoryByChangedByOrderedByChangedAtDesc() {

        UUID organizationId =
                createOrganization();

        UUID changedBy =
                createUserAccount(organizationId);

        UUID transactionId =
                createTransaction(organizationId);

        insertStatusHistory(
                UUID.randomUUID(),
                transactionId,
                "RECEIVED",
                "UNDER_REVIEW",
                "First change",
                changedBy,
                LocalDateTime.of(
                        2026,
                        8,
                        26,
                        8,
                        0
                )
        );

        insertStatusHistory(
                UUID.randomUUID(),
                transactionId,
                "UNDER_REVIEW",
                "DECLINED",
                "Second change",
                changedBy,
                LocalDateTime.of(
                        2026,
                        8,
                        26,
                        10,
                        0
                )
        );

        List<TransactionStatusHistoryResponse> results =
                transactionStatusHistoryService
                        .getStatusHistoryByChangedBy(
                                changedBy
                        );

        assertEquals(
                2,
                results.size()
        );

        assertEquals(
                "DECLINED",
                results.get(0).getCurrentStatus()
        );

        assertEquals(
                "UNDER_REVIEW",
                results.get(1).getCurrentStatus()
        );
    }

    @Test
    void shouldRejectUnknownStatusHistoryId() {

        assertThrows(
                ResourceNotFoundException.class,
                () ->
                        transactionStatusHistoryService
                                .getStatusHistoryById(
                                        UUID.randomUUID()
                                )
        );
    }

    @Test
    void shouldRejectCreateForUnknownTransaction() {

        TransactionStatusHistoryRequest request =
                new TransactionStatusHistoryRequest();

        request.setPreviousStatus("RECEIVED");
        request.setCurrentStatus("UNDER_REVIEW");

        assertThrows(
                ResourceNotFoundException.class,
                () ->
                        transactionStatusHistoryService
                                .createStatusHistory(
                                        UUID.randomUUID(),
                                        request
                                )
        );
    }

    @Test
    void shouldRejectHistoryLookupForUnknownTransaction() {

        assertThrows(
                ResourceNotFoundException.class,
                () ->
                        transactionStatusHistoryService
                                .getStatusHistoryByTransactionId(
                                        UUID.randomUUID()
                                )
        );
    }

    private UUID createOrganization() {

        UUID organizationId =
                UUID.randomUUID();

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
                "ORG-" + organizationId,
                "Transaction Status History Test Organization",
                "GT",
                "America/Guatemala",
                "ACTIVE"
        );

        return organizationId;
    }

    private UUID createUserAccount(
            UUID organizationId) {

        UUID userId =
                UUID.randomUUID();

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
                "status.history." + userId,
                "Transaction Status History Test User",
                userId + "@example.com",
                "LOCAL",
                false,
                "ACTIVE",
                0
        );

        return userId;
    }

    private UUID createTransaction(
            UUID organizationId) {

        UUID customerId =
                UUID.randomUUID();

        UUID transactionId =
                UUID.randomUUID();

        UUID createdBy =
                UUID.randomUUID();

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
                customerId,
                "TSH-" + customerId.toString().substring(0, 8),
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
                transactionId,
                "STATUS-HISTORY-TRANSACTION-" + transactionId,
                customerId,
                organizationId,
                "TEST",
                new BigDecimal("100.00"),
                "GTQ",
                "RECEIVED",
                "PENDING",
                BigDecimal.ZERO,
                createdBy,
                1
        );

        return transactionId;
    }

    private void insertStatusHistory(
            UUID historyId,
            UUID transactionId,
            String previousStatus,
            String currentStatus,
            String changeReason,
            UUID changedBy,
            LocalDateTime changedAt) {

        jdbcTemplate.update(
                """
                INSERT INTO transaction.transaction_status_history (
                    history_id,
                    transaction_id,
                    previous_status,
                    current_status,
                    change_reason,
                    changed_by,
                    changed_at
                )
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """,
                historyId,
                transactionId,
                previousStatus,
                currentStatus,
                changeReason,
                changedBy,
                changedAt
        );
    }
}