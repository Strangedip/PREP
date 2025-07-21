# URL Shortener System - High Level Design

## Problem Statement

Design a URL shortening service like bit.ly, TinyURL, or goo.gl that can:

- **Shorten long URLs** into compact, shareable links
- **Redirect users** from short URLs to original URLs
- **Handle high traffic** (100M URLs shortened per day)
- **Provide analytics** on link usage and clicks
- **Support custom aliases** for branded short URLs
- **Ensure high availability** and low latency globally

## Requirements Analysis

### Functional Requirements

1. **URL Shortening**
   - Convert long URLs to short URLs (6-8 characters)
   - Support custom aliases for branded links
   - Handle duplicate URLs efficiently

2. **URL Redirection**
   - Redirect short URLs to original URLs
   - Track click analytics and metadata
   - Handle expired or deleted URLs

3. **Analytics and Reporting**
   - Track click counts, timestamps, and geographic data
   - Generate usage reports and analytics dashboards
   - Real-time and historical data analysis

4. **User Management**
   - User accounts and authentication
   - URL ownership and management
   - Bulk URL operations

### Non-Functional Requirements

1. **Scale**
   - **100M URLs shortened per day** (1,157 URLs/second)
   - **10B redirects per day** (115,740 redirects/second)
   - **100:1 read-to-write ratio**

2. **Performance**
   - **Redirection latency < 100ms**
   - **URL shortening < 500ms**
   - **99.9% availability**

3. **Storage**
   - **5-year retention** for URLs
   - **100B URLs total** (considering growth)
   - **500 bytes per URL** (metadata included)
   - **~50TB total storage** needed

4. **Geographic Distribution**
   - **Global CDN** for low latency
   - **Multi-region deployment**
   - **Disaster recovery**

## Capacity Estimation

### QPS (Queries Per Second)
```
URL Shortening: 100M/day = 1,157 QPS
URL Redirects: 10B/day = 115,740 QPS
Peak Traffic: 2x average = 231,480 QPS (redirects)
```

### Storage Requirements
```
URLs per year: 100M × 365 = 36.5B URLs
5-year total: 36.5B × 5 = 182.5B URLs
Storage per URL: 500 bytes (URL + metadata)
Total storage: 182.5B × 500 bytes = ~91TB
```

### Bandwidth
```
Incoming: 1,157 QPS × 500 bytes = 0.6 MB/s
Outgoing: 115,740 QPS × 500 bytes = 58 MB/s
```

### Memory (Cache)
```
Daily active URLs: 20% of total redirects
Cache requirement: 2B URLs × 500 bytes = 1TB
With replication: ~3TB total cache
```

## High-Level System Architecture

```
                                    ┌─────────────────┐
                                    │   Load Balancer │
                                    │    (Layer 7)    │
                                    └─────────┬───────┘
                                              │
                    ┌─────────────────────────┼─────────────────────────┐
                    │                         │                         │
            ┌───────▼────────┐      ┌────────▼────────┐      ┌────────▼────────┐
            │  URL Shortening │      │  URL Redirection│      │   Analytics     │
            │    Service      │      │     Service     │      │    Service      │
            └───────┬────────┘      └────────┬────────┘      └────────┬────────┘
                    │                        │                        │
    ┌───────────────┼────────────────────────┼────────────────────────┼───────────────┐
    │               │                        │                        │               │
    │   ┌───────────▼──────────┐    ┌───────▼───────┐         ┌──────▼──────┐        │
    │   │     Write DB         │    │  Read Cache   │         │ Analytics   │        │
    │   │   (Master-Slave)     │    │    (Redis)    │         │     DB      │        │
    │   │      MySQL           │    │   Cluster     │         │ (ClickHouse)│        │
    │   └──────────────────────┘    └───────────────┘         └─────────────┘        │
    │                                                                                 │
    │                              ┌─────────────────┐                               │
    │                              │       CDN       │                               │
    │                              │   (CloudFlare)  │                               │
    │                              └─────────────────┘                               │
    └─────────────────────────────────────────────────────────────────────────────────┘
```

