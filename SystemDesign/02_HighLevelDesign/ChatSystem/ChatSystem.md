# Real-Time Chat System — High-Level Design

## Problem Statement

Design a real-time chat system (like WhatsApp, Slack, or Telegram) that can:

- **1-on-1 messaging** with real-time delivery
- **Group messaging** (up to 500 members per group)
- **Online/offline status (presence)** tracking
- **Message delivery guarantees** (sent → delivered → read receipts)
- **Offline message delivery** (messages stored and delivered when user comes online)
- **Message search** by keyword
- **Support 500M daily active users** with 50B messages per day

---

## Requirements

### Functional Requirements

1. **1:1 Chat**: User A sends message to User B. If B is online, deliver in real-time. If offline, store and deliver when B comes online.
2. **Group Chat**: Messages sent to a group are delivered to all group members.
3. **Message Status**: Sent (single check), Delivered (double check), Read (blue check).
4. **Presence**: Show online/offline/last-seen status.
5. **Push Notifications**: Notify offline users of new messages.
6. **Media Support**: Send images, videos, documents (via URL, not inline).
7. **Message History**: Users can scroll back through conversation history.

### Non-Functional Requirements

1. **Latency**: < 100ms for message delivery between online users.
2. **Scale**: 500M DAU, 50B messages/day (~580K msg/sec average, 1.5M msg/sec peak).
3. **Availability**: 99.99% uptime.
4. **Ordering**: Messages within a conversation are ordered (eventual consistency across devices is acceptable).
5. **Storage**: Messages stored for 5 years. ~50B/day × 365 × 5 × 100 bytes = ~9PB.

---

## Capacity Estimation

```
Messages:
  50B messages/day = 580K messages/sec (average)
  Peak (3x): 1.74M messages/sec

Storage:
  Average message: 100 bytes (text) + 50 bytes (metadata) = 150 bytes
  Daily: 50B × 150 bytes = 7.5TB/day
  5 years: 7.5TB × 365 × 5 = ~13.7PB

Connections:
  500M DAU, assume 60% online at peak = 300M concurrent WebSocket connections
  Each connection: ~10KB memory → 300M × 10KB = 3TB RAM for connections

Bandwidth:
  Incoming: 580K msg/sec × 150 bytes = 87 MB/s
  Outgoing: Same for 1:1, 10x-100x for group (fan-out) = 870 MB/s to 8.7 GB/s
```

---

## High-Level Architecture

```
┌──────────┐     ┌──────────────────┐     ┌──────────────────┐
│  Client   │────▶│   Load Balancer  │────▶│   Chat Service   │
│(Mobile/Web)│    │  (Layer 4/7)     │     │  (WebSocket)     │
└──────────┘     └──────────────────┘     └────────┬─────────┘
                                                    │
              ┌─────────────────────────────────────┼─────────────────────────────┐
              │                                     │                             │
     ┌────────▼────────┐              ┌────────────▼────────────┐    ┌──────────▼────────┐
     │  Session Store  │              │     Message Queue       │    │  Presence Service  │
     │  (Redis)        │              │     (Kafka)             │    │  (Redis + PubSub)  │
     │                 │              │                         │    │                    │
     │ userId → serverId│              │  Topic: chat.messages  │    │ userId → status    │
     │ userId → wsConn  │              │  Topic: chat.receipts  │    │ userId → lastSeen  │
     └─────────────────┘              └─────────────┬───────────┘    └───────────────────┘
                                                    │
              ┌─────────────────────────────────────┼─────────────────────────────┐
              │                                     │                             │
     ┌────────▼────────┐              ┌────────────▼────────────┐    ┌──────────▼────────┐
     │  Message Store  │              │  Push Notification      │    │  Media Service    │
     │  (Cassandra)    │              │  Service                │    │  (S3 + CDN)       │
     │                 │              │  (Firebase/APNs)        │    │                    │
     │  Partition key:  │              │                         │    │  Upload → S3 URL  │
     │  conversation_id│              │  Offline → push notif   │    │  Deliver URL only │
     └─────────────────┘              └─────────────────────────┘    └───────────────────┘
```

