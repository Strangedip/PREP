# Web Crawler — High-Level Design

> **You are here**: Senior SDE — System Design (HLD)
> **Roadmap**: [Developer Master Roadmap](../../../ROADMAP.md) | **Prerequisites**: [HLD_Template.md](../../00_Templates/HLD_Template/HLD_Template.md)

## Problem Statement

Design a distributed web crawler that:

- **Discovers and fetches** billions of web pages from the internet
- **Respects politeness** — rate limits per domain, robots.txt
- **Avoids duplicates** — same URL/content not crawled twice
- **Scales horizontally** — add workers without coordination bottlenecks
- **Prioritizes** important pages (freshness, popularity)
- **Extracts** links for further crawling and optionally stores page content

---

## Requirements

### Functional Requirements

1. **Seed URLs** — Start from configured seed list; expand via discovered links
2. **Fetch & Parse** — HTTP GET, extract `<a href>`, normalize URLs
3. **Politeness** — Max N requests/sec per domain; honor `robots.txt`
4. **Dedup** — Skip URLs already seen (URL + optional content hash)
5. **Storage** — Store raw HTML or extracted text for search indexing
6. **Priority** — Frontier queue with priority (recrawl hot pages faster)

### Non-Functional Requirements

1. **Scale** — Crawl 1B+ pages; 100K+ fetch QPS at peak
2. **Reliability** — Retry transient failures; dead-letter for permanent failures
3. **Extensibility** — Plug-in parsers for different content types

---

## Capacity Estimation

```
Pages to crawl: 1B pages (initial corpus)
Average page: 50 KB HTML
Storage: 1B × 50 KB = 50 PB (compressed ~10-15 PB)
Fetch rate: 100K pages/sec peak
Bandwidth: 100K × 50 KB = 5 GB/sec
URL frontier: 10B unique URLs × 100 bytes = 1 TB (Bloom filter + DB)
```

---

## High-Level Architecture

```
┌──────────────┐     ┌─────────────────┐     ┌──────────────────┐
│ Seed Config  │────▶│  URL Frontier   │────▶│  Fetch Workers   │
│ (S3/DB)      │     │  (Priority Q)   │     │  (pool per host) │
└──────────────┘     │  Kafka/SQS      │     └────────┬─────────┘
                     └────────▲────────┘              │
                              │                         │
                     ┌────────┴────────┐     ┌──────────▼─────────┐
                     │  Link Extractor │◀────│  HTTP Fetcher      │
                     │  + Normalizer   │     │  (async, timeouts) │
                     └────────┬────────┘     └────────────────────┘
                              │
              ┌───────────────┼───────────────┐
              ▼               ▼               ▼
     ┌────────────┐  ┌────────────┐  ┌────────────┐
     │ Dedup      │  │ Content    │  │ robots.txt │
     │ Bloom+DB   │  │ Store S3   │  │ Cache      │
     └────────────┘  └────────────┘  └────────────┘
```

---

## Key Components

### URL Frontier

- **Priority queue** per domain or global with domain-aware scheduling
- **Kafka topics** partitioned by `hash(domain)` for locality
- **Politeness**: Token bucket per domain — worker checks before fetch

### URL Normalization

```
http://Example.com:80/page#section → http://example.com/page
Remove default port, lowercase host, resolve relative paths, strip fragment
```

### Dedup Strategy

| Layer | Mechanism | Purpose |
|-------|-----------|---------|
| **Bloom filter** | In-memory, per worker | Fast "probably seen" — may have false positives |
| **URL DB** | DynamoDB / Redis SET | Exact URL dedup |
| **Content hash** | Simhash / SHA of body | Near-duplicate detection |

### Fetch Worker

```java
// Politeness: one queue per domain, max 1 concurrent + rate limit
if (!rateLimiter.tryAcquire(domain)) {
    requeueWithDelay(url, delayMs);
    return;
}
HttpResponse resp = httpClient.get(url, timeout(10s));
if (resp.status() == 200) {
    storeContent(url, resp.body());
    enqueue(extractLinks(resp.body(), baseUrl));
}
```

### robots.txt

- Cache per domain with TTL (24h)
- Parse `Disallow` paths before enqueueing URLs

---

## Data Model

```sql
-- URL metadata (PostgreSQL or DynamoDB)
urls (
  url_hash PK,
  url TEXT,
  domain,
  last_crawled_at,
  priority_score,
  status  -- pending | fetched | failed
)

-- Optional: crawl history for recrawl scheduling
crawl_log (url_hash, fetched_at, http_status, content_hash)
```

**Content**: S3 `s3://crawl-bucket/{url_hash}.html` — cheap, scalable.

---

## APIs (Internal)

| Endpoint | Purpose |
|----------|---------|
| `POST /crawl/seeds` | Add seed URLs |
| `GET /crawl/stats` | Pages crawled, queue depth, errors |
| `POST /crawl/recrawl` | Force recrawl domain or URL pattern |

---

## Scaling & Failure Modes

