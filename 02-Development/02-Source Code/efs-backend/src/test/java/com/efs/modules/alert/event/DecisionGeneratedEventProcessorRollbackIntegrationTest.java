package com.efs.modules.alert.event;

import com.efs.shared.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class DecisionGeneratedEventProcessorRollbackIntegrationTest {

    @Autowired
    private DecisionGeneratedEventProcessor processor;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void shouldRollbackProcessedDomainEventWhenProcessingFails() {

        UUID messageId =
                UUID.randomUUID();

        UUID correlationId =
                UUID.randomUUID();

        UUID missingDecisionId =
                UUID.randomUUID();

        DecisionGeneratedEventMessage message =
                new DecisionGeneratedEventMessage(
                        messageId,
                        correlationId,
                        missingDecisionId
                );

        assertThrows(
                ResourceNotFoundException.class,
                () -> processor.process(
                        message
                )
        );

        Integer processedEventCount =
                jdbcTemplate.queryForObject(
                        """
                        SELECT COUNT(*)
                        FROM integration.processed_domain_event
                        WHERE message_id = ?
                          AND consumer_name = ?
                          AND event_type = ?
                        """,
                        Integer.class,
                        messageId,
                        "Alert Engine",
                        "DecisionGenerated"
                );

        assertEquals(
                0,
                processedEventCount
        );
    }
}
