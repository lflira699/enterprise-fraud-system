package com.efs.modules.customer.repository;

import com.efs.modules.customer.entity.CustomerBiometric;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CustomerBiometricRepository
        extends JpaRepository<CustomerBiometric, UUID> {

    List<CustomerBiometric> findByCustomerIdAndDeletedAtIsNull(
            UUID customerId
    );

    Optional<CustomerBiometric> findByBiometricIdAndDeletedAtIsNull(
            UUID biometricId
    );

    List<CustomerBiometric> findByCustomerIdAndBiometricTypeAndDeletedAtIsNull(
            UUID customerId,
            String biometricType
    );
}