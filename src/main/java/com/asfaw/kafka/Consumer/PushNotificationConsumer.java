package com.asfaw.kafka.Consumer;

import com.asfaw.kafka.notification.model.NotificationEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class PushNotificationConsumer {

    @KafkaListener(
            topics = "notification.push.trucksload",
            groupId = "${app.kafka.groups.push:push-group}",
            containerFactory = "notificationKafkaListenerContainerFactory"
    )
    public void consume(NotificationEvent event) {
        System.out.println("Sending Push: " + event);
    }
}
