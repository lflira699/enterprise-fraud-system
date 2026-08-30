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
class CustomerRelationshipControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldCreateRelationship() throws Exception {

        UUID customerId = createCustomer();
        UUID relatedCustomerId = createCustomer();
        UUID createdBy = UUID.randomUUID();

        String requestBody =
                """
                {
                  "relatedCustomerId": "%s",
                  "relationshipType": "FAMILY",
                  "relationshipStatus": "ACTIVE",
                  "relationshipDescription": "Immediate family relationship",
                  "effectiveDate": "2026-01-01",
                  "expirationDate": "2027-01-01",
                  "createdBy": "%s"
                }
                """.formatted(
                        relatedCustomerId,
                        createdBy
                );

        mockMvc.perform(
                        post(
                                "/api/v1/customers/{customerId}/relationships",
                                customerId
                        )
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isCreated())
                .andExpect(
                        jsonPath("$.customerRelationshipId").exists()
                )
                .andExpect(
                        jsonPath("$.customerId")
                                .value(customerId.toString())
                )
                .andExpect(
                        jsonPath("$.relatedCustomerId")
                                .value(relatedCustomerId.toString())
                )
                .andExpect(
                        jsonPath("$.relationshipType")
                                .value("FAMILY")
                )
                .andExpect(
                        jsonPath("$.relationshipStatus")
                                .value("ACTIVE")
                )
                .andExpect(
                        jsonPath("$.relationshipDescription")
                                .value("Immediate family relationship")
                )
                .andExpect(
                        jsonPath("$.effectiveDate")
                                .value("2026-01-01")
                )
                .andExpect(
                        jsonPath("$.expirationDate")
                                .value("2027-01-01")
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
    void shouldAllowRelationshipWithoutRelatedCustomer()
            throws Exception {

        UUID customerId = createCustomer();

        String requestBody =
                """
                {
                  "relationshipType": "EXTERNAL_REFERENCE",
                  "relationshipStatus": "ACTIVE",
                  "relationshipDescription": "Relationship without linked customer"
                }
                """;

        mockMvc.perform(
                        post(
                                "/api/v1/customers/{customerId}/relationships",
                                customerId
                        )
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isCreated())
                .andExpect(
                        jsonPath("$.customerRelationshipId").exists()
                )
                .andExpect(
                        jsonPath("$.customerId")
                                .value(customerId.toString())
                )
                .andExpect(
                        jsonPath("$.relationshipType")
                                .value("EXTERNAL_REFERENCE")
                );
    }

    @Test
    void shouldRejectInvalidCreateRequest()
            throws Exception {

        UUID customerId = createCustomer();

        String requestBody =
                """
                {
                  "relationshipType": "",
                  "relationshipStatus": ""
                }
                """;

        mockMvc.perform(
                        post(
                                "/api/v1/customers/{customerId}/relationships",
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
                        jsonPath("$.validationErrors.relationshipType")
                                .exists()
                )
                .andExpect(
                        jsonPath("$.validationErrors.relationshipStatus")
                                .exists()
                );
    }

    @Test
    void shouldReturnNotFoundWhenCreatingForUnknownCustomer()
            throws Exception {

        UUID unknownCustomerId = UUID.randomUUID();

        mockMvc.perform(
                        post(
                                "/api/v1/customers/{customerId}/relationships",
                                unknownCustomerId
                        )
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        validRelationshipBody(null)
                                )
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnNotFoundForUnknownRelatedCustomer()
            throws Exception {

        UUID customerId = createCustomer();
        UUID unknownRelatedCustomerId = UUID.randomUUID();

        mockMvc.perform(
                        post(
                                "/api/v1/customers/{customerId}/relationships",
                                customerId
                        )
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        validRelationshipBody(
                                                unknownRelatedCustomerId
                                        )
                                )
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldRejectSelfRelationship()
            throws Exception {

        UUID customerId = createCustomer();

        mockMvc.perform(
                        post(
                                "/api/v1/customers/{customerId}/relationships",
                                customerId
                        )
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        validRelationshipBody(
                                                customerId
                                        )
                                )
                )
                .andExpect(status().isUnprocessableEntity())
                .andExpect(
                        jsonPath("$.errorCode")
                                .value("BUSINESS_VALIDATION_ERROR")
                );
    }

    @Test
    void shouldRejectExpirationBeforeEffectiveDate()
            throws Exception {

        UUID customerId = createCustomer();
        UUID relatedCustomerId = createCustomer();

        String requestBody =
                """
                {
                  "relatedCustomerId": "%s",
                  "relationshipType": "FAMILY",
                  "relationshipStatus": "ACTIVE",
                  "effectiveDate": "2026-08-30",
                  "expirationDate": "2026-08-29"
                }
                """.formatted(
                        relatedCustomerId
                );

        mockMvc.perform(
                        post(
                                "/api/v1/customers/{customerId}/relationships",
                                customerId
                        )
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isUnprocessableEntity())
                .andExpect(
                        jsonPath("$.errorCode")
                                .value("BUSINESS_VALIDATION_ERROR")
                );
    }

    @Test
    void shouldGetRelationshipById()
            throws Exception {

        UUID customerId = createCustomer();
        UUID relatedCustomerId = createCustomer();

        JsonNode created =
                createRelationship(
                        customerId,
                        relatedCustomerId
                );

        UUID relationshipId =
                UUID.fromString(
                        created.get(
                                "customerRelationshipId"
                        ).asText()
                );

        mockMvc.perform(
                        get(
                                "/api/v1/customers/{customerId}/relationships/{relationshipId}",
                                customerId,
                                relationshipId
                        )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.customerRelationshipId")
                                .value(relationshipId.toString())
                )
                .andExpect(
                        jsonPath("$.customerId")
                                .value(customerId.toString())
                )
                .andExpect(
                        jsonPath("$.relatedCustomerId")
                                .value(relatedCustomerId.toString())
                );
    }

    @Test
    void shouldReturnNotFoundForUnknownRelationship()
            throws Exception {

        UUID customerId = createCustomer();

        mockMvc.perform(
                        get(
                                "/api/v1/customers/{customerId}/relationships/{relationshipId}",
                                customerId,
                                UUID.randomUUID()
                        )
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnNotFoundWhenRelationshipBelongsToDifferentCustomer()
            throws Exception {

        UUID ownerCustomerId = createCustomer();
        UUID otherCustomerId = createCustomer();
        UUID relatedCustomerId = createCustomer();

        JsonNode created =
                createRelationship(
                        ownerCustomerId,
                        relatedCustomerId
                );

        UUID relationshipId =
                UUID.fromString(
                        created.get(
                                "customerRelationshipId"
                        ).asText()
                );

        mockMvc.perform(
                        get(
                                "/api/v1/customers/{customerId}/relationships/{relationshipId}",
                                otherCustomerId,
                                relationshipId
                        )
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldGetRelationshipsByCustomer()
            throws Exception {

        UUID customerId = createCustomer();

        JsonNode first =
                createRelationship(
                        customerId,
                        createCustomer()
                );

        JsonNode second =
                createRelationship(
                        customerId,
                        createCustomer()
                );

        mockMvc.perform(
                        get(
                                "/api/v1/customers/{customerId}/relationships",
                                customerId
                        )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$").isArray()
                )
                .andExpect(
                        jsonPath("$[*].customerRelationshipId")
                                .value(
                                        hasItem(
                                                first.get(
                                                        "customerRelationshipId"
                                                ).asText()
                                        )
                                )
                )
                .andExpect(
                        jsonPath("$[*].customerRelationshipId")
                                .value(
                                        hasItem(
                                                second.get(
                                                        "customerRelationshipId"
                                                ).asText()
                                        )
                                )
                );
    }

    @Test
    void shouldUpdateRelationship()
            throws Exception {

        UUID customerId = createCustomer();
        UUID originalRelatedCustomerId = createCustomer();
        UUID newRelatedCustomerId = createCustomer();

        JsonNode created =
                createRelationship(
                        customerId,
                        originalRelatedCustomerId
                );

        UUID relationshipId =
                UUID.fromString(
                        created.get(
                                "customerRelationshipId"
                        ).asText()
                );

        UUID updatedBy = UUID.randomUUID();

        String requestBody =
                """
                {
                  "relatedCustomerId": "%s",
                  "relationshipType": "BUSINESS",
                  "relationshipStatus": "INACTIVE",
                  "relationshipDescription": "Updated relationship",
                  "effectiveDate": "2026-02-01",
                  "expirationDate": "2027-02-01",
                  "updatedBy": "%s"
                }
                """.formatted(
                        newRelatedCustomerId,
                        updatedBy
                );

        mockMvc.perform(
                        put(
                                "/api/v1/customers/{customerId}/relationships/{relationshipId}",
                                customerId,
                                relationshipId
                        )
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.customerRelationshipId")
                                .value(relationshipId.toString())
                )
                .andExpect(
                        jsonPath("$.customerId")
                                .value(customerId.toString())
                )
                .andExpect(
                        jsonPath("$.relatedCustomerId")
                                .value(newRelatedCustomerId.toString())
                )
                .andExpect(
                        jsonPath("$.relationshipType")
                                .value("BUSINESS")
                )
                .andExpect(
                        jsonPath("$.relationshipStatus")
                                .value("INACTIVE")
                )
                .andExpect(
                        jsonPath("$.relationshipDescription")
                                .value("Updated relationship")
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

        UUID ownerCustomerId = createCustomer();
        UUID otherCustomerId = createCustomer();

        JsonNode created =
                createRelationship(
                        ownerCustomerId,
                        createCustomer()
                );

        UUID relationshipId =
                UUID.fromString(
                        created.get(
                                "customerRelationshipId"
                        ).asText()
                );

        mockMvc.perform(
                        put(
                                "/api/v1/customers/{customerId}/relationships/{relationshipId}",
                                otherCustomerId,
                                relationshipId
                        )
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        validRelationshipBody(
                                                createCustomer()
                                        )
                                )
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldDeleteRelationshipAndHideItFromReads()
            throws Exception {

        UUID customerId = createCustomer();

        JsonNode created =
                createRelationship(
                        customerId,
                        createCustomer()
                );

        UUID relationshipId =
                UUID.fromString(
                        created.get(
                                "customerRelationshipId"
                        ).asText()
                );

        UUID deletedBy = UUID.randomUUID();

        mockMvc.perform(
                        delete(
                                "/api/v1/customers/{customerId}/relationships/{relationshipId}",
                                customerId,
                                relationshipId
                        )
                                .param(
                                        "deletedBy",
                                        deletedBy.toString()
                                )
                )
                .andExpect(status().isNoContent());

        mockMvc.perform(
                        get(
                                "/api/v1/customers/{customerId}/relationships/{relationshipId}",
                                customerId,
                                relationshipId
                        )
                )
                .andExpect(status().isNotFound());

        mockMvc.perform(
                        get(
                                "/api/v1/customers/{customerId}/relationships",
                                customerId
                        )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$[*].customerRelationshipId")
                                .value(
                                        not(
                                                hasItem(
                                                        relationshipId.toString()
                                                )
                                        )
                                )
                );
    }

    @Test
    void shouldRejectDeleteThroughDifferentCustomer()
            throws Exception {

        UUID ownerCustomerId = createCustomer();
        UUID otherCustomerId = createCustomer();

        JsonNode created =
                createRelationship(
                        ownerCustomerId,
                        createCustomer()
                );

        UUID relationshipId =
                UUID.fromString(
                        created.get(
                                "customerRelationshipId"
                        ).asText()
                );

        mockMvc.perform(
                        delete(
                                "/api/v1/customers/{customerId}/relationships/{relationshipId}",
                                otherCustomerId,
                                relationshipId
                        )
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldRejectRelationshipOperationsForSoftDeletedCustomer()
            throws Exception {

        UUID customerId = createCustomer();

        mockMvc.perform(
                        delete(
                                "/api/v1/customers/{customerId}",
                                customerId
                        )
                )
                .andExpect(status().isNoContent());

        mockMvc.perform(
                        post(
                                "/api/v1/customers/{customerId}/relationships",
                                customerId
                        )
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        validRelationshipBody(null)
                                )
                )
                .andExpect(status().isNotFound());

        mockMvc.perform(
                        get(
                                "/api/v1/customers/{customerId}/relationships",
                                customerId
                        )
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldRejectSoftDeletedRelatedCustomer()
            throws Exception {

        UUID customerId = createCustomer();
        UUID relatedCustomerId = createCustomer();

        mockMvc.perform(
                        delete(
                                "/api/v1/customers/{customerId}",
                                relatedCustomerId
                        )
                )
                .andExpect(status().isNoContent());

        mockMvc.perform(
                        post(
                                "/api/v1/customers/{customerId}/relationships",
                                customerId
                        )
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        validRelationshipBody(
                                                relatedCustomerId
                                        )
                                )
                )
                .andExpect(status().isNotFound());
    }

    private UUID createCustomer()
            throws Exception {

        String customerNumber =
                "CREL-CTRL-" + UUID.randomUUID();

        String requestBody =
                """
                {
                  "customerNumber": "%s",
                  "customerType": "INDIVIDUAL",
                  "firstName": "Relationship",
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

    private JsonNode createRelationship(
            UUID customerId,
            UUID relatedCustomerId)
            throws Exception {

        MvcResult result =
                mockMvc.perform(
                                post(
                                        "/api/v1/customers/{customerId}/relationships",
                                        customerId
                                )
                                        .contentType(
                                                MediaType.APPLICATION_JSON
                                        )
                                        .content(
                                                validRelationshipBody(
                                                        relatedCustomerId
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

    private String validRelationshipBody(
            UUID relatedCustomerId) {

        if (relatedCustomerId == null) {
            return """
                    {
                      "relationshipType": "FAMILY",
                      "relationshipStatus": "ACTIVE"
                    }
                    """;
        }

        return """
                {
                  "relatedCustomerId": "%s",
                  "relationshipType": "FAMILY",
                  "relationshipStatus": "ACTIVE"
                }
                """.formatted(
                relatedCustomerId
        );
    }
}