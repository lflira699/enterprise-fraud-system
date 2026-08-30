package com.efs.modules.customer.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

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
class CustomerDeviceControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldCreateDevice() throws Exception {

        UUID customerId =
                createCustomer();

        String fingerprint =
                newFingerprint();

        String requestBody =
                """
                {
                  "deviceFingerprint": "%s",
                  "deviceType": "MOBILE",
                  "operatingSystem": "Android",
                  "browser": "Chrome",
                  "country": "Guatemala",
                  "city": "Guatemala City",
                  "trustLevel": "TRUSTED",
                  "lastSeen": "2026-08-30T10:00:00",
                  "active": true
                }
                """.formatted(
                        fingerprint
                );

        mockMvc.perform(
                        post(
                                "/api/v1/customers/{customerId}/devices",
                                customerId
                        )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(requestBody)
                )
                .andExpect(
                        status().isCreated()
                )
                .andExpect(
                        jsonPath("$.deviceId").exists()
                )
                .andExpect(
                        jsonPath("$.customerId")
                                .value(customerId.toString())
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
                        jsonPath("$.browser")
                                .value("Chrome")
                )
                .andExpect(
                        jsonPath("$.country")
                                .value("Guatemala")
                )
                .andExpect(
                        jsonPath("$.city")
                                .value("Guatemala City")
                )
                .andExpect(
                        jsonPath("$.trustLevel")
                                .value("TRUSTED")
                )
                .andExpect(
                        jsonPath("$.lastSeen")
                                .value("2026-08-30T10:00:00")
                )
                .andExpect(
                        jsonPath("$.active")
                                .value(true)
                );
    }

    @Test
    void shouldApplyActiveTrueDefault()
            throws Exception {

        UUID customerId =
                createCustomer();

        mockMvc.perform(
                        post(
                                "/api/v1/customers/{customerId}/devices",
                                customerId
                        )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        validDeviceBody(
                                                newFingerprint()
                                        )
                                )
                )
                .andExpect(
                        status().isCreated()
                )
                .andExpect(
                        jsonPath("$.active")
                                .value(true)
                );
    }

    @Test
    void shouldRejectInvalidCreateRequest()
            throws Exception {

        UUID customerId =
                createCustomer();

        String requestBody =
                """
                {
                  "deviceFingerprint": ""
                }
                """;

        mockMvc.perform(
                        post(
                                "/api/v1/customers/{customerId}/devices",
                                customerId
                        )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(requestBody)
                )
                .andExpect(
                        status().isBadRequest()
                )
                .andExpect(
                        jsonPath("$.errorCode")
                                .value("VALIDATION_ERROR")
                )
                .andExpect(
                        jsonPath("$.validationErrors.deviceFingerprint")
                                .exists()
                );
    }

    @Test
    void shouldReturnNotFoundWhenCreatingDeviceForUnknownCustomer()
            throws Exception {

        UUID unknownCustomerId =
                UUID.randomUUID();

        mockMvc.perform(
                        post(
                                "/api/v1/customers/{customerId}/devices",
                                unknownCustomerId
                        )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        validDeviceBody(
                                                newFingerprint()
                                        )
                                )
                )
                .andExpect(
                        status().isNotFound()
                );
    }

    @Test
    void shouldRejectDuplicateFingerprintForSameCustomer()
            throws Exception {

        UUID customerId =
                createCustomer();

        String fingerprint =
                newFingerprint();

        createDevice(
                customerId,
                fingerprint
        );

        mockMvc.perform(
                        post(
                                "/api/v1/customers/{customerId}/devices",
                                customerId
                        )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        validDeviceBody(
                                                fingerprint
                                        )
                                )
                )
                .andExpect(
                        status().isConflict()
                )
                .andExpect(
                        jsonPath("$.errorCode")
                                .value("CUSTOMER_DUPLICATE_RECORD")
                );
    }

    @Test
    void shouldAllowSameFingerprintForDifferentCustomers()
            throws Exception {

        UUID firstCustomerId =
                createCustomer();

        UUID secondCustomerId =
                createCustomer();

        String fingerprint =
                newFingerprint();

        createDevice(
                firstCustomerId,
                fingerprint
        );

        mockMvc.perform(
                        post(
                                "/api/v1/customers/{customerId}/devices",
                                secondCustomerId
                        )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        validDeviceBody(
                                                fingerprint
                                        )
                                )
                )
                .andExpect(
                        status().isCreated()
                )
                .andExpect(
                        jsonPath("$.customerId")
                                .value(secondCustomerId.toString())
                )
                .andExpect(
                        jsonPath("$.deviceFingerprint")
                                .value(fingerprint)
                );
    }

    @Test
    void shouldGetDeviceById()
            throws Exception {

        UUID customerId =
                createCustomer();

        String fingerprint =
                newFingerprint();

        JsonNode created =
                createDevice(
                        customerId,
                        fingerprint
                );

        UUID deviceId =
                UUID.fromString(
                        created.get(
                                "deviceId"
                        ).asText()
                );

        mockMvc.perform(
                        get(
                                "/api/v1/customers/{customerId}/devices/{deviceId}",
                                customerId,
                                deviceId
                        )
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath("$.deviceId")
                                .value(deviceId.toString())
                )
                .andExpect(
                        jsonPath("$.customerId")
                                .value(customerId.toString())
                )
                .andExpect(
                        jsonPath("$.deviceFingerprint")
                                .value(fingerprint)
                );
    }

    @Test
    void shouldReturnNotFoundWhenDeviceBelongsToDifferentCustomer()
            throws Exception {

        UUID ownerCustomerId =
                createCustomer();

        UUID otherCustomerId =
                createCustomer();

        JsonNode created =
                createDevice(
                        ownerCustomerId,
                        newFingerprint()
                );

        UUID deviceId =
                UUID.fromString(
                        created.get(
                                "deviceId"
                        ).asText()
                );

        mockMvc.perform(
                        get(
                                "/api/v1/customers/{customerId}/devices/{deviceId}",
                                otherCustomerId,
                                deviceId
                        )
                )
                .andExpect(
                        status().isNotFound()
                );
    }

    @Test
    void shouldGetDevicesByCustomer()
            throws Exception {

        UUID customerId =
                createCustomer();

        JsonNode first =
                createDevice(
                        customerId,
                        newFingerprint()
                );

        JsonNode second =
                createDevice(
                        customerId,
                        newFingerprint()
                );

        mockMvc.perform(
                        get(
                                "/api/v1/customers/{customerId}/devices",
                                customerId
                        )
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath("$").isArray()
                )
                .andExpect(
                        jsonPath("$[*].deviceId")
                                .value(
                                        hasItem(
                                                first.get(
                                                        "deviceId"
                                                ).asText()
                                        )
                                )
                )
                .andExpect(
                        jsonPath("$[*].deviceId")
                                .value(
                                        hasItem(
                                                second.get(
                                                        "deviceId"
                                                ).asText()
                                        )
                                )
                );
    }

    @Test
    void shouldUpdateDevice()
            throws Exception {

        UUID customerId =
                createCustomer();

        JsonNode created =
                createDevice(
                        customerId,
                        newFingerprint()
                );

        UUID deviceId =
                UUID.fromString(
                        created.get(
                                "deviceId"
                        ).asText()
                );

        String updatedFingerprint =
                newFingerprint();

        String requestBody =
                """
                {
                  "deviceFingerprint": "%s",
                  "deviceType": "DESKTOP",
                  "operatingSystem": "Windows 11",
                  "browser": "Edge",
                  "country": "Guatemala",
                  "city": "Guatemala City",
                  "trustLevel": "KNOWN",
                  "lastSeen": "2026-08-30T10:30:00",
                  "active": true
                }
                """.formatted(
                        updatedFingerprint
                );

        mockMvc.perform(
                        put(
                                "/api/v1/customers/{customerId}/devices/{deviceId}",
                                customerId,
                                deviceId
                        )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(requestBody)
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath("$.deviceId")
                                .value(deviceId.toString())
                )
                .andExpect(
                        jsonPath("$.customerId")
                                .value(customerId.toString())
                )
                .andExpect(
                        jsonPath("$.deviceFingerprint")
                                .value(updatedFingerprint)
                )
                .andExpect(
                        jsonPath("$.deviceType")
                                .value("DESKTOP")
                )
                .andExpect(
                        jsonPath("$.operatingSystem")
                                .value("Windows 11")
                )
                .andExpect(
                        jsonPath("$.browser")
                                .value("Edge")
                )
                .andExpect(
                        jsonPath("$.trustLevel")
                                .value("KNOWN")
                )
                .andExpect(
                        jsonPath("$.active")
                                .value(true)
                );
    }

    @Test
    void shouldRejectDuplicateFingerprintOnUpdate()
            throws Exception {

        UUID customerId =
                createCustomer();

        String firstFingerprint =
                newFingerprint();

        String secondFingerprint =
                newFingerprint();

        JsonNode firstDevice =
                createDevice(
                        customerId,
                        firstFingerprint
                );

        createDevice(
                customerId,
                secondFingerprint
        );

        UUID firstDeviceId =
                UUID.fromString(
                        firstDevice.get(
                                "deviceId"
                        ).asText()
                );

        mockMvc.perform(
                        put(
                                "/api/v1/customers/{customerId}/devices/{deviceId}",
                                customerId,
                                firstDeviceId
                        )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        validDeviceBody(
                                                secondFingerprint
                                        )
                                )
                )
                .andExpect(
                        status().isConflict()
                )
                .andExpect(
                        jsonPath("$.errorCode")
                                .value("CUSTOMER_DUPLICATE_RECORD")
                );
    }

    @Test
    void shouldRejectUpdateThroughDifferentCustomer()
            throws Exception {

        UUID ownerCustomerId =
                createCustomer();

        UUID otherCustomerId =
                createCustomer();

        JsonNode created =
                createDevice(
                        ownerCustomerId,
                        newFingerprint()
                );

        UUID deviceId =
                UUID.fromString(
                        created.get(
                                "deviceId"
                        ).asText()
                );

        mockMvc.perform(
                        put(
                                "/api/v1/customers/{customerId}/devices/{deviceId}",
                                otherCustomerId,
                                deviceId
                        )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        validDeviceBody(
                                                newFingerprint()
                                        )
                                )
                )
                .andExpect(
                        status().isNotFound()
                );
    }

    @Test
    void shouldRejectCreateAndListForSoftDeletedCustomer()
            throws Exception {

        UUID customerId =
                createCustomer();

        mockMvc.perform(
                        delete(
                                "/api/v1/customers/{customerId}",
                                customerId
                        )
                )
                .andExpect(
                        status().isNoContent()
                );

        mockMvc.perform(
                        post(
                                "/api/v1/customers/{customerId}/devices",
                                customerId
                        )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        validDeviceBody(
                                                newFingerprint()
                                        )
                                )
                )
                .andExpect(
                        status().isNotFound()
                );

        mockMvc.perform(
                        get(
                                "/api/v1/customers/{customerId}/devices",
                                customerId
                        )
                )
                .andExpect(
                        status().isNotFound()
                );
    }

    private UUID createCustomer()
            throws Exception {

        String customerNumber =
                "CD-CTRL-" + UUID.randomUUID();

        String requestBody =
                """
                {
                  "customerNumber": "%s",
                  "customerType": "INDIVIDUAL",
                  "firstName": "Device",
                  "lastName": "Controller"
                }
                """.formatted(
                        customerNumber
                );

        MvcResult result =
                mockMvc.perform(
                                post("/api/v1/customers")
                                        .contentType(
                                                MediaType.APPLICATION_JSON
                                        )
                                        .content(requestBody)
                        )
                        .andExpect(
                                status().isCreated()
                        )
                        .andReturn();

        JsonNode response =
                objectMapper.readTree(
                        result.getResponse()
                                .getContentAsString()
                );

        return UUID.fromString(
                response.get(
                        "customerId"
                ).asText()
        );
    }

    private JsonNode createDevice(
            UUID customerId,
            String fingerprint)
            throws Exception {

        MvcResult result =
                mockMvc.perform(
                                post(
                                        "/api/v1/customers/{customerId}/devices",
                                        customerId
                                )
                                        .contentType(
                                                MediaType.APPLICATION_JSON
                                        )
                                        .content(
                                                validDeviceBody(
                                                        fingerprint
                                                )
                                        )
                        )
                        .andExpect(
                                status().isCreated()
                        )
                        .andReturn();

        return objectMapper.readTree(
                result.getResponse()
                        .getContentAsString()
        );
    }

    private String validDeviceBody(
            String fingerprint) {

        return """
                {
                  "deviceFingerprint": "%s"
                }
                """.formatted(
                fingerprint
        );
    }

    private String newFingerprint() {

        return "DFP-" + UUID.randomUUID();
    }
}