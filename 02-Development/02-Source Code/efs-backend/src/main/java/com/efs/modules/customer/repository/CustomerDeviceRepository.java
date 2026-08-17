package com.efs.modules.customer.repository;

import com.efs.modules.customer.entity.CustomerDevice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CustomerDeviceRepository
        extends JpaRepository<CustomerDevice, UUID> {

    List<CustomerDevice> findByCustomerId(UUID customerId);

    Optional<CustomerDevice> findByCustomerIdAndDeviceFingerprint(
            UUID customerId,
            String deviceFingerprint
    );

    boolean existsByCustomerIdAndDeviceFingerprint(
            UUID customerId,
            String deviceFingerprint
    );
}