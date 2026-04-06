package com.asfaw.kafka.outbox.service;

import com.asfaw.kafka.order.model.OrderEvent;
import com.asfaw.kafka.outbox.model.OutboxEvent;
import com.asfaw.kafka.outbox.model.OutboxStatus;
import com.asfaw.kafka.outbox.repository.OutboxEventRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Set;

@Service
public class OutboxService {

    private static final int ERROR_MAX_LENGTH = 1000;

    private final OutboxEventRepository outboxEventRepository;

    public OutboxService(OutboxEventRepository outboxEventRepository) {
        this.outboxEventRepository = outboxEventRepository;
    }

    @Transactional
    public void enqueueNotificationFromOrder(OrderEvent orderEvent) {
        if (orderEvent == null || !StringUtils.hasText(orderEvent.getNotificationType())) {
            System.out.println("Skipping outbox write because notificationType is missing: " + orderEvent);
            return;
        }

        OutboxEvent outboxEvent = new OutboxEvent();
        outboxEvent.setAggregateId(orderEvent.getOrderId());
        outboxEvent.setUserId(orderEvent.getUserId());
        outboxEvent.setMessage(orderEvent.getMessage());
        outboxEvent.setNotificationType(orderEvent.getNotificationType());
        outboxEvent.setStatus(OutboxStatus.PENDING);
        outboxEvent.setRetryCount(0);
        outboxEvent.setNextAttemptAt(Instant.now());

        outboxEventRepository.save(outboxEvent);
    }

    @Transactional(readOnly = true)
    public List<OutboxEvent> fetchDueEvents(int batchSize) {
        return outboxEventRepository.findByStatusInAndNextAttemptAtLessThanEqualOrderByCreatedAtAsc(
                Set.of(OutboxStatus.PENDING, OutboxStatus.FAILED_RETRYABLE),
                Instant.now(),
                PageRequest.of(0, batchSize)
        );
    }

    @Transactional
    public boolean claimForPublishing(String outboxId, OutboxStatus currentStatus) {
        int updated = outboxEventRepository.updateStatusIfCurrent(
                outboxId,
                currentStatus,
                OutboxStatus.PUBLISHING,
                Instant.now()
        );
        return updated == 1;
    }

    @Transactional
    public void markPublished(String outboxId) {
        outboxEventRepository.markPublished(
                outboxId,
                OutboxStatus.PUBLISHED,
                Instant.now(),
                Instant.now()
        );
    }

    @Transactional
    public void markFailed(String outboxId, int previousRetryCount, int maxRetries, Duration retryBackoff, String error) {
        int nextRetryCount = previousRetryCount + 1;
        boolean exhausted = nextRetryCount >= maxRetries;
        OutboxStatus nextStatus = exhausted ? OutboxStatus.FAILED_PERMANENT : OutboxStatus.FAILED_RETRYABLE;
        Instant nextAttemptAt = exhausted ? Instant.now().plus(Duration.ofDays(36500)) : Instant.now().plus(retryBackoff);

        outboxEventRepository.markFailed(
                outboxId,
                nextStatus,
                nextRetryCount,
                nextAttemptAt,
                abbreviateError(error),
                Instant.now()
        );
    }

    private String abbreviateError(String error) {
        if (error == null) {
            return null;
        }
        if (error.length() <= ERROR_MAX_LENGTH) {
            return error;
        }
        return error.substring(0, ERROR_MAX_LENGTH);
    }
}