---

## Component Deep Dive

### 1. Chat Service (WebSocket Server)

```
Client ──── WebSocket ──── Chat Server ──── Kafka ──── Chat Server ──── Client
(User A)                   (Server 1)                   (Server 2)      (User B)
```

- Each client maintains a persistent WebSocket connection to one Chat Server.
- The **Session Store (Redis)** maps `userId → serverId` so we know which server hosts a user's connection.
- When User A sends a message to User B:
  1. A's Chat Server receives the message via WebSocket.
  2. It publishes the message to Kafka topic `chat.messages`.
  3. B's Chat Server (consumer) reads the message from Kafka.
  4. It looks up B's WebSocket connection and delivers the message.

**Why Kafka between Chat Servers?**
- Decouples sender and receiver servers.
- If B's server is temporarily overwhelmed, Kafka buffers messages.
- Enables replay if a server crashes (Kafka retention).

### 2. Message Delivery Flow (1:1)

```
Step 1: User A → Chat Server 1 (via WebSocket)
        Message: {from: A, to: B, text: "Hello", msgId: "uuid-123", timestamp: ...}

Step 2: Chat Server 1 → Kafka (topic: chat.messages)
        + Store to Cassandra (message_status: SENT)
        + Send ACK to User A: "Message SENT ✓"

Step 3: Kafka → Chat Server 2 (B's server, consumer)
        Look up B's WebSocket connection in Redis.

Step 4: If B is ONLINE:
            Chat Server 2 → User B (via WebSocket)
            B's client sends ACK → Chat Server 2
            Update Cassandra: message_status = DELIVERED ✓✓
            Notify A: "Message DELIVERED ✓✓"

        If B is OFFLINE:
            Push Notification Service → APNs/Firebase → B's device
            When B comes online, fetch undelivered messages from Cassandra.
```

### 3. Group Messaging

Group messages use **fan-out on write** for small groups and **fan-out on read** for large groups:

| Group Size | Strategy | How |
|------------|----------|-----|
| Small (≤100 members) | **Fan-out on write** | Chat Server writes one message to each member's inbox in Cassandra. Kafka delivers to each online member. |
| Large (>100 members) | **Fan-out on read** | Store message once per group. Members fetch from group feed when they open the chat. |

**Small group fan-out**:
```
User A sends to Group G (members: A, B, C, D)

→ Kafka: 3 messages (to B, C, D — skip A)
→ Cassandra: 1 message in group's message table
→ Deliver to online members (B, D)
→ Push notify offline members (C)
```

### 4. Presence Service

**Challenge**: 300M concurrent users, each updating their status every 30 seconds.

**Solution**: Redis with TTL-based presence.

```
SET user:{userId}:presence "online" EX 35
```

- Client sends heartbeat every 30 seconds.
- If heartbeat stops (timeout), key expires → user is offline.
- Last-seen is updated on each heartbeat: `SET user:{userId}:lastSeen {timestamp}`
- Presence changes are published via Redis Pub/Sub to subscribed friends.

**Optimization**: For users with 1000+ friends, don't push presence updates. Instead, friends pull presence when they open the chat.

### 5. Message Storage (Cassandra)

**Why Cassandra?**
- Write-heavy workload (580K writes/sec).
- Easy horizontal scaling (just add nodes).
- Tunable consistency (use `LOCAL_QUORUM` for messages).
- Time-series friendly (messages are naturally ordered by time).

**Schema**:

