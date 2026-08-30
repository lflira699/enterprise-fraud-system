package com.efs.modules.detection.controller;

import com.efs.modules.customer.entity.Customer;
import com.efs.modules.customer.repository.CustomerRepository;
import com.efs.modules.transaction.entity.Transaction;
import com.efs.modules.transaction.repository.TransactionRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class CorrelationControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Test
    void shouldCreateCorrelationWithFullPayload()
            throws Exception {

        Customer customer =
                createCustomer();

        Transaction transaction =
                createTransaction(
                        customer.getCustomerId()
                );

        String correlationKey =
                uniqueKey("FULL");

        Map<String, Object> request =
                validCorrelationRequest(
                        customer.getCustomerId(),
                        transaction.getTransactionId(),
                        correlationKey,
                        "TRANSACTION",
                        "OPEN"
                );

        request.put(
                "correlationContext",
                Map.of(
                        "source", "CONTROLLER_TEST",
                        "channel", "WEB"
                )
        );

        mockMvc.perform(
                        post(
                                "/api/v1/detection/correlations"
                        )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        objectMapper.writeValueAsString(
                                                request
                                        )
                                )
                )
                .andExpect(status().isCreated())
                .andExpect(
                        jsonPath("$.correlationId")
                                .exists()
                )
                .andExpect(
                        jsonPath("$.customerId")
                                .value(
                                        customer.getCustomerId()
                                                .toString()
                                )
                )
                .andExpect(
                        jsonPath("$.transactionId")
                                .value(
                                        transaction.getTransactionId()
                                                .toString()
                                )
                )
                .andExpect(
                        jsonPath("$.correlationKey")
                                .value(correlationKey)
                )
                .andExpect(
                        jsonPath("$.correlationType")
                                .value("TRANSACTION")
                )
                .andExpect(
                        jsonPath("$.correlationStatus")
                                .value("OPEN")
                )
                .andExpect(
                        jsonPath("$.windowStart")
                                .value(
                                        "2026-08-30T12:00:00"
                                )
                )
                .andExpect(
                        jsonPath("$.windowEnd")
                                .value(
                                        "2026-08-30T12:30:00"
                                )
                )
                .andExpect(
                        jsonPath("$.eventCount")
                                .value(3)
                )
                .andExpect(
                        jsonPath("$.matchedRuleCount")
                                .value(1)
                )
                .andExpect(
                        jsonPath("$.confidence")
                                .value(0)
                )
                .andExpect(
                        jsonPath(
                                "$.correlationContext.source"
                        )
                                .value("CONTROLLER_TEST")
                )
                .andExpect(
                        jsonPath(
                                "$.correlationContext.channel"
                        )
                                .value("WEB")
                )
                .andExpect(
                        jsonPath("$.createdAt")
                                .exists()
                )
                .andExpect(
                        jsonPath("$.updatedAt")
                                .exists()
                );
    }

    @Test
    void shouldCreateCorrelationWithoutCustomerAndTransaction()
            throws Exception {

        String correlationKey =
                uniqueKey("GLOBAL");

        Map<String, Object> request =
                validCorrelationRequest(
                        null,
                        null,
                        correlationKey,
                        "GLOBAL",
                        "OPEN"
                );

        mockMvc.perform(
                        post(
                                "/api/v1/detection/correlations"
                        )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        objectMapper.writeValueAsString(
                                                request
                                        )
                                )
                )
                .andExpect(status().isCreated())
                .andExpect(
                        jsonPath("$.correlationId")
                                .exists()
                )
                .andExpect(
                        jsonPath("$.customerId")
                                .doesNotExist()
                )
                .andExpect(
                        jsonPath("$.transactionId")
                                .doesNotExist()
                )
                .andExpect(
                        jsonPath("$.correlationKey")
                                .value(correlationKey)
                )
                .andExpect(
                        jsonPath("$.correlationType")
                                .value("GLOBAL")
                )
                .andExpect(
                        jsonPath("$.confidence")
                                .value(0)
                );
    }

    @Test
    void shouldRejectInvalidCreateRequest()
            throws Exception {

        Map<String, Object> request =
                new LinkedHashMap<>();

        request.put(
                "correlationKey",
                ""
        );

        request.put(
                "correlationType",
                ""
        );

        request.put(
                "correlationStatus",
                ""
        );

        mockMvc.perform(
                        post(
                                "/api/v1/detection/correlations"
                        )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        objectMapper.writeValueAsString(
                                                request
                                        )
                                )
                )
                .andExpect(status().isBadRequest())
                .andExpect(
                        jsonPath("$.errorCode")
                                .value("VALIDATION_ERROR")
                )
                .andExpect(
                        jsonPath(
                                "$.validationErrors.correlationKey"
                        )
                                .exists()
                )
                .andExpect(
                        jsonPath(
                                "$.validationErrors.correlationType"
                        )
                                .exists()
                )
                .andExpect(
                        jsonPath(
                                "$.validationErrors.correlationStatus"
                        )
                                .exists()
                )
                .andExpect(
                        jsonPath(
                                "$.validationErrors.windowStart"
                        )
                                .exists()
                )
                .andExpect(
                        jsonPath(
                                "$.validationErrors.windowEnd"
                        )
                                .exists()
                )
                .andExpect(
                        jsonPath(
                                "$.validationErrors.eventCount"
                        )
                                .exists()
                )
                .andExpect(
                        jsonPath(
                                "$.validationErrors.matchedRuleCount"
                        )
                                .exists()
                );
    }

    @Test
    void shouldGetCorrelationById()
            throws Exception {

        String correlationKey =
                uniqueKey("ID");

        JsonNode created =
                createCorrelation(
                        null,
                        null,
                        correlationKey,
                        "GLOBAL",
                        "OPEN"
                );

        UUID correlationId =
                UUID.fromString(
                        created.get(
                                "correlationId"
                        ).asText()
                );

        mockMvc.perform(
                        get(
                                "/api/v1/detection/correlations/{correlationId}",
                                correlationId
                        )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.correlationId")
                                .value(
                                        correlationId.toString()
                                )
                )
                .andExpect(
                        jsonPath("$.correlationKey")
                                .value(correlationKey)
                )
                .andExpect(
                        jsonPath("$.correlationType")
                                .value("GLOBAL")
                )
                .andExpect(
                        jsonPath("$.correlationStatus")
                                .value("OPEN")
                );
    }

    @Test
    void shouldReturnNotFoundForUnknownCorrelation()
            throws Exception {

        UUID unknownCorrelationId =
                UUID.randomUUID();

        mockMvc.perform(
                        get(
                                "/api/v1/detection/correlations/{correlationId}",
                                unknownCorrelationId
                        )
                )
                .andExpect(status().isNotFound())
                .andExpect(
                        jsonPath("$.status")
                                .value(404)
                )
                .andExpect(
                        jsonPath("$.errorCode")
                                .value(
                                        "CUSTOMER_RESOURCE_NOT_FOUND"
                                )
                );
    }

    @Test
    void shouldGetCorrelationsByCustomer()
            throws Exception {

        Customer customer =
                createCustomer();

        JsonNode first =
                createCorrelation(
                        customer.getCustomerId(),
                        null,
                        uniqueKey("CUSTOMER-1"),
                        "CUSTOMER",
                        "OPEN"
                );

        JsonNode second =
                createCorrelation(
                        customer.getCustomerId(),
                        null,
                        uniqueKey("CUSTOMER-2"),
                        "CUSTOMER",
                        "OPEN"
                );

        mockMvc.perform(
                        get(
                                "/api/v1/detection/correlations/customer/{customerId}",
                                customer.getCustomerId()
                        )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$")
                                .isArray()
                )
                .andExpect(
                        jsonPath(
                                "$[*].correlationId"
                        )
                                .value(
                                        hasItem(
                                                first.get(
                                                        "correlationId"
                                                ).asText()
                                        )
                                )
                )
                .andExpect(
                        jsonPath(
                                "$[*].correlationId"
                        )
                                .value(
                                        hasItem(
                                                second.get(
                                                        "correlationId"
                                                ).asText()
                                        )
                                )
                );
    }

    @Test
    void shouldGetCorrelationsByTransaction()
            throws Exception {

        Customer customer =
                createCustomer();

        Transaction transaction =
                createTransaction(
                        customer.getCustomerId()
                );

        JsonNode created =
                createCorrelation(
                        customer.getCustomerId(),
                        transaction.getTransactionId(),
                        uniqueKey("TRANSACTION"),
                        "TRANSACTION",
                        "OPEN"
                );

        mockMvc.perform(
                        get(
                                "/api/v1/detection/correlations/transaction/{transactionId}",
                                transaction.getTransactionId()
                        )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$")
                                .isArray()
                )
                .andExpect(
                        jsonPath(
                                "$[*].correlationId"
                        )
                                .value(
                                        hasItem(
                                                created.get(
                                                        "correlationId"
                                                ).asText()
                                        )
                                )
                );
    }

    @Test
    void shouldGetCorrelationsByKey()
            throws Exception {

        String correlationKey =
                uniqueKey("KEY");

        JsonNode created =
                createCorrelation(
                        null,
                        null,
                        correlationKey,
                        "GLOBAL",
                        "OPEN"
                );

        mockMvc.perform(
                        get(
                                "/api/v1/detection/correlations/key/{correlationKey}",
                                correlationKey
                        )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$")
                                .isArray()
                )
                .andExpect(
                        jsonPath(
                                "$[*].correlationId"
                        )
                                .value(
                                        hasItem(
                                                created.get(
                                                        "correlationId"
                                                ).asText()
                                        )
                                )
                )
                .andExpect(
                        jsonPath(
                                "$[*].correlationKey"
                        )
                                .value(
                                        hasItem(
                                                correlationKey
                                        )
                                )
                );
    }

    @Test
    void shouldGetCorrelationsByType()
            throws Exception {

        String correlationType =
                "CTRL_TYPE_" +
                        UUID.randomUUID()
                                .toString()
                                .substring(
                                        0,
                                        8
                                );

        JsonNode created =
                createCorrelation(
                        null,
                        null,
                        uniqueKey("TYPE"),
                        correlationType,
                        "OPEN"
                );

        mockMvc.perform(
                        get(
                                "/api/v1/detection/correlations/type/{correlationType}",
                                correlationType
                        )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$")
                                .isArray()
                )
                .andExpect(
                        jsonPath(
                                "$[*].correlationId"
                        )
                                .value(
                                        hasItem(
                                                created.get(
                                                        "correlationId"
                                                ).asText()
                                        )
                                )
                )
                .andExpect(
                        jsonPath(
                                "$[*].correlationType"
                        )
                                .value(
                                        hasItem(
                                                correlationType
                                        )
                                )
                );
    }

    @Test
    void shouldGetCorrelationsByStatus()
            throws Exception {

        String correlationStatus =
                "CTRL_" +
                        UUID.randomUUID()
                                .toString()
                                .substring(
                                        0,
                                        8
                                );

        JsonNode created =
                createCorrelation(
                        null,
                        null,
                        uniqueKey("STATUS"),
                        "GLOBAL",
                        correlationStatus
                );

        mockMvc.perform(
                        get(
                                "/api/v1/detection/correlations/status/{correlationStatus}",
                                correlationStatus
                        )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$")
                                .isArray()
                )
                .andExpect(
                        jsonPath(
                                "$[*].correlationId"
                        )
                                .value(
                                        hasItem(
                                                created.get(
                                                        "correlationId"
                                                ).asText()
                                        )
                                )
                )
                .andExpect(
                        jsonPath(
                                "$[*].correlationStatus"
                        )
                                .value(
                                        hasItem(
                                                correlationStatus
                                        )
                                )
                );
    }

    private JsonNode createCorrelation(
            UUID customerId,
            UUID transactionId,
            String correlationKey,
            String correlationType,
            String correlationStatus)
            throws Exception {

        Map<String, Object> request =
                validCorrelationRequest(
                        customerId,
                        transactionId,
                        correlationKey,
                        correlationType,
                        correlationStatus
                );

        MvcResult result =
                mockMvc.perform(
                                post(
                                        "/api/v1/detection/correlations"
                                )
                                        .contentType(
                                                MediaType.APPLICATION_JSON
                                        )
                                        .content(
                                                objectMapper
                                                        .writeValueAsString(
                                                                request
                                                        )
                                        )
                        )
                        .andExpect(status().isCreated())
                        .andReturn();

        return objectMapper.readTree(
                result.getResponse()
                        .getContentAsString()
        );
    }

    private Map<String, Object> validCorrelationRequest(
            UUID customerId,
            UUID transactionId,
            String correlationKey,
            String correlationType,
            String correlationStatus) {

        Map<String, Object> request =
                new LinkedHashMap<>();

        if (customerId != null) {
            request.put(
                    "customerId",
                    customerId
            );
        }

        if (transactionId != null) {
            request.put(
                    "transactionId",
                    transactionId
            );
        }

        request.put(
                "correlationKey",
                correlationKey
        );

        request.put(
                "correlationType",
                correlationType
        );

        request.put(
                "correlationStatus",
                correlationStatus
        );

        request.put(
                "windowStart",
                "2026-08-30T12:00:00"
        );

        request.put(
                "windowEnd",
                "2026-08-30T12:30:00"
        );

        request.put(
                "eventCount",
                3
        );

        request.put(
                "matchedRuleCount",
                1
        );

        return request;
    }

    private Customer createCustomer() {

        LocalDateTime now =
                LocalDateTime.now();

        Customer customer =
                new Customer();

        customer.setCustomerNumber(
                "CORR-CTRL-" + UUID.randomUUID()
        );

        customer.setCustomerType(
                "INDIVIDUAL"
        );

        customer.setFirstName(
                "Correlation"
        );

        customer.setLastName(
                "Controller"
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

        return customerRepository.saveAndFlush(
                customer
        );
    }

    private Transaction createTransaction(
            UUID customerId) {

        LocalDateTime now =
                LocalDateTime.now();

        Transaction transaction =
                new Transaction();

        transaction.setTransactionReference(
                "EFS-CORR-CTRL-" + UUID.randomUUID()
        );

        transaction.setCustomerId(
                customerId
        );

        transaction.setOrganizationId(
                UUID.randomUUID()
        );

        transaction.setTransactionType(
                "PAYMENT"
        );

        transaction.setAmount(
                new BigDecimal("450.00")
        );

        transaction.setCurrencyCode(
                "GTQ"
        );

        transaction.setTransactionDatetime(
                now
        );

        transaction.setTransactionStatus(
                "PENDING"
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
                UUID.randomUUID()
        );

        transaction.setRecordVersion(
                0
        );

        return transactionRepository.saveAndFlush(
                transaction
        );
    }

    private String uniqueKey(
            String prefix) {

        return "CTRL-" +
                prefix +
                "-" +
                UUID.randomUUID();
    }
}