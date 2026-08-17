package com.efs.modules.customer.mapper;

import com.efs.modules.customer.dto.CustomerRiskProfileRequest;
import com.efs.modules.customer.dto.CustomerRiskProfileResponse;
import com.efs.modules.customer.entity.CustomerRiskProfile;
import org.springframework.stereotype.Component;

@Component
public class CustomerRiskProfileMapper {

    public CustomerRiskProfile toEntity(
            CustomerRiskProfileRequest request) {

        CustomerRiskProfile profile =
                new CustomerRiskProfile();

        profile.setCurrentRiskScore(request.getCurrentRiskScore());
        profile.setRiskLevel(request.getRiskLevel());
        profile.setBehaviorScore(request.getBehaviorScore());
        profile.setFraudScore(request.getFraudScore());
        profile.setAmlScore(request.getAmlScore());
        profile.setKycScore(request.getKycScore());
        profile.setDeviceScore(request.getDeviceScore());
        profile.setSanctionsScore(request.getSanctionsScore());
        profile.setPepScore(request.getPepScore());
        profile.setWatchlistScore(request.getWatchlistScore());
        profile.setCreatedBy(request.getCreatedBy());
        profile.setUpdatedBy(request.getUpdatedBy());

        return profile;
    }

    public void updateEntity(
            CustomerRiskProfileRequest request,
            CustomerRiskProfile profile) {

        profile.setCurrentRiskScore(request.getCurrentRiskScore());
        profile.setRiskLevel(request.getRiskLevel());
        profile.setBehaviorScore(request.getBehaviorScore());
        profile.setFraudScore(request.getFraudScore());
        profile.setAmlScore(request.getAmlScore());
        profile.setKycScore(request.getKycScore());
        profile.setDeviceScore(request.getDeviceScore());
        profile.setSanctionsScore(request.getSanctionsScore());
        profile.setPepScore(request.getPepScore());
        profile.setWatchlistScore(request.getWatchlistScore());
        profile.setUpdatedBy(request.getUpdatedBy());
    }

    public CustomerRiskProfileResponse toResponse(
            CustomerRiskProfile profile) {

        CustomerRiskProfileResponse response =
                new CustomerRiskProfileResponse();

        response.setProfileId(profile.getProfileId());
        response.setCustomerId(profile.getCustomerId());
        response.setCurrentRiskScore(profile.getCurrentRiskScore());
        response.setRiskLevel(profile.getRiskLevel());
        response.setLastCalculation(profile.getLastCalculation());
        response.setBehaviorScore(profile.getBehaviorScore());
        response.setFraudScore(profile.getFraudScore());
        response.setAmlScore(profile.getAmlScore());
        response.setKycScore(profile.getKycScore());
        response.setDeviceScore(profile.getDeviceScore());
        response.setSanctionsScore(profile.getSanctionsScore());
        response.setPepScore(profile.getPepScore());
        response.setWatchlistScore(profile.getWatchlistScore());
        response.setCreatedAt(profile.getCreatedAt());
        response.setCreatedBy(profile.getCreatedBy());
        response.setUpdatedAt(profile.getUpdatedAt());
        response.setUpdatedBy(profile.getUpdatedBy());
        response.setDeletedAt(profile.getDeletedAt());
        response.setRecordVersion(profile.getRecordVersion());

        return response;
    }
}