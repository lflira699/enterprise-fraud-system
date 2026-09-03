package com.efs.modules.alert.event;

import java.util.UUID;

public record DecisionGeneratedEventMessage(
        UUID messageId,
        UUID correlationId,
        UUID decisionId) {
}