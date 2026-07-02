# Instagram-Style Photo Social Network — High-Level Design

> **You are here**: Senior SDE — System Design (HLD)
> **Roadmap**: [Developer Master Roadmap](../../../ROADMAP.md) | **Prerequisites**: [HLD_Template.md](../../00_Templates/HLD_Template/HLD_Template.md)

## Problem Statement

Design a photo-centric social network like **Instagram**:

- **Upload photos/videos** with filters and captions
- **Home feed** — posts from people you follow
- **Stories** — ephemeral 24-hour content
- **Explore** — discover popular/trending content
- **Follow graph** — follow/unfollow users
- **Likes, comments** on posts
- **Scale**: 2B+ users, 100M+ photos/day

> **Related**: Fan-out patterns overlap with [NewsFeed](../NewsFeed/NewsFeed.md) and [Twitter](../Twitter/Twitter.md). This design emphasizes **media pipeline**, **CDN**, and **Stories TTL**.

---

## Requirements

### Functional Requirements

1. **Upload media**: Photo/video upload, optional filters, caption, location, tags.
2. **Feed**: Scrollable timeline of posts from followed users, ranked by recency/relevance.
3. **Stories**: 24-hour ephemeral posts; ring UI on profile.
4. **Explore**: Recommended posts based on engagement and interests.
5. **Social actions**: Like, comment, share, follow.
6. **Notifications**: Someone liked, commented, or followed you.
7. **Profile**: Grid of user's posts, follower/following counts.

### Non-Functional Requirements

1. **Upload latency**: Acknowledge upload < 2s; processing async.
2. **Feed latency**: p99 < 500ms for first page.
3. **Availability**: 99.9% for read path.
4. **Durability**: Media stored permanently (except Stories).

---

## Capacity Estimation

```
Users: 2B total, 500M DAU
Posts: 100M photos/day + 50M videos/day
Average photo: 2 MB raw → ~500 KB after compression + multiple resolutions
Daily media storage: 100M × 500 KB ≈ 50 TB/day (before replication)

Feed reads: 500M DAU × 20 feed loads/day = 10B feed requests/day ≈ 115K QPS avg
Peak read QPS: ~500K

Stories: 200M stories/day, delete after 24h — lower long-term storage
```

---

## High-Level Architecture

```
UPLOAD PATH:
Client → API Gateway → Upload Service → S3 (raw)
                              ↓
                    Media Processing Workers
                    (resize, transcode, thumbnails)
                              ↓
                    S3 (multi-resolution) + CDN
                              ↓
                    Metadata DB (post record)

READ PATH (Feed):
Client → Feed API → Feed Cache (Redis) → precomputed timeline
                 ↘ miss → Fan-out on read OR fetch from Posts DB
```

---

## Media Processing Pipeline

```
1. Client uploads to S3 via presigned URL (multipart for video)
2. S3 event → SQS/Kafka → Worker pool
3. Worker generates:
   - Thumbnail (150px)
   - Feed size (1080px)
   - Full resolution (stored)
   - Video: H.264/H.265 renditions for adaptive playback
4. Write metadata: post_id, user_id, s3_keys[], dimensions, created_at
5. Invalidate CDN cache keys for user profile if needed
6. Fan-out post ID to followers' feed caches (or lazy on read)
```

**Async processing**: Return `post_id` with `status=processing`; client polls or WebSocket when ready.

---

## Feed Generation

### Fan-out on Write (for regular users)

```
On new post:
  For each follower (if < 10K followers):
    PUSH post_id into follower's feed cache (Redis sorted set by timestamp)
```

### Fan-out on Read (for celebrities)

```
Celebrity with 50M followers — don't fan-out on write
On feed request:
  Merge: cached feed + fetch recent posts from celebrity accounts user follows
```

**Hybrid**: Fan-out write if follower count < threshold (e.g., 10K); else fan-out read.

### Ranking (Explore & Feed)

```
Signals: recency, engagement (likes/comments velocity), relationship strength, content type
Offline: ML model trains on engagement data (Spark)
Online: score candidates in real-time ranking service
```

---

## Stories Architecture

```
Stories stored with TTL = 24 hours
Redis sorted set per user: story_ids with expiry timestamp
CDN for story media (short TTL cache)
Background job deletes S3 objects after TTL + grace period
Feed: merge active stories from followed users at top of app
```

**Difference from posts**: No permanent DB row; ephemeral index in Redis + time-limited S3 lifecycle policy.

---

## Database Schema

```sql
users (id, username, bio, avatar_url, follower_count, following_count)
follows (follower_id, followee_id, created_at)  -- graph edges
posts (id, user_id, caption, media_urls JSON, location, created_at, like_count)
comments (id, post_id, user_id, text, created_at)
likes (post_id, user_id, created_at)  -- or counter + bloom filter for "did I like"

-- Sharded by user_id for posts; by post_id for comments/likes
```

**Counters**: `like_count` updated async via Kafka consumer (avoid hot row on every like).

---

## CDN Strategy

