package com.efs.modules.integration.service;

import java.util.UUID;

public interface ExternalNotificationDeliveryAvailabilityServiceInterface {

    boolean isAvailable(
            UUID organizationId,
            UUID tenantId,
            String channel
    );
}