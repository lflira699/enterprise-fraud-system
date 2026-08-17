package com.efs.modules.customer.repository;

import com.efs.modules.customer.entity.CustomerDocument;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CustomerDocumentRepository
        extends JpaRepository<CustomerDocument, UUID> {

    List<CustomerDocument> findByCustomerId(UUID customerId);

    Optional<CustomerDocument> findByCustomerIdAndDocumentTypeAndDocumentNumber(
            UUID customerId,
            String documentType,
            String documentNumber
    );

    boolean existsByCustomerIdAndDocumentTypeAndDocumentNumber(
            UUID customerId,
            String documentType,
            String documentNumber
    );
}