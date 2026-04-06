package com.asfaw.kafka.Consumer;

import com.asfaw.kafka.notification.model.NotificationEventEnvelope;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class EmailNotificationConsumer {

    @KafkaListener(
            topics = "notification.email.trucksload",
            groupId = "${app.kafka.groups.email:email-group}",
            containerFactory = "notificationKafkaListenerContainerFactory"
    )
    public void consume(NotificationEventEnvelope eventEnvelope) {
        System.out.println("Sending Email from envelope "
                + (eventEnvelope != null ? eventEnvelope.getEventId() : "null")
                + ": " + (eventEnvelope != null ? eventEnvelope.getPayload() : null));
    }
}
