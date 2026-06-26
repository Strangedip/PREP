# Section 12: Application Security, OWASP & Cloud Fundamentals

> **Why This Matters for Lead Engineers**: Security is non-negotiable at every level. Lead engineers are expected to design secure systems from the ground up, conduct threat modeling, ensure OWASP Top 10 compliance, and understand cloud infrastructure (AWS/GCP/Azure) enough to make informed architectural decisions. These topics are increasingly asked in system design rounds and leadership discussions.

---

## 12.1 OWASP Top 10 (2025) — Complete Guide

The [OWASP Top 10:2025](https://owasp.org/Top10/2025/) is the authoritative list of critical web application security risks. Every lead engineer must know these cold. This section is aligned with **2025** rankings and categories while preserving deep-dive material from prior versions where it still applies.

### What Changed from OWASP Top 10 (2021)

| 2025 Rank | Category | Notes vs 2021 |
|-----------|----------|---------------|
| **A01** | Broken Access Control | Still #1. **SSRF is now part of this category** (was A10:2021). |
| **A02** | Security Misconfiguration | Moved up from #5 — cloud/config defaults are a major attack surface. |
| **A03** | Software Supply Chain Failures | **New/expands** A06:2021 Vulnerable Components — dependencies, build pipeline, distribution. |
| **A04** | Cryptographic Failures | Was A02:2021 — same risks, slightly lower rank but still critical. |
| **A05** | Injection | Was A03:2021 — SQL, XSS, command injection, etc. |
| **A06** | Insecure Design | Was A04:2021 — threat modeling, STRIDE, secure design patterns. |
| **A07** | Authentication Failures | Was A07:2021 Identification and Authentication Failures — MFA, session, brute force. |
| **A08** | Software or Data Integrity Failures | Was A08:2021 — CI/CD integrity, deserialization, signed artifacts. |
| **A09** | Security Logging and Alerting Failures | Was A09:2021 — audit logs, monitoring, alerting. |
| **A10** | Mishandling of Exceptional Conditions | **New for 2025** — error handling, fail-open logic, exceptional paths. |

**Interview tip (2025)**: Mention you follow OWASP **2025** — especially supply chain (A03), misconfiguration at #2, and that SSRF is discussed under access control (A01).

---

### A01:2025 — Broken Access Control

**What it is**: Users can act outside their intended permissions. This remains the **#1** most common vulnerability in OWASP 2025 data (~73% of apps had related CWEs).

**Examples**:
- Horizontal privilege escalation: User A accesses User B's data by changing `/api/users/123/orders` to `/api/users/456/orders`
- Vertical privilege escalation: Regular user accesses admin endpoints
- IDOR (Insecure Direct Object Reference): Predictable IDs expose resources
- Missing function-level access control: Admin endpoints accessible without role check
- **SSRF** (Server-Side Request Forgery): Server tricked into requesting internal/metadata URLs — **rolled into A01:2025**

**Prevention in Spring Boot**:
```java
// Method-level security
@PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.id")
@GetMapping("/users/{userId}/orders")
public ResponseEntity<List<Order>> getUserOrders(@PathVariable Long userId) {
    return ResponseEntity.ok(orderService.getOrdersByUserId(userId));
}

// Global security configuration
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    return http
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/api/admin/**").hasRole("ADMIN")
            .requestMatchers("/api/users/{userId}/**").access(
                new WebExpressionAuthorizationManager(
                    "#userId == authentication.principal.id or hasRole('ADMIN')"
                )
            )
            .requestMatchers("/api/public/**").permitAll()
            .anyRequest().authenticated()
        )
        .build();
}
```

**Interview Answer**: "Broken Access Control is still #1 in OWASP 2025. I prevent it using defense-in-depth: method-level security with @PreAuthorize, URL-pattern-based authorization in SecurityFilterChain, and ownership validation in the service layer. I never rely solely on the frontend hiding buttons — the server must enforce authorization on every request. SSRF is now categorized here too, so I also validate outbound URLs and block internal/metadata targets."

#### SSRF Prevention (part of A01:2025)

**What it is**: Attacker tricks the server into making requests to unintended locations (internal services, metadata endpoints, cloud provider APIs).

**Prevention**:
```java
// Validate and sanitize URLs before making server-side requests
public void fetchExternalResource(String url) {
    URI uri = URI.create(url);

    // Block private IP ranges
    InetAddress address = InetAddress.getByName(uri.getHost());
    if (address.isLoopbackAddress() || address.isSiteLocalAddress() || address.isLinkLocalAddress()) {
        throw new SecurityException("Access to internal networks is blocked");
    }

    // Block cloud metadata endpoints
    if (uri.getHost().equals("169.254.169.254")) {
        throw new SecurityException("Access to cloud metadata is blocked");
    }

    // Allowlist approach (preferred)
    if (!ALLOWED_DOMAINS.contains(uri.getHost())) {
        throw new SecurityException("Domain not in allowlist");
    }
}
```

---

### A02:2025 — Security Misconfiguration

**What it is**: Missing security hardening, default configurations, unnecessary features enabled. **Ranked #2 in 2025** — configuration errors grew with cloud-native and multi-service deployments.

**Common Issues and Fixes**:
```yaml
# application.yml — Production Security Hardening

# Disable actuator endpoints in production (or protect them)
management:
  endpoints:
    web:
      exposure:
        include: health,info,prometheus
  endpoint:
    health:
      show-details: never  # Don't expose internal details
    env:
      enabled: false       # Never expose env variables

# Disable stack traces in error responses
server:
  error:
    include-stacktrace: never
    include-message: never

# Secure headers
spring:
  security:
    headers:
      frame-options: DENY
      xss-protection: 1; mode=block
      content-type-options: nosniff
```

**Docker Security**:
```dockerfile
# Use non-root user
FROM eclipse-temurin:21-jre-alpine
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser
COPY --chown=appuser:appgroup target/app.jar /app/app.jar
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
```

**Additional 2025 focus**: Lock down cloud IAM defaults, disable unused API endpoints, secure K8s RBAC and NetworkPolicies, rotate default credentials, and audit Terraform/Helm charts in CI.

---

### A03:2025 — Software Supply Chain Failures

**What it is**: Compromised or vulnerable dependencies, build pipelines, and artifact distribution — expands the older "Vulnerable Components" category to the full supply chain (OWASP 2025 A03).

**Examples**:
- Known CVEs in transitive Maven/npm dependencies
- Dependency confusion / typosquatting attacks
- Compromised CI/CD publishing malicious artifacts
- Unsigned container images deployed to production

**Prevention**:
```xml
<!-- Maven: OWASP Dependency Check Plugin -->
<plugin>
    <groupId>org.owasp</groupId>
    <artifactId>dependency-check-maven</artifactId>
    <version>9.0.9</version>
    <configuration>
        <failBuildOnCVSS>7</failBuildOnCVSS> <!-- Fail on HIGH severity -->
    </configuration>
</plugin>
```

```yaml
# GitHub Actions: Automated dependency scanning
- name: OWASP Dependency Check
  uses: dependency-check/Dependency-Check_Action@main
  with:
    project: 'my-app'
    path: '.'
    format: 'HTML'

# Also use:
# - Snyk (snyk test)
# - GitHub Dependabot
# - Trivy (container scanning)
# - Cosign / Sigstore for signed images
# - SBOM generation (CycloneDX) in CI
```

**Supply chain checklist**:
- Pin dependency versions; review lockfiles in PRs
- Sign commits; protect `main` with required reviews
- Scan images before deploy; verify image signatures in K8s admission
- Use private artifact registry with least-privilege publish tokens

---

### A04:2025 — Cryptographic Failures

**What it is**: Sensitive data exposed due to weak or missing encryption. Still a top risk in 2025 (A04) — often leads directly to data breach.

**Examples**:
- Passwords stored in plaintext or weak hashes (MD5, SHA1)
- Sensitive data transmitted over HTTP instead of HTTPS
- Weak encryption algorithms (DES, RC4)
- Hardcoded encryption keys in source code

**Prevention**:
```java
// Password hashing with BCrypt (adaptive, salted)
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder(12); // 12 rounds
}

// Data encryption at rest
@Column(name = "ssn")
@Convert(converter = AesEncryptor.class)
private String socialSecurityNumber;

// Force HTTPS
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    return http
        .requiresChannel(channel -> channel.anyRequest().requiresSecure())
        .headers(headers -> headers
            .httpStrictTransportSecurity(hsts -> hsts
                .includeSubDomains(true)
                .maxAgeInSeconds(31536000)
            )
        )
        .build();
}
```

**Key Rules**:
- Use BCrypt or Argon2 for password hashing (never MD5/SHA1)
- Use AES-256-GCM for data encryption at rest
- Use TLS 1.3 for data in transit
- Store secrets in Vault/AWS Secrets Manager, never in code or config files
- Rotate encryption keys regularly

---

### A05:2025 — Injection

**What it is**: Untrusted data sent to an interpreter as part of a command or query.

**Types**: SQL Injection, NoSQL Injection, OS Command Injection, LDAP Injection, XSS (Cross-Site Scripting)

**SQL Injection Prevention**:
```java
// BAD — vulnerable to SQL injection
String query = "SELECT * FROM users WHERE username = '" + username + "'";

// GOOD — parameterized query via JPA
@Query("SELECT u FROM User u WHERE u.username = :username")
Optional<User> findByUsername(@Param("username") String username);

// GOOD — Spring Data JPA derived queries (always parameterized)
Optional<User> findByUsernameAndStatus(String username, UserStatus status);

// GOOD — Native query with parameters
@Query(value = "SELECT * FROM users WHERE email = :email", nativeQuery = true)
Optional<User> findByEmail(@Param("email") String email);
```

**XSS Prevention**:
```java
// Input validation
@PostMapping("/comments")
public ResponseEntity<Comment> createComment(
    @RequestBody @Valid CommentRequest request) {
    // @Valid triggers bean validation
    return ResponseEntity.ok(commentService.create(request));
}

public class CommentRequest {
    @NotBlank
    @Size(max = 1000)
    @Pattern(regexp = "^[\\w\\s.,!?'-]+$", message = "Invalid characters")
    private String content;
}

// Output encoding — handled automatically by Thymeleaf/Angular
// Angular auto-escapes interpolated values: {{ userInput }}
// React auto-escapes JSX expressions: {userInput}
```

**Content Security Policy Header**:
```java
.headers(headers -> headers
    .contentSecurityPolicy(csp -> csp
        .policyDirectives("default-src 'self'; script-src 'self'; style-src 'self' 'unsafe-inline'")
    )
)
```

---

### A06:2025 — Insecure Design

**What it is**: Flaws in the design itself, not just the implementation. This is about missing security requirements and threat modeling.

**Prevention**:
- **Threat Modeling**: Use STRIDE (Spoofing, Tampering, Repudiation, Information Disclosure, Denial of Service, Elevation of Privilege) during design phase
- **Secure Design Patterns**: Use rate limiting, input validation, least privilege, defense in depth
- **Security User Stories**: "As an attacker, I want to... so I can..." during requirements gathering
- **Architecture Decision Records (ADRs)**: Document security decisions and trade-offs

**STRIDE Threat Model Example for a Payment API**:

| Threat | Category | Mitigation |
|--------|----------|-----------|
| Attacker impersonates a user | Spoofing | OAuth2 + JWT, MFA |
| Attacker modifies payment amount | Tampering | Server-side validation, HMAC signatures |
| User denies making a payment | Repudiation | Audit logs, transaction receipts |
| Payment data leaked in logs | Information Disclosure | Log masking, PII redaction |
| DDoS on payment endpoint | Denial of Service | Rate limiting, WAF, circuit breakers |
| Regular user accesses admin functions | Elevation of Privilege | RBAC, method-level security |

---

### A07:2025 — Authentication Failures

**What it is**: Weak authentication mechanisms allowing account compromise.

**Prevention**:
- Implement multi-factor authentication (MFA)
- Use strong password policies (12+ chars, complexity)
- Rate-limit login attempts (prevent brute force)
- Use secure session management (HttpOnly, Secure, SameSite cookies)
- Implement account lockout after failed attempts

```java
// Rate limiting login attempts
@Bean
public RateLimiter loginRateLimiter() {
    return RateLimiter.of("loginRateLimiter",
        RateLimiterConfig.custom()
            .limitForPeriod(5)           // 5 attempts
            .limitRefreshPeriod(Duration.ofMinutes(15))  // per 15 minutes
            .timeoutDuration(Duration.ZERO)
            .build()
    );
}

// Secure session configuration
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    return http
        .sessionManagement(session -> session
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // For JWT
        )
        .build();
}
```

---

### A08:2025 — Software and Data Integrity Failures

**What it is**: Code and infrastructure that does not protect against integrity violations (CI/CD pipeline attacks, insecure deserialization, unsigned updates).

**Prevention**:
- Verify checksums/signatures of downloaded dependencies
- Use signed commits and protected branches
- Implement CI/CD pipeline security (least privilege, approval gates)
- Avoid Java serialization; use JSON with Jackson (type-safe)
- Use Subresource Integrity (SRI) for CDN scripts

---

### A09:2025 — Security Logging and Alerting Failures

**What it is**: Insufficient logging makes it impossible to detect, escalate, and respond to attacks.

**What to Log**:
```java
// Security event logging
@Component
public class SecurityAuditLogger {

    private static final Logger securityLog = LoggerFactory.getLogger("SECURITY_AUDIT");

    public void logAuthSuccess(String username, String ip) {
        securityLog.info("AUTH_SUCCESS user={} ip={} timestamp={}",
            username, ip, Instant.now());
    }

    public void logAuthFailure(String username, String ip, String reason) {
        securityLog.warn("AUTH_FAILURE user={} ip={} reason={} timestamp={}",
            username, ip, reason, Instant.now());
    }

    public void logAccessDenied(String username, String resource, String method) {
        securityLog.warn("ACCESS_DENIED user={} resource={} method={} timestamp={}",
            username, resource, method, Instant.now());
    }

    public void logDataAccess(String username, String entity, Long entityId) {
        securityLog.info("DATA_ACCESS user={} entity={} id={} timestamp={}",
            username, entity, entityId, Instant.now());
    }
}
```

**What MUST be logged**: Login success/failure, access denied, privilege escalation attempts, sensitive data access, configuration changes, API key usage.

**What MUST NOT be logged**: Passwords, credit card numbers, SSNs, session tokens, full request/response bodies with PII.

---

### A10:2025 — Mishandling of Exceptional Conditions

**What it is**: **New category in OWASP 2025.** Security flaws from improper handling of errors, edge cases, and abnormal system states — logic bugs, failing open, leaking internals in exceptions, and unsafe recovery paths.

**Examples**:
- Payment API returns success when downstream processor times out (fail-open)
- Stack traces and internal paths exposed in API error responses
- Race conditions when inventory goes negative under concurrent load
- Retry logic bypasses rate limits or idempotency checks
- "Catch-all" handlers that grant access on any error

**Prevention in Spring Boot**:
```java
// RFC 7807 Problem Details — no stack traces to clients
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleUnexpected(Exception ex, HttpServletRequest request) {
        log.error("Unhandled error path={}", request.getRequestURI(), ex); // log server-side only
        return ProblemDetail.forStatusAndDetail(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "An unexpected error occurred"); // generic client message
    }

    @ExceptionHandler(PaymentTimeoutException.class)
    public ProblemDetail handlePaymentTimeout(PaymentTimeoutException ex) {
        // FAIL CLOSED — do not mark payment as successful
        return ProblemDetail.forStatusAndDetail(
            HttpStatus.SERVICE_UNAVAILABLE,
            "Payment could not be confirmed. Please retry.");
    }
}
```

**Design principles**:
- **Fail closed** on security and financial operations — deny by default on ambiguity
- Never expose stack traces, SQL, or file paths in production API responses
- Test exceptional paths: timeouts, partial failures, duplicate requests, null inputs
- Use idempotency keys for retried operations
- Circuit breakers with safe fallbacks (not silent success)

**Note**: SSRF prevention is under **A01:2025** (Broken Access Control) in the 2025 list — see SSRF section above.

---

## 12.2 JWT Security Deep Dive

### JWT Structure
```
Header.Payload.Signature

Header:  { "alg": "RS256", "typ": "JWT" }
Payload: { "sub": "user123", "role": "ADMIN", "iat": 1707832800, "exp": 1707836400 }
Signature: RSASHA256(base64UrlEncode(header) + "." + base64UrlEncode(payload), privateKey)
```

### Common JWT Vulnerabilities

| Vulnerability | Description | Prevention |
|--------------|-------------|-----------|
| Algorithm confusion | Attacker changes alg from RS256 to HS256, uses public key as HMAC secret | Explicitly set and validate the algorithm server-side |
| None algorithm | Attacker sets alg to "none" to bypass verification | Reject tokens with alg=none |
| Weak secrets | HMAC secret is guessable | Use 256+ bit random secrets, or prefer RS256 |
| Missing expiration | Token never expires | Always set exp claim, use short TTL (15 min) |
| Token leakage | Token stored in localStorage, exposed to XSS | Store in httpOnly cookies or memory only |
| Missing audience/issuer | Token from one service accepted by another | Validate aud and iss claims |

### Secure JWT Implementation

```java
@Component
public class JwtTokenProvider {
    private final RSAPrivateKey privateKey;
    private final RSAPublicKey publicKey;
    private static final long ACCESS_TOKEN_VALIDITY = 15 * 60 * 1000; // 15 minutes
    private static final long REFRESH_TOKEN_VALIDITY = 7 * 24 * 60 * 60 * 1000; // 7 days

    public String generateAccessToken(UserDetails user) {
        return Jwts.builder()
            .setSubject(user.getUsername())
            .claim("roles", user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority).toList())
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_VALIDITY))
            .setIssuer("my-app")
            .setAudience("my-app-api")
            .setId(UUID.randomUUID().toString()) // jti for token revocation
            .signWith(privateKey, SignatureAlgorithm.RS256)
            .compact();
    }

    public Claims validateToken(String token) {
        return Jwts.parserBuilder()
            .setSigningKey(publicKey)
            .requireIssuer("my-app")
            .requireAudience("my-app-api")
            .build()
            .parseClaimsJws(token)
            .getBody();
    }
}
```

---

## 12.3 CORS (Cross-Origin Resource Sharing)

### Why CORS Exists
Browsers enforce the Same-Origin Policy: JavaScript on `app.example.com` cannot make requests to `api.example.com` without explicit permission.

### Spring Boot CORS Configuration

```java
@Bean
public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration config = new CorsConfiguration();
    config.setAllowedOrigins(List.of("https://app.example.com")); // Never use "*" with credentials
    config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH"));
    config.setAllowedHeaders(List.of("Authorization", "Content-Type", "X-Request-ID"));
    config.setExposedHeaders(List.of("X-Total-Count", "X-Request-ID"));
    config.setAllowCredentials(true);
    config.setMaxAge(3600L); // Preflight cache duration

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/api/**", config);
    return source;
}
```

**Interview Answer**: "CORS is a browser security mechanism. I configure it server-side to explicitly whitelist trusted origins, methods, and headers. I never use `*` as the allowed origin when credentials are involved. I set `maxAge` to reduce preflight requests."

---

## 12.4 Cloud Fundamentals for Backend Engineers

> You do not need to be a certified cloud architect, but as a Lead Engineer, you must understand the services you depend on and make informed decisions about infrastructure.

### AWS Core Services for Java/Spring Boot Engineers

| Service | What It Does | When to Use |
|---------|-------------|-------------|
| **EC2** | Virtual servers | Legacy apps, custom runtime needs |
| **ECS/EKS** | Container orchestration (Docker/Kubernetes) | Microservices deployment (EKS for K8s, ECS for simpler) |
| **Lambda** | Serverless functions | Event-driven, low-traffic endpoints, background jobs |
| **RDS** | Managed relational databases (PostgreSQL, MySQL) | Primary database for ACID workloads |
| **DynamoDB** | Managed NoSQL (key-value + document) | High-throughput, low-latency, single-table design |
| **ElastiCache** | Managed Redis/Memcached | Caching, session storage, rate limiting |
| **S3** | Object storage | File uploads, static assets, backups, data lake |
| **SQS** | Managed message queue | Async processing, decoupling services |
| **SNS** | Pub/sub notification service | Fan-out messages to multiple subscribers |
| **CloudFront** | CDN | Static asset delivery, API caching at edge |
| **Route 53** | DNS | Domain management, health checks, weighted routing |
| **ALB/NLB** | Load balancers | Traffic distribution (ALB for HTTP, NLB for TCP) |
| **IAM** | Identity and access management | Service permissions, roles, policies |
| **Secrets Manager** | Secret storage | API keys, DB passwords, certificates |
| **CloudWatch** | Monitoring and logging | Logs, metrics, alarms |
| **VPC** | Virtual network | Network isolation, subnets, security groups |

### Typical Spring Boot Deployment Architecture on AWS

```
                    ┌────────────────┐
                    │   Route 53     │
                    │   (DNS)        │
                    └───────┬────────┘
                            │
                    ┌───────▼────────┐
                    │  CloudFront    │
                    │  (CDN)         │
                    └───────┬────────┘
                            │
                    ┌───────▼────────┐
                    │  ALB           │
                    │(Load Balancer) │
                    └───────┬────────┘
                            │
              ┌─────────────┼─────────────┐
              │             │             │
        ┌─────▼─────┐ ┌────▼─────┐ ┌─────▼─────┐
        │  EKS Pod  │ │ EKS Pod  │ │  EKS Pod  │
        │  (Spring  │ │ (Spring  │ │  (Spring  │
        │   Boot)   │ │  Boot)   │ │   Boot)   │
        └─────┬─────┘ └────┬─────┘ └─────┬─────┘
              │             │             │
     ┌────────┼─────────────┼─────────────┼────────┐
     │        │             │             │        │
  ┌──▼──┐  ┌─▼───┐    ┌────▼────┐   ┌────▼────┐   │
  │ RDS │  │Redis│    │   S3    │   │  SQS    │   │
  │(PG) │  │Cache│    │(Files)  │   │(Queue)  │   │
  └─────┘  └─────┘    └─────────┘   └─────────┘   │
                                                    │
                                              ┌─────▼─────┐
                                              │CloudWatch │
                                              │(Logs/     │
                                              │ Metrics)  │
                                              └───────────┘
```

### IAM — Least Privilege Principle

```json
{
    "Version": "2012-10-17",
    "Statement": [
        {
            "Effect": "Allow",
            "Action": [
                "s3:GetObject",
                "s3:PutObject"
            ],
            "Resource": "arn:aws:s3:::my-app-uploads/*"
        },
        {
            "Effect": "Allow",
            "Action": [
                "sqs:SendMessage",
                "sqs:ReceiveMessage",
                "sqs:DeleteMessage"
            ],
            "Resource": "arn:aws:sqs:us-east-1:123456789:my-app-queue"
        }
    ]
}
```

**Rule**: Never use `"Action": "*"` or `"Resource": "*"` in production. Every service should have the minimum permissions needed to function.

### VPC — Network Security

```
VPC (10.0.0.0/16)
├── Public Subnet (10.0.1.0/24) — ALB, NAT Gateway
├── Private Subnet A (10.0.10.0/24) — EKS Pods
├── Private Subnet B (10.0.20.0/24) — EKS Pods
└── Data Subnet (10.0.30.0/24) — RDS, ElastiCache

Security Groups:
  ALB → allows port 443 from 0.0.0.0/0
  EKS → allows port 8080 from ALB security group only
  RDS → allows port 5432 from EKS security group only
  ElastiCache → allows port 6379 from EKS security group only
```

**Key Principle**: The database should NEVER be accessible from the internet. Only the ALB is in a public subnet. Everything else is in private subnets.

### AWS Services Decision Guide

| Scenario | Service |
|----------|---------|
| Need a relational DB with ACID | RDS (PostgreSQL/MySQL) |
| Need sub-ms reads at massive scale | DynamoDB + DAX |
| Need to cache API responses | ElastiCache (Redis) |
| Need to process tasks async | SQS + Lambda or ECS worker |
| Need to send notifications | SNS (fan-out) + SQS (per consumer) |
| Need to store user uploads | S3 with pre-signed URLs |
| Need to serve static frontend | S3 + CloudFront |
| Need full-text search | OpenSearch (managed Elasticsearch) |
| Need to run containers | EKS (Kubernetes) or ECS (simpler) |
| Need serverless compute | Lambda (< 15 min, < 10 GB memory) |
| Need secrets management | Secrets Manager or SSM Parameter Store |
| Need CI/CD | CodePipeline + CodeBuild or GitHub Actions |

---

## 12.5 Security in System Design Interviews

### Security Checklist for Every System Design

When designing any system in an interview, mention these security considerations:

1. **Authentication**: How are users authenticated? (OAuth2 + JWT, API keys for services)
2. **Authorization**: How are permissions enforced? (RBAC, ABAC, resource-level)
3. **Encryption**: Data at rest (AES-256) and in transit (TLS 1.3)
4. **Input Validation**: How is untrusted input handled? (Server-side validation, parameterized queries)
5. **Rate Limiting**: How are abuse and DDoS prevented? (Token bucket at API gateway)
6. **Audit Logging**: What security events are logged? (Auth, access, changes)
7. **Secrets Management**: How are API keys and passwords stored? (Vault, not in code)
8. **Network Security**: How are internal services protected? (VPC, security groups, service mesh)

**Interview Tip**: Proactively mentioning 2-3 security considerations during a system design interview demonstrates senior-level thinking and sets you apart.

---

## 12.6 Interview Quick Reference

### OWASP Top 10:2025 One-Liner Summary

| # | Risk (2025) | One-Line Prevention |
|---|-------------|---------------------|
| A01 | Broken Access Control (+ SSRF) | Server-side authz every request; URL allowlists for outbound calls |
| A02 | Security Misconfiguration | Disable defaults, harden cloud/K8s, minimal actuator exposure |
| A03 | Software Supply Chain Failures | Dependency scan, SBOM, signed images, protected CI/CD |
| A04 | Cryptographic Failures | BCrypt/Argon2 passwords, AES-256, TLS 1.3, secrets in Vault |
| A05 | Injection | Parameterized queries, input validation, output encoding, CSP |
| A06 | Insecure Design | Threat modeling (STRIDE), security user stories, ADRs |
| A07 | Authentication Failures | MFA, rate-limited login, secure sessions, account lockout |
| A08 | Software/Data Integrity Failures | Signed commits, CI/CD gates, no unsafe deserialization |
| A09 | Logging & Alerting Failures | Log security events, mask PII, centralize, alert on anomalies |
| A10 | Mishandling Exceptional Conditions | Fail closed, no stack traces to clients, test error paths |

### OWASP 2021 → 2025 Quick Mapping (reference)

| 2021 | 2025 |
|------|------|
| A01 Broken Access Control | A01 (+ SSRF merged in) |
| A02 Cryptographic Failures | A04 |
| A03 Injection | A05 |
| A04 Insecure Design | A06 |
| A05 Security Misconfiguration | A02 |
| A06 Vulnerable Components | A03 (expanded to Supply Chain) |
| A07 Identification/Authentication Failures | A07 Authentication Failures |
| A08 Software/Data Integrity | A08 |
| A09 Logging/Monitoring Failures | A09 |
| A10 SSRF | A01 (no longer standalone) |
| — | A10 Mishandling Exceptional Conditions (new) |

### Security Headers Every Response Should Have

| Header | Value | Purpose |
|--------|-------|---------|
| `Strict-Transport-Security` | `max-age=31536000; includeSubDomains` | Force HTTPS |
| `Content-Security-Policy` | `default-src 'self'` | Prevent XSS |
| `X-Content-Type-Options` | `nosniff` | Prevent MIME sniffing |
| `X-Frame-Options` | `DENY` | Prevent clickjacking |
| `X-XSS-Protection` | `0` | Disable legacy XSS filter (CSP replaces it) |
| `Referrer-Policy` | `strict-origin-when-cross-origin` | Control referrer leakage |
| `Permissions-Policy` | `camera=(), microphone=()` | Restrict browser features |

