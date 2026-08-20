package com.efs.modules.integration.repository;

import com.efs.modules.integration.entity.OutboxEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface OutboxEventRepository
        extends JpaRepository<OutboxEvent, UUID> {

    List<OutboxEvent> findByStatusOrderByOccurredAtAsc(
            String status
    );

    List<OutboxEvent> findByStatusAndNextAttemptAtLessThanEqualOrderByOccurredAtAsc(
            String status,
            java.time.LocalDateTime nextAttemptAt
    );
}