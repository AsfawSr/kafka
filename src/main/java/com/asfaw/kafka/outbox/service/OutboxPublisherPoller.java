package com.asfaw.kafka.outbox.service;

import com.asfaw.kafka.notification.model.NotificationEvent;
import com.asfaw.kafka.notification.service.NotificationProducer;
import com.asfaw.kafka.outbox.config.OutboxPublisherProperties;
import com.asfaw.kafka.outbox.model.OutboxEvent;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class OutboxPublisherPoller {

    private final OutboxService outboxService;
    private final NotificationProducer notificationProducer;
    private final OutboxPublisherProperties properties;

    public OutboxPublisherPoller(
            OutboxService outboxService,
            NotificationProducer notificationProducer,
            OutboxPublisherProperties properties
    ) {
        this.outboxService = outboxService;
        this.notificationProducer = notificationProducer;
        this.properties = properties;
    }

    @Scheduled(fixedDelayString = "${app.outbox.publisher.fixed-delay-ms:3000}")
    public void publishDueEvents() {
        List<OutboxEvent> dueEvents = outboxService.fetchDueEvents(properties.getBatchSize());
        for (OutboxEvent dueEvent : dueEvents) {
            processOne(dueEvent);
        }
    }

    private void processOne(OutboxEvent outboxEvent) {
        boolean claimed = outboxService.claimForPublishing(outboxEvent.getId(), outboxEvent.getStatus());
        if (!claimed) {
            return;
        }

        NotificationEvent notificationEvent = new NotificationEvent(
                outboxEvent.getUserId(),
                outboxEvent.getMessage(),
                outboxEvent.getNotificationType()
        );

        try {
            notificationProducer.send(outboxEvent.getId(), notificationEvent)
                    .get(properties.getAckTimeoutMs(), TimeUnit.MILLISECONDS);
            outboxService.markPublished(outboxEvent.getId());
            System.out.println("Published outbox event " + outboxEvent.getId());
        } catch (Exception ex) {
            outboxService.markFailed(
                    outboxEvent.getId(),
                    outboxEvent.getRetryCount(),
                    properties.getMaxRetries(),
                    Duration.ofMillis(properties.getRetryBackoffMs()),
                    ex.getMessage()
            );
            System.out.println("Failed publishing outbox event " + outboxEvent.getId() + ": " + ex.getMessage());
        }
    }
}

