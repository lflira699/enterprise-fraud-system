package com.efs.modules.dashboard.dto;

import java.util.List;
import java.util.UUID;

public class DashboardEffectiveFilters {

    private final UUID tenantId;

    private final List<DashboardComponent>
            components;

    private final DashboardFilterSource
            filterSource;

    public DashboardEffectiveFilters(
            UUID tenantId,
            List<DashboardComponent> components,
            DashboardFilterSource filterSource) {

        this.tenantId =
                tenantId;

        this.components =
                List.copyOf(
                        components
                );

        this.filterSource =
                filterSource;
    }

    public UUID getTenantId() {
        return tenantId;
    }

    public List<DashboardComponent>
    getComponents() {

        return components;
    }

    public DashboardFilterSource
    getFilterSource() {

        return filterSource;
    }
}
