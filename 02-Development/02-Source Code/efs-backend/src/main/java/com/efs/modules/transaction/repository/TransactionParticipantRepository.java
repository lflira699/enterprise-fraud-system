package com.efs.modules.transaction.repository;

import com.efs.modules.transaction.entity.TransactionParticipant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TransactionParticipantRepository
        extends JpaRepository<TransactionParticipant, UUID> {

    Optional<TransactionParticipant> findByParticipantId(
            UUID participantId
    );

    List<TransactionParticipant> findByTransactionId(
            UUID transactionId
    );

    List<TransactionParticipant> findByCustomerId(
            UUID customerId
    );
}