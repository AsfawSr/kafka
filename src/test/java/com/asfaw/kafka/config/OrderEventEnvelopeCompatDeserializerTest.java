package com.asfaw.kafka.config;

import com.asfaw.kafka.order.model.OrderEventEnvelope;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class OrderEventEnvelopeCompatDeserializerTest {

    private final OrderEventEnvelopeCompatDeserializer deserializer = new OrderEventEnvelopeCompatDeserializer();

    @Test
    void deserializeSupportsEnvelopeMessage() {
        String json = """
                {
                  "eventId": "evt-1",
                  "eventType": "ORDER_CREATED",
                  "version": 1,
                  "timestamp": "2026-04-06T09:00:00Z",
                  "payload": {
                    "orderId": "o-100",
                    "userId": "u-1",
                    "message": "hello",
                    "notificationType": "EMAIL"
                  }
                }
                """;

        OrderEventEnvelope result = deserializer.deserialize("order", json.getBytes(StandardCharsets.UTF_8));

        assertEquals("evt-1", result.getEventId());
        assertEquals("ORDER_CREATED", result.getEventType());
        assertEquals(1, result.getVersion());
        assertNotNull(result.getPayload());
        assertEquals("o-100", result.getPayload().getOrderId());
    }

    @Test
    void deserializeSupportsLegacyFlatOrderEvent() {
        String json = """
                {
                  "orderId": "o-101",
                  "userId": "u-2",
                  "message": "legacy",
                  "notificationType": "SMS"
                }
                """;

        OrderEventEnvelope result = deserializer.deserialize("order", json.getBytes(StandardCharsets.UTF_8));

        assertNotNull(result.getEventId());
        assertEquals("ORDER_CREATED", result.getEventType());
        assertEquals(1, result.getVersion());
        assertNotNull(result.getTimestamp());
        assertNotNull(result.getPayload());
        assertEquals("o-101", result.getPayload().getOrderId());
        assertEquals("SMS", result.getPayload().getNotificationType());
    }
}

