package com.efs.modules.customer.repository;

import com.efs.modules.customer.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CustomerRepository extends JpaRepository<Customer, UUID> {

    Optional<Customer> findByCustomerNumber(
            String customerNumber
    );

    Optional<Customer> findByCustomerIdAndDeletedAtIsNull(
            UUID customerId
    );

    Optional<Customer> findByCustomerNumberAndDeletedAtIsNull(
            String customerNumber
    );

    List<Customer> findAllByDeletedAtIsNull();

    boolean existsByCustomerNumber(
            String customerNumber
    );
}