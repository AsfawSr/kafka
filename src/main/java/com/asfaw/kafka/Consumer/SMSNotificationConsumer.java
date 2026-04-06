package com.asfaw.kafka.Consumer;

import com.asfaw.kafka.notification.model.NotificationEventEnvelope;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class SMSNotificationConsumer {

    @KafkaListener(
            topics = "notification.sms.trucksload",
            groupId = "${app.kafka.groups.sms:sms-group}",
            containerFactory = "notificationKafkaListenerContainerFactory"
    )
    public void consume(NotificationEventEnvelope eventEnvelope) {
        System.out.println("Sending SMS from envelope "
                + (eventEnvelope != null ? eventEnvelope.getEventId() : "null")
                + ": " + (eventEnvelope != null ? eventEnvelope.getPayload() : null));
    }
}
