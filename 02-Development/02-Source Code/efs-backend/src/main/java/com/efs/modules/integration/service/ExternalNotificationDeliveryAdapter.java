package com.efs.modules.integration.service;

import com.efs.modules.integration.dto.ExternalNotificationDeliveryRequest;
import com.efs.modules.integration.dto.ExternalNotificationDeliveryResult;

public interface ExternalNotificationDeliveryAdapter {

    boolean supports(
            String channel
    );

    ExternalNotificationDeliveryResult deliver(
            ExternalNotificationDeliveryRequest request
    );
}