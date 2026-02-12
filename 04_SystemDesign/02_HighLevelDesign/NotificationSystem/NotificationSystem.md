# Notification System — High-Level Design

## Problem Statement

Design a notification system that can:

- **Send notifications** via multiple channels: Push (iOS/Android), Email, SMS, In-App
- **Handle 1 billion notifications per day** with low latency
- **Support prioritization**: Critical alerts delivered immediately, marketing batched
- **Provide delivery tracking**: sent, delivered, opened, clicked
- **Support user preferences**: users can opt-out of specific channels or notification types
- **Handle rate limiting** per user to prevent notification fatigue
- **Support templating** for consistent notification formatting

---

## Requirements

### Functional Requirements

1. **Multi-channel delivery**: Push, Email, SMS, In-App, Webhook
2. **User preferences**: Per-channel, per-notification-type opt-in/out
3. **Templating**: Define notification templates with variable substitution
4. **Scheduling**: Send now or schedule for later
5. **Priority levels**: CRITICAL (immediate), HIGH (within 1 min), MEDIUM (within 5 min), LOW (batched hourly)
6. **Analytics**: Delivery rate, open rate, click-through rate per notification type

### Non-Functional Requirements

1. **Scale**: 1B notifications/day (11,574 notifications/sec average, 50K/sec peak)
2. **Latency**: CRITICAL notifications delivered within 1 second, MEDIUM within 5 minutes
3. **Reliability**: At-least-once delivery (no notification lost, duplicates acceptable)
4. **Availability**: 99.99% uptime

---

## High-Level Architecture

```
                    ┌───────────────────┐
                    │  Notification API  │  ← Entry point for all services
                    │  (REST / gRPC)     │
                    └────────┬──────────┘
                             │
                    ┌────────▼──────────┐
                    │  Validation &     │
                    │  Enrichment       │  ← Check preferences, rate limit,
                    │  Service          │     resolve template, add metadata
                    └────────┬──────────┘
                             │
                    ┌────────▼──────────┐
                    │     Kafka         │  ← One topic per priority
                    │                   │     critical.notifications
                    │  Priority-based   │     high.notifications
                    │  Topics           │     medium.notifications
                    │                   │     low.notifications
                    └────────┬──────────┘
                             │
          ┌──────────────────┼──────────────────┐──────────────────┐
          │                  │                  │                  │
  ┌───────▼──────┐  ┌───────▼──────┐  ┌───────▼──────┐  ┌───────▼──────┐
  │ Push Worker  │  │ Email Worker │  │ SMS Worker   │  │ In-App Worker│
  │              │  │              │  │              │  │              │
  │ APNs/Firebase│  │ SES/SendGrid │  │ Twilio/SNS   │  │ WebSocket    │
  └──────┬───────┘  └──────┬───────┘  └──────┬───────┘  └──────┬───────┘
         │                 │                 │                 │
  ┌──────▼─────────────────▼─────────────────▼─────────────────▼───────┐
  │                        Delivery Tracker                            │
  │                 (Track: sent, delivered, opened, clicked)          │
  │                          Cassandra / ClickHouse                    │
  └────────────────────────────────────────────────────────────────────┘
```

---

## Component Deep Dive

### 1. Notification API

```java
// REST endpoint for sending notifications
@RestController
@RequestMapping("/api/v1/notifications")
public class NotificationController {

    @PostMapping
    public ResponseEntity<NotificationResponse> send(
            @RequestBody NotificationRequest request) {

        // Validate
        // Enrich (resolve template, check preferences)
        // Publish to Kafka
        // Return async acknowledgment

        return ResponseEntity.accepted()
            .body(new NotificationResponse(notificationId, "QUEUED"));
    }
}

public record NotificationRequest(
    String recipientUserId,
    String notificationType,     // "ORDER_SHIPPED", "PAYMENT_RECEIVED", etc.
    Priority priority,           // CRITICAL, HIGH, MEDIUM, LOW
    List<Channel> channels,      // PUSH, EMAIL, SMS, IN_APP
    Map<String, String> data,    // Template variables: {orderNumber: "12345"}
    LocalDateTime scheduledAt    // null = send now
) {}

public enum Priority { CRITICAL, HIGH, MEDIUM, LOW; }
public enum Channel { PUSH, EMAIL, SMS, IN_APP, WEBHOOK; }
```

