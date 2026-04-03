package com.asfaw.kafka.notification.controller;

import com.asfaw.kafka.notification.model.NotificationEvent;
import com.asfaw.kafka.notification.service.NotificationProducer;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<String> notify(@RequestBody NotificationEvent event) {
        try {
            producer.send(event);
            return ResponseEntity.status(HttpStatus.ACCEPTED).body("Notification event sent!");
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }
}
