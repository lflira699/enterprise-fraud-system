package com.efs.modules.transaction.controller;

import com.efs.modules.customer.entity.Customer;
import com.efs.modules.customer.repository.CustomerRepository;
import com.efs.modules.transaction.dto.TransactionLocationRequest;
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
class TransactionLocationControllerIntegrationTest {

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
                "TL-CTRL-" + UUID.randomUUID()
        );

        customer.setCustomerType(
                "INDIVIDUAL"
        );

        customer.setFirstName(
                "Transaction"
        );

        customer.setLastName(
                "Location"
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
                "TL-CTRL-TXN-" + UUID.randomUUID()
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
                new BigDecimal("850.00")
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
    void shouldCreateTransactionLocation()
            throws Exception {

        TransactionLocationRequest request =
                new TransactionLocationRequest();

        request.setIpAddress(
                "203.0.113.25"
        );

        request.setCountryCode(
                "GT"
        );

        request.setState(
                "Guatemala"
        );

        request.setCity(
                "Guatemala City"
        );

        request.setPostalCode(
                "01010"
        );

        request.setLatitude(
                new BigDecimal("14.6349150")
        );

        request.setLongitude(
                new BigDecimal("-90.5068820")
        );

        request.setAsn(
                64512L
        );

        request.setInternetProvider(
                "Controller Test ISP"
        );

        request.setVpnDetected(
                true
        );

        request.setProxyDetected(
                false
        );

        request.setTorDetected(
                true
        );

        mockMvc.perform(
                        post(
                                "/api/v1/transactions/{transactionId}/locations",
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
                        jsonPath("$.locationId")
                                .exists()
                )
                .andExpect(
                        jsonPath("$.transactionId")
                                .value(
                                        transactionId.toString()
                                )
                )
                .andExpect(
                        jsonPath("$.ipAddress")
                                .value("203.0.113.25")
                )
                .andExpect(
                        jsonPath("$.countryCode")
                                .value("GT")
                )
                .andExpect(
                        jsonPath("$.state")
                                .value("Guatemala")
                )
                .andExpect(
                        jsonPath("$.city")
                                .value("Guatemala City")
                )
                .andExpect(
                        jsonPath("$.postalCode")
                                .value("01010")
                )
                .andExpect(
                        jsonPath("$.latitude")
                                .value(14.6349150)
                )
                .andExpect(
                        jsonPath("$.longitude")
                                .value(-90.5068820)
                )
                .andExpect(
                        jsonPath("$.asn")
                                .value(64512)
                )
                .andExpect(
                        jsonPath("$.internetProvider")
                                .value("Controller Test ISP")
                )
                .andExpect(
                        jsonPath("$.vpnDetected")
                                .value(true)
                )
                .andExpect(
                        jsonPath("$.proxyDetected")
                                .value(false)
                )
                .andExpect(
                        jsonPath("$.torDetected")
                                .value(true)
                )
                .andExpect(
                        jsonPath("$.createdAt")
                                .exists()
                );
    }

    @Test
    void shouldApplyFalseDefaultsOnCreate()
            throws Exception {

        MvcResult result =
                mockMvc.perform(
                                post(
                                        "/api/v1/transactions/{transactionId}/locations",
                                        transactionId
                                )
                                        .contentType(
                                                MediaType.APPLICATION_JSON
                                        )
                                        .content(
                                                objectMapper
                                                        .writeValueAsString(
                                                                new TransactionLocationRequest()
                                                        )
                                        )
                        )
                        .andExpect(status().isCreated())
                        .andExpect(
                                jsonPath("$.locationId")
                                        .exists()
                        )
                        .andExpect(
                                jsonPath("$.transactionId")
                                        .value(
                                                transactionId.toString()
                                        )
                        )
                        .andExpect(
                                jsonPath("$.vpnDetected")
                                        .value(false)
                        )
                        .andExpect(
                                jsonPath("$.proxyDetected")
                                        .value(false)
                        )
                        .andExpect(
                                jsonPath("$.torDetected")
                                        .value(false)
                        )
                        .andExpect(
                                jsonPath("$.createdAt")
                                        .exists()
                        )
                        .andReturn();

        JsonNode response =
                objectMapper.readTree(
                        result.getResponse()
                                .getContentAsString()
                );

        if (response.has("ipAddress")) {
            org.junit.jupiter.api.Assertions.assertTrue(
                    response.get("ipAddress").isNull()
            );
        }
    }

    @Test
    void shouldRejectInvalidCountryCodeLength()
            throws Exception {

        TransactionLocationRequest request =
                new TransactionLocationRequest();

        request.setCountryCode(
                "GTM"
        );

        mockMvc.perform(
                        post(
                                "/api/v1/transactions/{transactionId}/locations",
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
    void shouldReturnNotFoundWhenCreatingLocationForUnknownTransaction()
            throws Exception {

        TransactionLocationRequest request =
                new TransactionLocationRequest();

        request.setCountryCode(
                "GT"
        );

        mockMvc.perform(
                        post(
                                "/api/v1/transactions/{transactionId}/locations",
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
    void shouldGetTransactionLocationById()
            throws Exception {

        JsonNode created =
                createLocation(
                        "203.0.113.30",
                        "GT",
                        64520L
                );

        UUID locationId =
                UUID.fromString(
                        created.get(
                                "locationId"
                        ).asText()
                );

        mockMvc.perform(
                        get(
                                "/api/v1/transactions/locations/{locationId}",
                                locationId
                        )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.locationId")
                                .value(
                                        locationId.toString()
                                )
                )
                .andExpect(
                        jsonPath("$.transactionId")
                                .value(
                                        transactionId.toString()
                                )
                )
                .andExpect(
                        jsonPath("$.ipAddress")
                                .value("203.0.113.30")
                )
                .andExpect(
                        jsonPath("$.countryCode")
                                .value("GT")
                );
    }

    @Test
    void shouldReturnNotFoundForUnknownLocation()
            throws Exception {

        mockMvc.perform(
                        get(
                                "/api/v1/transactions/locations/{locationId}",
                                UUID.randomUUID()
                        )
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldGetLocationsByTransactionId()
            throws Exception {

        JsonNode first =
                createLocation(
                        "203.0.113.40",
                        "GT",
                        64530L
                );

        JsonNode second =
                createLocation(
                        "203.0.113.41",
                        "US",
                        64531L
                );

        mockMvc.perform(
                        get(
                                "/api/v1/transactions/{transactionId}/locations",
                                transactionId
                        )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$")
                                .isArray()
                )
                .andExpect(
                        jsonPath("$[*].locationId")
                                .value(
                                        hasItem(
                                                first.get(
                                                        "locationId"
                                                ).asText()
                                        )
                                )
                )
                .andExpect(
                        jsonPath("$[*].locationId")
                                .value(
                                        hasItem(
                                                second.get(
                                                        "locationId"
                                                ).asText()
                                        )
                                )
                );
    }

    @Test
    void shouldGetLocationsByIpAddress()
            throws Exception {

        String ipAddress =
                "203.0.113.50";

        JsonNode first =
                createLocation(
                        ipAddress,
                        "GT",
                        64540L
                );

        JsonNode second =
                createLocation(
                        ipAddress,
                        "US",
                        64541L
                );

        createLocation(
                "203.0.113.51",
                "GT",
                64542L
        );

        mockMvc.perform(
                        get(
                                "/api/v1/transactions/locations/ip/{ipAddress}",
                                ipAddress
                        )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$")
                                .isArray()
                )
                .andExpect(
                        jsonPath("$[*].locationId")
                                .value(
                                        hasItem(
                                                first.get(
                                                        "locationId"
                                                ).asText()
                                        )
                                )
                )
                .andExpect(
                        jsonPath("$[*].locationId")
                                .value(
                                        hasItem(
                                                second.get(
                                                        "locationId"
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
    void shouldGetLocationsByCountryCode()
            throws Exception {

        JsonNode first =
                createLocation(
                        "203.0.113.60",
                        "GT",
                        64550L
                );

        JsonNode second =
                createLocation(
                        "203.0.113.61",
                        "GT",
                        64551L
                );

        createLocation(
                "203.0.113.62",
                "US",
                64552L
        );

        mockMvc.perform(
                        get(
                                "/api/v1/transactions/locations/country/{countryCode}",
                                "GT"
                        )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$")
                                .isArray()
                )
                .andExpect(
                        jsonPath("$[*].locationId")
                                .value(
                                        hasItem(
                                                first.get(
                                                        "locationId"
                                                ).asText()
                                        )
                                )
                )
                .andExpect(
                        jsonPath("$[*].locationId")
                                .value(
                                        hasItem(
                                                second.get(
                                                        "locationId"
                                                ).asText()
                                        )
                                )
                )
                .andExpect(
                        jsonPath("$[*].countryCode")
                                .value(
                                        hasItem("GT")
                                )
                );
    }

    @Test
    void shouldGetLocationsByAsn()
            throws Exception {

        Long asn =
                64560L;

        JsonNode first =
                createLocation(
                        "203.0.113.70",
                        "GT",
                        asn
                );

        JsonNode second =
                createLocation(
                        "203.0.113.71",
                        "US",
                        asn
                );

        createLocation(
                "203.0.113.72",
                "GT",
                64561L
        );

        mockMvc.perform(
                        get(
                                "/api/v1/transactions/locations/asn/{asn}",
                                asn
                        )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$")
                                .isArray()
                )
                .andExpect(
                        jsonPath("$[*].locationId")
                                .value(
                                        hasItem(
                                                first.get(
                                                        "locationId"
                                                ).asText()
                                        )
                                )
                )
                .andExpect(
                        jsonPath("$[*].locationId")
                                .value(
                                        hasItem(
                                                second.get(
                                                        "locationId"
                                                ).asText()
                                        )
                                )
                )
                .andExpect(
                        jsonPath("$[*].asn")
                                .value(
                                        hasItem(64560)
                                )
                );
    }

    private JsonNode createLocation(
            String ipAddress,
            String countryCode,
            Long asn)
            throws Exception {

        TransactionLocationRequest request =
                new TransactionLocationRequest();

        request.setIpAddress(
                ipAddress
        );

        request.setCountryCode(
                countryCode
        );

        request.setAsn(
                asn
        );

        MvcResult result =
                mockMvc.perform(
                                post(
                                        "/api/v1/transactions/{transactionId}/locations",
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