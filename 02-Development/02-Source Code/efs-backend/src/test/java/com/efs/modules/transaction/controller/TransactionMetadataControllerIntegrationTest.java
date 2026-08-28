package com.efs.modules.transaction.controller;

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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class TransactionMetadataControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void shouldCreateMetadata()
            throws Exception {

        UUID organizationId =
                createOrganization();

        UUID transactionId =
                createTransaction(organizationId);

        String requestBody =
                """
                {
                  "metadataType": "DEVICE_CONTEXT",
                  "metadataJson": {
                    "deviceId": "DEVICE-001",
                    "trusted": true
                  }
                }
                """;

        mockMvc.perform(
                        post(
                                "/api/v1/transactions/{transactionId}/metadata",
                                transactionId
                        )
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.metadataId").exists())
                .andExpect(
                        jsonPath("$.transactionId")
                                .value(transactionId.toString())
                )
                .andExpect(
                        jsonPath("$.metadataType")
                                .value("DEVICE_CONTEXT")
                )
                .andExpect(
                        jsonPath("$.metadataJson.deviceId")
                                .value("DEVICE-001")
                )
                .andExpect(
                        jsonPath("$.metadataJson.trusted")
                                .value(true)
                )
                .andExpect(jsonPath("$.createdAt").exists());
    }

    @Test
    void shouldRejectBlankMetadataType()
            throws Exception {

        UUID organizationId =
                createOrganization();

        UUID transactionId =
                createTransaction(organizationId);

        String requestBody =
                """
                {
                  "metadataType": "",
                  "metadataJson": {
                    "deviceId": "DEVICE-001"
                  }
                }
                """;

        mockMvc.perform(
                        post(
                                "/api/v1/transactions/{transactionId}/metadata",
                                transactionId
                        )
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRejectMissingMetadataJson()
            throws Exception {

        UUID organizationId =
                createOrganization();

        UUID transactionId =
                createTransaction(organizationId);

        String requestBody =
                """
                {
                  "metadataType": "DEVICE_CONTEXT"
                }
                """;

        mockMvc.perform(
                        post(
                                "/api/v1/transactions/{transactionId}/metadata",
                                transactionId
                        )
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldGetMetadataById()
            throws Exception {

        UUID organizationId =
                createOrganization();

        UUID transactionId =
                createTransaction(organizationId);

        UUID metadataId =
                UUID.randomUUID();

        insertMetadata(
                metadataId,
                transactionId,
                "DEVICE_CONTEXT",
                """
                {
                  "deviceId": "DEVICE-002",
                  "trusted": false
                }
                """,
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
                                "/api/v1/transactions/metadata/{metadataId}",
                                metadataId
                        )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.metadataId")
                                .value(metadataId.toString())
                )
                .andExpect(
                        jsonPath("$.transactionId")
                                .value(transactionId.toString())
                )
                .andExpect(
                        jsonPath("$.metadataType")
                                .value("DEVICE_CONTEXT")
                )
                .andExpect(
                        jsonPath("$.metadataJson.deviceId")
                                .value("DEVICE-002")
                );
    }

    @Test
    void shouldGetMetadataByTransactionOrderedByCreatedAtDescending()
            throws Exception {

        UUID organizationId =
                createOrganization();

        UUID transactionId =
                createTransaction(organizationId);

        insertMetadata(
                UUID.randomUUID(),
                transactionId,
                "DEVICE_CONTEXT",
                """
                {
                  "deviceId": "DEVICE-003"
                }
                """,
                LocalDateTime.of(
                        2026,
                        8,
                        26,
                        8,
                        0
                )
        );

        insertMetadata(
                UUID.randomUUID(),
                transactionId,
                "NETWORK_CONTEXT",
                """
                {
                  "ipAddress": "192.0.2.10"
                }
                """,
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
                                "/api/v1/transactions/{transactionId}/metadata",
                                transactionId
                        )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(
                        jsonPath("$[0].metadataType")
                                .value("NETWORK_CONTEXT")
                )
                .andExpect(
                        jsonPath("$[1].metadataType")
                                .value("DEVICE_CONTEXT")
                );
    }

    @Test
    void shouldGetMetadataByTypeOrderedByCreatedAtDescending()
            throws Exception {

        UUID organizationId =
                createOrganization();

        UUID transactionIdOne =
                createTransaction(organizationId);

        UUID transactionIdTwo =
                createTransaction(organizationId);

        insertMetadata(
                UUID.randomUUID(),
                transactionIdOne,
                "DEVICE_CONTEXT",
                """
                {
                  "deviceId": "DEVICE-004"
                }
                """,
                LocalDateTime.of(
                        2026,
                        8,
                        26,
                        8,
                        0
                )
        );

        insertMetadata(
                UUID.randomUUID(),
                transactionIdTwo,
                "DEVICE_CONTEXT",
                """
                {
                  "deviceId": "DEVICE-005"
                }
                """,
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
                                "/api/v1/transactions/metadata/type/{metadataType}",
                                "DEVICE_CONTEXT"
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
    void shouldReturnEmptyListForUnknownMetadataType()
            throws Exception {

        mockMvc.perform(
                        get(
                                "/api/v1/transactions/metadata/type/{metadataType}",
                                "NON_EXISTENT_METADATA_TYPE"
                        )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void shouldReturnNotFoundForUnknownMetadataId()
            throws Exception {

        mockMvc.perform(
                        get(
                                "/api/v1/transactions/metadata/{metadataId}",
                                UUID.randomUUID()
                        )
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnNotFoundForUnknownTransaction()
            throws Exception {

        mockMvc.perform(
                        get(
                                "/api/v1/transactions/{transactionId}/metadata",
                                UUID.randomUUID()
                        )
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnNotFoundWhenCreatingMetadataForUnknownTransaction()
            throws Exception {

        String requestBody =
                """
                {
                  "metadataType": "DEVICE_CONTEXT",
                  "metadataJson": {
                    "deviceId": "DEVICE-UNKNOWN"
                  }
                }
                """;

        mockMvc.perform(
                        post(
                                "/api/v1/transactions/{transactionId}/metadata",
                                UUID.randomUUID()
                        )
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isNotFound());
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
                "Transaction Metadata Controller Test Organization",
                "GT",
                "America/Guatemala",
                "ACTIVE"
        );

        return organizationId;
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
                "TMC-" + customerId.toString().substring(0, 8),
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
                "TRANSACTION-METADATA-CONTROLLER-" + transactionId,
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

    private void insertMetadata(
            UUID metadataId,
            UUID transactionId,
            String metadataType,
            String metadataJson,
            LocalDateTime createdAt) {

        jdbcTemplate.update(
                """
                INSERT INTO transaction.transaction_metadata (
                    metadata_id,
                    transaction_id,
                    metadata_type,
                    metadata_json,
                    created_at
                )
                VALUES (
                    ?,
                    ?,
                    ?,
                    CAST(? AS jsonb),
                    ?
                )
                """,
                metadataId,
                transactionId,
                metadataType,
                metadataJson,
                createdAt
        );
    }
}