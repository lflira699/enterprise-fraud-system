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
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class CustomerBiometricControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldCreateBiometric() throws Exception {

        UUID customerId =
                createCustomer();

        UUID createdBy =
                UUID.randomUUID();

        String providerReference =
                "BIO-PROVIDER-" + UUID.randomUUID();

        String requestBody =
                """
                {
                  "biometricType": "FACE",
                  "verificationStatus": "VERIFIED",
                  "verificationScore": 98.75,
                  "providerReference": "%s",
                  "enrolledAt": "2026-08-01T10:15:30",
                  "lastVerifiedAt": "2026-08-30T09:30:00",
                  "active": true,
                  "createdBy": "%s"
                }
                """.formatted(
                        providerReference,
                        createdBy
                );

        mockMvc.perform(
                        post(
                                "/api/v1/customers/{customerId}/biometrics",
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
                        jsonPath("$.biometricId").exists()
                )
                .andExpect(
                        jsonPath("$.customerId")
                                .value(customerId.toString())
                )
                .andExpect(
                        jsonPath("$.biometricType")
                                .value("FACE")
                )
                .andExpect(
                        jsonPath("$.verificationStatus")
                                .value("VERIFIED")
                )
                .andExpect(
                        jsonPath("$.verificationScore")
                                .value(98.75)
                )
                .andExpect(
                        jsonPath("$.providerReference")
                                .value(providerReference)
                )
                .andExpect(
                        jsonPath("$.enrolledAt")
                                .value("2026-08-01T10:15:30")
                )
                .andExpect(
                        jsonPath("$.lastVerifiedAt")
                                .value("2026-08-30T09:30:00")
                )
                .andExpect(
                        jsonPath("$.active")
                                .value(true)
                )
                .andExpect(
                        jsonPath("$.createdBy")
                                .value(createdBy.toString())
                )
                .andExpect(
                        jsonPath("$.createdAt").exists()
                )
                .andExpect(
                        jsonPath("$.updatedAt").exists()
                );
    }

    @Test
    void shouldApplyActiveTrueDefault()
            throws Exception {

        UUID customerId =
                createCustomer();

        String requestBody =
                """
                {
                  "biometricType": "FINGERPRINT",
                  "verificationStatus": "PENDING"
                }
                """;

        mockMvc.perform(
                        post(
                                "/api/v1/customers/{customerId}/biometrics",
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
                  "biometricType": "",
                  "verificationStatus": ""
                }
                """;

        mockMvc.perform(
                        post(
                                "/api/v1/customers/{customerId}/biometrics",
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
                        jsonPath("$.validationErrors.biometricType")
                                .exists()
                )
                .andExpect(
                        jsonPath("$.validationErrors.verificationStatus")
                                .exists()
                );
    }

    @Test
    void shouldReturnNotFoundWhenCreatingBiometricForUnknownCustomer()
            throws Exception {

        UUID unknownCustomerId =
                UUID.randomUUID();

        mockMvc.perform(
                        post(
                                "/api/v1/customers/{customerId}/biometrics",
                                unknownCustomerId
                        )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        validBiometricBody(
                                                "FACE",
                                                "VERIFIED"
                                        )
                                )
                )
                .andExpect(
                        status().isNotFound()
                );
    }

    @Test
    void shouldGetBiometricById()
            throws Exception {

        UUID customerId =
                createCustomer();

        JsonNode created =
                createBiometric(
                        customerId,
                        "FACE",
                        "VERIFIED"
                );

        UUID biometricId =
                UUID.fromString(
                        created.get(
                                "biometricId"
                        ).asText()
                );

        mockMvc.perform(
                        get(
                                "/api/v1/customers/{customerId}/biometrics/{biometricId}",
                                customerId,
                                biometricId
                        )
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath("$.biometricId")
                                .value(biometricId.toString())
                )
                .andExpect(
                        jsonPath("$.customerId")
                                .value(customerId.toString())
                )
                .andExpect(
                        jsonPath("$.biometricType")
                                .value("FACE")
                )
                .andExpect(
                        jsonPath("$.verificationStatus")
                                .value("VERIFIED")
                );
    }

    @Test
    void shouldReturnNotFoundWhenBiometricBelongsToDifferentCustomer()
            throws Exception {

        UUID ownerCustomerId =
                createCustomer();

        UUID otherCustomerId =
                createCustomer();

        JsonNode created =
                createBiometric(
                        ownerCustomerId,
                        "FACE",
                        "VERIFIED"
                );

        UUID biometricId =
                UUID.fromString(
                        created.get(
                                "biometricId"
                        ).asText()
                );

        mockMvc.perform(
                        get(
                                "/api/v1/customers/{customerId}/biometrics/{biometricId}",
                                otherCustomerId,
                                biometricId
                        )
                )
                .andExpect(
                        status().isNotFound()
                );
    }

    @Test
    void shouldGetBiometricsByCustomer()
            throws Exception {

        UUID customerId =
                createCustomer();

        JsonNode first =
                createBiometric(
                        customerId,
                        "FACE",
                        "VERIFIED"
                );

        JsonNode second =
                createBiometric(
                        customerId,
                        "FINGERPRINT",
                        "PENDING"
                );

        mockMvc.perform(
                        get(
                                "/api/v1/customers/{customerId}/biometrics",
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
                        jsonPath("$[*].biometricId")
                                .value(
                                        hasItem(
                                                first.get(
                                                        "biometricId"
                                                ).asText()
                                        )
                                )
                )
                .andExpect(
                        jsonPath("$[*].biometricId")
                                .value(
                                        hasItem(
                                                second.get(
                                                        "biometricId"
                                                ).asText()
                                        )
                                )
                );
    }

    @Test
    void shouldUpdateBiometric()
            throws Exception {

        UUID customerId =
                createCustomer();

        JsonNode created =
                createBiometric(
                        customerId,
                        "FACE",
                        "PENDING"
                );

        UUID biometricId =
                UUID.fromString(
                        created.get(
                                "biometricId"
                        ).asText()
                );

        UUID updatedBy =
                UUID.randomUUID();

        String providerReference =
                "BIO-UPDATED-" + UUID.randomUUID();

        String requestBody =
                """
                {
                  "biometricType": "FACE",
                  "verificationStatus": "VERIFIED",
                  "verificationScore": 99.50,
                  "providerReference": "%s",
                  "enrolledAt": "2026-08-01T10:00:00",
                  "lastVerifiedAt": "2026-08-30T09:45:00",
                  "active": true,
                  "updatedBy": "%s"
                }
                """.formatted(
                        providerReference,
                        updatedBy
                );

        mockMvc.perform(
                        put(
                                "/api/v1/customers/{customerId}/biometrics/{biometricId}",
                                customerId,
                                biometricId
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
                        jsonPath("$.biometricId")
                                .value(biometricId.toString())
                )
                .andExpect(
                        jsonPath("$.verificationStatus")
                                .value("VERIFIED")
                )
                .andExpect(
                        jsonPath("$.verificationScore")
                                .value(99.50)
                )
                .andExpect(
                        jsonPath("$.providerReference")
                                .value(providerReference)
                )
                .andExpect(
                        jsonPath("$.active")
                                .value(true)
                )
                .andExpect(
                        jsonPath("$.updatedBy")
                                .value(updatedBy.toString())
                )
                .andExpect(
                        jsonPath("$.updatedAt").exists()
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
                createBiometric(
                        ownerCustomerId,
                        "FACE",
                        "PENDING"
                );

        UUID biometricId =
                UUID.fromString(
                        created.get(
                                "biometricId"
                        ).asText()
                );

        mockMvc.perform(
                        put(
                                "/api/v1/customers/{customerId}/biometrics/{biometricId}",
                                otherCustomerId,
                                biometricId
                        )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        validBiometricBody(
                                                "FACE",
                                                "VERIFIED"
                                        )
                                )
                )
                .andExpect(
                        status().isNotFound()
                );
    }

    @Test
    void shouldDeleteBiometricAndHideItFromReads()
            throws Exception {

        UUID customerId =
                createCustomer();

        JsonNode created =
                createBiometric(
                        customerId,
                        "FACE",
                        "VERIFIED"
                );

        UUID biometricId =
                UUID.fromString(
                        created.get(
                                "biometricId"
                        ).asText()
                );

        mockMvc.perform(
                        delete(
                                "/api/v1/customers/{customerId}/biometrics/{biometricId}",
                                customerId,
                                biometricId
                        )
                )
                .andExpect(
                        status().isNoContent()
                );

        mockMvc.perform(
                        get(
                                "/api/v1/customers/{customerId}/biometrics/{biometricId}",
                                customerId,
                                biometricId
                        )
                )
                .andExpect(
                        status().isNotFound()
                );

        mockMvc.perform(
                        get(
                                "/api/v1/customers/{customerId}/biometrics",
                                customerId
                        )
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath("$[*].biometricId")
                                .value(
                                        not(
                                                hasItem(
                                                        biometricId.toString()
                                                )
                                        )
                                )
                );
    }

    @Test
    void shouldRejectDeleteThroughDifferentCustomer()
            throws Exception {

        UUID ownerCustomerId =
                createCustomer();

        UUID otherCustomerId =
                createCustomer();

        JsonNode created =
                createBiometric(
                        ownerCustomerId,
                        "FACE",
                        "VERIFIED"
                );

        UUID biometricId =
                UUID.fromString(
                        created.get(
                                "biometricId"
                        ).asText()
                );

        mockMvc.perform(
                        delete(
                                "/api/v1/customers/{customerId}/biometrics/{biometricId}",
                                otherCustomerId,
                                biometricId
                        )
                )
                .andExpect(
                        status().isNotFound()
                );
    }

    @Test
    void shouldRejectBiometricOperationsForSoftDeletedCustomer()
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
                                "/api/v1/customers/{customerId}/biometrics",
                                customerId
                        )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        validBiometricBody(
                                                "FACE",
                                                "VERIFIED"
                                        )
                                )
                )
                .andExpect(
                        status().isNotFound()
                );

        mockMvc.perform(
                        get(
                                "/api/v1/customers/{customerId}/biometrics",
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
                "CBIO-CTRL-" + UUID.randomUUID();

        String requestBody =
                """
                {
                  "customerNumber": "%s",
                  "customerType": "INDIVIDUAL",
                  "firstName": "Biometric",
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

    private JsonNode createBiometric(
            UUID customerId,
            String biometricType,
            String verificationStatus)
            throws Exception {

        MvcResult result =
                mockMvc.perform(
                                post(
                                        "/api/v1/customers/{customerId}/biometrics",
                                        customerId
                                )
                                        .contentType(
                                                MediaType.APPLICATION_JSON
                                        )
                                        .content(
                                                validBiometricBody(
                                                        biometricType,
                                                        verificationStatus
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

    private String validBiometricBody(
            String biometricType,
            String verificationStatus) {

        return """
                {
                  "biometricType": "%s",
                  "verificationStatus": "%s"
                }
                """.formatted(
                biometricType,
                verificationStatus
        );
    }
}