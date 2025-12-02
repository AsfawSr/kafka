package com.asfaw.kafka.notification.controller;

import com.asfaw.kafka.notification.model.NotificationEvent;
import com.asfaw.kafka.notification.service.NotificationProducer;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notify")
public class NotificationController {

    private final NotificationProducer producer;

    public NotificationController(NotificationProducer producer) {
        this.producer = producer;
    }

    @PostMapping
    public String notify(@RequestBody NotificationEvent event) {
        producer.send(event);
        return "Notification event sent!";
    }
}


