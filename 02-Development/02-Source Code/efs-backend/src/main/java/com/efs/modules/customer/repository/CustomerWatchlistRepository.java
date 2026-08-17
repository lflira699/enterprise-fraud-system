package com.efs.modules.customer.repository;

import com.efs.modules.customer.entity.CustomerWatchlist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CustomerWatchlistRepository
        extends JpaRepository<CustomerWatchlist, UUID> {

    List<CustomerWatchlist> findByCustomerIdAndDeletedAtIsNull(
            UUID customerId
    );

    Optional<CustomerWatchlist> findByWatchlistIdAndDeletedAtIsNull(
            UUID watchlistId
    );

    List<CustomerWatchlist> findByCustomerIdAndActiveTrueAndDeletedAtIsNull(
            UUID customerId
    );
}