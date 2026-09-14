package com.efs.modules.dashboard.service;

import com.efs.modules.dashboard.dto.DashboardResponse;
import com.efs.shared.security.SecurityContext;

public interface DashboardServiceInterface {

    DashboardResponse getDashboard(
            SecurityContext securityContext
    );
}
