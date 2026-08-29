package com.efs.modules.catalog.service;

import com.efs.modules.catalog.dto.CurrencyRequest;
import com.efs.modules.catalog.dto.CurrencyResponse;
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
class CurrencyServiceIntegrationTest {

    @Autowired
    private CurrencyServiceInterface currencyService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void shouldCreateAndRetrieveCurrencyById() {

        CurrencyRequest request =
                new CurrencyRequest();

        request.setCurrencyCode("GTQ");
        request.setNumericCode("320");
        request.setCurrencyName("Guatemalan Quetzal");
        request.setMinorUnit((short) 2);
        request.setStatus("ACTIVE");

        CurrencyResponse created =
                currencyService.createCurrency(request);

        assertNotNull(created);
        assertNotNull(created.getCurrencyId());

        assertEquals(
                "GTQ",
                created.getCurrencyCode()
        );

        assertEquals(
                "320",
                created.getNumericCode()
        );

        assertEquals(
                "Guatemalan Quetzal",
                created.getCurrencyName()
        );

        assertEquals(
                (short) 2,
                created.getMinorUnit()
        );

        assertEquals(
                "ACTIVE",
                created.getStatus()
        );

        assertNotNull(
                created.getCreatedAt()
        );

        CurrencyResponse retrieved =
                currencyService.getCurrencyById(
                        created.getCurrencyId()
                );

        assertEquals(
                created.getCurrencyId(),
                retrieved.getCurrencyId()
        );
    }

    @Test
    void shouldAllowNumericCodeToBeNull() {

        CurrencyRequest request =
                new CurrencyRequest();

        request.setCurrencyCode("ZZZ");
        request.setCurrencyName("Test Currency");
        request.setMinorUnit((short) 2);
        request.setStatus("ACTIVE");

        CurrencyResponse created =
                currencyService.createCurrency(request);

        assertNotNull(
                created.getCurrencyId()
        );

        assertNull(
                created.getNumericCode()
        );
    }

    @Test
    void shouldRetrieveCurrencyByCurrencyCode() {

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

        CurrencyResponse result =
                currencyService.getCurrencyByCurrencyCode(
                        "USD"
                );

        assertEquals(
                currencyId,
                result.getCurrencyId()
        );

        assertEquals(
                "USD",
                result.getCurrencyCode()
        );
    }

    @Test
    void shouldRetrieveCurrencyByNumericCode() {

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

        CurrencyResponse result =
                currencyService.getCurrencyByNumericCode(
                        "978"
                );

        assertEquals(
                currencyId,
                result.getCurrencyId()
        );

        assertEquals(
                "978",
                result.getNumericCode()
        );
    }

    @Test
    void shouldReturnCurrenciesByStatusOrderedByName() {

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

        List<CurrencyResponse> results =
                currencyService.getCurrenciesByStatus(
                        "ACTIVE"
                );

        assertEquals(
                2,
                results.size()
        );

        assertEquals(
                "Canadian Dollar",
                results.get(0).getCurrencyName()
        );

        assertEquals(
                "Mexican Peso",
                results.get(1).getCurrencyName()
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
    void shouldReturnAllCurrenciesOrderedByName() {

        insertCurrency(
                UUID.randomUUID(),
                "JPY",
                "392",
                "Japanese Yen",
                (short) 0,
                "ACTIVE"
        );

        insertCurrency(
                UUID.randomUUID(),
                "GBP",
                "826",
                "Pound Sterling",
                (short) 2,
                "ACTIVE"
        );

        List<CurrencyResponse> results =
                currencyService.getAllCurrencies();

        assertEquals(
                2,
                results.size()
        );

        assertEquals(
                "Japanese Yen",
                results.get(0).getCurrencyName()
        );

        assertEquals(
                "Pound Sterling",
                results.get(1).getCurrencyName()
        );
    }

    @Test
    void shouldRejectUnknownCurrencyId() {

        UUID unknownCurrencyId =
                UUID.randomUUID();

        assertThrows(
                ResourceNotFoundException.class,
                () ->
                        currencyService.getCurrencyById(
                                unknownCurrencyId
                        )
        );
    }

    @Test
    void shouldRejectUnknownCurrencyCode() {

        assertThrows(
                ResourceNotFoundException.class,
                () ->
                        currencyService.getCurrencyByCurrencyCode(
                                "XXX"
                        )
        );
    }

    @Test
    void shouldRejectUnknownNumericCode() {

        assertThrows(
                ResourceNotFoundException.class,
                () ->
                        currencyService.getCurrencyByNumericCode(
                                "999"
                        )
        );
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