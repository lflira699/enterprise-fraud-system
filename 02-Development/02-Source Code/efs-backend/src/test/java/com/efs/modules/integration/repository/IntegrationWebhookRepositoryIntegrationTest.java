package com.efs.modules.integration.repository;

import com.efs.modules.integration.entity.IntegrationEndpoint;
import com.efs.modules.integration.entity.IntegrationWebhook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
class IntegrationWebhookRepositoryIntegrationTest {

    @Autowired
    private IntegrationWebhookRepository webhookRepository;

    @Autowired
    private IntegrationEndpointRepository endpointRepository;

    private UUID endpointId;

    @BeforeEach
    void setUp() {

        IntegrationEndpoint endpoint =
                new IntegrationEndpoint();

        endpoint.setEndpointCode(
                "EFS-V86-ENDPOINT"
        );

        endpoint.setEndpointName(
                "V86 Webhook Test Endpoint"
        );

        endpoint.setEndpointUrl(
                "https://integration.example.test/v86"
        );

        endpoint.setProtocol(
                "HTTPS"
        );

        endpoint.setAuthenticationType(
                "API_KEY"
        );

        endpoint.setTimeoutSeconds(
                30
        );

        endpoint.setStatus(
                "ACTIVE"
        );

        IntegrationEndpoint savedEndpoint =
                endpointRepository.saveAndFlush(
                        endpoint
                );

        endpointId =
                savedEndpoint.getEndpointId();

        assertNotNull(
                endpointId
        );
    }