## Detailed Component Design

### 1. URL Shortening Service

#### Base62 Encoding Algorithm
```python
class URLShortener:
    def __init__(self):
        self.base62_chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
        self.counter = 0
    
    def encode(self, number):
        """Convert number to base62 string"""
        if number == 0:
            return self.base62_chars[0]
        
        result = ""
        while number > 0:
            result = self.base62_chars[number % 62] + result
            number //= 62
        return result
    
    def generate_short_url(self, long_url):
        """Generate short URL using auto-increment counter"""
        # Use distributed counter (Redis/Zookeeper)
        url_id = self.get_next_id()
        short_code = self.encode(url_id)
        
        return f"https://short.ly/{short_code}"
```

#### Database Schema
```sql
-- URLs table for storing mapping
CREATE TABLE urls (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    short_code VARCHAR(10) UNIQUE NOT NULL,
    long_url TEXT NOT NULL,
    user_id BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP NULL,
    is_active BOOLEAN DEFAULT TRUE,
    
    INDEX idx_short_code (short_code),
    INDEX idx_user_id (user_id),
    INDEX idx_created_at (created_at)
);

-- URL analytics table
CREATE TABLE url_analytics (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    short_code VARCHAR(10) NOT NULL,
    clicked_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    ip_address VARCHAR(45),
    user_agent TEXT,
    referer TEXT,
    country VARCHAR(2),
    city VARCHAR(100),
    
    INDEX idx_short_code_time (short_code, clicked_at),
    INDEX idx_clicked_at (clicked_at)
);
```

### 2. URL Redirection Service

#### High-Performance Redirection
```java
@RestController
public class RedirectionController {
    
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    
    @Autowired
    private URLService urlService;
    
    @GetMapping("/{shortCode}")
    public ResponseEntity<Void> redirect(@PathVariable String shortCode,
                                       HttpServletRequest request) {
        
        // 1. Check cache first (L1 - Application Cache)
        String longUrl = applicationCache.get(shortCode);
        
        if (longUrl == null) {
            // 2. Check Redis cache (L2 - Distributed Cache)
            longUrl = redisTemplate.opsForValue().get("url:" + shortCode);
            
            if (longUrl == null) {
                // 3. Check database (L3 - Persistent Storage)
                URLEntity urlEntity = urlService.findByShortCode(shortCode);
                
                if (urlEntity == null || !urlEntity.isActive()) {
                    return ResponseEntity.notFound().build();
                }
                
                longUrl = urlEntity.getLongUrl();
                
                // Cache for future requests
                redisTemplate.opsForValue().set("url:" + shortCode, longUrl, 
                    Duration.ofHours(24));
                applicationCache.put(shortCode, longUrl);
            }
        }
        
        // 4. Record analytics asynchronously
        analyticsService.recordClickAsync(shortCode, request);
        
        // 5. Redirect with 301 (permanent) or 302 (temporary)
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create(longUrl));
        return new ResponseEntity<>(headers, HttpStatus.MOVED_PERMANENTLY);
    }
}
```

### 3. Caching Strategy

#### Multi-Layer Caching
```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   L1: App Cache │    │  L2: Redis      │    │  L3: Database   │
│   (In-Memory)   │    │  (Distributed)  │    │   (MySQL)       │
├─────────────────┤    ├─────────────────┤    ├─────────────────┤
│ Size: 10K URLs │    │ Size: 10M URLs  │    │ Size: 100B URLs │
│ TTL: 1 hour     │    │ TTL: 24 hours   │    │ Persistent      │
│ Hit Rate: 40%   │    │ Hit Rate: 35%   │    │ Hit Rate: 25%   │
└─────────────────┘    └─────────────────┘    └─────────────────┘
```

