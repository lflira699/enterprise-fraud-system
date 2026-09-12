package com.efs.modules.integration.service;

import com.efs.modules.integration.dto.ExternalNotificationDeliveryRequest;
import com.efs.modules.integration.dto.ExternalNotificationDeliveryResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExternalNotificationDeliveryServiceTest {

    @Mock
    private ExternalNotificationDeliveryAdapter firstAdapter;

    @Mock
    private ExternalNotificationDeliveryAdapter secondAdapter;

    @Test
    void shouldDelegateToExactlyOneMatchingAdapter() {

        UUID organizationId =
                UUID.randomUUID();

        UUID tenantId =
                UUID.randomUUID();

        ExternalNotificationDeliveryRequest request =
                createRequest(
                        organizationId,
                        tenantId,
                        "EMAIL"
                );

        ExternalNotificationDeliveryResult expected =
                new ExternalNotificationDeliveryResult();

        expected.setDelivered(
                true
        );

        when(
                firstAdapter.supports(
                        organizationId,
                        tenantId,
                        "EMAIL"
                )
        ).thenReturn(
                true
        );

        when(
                secondAdapter.supports(
                        organizationId,
                        tenantId,
                        "EMAIL"
                )
        ).thenReturn(
                false
        );

        when(
                firstAdapter.deliver(
                        request
                )
        ).thenReturn(
                expected
        );

        ExternalNotificationDeliveryService service =
                new ExternalNotificationDeliveryService(
                        List.of(
                                firstAdapter,
                                secondAdapter
                        )
                );

        ExternalNotificationDeliveryResult result =
                service.deliver(
                        request
                );

        assertSame(
                expected,
                result
        );

        verify(
                firstAdapter
        ).deliver(
                request
        );

        verify(
                secondAdapter,
                never()
        ).deliver(
                request
        );
    }

    @Test
    void shouldRejectWhenNoAdapterMatches() {

        UUID organizationId =
                UUID.randomUUID();

        UUID tenantId =
                UUID.randomUUID();

        ExternalNotificationDeliveryRequest request =
                createRequest(
                        organizationId,
                        tenantId,
                        "EMAIL"
                );

        when(
                firstAdapter.supports(
                        organizationId,
                        tenantId,
                        "EMAIL"
                )
        ).thenReturn(
                false
        );

        ExternalNotificationDeliveryService service =
                new ExternalNotificationDeliveryService(
                        List.of(
                                firstAdapter
                        )
                );

        assertThrows(
                IllegalStateException.class,
                () ->
                        service.deliver(
                                request
                        )
        );

        verify(
                firstAdapter,
                never()
        ).deliver(
                request
        );
    }

    @Test
    void shouldRejectWhenMultipleAdaptersMatch() {

        UUID organizationId =
                UUID.randomUUID();

        UUID tenantId =
                UUID.randomUUID();

        ExternalNotificationDeliveryRequest request =
                createRequest(
                        organizationId,
                        tenantId,
                        "EMAIL"
                );

        when(
                firstAdapter.supports(
                        organizationId,
                        tenantId,
                        "EMAIL"
                )
        ).thenReturn(
                true
        );

        when(
                secondAdapter.supports(
                        organizationId,
                        tenantId,
                        "EMAIL"
                )
        ).thenReturn(
                true
        );

        ExternalNotificationDeliveryService service =
                new ExternalNotificationDeliveryService(
                        List.of(
                                firstAdapter,
                                secondAdapter
                        )
                );

        assertThrows(
                IllegalStateException.class,
                () ->
                        service.deliver(
                                request
                        )
        );

        verify(
                firstAdapter,
                never()
        ).deliver(
                request
        );

        verify(
                secondAdapter,
                never()
        ).deliver(
                request
        );
    }

    @Test
    void shouldRequireRequest() {

        ExternalNotificationDeliveryService service =
                new ExternalNotificationDeliveryService(
                        List.of()
                );

        assertThrows(
                IllegalArgumentException.class,
                () ->
                        service.deliver(
                                null
                        )
        );
    }

    private ExternalNotificationDeliveryRequest createRequest(
            UUID organizationId,
            UUID tenantId,
            String channel) {

        ExternalNotificationDeliveryRequest request =
                new ExternalNotificationDeliveryRequest();

        request.setOrganizationId(
                organizationId
        );

        request.setTenantId(
                tenantId
        );

        request.setChannel(
                channel
        );

        return request;
    }
}