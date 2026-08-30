package com.efs.modules.detection.controller;

import com.efs.modules.customer.entity.Customer;
import com.efs.modules.customer.repository.CustomerRepository;
import com.efs.modules.detection.entity.Correlation;
import com.efs.modules.detection.repository.CorrelationRepository;
import com.efs.modules.transaction.entity.Transaction;
import com.efs.modules.transaction.entity.TransactionEvent;
import com.efs.modules.transaction.repository.TransactionEventRepository;
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
class CorrelationEventControllerIntegrationTest {

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

    @Autowired
    private TransactionEventRepository transactionEventRepository;

    private UUID customerId;
    private UUID transactionId;
    private UUID correlationId;
    private UUID eventId;

    @BeforeEach
    void setUp() {

        LocalDateTime now =
                LocalDateTime.now();

        Customer customer =
                new Customer();

        customer.setCustomerNumber(
                "CE-CTRL-" + UUID.randomUUID()
        );

        customer.setCustomerType(
                "INDIVIDUAL"
        );

        customer.setFirstName(
                "Correlation"
        );

        customer.setLastName(
                "Event Controller"
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
                "EFS-CE-CTRL-" + UUID.randomUUID()
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
                new BigDecimal("500.00")
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
                createAndSaveCorrelation(
                        "BASE"
                );

        correlationId =
                correlation.getCorrelationId();

        TransactionEvent event =
                createAndSaveTransactionEvent(
                        "TRANSACTION_RECEIVED"
                );

        eventId =
                event.getEventId();
    }

    @Test
    void shouldCreateCorrelationEvent()
            throws Exception {

        String requestBody =
                """
                {
                  "correlationId": "%s",
                  "eventId": "%s",
                  "eventRole": "PRIMARY"
                }
                """.formatted(
                        correlationId,
                        eventId
                );

        mockMvc.perform(
                        post(
                                "/api/v1/detection/correlation-events"
                        )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(requestBody)
                )
                .andExpect(status().isCreated())
                .andExpect(
                        jsonPath("$.correlationEventId")
                                .exists()
                )
                .andExpect(
                        jsonPath("$.correlationId")
                                .value(correlationId.toString())
                )
                .andExpect(
                        jsonPath("$.eventId")
                                .value(eventId.toString())
                )
                .andExpect(
                        jsonPath("$.eventRole")
                                .value("PRIMARY")
                )
                .andExpect(
                        jsonPath("$.createdAt")
                                .exists()
                );
    }

    @Test
    void shouldCreateCorrelationEventWithoutRole()
            throws Exception {

        String requestBody =
                """
                {
                  "correlationId": "%s",
                  "eventId": "%s"
                }
                """.formatted(
                        correlationId,
                        eventId
                );

        mockMvc.perform(
                        post(
                                "/api/v1/detection/correlation-events"
                        )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(requestBody)
                )
                .andExpect(status().isCreated())
                .andExpect(
                        jsonPath("$.correlationEventId")
                                .exists()
                )
                .andExpect(
                        jsonPath("$.correlationId")
                                .value(correlationId.toString())
                )
                .andExpect(
                        jsonPath("$.eventId")
                                .value(eventId.toString())
                )
                .andExpect(
                        jsonPath("$.createdAt")
                                .exists()
                );
    }

    @Test
    void shouldRejectCreateWhenRequiredFieldsAreMissing()
            throws Exception {

        String requestBody =
                """
                {
                  "eventRole": "PRIMARY"
                }
                """;

        mockMvc.perform(
                        post(
                                "/api/v1/detection/correlation-events"
                        )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(requestBody)
                )
                .andExpect(status().isBadRequest())
                .andExpect(
                        jsonPath("$.errorCode")
                                .value("VALIDATION_ERROR")
                )
                .andExpect(
                        jsonPath(
                                "$.validationErrors.correlationId"
                        )
                                .exists()
                )
                .andExpect(
                        jsonPath(
                                "$.validationErrors.eventId"
                        )
                                .exists()
                );
    }

    @Test
    void shouldRejectEventRoleLongerThanFortyCharacters()
            throws Exception {

        String requestBody =
                """
                {
                  "correlationId": "%s",
                  "eventId": "%s",
                  "eventRole": "12345678901234567890123456789012345678901"
                }
                """.formatted(
                        correlationId,
                        eventId
                );

        mockMvc.perform(
                        post(
                                "/api/v1/detection/correlation-events"
                        )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(requestBody)
                )
                .andExpect(status().isBadRequest())
                .andExpect(
                        jsonPath("$.errorCode")
                                .value("VALIDATION_ERROR")
                )
                .andExpect(
                        jsonPath(
                                "$.validationErrors.eventRole"
                        )
                                .exists()
                );
    }

    @Test
    void shouldGetCorrelationEventById()
            throws Exception {

        JsonNode created =
                createCorrelationEvent(
                        correlationId,
                        eventId,
                        "TRIGGER"
                );

        UUID correlationEventId =
                UUID.fromString(
                        created.get(
                                "correlationEventId"
                        ).asText()
                );

        mockMvc.perform(
                        get(
                                "/api/v1/detection/correlation-events/{correlationEventId}",
                                correlationEventId
                        )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.correlationEventId")
                                .value(
                                        correlationEventId.toString()
                                )
                )
                .andExpect(
                        jsonPath("$.correlationId")
                                .value(correlationId.toString())
                )
                .andExpect(
                        jsonPath("$.eventId")
                                .value(eventId.toString())
                )
                .andExpect(
                        jsonPath("$.eventRole")
                                .value("TRIGGER")
                );
    }

    @Test
    void shouldReturnNotFoundForUnknownCorrelationEvent()
            throws Exception {

        mockMvc.perform(
                        get(
                                "/api/v1/detection/correlation-events/{correlationEventId}",
                                UUID.randomUUID()
                        )
                )
                .andExpect(status().isNotFound())
                .andExpect(
                        jsonPath("$.status")
                                .value(404)
                );
    }

    @Test
    void shouldGetEventsByCorrelation()
            throws Exception {

        TransactionEvent secondEvent =
                createAndSaveTransactionEvent(
                        "SECOND_EVENT"
                );

        JsonNode first =
                createCorrelationEvent(
                        correlationId,
                        eventId,
                        "PRIMARY"
                );

        JsonNode second =
                createCorrelationEvent(
                        correlationId,
                        secondEvent.getEventId(),
                        "SECONDARY"
                );

        mockMvc.perform(
                        get(
                                "/api/v1/detection/correlation-events/correlation/{correlationId}",
                                correlationId
                        )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$")
                                .isArray()
                )
                .andExpect(
                        jsonPath(
                                "$[*].correlationEventId"
                        )
                                .value(
                                        hasItem(
                                                first.get(
                                                        "correlationEventId"
                                                ).asText()
                                        )
                                )
                )
                .andExpect(
                        jsonPath(
                                "$[*].correlationEventId"
                        )
                                .value(
                                        hasItem(
                                                second.get(
                                                        "correlationEventId"
                                                ).asText()
                                        )
                                )
                );
    }

    @Test
    void shouldGetCorrelationsByEvent()
            throws Exception {

        Correlation secondCorrelation =
                createAndSaveCorrelation(
                        "SECOND"
                );

        JsonNode first =
                createCorrelationEvent(
                        correlationId,
                        eventId,
                        "PRIMARY"
                );

        JsonNode second =
                createCorrelationEvent(
                        secondCorrelation.getCorrelationId(),
                        eventId,
                        "RELATED"
                );

        mockMvc.perform(
                        get(
                                "/api/v1/detection/correlation-events/event/{eventId}",
                                eventId
                        )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$")
                                .isArray()
                )
                .andExpect(
                        jsonPath(
                                "$[*].correlationEventId"
                        )
                                .value(
                                        hasItem(
                                                first.get(
                                                        "correlationEventId"
                                                ).asText()
                                        )
                                )
                )
                .andExpect(
                        jsonPath(
                                "$[*].correlationEventId"
                        )
                                .value(
                                        hasItem(
                                                second.get(
                                                        "correlationEventId"
                                                ).asText()
                                        )
                                )
                );
    }

    @Test
    void shouldGetEventsByRole()
            throws Exception {

        String eventRole =
                "ROLE_" +
                        UUID.randomUUID()
                                .toString()
                                .substring(
                                        0,
                                        8
                                );

        TransactionEvent secondEvent =
                createAndSaveTransactionEvent(
                        "ROLE_EVENT"
                );

        JsonNode first =
                createCorrelationEvent(
                        correlationId,
                        eventId,
                        eventRole
                );

        JsonNode second =
                createCorrelationEvent(
                        correlationId,
                        secondEvent.getEventId(),
                        eventRole
                );

        mockMvc.perform(
                        get(
                                "/api/v1/detection/correlation-events/role/{eventRole}",
                                eventRole
                        )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$")
                                .isArray()
                )
                .andExpect(
                        jsonPath(
                                "$[*].correlationEventId"
                        )
                                .value(
                                        hasItem(
                                                first.get(
                                                        "correlationEventId"
                                                ).asText()
                                        )
                                )
                )
                .andExpect(
                        jsonPath(
                                "$[*].correlationEventId"
                        )
                                .value(
                                        hasItem(
                                                second.get(
                                                        "correlationEventId"
                                                ).asText()
                                        )
                                )
                )
                .andExpect(
                        jsonPath(
                                "$[*].eventRole"
                        )
                                .value(
                                        hasItem(eventRole)
                                )
                );
    }

    private JsonNode createCorrelationEvent(
            UUID targetCorrelationId,
            UUID targetEventId,
            String eventRole)
            throws Exception {

        String requestBody =
                """
                {
                  "correlationId": "%s",
                  "eventId": "%s",
                  "eventRole": "%s"
                }
                """.formatted(
                        targetCorrelationId,
                        targetEventId,
                        eventRole
                );

        MvcResult result =
                mockMvc.perform(
                                post(
                                        "/api/v1/detection/correlation-events"
                                )
                                        .contentType(
                                                MediaType.APPLICATION_JSON
                                        )
                                        .content(requestBody)
                        )
                        .andExpect(status().isCreated())
                        .andReturn();

        return objectMapper.readTree(
                result.getResponse()
                        .getContentAsString()
        );
    }

    private Correlation createAndSaveCorrelation(
            String suffix) {

        LocalDateTime now =
                LocalDateTime.now();

        Correlation correlation =
                new Correlation();

        correlation.setCustomerId(
                customerId
        );

        correlation.setTransactionId(
                transactionId
        );

        correlation.setCorrelationKey(
                "CE-CTRL-" +
                        suffix +
                        "-" +
                        UUID.randomUUID()
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
                BigDecimal.ZERO
        );

        correlation.setCreatedAt(
                now
        );

        correlation.setUpdatedAt(
                now
        );

        return correlationRepository.saveAndFlush(
                correlation
        );
    }

    private TransactionEvent createAndSaveTransactionEvent(
            String eventType) {

        LocalDateTime now =
                LocalDateTime.now();

        TransactionEvent transactionEvent =
                new TransactionEvent();

        transactionEvent.setTransactionId(
                transactionId
        );

        transactionEvent.setEventType(
                eventType
        );

        transactionEvent.setEventTimestamp(
                now
        );

        transactionEvent.setComponentName(
                "CORRELATION_EVENT_CONTROLLER_TEST"
        );

        transactionEvent.setEventResult(
                "SUCCESS"
        );

        transactionEvent.setSeverity(
                "INFO"
        );

        transactionEvent.setEventMessage(
                "Correlation event controller integration test"
        );

        transactionEvent.setExecutionTimeMs(
                10
        );

        return transactionEventRepository.saveAndFlush(
                transactionEvent
        );
    }
}