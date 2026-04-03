package com.asfaw.kafka.notification.service;

import com.asfaw.kafka.notification.model.NotificationEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.Map;

@Service
public class NotificationProducer {

    private static final Map<String, String> TOPIC_BY_TYPE = Map.of(
            "EMAIL", "notification.email.trucksload",
            "SMS", "notification.sms.trucksload",
            "PUSH", "notification.push.trucksload"
    );

    private final KafkaTemplate<String, NotificationEvent> kafkaTemplate;

    public NotificationProducer(KafkaTemplate<String, NotificationEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void send(NotificationEvent event) {
        if (event == null || event.getType() == null || event.getType().isBlank()) {
            throw new IllegalArgumentException("Notification type is required");
        }

        String normalizedType = event.getType().trim().toUpperCase(Locale.ROOT);
        String topic = TOPIC_BY_TYPE.get(normalizedType);

        if (topic == null) {
            throw new IllegalArgumentException("Unknown notification type: " + event.getType());
        }

        kafkaTemplate.send(topic, event);
    }
}
