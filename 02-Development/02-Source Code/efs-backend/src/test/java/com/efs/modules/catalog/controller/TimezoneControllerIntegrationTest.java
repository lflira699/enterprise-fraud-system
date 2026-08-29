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
class TimezoneControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void shouldCreateTimezone()
            throws Exception {

        String requestBody =
                """
                {
                  "timezoneCode": "America/Guatemala",
                  "timezoneName": "Guatemala",
                  "status": "ACTIVE"
                }
                """;

        mockMvc.perform(
                        post("/api/v1/timezones")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.timezoneId").exists())
                .andExpect(
                        jsonPath("$.timezoneCode")
                                .value("America/Guatemala")
                )
                .andExpect(
                        jsonPath("$.timezoneName")
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
    void shouldRejectInvalidTimezone()
            throws Exception {

        String requestBody =
                """
                {
                  "timezoneCode": "",
                  "timezoneName": "",
                  "status": ""
                }
                """;

        mockMvc.perform(
                        post("/api/v1/timezones")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldGetTimezoneById()
            throws Exception {

        UUID timezoneId =
                UUID.randomUUID();

        insertTimezone(
                timezoneId,
                "America/New_York",
                "New York",
                "ACTIVE"
        );

        mockMvc.perform(
                        get(
                                "/api/v1/timezones/{timezoneId}",
                                timezoneId
                        )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.timezoneId")
                                .value(timezoneId.toString())
                )
                .andExpect(
                        jsonPath("$.timezoneCode")
                                .value("America/New_York")
                )
                .andExpect(
                        jsonPath("$.timezoneName")
                                .value("New York")
                );
    }

    @Test
    void shouldGetTimezoneByTimezoneCode()
            throws Exception {

        UUID timezoneId =
                UUID.randomUUID();

        insertTimezone(
                timezoneId,
                "UTC",
                "Coordinated Universal Time",
                "ACTIVE"
        );

        mockMvc.perform(
                        get(
                                "/api/v1/timezones/code/{timezoneCode}",
                                "UTC"
                        )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.timezoneId")
                                .value(timezoneId.toString())
                )
                .andExpect(
                        jsonPath("$.timezoneCode")
                                .value("UTC")
                );
    }

    @Test
    void shouldGetTimezonesByStatus()
            throws Exception {

        insertTimezone(
                UUID.randomUUID(),
                "America/Guatemala",
                "Guatemala",
                "ACTIVE"
        );

        insertTimezone(
                UUID.randomUUID(),
                "America/New_York",
                "New York",
                "ACTIVE"
        );

        mockMvc.perform(
                        get("/api/v1/timezones")
                                .param("status", "ACTIVE")
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.length()")
                                .value(2)
                )
                .andExpect(
                        jsonPath("$[0].timezoneName")
                                .value("Guatemala")
                )
                .andExpect(
                        jsonPath("$[1].timezoneName")
                                .value("New York")
                );
    }

    @Test
    void shouldGetAllTimezones()
            throws Exception {

        insertTimezone(
                UUID.randomUUID(),
                "Europe/London",
                "London",
                "ACTIVE"
        );

        insertTimezone(
                UUID.randomUUID(),
                "Europe/Madrid",
                "Madrid",
                "ACTIVE"
        );

        mockMvc.perform(
                        get("/api/v1/timezones")
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.length()")
                                .value(2)
                )
                .andExpect(
                        jsonPath("$[0].timezoneName")
                                .value("London")
                )
                .andExpect(
                        jsonPath("$[1].timezoneName")
                                .value("Madrid")
                );
    }

    @Test
    void shouldReturnNotFoundForUnknownTimezoneId()
            throws Exception {

        UUID unknownTimezoneId =
                UUID.randomUUID();

        mockMvc.perform(
                        get(
                                "/api/v1/timezones/{timezoneId}",
                                unknownTimezoneId
                        )
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnNotFoundForUnknownTimezoneCode()
            throws Exception {

        mockMvc.perform(
                        get(
                                "/api/v1/timezones/code/{timezoneCode}",
                                "Unknown-Timezone"
                        )
                )
                .andExpect(status().isNotFound());
    }

    private void insertTimezone(
            UUID timezoneId,
            String timezoneCode,
            String timezoneName,
            String status) {

        jdbcTemplate.update(
                """
                INSERT INTO catalog.timezone (
                    timezone_id,
                    timezone_code,
                    timezone_name,
                    status,
                    created_at
                )
                VALUES (
                    ?,
                    ?,
                    ?,
                    ?,
                    clock_timestamp()
                )
                """,
                timezoneId,
                timezoneCode,
                timezoneName,
                status
        );
    }
}