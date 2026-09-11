package com.efs.modules.administration.dto;

import java.util.UUID;

public record UserAccountReference(
        UUID userId,
        UUID organizationId,
        UUID tenantId,
        String email) {
}