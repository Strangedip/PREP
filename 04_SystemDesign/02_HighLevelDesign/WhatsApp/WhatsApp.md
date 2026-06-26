# WhatsApp-Style Messaging — High-Level Design

## Problem Statement

Design a messaging platform like **WhatsApp** with capabilities beyond a generic chat system:

- **End-to-end encryption (E2EE)** for 1:1 and group messages
- **Group messaging** (up to 256+ members)
- **Media messages** (images, video, voice notes) without storing plaintext on servers
- **Online/offline delivery** with delivery and read receipts
- **Multi-device sync** (phone, desktop, web)
- **Backup & restore** (encrypted cloud backup)
- **500M+ DAU**, billions of messages per day

> **Difference from [ChatSystem](../ChatSystem/ChatSystem.md)**: ChatSystem covers WebSocket fan-out, presence, and message queues. This design adds **E2EE (Signal protocol)**, **multi-device key management**, **encrypted media**, and **backup architecture**.

---

## Requirements

### Functional Requirements

1. **E2EE 1:1 chat**: Only sender and recipient can read message content.
2. **E2EE group chat**: Group key distribution when members join/leave.
3. **Media**: Encrypt media client-side; server stores blobs; CDN delivers encrypted bytes.
4. **Delivery states**: Sent → Delivered → Read (metadata may be visible to server; content encrypted).
5. **Multi-device**: Same account on phone + desktop; messages synced encrypted.
6. **Offline delivery**: Store encrypted envelopes until recipient is online.
7. **Push notifications**: Alert offline users (no message content in push payload).
8. **Encrypted backup**: Optional cloud backup with user-held recovery key.

### Non-Functional Requirements

1. **Latency**: < 200ms delivery for online users (excluding client crypto).
2. **Scale**: 50B+ messages/day, 500M DAU.
3. **Privacy**: Server cannot decrypt message bodies (E2EE guarantee).
4. **Availability**: 99.99% for message delivery path.

---

## Capacity Estimation

```
Messages: 50B/day ≈ 580K msg/sec (avg), ~1.7M/sec peak
Encrypted payload: ~200 bytes avg (text) + metadata envelope
Storage: 50B × 200B × 365 × 5 years ≈ ~18 PB (encrypted blobs)

Media: 20% of messages include media reference
Media storage: S3 + CDN — multi-PB, egress-heavy

Concurrent connections: 300M peak WebSocket sessions
```

---

## High-Level Architecture

```
┌─────────────┐     E2EE encrypt      ┌──────────────────┐
│   Client    │ ──────────────────────►│  API / WebSocket │
│ (Signal lib)│                        │  Gateway         │
└─────────────┘                        └────────┬─────────┘
                                                  │
                    ┌─────────────────────────────┼─────────────────────────────┐
                    │                             │                             │
           ┌────────▼────────┐         ┌─────────▼─────────┐        ┌─────────▼─────────┐
           │ Message Router   │         │  Key Directory     │        │  Media Service     │
           │ (encrypted       │         │  (public keys only)│        │  (encrypted blobs) │
           │  envelopes only) │         │  PreKey bundles    │        │  S3 + CDN          │
           └────────┬────────┘         └────────────────────┘        └────────────────────┘
                    │
           ┌────────▼────────┐         ┌─────────────────────┐
           │ Message Queue    │         │  Push Service        │
           │ (Kafka)          │         │  (APNs / FCM)        │
           └────────┬────────┘         └─────────────────────┘
                    │
           ┌────────▼────────┐
           │ Encrypted Store  │
           │ (Cassandra)      │
           │ envelope + meta  │
           └──────────────────┘
```

**Critical principle**: Servers route **encrypted envelopes** — never plaintext content.

---

## End-to-End Encryption (Signal Protocol Overview)

### Key Concepts

| Concept | Purpose |
|---------|---------|
| **Identity Key** | Long-term public/private key pair per device |
| **PreKeys** | One-time keys for async first message |
| **Double Ratchet** | Forward secrecy — compromise of today’s keys doesn’t reveal past messages |
| **Session** | Established after initial key exchange |

### Message Flow (1:1)

