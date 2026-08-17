package com.efs.modules.customer.mapper;

import com.efs.modules.customer.dto.CustomerBiometricRequest;
import com.efs.modules.customer.dto.CustomerBiometricResponse;
import com.efs.modules.customer.entity.CustomerBiometric;
import org.springframework.stereotype.Component;

@Component
public class CustomerBiometricMapper {

    public CustomerBiometric toEntity(
            CustomerBiometricRequest request) {

        CustomerBiometric biometric =
                new CustomerBiometric();

        biometric.setBiometricType(request.getBiometricType());
        biometric.setVerificationStatus(request.getVerificationStatus());
        biometric.setVerificationScore(request.getVerificationScore());
        biometric.setProviderReference(request.getProviderReference());
        biometric.setEnrolledAt(request.getEnrolledAt());
        biometric.setLastVerifiedAt(request.getLastVerifiedAt());
        biometric.setActive(request.getActive());
        biometric.setCreatedBy(request.getCreatedBy());
        biometric.setUpdatedBy(request.getUpdatedBy());

        return biometric;
    }

    public void updateEntity(
            CustomerBiometricRequest request,
            CustomerBiometric biometric) {

        biometric.setBiometricType(request.getBiometricType());
        biometric.setVerificationStatus(request.getVerificationStatus());
        biometric.setVerificationScore(request.getVerificationScore());
        biometric.setProviderReference(request.getProviderReference());
        biometric.setEnrolledAt(request.getEnrolledAt());
        biometric.setLastVerifiedAt(request.getLastVerifiedAt());
        biometric.setActive(request.getActive());
        biometric.setUpdatedBy(request.getUpdatedBy());
    }

    public CustomerBiometricResponse toResponse(
            CustomerBiometric biometric) {

        CustomerBiometricResponse response =
                new CustomerBiometricResponse();

        response.setBiometricId(biometric.getBiometricId());
        response.setCustomerId(biometric.getCustomerId());
        response.setBiometricType(biometric.getBiometricType());
        response.setVerificationStatus(
                biometric.getVerificationStatus()
        );
        response.setVerificationScore(
                biometric.getVerificationScore()
        );
        response.setProviderReference(
                biometric.getProviderReference()
        );
        response.setEnrolledAt(biometric.getEnrolledAt());
        response.setLastVerifiedAt(
                biometric.getLastVerifiedAt()
        );
        response.setActive(biometric.getActive());
        response.setCreatedAt(biometric.getCreatedAt());
        response.setCreatedBy(biometric.getCreatedBy());
        response.setUpdatedAt(biometric.getUpdatedAt());
        response.setUpdatedBy(biometric.getUpdatedBy());
        response.setDeletedAt(biometric.getDeletedAt());
        response.setRecordVersion(
                biometric.getRecordVersion()
        );

        return response;
    }
}