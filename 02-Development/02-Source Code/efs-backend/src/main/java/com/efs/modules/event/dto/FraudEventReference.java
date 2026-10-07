package com.efs.modules.event.dto;

import java.util.UUID;

public record FraudEventReference(
        UUID fraudEventId,
        UUID organizationId,
        UUID tenantId,
        UUID correlationId) {
}