### 2. Validation & Enrichment Service

Before queuing, every notification goes through:

```
1. Rate Limit Check
   - Max 10 push notifications per hour per user
   - Max 3 SMS per day per user
   - CRITICAL priority bypasses rate limits

2. User Preference Check
   - User opted out of marketing emails? → Skip EMAIL channel
   - User disabled push for "PROMOTION" type? → Skip PUSH

3. Template Resolution
   - notificationType = "ORDER_SHIPPED"
   - Template: "Your order {{orderNumber}} has shipped! Track: {{trackingUrl}}"
   - Resolved: "Your order 12345 has shipped! Track: https://..."

4. Contact Resolution
   - Resolve userId → email, phone, device tokens
   - User may have multiple devices (send push to all)

5. Deduplication
   - Idempotency key prevents duplicate sends
   - Hash(recipientId + notificationType + data) checked against Redis (TTL: 1 hour)
```

### 3. Priority-Based Kafka Topics

```
critical.notifications  → Partition count: 100, Consumer parallelism: 100
high.notifications      → Partition count: 50
medium.notifications    → Partition count: 20
low.notifications       → Partition count: 10 (batched processing)
```

**Why separate topics per priority?**
- CRITICAL messages are never blocked behind a queue of LOW messages.
- Each priority level can have different consumer group sizes.
- LOW priority consumers can batch process (more efficient).

### 4. Channel-Specific Workers

Each worker is a Kafka consumer that handles one channel:

**Push Worker (APNs / Firebase)**:
```java
@KafkaListener(topics = {"critical.notifications", "high.notifications"})
public void processPushNotification(NotificationEvent event) {
    if (!event.getChannels().contains(Channel.PUSH)) return;

    List<String> deviceTokens = deviceTokenService.getTokens(event.getUserId());

    for (String token : deviceTokens) {
        try {
            firebaseMessaging.send(Message.builder()
                .setToken(token)
                .setNotification(Notification.builder()
                    .setTitle(event.getTitle())
                    .setBody(event.getBody())
                    .build())
                .putAllData(event.getData())
                .build());

            deliveryTracker.markSent(event.getId(), Channel.PUSH, token);
        } catch (FirebaseMessagingException e) {
            if (e.getMessagingErrorCode() == MessagingErrorCode.UNREGISTERED) {
                // Token is invalid — remove it
                deviceTokenService.removeToken(token);
            } else {
                // Retry with exponential backoff
                retryQueue.add(event, Channel.PUSH);
            }
        }
    }
}
```

**Email Worker (SES / SendGrid)**:
```java
@KafkaListener(topics = {"critical.notifications", "high.notifications",
                          "medium.notifications", "low.notifications"})
public void processEmailNotification(NotificationEvent event) {
    if (!event.getChannels().contains(Channel.EMAIL)) return;

    String email = userService.getEmail(event.getUserId());
    String htmlBody = templateEngine.render(event.getEmailTemplate(), event.getData());

    SendEmailRequest request = SendEmailRequest.builder()
        .destination(Destination.builder().toAddresses(email).build())
        .message(Message.builder()
            .subject(Content.builder().data(event.getTitle()).build())
            .body(Body.builder().html(Content.builder().data(htmlBody).build()).build())
            .build())
        .source("notifications@app.com")
        .build();

    sesClient.sendEmail(request);
    deliveryTracker.markSent(event.getId(), Channel.EMAIL, email);
}
```

