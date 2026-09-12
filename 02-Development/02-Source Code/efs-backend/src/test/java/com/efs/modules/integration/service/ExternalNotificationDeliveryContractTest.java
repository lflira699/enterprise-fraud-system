package com.efs.modules.integration.service;

import com.efs.modules.integration.dto.ExternalNotificationDeliveryRequest;
import com.efs.modules.integration.dto.ExternalNotificationDeliveryResult;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ExternalNotificationDeliveryContractTest {

    @Test
    void shouldPreserveExternalNotificationDeliveryRequestContract() {

        UUID notificationDeliveryId =
                UUID.randomUUID();

        UUID correlationId =
                UUID.randomUUID();

        UUID organizationId =
                UUID.randomUUID();

        UUID tenantId =
                UUID.randomUUID();

        UUID recipientUserId =
                UUID.randomUUID();

        ExternalNotificationDeliveryRequest request =
                new ExternalNotificationDeliveryRequest();

        request.setNotificationDeliveryId(
                notificationDeliveryId
        );

        request.setCorrelationId(
                correlationId
        );

        request.setOrganizationId(
                organizationId
        );

        request.setTenantId(
                tenantId
        );

        request.setChannel(
                "EMAIL"
        );

        request.setRecipientUserId(
                recipientUserId
        );

        request.setDestination(
                "recipient@example.com"
        );

        request.setSubject(
                "Subject"
        );

        request.setBody(
                "Body"
        );

        assertEquals(
                notificationDeliveryId,
                request.getNotificationDeliveryId()
        );

        assertEquals(
                correlationId,
                request.getCorrelationId()
        );

        assertEquals(
                organizationId,
                request.getOrganizationId()
        );

        assertEquals(
                tenantId,
                request.getTenantId()
        );

        assertEquals(
                "EMAIL",
                request.getChannel()
        );

        assertEquals(
                recipientUserId,
                request.getRecipientUserId()
        );

        assertEquals(
                "recipient@example.com",
                request.getDestination()
        );

        assertEquals(
                "Subject",
                request.getSubject()
        );

        assertEquals(
                "Body",
                request.getBody()
        );
    }

    @Test
    void shouldPreserveExternalNotificationDeliveryResultContract() {

        ExternalNotificationDeliveryResult result =
                new ExternalNotificationDeliveryResult();

        assertFalse(
                result.isDelivered()
        );

        result.setDelivered(
                true
        );

        result.setDeliveryReference(
                "provider-reference"
        );

        result.setDeliveryResult(
                "DELIVERED"
        );

        assertTrue(
                result.isDelivered()
        );

        assertEquals(
                "provider-reference",
                result.getDeliveryReference()
        );

        assertEquals(
                "DELIVERED",
                result.getDeliveryResult()
        );
    }

    @Test
    void shouldExposeProviderNeutralServiceContract() {

        ExternalNotificationDeliveryServiceInterface service =
                request -> {

                    ExternalNotificationDeliveryResult result =
                            new ExternalNotificationDeliveryResult();

                    result.setDelivered(
                            true
                    );

                    return result;
                };

        ExternalNotificationDeliveryResult result =
                service.deliver(
                        new ExternalNotificationDeliveryRequest()
                );

        assertTrue(
                result.isDelivered()
        );
    }

    @Test
    void shouldExposeScopedAdapterContract() {

        java.util.UUID organizationId =
                java.util.UUID.randomUUID();

        java.util.UUID tenantId =
                java.util.UUID.randomUUID();

        ExternalNotificationDeliveryAdapter adapter =
                new ExternalNotificationDeliveryAdapter() {

                    @Override
                    public boolean supports(
                            java.util.UUID candidateOrganizationId,
                            java.util.UUID candidateTenantId,
                            String channel) {

                        return organizationId.equals(
                                candidateOrganizationId
                        )
                                && tenantId.equals(
                                        candidateTenantId
                                )
                                && "EMAIL".equals(
                                        channel
                                );
                    }

                    @Override
                    public ExternalNotificationDeliveryResult deliver(
                            ExternalNotificationDeliveryRequest request) {

                        ExternalNotificationDeliveryResult result =
                                new ExternalNotificationDeliveryResult();

                        result.setDelivered(
                                true
                        );

                        return result;
                    }
                };

        assertTrue(
                adapter.supports(
                        organizationId,
                        tenantId,
                        "EMAIL"
                )
        );

        assertFalse(
                adapter.supports(
                        organizationId,
                        tenantId,
                        "SMS"
                )
        );

        assertFalse(
                adapter.supports(
                        java.util.UUID.randomUUID(),
                        tenantId,
                        "EMAIL"
                )
        );

        assertTrue(
                adapter.deliver(
                        new ExternalNotificationDeliveryRequest()
                ).isDelivered()
        );
    }
}