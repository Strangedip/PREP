# Section 00B: Web & HTTP Fundamentals

> **Level**: ALL — Required before REST APIs, Angular, and system design
> **Complements**: [04_API_Design_REST.md](./04_API_Design_REST.md), [17_Networking_Protocols.md](./17_Networking_Protocols.md)

> **You are here**: Fresher — Technical Skills
> **Roadmap**: [Developer Master Roadmap](../ROADMAP.md) | **Prerequisites**: [00_TableOfContents.md](00_TableOfContents.md) | **Next**: [01_Modern_Java_Features.md](01_Modern_Java_Features.md)

---

## 00B.1 How the Web Works

```
Browser → DNS (domain → IP) → TCP connection → TLS (HTTPS) → HTTP request → Server → HTTP response
```

| Term | Meaning |
|------|---------|
| **URL** | `https://api.example.com/v1/users?page=1` |
| **DNS** | Resolves `api.example.com` to IP address |
| **HTTPS** | HTTP + TLS encryption |
| **Cookie** | Small data stored by browser, sent with requests |
| **Session** | Server-side user state (or JWT in stateless apps) |

---

## 00B.2 HTTP Methods & Idempotency

| Method | Purpose | Idempotent? | Safe? |
|--------|---------|-------------|-------|
| GET | Read resource | Yes | Yes |
| POST | Create | No | No |
| PUT | Replace resource | Yes | No |
| PATCH | Partial update | No* | No |
| DELETE | Remove | Yes | No |

**Idempotent**: Multiple identical requests = same effect as one (important for retries).

---

## 00B.3 Status Codes (Must Know)

| Code | Meaning | When |
|------|---------|------|
| 200 | OK | Success |
| 201 | Created | POST succeeded |
| 204 | No Content | DELETE succeeded |
| 400 | Bad Request | Invalid input |
| 401 | Unauthorized | Not authenticated |
| 403 | Forbidden | Authenticated but not allowed |
| 404 | Not Found | Resource missing |
| 409 | Conflict | Duplicate, version conflict |
| 429 | Too Many Requests | Rate limited |
| 500 | Internal Server Error | Server bug |
| 503 | Service Unavailable | Down / overloaded |

---

## 00B.4 Headers You Should Know

| Header | Purpose |
|--------|---------|
| `Content-Type` | Body format (`application/json`) |
| `Authorization` | `Bearer <JWT>` or Basic auth |
| `Cache-Control` | Caching policy |
| `ETag` | Resource version for conditional GET |
| `Cookie` / `Set-Cookie` | Session cookies |
| `Origin` | CORS — where request came from |
| `X-Request-ID` | Distributed tracing correlation |

---

## 00B.5 Authentication Basics

| Approach | How | Stateless? |
|----------|-----|--------------|
| **Session cookie** | Server stores session; cookie = session ID | No |
| **JWT** | Server signs token; client sends each request | Yes |
| **OAuth2** | Delegate auth to Google/GitHub/etc. | Yes (tokens) |

**JWT structure**: `Header.Payload.Signature` — server verifies signature, reads claims (user id, roles).

---

## 00B.6 CORS (Preview)

Browsers block JS from `app.com` calling `api.other.com` unless server allows it via `Access-Control-Allow-Origin`. Deep dive: [§12 Security](./12_Security_OWASP_Cloud.md).

---

## 00B.7 REST at a Glance

- Resources as URLs: `/users/42`, `/orders/99`
- Use HTTP methods correctly
- JSON bodies for APIs
- Versioning: `/v1/...` or header
- Pagination: `?page=2&limit=20` or cursor

Full guide: [04_API_Design_REST.md](./04_API_Design_REST.md).

---

## 00B.8 Interview Quick Reference

| Question | Answer |
|----------|--------|
| GET vs POST? | GET reads (safe, cacheable). POST creates (not idempotent). |
| 401 vs 403? | 401: not logged in. 403: logged in but forbidden. |
| Cookie vs JWT? | Cookie: server session. JWT: self-contained token, stateless. |
| What is HTTPS? | HTTP over TLS — encrypts data in transit. |
| Idempotent methods? | GET, PUT, DELETE (repeat = same effect). POST is not. |
| CORS? | Browser security; server must allow cross-origin requests. |
