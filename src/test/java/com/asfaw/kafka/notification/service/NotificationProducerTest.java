package com.asfaw.kafka.notification.service;

import com.asfaw.kafka.common.model.EventEnvelopeFactory;
import com.asfaw.kafka.notification.model.NotificationEvent;
import com.asfaw.kafka.notification.model.NotificationEventEnvelope;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class NotificationProducerTest {

    @Mock
    private KafkaTemplate<String, NotificationEventEnvelope> kafkaTemplate;

    @Mock
    private EventEnvelopeFactory envelopeFactory;

    @InjectMocks
    private NotificationProducer producer;

    @Test
    void sendPublishesToExpectedTopicForCaseInsensitiveType() {
        NotificationEvent event = new NotificationEvent("u-1", "hello", "email");
        NotificationEventEnvelope envelope = new NotificationEventEnvelope();
        envelope.setEventId("evt-100");
        envelope.setPayload(event);
        org.mockito.Mockito.when(envelopeFactory.notificationEnvelope(event)).thenReturn(envelope);

        producer.send(event);

        ArgumentCaptor<String> topicCaptor = ArgumentCaptor.forClass(String.class);
        verify(kafkaTemplate).send(topicCaptor.capture(), org.mockito.ArgumentMatchers.eq("evt-100"), org.mockito.ArgumentMatchers.eq(envelope));
        assertEquals("notification.email.trucksload", topicCaptor.getValue());
    }

    @Test
    void sendThrowsForUnknownType() {
        NotificationEvent event = new NotificationEvent("u-1", "hello", "fax");

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> producer.send(event));

        assertEquals("Unknown notification type: fax", ex.getMessage());
        verifyNoInteractions(kafkaTemplate);
    }

    @Test
    void sendThrowsWhenTypeMissing() {
        NotificationEvent event = new NotificationEvent("u-1", "hello", " ");

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> producer.send(event));

        assertEquals("Notification type is required", ex.getMessage());
        verifyNoInteractions(kafkaTemplate);
    }
}

