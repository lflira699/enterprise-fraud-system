package com.efs.modules.transaction.controller;

import com.efs.modules.customer.entity.Customer;
import com.efs.modules.customer.repository.CustomerRepository;
import com.efs.modules.transaction.dto.TransactionChannelRequest;
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
class TransactionChannelControllerIntegrationTest {

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
                "TC-CTRL-" + UUID.randomUUID()
        );

        customer.setCustomerType(
                "INDIVIDUAL"
        );

        customer.setFirstName(
                "Transaction"
        );

        customer.setLastName(
                "Channel"
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
                "TC-CTRL-TXN-" + UUID.randomUUID()
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
                new BigDecimal("500.00")
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
    void shouldCreateTransactionChannel()
            throws Exception {

        TransactionChannelRequest request =
                buildRequest(
                        "MOBILE"
                );

        request.setApplicationName(
                "EFS Mobile"
        );

        request.setApplicationVersion(
                "2.5.1"
        );

        request.setSdkVersion(
                "5.4.0"
        );

        request.setApiVersion(
                "v1"
        );

        request.setAuthenticationMethod(
                "MFA"
        );

        request.setSessionDuration(
                1800
        );

        mockMvc.perform(
                        post(
                                "/api/v1/transactions/{transactionId}/channels",
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
                        jsonPath("$.channelTransactionId")
                                .exists()
                )
                .andExpect(
                        jsonPath("$.transactionId")
                                .value(
                                        transactionId.toString()
                                )
                )
                .andExpect(
                        jsonPath("$.channelType")
                                .value("MOBILE")
                )
                .andExpect(
                        jsonPath("$.applicationName")
                                .value("EFS Mobile")
                )
                .andExpect(
                        jsonPath("$.applicationVersion")
                                .value("2.5.1")
                )
                .andExpect(
                        jsonPath("$.sdkVersion")
                                .value("5.4.0")
                )
                .andExpect(
                        jsonPath("$.apiVersion")
                                .value("v1")
                )
                .andExpect(
                        jsonPath("$.authenticationMethod")
                                .value("MFA")
                )
                .andExpect(
                        jsonPath("$.sessionDuration")
                                .value(1800)
                )
                .andExpect(
                        jsonPath("$.createdAt")
                                .exists()
                );
    }

    @Test
    void shouldRejectBlankChannelType()
            throws Exception {

        TransactionChannelRequest request =
                buildRequest(
                        " "
                );

        mockMvc.perform(
                        post(
                                "/api/v1/transactions/{transactionId}/channels",
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
    void shouldReturnNotFoundWhenCreatingChannelForUnknownTransaction()
            throws Exception {

        mockMvc.perform(
                        post(
                                "/api/v1/transactions/{transactionId}/channels",
                                UUID.randomUUID()
                        )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        objectMapper.writeValueAsString(
                                                buildRequest("WEB")
                                        )
                                )
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldGetChannelById()
            throws Exception {

        JsonNode created =
                createChannel(
                        "MOBILE",
                        null
                );

        UUID channelTransactionId =
                UUID.fromString(
                        created.get(
                                "channelTransactionId"
                        ).asText()
                );

        mockMvc.perform(
                        get(
                                "/api/v1/transactions/channels/{channelTransactionId}",
                                channelTransactionId
                        )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.channelTransactionId")
                                .value(
                                        channelTransactionId.toString()
                                )
                )
                .andExpect(
                        jsonPath("$.transactionId")
                                .value(
                                        transactionId.toString()
                                )
                )
                .andExpect(
                        jsonPath("$.channelType")
                                .value("MOBILE")
                );
    }

    @Test
    void shouldReturnNotFoundForUnknownChannel()
            throws Exception {

        mockMvc.perform(
                        get(
                                "/api/v1/transactions/channels/{channelTransactionId}",
                                UUID.randomUUID()
                        )
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldGetChannelsByTransactionId()
            throws Exception {

        JsonNode first =
                createChannel(
                        "WEB",
                        null
                );

        JsonNode second =
                createChannel(
                        "MOBILE",
                        null
                );

        mockMvc.perform(
                        get(
                                "/api/v1/transactions/{transactionId}/channels",
                                transactionId
                        )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$")
                                .isArray()
                )
                .andExpect(
                        jsonPath("$[*].channelTransactionId")
                                .value(
                                        hasItem(
                                                first.get(
                                                        "channelTransactionId"
                                                ).asText()
                                        )
                                )
                )
                .andExpect(
                        jsonPath("$[*].channelTransactionId")
                                .value(
                                        hasItem(
                                                second.get(
                                                        "channelTransactionId"
                                                ).asText()
                                        )
                                )
                );
    }

    @Test
    void shouldGetChannelsByType()
            throws Exception {

        String targetType =
                "TYPE_" +
                        UUID.randomUUID()
                                .toString()
                                .substring(0, 8);

        JsonNode expected =
                createChannel(
                        targetType,
                        null
                );

        createChannel(
                "OTHER_" +
                        UUID.randomUUID()
                                .toString()
                                .substring(0, 8),
                null
        );

        mockMvc.perform(
                        get(
                                "/api/v1/transactions/channels/type/{channelType}",
                                targetType
                        )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$")
                                .isArray()
                )
                .andExpect(
                        jsonPath("$[*].channelTransactionId")
                                .value(
                                        hasItem(
                                                expected.get(
                                                        "channelTransactionId"
                                                ).asText()
                                        )
                                )
                )
                .andExpect(
                        jsonPath("$[*].channelType")
                                .value(
                                        hasItem(targetType)
                                )
                );
    }

    @Test
    void shouldGetChannelsByApplicationName()
            throws Exception {

        String applicationName =
                "APP-" +
                        UUID.randomUUID()
                                .toString()
                                .substring(0, 8);

        JsonNode expected =
                createChannel(
                        "WEB",
                        applicationName
                );

        createChannel(
                "MOBILE",
                "OTHER-" +
                        UUID.randomUUID()
                                .toString()
                                .substring(0, 8)
        );

        mockMvc.perform(
                        get(
                                "/api/v1/transactions/channels/application/{applicationName}",
                                applicationName
                        )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$")
                                .isArray()
                )
                .andExpect(
                        jsonPath("$[*].channelTransactionId")
                                .value(
                                        hasItem(
                                                expected.get(
                                                        "channelTransactionId"
                                                ).asText()
                                        )
                                )
                )
                .andExpect(
                        jsonPath("$[*].applicationName")
                                .value(
                                        hasItem(applicationName)
                                )
                );
    }

    private JsonNode createChannel(
            String channelType,
            String applicationName)
            throws Exception {

        TransactionChannelRequest request =
                buildRequest(
                        channelType
                );

        request.setApplicationName(
                applicationName
        );

        MvcResult result =
                mockMvc.perform(
                                post(
                                        "/api/v1/transactions/{transactionId}/channels",
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

    private TransactionChannelRequest buildRequest(
            String channelType) {

        TransactionChannelRequest request =
                new TransactionChannelRequest();

        request.setChannelType(
                channelType
        );

        return request;
    }
}