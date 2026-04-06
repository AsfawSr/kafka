package com.asfaw.kafka.config;

import com.asfaw.kafka.common.model.EventEnvelopeFactory;
import com.asfaw.kafka.order.model.OrderEvent;
import com.asfaw.kafka.order.model.OrderEventEnvelope;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;

import java.time.Instant;
import java.util.UUID;

public class OrderEventEnvelopeCompatDeserializer implements Deserializer<OrderEventEnvelope> {

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @Override
    public OrderEventEnvelope deserialize(String topic, byte[] data) {
        if (data == null || data.length == 0) {
            return null;
        }

        try {
            JsonNode root = objectMapper.readTree(data);
            if (root.has("payload")) {
                OrderEventEnvelope envelope = objectMapper.treeToValue(root, OrderEventEnvelope.class);
                if (envelope.getEventId() == null || envelope.getEventId().isBlank()) {
                    envelope.setEventId(UUID.randomUUID().toString());
                }
                if (envelope.getEventType() == null || envelope.getEventType().isBlank()) {
                    envelope.setEventType(EventEnvelopeFactory.ORDER_CREATED_EVENT_TYPE);
                }
                if (envelope.getVersion() <= 0) {
                    envelope.setVersion(EventEnvelopeFactory.CURRENT_VERSION);
                }
                if (envelope.getTimestamp() == null) {
                    envelope.setTimestamp(Instant.now());
                }
                return envelope;
            }

            OrderEvent legacyEvent = objectMapper.treeToValue(root, OrderEvent.class);
            OrderEventEnvelope legacyEnvelope = new OrderEventEnvelope();
            legacyEnvelope.setEventId(UUID.randomUUID().toString());
            legacyEnvelope.setEventType(EventEnvelopeFactory.ORDER_CREATED_EVENT_TYPE);
            legacyEnvelope.setVersion(1);
            legacyEnvelope.setTimestamp(Instant.now());
            legacyEnvelope.setPayload(legacyEvent);
            return legacyEnvelope;
        } catch (Exception ex) {
            throw new SerializationException("Failed to deserialize order event envelope", ex);
        }
    }
}

