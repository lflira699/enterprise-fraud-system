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
class CustomerWatchlistControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldCreateWatchlist() throws Exception {

        UUID customerId =
                createCustomer();

        UUID createdBy =
                UUID.randomUUID();

        String requestBody =
                """
                {
                  "watchlistType": "SANCTIONS",
                  "watchlistSource": "INTERNAL_TEST_SOURCE",
                  "matchStatus": "POTENTIAL_MATCH",
                  "matchScore": 91.75,
                  "matchedName": "Test Customer Match",
                  "referenceId": "WL-REF-001",
                  "detectedAt": "2026-08-30T12:00:00",
                  "lastCheckedAt": "2026-08-30T12:30:00",
                  "active": false,
                  "createdBy": "%s"
                }
                """.formatted(
                        createdBy
                );

        mockMvc.perform(
                        post(
                                "/api/v1/customers/{customerId}/watchlists",
                                customerId
                        )
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isCreated())
                .andExpect(
                        jsonPath("$.watchlistId").exists()
                )
                .andExpect(
                        jsonPath("$.customerId")
                                .value(customerId.toString())
                )
                .andExpect(
                        jsonPath("$.watchlistType")
                                .value("SANCTIONS")
                )
                .andExpect(
                        jsonPath("$.watchlistSource")
                                .value("INTERNAL_TEST_SOURCE")
                )
                .andExpect(
                        jsonPath("$.matchStatus")
                                .value("POTENTIAL_MATCH")
                )
                .andExpect(
                        jsonPath("$.matchScore")
                                .value(91.75)
                )
                .andExpect(
                        jsonPath("$.matchedName")
                                .value("Test Customer Match")
                )
                .andExpect(
                        jsonPath("$.referenceId")
                                .value("WL-REF-001")
                )
                .andExpect(
                        jsonPath("$.detectedAt")
                                .value("2026-08-30T12:00:00")
                )
                .andExpect(
                        jsonPath("$.lastCheckedAt")
                                .value("2026-08-30T12:30:00")
                )
                .andExpect(
                        jsonPath("$.active")
                                .value(false)
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
    void shouldApplyDefaultDetectedAtAndActiveTrue()
            throws Exception {

        UUID customerId =
                createCustomer();

        mockMvc.perform(
                        post(
                                "/api/v1/customers/{customerId}/watchlists",
                                customerId
                        )
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        validWatchlistBody()
                                )
                )
                .andExpect(status().isCreated())
                .andExpect(
                        jsonPath("$.detectedAt").exists()
                )
                .andExpect(
                        jsonPath("$.active")
                                .value(true)
                )
                .andExpect(
                        jsonPath("$.createdAt").exists()
                )
                .andExpect(
                        jsonPath("$.updatedAt").exists()
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
                  "watchlistType": "",
                  "watchlistSource": "",
                  "matchStatus": ""
                }
                """;

        mockMvc.perform(
                        post(
                                "/api/v1/customers/{customerId}/watchlists",
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
                        jsonPath("$.validationErrors.watchlistType")
                                .exists()
                )
                .andExpect(
                        jsonPath("$.validationErrors.watchlistSource")
                                .exists()
                )
                .andExpect(
                        jsonPath("$.validationErrors.matchStatus")
                                .exists()
                );
    }

    @Test
    void shouldReturnNotFoundWhenCreatingForUnknownCustomer()
            throws Exception {

        UUID unknownCustomerId =
                UUID.randomUUID();

        mockMvc.perform(
                        post(
                                "/api/v1/customers/{customerId}/watchlists",
                                unknownCustomerId
                        )
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        validWatchlistBody()
                                )
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldGetWatchlistById()
            throws Exception {

        UUID customerId =
                createCustomer();

        JsonNode created =
                createWatchlist(customerId);

        UUID watchlistId =
                UUID.fromString(
                        created.get(
                                "watchlistId"
                        ).asText()
                );

        mockMvc.perform(
                        get(
                                "/api/v1/customers/{customerId}/watchlists/{watchlistId}",
                                customerId,
                                watchlistId
                        )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.watchlistId")
                                .value(watchlistId.toString())
                )
                .andExpect(
                        jsonPath("$.customerId")
                                .value(customerId.toString())
                )
                .andExpect(
                        jsonPath("$.watchlistType")
                                .value("SANCTIONS")
                )
                .andExpect(
                        jsonPath("$.watchlistSource")
                                .value("TEST_SOURCE")
                )
                .andExpect(
                        jsonPath("$.matchStatus")
                                .value("POTENTIAL_MATCH")
                );
    }

    @Test
    void shouldReturnNotFoundForUnknownWatchlist()
            throws Exception {

        UUID customerId =
                createCustomer();

        mockMvc.perform(
                        get(
                                "/api/v1/customers/{customerId}/watchlists/{watchlistId}",
                                customerId,
                                UUID.randomUUID()
                        )
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnNotFoundWhenWatchlistBelongsToDifferentCustomer()
            throws Exception {

        UUID ownerCustomerId =
                createCustomer();

        UUID otherCustomerId =
                createCustomer();

        JsonNode created =
                createWatchlist(ownerCustomerId);

        UUID watchlistId =
                UUID.fromString(
                        created.get(
                                "watchlistId"
                        ).asText()
                );

        mockMvc.perform(
                        get(
                                "/api/v1/customers/{customerId}/watchlists/{watchlistId}",
                                otherCustomerId,
                                watchlistId
                        )
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldGetWatchlistsByCustomer()
            throws Exception {

        UUID customerId =
                createCustomer();

        JsonNode first =
                createWatchlist(customerId);

        JsonNode second =
                createWatchlist(customerId);

        mockMvc.perform(
                        get(
                                "/api/v1/customers/{customerId}/watchlists",
                                customerId
                        )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$").isArray()
                )
                .andExpect(
                        jsonPath("$[*].watchlistId")
                                .value(
                                        hasItem(
                                                first.get(
                                                        "watchlistId"
                                                ).asText()
                                        )
                                )
                )
                .andExpect(
                        jsonPath("$[*].watchlistId")
                                .value(
                                        hasItem(
                                                second.get(
                                                        "watchlistId"
                                                ).asText()
                                        )
                                )
                );
    }

    @Test
    void shouldUpdateWatchlist()
            throws Exception {

        UUID customerId =
                createCustomer();

        JsonNode created =
                createWatchlist(customerId);

        UUID watchlistId =
                UUID.fromString(
                        created.get(
                                "watchlistId"
                        ).asText()
                );

        UUID updatedBy =
                UUID.randomUUID();

        String requestBody =
                """
                {
                  "watchlistType": "PEP",
                  "watchlistSource": "UPDATED_TEST_SOURCE",
                  "matchStatus": "CONFIRMED_MATCH",
                  "matchScore": 98.50,
                  "matchedName": "Updated Match",
                  "referenceId": "WL-UPDATED-001",
                  "detectedAt": "2026-08-30T11:00:00",
                  "lastCheckedAt": "2026-08-30T13:00:00",
                  "active": true,
                  "updatedBy": "%s"
                }
                """.formatted(
                        updatedBy
                );

        mockMvc.perform(
                        put(
                                "/api/v1/customers/{customerId}/watchlists/{watchlistId}",
                                customerId,
                                watchlistId
                        )
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.watchlistId")
                                .value(watchlistId.toString())
                )
                .andExpect(
                        jsonPath("$.customerId")
                                .value(customerId.toString())
                )
                .andExpect(
                        jsonPath("$.watchlistType")
                                .value("PEP")
                )
                .andExpect(
                        jsonPath("$.watchlistSource")
                                .value("UPDATED_TEST_SOURCE")
                )
                .andExpect(
                        jsonPath("$.matchStatus")
                                .value("CONFIRMED_MATCH")
                )
                .andExpect(
                        jsonPath("$.matchScore")
                                .value(98.50)
                )
                .andExpect(
                        jsonPath("$.matchedName")
                                .value("Updated Match")
                )
                .andExpect(
                        jsonPath("$.referenceId")
                                .value("WL-UPDATED-001")
                )
                .andExpect(
                        jsonPath("$.lastCheckedAt")
                                .value("2026-08-30T13:00:00")
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
                createWatchlist(ownerCustomerId);

        UUID watchlistId =
                UUID.fromString(
                        created.get(
                                "watchlistId"
                        ).asText()
                );

        mockMvc.perform(
                        put(
                                "/api/v1/customers/{customerId}/watchlists/{watchlistId}",
                                otherCustomerId,
                                watchlistId
                        )
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        validWatchlistBody()
                                )
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldDeleteWatchlistAndHideItFromReads()
            throws Exception {

        UUID customerId =
                createCustomer();

        JsonNode created =
                createWatchlist(customerId);

        UUID watchlistId =
                UUID.fromString(
                        created.get(
                                "watchlistId"
                        ).asText()
                );

        mockMvc.perform(
                        delete(
                                "/api/v1/customers/{customerId}/watchlists/{watchlistId}",
                                customerId,
                                watchlistId
                        )
                )
                .andExpect(status().isNoContent());

        mockMvc.perform(
                        get(
                                "/api/v1/customers/{customerId}/watchlists/{watchlistId}",
                                customerId,
                                watchlistId
                        )
                )
                .andExpect(status().isNotFound());

        mockMvc.perform(
                        get(
                                "/api/v1/customers/{customerId}/watchlists",
                                customerId
                        )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$[*].watchlistId")
                                .value(
                                        not(
                                                hasItem(
                                                        watchlistId.toString()
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
                createWatchlist(ownerCustomerId);

        UUID watchlistId =
                UUID.fromString(
                        created.get(
                                "watchlistId"
                        ).asText()
                );

        mockMvc.perform(
                        delete(
                                "/api/v1/customers/{customerId}/watchlists/{watchlistId}",
                                otherCustomerId,
                                watchlistId
                        )
                )
                .andExpect(status().isNotFound());
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
                .andExpect(status().isNoContent());

        mockMvc.perform(
                        post(
                                "/api/v1/customers/{customerId}/watchlists",
                                customerId
                        )
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        validWatchlistBody()
                                )
                )
                .andExpect(status().isNotFound());

        mockMvc.perform(
                        get(
                                "/api/v1/customers/{customerId}/watchlists",
                                customerId
                        )
                )
                .andExpect(status().isNotFound());
    }

    private UUID createCustomer()
            throws Exception {

        String customerNumber =
                "CWATCH-CTRL-" + UUID.randomUUID();

        String requestBody =
                """
                {
                  "customerNumber": "%s",
                  "customerType": "INDIVIDUAL",
                  "firstName": "Watchlist",
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

    private JsonNode createWatchlist(
            UUID customerId)
            throws Exception {

        MvcResult result =
                mockMvc.perform(
                                post(
                                        "/api/v1/customers/{customerId}/watchlists",
                                        customerId
                                )
                                        .contentType(
                                                MediaType.APPLICATION_JSON
                                        )
                                        .content(
                                                validWatchlistBody()
                                        )
                        )
                        .andExpect(status().isCreated())
                        .andReturn();

        return objectMapper.readTree(
                result.getResponse()
                        .getContentAsString()
        );
    }

    private String validWatchlistBody() {

        return """
                {
                  "watchlistType": "SANCTIONS",
                  "watchlistSource": "TEST_SOURCE",
                  "matchStatus": "POTENTIAL_MATCH"
                }
                """;
    }
}