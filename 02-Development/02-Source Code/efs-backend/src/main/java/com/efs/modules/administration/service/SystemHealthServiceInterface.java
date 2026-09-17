package com.efs.modules.administration.service;

import com.efs.modules.administration.dto.SystemHealthResponse;
import com.efs.shared.security.SecurityContext;

public interface SystemHealthServiceInterface {

    SystemHealthResponse getSystemHealth(
            SecurityContext securityContext
    );
}