#### Cache Invalidation Strategy
```java
public class URLCacheManager {
    
    public void invalidateURL(String shortCode) {
        // 1. Remove from application cache
        applicationCache.remove(shortCode);
        
        // 2. Remove from Redis
        redisTemplate.delete("url:" + shortCode);
        
        // 3. Notify other instances via pub/sub
        redisTemplate.convertAndSend("cache.invalidate", shortCode);
    }
    
    @EventListener
    public void handleCacheInvalidation(String shortCode) {
        applicationCache.remove(shortCode);
    }
}
```

### 4. Database Design and Sharding

#### Sharding Strategy
```
Shard Key: hash(short_code) % num_shards

Shard 1: short_codes [0, 21]     → DB_Shard_1
Shard 2: short_codes [22, 43]    → DB_Shard_2  
Shard 3: short_codes [44, 61]    → DB_Shard_3

Benefits:
- Even distribution of data
- No hotspots
- Easy to add new shards
```

#### Master-Slave Configuration
```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Write Master  │    │  Read Replica 1 │    │  Read Replica 2 │
│   (Primary)     │────▶│  (Secondary)    │    │  (Secondary)    │
├─────────────────┤    ├─────────────────┤    ├─────────────────┤
│ Handles writes  │    │ Handles reads   │    │ Handles reads   │
│ Synchronous     │    │ Asynchronous    │    │ Asynchronous    │
│ ACID compliance │    │ replication     │    │ replication     │
└─────────────────┘    └─────────────────┘    └─────────────────┘
```

### 5. Analytics Service

#### Real-time Analytics Pipeline
```
Click Event → Kafka → Stream Processing → ClickHouse → Dashboard
                │            │               │           │
                │            │               │           │
           Buffer &      Aggregate &      Store &     Query &
           Batch        Transform      Analyze      Visualize
```

#### Analytics Data Model
```sql
-- ClickHouse table for analytics
CREATE TABLE url_clicks (
    short_code String,
    clicked_at DateTime,
    ip_address String,
    country String,
    city String,
    device_type String,
    browser String,
    referer String
) ENGINE = MergeTree()
PARTITION BY toYYYYMM(clicked_at)
ORDER BY (short_code, clicked_at);
```

### 6. Rate Limiting

#### Token Bucket Algorithm
```java
@Component
public class RateLimiter {
    
    private final RedisTemplate<String, String> redisTemplate;
    
    public boolean isAllowed(String userId, int requestsPerHour) {
        String key = "rate_limit:" + userId;
        String currentTime = String.valueOf(System.currentTimeMillis() / 1000);
        
        // Sliding window rate limiting
        redisTemplate.opsForZSet().removeRangeByScore(key, 
            0, System.currentTimeMillis() - 3600000); // Remove old entries
        
        Long currentRequests = redisTemplate.opsForZSet().count(key, 
            System.currentTimeMillis() - 3600000, System.currentTimeMillis());
        
        if (currentRequests < requestsPerHour) {
            redisTemplate.opsForZSet().add(key, currentTime, System.currentTimeMillis());
            redisTemplate.expire(key, Duration.ofHours(1));
            return true;
        }
        
        return false;
    }
}
```

## Scalability and Performance Optimizations

### 1. Horizontal Scaling

#### Auto-scaling Configuration
```yaml
# Kubernetes HPA configuration
apiVersion: autoscaling/v2
kind: HorizontalPodAutoscaler
metadata:
  name: url-shortener-hpa
spec:
  scaleTargetRef:
    apiVersion: apps/v1
    kind: Deployment
    name: url-shortener
  minReplicas: 10
  maxReplicas: 100
  metrics:
  - type: Resource
    resource:
      name: cpu
      target:
        type: Utilization
        averageUtilization: 70
  - type: Resource
    resource:
      name: memory
      target:
        type: Utilization
        averageUtilization: 80
```

### 2. Global Distribution