| Challenge | Solution |
|-----------|----------|
| Hot domains | Per-domain rate limits + dedicated queues |
| Coordinator bottleneck | Decentralized frontier (Kafka partitions) |
| Duplicate explosion | Bloom + distributed dedup store |
| Slow/dead hosts | Timeout, circuit breaker per domain |
| Worker crash | At-least-once queue — idempotent dedup on reprocess |

---

## Spring Boot Reference Sketch

Focused Java 17 / Spring Boot 3.x sketch of frontier enqueue + polite fetch worker — not production-complete.

```java
@RestController
@RequestMapping("/crawl")
public class CrawlAdminController {
    private final CrawlOrchestrator crawlOrchestrator;

    public CrawlAdminController(CrawlOrchestrator crawlOrchestrator) {
        this.crawlOrchestrator = crawlOrchestrator;
    }

    @PostMapping("/seeds")
    public ResponseEntity<Void> addSeeds(@RequestBody SeedRequest request) {
        crawlOrchestrator.enqueueSeeds(request.urls(), request.priority());
        return ResponseEntity.accepted().build();
    }
}

public interface UrlFrontierRepository {
    boolean markSeenIfAbsent(String urlHash, String normalizedUrl, String domain, int priority);
    Optional<FrontierEntry> pollNextForDomain(String domain);
    void requeueWithDelay(FrontierEntry entry, Duration delay);
}

@Service
public class CrawlOrchestrator {
    private final UrlFrontierRepository frontier;
    private final DomainRateLimiter rateLimiter;
    private final RobotsTxtCache robotsTxtCache;
    private final PageFetcher pageFetcher;
    private final LinkExtractor linkExtractor;

    @Async
    public void processUrl(FrontierEntry entry) {
        String domain = entry.domain();
        if (!rateLimiter.tryAcquire(domain)) {
            frontier.requeueWithDelay(entry, Duration.ofSeconds(1));
            return;
        }
        if (!robotsTxtCache.isAllowed(domain, entry.path())) {
            return; // honor robots.txt — cached 24h per domain
        }

        FetchResult result = pageFetcher.fetch(entry.normalizedUrl());
        if (!result.success()) return;

        pageFetcher.storeContent(entry.urlHash(), result.body());
        linkExtractor.extract(result.body(), entry.normalizedUrl()).forEach(link ->
                frontier.markSeenIfAbsent(sha256(link), link, extractDomain(link), entry.priority())
        );
    }

    public void enqueueSeeds(List<String> urls, int priority) {
        urls.forEach(url -> frontier.markSeenIfAbsent(sha256(url), normalize(url), extractDomain(url), priority));
    }

    private String sha256(String url) { return Integer.toHexString(url.hashCode()); }
    private String normalize(String url) { return url.toLowerCase(); }
    private String extractDomain(String url) { return URI.create(url).getHost(); }
}

public record SeedRequest(List<String> urls, int priority) {}
public record FrontierEntry(String urlHash, String normalizedUrl, String domain, String path, int priority) {}
```

> **Idempotency / async**: `markSeenIfAbsent` makes at-least-once queue delivery safe. Per-domain token buckets enforce politeness; Bloom filter can short-circuit before DB dedup.

---

## Interview Discussion Points

1. **BFS vs priority** — BFS for discovery; priority for recrawl and important sites
2. **Politeness** — Why per-domain limits matter (IP ban, ethics)
3. **Bloom filter false positives** — Acceptable to skip rare new URL vs DB cost
4. **Distributed crawling** — Consistent hashing on domain for worker assignment
5. **Relation to search** — Crawler feeds indexer (Elasticsearch) — see Search Autocomplete HLD

---

## Deep dive: URL Frontier design

The frontier is the **heart of the crawler** — it decides what to fetch next.

### BFS vs priority crawl

| Strategy | Behavior | Best for |
|----------|----------|----------|
| **BFS** | Discovers pages breadth-first from seeds | Initial corpus mapping |
| **Priority queue** | High PageRank / fresh pages first | Production search engine |
| **Recency-weighted** | Boost `last_crawled_at` stale pages | News, ecommerce prices |

### Domain-aware scheduling

Problem: One hot domain (e.g. `wikipedia.org`) can starve others.

```
Solution: Separate sub-queue per domain
  - Global scheduler picks domain with highest priority AND available token
  - Token bucket per domain: max 2 req/sec default
  - Worker pool: hash(domain) % N → dedicated worker affinity (cache robots.txt)
```

### Kafka partitioning

```
Topic: crawl-frontier
Partition key: hash(domain)  → all URLs for amazon.in land in same partition
                              → preserves order + politeness per domain
```

---

## Deep dive: URL normalization (must get right)

Inconsistent normalization causes duplicate fetches:

```
Raw:      HTTP://Example.com:80/page/index.html?utm=abc#section
Normalized: http://example.com/page/index.html

Rules:
  1. Lowercase scheme and host
  2. Remove default ports (80 http, 443 https)
  3. Resolve relative URLs against base
  4. Strip fragment (#section) — not sent to server
  5. Optional: strip tracking params (utm_*, fbclid)
  6. Canonicalize trailing slash policy
  7. Punycode international domains
```

