package com.efs.modules.dashboard.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class DashboardResponse {

    private final LocalDateTime generatedAt;
    private final DashboardDataStatus dataStatus;
    private final Long criticalAlerts;
    private final Long openAlerts;
    private final Long openCases;
    private final Long closedCases;
    private final BigDecimal averageRiskScore;
    private final Long activatedDetectionScenarios;

    private final List<DashboardComponent>
            unavailableComponents;

    private final DashboardEffectiveFilters
            effectiveFilters;

    public DashboardResponse(
            LocalDateTime generatedAt,
            DashboardDataStatus dataStatus,
            Long criticalAlerts,
            Long openAlerts,
            Long openCases,
            Long closedCases,
            BigDecimal averageRiskScore,
            Long activatedDetectionScenarios,
            List<DashboardComponent> unavailableComponents) {

        this(
                generatedAt,
                dataStatus,
                criticalAlerts,
                openAlerts,
                openCases,
                closedCases,
                averageRiskScore,
                activatedDetectionScenarios,
                unavailableComponents,
                null
        );
    }

    public DashboardResponse(
            LocalDateTime generatedAt,
            DashboardDataStatus dataStatus,
            Long criticalAlerts,
            Long openAlerts,
            Long openCases,
            Long closedCases,
            BigDecimal averageRiskScore,
            Long activatedDetectionScenarios,
            List<DashboardComponent> unavailableComponents,
            DashboardEffectiveFilters effectiveFilters) {

        this.generatedAt =
                generatedAt;

        this.dataStatus =
                dataStatus;

        this.criticalAlerts =
                criticalAlerts;

        this.openAlerts =
                openAlerts;

        this.openCases =
                openCases;

        this.closedCases =
                closedCases;

        this.averageRiskScore =
                averageRiskScore;

        this.activatedDetectionScenarios =
                activatedDetectionScenarios;

        this.unavailableComponents =
                List.copyOf(
                        unavailableComponents
                );

        this.effectiveFilters =
                effectiveFilters;
    }

    public LocalDateTime getGeneratedAt() {
        return generatedAt;
    }

    public DashboardDataStatus getDataStatus() {
        return dataStatus;
    }

    public Long getCriticalAlerts() {
        return criticalAlerts;
    }

    public Long getOpenAlerts() {
        return openAlerts;
    }

    public Long getOpenCases() {
        return openCases;
    }

    public Long getClosedCases() {
        return closedCases;
    }

    public BigDecimal getAverageRiskScore() {
        return averageRiskScore;
    }

    public Long getActivatedDetectionScenarios() {
        return activatedDetectionScenarios;
    }

    public List<DashboardComponent>
    getUnavailableComponents() {

        return unavailableComponents;
    }

    public DashboardEffectiveFilters
    getEffectiveFilters() {

        return effectiveFilters;
    }
}
