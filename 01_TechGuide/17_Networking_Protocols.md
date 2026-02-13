# Section 17: Networking & Protocols for Engineers

> **Level**: MID+ (HTTP, DNS basics) to LEAD (HTTP/2 internals, TLS, gRPC, connection pooling)
> **Why This Matters**: System Design interviews require deep networking knowledge. You can't design a chat system without understanding WebSockets, or a CDN without understanding DNS and TLS. This is the invisible foundation of every distributed system.

---

## 17.1 OSI Model & TCP/IP — Quick Reference

### The Deep Dive & Solution

```
Layer 7 — Application   : HTTP, HTTPS, gRPC, WebSocket, DNS, SMTP
Layer 6 — Presentation  : TLS/SSL encryption, data serialization (JSON, Protobuf)
Layer 5 — Session        : Session management, connection establishment
Layer 4 — Transport      : TCP (reliable, ordered), UDP (fast, unreliable)
Layer 3 — Network        : IP addressing, routing (IPv4, IPv6)
Layer 2 — Data Link      : Ethernet, MAC addresses, switches
Layer 1 — Physical       : Cables, radio signals, fiber optics
```

**Interview tip**: You only need Layers 4-7 for software engineering interviews. Below that is network engineering.

#### TCP vs UDP — When to Use Each

```
| Feature             | TCP                          | UDP                           |
|--------------------|------------------------------|-------------------------------|
| Connection         | Connection-oriented (3-way)  | Connectionless                |
| Reliability        | Guaranteed delivery, ordering| No guarantees                 |
| Flow control       | Yes (sliding window)         | No                            |
| Congestion control | Yes (slow start, AIMD)       | No                            |
| Overhead           | High (20-byte header minimum)| Low (8-byte header)           |
| Use cases          | HTTP, databases, file transfer| Video streaming, DNS, gaming  |
| Latency            | Higher (handshake, ACKs)     | Lower (fire and forget)       |
```

#### TCP Three-Way Handshake

```
Client                    Server
  |                         |
  |--- SYN (seq=x) ------->|   Step 1: Client sends SYN (synchronize)
  |                         |
  |<-- SYN-ACK (seq=y,      |   Step 2: Server responds with SYN-ACK
  |     ack=x+1) ----------|
  |                         |
  |--- ACK (ack=y+1) ----->|   Step 3: Client sends ACK → Connection established
  |                         |
  |<==== DATA FLOW ========>|   Now data can flow bidirectionally
```

**Connection termination (four-way):**
```
Client                    Server
  |--- FIN --------------->|   Client wants to close
  |<-- ACK ---------------|   Server acknowledges
  |<-- FIN ----------------|   Server also wants to close
  |--- ACK --------------->|   Client acknowledges → Connection closed
```

**Interview question: What is TIME_WAIT?**
After sending the final ACK, the client enters `TIME_WAIT` state for 2×MSL (Maximum Segment Lifetime, typically 60 seconds). This ensures:
1. Delayed packets from the old connection don't interfere with a new connection on the same port
2. The final ACK has time to reach the server (if lost, server retransmits FIN)

**Production impact**: High-throughput servers can run out of ports due to `TIME_WAIT`. Solutions:
- Enable `SO_REUSEADDR` / `SO_REUSEPORT`
- Use connection pooling
- Tune `tcp_tw_reuse` in Linux kernel

---

## 17.2 HTTP Deep Dive — HTTP/1.1, HTTP/2, HTTP/3

### HTTP/1.1 — The Baseline

```
Problems with HTTP/1.1:
1. Head-of-line blocking: One slow request blocks all requests on that connection
2. No multiplexing: One request-response per connection at a time
3. Workaround: Browsers open 6 parallel connections per domain
4. Redundant headers: Same headers sent with every request (cookies, user-agent, etc.)
5. Text-based: Headers are human-readable but verbose

Connection: keep-alive (default since HTTP/1.1)
- Reuses TCP connection for multiple requests
- But still sequential within a connection
```

### HTTP/2 — Multiplexing & Server Push

