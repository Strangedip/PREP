# Section 00B: Web & HTTP Fundamentals

> **Level**: ALL — Required before REST APIs, Angular, and system design
> **Depth**: Standard (foundational explanations with request/response walkthroughs)
> **Complements**: [04_API_Design_REST.md](./04_API_Design_REST.md), [17_Networking_Protocols.md](./17_Networking_Protocols.md)

> **You are here**: Fresher — Technical Skills
> **Roadmap**: [Developer Master Roadmap](../ROADMAP.md) | **Prerequisites**: [00_Java_OOP_Fundamentals.md](00_Java_OOP_Fundamentals.md) | **Next**: [35_SQL_Fundamentals.md](./35_SQL_Fundamentals.md)

---

## Why web fundamentals matter

Every backend and full-stack interview assumes you understand **how a browser talks to a server**. Spring Boot REST controllers, Angular HTTP clients, JWT auth, and system design all build on HTTP. This section explains the full journey from typing a URL to receiving JSON.

---

## 00B.1 How the Web Works — End to End

### What happens when you visit `https://api.example.com/v1/users`

```
1. Browser parses URL
2. DNS lookup: api.example.com → 203.0.113.42 (IP address)
3. TCP handshake: SYN → SYN-ACK → ACK (reliable connection)
4. TLS handshake: encrypt channel (HTTPS)
5. HTTP request sent over encrypted connection
6. Server processes request, returns HTTP response
7. Browser/client parses response (JSON, HTML, etc.)
```

### Key terms explained

| Term | What it is | Example |
|------|------------|---------|
| **URL** | Address of a resource | `https://api.example.com/v1/users?page=1` |
| **DNS** | Phone book of the internet — domain → IP | `google.com` → `142.250.x.x` |
| **TCP** | Reliable transport — guarantees delivery order | Underlies HTTP |
| **HTTPS** | HTTP + TLS encryption | Padlock in browser; required for production |
| **Cookie** | Small key-value stored by browser, sent with requests | `session_id=abc123` |
| **Session** | Server-side user state | Shopping cart, login state |
| **JWT** | Self-contained signed token (stateless session) | `Bearer eyJhbG...` |

### Anatomy of a URL

```
https://api.example.com:443/v1/users?page=2&limit=20#section
└─┬─┘   └──────┬──────┘└┬┘ └────┬────┘ └──────┬──────┘ └──┬──┘
scheme      host       port   path        query       fragment
```

---

## 00B.2 HTTP Request and Response Structure

### HTTP Request

```http
GET /v1/users/42 HTTP/1.1
Host: api.example.com
Authorization: Bearer eyJhbGciOiJIUzI1NiIs...
Accept: application/json
Content-Type: application/json

(no body for GET)
```

| Part | Purpose |
|------|---------|
| **Request line** | Method + path + HTTP version |
| **Headers** | Metadata (auth, content type, caching) |
| **Body** | Data sent to server (POST, PUT, PATCH) — usually JSON |

### HTTP Response

```http
HTTP/1.1 200 OK
Content-Type: application/json
Cache-Control: max-age=3600

{"id": 42, "name": "Alice", "email": "alice@example.com"}
```

| Part | Purpose |
|------|---------|
| **Status line** | HTTP version + status code + reason phrase |
| **Headers** | Metadata (content type, caching, cookies) |
| **Body** | Response payload |

---

## 00B.3 HTTP Methods & Idempotency

| Method | Purpose | Has body? | Idempotent? | Safe? |
|--------|---------|-----------|-------------|-------|
| **GET** | Read resource | No | Yes | Yes |
| **POST** | Create resource | Yes | No | No |
| **PUT** | Replace entire resource | Yes | Yes | No |
| **PATCH** | Partial update | Yes | No* | No |
| **DELETE** | Remove resource | Rarely | Yes | No |

**Idempotent**: Calling the same request multiple times has the **same effect** as calling it once.

```
PUT /users/42  { "name": "Alice" }   → name is Alice
PUT /users/42  { "name": "Alice" }   → still Alice (same result)

POST /users  { "name": "Bob" }       → creates user id=43
POST /users  { "name": "Bob" }       → creates ANOTHER user id=44 (not idempotent!)
```

**Safe**: Does not modify server state. Only GET is safe.

