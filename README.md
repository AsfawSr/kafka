# Kafka Producer-Consumer Flow (Transactional Outbox)

This project now uses a transactional outbox flow so `OrderConsumer` does not publish directly to Kafka.

## Flow

1. `POST /api/orders` publishes a versioned envelope to Kafka topic `order`.
2. `OrderConsumer` listens to `order` and writes a row into `outbox_event` (`PENDING`) through `OutboxService`.
3. `OutboxPublisherPoller` runs on a schedule, fetches due outbox rows, and claims them (`PUBLISHING`).
4. `NotificationProducer` publishes a versioned envelope to one of:
   - `notification.email.trucksload`
   - `notification.sms.trucksload`
   - `notification.push.trucksload`
5. Publish success => outbox row becomes `PUBLISHED`; publish failure => retry metadata is updated.

## Outbox State Model

- `PENDING`: created and waiting for first publish attempt.
- `PUBLISHING`: claimed by a poller instance to avoid double send.
- `PUBLISHED`: successfully sent to Kafka.
- `FAILED_RETRYABLE`: failed but scheduled for another attempt.
- `FAILED_PERMANENT`: retries exhausted.

## Standard Event Envelope

All Kafka messages now use a shared envelope format:

```json
{
  "eventId": "8f76db31-9a4c-48a8-a54f-9c1e58b6fcb3",
  "eventType": "ORDER_CREATED",
  "version": 1,
  "timestamp": "2026-04-06T10:32:14.120Z",
  "payload": {
    "orderId": "o-1001",
    "userId": "u-42",
    "message": "Your order has shipped",
    "notificationType": "EMAIL"
  }
}
```

For notification topics, `eventType` is `NOTIFICATION_REQUESTED` and payload is `NotificationEvent`.

## Why This Pattern

- Prevents losing notification events between consume and publish steps.
- Gives a recoverable queue in your DB with clear processing state.
- Makes retries and failure handling explicit and inspectable.

## Example Request

```json
{
  "orderId": "o-1001",
  "userId": "u-42",
  "message": "Your order has shipped",
  "notificationType": "EMAIL"
}
```

## Try It

Run the app:

```powershell
.\mvnw.cmd spring-boot:run
```

Publish an order event:

```powershell
curl -X POST "http://localhost:8080/api/orders" -H "Content-Type: application/json" -d '{"orderId":"o-1001","userId":"u-42","message":"Your order has shipped","notificationType":"EMAIL"}'
```

Inspect outbox rows in H2 console (`/h2-console`) using:

```sql
select id, aggregate_id, notification_type, status, retry_count, next_attempt_at, published_at
from outbox_event
order by created_at desc;
```

## Outbox Publisher Settings

Defined in `application.properties` under `app.outbox.publisher.*`:

- `fixed-delay-ms`: poll interval.
- `batch-size`: rows fetched per polling run.
- `max-retries`: retry cap before permanent failure.
- `retry-backoff-ms`: delay between retries.
- `ack-timeout-ms`: max wait for Kafka send ack.

## Cross-Microservice Pattern

To consume this in another microservice, keep the same event shape (`OrderEvent` JSON), subscribe to the same topic (`order`) with a different consumer group, and implement your own business logic in that listener.

