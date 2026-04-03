package com.asfaw.kafka.service;

import com.asfaw.kafka.notification.model.NotificationEvent;
import com.asfaw.kafka.notification.service.NotificationProducer;
import com.asfaw.kafka.order.model.OrderEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class OrderConsumer {

    private final NotificationProducer notificationProducer;

    public OrderConsumer(NotificationProducer notificationProducer) {
        this.notificationProducer = notificationProducer;
    }

    @KafkaListener(
            topics = "${app.kafka.topics.order:order}",
            groupId = "${app.kafka.groups.order-processor:order-processor-group}",
            containerFactory = "orderKafkaListenerContainerFactory"
    )
    public void consume(OrderEvent event) {
        if (event == null || !StringUtils.hasText(event.getNotificationType())) {
            System.out.println("Skipping order event due to missing notificationType: " + event);
            return;
        }

        NotificationEvent notificationEvent = new NotificationEvent(
                event.getUserId(),
                event.getMessage(),
                event.getNotificationType()
        );
        notificationProducer.send(notificationEvent);

        System.out.println("Order processed for notification flow: " + event.getOrderId());
    }
}
