package com.efs.modules.transaction.repository;

import com.efs.modules.transaction.entity.TransactionChannel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TransactionChannelRepository
        extends JpaRepository<TransactionChannel, UUID> {

    Optional<TransactionChannel> findByChannelTransactionId(
            UUID channelTransactionId
    );

    List<TransactionChannel> findByTransactionId(
            UUID transactionId
    );

    List<TransactionChannel> findByChannelType(
            String channelType
    );

    List<TransactionChannel> findByApplicationName(
            String applicationName
    );
}