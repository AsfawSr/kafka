package com.asfaw.kafka.Consumer;

import com.asfaw.kafka.notification.model.NotificationEventEnvelope;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class PushNotificationConsumer {

    @KafkaListener(
            topics = "notification.push.trucksload",
            groupId = "${app.kafka.groups.push:push-group}",
            containerFactory = "notificationKafkaListenerContainerFactory"
    )
    public void consume(NotificationEventEnvelope eventEnvelope) {
        System.out.println("Sending Push from envelope "
                + (eventEnvelope != null ? eventEnvelope.getEventId() : "null")
                + ": " + (eventEnvelope != null ? eventEnvelope.getPayload() : null));
    }
}