```
Key improvements over HTTP/1.1:

1. MULTIPLEXING — Multiple requests/responses on a SINGLE TCP connection
   - Each request is a "stream" with a unique ID
   - Streams are interleaved on the same connection
   - No head-of-line blocking at the HTTP level

2. BINARY FRAMING — Not text-based
   - Headers and data sent as binary frames
   - More efficient to parse

3. HEADER COMPRESSION (HPACK)
   - Static table: 61 commonly used header fields
   - Dynamic table: per-connection header cache
   - Huffman encoding for header values
   - First request: full headers. Subsequent: only differences

4. STREAM PRIORITIZATION
   - Clients can assign priority/weight to streams
   - Server uses these hints to allocate resources

5. SERVER PUSH (deprecated in practice)
   - Server proactively sends resources the client will need
   - Example: Push CSS/JS when HTML is requested
   - Removed in Chrome 106 — cache invalidation issues

Impact on System Design:
- gRPC uses HTTP/2 for bidirectional streaming
- Less need for domain sharding workarounds
- Connection pooling becomes even more important
- Load balancers must be HTTP/2-aware (L7)
```

### HTTP/3 — QUIC (UDP-Based)

```
HTTP/3 runs on QUIC (Quick UDP Internet Connections) instead of TCP:

1. NO TCP HEAD-OF-LINE BLOCKING
   - TCP: one lost packet blocks ALL streams (even those not affected)
   - QUIC: only the affected stream is blocked

2. FASTER CONNECTION SETUP
   - TCP + TLS: 3 round-trips (TCP handshake + TLS handshake)
   - QUIC: 1 round-trip (TLS 1.3 built into QUIC)
   - 0-RTT: For returning connections, data sent in the FIRST packet

3. CONNECTION MIGRATION
   - TCP: connection tied to IP:port (breaks on Wi-Fi → cellular switch)
   - QUIC: connection identified by a Connection ID (survives network changes)

4. BUILT-IN ENCRYPTION
   - TLS 1.3 is mandatory — no unencrypted QUIC connections

When to mention HTTP/3 in interviews:
- Mobile applications (connection migration)
- Global applications (faster connection setup)
- High-latency networks (reduced round trips)
- Real-time applications (no head-of-line blocking)
```

### HTTP Comparison Table

```
| Feature                  | HTTP/1.1        | HTTP/2          | HTTP/3 (QUIC)   |
|-------------------------|-----------------|-----------------|-----------------|
| Transport               | TCP             | TCP             | UDP (QUIC)      |
| Multiplexing            | No              | Yes             | Yes             |
| Header compression      | No              | HPACK           | QPACK           |
| HOL blocking            | Yes (HTTP+TCP)  | TCP-level only  | No              |
| Connection setup        | TCP + TLS (3 RT)| TCP + TLS (3 RT)| 1 RT (0-RTT)   |
| Connection migration    | No              | No              | Yes             |
| Encryption              | Optional (HTTPS)| Optional        | Mandatory       |
| Binary                  | No (text)       | Yes             | Yes             |
| Adoption (2026)         | Legacy          | ~60% of web     | ~30% of web     |
```

---

## 17.3 TLS/SSL — Encryption in Transit

### The Deep Dive & Solution

```
TLS 1.3 Handshake (simplified):

Client                              Server
  |                                    |
  |--- ClientHello ------------------>|   Supported cipher suites, random, key share
  |                                    |
  |<-- ServerHello, Certificate,       |   Selected cipher suite, certificate,
  |    CertificateVerify, Finished ---|   server key share, finished
  |                                    |
  |--- Finished --------------------->|   Client confirms
  |                                    |
  |<========= Encrypted Data ========>|   Application data flows

TLS 1.3 improvements over TLS 1.2:
- 1-RTT handshake (TLS 1.2: 2-RTT)
- 0-RTT resumption for returning clients
- Removed insecure algorithms (RC4, SHA-1, RSA key exchange)
- All ciphers use AEAD (Authenticated Encryption with Associated Data)
- Forward secrecy is MANDATORY (Diffie-Hellman only)
```

**Interview keywords**: Forward Secrecy, AEAD, Certificate Pinning, mTLS, OCSP Stapling

#### mTLS (Mutual TLS) — Service-to-Service Authentication

```
Standard TLS: Only the SERVER presents a certificate
Mutual TLS: BOTH client and server present certificates

Use case: Microservice-to-microservice authentication
- Service mesh (Istio, Linkerd) uses mTLS automatically
- No need for JWT/API keys between internal services
- Certificate rotation handled by the mesh

In Kubernetes with Istio:
- Istio sidecar (Envoy proxy) handles mTLS transparently
- Application code doesn't need to change
- PeerAuthentication policy enforces mTLS
```

