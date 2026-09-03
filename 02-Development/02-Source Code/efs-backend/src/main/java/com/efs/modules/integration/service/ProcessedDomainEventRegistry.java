package com.efs.modules.integration.service;

import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class ProcessedDomainEventRegistry {

    private final EntityManager entityManager;

    public ProcessedDomainEventRegistry(
            EntityManager entityManager) {

        this.entityManager =
                entityManager;
    }

    @Transactional(
            propagation = Propagation.MANDATORY
    )
    public boolean register(
            UUID messageId,
            String consumerName,
            String eventType) {

        if (messageId == null) {
            throw new IllegalArgumentException(
                    "Domain event messageId is required"
            );
        }

        if (consumerName == null
                || consumerName.isBlank()) {

            throw new IllegalArgumentException(
                    "Domain event consumer name is required"
            );
        }

        if (eventType == null
                || eventType.isBlank()) {

            throw new IllegalArgumentException(
                    "Domain event type is required"
            );
        }

        int insertedRows =
                entityManager
                        .createNativeQuery(
                                """
                                INSERT INTO integration.processed_domain_event (
                                    message_id,
                                    consumer_name,
                                    event_type
                                )
                                VALUES (
                                    :messageId,
                                    :consumerName,
                                    :eventType
                                )
                                ON CONFLICT (
                                    message_id,
                                    consumer_name
                                )
                                DO NOTHING
                                """
                        )
                        .setParameter(
                                "messageId",
                                messageId
                        )
                        .setParameter(
                                "consumerName",
                                consumerName
                        )
                        .setParameter(
                                "eventType",
                                eventType
                        )
                        .executeUpdate();

        return insertedRows == 1;
    }
}