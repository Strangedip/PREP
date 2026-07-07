# Search Autocomplete (Typeahead) — High-Level Design

> **You are here**: Senior SDE — System Design (HLD)
> **Roadmap**: [Developer Master Roadmap](../../../ROADMAP.md) | **Prerequisites**: [HLD_Template.md](../../00_Templates/HLD_Template/HLD_Template.md)

## Problem Statement

Design a search autocomplete system that:

- **Returns top suggestions** as the user types (prefix matching)
- **Ranks by popularity** (frequency, recency, personalization)
- **Handles billions of queries** with sub-100ms latency
- **Updates rankings** as search trends change
- **Supports multiple locales** and typo tolerance (optional)

**Examples**: Google Search suggestions, Amazon product search, Twitter user search.

---

## Requirements

### Functional Requirements

1. **Prefix search**: Given prefix `str`, return top K suggestions (default K=10).
2. **Ranking**: Order by global popularity, optionally personalized per user.
3. **Data sources**: Historical search logs, trending queries, curated lists.
4. **Update frequency**: Trending data refreshed every few minutes; static catalogs daily.

### Non-Functional Requirements

1. **Latency**: p99 < 100ms for autocomplete API.
2. **Availability**: 99.99% — search is revenue-critical.
3. **Scale**: 500M users, 10B searches/day → ~120K QPS average, ~500K QPS peak.
4. **Consistency**: Eventually consistent rankings acceptable (stale suggestions OK for minutes).

---

## Capacity Estimation

```
Searches/day:     10B
Average QPS:      10B / 86,400 ≈ 115,000 QPS
Peak QPS:         ~500,000 (4-5x average)

Unique prefixes queried: ~50M active prefixes/day
Top-K response size: 10 strings × ~50 bytes = 500 bytes/response
Outbound bandwidth peak: 500K × 500B = 250 MB/s

Storage:
  Unique queries in catalog: ~100M entries
  Trie node estimate: ~500 bytes/node × 50M nodes ≈ 25 GB (compressed less)
  Trending scores: 100M × 16 bytes ≈ 1.6 GB in memory
```

---

## High-Level Architecture

```
┌─────────────┐     ┌──────────────────┐     ┌─────────────────────┐
│   Client    │────►│  API Gateway /   │────►│ Autocomplete Service │
│  (debounce) │     │  Load Balancer   │     │  (stateless, many)   │
└─────────────┘     └──────────────────┘     └──────────┬──────────┘
                                                        │
                    ┌───────────────────────────────────┼───────────────────────┐
                    │                                   │                       │
                    ▼                                   ▼                       ▼
           ┌────────────────┐              ┌──────────────────┐    ┌─────────────────┐
           │  Redis Cache   │              │  Trie Service    │    │ Ranking Service │
           │  prefix→topK   │              │  (in-memory trie │    │ (trending scores)│
           │  TTL 5-60 min  │              │  or distributed) │    │                 │
           └────────────────┘              └──────────────────┘    └─────────────────┘
                                                        │
                                                        ▼
                                           ┌──────────────────────┐
                                           │  Offline Pipeline    │
                                           │  Spark + Airflow     │
                                           │  Log → Aggregate →   │
                                           │  Build trie snapshots│
                                           └──────────────────────┘
```

---

## Core Data Structures

### 1. Trie (Prefix Tree)

Each node stores:
- `children: Map<char, TrieNode>`
- `topK: List<String>` — precomputed top suggestions for this prefix (K=10)
- `isEnd: boolean`

**Why precompute topK at each node?** Query is O(prefix length) — no traversal of entire subtree at request time.

### 2. Alternative: Elasticsearch / OpenSearch

- Prefix query on `keyword` field with edge n-gram tokenizer
- Better for fuzzy matching and complex ranking
- Trade-off: higher latency than in-memory trie

### 3. Ranking Signals

| Signal | Weight | Source |
|--------|--------|--------|
| Global frequency | High | Search logs (last 30 days) |
| Recency / trending | Medium | Spike detection (compare 1h vs 24h) |
| Personal history | Medium | User's past searches |
| Business rules | Override | Promoted products, safety blocklist |

---

## API Design

