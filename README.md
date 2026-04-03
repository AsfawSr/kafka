# Kafka Producer-Consumer Flow

This project now demonstrates a full flow where one produced message becomes input to another consumer-driven feature.

## Flow

1. `POST /api/orders` publishes an `OrderEvent` to Kafka topic `order`.
2. `OrderConsumer` listens to `order` and maps the message into a `NotificationEvent`.
3. `NotificationProducer` routes that `NotificationEvent` to one of:
   - `notification.email.trucksload`
   - `notification.sms.trucksload`
   - `notification.push.trucksload`
4. Channel consumers process those events.

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

## Cross-Microservice Pattern

To consume this in another microservice, keep the same event shape (`OrderEvent` JSON), subscribe to the same topic (`order`) with a different consumer group, and implement your own business logic in that listener.

