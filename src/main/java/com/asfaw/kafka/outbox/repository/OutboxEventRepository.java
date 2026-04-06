package com.asfaw.kafka.outbox.repository;

import com.asfaw.kafka.outbox.model.OutboxEvent;
import com.asfaw.kafka.outbox.model.OutboxStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Collection;
import java.util.List;

public interface OutboxEventRepository extends JpaRepository<OutboxEvent, String> {

    List<OutboxEvent> findByStatusInAndNextAttemptAtLessThanEqualOrderByCreatedAtAsc(
            Collection<OutboxStatus> statuses,
            Instant nextAttemptAt,
            Pageable pageable
    );

    @Modifying
    @Query("""
            update OutboxEvent e
               set e.status = :newStatus,
                   e.updatedAt = :updatedAt
             where e.id = :id
               and e.status = :currentStatus
            """)
    int updateStatusIfCurrent(
            @Param("id") String id,
            @Param("currentStatus") OutboxStatus currentStatus,
            @Param("newStatus") OutboxStatus newStatus,
            @Param("updatedAt") Instant updatedAt
    );

    @Modifying
    @Query("""
            update OutboxEvent e
               set e.status = :status,
                   e.publishedAt = :publishedAt,
                   e.lastError = null,
                   e.updatedAt = :updatedAt
             where e.id = :id
            """)
    int markPublished(
            @Param("id") String id,
            @Param("status") OutboxStatus status,
            @Param("publishedAt") Instant publishedAt,
            @Param("updatedAt") Instant updatedAt
    );

    @Modifying
    @Query("""
            update OutboxEvent e
               set e.status = :status,
                   e.retryCount = :retryCount,
                   e.nextAttemptAt = :nextAttemptAt,
                   e.lastError = :lastError,
                   e.updatedAt = :updatedAt
             where e.id = :id
            """)
    int markFailed(
            @Param("id") String id,
            @Param("status") OutboxStatus status,
            @Param("retryCount") int retryCount,
            @Param("nextAttemptAt") Instant nextAttemptAt,
            @Param("lastError") String lastError,
            @Param("updatedAt") Instant updatedAt
    );
}

