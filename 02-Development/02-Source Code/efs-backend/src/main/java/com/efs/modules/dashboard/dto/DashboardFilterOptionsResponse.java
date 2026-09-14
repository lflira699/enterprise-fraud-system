package com.efs.modules.dashboard.dto;

import java.util.List;
import java.util.UUID;

public class DashboardFilterOptionsResponse {

    private final boolean tenantSelectionAllowed;

    private final UUID effectiveTenantId;

    private final List<DashboardComponent>
            availableComponents;

    private final List<DashboardComponent>
            defaultComponents;

    public DashboardFilterOptionsResponse(
            boolean tenantSelectionAllowed,
            UUID effectiveTenantId,
            List<DashboardComponent> availableComponents,
            List<DashboardComponent> defaultComponents) {

        this.tenantSelectionAllowed =
                tenantSelectionAllowed;

        this.effectiveTenantId =
                effectiveTenantId;

        this.availableComponents =
                List.copyOf(
                        availableComponents
                );

        this.defaultComponents =
                List.copyOf(
                        defaultComponents
                );
    }

    public boolean isTenantSelectionAllowed() {
        return tenantSelectionAllowed;
    }

    public UUID getEffectiveTenantId() {
        return effectiveTenantId;
    }

    public List<DashboardComponent>
    getAvailableComponents() {

        return availableComponents;
    }

    public List<DashboardComponent>
    getDefaultComponents() {

        return defaultComponents;
    }
}
