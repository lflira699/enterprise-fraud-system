package com.efs.modules.reporting.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class ReportExportDatabaseIntegrationTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void reportExportPermissionShouldExistWithoutNewReportingPersistence() {

        Map<String, Object> permission =
                jdbcTemplate.queryForMap(
                        """
                        SELECT permission_code,
                               permission_name,
                               resource,
                               action
                        FROM administration.permission
                        WHERE permission_code = 'report.export'
                        """
                );

        assertEquals(
                "report.export",
                permission.get(
                        "permission_code"
                )
        );

        assertEquals(
                "Export Generated Reports",
                permission.get(
                        "permission_name"
                )
        );

        assertEquals(
                "report",
                permission.get(
                        "resource"
                )
        );

        assertEquals(
                "export",
                permission.get(
                        "action"
                )
        );

        Long generatedReportTableCount =
                jdbcTemplate.queryForObject(
                        """
                        SELECT COUNT(*)
                        FROM information_schema.tables
                        WHERE table_schema = 'reporting'
                          AND table_name = 'generated_report'
                          AND table_type = 'BASE TABLE'
                        """,
                        Long.class
                );

        assertEquals(
                1L,
                generatedReportTableCount.longValue()
        );

        Long reportingTableCount =
                jdbcTemplate.queryForObject(
                        """
                        SELECT COUNT(*)
                        FROM information_schema.tables
                        WHERE table_schema = 'reporting'
                          AND table_type = 'BASE TABLE'
                        """,
                        Long.class
                );

        assertEquals(
                1L,
                reportingTableCount.longValue()
        );
    }
}