#### CDN Integration
```javascript
// CloudFlare Worker for edge redirection
addEventListener('fetch', event => {
  event.respondWith(handleRequest(event.request))
})

async function handleRequest(request) {
  const url = new URL(request.url)
  const shortCode = url.pathname.substring(1)
  
  // Check edge cache first
  const cache = caches.default
  const cacheKey = new Request(url.toString(), request)
  let response = await cache.match(cacheKey)
  
  if (!response) {
    // Fetch from origin server
    const originUrl = `https://api.shortener.com/redirect/${shortCode}`
    response = await fetch(originUrl)
    
    // Cache successful redirects
    if (response.status === 301 || response.status === 302) {
      response = new Response(response.body, {
        status: response.status,
        statusText: response.statusText,
        headers: {
          ...response.headers,
          'Cache-Control': 'public, max-age=86400'
        }
      })
      
      event.waitUntil(cache.put(cacheKey, response.clone()))
    }
  }
  
  return response
}
```

### 3. Security Measures

#### URL Validation and Sanitization
```java
public class URLValidator {
    
    private static final List<String> BLACKLISTED_DOMAINS = Arrays.asList(
        "malware.com", "phishing.net", "spam.org"
    );
    
    private static final Pattern URL_PATTERN = Pattern.compile(
        "^https?://[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}(/.*)?$"
    );
    
    public ValidationResult validateURL(String url) {
        // 1. Format validation
        if (!URL_PATTERN.matcher(url).matches()) {
            return ValidationResult.invalid("Invalid URL format");
        }
        
        // 2. Length validation
        if (url.length() > 2048) {
            return ValidationResult.invalid("URL too long");
        }
        
        // 3. Blacklist check
        try {
            URI uri = new URI(url);
            String domain = uri.getHost().toLowerCase();
            
            if (BLACKLISTED_DOMAINS.contains(domain)) {
                return ValidationResult.invalid("Domain is blacklisted");
            }
        } catch (URISyntaxException e) {
            return ValidationResult.invalid("Invalid URI syntax");
        }
        
        // 4. Malware/phishing check (external service)
        if (isMaliciousURL(url)) {
            return ValidationResult.invalid("URL flagged as malicious");
        }
        
        return ValidationResult.valid();
    }
}
```

## Advanced Features

### 1. Custom Aliases and Branded Links
```java
public class CustomAliasService {
    
    public String createCustomAlias(String longUrl, String customAlias, String userId) {
        // Validate custom alias
        if (!isValidAlias(customAlias)) {
            throw new InvalidAliasException("Alias contains invalid characters");
        }
        
        // Check availability
        if (aliasExists(customAlias)) {
            throw new AliasNotAvailableException("Alias already taken");
        }
        
        // Create branded short URL
        URLEntity urlEntity = new URLEntity();
        urlEntity.setShortCode(customAlias);
        urlEntity.setLongUrl(longUrl);
        urlEntity.setUserId(userId);
        urlEntity.setCustomAlias(true);
        
        urlRepository.save(urlEntity);
        
        return "https://mybrand.ly/" + customAlias;
    }
}
```

### 2. Link Expiration and Management
```java
@Scheduled(fixedRate = 3600000) // Run every hour
public void cleanupExpiredURLs() {
    List<URLEntity> expiredUrls = urlRepository.findExpiredUrls(LocalDateTime.now());
    
    for (URLEntity url : expiredUrls) {
        // Soft delete
        url.setActive(false);
        urlRepository.save(url);
        
        // Remove from cache
        cacheManager.evict("urls", url.getShortCode());
        
        // Log for analytics
        logger.info("Expired URL: {}", url.getShortCode());
    }
}
```

### 3. Bulk Operations
```java
public class BulkURLService {
    
