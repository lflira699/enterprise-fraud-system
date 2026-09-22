package com.efs.modules.transaction.controller;

import com.efs.modules.administration.dto.UserAccountReference;
import com.efs.modules.administration.service.UserAccountLookupServiceInterface;
import com.efs.shared.security.SecurityContext;
import com.efs.shared.security.SecurityContextProvider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class TransactionStatusHistoryControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @MockitoBean
    private SecurityContextProvider securityContextProvider;

    @MockitoBean
    private UserAccountLookupServiceInterface
            userAccountLookupService;

    private final UUID actorId =
            UUID.randomUUID();

    private UUID authorizedOrganizationId;

    @Test
    void shouldCreateStatusHistory()
            throws Exception {

        UUID organizationId =
                createOrganization();

        UUID changedBy =
                createUserAccount(organizationId);

        UUID transactionId =
                createTransaction(organizationId);

        String requestBody =
                """
                {
                  "previousStatus": "RECEIVED",
                  "currentStatus": "UNDER_REVIEW",
                  "changeReason": "Transaction moved to investigation",
                  "changedBy": "%s"
                }
                """.formatted(changedBy);

        mockMvc.perform(
                        post(
                                "/api/v1/transactions/{transactionId}/status-history",
                                transactionId
                        )
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.historyId").exists())
                .andExpect(
                        jsonPath("$.transactionId")
                                .value(transactionId.toString())
                )
                .andExpect(
                        jsonPath("$.previousStatus")
                                .value("RECEIVED")
                )
                .andExpect(
                        jsonPath("$.currentStatus")
                                .value("UNDER_REVIEW")
                )
                .andExpect(
                        jsonPath("$.changeReason")
                                .value("Transaction moved to investigation")
                )
                .andExpect(
                        jsonPath("$.changedBy")
                                .value(changedBy.toString())
                )
                .andExpect(jsonPath("$.changedAt").exists());
    }

    @Test
    void shouldRejectInvalidStatusHistoryRequest()
            throws Exception {

        UUID organizationId =
                createOrganization();

        UUID transactionId =
                createTransaction(organizationId);

        String requestBody =
                """
                {
                  "previousStatus": "RECEIVED",
                  "currentStatus": ""
                }
                """;

        mockMvc.perform(
                        post(
                                "/api/v1/transactions/{transactionId}/status-history",
                                transactionId
                        )
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldGetStatusHistoryById()
            throws Exception {

        UUID organizationId =
                createOrganization();

        UUID changedBy =
                createUserAccount(organizationId);

        UUID transactionId =
                createTransaction(organizationId);

        UUID historyId =
                UUID.randomUUID();

        insertStatusHistory(
                historyId,
                transactionId,
                "RECEIVED",
                "UNDER_REVIEW",
                "Transaction under review",
                changedBy,
                LocalDateTime.of(
                        2026,
                        8,
                        26,
                        8,
                        0
                )
        );

        mockMvc.perform(
                        get(
                                "/api/v1/transactions/status-history/{historyId}",
                                historyId
                        )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.historyId")
                                .value(historyId.toString())
                )
                .andExpect(
                        jsonPath("$.transactionId")
                                .value(transactionId.toString())
                )
                .andExpect(
                        jsonPath("$.currentStatus")
                                .value("UNDER_REVIEW")
                );
    }

    @Test
    void shouldGetStatusHistoryByTransaction()
            throws Exception {

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

        mockMvc.perform(
                        get(
                                "/api/v1/transactions/{transactionId}/status-history",
                                transactionId
                        )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(
                        jsonPath("$[0].currentStatus")
                                .value("APPROVED")
                )
                .andExpect(
                        jsonPath("$[1].currentStatus")
                                .value("UNDER_REVIEW")
                );
    }

    @Test
    void shouldGetStatusHistoryByCurrentStatus()
            throws Exception {

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

        mockMvc.perform(
                        get(
                                "/api/v1/transactions/status-history/status/{currentStatus}",
                                "UNDER_REVIEW"
                        )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(
                        jsonPath("$[0].transactionId")
                                .value(transactionIdTwo.toString())
                )
                .andExpect(
                        jsonPath("$[1].transactionId")
                                .value(transactionIdOne.toString())
                );
    }

    @Test
    void shouldGetStatusHistoryByChangedBy()
            throws Exception {

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

        mockMvc.perform(
                        get(
                                "/api/v1/transactions/status-history/changed-by/{changedBy}",
                                changedBy
                        )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(
                        jsonPath("$[0].currentStatus")
                                .value("DECLINED")
                )
                .andExpect(
                        jsonPath("$[1].currentStatus")
                                .value("UNDER_REVIEW")
                );
    }

    @Test
    void shouldReturnNotFoundForUnknownStatusHistoryId()
            throws Exception {

        createOrganization();

        mockMvc.perform(
                        get(
                                "/api/v1/transactions/status-history/{historyId}",
                                UUID.randomUUID()
                        )
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnNotFoundForUnknownTransaction()
            throws Exception {

        createOrganization();

        mockMvc.perform(
                        get(
                                "/api/v1/transactions/{transactionId}/status-history",
                                UUID.randomUUID()
                        )
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnNotFoundWhenCreatingHistoryForUnknownTransaction()
            throws Exception {

        createOrganization();

        String requestBody =
                """
                {
                  "previousStatus": "RECEIVED",
                  "currentStatus": "UNDER_REVIEW",
                  "changeReason": "Unknown transaction"
                }
                """;

        mockMvc.perform(
                        post(
                                "/api/v1/transactions/{transactionId}/status-history",
                                UUID.randomUUID()
                        )
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnNotFoundWhenCreatingHistoryForSoftDeletedTransaction()
            throws Exception {

        UUID organizationId =
                createOrganization();

        UUID changedBy =
                createUserAccount(organizationId);

        UUID transactionId =
                createTransaction(organizationId);

        softDeleteTransaction(transactionId);

        String requestBody =
                """
                {
                  "previousStatus": "RECEIVED",
                  "currentStatus": "UNDER_REVIEW",
                  "changeReason": "Soft deleted parent",
                  "changedBy": "%s"
                }
                """.formatted(changedBy);

        mockMvc.perform(
                        post(
                                "/api/v1/transactions/{transactionId}/status-history",
                                transactionId
                        )
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnNotFoundForHistoryWhenParentTransactionIsSoftDeleted()
            throws Exception {

        UUID organizationId =
                createOrganization();

        UUID changedBy =
                createUserAccount(organizationId);

        UUID transactionId =
                createTransaction(organizationId);

        UUID historyId =
                UUID.randomUUID();

        insertStatusHistory(
                historyId,
                transactionId,
                "RECEIVED",
                "UNDER_REVIEW",
                "Soft deleted parent",
                changedBy,
                LocalDateTime.now()
        );

        softDeleteTransaction(transactionId);

        mockMvc.perform(
                        get(
                                "/api/v1/transactions/status-history/{historyId}",
                                historyId
                        )
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnNotFoundForTransactionHistoryWhenParentIsSoftDeleted()
            throws Exception {

        UUID organizationId =
                createOrganization();

        UUID transactionId =
                createTransaction(organizationId);

        softDeleteTransaction(transactionId);

        mockMvc.perform(
                        get(
                                "/api/v1/transactions/{transactionId}/status-history",
                                transactionId
                        )
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldExcludeSoftDeletedParentFromCurrentStatusEndpoint()
            throws Exception {

        UUID organizationId =
                createOrganization();

        UUID changedBy =
                createUserAccount(organizationId);

        UUID deletedTransactionId =
                createTransaction(organizationId);

        UUID activeTransactionId =
                createTransaction(organizationId);

        String currentStatus =
                "SOFT_STATUS_" +
                        UUID.randomUUID()
                                .toString()
                                .substring(0, 8);

        insertStatusHistory(
                UUID.randomUUID(),
                deletedTransactionId,
                "RECEIVED",
                currentStatus,
                "Deleted parent",
                changedBy,
                LocalDateTime.of(2026, 9, 21, 8, 0)
        );

        insertStatusHistory(
                UUID.randomUUID(),
                activeTransactionId,
                "RECEIVED",
                currentStatus,
                "Active parent",
                changedBy,
                LocalDateTime.of(2026, 9, 21, 9, 0)
        );

        softDeleteTransaction(deletedTransactionId);

        mockMvc.perform(
                        get(
                                "/api/v1/transactions/status-history/status/{currentStatus}",
                                currentStatus
                        )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(
                        jsonPath("$[0].transactionId")
                                .value(activeTransactionId.toString())
                );
    }

    @Test
    void shouldExcludeSoftDeletedParentFromChangedByEndpoint()
            throws Exception {

        UUID organizationId =
                createOrganization();

        UUID changedBy =
                createUserAccount(organizationId);

        UUID deletedTransactionId =
                createTransaction(organizationId);

        UUID activeTransactionId =
                createTransaction(organizationId);

        insertStatusHistory(
                UUID.randomUUID(),
                deletedTransactionId,
                "RECEIVED",
                "UNDER_REVIEW",
                "Deleted parent",
                changedBy,
                LocalDateTime.of(2026, 9, 21, 8, 0)
        );

        insertStatusHistory(
                UUID.randomUUID(),
                activeTransactionId,
                "UNDER_REVIEW",
                "APPROVED",
                "Active parent",
                changedBy,
                LocalDateTime.of(2026, 9, 21, 9, 0)
        );

        softDeleteTransaction(deletedTransactionId);

        mockMvc.perform(
                        get(
                                "/api/v1/transactions/status-history/changed-by/{changedBy}",
                                changedBy
                        )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(
                        jsonPath("$[0].transactionId")
                                .value(activeTransactionId.toString())
                );
    }

    @Test
    void shouldReturnForbiddenWhenCreatingWithoutTransactionUpdate()
            throws Exception {

        UUID organizationId =
                createOrganization();

        UUID transactionId =
                createTransaction(
                        organizationId
                );

        authorize(
                Set.of("transaction.view"),
                organizationId,
                null
        );

        String requestBody =
                """
                {
                  "previousStatus": "RECEIVED",
                  "currentStatus": "UNDER_REVIEW",
                  "changeReason": "Permission test"
                }
                """;

        mockMvc.perform(
                        post(
                                "/api/v1/transactions/{transactionId}/status-history",
                                transactionId
                        )
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldReturnForbiddenWhenReadingWithoutTransactionView()
            throws Exception {

        UUID organizationId =
                createOrganization();

        UUID changedBy =
                createUserAccount(
                        organizationId
                );

        UUID transactionId =
                createTransaction(
                        organizationId
                );

        UUID historyId =
                UUID.randomUUID();

        insertStatusHistory(
                historyId,
                transactionId,
                "RECEIVED",
                "UNDER_REVIEW",
                "Permission test",
                changedBy,
                LocalDateTime.now()
        );

        authorize(
                Set.of("transaction.update"),
                organizationId,
                null
        );

        mockMvc.perform(
                        get(
                                "/api/v1/transactions/status-history/{historyId}",
                                historyId
                        )
                )
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldHideCrossOrganizationStatusHistoryById()
            throws Exception {

        UUID organizationId =
                createOrganization();

        UUID changedBy =
                createUserAccount(
                        organizationId
                );

        UUID transactionId =
                createTransaction(
                        organizationId
                );

        UUID historyId =
                UUID.randomUUID();

        insertStatusHistory(
                historyId,
                transactionId,
                "RECEIVED",
                "UNDER_REVIEW",
                "Cross organization id",
                changedBy,
                LocalDateTime.now()
        );

        authorize(
                allPermissions(),
                UUID.randomUUID(),
                null
        );

        mockMvc.perform(
                        get(
                                "/api/v1/transactions/status-history/{historyId}",
                                historyId
                        )
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldHideCrossOrganizationStatusHistoryList()
            throws Exception {

        UUID organizationId =
                createOrganization();

        UUID transactionId =
                createTransaction(
                        organizationId
                );

        authorize(
                allPermissions(),
                UUID.randomUUID(),
                null
        );

        mockMvc.perform(
                        get(
                                "/api/v1/transactions/{transactionId}/status-history",
                                transactionId
                        )
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldHideCrossOrganizationStatusHistoryCreate()
            throws Exception {

        UUID organizationId =
                createOrganization();

        UUID transactionId =
                createTransaction(
                        organizationId
                );

        authorize(
                allPermissions(),
                UUID.randomUUID(),
                null
        );

        String requestBody =
                """
                {
                  "previousStatus": "RECEIVED",
                  "currentStatus": "UNDER_REVIEW",
                  "changeReason": "Cross organization create"
                }
                """;

        mockMvc.perform(
                        post(
                                "/api/v1/transactions/{transactionId}/status-history",
                                transactionId
                        )
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldFilterCrossOrganizationStatusHistoryGlobalQueries()
            throws Exception {

        UUID organizationId =
                createOrganization();

        UUID changedBy =
                createUserAccount(
                        organizationId
                );

        UUID transactionId =
                createTransaction(
                        organizationId
                );

        String currentStatus =
                "SEC_" +
                        UUID.randomUUID()
                                .toString()
                                .substring(0, 8);

        insertStatusHistory(
                UUID.randomUUID(),
                transactionId,
                "RECEIVED",
                currentStatus,
                "Cross organization global filter",
                changedBy,
                LocalDateTime.now()
        );

        authorize(
                allPermissions(),
                UUID.randomUUID(),
                null
        );

        mockMvc.perform(
                        get(
                                "/api/v1/transactions/status-history/status/{currentStatus}",
                                currentStatus
                        )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        mockMvc.perform(
                        get(
                                "/api/v1/transactions/status-history/changed-by/{changedBy}",
                                changedBy
                        )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    private void authorize(
            Set<String> permissions,
            UUID organizationId,
            UUID tenantId) {

        authorizedOrganizationId =
                organizationId;

        when(
                securityContextProvider
                        .getCurrentContext()
        ).thenReturn(
                new SecurityContext(
                        actorId,
                        tenantId,
                        null,
                        Set.of(),
                        permissions,
                        Set.of()
                )
        );

        when(
                userAccountLookupService
                        .getAuthorizedUser(
                                actorId
                        )
        ).thenReturn(
                new UserAccountReference(
                        actorId,
                        organizationId,
                        tenantId,
                        "transaction-status-history-controller@example.com"
                )
        );
    }

    private Set<String> allPermissions() {

        return Set.of(
                "transaction.view",
                "transaction.update"
        );
    }

    private void softDeleteTransaction(
            UUID transactionId) {

        jdbcTemplate.update(
                "UPDATE transaction.transaction " +
                        "SET deleted_at = CURRENT_TIMESTAMP " +
                        "WHERE transaction_id = ?",
                transactionId
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
                "Transaction Status History Controller Test Organization",
                "GT",
                "America/Guatemala",
                "ACTIVE"
        );

        if (authorizedOrganizationId == null) {

            authorize(
                    allPermissions(),
                    organizationId,
                    null
            );
        }

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
                "status.history.controller." + userId,
                "Transaction Status History Controller Test User",
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
                "TSHC-" + customerId.toString().substring(0, 8),
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
                "TSH-CONTROLLER-" + transactionId,
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