    @Test
    void shouldSaveIntegrationWebhook() {

        LocalDateTime lastExecution =
                LocalDateTime.now().minusMinutes(5);

        IntegrationWebhook webhook =
                createWebhook(
                        endpointId,
                        "TRANSACTION_UPDATED",
                        "https://webhook.example.test/transaction",
                        "POST",
                        0,
                        lastExecution,
                        "ACTIVE",
                        LocalDateTime.now()
                );

        IntegrationWebhook saved =
                webhookRepository.saveAndFlush(
                        webhook
                );

        assertNotNull(
                saved.getWebhookId()
        );

        assertEquals(
                endpointId,
                saved.getEndpointId()
        );

        assertEquals(
                "TRANSACTION_UPDATED",
                saved.getEventName()
        );

        assertEquals(
                "https://webhook.example.test/transaction",
                saved.getTargetUrl()
        );

        assertEquals(
                "POST",
                saved.getHttpMethod()
        );

        assertEquals(
                0,
                saved.getRetryCount()
        );

        assertEquals(
                lastExecution,
                saved.getLastExecution()
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
    void shouldFindIntegrationWebhookById() {

        IntegrationWebhook saved =
                webhookRepository.saveAndFlush(
                        createWebhook(
                                endpointId,
                                "CASE_CREATED",
                                "https://webhook.example.test/case",
                                "POST",
                                1,
                                null,
                                "ACTIVE",
                                LocalDateTime.now()
                        )
                );

        Optional<IntegrationWebhook> result =
                webhookRepository.findById(
                        saved.getWebhookId()
                );

        assertTrue(
                result.isPresent()
        );

        assertEquals(
                saved.getWebhookId(),
                result.get().getWebhookId()
        );

        assertEquals(
                endpointId,
                result.get().getEndpointId()
        );

        assertEquals(
                "CASE_CREATED",
                result.get().getEventName()
        );
    }

    @Test
    void shouldFindWebhooksByEndpointId() {

        webhookRepository.save(
                createWebhook(
                        endpointId,
                        "TRANSACTION_CREATED",
                        "https://webhook.example.test/created",
                        "POST",
                        0,
                        null,
                        "ACTIVE",
                        LocalDateTime.now().minusMinutes(2)
                )
        );

        webhookRepository.save(
                createWebhook(
                        endpointId,
                        "TRANSACTION_UPDATED",
                        "https://webhook.example.test/updated",
                        "POST",
                        0,
                        null,
                        "ACTIVE",
                        LocalDateTime.now()
                )
        );

        webhookRepository.flush();

        List<IntegrationWebhook> result =
                webhookRepository.findByEndpointId(
                        endpointId
                );

        assertEquals(
                2,
                result.size()
        );

        assertTrue(
                result.stream()
                        .allMatch(
                                webhook ->
                                        endpointId.equals(
                                                webhook.getEndpointId()
                                        )
                        )
        );
    }

    @Test
    void shouldReturnEmptyListWhenEndpointIdDoesNotExist() {

        List<IntegrationWebhook> result =
                webhookRepository.findByEndpointId(
                        UUID.fromString(
                                "aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee"
                        )
                );

        assertNotNull(
                result
        );

        assertTrue(
                result.isEmpty()
        );
    }

    @Test
    void shouldFindWebhooksByStatusOrderedByCreatedAtDesc() {

        LocalDateTime baseTime =
                LocalDateTime.now();

        webhookRepository.save(
                createWebhook(
                        endpointId,
                        "OLDER_ACTIVE_EVENT",
                        "https://webhook.example.test/older",
                        "POST",
                        0,
                        null,
                        "ACTIVE",
                        baseTime.minusMinutes(2)
                )
        );

        webhookRepository.save(
                createWebhook(
                        endpointId,
                        "NEWER_ACTIVE_EVENT",
                        "https://webhook.example.test/newer",
                        "POST",
                        0,
                        null,
                        "ACTIVE",
                        baseTime
                )
        );

        webhookRepository.save(
                createWebhook(
                        endpointId,
                        "INACTIVE_EVENT",
                        "https://webhook.example.test/inactive",
                        "POST",
                        0,
                        null,
                        "INACTIVE",
                        baseTime.plusMinutes(1)
                )
        );

        webhookRepository.flush();

        List<IntegrationWebhook> result =
                webhookRepository
                        .findByStatusOrderByCreatedAtDesc(
                                "ACTIVE"
                        );

        assertEquals(
                2,
                result.size()
        );

        assertEquals(
                "NEWER_ACTIVE_EVENT",
                result.get(0).getEventName()
        );

        assertEquals(
                "OLDER_ACTIVE_EVENT",
                result.get(1).getEventName()
        );

        assertTrue(
                result.stream()
                        .allMatch(
                                webhook ->
                                        "ACTIVE".equals(
                                                webhook.getStatus()
                                        )
                        )
        );
    }

    @Test
    void shouldReturnEmptyListWhenStatusDoesNotExist() {

        webhookRepository.saveAndFlush(
                createWebhook(
                        endpointId,
                        "ACTIVE_EVENT",
                        "https://webhook.example.test/active",
                        "POST",
                        0,
                        null,
                        "ACTIVE",
                        LocalDateTime.now()
                )
        );

        List<IntegrationWebhook> result =
                webhookRepository
                        .findByStatusOrderByCreatedAtDesc(
                                "UNKNOWN"
                        );

        assertNotNull(
                result
        );

        assertTrue(
                result.isEmpty()
        );
    }

    @Test
    void shouldPersistRetryCountAndLastExecution() {

        LocalDateTime lastExecution =
                LocalDateTime.now().minusMinutes(10);

        IntegrationWebhook saved =
                webhookRepository.saveAndFlush(
                        createWebhook(
                                endpointId,
                                "RETRY_EVENT",
                                "https://webhook.example.test/retry",
                                "POST",
                                3,
                                lastExecution,
                                "ACTIVE",
                                LocalDateTime.now()
                        )
                );

        assertEquals(
                3,
                saved.getRetryCount()
        );

        assertEquals(
                lastExecution,
                saved.getLastExecution()
        );
    }

    private IntegrationWebhook createWebhook(
            UUID endpointId,
            String eventName,
            String targetUrl,
            String httpMethod,
            Integer retryCount,
            LocalDateTime lastExecution,
            String status,
            LocalDateTime createdAt) {

        IntegrationWebhook webhook =
                new IntegrationWebhook();

        webhook.setEndpointId(
                endpointId
        );

        webhook.setEventName(
                eventName
        );

        webhook.setTargetUrl(
                targetUrl
        );

        webhook.setHttpMethod(
                httpMethod
        );

        webhook.setRetryCount(
                retryCount
        );

        webhook.setLastExecution(
                lastExecution
        );

        webhook.setStatus(
                status
        );

        webhook.setCreatedAt(
                createdAt
        );

        return webhook;
    }
}