    @Async
    public CompletableFuture<BulkResult> shortenBulkURLs(List<String> urls, String userId) {
        List<String> shortened = new ArrayList<>();
        List<String> failed = new ArrayList<>();
        
        // Process in batches to avoid memory issues
        int batchSize = 1000;
        for (int i = 0; i < urls.size(); i += batchSize) {
            List<String> batch = urls.subList(i, Math.min(i + batchSize, urls.size()));
            
            for (String url : batch) {
                try {
                    String shortUrl = urlShorteningService.shortenURL(url, userId);
                    shortened.add(shortUrl);
                } catch (Exception e) {
                    failed.add(url);
                    logger.error("Failed to shorten URL: {}", url, e);
                }
            }
        }
        
        return CompletableFuture.completedFuture(new BulkResult(shortened, failed));
    }
}
```

## Monitoring and Observability

### 1. Key Metrics
```yaml
# Prometheus metrics configuration
metrics:
  - name: url_shortening_requests_total
    type: counter
    help: Total number of URL shortening requests
    labels: [status, user_type]
    
  - name: url_redirection_requests_total
    type: counter
    help: Total number of URL redirection requests
    labels: [status, cache_hit]
    
  - name: url_redirection_latency_seconds
    type: histogram
    help: URL redirection latency
    buckets: [0.001, 0.01, 0.1, 0.5, 1.0, 2.0, 5.0]
    
  - name: cache_hit_ratio
    type: gauge
    help: Cache hit ratio percentage
    labels: [cache_layer]
    
  - name: database_connection_pool_size
    type: gauge
    help: Database connection pool size
    labels: [database_shard]
```

### 2. Health Checks
```java
@Component
public class HealthCheckService {
    
    @HealthIndicator
    public Health database() {
        try {
            urlRepository.count();
            return Health.up()
                .withDetail("database", "MySQL connection healthy")
                .build();
        } catch (Exception e) {
            return Health.down()
                .withDetail("database", "MySQL connection failed")
                .withException(e)
                .build();
        }
    }
    
    @HealthIndicator
    public Health cache() {
        try {
            redisTemplate.opsForValue().get("health_check");
            return Health.up()
                .withDetail("cache", "Redis connection healthy")
                .build();
        } catch (Exception e) {
            return Health.down()
                .withDetail("cache", "Redis connection failed")
                .withException(e)
                .build();
        }
    }
}
```

## Disaster Recovery and Data Backup

### 1. Backup Strategy
```bash
#!/bin/bash
# Daily backup script

# Database backup
mysqldump --host=$DB_HOST --user=$DB_USER --password=$DB_PASS \
  --single-transaction --routines --triggers url_shortener > \
  /backups/mysql_$(date +%Y%m%d_%H%M%S).sql

# Compress and upload to S3
gzip /backups/mysql_$(date +%Y%m%d_%H%M%S).sql
aws s3 cp /backups/mysql_$(date +%Y%m%d_%H%M%S).sql.gz \
  s3://url-shortener-backups/mysql/

# Redis backup
redis-cli --rdb /backups/redis_$(date +%Y%m%d_%H%M%S).rdb
aws s3 cp /backups/redis_$(date +%Y%m%d_%H%M%S).rdb \
  s3://url-shortener-backups/redis/

# Cleanup old backups (keep 30 days)
find /backups -name "*.sql.gz" -mtime +30 -delete
find /backups -name "*.rdb" -mtime +30 -delete
```

### 2. Multi-Region Deployment
```yaml
# Terraform configuration for multi-region deployment
resource "aws_instance" "url_shortener" {
  count = var.instance_count
  
  ami           = var.ami_id
  instance_type = var.instance_type
  
  # Deploy across multiple AZs
  availability_zone = data.aws_availability_zones.available.names[count.index % length(data.aws_availability_zones.available.names)]
  
  vpc_security_group_ids = [aws_security_group.url_shortener.id]
  subnet_id              = aws_subnet.private[count.index % length(aws_subnet.private)].id
  
  user_data = templatefile("${path.module}/user_data.sh", {
    db_endpoint = aws_rds_cluster.main.endpoint
    redis_endpoint = aws_elasticache_cluster.main.cache_nodes.0.address
  })
  
  tags = {
    Name = "url-shortener-${count.index + 1}"
    Environment = var.environment
  }
}
```

## API Design

### REST API Endpoints
```yaml
# OpenAPI 3.0 specification
openapi: 3.0.0
info:
  title: URL Shortener API
  version: 1.0.0
  
