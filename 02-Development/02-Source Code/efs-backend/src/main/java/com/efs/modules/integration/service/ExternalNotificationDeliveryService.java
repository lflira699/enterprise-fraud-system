package com.efs.modules.integration.service;

import com.efs.modules.integration.dto.ExternalNotificationDeliveryRequest;
import com.efs.modules.integration.dto.ExternalNotificationDeliveryResult;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExternalNotificationDeliveryService
        implements ExternalNotificationDeliveryServiceInterface {

    private final List<ExternalNotificationDeliveryAdapter>
            adapters;

    public ExternalNotificationDeliveryService(
            List<ExternalNotificationDeliveryAdapter> adapters) {

        this.adapters =
                adapters == null
                        ? List.of()
                        : List.copyOf(adapters);
    }

    @Override
    public ExternalNotificationDeliveryResult deliver(
            ExternalNotificationDeliveryRequest request) {

        requireRequestContext(
                request
        );

        List<ExternalNotificationDeliveryAdapter>
                matchingAdapters =
                adapters.stream()
                        .filter(
                                adapter ->
                                        adapter.supports(
                                                request.getOrganizationId(),
                                                request.getTenantId(),
                                                request.getChannel()
                                        )
                        )
                        .limit(
                                2
                        )
                        .toList();

        if (matchingAdapters.size() != 1) {
            throw new IllegalStateException(
                    "External notification delivery configuration is unavailable or ambiguous"
            );
        }

        return matchingAdapters
                .get(
                        0
                )
                .deliver(
                        request
                );
    }

    private void requireRequestContext(
            ExternalNotificationDeliveryRequest request) {

        if (request == null) {
            throw new IllegalArgumentException(
                    "External notification delivery request is required"
            );
        }

        if (request.getOrganizationId() == null) {
            throw new IllegalArgumentException(
                    "Organization id is required"
            );
        }

        if (request.getTenantId() == null) {
            throw new IllegalArgumentException(
                    "Tenant id is required"
            );
        }

        if (request.getChannel() == null
                || request.getChannel().isBlank()) {

            throw new IllegalArgumentException(
                    "Channel is required"
            );
        }
    }
}