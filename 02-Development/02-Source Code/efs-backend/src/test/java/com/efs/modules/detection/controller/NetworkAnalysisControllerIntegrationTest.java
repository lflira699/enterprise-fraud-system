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
class NetworkAnalysisControllerIntegrationTest {

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
                "NA-CTRL-" + UUID.randomUUID()
        );

        customer.setCustomerType(
                "INDIVIDUAL"
        );

        customer.setFirstName(
                "Network"
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
                "EFS-NA-CTRL-" + UUID.randomUUID()
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
                "NA-CTRL-CORR-" + UUID.randomUUID()
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
    void shouldCreateNetworkAnalysisWithFullPayload()
            throws Exception {

        String networkKey =
                "NA-KEY-" + UUID.randomUUID();

        Map<String, Object> request =
                fullRequest(
                        "COMPLETED",
                        "CUSTOMER_NETWORK",
                        networkKey
                );

        mockMvc.perform(
                        post(
                                "/api/v1/detection/network-analyses"
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
                        jsonPath("$.networkAnalysisId")
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
                        jsonPath("$.networkType")
                                .value("CUSTOMER_NETWORK")
                )
                .andExpect(
                        jsonPath("$.networkKey")
                                .value(networkKey)
                )
                .andExpect(
                        jsonPath("$.entityCount")
                                .value(0)
                )
                .andExpect(
                        jsonPath("$.relationshipCount")
                                .value(0)
                )
                .andExpect(
                        jsonPath("$.networkIndicators.sharedDevice")
                                .value(true)
                )
                .andExpect(
                        jsonPath("$.networkIndicators.linkedAccounts")
                                .value(4)
                )
                .andExpect(
                        jsonPath("$.analysisContext.source")
                                .value("CONTROLLER_TEST")
                )
                .andExpect(
                        jsonPath("$.analysisContext.channel")
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
    void shouldCreateNetworkAnalysisWithOnlyRequiredFields()
            throws Exception {

        Map<String, Object> request =
                new LinkedHashMap<>();

        request.put(
                "analysisStatus",
                "COMPLETED"
        );

        request.put(
                "networkType",
                "STANDALONE_NETWORK"
        );

        mockMvc.perform(
                        post(
                                "/api/v1/detection/network-analyses"
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
                        jsonPath("$.networkAnalysisId")
                                .exists()
                )
                .andExpect(
                        jsonPath("$.analysisStatus")
                                .value("COMPLETED")
                )
                .andExpect(
                        jsonPath("$.networkType")
                                .value("STANDALONE_NETWORK")
                )
                .andExpect(
                        jsonPath("$.entityCount")
                                .value(0)
                )
                .andExpect(
                        jsonPath("$.relationshipCount")
                                .value(0)
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

        mockMvc.perform(
                        post(
                                "/api/v1/detection/network-analyses"
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
                                "$.validationErrors.analysisStatus"
                        )
                                .exists()
                )
                .andExpect(
                        jsonPath(
                                "$.validationErrors.networkType"
                        )
                                .exists()
                );
    }

    @Test
    void shouldGetNetworkAnalysisById()
            throws Exception {

        JsonNode created =
                createAnalysis(
                        "COMPLETED",
                        "CUSTOMER_NETWORK",
                        "NA-KEY-" + UUID.randomUUID()
                );

        UUID networkAnalysisId =
                UUID.fromString(
                        created.get(
                                "networkAnalysisId"
                        ).asText()
                );

        mockMvc.perform(
                        get(
                                "/api/v1/detection/network-analyses/{networkAnalysisId}",
                                networkAnalysisId
                        )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.networkAnalysisId")
                                .value(
                                        networkAnalysisId.toString()
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
                )
                .andExpect(
                        jsonPath("$.networkType")
                                .value("CUSTOMER_NETWORK")
                )
                .andExpect(
                        jsonPath("$.entityCount")
                                .value(0)
                )
                .andExpect(
                        jsonPath("$.relationshipCount")
                                .value(0)
                );
    }

    @Test
    void shouldReturnNotFoundForUnknownNetworkAnalysis()
            throws Exception {

        mockMvc.perform(
                        get(
                                "/api/v1/detection/network-analyses/{networkAnalysisId}",
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
    void shouldGetAnalysesByCustomer()
            throws Exception {

        JsonNode first =
                createAnalysis(
                        "COMPLETED",
                        "CUSTOMER_NETWORK",
                        "NA-KEY-" + UUID.randomUUID()
                );

        JsonNode second =
                createAnalysis(
                        "PENDING",
                        "CUSTOMER_NETWORK",
                        "NA-KEY-" + UUID.randomUUID()
                );

        mockMvc.perform(
                        get(
                                "/api/v1/detection/network-analyses/customer/{customerId}",
                                customerId
                        )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$")
                                .isArray()
                )
                .andExpect(
                        jsonPath("$[*].networkAnalysisId")
                                .value(
                                        hasItem(
                                                first.get(
                                                        "networkAnalysisId"
                                                ).asText()
                                        )
                                )
                )
                .andExpect(
                        jsonPath("$[*].networkAnalysisId")
                                .value(
                                        hasItem(
                                                second.get(
                                                        "networkAnalysisId"
                                                ).asText()
                                        )
                                )
                );
    }

    @Test
    void shouldGetAnalysesByTransaction()
            throws Exception {

        JsonNode created =
                createAnalysis(
                        "COMPLETED",
                        "TRANSACTION_NETWORK",
                        "NA-KEY-" + UUID.randomUUID()
                );

        mockMvc.perform(
                        get(
                                "/api/v1/detection/network-analyses/transaction/{transactionId}",
                                transactionId
                        )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$[*].networkAnalysisId")
                                .value(
                                        hasItem(
                                                created.get(
                                                        "networkAnalysisId"
                                                ).asText()
                                        )
                                )
                );
    }

    @Test
    void shouldGetAnalysesByCorrelation()
            throws Exception {

        JsonNode created =
                createAnalysis(
                        "COMPLETED",
                        "CORRELATION_NETWORK",
                        "NA-KEY-" + UUID.randomUUID()
                );

        mockMvc.perform(
                        get(
                                "/api/v1/detection/network-analyses/correlation/{correlationId}",
                                correlationId
                        )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$[*].networkAnalysisId")
                                .value(
                                        hasItem(
                                                created.get(
                                                        "networkAnalysisId"
                                                ).asText()
                                        )
                                )
                );
    }

    @Test
    void shouldGetAnalysesByType()
            throws Exception {

        String networkType =
                "TYPE_" +
                        UUID.randomUUID()
                                .toString()
                                .substring(
                                        0,
                                        8
                                );

        JsonNode first =
                createAnalysis(
                        "COMPLETED",
                        networkType,
                        "NA-KEY-" + UUID.randomUUID()
                );

        JsonNode second =
                createAnalysis(
                        "PENDING",
                        networkType,
                        "NA-KEY-" + UUID.randomUUID()
                );

        mockMvc.perform(
                        get(
                                "/api/v1/detection/network-analyses/type/{networkType}",
                                networkType
                        )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$[*].networkAnalysisId")
                                .value(
                                        hasItem(
                                                first.get(
                                                        "networkAnalysisId"
                                                ).asText()
                                        )
                                )
                )
                .andExpect(
                        jsonPath("$[*].networkAnalysisId")
                                .value(
                                        hasItem(
                                                second.get(
                                                        "networkAnalysisId"
                                                ).asText()
                                        )
                                )
                )
                .andExpect(
                        jsonPath("$[*].networkType")
                                .value(
                                        hasItem(networkType)
                                )
                );
    }

    @Test
    void shouldGetAnalysesByStatus()
            throws Exception {

        String analysisStatus =
                "NA_" +
                        UUID.randomUUID()
                                .toString()
                                .substring(
                                        0,
                                        8
                                );

        JsonNode first =
                createAnalysis(
                        analysisStatus,
                        "CUSTOMER_NETWORK",
                        "NA-KEY-" + UUID.randomUUID()
                );

        JsonNode second =
                createAnalysis(
                        analysisStatus,
                        "TRANSACTION_NETWORK",
                        "NA-KEY-" + UUID.randomUUID()
                );

        mockMvc.perform(
                        get(
                                "/api/v1/detection/network-analyses/status/{analysisStatus}",
                                analysisStatus
                        )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$[*].networkAnalysisId")
                                .value(
                                        hasItem(
                                                first.get(
                                                        "networkAnalysisId"
                                                ).asText()
                                        )
                                )
                )
                .andExpect(
                        jsonPath("$[*].networkAnalysisId")
                                .value(
                                        hasItem(
                                                second.get(
                                                        "networkAnalysisId"
                                                ).asText()
                                        )
                                )
                )
                .andExpect(
                        jsonPath("$[*].analysisStatus")
                                .value(
                                        hasItem(analysisStatus)
                                )
                );
    }

    @Test
    void shouldGetAnalysesByKey()
            throws Exception {

        String networkKey =
                "NA-KEY-" + UUID.randomUUID();

        JsonNode first =
                createAnalysis(
                        "COMPLETED",
                        "CUSTOMER_NETWORK",
                        networkKey
                );

        JsonNode second =
                createAnalysis(
                        "PENDING",
                        "TRANSACTION_NETWORK",
                        networkKey
                );

        mockMvc.perform(
                        get(
                                "/api/v1/detection/network-analyses/key/{networkKey}",
                                networkKey
                        )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$[*].networkAnalysisId")
                                .value(
                                        hasItem(
                                                first.get(
                                                        "networkAnalysisId"
                                                ).asText()
                                        )
                                )
                )
                .andExpect(
                        jsonPath("$[*].networkAnalysisId")
                                .value(
                                        hasItem(
                                                second.get(
                                                        "networkAnalysisId"
                                                ).asText()
                                        )
                                )
                )
                .andExpect(
                        jsonPath("$[*].networkKey")
                                .value(
                                        hasItem(networkKey)
                                )
                );
    }

    private JsonNode createAnalysis(
            String analysisStatus,
            String networkType,
            String networkKey)
            throws Exception {

        Map<String, Object> request =
                fullRequest(
                        analysisStatus,
                        networkType,
                        networkKey
                );

        MvcResult result =
                mockMvc.perform(
                                post(
                                        "/api/v1/detection/network-analyses"
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
            String analysisStatus,
            String networkType,
            String networkKey) {

        Map<String, Object> request =
                new LinkedHashMap<>();

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
                "analysisStatus",
                analysisStatus
        );

        request.put(
                "networkType",
                networkType
        );

        request.put(
                "networkKey",
                networkKey
        );

        request.put(
                "networkIndicators",
                Map.of(
                        "sharedDevice", true,
                        "linkedAccounts", 4
                )
        );

        request.put(
                "analysisContext",
                Map.of(
                        "source", "CONTROLLER_TEST",
                        "channel", "WEB"
                )
        );

        return request;
    }
}