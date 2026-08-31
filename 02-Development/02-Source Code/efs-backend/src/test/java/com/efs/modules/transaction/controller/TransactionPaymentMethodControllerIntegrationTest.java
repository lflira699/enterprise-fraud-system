package com.efs.modules.transaction.controller;

import com.efs.modules.customer.entity.Customer;
import com.efs.modules.customer.repository.CustomerRepository;
import com.efs.modules.transaction.dto.TransactionPaymentMethodRequest;
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
import java.time.LocalDate;
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
class TransactionPaymentMethodControllerIntegrationTest {

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
                "TPM-CTRL-" + UUID.randomUUID()
        );

        customer.setCustomerType(
                "INDIVIDUAL"
        );

        customer.setFirstName(
                "Transaction"
        );

        customer.setLastName(
                "PaymentMethod"
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
                "TPM-CTRL-TXN-" + UUID.randomUUID()
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
                new BigDecimal("1200.00")
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
    void shouldCreateTransactionPaymentMethod()
            throws Exception {

        LocalDate expirationDate =
                LocalDate.of(
                        2030,
                        12,
                        31
                );

        String tokenReference =
                "TOKEN-" + UUID.randomUUID();

        TransactionPaymentMethodRequest request =
                buildRequest(
                        "CARD"
                );

        request.setNetwork(
                "VISA"
        );

        request.setIssuer(
                "Controller Test Bank"
        );

        request.setMaskedIdentifier(
                "**** **** **** 1234"
        );

        request.setTokenReference(
                tokenReference
        );

        request.setExpirationDate(
                expirationDate
        );

        mockMvc.perform(
                        post(
                                "/api/v1/transactions/{transactionId}/payment-methods",
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
                        jsonPath("$.paymentMethodId")
                                .exists()
                )
                .andExpect(
                        jsonPath("$.transactionId")
                                .value(
                                        transactionId.toString()
                                )
                )
                .andExpect(
                        jsonPath("$.paymentType")
                                .value("CARD")
                )
                .andExpect(
                        jsonPath("$.network")
                                .value("VISA")
                )
                .andExpect(
                        jsonPath("$.issuer")
                                .value("Controller Test Bank")
                )
                .andExpect(
                        jsonPath("$.maskedIdentifier")
                                .value("**** **** **** 1234")
                )
                .andExpect(
                        jsonPath("$.tokenReference")
                                .value(tokenReference)
                )
                .andExpect(
                        jsonPath("$.expirationDate")
                                .value("2030-12-31")
                )
                .andExpect(
                        jsonPath("$.createdAt")
                                .exists()
                );
    }

    @Test
    void shouldRejectBlankPaymentType()
            throws Exception {

        TransactionPaymentMethodRequest request =
                buildRequest(
                        " "
                );

        mockMvc.perform(
                        post(
                                "/api/v1/transactions/{transactionId}/payment-methods",
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
    void shouldReturnNotFoundWhenCreatingPaymentMethodForUnknownTransaction()
            throws Exception {

        mockMvc.perform(
                        post(
                                "/api/v1/transactions/{transactionId}/payment-methods",
                                UUID.randomUUID()
                        )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        objectMapper.writeValueAsString(
                                                buildRequest("CARD")
                                        )
                                )
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldGetTransactionPaymentMethodById()
            throws Exception {

        JsonNode created =
                createPaymentMethod(
                        "CARD",
                        "MASTERCARD"
                );

        UUID paymentMethodId =
                UUID.fromString(
                        created.get(
                                "paymentMethodId"
                        ).asText()
                );

        mockMvc.perform(
                        get(
                                "/api/v1/transactions/payment-methods/{paymentMethodId}",
                                paymentMethodId
                        )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.paymentMethodId")
                                .value(
                                        paymentMethodId.toString()
                                )
                )
                .andExpect(
                        jsonPath("$.transactionId")
                                .value(
                                        transactionId.toString()
                                )
                )
                .andExpect(
                        jsonPath("$.paymentType")
                                .value("CARD")
                )
                .andExpect(
                        jsonPath("$.network")
                                .value("MASTERCARD")
                );
    }

    @Test
    void shouldReturnNotFoundForUnknownPaymentMethod()
            throws Exception {

        mockMvc.perform(
                        get(
                                "/api/v1/transactions/payment-methods/{paymentMethodId}",
                                UUID.randomUUID()
                        )
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldGetPaymentMethodsByTransactionId()
            throws Exception {

        JsonNode first =
                createPaymentMethod(
                        "CARD",
                        "VISA"
                );

        JsonNode second =
                createPaymentMethod(
                        "BANK_ACCOUNT",
                        null
                );

        mockMvc.perform(
                        get(
                                "/api/v1/transactions/{transactionId}/payment-methods",
                                transactionId
                        )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$")
                                .isArray()
                )
                .andExpect(
                        jsonPath("$[*].paymentMethodId")
                                .value(
                                        hasItem(
                                                first.get(
                                                        "paymentMethodId"
                                                ).asText()
                                        )
                                )
                )
                .andExpect(
                        jsonPath("$[*].paymentMethodId")
                                .value(
                                        hasItem(
                                                second.get(
                                                        "paymentMethodId"
                                                ).asText()
                                        )
                                )
                );
    }

    private JsonNode createPaymentMethod(
            String paymentType,
            String network)
            throws Exception {

        TransactionPaymentMethodRequest request =
                buildRequest(
                        paymentType
                );

        request.setNetwork(
                network
        );

        MvcResult result =
                mockMvc.perform(
                                post(
                                        "/api/v1/transactions/{transactionId}/payment-methods",
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

    private TransactionPaymentMethodRequest buildRequest(
            String paymentType) {

        TransactionPaymentMethodRequest request =
                new TransactionPaymentMethodRequest();

        request.setPaymentType(
                paymentType
        );

        return request;
    }
}