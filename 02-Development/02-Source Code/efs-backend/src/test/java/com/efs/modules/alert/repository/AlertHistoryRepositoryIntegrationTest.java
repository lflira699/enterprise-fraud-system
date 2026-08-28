package com.efs.modules.alert.repository;

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
class AlertHistoryRepositoryIntegrationTest {

    private static final UUID NON_EXISTENT_ALERT_ID =
            UUID.fromString(
                    "88888888-aaaa-bbbb-cccc-888888888888"
            );

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void shouldRejectHistoryWithNonExistentAlert() {

        assertThrows(
                DataIntegrityViolationException.class,
                () -> jdbcTemplate.update(
                        """
                        INSERT INTO alert.alert_history (
                            alert_id,
                            action_type,
                            previous_status,
                            new_status,
                            changed_at
                        )
                        VALUES (?, ?, ?, ?, ?)
                        """,
                        NON_EXISTENT_ALERT_ID,
                        "STATUS_CHANGE",
                        "NEW",
                        "IN_PROGRESS",
                        LocalDateTime.now()
                )
        );
    }
}