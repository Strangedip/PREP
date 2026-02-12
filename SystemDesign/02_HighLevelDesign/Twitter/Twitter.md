# Design Twitter / Social Media Feed

## Problem Statement

Design a social media platform like Twitter where users can:
- Post tweets (280 characters)
- Follow/unfollow other users
- View a personalized home timeline (news feed)
- Search tweets
- Like and retweet

---

## Step 1: Requirements

### Functional Requirements
1. **Post a tweet** — Text, images, videos (up to 280 characters text)
2. **Follow/unfollow users** — Directed graph (A follows B does not mean B follows A)
3. **Home timeline** — Aggregated feed of tweets from people you follow, reverse chronological + ranked
4. **User timeline** — All tweets from a specific user
5. **Search tweets** — Full-text search by keywords, hashtags, mentions
6. **Like/Retweet** — Engagement actions

### Non-Functional Requirements
- **Scale**: 500M users, 200M DAU, 500M tweets/day
- **Read-heavy**: 100:1 read-to-write ratio
- **Latency**: Home timeline loads in < 300ms
- **Availability**: 99.99% uptime (AP system — prioritize availability over strong consistency)
- **Eventual consistency**: A follower seeing a tweet a few seconds late is acceptable

### Capacity Estimation

```
DAU: 200M users
Tweets/day: 500M → ~5,800 tweets/sec, peak ~12K tweets/sec
Timeline reads: 200M users × 5 reads/day = 1B reads/day → ~12K reads/sec, peak ~35K reads/sec

Storage per tweet: ~1 KB (text + metadata)
Tweet storage/day: 500M × 1 KB = 500 GB/day → ~180 TB/year
Media storage: 10% of tweets have media → 50M × 500 KB avg = 25 TB/day

Follow relationships: 500M users × 200 avg followings = 100B edges
```

---

## Step 2: High-Level Architecture

```
┌─────────────┐     ┌──────────────────┐     ┌──────────────────┐
│   Clients   │────▶│  API Gateway /   │────▶│  Load Balancer   │
│ (Web/Mobile)│     │  CDN             │     │                  │
└─────────────┘     └──────────────────┘     └────────┬─────────┘
                                                       │
                    ┌──────────────────────────────────┼──────────────┐
                    │                                  │              │
              ┌─────▼─────┐  ┌──────────────┐  ┌──────▼──────┐      │
              │  Tweet     │  │  Timeline    │  │   Search    │      │
              │  Service   │  │  Service     │  │   Service   │      │
              └─────┬──────┘  └──────┬───────┘  └──────┬──────┘      │
                    │                │                  │             │
              ┌─────▼──────┐  ┌──────▼───────┐  ┌──────▼──────┐     │
              │  Tweet DB  │  │ Timeline     │  │Elasticsearch│     │
              │(Sharded    │  │ Cache        │  │  Cluster    │     │
              │ MySQL/     │  │ (Redis)      │  │             │     │
              │ Cassandra) │  │              │  └─────────────┘     │
              └────────────┘  └──────────────┘                      │
                                                                    │
              ┌──────────────┐  ┌──────────────┐  ┌────────────────┐│
              │  User/Graph  │  │  Fan-out     │  │  Media         ││
              │  Service     │  │  Service     │  │  Service       ││
              └──────┬───────┘  └──────┬───────┘  └────────┬───────┘│
                     │                 │                    │        │
              ┌──────▼───────┐  ┌──────▼───────┐  ┌────────▼──────┐ │
              │  Graph DB/   │  │  Kafka       │  │  Object Store │ │
              │  Social Graph│  │  Message     │  │  (S3/CDN)     │ │
              │  (Redis/Neo4j│  │  Queue       │  │               │ │
              └──────────────┘  └──────────────┘  └───────────────┘ │
                                                                    │
              ┌──────────────┐  ┌──────────────┐                    │
              │  Analytics   │  │ Notification │◀───────────────────┘
              │  Service     │  │  Service     │
              └──────────────┘  └──────────────┘
```

---

## Step 3: Detailed Component Design

### 3.1 Tweet Service

**Responsibilities**: Create, read, delete tweets. Store tweet metadata.

**Database Schema (Tweets Table)**:
```sql
CREATE TABLE tweets (
    tweet_id     BIGINT PRIMARY KEY,   -- Snowflake ID (timestamp + machine + sequence)
    user_id      BIGINT NOT NULL,
    content      VARCHAR(280) NOT NULL,
    media_urls   TEXT[],               -- Array of media URLs
    created_at   TIMESTAMP NOT NULL,
    like_count   INT DEFAULT 0,
    retweet_count INT DEFAULT 0,
    reply_count  INT DEFAULT 0,
    is_retweet   BOOLEAN DEFAULT FALSE,
    original_tweet_id BIGINT,          -- If retweet, points to original
    INDEX idx_user_time (user_id, created_at DESC)
);
```

