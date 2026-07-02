# Section 24: Platform Engineering & Internal Developer Platforms (2026)

> **Level**: SR+ (golden paths) to LEAD (IDP strategy, developer experience metrics)
> **Why This Matters**: "Platform Engineering" replaced "DevOps team" at many companies. Lead engineers design internal platforms that let 100 developers ship safely without each team rebuilding CI/CD, K8s, and observability from scratch.

> **You are here**: Staff Engineer — Technical Skills
> **Roadmap**: [Developer Master Roadmap](../ROADMAP.md) | **Prerequisites**: [23_SRE_Reliability_Engineering.md](23_SRE_Reliability_Engineering.md) | **Next**: [25_Data_Engineering_Fundamentals.md](25_Data_Engineering_Fundamentals.md)

---

## 24.1 What is Platform Engineering?

**Definition**: Build and maintain **Internal Developer Platforms (IDPs)** — self-service tooling, templates, and abstractions that product teams use to deploy and operate software.

```
Product Team                    Platform Team
     │                                │
     │  "I need a new microservice"   │
     ├───────────────────────────────►│
     │                                │ Golden path template
     │◄───────────────────────────────┤ (Spring Boot + K8s + CI + monitoring)
     │  Deploy in 1 hour, not 2 weeks │
```

**Goal**: Reduce cognitive load — developers focus on business logic, platform handles infra patterns.

---

## 24.2 IDP Components (2026 Stack)

| Layer | Tools / Patterns |
|-------|------------------|
| **Developer Portal** | Backstage, Port, Cortex — service catalog, docs, APIs |
| **Golden Path Templates** | Cookiecutter, Archetypes, `backstage-create` |
| **CI/CD** | GitHub Actions, GitLab CI, Argo CD (GitOps) |
| **Infrastructure** | Kubernetes (EKS/GKE), Terraform, Crossplane |
| **Secrets** | Vault, AWS Secrets Manager, External Secrets Operator |
| **Observability** | OpenTelemetry, Grafana stack, Datadog |
| **Service Mesh** | Istio, Linkerd (optional — mTLS, traffic management) |
| **Policy** | OPA, Kyverno — enforce security standards in cluster |

---

## 24.3 Golden Paths — Core Concept

A **golden path** is the officially supported, documented, easiest way to accomplish a task.

| Golden Path | What It Provides |
|-------------|------------------|
| **New microservice** | Spring Boot template + Dockerfile + Helm chart + CI pipeline + dashboards |
| **New database** | Approved RDS instance with backup, monitoring, connection pooling |
| **Feature flag** | Integrated LaunchDarkly/Unleash with SDK |
| **API exposure** | API Gateway route + auth + rate limit pre-configured |

**Interview point**: Golden paths are **opinionated but optional** — teams can diverge, but default path is fastest and safest.

---

## 24.4 Backstage (Spotify) — Developer Portal

```
Software Catalog     →  All services, owners, dependencies, docs
TechDocs             →  Markdown docs rendered in portal
Software Templates   →  Scaffold new repos from templates
Plugins              →  Kubernetes, Argo CD, Grafana, PagerDuty integration
```

**Why Backstage**: Open source, CNCF project, integrates with existing tools — not a replacement for K8s or CI.

---

## 24.5 Platform Team Metrics (DORA + DX)

| Metric | What It Measures |
|--------|------------------|
| **Deployment frequency** | How often teams ship |
| **Lead time for changes** | Commit → production |
| **Change failure rate** | % deploys causing incidents |
| **MTTR** | Mean time to recover |
| **Developer satisfaction** | DX surveys, platform NPS |
| **Self-service rate** | % tasks done without platform team ticket |

**Anti-pattern**: Platform team becomes a ticket queue — goal is self-service APIs and templates.

---

## 24.6 GitOps for Platform Teams

```
Developer pushes → Git repo (manifests) → Argo CD / Flux watches → K8s cluster reconciles
```

**Benefits**: Audit trail, rollback via git revert, consistent environments, PR-based infra changes.

---

## 24.7 Security & Compliance in IDPs

| Control | Implementation |
|---------|----------------|
| **Image scanning** | Trivy, Snyk in CI — block vulnerable images |
| **Policy as code** | Kyverno: "all pods must have resource limits" |
| **SBOM** | Software Bill of Materials for supply chain |
| **mTLS** | Service mesh or cert-manager for internal TLS |
| **Least privilege IAM** | Terraform modules enforce scoped roles |

