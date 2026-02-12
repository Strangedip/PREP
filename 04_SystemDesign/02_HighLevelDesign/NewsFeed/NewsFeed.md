# News Feed System — High-Level Design

## Problem Statement

Design a social media news feed system (like Facebook, Twitter/X, or LinkedIn) that can:

- **Generate personalized feeds** for each user from posts by people/pages they follow
- **Handle 500M DAU** with each user refreshing feed 10-20 times/day
- **Display posts in near real-time** (new post appears in followers' feeds within seconds)
- **Support mixed content**: text, images, videos, links, polls
- **Rank posts** by relevance (not just chronological)
- **Support pagination** (infinite scroll)

---

## Requirements

### Functional Requirements

1. **Post Creation**: User creates a post (text, image, video, link).
2. **Feed Generation**: User opens app → sees a personalized feed of posts from followed users/pages.
3. **Feed Refresh**: Pull-to-refresh or auto-refresh with new posts.
4. **Interactions**: Like, comment, share, save — these affect ranking.
5. **Follow/Unfollow**: Changes affect which posts appear in feed.

### Non-Functional Requirements

1. **Scale**: 500M DAU, 200M posts/day, 10B feed reads/day.
2. **Latency**: Feed load < 200ms.
3. **Freshness**: New post visible to followers within 5 seconds.
4. **Availability**: 99.99%.

---

## Capacity Estimation

```
Posts: 200M/day = 2,315 posts/sec (average), 10K/sec (peak)
Feed reads: 10B/day = 115,740 reads/sec, 500K/sec (peak)

Ratio: 50:1 read-to-write → heavily read-optimized system

Storage per post: 1KB (metadata) + 500KB avg (media via CDN URL) = ~1KB stored
Post storage: 200M × 1KB = 200GB/day
Feed cache: 500M users × 500 post IDs × 8 bytes = 2TB in-memory
```

---

## The Core Problem: Fan-Out

The central design decision is how to deliver a new post to all followers' feeds.

### Approach 1: Fan-Out on Write (Push Model)

When User A creates a post, **immediately** write it to every follower's feed cache.

```
User A (1000 followers) creates a post
  → Write post to User A's post table
  → Fan-out: Write post_id to 1000 followers' feed caches
  → Each follower's feed is pre-computed and ready to read
```

**Pros**:
- Feed reads are **extremely fast** (just read from pre-computed cache).
- Feed is always up-to-date.

**Cons**:
- **Celebrity problem**: A user with 100M followers → 100M writes per post.
- Write amplification: 200M posts/day × 1000 avg followers = 200B writes/day.
- Wasted writes: Many followers may never check their feed.

### Approach 2: Fan-Out on Read (Pull Model)

When a user opens their feed, **query** the posts of everyone they follow, merge, rank, and return.

```
User opens feed
  → Get list of followed users (e.g., 500 users)
  → For each followed user, get their latest N posts
  → Merge all posts, rank, return top 50
```

**Pros**:
- No wasted writes. Only compute feed when someone reads it.
- Works great for celebrity users.

**Cons**:
- Feed reads are **slow** (fan-out query at read time).
- For a user following 500 accounts, this requires 500 queries.

### Approach 3: Hybrid (Facebook/Twitter Approach) — **Recommended**

```
Regular users (< 10K followers): Fan-out on Write
  → Pre-compute followers' feeds immediately

Celebrity users (> 10K followers): Fan-out on Read
  → Don't fan-out. Instead, merge celebrity posts at read time.

Feed Generation = Pre-computed feed + Live merge of celebrity posts
```

**Feed read flow**:
```
1. Read pre-computed feed from cache (fan-out on write results)
2. Get list of followed celebrities
3. Fetch latest posts from each celebrity's post table
4. Merge celebrity posts with pre-computed feed
5. Rank the combined feed
6. Return top N posts with pagination cursor
```

---

## Architecture

```
                    ┌──────────────────┐
                    │  Post Service    │  ← Create, edit, delete posts
                    └────────┬─────────┘
                             │ New post event
                    ┌────────▼─────────┐
                    │     Kafka        │
                    │  topic: posts    │
                    └────────┬─────────┘
                             │
              ┌──────────────┼──────────────┐
              │                             │
     ┌────────▼────────┐          ┌────────▼─────────┐
     │  Fan-Out Worker  │          │  Post Store      │
     │                  │          │  (Cassandra)     │
     │  For each post:  │          │                  │
     │  If author <10K  │          │  Partition key:  │
     │  followers:      │          │  user_id         │
     │  Push to cache   │          └──────────────────┘
     │                  │
     │  If author ≥10K: │
     │  Skip (on-read)  │
     └────────┬─────────┘
              │
     ┌────────▼──────────┐
     │  Feed Cache       │
     │  (Redis)          │
     │                   │
     │  Key: feed:{uid}  │
     │  Value: SortedSet │
     │    of post_ids    │
     │    scored by time │
     │  Max: 500 entries │
     └───────────────────┘

                    ┌──────────────────┐
                    │  Feed Service    │  ← Read feed (API)
                    ├──────────────────┤
                    │ 1. Read cache    │
                    │ 2. Merge celeb   │
                    │ 3. Rank          │
                    │ 4. Return N+cursor│
                    └──────────────────┘
```

---

## Feed Ranking

A simple chronological feed quickly becomes irrelevant. Modern feeds use a **ranking model**:

```
Score = f(relevance, recency, engagement, user_affinity)

Relevance:
- Content type preference (does user engage more with videos or text?)
- Topic matching (user interested in "tech" → boost tech posts)

Recency:
- Exponential decay: score *= e^(-λ × hours_old)
- Recent posts rank higher, but not strictly chronological

Engagement:
- Number of likes, comments, shares (weighted)
- High engagement signals quality content

User Affinity:
- How often does this user interact with the author?
- Recent interactions (likes, comments, DMs) boost affinity score
```

**Simple ranking formula**:
```
post_score = (engagement_score × 0.3) +
             (recency_score × 0.4) +
             (affinity_score × 0.2) +
             (relevance_score × 0.1)
```

In production (Facebook, TikTok), this is a trained ML model that predicts the probability of user engagement.

---

## Pagination (Cursor-Based)

Feed uses **cursor-based pagination**, not offset-based:

```java
// API: GET /api/v1/feed?cursor=eyJ0aW1lIjoiMjAyNi0wMS0xNVQxMDowMDowMCIsInNjb3JlIjowLjg1fQ==&limit=20

@GetMapping("/feed")
public FeedResponse getFeed(
        @RequestParam(required = false) String cursor,
        @RequestParam(defaultValue = "20") int limit) {

    // Decode cursor (base64 encoded JSON: {time, score, postId})
    FeedCursor decodedCursor = decodeCursor(cursor);

    // Fetch posts with score < cursor.score (descending)
    List<FeedItem> posts = feedService.getFeed(userId, decodedCursor, limit + 1);

    // Build next cursor from last item
    boolean hasMore = posts.size() > limit;
    if (hasMore) posts = posts.subList(0, limit);

    String nextCursor = hasMore ? encodeCursor(posts.get(posts.size() - 1)) : null;

    return new FeedResponse(posts, nextCursor, hasMore);
}
```

**Why cursor-based?**
- Offset-based: `OFFSET 10000` → scans and discards 10000 rows. Slow for deep pagination.
- Cursor-based: `WHERE score < 0.85 AND created_at < '2026-01-15'` → index seek. Fast at any depth.
- Consistent: New posts added between pages don't cause duplicates or missing items.

---

## Data Storage

### Post Store (Cassandra)

```sql
-- Posts by user (for fan-out on read for celebrities)
CREATE TABLE user_posts (
    user_id UUID,
    post_id TIMEUUID,
    content TEXT,
    media_urls LIST<TEXT>,
    post_type TEXT,
    like_count COUNTER,     -- Stored separately in counter table
    comment_count COUNTER,
    created_at TIMESTAMP,
    PRIMARY KEY (user_id, post_id)
) WITH CLUSTERING ORDER BY (post_id DESC);
```

### Feed Cache (Redis)

```
Key: feed:{userId}
Type: Sorted Set
Score: post ranking score (combination of time + relevance)
Member: post_id

Operations:
  ZADD feed:user123 0.95 "post-abc"  → Add post to feed
  ZREVRANGE feed:user123 0 19        → Get top 20 posts
  ZREMRANGEBYRANK feed:user123 0 -501 → Trim to 500 max
```

### Social Graph (who follows whom)

```sql
-- Cassandra table for follower lookups
CREATE TABLE followers (
    user_id UUID,         -- The user being followed
    follower_id UUID,     -- The user who follows
    followed_at TIMESTAMP,
    PRIMARY KEY (user_id, follower_id)
);

CREATE TABLE following (
    user_id UUID,         -- The user who follows
    following_id UUID,    -- The user being followed
    followed_at TIMESTAMP,
    PRIMARY KEY (user_id, following_id)
);
```

---

## Scaling Strategy

| Component | Scale | Strategy |
|-----------|-------|----------|
| Post Service | 10K writes/sec | 5-10 stateless servers |
| Fan-Out Workers | 200B cache writes/day | 50-100 workers, Kafka partitions |
| Feed Cache (Redis) | 500K reads/sec, 2TB | Redis Cluster (50+ nodes) |
| Feed Service | 500K reads/sec | 20-50 stateless servers + Redis |
| Post Store (Cassandra) | 10K writes/sec, 200GB/day | 10-20 node cluster |
| CDN | Media serving | CloudFront/CloudFlare |

---

## Interview Discussion Points

1. **How do you handle the celebrity problem?**
   - Hybrid fan-out: Pre-compute for regular users, on-read merge for celebrities.
   - Threshold: Users with >10K followers are "celebrities." This threshold is configurable.
   - At read time, merge at most 100-200 celebrity post queries (cached in Redis).

2. **How do you ensure feed freshness?**
   - Fan-out workers process new posts within seconds.
   - For celebrity posts (on-read), the feed service always fetches the latest from cache.
   - WebSocket/SSE can push "new posts available" indicator to online users.

3. **How do you prevent a stale feed?**
   - Redis feed cache has a TTL of 24 hours. After TTL, rebuild from source.
   - On unfollow, remove that user's posts from the feed cache.
   - On post deletion, publish delete event → fan-out workers remove from all caches.

4. **What about content moderation?**
   - Posts go through a moderation pipeline (ML classifier + human review) before being eligible for fan-out.
   - Flagged posts are removed from all caches via a "post removed" event.