---

## 17.4 DNS — Domain Name System

### The Deep Dive & Solution

```
DNS Resolution Flow (what happens when you type google.com):

Browser Cache → OS Cache → Router Cache → ISP DNS → Root DNS → TLD DNS → Authoritative DNS

1. Browser checks its DNS cache
2. OS checks /etc/hosts and its cache (resolved)
3. Query goes to configured DNS resolver (usually ISP or 8.8.8.8)
4. Resolver asks Root nameserver → "Who handles .com?"
5. Root says → TLD nameserver for .com
6. TLD says → Authoritative nameserver for google.com
7. Authoritative returns → IP address 142.250.80.46
8. Resolver caches the result (TTL = Time To Live)
```

#### DNS Record Types (System Design Interview Essential)

```
| Record | Purpose                    | Example                              |
|--------|----------------------------|--------------------------------------|
| A      | Domain → IPv4 address      | example.com → 93.184.216.34          |
| AAAA   | Domain → IPv6 address      | example.com → 2606:2800:220:1:...    |
| CNAME  | Alias to another domain    | www.example.com → example.com        |
| MX     | Mail server for domain     | example.com → mail.example.com       |
| NS     | Nameserver for domain      | example.com → ns1.example.com        |
| TXT    | Arbitrary text (SPF, DKIM) | example.com → "v=spf1 include:..."   |
| SRV    | Service discovery          | _http._tcp.example.com → ...         |
| PTR    | Reverse lookup (IP → name) | 34.216.184.93 → example.com          |
```

#### DNS in System Design

```
Global Load Balancing with DNS:
1. GeoDNS: Return different IPs based on client location
   - US users → us-east-1 IP
   - EU users → eu-west-1 IP

2. Weighted DNS: Distribute traffic across servers
   - Server A (weight 70) → 70% of traffic
   - Server B (weight 30) → 30% of traffic

3. Health-checked DNS: Remove unhealthy servers
   - AWS Route 53 health checks
   - Failover routing: primary → secondary

Limitations:
- TTL caching: DNS changes take time to propagate (minutes to hours)
- No connection-level load balancing (just IP resolution)
- Clients may cache aggressively (ignoring TTL)
```

---

## 17.5 WebSocket Protocol

### The Deep Dive & Solution

```
WebSocket provides FULL-DUPLEX communication over a single TCP connection.

HTTP: Request → Response (client initiates)
WebSocket: Bidirectional (both sides can send anytime)

Handshake (upgrades from HTTP):

Client → Server:
GET /chat HTTP/1.1
Host: server.example.com
Upgrade: websocket
Connection: Upgrade
Sec-WebSocket-Key: dGhlIHNhbXBsZSBub25jZQ==
Sec-WebSocket-Version: 13

Server → Client:
HTTP/1.1 101 Switching Protocols
Upgrade: websocket
Connection: Upgrade
Sec-WebSocket-Accept: s3pPLMBiTxaQ9kYGzzhZRbK+xOo=

After handshake: persistent bidirectional connection (no more HTTP overhead)
```

```java
// Spring Boot WebSocket with STOMP:
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic", "/queue");  // Server → Client
        config.setApplicationDestinationPrefixes("/app"); // Client → Server
    }
    
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOrigins("https://example.com")
                .withSockJS(); // Fallback for old browsers
    }
}

@Controller
public class ChatController {
    
    @MessageMapping("/chat.send")     // Client sends to /app/chat.send
    @SendTo("/topic/messages")         // Broadcast to all subscribers
    public ChatMessage sendMessage(ChatMessage message) {
        return message;
    }
    
    // Send to specific user:
    @Autowired private SimpMessagingTemplate template;
    
    public void sendToUser(String userId, Notification notification) {
        template.convertAndSendToUser(userId, "/queue/notifications", notification);
    }
}
```

**WebSocket vs SSE vs Long Polling:**

