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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class CustomerHistoryControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldCreateHistory() throws Exception {

        UUID customerId =
                createCustomer();

        UUID createdBy =
                UUID.randomUUID();

        String requestBody =
                """
                {
                  "eventType": "RISK_CHANGE",
                  "eventDescription": "Customer risk profile changed",
                  "previousStatus": "ACTIVE",
                  "newStatus": "ACTIVE",
                  "previousRiskLevel": "LOW",
                  "newRiskLevel": "HIGH",
                  "previousRiskScore": 25.50,
                  "newRiskScore": 82.75,
                  "eventTimestamp": "2026-08-30T10:30:00",
                  "sourceReference": "CTRL-HISTORY-001",
                  "createdBy": "%s"
                }
                """.formatted(
                        createdBy
                );

        mockMvc.perform(
                        post(
                                "/api/v1/customers/{customerId}/history",
                                customerId
                        )
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isCreated())
                .andExpect(
                        jsonPath("$.customerHistoryId").exists()
                )
                .andExpect(
                        jsonPath("$.customerId")
                                .value(customerId.toString())
                )
                .andExpect(
                        jsonPath("$.eventType")
                                .value("RISK_CHANGE")
                )
                .andExpect(
                        jsonPath("$.eventDescription")
                                .value("Customer risk profile changed")
                )
                .andExpect(
                        jsonPath("$.previousStatus")
                                .value("ACTIVE")
                )
                .andExpect(
                        jsonPath("$.newStatus")
                                .value("ACTIVE")
                )
                .andExpect(
                        jsonPath("$.previousRiskLevel")
                                .value("LOW")
                )
                .andExpect(
                        jsonPath("$.newRiskLevel")
                                .value("HIGH")
                )
                .andExpect(
                        jsonPath("$.previousRiskScore")
                                .value(25.50)
                )
                .andExpect(
                        jsonPath("$.newRiskScore")
                                .value(82.75)
                )
                .andExpect(
                        jsonPath("$.eventTimestamp")
                                .value("2026-08-30T10:30:00")
                )
                .andExpect(
                        jsonPath("$.sourceReference")
                                .value("CTRL-HISTORY-001")
                )
                .andExpect(
                        jsonPath("$.createdBy")
                                .value(createdBy.toString())
                )
                .andExpect(
                        jsonPath("$.createdAt").exists()
                );
    }

    @Test
    void shouldApplyDefaultEventTimestamp()
            throws Exception {

        UUID customerId =
                createCustomer();

        mockMvc.perform(
                        post(
                                "/api/v1/customers/{customerId}/history",
                                customerId
                        )
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        validHistoryBody(
                                                "STATUS_CHANGE"
                                        )
                                )
                )
                .andExpect(status().isCreated())
                .andExpect(
                        jsonPath("$.eventTimestamp").exists()
                )
                .andExpect(
                        jsonPath("$.createdAt").exists()
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
                  "eventType": ""
                }
                """;

        mockMvc.perform(
                        post(
                                "/api/v1/customers/{customerId}/history",
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
                        jsonPath("$.validationErrors.eventType")
                                .exists()
                );
    }

    @Test
    void shouldReturnNotFoundWhenCreatingHistoryForUnknownCustomer()
            throws Exception {

        UUID unknownCustomerId =
                UUID.randomUUID();

        mockMvc.perform(
                        post(
                                "/api/v1/customers/{customerId}/history",
                                unknownCustomerId
                        )
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        validHistoryBody(
                                                "STATUS_CHANGE"
                                        )
                                )
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldGetHistoryById()
            throws Exception {

        UUID customerId =
                createCustomer();

        JsonNode created =
                createHistory(
                        customerId,
                        "PROFILE_CHANGE",
                        "2026-08-30T10:00:00"
                );

        UUID historyId =
                UUID.fromString(
                        created.get(
                                "customerHistoryId"
                        ).asText()
                );

        mockMvc.perform(
                        get(
                                "/api/v1/customers/{customerId}/history/{historyId}",
                                customerId,
                                historyId
                        )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.customerHistoryId")
                                .value(historyId.toString())
                )
                .andExpect(
                        jsonPath("$.customerId")
                                .value(customerId.toString())
                )
                .andExpect(
                        jsonPath("$.eventType")
                                .value("PROFILE_CHANGE")
                );
    }

    @Test
    void shouldReturnNotFoundForUnknownHistory()
            throws Exception {

        UUID customerId =
                createCustomer();

        UUID unknownHistoryId =
                UUID.randomUUID();

        mockMvc.perform(
                        get(
                                "/api/v1/customers/{customerId}/history/{historyId}",
                                customerId,
                                unknownHistoryId
                        )
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnNotFoundWhenHistoryBelongsToDifferentCustomer()
            throws Exception {

        UUID ownerCustomerId =
                createCustomer();

        UUID otherCustomerId =
                createCustomer();

        JsonNode created =
                createHistory(
                        ownerCustomerId,
                        "RISK_CHANGE",
                        "2026-08-30T10:00:00"
                );

        UUID historyId =
                UUID.fromString(
                        created.get(
                                "customerHistoryId"
                        ).asText()
                );

        mockMvc.perform(
                        get(
                                "/api/v1/customers/{customerId}/history/{historyId}",
                                otherCustomerId,
                                historyId
                        )
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldGetHistoryByCustomerOrderedByEventTimestampDescending()
            throws Exception {

        UUID customerId =
                createCustomer();

        JsonNode older =
                createHistory(
                        customerId,
                        "OLDER_EVENT",
                        "2026-08-30T09:00:00"
                );

        JsonNode newer =
                createHistory(
                        customerId,
                        "NEWER_EVENT",
                        "2026-08-30T11:00:00"
                );

        mockMvc.perform(
                        get(
                                "/api/v1/customers/{customerId}/history",
                                customerId
                        )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$").isArray()
                )
                .andExpect(
                        jsonPath("$[0].customerHistoryId")
                                .value(
                                        newer.get(
                                                "customerHistoryId"
                                        ).asText()
                                )
                )
                .andExpect(
                        jsonPath("$[0].eventType")
                                .value("NEWER_EVENT")
                )
                .andExpect(
                        jsonPath("$[1].customerHistoryId")
                                .value(
                                        older.get(
                                                "customerHistoryId"
                                        ).asText()
                                )
                )
                .andExpect(
                        jsonPath("$[1].eventType")
                                .value("OLDER_EVENT")
                );
    }

    @Test
    void shouldRejectHistoryOperationsForSoftDeletedCustomer()
            throws Exception {

        UUID customerId =
                createCustomer();

        mockMvc.perform(
                        delete(
                                "/api/v1/customers/{customerId}",
                                customerId
                        )
                )
                .andExpect(status().isNoContent());

        mockMvc.perform(
                        post(
                                "/api/v1/customers/{customerId}/history",
                                customerId
                        )
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        validHistoryBody(
                                                "STATUS_CHANGE"
                                        )
                                )
                )
                .andExpect(status().isNotFound());

        mockMvc.perform(
                        get(
                                "/api/v1/customers/{customerId}/history",
                                customerId
                        )
                )
                .andExpect(status().isNotFound());
    }

    private UUID createCustomer()
            throws Exception {

        String customerNumber =
                "CHIST-CTRL-" + UUID.randomUUID();

        String requestBody =
                """
                {
                  "customerNumber": "%s",
                  "customerType": "INDIVIDUAL",
                  "firstName": "History",
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
                        .andExpect(status().isCreated())
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

    private JsonNode createHistory(
            UUID customerId,
            String eventType,
            String eventTimestamp)
            throws Exception {

        String requestBody =
                """
                {
                  "eventType": "%s",
                  "eventTimestamp": "%s"
                }
                """.formatted(
                        eventType,
                        eventTimestamp
                );

        MvcResult result =
                mockMvc.perform(
                                post(
                                        "/api/v1/customers/{customerId}/history",
                                        customerId
                                )
                                        .contentType(
                                                MediaType.APPLICATION_JSON
                                        )
                                        .content(requestBody)
                        )
                        .andExpect(status().isCreated())
                        .andReturn();

        return objectMapper.readTree(
                result.getResponse()
                        .getContentAsString()
        );
    }

    private String validHistoryBody(
            String eventType) {

        return """
                {
                  "eventType": "%s"
                }
                """.formatted(
                eventType
        );
    }
}