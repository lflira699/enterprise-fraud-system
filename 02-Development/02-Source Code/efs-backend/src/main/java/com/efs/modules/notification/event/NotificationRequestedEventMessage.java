package com.efs.modules.notification.event;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public record NotificationRequestedEventMessage(
        UUID messageId,
        UUID correlationId,
        String notificationType,
        String templateCode,
        UUID organizationId,
        UUID tenantId,
        String sourceComponent,
        String sourceEntityType,
        UUID sourceEntityId,
        List<UUID> recipientUserIds,
        Map<String, Object> templateParameters) {
}