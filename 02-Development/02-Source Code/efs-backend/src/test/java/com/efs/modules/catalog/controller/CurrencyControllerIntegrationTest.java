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
class CurrencyControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void shouldCreateCurrency()
            throws Exception {

        String requestBody =
                """
                {
                  "currencyCode": "GTQ",
                  "numericCode": "320",
                  "currencyName": "Guatemalan Quetzal",
                  "minorUnit": 2,
                  "status": "ACTIVE"
                }
                """;

        mockMvc.perform(
                        post("/api/v1/currencies")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.currencyId").exists())
                .andExpect(
                        jsonPath("$.currencyCode")
                                .value("GTQ")
                )
                .andExpect(
                        jsonPath("$.numericCode")
                                .value("320")
                )
                .andExpect(
                        jsonPath("$.currencyName")
                                .value("Guatemalan Quetzal")
                )
                .andExpect(
                        jsonPath("$.minorUnit")
                                .value(2)
                )
                .andExpect(
                        jsonPath("$.status")
                                .value("ACTIVE")
                )
                .andExpect(
                        jsonPath("$.createdAt").exists()
                );
    }

    @Test
    void shouldRejectInvalidCurrency()
            throws Exception {

        String requestBody =
                """
                {
                  "currencyCode": "GT",
                  "numericCode": "32",
                  "currencyName": "",
                  "status": ""
                }
                """;

        mockMvc.perform(
                        post("/api/v1/currencies")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldGetCurrencyById()
            throws Exception {

        UUID currencyId =
                UUID.randomUUID();

        insertCurrency(
                currencyId,
                "USD",
                "840",
                "US Dollar",
                (short) 2,
                "ACTIVE"
        );

        mockMvc.perform(
                        get(
                                "/api/v1/currencies/{currencyId}",
                                currencyId
                        )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.currencyId")
                                .value(currencyId.toString())
                )
                .andExpect(
                        jsonPath("$.currencyCode")
                                .value("USD")
                );
    }

    @Test
    void shouldGetCurrencyByCurrencyCode()
            throws Exception {

        UUID currencyId =
                UUID.randomUUID();

        insertCurrency(
                currencyId,
                "EUR",
                "978",
                "Euro",
                (short) 2,
                "ACTIVE"
        );

        mockMvc.perform(
                        get(
                                "/api/v1/currencies/code/{currencyCode}",
                                "EUR"
                        )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.currencyId")
                                .value(currencyId.toString())
                )
                .andExpect(
                        jsonPath("$.currencyCode")
                                .value("EUR")
                );
    }

    @Test
    void shouldGetCurrencyByNumericCode()
            throws Exception {

        UUID currencyId =
                UUID.randomUUID();

        insertCurrency(
                currencyId,
                "JPY",
                "392",
                "Japanese Yen",
                (short) 0,
                "ACTIVE"
        );

        mockMvc.perform(
                        get(
                                "/api/v1/currencies/numeric/{numericCode}",
                                "392"
                        )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.currencyId")
                                .value(currencyId.toString())
                )
                .andExpect(
                        jsonPath("$.numericCode")
                                .value("392")
                )
                .andExpect(
                        jsonPath("$.minorUnit")
                                .value(0)
                );
    }

    @Test
    void shouldGetCurrenciesByStatus()
            throws Exception {

        insertCurrency(
                UUID.randomUUID(),
                "CAD",
                "124",
                "Canadian Dollar",
                (short) 2,
                "ACTIVE"
        );

        insertCurrency(
                UUID.randomUUID(),
                "MXN",
                "484",
                "Mexican Peso",
                (short) 2,
                "ACTIVE"
        );

        mockMvc.perform(
                        get("/api/v1/currencies")
                                .param("status", "ACTIVE")
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.length()")
                                .value(2)
                )
                .andExpect(
                        jsonPath("$[0].currencyName")
                                .value("Canadian Dollar")
                )
                .andExpect(
                        jsonPath("$[1].currencyName")
                                .value("Mexican Peso")
                );
    }

    @Test
    void shouldGetAllCurrencies()
            throws Exception {

        insertCurrency(
                UUID.randomUUID(),
                "GBP",
                "826",
                "Pound Sterling",
                (short) 2,
                "ACTIVE"
        );

        insertCurrency(
                UUID.randomUUID(),
                "CHF",
                "756",
                "Swiss Franc",
                (short) 2,
                "ACTIVE"
        );

        mockMvc.perform(
                        get("/api/v1/currencies")
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.length()")
                                .value(2)
                )
                .andExpect(
                        jsonPath("$[0].currencyName")
                                .value("Pound Sterling")
                )
                .andExpect(
                        jsonPath("$[1].currencyName")
                                .value("Swiss Franc")
                );
    }

    @Test
    void shouldReturnNotFoundForUnknownCurrencyId()
            throws Exception {

        UUID unknownCurrencyId =
                UUID.randomUUID();

        mockMvc.perform(
                        get(
                                "/api/v1/currencies/{currencyId}",
                                unknownCurrencyId
                        )
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnNotFoundForUnknownCurrencyCode()
            throws Exception {

        mockMvc.perform(
                        get(
                                "/api/v1/currencies/code/{currencyCode}",
                                "XXX"
                        )
                )
                .andExpect(status().isNotFound());
    }

    private void insertCurrency(
            UUID currencyId,
            String currencyCode,
            String numericCode,
            String currencyName,
            short minorUnit,
            String status) {

        jdbcTemplate.update(
                """
                INSERT INTO catalog.currency (
                    currency_id,
                    currency_code,
                    numeric_code,
                    currency_name,
                    minor_unit,
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
                currencyId,
                currencyCode,
                numericCode,
                currencyName,
                minorUnit,
                status
        );
    }
}