**Interview trap**: Treating `http://a.com` and `http://a.com/` as different — pick one rule and apply consistently.

---

## Deep dive: Deduplication layers

```
New URL discovered
    │
    ▼
┌─────────────────┐
│ Bloom filter    │  "Probably seen?" → skip (fast, in-memory per worker)
└────────┬────────┘
         │ probably new
         ▼
┌─────────────────┐
│ Redis SET / DB  │  Exact URL hash dedup (distributed)
└────────┬────────┘
         │ new URL
         ▼
┌─────────────────┐
│ Content hash    │  Simhash / SHA-256 of body → near-duplicate detection
└─────────────────┘
```

### Bloom filter math (interview bonus)

- Size `m` bits, `k` hash functions, `n` inserted elements
- False positive rate ≈ `(1 - e^(-kn/m))^k`
- **1% FP rate** with 1B URLs ≈ 1.2 GB bloom filter — acceptable RAM cost vs DB lookups

**False positive consequence**: Rare new URL skipped — acceptable at web scale.

---

## Deep dive: Politeness and robots.txt

### Why politeness matters

- **Ethical**: Don't DDoS small sites
- **Practical**: Aggressive crawling → IP ban → zero data
- **Legal**: robots.txt is convention (not law everywhere, but industry standard)

### robots.txt flow

```
1. First fetch for domain → GET /robots.txt (cache 24h)
2. Parse Disallow: /admin, /private
3. Before enqueueing URL → check path allowed
4. Respect Crawl-delay if present (non-standard but used)
```

### Rate limiting implementation

```java
// Token bucket per domain
class DomainRateLimiter {
    private final Map<String, RateLimiter> buckets = new ConcurrentHashMap<>();

    boolean tryAcquire(String domain) {
        return buckets
            .computeIfAbsent(domain, d -> RateLimiter.create(1.0)) // 1 req/sec
            .tryAcquire();
    }
}
```

---

## Deep dive: Fetch worker lifecycle

```
1. Poll URL from frontier (blocked if domain rate limited → requeue with delay)
2. Check robots.txt cache
3. HTTP GET with timeouts (connect 5s, read 10s)
4. Handle redirects (max 5 hops; normalize each redirect URL)
5. Parse Content-Type — only process text/html for link extraction
6. Store raw HTML to S3: s3://crawl/{url_hash}.html
7. Extract links → normalize → dedup → enqueue new URLs
8. Update crawl_log (status, content_hash, fetched_at)
```

### HTTP status handling

| Status | Action |
|--------|--------|
| 200 | Store + extract links |
| 301/302 | Follow redirect; enqueue final URL |
| 404 | Mark fetched; don't recrawl soon |
| 429 | Back off domain rate; requeue |
| 5xx | Retry with exponential backoff (max 3) |
| Timeout | Retry; circuit-break domain after N failures |

---

## Recrawl strategy

Not every page needs daily refresh:

| Page type | Recrawl interval |
|-----------|------------------|
| News homepage | 15–60 minutes |
| Product price page | 1–6 hours |
| Static blog post | 7–30 days |
| 404 / dead | 90 days or never |

**Priority score formula** (simplified):

```
priority = w1 * page_rank + w2 * freshness_need + w3 * change_frequency
```

---

## Distributed coordination

| Challenge | Single-machine | Distributed |
|-----------|------------------|-------------|
| Frontier | PriorityQueue in memory | Kafka partitioned by domain |
| Dedup | HashSet | Redis Cluster + Bloom per worker |
| robots.txt | Local cache | Redis with TTL |
| Politeness | synchronized | Per-domain token bucket in Redis |

**No central coordinator**: Workers are stateless; Kafka consumer groups partition work.

---

## Failure modes (expanded)

| Failure | Symptom | Mitigation |
|---------|---------|------------|
| Hot domain queue | One domain blocks partition | Per-domain fair scheduling |
| Duplicate explosion | 10B URLs, mostly dupes | Bloom + aggressive normalization |
| Slow host | Worker stuck 30s | Strict timeouts; circuit breaker |
| Worker crash mid-fetch | URL lost | At-least-once queue; idempotent dedup |
| robots.txt 500 | Unknown if allowed | Default deny or retry; don't crawl until resolved |
| DNS failure | Domain unreachable | Dead-letter; alert if seed domain |
| Parser bug | Infinite link loop | Max depth limit; max URLs per domain |
| Storage full | S3 write fails | Tiered storage; compress HTML |

---

## Connection to search indexing

```
Crawler → S3 (raw HTML)
       → Parser extracts text, title, links
       → Indexer builds inverted index (Elasticsearch)
       → Search API serves queries
```

See [SearchAutocomplete HLD](../SearchAutocomplete/SearchAutocomplete.md) for query-side design.

**Related**: [SearchAutocomplete](../SearchAutocomplete/SearchAutocomplete.md), [DistributedCache](../DistributedCache/DistributedCache.md)
