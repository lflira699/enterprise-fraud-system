package com.efs.modules.transaction.controller;

import com.efs.modules.customer.entity.Customer;
import com.efs.modules.customer.repository.CustomerRepository;
import com.efs.modules.transaction.dto.TransactionEventRequest;
import com.efs.modules.transaction.entity.Transaction;
import com.efs.modules.transaction.repository.TransactionRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
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
import java.util.UUID;

import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class TransactionEventControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private CustomerRepository customerRepository;

    private UUID transactionId;

    @BeforeEach
    void setUp() {

        LocalDateTime now =
                LocalDateTime.now();

        Customer customer =
                new Customer();

        customer.setCustomerNumber(
                "TEVT-CTRL-" + UUID.randomUUID()
        );

        customer.setCustomerType(
                "INDIVIDUAL"
        );

        customer.setFirstName(
                "Transaction"
        );

        customer.setLastName(
                "Event"
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

        Customer savedCustomer =
                customerRepository.saveAndFlush(
                        customer
                );

        Transaction transaction =
                new Transaction();

        transaction.setTransactionReference(
                "TEVT-CTRL-TXN-" + UUID.randomUUID()
        );

        transaction.setCustomerId(
                savedCustomer.getCustomerId()
        );

        transaction.setOrganizationId(
                UUID.randomUUID()
        );

        transaction.setTransactionType(
                "PAYMENT"
        );

        transaction.setAmount(
                new BigDecimal("1000.00")
        );

        transaction.setCurrencyCode(
                "GTQ"
        );

        transaction.setTransactionDatetime(
                now
        );

        transaction.setTransactionStatus(
                "RECEIVED"
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

        Transaction savedTransaction =
                transactionRepository.saveAndFlush(
                        transaction
                );

        transactionId =
                savedTransaction.getTransactionId();
    }

    @Test
    void shouldCreateTransactionEvent()
            throws Exception {

        UUID correlationId =
                UUID.randomUUID();

        UUID requestId =
                UUID.randomUUID();

        TransactionEventRequest request =
                buildRequest(
                        "RISK_EVALUATION",
                        "RISK_ENGINE"
                );

        request.setEventResult(
                "MATCHED"
        );

        request.setSeverity(
                "HIGH"
        );

        request.setCorrelationId(
                correlationId
        );

        request.setRequestId(
                requestId
        );

        request.setEventMessage(
                "Risk evaluation completed"
        );

        request.setExecutionTimeMs(
                125
        );

        mockMvc.perform(
                        post(
                                "/api/v1/transactions/{transactionId}/events",
                                transactionId
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
                        jsonPath("$.eventId")
                                .exists()
                )
                .andExpect(
                        jsonPath("$.transactionId")
                                .value(
                                        transactionId.toString()
                                )
                )
                .andExpect(
                        jsonPath("$.eventType")
                                .value("RISK_EVALUATION")
                )
                .andExpect(
                        jsonPath("$.eventTimestamp")
                                .exists()
                )
                .andExpect(
                        jsonPath("$.componentName")
                                .value("RISK_ENGINE")
                )
                .andExpect(
                        jsonPath("$.eventResult")
                                .value("MATCHED")
                )
                .andExpect(
                        jsonPath("$.severity")
                                .value("HIGH")
                )
                .andExpect(
                        jsonPath("$.correlationId")
                                .value(
                                        correlationId.toString()
                                )
                )
                .andExpect(
                        jsonPath("$.requestId")
                                .value(
                                        requestId.toString()
                                )
                )
                .andExpect(
                        jsonPath("$.eventMessage")
                                .value(
                                        "Risk evaluation completed"
                                )
                )
                .andExpect(
                        jsonPath("$.executionTimeMs")
                                .value(125)
                );
    }

    @Test
    void shouldRejectInvalidTransactionEventRequest()
            throws Exception {

        TransactionEventRequest request =
                buildRequest(
                        " ",
                        "RISK_ENGINE"
                );

        mockMvc.perform(
                        post(
                                "/api/v1/transactions/{transactionId}/events",
                                transactionId
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
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnNotFoundWhenCreatingEventForUnknownTransaction()
            throws Exception {

        mockMvc.perform(
                        post(
                                "/api/v1/transactions/{transactionId}/events",
                                UUID.randomUUID()
                        )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        objectMapper.writeValueAsString(
                                                buildRequest(
                                                        "RECEIVED",
                                                        "TRANSACTION_ENGINE"
                                                )
                                        )
                                )
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldGetTransactionEventById()
            throws Exception {

        JsonNode created =
                createEvent(
                        "RISK_EVALUATION",
                        "RISK_ENGINE",
                        null
                );

        UUID eventId =
                UUID.fromString(
                        created.get(
                                "eventId"
                        ).asText()
                );

        mockMvc.perform(
                        get(
                                "/api/v1/transactions/events/{eventId}",
                                eventId
                        )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.eventId")
                                .value(
                                        eventId.toString()
                                )
                )
                .andExpect(
                        jsonPath("$.transactionId")
                                .value(
                                        transactionId.toString()
                                )
                )
                .andExpect(
                        jsonPath("$.eventType")
                                .value("RISK_EVALUATION")
                )
                .andExpect(
                        jsonPath("$.componentName")
                                .value("RISK_ENGINE")
                );
    }

    @Test
    void shouldReturnNotFoundForUnknownEvent()
            throws Exception {

        mockMvc.perform(
                        get(
                                "/api/v1/transactions/events/{eventId}",
                                UUID.randomUUID()
                        )
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldGetEventsByTransactionId()
            throws Exception {

        JsonNode first =
                createEvent(
                        "RECEIVED",
                        "TRANSACTION_ENGINE",
                        null
                );

        JsonNode second =
                createEvent(
                        "RISK_EVALUATION",
                        "RISK_ENGINE",
                        null
                );

        mockMvc.perform(
                        get(
                                "/api/v1/transactions/{transactionId}/events",
                                transactionId
                        )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$")
                                .isArray()
                )
                .andExpect(
                        jsonPath("$[*].eventId")
                                .value(
                                        hasItem(
                                                first.get(
                                                        "eventId"
                                                ).asText()
                                        )
                                )
                )
                .andExpect(
                        jsonPath("$[*].eventId")
                                .value(
                                        hasItem(
                                                second.get(
                                                        "eventId"
                                                ).asText()
                                        )
                                )
                );
    }

    @Test
    void shouldGetEventsByType()
            throws Exception {

        String eventType =
                "TYPE_" +
                        UUID.randomUUID()
                                .toString()
                                .substring(0, 8);

        JsonNode expected =
                createEvent(
                        eventType,
                        "COMPONENT_A",
                        null
                );

        createEvent(
                "OTHER_" +
                        UUID.randomUUID()
                                .toString()
                                .substring(0, 8),
                "COMPONENT_B",
                null
        );

        mockMvc.perform(
                        get(
                                "/api/v1/transactions/events/type/{eventType}",
                                eventType
                        )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$")
                                .isArray()
                )
                .andExpect(
                        jsonPath("$[*].eventId")
                                .value(
                                        hasItem(
                                                expected.get(
                                                        "eventId"
                                                ).asText()
                                        )
                                )
                )
                .andExpect(
                        jsonPath("$[*].eventType")
                                .value(
                                        hasItem(eventType)
                                )
                );
    }

    @Test
    void shouldGetEventsByComponentName()
            throws Exception {

        String componentName =
                "COMP_" +
                        UUID.randomUUID()
                                .toString()
                                .substring(0, 8);

        JsonNode expected =
                createEvent(
                        "EVENT_A",
                        componentName,
                        null
                );

        createEvent(
                "EVENT_B",
                "OTHER_COMPONENT",
                null
        );

        mockMvc.perform(
                        get(
                                "/api/v1/transactions/events/component/{componentName}",
                                componentName
                        )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$")
                                .isArray()
                )
                .andExpect(
                        jsonPath("$[*].eventId")
                                .value(
                                        hasItem(
                                                expected.get(
                                                        "eventId"
                                                ).asText()
                                        )
                                )
                )
                .andExpect(
                        jsonPath("$[*].componentName")
                                .value(
                                        hasItem(componentName)
                                )
                );
    }

    @Test
    void shouldGetEventsByCorrelationId()
            throws Exception {

        UUID correlationId =
                UUID.randomUUID();

        JsonNode first =
                createEvent(
                        "EVENT_A",
                        "RISK_ENGINE",
                        correlationId
                );

        JsonNode second =
                createEvent(
                        "EVENT_B",
                        "RULE_ENGINE",
                        correlationId
                );

        createEvent(
                "EVENT_C",
                "TRANSACTION_ENGINE",
                UUID.randomUUID()
        );

        mockMvc.perform(
                        get(
                                "/api/v1/transactions/events/correlation/{correlationId}",
                                correlationId
                        )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$")
                                .isArray()
                )
                .andExpect(
                        jsonPath("$[*].eventId")
                                .value(
                                        hasItem(
                                                first.get(
                                                        "eventId"
                                                ).asText()
                                        )
                                )
                )
                .andExpect(
                        jsonPath("$[*].eventId")
                                .value(
                                        hasItem(
                                                second.get(
                                                        "eventId"
                                                ).asText()
                                        )
                                )
                )
                .andExpect(
                        jsonPath("$[*].correlationId")
                                .value(
                                        hasItem(
                                                correlationId.toString()
                                        )
                                )
                );
    }

    private JsonNode createEvent(
            String eventType,
            String componentName,
            UUID correlationId)
            throws Exception {

        TransactionEventRequest request =
                buildRequest(
                        eventType,
                        componentName
                );

        request.setCorrelationId(
                correlationId
        );

        MvcResult result =
                mockMvc.perform(
                                post(
                                        "/api/v1/transactions/{transactionId}/events",
                                        transactionId
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

    private TransactionEventRequest buildRequest(
            String eventType,
            String componentName) {

        TransactionEventRequest request =
                new TransactionEventRequest();

        request.setEventType(
                eventType
        );

        request.setComponentName(
                componentName
        );

        return request;
    }
}