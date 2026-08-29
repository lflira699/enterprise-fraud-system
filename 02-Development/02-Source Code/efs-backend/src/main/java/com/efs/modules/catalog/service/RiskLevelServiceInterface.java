package com.efs.modules.catalog.service;

import com.efs.modules.catalog.dto.RiskLevelRequest;
import com.efs.modules.catalog.dto.RiskLevelResponse;

import java.util.List;
import java.util.UUID;

public interface RiskLevelServiceInterface {

    RiskLevelResponse createRiskLevel(
            RiskLevelRequest request
    );

    RiskLevelResponse getRiskLevelById(
            UUID riskLevelId
    );

    RiskLevelResponse getRiskLevelByRiskCode(
            String riskCode
    );

    List<RiskLevelResponse> getRiskLevelsByStatus(
            String status
    );

    List<RiskLevelResponse> getAllRiskLevels();
}