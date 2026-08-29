package com.efs.modules.catalog.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class TransactionTypeControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void shouldCreateTransactionType()
            throws Exception {

        String requestBody =
                """
                {
                  "transactionTypeCode": "TRANSFER",
                  "transactionTypeName": "Transfer",
                  "description": "Transfer transaction type",
                  "displayOrder": 2,
                  "status": "ACTIVE"
                }
                """;

        mockMvc.perform(
                        post("/api/v1/transaction-types")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isCreated())
                .andExpect(
                        jsonPath("$.transactionTypeId")
                                .exists()
                )
                .andExpect(
                        jsonPath("$.transactionTypeCode")
                                .value("TRANSFER")
                )
                .andExpect(
                        jsonPath("$.transactionTypeName")
                                .value("Transfer")
                )
                .andExpect(
                        jsonPath("$.description")
                                .value("Transfer transaction type")
                )
                .andExpect(
                        jsonPath("$.displayOrder")
                                .value(2)
                )
                .andExpect(
                        jsonPath("$.status")
                                .value("ACTIVE")
                )
                .andExpect(
                        jsonPath("$.createdAt")
                                .exists()
                );
    }

    @Test
    void shouldRejectInvalidTransactionType()
            throws Exception {

        String requestBody =
                """
                {
                  "transactionTypeCode": "",
                  "transactionTypeName": "",
                  "description": "Invalid transaction type",
                  "status": ""
                }
                """;

        mockMvc.perform(
                        post("/api/v1/transaction-types")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(
                        status().isBadRequest()
                );
    }

    @Test
    void shouldGetTransactionTypeById()
            throws Exception {

        UUID transactionTypeId =
                UUID.randomUUID();

        insertTransactionType(
                transactionTypeId,
                "PAYMENT",
                "Payment",
                "Payment transaction type",
                (short) 1,
                "ACTIVE"
        );

        mockMvc.perform(
                        get(
                                "/api/v1/transaction-types/{transactionTypeId}",
                                transactionTypeId
                        )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.transactionTypeId")
                                .value(
                                        transactionTypeId.toString()
                                )
                )
                .andExpect(
                        jsonPath("$.transactionTypeCode")
                                .value("PAYMENT")
                )
                .andExpect(
                        jsonPath("$.transactionTypeName")
                                .value("Payment")
                )
                .andExpect(
                        jsonPath("$.displayOrder")
                                .value(1)
                );
    }

    @Test
    void shouldGetTransactionTypeByCode()
            throws Exception {

        UUID transactionTypeId =
                UUID.randomUUID();

        insertTransactionType(
                transactionTypeId,
                "TRANSFER",
                "Transfer",
                "Transfer transaction type",
                (short) 2,
                "ACTIVE"
        );

        mockMvc.perform(
                        get(
                                "/api/v1/transaction-types/code/{transactionTypeCode}",
                                "TRANSFER"
                        )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.transactionTypeId")
                                .value(
                                        transactionTypeId.toString()
                                )
                )
                .andExpect(
                        jsonPath("$.transactionTypeCode")
                                .value("TRANSFER")
                )
                .andExpect(
                        jsonPath("$.transactionTypeName")
                                .value("Transfer")
                );
    }

    @Test
    void shouldGetTransactionTypesByStatusOrderedByDisplayOrder()
            throws Exception {

        insertTransactionType(
                UUID.randomUUID(),
                "WITHDRAWAL",
                "Withdrawal",
                null,
                (short) 3,
                "ACTIVE"
        );

        insertTransactionType(
                UUID.randomUUID(),
                "PAYMENT",
                "Payment",
                null,
                (short) 1,
                "ACTIVE"
        );

        insertTransactionType(
                UUID.randomUUID(),
                "TRANSFER",
                "Transfer",
                null,
                (short) 2,
                "ACTIVE"
        );

        mockMvc.perform(
                        get("/api/v1/transaction-types")
                                .param(
                                        "status",
                                        "ACTIVE"
                                )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.length()")
                                .value(3)
                )
                .andExpect(
                        jsonPath("$[0].transactionTypeCode")
                                .value("PAYMENT")
                )
                .andExpect(
                        jsonPath("$[1].transactionTypeCode")
                                .value("TRANSFER")
                )
                .andExpect(
                        jsonPath("$[2].transactionTypeCode")
                                .value("WITHDRAWAL")
                );
    }

    @Test
    void shouldGetAllTransactionTypesOrderedByDisplayOrder()
            throws Exception {

        insertTransactionType(
                UUID.randomUUID(),
                "WITHDRAWAL",
                "Withdrawal",
                null,
                (short) 3,
                "ACTIVE"
        );

        insertTransactionType(
                UUID.randomUUID(),
                "PAYMENT",
                "Payment",
                null,
                (short) 1,
                "ACTIVE"
        );

        insertTransactionType(
                UUID.randomUUID(),
                "TRANSFER",
                "Transfer",
                null,
                (short) 2,
                "ACTIVE"
        );

        mockMvc.perform(
                        get("/api/v1/transaction-types")
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.length()")
                                .value(3)
                )
                .andExpect(
                        jsonPath("$[0].displayOrder")
                                .value(1)
                )
                .andExpect(
                        jsonPath("$[1].displayOrder")
                                .value(2)
                )
                .andExpect(
                        jsonPath("$[2].displayOrder")
                                .value(3)
                );
    }

    @Test
    void shouldReturnNotFoundForUnknownTransactionTypeId()
            throws Exception {

        UUID unknownTransactionTypeId =
                UUID.randomUUID();

        mockMvc.perform(
                        get(
                                "/api/v1/transaction-types/{transactionTypeId}",
                                unknownTransactionTypeId
                        )
                )
                .andExpect(
                        status().isNotFound()
                );
    }

    @Test
    void shouldReturnNotFoundForUnknownTransactionTypeCode()
            throws Exception {

        mockMvc.perform(
                        get(
                                "/api/v1/transaction-types/code/{transactionTypeCode}",
                                "UNKNOWN"
                        )
                )
                .andExpect(
                        status().isNotFound()
                );
    }

    private void insertTransactionType(
            UUID transactionTypeId,
            String transactionTypeCode,
            String transactionTypeName,
            String description,
            Short displayOrder,
            String status) {

        jdbcTemplate.update(
                """
                INSERT INTO catalog.transaction_type (
                    transaction_type_id,
                    transaction_type_code,
                    transaction_type_name,
                    description,
                    display_order,
                    status,
                    created_at
                )
                VALUES (
                    ?,
                    ?,
                    ?,
                    ?,
                    ?,
                    ?,
                    clock_timestamp()
                )
                """,
                transactionTypeId,
                transactionTypeCode,
                transactionTypeName,
                description,
                displayOrder,
                status
        );
    }
}