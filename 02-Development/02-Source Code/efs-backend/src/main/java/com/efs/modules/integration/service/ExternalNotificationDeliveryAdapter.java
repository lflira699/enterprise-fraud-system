package com.efs.modules.integration.service;

import com.efs.modules.integration.dto.ExternalNotificationDeliveryRequest;
import com.efs.modules.integration.dto.ExternalNotificationDeliveryResult;

import java.util.UUID;

public interface ExternalNotificationDeliveryAdapter {

    boolean supports(
            UUID organizationId,
            UUID tenantId,
            String channel
    );

    ExternalNotificationDeliveryResult deliver(
            ExternalNotificationDeliveryRequest request
    );
}