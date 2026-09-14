package com.efs.modules.alert.dto;

public class AlertDashboardMetricsResponse {

    private final long criticalAlerts;
    private final long openAlerts;

    public AlertDashboardMetricsResponse(
            long criticalAlerts,
            long openAlerts) {

        this.criticalAlerts =
                criticalAlerts;

        this.openAlerts =
                openAlerts;
    }

    public long getCriticalAlerts() {
        return criticalAlerts;
    }

    public long getOpenAlerts() {
        return openAlerts;
    }
}
