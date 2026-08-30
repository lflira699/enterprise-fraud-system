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
class CustomerAddressControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldCreateAddress() throws Exception {

        UUID customerId =
                createCustomer();

        UUID createdBy =
                UUID.randomUUID();

        String requestBody =
                """
                {
                  "addressType": "HOME",
                  "addressLine1": "10 Avenida 1-20",
                  "addressLine2": "Zona 10",
                  "city": "Guatemala City",
                  "state": "Guatemala",
                  "postalCode": "01010",
                  "countryCode": "GTM",
                  "primary": true,
                  "effectiveDate": "2026-01-01",
                  "expirationDate": "2030-12-31",
                  "createdBy": "%s"
                }
                """.formatted(
                        createdBy
                );

        mockMvc.perform(
                        post(
                                "/api/v1/customers/{customerId}/addresses",
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
                        jsonPath("$.customerAddressId").exists()
                )
                .andExpect(
                        jsonPath("$.customerId")
                                .value(customerId.toString())
                )
                .andExpect(
                        jsonPath("$.addressType")
                                .value("HOME")
                )
                .andExpect(
                        jsonPath("$.addressLine1")
                                .value("10 Avenida 1-20")
                )
                .andExpect(
                        jsonPath("$.addressLine2")
                                .value("Zona 10")
                )
                .andExpect(
                        jsonPath("$.city")
                                .value("Guatemala City")
                )
                .andExpect(
                        jsonPath("$.state")
                                .value("Guatemala")
                )
                .andExpect(
                        jsonPath("$.postalCode")
                                .value("01010")
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
                        jsonPath("$.effectiveDate")
                                .value("2026-01-01")
                )
                .andExpect(
                        jsonPath("$.expirationDate")
                                .value("2030-12-31")
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
                )
                .andExpect(
                        jsonPath("$.deletedAt").doesNotExist()
                );
    }

