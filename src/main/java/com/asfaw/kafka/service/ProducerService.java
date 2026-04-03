package com.asfaw.kafka.service;

import com.asfaw.kafka.order.model.OrderEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class ProducerService {

    private final KafkaTemplate<String, OrderEvent> orderKafkaTemplate;

    public ProducerService(KafkaTemplate<String, OrderEvent> orderKafkaTemplate) {
        this.orderKafkaTemplate = orderKafkaTemplate;
    }

    public void sendOrder(OrderEvent event) {
        orderKafkaTemplate.send("order", event);
    }
}