**Why idempotency matters**: Retries. If a network timeout occurs, you can safely retry GET, PUT, DELETE. Retrying POST may create duplicates — use idempotency keys in production APIs.

---

## 00B.4 Status Codes — Know These Cold

### 2xx Success

| Code | Meaning | When to use |
|------|---------|-------------|
| **200 OK** | Success with body | GET, PUT, PATCH success |
| **201 Created** | Resource created | POST success — include `Location` header |
| **204 No Content** | Success, no body | DELETE success |

### 4xx Client Error (caller fixable)

| Code | Meaning | When to use |
|------|---------|-------------|
| **400 Bad Request** | Malformed input | Validation failed, invalid JSON |
| **401 Unauthorized** | Not authenticated | Missing or invalid token |
| **403 Forbidden** | Authenticated but not allowed | User lacks permission |
| **404 Not Found** | Resource doesn't exist | Wrong ID or deleted resource |
| **409 Conflict** | State conflict | Duplicate email, version mismatch |
| **422 Unprocessable Entity** | Semantic validation error | Valid JSON but business rule failed |
| **429 Too Many Requests** | Rate limited | Include `Retry-After` header |

### 5xx Server Error (your bug or infra)

| Code | Meaning | When to use |
|------|---------|-------------|
| **500 Internal Server Error** | Unhandled exception | Bug in code — fix and deploy |
| **502 Bad Gateway** | Upstream server failed | Load balancer can't reach backend |
| **503 Service Unavailable** | Temporarily down | Maintenance, overload — retry later |
| **504 Gateway Timeout** | Upstream too slow | Downstream service timeout |

### Interview favorites

| Question | Answer |
|----------|--------|
| **401 vs 403?** | 401: "Who are you?" — not logged in or bad token. 403: "I know who you are, but you can't do this." |
| **When 404 vs 400?** | 404: resource ID doesn't exist. 400: request format is wrong. |
| **When 409?** | Optimistic locking failure, duplicate unique key, conflicting state. |

---

## 00B.5 Headers You Should Know

| Header | Direction | Purpose |
|--------|-----------|---------|
| `Content-Type` | Request/Response | Body format: `application/json`, `text/html` |
| `Accept` | Request | What response formats client accepts |
| `Authorization` | Request | `Bearer <JWT>` or `Basic base64(user:pass)` |
| `Cache-Control` | Response | `max-age=3600`, `no-cache`, `no-store` |
| `ETag` | Response | Resource version hash for conditional requests |
| `If-None-Match` | Request | Send cached ETag; server returns 304 if unchanged |
| `Cookie` / `Set-Cookie` | Both | Session tracking |
| `Origin` | Request | CORS — where the browser request originated |
| `Access-Control-Allow-Origin` | Response | CORS — which origins may read response |
| `X-Request-ID` / `traceparent` | Request | Distributed tracing correlation |
| `Location` | Response | URL of created resource (with 201) |
| `Retry-After` | Response | Seconds until client should retry (429, 503) |

---

## 00B.6 Authentication — Session, JWT, OAuth2

### Session-based (stateful)

```
1. User logs in → server creates session, stores in DB/Redis
2. Server sends Set-Cookie: session_id=abc123
3. Browser sends Cookie: session_id=abc123 on every request
4. Server looks up session → knows who you are
```

**Pros**: Easy to invalidate (delete session). **Cons**: Server must store sessions; harder to scale across regions.

### JWT (stateless)

```
1. User logs in → server signs token with secret/private key
2. Token = Header.Payload.Signature (Base64)
3. Client sends Authorization: Bearer <token> on every request
4. Server verifies signature, reads claims (userId, roles) — no DB lookup
```

**Payload example** (decoded):
```json
{
  "sub": "user-42",
  "roles": ["USER", "ADMIN"],
  "exp": 1712345678
}
```

**Pros**: Stateless, scales horizontally. **Cons**: Hard to revoke before expiry (need blocklist or short TTL + refresh tokens).

### OAuth2 (delegated auth)

"Login with Google" — user authenticates with Google; your app receives an access token to call Google APIs or establish identity.

| Flow | Use case |
|------|----------|
| Authorization Code | Web apps (most common) |
| Client Credentials | Service-to-service |
| PKCE | Mobile/SPA without client secret |

