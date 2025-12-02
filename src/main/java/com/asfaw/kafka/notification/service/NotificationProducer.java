package com.asfaw.kafka.notification.service;

import com.asfaw.kafka.notification.model.NotificationEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class NotificationProducer {

    private final KafkaTemplate<String, NotificationEvent> kafkaTemplate;

    public NotificationProducer(KafkaTemplate<String, NotificationEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void send(NotificationEvent event) {

        String topic = switch (event.getType()) {
            case "EMAIL" -> "notification.email.trucksload";
            case "SMS" -> "notification.sms.trucksload";
            case "PUSH" -> "notification.push.trucksload";
            default -> throw new IllegalArgumentException("Unknown notification type");
        };

        kafkaTemplate.send(topic, event);
    }
}