```
| Feature            | WebSocket           | SSE (Server-Sent Events) | Long Polling    |
|-------------------|---------------------|--------------------------|-----------------|
| Direction         | Bidirectional       | Server → Client only     | Simulated bidir |
| Protocol          | ws:// / wss://      | HTTP                     | HTTP            |
| Connection        | Persistent          | Persistent               | Repeated        |
| Binary data       | Yes                 | No (text only)           | Yes             |
| Auto-reconnect    | No (manual)         | Yes (built-in)           | Manual          |
| Proxy-friendly    | Sometimes issues    | Yes (standard HTTP)      | Yes             |
| Use case          | Chat, gaming        | Notifications, feeds     | Legacy fallback |
| Spring support    | @MessageMapping     | SseEmitter, Flux<SSE>    | DeferredResult  |
```

---

## 17.6 gRPC & Protocol Buffers

### The Deep Dive & Solution

```
gRPC = Google Remote Procedure Call
- Uses HTTP/2 for transport (multiplexing, streaming)
- Uses Protocol Buffers for serialization (10x smaller than JSON)
- Supports 4 communication patterns:
  1. Unary (like REST: request → response)
  2. Server streaming (one request → stream of responses)
  3. Client streaming (stream of requests → one response)
  4. Bidirectional streaming (stream ↔ stream)
```

```protobuf
// order.proto — Define the API contract
syntax = "proto3";
package com.example.order;

service OrderService {
    // Unary
    rpc GetOrder (GetOrderRequest) returns (OrderResponse);
    
    // Server streaming — watch order status changes
    rpc WatchOrderStatus (GetOrderRequest) returns (stream OrderStatusUpdate);
    
    // Client streaming — batch upload orders
    rpc BatchCreateOrders (stream CreateOrderRequest) returns (BatchCreateResponse);
    
    // Bidirectional streaming — real-time order processing
    rpc ProcessOrders (stream OrderEvent) returns (stream ProcessingResult);
}

message GetOrderRequest {
    string order_id = 1;
}

message OrderResponse {
    string order_id = 1;
    string customer_name = 2;
    double amount = 3;
    OrderStatus status = 4;
    google.protobuf.Timestamp created_at = 5;
}

enum OrderStatus {
    PENDING = 0;
    CONFIRMED = 1;
    SHIPPED = 2;
    DELIVERED = 3;
    CANCELLED = 4;
}
```

**gRPC vs REST decision:**

```
| Factor           | gRPC                          | REST                        |
|-----------------|-------------------------------|------------------------------|
| Serialization   | Protobuf (binary, compact)    | JSON (text, human-readable) |
| Performance     | ~10x faster (binary + HTTP/2) | Adequate for most use cases |
| Streaming       | Native (4 patterns)           | SSE/WebSocket (separate)    |
| Browser support | Limited (needs grpc-web proxy) | Universal                   |
| Code generation | Required (from .proto)        | Optional (OpenAPI)          |
| Documentation   | .proto IS the documentation   | Swagger/OpenAPI             |
| Tooling         | Good (but less than REST)     | Excellent (Postman, curl)   |
| Learning curve  | Higher                        | Lower                       |

When to use gRPC:
- Internal microservice-to-microservice communication
- High-throughput, low-latency requirements
- Streaming data (real-time updates, event streams)
- Polyglot environments (Java, Go, Python, etc.)

When to use REST:
- Public APIs (browser clients)
- Simple CRUD operations
- Third-party integrations
- When human readability matters
```

---

## 17.7 CDN (Content Delivery Network)

### The Deep Dive & Solution

```
CDN Architecture:

User (Mumbai) → CDN Edge (Mumbai POP) → [Cache HIT? Return cached content]
                                        → [Cache MISS? Fetch from Origin (us-east-1)]

Key CDN concepts for System Design:
1. POP (Point of Presence): Edge server location (100+ worldwide)
2. Origin: Your actual server where content lives
3. Cache-Control headers: Determine what/how long to cache
4. Invalidation: Purge cached content when it changes
5. Edge Computing: Run code at the edge (Cloudflare Workers, Lambda@Edge)
```

```
Cache-Control headers:

Cache-Control: public, max-age=31536000    # Cache for 1 year (static assets)
Cache-Control: private, max-age=0          # Don't cache in shared caches
Cache-Control: no-store                     # Never cache (sensitive data)
Cache-Control: no-cache                     # Cache but revalidate every time
Cache-Control: stale-while-revalidate=60   # Serve stale for 60s while refreshing

ETag (content fingerprint):
ETag: "33a64df5"                           # Server sends with response
If-None-Match: "33a64df5"                  # Client sends — if match, 304 Not Modified
```

