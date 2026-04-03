package com.asfaw.kafka.notification.controller;

import com.asfaw.kafka.notification.model.NotificationEvent;
import com.asfaw.kafka.notification.service.NotificationProducer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class NotificationControllerTest {

    @Mock
    private NotificationProducer producer;

    @InjectMocks
    private NotificationController controller;

    @Test
    void notifyReturnsAcceptedWhenMessageIsQueued() {
        NotificationEvent event = new NotificationEvent("u-1", "hello", "EMAIL");

        ResponseEntity<String> response = controller.notify(event);

        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
        assertEquals("Notification event sent!", response.getBody());
        verify(producer).send(event);
    }

    @Test
    void notifyReturnsBadRequestForInvalidType() {
        NotificationEvent event = new NotificationEvent("u-1", "hello", "FAX");
        doThrow(new IllegalArgumentException("Unknown notification type: FAX"))
                .when(producer).send(event);

        ResponseEntity<String> response = controller.notify(event);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Unknown notification type: FAX", response.getBody());
    }
}

