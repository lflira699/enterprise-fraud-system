package com.efs.modules.transaction.controller;

import com.efs.modules.customer.entity.Customer;
import com.efs.modules.customer.repository.CustomerRepository;
import com.efs.modules.transaction.dto.TransactionRuleResultRequest;
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
class TransactionRuleResultControllerIntegrationTest {

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
                "TRR-CTRL-" + UUID.randomUUID()
        );

        customer.setCustomerType(
                "INDIVIDUAL"
        );

        customer.setFirstName(
                "Transaction"
        );

        customer.setLastName(
                "RuleResult"
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
                "TRR-CTRL-TXN-" + UUID.randomUUID()
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
                new BigDecimal("250.00")
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
    }

    @Test
    void shouldCreateTransactionRuleResult()
            throws Exception {

        UUID ruleId =
                UUID.randomUUID();

        TransactionRuleResultRequest request =
                buildRequest(
                        ruleId,
                        "MATCH",
                        (short) 1,
                        new BigDecimal("25.00")
                );

        request.setExecutionTimeMs(
                125
        );

        request.setRecommendedAction(
                "REVIEW"
        );

        request.setExplanation(
                "Controller integration test"
        );

        mockMvc.perform(
                        post(
                                "/api/v1/transactions/{transactionId}/rule-results",
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
                        jsonPath("$.ruleResultId")
                                .exists()
                )
                .andExpect(
                        jsonPath("$.transactionId")
                                .value(
                                        transactionId.toString()
                                )
                )
                .andExpect(
                        jsonPath("$.ruleId")
                                .value(
                                        ruleId.toString()
                                )
                )
                .andExpect(
                        jsonPath("$.ruleVersion")
                                .value(1)
                )
                .andExpect(
                        jsonPath("$.executionOrder")
                                .value(1)
                )
                .andExpect(
                        jsonPath("$.executionTimeMs")
                                .value(125)
                )
                .andExpect(
                        jsonPath("$.evaluationResult")
                                .value("MATCH")
                )
                .andExpect(
                        jsonPath("$.recommendedAction")
                                .value("REVIEW")
                )
                .andExpect(
                        jsonPath("$.explanation")
                                .value(
                                        "Controller integration test"
                                )
                )
                .andExpect(
                        jsonPath("$.executedAt")
                                .exists()
                );
    }

    @Test
    void shouldRejectInvalidRuleResultRequest()
            throws Exception {

        TransactionRuleResultRequest request =
                buildRequest(
                        UUID.randomUUID(),
                        " ",
                        (short) 1,
                        new BigDecimal("10.00")
                );

        mockMvc.perform(
                        post(
                                "/api/v1/transactions/{transactionId}/rule-results",
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
    void shouldReturnNotFoundWhenCreatingRuleResultForUnknownTransaction()
            throws Exception {

        mockMvc.perform(
                        post(
                                "/api/v1/transactions/{transactionId}/rule-results",
                                UUID.randomUUID()
                        )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        objectMapper.writeValueAsString(
                                                buildRequest(
                                                        UUID.randomUUID(),
                                                        "MATCH",
                                                        (short) 1,
                                                        new BigDecimal("25.00")
                                                )
                                        )
                                )
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldGetTransactionRuleResultById()
            throws Exception {

        UUID ruleId =
                UUID.randomUUID();

        JsonNode created =
                createRuleResult(
                        ruleId,
                        "MATCH",
                        (short) 1,
                        new BigDecimal("40.00")
                );

        UUID ruleResultId =
                UUID.fromString(
                        created.get(
                                "ruleResultId"
                        ).asText()
                );

        mockMvc.perform(
                        get(
                                "/api/v1/transactions/rule-results/{ruleResultId}",
                                ruleResultId
                        )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.ruleResultId")
                                .value(
                                        ruleResultId.toString()
                                )
                )
                .andExpect(
                        jsonPath("$.transactionId")
                                .value(
                                        transactionId.toString()
                                )
                )
                .andExpect(
                        jsonPath("$.ruleId")
                                .value(
                                        ruleId.toString()
                                )
                )
                .andExpect(
                        jsonPath("$.evaluationResult")
                                .value("MATCH")
                );
    }

    @Test
    void shouldReturnNotFoundForUnknownRuleResult()
            throws Exception {

        mockMvc.perform(
                        get(
                                "/api/v1/transactions/rule-results/{ruleResultId}",
                                UUID.randomUUID()
                        )
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldGetRuleResultsByTransactionId()
            throws Exception {

        JsonNode first =
                createRuleResult(
                        UUID.randomUUID(),
                        "MATCH",
                        (short) 1,
                        new BigDecimal("30.00")
                );

        JsonNode second =
                createRuleResult(
                        UUID.randomUUID(),
                        "NO_MATCH",
                        (short) 2,
                        BigDecimal.ZERO
                );

        mockMvc.perform(
                        get(
                                "/api/v1/transactions/{transactionId}/rule-results",
                                transactionId
                        )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$")
                                .isArray()
                )
                .andExpect(
                        jsonPath("$[*].ruleResultId")
                                .value(
                                        hasItem(
                                                first.get(
                                                        "ruleResultId"
                                                ).asText()
                                        )
                                )
                )
                .andExpect(
                        jsonPath("$[*].ruleResultId")
                                .value(
                                        hasItem(
                                                second.get(
                                                        "ruleResultId"
                                                ).asText()
                                        )
                                )
                );
    }

    @Test
    void shouldGetRuleResultsByRuleId()
            throws Exception {

        UUID ruleId =
                UUID.randomUUID();

        JsonNode first =
                createRuleResult(
                        ruleId,
                        "MATCH",
                        (short) 1,
                        new BigDecimal("20.00")
                );

        JsonNode second =
                createRuleResult(
                        ruleId,
                        "NO_MATCH",
                        (short) 2,
                        BigDecimal.ZERO
                );

        createRuleResult(
                UUID.randomUUID(),
                "MATCH",
                (short) 3,
                new BigDecimal("15.00")
        );

        mockMvc.perform(
                        get(
                                "/api/v1/transactions/rule-results/rule/{ruleId}",
                                ruleId
                        )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$")
                                .isArray()
                )
                .andExpect(
                        jsonPath("$[*].ruleResultId")
                                .value(
                                        hasItem(
                                                first.get(
                                                        "ruleResultId"
                                                ).asText()
                                        )
                                )
                )
                .andExpect(
                        jsonPath("$[*].ruleResultId")
                                .value(
                                        hasItem(
                                                second.get(
                                                        "ruleResultId"
                                                ).asText()
                                        )
                                )
                )
                .andExpect(
                        jsonPath("$[*].ruleId")
                                .value(
                                        hasItem(
                                                ruleId.toString()
                                        )
                                )
                );
    }

    @Test
    void shouldGetRuleResultsByEvaluationResult()
            throws Exception {

        String evaluationResult =
                "MATCH_" +
                        UUID.randomUUID()
                                .toString()
                                .substring(0, 8);

        JsonNode first =
                createRuleResult(
                        UUID.randomUUID(),
                        evaluationResult,
                        (short) 1,
                        new BigDecimal("50.00")
                );

        JsonNode second =
                createRuleResult(
                        UUID.randomUUID(),
                        evaluationResult,
                        (short) 2,
                        new BigDecimal("25.00")
                );

        createRuleResult(
                UUID.randomUUID(),
                "NO_MATCH",
                (short) 3,
                BigDecimal.ZERO
        );

        mockMvc.perform(
                        get(
                                "/api/v1/transactions/rule-results/result/{evaluationResult}",
                                evaluationResult
                        )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$")
                                .isArray()
                )
                .andExpect(
                        jsonPath("$[*].ruleResultId")
                                .value(
                                        hasItem(
                                                first.get(
                                                        "ruleResultId"
                                                ).asText()
                                        )
                                )
                )
                .andExpect(
                        jsonPath("$[*].ruleResultId")
                                .value(
                                        hasItem(
                                                second.get(
                                                        "ruleResultId"
                                                ).asText()
                                        )
                                )
                )
                .andExpect(
                        jsonPath("$[*].evaluationResult")
                                .value(
                                        hasItem(
                                                evaluationResult
                                        )
                                )
                );
    }

    private JsonNode createRuleResult(
            UUID ruleId,
            String evaluationResult,
            Short executionOrder,
            BigDecimal riskPoints)
            throws Exception {

        TransactionRuleResultRequest request =
                buildRequest(
                        ruleId,
                        evaluationResult,
                        executionOrder,
                        riskPoints
                );

        MvcResult result =
                mockMvc.perform(
                                post(
                                        "/api/v1/transactions/{transactionId}/rule-results",
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

    private TransactionRuleResultRequest buildRequest(
            UUID ruleId,
            String evaluationResult,
            Short executionOrder,
            BigDecimal riskPoints) {

        TransactionRuleResultRequest request =
                new TransactionRuleResultRequest();

        request.setRuleId(
                ruleId
        );

        request.setRuleVersion(
                1
        );

        request.setExecutionOrder(
                executionOrder
        );

        request.setEvaluationResult(
                evaluationResult
        );

        request.setRiskPoints(
                riskPoints
        );

        return request;
    }
}