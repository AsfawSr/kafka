package com.asfaw.kafka.service;

import com.asfaw.kafka.common.model.EventEnvelopeFactory;
import com.asfaw.kafka.order.model.OrderEvent;
import com.asfaw.kafka.order.model.OrderEventEnvelope;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class ProducerService {

    private final KafkaTemplate<String, OrderEventEnvelope> orderKafkaTemplate;
    private final EventEnvelopeFactory envelopeFactory;

    public ProducerService(
            KafkaTemplate<String, OrderEventEnvelope> orderKafkaTemplate,
            EventEnvelopeFactory envelopeFactory
    ) {
        this.orderKafkaTemplate = orderKafkaTemplate;
        this.envelopeFactory = envelopeFactory;
    }

    public void sendOrder(OrderEvent event) {
        OrderEventEnvelope envelope = envelopeFactory.orderEnvelope(event);
        orderKafkaTemplate.send("order", envelope.getEventId(), envelope);
    }
}
