package com.efs.modules.customer.repository;

import com.efs.modules.customer.entity.CustomerHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CustomerHistoryRepository
        extends JpaRepository<CustomerHistory, UUID> {

    List<CustomerHistory> findByCustomerIdOrderByEventTimestampDesc(
            UUID customerId
    );

    Optional<CustomerHistory> findByCustomerHistoryId(
            UUID customerHistoryId
    );

    List<CustomerHistory> findByCustomerIdAndEventTypeOrderByEventTimestampDesc(
            UUID customerId,
            String eventType
    );
}