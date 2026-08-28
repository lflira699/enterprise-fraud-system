package com.efs.modules.casemanagement.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class CaseAlertRepositoryIntegrationTest {

    private static final UUID ORGANIZATION_ID =
            UUID.fromString(
                    "91919191-aaaa-bbbb-cccc-919191919191"
            );

    private static final UUID CASE_ID =
            UUID.fromString(
                    "92929292-aaaa-bbbb-cccc-929292929292"
            );

    private static final UUID NON_EXISTENT_SOURCE_ALERT_ID =
            UUID.fromString(
                    "93939393-aaaa-bbbb-cccc-939393939393"
            );

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {

        jdbcTemplate.update(
                """
                INSERT INTO administration.organization (
                    organization_id,
                    organization_code,
                    legal_name,
                    country_code,
                    timezone,
                    status
                )
                VALUES (?, ?, ?, ?, ?, ?)
                """,
                ORGANIZATION_ID,
                "EFS-V122-ORG",
                "EFS V122 Organization",
                "GT",
                "America/Guatemala",
                "ACTIVE"
        );

        jdbcTemplate.update(
                """
                INSERT INTO case_management.case (
                    case_id,
                    case_number,
                    organization_id,
                    case_type,
                    category,
                    severity,
                    priority,
                    current_status,
                    created_at,
                    updated_at
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """,
                CASE_ID,
                "EFS-V122-CASE",
                ORGANIZATION_ID,
                "FRAUD",
                "TRANSACTION",
                "HIGH",
                "HIGH",
                "OPEN",
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    @Test
    void shouldRejectCaseAlertWithNonExistentSourceAlert() {

        assertThrows(
                DataIntegrityViolationException.class,
                () -> jdbcTemplate.update(
                        """
                        INSERT INTO case_management.case_alert (
                            case_id,
                            alert_type,
                            alert_source,
                            severity,
                            generated_at,
                            source_alert_id
                        )
                        VALUES (?, ?, ?, ?, ?, ?)
                        """,
                        CASE_ID,
                        "FRAUD",
                        "ALERT_DOMAIN",
                        "HIGH",
                        LocalDateTime.now(),
                        NON_EXISTENT_SOURCE_ALERT_ID
                )
        );
    }
}