package com.efs.modules.transaction.controller;

import com.efs.modules.administration.dto.UserAccountReference;
import com.efs.modules.administration.service.UserAccountLookupServiceInterface;
import com.efs.modules.customer.entity.Customer;
import com.efs.modules.customer.repository.CustomerRepository;
import com.efs.modules.transaction.dto.TransactionEventRequest;
import com.efs.modules.transaction.entity.Transaction;
import com.efs.modules.transaction.repository.TransactionRepository;
import com.efs.shared.security.SecurityContext;
import com.efs.shared.security.SecurityContextProvider;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.when;
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

    @MockitoBean
    private SecurityContextProvider securityContextProvider;

    @MockitoBean
    private UserAccountLookupServiceInterface
            userAccountLookupService;

    private final UUID actorId =
            UUID.randomUUID();

    private UUID organizationId;
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

        organizationId =
                UUID.randomUUID();

        transaction.setOrganizationId(
                organizationId
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

        authorize(
                allPermissions(),
                organizationId,
                null
        );
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

    @Test
    void shouldRejectNegativeExecutionTime()
            throws Exception {

        TransactionEventRequest request =
                buildRequest(
                        "VALIDATION",
                        "TRANSACTION"
                );

        request.setExecutionTimeMs(-1);

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
    void shouldReturnNotFoundWhenCreatingEventForSoftDeletedTransaction()
            throws Exception {

        softDeleteTransaction();

        TransactionEventRequest request =
                buildRequest(
                        "CREATE_SOFT",
                        "TRANSACTION"
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
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnNotFoundForEventWhenParentTransactionIsSoftDeleted()
            throws Exception {

        JsonNode created =
                createEvent(
                        "SOFT_ID",
                        "TRANSACTION",
                        null
                );

        UUID eventId =
                UUID.fromString(
                        created.get(
                                "eventId"
                        ).asText()
                );

        softDeleteTransaction();

        mockMvc.perform(
                        get(
                                "/api/v1/transactions/events/{eventId}",
                                eventId
                        )
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnNotFoundForEventListWhenParentTransactionIsSoftDeleted()
            throws Exception {

        softDeleteTransaction();

        mockMvc.perform(
                        get(
                                "/api/v1/transactions/{transactionId}/events",
                                transactionId
                        )
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldExcludeSoftDeletedParentWhenQueryingEventsByType()
            throws Exception {

        String eventType =
                "TYPE_SOFT_" +
                        UUID.randomUUID()
                                .toString()
                                .substring(0, 8);

        createEvent(
                eventType,
                "TRANSACTION",
                null
        );

        softDeleteTransaction();

        mockMvc.perform(
                        get(
                                "/api/v1/transactions/events/type/{eventType}",
                                eventType
                        )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void shouldExcludeSoftDeletedParentWhenQueryingEventsByComponent()
            throws Exception {

        String componentName =
                "COMP_SOFT_" +
                        UUID.randomUUID()
                                .toString()
                                .substring(0, 8);

        createEvent(
                "PROCESSING",
                componentName,
                null
        );

        softDeleteTransaction();

        mockMvc.perform(
                        get(
                                "/api/v1/transactions/events/component/{componentName}",
                                componentName
                        )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void shouldExcludeSoftDeletedParentWhenQueryingEventsByCorrelationId()
            throws Exception {

        UUID correlationId =
                UUID.randomUUID();

        createEvent(
                "CORRELATION",
                "TRANSACTION",
                correlationId
        );

        softDeleteTransaction();

        mockMvc.perform(
                        get(
                                "/api/v1/transactions/events/correlation/{correlationId}",
                                correlationId
                        )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void shouldReturnForbiddenWhenCreatingWithoutTransactionUpdate()
            throws Exception {

        authorize(
                Set.of("transaction.view"),
                organizationId,
                null
        );

        TransactionEventRequest request =
                buildRequest(
                        "SECURITY_CREATE",
                        "TRANSACTION"
                );

        mockMvc.perform(
                        post(
                                "/api/v1/transactions/{transactionId}/events",
                                transactionId
                        )
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(
                                                request
                                        )
                                )
                )
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldReturnForbiddenWhenReadingWithoutTransactionView()
            throws Exception {

        JsonNode created =
                createEvent(
                        "SECURITY_READ",
                        "TRANSACTION",
                        null
                );

        authorize(
                Set.of("transaction.update"),
                organizationId,
                null
        );

        mockMvc.perform(
                        get(
                                "/api/v1/transactions/events/{eventId}",
                                UUID.fromString(
                                        created.get("eventId").asText()
                                )
                        )
                )
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldHideCrossOrganizationEventById()
            throws Exception {

        UUID authorizedOrganization =
                organizationId;

        UUID otherOrganization =
                UUID.randomUUID();

        UUID otherTransaction =
                createTransactionForOrganization(
                        otherOrganization
                );

        authorize(
                allPermissions(),
                otherOrganization,
                null
        );

        JsonNode hidden =
                createEventForTransaction(
                        otherTransaction,
                        "SECURITY_ID",
                        "TRANSACTION",
                        null
                );

        authorize(
                allPermissions(),
                authorizedOrganization,
                null
        );

        mockMvc.perform(
                        get(
                                "/api/v1/transactions/events/{eventId}",
                                UUID.fromString(
                                        hidden.get("eventId").asText()
                                )
                        )
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldHideCrossOrganizationEventList()
            throws Exception {

        UUID otherTransaction =
                createTransactionForOrganization(
                        UUID.randomUUID()
                );

        mockMvc.perform(
                        get(
                                "/api/v1/transactions/{transactionId}/events",
                                otherTransaction
                        )
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldHideCrossOrganizationEventCreate()
            throws Exception {

        UUID otherTransaction =
                createTransactionForOrganization(
                        UUID.randomUUID()
                );

        TransactionEventRequest request =
                buildRequest(
                        "SECURITY_CROSS_CREATE",
                        "TRANSACTION"
                );

        mockMvc.perform(
                        post(
                                "/api/v1/transactions/{transactionId}/events",
                                otherTransaction
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
    void shouldFilterCrossOrganizationEventsFromGlobalQueries()
            throws Exception {

        UUID authorizedOrganization =
                organizationId;

        String eventType =
                "SECURITY_FILTER";

        String componentName =
                "SECURITY_COMPONENT";

        UUID correlationId =
                UUID.randomUUID();

        createEvent(
                eventType,
                componentName,
                correlationId
        );

        UUID otherOrganization =
                UUID.randomUUID();

        UUID otherTransaction =
                createTransactionForOrganization(
                        otherOrganization
                );

        authorize(
                allPermissions(),
                otherOrganization,
                null
        );

        createEventForTransaction(
                otherTransaction,
                eventType,
                componentName,
                correlationId
        );

        authorize(
                allPermissions(),
                authorizedOrganization,
                null
        );

        mockMvc.perform(
                        get(
                                "/api/v1/transactions/events/type/{eventType}",
                                eventType
                        )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(
                        jsonPath("$[0].transactionId")
                                .value(transactionId.toString())
                );

        mockMvc.perform(
                        get(
                                "/api/v1/transactions/events/component/{componentName}",
                                componentName
                        )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(
                        jsonPath("$[0].transactionId")
                                .value(transactionId.toString())
                );

        mockMvc.perform(
                        get(
                                "/api/v1/transactions/events/correlation/{correlationId}",
                                correlationId
                        )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(
                        jsonPath("$[0].transactionId")
                                .value(transactionId.toString())
                );
    }

    private UUID createTransactionForOrganization(
            UUID targetOrganizationId) {

        Transaction source =
                transactionRepository
                        .findById(transactionId)
                        .orElseThrow();

        LocalDateTime now =
                LocalDateTime.now();

        Transaction transaction =
                new Transaction();

        transaction.setTransactionReference(
                "TEVT-SEC-TXN-" + UUID.randomUUID()
        );

        transaction.setCustomerId(
                source.getCustomerId()
        );

        transaction.setOrganizationId(
                targetOrganizationId
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

        return transactionRepository
                .saveAndFlush(transaction)
                .getTransactionId();
    }

    private JsonNode createEventForTransaction(
            UUID targetTransactionId,
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
                                        targetTransactionId
                                )
                                        .contentType(MediaType.APPLICATION_JSON)
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

    private void authorize(
            Set<String> permissions,
            UUID targetOrganizationId,
            UUID tenantId) {

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
                        .getAuthorizedUser(actorId)
        ).thenReturn(
                new UserAccountReference(
                        actorId,
                        targetOrganizationId,
                        tenantId,
                        "transaction-event-controller@example.com"
                )
        );
    }

    private Set<String> allPermissions() {

        return Set.of(
                "transaction.view",
                "transaction.update"
        );
    }
    private void softDeleteTransaction() {

        Transaction transaction =
                transactionRepository
                        .findById(transactionId)
                        .orElseThrow();

        transaction.setDeletedAt(
                LocalDateTime.now()
        );

        transactionRepository.saveAndFlush(
                transaction
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
