package com.asfaw.kafka.outbox.model;

public enum OutboxStatus {
    PENDING,
    PUBLISHING,
    PUBLISHED,
    FAILED_RETRYABLE,
    FAILED_PERMANENT
}

