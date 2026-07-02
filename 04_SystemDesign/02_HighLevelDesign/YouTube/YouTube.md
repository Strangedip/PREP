# YouTube — High-Level Design

> **You are here**: Senior SDE — System Design (HLD)
> **Roadmap**: [Developer Master Roadmap](../../../ROADMAP.md) | **Prerequisites**: [HLD_Template.md](../../00_Templates/HLD_Template/HLD_Template.md)

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

## Spring Boot Reference Sketch

Focused Java 17 / Spring Boot 3.x sketch of resumable upload + async transcode + playback — not production-complete.

```java
@RestController
@RequestMapping("/v1")
public class YouTubeController {
    private final UploadService uploadService;
    private final VideoService videoService;

    public YouTubeController(UploadService uploadService, VideoService videoService) {
        this.uploadService = uploadService;
        this.videoService = videoService;
    }

    @PostMapping("/uploads")
    public UploadSessionResponse startUpload(@RequestBody StartUploadRequest request) {
        return uploadService.createSession(request.userId(), request.title(), request.totalBytes());
    }

    @PostMapping("/uploads/{sessionId}/complete")
    public VideoResponse completeUpload(@PathVariable String sessionId) {
        return uploadService.completeAndEnqueueTranscode(sessionId);
    }

    @GetMapping("/videos/{videoId}/stream")
    public StreamResponse stream(@PathVariable String videoId) {
        return videoService.issueCdnPlaybackUrl(videoId);
    }
}

public interface VideoRepository {
    Video save(Video video);
    Optional<Video> findById(String videoId);
    void updateStatus(String videoId, VideoStatus status, String manifestUrl);
}

@Service
public class UploadService {
    private final UploadSessionRepository sessions;
    private final VideoRepository videoRepository;
    private final ApplicationEventPublisher events;

    public UploadSessionResponse createSession(long userId, String title, long totalBytes) {
        String sessionId = UUID.randomUUID().toString();
        sessions.create(sessionId, userId, title, totalBytes);
        return new UploadSessionResponse(sessionId, sessions.presignedChunkUrls(sessionId));
    }

    public VideoResponse completeAndEnqueueTranscode(String sessionId) {
        UploadSession session = sessions.markComplete(sessionId);
        Video video = videoRepository.save(new Video(session.userId(), session.title(), VideoStatus.PROCESSING));
        events.publishEvent(new TranscodeJobEvent(video.id(), session.rawObjectKey()));
        return new VideoResponse(video.id(), video.status());
    }
}

@Service
public class VideoService {
    private final VideoRepository videoRepository;
    private final ViewCounter viewCounter; // Redis INCR, flushed async to DB

    public StreamResponse issueCdnPlaybackUrl(String videoId) {
        Video video = videoRepository.findById(videoId)
                .filter(v -> v.status() == VideoStatus.READY)
                .orElseThrow(() -> new NotFoundException(videoId));
        viewCounter.incrementAsync(videoId); // approximate, at-least-once
        return new StreamResponse(signCdnUrl(video.manifestUrl()), Duration.ofMinutes(15));
    }

    private String signCdnUrl(String manifestUrl) { return manifestUrl + "?token=..."; }
}

public record TranscodeJobEvent(String videoId, String rawObjectKey) {}
public enum VideoStatus { PROCESSING, READY, FAILED }
```

> **Async / idempotency**: Chunk uploads to object storage are idempotent by part number. Transcode workers consume `TranscodeJobEvent` asynchronously; view counts are eventually consistent via Redis.

---

## Interview Discussion Points

1. **Upload vs playback scale** — Different bottlenecks (write bandwidth vs CDN egress)
2. **Transcoding cost** — GPU workers, async, priority queues
3. **View count accuracy** — At-least-once increments; approximate at billion scale
4. **Copyright / moderation** — Separate pipeline (hash matching, ML classifiers)
5. **Live vs VOD** — Live uses RTMP ingest + low-latency HLS; VOD uses batch transcode

**Related**: [VideoStreaming](../VideoStreaming/VideoStreaming.md), [Instagram](../Instagram/Instagram.md), [SearchAutocomplete](../SearchAutocomplete/SearchAutocomplete.md)
