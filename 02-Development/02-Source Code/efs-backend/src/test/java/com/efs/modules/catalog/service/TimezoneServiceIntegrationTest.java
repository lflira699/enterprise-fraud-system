package com.efs.modules.catalog.service;

import com.efs.modules.catalog.dto.TimezoneRequest;
import com.efs.modules.catalog.dto.TimezoneResponse;
import com.efs.shared.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
class TimezoneServiceIntegrationTest {

    @Autowired
    private TimezoneServiceInterface timezoneService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void shouldCreateAndRetrieveTimezoneById() {

        TimezoneRequest request =
                new TimezoneRequest();

        request.setTimezoneCode("America/Guatemala");
        request.setTimezoneName("Guatemala");
        request.setStatus("ACTIVE");

        TimezoneResponse created =
                timezoneService.createTimezone(request);

        assertNotNull(created);
        assertNotNull(created.getTimezoneId());

        assertEquals(
                "America/Guatemala",
                created.getTimezoneCode()
        );

        assertEquals(
                "Guatemala",
                created.getTimezoneName()
        );

        assertEquals(
                "ACTIVE",
                created.getStatus()
        );

        assertNotNull(
                created.getCreatedAt()
        );

        TimezoneResponse retrieved =
                timezoneService.getTimezoneById(
                        created.getTimezoneId()
                );

        assertEquals(
                created.getTimezoneId(),
                retrieved.getTimezoneId()
        );
    }

    @Test
    void shouldRetrieveTimezoneByTimezoneCode() {

        UUID timezoneId =
                UUID.randomUUID();

        insertTimezone(
                timezoneId,
                "America/New_York",
                "New York",
                "ACTIVE"
        );

        TimezoneResponse result =
                timezoneService.getTimezoneByTimezoneCode(
                        "America/New_York"
                );

        assertEquals(
                timezoneId,
                result.getTimezoneId()
        );

        assertEquals(
                "America/New_York",
                result.getTimezoneCode()
        );
    }

    @Test
    void shouldReturnTimezonesByStatusOrderedByName() {

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

        List<TimezoneResponse> results =
                timezoneService.getTimezonesByStatus(
                        "ACTIVE"
                );

        assertEquals(
                2,
                results.size()
        );

        assertEquals(
                "Guatemala",
                results.get(0).getTimezoneName()
        );

        assertEquals(
                "New York",
                results.get(1).getTimezoneName()
        );

        assertTrue(
                results.stream()
                        .allMatch(
                                result ->
                                        "ACTIVE".equals(
                                                result.getStatus()
                                        )
                        )
        );
    }

    @Test
    void shouldReturnAllTimezonesOrderedByName() {

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

        List<TimezoneResponse> results =
                timezoneService.getAllTimezones();

        assertEquals(
                2,
                results.size()
        );

        assertEquals(
                "London",
                results.get(0).getTimezoneName()
        );

        assertEquals(
                "Madrid",
                results.get(1).getTimezoneName()
        );
    }

    @Test
    void shouldRejectUnknownTimezoneId() {

        UUID unknownTimezoneId =
                UUID.randomUUID();

        assertThrows(
                ResourceNotFoundException.class,
                () ->
                        timezoneService.getTimezoneById(
                                unknownTimezoneId
                        )
        );
    }

    @Test
    void shouldRejectUnknownTimezoneCode() {

        assertThrows(
                ResourceNotFoundException.class,
                () ->
                        timezoneService.getTimezoneByTimezoneCode(
                                "Unknown/Timezone"
                        )
        );
    }

    @Test
    void shouldPersistTimezoneCodeAndName() {

        TimezoneRequest request =
                new TimezoneRequest();

        request.setTimezoneCode("Asia/Tokyo");
        request.setTimezoneName("Tokyo");
        request.setStatus("ACTIVE");

        TimezoneResponse created =
                timezoneService.createTimezone(request);

        String timezoneCode =
                jdbcTemplate.queryForObject(
                        """
                        SELECT timezone_code
                        FROM catalog.timezone
                        WHERE timezone_id = ?
                        """,
                        String.class,
                        created.getTimezoneId()
                );

        String timezoneName =
                jdbcTemplate.queryForObject(
                        """
                        SELECT timezone_name
                        FROM catalog.timezone
                        WHERE timezone_id = ?
                        """,
                        String.class,
                        created.getTimezoneId()
                );

        assertEquals(
                "Asia/Tokyo",
                timezoneCode
        );

        assertEquals(
                "Tokyo",
                timezoneName
        );
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