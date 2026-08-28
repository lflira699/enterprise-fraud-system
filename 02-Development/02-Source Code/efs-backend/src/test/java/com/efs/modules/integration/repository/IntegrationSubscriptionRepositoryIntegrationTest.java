package com.efs.modules.integration.repository;

import com.efs.modules.integration.entity.IntegrationEvent;
import com.efs.modules.integration.entity.IntegrationSubscription;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
class IntegrationSubscriptionRepositoryIntegrationTest {

    @Autowired
    private IntegrationSubscriptionRepository integrationSubscriptionRepository;

    @Autowired
    private IntegrationEventRepository integrationEventRepository;

    private UUID eventId;

    @BeforeEach
    void setUp() {

        IntegrationEvent event =
                new IntegrationEvent();

        event.setEventName(
                "V89_SUBSCRIPTION_TEST_EVENT"
        );

        event.setEventVersion(
                "1.0"
        );

        event.setEventPayload(
                Map.of(
                        "test",
                        "V89"
                )
        );

        event.setPublishedAt(
                LocalDateTime.now()
        );

        event.setPublisher(
                "EFS"
        );

        event.setStatus(
                "PUBLISHED"
        );

        IntegrationEvent savedEvent =
                integrationEventRepository.saveAndFlush(
                        event
                );

        eventId =
                savedEvent.getEventId();

        assertNotNull(
                eventId
        );
    }

    @Test
    void shouldSaveIntegrationSubscription() {

        IntegrationSubscription subscription =
                createSubscription(
                        eventId,
                        "fraud-investigation",
                        "EVENT",
                        "ACTIVE"
                );

        IntegrationSubscription saved =
                integrationSubscriptionRepository.saveAndFlush(
                        subscription
                );

        assertNotNull(
                saved.getSubscriptionId()
        );

        assertEquals(
                eventId,
                saved.getEventId()
        );

        assertEquals(
                "fraud-investigation",
                saved.getSubscriber()
        );

        assertEquals(
                "EVENT",
                saved.getDeliveryType()
        );

        assertEquals(
                "ACTIVE",
                saved.getStatus()
        );

        assertNotNull(
                saved.getCreatedAt()
        );
    }

    @Test
    void shouldFindIntegrationSubscriptionById() {

        IntegrationSubscription saved =
                integrationSubscriptionRepository.saveAndFlush(
                        createSubscription(
                                eventId,
                                "case-management",
                                "EVENT",
                                "ACTIVE"
                        )
                );

        IntegrationSubscription found =
                integrationSubscriptionRepository
                        .findById(
                                saved.getSubscriptionId()
                        )
                        .orElseThrow();

        assertEquals(
                saved.getSubscriptionId(),
                found.getSubscriptionId()
        );

        assertEquals(
                eventId,
                found.getEventId()
        );

        assertEquals(
                "case-management",
                found.getSubscriber()
        );
    }

    @Test
    void shouldFindSubscriptionsByEventId() {

        integrationSubscriptionRepository.saveAndFlush(
                createSubscription(
                        eventId,
                        "subscriber-one",
                        "EVENT",
                        "ACTIVE"
                )
        );

        integrationSubscriptionRepository.saveAndFlush(
                createSubscription(
                        eventId,
                        "subscriber-two",
                        "WEBHOOK",
                        "ACTIVE"
                )
        );

        List<IntegrationSubscription> subscriptions =
                integrationSubscriptionRepository
                        .findByEventId(
                                eventId
                        );

        assertEquals(
                2,
                subscriptions.size()
        );

        assertTrue(
                subscriptions.stream()
                        .allMatch(
                                subscription ->
                                        eventId.equals(
                                                subscription.getEventId()
                                        )
                        )
        );
    }

    @Test
    void shouldFindSubscriptionsByStatusOrderedByCreatedAtDesc() {

        LocalDateTime baseTime =
                LocalDateTime.now();

        IntegrationSubscription older =
                createSubscription(
                        eventId,
                        "older-subscriber",
                        "EVENT",
                        "V89_TEST_ACTIVE"
                );

        older.setCreatedAt(
                baseTime.minusMinutes(10)
        );

        integrationSubscriptionRepository.saveAndFlush(
                older
        );

        IntegrationSubscription newer =
                createSubscription(
                        eventId,
                        "newer-subscriber",
                        "EVENT",
                        "V89_TEST_ACTIVE"
                );

        newer.setCreatedAt(
                baseTime
        );

        integrationSubscriptionRepository.saveAndFlush(
                newer
        );

        List<IntegrationSubscription> subscriptions =
                integrationSubscriptionRepository
                        .findByStatusOrderByCreatedAtDesc(
                                "V89_TEST_ACTIVE"
                        );

        assertEquals(
                2,
                subscriptions.size()
        );

        assertEquals(
                "newer-subscriber",
                subscriptions.get(0).getSubscriber()
        );

        assertEquals(
                "older-subscriber",
                subscriptions.get(1).getSubscriber()
        );

        assertTrue(
                subscriptions.stream()
                        .allMatch(
                                subscription ->
                                        "V89_TEST_ACTIVE".equals(
                                                subscription.getStatus()
                                        )
                        )
        );
    }

    @Test
    void shouldReturnEmptyListWhenEventHasNoSubscriptions() {

        List<IntegrationSubscription> subscriptions =
                integrationSubscriptionRepository
                        .findByEventId(
                                UUID.fromString(
                                        "aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee"
                                )
                        );

        assertNotNull(
                subscriptions
        );

        assertTrue(
                subscriptions.isEmpty()
        );
    }

    @Test
    void shouldReturnEmptyListWhenStatusDoesNotExist() {

        List<IntegrationSubscription> subscriptions =
                integrationSubscriptionRepository
                        .findByStatusOrderByCreatedAtDesc(
                                "NON_EXISTENT_V89_STATUS"
                        );

        assertNotNull(
                subscriptions
        );

        assertTrue(
                subscriptions.isEmpty()
        );
    }

    private IntegrationSubscription createSubscription(
            UUID eventId,
            String subscriber,
            String deliveryType,
            String status) {

        IntegrationSubscription subscription =
                new IntegrationSubscription();

        subscription.setEventId(
                eventId
        );

        subscription.setSubscriber(
                subscriber
        );

        subscription.setDeliveryType(
                deliveryType
        );

        subscription.setStatus(
                status
        );

        subscription.setCreatedAt(
                LocalDateTime.now()
        );

        return subscription;
    }
}