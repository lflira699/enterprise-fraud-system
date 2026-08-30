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
class CustomerControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldCreateCustomer() throws Exception {

        String customerNumber =
                newCustomerNumber();

        String requestBody =
                """
                {
                  "customerNumber": "%s",
                  "customerType": "INDIVIDUAL",
                  "firstName": "Fernando",
                  "middleName": "Integration",
                  "lastName": "Customer",
                  "secondLastName": "Test",
                  "legalName": "Fernando Integration Customer Test",
                  "riskLevel": "MEDIUM",
                  "riskScore": 35.50,
                  "customerStatus": "ACTIVE"
                }
                """.formatted(
                        customerNumber
                );

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
                .andExpect(
                        jsonPath("$.customerId").exists()
                )
                .andExpect(
                        jsonPath("$.customerNumber")
                                .value(customerNumber)
                )
                .andExpect(
                        jsonPath("$.customerType")
                                .value("INDIVIDUAL")
                )
                .andExpect(
                        jsonPath("$.firstName")
                                .value("Fernando")
                )
                .andExpect(
                        jsonPath("$.middleName")
                                .value("Integration")
                )
                .andExpect(
                        jsonPath("$.lastName")
                                .value("Customer")
                )
                .andExpect(
                        jsonPath("$.secondLastName")
                                .value("Test")
                )
                .andExpect(
                        jsonPath("$.legalName")
                                .value(
                                        "Fernando Integration Customer Test"
                                )
                )
                .andExpect(
                        jsonPath("$.riskLevel")
                                .value("MEDIUM")
                )
                .andExpect(
                        jsonPath("$.riskScore")
                                .value(35.50)
                )
                .andExpect(
                        jsonPath("$.customerStatus")
                                .value("ACTIVE")
                )
                .andExpect(
                        jsonPath("$.createdAt").exists()
                )
                .andExpect(
                        jsonPath("$.updatedAt").exists()
                )
                .andExpect(
                        jsonPath("$.recordStatus")
                                .value("ACTIVE")
                )
                .andExpect(
                        jsonPath("$.recordVersion").exists()
                );
    }

    @Test
    void shouldApplyCustomerDefaultsWhenOptionalRiskValuesAreNotProvided()
            throws Exception {

        String customerNumber =
                newCustomerNumber();

        String requestBody =
                """
                {
                  "customerNumber": "%s",
                  "customerType": "INDIVIDUAL",
                  "firstName": "Default",
                  "lastName": "Customer"
                }
                """.formatted(
                        customerNumber
                );

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
                .andExpect(
                        jsonPath("$.customerNumber")
                                .value(customerNumber)
                )
                .andExpect(
                        jsonPath("$.riskLevel")
                                .value("LOW")
                )
                .andExpect(
                        jsonPath("$.riskScore")
                                .value(0)
                )
                .andExpect(
                        jsonPath("$.customerStatus")
                                .value("ACTIVE")
                )
                .andExpect(
                        jsonPath("$.recordStatus")
                                .value("ACTIVE")
                );
    }

    @Test
    void shouldRejectInvalidCreateRequest()
            throws Exception {

        String requestBody =
                """
                {
                  "customerNumber": "",
                  "customerType": ""
                }
                """;

        mockMvc.perform(
                        post("/api/v1/customers")
                                .header(
                                        "X-Correlation-ID",
                                        "CUSTOMER-VALIDATION-TEST"
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
                        jsonPath("$.status")
                                .value(400)
                )
                .andExpect(
                        jsonPath("$.errorCode")
                                .value("VALIDATION_ERROR")
                )
                .andExpect(
                        jsonPath("$.message")
                                .value(
                                        "Request validation failed"
                                )
                )
                .andExpect(
                        jsonPath("$.correlationId")
                                .value(
                                        "CUSTOMER-VALIDATION-TEST"
                                )
                )
                .andExpect(
                        jsonPath("$.path")
                                .value(
                                        "/api/v1/customers"
                                )
                )
                .andExpect(
                        jsonPath(
                                "$.validationErrors.customerNumber"
                        ).exists()
                )
                .andExpect(
                        jsonPath(
                                "$.validationErrors.customerType"
                        ).exists()
                );
    }

    @Test
    void shouldRejectDuplicateCustomerNumber()
            throws Exception {

        String customerNumber =
                newCustomerNumber();

        String requestBody =
                validRequestBody(
                        customerNumber,
                        "Duplicate",
                        "Customer"
                );

        mockMvc.perform(
                        post("/api/v1/customers")
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(requestBody)
                )
                .andExpect(
                        status().isCreated()
                );

        mockMvc.perform(
                        post("/api/v1/customers")
                                .header(
                                        "X-Correlation-ID",
                                        "CUSTOMER-DUPLICATE-TEST"
                                )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(requestBody)
                )
                .andExpect(
                        status().isConflict()
                )
                .andExpect(
                        jsonPath("$.status")
                                .value(409)
                )
                .andExpect(
                        jsonPath("$.errorCode")
                                .value(
                                        "CUSTOMER_DUPLICATE_RECORD"
                                )
                )
                .andExpect(
                        jsonPath("$.correlationId")
                                .value(
                                        "CUSTOMER-DUPLICATE-TEST"
                                )
                )
                .andExpect(
                        jsonPath("$.path")
                                .value(
                                        "/api/v1/customers"
                                )
                );
    }

    @Test
    void shouldGetCustomerById()
            throws Exception {

        JsonNode created =
                createCustomer(
                        newCustomerNumber(),
                        "GetById",
                        "Customer"
                );

        String customerId =
                created.get(
                        "customerId"
                ).asText();

        mockMvc.perform(
                        get(
                                "/api/v1/customers/{customerId}",
                                UUID.fromString(customerId)
                        )
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath("$.customerId")
                                .value(customerId)
                )
                .andExpect(
                        jsonPath("$.customerNumber")
                                .value(
                                        created.get(
                                                "customerNumber"
                                        ).asText()
                                )
                )
                .andExpect(
                        jsonPath("$.firstName")
                                .value("GetById")
                )
                .andExpect(
                        jsonPath("$.lastName")
                                .value("Customer")
                );
    }

    @Test
    void shouldGetCustomerByNumber()
            throws Exception {

        String customerNumber =
                newCustomerNumber();

        JsonNode created =
                createCustomer(
                        customerNumber,
                        "GetByNumber",
                        "Customer"
                );

        mockMvc.perform(
                        get(
                                "/api/v1/customers/number/{customerNumber}",
                                customerNumber
                        )
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath("$.customerId")
                                .value(
                                        created.get(
                                                "customerId"
                                        ).asText()
                                )
                )
                .andExpect(
                        jsonPath("$.customerNumber")
                                .value(customerNumber)
                )
                .andExpect(
                        jsonPath("$.firstName")
                                .value("GetByNumber")
                );
    }

    @Test
    void shouldGetAllCustomers()
            throws Exception {

        JsonNode first =
                createCustomer(
                        newCustomerNumber(),
                        "ListA",
                        "Customer"
                );

        JsonNode second =
                createCustomer(
                        newCustomerNumber(),
                        "ListB",
                        "Customer"
                );

        mockMvc.perform(
                        get("/api/v1/customers")
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath("$").isArray()
                )
                .andExpect(
                        jsonPath("$[*].customerId")
                                .value(
                                        hasItem(
                                                first.get(
                                                        "customerId"
                                                ).asText()
                                        )
                                )
                )
                .andExpect(
                        jsonPath("$[*].customerId")
                                .value(
                                        hasItem(
                                                second.get(
                                                        "customerId"
                                                ).asText()
                                        )
                                )
                );
    }

    @Test
    void shouldUpdateCustomer()
            throws Exception {

        JsonNode created =
                createCustomer(
                        newCustomerNumber(),
                        "Original",
                        "Customer"
                );

        UUID customerId =
                UUID.fromString(
                        created.get(
                                "customerId"
                        ).asText()
                );

        String updatedNumber =
                newCustomerNumber();

        String requestBody =
                """
                {
                  "customerNumber": "%s",
                  "customerType": "INDIVIDUAL",
                  "firstName": "Updated",
                  "middleName": "Controller",
                  "lastName": "Customer",
                  "secondLastName": "Integration",
                  "legalName": "Updated Controller Customer Integration",
                  "riskLevel": "HIGH",
                  "riskScore": 88.25,
                  "customerStatus": "ACTIVE"
                }
                """.formatted(
                        updatedNumber
                );

        mockMvc.perform(
                        put(
                                "/api/v1/customers/{customerId}",
                                customerId
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
                        jsonPath("$.customerId")
                                .value(
                                        customerId.toString()
                                )
                )
                .andExpect(
                        jsonPath("$.customerNumber")
                                .value(updatedNumber)
                )
                .andExpect(
                        jsonPath("$.firstName")
                                .value("Updated")
                )
                .andExpect(
                        jsonPath("$.middleName")
                                .value("Controller")
                )
                .andExpect(
                        jsonPath("$.secondLastName")
                                .value("Integration")
                )
                .andExpect(
                        jsonPath("$.riskLevel")
                                .value("HIGH")
                )
                .andExpect(
                        jsonPath("$.riskScore")
                                .value(88.25)
                )
                .andExpect(
                        jsonPath("$.updatedAt").exists()
                );
    }

    @Test
    void shouldDeleteCustomerAndHideItFromSubsequentReads()
            throws Exception {

        JsonNode created =
                createCustomer(
                        newCustomerNumber(),
                        "Delete",
                        "Customer"
                );

        UUID customerId =
                UUID.fromString(
                        created.get(
                                "customerId"
                        ).asText()
                );

        String customerNumber =
                created.get(
                        "customerNumber"
                ).asText();

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
                        get(
                                "/api/v1/customers/{customerId}",
                                customerId
                        )
                )
                .andExpect(
                        status().isNotFound()
                );

        mockMvc.perform(
                        get(
                                "/api/v1/customers/number/{customerNumber}",
                                customerNumber
                        )
                )
                .andExpect(
                        status().isNotFound()
                );
    }

    @Test
    void shouldReturnNotFoundForUnknownCustomer()
            throws Exception {

        UUID unknownCustomerId =
                UUID.randomUUID();

        mockMvc.perform(
                        get(
                                "/api/v1/customers/{customerId}",
                                unknownCustomerId
                        )
                                .header(
                                        "X-Correlation-ID",
                                        "CUSTOMER-NOT-FOUND-TEST"
                                )
                )
                .andExpect(
                        status().isNotFound()
                )
                .andExpect(
                        jsonPath("$.status")
                                .value(404)
                )
                .andExpect(
                        jsonPath("$.errorCode")
                                .value(
                                        "CUSTOMER_RESOURCE_NOT_FOUND"
                                )
                )
                .andExpect(
                        jsonPath("$.correlationId")
                                .value(
                                        "CUSTOMER-NOT-FOUND-TEST"
                                )
                )
                .andExpect(
                        jsonPath("$.path")
                                .value(
                                        "/api/v1/customers/"
                                                + unknownCustomerId
                                )
                );
    }

    private JsonNode createCustomer(
            String customerNumber,
            String firstName,
            String lastName)
            throws Exception {

        MvcResult result =
                mockMvc.perform(
                                post("/api/v1/customers")
                                        .contentType(
                                                MediaType.APPLICATION_JSON
                                        )
                                        .content(
                                                validRequestBody(
                                                        customerNumber,
                                                        firstName,
                                                        lastName
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

    private String validRequestBody(
            String customerNumber,
            String firstName,
            String lastName) {

        return """
                {
                  "customerNumber": "%s",
                  "customerType": "INDIVIDUAL",
                  "firstName": "%s",
                  "lastName": "%s",
                  "riskLevel": "LOW",
                  "riskScore": 0,
                  "customerStatus": "ACTIVE"
                }
                """.formatted(
                customerNumber,
                firstName,
                lastName
        );
    }

    private String newCustomerNumber() {

        return "CCTRL-" + UUID.randomUUID();
    }
}