**Sharding**: Shard by `user_id` so all tweets from one user are co-located. This makes the user timeline query efficient (single shard lookup).

**Tweet ID Generation**: Use Twitter's Snowflake ID — 64-bit ID composed of:
- 41 bits: timestamp (milliseconds since epoch) — sortable by time
- 10 bits: machine/datacenter ID
- 12 bits: sequence number (4096 per machine per ms)

This gives us time-sortable, globally unique IDs without a centralized ID generator.

### 3.2 Social Graph Service

**Responsibilities**: Manage follow/unfollow relationships. Answer "who does user X follow?" and "who follows user X?"

**Storage Options**:

| Option | Pros | Cons |
|--------|------|------|
| MySQL | ACID, familiar | Follow counts can be huge; cross-shard joins |
| Redis Sorted Set | O(1) lookup, fast | Memory-intensive for 100B edges |
| Neo4j / Graph DB | Natural for graph queries | Scaling challenges |

**Recommended**: Redis for hot data (recent followers, following lists) + MySQL/Cassandra for persistent storage.

```
Redis structure:
  following:{user_id} → Sorted Set of user IDs (sorted by follow time)
  followers:{user_id} → Sorted Set of user IDs

MySQL:
  CREATE TABLE follows (
      follower_id  BIGINT,
      followee_id  BIGINT,
      created_at   TIMESTAMP,
      PRIMARY KEY (follower_id, followee_id),
      INDEX idx_followee (followee_id, follower_id)
  );
```

### 3.3 Timeline Service — The Core Challenge

This is the **heart of the system**. There are two approaches:

#### Fan-Out on Write (Push Model)

When a user posts a tweet, immediately push it to all followers' timelines.

```
User A posts a tweet
  → Fan-out Service reads A's follower list (1000 followers)
  → For each follower, prepend the tweet to their cached timeline
  → Timeline is stored in Redis as a sorted set (score = tweet timestamp)
```

**Pros**:
- Home timeline reads are very fast — just read from Redis
- Simple read path

**Cons**:
- Celebrity problem: A user with 50M followers causes 50M write operations per tweet
- Write amplification for popular users

#### Fan-Out on Read (Pull Model)

When a user requests their timeline, fetch tweets from all people they follow in real-time.

```
User B requests home timeline
  → Get B's following list (200 users)
  → Fetch recent tweets from each of the 200 users
  → Merge and rank
  → Return top N results
```

**Pros**:
- No write amplification
- Posting is fast

**Cons**:
- Read-time is slow (must query 200+ user timelines and merge)
- Latency increases with number of followings

#### Hybrid Approach (What Twitter Actually Uses)

- **Regular users** (< 10K followers): Fan-out on write. Their tweets are pushed to followers' timelines.
- **Celebrity users** (> 10K followers): Fan-out on read. Their tweets are NOT pushed; instead, they are merged at read time.
- When User B reads their timeline: fetch cached timeline (from fan-out on write) + merge celebrity tweets (from fan-out on read).

```java
// Pseudocode for hybrid timeline generation
public List<Tweet> getHomeTimeline(long userId) {
    // Step 1: Get pre-computed timeline from Redis (fan-out on write results)
    List<Tweet> cachedTimeline = redis.getTimeline(userId, limit = 200);

    // Step 2: Get list of celebrities this user follows
    List<Long> celebrities = getCelebrityFollowings(userId);

    // Step 3: Fetch recent tweets from celebrities (fan-out on read)
    List<Tweet> celebrityTweets = new ArrayList<>();
    for (Long celeb : celebrities) {
        celebrityTweets.addAll(tweetService.getRecentTweets(celeb, limit = 20));
    }

    // Step 4: Merge and rank
    List<Tweet> merged = merge(cachedTimeline, celebrityTweets);
    return rank(merged).subList(0, Math.min(50, merged.size()));
}
```

### 3.4 Fan-Out Service

Handles the asynchronous push of tweets to followers' timelines.

```
Tweet Created → Kafka Topic "tweet-created"
  → Fan-Out Workers consume the message
  → For each follower of the tweet author:
      → LPUSH tweet_id to Redis list "timeline:{follower_id}"
      → Trim list to 800 entries (no need to keep ancient tweets)
```

**Why Kafka?**: The fan-out is asynchronous. Kafka provides:
- Durability (tweets are not lost if workers crash)
- Scalability (partition by user_id for parallel processing)
- Backpressure handling (slow consumers do not block producers)

### 3.5 Search Service

**Architecture**: Use Elasticsearch for full-text search.

