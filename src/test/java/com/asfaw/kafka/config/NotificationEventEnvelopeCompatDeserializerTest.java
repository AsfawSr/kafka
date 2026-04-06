package com.asfaw.kafka.config;

import com.asfaw.kafka.notification.model.NotificationEventEnvelope;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class NotificationEventEnvelopeCompatDeserializerTest {

    private final NotificationEventEnvelopeCompatDeserializer deserializer = new NotificationEventEnvelopeCompatDeserializer();

    @Test
    void deserializeSupportsEnvelopeMessage() {
        String json = """
                {
                  "eventId": "evt-10",
                  "eventType": "NOTIFICATION_REQUESTED",
                  "version": 1,
                  "timestamp": "2026-04-06T09:00:00Z",
                  "payload": {
                    "userId": "u-1",
                    "message": "hello",
                    "type": "EMAIL"
                  }
                }
                """;

        NotificationEventEnvelope result = deserializer.deserialize("notification.email.trucksload", json.getBytes(StandardCharsets.UTF_8));

        assertEquals("evt-10", result.getEventId());
        assertEquals("NOTIFICATION_REQUESTED", result.getEventType());
        assertEquals(1, result.getVersion());
        assertNotNull(result.getPayload());
        assertEquals("EMAIL", result.getPayload().getType());
    }

    @Test
    void deserializeSupportsLegacyFlatNotificationEvent() {
        String json = """
                {
                  "userId": "u-2",
                  "message": "legacy",
                  "type": "PUSH"
                }
                """;

        NotificationEventEnvelope result = deserializer.deserialize("notification.push.trucksload", json.getBytes(StandardCharsets.UTF_8));

        assertNotNull(result.getEventId());
        assertEquals("NOTIFICATION_REQUESTED", result.getEventType());
        assertEquals(1, result.getVersion());
        assertNotNull(result.getTimestamp());
        assertNotNull(result.getPayload());
        assertEquals("u-2", result.getPayload().getUserId());
        assertEquals("PUSH", result.getPayload().getType());
    }
}

