package com.efs.modules.customer.repository;

import com.efs.modules.customer.entity.CustomerAddress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CustomerAddressRepository
        extends JpaRepository<CustomerAddress, UUID> {

    List<CustomerAddress> findByCustomerIdAndDeletedAtIsNull(
            UUID customerId
    );

    Optional<CustomerAddress> findByCustomerAddressIdAndDeletedAtIsNull(
            UUID customerAddressId
    );

    List<CustomerAddress> findByCustomerIdAndPrimaryTrueAndDeletedAtIsNull(
            UUID customerId
    );
}