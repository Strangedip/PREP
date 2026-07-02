# Section 29: Advanced Networking & Infrastructure

> **Level**: SR+ (VPC, load balancing, firewalls) to LEAD (multi-region, zero trust, DDoS)
> **Complements**: [17_Networking_Protocols.md](./17_Networking_Protocols.md) — Section 17 covers protocols (HTTP, TLS, DNS); this section covers **infrastructure networking**.

> **You are here**: Senior SDE — Technical Skills
> **Roadmap**: [Developer Master Roadmap](../ROADMAP.md) | **Prerequisites**: [28_Redis_Distributed_Caching.md](28_Redis_Distributed_Caching.md) | **Next**: [30_Kubernetes_Deep_Dive.md](30_Kubernetes_Deep_Dive.md)

---

## 29.1 IP Addressing & Subnets

### IPv4 Private Ranges (RFC 1918)
| Range | Typical Use |
|-------|-------------|
| 10.0.0.0/8 | Large enterprise VPCs |
| 172.16.0.0/12 | AWS default VPC ranges |
| 192.168.0.0/16 | Small networks, home |

### CIDR Notation
`10.0.1.0/24` → 256 addresses (10.0.1.0 – 10.0.1.255), 254 usable hosts.

**Subnet design**: Reserve tiers — public (ALB), private (app servers), data (DB — no internet).

---

## 29.2 VPC Architecture (AWS Model — Applies to GCP/Azure)

```
Internet
    │
┌───▼──────────────────────────────────────────────────┐
│  VPC (10.0.0.0/16)                                     │
│  ┌─────────────────┐    ┌─────────────────────────┐   │
│  │ Public Subnet    │    │ Private Subnet           │   │
│  │  ALB, NAT Gateway│    │  App servers, workers    │   │
│  │  10.0.1.0/24     │    │  10.0.2.0/24             │   │
│  └────────┬─────────┘    └───────────┬─────────────┘   │
│           │                          │                 │
│           │         ┌────────────────▼─────────────┐   │
│           │         │  Data Subnet (isolated)       │   │
│           │         │  RDS, Redis — no public IP    │   │
│           │         │  10.0.3.0/24                  │   │
│           │         └───────────────────────────────┘   │
└────────────────────────────────────────────────────────┘
```

### Key Components
| Component | Role |
|-----------|------|
| **Internet Gateway** | VPC ↔ internet |
| **NAT Gateway** | Private subnet outbound internet (updates, APIs) — no inbound |
| **Route Tables** | Direct traffic per subnet |
| **Security Groups** | Stateful firewall at instance level (allow rules only) |
| **NACLs** | Stateless subnet-level firewall (allow + deny) |

**Stateful vs Stateless**: SG remembers connection — return traffic auto-allowed. NACL must allow both directions explicitly.

---

## 29.3 Load Balancers — L4 vs L7

| Layer | Name | Routes On | Use Case |
|-------|------|-----------|----------|
| **L4** | NLB, TCP load balancer | IP + port | Raw TCP, ultra-low latency, millions RPS |
| **L7** | ALB, HTTP(S) LB | HTTP headers, path, host | REST APIs, path-based routing, TLS termination |

### L7 Capabilities
- Path routing: `/api/*` → backend, `/static/*` → S3
- Host routing: `api.example.com` vs `admin.example.com`
- TLS termination at LB
- WAF integration

### Algorithms
| Algorithm | Behavior |
|-----------|----------|
| Round Robin | Rotate evenly — bad if server capacities differ |
| Least Connections | Send to server with fewest active connections |
| IP Hash | Session affinity — same client → same server |
| Weighted | Proportional to server capacity |

---

## 29.4 DNS Deep Dive (Production)

### Record Types
| Type | Purpose |
|------|---------|
| A / AAAA | Hostname → IP (v4 / v6) |
| CNAME | Alias to another hostname |
| ALIAS/ANAME | AWS alias to AWS resource (no extra lookup) |
| MX | Mail servers |
| TXT | SPF, DKIM, verification |
| NS | Delegates subdomain to another DNS server |

### DNS in System Design
- **GeoDNS / Latency routing** — Route 53, Cloudflare — user → nearest region
- **Health checks** — Failover to backup region if primary unhealthy
- **TTL trade-off** — Low TTL (60s) for fast failover; high TTL for cache efficiency
- **DNS propagation** — Changes not instant globally

### Split-Horizon DNS
Internal users resolve `db.internal` to private IP; external gets public IP or nothing.

---

## 29.5 NAT, Reverse Proxy, Forward Proxy

| Type | Direction | Example |
|------|-----------|---------|
| **NAT Gateway** | Private → Internet (outbound only) | App servers fetch packages |
| **Reverse Proxy** | Internet → Internal servers | Nginx, ALB in front of app tier |
| **Forward Proxy** | Internal → Internet (filtered) | Corporate proxy, egress control |

---

## 29.6 Service Mesh Networking (Istio / Linkerd)

