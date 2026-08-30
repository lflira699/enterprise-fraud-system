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
class RelationshipAnalysisControllerIntegrationTest {

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
                "RA-CTRL-" + UUID.randomUUID()
        );

        customer.setCustomerType(
                "INDIVIDUAL"
        );

        customer.setFirstName(
                "Relationship"
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
                "EFS-RA-CTRL-" + UUID.randomUUID()
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
                new BigDecimal("1100.00")
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
                "RA-CTRL-CORR-" + UUID.randomUUID()
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
    void shouldCreateRelationshipAnalysisWithFullPayload()
            throws Exception {

        String sourceEntityKey =
                "SRC-" + UUID.randomUUID();

        String targetEntityKey =
                "TGT-" + UUID.randomUUID();

        Map<String, Object> request =
                fullRequest(
                        "COMPLETED",
                        "CUSTOMER_TO_DEVICE",
                        sourceEntityKey,
                        targetEntityKey
                );

        mockMvc.perform(
                        post(
                                "/api/v1/detection/relationship-analyses"
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
                        jsonPath("$.relationshipAnalysisId")
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
                        jsonPath("$.relationshipType")
                                .value("CUSTOMER_TO_DEVICE")
                )
                .andExpect(
                        jsonPath("$.sourceEntityType")
                                .value("CUSTOMER")
                )
                .andExpect(
                        jsonPath("$.sourceEntityKey")
                                .value(sourceEntityKey)
                )
                .andExpect(
                        jsonPath("$.targetEntityType")
                                .value("DEVICE")
                )
                .andExpect(
                        jsonPath("$.targetEntityKey")
                                .value(targetEntityKey)
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
                        jsonPath(
                                "$.relationshipIndicators.sharedDevice"
                        )
                                .value(true)
                )
                .andExpect(
                        jsonPath(
                                "$.relationshipIndicators.linkedEntities"
                        )
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
    void shouldCreateRelationshipAnalysisWithoutOptionalReferences()
            throws Exception {

        Map<String, Object> request =
                requiredRequest(
                        "COMPLETED",
                        "STANDALONE_LINK",
                        "SRC-" + UUID.randomUUID(),
                        "TGT-" + UUID.randomUUID()
                );

        mockMvc.perform(
                        post(
                                "/api/v1/detection/relationship-analyses"
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
                        jsonPath("$.relationshipAnalysisId")
                                .exists()
                )
                .andExpect(
                        jsonPath("$.analysisStatus")
                                .value("COMPLETED")
                )
                .andExpect(
                        jsonPath("$.relationshipType")
                                .value("STANDALONE_LINK")
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
                                "/api/v1/detection/relationship-analyses"
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
                                "$.validationErrors.relationshipType"
                        )
                                .exists()
                )
                .andExpect(
                        jsonPath(
                                "$.validationErrors.sourceEntityType"
                        )
                                .exists()
                )
                .andExpect(
                        jsonPath(
                                "$.validationErrors.sourceEntityKey"
                        )
                                .exists()
                )
                .andExpect(
                        jsonPath(
                                "$.validationErrors.targetEntityType"
                        )
                                .exists()
                )
                .andExpect(
                        jsonPath(
                                "$.validationErrors.targetEntityKey"
                        )
                                .exists()
                );
    }

    @Test
    void shouldGetRelationshipAnalysisById()
            throws Exception {

        JsonNode created =
                createAnalysis(
                        "COMPLETED",
                        "CUSTOMER_TO_DEVICE",
                        "SRC-" + UUID.randomUUID(),
                        "TGT-" + UUID.randomUUID()
                );

        UUID relationshipAnalysisId =
                UUID.fromString(
                        created.get(
                                "relationshipAnalysisId"
                        ).asText()
                );

        mockMvc.perform(
                        get(
                                "/api/v1/detection/relationship-analyses/{relationshipAnalysisId}",
                                relationshipAnalysisId
                        )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.relationshipAnalysisId")
                                .value(
                                        relationshipAnalysisId.toString()
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
                        jsonPath("$.relationshipType")
                                .value("CUSTOMER_TO_DEVICE")
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
    void shouldReturnNotFoundForUnknownRelationshipAnalysis()
            throws Exception {

        mockMvc.perform(
                        get(
                                "/api/v1/detection/relationship-analyses/{relationshipAnalysisId}",
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
                        "CUSTOMER_LINK",
                        "SRC-" + UUID.randomUUID(),
                        "TGT-" + UUID.randomUUID()
                );

        JsonNode second =
                createAnalysis(
                        "PENDING",
                        "CUSTOMER_LINK",
                        "SRC-" + UUID.randomUUID(),
                        "TGT-" + UUID.randomUUID()
                );

        mockMvc.perform(
                        get(
                                "/api/v1/detection/relationship-analyses/customer/{customerId}",
                                customerId
                        )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$")
                                .isArray()
                )
                .andExpect(
                        jsonPath("$[*].relationshipAnalysisId")
                                .value(
                                        hasItem(
                                                first.get(
                                                        "relationshipAnalysisId"
                                                ).asText()
                                        )
                                )
                )
                .andExpect(
                        jsonPath("$[*].relationshipAnalysisId")
                                .value(
                                        hasItem(
                                                second.get(
                                                        "relationshipAnalysisId"
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
                        "TRANSACTION_LINK",
                        "SRC-" + UUID.randomUUID(),
                        "TGT-" + UUID.randomUUID()
                );

        mockMvc.perform(
                        get(
                                "/api/v1/detection/relationship-analyses/transaction/{transactionId}",
                                transactionId
                        )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$[*].relationshipAnalysisId")
                                .value(
                                        hasItem(
                                                created.get(
                                                        "relationshipAnalysisId"
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
                        "CORRELATION_LINK",
                        "SRC-" + UUID.randomUUID(),
                        "TGT-" + UUID.randomUUID()
                );

        mockMvc.perform(
                        get(
                                "/api/v1/detection/relationship-analyses/correlation/{correlationId}",
                                correlationId
                        )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$[*].relationshipAnalysisId")
                                .value(
                                        hasItem(
                                                created.get(
                                                        "relationshipAnalysisId"
                                                ).asText()
                                        )
                                )
                );
    }

    @Test
    void shouldGetAnalysesByType()
            throws Exception {

        String relationshipType =
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
                        relationshipType,
                        "SRC-" + UUID.randomUUID(),
                        "TGT-" + UUID.randomUUID()
                );

        JsonNode second =
                createAnalysis(
                        "PENDING",
                        relationshipType,
                        "SRC-" + UUID.randomUUID(),
                        "TGT-" + UUID.randomUUID()
                );

        mockMvc.perform(
                        get(
                                "/api/v1/detection/relationship-analyses/type/{relationshipType}",
                                relationshipType
                        )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$[*].relationshipAnalysisId")
                                .value(
                                        hasItem(
                                                first.get(
                                                        "relationshipAnalysisId"
                                                ).asText()
                                        )
                                )
                )
                .andExpect(
                        jsonPath("$[*].relationshipAnalysisId")
                                .value(
                                        hasItem(
                                                second.get(
                                                        "relationshipAnalysisId"
                                                ).asText()
                                        )
                                )
                )
                .andExpect(
                        jsonPath("$[*].relationshipType")
                                .value(
                                        hasItem(
                                                relationshipType
                                        )
                                )
                );
    }

    @Test
    void shouldGetAnalysesBySource()
            throws Exception {

        String sourceEntityKey =
                "SRC-" + UUID.randomUUID();

        JsonNode first =
                createAnalysis(
                        "COMPLETED",
                        "SOURCE_LINK",
                        sourceEntityKey,
                        "TGT-" + UUID.randomUUID()
                );

        JsonNode second =
                createAnalysis(
                        "PENDING",
                        "SOURCE_LINK",
                        sourceEntityKey,
                        "TGT-" + UUID.randomUUID()
                );

        mockMvc.perform(
                        get(
                                "/api/v1/detection/relationship-analyses/source/{sourceEntityKey}",
                                sourceEntityKey
                        )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$[*].relationshipAnalysisId")
                                .value(
                                        hasItem(
                                                first.get(
                                                        "relationshipAnalysisId"
                                                ).asText()
                                        )
                                )
                )
                .andExpect(
                        jsonPath("$[*].relationshipAnalysisId")
                                .value(
                                        hasItem(
                                                second.get(
                                                        "relationshipAnalysisId"
                                                ).asText()
                                        )
                                )
                )
                .andExpect(
                        jsonPath("$[*].sourceEntityKey")
                                .value(
                                        hasItem(
                                                sourceEntityKey
                                        )
                                )
                );
    }

    @Test
    void shouldGetAnalysesByTarget()
            throws Exception {

        String targetEntityKey =
                "TGT-" + UUID.randomUUID();

        JsonNode first =
                createAnalysis(
                        "COMPLETED",
                        "TARGET_LINK",
                        "SRC-" + UUID.randomUUID(),
                        targetEntityKey
                );

        JsonNode second =
                createAnalysis(
                        "PENDING",
                        "TARGET_LINK",
                        "SRC-" + UUID.randomUUID(),
                        targetEntityKey
                );

        mockMvc.perform(
                        get(
                                "/api/v1/detection/relationship-analyses/target/{targetEntityKey}",
                                targetEntityKey
                        )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$[*].relationshipAnalysisId")
                                .value(
                                        hasItem(
                                                first.get(
                                                        "relationshipAnalysisId"
                                                ).asText()
                                        )
                                )
                )
                .andExpect(
                        jsonPath("$[*].relationshipAnalysisId")
                                .value(
                                        hasItem(
                                                second.get(
                                                        "relationshipAnalysisId"
                                                ).asText()
                                        )
                                )
                )
                .andExpect(
                        jsonPath("$[*].targetEntityKey")
                                .value(
                                        hasItem(
                                                targetEntityKey
                                        )
                                )
                );
    }

    @Test
    void shouldGetAnalysesByStatus()
            throws Exception {

        String analysisStatus =
                "RA_" +
                        UUID.randomUUID()
                                .toString()
                                .substring(
                                        0,
                                        8
                                );

        JsonNode first =
                createAnalysis(
                        analysisStatus,
                        "STATUS_LINK",
                        "SRC-" + UUID.randomUUID(),
                        "TGT-" + UUID.randomUUID()
                );

        JsonNode second =
                createAnalysis(
                        analysisStatus,
                        "STATUS_LINK",
                        "SRC-" + UUID.randomUUID(),
                        "TGT-" + UUID.randomUUID()
                );

        mockMvc.perform(
                        get(
                                "/api/v1/detection/relationship-analyses/status/{analysisStatus}",
                                analysisStatus
                        )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$[*].relationshipAnalysisId")
                                .value(
                                        hasItem(
                                                first.get(
                                                        "relationshipAnalysisId"
                                                ).asText()
                                        )
                                )
                )
                .andExpect(
                        jsonPath("$[*].relationshipAnalysisId")
                                .value(
                                        hasItem(
                                                second.get(
                                                        "relationshipAnalysisId"
                                                ).asText()
                                        )
                                )
                )
                .andExpect(
                        jsonPath("$[*].analysisStatus")
                                .value(
                                        hasItem(
                                                analysisStatus
                                        )
                                )
                );
    }

    private JsonNode createAnalysis(
            String analysisStatus,
            String relationshipType,
            String sourceEntityKey,
            String targetEntityKey)
            throws Exception {

        Map<String, Object> request =
                fullRequest(
                        analysisStatus,
                        relationshipType,
                        sourceEntityKey,
                        targetEntityKey
                );

        MvcResult result =
                mockMvc.perform(
                                post(
                                        "/api/v1/detection/relationship-analyses"
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
            String relationshipType,
            String sourceEntityKey,
            String targetEntityKey) {

        Map<String, Object> request =
                requiredRequest(
                        analysisStatus,
                        relationshipType,
                        sourceEntityKey,
                        targetEntityKey
                );

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
                "relationshipIndicators",
                Map.of(
                        "sharedDevice", true,
                        "linkedEntities", 4
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

    private Map<String, Object> requiredRequest(
            String analysisStatus,
            String relationshipType,
            String sourceEntityKey,
            String targetEntityKey) {

        Map<String, Object> request =
                new LinkedHashMap<>();

        request.put(
                "analysisStatus",
                analysisStatus
        );

        request.put(
                "relationshipType",
                relationshipType
        );

        request.put(
                "sourceEntityType",
                "CUSTOMER"
        );

        request.put(
                "sourceEntityKey",
                sourceEntityKey
        );

        request.put(
                "targetEntityType",
                "DEVICE"
        );

        request.put(
                "targetEntityKey",
                targetEntityKey
        );

        return request;
    }
}