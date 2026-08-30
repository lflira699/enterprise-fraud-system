package com.efs.modules.detection.controller;

import com.efs.modules.customer.entity.Customer;
import com.efs.modules.customer.repository.CustomerRepository;
import com.efs.modules.detection.entity.Correlation;
import com.efs.modules.detection.repository.CorrelationRepository;
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
class TimelineEventControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private CorrelationRepository correlationRepository;

    private UUID customerId;
    private UUID transactionId;
    private UUID correlationId;

    @BeforeEach
    void setUp() {

        LocalDateTime now =
                LocalDateTime.now();

        Customer customer =
                new Customer();

        customer.setCustomerNumber(
                "TE-CTRL-" + UUID.randomUUID()
        );

        customer.setCustomerType(
                "INDIVIDUAL"
        );

        customer.setFirstName(
                "Timeline"
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

        Customer savedCustomer =
                customerRepository.saveAndFlush(
                        customer
                );

        customerId =
                savedCustomer.getCustomerId();

        Transaction transaction =
                new Transaction();

        transaction.setTransactionReference(
                "EFS-TE-CTRL-" + UUID.randomUUID()
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
                new BigDecimal("1500.00")
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

        Transaction savedTransaction =
                transactionRepository.saveAndFlush(
                        transaction
                );

        transactionId =
                savedTransaction.getTransactionId();

        Correlation correlation =
                new Correlation();

        correlation.setCustomerId(
                customerId
        );

        correlation.setTransactionId(
                transactionId
        );

        correlation.setCorrelationKey(
                "TE-CTRL-CORR-" + UUID.randomUUID()
        );

        correlation.setCorrelationType(
                "TRANSACTION"
        );

        correlation.setCorrelationStatus(
                "OPEN"
        );

        correlation.setWindowStart(
                now.minusMinutes(30)
        );

        correlation.setWindowEnd(
                now
        );

        correlation.setEventCount(
                1
        );

        correlation.setMatchedRuleCount(
                (short) 0
        );

        correlation.setConfidence(
                new BigDecimal("0.7500")
        );

        correlation.setCreatedAt(
                now
        );

        correlation.setUpdatedAt(
                now
        );

        Correlation savedCorrelation =
                correlationRepository.saveAndFlush(
                        correlation
                );

        correlationId =
                savedCorrelation.getCorrelationId();
    }

    @Test
    void shouldCreateTimelineEventWithFullPayload()
            throws Exception {

        UUID eventReferenceId =
                UUID.randomUUID();

        LocalDateTime eventTimestamp =
                LocalDateTime.now()
                        .minusMinutes(5)
                        .withNano(0);

        Map<String, Object> request =
                fullRequest(
                        "TRANSACTION_EVENT",
                        "DETECTION",
                        eventTimestamp,
                        eventReferenceId,
                        1
                );

        mockMvc.perform(
                        post(
                                "/api/v1/detection/timeline-events"
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
                        jsonPath("$.timelineEventId")
                                .exists()
                )
                .andExpect(
                        jsonPath("$.customerId")
                                .value(customerId.toString())
                )
                .andExpect(
                        jsonPath("$.transactionId")
                                .value(transactionId.toString())
                )
                .andExpect(
                        jsonPath("$.correlationId")
                                .value(correlationId.toString())
                )
                .andExpect(
                        jsonPath("$.eventType")
                                .value("TRANSACTION_EVENT")
                )
                .andExpect(
                        jsonPath("$.eventSource")
                                .value("DETECTION")
                )
                .andExpect(
                        jsonPath("$.eventReferenceId")
                                .value(eventReferenceId.toString())
                )
                .andExpect(
                        jsonPath("$.eventTimestamp")
                                .exists()
                )
                .andExpect(
                        jsonPath("$.sequenceNumber")
                                .value(1)
                )
                .andExpect(
                        jsonPath("$.eventSummary")
                                .value(
                                        "Timeline event created by controller integration test"
                                )
                )
                .andExpect(
                        jsonPath("$.eventData.riskLevel")
                                .value("HIGH")
                )
                .andExpect(
                        jsonPath("$.eventData.score")
                                .value(87)
                )
                .andExpect(
                        jsonPath("$.eventData.reviewRequired")
                                .value(true)
                )
                .andExpect(
                        jsonPath("$.createdAt")
                                .exists()
                );
    }

    @Test
    void shouldCreateTimelineEventWithOnlyRequiredFields()
            throws Exception {

        Map<String, Object> request =
                requiredRequest(
                        "STANDALONE_EVENT",
                        LocalDateTime.now()
                                .withNano(0)
                );

        mockMvc.perform(
                        post(
                                "/api/v1/detection/timeline-events"
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
                        jsonPath("$.timelineEventId")
                                .exists()
                )
                .andExpect(
                        jsonPath("$.eventType")
                                .value("STANDALONE_EVENT")
                )
                .andExpect(
                        jsonPath("$.eventTimestamp")
                                .exists()
                )
                .andExpect(
                        jsonPath("$.createdAt")
                                .exists()
                );
    }

    @Test
    void shouldRejectCreateWhenRequiredFieldsAreMissing()
            throws Exception {

        mockMvc.perform(
                        post(
                                "/api/v1/detection/timeline-events"
                        )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content("{}")
                )
                .andExpect(status().isBadRequest())
                .andExpect(
                        jsonPath("$.errorCode")
                                .value("VALIDATION_ERROR")
                )
                .andExpect(
                        jsonPath(
                                "$.validationErrors.eventType"
                        )
                                .exists()
                )
                .andExpect(
                        jsonPath(
                                "$.validationErrors.eventTimestamp"
                        )
                                .exists()
                );
    }

    @Test
    void shouldRejectCreateWhenSizedFieldsExceedMaximumLength()
            throws Exception {

        Map<String, Object> request =
                requiredRequest(
                        "E".repeat(51),
                        LocalDateTime.now()
                                .withNano(0)
                );

        request.put(
                "eventSource",
                "S".repeat(81)
        );

        request.put(
                "eventSummary",
                "X".repeat(501)
        );

        mockMvc.perform(
                        post(
                                "/api/v1/detection/timeline-events"
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
                                "$.validationErrors.eventType"
                        )
                                .exists()
                )
                .andExpect(
                        jsonPath(
                                "$.validationErrors.eventSource"
                        )
                                .exists()
                )
                .andExpect(
                        jsonPath(
                                "$.validationErrors.eventSummary"
                        )
                                .exists()
                );
    }

    @Test
    void shouldGetTimelineEventById()
            throws Exception {

        JsonNode created =
                createReferencedEvent(
                        "RISK_EVENT",
                        "RISK",
                        1
                );

        UUID timelineEventId =
                UUID.fromString(
                        created.get(
                                "timelineEventId"
                        ).asText()
                );

        mockMvc.perform(
                        get(
                                "/api/v1/detection/timeline-events/{timelineEventId}",
                                timelineEventId
                        )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.timelineEventId")
                                .value(timelineEventId.toString())
                )
                .andExpect(
                        jsonPath("$.customerId")
                                .value(customerId.toString())
                )
                .andExpect(
                        jsonPath("$.transactionId")
                                .value(transactionId.toString())
                )
                .andExpect(
                        jsonPath("$.correlationId")
                                .value(correlationId.toString())
                )
                .andExpect(
                        jsonPath("$.eventType")
                                .value("RISK_EVENT")
                )
                .andExpect(
                        jsonPath("$.eventSource")
                                .value("RISK")
                );
    }

    @Test
    void shouldReturnNotFoundForUnknownTimelineEvent()
            throws Exception {

        mockMvc.perform(
                        get(
                                "/api/v1/detection/timeline-events/{timelineEventId}",
                                UUID.randomUUID()
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
    void shouldGetEventsByCustomer()
            throws Exception {

        JsonNode first =
                createReferencedEvent(
                        "CUSTOMER_EVENT",
                        "CUSTOMER",
                        1
                );

        JsonNode second =
                createReferencedEvent(
                        "RISK_EVENT",
                        "RISK",
                        2
                );

        mockMvc.perform(
                        get(
                                "/api/v1/detection/timeline-events/customer/{customerId}",
                                customerId
                        )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$")
                                .isArray()
                )
                .andExpect(
                        jsonPath("$[*].timelineEventId")
                                .value(
                                        hasItem(
                                                first.get(
                                                        "timelineEventId"
                                                ).asText()
                                        )
                                )
                )
                .andExpect(
                        jsonPath("$[*].timelineEventId")
                                .value(
                                        hasItem(
                                                second.get(
                                                        "timelineEventId"
                                                ).asText()
                                        )
                                )
                );
    }

    @Test
    void shouldGetEventsByTransaction()
            throws Exception {

        JsonNode first =
                createReferencedEvent(
                        "TRANSACTION_EVENT",
                        "TRANSACTION",
                        1
                );

        JsonNode second =
                createReferencedEvent(
                        "RISK_EVENT",
                        "RISK",
                        2
                );

        mockMvc.perform(
                        get(
                                "/api/v1/detection/timeline-events/transaction/{transactionId}",
                                transactionId
                        )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$[*].timelineEventId")
                                .value(
                                        hasItem(
                                                first.get(
                                                        "timelineEventId"
                                                ).asText()
                                        )
                                )
                )
                .andExpect(
                        jsonPath("$[*].timelineEventId")
                                .value(
                                        hasItem(
                                                second.get(
                                                        "timelineEventId"
                                                ).asText()
                                        )
                                )
                );
    }

    @Test
    void shouldGetEventsByCorrelation()
            throws Exception {

        JsonNode first =
                createReferencedEvent(
                        "CORRELATION_EVENT",
                        "DETECTION",
                        1
                );

        JsonNode second =
                createReferencedEvent(
                        "CORRELATION_EVENT",
                        "DETECTION",
                        2
                );

        mockMvc.perform(
                        get(
                                "/api/v1/detection/timeline-events/correlation/{correlationId}",
                                correlationId
                        )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$[*].timelineEventId")
                                .value(
                                        hasItem(
                                                first.get(
                                                        "timelineEventId"
                                                ).asText()
                                        )
                                )
                )
                .andExpect(
                        jsonPath("$[*].timelineEventId")
                                .value(
                                        hasItem(
                                                second.get(
                                                        "timelineEventId"
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
                                .substring(
                                        0,
                                        8
                                );

        JsonNode first =
                createReferencedEvent(
                        eventType,
                        "SOURCE_A",
                        1
                );

        JsonNode second =
                createReferencedEvent(
                        eventType,
                        "SOURCE_B",
                        2
                );

        mockMvc.perform(
                        get(
                                "/api/v1/detection/timeline-events/type/{eventType}",
                                eventType
                        )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$[*].timelineEventId")
                                .value(
                                        hasItem(
                                                first.get(
                                                        "timelineEventId"
                                                ).asText()
                                        )
                                )
                )
                .andExpect(
                        jsonPath("$[*].timelineEventId")
                                .value(
                                        hasItem(
                                                second.get(
                                                        "timelineEventId"
                                                ).asText()
                                        )
                                )
                )
                .andExpect(
                        jsonPath("$[*].eventType")
                                .value(
                                        hasItem(
                                                eventType
                                        )
                                )
                );
    }

    @Test
    void shouldGetEventsBySource()
            throws Exception {

        String eventSource =
                "SRC_" +
                        UUID.randomUUID()
                                .toString()
                                .substring(
                                        0,
                                        8
                                );

        JsonNode first =
                createReferencedEvent(
                        "TRANSACTION_EVENT",
                        eventSource,
                        1
                );

        JsonNode second =
                createReferencedEvent(
                        "RISK_EVENT",
                        eventSource,
                        2
                );

        mockMvc.perform(
                        get(
                                "/api/v1/detection/timeline-events/source/{eventSource}",
                                eventSource
                        )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$[*].timelineEventId")
                                .value(
                                        hasItem(
                                                first.get(
                                                        "timelineEventId"
                                                ).asText()
                                        )
                                )
                )
                .andExpect(
                        jsonPath("$[*].timelineEventId")
                                .value(
                                        hasItem(
                                                second.get(
                                                        "timelineEventId"
                                                ).asText()
                                        )
                                )
                )
                .andExpect(
                        jsonPath("$[*].eventSource")
                                .value(
                                        hasItem(
                                                eventSource
                                        )
                                )
                );
    }

    private JsonNode createReferencedEvent(
            String eventType,
            String eventSource,
            Integer sequenceNumber)
            throws Exception {

        Map<String, Object> request =
                requiredRequest(
                        eventType,
                        LocalDateTime.now()
                                .plusSeconds(sequenceNumber)
                                .withNano(0)
                );

        request.put(
                "customerId",
                customerId
        );

        request.put(
                "transactionId",
                transactionId
        );

        request.put(
                "correlationId",
                correlationId
        );

        request.put(
                "eventSource",
                eventSource
        );

        request.put(
                "sequenceNumber",
                sequenceNumber
        );

        MvcResult result =
                mockMvc.perform(
                                post(
                                        "/api/v1/detection/timeline-events"
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

    private Map<String, Object> fullRequest(
            String eventType,
            String eventSource,
            LocalDateTime eventTimestamp,
            UUID eventReferenceId,
            Integer sequenceNumber) {

        Map<String, Object> request =
                requiredRequest(
                        eventType,
                        eventTimestamp
                );

        request.put(
                "customerId",
                customerId
        );

        request.put(
                "transactionId",
                transactionId
        );

        request.put(
                "correlationId",
                correlationId
        );

        request.put(
                "eventSource",
                eventSource
        );

        request.put(
                "eventReferenceId",
                eventReferenceId
        );

        request.put(
                "sequenceNumber",
                sequenceNumber
        );

        request.put(
                "eventSummary",
                "Timeline event created by controller integration test"
        );

        request.put(
                "eventData",
                Map.of(
                        "riskLevel", "HIGH",
                        "score", 87,
                        "reviewRequired", true
                )
        );

        return request;
    }

    private Map<String, Object> requiredRequest(
            String eventType,
            LocalDateTime eventTimestamp) {

        Map<String, Object> request =
                new LinkedHashMap<>();

        request.put(
                "eventType",
                eventType
        );

        request.put(
                "eventTimestamp",
                eventTimestamp
        );

        return request;
    }
}