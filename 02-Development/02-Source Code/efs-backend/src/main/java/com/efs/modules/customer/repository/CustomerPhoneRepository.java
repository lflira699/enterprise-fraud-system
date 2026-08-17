package com.efs.modules.customer.repository;

import com.efs.modules.customer.entity.CustomerPhone;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CustomerPhoneRepository
        extends JpaRepository<CustomerPhone, UUID> {

    List<CustomerPhone> findByCustomerIdAndDeletedAtIsNull(
            UUID customerId
    );

    Optional<CustomerPhone> findByCustomerPhoneIdAndDeletedAtIsNull(
            UUID customerPhoneId
    );

    List<CustomerPhone> findByCustomerIdAndPrimaryTrueAndDeletedAtIsNull(
            UUID customerId
    );

    Optional<CustomerPhone> findByCustomerIdAndPhoneNumberAndDeletedAtIsNull(
            UUID customerId,
            String phoneNumber
    );
}