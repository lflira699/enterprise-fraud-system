package com.efs.modules.customer.repository;

import com.efs.modules.customer.entity.CustomerEmail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CustomerEmailRepository
        extends JpaRepository<CustomerEmail, UUID> {

    List<CustomerEmail> findByCustomerIdAndDeletedAtIsNull(
            UUID customerId
    );

    Optional<CustomerEmail> findByCustomerEmailIdAndDeletedAtIsNull(
            UUID customerEmailId
    );

    List<CustomerEmail> findByCustomerIdAndPrimaryTrueAndDeletedAtIsNull(
            UUID customerId
    );

    Optional<CustomerEmail> findByCustomerIdAndEmailAddressAndDeletedAtIsNull(
            UUID customerId,
            String emailAddress
    );
}