paths:
  /api/v1/shorten:
    post:
      summary: Shorten a URL
      requestBody:
        required: true
        content:
          application/json:
            schema:
              type: object
              properties:
                url:
                  type: string
                  format: uri
                  example: "https://example.com/very/long/url"
                custom_alias:
                  type: string
                  example: "my-link"
                expires_at:
                  type: string
                  format: date-time
      responses:
        200:
          description: URL shortened successfully
          content:
            application/json:
              schema:
                type: object
                properties:
                  short_url:
                    type: string
                    example: "https://short.ly/abc123"
                  expires_at:
                    type: string
                    format: date-time
                    
  /{shortCode}:
    get:
      summary: Redirect to original URL
      parameters:
        - name: shortCode
          in: path
          required: true
          schema:
            type: string
      responses:
        301:
          description: Permanent redirect
          headers:
            Location:
              schema:
                type: string
                format: uri
        404:
          description: Short URL not found
          
  /api/v1/analytics/{shortCode}:
    get:
      summary: Get analytics for a short URL
      parameters:
        - name: shortCode
          in: path
          required: true
          schema:
            type: string
        - name: period
          in: query
          schema:
            type: string
            enum: [hour, day, week, month]
      responses:
        200:
          description: Analytics data
          content:
            application/json:
              schema:
                type: object
                properties:
                  total_clicks:
                    type: integer
                  unique_clicks:
                    type: integer
                  click_data:
                    type: array
                    items:
                      type: object
```

## Alternative Approaches and Trade-offs

### 1. Encoding Strategies

#### Counter-based vs Hash-based
```
Counter-based (Chosen):
✅ Shorter URLs (6-7 characters)
✅ Sequential, predictable length
✅ Easy to implement
❌ Single point of failure (counter)
❌ Guessable URLs

Hash-based:
✅ No single point of failure
✅ Non-guessable URLs
❌ Longer URLs (8-10 characters)
❌ Collision handling needed
```

### 2. Database Choices

#### SQL vs NoSQL
```
MySQL (Chosen):
✅ ACID compliance
✅ Strong consistency
✅ Complex queries support
❌ Horizontal scaling complexity

Cassandra:
✅ Horizontal scaling
✅ High availability
✅ Fast writes
❌ Eventual consistency
❌ Limited query capabilities
```

### 3. Caching Strategies

#### Write-through vs Write-behind
```
Write-through (Chosen):
✅ Data consistency
✅ Simple implementation
❌ Higher write latency

Write-behind:
✅ Lower write latency
✅ Better performance
❌ Data loss risk
❌ Complexity
```

## Future Enhancements

1. **Machine Learning Features**
   - Predictive caching based on access patterns
   - Fraud detection for malicious URLs
   - Personalized short URL recommendations

2. **Advanced Analytics**
   - Real-time click heatmaps
   - A/B testing for short URLs
   - Conversion tracking integration

3. **Enterprise Features**
   - White-label solutions
   - API rate limiting tiers
   - Advanced security features (2FA, SSO)

4. **Mobile and IoT Integration**
   - QR code generation
   - Deep linking support
   - IoT device URL shortening

## Interview Discussion Points

### 1. **Scalability Questions**
- How to handle 10x traffic growth?
- Database sharding strategies
- Caching layer optimization

### 2. **System Design Trade-offs**
- Consistency vs availability
- Read vs write optimization
- Cost vs performance

### 3. **Real-world Challenges**
- URL spam and abuse prevention
- Geographic distribution
- Disaster recovery planning

This URL shortener design demonstrates comprehensive understanding of distributed systems, scalability patterns, and real-world implementation challenges while maintaining simplicity and performance. 