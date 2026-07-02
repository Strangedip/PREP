# Video Streaming (YouTube / Netflix) — High-Level Design

> **You are here**: Senior SDE — System Design (HLD)
> **Roadmap**: [Developer Master Roadmap](../../../ROADMAP.md) | **Prerequisites**: [HLD_Template.md](../../00_Templates/HLD_Template/HLD_Template.md)

## Problem Statement

Design a video streaming platform that:

- **Uploads videos** from creators (any format, up to 4K)
- **Transcodes** to multiple resolutions and codecs
- **Streams to viewers** with minimal buffering worldwide
- **Supports live streaming** (optional extension)
- **Handles viral videos** — sudden traffic spikes to single content

---

## Requirements

### Functional Requirements

1. **Upload**: Creators upload raw video; system processes and publishes.
2. **Playback**: Users stream with adaptive quality based on bandwidth.
3. **Search & discovery**: Find videos by title, tags, recommendations.
4. **Analytics**: View counts, watch time (creator dashboard).

### Non-Functional Requirements

1. **Scale**: 2B users, 500M hours watched/day, 1M new uploads/day.
2. **Latency**: Start playback < 2s; rebuffer rate < 0.1%.
3. **Availability**: 99.99% for playback path.
4. **Global**: Low latency in 100+ countries via CDN.

---

## Capacity Estimation

```
Views/day: 5B video starts
Peak view QPS: 5B / 86400 × 5 ≈ 290K concurrent streams starting/sec at peak

Upload: 1M videos/day, average 5 GB raw → 5 PB/day raw input
After transcoding (5 qualities): ~15 PB/day output (before CDN caching)

CDN egress: Assume 1 Mbps average bitrate × 100M concurrent viewers = 100 Tbps global
(Most served from CDN edge — origin egress much lower)
```

---

## High-Level Architecture

```
UPLOAD PATH:
Creator → Upload API → Raw Storage (S3) → Transcoding Queue (Kafka)
                                              ↓
                                    Transcoding Workers (GPU fleet)
                                              ↓
                                    Encoded chunks → Object Storage
                                              ↓
                                    Metadata DB updated (ready to publish)

PLAYBACK PATH:
Viewer → CDN Edge (cached segments) ──miss──► Origin / Mid-tier cache
              ↓
         Video Player (HLS/DASH adaptive)
              ↓
         Analytics events → Kafka → Real-time counters
```

---

## Transcoding Pipeline

```
Raw video (MP4/MOV)
    → Split into segments (2-10 sec each)
    → Transcode to multiple renditions:
        240p, 360p, 480p, 720p, 1080p, 4K
    → Multiple codecs: H.264 (compatibility), H.265/HEVC, AV1 (efficiency)
    → Package as HLS (.m3u8 + .ts segments) or DASH
    → Upload segments to object storage
    → Register in metadata DB
```

**Worker scaling**: GPU instances (AWS g4dn) for encode; auto-scale on queue depth.

**Priority queue**: Popular creators / time-sensitive content transcoded first.

---

## Adaptive Bitrate Streaming (ABR)

Player monitors bandwidth and buffer, switches quality dynamically:

```
Bandwidth high + buffer full  → switch to 1080p
Bandwidth drops               → switch to 480p (avoid rebuffer)
```

**Protocols**:
- **HLS** (Apple): `.m3u8` playlist + `.ts` or `.m4s` segments
- **DASH** (Google): XML manifest + segments
- **LL-HLS / LL-DASH**: Low-latency live (~3s delay)

---

## CDN Strategy

| Tier | Role |
|------|------|
| **Edge CDN** | 1000+ PoPs — serve cached segments closest to user |
| **Mid-tier / Shield** | Protect origin from thundering herd on viral video |
| **Origin** | Object storage (S3) — only on CDN miss |

**Viral video handling**:
- Predict hot content → proactive CDN prefetch
- Multiple CDN providers (multi-CDN) for capacity
- Origin shield collapses requests to single region

---

## Metadata & Search

```sql
videos (id, creator_id, title, description, status, duration, created_at)
video_renditions (video_id, resolution, codec, manifest_url, bitrate)
video_stats (video_id, view_count, watch_time_seconds)  -- updated async
```

**Search**: Elasticsearch with title, tags, transcript (from speech-to-text pipeline).

---

## API Design

