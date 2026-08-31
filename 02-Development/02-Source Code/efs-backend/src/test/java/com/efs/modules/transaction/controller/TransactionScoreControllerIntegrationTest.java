package com.efs.modules.transaction.controller;

import com.efs.modules.customer.entity.Customer;
import com.efs.modules.customer.repository.CustomerRepository;
import com.efs.modules.transaction.dto.TransactionScoreRequest;
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
class TransactionScoreControllerIntegrationTest {

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
                "TS-CTRL-" + UUID.randomUUID()
        );

        customer.setCustomerType(
                "INDIVIDUAL"
        );

        customer.setFirstName(
                "Transaction"
        );

        customer.setLastName(
                "Score"
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
                "TS-CTRL-TXN-" + UUID.randomUUID()
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
    void shouldCreateTransactionScore()
            throws Exception {

        TransactionScoreRequest request =
                buildRequest(
                        "RULES",
                        new BigDecimal("25.00")
                );

        request.setScoreWeight(
                new BigDecimal("40.00")
        );

        request.setScoringModel(
                "EFS-RISK"
        );

        request.setModelVersion(
                "1.0"
        );

        mockMvc.perform(
                        post(
                                "/api/v1/transactions/{transactionId}/scores",
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
                        jsonPath("$.scoreId")
                                .exists()
                )
                .andExpect(
                        jsonPath("$.transactionId")
                                .value(
                                        transactionId.toString()
                                )
                )
                .andExpect(
                        jsonPath("$.scoreType")
                                .value("RULES")
                )
                .andExpect(
                        jsonPath("$.scoreValue")
                                .value(25.00)
                )
                .andExpect(
                        jsonPath("$.scoreWeight")
                                .value(40.00)
                )
                .andExpect(
                        jsonPath("$.scoringModel")
                                .value("EFS-RISK")
                )
                .andExpect(
                        jsonPath("$.modelVersion")
                                .value("1.0")
                )
                .andExpect(
                        jsonPath("$.calculatedAt")
                                .exists()
                );
    }

    @Test
    void shouldRejectBlankScoreType()
            throws Exception {

        TransactionScoreRequest request =
                buildRequest(
                        " ",
                        new BigDecimal("25.00")
                );

        mockMvc.perform(
                        post(
                                "/api/v1/transactions/{transactionId}/scores",
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
    void shouldRejectMissingScoreValue()
            throws Exception {

        TransactionScoreRequest request =
                new TransactionScoreRequest();

        request.setScoreType(
                "RULES"
        );

        mockMvc.perform(
                        post(
                                "/api/v1/transactions/{transactionId}/scores",
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
    void shouldReturnNotFoundWhenCreatingScoreForUnknownTransaction()
            throws Exception {

        mockMvc.perform(
                        post(
                                "/api/v1/transactions/{transactionId}/scores",
                                UUID.randomUUID()
                        )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        objectMapper.writeValueAsString(
                                                buildRequest(
                                                        "RULES",
                                                        new BigDecimal("25.00")
                                                )
                                        )
                                )
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldGetTransactionScoreById()
            throws Exception {

        JsonNode created =
                createScore(
                        "RULES",
                        new BigDecimal("30.00"),
                        "EFS-RISK"
                );

        UUID scoreId =
                UUID.fromString(
                        created.get(
                                "scoreId"
                        ).asText()
                );

        mockMvc.perform(
                        get(
                                "/api/v1/transactions/scores/{scoreId}",
                                scoreId
                        )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.scoreId")
                                .value(
                                        scoreId.toString()
                                )
                )
                .andExpect(
                        jsonPath("$.transactionId")
                                .value(
                                        transactionId.toString()
                                )
                )
                .andExpect(
                        jsonPath("$.scoreType")
                                .value("RULES")
                )
                .andExpect(
                        jsonPath("$.scoringModel")
                                .value("EFS-RISK")
                );
    }

    @Test
    void shouldReturnNotFoundForUnknownScore()
            throws Exception {

        mockMvc.perform(
                        get(
                                "/api/v1/transactions/scores/{scoreId}",
                                UUID.randomUUID()
                        )
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldGetScoresByTransactionId()
            throws Exception {

        JsonNode first =
                createScore(
                        "RULES",
                        new BigDecimal("20.00"),
                        "EFS-RISK"
                );

        JsonNode second =
                createScore(
                        "BEHAVIORAL",
                        new BigDecimal("30.00"),
                        "EFS-RISK"
                );

        mockMvc.perform(
                        get(
                                "/api/v1/transactions/{transactionId}/scores",
                                transactionId
                        )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$")
                                .isArray()
                )
                .andExpect(
                        jsonPath("$[*].scoreId")
                                .value(
                                        hasItem(
                                                first.get(
                                                        "scoreId"
                                                ).asText()
                                        )
                                )
                )
                .andExpect(
                        jsonPath("$[*].scoreId")
                                .value(
                                        hasItem(
                                                second.get(
                                                        "scoreId"
                                                ).asText()
                                        )
                                )
                );
    }

    @Test
    void shouldGetScoresByType()
            throws Exception {

        String scoreType =
                "TYPE_" +
                        UUID.randomUUID()
                                .toString()
                                .substring(0, 8);

        JsonNode first =
                createScore(
                        scoreType,
                        new BigDecimal("20.00"),
                        "MODEL_A"
                );

        JsonNode second =
                createScore(
                        scoreType,
                        new BigDecimal("30.00"),
                        "MODEL_B"
                );

        createScore(
                "OTHER_" +
                        UUID.randomUUID()
                                .toString()
                                .substring(0, 8),
                new BigDecimal("40.00"),
                "MODEL_C"
        );

        mockMvc.perform(
                        get(
                                "/api/v1/transactions/scores/type/{scoreType}",
                                scoreType
                        )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$")
                                .isArray()
                )
                .andExpect(
                        jsonPath("$[*].scoreId")
                                .value(
                                        hasItem(
                                                first.get(
                                                        "scoreId"
                                                ).asText()
                                        )
                                )
                )
                .andExpect(
                        jsonPath("$[*].scoreId")
                                .value(
                                        hasItem(
                                                second.get(
                                                        "scoreId"
                                                ).asText()
                                        )
                                )
                )
                .andExpect(
                        jsonPath("$[*].scoreType")
                                .value(
                                        hasItem(scoreType)
                                )
                );
    }

    @Test
    void shouldGetScoresByScoringModel()
            throws Exception {

        String scoringModel =
                "MODEL_" +
                        UUID.randomUUID()
                                .toString()
                                .substring(0, 8);

        JsonNode first =
                createScore(
                        "RULES",
                        new BigDecimal("25.00"),
                        scoringModel
                );

        JsonNode second =
                createScore(
                        "BEHAVIORAL",
                        new BigDecimal("35.00"),
                        scoringModel
                );

        createScore(
                "NETWORK",
                new BigDecimal("45.00"),
                "OTHER_MODEL"
        );

        mockMvc.perform(
                        get(
                                "/api/v1/transactions/scores/model/{scoringModel}",
                                scoringModel
                        )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$")
                                .isArray()
                )
                .andExpect(
                        jsonPath("$[*].scoreId")
                                .value(
                                        hasItem(
                                                first.get(
                                                        "scoreId"
                                                ).asText()
                                        )
                                )
                )
                .andExpect(
                        jsonPath("$[*].scoreId")
                                .value(
                                        hasItem(
                                                second.get(
                                                        "scoreId"
                                                ).asText()
                                        )
                                )
                )
                .andExpect(
                        jsonPath("$[*].scoringModel")
                                .value(
                                        hasItem(scoringModel)
                                )
                );
    }

    private JsonNode createScore(
            String scoreType,
            BigDecimal scoreValue,
            String scoringModel)
            throws Exception {

        TransactionScoreRequest request =
                buildRequest(
                        scoreType,
                        scoreValue
                );

        request.setScoreWeight(
                new BigDecimal("40.00")
        );

        request.setScoringModel(
                scoringModel
        );

        request.setModelVersion(
                "1.0"
        );

        MvcResult result =
                mockMvc.perform(
                                post(
                                        "/api/v1/transactions/{transactionId}/scores",
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

    private TransactionScoreRequest buildRequest(
            String scoreType,
            BigDecimal scoreValue) {

        TransactionScoreRequest request =
                new TransactionScoreRequest();

        request.setScoreType(
                scoreType
        );

        request.setScoreValue(
                scoreValue
        );

        return request;
    }
}