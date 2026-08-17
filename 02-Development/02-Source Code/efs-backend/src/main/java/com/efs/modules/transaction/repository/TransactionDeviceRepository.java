package com.efs.modules.transaction.repository;

import com.efs.modules.transaction.entity.TransactionDevice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TransactionDeviceRepository
        extends JpaRepository<TransactionDevice, UUID> {

    Optional<TransactionDevice> findByDeviceTransactionId(
            UUID deviceTransactionId
    );

    List<TransactionDevice> findByTransactionId(
            UUID transactionId
    );

    List<TransactionDevice> findByDeviceFingerprint(
            String deviceFingerprint
    );
}