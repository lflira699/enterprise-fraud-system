package com.efs.modules.catalog.service;

import com.efs.modules.catalog.dto.CountryRequest;
import com.efs.modules.catalog.dto.CountryResponse;
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
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
class CountryServiceIntegrationTest {

    @Autowired
    private CountryServiceInterface countryService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void shouldCreateAndRetrieveCountryById() {

        CountryRequest request =
                new CountryRequest();

        request.setCountryCode("GT");
        request.setAlpha3Code("GTM");
        request.setNumericCode("320");
        request.setCountryName("Guatemala");
        request.setStatus("ACTIVE");

        CountryResponse created =
                countryService.createCountry(request);

        assertNotNull(created);
        assertNotNull(created.getCountryId());

        assertEquals(
                "GT",
                created.getCountryCode()
        );

        assertEquals(
                "GTM",
                created.getAlpha3Code()
        );

        assertEquals(
                "320",
                created.getNumericCode()
        );

        assertEquals(
                "Guatemala",
                created.getCountryName()
        );

        assertEquals(
                "ACTIVE",
                created.getStatus()
        );

        assertNotNull(
                created.getCreatedAt()
        );

        CountryResponse retrieved =
                countryService.getCountryById(
                        created.getCountryId()
                );

        assertEquals(
                created.getCountryId(),
                retrieved.getCountryId()
        );
    }

    @Test
    void shouldAllowNumericCodeToBeNull() {

        CountryRequest request =
                new CountryRequest();

        request.setCountryCode("ZZ");
        request.setAlpha3Code("ZZZ");
        request.setCountryName("Test Country");
        request.setStatus("ACTIVE");

        CountryResponse created =
                countryService.createCountry(request);

        assertNotNull(
                created.getCountryId()
        );

        assertNull(
                created.getNumericCode()
        );
    }

    @Test
    void shouldRetrieveCountryByCountryCode() {

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

        CountryResponse result =
                countryService.getCountryByCountryCode(
                        "US"
                );

        assertEquals(
                countryId,
                result.getCountryId()
        );

        assertEquals(
                "US",
                result.getCountryCode()
        );
    }

    @Test
    void shouldRetrieveCountryByAlpha3Code() {

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

        CountryResponse result =
                countryService.getCountryByAlpha3Code(
                        "MEX"
                );

        assertEquals(
                countryId,
                result.getCountryId()
        );

        assertEquals(
                "MEX",
                result.getAlpha3Code()
        );
    }

    @Test
    void shouldRetrieveCountryByNumericCode() {

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

        CountryResponse result =
                countryService.getCountryByNumericCode(
                        "188"
                );

        assertEquals(
                countryId,
                result.getCountryId()
        );

        assertEquals(
                "188",
                result.getNumericCode()
        );
    }

    @Test
    void shouldReturnCountriesByStatusOrderedByName() {

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

        List<CountryResponse> results =
                countryService.getCountriesByStatus(
                        "ACTIVE"
                );

        assertEquals(
                2,
                results.size()
        );

        assertEquals(
                "El Salvador",
                results.get(0).getCountryName()
        );

        assertEquals(
                "Honduras",
                results.get(1).getCountryName()
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
    void shouldReturnAllCountriesOrderedByName() {

        insertCountry(
                UUID.randomUUID(),
                "PA",
                "PAN",
                "591",
                "Panama",
                "ACTIVE"
        );

        insertCountry(
                UUID.randomUUID(),
                "BZ",
                "BLZ",
                "084",
                "Belize",
                "ACTIVE"
        );

        List<CountryResponse> results =
                countryService.getAllCountries();

        assertEquals(
                2,
                results.size()
        );

        assertEquals(
                "Belize",
                results.get(0).getCountryName()
        );

        assertEquals(
                "Panama",
                results.get(1).getCountryName()
        );
    }

    @Test
    void shouldRejectUnknownCountryId() {

        UUID unknownCountryId =
                UUID.randomUUID();

        assertThrows(
                ResourceNotFoundException.class,
                () ->
                        countryService.getCountryById(
                                unknownCountryId
                        )
        );
    }

    @Test
    void shouldRejectUnknownCountryCode() {

        assertThrows(
                ResourceNotFoundException.class,
                () ->
                        countryService.getCountryByCountryCode(
                                "XX"
                        )
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