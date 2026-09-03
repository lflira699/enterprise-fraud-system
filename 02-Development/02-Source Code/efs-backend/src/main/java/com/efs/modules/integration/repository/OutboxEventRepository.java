package com.efs.modules.integration.repository;

import com.efs.modules.integration.entity.OutboxEvent;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OutboxEventRepository
        extends JpaRepository<OutboxEvent, UUID> {

    List<OutboxEvent> findByStatusOrderByOccurredAtAsc(
            String status
    );

    List<OutboxEvent> findByStatusAndNextAttemptAtLessThanEqualOrderByOccurredAtAsc(
            String status,
            LocalDateTime nextAttemptAt
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            select event
            from OutboxEvent event
            where event.id = :eventId
            """)
    Optional<OutboxEvent> findByIdForUpdate(
            @Param("eventId") UUID eventId
    );
}