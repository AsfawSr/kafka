package com.asfaw.kafka.outbox.service;

import com.asfaw.kafka.order.model.OrderEvent;
import com.asfaw.kafka.outbox.model.OutboxEvent;
import com.asfaw.kafka.outbox.model.OutboxStatus;
import com.asfaw.kafka.outbox.repository.OutboxEventRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class OutboxServiceTest {

    @Mock
    private OutboxEventRepository outboxEventRepository;

    @InjectMocks
    private OutboxService outboxService;

    @Test
    void enqueueNotificationFromOrderSavesPendingOutboxRecord() {
        OrderEvent orderEvent = new OrderEvent("o-100", "u-1", "hello", "EMAIL");

        outboxService.enqueueNotificationFromOrder(orderEvent);

        ArgumentCaptor<OutboxEvent> captor = ArgumentCaptor.forClass(OutboxEvent.class);
        verify(outboxEventRepository).save(captor.capture());

        OutboxEvent saved = captor.getValue();
        assertEquals("o-100", saved.getAggregateId());
        assertEquals("u-1", saved.getUserId());
        assertEquals("hello", saved.getMessage());
        assertEquals("EMAIL", saved.getNotificationType());
        assertEquals(OutboxStatus.PENDING, saved.getStatus());
        assertEquals(0, saved.getRetryCount());
        assertNotNull(saved.getNextAttemptAt());
    }

    @Test
    void enqueueNotificationFromOrderSkipsWhenTypeMissing() {
        outboxService.enqueueNotificationFromOrder(new OrderEvent("o-100", "u-1", "hello", "  "));

        verifyNoInteractions(outboxEventRepository);
    }

    @Test
    void markFailedMovesToPermanentWhenRetriesExhausted() {
        outboxService.markFailed("outbox-1", 4, 5, Duration.ofSeconds(10), "boom");

        verify(outboxEventRepository).markFailed(
                org.mockito.ArgumentMatchers.eq("outbox-1"),
                org.mockito.ArgumentMatchers.eq(OutboxStatus.FAILED_PERMANENT),
                org.mockito.ArgumentMatchers.eq(5),
                any(),
                org.mockito.ArgumentMatchers.eq("boom"),
                any()
        );
    }
}

