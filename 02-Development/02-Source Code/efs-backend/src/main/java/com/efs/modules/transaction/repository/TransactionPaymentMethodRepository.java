package com.efs.modules.transaction.repository;

import com.efs.modules.transaction.entity.TransactionPaymentMethod;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TransactionPaymentMethodRepository
        extends JpaRepository<TransactionPaymentMethod, UUID> {

    Optional<TransactionPaymentMethod> findByPaymentMethodId(
            UUID paymentMethodId
    );

    List<TransactionPaymentMethod> findByTransactionId(
            UUID transactionId
    );
}