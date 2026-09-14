package com.efs.modules.dashboard.service;

import com.efs.modules.dashboard.dto.DashboardFilterCriteria;
import com.efs.modules.dashboard.dto.DashboardFilterOptionsResponse;
import com.efs.modules.dashboard.dto.DashboardResponse;
import com.efs.shared.security.SecurityContext;

import java.util.UUID;

public interface DashboardServiceInterface {

    DashboardResponse getDashboard(
            SecurityContext securityContext
    );

    DashboardResponse getDashboard(
            SecurityContext securityContext,
            DashboardFilterCriteria filterCriteria
    );

    DashboardFilterOptionsResponse
    getFilterOptions(
            SecurityContext securityContext,
            UUID tenantId
    );
}