```sql
-- Messages table (partition by conversation_id, ordered by timestamp)
CREATE TABLE messages (
    conversation_id UUID,       -- Deterministic: hash(min(userId_a, userId_b))
    message_id TIMEUUID,        -- Time-based UUID for ordering
    sender_id UUID,
    message_type TEXT,           -- 'text', 'image', 'video', 'document'
    content TEXT,
    media_url TEXT,
    status TEXT,                 -- 'sent', 'delivered', 'read'
    created_at TIMESTAMP,
    PRIMARY KEY (conversation_id, message_id)
) WITH CLUSTERING ORDER BY (message_id DESC);

-- Group messages
CREATE TABLE group_messages (
    group_id UUID,
    message_id TIMEUUID,
    sender_id UUID,
    content TEXT,
    media_url TEXT,
    created_at TIMESTAMP,
    PRIMARY KEY (group_id, message_id)
) WITH CLUSTERING ORDER BY (message_id DESC);

-- User conversations (inbox)
CREATE TABLE user_conversations (
    user_id UUID,
    conversation_id UUID,
    last_message_preview TEXT,
    last_message_time TIMESTAMP,
    unread_count INT,
    PRIMARY KEY (user_id, last_message_time)
) WITH CLUSTERING ORDER BY (last_message_time DESC);
```

### 6. Media Handling

Media files are NOT stored in the chat system. The flow:

```
1. Client uploads file → Media Service → S3 bucket
2. S3 returns URL: "https://cdn.chat.com/media/abc123.jpg"
3. Client sends message with media_url (not the file content)
4. Recipient client downloads media from CDN using the URL
```

---

## Message Ordering

**Problem**: In a distributed system, messages can arrive out of order.

**Solution**: Use **Lamport timestamps** or **server-assigned timestamps**:

1. Each message gets a `message_id` that is a `TIMEUUID` (time-based UUID).
2. Cassandra orders messages by `message_id` within a partition.
3. Clients display messages sorted by `message_id`.

**Edge case**: Two messages sent at the exact same millisecond from different servers.
- `TIMEUUID` includes a node ID component, ensuring uniqueness.
- For display, we use `message_id` ordering (deterministic).

---

## Scaling Strategy

### WebSocket Connections

```
300M connections ÷ 50K connections/server = 6,000 WebSocket servers

Each server:
- 50K concurrent WebSocket connections
- ~500MB RAM for connections
- 8 cores, 32GB RAM
```

### Kafka

```
Partitions: 1000+ partitions per topic
Throughput: 580K msg/sec → well within Kafka's capability (millions/sec)
Consumer groups: One consumer group per Chat Server cluster
```

### Cassandra

```
Write throughput: 580K writes/sec → 20-30 Cassandra nodes (each handles 20-30K writes/sec)
Storage: 7.5TB/day → 2.7PB/year
Replication factor: 3
```

---

## End-to-End Encryption (E2EE)

For a WhatsApp-like system, messages are encrypted client-side:

```
1. User A generates message key (AES-256)
2. Encrypt message with AES-256
3. Encrypt AES key with B's public key (RSA/X25519)
4. Send encrypted message + encrypted key to server
5. Server stores encrypted blob (cannot read content)
6. User B decrypts AES key with their private key
7. User B decrypts message with AES key
```

The server **never sees plaintext messages**. This has implications:
- Server-side search is impossible (client-side search only).
- Message moderation requires client-side reporting.

---

## Interview Discussion Points

1. **How do you handle the thundering herd when a celebrity posts in a large group?**
   - Use fan-out on read for large groups. Celebrity's message is stored once.
   - Members pull from group feed, served from cache.
   - Stagger push notifications over minutes to avoid notification storm.

2. **How do you ensure message ordering across devices?**
   - Server-assigned `TIMEUUID` is the source of truth.
   - When a user opens a conversation on a new device, fetch messages ordered by `message_id`.
   - Conflict: If user edits message on phone and tablet simultaneously, last-write-wins based on server timestamp.

3. **What if a Chat Server crashes?**
   - WebSocket connections are lost. Clients auto-reconnect to another server.
   - Redis session store is updated with new `userId → serverId` mapping.
   - Kafka ensures no messages are lost (consumer will re-read from last committed offset).

4. **WebSocket vs Long Polling vs Server-Sent Events?**
   - **WebSocket**: Full-duplex, lowest latency, ideal for chat. Chosen.
   - **Long Polling**: Higher latency, more HTTP overhead. Used as fallback.
   - **SSE**: Server-to-client only, not suitable for chat (need client→server too).

