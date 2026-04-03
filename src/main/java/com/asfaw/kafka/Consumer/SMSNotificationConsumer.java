package com.asfaw.kafka.Consumer;

import com.asfaw.kafka.notification.model.NotificationEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class SMSNotificationConsumer {

    @KafkaListener(
            topics = "notification.sms.trucksload",
            groupId = "${app.kafka.groups.sms:sms-group}",
            containerFactory = "notificationKafkaListenerContainerFactory"
    )
    public void consume(NotificationEvent event) {
        System.out.println("Sending SMS: " + event);
    }
}
