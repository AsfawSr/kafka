package com.asfaw.kafka.service;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class OrderConsumer {

    @KafkaListener(topics = "order", groupId = "demo-group")
    public void consume(String message) {
        System.out.println("📥 Consumed: " + message);
    }
}

