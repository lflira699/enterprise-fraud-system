package com.efs.modules.event.service;

import com.efs.modules.event.dto.FraudEventReference;

import java.util.UUID;

public interface FraudEventLookupServiceInterface {

    FraudEventReference lookupCanonicalEvent(
            UUID fraudEventId,
            UUID organizationId,
            UUID tenantId);
}