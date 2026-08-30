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
class CustomerEmailControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldCreateEmail() throws Exception {

        UUID customerId =
                createCustomer();

        UUID createdBy =
                UUID.randomUUID();

        String emailAddress =
                newEmailAddress();

        String requestBody =
                """
                {
                  "emailType": "PERSONAL",
                  "emailAddress": "%s",
                  "primary": true,
                  "verified": true,
                  "createdBy": "%s"
                }
                """.formatted(
                        emailAddress,
                        createdBy
                );

        mockMvc.perform(
                        post(
                                "/api/v1/customers/{customerId}/emails",
                                customerId
                        )
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isCreated())
                .andExpect(
                        jsonPath("$.customerEmailId").exists()
                )
                .andExpect(
                        jsonPath("$.customerId")
                                .value(customerId.toString())
                )
                .andExpect(
                        jsonPath("$.emailType")
                                .value("PERSONAL")
                )
                .andExpect(
                        jsonPath("$.emailAddress")
                                .value(emailAddress)
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
                                "/api/v1/customers/{customerId}/emails",
                                customerId
                        )
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        validEmailBody(
                                                "PERSONAL",
                                                newEmailAddress()
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
                  "emailType": "",
                  "emailAddress": "invalid-email"
                }
                """;

        mockMvc.perform(
                        post(
                                "/api/v1/customers/{customerId}/emails",
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
                        jsonPath("$.validationErrors.emailType")
                                .exists()
                )
                .andExpect(
                        jsonPath("$.validationErrors.emailAddress")
                                .exists()
                );
    }

    @Test
    void shouldReturnNotFoundWhenCreatingEmailForUnknownCustomer()
            throws Exception {

        UUID unknownCustomerId =
                UUID.randomUUID();

        mockMvc.perform(
                        post(
                                "/api/v1/customers/{customerId}/emails",
                                unknownCustomerId
                        )
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        validEmailBody(
                                                "PERSONAL",
                                                newEmailAddress()
                                        )
                                )
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldGetEmailById()
            throws Exception {

        UUID customerId =
                createCustomer();

        String emailAddress =
                newEmailAddress();

        JsonNode created =
                createEmail(
                        customerId,
                        "PERSONAL",
                        emailAddress
                );

        UUID emailId =
                UUID.fromString(
                        created.get(
                                "customerEmailId"
                        ).asText()
                );

        mockMvc.perform(
                        get(
                                "/api/v1/customers/{customerId}/emails/{emailId}",
                                customerId,
                                emailId
                        )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.customerEmailId")
                                .value(emailId.toString())
                )
                .andExpect(
                        jsonPath("$.customerId")
                                .value(customerId.toString())
                )
                .andExpect(
                        jsonPath("$.emailAddress")
                                .value(emailAddress)
                );
    }

    @Test
    void shouldReturnNotFoundWhenEmailBelongsToDifferentCustomer()
            throws Exception {

        UUID ownerCustomerId =
                createCustomer();

        UUID otherCustomerId =
                createCustomer();

        JsonNode created =
                createEmail(
                        ownerCustomerId,
                        "PERSONAL",
                        newEmailAddress()
                );

        UUID emailId =
                UUID.fromString(
                        created.get(
                                "customerEmailId"
                        ).asText()
                );

        mockMvc.perform(
                        get(
                                "/api/v1/customers/{customerId}/emails/{emailId}",
                                otherCustomerId,
                                emailId
                        )
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldGetEmailsByCustomer()
            throws Exception {

        UUID customerId =
                createCustomer();

        JsonNode first =
                createEmail(
                        customerId,
                        "PERSONAL",
                        newEmailAddress()
                );

        JsonNode second =
                createEmail(
                        customerId,
                        "WORK",
                        newEmailAddress()
                );

        mockMvc.perform(
                        get(
                                "/api/v1/customers/{customerId}/emails",
                                customerId
                        )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$").isArray()
                )
                .andExpect(
                        jsonPath("$[*].customerEmailId")
                                .value(
                                        hasItem(
                                                first.get(
                                                        "customerEmailId"
                                                ).asText()
                                        )
                                )
                )
                .andExpect(
                        jsonPath("$[*].customerEmailId")
                                .value(
                                        hasItem(
                                                second.get(
                                                        "customerEmailId"
                                                ).asText()
                                        )
                                )
                );
    }

    @Test
    void shouldUpdateEmail()
            throws Exception {

        UUID customerId =
                createCustomer();

        JsonNode created =
                createEmail(
                        customerId,
                        "PERSONAL",
                        newEmailAddress()
                );

        UUID emailId =
                UUID.fromString(
                        created.get(
                                "customerEmailId"
                        ).asText()
                );

        UUID updatedBy =
                UUID.randomUUID();

        String updatedEmailAddress =
                newEmailAddress();

        String requestBody =
                """
                {
                  "emailType": "WORK",
                  "emailAddress": "%s",
                  "primary": true,
                  "verified": true,
                  "updatedBy": "%s"
                }
                """.formatted(
                        updatedEmailAddress,
                        updatedBy
                );

        mockMvc.perform(
                        put(
                                "/api/v1/customers/{customerId}/emails/{emailId}",
                                customerId,
                                emailId
                        )
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.customerEmailId")
                                .value(emailId.toString())
                )
                .andExpect(
                        jsonPath("$.customerId")
                                .value(customerId.toString())
                )
                .andExpect(
                        jsonPath("$.emailType")
                                .value("WORK")
                )
                .andExpect(
                        jsonPath("$.emailAddress")
                                .value(updatedEmailAddress)
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
                createEmail(
                        ownerCustomerId,
                        "PERSONAL",
                        newEmailAddress()
                );

        UUID emailId =
                UUID.fromString(
                        created.get(
                                "customerEmailId"
                        ).asText()
                );

        mockMvc.perform(
                        put(
                                "/api/v1/customers/{customerId}/emails/{emailId}",
                                otherCustomerId,
                                emailId
                        )
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        validEmailBody(
                                                "WORK",
                                                newEmailAddress()
                                        )
                                )
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldDeleteEmailAndHideItFromReads()
            throws Exception {

        UUID customerId =
                createCustomer();

        JsonNode created =
                createEmail(
                        customerId,
                        "PERSONAL",
                        newEmailAddress()
                );

        UUID emailId =
                UUID.fromString(
                        created.get(
                                "customerEmailId"
                        ).asText()
                );

        UUID deletedBy =
                UUID.randomUUID();

        mockMvc.perform(
                        delete(
                                "/api/v1/customers/{customerId}/emails/{emailId}",
                                customerId,
                                emailId
                        )
                                .param(
                                        "deletedBy",
                                        deletedBy.toString()
                                )
                )
                .andExpect(status().isNoContent());

        mockMvc.perform(
                        get(
                                "/api/v1/customers/{customerId}/emails/{emailId}",
                                customerId,
                                emailId
                        )
                )
                .andExpect(status().isNotFound());

        mockMvc.perform(
                        get(
                                "/api/v1/customers/{customerId}/emails",
                                customerId
                        )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$[*].customerEmailId")
                                .value(
                                        not(
                                                hasItem(
                                                        emailId.toString()
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
                createEmail(
                        ownerCustomerId,
                        "PERSONAL",
                        newEmailAddress()
                );

        UUID emailId =
                UUID.fromString(
                        created.get(
                                "customerEmailId"
                        ).asText()
                );

        mockMvc.perform(
                        delete(
                                "/api/v1/customers/{customerId}/emails/{emailId}",
                                otherCustomerId,
                                emailId
                        )
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldRejectEmailOperationsForSoftDeletedCustomer()
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
                                "/api/v1/customers/{customerId}/emails",
                                customerId
                        )
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        validEmailBody(
                                                "PERSONAL",
                                                newEmailAddress()
                                        )
                                )
                )
                .andExpect(status().isNotFound());

        mockMvc.perform(
                        get(
                                "/api/v1/customers/{customerId}/emails",
                                customerId
                        )
                )
                .andExpect(status().isNotFound());
    }

    private UUID createCustomer()
            throws Exception {

        String customerNumber =
                "CEMAIL-CTRL-" + UUID.randomUUID();

        String requestBody =
                """
                {
                  "customerNumber": "%s",
                  "customerType": "INDIVIDUAL",
                  "firstName": "Email",
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

    private JsonNode createEmail(
            UUID customerId,
            String emailType,
            String emailAddress)
            throws Exception {

        MvcResult result =
                mockMvc.perform(
                                post(
                                        "/api/v1/customers/{customerId}/emails",
                                        customerId
                                )
                                        .contentType(
                                                MediaType.APPLICATION_JSON
                                        )
                                        .content(
                                                validEmailBody(
                                                        emailType,
                                                        emailAddress
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

    private String validEmailBody(
            String emailType,
            String emailAddress) {

        return """
                {
                  "emailType": "%s",
                  "emailAddress": "%s"
                }
                """.formatted(
                emailType,
                emailAddress
        );
    }

    private String newEmailAddress() {

        return "ctrl-"
                + UUID.randomUUID()
                + "@example.com";
    }
}