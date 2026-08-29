package com.efs.modules.catalog.mapper;

import com.efs.modules.catalog.dto.RiskLevelRequest;
import com.efs.modules.catalog.dto.RiskLevelResponse;
import com.efs.modules.catalog.entity.RiskLevel;
import org.springframework.stereotype.Component;

@Component
public class RiskLevelMapper {

    public RiskLevel toEntity(
            RiskLevelRequest request) {

        RiskLevel riskLevel =
                new RiskLevel();

        riskLevel.setRiskCode(
                request.getRiskCode()
        );

        riskLevel.setRiskName(
                request.getRiskName()
        );

        riskLevel.setDescription(
                request.getDescription()
        );

        riskLevel.setDisplayOrder(
                request.getDisplayOrder()
        );

        riskLevel.setStatus(
                request.getStatus()
        );

        return riskLevel;
    }

    public RiskLevelResponse toResponse(
            RiskLevel riskLevel) {

        RiskLevelResponse response =
                new RiskLevelResponse();

        response.setRiskLevelId(
                riskLevel.getRiskLevelId()
        );

        response.setRiskCode(
                riskLevel.getRiskCode()
        );

        response.setRiskName(
                riskLevel.getRiskName()
        );

        response.setDescription(
                riskLevel.getDescription()
        );

        response.setDisplayOrder(
                riskLevel.getDisplayOrder()
        );

        response.setStatus(
                riskLevel.getStatus()
        );

        response.setCreatedAt(
                riskLevel.getCreatedAt()
        );

        return response;
    }
}