Sidecar proxy (Envoy) on every pod:
- **mTLS** between all services automatically
- **Traffic splitting** — 90% v1, 10% v2 (canary)
- **Retries, timeouts, circuit breaking** at network layer
- **Observability** — per-service metrics without app changes

**Trade-off**: Operational complexity, latency overhead (~1-3ms per hop).

---

## 29.7 DDoS Protection & WAF

| Layer | Protection |
|-------|------------|
| **Network (L3/L4)** | SYN flood mitigation, rate limiting at edge (Shield, Cloudflare) |
| **Application (L7)** | WAF rules — SQL injection, XSS, bot detection |
| **CDN** | Absorb volumetric attacks at edge |

---

## 29.8 Zero Trust Networking

**Principle**: Never trust, always verify — even inside the corporate network.

- **mTLS** between all services
- **Identity-based access** (SPIFFE/SPIRE) not IP-based
- **Least privilege** network policies in Kubernetes
- **No flat network** — micro-segmentation

---

## 29.9 Interview Quick Reference

| # | Question | One-Line Answer |
|---|----------|-----------------|
| 1 | Public vs private subnet? | Public has route to IGW; private uses NAT for outbound only. |
| 2 | Security Group vs NACL? | SG: stateful, instance level. NACL: stateless, subnet level. |
| 3 | L4 vs L7 LB? | L4: TCP/IP. L7: HTTP-aware routing, TLS termination. |
| 4 | NAT Gateway purpose? | Allow private instances outbound internet without public IPs. |
| 5 | DNS failover? | Low TTL + health checks → route to backup region. |
| 6 | Why VPC peering limits? | Transitive routing not supported — use Transit Gateway. |
| 7 | Service mesh why? | mTLS, traffic management, observability without app code changes. |
| 8 | Stateful firewall? | Remembers connections — return traffic auto-permitted (Security Groups). |
| 9 | CDN vs reverse proxy? | CDN: global edge cache for static/content. Reverse proxy: origin gateway. |
| 10 | Zero trust? | Verify every request; no implicit trust inside network perimeter. |

**Must-say keywords**: VPC, subnet, NAT, IGW, security group, NACL, L4/L7, GeoDNS, mTLS, service mesh, WAF, zero trust.

---

## §29.10 Production & Interview Depth — Multi-AZ India Deployments & Payment Perimeter

Razorpay and PhonePe run in **ap-south-1 (Mumbai)** with DR in **ap-south-2 (Hyderabad)** or cross-region pairs. System design interviews ask how traffic flows during a zone failure while keeping **PCI-scoped subnets** isolated. Networking is not "ops only" — wrong security group on a payment pod is a compliance finding.

### Architecture: Three-Tier VPC for Fintech Checkout

```
Route 53 (latency routing India) → CloudFront/WAF
    → ALB (public subnet, TLS 1.3 termination)
        → EKS worker nodes (private subnet, no public IP)
            → RDS PostgreSQL (data subnet, SG: only from app SG)
            → ElastiCache Redis (data subnet)
NAT Gateway (per AZ) ← outbound PSP webhooks, package pulls
```

Aligns with [17_Networking_Protocols.md](./17_Networking_Protocols.md) TLS/DNS basics and [30_Kubernetes_Deep_Dive.md](./30_Kubernetes_Deep_Dive.md) for in-cluster `NetworkPolicy`.

### Trade-off: ALB vs NLB vs API Gateway at Festival Edge

| Component | Best for | Cost/latency | Indian sale note |
|-----------|----------|--------------|------------------|
| ALB (L7) | Path routing, WAF, OIDC | +2–5ms; rule limits | `/api/checkout` → dedicated target group |
| NLB (L4) | Millions RPS, static IPs | Lowest latency | gRPC internal, not browser-facing |
| API Gateway | Throttling, API keys | Higher $ at scale | Partner B2B APIs |
| CloudFront | Static assets, DDoS absorption | Edge POPs in India | Product images — origin shield in Mumbai |

### Spring Boot Behind ALB: Preserve Client IP & Health

```yaml
# application.yml — trust X-Forwarded-For from ALB only
server:
  forward-headers-strategy: native
management:
  endpoints:
    web:
      exposure:
        include: health
  endpoint:
    health:
      probes:
        enabled: true  # K8s liveness/readiness — separate from ALB target health
```

ALB health check `GET /actuator/health/readiness` — **not** deep DB check (cascade drain during DB blip). Security groups: app tier `ingress 443 from ALB-SG only`; DB tier `ingress 5432 from app-SG only`. Zero-trust extension: Istio mTLS between checkout → payment → ledger services per [06_Microservices_Distributed_Systems.md](./06_Microservices_Distributed_Systems.md). Festival prep: lower DNS TTL to 60s, pre-warm NAT Gateway capacity (avoid SNAT port exhaustion), enable AWS Shield Advanced on public endpoints during high-visibility sales.
