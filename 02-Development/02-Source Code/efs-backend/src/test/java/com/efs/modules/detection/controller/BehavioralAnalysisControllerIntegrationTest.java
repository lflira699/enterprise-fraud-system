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
import java.util.UUID;

import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class BehavioralAnalysisControllerIntegrationTest {

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
                "BA-CTRL-" + UUID.randomUUID()
        );

        customer.setCustomerType(
                "INDIVIDUAL"
        );

        customer.setFirstName(
                "Behavioral"
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
                "EFS-BA-CTRL-" + UUID.randomUUID()
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
                new BigDecimal("750.00")
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
                "BA-CTRL-CORR-" + UUID.randomUUID()
        );

        correlation.setCorrelationType(
                "BEHAVIORAL"
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

        Correlation savedCorrelation =
                correlationRepository.saveAndFlush(
                        correlation
                );

        correlationId =
                savedCorrelation.getCorrelationId();
    }

    @Test
    void shouldCreateBehavioralAnalysisWithFullPayload()
            throws Exception {

        String requestBody =
                """
                {
                  "customerId": "%s",
                  "transactionId": "%s",
                  "correlationId": "%s",
                  "analysisStatus": "COMPLETED",
                  "baselineWindowDays": 30,
                  "observedWindowStart": "2026-08-30T11:00:00",
                  "observedWindowEnd": "2026-08-30T12:00:00",
                  "behavioralIndicators": {
                    "velocityRisk": "HIGH",
                    "channelChange": true
                  },
                  "analysisContext": {
                    "source": "CONTROLLER_TEST",
                    "channel": "WEB"
                  }
                }
                """.formatted(
                        customerId,
                        transactionId,
                        correlationId
                );

        mockMvc.perform(
                        post(
                                "/api/v1/detection/behavioral-analyses"
                        )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(requestBody)
                )
                .andExpect(status().isCreated())
                .andExpect(
                        jsonPath("$.behavioralAnalysisId")
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
                        jsonPath("$.analysisStatus")
                                .value("COMPLETED")
                )
                .andExpect(
                        jsonPath("$.baselineWindowDays")
                                .value(30)
                )
                .andExpect(
                        jsonPath("$.observedWindowStart")
                                .value("2026-08-30T11:00:00")
                )
                .andExpect(
                        jsonPath("$.observedWindowEnd")
                                .value("2026-08-30T12:00:00")
                )
                .andExpect(
                        jsonPath(
                                "$.behavioralIndicators.velocityRisk"
                        )
                                .value("HIGH")
                )
                .andExpect(
                        jsonPath(
                                "$.behavioralIndicators.channelChange"
                        )
                                .value(true)
                )
                .andExpect(
                        jsonPath(
                                "$.analysisContext.source"
                        )
                                .value("CONTROLLER_TEST")
                )
                .andExpect(
                        jsonPath(
                                "$.analysisContext.channel"
                        )
                                .value("WEB")
                )
                .andExpect(
                        jsonPath("$.analyzedAt")
                                .exists()
                )
                .andExpect(
                        jsonPath("$.createdAt")
                                .exists()
                );
    }

    @Test
    void shouldCreateBehavioralAnalysisWithoutTransactionAndCorrelation()
            throws Exception {

        String requestBody =
                """
                {
                  "customerId": "%s",
                  "analysisStatus": "PENDING"
                }
                """.formatted(
                        customerId
                );

        mockMvc.perform(
                        post(
                                "/api/v1/detection/behavioral-analyses"
                        )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(requestBody)
                )
                .andExpect(status().isCreated())
                .andExpect(
                        jsonPath("$.behavioralAnalysisId")
                                .exists()
                )
                .andExpect(
                        jsonPath("$.customerId")
                                .value(customerId.toString())
                )
                .andExpect(
                        jsonPath("$.transactionId")
                                .doesNotExist()
                )
                .andExpect(
                        jsonPath("$.correlationId")
                                .doesNotExist()
                )
                .andExpect(
                        jsonPath("$.analysisStatus")
                                .value("PENDING")
                )
                .andExpect(
                        jsonPath("$.analyzedAt")
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

        String requestBody =
                """
                {
                  "baselineWindowDays": 30
                }
                """;

        mockMvc.perform(
                        post(
                                "/api/v1/detection/behavioral-analyses"
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
                                "$.validationErrors.customerId"
                        )
                                .exists()
                )
                .andExpect(
                        jsonPath(
                                "$.validationErrors.analysisStatus"
                        )
                                .exists()
                );
    }

    @Test
    void shouldGetBehavioralAnalysisById()
            throws Exception {

        JsonNode created =
                createBehavioralAnalysis(
                        "COMPLETED",
                        transactionId,
                        correlationId
                );

        UUID behavioralAnalysisId =
                UUID.fromString(
                        created.get(
                                "behavioralAnalysisId"
                        ).asText()
                );

        mockMvc.perform(
                        get(
                                "/api/v1/detection/behavioral-analyses/{behavioralAnalysisId}",
                                behavioralAnalysisId
                        )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.behavioralAnalysisId")
                                .value(
                                        behavioralAnalysisId.toString()
                                )
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
                        jsonPath("$.analysisStatus")
                                .value("COMPLETED")
                );
    }

    @Test
    void shouldReturnNotFoundForUnknownBehavioralAnalysis()
            throws Exception {

        UUID unknownId =
                UUID.randomUUID();

        String correlationHeader =
                "BA-CTRL-" + UUID.randomUUID();

        mockMvc.perform(
                        get(
                                "/api/v1/detection/behavioral-analyses/{behavioralAnalysisId}",
                                unknownId
                        )
                                .header(
                                        "X-Correlation-ID",
                                        correlationHeader
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
                )
                .andExpect(
                        jsonPath("$.correlationId")
                                .value(correlationHeader)
                )
                .andExpect(
                        jsonPath("$.path")
                                .value(
                                        "/api/v1/detection/behavioral-analyses/"
                                                + unknownId
                                )
                );
    }

    @Test
    void shouldGetAnalysesByCustomer()
            throws Exception {

        JsonNode first =
                createBehavioralAnalysis(
                        "COMPLETED",
                        transactionId,
                        correlationId
                );

        JsonNode second =
                createBehavioralAnalysis(
                        "PENDING",
                        null,
                        null
                );

        mockMvc.perform(
                        get(
                                "/api/v1/detection/behavioral-analyses/customer/{customerId}",
                                customerId
                        )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$")
                                .isArray()
                )
                .andExpect(
                        jsonPath(
                                "$[*].behavioralAnalysisId"
                        )
                                .value(
                                        hasItem(
                                                first.get(
                                                        "behavioralAnalysisId"
                                                ).asText()
                                        )
                                )
                )
                .andExpect(
                        jsonPath(
                                "$[*].behavioralAnalysisId"
                        )
                                .value(
                                        hasItem(
                                                second.get(
                                                        "behavioralAnalysisId"
                                                ).asText()
                                        )
                                )
                );
    }

    @Test
    void shouldGetAnalysesByTransaction()
            throws Exception {

        JsonNode created =
                createBehavioralAnalysis(
                        "COMPLETED",
                        transactionId,
                        correlationId
                );

        mockMvc.perform(
                        get(
                                "/api/v1/detection/behavioral-analyses/transaction/{transactionId}",
                                transactionId
                        )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$")
                                .isArray()
                )
                .andExpect(
                        jsonPath(
                                "$[*].behavioralAnalysisId"
                        )
                                .value(
                                        hasItem(
                                                created.get(
                                                        "behavioralAnalysisId"
                                                ).asText()
                                        )
                                )
                );
    }

    @Test
    void shouldGetAnalysesByCorrelation()
            throws Exception {

        JsonNode created =
                createBehavioralAnalysis(
                        "COMPLETED",
                        transactionId,
                        correlationId
                );

        mockMvc.perform(
                        get(
                                "/api/v1/detection/behavioral-analyses/correlation/{correlationId}",
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
                                "$[*].behavioralAnalysisId"
                        )
                                .value(
                                        hasItem(
                                                created.get(
                                                        "behavioralAnalysisId"
                                                ).asText()
                                        )
                                )
                );
    }

    @Test
    void shouldGetAnalysesByStatus()
            throws Exception {

        String statusValue =
                "CTRL_" +
                        UUID.randomUUID()
                                .toString()
                                .substring(
                                        0,
                                        8
                                );

        JsonNode first =
                createBehavioralAnalysis(
                        statusValue,
                        transactionId,
                        correlationId
                );

        JsonNode second =
                createBehavioralAnalysis(
                        statusValue,
                        null,
                        null
                );

        mockMvc.perform(
                        get(
                                "/api/v1/detection/behavioral-analyses/status/{analysisStatus}",
                                statusValue
                        )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$")
                                .isArray()
                )
                .andExpect(
                        jsonPath(
                                "$[*].behavioralAnalysisId"
                        )
                                .value(
                                        hasItem(
                                                first.get(
                                                        "behavioralAnalysisId"
                                                ).asText()
                                        )
                                )
                )
                .andExpect(
                        jsonPath(
                                "$[*].behavioralAnalysisId"
                        )
                                .value(
                                        hasItem(
                                                second.get(
                                                        "behavioralAnalysisId"
                                                ).asText()
                                        )
                                )
                );
    }

    private JsonNode createBehavioralAnalysis(
            String analysisStatus,
            UUID targetTransactionId,
            UUID targetCorrelationId)
            throws Exception {

        StringBuilder requestBody =
                new StringBuilder();

        requestBody.append(
                """
                {
                  "customerId": "%s",
                  "analysisStatus": "%s"
                """.formatted(
                        customerId,
                        analysisStatus
                )
        );

        if (targetTransactionId != null) {

            requestBody.append(
                    """
                    ,
                      "transactionId": "%s"
                    """.formatted(
                            targetTransactionId
                    )
            );
        }

        if (targetCorrelationId != null) {

            requestBody.append(
                    """
                    ,
                      "correlationId": "%s"
                    """.formatted(
                            targetCorrelationId
                    )
            );
        }

        requestBody.append(
                """
                }
                """
        );

        MvcResult result =
                mockMvc.perform(
                                post(
                                        "/api/v1/detection/behavioral-analyses"
                                )
                                        .contentType(
                                                MediaType.APPLICATION_JSON
                                        )
                                        .content(
                                                requestBody.toString()
                                        )
                        )
                        .andExpect(status().isCreated())
                        .andReturn();

        return objectMapper.readTree(
                result.getResponse()
                        .getContentAsString()
        );
    }
}