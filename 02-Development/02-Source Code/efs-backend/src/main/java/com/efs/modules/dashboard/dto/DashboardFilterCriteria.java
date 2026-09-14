package com.efs.modules.dashboard.dto;

import java.util.UUID;

public record DashboardFilterCriteria(
        UUID tenantId,
        String components) {

    public static DashboardFilterCriteria defaults() {

        return new DashboardFilterCriteria(
                null,
                null
        );
    }

    public boolean hasExplicitFilters() {

        return tenantId != null
                || components != null;
    }
}
