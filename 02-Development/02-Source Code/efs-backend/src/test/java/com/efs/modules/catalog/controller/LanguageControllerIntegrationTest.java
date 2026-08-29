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
class LanguageControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void shouldCreateLanguage()
            throws Exception {

        String requestBody =
                """
                {
                  "languageCode": "es",
                  "alpha3Code": "spa",
                  "languageName": "Spanish",
                  "status": "ACTIVE"
                }
                """;

        mockMvc.perform(
                        post("/api/v1/languages")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.languageId").exists())
                .andExpect(
                        jsonPath("$.languageCode")
                                .value("es")
                )
                .andExpect(
                        jsonPath("$.alpha3Code")
                                .value("spa")
                )
                .andExpect(
                        jsonPath("$.languageName")
                                .value("Spanish")
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
    void shouldRejectInvalidLanguage()
            throws Exception {

        String requestBody =
                """
                {
                  "languageCode": "e",
                  "alpha3Code": "sp",
                  "languageName": "",
                  "status": ""
                }
                """;

        mockMvc.perform(
                        post("/api/v1/languages")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldGetLanguageById()
            throws Exception {

        UUID languageId =
                UUID.randomUUID();

        insertLanguage(
                languageId,
                "en",
                "eng",
                "English",
                "ACTIVE"
        );

        mockMvc.perform(
                        get(
                                "/api/v1/languages/{languageId}",
                                languageId
                        )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.languageId")
                                .value(languageId.toString())
                )
                .andExpect(
                        jsonPath("$.languageCode")
                                .value("en")
                )
                .andExpect(
                        jsonPath("$.alpha3Code")
                                .value("eng")
                );
    }

    @Test
    void shouldGetLanguageByLanguageCode()
            throws Exception {

        UUID languageId =
                UUID.randomUUID();

        insertLanguage(
                languageId,
                "fr",
                "fra",
                "French",
                "ACTIVE"
        );

        mockMvc.perform(
                        get(
                                "/api/v1/languages/code/{languageCode}",
                                "fr"
                        )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.languageId")
                                .value(languageId.toString())
                )
                .andExpect(
                        jsonPath("$.languageCode")
                                .value("fr")
                );
    }

    @Test
    void shouldGetLanguageByAlpha3Code()
            throws Exception {

        UUID languageId =
                UUID.randomUUID();

        insertLanguage(
                languageId,
                "de",
                "deu",
                "German",
                "ACTIVE"
        );

        mockMvc.perform(
                        get(
                                "/api/v1/languages/alpha3/{alpha3Code}",
                                "deu"
                        )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.languageId")
                                .value(languageId.toString())
                )
                .andExpect(
                        jsonPath("$.alpha3Code")
                                .value("deu")
                );
    }

    @Test
    void shouldGetLanguagesByStatus()
            throws Exception {

        insertLanguage(
                UUID.randomUUID(),
                "de",
                "deu",
                "German",
                "ACTIVE"
        );

        insertLanguage(
                UUID.randomUUID(),
                "it",
                "ita",
                "Italian",
                "ACTIVE"
        );

        mockMvc.perform(
                        get("/api/v1/languages")
                                .param("status", "ACTIVE")
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.length()")
                                .value(2)
                )
                .andExpect(
                        jsonPath("$[0].languageName")
                                .value("German")
                )
                .andExpect(
                        jsonPath("$[1].languageName")
                                .value("Italian")
                );
    }

    @Test
    void shouldGetAllLanguages()
            throws Exception {

        insertLanguage(
                UUID.randomUUID(),
                "pt",
                "por",
                "Portuguese",
                "ACTIVE"
        );

        insertLanguage(
                UUID.randomUUID(),
                "es",
                "spa",
                "Spanish",
                "ACTIVE"
        );

        mockMvc.perform(
                        get("/api/v1/languages")
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.length()")
                                .value(2)
                )
                .andExpect(
                        jsonPath("$[0].languageName")
                                .value("Portuguese")
                )
                .andExpect(
                        jsonPath("$[1].languageName")
                                .value("Spanish")
                );
    }

    @Test
    void shouldReturnNotFoundForUnknownLanguageId()
            throws Exception {

        UUID unknownLanguageId =
                UUID.randomUUID();

        mockMvc.perform(
                        get(
                                "/api/v1/languages/{languageId}",
                                unknownLanguageId
                        )
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnNotFoundForUnknownLanguageCode()
            throws Exception {

        mockMvc.perform(
                        get(
                                "/api/v1/languages/code/{languageCode}",
                                "zz"
                        )
                )
                .andExpect(status().isNotFound());
    }

    private void insertLanguage(
            UUID languageId,
            String languageCode,
            String alpha3Code,
            String languageName,
            String status) {

        jdbcTemplate.update(
                """
                INSERT INTO catalog.language (
                    language_id,
                    language_code,
                    alpha3_code,
                    language_name,
                    status,
                    created_at
                )
                VALUES (
                    ?,
                    ?,
                    ?,
                    ?,
                    ?,
                    clock_timestamp()
                )
                """,
                languageId,
                languageCode,
                alpha3Code,
                languageName,
                status
        );
    }
}