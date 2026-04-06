package com.asfaw.kafka.config;

import com.asfaw.kafka.common.model.EventEnvelopeFactory;
import com.asfaw.kafka.notification.model.NotificationEvent;
import com.asfaw.kafka.notification.model.NotificationEventEnvelope;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;

import java.time.Instant;
import java.util.UUID;

public class NotificationEventEnvelopeCompatDeserializer implements Deserializer<NotificationEventEnvelope> {

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @Override
    public NotificationEventEnvelope deserialize(String topic, byte[] data) {
        if (data == null || data.length == 0) {
            return null;
        }

        try {
            JsonNode root = objectMapper.readTree(data);
            if (root.has("payload")) {
                NotificationEventEnvelope envelope = objectMapper.treeToValue(root, NotificationEventEnvelope.class);
                if (envelope.getEventId() == null || envelope.getEventId().isBlank()) {
                    envelope.setEventId(UUID.randomUUID().toString());
                }
                if (envelope.getEventType() == null || envelope.getEventType().isBlank()) {
                    envelope.setEventType(EventEnvelopeFactory.NOTIFICATION_REQUESTED_EVENT_TYPE);
                }
                if (envelope.getVersion() <= 0) {
                    envelope.setVersion(EventEnvelopeFactory.CURRENT_VERSION);
                }
                if (envelope.getTimestamp() == null) {
                    envelope.setTimestamp(Instant.now());
                }
                return envelope;
            }

            NotificationEvent legacyEvent = objectMapper.treeToValue(root, NotificationEvent.class);
            NotificationEventEnvelope legacyEnvelope = new NotificationEventEnvelope();
            legacyEnvelope.setEventId(UUID.randomUUID().toString());
            legacyEnvelope.setEventType(EventEnvelopeFactory.NOTIFICATION_REQUESTED_EVENT_TYPE);
            legacyEnvelope.setVersion(1);
            legacyEnvelope.setTimestamp(Instant.now());
            legacyEnvelope.setPayload(legacyEvent);
            return legacyEnvelope;
        } catch (Exception ex) {
            throw new SerializationException("Failed to deserialize notification event envelope", ex);
        }
    }
}