---

## 24.8 Platform vs Microservices Team

| Question | Platform Team | Product Team |
|----------|---------------|--------------|
| Owns | Templates, clusters, shared services | Business features |
| Success metric | Developer velocity, platform reliability | User metrics, revenue |
| Builds | Payment platform SDK, deploy pipeline | Checkout flow, pricing logic |

**Thinnest viable platform**: Start with 2-3 golden paths; expand based on repeated pain points — not build everything upfront.

---

## 24.9 Interview Quick Reference

| # | Question | One-Line Answer |
|---|----------|-----------------|
| 1 | Platform engineering? | Build internal developer platforms for self-service infra and golden paths. |
| 2 | Golden path? | Opinionated, supported default for common tasks (new service, deploy, DB). |
| 3 | Backstage? | Open-source developer portal — service catalog, docs, templates. |
| 4 | IDP vs PaaS? | IDP is org-specific composition of tools; PaaS is vendor-managed (Heroku, Cloud Run). |
| 5 | GitOps? | Git as source of truth for infra; Argo CD reconciles cluster to repo state. |
| 6 | Platform team anti-pattern? | Becoming a bottleneck ticket queue instead of enabling self-service. |
| 7 | DORA metrics? | Deploy frequency, lead time, change failure rate, MTTR. |
| 8 | Developer experience? | How easy it is to build and ship — measured via surveys and self-service rate. |
| 9 | Policy in K8s? | OPA/Kyverno enforce security standards automatically on every deploy. |
| 10 | When to build a platform? | When 10+ teams repeat same infra work — consolidate into reusable abstractions. |

**Must-say keywords**: IDP, golden path, Backstage, GitOps, self-service, cognitive load, DORA, developer experience, thin platform.

---

## §24.10 Production & Interview Depth — IDP Rollout in 200+ Engineer Orgs

Swiggy and Flipkart-scale engineering orgs don't build a platform team of 40 on day one. They start with **two golden paths** — "new Spring Boot microservice on EKS" and "expose REST API behind internal gateway" — and measure self-service rate. Interviewers ask how you'd avoid becoming a **ticket queue** while enforcing PCI-ish standards for payment-adjacent services.

### Trade-off: Backstage Portal vs Lightweight Templates

| Approach | Pros | Cons | Indian org fit |
|----------|------|------|----------------|
| Full Backstage IDP | Service catalog, TechDocs, plugin ecosystem | 6–12 month rollout, dedicated team | 150+ engineers, multi-BU |
| Cookiecutter + GitHub Actions | Ship in weeks, low ops burden | No unified catalog | Startups → mid-size (Razorpay early IDP) |
| Harness/Port SaaS | Faster time-to-value | Vendor cost, less customization | Teams without platform headcount |
| Terraform modules only | Infra consistency | Devs still wire CI/observability manually | Platform v0.1 |

### Golden Path: Spring Boot 3.x Service Template

```
backstage-create → repo with:
  ├── pom.xml (Java 21, Spring Boot 3.3, OTel agent)
  ├── Dockerfile + Helm chart (values: dev/stage/prod)
  ├── .github/workflows/deploy.yml → Argo CD
  ├── dashboards/ (Grafana RED — from 11_Observability)
  └── catalog-info.yaml (owner, tier, dependencies)
```

Product team runs `create-microservice --name=order-pricing --tier=1`; platform guarantees **Pod Security Standards**, **Kyverno** resource limits, and **pre-wired** Prometheus scrape — see [30_Kubernetes_Deep_Dive.md](./30_Kubernetes_Deep_Dive.md). Platform success metric: **< 10% of deploys need platform-team Slack ping** during festival prep.

```yaml
# catalog-info.yaml — every service declares SLO tier
apiVersion: backstage.io/v1alpha1
kind: Component
metadata:
  name: checkout-service
  annotations:
    prep.io/slo-tier: "tier-1"  # payment path — stricter canary gates
spec:
  type: service
  lifecycle: production
  owner: team-checkout
```

Cross-link reliability gates to [23_SRE_Reliability_Engineering.md](./23_SRE_Reliability_Engineering.md): tier-1 services get mandatory canary analysis and error-budget checks in the deploy pipeline. Thin platform beats grand vision — expand paths only when three squads repeat the same pain (CDC setup → add Debezium golden path from [25_Data_Engineering_Fundamentals.md](./25_Data_Engineering_Fundamentals.md)).
