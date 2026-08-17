package com.efs.modules.customer.repository;

import com.efs.modules.customer.entity.CustomerRiskProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CustomerRiskProfileRepository
        extends JpaRepository<CustomerRiskProfile, UUID> {

    Optional<CustomerRiskProfile>
    findByCustomerIdAndDeletedAtIsNull(UUID customerId);

    Optional<CustomerRiskProfile>
    findByProfileIdAndDeletedAtIsNull(UUID profileId);

    boolean existsByCustomerIdAndDeletedAtIsNull(UUID customerId);
}