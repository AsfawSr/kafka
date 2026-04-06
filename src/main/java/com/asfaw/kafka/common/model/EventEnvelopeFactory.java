package com.asfaw.kafka.common.model;

import com.asfaw.kafka.notification.model.NotificationEvent;
import com.asfaw.kafka.notification.model.NotificationEventEnvelope;
import com.asfaw.kafka.order.model.OrderEvent;
import com.asfaw.kafka.order.model.OrderEventEnvelope;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

@Component
public class EventEnvelopeFactory {

    public static final int CURRENT_VERSION = 1;
    public static final String ORDER_CREATED_EVENT_TYPE = "ORDER_CREATED";
    public static final String NOTIFICATION_REQUESTED_EVENT_TYPE = "NOTIFICATION_REQUESTED";

    public OrderEventEnvelope orderEnvelope(OrderEvent payload) {
        OrderEventEnvelope envelope = new OrderEventEnvelope();
        envelope.setEventId(UUID.randomUUID().toString());
        envelope.setEventType(ORDER_CREATED_EVENT_TYPE);
        envelope.setVersion(CURRENT_VERSION);
        envelope.setTimestamp(Instant.now());
        envelope.setPayload(payload);
        return envelope;
    }

    public NotificationEventEnvelope notificationEnvelope(NotificationEvent payload) {
        NotificationEventEnvelope envelope = new NotificationEventEnvelope();
        envelope.setEventId(UUID.randomUUID().toString());
        envelope.setEventType(NOTIFICATION_REQUESTED_EVENT_TYPE);
        envelope.setVersion(CURRENT_VERSION);
        envelope.setTimestamp(Instant.now());
        envelope.setPayload(payload);
        return envelope;
    }
}