```
GET /v1/suggest?q=str&limit=10&user_id=optional

Response:
{
  "suggestions": [
    { "text": "strawberry", "score": 0.95 },
    { "text": "streaming", "score": 0.89 },
    { "text": "street", "score": 0.82 }
  ],
  "latency_ms": 12
}
```

**Client optimization**: Debounce 150-300ms; cancel in-flight requests on new keystroke.

---

## Detailed Component Design

### Autocomplete Service (Online Path)

```
1. Normalize query (lowercase, trim, locale)
2. Check Redis: key = "suggest:{prefix}:{locale}"
3. If hit → return cached topK
4. If miss → query Trie shard OR ES cluster
5. Merge with personalization layer (user history from Redis)
6. Apply blocklist / safety filter
7. Cache result, return
```

### Offline Pipeline (Batch)

```
Search logs (Kafka/S3)
    → Spark: aggregate query counts per hour
    → Trending detector: z-score on hourly counts
    → Merge with static catalog
    → Build trie snapshot (serialized)
    → Push to Trie Service instances (rolling update)
```

**Trie update strategy**: Double-buffer — load new trie in background, swap pointer atomically (no downtime).

---

## Scaling Strategy

| Challenge | Solution |
|-----------|----------|
| Memory for full trie | Shard by first 2 chars (`st*` → shard 1) |
| Hot prefixes (`a`, `s`) | Dedicated cache layer, CDN for popular prefixes |
| Global distribution | Geo-replicated trie snapshots per region |
| Personalization | Per-user top 20 history in Redis — merge at read time |

### Consistent Hashing for Trie Shards

```
shard = hash(prefix[0:2]) % num_shards
```

---

## Caching Layers

| Layer | TTL | Hit Rate Target |
|-------|-----|-----------------|
| Client local cache | Session | — |
| CDN / Edge | 1 min | 30% for mega-hot prefixes |
| Redis cluster | 5-60 min | 80%+ |
| In-process trie | Until snapshot swap | 95%+ on miss path |

**Cache stampede**: Use single-flight (one request rebuilds, others wait) on Redis miss.

---

## Security & Safety

- **Blocklist**: Profanity, illegal content — filter before return
- **Injection**: Treat query as data, never execute as code
- **Rate limiting**: Per IP/user — autocomplete is abuse vector for scraping
- **Do not leak PII**: Never suggest other users' private searches in global suggestions

---

## Spring Boot Reference Sketch

Focused Java 17 / Spring Boot 3.x sketch of the autocomplete read path — not production-complete.

```java
@RestController
@RequestMapping("/v1/suggest")
public class AutocompleteController {
    private final AutocompleteService autocompleteService;

    public AutocompleteController(AutocompleteService autocompleteService) {
        this.autocompleteService = autocompleteService;
    }

    @GetMapping
    public SuggestResponse suggest(
            @RequestParam("q") String query,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(required = false) Long userId) {
        return autocompleteService.suggest(query, limit, userId);
    }
}

public interface TrieRepository {
    List<Suggestion> findByPrefix(String normalizedPrefix, String locale, int limit);
}

@Service
public class AutocompleteService {
    private static final Duration CACHE_TTL = Duration.ofMinutes(15);
    private final StringRedisTemplate redis;
    private final TrieRepository trieRepository;

    public SuggestResponse suggest(String query, int limit, Long userId) {
        String prefix = normalize(query);
        if (prefix.length() < 2) return SuggestResponse.empty();

        String cacheKey = "suggest:%s:%s".formatted(prefix, defaultLocale());
        List<Suggestion> cached = readCache(cacheKey);
        if (cached != null) return new SuggestResponse(cached, true);

        List<Suggestion> global = trieRepository.findByPrefix(prefix, defaultLocale(), limit);
        List<Suggestion> merged = mergePersonalization(global, userId, limit);

        redis.opsForValue().set(cacheKey, serialize(merged), CACHE_TTL);
        return new SuggestResponse(merged, false);
    }

    private String normalize(String query) {
        return query.trim().toLowerCase(Locale.ROOT);
    }

    private List<Suggestion> mergePersonalization(List<Suggestion> global, Long userId, int limit) {
        if (userId == null) return global.stream().limit(limit).toList();
        // Async user-history fetch can run in parallel with trie lookup in production
        return global.stream().limit(limit).toList();
    }

    private List<Suggestion> readCache(String key) { /* JSON deserialize */ return null; }
    private String serialize(List<Suggestion> items) { return "[]"; }
    private String defaultLocale() { return "en-US"; }
}

public record Suggestion(String text, double score) {}
public record SuggestResponse(List<Suggestion> suggestions, boolean cacheHit) {
    static SuggestResponse empty() { return new SuggestResponse(List.of(), false); }
}
```

