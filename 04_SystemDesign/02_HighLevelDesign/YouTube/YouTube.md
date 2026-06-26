# YouTube — High-Level Design

## Problem Statement

Design a video platform like YouTube that supports:

- **Video upload** from users (large files, resumable)
- **Transcoding** to multiple formats/resolutions (360p, 720p, 1080p)
- **Streaming** to millions of concurrent viewers via CDN
- **Metadata** — title, description, thumbnails, comments
- **Search & recommendations** — discover content
- **Scale** — 500 hours of video uploaded per minute; 1B+ hours watched daily

**Note**: Core video pipeline overlaps [VideoStreaming.md](../VideoStreaming/VideoStreaming.md). This doc adds **upload, metadata, social, and recommendation** layers.

---

## Requirements

### Functional Requirements

1. **Upload** — Resumable upload, progress, format validation
2. **Processing** — Transcode to H.264/H.265, generate thumbnails
3. **Playback** — Adaptive bitrate streaming (ABR), global low latency
4. **Metadata** — CRUD on video info, visibility (public/private)
5. **Engagement** — Views, likes, comments, subscriptions
6. **Search** — Full-text search on title/description
7. **Feed** — Home feed mix of subscriptions + recommendations

### Non-Functional Requirements

1. **Upload** — Support files up to 128 GB; resume after disconnect
2. **Availability** — 99.9% playback availability
3. **Latency** — Start playback < 2s; live stream < 5s glass-to-glass

---

## Capacity Estimation

```
Upload: 500 hrs/min × 60 min × 1 GB/hr (avg raw) ≈ 30 TB/min peak (highly variable)
Views: 1B hours/day ≈ 11.5K hours/sec watched
Concurrent viewers peak: 100M
Storage: Exabyte-scale over years (transcoded copies multiply size)
CDN egress: Dominant cost — 100M × 2 Mbps avg ≈ 200 Tbps peak (geo-distributed)
```

---

## High-Level Architecture

```
┌──────────┐  upload   ┌─────────────┐  queue  ┌──────────────────┐
│  Client  │──────────▶│ Upload API  │────────▶│ Transcode Workers│
│          │  chunks   │ + Object    │         │ (GPU fleet)      │
└────┬─────┘           │   Storage   │         └────────┬─────────┘
     │                 └─────────────┘                  │
     │ playback                                        ▼
     │                 ┌─────────────┐         ┌──────────────────┐
     └────────────────▶│ CDN Edge    │◀────────│ Processed Video  │
                       │ (CloudFront)│         │ Store (S3/GCS)   │
                       └─────────────┘         └──────────────────┘

┌──────────────┐  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐
│ Metadata DB  │  │ Search Index │  │ Comment Svc  │  │ Recommendation│
│ (PostgreSQL) │  │ (Elasticsearch)│  │ (sharded)    │  │ (ML + cache)  │
└──────────────┘  └──────────────┘  └──────────────┘  └──────────────┘
```

---

## Upload Flow

1. Client requests upload session → API returns signed URLs for chunks
2. Chunks uploaded directly to **object storage** (bypass API servers)
3. On complete → message to **transcode queue** (Kafka/SQS)
4. Worker: transcode, thumbnail, update metadata `status=ready`

```java
// Resumable: track chunk manifest in DB
upload_sessions (id, user_id, total_size, chunks_completed[], status)
```

---

## Transcoding Pipeline

| Stage | Output |
|-------|--------|
| Ingest | Raw file in `uploads/` bucket |
| Transcode | 360p, 480p, 720p, 1080p MP4 or HLS segments |
| Thumbnail | Sprite + poster image |
| Publish | Copy to `delivery/` bucket, CDN invalidate |

**Priority queue**: Popular creators / live events get higher priority workers.

---

## Playback & CDN

- **HLS/DASH** — Client switches bitrate based on bandwidth
- **CDN** — 95%+ requests served from edge; origin only on cache miss
- **Signed URLs** — Optional TTL for private videos

Deep dive: [VideoStreaming.md](../VideoStreaming/VideoStreaming.md).

---

## Data Model (Core)

```sql
videos (id, user_id, title, description, status, duration_sec, created_at)
video_formats (video_id, resolution, s3_path, bitrate)
views (video_id, date, count)  -- aggregated counter
comments (id, video_id, user_id, text, created_at)
subscriptions (subscriber_id, channel_id)
```

**View counting**: Increment in Redis; flush to DB periodically (approximate counts OK at scale).

---

## Search & Recommendations

| System | Approach |
|--------|----------|
| **Search** | Elasticsearch on title/description/tags; ranking by relevance + popularity |
| **Recommendations** | Offline ML (collaborative filtering, embeddings) → precomputed candidates per user in cache |
| **Home feed** | Merge subscription new videos + recommendation slots |

---

## APIs

| Method | Endpoint | Purpose |
|--------|----------|---------|
| POST | `/v1/uploads` | Start resumable upload |
| PUT | `/v1/uploads/{id}/chunks/{n}` | Upload chunk |
| GET | `/v1/videos/{id}` | Metadata + playback URLs |
| GET | `/v1/videos/{id}/stream` | Redirect to CDN signed URL |
| GET | `/v1/feed/home` | Personalized feed |
| GET | `/v1/search?q=` | Search videos |

---

## Interview Discussion Points

1. **Upload vs playback scale** — Different bottlenecks (write bandwidth vs CDN egress)
2. **Transcoding cost** — GPU workers, async, priority queues
3. **View count accuracy** — At-least-once increments; approximate at billion scale
4. **Copyright / moderation** — Separate pipeline (hash matching, ML classifiers)
5. **Live vs VOD** — Live uses RTMP ingest + low-latency HLS; VOD uses batch transcode

**Related**: [VideoStreaming](../VideoStreaming/VideoStreaming.md), [Instagram](../Instagram/Instagram.md), [SearchAutocomplete](../SearchAutocomplete/SearchAutocomplete.md)
