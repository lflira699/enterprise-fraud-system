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
class DeviceAnalysisControllerIntegrationTest {

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
                "DA-CTRL-" + UUID.randomUUID()
        );

        customer.setCustomerType(
                "INDIVIDUAL"
        );

        customer.setFirstName(
                "Device"
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
                "EFS-DA-CTRL-" + UUID.randomUUID()
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
                "DA-CTRL-CORR-" + UUID.randomUUID()
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
    void shouldCreateDeviceAnalysisWithFullPayload()
            throws Exception {

        String deviceId =
                "DEVICE-" + UUID.randomUUID();

        String fingerprint =
                "FP-" + UUID.randomUUID();

        Map<String, Object> request =
                fullRequest(
                        "COMPLETED",
                        deviceId,
                        fingerprint,
                        "10.107.10.25"
                );

        mockMvc.perform(
                        post(
                                "/api/v1/detection/device-analyses"
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
                        jsonPath("$.deviceAnalysisId")
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
                        jsonPath("$.deviceId")
                                .value(deviceId)
                )
                .andExpect(
                        jsonPath("$.deviceFingerprint")
                                .value(fingerprint)
                )
                .andExpect(
                        jsonPath("$.deviceType")
                                .value("MOBILE")
                )
                .andExpect(
                        jsonPath("$.operatingSystem")
                                .value("ANDROID")
                )
                .andExpect(
                        jsonPath("$.browser")
                                .value("CHROME")
                )
                .andExpect(
                        jsonPath("$.ipAddress")
                                .value("10.107.10.25")
                )
                .andExpect(
                        jsonPath("$.geolocationContext.country")
                                .value("GT")
                )
                .andExpect(
                        jsonPath("$.geolocationContext.city")
                                .value("Guatemala")
                )
                .andExpect(
                        jsonPath("$.deviceIndicators.knownDevice")
                                .value(true)
                )
                .andExpect(
                        jsonPath("$.deviceIndicators.historicalMatches")
                                .value(8)
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
    void shouldCreateDeviceAnalysisWithOnlyRequiredStatus()
            throws Exception {

        Map<String, Object> request =
                new LinkedHashMap<>();

        request.put(
                "analysisStatus",
                "COMPLETED"
        );

        mockMvc.perform(
                        post(
                                "/api/v1/detection/device-analyses"
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
                        jsonPath("$.deviceAnalysisId")
                                .exists()
                )
                .andExpect(
                        jsonPath("$.analysisStatus")
                                .value("COMPLETED")
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
    void shouldRejectCreateWithoutAnalysisStatus()
            throws Exception {

        mockMvc.perform(
                        post(
                                "/api/v1/detection/device-analyses"
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
                );
    }

    @Test
    void shouldGetDeviceAnalysisById()
            throws Exception {

        JsonNode created =
                createAnalysis(
                        "COMPLETED",
                        "DEVICE-" + UUID.randomUUID(),
                        "FP-" + UUID.randomUUID(),
                        "10.107.10.25"
                );

        UUID deviceAnalysisId =
                UUID.fromString(
                        created.get(
                                "deviceAnalysisId"
                        ).asText()
                );

        mockMvc.perform(
                        get(
                                "/api/v1/detection/device-analyses/{deviceAnalysisId}",
                                deviceAnalysisId
                        )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.deviceAnalysisId")
                                .value(
                                        deviceAnalysisId.toString()
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
                );
    }

    @Test
    void shouldReturnNotFoundForUnknownDeviceAnalysis()
            throws Exception {

        mockMvc.perform(
                        get(
                                "/api/v1/detection/device-analyses/{deviceAnalysisId}",
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
                        "DEVICE-" + UUID.randomUUID(),
                        "FP-" + UUID.randomUUID(),
                        "10.107.10.25"
                );

        JsonNode second =
                createAnalysis(
                        "PENDING",
                        "DEVICE-" + UUID.randomUUID(),
                        "FP-" + UUID.randomUUID(),
                        "10.107.10.26"
                );

        mockMvc.perform(
                        get(
                                "/api/v1/detection/device-analyses/customer/{customerId}",
                                customerId
                        )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$")
                                .isArray()
                )
                .andExpect(
                        jsonPath("$[*].deviceAnalysisId")
                                .value(
                                        hasItem(
                                                first.get(
                                                        "deviceAnalysisId"
                                                ).asText()
                                        )
                                )
                )
                .andExpect(
                        jsonPath("$[*].deviceAnalysisId")
                                .value(
                                        hasItem(
                                                second.get(
                                                        "deviceAnalysisId"
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
                        "DEVICE-" + UUID.randomUUID(),
                        "FP-" + UUID.randomUUID(),
                        "10.107.10.25"
                );

        mockMvc.perform(
                        get(
                                "/api/v1/detection/device-analyses/transaction/{transactionId}",
                                transactionId
                        )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$[*].deviceAnalysisId")
                                .value(
                                        hasItem(
                                                created.get(
                                                        "deviceAnalysisId"
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
                        "DEVICE-" + UUID.randomUUID(),
                        "FP-" + UUID.randomUUID(),
                        "10.107.10.25"
                );

        mockMvc.perform(
                        get(
                                "/api/v1/detection/device-analyses/correlation/{correlationId}",
                                correlationId
                        )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$[*].deviceAnalysisId")
                                .value(
                                        hasItem(
                                                created.get(
                                                        "deviceAnalysisId"
                                                ).asText()
                                        )
                                )
                );
    }

    @Test
    void shouldGetAnalysesByDeviceId()
            throws Exception {

        String deviceId =
                "DEVICE-" + UUID.randomUUID();

        JsonNode created =
                createAnalysis(
                        "COMPLETED",
                        deviceId,
                        "FP-" + UUID.randomUUID(),
                        "10.107.10.25"
                );

        mockMvc.perform(
                        get(
                                "/api/v1/detection/device-analyses/device/{deviceId}",
                                deviceId
                        )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$[*].deviceAnalysisId")
                                .value(
                                        hasItem(
                                                created.get(
                                                        "deviceAnalysisId"
                                                ).asText()
                                        )
                                )
                )
                .andExpect(
                        jsonPath("$[*].deviceId")
                                .value(
                                        hasItem(deviceId)
                                )
                );
    }

    @Test
    void shouldGetAnalysesByFingerprint()
            throws Exception {

        String fingerprint =
                "FP-" + UUID.randomUUID();

        JsonNode created =
                createAnalysis(
                        "COMPLETED",
                        "DEVICE-" + UUID.randomUUID(),
                        fingerprint,
                        "10.107.10.25"
                );

        mockMvc.perform(
                        get(
                                "/api/v1/detection/device-analyses/fingerprint/{deviceFingerprint}",
                                fingerprint
                        )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$[*].deviceAnalysisId")
                                .value(
                                        hasItem(
                                                created.get(
                                                        "deviceAnalysisId"
                                                ).asText()
                                        )
                                )
                )
                .andExpect(
                        jsonPath("$[*].deviceFingerprint")
                                .value(
                                        hasItem(fingerprint)
                                )
                );
    }

    @Test
    void shouldGetAnalysesByIpAddress()
            throws Exception {

        String ipAddress =
                "10.107.20.55";

        JsonNode created =
                createAnalysis(
                        "COMPLETED",
                        "DEVICE-" + UUID.randomUUID(),
                        "FP-" + UUID.randomUUID(),
                        ipAddress
                );

        mockMvc.perform(
                        get(
                                "/api/v1/detection/device-analyses/ip/{ipAddress}",
                                ipAddress
                        )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$[*].deviceAnalysisId")
                                .value(
                                        hasItem(
                                                created.get(
                                                        "deviceAnalysisId"
                                                ).asText()
                                        )
                                )
                )
                .andExpect(
                        jsonPath("$[*].ipAddress")
                                .value(
                                        hasItem(ipAddress)
                                )
                );
    }

    @Test
    void shouldGetAnalysesByStatus()
            throws Exception {

        String analysisStatus =
                "DA_" +
                        UUID.randomUUID()
                                .toString()
                                .substring(
                                        0,
                                        8
                                );

        JsonNode first =
                createAnalysis(
                        analysisStatus,
                        "DEVICE-" + UUID.randomUUID(),
                        "FP-" + UUID.randomUUID(),
                        "10.107.10.25"
                );

        JsonNode second =
                createAnalysis(
                        analysisStatus,
                        "DEVICE-" + UUID.randomUUID(),
                        "FP-" + UUID.randomUUID(),
                        "10.107.10.26"
                );

        mockMvc.perform(
                        get(
                                "/api/v1/detection/device-analyses/status/{analysisStatus}",
                                analysisStatus
                        )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$[*].deviceAnalysisId")
                                .value(
                                        hasItem(
                                                first.get(
                                                        "deviceAnalysisId"
                                                ).asText()
                                        )
                                )
                )
                .andExpect(
                        jsonPath("$[*].deviceAnalysisId")
                                .value(
                                        hasItem(
                                                second.get(
                                                        "deviceAnalysisId"
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

    private JsonNode createAnalysis(
            String analysisStatus,
            String deviceId,
            String deviceFingerprint,
            String ipAddress)
            throws Exception {

        Map<String, Object> request =
                fullRequest(
                        analysisStatus,
                        deviceId,
                        deviceFingerprint,
                        ipAddress
                );

        MvcResult result =
                mockMvc.perform(
                                post(
                                        "/api/v1/detection/device-analyses"
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
            String deviceId,
            String deviceFingerprint,
            String ipAddress) {

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
                "deviceId",
                deviceId
        );

        request.put(
                "deviceFingerprint",
                deviceFingerprint
        );

        request.put(
                "deviceType",
                "MOBILE"
        );

        request.put(
                "operatingSystem",
                "ANDROID"
        );

        request.put(
                "browser",
                "CHROME"
        );

        request.put(
                "ipAddress",
                ipAddress
        );

        request.put(
                "geolocationContext",
                Map.of(
                        "country", "GT",
                        "city", "Guatemala"
                )
        );

        request.put(
                "deviceIndicators",
                Map.of(
                        "knownDevice", true,
                        "historicalMatches", 8
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