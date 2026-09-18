package com.efs.modules.risk.port.out;

import com.efs.modules.customer.dto.CustomerRiskProfileRequest;
import com.efs.modules.customer.dto.CustomerRiskProfileResponse;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface CustomerRiskPersistencePort {

    boolean activeCustomerExists(
            UUID customerId
    );

    boolean activeRiskProfileExists(
            UUID customerId
    );

    Optional<CustomerRiskProfileResponse>
    findActiveRiskProfile(
            UUID customerId
    );

    CustomerRiskProfileResponse createRiskProfile(
            UUID customerId,
            CustomerRiskProfileRequest request,
            BigDecimal calculatedRiskScore,
            String calculatedRiskLevel,
            LocalDateTime timestamp
    );

    CustomerRiskProfileResponse updateRiskProfile(
            UUID customerId,
            CustomerRiskProfileRequest request,
            BigDecimal calculatedRiskScore,
            String calculatedRiskLevel,
            LocalDateTime timestamp
    );

    Optional<String>
    findLatestRiskAssessmentSourceReference(
            UUID customerId,
            String eventType
    );

    void createRiskAssessmentHistory(
            UUID customerId,
            BigDecimal previousRiskScore,
            String previousRiskLevel,
            BigDecimal newRiskScore,
            String newRiskLevel,
            String sourceReference,
            LocalDateTime timestamp
    );
}