**Index Structure**:
```json
{
  "tweet_id": 12345,
  "user_id": 67890,
  "content": "Spring Boot 3.x with virtual threads is amazing #java",
  "hashtags": ["java"],
  "mentions": [],
  "created_at": "2026-02-13T10:30:00Z",
  "like_count": 42,
  "language": "en"
}
```

**Search Flow**:
1. Tweets are indexed into Elasticsearch asynchronously (via Kafka).
2. Search queries hit Elasticsearch with BM25 ranking + boosting for engagement.
3. Results are enriched with user profile data from the User Service.

### 3.6 Media Service

- **Upload**: Client uploads media to a pre-signed S3 URL.
- **Processing**: Transcode videos, generate thumbnails, create multiple resolutions.
- **Delivery**: Serve through CDN (CloudFront/Akamai) for low-latency global delivery.

---

## Step 4: API Design

```
POST   /api/v1/tweets
  Body: { "content": "Hello world", "media_ids": ["abc123"] }
  Response: 201 { "tweet_id": "...", "created_at": "..." }

GET    /api/v1/timeline/home?cursor=<tweet_id>&limit=20
  Response: 200 { "tweets": [...], "next_cursor": "..." }

GET    /api/v1/users/{user_id}/tweets?cursor=<tweet_id>&limit=20
  Response: 200 { "tweets": [...], "next_cursor": "..." }

POST   /api/v1/users/{user_id}/follow
DELETE /api/v1/users/{user_id}/follow

POST   /api/v1/tweets/{tweet_id}/like
DELETE /api/v1/tweets/{tweet_id}/like

GET    /api/v1/search?q=spring+boot&type=tweets&cursor=...
```

**Pagination**: Use cursor-based pagination (not offset) because:
- New tweets are constantly added, making offset-based pagination inconsistent.
- Cursor (usually a tweet_id or timestamp) provides stable pagination.

---

## Step 5: Scaling Strategy

### Database Scaling

| Component | Strategy |
|-----------|----------|
| Tweets | Shard by user_id (range or hash), replicas for reads |
| Social Graph | Shard by follower_id, cache hot edges in Redis |
| Timeline Cache | Redis Cluster, shard by user_id, TTL = 7 days |
| Search | Elasticsearch cluster with shards and replicas |

### Caching Layers

1. **CDN**: Static assets, media, profile images
2. **Application Cache (Redis)**: Timelines, user profiles, follower lists, tweet counts
3. **Database Cache**: MySQL query cache, connection pooling

### Handling the Celebrity Problem

| User Type | Followers | Fan-Out Strategy |
|-----------|-----------|-----------------|
| Regular (< 10K followers) | Push | Write to all followers' cached timelines |
| Celebrity (10K - 1M) | Hybrid | Push to active followers only |
| Mega-celebrity (> 1M) | Pull | Do NOT fan-out; merge at read time |

---

## Step 6: Monitoring and Operational Concerns

### Key Metrics

| Metric | Target | Alert Threshold |
|--------|--------|----------------|
| Timeline latency (p99) | < 300ms | > 500ms |
| Tweet post latency (p99) | < 200ms | > 400ms |
| Fan-out lag | < 5 seconds | > 30 seconds |
| Search latency (p99) | < 200ms | > 500ms |
| Error rate | < 0.1% | > 1% |

### Failure Handling

| Failure | Impact | Mitigation |
|---------|--------|-----------|
| Redis cluster down | Timelines unavailable | Fall back to DB-based timeline generation |
| Kafka consumer lag | Delayed fan-out | Auto-scale consumers, prioritize recent tweets |
| Elasticsearch down | Search unavailable | Graceful degradation, show trending instead |
| Celebrity tweet storm | Fan-out backlog | Rate limit fan-out, prioritize active users |

---

## Interview Discussion Points

1. **Why hybrid fan-out?** — Pure push cannot handle celebrities (50M writes per tweet). Pure pull is too slow (merge 200 timelines per read). Hybrid balances both.
2. **Why Snowflake IDs?** — Time-sortable (no need for secondary index on created_at), globally unique without coordination, 64-bit fits in a long.
3. **Why cursor pagination?** — New tweets arrive constantly; offset pagination would skip or duplicate tweets.
4. **Consistency model** — Eventual consistency is acceptable. A follower seeing a tweet 2-3 seconds late is fine. Strong consistency would require synchronous fan-out, which is too slow.
5. **How to handle trending topics?** — Count hashtag frequencies in a sliding window using Kafka Streams or Apache Flink, rank by velocity (rate of increase), not just volume.

---

**Difficulty**: Hard
**Frequency**: Very High — the #1 most asked HLD problem
**Key Patterns**: Fan-out on Write/Read, Hybrid approach, Snowflake IDs, Cursor Pagination, Pub/Sub