> **Caching / async**: Redis prefix→topK with single-flight on miss; offline trie rebuilds swap atomically. Personalization merge is eventually consistent — stale rankings for minutes are acceptable.

---

## Interview Discussion Points

1. **Trie vs Elasticsearch?** Trie for ultra-low latency simple prefix; ES for fuzzy, multi-field, complex ranking.
2. **How to handle typos?** Fuzzy trie, edit distance 1-2, or ES `fuzziness` parameter.
3. **Celebrity / viral query problem?** Sudden spike — trending layer updates faster than trie rebuild; overlay trending cache.
4. **Personalization without latency hit?** Async fetch user history parallel to trie lookup; merge with 50ms budget.
5. **How often rebuild trie?** Hourly for trends; daily full rebuild; incremental updates for new catalog items.

---

## Trade-offs Summary

| Choice | Pros | Cons |
|--------|------|------|
| In-memory trie | Sub-10ms reads | Memory cost, update complexity |
| Elasticsearch | Flexible ranking | 20-50ms+, operational cost |
| Precomputed topK at nodes | Fast reads | Large memory, stale on rank change |
| Fan-out on write (precompute all prefixes) | Fast reads | Expensive offline compute |

---

## Deep dive: Trie with precomputed topK

### Building the trie (offline)

For each query in search logs (e.g., "strawberry", count=1M):

```
Insert "strawberry" into trie
At node 's':     add "strawberry" to topK if count qualifies
At node 'st':     add "strawberry" to topK
At node 'str':    add "strawberry" to topK
At node 'stra':   add "strawberry" to topK
... every prefix node gets updated
```

### Query walkthrough

```
User types: "str"

1. Normalize → "str"
2. Redis miss → trie lookup
3. Walk: root → s → t → r
4. Return node('r').topK = ["strawberry", "streaming", "street", ...]
5. Cache in Redis: suggest:str:en → JSON (TTL 15 min)
```

**Time complexity**: O(prefix length) — typically 3-15 character steps.

```mermaid
flowchart LR
    root --> s --> t --> r
    r -->|"topK"| TK["strawberry, streaming, street"]
```

### Memory trade-off

| Approach | Query time | Memory | Freshness |
|----------|------------|--------|-----------|
| Trie + topK at each node | O(prefix) | High (~25 GB) | Stale until rebuild |
| Trie without topK | O(prefix + K log N) | Lower | Better |
| Elasticsearch edge n-gram | 20-50ms | ES cluster cost | Near real-time |

---

## Deep dive: Trending overlay

Trie rebuilds hourly — viral queries spike in minutes.

```
Separate Redis sorted set: trending:global (updated every 1 min from stream)
On suggest("cor"): merge trie results + trending matches
  → "coronavirus" may appear despite hourly trie
```

---

## Deep dive: Personalization merge

```
Parallel:
  Thread 1: trie lookup("str") → 50ms budget
  Thread 2: user:42:recent_searches from Redis → 10ms

Merge:
  Boost score if suggestion in user's history
  Cap personalization influence at 30% (don't override global popularity entirely)
```

---

## Failure modes

| Failure | Mitigation |
|---------|------------|
| Trie rebuild failure | Keep old snapshot; double-buffer swap |
| Hot prefix "a" | Dedicated Redis cache; CDN for mega-prefix |
| Scraping abuse | Rate limit per IP; CAPTCHA on burst |
| PII in suggestions | Blocklist; never use private searches globally |
| Stale trending | Overlay layer updates faster than full trie |

---

## Interview walkthrough (40 min)

1. **Requirements** — latency, scale, ranking signals
2. **Trie + precomputed topK** — why O(prefix) matters
3. **Caching layers** — Redis, CDN, client debounce
4. **Offline pipeline** — Spark aggregation, trending
5. **Trie vs Elasticsearch** — trade-off table
6. **Safety** — blocklist, rate limits

