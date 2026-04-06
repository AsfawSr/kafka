package com.asfaw.kafka.service;

import com.asfaw.kafka.notification.service.NotificationProducer;
import com.asfaw.kafka.order.model.OrderEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class OrderConsumerTest {

    @Mock
    private NotificationProducer notificationProducer;

    @InjectMocks
    private OrderConsumer orderConsumer;

    @Test
    void consumeBuildsNotificationEventAndPublishesIt() {
        OrderEvent orderEvent = new OrderEvent("o-100", "u-1", "Your order is confirmed", "EMAIL");

        orderConsumer.consume(orderEvent);

        verify(notificationProducer).send(argThat(event ->
                "u-1".equals(event.getUserId())
                        && "Your order is confirmed".equals(event.getMessage())
                        && "EMAIL".equals(event.getType())
        ));
    }

    @Test
    void consumeSkipsWhenNotificationTypeMissing() {
        OrderEvent orderEvent = new OrderEvent("o-101", "u-1", "hello", " ");

        orderConsumer.consume(orderEvent);


        verify(notificationProducer, never()).send(argThat(event -> true));
    }
}

