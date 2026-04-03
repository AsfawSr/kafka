package com.asfaw.kafka.service;

import com.asfaw.kafka.order.model.OrderEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumerService {

    @KafkaListener(
            topics = "${app.kafka.topics.order:order}",
            groupId = "${app.kafka.groups.order-audit:order-audit-group}",
            containerFactory = "orderKafkaListenerContainerFactory"
    )
    public void consume(OrderEvent message) {
        System.out.println("Order audit consumer received: " + message);
    }
}