---

## 17.8 Connection Pooling

### The Deep Dive & Solution

```java
// Problem: Creating a new TCP connection for every request is expensive
// TCP handshake (1.5 RTT) + TLS handshake (1 RTT) = 2.5 RTT overhead

// Solution: Maintain a pool of reusable connections

// HikariCP — Database connection pool (Spring Boot default)
spring:
  datasource:
    hikari:
      maximum-pool-size: 20        # Max connections in pool
      minimum-idle: 5              # Min idle connections maintained
      connection-timeout: 20000    # Max wait for connection (ms)
      idle-timeout: 300000         # Max time a connection can sit idle (ms)
      max-lifetime: 1200000        # Max lifetime of a connection (ms)
      leak-detection-threshold: 5000 # Log warning if connection not returned in 5s

// Sizing formula (from HikariCP wiki):
// connections = ((core_count * 2) + effective_spindle_count)
// For SSD: connections = core_count * 2 + 1
// Example: 4-core server → 9 connections is optimal
// More connections = more context switching = SLOWER (counterintuitive!)
```

```java
// HTTP connection pooling — Apache HttpClient / OkHttp
// Spring RestTemplate / WebClient use connection pools internally

// RestTemplate with connection pool:
@Bean
public RestTemplate restTemplate() {
    var connectionManager = new PoolingHttpClientConnectionManager();
    connectionManager.setMaxTotal(200);           // Total connections across all routes
    connectionManager.setDefaultMaxPerRoute(20);  // Max connections per host
    
    var httpClient = HttpClients.custom()
        .setConnectionManager(connectionManager)
        .setKeepAliveStrategy((response, context) -> 30_000) // 30s keep-alive
        .build();
    
    var factory = new HttpComponentsClientHttpRequestFactory(httpClient);
    factory.setConnectTimeout(5000);
    factory.setReadTimeout(10000);
    
    return new RestTemplate(factory);
}
```

---

## 17.9 Interview Quick Reference — Networking

### Top Questions and One-Line Answers

| Question | Answer |
|----------|--------|
| TCP vs UDP? | TCP: reliable, ordered, connection-oriented. UDP: fast, unreliable, connectionless. |
| HTTP/1.1 vs HTTP/2? | HTTP/2: multiplexing, binary, header compression, single connection. |
| HTTP/2 vs HTTP/3? | HTTP/3: QUIC (UDP), no TCP HOL blocking, 0-RTT, connection migration. |
| What is TLS? | Transport Layer Security — encrypts data in transit. TLS 1.3 is current. |
| What is mTLS? | Mutual TLS — both client AND server present certificates. Used in service mesh. |
| How DNS works? | Browser → OS → Resolver → Root → TLD → Authoritative → returns IP. |
| WebSocket vs HTTP? | WebSocket: persistent, bidirectional. HTTP: request-response, client-initiated. |
| When to use gRPC? | Internal microservices, high throughput, streaming, polyglot environments. |
| What is a CDN? | Network of edge servers that cache content close to users for lower latency. |
| What is connection pooling? | Reuse TCP connections instead of creating new ones per request. |
| TCP TIME_WAIT? | After closing, socket waits 2×MSL (60s) to ensure delayed packets don't interfere. |
| What is keep-alive? | Reuse TCP connection for multiple HTTP requests (default in HTTP/1.1+). |

### Keywords to Use in Interviews

```
Transport: TCP 3-way handshake, Sliding Window, Congestion Control, Slow Start,
           TIME_WAIT, Connection Pooling, Keep-Alive, Nagle's Algorithm

HTTP: Multiplexing, HPACK, QPACK, Binary Framing, Stream Priority,
      0-RTT, Connection Migration, QUIC

Security: TLS 1.3, Forward Secrecy, AEAD, Certificate Pinning, mTLS,
          OCSP Stapling, Certificate Rotation, Let's Encrypt

DNS: GeoDNS, TTL, CNAME, A Record, NS Record, Anycast,
     Route 53, Health Check, Failover Routing

Protocols: gRPC, Protocol Buffers, Server-Sent Events, WebSocket,
           STOMP, SockJS, GraphQL, REST
```

