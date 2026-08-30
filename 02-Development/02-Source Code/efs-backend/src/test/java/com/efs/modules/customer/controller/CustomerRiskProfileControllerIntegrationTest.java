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

        UUID customerId = createCustomer();
        UUID createdBy = UUID.randomUUID();

        String requestBody =
                """
                {
                  "currentRiskScore": 88.50,
                  "riskLevel": "HIGH",
                  "behaviorScore": 71.25,
                  "fraudScore": 92.75,
                  "amlScore": 45.50,
                  "kycScore": 33.25,
                  "deviceScore": 81.75,
                  "sanctionsScore": 12.50,
                  "pepScore": 18.25,
                  "watchlistScore": 27.75,
                  "createdBy": "%s"
                }
                """.formatted(createdBy);

        mockMvc.perform(
                        post(
                                "/api/v1/customers/{customerId}/risk-profile",
                                customerId
                        )
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.profileId").exists())
                .andExpect(
                        jsonPath("$.customerId")
                                .value(customerId.toString())
                )
                .andExpect(
                        jsonPath("$.currentRiskScore")
                                .value(88.50)
                )
                .andExpect(
                        jsonPath("$.riskLevel")
                                .value("HIGH")
                )
                .andExpect(
                        jsonPath("$.behaviorScore")
                                .value(71.25)
                )
                .andExpect(
                        jsonPath("$.fraudScore")
                                .value(92.75)
                )
                .andExpect(
                        jsonPath("$.amlScore")
                                .value(45.50)
                )
                .andExpect(
                        jsonPath("$.kycScore")
                                .value(33.25)
                )
                .andExpect(
                        jsonPath("$.deviceScore")
                                .value(81.75)
                )
                .andExpect(
                        jsonPath("$.sanctionsScore")
                                .value(12.50)
                )
                .andExpect(
                        jsonPath("$.pepScore")
                                .value(18.25)
                )
                .andExpect(
                        jsonPath("$.watchlistScore")
                                .value(27.75)
                )
                .andExpect(
                        jsonPath("$.createdBy")
                                .value(createdBy.toString())
                )
                .andExpect(
                        jsonPath("$.lastCalculation").exists()
                )
                .andExpect(
                        jsonPath("$.createdAt").exists()
                )
                .andExpect(
                        jsonPath("$.updatedAt").exists()
                );
    }

    @Test
    void shouldApplyDefaultScores() throws Exception {

        UUID customerId = createCustomer();

        mockMvc.perform(
                        post(
                                "/api/v1/customers/{customerId}/risk-profile",
                                customerId
                        )
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        validRiskProfileBody("LOW")
                                )
                )
                .andExpect(status().isCreated())
                .andExpect(
                        jsonPath("$.currentRiskScore")
                                .value(0)
                )
                .andExpect(
                        jsonPath("$.behaviorScore")
                                .value(0)
                )
                .andExpect(
                        jsonPath("$.fraudScore")
                                .value(0)
                )
                .andExpect(
                        jsonPath("$.amlScore")
                                .value(0)
                )
                .andExpect(
                        jsonPath("$.kycScore")
                                .value(0)
                )
                .andExpect(
                        jsonPath("$.deviceScore")
                                .value(0)
                )
                .andExpect(
                        jsonPath("$.sanctionsScore")
                                .value(0)
                )
                .andExpect(
                        jsonPath("$.pepScore")
                                .value(0)
                )
                .andExpect(
                        jsonPath("$.watchlistScore")
                                .value(0)
                )
                .andExpect(
                        jsonPath("$.riskLevel")
                                .value("LOW")
                );
    }

    @Test
    void shouldRejectInvalidCreateRequest() throws Exception {

        UUID customerId = createCustomer();

        String requestBody =
                """
                {
                  "riskLevel": ""
                }
                """;

        mockMvc.perform(
                        post(
                                "/api/v1/customers/{customerId}/risk-profile",
                                customerId
                        )
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isBadRequest())
                .andExpect(
                        jsonPath("$.errorCode")
                                .value("VALIDATION_ERROR")
                )
                .andExpect(
                        jsonPath("$.validationErrors.riskLevel")
                                .exists()
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

        UUID customerId = createCustomer();

        JsonNode created =
                createRiskProfile(
                        customerId,
                        "LOW"
                );

        String profileId =
                created.get("profileId").asText();

        UUID updatedBy = UUID.randomUUID();

        String requestBody =
                """
                {
                  "currentRiskScore": 94.25,
                  "riskLevel": "HIGH",
                  "behaviorScore": 75.50,
                  "fraudScore": 96.75,
                  "amlScore": 51.25,
                  "kycScore": 44.50,
                  "deviceScore": 89.25,
                  "sanctionsScore": 15.00,
                  "pepScore": 22.50,
                  "watchlistScore": 31.75,
                  "updatedBy": "%s"
                }
                """.formatted(updatedBy);

        mockMvc.perform(
                        put(
                                "/api/v1/customers/{customerId}/risk-profile",
                                customerId
                        )
                                .contentType(MediaType.APPLICATION_JSON)
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
                                .value(94.25)
                )
                .andExpect(
                        jsonPath("$.riskLevel")
                                .value("HIGH")
                )
                .andExpect(
                        jsonPath("$.fraudScore")
                                .value(96.75)
                )
                .andExpect(
                        jsonPath("$.deviceScore")
                                .value(89.25)
                )
                .andExpect(
                        jsonPath("$.updatedBy")
                                .value(updatedBy.toString())
                )
                .andExpect(
                        jsonPath("$.lastCalculation").exists()
                )
                .andExpect(
                        jsonPath("$.updatedAt").exists()
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

        return """
                {
                  "riskLevel": "%s"
                }
                """.formatted(riskLevel);
    }
}