# Executive Communication & Board-Level Technical Narrative

> **You are here**: Principal / Architect — Influence
> **Roadmap**: [Developer Master Roadmap](../../ROADMAP.md) | **Prerequisites**: [§14 Leadership & Behavioral](../../01_TechGuide/14_Leadership_Behavioral_SystemDesign.md), [Tech Lead Conflict & Performance](../Levels/Tech_Lead_Conflict_and_Performance.md) | **Next**: [Multi-Year Vision](Multi_Year_Vision_Build_vs_Buy.md)

Staff engineers explain **how**; Principals explain **why now, what it costs, what we defer** — to VPs, product heads, and (sometimes) board risk committees. This guide is tailored to **India product companies** preparing for Principal loops and cross-functional leadership — not generic presentation skills.

---

## Audience calibration

| Audience | Cares about | Avoid | Time |
|----------|-------------|-------|------|
| **VP Engineering** | Roadmap fit, headcount, risk, DORA | Class diagrams | 15–30 min |
| **CPO / GM** | Revenue, time-to-market, reliability | Kafka partitions | 10–15 min |
| **CFO / Finance** | CapEx/OpEx, vendor contracts, ROI | Microservice count | 5–10 min |
| **Board / risk** | Security, compliance, existential outages | Implementation detail | 3–5 min + Q&A |
| **CTO (India startup)** | Bet coherence, talent retention, scale events | Buzzwords without metrics | 20 min |

---

## Narrative structure: SCQA + technical spine

**SCQA** (Situation–Complication–Question–Answer) — McKinsey-style, works for execs:

```
Situation:   Checkout p99 is 800ms; we lose 2% conversion at peak (Big Billion Days).
Complication: Monolith deploy blocks payment fixes; one DB for cart + analytics.
Question:    How do we hit 300ms p99 without 12-month rewrite?
Answer:      3-phase strangler — extract payment (Q1), async inventory (Q2), read replicas (Q3).
             Cost: 4 engineers × 2 quarters. Risk: mitigated by shadow traffic.
```

**Technical spine** (one slide or verbal):
```
Users → CDN → API GW → [Checkout | Payment | Inventory] → PostgreSQL / Kafka
                      ↑ golden path §24          ↑ PCI scope §38
```

---

## Board-level 3-minute template (outage or strategy)

### Post-incident (trust-building)

> "**What happened**: Payment webhook lag caused duplicate retry storms for 47 minutes, affecting 0.3% of transactions.
> **Customer impact**: 12K orders delayed confirmation; no duplicate charges (idempotency held).
> **Root cause**: Consumer autoscale lag + missing DLQ alert.
> **Remediation**: DLQ dashboards live this week; autoscale policy change; game day in 30 days.
> **What we need**: Approval for one SRE headcount on payment platform."

### Strategy (investment ask)

> "**Bet**: Event-native checkout on EKS by FY27.
> **Why now**: Incident trend + 2× traffic forecast.
> **Investment**: ₹X Cr (eng + infra); **return**: 30% fewer Sev-1, 2× deploy frequency.
> **Alternative rejected**: Buy iPaaS for orchestration — too slow for p99, weak audit story.
> **Ask**: Approve Q1 staffing + defer multi-region to H2."

---

## Translating engineering metrics to business language

| Engineering metric | Executive translation |
|--------------------|----------------------|
| p99 latency 800ms → 300ms | "~1.5% conversion recovery at peak" (use your A/B data) |
| 99.9% → 99.95% availability | "~4h less downtime/year" |
| Deploy weekly → daily | "Faster fraud rule rollout, smaller blast radius" |
| Kafka consumer lag | "Orders visible to warehouse 2 min late = SLA risk" |
| Tech debt 20% capacity | "One sprint per quarter not shipping features — security & speed tax" |
| PCI scope reduction | "Lower audit cost + faster partner onboarding" |

Use ranges if exact data unavailable — never fabricate precision.

---

## Saying no to executives (without career damage)

| Request | Principal response frame |
|---------|-------------------------|
| "Ship multi-region this quarter" | "We can do **DR failover** this quarter; **active-active writes** need conflict model — recommend Q3 after payment idempotency audit." |
| "Rewrite everything in Go" | "Rewrite has no user metric; propose **Go for new edge service** with Java interop — pilot 1 team." |
| "Add blockchain for audit" | "Append-only audit log in PostgreSQL + immutability meets SOX; blockchain adds ops cost without compliance gain." |

