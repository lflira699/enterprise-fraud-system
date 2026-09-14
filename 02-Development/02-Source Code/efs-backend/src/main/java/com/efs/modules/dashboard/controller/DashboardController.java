package com.efs.modules.dashboard.controller;

import com.efs.modules.dashboard.dto.DashboardResponse;
import com.efs.modules.dashboard.service.DashboardServiceInterface;
import com.efs.shared.security.SecurityContext;
import com.efs.shared.security.SecurityContextProvider;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/dashboard")
public class DashboardController {

    private final DashboardServiceInterface
            dashboardService;

    private final SecurityContextProvider
            securityContextProvider;

    public DashboardController(
            DashboardServiceInterface dashboardService,
            SecurityContextProvider securityContextProvider) {

        this.dashboardService =
                dashboardService;

        this.securityContextProvider =
                securityContextProvider;
    }

    @GetMapping
    public ResponseEntity<DashboardResponse>
    getDashboard() {

        SecurityContext securityContext =
                securityContextProvider
                        .getCurrentContext();

        return ResponseEntity.ok(
                dashboardService.getDashboard(
                        securityContext
                )
        );
    }
}
