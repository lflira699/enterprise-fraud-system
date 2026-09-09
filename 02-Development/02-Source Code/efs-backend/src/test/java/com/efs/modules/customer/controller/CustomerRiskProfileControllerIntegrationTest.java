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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class CustomerRiskProfileControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldCreateRiskProfile() throws Exception {

        UUID customerId =
                createCustomer();

        UUID createdBy =
                UUID.randomUUID();

        UUID correlationId =
                UUID.randomUUID();

        String requestBody =
                """
                {
                  "currentRiskScore": 88.50,
                  "riskLevel": "HIGH",
                  "behaviorScore": 50.00,
                  "fraudScore": 50.00,
                  "amlScore": 50.00,
                  "kycScore": 50.00,
                  "deviceScore": 50.00,
                  "sanctionsScore": 50.00,
                  "pepScore": 50.00,
                  "watchlistScore": 50.00,
                  "createdBy": "%s",
                  "correlationId": "%s"
                }
                """.formatted(
                        createdBy,
                        correlationId
                );

        mockMvc.perform(
                        post(
                                "/api/v1/customers/{customerId}/risk-profile",
                                customerId
                        )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(requestBody)
                )
                .andExpect(status().isCreated())
                .andExpect(
                        jsonPath("$.profileId").exists()
                )
                .andExpect(
                        jsonPath("$.customerId")
                                .value(customerId.toString())
                )
                .andExpect(
                        jsonPath("$.currentRiskScore")
                                .value(50.00)
                )
                .andExpect(
                        jsonPath("$.riskLevel")
                                .value("MEDIUM")
                )
                .andExpect(
                        jsonPath("$.behaviorScore")
                                .value(50.00)
                )
                .andExpect(
                        jsonPath("$.fraudScore")
                                .value(50.00)
                )
                .andExpect(
                        jsonPath("$.amlScore")
                                .value(50.00)
                )
                .andExpect(
                        jsonPath("$.kycScore")
                                .value(50.00)
                )
                .andExpect(
                        jsonPath("$.deviceScore")
                                .value(50.00)
                )
                .andExpect(
                        jsonPath("$.sanctionsScore")
                                .value(50.00)
                )
                .andExpect(
                        jsonPath("$.pepScore")
                                .value(50.00)
                )
                .andExpect(
                        jsonPath("$.watchlistScore")
                                .value(50.00)
                )
                .andExpect(
                        jsonPath("$.lastCalculation").exists()
                );
    }

    @Test
    void shouldReuseRiskProfileForSameEvaluationProcess()
            throws Exception {

        UUID customerId =
                createCustomer();

        UUID correlationId =
                UUID.randomUUID();

        mockMvc.perform(
                        post(
                                "/api/v1/customers/{customerId}/risk-profile",
                                customerId
                        )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        validRiskProfileBody(
                                                "LOW",
                                                correlationId
                                        )
                                )
                )
                .andExpect(status().isCreated())
                .andExpect(
                        jsonPath("$.currentRiskScore")
                                .value(30.00)
                )
                .andExpect(
                        jsonPath("$.riskLevel")
                                .value("LOW")
                );

        mockMvc.perform(
                        post(
                                "/api/v1/customers/{customerId}/risk-profile",
                                customerId
                        )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        validRiskProfileBody(
                                                "CRITICAL",
                                                correlationId
                                        )
                                )
                )
                .andExpect(status().isCreated())
                .andExpect(
                        jsonPath("$.currentRiskScore")
                                .value(30.00)
                )
                .andExpect(
                        jsonPath("$.riskLevel")
                                .value("LOW")
                );
    }

    @Test
    void shouldRejectInvalidCreateRequest() throws Exception {

        UUID customerId =
                createCustomer();

        String requestBody =
                """
                {
                  "behaviorScore": 50.00,
                  "fraudScore": 50.00,
                  "amlScore": 50.00,
                  "kycScore": 50.00,
                  "deviceScore": 50.00,
                  "sanctionsScore": 50.00,
                  "pepScore": 50.00,
                  "watchlistScore": 50.00
                }
                """;

        mockMvc.perform(
                        post(
                                "/api/v1/customers/{customerId}/risk-profile",
                                customerId
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
                                "$.validationErrors.correlationId"
                        ).exists()
                );
    }

    @Test
    void shouldReturnNotFoundWhenCreatingForUnknownCustomer()
            throws Exception {

        UUID unknownCustomerId = UUID.randomUUID();

        mockMvc.perform(
                        post(
                                "/api/v1/customers/{customerId}/risk-profile",
                                unknownCustomerId
                        )
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        validRiskProfileBody("LOW")
                                )
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldRejectDuplicateActiveRiskProfile()
            throws Exception {

        UUID customerId = createCustomer();

        createRiskProfile(
                customerId,
                "LOW"
        );

        mockMvc.perform(
                        post(
                                "/api/v1/customers/{customerId}/risk-profile",
                                customerId
                        )
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        validRiskProfileBody("HIGH")
                                )
                )
                .andExpect(status().isConflict())
                .andExpect(
                        jsonPath("$.errorCode")
                                .value("CUSTOMER_DUPLICATE_RECORD")
                );
    }

    @Test
    void shouldGetRiskProfile() throws Exception {

        UUID customerId = createCustomer();

        JsonNode created =
                createRiskProfile(
                        customerId,
                        "MEDIUM"
                );

        String profileId =
                created.get("profileId").asText();

        mockMvc.perform(
                        get(
                                "/api/v1/customers/{customerId}/risk-profile",
                                customerId
                        )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.profileId")
                                .value(profileId)
                )
                .andExpect(
                        jsonPath("$.customerId")
                                .value(customerId.toString())
                )
                .andExpect(
                        jsonPath("$.riskLevel")
                                .value("MEDIUM")
                );
    }

    @Test
    void shouldReturnNotFoundWhenCustomerHasNoRiskProfile()
            throws Exception {

        UUID customerId = createCustomer();

        mockMvc.perform(
                        get(
                                "/api/v1/customers/{customerId}/risk-profile",
                                customerId
                        )
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnNotFoundWhenGettingRiskProfileForUnknownCustomer()
            throws Exception {

        mockMvc.perform(
                        get(
                                "/api/v1/customers/{customerId}/risk-profile",
                                UUID.randomUUID()
                        )
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldUpdateRiskProfile() throws Exception {

        UUID customerId =
                createCustomer();

        JsonNode created =
                createRiskProfile(
                        customerId,
                        "LOW"
                );

        String profileId =
                created.get("profileId").asText();

        UUID updatedBy =
                UUID.randomUUID();

        UUID correlationId =
                UUID.randomUUID();

        String requestBody =
                """
                {
                  "currentRiskScore": 94.25,
                  "riskLevel": "HIGH",
                  "behaviorScore": 80.00,
                  "fraudScore": 80.00,
                  "amlScore": 80.00,
                  "kycScore": 80.00,
                  "deviceScore": 80.00,
                  "sanctionsScore": 80.00,
                  "pepScore": 80.00,
                  "watchlistScore": 80.00,
                  "updatedBy": "%s",
                  "correlationId": "%s"
                }
                """.formatted(
                        updatedBy,
                        correlationId
                );

        mockMvc.perform(
                        put(
                                "/api/v1/customers/{customerId}/risk-profile",
                                customerId
                        )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(requestBody)
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.profileId")
                                .value(profileId)
                )
                .andExpect(
                        jsonPath("$.customerId")
                                .value(customerId.toString())
                )
                .andExpect(
                        jsonPath("$.currentRiskScore")
                                .value(80.00)
                )
                .andExpect(
                        jsonPath("$.riskLevel")
                                .value("CRITICAL")
                )
                .andExpect(
                        jsonPath("$.behaviorScore")
                                .value(80.00)
                )
                .andExpect(
                        jsonPath("$.watchlistScore")
                                .value(80.00)
                )
                .andExpect(
                        jsonPath("$.lastCalculation").exists()
                );
    }

    @Test
    void shouldReturnNotFoundWhenUpdatingMissingRiskProfile()
            throws Exception {

        UUID customerId = createCustomer();

        mockMvc.perform(
                        put(
                                "/api/v1/customers/{customerId}/risk-profile",
                                customerId
                        )
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        validRiskProfileBody("HIGH")
                                )
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldDeleteRiskProfileAndHideItFromReads()
            throws Exception {

        UUID customerId = createCustomer();

        createRiskProfile(
                customerId,
                "MEDIUM"
        );

        mockMvc.perform(
                        delete(
                                "/api/v1/customers/{customerId}/risk-profile",
                                customerId
                        )
                )
                .andExpect(status().isNoContent());

        mockMvc.perform(
                        get(
                                "/api/v1/customers/{customerId}/risk-profile",
                                customerId
                        )
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnNotFoundWhenDeletingMissingRiskProfile()
            throws Exception {

        UUID customerId = createCustomer();

        mockMvc.perform(
                        delete(
                                "/api/v1/customers/{customerId}/risk-profile",
                                customerId
                        )
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldRejectCreateForSoftDeletedCustomer()
            throws Exception {

        UUID customerId = createCustomer();

        deleteCustomer(customerId);

        mockMvc.perform(
                        post(
                                "/api/v1/customers/{customerId}/risk-profile",
                                customerId
                        )
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        validRiskProfileBody("HIGH")
                                )
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldRejectGetForSoftDeletedCustomer()
            throws Exception {

        UUID customerId = createCustomer();

        createRiskProfile(
                customerId,
                "MEDIUM"
        );

        deleteCustomer(customerId);

        mockMvc.perform(
                        get(
                                "/api/v1/customers/{customerId}/risk-profile",
                                customerId
                        )
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldRejectUpdateForSoftDeletedCustomer()
            throws Exception {

        UUID customerId = createCustomer();

        createRiskProfile(
                customerId,
                "LOW"
        );

        deleteCustomer(customerId);

        mockMvc.perform(
                        put(
                                "/api/v1/customers/{customerId}/risk-profile",
                                customerId
                        )
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        validRiskProfileBody("HIGH")
                                )
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldRejectDeleteForSoftDeletedCustomer()
            throws Exception {

        UUID customerId = createCustomer();

        createRiskProfile(
                customerId,
                "LOW"
        );

        deleteCustomer(customerId);

        mockMvc.perform(
                        delete(
                                "/api/v1/customers/{customerId}/risk-profile",
                                customerId
                        )
                )
                .andExpect(status().isNotFound());
    }

    private UUID createCustomer() throws Exception {

        String customerNumber =
                "CRISK-CTRL-" + UUID.randomUUID();

        String requestBody =
                """
                {
                  "customerNumber": "%s",
                  "customerType": "INDIVIDUAL",
                  "firstName": "Risk",
                  "lastName": "Profile"
                }
                """.formatted(customerNumber);

        MvcResult result =
                mockMvc.perform(
                                post("/api/v1/customers")
                                        .contentType(
                                                MediaType.APPLICATION_JSON
                                        )
                                        .content(requestBody)
                        )
                        .andExpect(status().isCreated())
                        .andReturn();

        JsonNode response =
                objectMapper.readTree(
                        result.getResponse()
                                .getContentAsString()
                );

        return UUID.fromString(
                response.get("customerId").asText()
        );
    }

    private JsonNode createRiskProfile(
            UUID customerId,
            String riskLevel)
            throws Exception {

        MvcResult result =
                mockMvc.perform(
                                post(
                                        "/api/v1/customers/{customerId}/risk-profile",
                                        customerId
                                )
                                        .contentType(
                                                MediaType.APPLICATION_JSON
                                        )
                                        .content(
                                                validRiskProfileBody(
                                                        riskLevel
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

    private void deleteCustomer(
            UUID customerId)
            throws Exception {

        mockMvc.perform(
                        delete(
                                "/api/v1/customers/{customerId}",
                                customerId
                        )
                )
                .andExpect(status().isNoContent());
    }

    private String validRiskProfileBody(
            String riskLevel) {

        return validRiskProfileBody(
                riskLevel,
                UUID.randomUUID()
        );
    }

    private String validRiskProfileBody(
            String riskLevel,
            UUID correlationId) {

        String score =
                switch (riskLevel) {
                    case "VERY_LOW" -> "10.00";
                    case "LOW" -> "30.00";
                    case "MEDIUM" -> "50.00";
                    case "HIGH" -> "70.00";
                    case "CRITICAL" -> "90.00";
                    default -> throw new IllegalArgumentException(
                            "Unsupported test risk level: "
                                    + riskLevel
                    );
                };

        return """
                {
                  "riskLevel": "%s",
                  "behaviorScore": %s,
                  "fraudScore": %s,
                  "amlScore": %s,
                  "kycScore": %s,
                  "deviceScore": %s,
                  "sanctionsScore": %s,
                  "pepScore": %s,
                  "watchlistScore": %s,
                  "correlationId": "%s"
                }
                """.formatted(
                        riskLevel,
                        score,
                        score,
                        score,
                        score,
                        score,
                        score,
                        score,
                        score,
                        correlationId
                );
    }
}