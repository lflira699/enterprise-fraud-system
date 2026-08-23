package com.efs.modules.casemanagement.mapper;

import com.efs.modules.casemanagement.dto.CaseResolutionRequest;
import com.efs.modules.casemanagement.dto.CaseResolutionResponse;
import com.efs.modules.casemanagement.entity.CaseResolution;
import org.springframework.stereotype.Component;

@Component
public class CaseResolutionMapper {

    public CaseResolution toEntity(
            CaseResolutionRequest request) {

        CaseResolution resolution =
                new CaseResolution();

        resolution.setResolutionType(
                request.getResolutionType()
        );

        resolution.setResolutionSummary(
                request.getResolutionSummary()
        );

        resolution.setEconomicImpact(
                request.getEconomicImpact()
        );

        resolution.setCurrencyCode(
                request.getCurrencyCode()
        );

        resolution.setResolvedBy(
                request.getResolvedBy()
        );

        resolution.setApprovedBy(
                request.getApprovedBy()
        );

        return resolution;
    }

    public CaseResolutionResponse toResponse(
            CaseResolution resolution) {

        CaseResolutionResponse response =
                new CaseResolutionResponse();

        response.setResolutionId(
                resolution.getResolutionId()
        );

        response.setCaseId(
                resolution.getCaseId()
        );

        response.setResolutionType(
                resolution.getResolutionType()
        );

        response.setResolutionSummary(
                resolution.getResolutionSummary()
        );

        response.setEconomicImpact(
                resolution.getEconomicImpact()
        );

        response.setCurrencyCode(
                resolution.getCurrencyCode()
        );

        response.setResolvedBy(
                resolution.getResolvedBy()
        );

        response.setResolvedAt(
                resolution.getResolvedAt()
        );

        response.setApprovedBy(
                resolution.getApprovedBy()
        );

        return response;
    }
}