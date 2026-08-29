package com.efs.modules.catalog.service;

import com.efs.modules.catalog.dto.LanguageRequest;
import com.efs.modules.catalog.dto.LanguageResponse;
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
class LanguageServiceIntegrationTest {

    @Autowired
    private LanguageServiceInterface languageService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void shouldCreateAndRetrieveLanguageById() {

        LanguageRequest request =
                new LanguageRequest();

        request.setLanguageCode("es");
        request.setAlpha3Code("spa");
        request.setLanguageName("Spanish");
        request.setStatus("ACTIVE");

        LanguageResponse created =
                languageService.createLanguage(request);

        assertNotNull(created);
        assertNotNull(created.getLanguageId());

        assertEquals(
                "es",
                created.getLanguageCode()
        );

        assertEquals(
                "spa",
                created.getAlpha3Code()
        );

        assertEquals(
                "Spanish",
                created.getLanguageName()
        );

        assertEquals(
                "ACTIVE",
                created.getStatus()
        );

        assertNotNull(
                created.getCreatedAt()
        );

        LanguageResponse retrieved =
                languageService.getLanguageById(
                        created.getLanguageId()
                );

        assertEquals(
                created.getLanguageId(),
                retrieved.getLanguageId()
        );
    }

    @Test
    void shouldRetrieveLanguageByLanguageCode() {

        UUID languageId =
                UUID.randomUUID();

        insertLanguage(
                languageId,
                "en",
                "eng",
                "English",
                "ACTIVE"
        );

        LanguageResponse result =
                languageService.getLanguageByLanguageCode(
                        "en"
                );

        assertEquals(
                languageId,
                result.getLanguageId()
        );

        assertEquals(
                "en",
                result.getLanguageCode()
        );

        assertEquals(
                "eng",
                result.getAlpha3Code()
        );
    }

    @Test
    void shouldRetrieveLanguageByAlpha3Code() {

        UUID languageId =
                UUID.randomUUID();

        insertLanguage(
                languageId,
                "fr",
                "fra",
                "French",
                "ACTIVE"
        );

        LanguageResponse result =
                languageService.getLanguageByAlpha3Code(
                        "fra"
                );

        assertEquals(
                languageId,
                result.getLanguageId()
        );

        assertEquals(
                "fra",
                result.getAlpha3Code()
        );
    }

    @Test
    void shouldReturnLanguagesByStatusOrderedByName() {

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

        List<LanguageResponse> results =
                languageService.getLanguagesByStatus(
                        "ACTIVE"
                );

        assertEquals(
                2,
                results.size()
        );

        assertEquals(
                "German",
                results.get(0).getLanguageName()
        );

        assertEquals(
                "Italian",
                results.get(1).getLanguageName()
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
    void shouldReturnAllLanguagesOrderedByName() {

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

        List<LanguageResponse> results =
                languageService.getAllLanguages();

        assertEquals(
                2,
                results.size()
        );

        assertEquals(
                "Portuguese",
                results.get(0).getLanguageName()
        );

        assertEquals(
                "Spanish",
                results.get(1).getLanguageName()
        );
    }

    @Test
    void shouldRejectUnknownLanguageId() {

        UUID unknownLanguageId =
                UUID.randomUUID();

        assertThrows(
                ResourceNotFoundException.class,
                () ->
                        languageService.getLanguageById(
                                unknownLanguageId
                        )
        );
    }

    @Test
    void shouldRejectUnknownLanguageCode() {

        assertThrows(
                ResourceNotFoundException.class,
                () ->
                        languageService.getLanguageByLanguageCode(
                                "zz"
                        )
        );
    }

    @Test
    void shouldRejectUnknownAlpha3Code() {

        assertThrows(
                ResourceNotFoundException.class,
                () ->
                        languageService.getLanguageByAlpha3Code(
                                "zzz"
                        )
        );
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