```
1. Alice fetches Bob's prekey bundle from Key Directory (public keys only)
2. Alice encrypts message on device using Signal protocol
3. Alice sends encrypted envelope to server
4. Server stores/forwards envelope — cannot decrypt
5. Bob's device decrypts with local private keys
```

### Group Messaging

- **Sender Keys** or **MLS (Messaging Layer Security)** for group E2EE
- When member leaves: rotate group key; re-encrypt for remaining members
- Server distributes key update messages (still encrypted per member)

**Interview**: You don't implement Signal from scratch — use **libsignal** / established libraries. Focus on **key directory**, **envelope routing**, and **member change** flows.

---

## Multi-Device Sync

```
Primary phone establishes sessions with each contact
Desktop links via QR scan — shares account identity via secure channel
Each device has own device ID + keys registered in Key Directory
Sender encrypts for ALL recipient devices (fan-out on client or server-assisted envelope list)
```

**Server role**: Store device registry and route envelopes to all active device IDs for a user.

---

## Media Messages

```
1. Client encrypts media file with random AES key
2. Upload encrypted blob to Media Service (presigned S3 URL)
3. Send message envelope containing: encrypted media URL + encrypted AES key (in E2EE payload)
4. Recipient downloads blob, decrypts locally
```

Server never has decryption key for media.

---

## Encrypted Backup

```
User sets backup password → derives key on device
Backup: encrypted message history blob → S3
Recovery: user password + backup file on new device
Optional: hardware security / recovery PIN — trade-off usability vs security
```

**Never store user backup password on server.**

---

## Data Model (Server-Side — Metadata Only)

```sql
users (id, phone_hash, created_at)
devices (id, user_id, device_id, identity_key_public, last_seen)
messages (
  id, sender_device_id, recipient_user_id,
  encrypted_payload BLOB,  -- opaque to server
  timestamp, delivery_status
)
groups (id, created_by, member_count)
group_members (group_id, user_id, joined_at)
```

**Partitioning**: Messages by `recipient_user_id` or `conversation_id` in Cassandra.

---

## API Design

```
POST   /v1/keys/register          — register device public keys / prekeys
GET    /v1/keys/{userId}/{deviceId} — fetch prekey bundle
POST   /v1/messages               — send encrypted envelope
GET    /v1/messages/pending       — poll/WebSocket pull pending envelopes
POST   /v1/media/upload           — presigned URL for encrypted blob
POST   /v1/groups                 — create group (metadata)
POST   /v1/groups/{id}/members    — add/remove (triggers client key rotation)
POST   /v1/backup/upload          — encrypted backup blob
```

---

## Scaling Strategy

| Component | Strategy |
|-----------|----------|
| WebSocket connections | Shard by user_id; sticky sessions; millions per region |
| Message store | Cassandra — partition by conversation, time-ordered |
| Key directory | Redis cache for hot prekey bundles |
| Media | S3 + CloudFront; separate upload path from message API |
| Push | FCM/APNs with generic "New message" — no content |

---

## Security Considerations

- **Metadata leakage**: Server sees who talks to whom and when — mention metadata minimization in interview
- **Key verification**: Safety numbers / QR verify out-of-band
- **Blocked users**: Stop routing envelopes server-side
- **Report abuse**: Limited metadata for moderation (E2EE limits server-side content moderation)
- **SSRF**: Validate media upload URLs; block internal fetches (OWASP A01:2025)

---

## Interview Discussion Points

1. **Why E2EE changes architecture?** Server is dumb router; key management is client-heavy.
2. **Group key rotation on member leave?** Prevent ex-member decrypting future messages.
3. **Multi-device fan-out?** Encrypt per device or use sync protocol between user's own devices.
4. **Backup vs E2EE tension?** Backup key must be user-held or E2EE is weakened.
5. **Difference from Slack/Discord?** Those are often server-readable; WhatsApp model is privacy-first.

---

## Trade-offs

| Choice | Pros | Cons |
|--------|------|------|
| Full E2EE | Maximum privacy | No server-side search/moderation on content |
| Signal protocol | Industry-proven, forward secrecy | Complex client implementation |
| Encrypted media on S3 | Scales cheaply | CDN caches encrypted blobs only |
| Cassandra for envelopes | Write scale | Ops complexity |
