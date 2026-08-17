package com.efs.modules.customer.repository;

import com.efs.modules.customer.entity.CustomerRelationship;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CustomerRelationshipRepository
        extends JpaRepository<CustomerRelationship, UUID> {

    List<CustomerRelationship> findByCustomerIdAndDeletedAtIsNull(
            UUID customerId
    );

    Optional<CustomerRelationship>
    findByCustomerRelationshipIdAndDeletedAtIsNull(
            UUID customerRelationshipId
    );

    List<CustomerRelationship>
    findByRelatedCustomerIdAndDeletedAtIsNull(
            UUID relatedCustomerId
    );
}