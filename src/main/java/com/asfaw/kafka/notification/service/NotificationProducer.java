package com.asfaw.kafka.notification.service;

import com.asfaw.kafka.common.model.EventEnvelopeFactory;
import com.asfaw.kafka.notification.model.NotificationEvent;
import com.asfaw.kafka.notification.model.NotificationEventEnvelope;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.Locale;
import java.util.Map;

@Service
public class NotificationProducer {

    private static final Map<String, String> TOPIC_BY_TYPE = Map.of(
            "EMAIL", "notification.email.trucksload",
            "SMS", "notification.sms.trucksload",
            "PUSH", "notification.push.trucksload"
    );

    private final KafkaTemplate<String, NotificationEventEnvelope> kafkaTemplate;
    private final EventEnvelopeFactory envelopeFactory;

    public NotificationProducer(
            KafkaTemplate<String, NotificationEventEnvelope> kafkaTemplate,
            EventEnvelopeFactory envelopeFactory
    ) {
        this.kafkaTemplate = kafkaTemplate;
        this.envelopeFactory = envelopeFactory;
    }

    public void send(NotificationEvent event) {
        String topic = resolveTopic(event);
        NotificationEventEnvelope envelope = envelopeFactory.notificationEnvelope(event);
        kafkaTemplate.send(topic, envelope.getEventId(), envelope);
    }

    public CompletableFuture<SendResult<String, NotificationEventEnvelope>> send(String key, NotificationEvent event) {
        String topic = resolveTopic(event);
        NotificationEventEnvelope envelope = envelopeFactory.notificationEnvelope(event);
        return kafkaTemplate.send(topic, key, envelope);
    }

    private String resolveTopic(NotificationEvent event) {
        if (event == null || event.getType() == null || event.getType().isBlank()) {
            throw new IllegalArgumentException("Notification type is required");
        }

        String normalizedType = event.getType().trim().toUpperCase(Locale.ROOT);
        String topic = TOPIC_BY_TYPE.get(normalizedType);

        if (topic == null) {
            throw new IllegalArgumentException("Unknown notification type: " + event.getType());
        }

        return topic;
    }
}