| Content | CDN TTL | Origin |
|---------|---------|--------|
| Profile avatars | 1 day | S3 |
| Feed images | 7 days | S3 processed bucket |
| Stories | 1 hour | S3 stories bucket |
| Static app assets | Long | S3/CloudFront |

**URL signing**: Prevent hotlinking; short-lived signed URLs for private accounts.

---

## API Design

```
POST   /v1/media/upload/init       — presigned multipart upload
POST   /v1/posts                   — create post metadata after upload
GET    /v1/feed?cursor=            — home feed (cursor pagination)
GET    /v1/users/{id}/posts        — profile grid
POST   /v1/stories                 — upload story (24h TTL)
GET    /v1/stories/following       — active stories ring data
GET    /v1/explore                 — ranked discovery feed
POST   /v1/posts/{id}/like
POST   /v1/posts/{id}/comments
POST   /v1/users/{id}/follow
```

---

## Scaling Strategy

| Bottleneck | Solution |
|------------|----------|
| Media storage | S3 infinite scale; lifecycle to Glacier for old archives |
| Feed read | Redis clusters per user shard; CDN for media |
| Celebrity fan-out | Fan-out on read + cache celebrity post lists |
| Like storms | Async counter updates; rate limit per user |
| Explore ranking | Precompute candidate pools; cache top N per user segment |

---

## Spring Boot Reference Sketch

Focused Java 17 / Spring Boot 3.x sketch of post creation + hybrid feed read — not production-complete.

```java
@RestController
@RequestMapping("/v1")
public class PostController {
    private final PostService postService;
    private final FeedService feedService;

    public PostController(PostService postService, FeedService feedService) {
        this.postService = postService;
        this.feedService = feedService;
    }

    @PostMapping("/posts")
    public PostResponse createPost(@RequestBody CreatePostRequest request) {
        return postService.createPost(request);
    }

    @GetMapping("/feed")
    public FeedPage homeFeed(@RequestParam long userId, @RequestParam(required = false) String cursor) {
        return feedService.getHomeFeed(userId, cursor, 20);
    }
}

public interface FeedCacheRepository {
    List<Long> getCachedPostIds(long userId, int limit);
    void pushToFollowerFeeds(long authorId, long postId, Instant createdAt, int followerThreshold);
    List<Long> getRecentPosts(long celebrityUserId, int limit);
}

@Service
public class PostService {
    private final PostRepository postRepository;
    private final FeedCacheRepository feedCacheRepository;
    private final ApplicationEventPublisher events;

    public PostResponse createPost(CreatePostRequest request) {
        Post post = postRepository.save(new Post(request.userId(), request.caption(), request.mediaKeys()));
        feedCacheRepository.pushToFollowerFeeds(
                post.userId(), post.id(), post.createdAt(), 10_000); // fan-out on write below threshold
        events.publishEvent(new PostCreatedEvent(post.id(), post.userId())); // async media processing
        return new PostResponse(post.id(), "PROCESSING");
    }
}

@Service
public class FeedService {
    private static final int CELEBRITY_THRESHOLD = 10_000;
    private final FeedCacheRepository feedCacheRepository;
    private final FollowGraphRepository followGraphRepository;

    public FeedPage getHomeFeed(long userId, String cursor, int limit) {
        List<Long> cached = feedCacheRepository.getCachedPostIds(userId, limit);
        List<Long> celebrities = followGraphRepository.celebrityFollowees(userId, CELEBRITY_THRESHOLD);
        List<Long> celebrityPosts = celebrities.stream()
                .flatMap(id -> feedCacheRepository.getRecentPosts(id, 5).stream())
                .toList();
        List<Long> merged = mergeAndRank(cached, celebrityPosts, limit); // hybrid fan-out
        return new FeedPage(merged, nextCursor(merged));
    }

    private List<Long> mergeAndRank(List<Long> cached, List<Long> celebrityPosts, int limit) {
        return Stream.concat(cached.stream(), celebrityPosts.stream()).distinct().limit(limit).toList();
    }

    private String nextCursor(List<Long> ids) { return ids.isEmpty() ? null : ids.get(ids.size() - 1).toString(); }
}

public record PostCreatedEvent(long postId, long userId) {}
```

> **Async / caching**: Media resize/transcode runs async after `PostCreatedEvent`. Redis feed cache is eventually consistent; like counts updated via Kafka consumer.

---

## Interview Discussion Points

1. **Fan-out on write vs read?** Write: fast reads, slow for celebrities. Read: slow for users following many celebrities.
2. **Why separate media pipeline?** CPU-heavy image/video work must not block API servers.
3. **Stories vs posts storage?** TTL indexes + S3 lifecycle vs permanent metadata DB.
4. **Consistent like counts?** Eventual consistency acceptable — show approximate count.
5. **CDN invalidation?** Versioned URLs (`photo.jpg?v=timestamp`) instead of purge.

---

## Trade-offs

| Choice | Pros | Cons |
|--------|------|------|
| S3 + CDN | Cheap media at scale | Upload latency for processing |
| Redis feed cache | Sub-ms feed reads | Memory cost for active users |
| Async like counters | Handles viral posts | Stale counts briefly |
| Fan-out hybrid | Balances celebrity problem | Two code paths to maintain |
