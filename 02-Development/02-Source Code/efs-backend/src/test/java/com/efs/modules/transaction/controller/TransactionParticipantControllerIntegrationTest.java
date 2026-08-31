package com.efs.modules.transaction.controller;

import com.efs.modules.customer.entity.Customer;
import com.efs.modules.customer.repository.CustomerRepository;
import com.efs.modules.transaction.dto.TransactionParticipantRequest;
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
class TransactionParticipantControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private CustomerRepository customerRepository;

    private UUID transactionId;
    private UUID customerId;

    @BeforeEach
    void setUp() {

        LocalDateTime now =
                LocalDateTime.now();

        Customer customer =
                createCustomer(
                        "TP-CTRL-"
                );

        customerId =
                customer.getCustomerId();

        Transaction transaction =
                new Transaction();

        transaction.setTransactionReference(
                "TP-CTRL-TXN-" + UUID.randomUUID()
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
                new BigDecimal("900.00")
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
    void shouldCreateTransactionParticipant()
            throws Exception {

        UUID institutionId =
                UUID.randomUUID();

        String externalIdentifier =
                "EXT-" + UUID.randomUUID();

        TransactionParticipantRequest request =
                buildRequest(
                        "SENDER"
                );

        request.setCustomerId(
                customerId
        );

        request.setExternalIdentifier(
                externalIdentifier
        );

        request.setInstitutionId(
                institutionId
        );

        request.setCountryCode(
                "GT"
        );

        request.setRiskLevel(
                "LOW"
        );

        mockMvc.perform(
                        post(
                                "/api/v1/transactions/{transactionId}/participants",
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
                        jsonPath("$.participantId")
                                .exists()
                )
                .andExpect(
                        jsonPath("$.transactionId")
                                .value(
                                        transactionId.toString()
                                )
                )
                .andExpect(
                        jsonPath("$.participantType")
                                .value("SENDER")
                )
                .andExpect(
                        jsonPath("$.customerId")
                                .value(
                                        customerId.toString()
                                )
                )
                .andExpect(
                        jsonPath("$.externalIdentifier")
                                .value(
                                        externalIdentifier
                                )
                )
                .andExpect(
                        jsonPath("$.institutionId")
                                .value(
                                        institutionId.toString()
                                )
                )
                .andExpect(
                        jsonPath("$.countryCode")
                                .value("GT")
                )
                .andExpect(
                        jsonPath("$.riskLevel")
                                .value("LOW")
                )
                .andExpect(
                        jsonPath("$.createdAt")
                                .exists()
                );
    }

    @Test
    void shouldRejectBlankParticipantType()
            throws Exception {

        TransactionParticipantRequest request =
                buildRequest(
                        " "
                );

        mockMvc.perform(
                        post(
                                "/api/v1/transactions/{transactionId}/participants",
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
    void shouldRejectInvalidCountryCodeLength()
            throws Exception {

        TransactionParticipantRequest request =
                buildRequest(
                        "SENDER"
                );

        request.setCountryCode(
                "GTM"
        );

        mockMvc.perform(
                        post(
                                "/api/v1/transactions/{transactionId}/participants",
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
    void shouldReturnNotFoundWhenCreatingParticipantForUnknownTransaction()
            throws Exception {

        mockMvc.perform(
                        post(
                                "/api/v1/transactions/{transactionId}/participants",
                                UUID.randomUUID()
                        )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        objectMapper.writeValueAsString(
                                                buildRequest(
                                                        "SENDER"
                                                )
                                        )
                                )
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnNotFoundWhenCreatingParticipantForUnknownCustomer()
            throws Exception {

        TransactionParticipantRequest request =
                buildRequest(
                        "SENDER"
                );

        request.setCustomerId(
                UUID.randomUUID()
        );

        mockMvc.perform(
                        post(
                                "/api/v1/transactions/{transactionId}/participants",
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
    void shouldGetTransactionParticipantById()
            throws Exception {

        JsonNode created =
                createParticipant(
                        "SENDER",
                        customerId
                );

        UUID participantId =
                UUID.fromString(
                        created.get(
                                "participantId"
                        ).asText()
                );

        mockMvc.perform(
                        get(
                                "/api/v1/transactions/participants/{participantId}",
                                participantId
                        )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.participantId")
                                .value(
                                        participantId.toString()
                                )
                )
                .andExpect(
                        jsonPath("$.transactionId")
                                .value(
                                        transactionId.toString()
                                )
                )
                .andExpect(
                        jsonPath("$.participantType")
                                .value("SENDER")
                )
                .andExpect(
                        jsonPath("$.customerId")
                                .value(
                                        customerId.toString()
                                )
                );
    }

    @Test
    void shouldReturnNotFoundForUnknownParticipant()
            throws Exception {

        mockMvc.perform(
                        get(
                                "/api/v1/transactions/participants/{participantId}",
                                UUID.randomUUID()
                        )
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldGetParticipantsByTransactionId()
            throws Exception {

        JsonNode first =
                createParticipant(
                        "SENDER",
                        null
                );

        JsonNode second =
                createParticipant(
                        "BENEFICIARY",
                        null
                );

        mockMvc.perform(
                        get(
                                "/api/v1/transactions/{transactionId}/participants",
                                transactionId
                        )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$")
                                .isArray()
                )
                .andExpect(
                        jsonPath("$[*].participantId")
                                .value(
                                        hasItem(
                                                first.get(
                                                        "participantId"
                                                ).asText()
                                        )
                                )
                )
                .andExpect(
                        jsonPath("$[*].participantId")
                                .value(
                                        hasItem(
                                                second.get(
                                                        "participantId"
                                                ).asText()
                                        )
                                )
                );
    }

    @Test
    void shouldGetParticipantsByCustomerId()
            throws Exception {

        Customer secondCustomer =
                createCustomer(
                        "TP-CTRL-B-"
                );

        JsonNode first =
                createParticipant(
                        "SENDER",
                        customerId
                );

        JsonNode second =
                createParticipant(
                        "BENEFICIARY",
                        customerId
                );

        createParticipant(
                "BENEFICIARY",
                secondCustomer.getCustomerId()
        );

        mockMvc.perform(
                        get(
                                "/api/v1/transactions/participants/customer/{customerId}",
                                customerId
                        )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$")
                                .isArray()
                )
                .andExpect(
                        jsonPath("$[*].participantId")
                                .value(
                                        hasItem(
                                                first.get(
                                                        "participantId"
                                                ).asText()
                                        )
                                )
                )
                .andExpect(
                        jsonPath("$[*].participantId")
                                .value(
                                        hasItem(
                                                second.get(
                                                        "participantId"
                                                ).asText()
                                        )
                                )
                )
                .andExpect(
                        jsonPath("$[*].customerId")
                                .value(
                                        hasItem(
                                                customerId.toString()
                                        )
                                )
                );
    }

    private JsonNode createParticipant(
            String participantType,
            UUID participantCustomerId)
            throws Exception {

        TransactionParticipantRequest request =
                buildRequest(
                        participantType
                );

        request.setCustomerId(
                participantCustomerId
        );

        MvcResult result =
                mockMvc.perform(
                                post(
                                        "/api/v1/transactions/{transactionId}/participants",
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

    private TransactionParticipantRequest buildRequest(
            String participantType) {

        TransactionParticipantRequest request =
                new TransactionParticipantRequest();

        request.setParticipantType(
                participantType
        );

        return request;
    }

    private Customer createCustomer(
            String prefix) {

        LocalDateTime now =
                LocalDateTime.now();

        Customer customer =
                new Customer();

        customer.setCustomerNumber(
                prefix + UUID.randomUUID()
        );

        customer.setCustomerType(
                "INDIVIDUAL"
        );

        customer.setFirstName(
                "Participant"
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
}