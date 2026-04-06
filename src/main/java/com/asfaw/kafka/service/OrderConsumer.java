package com.asfaw.kafka.service;

import com.asfaw.kafka.order.model.OrderEvent;
import com.asfaw.kafka.outbox.service.OutboxService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class OrderConsumer {

    private final OutboxService outboxService;

    public OrderConsumer(OutboxService outboxService) {
        this.outboxService = outboxService;
    }

    @KafkaListener(
            topics = "${app.kafka.topics.order:order}",
            groupId = "${app.kafka.groups.order-processor:order-processor-group}",
            containerFactory = "orderKafkaListenerContainerFactory"
    )
    public void consume(OrderEvent event) {
        outboxService.enqueueNotificationFromOrder(event);
        System.out.println("Order received and persisted to outbox: " + (event != null ? event.getOrderId() : "null"));
    }
}
