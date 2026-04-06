package com.asfaw.kafka.service;

import com.asfaw.kafka.order.model.OrderEvent;
import com.asfaw.kafka.order.model.OrderEventEnvelope;
import com.asfaw.kafka.outbox.service.OutboxService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class OrderConsumerTest {

    @Mock
    private OutboxService outboxService;

    @InjectMocks
    private OrderConsumer orderConsumer;

    @Test
    void consumeStoresOrderInOutbox() {
        OrderEvent orderEvent = new OrderEvent("o-100", "u-1", "Your order is confirmed", "EMAIL");
        OrderEventEnvelope envelope = new OrderEventEnvelope();
        envelope.setEventId("evt-1");
        envelope.setPayload(orderEvent);

        orderConsumer.consume(envelope);

        verify(outboxService).enqueueNotificationFromOrder(eq(orderEvent));
    }

    @Test
    void consumeStillDelegatesValidationToOutboxService() {
        OrderEvent orderEvent = new OrderEvent("o-101", "u-1", "hello", " ");
        OrderEventEnvelope envelope = new OrderEventEnvelope();
        envelope.setEventId("evt-2");
        envelope.setPayload(orderEvent);

        orderConsumer.consume(envelope);
        verify(outboxService).enqueueNotificationFromOrder(eq(orderEvent));
    }
}

