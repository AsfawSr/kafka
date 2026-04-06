package com.asfaw.kafka.service;

import com.asfaw.kafka.order.model.OrderEventEnvelope;
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
    public void consume(OrderEventEnvelope eventEnvelope) {
        if (eventEnvelope == null || eventEnvelope.getPayload() == null) {
            System.out.println("Skipping order envelope because payload is missing: " + eventEnvelope);
            return;
        }

        outboxService.enqueueNotificationFromOrder(eventEnvelope.getPayload());
        System.out.println(
                "Order envelope received: eventId=" + eventEnvelope.getEventId() +
                        ", orderId=" + eventEnvelope.getPayload().getOrderId()
        );
    }
}
