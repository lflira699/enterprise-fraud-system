package com.efs.modules.customer.service;

import com.efs.modules.customer.dto.CustomerBiometricRequest;
import com.efs.modules.customer.dto.CustomerBiometricResponse;

import java.util.List;
import java.util.UUID;

public interface CustomerBiometricServiceInterface {

    CustomerBiometricResponse createBiometric(
            UUID customerId,
            CustomerBiometricRequest request
    );

    CustomerBiometricResponse getBiometricById(
            UUID biometricId
    );

    List<CustomerBiometricResponse> getBiometricsByCustomerId(
            UUID customerId
    );

    CustomerBiometricResponse updateBiometric(
            UUID biometricId,
            CustomerBiometricRequest request
    );

    void deleteBiometric(
            UUID biometricId
    );
}