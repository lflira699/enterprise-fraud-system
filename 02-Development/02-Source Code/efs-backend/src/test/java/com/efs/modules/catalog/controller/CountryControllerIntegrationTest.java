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
class CountryControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void shouldCreateCountry()
            throws Exception {

        String requestBody =
                """
                {
                  "countryCode": "GT",
                  "alpha3Code": "GTM",
                  "numericCode": "320",
                  "countryName": "Guatemala",
                  "status": "ACTIVE"
                }
                """;

        mockMvc.perform(
                        post("/api/v1/countries")
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(requestBody)
                )
                .andExpect(
                        status().isCreated()
                )
                .andExpect(
                        jsonPath("$.countryId").exists()
                )
                .andExpect(
                        jsonPath("$.countryCode")
                                .value("GT")
                )
                .andExpect(
                        jsonPath("$.alpha3Code")
                                .value("GTM")
                )
                .andExpect(
                        jsonPath("$.numericCode")
                                .value("320")
                )
                .andExpect(
                        jsonPath("$.countryName")
                                .value("Guatemala")
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
    void shouldRejectInvalidCountry()
            throws Exception {

        String requestBody =
                """
                {
                  "countryCode": "G",
                  "alpha3Code": "GT",
                  "countryName": "",
                  "status": ""
                }
                """;

        mockMvc.perform(
                        post("/api/v1/countries")
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(requestBody)
                )
                .andExpect(
                        status().isBadRequest()
                );
    }

    @Test
    void shouldGetCountryById()
            throws Exception {

        UUID countryId =
                UUID.randomUUID();

        insertCountry(
                countryId,
                "US",
                "USA",
                "840",
                "United States",
                "ACTIVE"
        );

        mockMvc.perform(
                        get(
                                "/api/v1/countries/{countryId}",
                                countryId
                        )
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath("$.countryId")
                                .value(
                                        countryId.toString()
                                )
                )
                .andExpect(
                        jsonPath("$.countryCode")
                                .value("US")
                );
    }

    @Test
    void shouldGetCountryByCountryCode()
            throws Exception {

        UUID countryId =
                UUID.randomUUID();

        insertCountry(
                countryId,
                "MX",
                "MEX",
                "484",
                "Mexico",
                "ACTIVE"
        );

        mockMvc.perform(
                        get(
                                "/api/v1/countries/code/{countryCode}",
                                "MX"
                        )
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath("$.countryId")
                                .value(
                                        countryId.toString()
                                )
                )
                .andExpect(
                        jsonPath("$.countryCode")
                                .value("MX")
                );
    }

    @Test
    void shouldGetCountryByAlpha3Code()
            throws Exception {

        UUID countryId =
                UUID.randomUUID();

        insertCountry(
                countryId,
                "CR",
                "CRI",
                "188",
                "Costa Rica",
                "ACTIVE"
        );

        mockMvc.perform(
                        get(
                                "/api/v1/countries/alpha3/{alpha3Code}",
                                "CRI"
                        )
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath("$.countryId")
                                .value(
                                        countryId.toString()
                                )
                )
                .andExpect(
                        jsonPath("$.alpha3Code")
                                .value("CRI")
                );
    }

    @Test
    void shouldGetCountryByNumericCode()
            throws Exception {

        UUID countryId =
                UUID.randomUUID();

        insertCountry(
                countryId,
                "PA",
                "PAN",
                "591",
                "Panama",
                "ACTIVE"
        );

        mockMvc.perform(
                        get(
                                "/api/v1/countries/numeric/{numericCode}",
                                "591"
                        )
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath("$.countryId")
                                .value(
                                        countryId.toString()
                                )
                )
                .andExpect(
                        jsonPath("$.numericCode")
                                .value("591")
                );
    }

    @Test
    void shouldGetCountriesByStatus()
            throws Exception {

        insertCountry(
                UUID.randomUUID(),
                "SV",
                "SLV",
                "222",
                "El Salvador",
                "ACTIVE"
        );

        insertCountry(
                UUID.randomUUID(),
                "HN",
                "HND",
                "340",
                "Honduras",
                "ACTIVE"
        );

        mockMvc.perform(
                        get("/api/v1/countries")
                                .param(
                                        "status",
                                        "ACTIVE"
                                )
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath("$.length()")
                                .value(2)
                )
                .andExpect(
                        jsonPath("$[0].countryName")
                                .value("El Salvador")
                )
                .andExpect(
                        jsonPath("$[1].countryName")
                                .value("Honduras")
                );
    }

    @Test
    void shouldGetAllCountries()
            throws Exception {

        insertCountry(
                UUID.randomUUID(),
                "BZ",
                "BLZ",
                "084",
                "Belize",
                "ACTIVE"
        );

        insertCountry(
                UUID.randomUUID(),
                "NI",
                "NIC",
                "558",
                "Nicaragua",
                "ACTIVE"
        );

        mockMvc.perform(
                        get("/api/v1/countries")
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath("$.length()")
                                .value(2)
                )
                .andExpect(
                        jsonPath("$[0].countryName")
                                .value("Belize")
                )
                .andExpect(
                        jsonPath("$[1].countryName")
                                .value("Nicaragua")
                );
    }

    @Test
    void shouldReturnNotFoundForUnknownCountryId()
            throws Exception {

        UUID unknownCountryId =
                UUID.randomUUID();

        mockMvc.perform(
                        get(
                                "/api/v1/countries/{countryId}",
                                unknownCountryId
                        )
                )
                .andExpect(
                        status().isNotFound()
                );
    }

    private void insertCountry(
            UUID countryId,
            String countryCode,
            String alpha3Code,
            String numericCode,
            String countryName,
            String status) {

        jdbcTemplate.update(
                """
                INSERT INTO catalog.country (
                    country_id,
                    country_code,
                    alpha3_code,
                    numeric_code,
                    country_name,
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
                countryId,
                countryCode,
                alpha3Code,
                numericCode,
                countryName,
                status
        );
    }
}