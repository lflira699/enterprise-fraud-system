package com.efs.modules.transaction.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class TransactionHistoryControllerIntegrationTest {

    private static final UUID CUSTOMER_ID =
            UUID.fromString("82100000-0000-0000-0000-000000000001");

    private static final UUID TRANSACTION_ID =
            UUID.fromString("82100000-0000-0000-0000-000000000002");

    private static final UUID ORGANIZATION_ID =
            UUID.fromString("82100000-0000-0000-0000-000000000003");

    private static final UUID CREATED_BY =
            UUID.fromString("82100000-0000-0000-0000-000000000004");

    private static final UUID CHANGED_BY =
            UUID.fromString("82100000-0000-0000-0000-000000000005");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {

        insertOrganization();

        insertUserAccount(
                CREATED_BY,
                "efs-v82-controller-created"
        );

        insertUserAccount(
                CHANGED_BY,
                "efs-v82-controller-changed"
        );

        insertCustomer();
        insertTransaction();
    }

    @Test
    void shouldCreateTransactionHistory() throws Exception {

        Map<String, Object> request =
                createRequest(
                        1,
                        "Initial controller snapshot",
                        CHANGED_BY,
                        "RECEIVED",
                        "PENDING"
                );

        mockMvc.perform(
                        post(
                                "/api/v1/transactions/{transactionId}/history",
                                TRANSACTION_ID
                        )
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(
                                                request
                                        )
                                )
                )
                .andExpect(status().isCreated())
                .andExpect(
                        jsonPath("$.historyId").exists()
                )
                .andExpect(
                        jsonPath("$.transactionId")
                                .value(TRANSACTION_ID.toString())
                )
                .andExpect(
                        jsonPath("$.versionNumber")
                                .value(1)
                )
                .andExpect(
                        jsonPath("$.changeReason")
                                .value("Initial controller snapshot")
                )
                .andExpect(
                        jsonPath("$.changedBy")
                                .value(CHANGED_BY.toString())
                )
                .andExpect(
                        jsonPath("$.changedAt").exists()
                )
                .andExpect(
                        jsonPath("$.snapshotJson.transactionStatus")
                                .value("RECEIVED")
                )
                .andExpect(
                        jsonPath("$.snapshotJson.finalDecision")
                                .value("PENDING")
                );
    }

    @Test
    void shouldRejectCreateWhenVersionNumberIsMissing()
            throws Exception {

        Map<String, Object> request =
                createRequest(
                        1,
                        "Missing version",
                        CHANGED_BY,
                        "RECEIVED",
                        "PENDING"
                );

        request.remove("versionNumber");

        mockMvc.perform(
                        post(
                                "/api/v1/transactions/{transactionId}/history",
                                TRANSACTION_ID
                        )
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(
                                                request
                                        )
                                )
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRejectCreateWhenSnapshotJsonIsMissing()
            throws Exception {

        Map<String, Object> request =
                createRequest(
                        1,
                        "Missing snapshot",
                        CHANGED_BY,
                        "RECEIVED",
                        "PENDING"
                );

        request.remove("snapshotJson");

        mockMvc.perform(
                        post(
                                "/api/v1/transactions/{transactionId}/history",
                                TRANSACTION_ID
                        )
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(
                                                request
                                        )
                                )
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnNotFoundWhenCreatingForMissingTransaction()
            throws Exception {

        UUID missingTransactionId =
                UUID.fromString(
                        "82100000-0000-0000-0000-000000000099"
                );

        Map<String, Object> request =
                createRequest(
                        1,
                        "Missing transaction",
                        CHANGED_BY,
                        "RECEIVED",
                        "PENDING"
                );

        mockMvc.perform(
                        post(
                                "/api/v1/transactions/{transactionId}/history",
                                missingTransactionId
                        )
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(
                                                request
                                        )
                                )
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldGetTransactionHistoryById()
            throws Exception {

        UUID historyId =
                insertHistory(
                        1,
                        "History by id",
                        CHANGED_BY,
                        "RECEIVED",
                        "PENDING"
                );

        mockMvc.perform(
                        get(
                                "/api/v1/transactions/history/{historyId}",
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
                                .value(TRANSACTION_ID.toString())
                )
                .andExpect(
                        jsonPath("$.versionNumber")
                                .value(1)
                )
                .andExpect(
                        jsonPath("$.changeReason")
                                .value("History by id")
                );
    }

    @Test
    void shouldReturnNotFoundWhenHistoryIdDoesNotExist()
            throws Exception {

        UUID missingHistoryId =
                UUID.fromString(
                        "82100000-0000-0000-0000-000000000098"
                );

        mockMvc.perform(
                        get(
                                "/api/v1/transactions/history/{historyId}",
                                missingHistoryId
                        )
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldGetHistoryByTransactionId()
            throws Exception {

        insertHistory(
                1,
                "Version one",
                CHANGED_BY,
                "RECEIVED",
                "PENDING"
        );

        insertHistory(
                2,
                "Version two",
                CHANGED_BY,
                "REVIEW",
                "PENDING"
        );

        mockMvc.perform(
                        get(
                                "/api/v1/transactions/{transactionId}/history",
                                TRANSACTION_ID
                        )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.length()")
                                .value(2)
                )
                .andExpect(
                        jsonPath("$[0].versionNumber")
                                .value(2)
                )
                .andExpect(
                        jsonPath("$[1].versionNumber")
                                .value(1)
                );
    }

    @Test
    void shouldReturnNotFoundWhenTransactionDoesNotExist()
            throws Exception {

        UUID missingTransactionId =
                UUID.fromString(
                        "82100000-0000-0000-0000-000000000097"
                );

        mockMvc.perform(
                        get(
                                "/api/v1/transactions/{transactionId}/history",
                                missingTransactionId
                        )
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldGetHistoryByTransactionIdAndVersionNumber()
            throws Exception {

        insertHistory(
                1,
                "Version one",
                CHANGED_BY,
                "RECEIVED",
                "PENDING"
        );

        insertHistory(
                2,
                "Version two",
                CHANGED_BY,
                "REVIEW",
                "PENDING"
        );

        mockMvc.perform(
                        get(
                                "/api/v1/transactions/{transactionId}/history/version/{versionNumber}",
                                TRANSACTION_ID,
                                2
                        )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.transactionId")
                                .value(TRANSACTION_ID.toString())
                )
                .andExpect(
                        jsonPath("$.versionNumber")
                                .value(2)
                )
                .andExpect(
                        jsonPath("$.changeReason")
                                .value("Version two")
                )
                .andExpect(
                        jsonPath("$.snapshotJson.transactionStatus")
                                .value("REVIEW")
                );
    }

    @Test
    void shouldReturnNotFoundWhenVersionDoesNotExist()
            throws Exception {

        insertHistory(
                1,
                "Version one",
                CHANGED_BY,
                "RECEIVED",
                "PENDING"
        );

        mockMvc.perform(
                        get(
                                "/api/v1/transactions/{transactionId}/history/version/{versionNumber}",
                                TRANSACTION_ID,
                                99
                        )
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldGetHistoryByChangedBy()
            throws Exception {

        insertHistory(
                1,
                "Version one",
                CHANGED_BY,
                "RECEIVED",
                "PENDING"
        );

        insertHistory(
                2,
                "Version two",
                CHANGED_BY,
                "REVIEW",
                "PENDING"
        );

        mockMvc.perform(
                        get(
                                "/api/v1/transactions/history/changed-by/{changedBy}",
                                CHANGED_BY
                        )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.length()")
                                .value(2)
                )
                .andExpect(
                        jsonPath("$[0].changedBy")
                                .value(CHANGED_BY.toString())
                )
                .andExpect(
                        jsonPath("$[1].changedBy")
                                .value(CHANGED_BY.toString())
                );
    }

    private Map<String, Object> createRequest(
            Integer versionNumber,
            String changeReason,
            UUID changedBy,
            String transactionStatus,
            String finalDecision) {

        Map<String, Object> snapshot =
                new LinkedHashMap<>();

        snapshot.put(
                "transactionReference",
                "EFS-V82-CONTROLLER-TRANSACTION"
        );

        snapshot.put(
                "transactionType",
                "TEST"
        );

        snapshot.put(
                "amount",
                new BigDecimal("100.00")
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

        Map<String, Object> request =
                new LinkedHashMap<>();

        request.put(
                "versionNumber",
                versionNumber
        );

        request.put(
                "snapshotJson",
                snapshot
        );

        request.put(
                "changeReason",
                changeReason
        );

        request.put(
                "changedBy",
                changedBy
        );

        return request;
    }

    private UUID insertHistory(
            Integer versionNumber,
            String changeReason,
            UUID changedBy,
            String transactionStatus,
            String finalDecision) {

        UUID historyId =
                UUID.randomUUID();

        String snapshotJson =
                """
                {
                  "transactionReference":
                    "EFS-V82-CONTROLLER-TRANSACTION",
                  "transactionType": "TEST",
                  "amount": 100.00,
                  "currencyCode": "GTQ",
                  "transactionStatus": "%s",
                  "finalDecision": "%s"
                }
                """.formatted(
                        transactionStatus,
                        finalDecision
                );

        jdbcTemplate.update(
                """
                INSERT INTO transaction.transaction_history (
                    history_id,
                    transaction_id,
                    version_number,
                    snapshot_json,
                    change_reason,
                    changed_by,
                    changed_at
                )
                VALUES (
                    ?,
                    ?,
                    ?,
                    CAST(? AS jsonb),
                    ?,
                    ?,
                    clock_timestamp()
                )
                """,
                historyId,
                TRANSACTION_ID,
                versionNumber,
                snapshotJson,
                changeReason,
                changedBy
        );

        return historyId;
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
                "EFS-V82-CONTROLLER-ORG",
                "EFS V82 Controller Organization",
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
                "EFS V82 Controller User",
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
                "EFS-V82-CONTROLLER-CUSTOMER",
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
                "EFS-V82-CONTROLLER-TRANSACTION",
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