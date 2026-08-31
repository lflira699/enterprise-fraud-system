package com.efs.modules.transaction.controller;

import com.efs.modules.customer.entity.Customer;
import com.efs.modules.customer.repository.CustomerRepository;
import com.efs.modules.transaction.dto.TransactionRequest;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class TransactionControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CustomerRepository customerRepository;

    private UUID customerId;
    private UUID createdBy;

    @BeforeEach
    void setUp() {

        LocalDateTime now =
                LocalDateTime.now();

        Customer customer =
                new Customer();

        customer.setCustomerNumber(
                "TX-CTRL-" + UUID.randomUUID()
        );

        customer.setCustomerType(
                "INDIVIDUAL"
        );

        customer.setFirstName(
                "Transaction"
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

        createdBy =
                UUID.randomUUID();
    }

    @Test
    void shouldCreateTransaction()
            throws Exception {

        String reference =
                newReference();

        TransactionRequest request =
                buildRequest(
                        reference
                );

        request.setExternalReference(
                "EXT-" + UUID.randomUUID()
        );

        request.setTransactionSubtype(
                "ONLINE"
        );

        request.setTransactionStatus(
                "AUTHORIZED"
        );

        request.setFinalDecision(
                "REVIEW"
        );

        request.setFraudScore(
                new BigDecimal("72.50")
        );

        mockMvc.perform(
                        post("/api/v1/transactions")
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
                        jsonPath("$.transactionId")
                                .exists()
                )
                .andExpect(
                        jsonPath("$.transactionReference")
                                .value(reference)
                )
                .andExpect(
                        jsonPath("$.customerId")
                                .value(customerId.toString())
                )
                .andExpect(
                        jsonPath("$.transactionType")
                                .value("PAYMENT")
                )
                .andExpect(
                        jsonPath("$.amount")
                                .value(1500.0)
                )
                .andExpect(
                        jsonPath("$.currencyCode")
                                .value("GTQ")
                )
                .andExpect(
                        jsonPath("$.transactionStatus")
                                .value("AUTHORIZED")
                )
                .andExpect(
                        jsonPath("$.finalDecision")
                                .value("REVIEW")
                )
                .andExpect(
                        jsonPath("$.fraudScore")
                                .value(72.5)
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
    void shouldApplyApprovedDefaultsOnCreate()
            throws Exception {

        JsonNode created =
                createTransaction(
                        newReference()
                );

        mockMvc.perform(
                        get(
                                "/api/v1/transactions/{transactionId}",
                                created.get("transactionId").asText()
                        )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.transactionDatetime")
                                .exists()
                )
                .andExpect(
                        jsonPath("$.transactionStatus")
                                .value("RECEIVED")
                )
                .andExpect(
                        jsonPath("$.finalDecision")
                                .value("PENDING")
                )
                .andExpect(
                        jsonPath("$.fraudScore")
                                .value(0)
                );
    }

    @Test
    void shouldRejectInvalidTransactionRequest()
            throws Exception {

        TransactionRequest request =
                buildRequest(
                        " "
                );

        request.setAmount(
                BigDecimal.ZERO
        );

        mockMvc.perform(
                        post("/api/v1/transactions")
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
    void shouldRejectDuplicateTransactionReference()
            throws Exception {

        String reference =
                newReference();

        createTransaction(
                reference
        );

        mockMvc.perform(
                        post("/api/v1/transactions")
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        objectMapper.writeValueAsString(
                                                buildRequest(reference)
                                        )
                                )
                )
                .andExpect(status().isConflict());
    }

    @Test
    void shouldGetTransactionById()
            throws Exception {

        JsonNode created =
                createTransaction(
                        newReference()
                );

        String transactionId =
                created.get(
                        "transactionId"
                ).asText();

        mockMvc.perform(
                        get(
                                "/api/v1/transactions/{transactionId}",
                                transactionId
                        )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.transactionId")
                                .value(transactionId)
                )
                .andExpect(
                        jsonPath("$.transactionReference")
                                .value(
                                        created.get(
                                                "transactionReference"
                                        ).asText()
                                )
                )
                .andExpect(
                        jsonPath("$.customerId")
                                .value(customerId.toString())
                );
    }

    @Test
    void shouldReturnNotFoundForUnknownTransaction()
            throws Exception {

        mockMvc.perform(
                        get(
                                "/api/v1/transactions/{transactionId}",
                                UUID.randomUUID()
                        )
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldGetTransactionByReference()
            throws Exception {

        String reference =
                newReference();

        JsonNode created =
                createTransaction(
                        reference
                );

        mockMvc.perform(
                        get(
                                "/api/v1/transactions/reference/{transactionReference}",
                                reference
                        )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.transactionId")
                                .value(
                                        created.get(
                                                "transactionId"
                                        ).asText()
                                )
                )
                .andExpect(
                        jsonPath("$.transactionReference")
                                .value(reference)
                );
    }

    @Test
    void shouldGetTransactionsByCustomerId()
            throws Exception {

        JsonNode first =
                createTransaction(
                        newReference()
                );

        JsonNode second =
                createTransaction(
                        newReference()
                );

        mockMvc.perform(
                        get(
                                "/api/v1/transactions/customer/{customerId}",
                                customerId
                        )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$")
                                .isArray()
                )
                .andExpect(
                        jsonPath("$[*].transactionId")
                                .value(
                                        hasItem(
                                                first.get(
                                                        "transactionId"
                                                ).asText()
                                        )
                                )
                )
                .andExpect(
                        jsonPath("$[*].transactionId")
                                .value(
                                        hasItem(
                                                second.get(
                                                        "transactionId"
                                                ).asText()
                                        )
                                )
                );
    }

    @Test
    void shouldUpdateTransaction()
            throws Exception {

        JsonNode created =
                createTransaction(
                        newReference()
                );

        String transactionId =
                created.get(
                        "transactionId"
                ).asText();

        String updatedReference =
                newReference();

        TransactionRequest request =
                buildRequest(
                        updatedReference
                );

        request.setTransactionType(
                "TRANSFER"
        );

        request.setTransactionSubtype(
                "INTERNAL"
        );

        request.setAmount(
                new BigDecimal("2750.00")
        );

        request.setCurrencyCode(
                "USD"
        );

        request.setTransactionStatus(
                "PROCESSED"
        );

        request.setFinalDecision(
                "APPROVED"
        );

        request.setFraudScore(
                new BigDecimal("12.25")
        );

        mockMvc.perform(
                        put(
                                "/api/v1/transactions/{transactionId}",
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
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.transactionId")
                                .value(transactionId)
                )
                .andExpect(
                        jsonPath("$.transactionReference")
                                .value(updatedReference)
                )
                .andExpect(
                        jsonPath("$.transactionType")
                                .value("TRANSFER")
                )
                .andExpect(
                        jsonPath("$.transactionSubtype")
                                .value("INTERNAL")
                )
                .andExpect(
                        jsonPath("$.amount")
                                .value(2750.0)
                )
                .andExpect(
                        jsonPath("$.currencyCode")
                                .value("USD")
                )
                .andExpect(
                        jsonPath("$.transactionStatus")
                                .value("PROCESSED")
                )
                .andExpect(
                        jsonPath("$.finalDecision")
                                .value("APPROVED")
                )
                .andExpect(
                        jsonPath("$.fraudScore")
                                .value(12.25)
                );
    }

    @Test
    void shouldSoftDeleteTransaction()
            throws Exception {

        JsonNode created =
                createTransaction(
                        newReference()
                );

        String transactionId =
                created.get(
                        "transactionId"
                ).asText();

        mockMvc.perform(
                        delete(
                                "/api/v1/transactions/{transactionId}",
                                transactionId
                        )
                )
                .andExpect(status().isNoContent());

        mockMvc.perform(
                        get(
                                "/api/v1/transactions/{transactionId}",
                                transactionId
                        )
                )
                .andExpect(status().isNotFound());
    }

    private JsonNode createTransaction(
            String reference)
            throws Exception {

        MvcResult result =
                mockMvc.perform(
                                post("/api/v1/transactions")
                                        .contentType(
                                                MediaType.APPLICATION_JSON
                                        )
                                        .content(
                                                objectMapper
                                                        .writeValueAsString(
                                                                buildRequest(
                                                                        reference
                                                                )
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

    private TransactionRequest buildRequest(
            String transactionReference) {

        TransactionRequest request =
                new TransactionRequest();

        request.setTransactionReference(
                transactionReference
        );

        request.setCustomerId(
                customerId
        );

        request.setOrganizationId(
                UUID.randomUUID()
        );

        request.setTransactionType(
                "PAYMENT"
        );

        request.setAmount(
                new BigDecimal("1500.00")
        );

        request.setCurrencyCode(
                "GTQ"
        );

        request.setCreatedBy(
                createdBy
        );

        return request;
    }

    private String newReference() {

        return "TX-CTRL-" + UUID.randomUUID();
    }
}