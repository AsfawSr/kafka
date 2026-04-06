package com.asfaw.kafka.outbox.service;

import com.asfaw.kafka.notification.model.NotificationEvent;
import com.asfaw.kafka.notification.service.NotificationProducer;
import com.asfaw.kafka.outbox.config.OutboxPublisherProperties;
import com.asfaw.kafka.outbox.model.OutboxEvent;
import com.asfaw.kafka.outbox.model.OutboxStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OutboxPublisherPollerTest {

    @Mock
    private OutboxService outboxService;

    @Mock
    private NotificationProducer notificationProducer;

    @Mock
    private OutboxPublisherProperties properties;

    @InjectMocks
    private OutboxPublisherPoller poller;

    @Test
    void publishDueEventsMarksPublishedAfterKafkaAck() {
        OutboxEvent outboxEvent = buildEvent("obx-1", OutboxStatus.PENDING, 0);

        when(properties.getBatchSize()).thenReturn(10);
        when(properties.getAckTimeoutMs()).thenReturn(5000L);
        when(outboxService.fetchDueEvents(10)).thenReturn(List.of(outboxEvent));
        when(outboxService.claimForPublishing("obx-1", OutboxStatus.PENDING)).thenReturn(true);
        when(notificationProducer.send(eq("obx-1"), any(NotificationEvent.class)))
                .thenReturn(CompletableFuture.completedFuture(null));

        poller.publishDueEvents();

        verify(outboxService).markPublished("obx-1");
    }

    @Test
    void publishDueEventsMarksFailedWhenKafkaSendFails() {
        OutboxEvent outboxEvent = buildEvent("obx-2", OutboxStatus.PENDING, 1);

        when(properties.getBatchSize()).thenReturn(10);
        when(properties.getAckTimeoutMs()).thenReturn(5000L);
        when(properties.getMaxRetries()).thenReturn(5);
        when(properties.getRetryBackoffMs()).thenReturn(1000L);
        when(outboxService.fetchDueEvents(10)).thenReturn(List.of(outboxEvent));
        when(outboxService.claimForPublishing("obx-2", OutboxStatus.PENDING)).thenReturn(true);
        when(notificationProducer.send(eq("obx-2"), any(NotificationEvent.class)))
                .thenReturn(CompletableFuture.failedFuture(new RuntimeException("broker down")));

        poller.publishDueEvents();

        verify(outboxService).markFailed(eq("obx-2"), eq(1), eq(5), any(), eq("java.lang.RuntimeException: broker down"));
    }

    private OutboxEvent buildEvent(String id, OutboxStatus status, int retryCount) {
        OutboxEvent event = new OutboxEvent();
        event.setId(id);
        event.setStatus(status);
        event.setRetryCount(retryCount);
        event.setAggregateId("o-1");
        event.setUserId("u-1");
        event.setMessage("hello");
        event.setNotificationType("EMAIL");
        event.setCreatedAt(Instant.now());
        event.setUpdatedAt(Instant.now());
        event.setNextAttemptAt(Instant.now());
        return event;
    }
}

