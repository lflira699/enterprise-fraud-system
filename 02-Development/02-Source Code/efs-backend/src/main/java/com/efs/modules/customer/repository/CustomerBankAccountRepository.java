package com.efs.modules.customer.repository;

import com.efs.modules.customer.entity.CustomerBankAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CustomerBankAccountRepository
        extends JpaRepository<CustomerBankAccount, UUID> {

    List<CustomerBankAccount> findByCustomerIdAndDeletedAtIsNull(
            UUID customerId
    );

    Optional<CustomerBankAccount>
    findByCustomerBankAccountIdAndDeletedAtIsNull(
            UUID customerBankAccountId
    );

    List<CustomerBankAccount>
    findByCustomerIdAndPrimaryTrueAndDeletedAtIsNull(
            UUID customerId
    );

    Optional<CustomerBankAccount>
    findByCustomerIdAndAccountNumberAndDeletedAtIsNull(
            UUID customerId,
            String accountNumber
    );
}