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
class CustomerBankAccountControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldCreateBankAccount() throws Exception {

        UUID customerId =
                createCustomer();

        UUID createdBy =
                UUID.randomUUID();

        String accountNumber =
                newAccountNumber();

        String requestBody =
                """
                {
                  "bankName": "Integration Test Bank",
                  "accountNumber": "%s",
                  "accountType": "CHECKING",
                  "currencyCode": "GTQ",
                  "countryCode": "GTM",
                  "primary": true,
                  "verified": true,
                  "verificationStatus": "VERIFIED",
                  "createdBy": "%s"
                }
                """.formatted(
                        accountNumber,
                        createdBy
                );

        mockMvc.perform(
                        post(
                                "/api/v1/customers/{customerId}/bank-accounts",
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
                        jsonPath("$.customerBankAccountId").exists()
                )
                .andExpect(
                        jsonPath("$.customerId")
                                .value(customerId.toString())
                )
                .andExpect(
                        jsonPath("$.bankName")
                                .value("Integration Test Bank")
                )
                .andExpect(
                        jsonPath("$.accountNumber")
                                .value(accountNumber)
                )
                .andExpect(
                        jsonPath("$.accountType")
                                .value("CHECKING")
                )
                .andExpect(
                        jsonPath("$.currencyCode")
                                .value("GTQ")
                )
                .andExpect(
                        jsonPath("$.countryCode")
                                .value("GTM")
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
                        jsonPath("$.verificationStatus")
                                .value("VERIFIED")
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
    void shouldApplyPrimaryAndVerifiedDefaults()
            throws Exception {

        UUID customerId =
                createCustomer();

        String requestBody =
                """
                {
                  "bankName": "Default Test Bank",
                  "accountNumber": "%s"
                }
                """.formatted(
                        newAccountNumber()
                );

        mockMvc.perform(
                        post(
                                "/api/v1/customers/{customerId}/bank-accounts",
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
                  "bankName": "",
                  "accountNumber": ""
                }
                """;

        mockMvc.perform(
                        post(
                                "/api/v1/customers/{customerId}/bank-accounts",
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
                        jsonPath("$.validationErrors.bankName")
                                .exists()
                )
                .andExpect(
                        jsonPath("$.validationErrors.accountNumber")
                                .exists()
                );
    }

    @Test
    void shouldReturnNotFoundWhenCreatingBankAccountForUnknownCustomer()
            throws Exception {

        UUID unknownCustomerId =
                UUID.randomUUID();

        mockMvc.perform(
                        post(
                                "/api/v1/customers/{customerId}/bank-accounts",
                                unknownCustomerId
                        )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        validBankAccountBody(
                                                "Unknown Customer Bank",
                                                newAccountNumber()
                                        )
                                )
                )
                .andExpect(
                        status().isNotFound()
                );
    }

    @Test
    void shouldGetBankAccountById()
            throws Exception {

        UUID customerId =
                createCustomer();

        JsonNode created =
                createBankAccount(
                        customerId,
                        "Get By Id Bank",
                        newAccountNumber()
                );

        UUID bankAccountId =
                UUID.fromString(
                        created.get(
                                "customerBankAccountId"
                        ).asText()
                );

        mockMvc.perform(
                        get(
                                "/api/v1/customers/{customerId}/bank-accounts/{bankAccountId}",
                                customerId,
                                bankAccountId
                        )
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath("$.customerBankAccountId")
                                .value(bankAccountId.toString())
                )
                .andExpect(
                        jsonPath("$.customerId")
                                .value(customerId.toString())
                )
                .andExpect(
                        jsonPath("$.bankName")
                                .value("Get By Id Bank")
                );
    }

    @Test
    void shouldReturnNotFoundWhenBankAccountBelongsToDifferentCustomer()
            throws Exception {

        UUID ownerCustomerId =
                createCustomer();

        UUID otherCustomerId =
                createCustomer();

        JsonNode created =
                createBankAccount(
                        ownerCustomerId,
                        "Ownership Bank",
                        newAccountNumber()
                );

        UUID bankAccountId =
                UUID.fromString(
                        created.get(
                                "customerBankAccountId"
                        ).asText()
                );

        mockMvc.perform(
                        get(
                                "/api/v1/customers/{customerId}/bank-accounts/{bankAccountId}",
                                otherCustomerId,
                                bankAccountId
                        )
                )
                .andExpect(
                        status().isNotFound()
                );
    }

    @Test
    void shouldGetBankAccountsByCustomer()
            throws Exception {

        UUID customerId =
                createCustomer();

        JsonNode first =
                createBankAccount(
                        customerId,
                        "First Bank",
                        newAccountNumber()
                );

        JsonNode second =
                createBankAccount(
                        customerId,
                        "Second Bank",
                        newAccountNumber()
                );

        mockMvc.perform(
                        get(
                                "/api/v1/customers/{customerId}/bank-accounts",
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
                        jsonPath("$[*].customerBankAccountId")
                                .value(
                                        hasItem(
                                                first.get(
                                                        "customerBankAccountId"
                                                ).asText()
                                        )
                                )
                )
                .andExpect(
                        jsonPath("$[*].customerBankAccountId")
                                .value(
                                        hasItem(
                                                second.get(
                                                        "customerBankAccountId"
                                                ).asText()
                                        )
                                )
                );
    }

    @Test
    void shouldUpdateBankAccount()
            throws Exception {

        UUID customerId =
                createCustomer();

        JsonNode created =
                createBankAccount(
                        customerId,
                        "Original Bank",
                        newAccountNumber()
                );

        UUID bankAccountId =
                UUID.fromString(
                        created.get(
                                "customerBankAccountId"
                        ).asText()
                );

        UUID updatedBy =
                UUID.randomUUID();

        String updatedAccountNumber =
                newAccountNumber();

        String requestBody =
                """
                {
                  "bankName": "Updated Bank",
                  "accountNumber": "%s",
                  "accountType": "SAVINGS",
                  "currencyCode": "USD",
                  "countryCode": "USA",
                  "primary": true,
                  "verified": true,
                  "verificationStatus": "VERIFIED",
                  "updatedBy": "%s"
                }
                """.formatted(
                        updatedAccountNumber,
                        updatedBy
                );

        mockMvc.perform(
                        put(
                                "/api/v1/customers/{customerId}/bank-accounts/{bankAccountId}",
                                customerId,
                                bankAccountId
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
                        jsonPath("$.customerBankAccountId")
                                .value(bankAccountId.toString())
                )
                .andExpect(
                        jsonPath("$.bankName")
                                .value("Updated Bank")
                )
                .andExpect(
                        jsonPath("$.accountNumber")
                                .value(updatedAccountNumber)
                )
                .andExpect(
                        jsonPath("$.accountType")
                                .value("SAVINGS")
                )
                .andExpect(
                        jsonPath("$.currencyCode")
                                .value("USD")
                )
                .andExpect(
                        jsonPath("$.countryCode")
                                .value("USA")
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
                createBankAccount(
                        ownerCustomerId,
                        "Protected Bank",
                        newAccountNumber()
                );

        UUID bankAccountId =
                UUID.fromString(
                        created.get(
                                "customerBankAccountId"
                        ).asText()
                );

        mockMvc.perform(
                        put(
                                "/api/v1/customers/{customerId}/bank-accounts/{bankAccountId}",
                                otherCustomerId,
                                bankAccountId
                        )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        validBankAccountBody(
                                                "Unauthorized Bank",
                                                newAccountNumber()
                                        )
                                )
                )
                .andExpect(
                        status().isNotFound()
                );
    }

    @Test
    void shouldDeleteBankAccountAndHideItFromReads()
            throws Exception {

        UUID customerId =
                createCustomer();

        JsonNode created =
                createBankAccount(
                        customerId,
                        "Delete Bank",
                        newAccountNumber()
                );

        UUID bankAccountId =
                UUID.fromString(
                        created.get(
                                "customerBankAccountId"
                        ).asText()
                );

        UUID deletedBy =
                UUID.randomUUID();

        mockMvc.perform(
                        delete(
                                "/api/v1/customers/{customerId}/bank-accounts/{bankAccountId}",
                                customerId,
                                bankAccountId
                        )
                                .param(
                                        "deletedBy",
                                        deletedBy.toString()
                                )
                )
                .andExpect(
                        status().isNoContent()
                );

        mockMvc.perform(
                        get(
                                "/api/v1/customers/{customerId}/bank-accounts/{bankAccountId}",
                                customerId,
                                bankAccountId
                        )
                )
                .andExpect(
                        status().isNotFound()
                );

        mockMvc.perform(
                        get(
                                "/api/v1/customers/{customerId}/bank-accounts",
                                customerId
                        )
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath("$[*].customerBankAccountId")
                                .value(
                                        not(
                                                hasItem(
                                                        bankAccountId.toString()
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
                createBankAccount(
                        ownerCustomerId,
                        "Protected Delete Bank",
                        newAccountNumber()
                );

        UUID bankAccountId =
                UUID.fromString(
                        created.get(
                                "customerBankAccountId"
                        ).asText()
                );

        mockMvc.perform(
                        delete(
                                "/api/v1/customers/{customerId}/bank-accounts/{bankAccountId}",
                                otherCustomerId,
                                bankAccountId
                        )
                )
                .andExpect(
                        status().isNotFound()
                );
    }

    @Test
    void shouldRejectBankAccountOperationsForSoftDeletedCustomer()
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
                                "/api/v1/customers/{customerId}/bank-accounts",
                                customerId
                        )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        validBankAccountBody(
                                                "Deleted Customer Bank",
                                                newAccountNumber()
                                        )
                                )
                )
                .andExpect(
                        status().isNotFound()
                );

        mockMvc.perform(
                        get(
                                "/api/v1/customers/{customerId}/bank-accounts",
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
                "CBA-CTRL-" + UUID.randomUUID();

        String requestBody =
                """
                {
                  "customerNumber": "%s",
                  "customerType": "INDIVIDUAL",
                  "firstName": "BankAccount",
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

    private JsonNode createBankAccount(
            UUID customerId,
            String bankName,
            String accountNumber)
            throws Exception {

        MvcResult result =
                mockMvc.perform(
                                post(
                                        "/api/v1/customers/{customerId}/bank-accounts",
                                        customerId
                                )
                                        .contentType(
                                                MediaType.APPLICATION_JSON
                                        )
                                        .content(
                                                validBankAccountBody(
                                                        bankName,
                                                        accountNumber
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

    private String validBankAccountBody(
            String bankName,
            String accountNumber) {

        return """
                {
                  "bankName": "%s",
                  "accountNumber": "%s"
                }
                """.formatted(
                bankName,
                accountNumber
        );
    }

    private String newAccountNumber() {

        return "ACC-" + UUID.randomUUID();
    }
}