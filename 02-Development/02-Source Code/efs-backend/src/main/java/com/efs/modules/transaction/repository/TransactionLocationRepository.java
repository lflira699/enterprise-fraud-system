package com.efs.modules.transaction.repository;

import com.efs.modules.transaction.entity.TransactionLocation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.net.InetAddress;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TransactionLocationRepository
        extends JpaRepository<TransactionLocation, UUID> {

    Optional<TransactionLocation> findByLocationId(
            UUID locationId
    );

    List<TransactionLocation> findByTransactionId(
            UUID transactionId
    );

    List<TransactionLocation> findByIpAddress(
            InetAddress ipAddress
    );

    List<TransactionLocation> findByCountryCode(
            String countryCode
    );

    List<TransactionLocation> findByAsn(
            Long asn
    );
}