### Comparison

| Approach | Stateful? | Revocation | Scale |
|----------|-----------|------------|-------|
| Session cookie | Yes | Easy | Needs shared session store |
| JWT | No | Hard (short TTL helps) | Excellent |
| OAuth2 | Depends | Provider-managed | Excellent for third-party login |

---

## 00B.7 CORS — Why Your Frontend Can't Call the API

**Same-Origin Policy**: Browsers block JavaScript from reading responses from a different origin (scheme + host + port).

```
Frontend: https://app.example.com
API:      https://api.example.com   ← different host = cross-origin
```

**Fix**: API server returns CORS headers:

```http
Access-Control-Allow-Origin: https://app.example.com
Access-Control-Allow-Methods: GET, POST, PUT, DELETE
Access-Control-Allow-Headers: Content-Type, Authorization
```

**Preflight**: For non-simple requests (PUT, custom headers), browser sends `OPTIONS` first to check permissions.

Deep dive: [§12 Security](./12_Security_OWASP_Cloud.md)

---

## 00B.8 REST API Design at a Glance

REST (Representational State Transfer) treats server data as **resources** identified by URLs.

### Resource naming

```
GET    /v1/users           → list users
POST   /v1/users           → create user
GET    /v1/users/42        → get user 42
PUT    /v1/users/42        → replace user 42
PATCH  /v1/users/42        → partial update
DELETE /v1/users/42        → delete user 42
GET    /v1/users/42/orders → nested collection
```

### Best practices

| Practice | Example |
|----------|---------|
| Use nouns, not verbs | `/users` not `/getUsers` |
| Version in URL or header | `/v1/users` or `Accept: application/vnd.api.v1+json` |
| Pagination | `?page=2&limit=20` or cursor `?cursor=abc123` |
| Filtering | `?status=active&role=admin` |
| Consistent error format | `{"error": "VALIDATION_FAILED", "message": "...", "fields": [...]}` |

Full guide: [04_API_Design_REST.md](./04_API_Design_REST.md)

---

## 00B.9 HTTPS and TLS (Brief)

HTTP sends data in **plain text** — anyone on the network can read it. HTTPS wraps HTTP in **TLS encryption**.

```
Client ──[TLS handshake]── Server
         Agree cipher, verify certificate
Client ──[encrypted HTTP]── Server
```

**Certificate**: Proves server identity (signed by Certificate Authority). Browser warns if invalid.

**Interview answer**: "HTTPS = HTTP + TLS. Encrypts data in transit. Does not encrypt data at rest — that's a separate concern."

---

## 00B.10 Interview Quick Reference (full answers)

| Question | Answer |
|----------|--------|
| **GET vs POST?** | GET reads data, is safe and cacheable, no body. POST creates or submits data, not idempotent, has body. |
| **401 vs 403?** | 401: authentication failed or missing. 403: authenticated but lacks authorization for this action. |
| **Cookie vs JWT?** | Cookie often carries session ID (server stores state). JWT is self-contained signed token (stateless). |
| **What is HTTPS?** | HTTP over TLS — encrypts request/response in transit, verifies server identity via certificate. |
| **Idempotent methods?** | GET, PUT, DELETE. POST and PATCH are generally not. |
| **What is CORS?** | Browser security policy. Server must explicitly allow cross-origin requests via response headers. |
| **What happens on DNS failure?** | Browser cannot resolve IP → connection fails before HTTP. |
| **REST vs GraphQL?** | REST: multiple endpoints, server controls shape. GraphQL: single endpoint, client queries exact fields needed. |

---

## Hands-on exercise

Trace this flow on paper:

1. User clicks "Login" on Angular app at `app.example.com`
2. App sends `POST https://api.example.com/v1/auth/login` with `{email, password}`
3. Server validates, returns JWT + `200 OK`
4. App stores JWT, sends `GET /v1/profile` with `Authorization: Bearer ...`
5. Server returns user profile JSON

Label: method, status code, headers used, where auth happens.

**Next**: [04_API_Design_REST.md](./04_API_Design_REST.md) → [08_Angular_Frontend_Engineering.md](./08_Angular_Frontend_Engineering.md) → [01_Modern_Java_Features.md](./01_Modern_Java_Features.md).
