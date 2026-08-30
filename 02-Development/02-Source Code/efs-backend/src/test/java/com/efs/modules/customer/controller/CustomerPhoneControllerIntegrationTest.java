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
class CustomerPhoneControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldCreatePhone() throws Exception {

        UUID customerId =
                createCustomer();

        UUID createdBy =
                UUID.randomUUID();

        String phoneNumber =
                newPhoneNumber();

        String requestBody =
                """
                {
                  "phoneType": "MOBILE",
                  "countryCode": "+502",
                  "phoneNumber": "%s",
                  "primary": true,
                  "verified": true,
                  "createdBy": "%s"
                }
                """.formatted(
                        phoneNumber,
                        createdBy
                );

        mockMvc.perform(
                        post(
                                "/api/v1/customers/{customerId}/phones",
                                customerId
                        )
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isCreated())
                .andExpect(
                        jsonPath("$.customerPhoneId").exists()
                )
                .andExpect(
                        jsonPath("$.customerId")
                                .value(customerId.toString())
                )
                .andExpect(
                        jsonPath("$.phoneType")
                                .value("MOBILE")
                )
                .andExpect(
                        jsonPath("$.countryCode")
                                .value("+502")
                )
                .andExpect(
                        jsonPath("$.phoneNumber")
                                .value(phoneNumber)
                )
                .andExpect(
                        jsonPath("$.primary")
                                .value(true)
                )
                .andExpect(
                        jsonPath("$.verified")
                                .value(true)
                )
                .andExpect(
                        jsonPath("$.verifiedAt").exists()
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
    void shouldApplyDefaultPrimaryAndVerifiedFalse()
            throws Exception {

        UUID customerId =
                createCustomer();

        mockMvc.perform(
                        post(
                                "/api/v1/customers/{customerId}/phones",
                                customerId
                        )
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        validPhoneBody(
                                                "MOBILE",
                                                newPhoneNumber()
                                        )
                                )
                )
                .andExpect(status().isCreated())
                .andExpect(
                        jsonPath("$.primary")
                                .value(false)
                )
                .andExpect(
                        jsonPath("$.verified")
                                .value(false)
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
                  "phoneType": "",
                  "phoneNumber": ""
                }
                """;

        mockMvc.perform(
                        post(
                                "/api/v1/customers/{customerId}/phones",
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
                        jsonPath("$.validationErrors.phoneType")
                                .exists()
                )
                .andExpect(
                        jsonPath("$.validationErrors.phoneNumber")
                                .exists()
                );
    }

    @Test
    void shouldReturnNotFoundWhenCreatingPhoneForUnknownCustomer()
            throws Exception {

        UUID unknownCustomerId =
                UUID.randomUUID();

        mockMvc.perform(
                        post(
                                "/api/v1/customers/{customerId}/phones",
                                unknownCustomerId
                        )
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        validPhoneBody(
                                                "MOBILE",
                                                newPhoneNumber()
                                        )
                                )
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldGetPhoneById()
            throws Exception {

        UUID customerId =
                createCustomer();

        String phoneNumber =
                newPhoneNumber();

        JsonNode created =
                createPhone(
                        customerId,
                        "MOBILE",
                        phoneNumber
                );

        UUID phoneId =
                UUID.fromString(
                        created.get(
                                "customerPhoneId"
                        ).asText()
                );

        mockMvc.perform(
                        get(
                                "/api/v1/customers/{customerId}/phones/{phoneId}",
                                customerId,
                                phoneId
                        )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.customerPhoneId")
                                .value(phoneId.toString())
                )
                .andExpect(
                        jsonPath("$.customerId")
                                .value(customerId.toString())
                )
                .andExpect(
                        jsonPath("$.phoneNumber")
                                .value(phoneNumber)
                );
    }

    @Test
    void shouldReturnNotFoundWhenPhoneBelongsToDifferentCustomer()
            throws Exception {

        UUID ownerCustomerId =
                createCustomer();

        UUID otherCustomerId =
                createCustomer();

        JsonNode created =
                createPhone(
                        ownerCustomerId,
                        "MOBILE",
                        newPhoneNumber()
                );

        UUID phoneId =
                UUID.fromString(
                        created.get(
                                "customerPhoneId"
                        ).asText()
                );

        mockMvc.perform(
                        get(
                                "/api/v1/customers/{customerId}/phones/{phoneId}",
                                otherCustomerId,
                                phoneId
                        )
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldGetPhonesByCustomer()
            throws Exception {

        UUID customerId =
                createCustomer();

        JsonNode first =
                createPhone(
                        customerId,
                        "MOBILE",
                        newPhoneNumber()
                );

        JsonNode second =
                createPhone(
                        customerId,
                        "HOME",
                        newPhoneNumber()
                );

        mockMvc.perform(
                        get(
                                "/api/v1/customers/{customerId}/phones",
                                customerId
                        )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$").isArray()
                )
                .andExpect(
                        jsonPath("$[*].customerPhoneId")
                                .value(
                                        hasItem(
                                                first.get(
                                                        "customerPhoneId"
                                                ).asText()
                                        )
                                )
                )
                .andExpect(
                        jsonPath("$[*].customerPhoneId")
                                .value(
                                        hasItem(
                                                second.get(
                                                        "customerPhoneId"
                                                ).asText()
                                        )
                                )
                );
    }

    @Test
    void shouldUpdatePhone()
            throws Exception {

        UUID customerId =
                createCustomer();

        JsonNode created =
                createPhone(
                        customerId,
                        "MOBILE",
                        newPhoneNumber()
                );

        UUID phoneId =
                UUID.fromString(
                        created.get(
                                "customerPhoneId"
                        ).asText()
                );

        UUID updatedBy =
                UUID.randomUUID();

        String updatedPhoneNumber =
                newPhoneNumber();

        String requestBody =
                """
                {
                  "phoneType": "WORK",
                  "countryCode": "+502",
                  "phoneNumber": "%s",
                  "primary": true,
                  "verified": true,
                  "updatedBy": "%s"
                }
                """.formatted(
                        updatedPhoneNumber,
                        updatedBy
                );

        mockMvc.perform(
                        put(
                                "/api/v1/customers/{customerId}/phones/{phoneId}",
                                customerId,
                                phoneId
                        )
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.customerPhoneId")
                                .value(phoneId.toString())
                )
                .andExpect(
                        jsonPath("$.customerId")
                                .value(customerId.toString())
                )
                .andExpect(
                        jsonPath("$.phoneType")
                                .value("WORK")
                )
                .andExpect(
                        jsonPath("$.phoneNumber")
                                .value(updatedPhoneNumber)
                )
                .andExpect(
                        jsonPath("$.primary")
                                .value(true)
                )
                .andExpect(
                        jsonPath("$.verified")
                                .value(true)
                )
                .andExpect(
                        jsonPath("$.verifiedAt").exists()
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
                createPhone(
                        ownerCustomerId,
                        "MOBILE",
                        newPhoneNumber()
                );

        UUID phoneId =
                UUID.fromString(
                        created.get(
                                "customerPhoneId"
                        ).asText()
                );

        mockMvc.perform(
                        put(
                                "/api/v1/customers/{customerId}/phones/{phoneId}",
                                otherCustomerId,
                                phoneId
                        )
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        validPhoneBody(
                                                "WORK",
                                                newPhoneNumber()
                                        )
                                )
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldDeletePhoneAndHideItFromReads()
            throws Exception {

        UUID customerId =
                createCustomer();

        JsonNode created =
                createPhone(
                        customerId,
                        "MOBILE",
                        newPhoneNumber()
                );

        UUID phoneId =
                UUID.fromString(
                        created.get(
                                "customerPhoneId"
                        ).asText()
                );

        UUID deletedBy =
                UUID.randomUUID();

        mockMvc.perform(
                        delete(
                                "/api/v1/customers/{customerId}/phones/{phoneId}",
                                customerId,
                                phoneId
                        )
                                .param(
                                        "deletedBy",
                                        deletedBy.toString()
                                )
                )
                .andExpect(status().isNoContent());

        mockMvc.perform(
                        get(
                                "/api/v1/customers/{customerId}/phones/{phoneId}",
                                customerId,
                                phoneId
                        )
                )
                .andExpect(status().isNotFound());

        mockMvc.perform(
                        get(
                                "/api/v1/customers/{customerId}/phones",
                                customerId
                        )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$[*].customerPhoneId")
                                .value(
                                        not(
                                                hasItem(
                                                        phoneId.toString()
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
                createPhone(
                        ownerCustomerId,
                        "MOBILE",
                        newPhoneNumber()
                );

        UUID phoneId =
                UUID.fromString(
                        created.get(
                                "customerPhoneId"
                        ).asText()
                );

        mockMvc.perform(
                        delete(
                                "/api/v1/customers/{customerId}/phones/{phoneId}",
                                otherCustomerId,
                                phoneId
                        )
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldRejectPhoneOperationsForSoftDeletedCustomer()
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
                                "/api/v1/customers/{customerId}/phones",
                                customerId
                        )
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        validPhoneBody(
                                                "MOBILE",
                                                newPhoneNumber()
                                        )
                                )
                )
                .andExpect(status().isNotFound());

        mockMvc.perform(
                        get(
                                "/api/v1/customers/{customerId}/phones",
                                customerId
                        )
                )
                .andExpect(status().isNotFound());
    }

    private UUID createCustomer()
            throws Exception {

        String customerNumber =
                "CPHONE-CTRL-" + UUID.randomUUID();

        String requestBody =
                """
                {
                  "customerNumber": "%s",
                  "customerType": "INDIVIDUAL",
                  "firstName": "Phone",
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

    private JsonNode createPhone(
            UUID customerId,
            String phoneType,
            String phoneNumber)
            throws Exception {

        MvcResult result =
                mockMvc.perform(
                                post(
                                        "/api/v1/customers/{customerId}/phones",
                                        customerId
                                )
                                        .contentType(
                                                MediaType.APPLICATION_JSON
                                        )
                                        .content(
                                                validPhoneBody(
                                                        phoneType,
                                                        phoneNumber
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

    private String validPhoneBody(
            String phoneType,
            String phoneNumber) {

        return """
                {
                  "phoneType": "%s",
                  "countryCode": "+502",
                  "phoneNumber": "%s"
                }
                """.formatted(
                phoneType,
                phoneNumber
        );
    }

    private String newPhoneNumber() {

        return "+502"
                + String.format(
                        "%08d",
                        Math.floorMod(
                                UUID.randomUUID().hashCode(),
                                100000000
                        )
                );
    }
}