### 5. Retry and Dead Letter Queue

```
Notification → Worker → Send
                          │
                  Success ─┤─ Failure
                  ✓ Track  │
                           ├─ Retry 1 (1 sec delay)
                           ├─ Retry 2 (5 sec delay)
                           ├─ Retry 3 (30 sec delay)
                           └─ Dead Letter Queue (DLQ)
                              ↓
                           Alert ops team
                           Manual investigation
```

### 6. Delivery Tracking

```sql
-- ClickHouse table for notification analytics
CREATE TABLE notification_events (
    notification_id String,
    user_id String,
    notification_type String,
    channel String,          -- 'push', 'email', 'sms', 'in_app'
    event_type String,       -- 'sent', 'delivered', 'opened', 'clicked', 'failed'
    event_time DateTime,
    metadata String          -- JSON: device_type, email_provider, etc.
) ENGINE = MergeTree()
PARTITION BY toYYYYMM(event_time)
ORDER BY (notification_type, event_time);

-- Query: Delivery rate for ORDER_SHIPPED last 7 days
SELECT
    channel,
    countIf(event_type = 'sent') as sent,
    countIf(event_type = 'delivered') as delivered,
    round(delivered / sent * 100, 2) as delivery_rate
FROM notification_events
WHERE notification_type = 'ORDER_SHIPPED'
  AND event_time > now() - INTERVAL 7 DAY
GROUP BY channel;
```

---

## User Preference Storage

```sql
CREATE TABLE user_notification_preferences (
    user_id UUID PRIMARY KEY,
    push_enabled BOOLEAN DEFAULT TRUE,
    email_enabled BOOLEAN DEFAULT TRUE,
    sms_enabled BOOLEAN DEFAULT FALSE,
    in_app_enabled BOOLEAN DEFAULT TRUE,
    quiet_hours_start TIME,          -- e.g., 22:00
    quiet_hours_end TIME,            -- e.g., 08:00
    timezone VARCHAR(50),            -- e.g., 'America/New_York'
    category_preferences JSONB       -- {"MARKETING": {"push": false, "email": true}}
);
```

---

## Scaling Considerations

| Component | Scale | Solution |
|-----------|-------|----------|
| API | 50K req/sec | 10-20 stateless API servers behind LB |
| Kafka | 1B messages/day | Multi-partition topics, 3-node Kafka cluster |
| Push Worker | 10M pushes/day | 5-10 worker instances, parallel device sends |
| Email Worker | 500M emails/day | SES handles up to 50K/sec, batch for LOW priority |
| SMS Worker | 10M SMS/day | Twilio scales automatically |
| ClickHouse | 1B events/day | 3-node cluster with MergeTree engine |

---

## Interview Discussion Points

1. **How do you prevent notification fatigue?**
   - Per-user rate limiting (max N per hour per channel).
   - Intelligent batching: Group multiple LOW priority notifications into a digest.
   - Quiet hours: Respect user timezone, hold non-CRITICAL notifications during sleep hours.
   - Frequency capping: No more than 1 notification of the same type per user per hour.

2. **How do you handle at-least-once delivery without annoying duplicates?**
   - Kafka ensures at-least-once delivery to workers.
   - Deduplication at the worker level using notification_id + channel as idempotency key.
   - For push notifications, the device OS handles dedup (same notification_id replaces).
   - For email, include `Message-ID` header for dedup by email providers.

3. **How would you add a new channel (e.g., WhatsApp)?**
   - Create a new `WhatsAppWorker` that implements the same consumer interface.
   - Subscribe to the same Kafka topics.
   - Add `WHATSAPP` to the `Channel` enum and user preferences.
   - Zero changes to the API or enrichment service (Open/Closed Principle).

4. **What about internationalization (i18n)?**
   - Store templates in multiple languages.
   - User preference includes `locale`.
   - Template resolution selects the correct language version.

