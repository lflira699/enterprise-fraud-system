package com.efs.modules.integration.service;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ExternalNotificationDeliveryAvailabilityService
        implements ExternalNotificationDeliveryAvailabilityServiceInterface {

    private final List<ExternalNotificationDeliveryAdapter>
            adapters;

    public ExternalNotificationDeliveryAvailabilityService(
            List<ExternalNotificationDeliveryAdapter> adapters) {

        this.adapters =
                adapters == null
                        ? List.of()
                        : List.copyOf(adapters);
    }

    @Override
    public boolean isAvailable(
            UUID organizationId,
            UUID tenantId,
            String channel) {

        requireScope(
                organizationId,
                tenantId,
                channel
        );

        long matchingAdapters =
                adapters.stream()
                        .filter(
                                adapter ->
                                        adapter.supports(
                                                organizationId,
                                                tenantId,
                                                channel
                                        )
                        )
                        .limit(2)
                        .count();

        return matchingAdapters == 1L;
    }

    private void requireScope(
            UUID organizationId,
            UUID tenantId,
            String channel) {

        if (organizationId == null) {
            throw new IllegalArgumentException(
                    "Organization id is required"
            );
        }

        if (tenantId == null) {
            throw new IllegalArgumentException(
                    "Tenant id is required"
            );
        }

        if (channel == null || channel.isBlank()) {
            throw new IllegalArgumentException(
                    "Channel is required"
            );
        }
    }
}