```
POST   /v1/videos/upload/init     → presigned URL for raw upload
POST   /v1/videos/upload/complete   → trigger transcoding job
GET    /v1/videos/{id}/playback     → { manifest_url, cdn_token, expires }
GET    /v1/videos/{id}              → metadata
POST   /v1/videos/{id}/view          → record view event (async)
```

**Playback URL**: Short-lived signed CDN URL — prevents hotlinking.

---

## Live Streaming Extension

```
Encoder (OBS) → RTMP ingest → Live transcoder → HLS segments to CDN
                                    ↓
                              Real-time manifest updates
```

**Challenge**: Sub-second latency requires WebRTC or LL-HLS; trade-off vs cost.

---

## Security

- **Signed URLs**: CDN tokens expire in minutes
- **Geo-blocking**: Rights management per region
- **DRM**: Widevine/FairPlay for premium content
- **Content moderation**: ML pipeline on upload (async) before public publish

---

## Spring Boot Reference Sketch

Focused Java 17 / Spring Boot 3.x sketch of upload-complete → playback URL — not production-complete.

```java
@RestController
@RequestMapping("/v1/videos")
public class VideoPlaybackController {
    private final VideoPlaybackService videoPlaybackService;
    private final TranscodeService transcodeService;

    public VideoPlaybackController(VideoPlaybackService videoPlaybackService,
                                   TranscodeService transcodeService) {
        this.videoPlaybackService = videoPlaybackService;
        this.transcodeService = transcodeService;
    }

    @PostMapping("/upload/complete")
    public ResponseEntity<Void> completeUpload(@RequestBody UploadCompleteRequest request) {
        transcodeService.enqueueJob(request.videoId(), request.rawObjectKey());
        return ResponseEntity.accepted().build(); // async GPU workers consume Kafka queue
    }

    @GetMapping("/{videoId}/playback")
    public PlaybackResponse playback(@PathVariable String videoId, @RequestParam String clientIp) {
        return videoPlaybackService.issuePlaybackToken(videoId, clientIp);
    }
}

public interface VideoRenditionRepository {
    Optional<VideoRendition> findBestManifest(String videoId, String preferredCodec);
}

@Service
public class VideoPlaybackService {
    private static final Duration CDN_TOKEN_TTL = Duration.ofMinutes(10);
    private final VideoRenditionRepository renditionRepository;
    private final CdnTokenSigner cdnTokenSigner;

    public PlaybackResponse issuePlaybackToken(String videoId, String clientIp) {
        VideoRendition rendition = renditionRepository
                .findBestManifest(videoId, "h264")
                .orElseThrow(() -> new NotFoundException(videoId));

        String signedUrl = cdnTokenSigner.sign(rendition.manifestUrl(), CDN_TOKEN_TTL, clientIp);
        return new PlaybackResponse(signedUrl, rendition.resolutions(), CDN_TOKEN_TTL.toSeconds());
    }
}

@Service
public class TranscodeService {
    private final ApplicationEventPublisher events;

    public void enqueueJob(String videoId, String rawObjectKey) {
        events.publishEvent(new TranscodeRequestedEvent(videoId, rawObjectKey, Instant.now()));
        // Kafka consumer: split segments, encode renditions, register in VideoRenditionRepository
    }
}

public record TranscodeRequestedEvent(String videoId, String rawObjectKey, Instant requestedAt) {}
public record PlaybackResponse(String manifestUrl, List<String> resolutions, long expiresInSec) {}
```

> **Async / caching**: Transcoding is fully async — API returns `202 Accepted`. CDN edge caches HLS segments; signed playback URLs are short-lived and non-idempotent by design.

---

## Interview Discussion Points

1. **Why transcoding?** Devices and bandwidth vary — single raw file won't play everywhere efficiently.
2. **CDN vs origin?** 95%+ requests hit CDN; origin only on miss or new content.
3. **Viral video problem?** Thundering herd on origin — shield tier, prefetch, multi-CDN.
4. **HLS vs DASH?** HLS broader device support; DASH more flexible manifest.
5. **View count accuracy?** Async aggregation — exact count not needed in real-time.
6. **Storage cost?** Lifecycle policies — move old rarely-watched to Glacier.

---

## Trade-offs

| Choice | Pros | Cons |
|--------|------|------|
| Segment-based streaming | CDN-friendly, ABR | Many small files to manage |
| GPU transcoding | Fast encode | Expensive compute |
| Async view counts | Scales writes | Not real-time exact |
| Multi-CDN | Capacity + resilience | Operational complexity |
