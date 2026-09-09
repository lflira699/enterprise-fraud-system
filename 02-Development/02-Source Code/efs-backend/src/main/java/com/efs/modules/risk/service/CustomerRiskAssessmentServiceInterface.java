package com.efs.modules.risk.service;

import com.efs.modules.customer.dto.CustomerRiskProfileRequest;
import com.efs.modules.customer.dto.CustomerRiskProfileResponse;

import java.util.UUID;

public interface CustomerRiskAssessmentServiceInterface {

    CustomerRiskProfileResponse createRiskAssessment(
            UUID customerId,
            CustomerRiskProfileRequest request
    );

    CustomerRiskProfileResponse updateRiskAssessment(
            UUID customerId,
            CustomerRiskProfileRequest request
    );
}