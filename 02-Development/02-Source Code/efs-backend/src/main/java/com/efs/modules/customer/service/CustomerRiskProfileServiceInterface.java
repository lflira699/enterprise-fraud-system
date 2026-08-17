package com.efs.modules.customer.service;

import com.efs.modules.customer.dto.CustomerRiskProfileRequest;
import com.efs.modules.customer.dto.CustomerRiskProfileResponse;

import java.util.UUID;

public interface CustomerRiskProfileServiceInterface {

    CustomerRiskProfileResponse createRiskProfile(
            UUID customerId,
            CustomerRiskProfileRequest request
    );

    CustomerRiskProfileResponse getRiskProfileByCustomerId(
            UUID customerId
    );

    CustomerRiskProfileResponse updateRiskProfile(
            UUID customerId,
            CustomerRiskProfileRequest request
    );

    void deleteRiskProfile(UUID customerId);
}