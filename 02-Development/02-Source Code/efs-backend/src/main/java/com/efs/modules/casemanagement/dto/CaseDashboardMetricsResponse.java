package com.efs.modules.casemanagement.dto;

public class CaseDashboardMetricsResponse {

    private final long openCases;
    private final long closedCases;

    public CaseDashboardMetricsResponse(
            long openCases,
            long closedCases) {

        this.openCases =
                openCases;

        this.closedCases =
                closedCases;
    }

    public long getOpenCases() {
        return openCases;
    }

    public long getClosedCases() {
        return closedCases;
    }
}
