package com.efs.modules.integration.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
class ProcessedDomainEventRegistryIntegrationTest {

    @Autowired
    private ProcessedDomainEventRegistry registry;

    @Test
    void shouldRegisterDomainEventForConsumer() {

        boolean registered =
                registry.register(
                        UUID.randomUUID(),
                        "Alert Engine",
                        "DecisionGenerated"
                );

        assertTrue(
                registered
        );
    }

    @Test
    void shouldRejectDuplicateMessageForSameConsumer() {

        UUID messageId =
                UUID.randomUUID();

        boolean firstRegistration =
                registry.register(
                        messageId,
                        "Alert Engine",
                        "DecisionGenerated"
                );

        boolean duplicateRegistration =
                registry.register(
                        messageId,
                        "Alert Engine",
                        "DecisionGenerated"
                );

        assertTrue(
                firstRegistration
        );

        assertFalse(
                duplicateRegistration
        );
    }

    @Test
    void shouldAllowSameMessageForDifferentConsumers() {

        UUID messageId =
                UUID.randomUUID();

        boolean alertEngineRegistration =
                registry.register(
                        messageId,
                        "Alert Engine",
                        "DecisionGenerated"
                );

        boolean otherConsumerRegistration =
                registry.register(
                        messageId,
                        "Other Consumer",
                        "DecisionGenerated"
                );

        assertTrue(
                alertEngineRegistration
        );

        assertTrue(
                otherConsumerRegistration
        );
    }

    @Test
    void shouldRejectMissingMessageId() {

        assertThrows(
                IllegalArgumentException.class,
                () -> registry.register(
                        null,
                        "Alert Engine",
                        "DecisionGenerated"
                )
        );
    }

    @Test
    void shouldRejectMissingConsumerName() {

        assertThrows(
                IllegalArgumentException.class,
                () -> registry.register(
                        UUID.randomUUID(),
                        " ",
                        "DecisionGenerated"
                )
        );
    }

    @Test
    void shouldRejectMissingEventType() {

        assertThrows(
                IllegalArgumentException.class,
                () -> registry.register(
                        UUID.randomUUID(),
                        "Alert Engine",
                        " "
                )
        );
    }
}