package com.efs.modules.integration.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExternalNotificationDeliveryAvailabilityServiceTest {

    @Mock
    private ExternalNotificationDeliveryAdapter firstAdapter;

    @Mock
    private ExternalNotificationDeliveryAdapter secondAdapter;

    @Test
    void shouldReportAvailableWhenExactlyOneAdapterSupportsScope() {

        UUID organizationId =
                UUID.randomUUID();

        UUID tenantId =
                UUID.randomUUID();

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

        ExternalNotificationDeliveryAvailabilityService service =
                new ExternalNotificationDeliveryAvailabilityService(
                        List.of(
                                firstAdapter,
                                secondAdapter
                        )
                );

        assertTrue(
                service.isAvailable(
                        organizationId,
                        tenantId,
                        "EMAIL"
                )
        );

        verify(
                firstAdapter
        ).supports(
                organizationId,
                tenantId,
                "EMAIL"
        );

        verify(
                secondAdapter
        ).supports(
                organizationId,
                tenantId,
                "EMAIL"
        );
    }

    @Test
    void shouldReportUnavailableWhenNoAdaptersExist() {

        ExternalNotificationDeliveryAvailabilityService service =
                new ExternalNotificationDeliveryAvailabilityService(
                        List.of()
                );

        assertFalse(
                service.isAvailable(
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        "EMAIL"
                )
        );
    }

    @Test
    void shouldReportUnavailableWhenNoAdapterSupportsScope() {

        UUID organizationId =
                UUID.randomUUID();

        UUID tenantId =
                UUID.randomUUID();

        when(
                firstAdapter.supports(
                        organizationId,
                        tenantId,
                        "EMAIL"
                )
        ).thenReturn(
                false
        );

        ExternalNotificationDeliveryAvailabilityService service =
                new ExternalNotificationDeliveryAvailabilityService(
                        List.of(
                                firstAdapter
                        )
                );

        assertFalse(
                service.isAvailable(
                        organizationId,
                        tenantId,
                        "EMAIL"
                )
        );
    }

    @Test
    void shouldReportUnavailableWhenMultipleAdaptersSupportScope() {

        UUID organizationId =
                UUID.randomUUID();

        UUID tenantId =
                UUID.randomUUID();

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

        ExternalNotificationDeliveryAvailabilityService service =
                new ExternalNotificationDeliveryAvailabilityService(
                        List.of(
                                firstAdapter,
                                secondAdapter
                        )
                );

        assertFalse(
                service.isAvailable(
                        organizationId,
                        tenantId,
                        "EMAIL"
                )
        );
    }

    @Test
    void shouldRequireOrganizationId() {

        ExternalNotificationDeliveryAvailabilityService service =
                new ExternalNotificationDeliveryAvailabilityService(
                        List.of()
                );

        assertThrows(
                IllegalArgumentException.class,
                () ->
                        service.isAvailable(
                                null,
                                UUID.randomUUID(),
                                "EMAIL"
                        )
        );
    }

    @Test
    void shouldRequireTenantId() {

        ExternalNotificationDeliveryAvailabilityService service =
                new ExternalNotificationDeliveryAvailabilityService(
                        List.of()
                );

        assertThrows(
                IllegalArgumentException.class,
                () ->
                        service.isAvailable(
                                UUID.randomUUID(),
                                null,
                                "EMAIL"
                        )
        );
    }

    @Test
    void shouldRequireChannel() {

        ExternalNotificationDeliveryAvailabilityService service =
                new ExternalNotificationDeliveryAvailabilityService(
                        List.of()
                );

        assertThrows(
                IllegalArgumentException.class,
                () ->
                        service.isAvailable(
                                UUID.randomUUID(),
                                UUID.randomUUID(),
                                " "
                        )
        );
    }
}