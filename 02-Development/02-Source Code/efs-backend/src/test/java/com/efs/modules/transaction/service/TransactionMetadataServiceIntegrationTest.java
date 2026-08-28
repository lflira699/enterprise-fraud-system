package com.efs.modules.transaction.service;

import com.efs.modules.transaction.dto.TransactionMetadataRequest;
import com.efs.modules.transaction.dto.TransactionMetadataResponse;
import com.efs.shared.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
class TransactionMetadataServiceIntegrationTest {

    @Autowired
    private TransactionMetadataServiceInterface service;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void shouldCreateAndRetrieveMetadataById() {

        UUID organizationId =
                createOrganization();

        UUID transactionId =
                createTransaction(organizationId);

        TransactionMetadataRequest request =
                createRequest(
                        "DEVICE_CONTEXT",
                        Map.of(
                                "deviceId",
                                "DEVICE-001",
                                "trusted",
                                true
                        )
                );

        TransactionMetadataResponse created =
                service.createMetadata(
                        transactionId,
                        request
                );

        assertNotNull(created);
        assertNotNull(created.getMetadataId());
        assertEquals(
                transactionId,
                created.getTransactionId()
        );
        assertEquals(
                "DEVICE_CONTEXT",
                created.getMetadataType()
        );
        assertNotNull(created.getMetadataJson());
        assertEquals(
                "DEVICE-001",
                created.getMetadataJson().get("deviceId")
        );
        assertEquals(
                true,
                created.getMetadataJson().get("trusted")
        );
        assertNotNull(created.getCreatedAt());

        TransactionMetadataResponse retrieved =
                service.getMetadataById(
                        created.getMetadataId()
                );

        assertEquals(
                created.getMetadataId(),
                retrieved.getMetadataId()
        );
        assertEquals(
                transactionId,
                retrieved.getTransactionId()
        );
        assertEquals(
                "DEVICE_CONTEXT",
                retrieved.getMetadataType()
        );
        assertEquals(
                "DEVICE-001",
                retrieved.getMetadataJson().get("deviceId")
        );
    }

    @Test
    void shouldReturnMetadataByTransactionOrderedByCreatedAtDescending() {

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
                  "deviceId": "DEVICE-001"
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

        List<TransactionMetadataResponse> result =
                service.getMetadataByTransactionId(
                        transactionId
                );

        assertEquals(
                2,
                result.size()
        );

        assertEquals(
                "NETWORK_CONTEXT",
                result.get(0).getMetadataType()
        );

        assertEquals(
                "DEVICE_CONTEXT",
                result.get(1).getMetadataType()
        );
    }

    @Test
    void shouldReturnMetadataByTypeOrderedByCreatedAtDescending() {

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
                  "deviceId": "DEVICE-001"
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
                  "deviceId": "DEVICE-002"
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

        insertMetadata(
                UUID.randomUUID(),
                transactionIdOne,
                "NETWORK_CONTEXT",
                """
                {
                  "ipAddress": "192.0.2.20"
                }
                """,
                LocalDateTime.of(
                        2026,
                        8,
                        26,
                        11,
                        0
                )
        );

        List<TransactionMetadataResponse> result =
                service.getMetadataByType(
                        "DEVICE_CONTEXT"
                );

        assertEquals(
                2,
                result.size()
        );

        assertEquals(
                transactionIdTwo,
                result.get(0).getTransactionId()
        );

        assertEquals(
                transactionIdOne,
                result.get(1).getTransactionId()
        );

        assertTrue(
                result.stream()
                        .allMatch(metadata ->
                                "DEVICE_CONTEXT"
                                        .equals(
                                                metadata.getMetadataType()
                                        )
                        )
        );
    }

    @Test
    void shouldPreserveStructuredJsonMetadata() {

        UUID organizationId =
                createOrganization();

        UUID transactionId =
                createTransaction(organizationId);

        Map<String, Object> metadataJson =
                new LinkedHashMap<>();

        metadataJson.put(
                "deviceId",
                "DEVICE-STRUCTURED-001"
        );

        metadataJson.put(
                "trusted",
                true
        );

        metadataJson.put(
                "riskScore",
                25
        );

        metadataJson.put(
                "signals",
                List.of(
                        "KNOWN_DEVICE",
                        "KNOWN_IP"
                )
        );

        TransactionMetadataRequest request =
                createRequest(
                        "DEVICE_CONTEXT",
                        metadataJson
                );

        TransactionMetadataResponse created =
                service.createMetadata(
                        transactionId,
                        request
                );

        TransactionMetadataResponse retrieved =
                service.getMetadataById(
                        created.getMetadataId()
                );

        assertEquals(
                "DEVICE-STRUCTURED-001",
                retrieved.getMetadataJson().get("deviceId")
        );

        assertEquals(
                true,
                retrieved.getMetadataJson().get("trusted")
        );

        assertNotNull(
                retrieved.getMetadataJson().get("riskScore")
        );

        assertNotNull(
                retrieved.getMetadataJson().get("signals")
        );
    }

    @Test
    void shouldReturnEmptyListWhenMetadataTypeDoesNotExist() {

        List<TransactionMetadataResponse> result =
                service.getMetadataByType(
                        "NON_EXISTENT_METADATA_TYPE"
                );

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void shouldThrowWhenMetadataIdDoesNotExist() {

        UUID metadataId =
                UUID.randomUUID();

        ResourceNotFoundException exception =
                assertThrows(
                        ResourceNotFoundException.class,
                        () ->
                                service.getMetadataById(
                                        metadataId
                                )
                );

        assertTrue(
                exception.getMessage()
                        .contains(
                                metadataId.toString()
                        )
        );
    }

    @Test
    void shouldThrowWhenCreatingMetadataForUnknownTransaction() {

        UUID transactionId =
                UUID.randomUUID();

        TransactionMetadataRequest request =
                createRequest(
                        "DEVICE_CONTEXT",
                        Map.of(
                                "deviceId",
                                "DEVICE-UNKNOWN"
                        )
                );

        ResourceNotFoundException exception =
                assertThrows(
                        ResourceNotFoundException.class,
                        () ->
                                service.createMetadata(
                                        transactionId,
                                        request
                                )
                );

        assertTrue(
                exception.getMessage()
                        .contains(
                                transactionId.toString()
                        )
        );
    }

    @Test
    void shouldThrowWhenRetrievingMetadataForUnknownTransaction() {

        UUID transactionId =
                UUID.randomUUID();

        ResourceNotFoundException exception =
                assertThrows(
                        ResourceNotFoundException.class,
                        () ->
                                service.getMetadataByTransactionId(
                                        transactionId
                                )
                );

        assertTrue(
                exception.getMessage()
                        .contains(
                                transactionId.toString()
                        )
        );
    }

    private TransactionMetadataRequest createRequest(
            String metadataType,
            Map<String, Object> metadataJson) {

        TransactionMetadataRequest request =
                new TransactionMetadataRequest();

        request.setMetadataType(metadataType);
        request.setMetadataJson(metadataJson);

        return request;
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
                "Transaction Metadata Test Organization",
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
                "TM-" + customerId.toString().substring(0, 8),
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
                "TRANSACTION-METADATA-" + transactionId,
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