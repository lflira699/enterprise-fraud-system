package com.efs.modules.transaction.controller;

import com.efs.modules.customer.entity.Customer;
import com.efs.modules.customer.repository.CustomerRepository;
import com.efs.modules.transaction.dto.TransactionDeviceRequest;
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
class TransactionDeviceControllerIntegrationTest {

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
                "TD-CTRL-" + UUID.randomUUID()
        );

        customer.setCustomerType(
                "INDIVIDUAL"
        );

        customer.setFirstName(
                "Transaction"
        );

        customer.setLastName(
                "Device"
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
                "TD-CTRL-TXN-" + UUID.randomUUID()
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
                new BigDecimal("750.00")
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
    void shouldCreateTransactionDevice()
            throws Exception {

        UUID deviceId =
                UUID.randomUUID();

        String fingerprint =
                "FP-" + UUID.randomUUID();

        TransactionDeviceRequest request =
                new TransactionDeviceRequest();

        request.setDeviceId(
                deviceId
        );

        request.setDeviceFingerprint(
                fingerprint
        );

        request.setDeviceType(
                "MOBILE"
        );

        request.setOperatingSystem(
                "Android"
        );

        request.setOsVersion(
                "16"
        );

        request.setBrowser(
                "Chrome"
        );

        request.setBrowserVersion(
                "152"
        );

        request.setScreenResolution(
                "1080x2400"
        );

        request.setLanguage(
                "es-GT"
        );

        request.setTimezone(
                "America/Guatemala"
        );

        request.setTrustScore(
                new BigDecimal("92.50")
        );

        mockMvc.perform(
                        post(
                                "/api/v1/transactions/{transactionId}/devices",
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
                        jsonPath("$.deviceTransactionId")
                                .exists()
                )
                .andExpect(
                        jsonPath("$.transactionId")
                                .value(
                                        transactionId.toString()
                                )
                )
                .andExpect(
                        jsonPath("$.deviceId")
                                .value(
                                        deviceId.toString()
                                )
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
                                .value("Android")
                )
                .andExpect(
                        jsonPath("$.osVersion")
                                .value("16")
                )
                .andExpect(
                        jsonPath("$.browser")
                                .value("Chrome")
                )
                .andExpect(
                        jsonPath("$.browserVersion")
                                .value("152")
                )
                .andExpect(
                        jsonPath("$.screenResolution")
                                .value("1080x2400")
                )
                .andExpect(
                        jsonPath("$.language")
                                .value("es-GT")
                )
                .andExpect(
                        jsonPath("$.timezone")
                                .value("America/Guatemala")
                )
                .andExpect(
                        jsonPath("$.trustScore")
                                .value(92.5)
                )
                .andExpect(
                        jsonPath("$.createdAt")
                                .exists()
                );
    }

    @Test
    void shouldRejectOversizedDeviceFingerprint()
            throws Exception {

        TransactionDeviceRequest request =
                new TransactionDeviceRequest();

        request.setDeviceFingerprint(
                "X".repeat(256)
        );

        mockMvc.perform(
                        post(
                                "/api/v1/transactions/{transactionId}/devices",
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
    void shouldReturnNotFoundWhenCreatingDeviceForUnknownTransaction()
            throws Exception {

        TransactionDeviceRequest request =
                new TransactionDeviceRequest();

        request.setDeviceType(
                "MOBILE"
        );

        mockMvc.perform(
                        post(
                                "/api/v1/transactions/{transactionId}/devices",
                                UUID.randomUUID()
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
    void shouldGetTransactionDeviceById()
            throws Exception {

        JsonNode created =
                createDevice(
                        "FP-" + UUID.randomUUID(),
                        "DESKTOP"
                );

        UUID deviceTransactionId =
                UUID.fromString(
                        created.get(
                                "deviceTransactionId"
                        ).asText()
                );

        mockMvc.perform(
                        get(
                                "/api/v1/transactions/devices/{deviceTransactionId}",
                                deviceTransactionId
                        )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.deviceTransactionId")
                                .value(
                                        deviceTransactionId.toString()
                                )
                )
                .andExpect(
                        jsonPath("$.transactionId")
                                .value(
                                        transactionId.toString()
                                )
                )
                .andExpect(
                        jsonPath("$.deviceType")
                                .value("DESKTOP")
                );
    }

    @Test
    void shouldReturnNotFoundForUnknownDevice()
            throws Exception {

        mockMvc.perform(
                        get(
                                "/api/v1/transactions/devices/{deviceTransactionId}",
                                UUID.randomUUID()
                        )
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldGetDevicesByTransactionId()
            throws Exception {

        JsonNode first =
                createDevice(
                        "FP-A-" + UUID.randomUUID(),
                        "MOBILE"
                );

        JsonNode second =
                createDevice(
                        "FP-B-" + UUID.randomUUID(),
                        "TABLET"
                );

        mockMvc.perform(
                        get(
                                "/api/v1/transactions/{transactionId}/devices",
                                transactionId
                        )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$")
                                .isArray()
                )
                .andExpect(
                        jsonPath("$[*].deviceTransactionId")
                                .value(
                                        hasItem(
                                                first.get(
                                                        "deviceTransactionId"
                                                ).asText()
                                        )
                                )
                )
                .andExpect(
                        jsonPath("$[*].deviceTransactionId")
                                .value(
                                        hasItem(
                                                second.get(
                                                        "deviceTransactionId"
                                                ).asText()
                                        )
                                )
                );
    }

    @Test
    void shouldGetDevicesByFingerprint()
            throws Exception {

        String fingerprint =
                "FP-SHARED-" + UUID.randomUUID();

        JsonNode first =
                createDevice(
                        fingerprint,
                        "MOBILE"
                );

        JsonNode second =
                createDevice(
                        fingerprint,
                        "TABLET"
                );

        createDevice(
                "FP-OTHER-" + UUID.randomUUID(),
                "DESKTOP"
        );

        mockMvc.perform(
                        get(
                                "/api/v1/transactions/devices/fingerprint/{deviceFingerprint}",
                                fingerprint
                        )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$")
                                .isArray()
                )
                .andExpect(
                        jsonPath("$[*].deviceTransactionId")
                                .value(
                                        hasItem(
                                                first.get(
                                                        "deviceTransactionId"
                                                ).asText()
                                        )
                                )
                )
                .andExpect(
                        jsonPath("$[*].deviceTransactionId")
                                .value(
                                        hasItem(
                                                second.get(
                                                        "deviceTransactionId"
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

    private JsonNode createDevice(
            String fingerprint,
            String deviceType)
            throws Exception {

        TransactionDeviceRequest request =
                new TransactionDeviceRequest();

        request.setDeviceFingerprint(
                fingerprint
        );

        request.setDeviceType(
                deviceType
        );

        MvcResult result =
                mockMvc.perform(
                                post(
                                        "/api/v1/transactions/{transactionId}/devices",
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
}