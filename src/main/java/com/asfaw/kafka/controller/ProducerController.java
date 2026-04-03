package com.asfaw.kafka.controller;

import com.asfaw.kafka.order.model.OrderEvent;
import com.asfaw.kafka.service.ProducerService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
public class ProducerController {

    private final ProducerService producerService;

    public ProducerController(ProducerService producerService) {
        this.producerService = producerService;
    }

    @PostMapping
    public String publish(@RequestBody OrderEvent event) {
        producerService.sendOrder(event);
        return "Order event sent: " + event.getOrderId();
    }
}
