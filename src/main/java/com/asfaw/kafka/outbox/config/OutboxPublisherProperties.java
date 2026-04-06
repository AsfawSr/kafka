package com.asfaw.kafka.outbox.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app.outbox.publisher")
public class OutboxPublisherProperties {

    private long fixedDelayMs = 3000;
    private int batchSize = 25;
    private int maxRetries = 5;
    private long retryBackoffMs = 15000;
    private long ackTimeoutMs = 5000;

    public long getFixedDelayMs() {
        return fixedDelayMs;
    }

    public void setFixedDelayMs(long fixedDelayMs) {
        this.fixedDelayMs = fixedDelayMs;
    }

    public int getBatchSize() {
        return batchSize;
    }

    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }

    public int getMaxRetries() {
        return maxRetries;
    }

    public void setMaxRetries(int maxRetries) {
        this.maxRetries = maxRetries;
    }

    public long getRetryBackoffMs() {
        return retryBackoffMs;
    }

    public void setRetryBackoffMs(long retryBackoffMs) {
        this.retryBackoffMs = retryBackoffMs;
    }

    public long getAckTimeoutMs() {
        return ackTimeoutMs;
    }

    public void setAckTimeoutMs(long ackTimeoutMs) {
        this.ackTimeoutMs = ackTimeoutMs;
    }
}

