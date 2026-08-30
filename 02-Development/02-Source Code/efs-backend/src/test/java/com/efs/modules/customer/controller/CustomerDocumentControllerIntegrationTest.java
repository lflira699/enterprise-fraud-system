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
class CustomerDocumentControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldCreateDocument() throws Exception {

        UUID customerId =
                createCustomer();

        UUID verifiedBy =
                UUID.randomUUID();

        String documentNumber =
                newDocumentNumber();

        String requestBody =
                """
                {
                  "documentType": "PASSPORT",
                  "documentNumber": "%s",
                  "issuingCountry": "GTM",
                  "issueDate": "2024-01-15",
                  "expirationDate": "2034-01-15",
                  "verificationStatus": "VERIFIED",
                  "verifiedBy": "%s"
                }
                """.formatted(
                        documentNumber,
                        verifiedBy
                );

        mockMvc.perform(
                        post(
                                "/api/v1/customers/{customerId}/documents",
                                customerId
                        )
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isCreated())
                .andExpect(
                        jsonPath("$.documentId").exists()
                )
                .andExpect(
                        jsonPath("$.customerId")
                                .value(customerId.toString())
                )
                .andExpect(
                        jsonPath("$.documentType")
                                .value("PASSPORT")
                )
                .andExpect(
                        jsonPath("$.documentNumber")
                                .value(documentNumber)
                )
                .andExpect(
                        jsonPath("$.issuingCountry")
                                .value("GTM")
                )
                .andExpect(
                        jsonPath("$.issueDate")
                                .value("2024-01-15")
                )
                .andExpect(
                        jsonPath("$.expirationDate")
                                .value("2034-01-15")
                )
                .andExpect(
                        jsonPath("$.verificationStatus")
                                .value("VERIFIED")
                )
                .andExpect(
                        jsonPath("$.verifiedBy")
                                .value(verifiedBy.toString())
                )
                .andExpect(
                        jsonPath("$.verifiedAt").exists()
                )
                .andExpect(
                        jsonPath("$.createdAt").exists()
                )
                .andExpect(
                        jsonPath("$.updatedAt").exists()
                );
    }

    @Test
    void shouldCreateMinimalDocument()
            throws Exception {

        UUID customerId =
                createCustomer();

        String documentNumber =
                newDocumentNumber();

        mockMvc.perform(
                        post(
                                "/api/v1/customers/{customerId}/documents",
                                customerId
                        )
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        validDocumentBody(
                                                "NATIONAL_ID",
                                                documentNumber
                                        )
                                )
                )
                .andExpect(status().isCreated())
                .andExpect(
                        jsonPath("$.documentType")
                                .value("NATIONAL_ID")
                )
                .andExpect(
                        jsonPath("$.documentNumber")
                                .value(documentNumber)
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
                  "documentType": "",
                  "documentNumber": ""
                }
                """;

        mockMvc.perform(
                        post(
                                "/api/v1/customers/{customerId}/documents",
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
                        jsonPath("$.validationErrors.documentType")
                                .exists()
                )
                .andExpect(
                        jsonPath("$.validationErrors.documentNumber")
                                .exists()
                );
    }

    @Test
    void shouldReturnNotFoundWhenCreatingDocumentForUnknownCustomer()
            throws Exception {

        UUID unknownCustomerId =
                UUID.randomUUID();

        mockMvc.perform(
                        post(
                                "/api/v1/customers/{customerId}/documents",
                                unknownCustomerId
                        )
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        validDocumentBody(
                                                "PASSPORT",
                                                newDocumentNumber()
                                        )
                                )
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldRejectDuplicateDocumentForSameCustomer()
            throws Exception {

        UUID customerId =
                createCustomer();

        String documentNumber =
                newDocumentNumber();

        createDocument(
                customerId,
                "PASSPORT",
                documentNumber
        );

        mockMvc.perform(
                        post(
                                "/api/v1/customers/{customerId}/documents",
                                customerId
                        )
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        validDocumentBody(
                                                "PASSPORT",
                                                documentNumber
                                        )
                                )
                )
                .andExpect(status().isConflict())
                .andExpect(
                        jsonPath("$.errorCode")
                                .value("CUSTOMER_DUPLICATE_RECORD")
                );
    }

    @Test
    void shouldAllowSameDocumentForDifferentCustomers()
            throws Exception {

        UUID firstCustomerId =
                createCustomer();

        UUID secondCustomerId =
                createCustomer();

        String documentNumber =
                newDocumentNumber();

        createDocument(
                firstCustomerId,
                "PASSPORT",
                documentNumber
        );

        mockMvc.perform(
                        post(
                                "/api/v1/customers/{customerId}/documents",
                                secondCustomerId
                        )
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        validDocumentBody(
                                                "PASSPORT",
                                                documentNumber
                                        )
                                )
                )
                .andExpect(status().isCreated())
                .andExpect(
                        jsonPath("$.customerId")
                                .value(secondCustomerId.toString())
                )
                .andExpect(
                        jsonPath("$.documentNumber")
                                .value(documentNumber)
                );
    }

    @Test
    void shouldGetDocumentById()
            throws Exception {

        UUID customerId =
                createCustomer();

        JsonNode created =
                createDocument(
                        customerId,
                        "PASSPORT",
                        newDocumentNumber()
                );

        UUID documentId =
                UUID.fromString(
                        created.get("documentId").asText()
                );

        mockMvc.perform(
                        get(
                                "/api/v1/customers/{customerId}/documents/{documentId}",
                                customerId,
                                documentId
                        )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.documentId")
                                .value(documentId.toString())
                )
                .andExpect(
                        jsonPath("$.customerId")
                                .value(customerId.toString())
                )
                .andExpect(
                        jsonPath("$.documentType")
                                .value("PASSPORT")
                );
    }

    @Test
    void shouldReturnNotFoundForUnknownDocument()
            throws Exception {

        UUID customerId =
                createCustomer();

        UUID unknownDocumentId =
                UUID.randomUUID();

        mockMvc.perform(
                        get(
                                "/api/v1/customers/{customerId}/documents/{documentId}",
                                customerId,
                                unknownDocumentId
                        )
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnNotFoundWhenDocumentBelongsToDifferentCustomer()
            throws Exception {

        UUID ownerCustomerId =
                createCustomer();

        UUID otherCustomerId =
                createCustomer();

        JsonNode created =
                createDocument(
                        ownerCustomerId,
                        "PASSPORT",
                        newDocumentNumber()
                );

        UUID documentId =
                UUID.fromString(
                        created.get("documentId").asText()
                );

        mockMvc.perform(
                        get(
                                "/api/v1/customers/{customerId}/documents/{documentId}",
                                otherCustomerId,
                                documentId
                        )
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldGetDocumentsByCustomer()
            throws Exception {

        UUID customerId =
                createCustomer();

        JsonNode first =
                createDocument(
                        customerId,
                        "PASSPORT",
                        newDocumentNumber()
                );

        JsonNode second =
                createDocument(
                        customerId,
                        "NATIONAL_ID",
                        newDocumentNumber()
                );

        mockMvc.perform(
                        get(
                                "/api/v1/customers/{customerId}/documents",
                                customerId
                        )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$").isArray()
                )
                .andExpect(
                        jsonPath("$[*].documentId")
                                .value(
                                        hasItem(
                                                first.get(
                                                        "documentId"
                                                ).asText()
                                        )
                                )
                )
                .andExpect(
                        jsonPath("$[*].documentId")
                                .value(
                                        hasItem(
                                                second.get(
                                                        "documentId"
                                                ).asText()
                                        )
                                )
                );
    }

    @Test
    void shouldUpdateDocument()
            throws Exception {

        UUID customerId =
                createCustomer();

        JsonNode created =
                createDocument(
                        customerId,
                        "PASSPORT",
                        newDocumentNumber()
                );

        UUID documentId =
                UUID.fromString(
                        created.get("documentId").asText()
                );

        UUID verifiedBy =
                UUID.randomUUID();

        String updatedDocumentNumber =
                newDocumentNumber();

        String requestBody =
                """
                {
                  "documentType": "NATIONAL_ID",
                  "documentNumber": "%s",
                  "issuingCountry": "GTM",
                  "issueDate": "2025-02-01",
                  "expirationDate": "2035-02-01",
                  "verificationStatus": "VERIFIED",
                  "verifiedBy": "%s"
                }
                """.formatted(
                        updatedDocumentNumber,
                        verifiedBy
                );

        mockMvc.perform(
                        put(
                                "/api/v1/customers/{customerId}/documents/{documentId}",
                                customerId,
                                documentId
                        )
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.documentId")
                                .value(documentId.toString())
                )
                .andExpect(
                        jsonPath("$.customerId")
                                .value(customerId.toString())
                )
                .andExpect(
                        jsonPath("$.documentType")
                                .value("NATIONAL_ID")
                )
                .andExpect(
                        jsonPath("$.documentNumber")
                                .value(updatedDocumentNumber)
                )
                .andExpect(
                        jsonPath("$.verificationStatus")
                                .value("VERIFIED")
                )
                .andExpect(
                        jsonPath("$.verifiedBy")
                                .value(verifiedBy.toString())
                )
                .andExpect(
                        jsonPath("$.verifiedAt").exists()
                )
                .andExpect(
                        jsonPath("$.updatedAt").exists()
                );
    }

    @Test
    void shouldRejectDuplicateDocumentOnUpdate()
            throws Exception {

        UUID customerId =
                createCustomer();

        JsonNode first =
                createDocument(
                        customerId,
                        "PASSPORT",
                        newDocumentNumber()
                );

        String secondDocumentNumber =
                newDocumentNumber();

        createDocument(
                customerId,
                "NATIONAL_ID",
                secondDocumentNumber
        );

        UUID firstDocumentId =
                UUID.fromString(
                        first.get("documentId").asText()
                );

        mockMvc.perform(
                        put(
                                "/api/v1/customers/{customerId}/documents/{documentId}",
                                customerId,
                                firstDocumentId
                        )
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        validDocumentBody(
                                                "NATIONAL_ID",
                                                secondDocumentNumber
                                        )
                                )
                )
                .andExpect(status().isConflict())
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
                createDocument(
                        ownerCustomerId,
                        "PASSPORT",
                        newDocumentNumber()
                );

        UUID documentId =
                UUID.fromString(
                        created.get("documentId").asText()
                );

        mockMvc.perform(
                        put(
                                "/api/v1/customers/{customerId}/documents/{documentId}",
                                otherCustomerId,
                                documentId
                        )
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        validDocumentBody(
                                                "NATIONAL_ID",
                                                newDocumentNumber()
                                        )
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
                                "/api/v1/customers/{customerId}/documents",
                                customerId
                        )
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        validDocumentBody(
                                                "PASSPORT",
                                                newDocumentNumber()
                                        )
                                )
                )
                .andExpect(status().isNotFound());

        mockMvc.perform(
                        get(
                                "/api/v1/customers/{customerId}/documents",
                                customerId
                        )
                )
                .andExpect(status().isNotFound());
    }

    private UUID createCustomer()
            throws Exception {

        String customerNumber =
                "CDOC-CTRL-" + UUID.randomUUID();

        String requestBody =
                """
                {
                  "customerNumber": "%s",
                  "customerType": "INDIVIDUAL",
                  "firstName": "Document",
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
                response.get("customerId").asText()
        );
    }

    private JsonNode createDocument(
            UUID customerId,
            String documentType,
            String documentNumber)
            throws Exception {

        MvcResult result =
                mockMvc.perform(
                                post(
                                        "/api/v1/customers/{customerId}/documents",
                                        customerId
                                )
                                        .contentType(
                                                MediaType.APPLICATION_JSON
                                        )
                                        .content(
                                                validDocumentBody(
                                                        documentType,
                                                        documentNumber
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

    private String validDocumentBody(
            String documentType,
            String documentNumber) {

        return """
                {
                  "documentType": "%s",
                  "documentNumber": "%s"
                }
                """.formatted(
                documentType,
                documentNumber
        );
    }

    private String newDocumentNumber() {

        return "DOC-" + UUID.randomUUID();
    }
}