    @Test
    void shouldApplyPrimaryFalseDefault() throws Exception {

        UUID customerId =
                createCustomer();

        String requestBody =
                """
                {
                  "addressType": "WORK",
                  "addressLine1": "Business Center",
                  "countryCode": "GTM"
                }
                """;

        mockMvc.perform(
                        post(
                                "/api/v1/customers/{customerId}/addresses",
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
                );
    }

    @Test
    void shouldRejectInvalidCreateRequest() throws Exception {

        UUID customerId =
                createCustomer();

        String requestBody =
                """
                {
                  "addressType": "",
                  "addressLine1": "",
                  "countryCode": ""
                }
                """;

        mockMvc.perform(
                        post(
                                "/api/v1/customers/{customerId}/addresses",
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
                        jsonPath("$.validationErrors.addressType")
                                .exists()
                )
                .andExpect(
                        jsonPath("$.validationErrors.addressLine1")
                                .exists()
                )
                .andExpect(
                        jsonPath("$.validationErrors.countryCode")
                                .exists()
                );
    }

    @Test
    void shouldReturnNotFoundWhenCreatingAddressForUnknownCustomer()
            throws Exception {

        UUID unknownCustomerId =
                UUID.randomUUID();

        mockMvc.perform(
                        post(
                                "/api/v1/customers/{customerId}/addresses",
                                unknownCustomerId
                        )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        validAddressBody(
                                                "HOME",
                                                "Unknown Customer Address"
                                        )
                                )
                )
                .andExpect(
                        status().isNotFound()
                );
    }

    @Test
    void shouldGetAddressById() throws Exception {

        UUID customerId =
                createCustomer();

        JsonNode created =
                createAddress(
                        customerId,
                        "HOME",
                        "Address By Id"
                );

        UUID addressId =
                UUID.fromString(
                        created.get(
                                "customerAddressId"
                        ).asText()
                );

        mockMvc.perform(
                        get(
                                "/api/v1/customers/{customerId}/addresses/{addressId}",
                                customerId,
                                addressId
                        )
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath("$.customerAddressId")
                                .value(addressId.toString())
                )
                .andExpect(
                        jsonPath("$.customerId")
                                .value(customerId.toString())
                )
                .andExpect(
                        jsonPath("$.addressLine1")
                                .value("Address By Id")
                );
    }

    @Test
    void shouldReturnNotFoundWhenAddressBelongsToDifferentCustomer()
            throws Exception {

        UUID ownerCustomerId =
                createCustomer();

        UUID otherCustomerId =
                createCustomer();

        JsonNode created =
                createAddress(
                        ownerCustomerId,
                        "HOME",
                        "Ownership Address"
                );

        UUID addressId =
                UUID.fromString(
                        created.get(
                                "customerAddressId"
                        ).asText()
                );

        mockMvc.perform(
                        get(
                                "/api/v1/customers/{customerId}/addresses/{addressId}",
                                otherCustomerId,
                                addressId
                        )
                )
                .andExpect(
                        status().isNotFound()
                );
    }

    @Test
    void shouldGetAddressesByCustomer() throws Exception {

        UUID customerId =
                createCustomer();

        JsonNode first =
                createAddress(
                        customerId,
                        "HOME",
                        "First Address"
                );

        JsonNode second =
                createAddress(
                        customerId,
                        "WORK",
                        "Second Address"
                );

        mockMvc.perform(
                        get(
                                "/api/v1/customers/{customerId}/addresses",
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
                        jsonPath("$[*].customerAddressId")
                                .value(
                                        hasItem(
                                                first.get(
                                                        "customerAddressId"
                                                ).asText()
                                        )
                                )
                )
                .andExpect(
                        jsonPath("$[*].customerAddressId")
                                .value(
                                        hasItem(
                                                second.get(
                                                        "customerAddressId"
                                                ).asText()
                                        )
                                )
                );
    }

    @Test
    void shouldUpdateAddress() throws Exception {

        UUID customerId =
                createCustomer();

        JsonNode created =
                createAddress(
                        customerId,
                        "HOME",
                        "Original Address"
                );

        UUID addressId =
                UUID.fromString(
                        created.get(
                                "customerAddressId"
                        ).asText()
                );

        UUID updatedBy =
                UUID.randomUUID();

        String requestBody =
                """
                {
                  "addressType": "WORK",
                  "addressLine1": "Updated Address",
                  "addressLine2": "Suite 500",
                  "city": "Guatemala City",
                  "state": "Guatemala",
                  "postalCode": "01009",
                  "countryCode": "GTM",
                  "primary": true,
                  "updatedBy": "%s"
                }
                """.formatted(
                        updatedBy
                );

        mockMvc.perform(
                        put(
                                "/api/v1/customers/{customerId}/addresses/{addressId}",
                                customerId,
                                addressId
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
                        jsonPath("$.customerAddressId")
                                .value(addressId.toString())
                )
                .andExpect(
                        jsonPath("$.addressType")
                                .value("WORK")
                )
                .andExpect(
                        jsonPath("$.addressLine1")
                                .value("Updated Address")
                )
                .andExpect(
                        jsonPath("$.addressLine2")
                                .value("Suite 500")
                )
                .andExpect(
                        jsonPath("$.primary")
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
                createAddress(
                        ownerCustomerId,
                        "HOME",
                        "Protected Address"
                );

        UUID addressId =
                UUID.fromString(
                        created.get(
                                "customerAddressId"
                        ).asText()
                );

        mockMvc.perform(
                        put(
                                "/api/v1/customers/{customerId}/addresses/{addressId}",
                                otherCustomerId,
                                addressId
                        )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        validAddressBody(
                                                "WORK",
                                                "Unauthorized Update"
                                        )
                                )
                )
                .andExpect(
                        status().isNotFound()
                );
    }

    @Test
    void shouldDeleteAddressAndHideItFromReads()
            throws Exception {

        UUID customerId =
                createCustomer();

        JsonNode created =
                createAddress(
                        customerId,
                        "HOME",
                        "Delete Address"
                );

        UUID addressId =
                UUID.fromString(
                        created.get(
                                "customerAddressId"
                        ).asText()
                );

        UUID deletedBy =
                UUID.randomUUID();

        mockMvc.perform(
                        delete(
                                "/api/v1/customers/{customerId}/addresses/{addressId}",
                                customerId,
                                addressId
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
                                "/api/v1/customers/{customerId}/addresses/{addressId}",
                                customerId,
                                addressId
                        )
                )
                .andExpect(
                        status().isNotFound()
                );

        mockMvc.perform(
                        get(
                                "/api/v1/customers/{customerId}/addresses",
                                customerId
                        )
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath("$[*].customerAddressId")
                                .value(
                                        org.hamcrest.Matchers.not(
                                                hasItem(
                                                        addressId.toString()
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
                createAddress(
                        ownerCustomerId,
                        "HOME",
                        "Protected Delete"
                );

        UUID addressId =
                UUID.fromString(
                        created.get(
                                "customerAddressId"
                        ).asText()
                );

        mockMvc.perform(
                        delete(
                                "/api/v1/customers/{customerId}/addresses/{addressId}",
                                otherCustomerId,
                                addressId
                        )
                )
                .andExpect(
                        status().isNotFound()
                );
    }

    @Test
    void shouldRejectAddressOperationsForSoftDeletedCustomer()
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
                                "/api/v1/customers/{customerId}/addresses",
                                customerId
                        )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        validAddressBody(
                                                "HOME",
                                                "Deleted Customer Address"
                                        )
                                )
                )
                .andExpect(
                        status().isNotFound()
                );

        mockMvc.perform(
                        get(
                                "/api/v1/customers/{customerId}/addresses",
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
                "CA-CTRL-" + UUID.randomUUID();

        String requestBody =
                """
                {
                  "customerNumber": "%s",
                  "customerType": "INDIVIDUAL",
                  "firstName": "Address",
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

    private JsonNode createAddress(
            UUID customerId,
            String addressType,
            String addressLine1)
            throws Exception {

        MvcResult result =
                mockMvc.perform(
                                post(
                                        "/api/v1/customers/{customerId}/addresses",
                                        customerId
                                )
                                        .contentType(
                                                MediaType.APPLICATION_JSON
                                        )
                                        .content(
                                                validAddressBody(
                                                        addressType,
                                                        addressLine1
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

    private String validAddressBody(
            String addressType,
            String addressLine1) {

        return """
                {
                  "addressType": "%s",
                  "addressLine1": "%s",
                  "countryCode": "GTM"
                }
                """.formatted(
                addressType,
                addressLine1
        );
    }
}