Link: [Tech Lead Conflict](../Levels/Tech_Lead_Conflict_and_Performance.md) STAR stories.

---

## Written artifacts execs actually read

| Artifact | Length | Purpose |
|----------|--------|---------|
| **One-pager** | 1 page | Decision request with options table |
| **RFC executive summary** | 3 bullets | Prep for ARF ([Multi-Team Review](../Levels/Multi_Team_Architecture_Review.md)) |
| **Weekly engineering brief** | 5 bullets | Incidents, milestones, risks, asks |
| **QBR deck** | 10 slides max | OKR progress, DORA, top 3 risks |

**One-pager options table** (from [Build-vs-Buy](Multi_Year_Vision_Build_vs_Buy.md)):

| Option | Cost (18 mo) | Risk | Recommendation |
|--------|--------------|------|----------------|
| A: Strangler checkout | ₹X, 6 eng | Medium | **Recommended** |
| B: Big-bang rewrite | ₹3X, 15 eng | High | Reject |
| C: Buy iPaaS | ₹Y license | Medium-High | Defer |

---

## India-specific context

- **Festival scale**: Executives understand "BBD readiness" — tie narrative to concrete dates and load multipliers.
- **Regulatory**: RBI/PCI questions go to Principal — prepare 60-second compliance answer ([§38](../../01_TechGuide/38_Compliance_and_Regulated_Systems.md)).
- **Cost sensitivity**: OpEx (cloud, SaaS) often scrutinized more than eng headcount — show **unit economics** (cost per order, per MAU).

---

## Worked board packet — PayKart QBR (10 slides max)

Use with the vision narrative in [Multi-Year Vision](Multi_Year_Vision_Build_vs_Buy.md).

| # | Slide | Content (one idea each) |
|---|-------|-------------------------|
| 1 | Title | FY26 Q3 Engineering QBR — Checkout reliability |
| 2 | Outcome | Conversion and Sev-1 trend (business first) |
| 3 | Situation | Monolith deploy coupling; p99 900ms at peak |
| 4 | Complication | Festival load forecast 2×; PCI audit in Q4 |
| 5 | Bet | Strangle payment + inventory; buy vault/APM |
| 6 | Progress | 30% traffic on payment service; DLQ live |
| 7 | Money | ₹ per order; OpEx vs avoided downtime |
| 8 | Risks | Dual-write soak; hiring for stream team |
| 9 | Ask | Approve 2 engineers + defer multi-region |
| 10 | Appendix | Architecture spine (optional; leave behind) |

**Spoken opener (45 seconds)**:

> "Checkout conversion still leaks ~1.5% at peak when p99 crosses 800ms. We are mid-strangler on payment: thirty percent of traffic is on the new service with zero duplicate charges in shadow. I need approval to finish inventory extraction next quarter and explicitly defer multi-region so we do not split focus before SLO is green."

---

## Weekly eng brief (paste-ready)

```
1. Outcome: payment p99 420ms (target 350) — yellow
2. Shipped: DLQ dashboards; canary to 30%
3. Next: inventory reservation ACL in monolith
4. Risk: festival freeze starts [date] — need flag defaults agreed
5. Ask: PM confirm promo API can slip one sprint
```

IC-level visibility habits: [Stakeholder Updates](../../06_On_The_Job/09_Stakeholder_Updates_and_Visibility.md).

---

## Interview role-play prompts

1. "Explain last quarter's biggest technical bet to the CFO in 2 minutes."
2. "Board asks why you missed SLO during sale — what do you say?"
3. "CEO wants AI in every feature — how do you respond?" → [§05 AI](../../05_AI/README.md) phased approach
4. "Finance cut 20% cloud budget — what do you cut first vs last?"
5. Walk the QBR table above without slides.

---

## Related

- [Principal Interview Loop Guide](Interview_Loop_Guide.md)
- [Comp & Scope — Staff vs Principal](../Levels/Comp_and_Scope.md)
- [§20 Technical Leadership](../../01_TechGuide/20_Technical_Leadership_Architecture.md)
- [Stakeholder Updates](../../06_On_The_Job/09_Stakeholder_Updates_and_Visibility.md)
