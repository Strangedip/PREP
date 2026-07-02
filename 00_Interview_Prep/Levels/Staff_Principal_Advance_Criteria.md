# Staff & Principal — Advance Criteria (Self-Check)

> **You are here**: Staff / Principal — Interview Prep
> **Roadmap**: [Developer Master Roadmap](../../ROADMAP.md) | **Prerequisites**: [SelfAssessment](../Core/SelfAssessment.md) total ≥ 160 | **Next**: [Staff Loop Expectations](Staff_Loop_Expectations.md)

[SelfAssessment.md](../Core/SelfAssessment.md) ends at Lead-oriented sections without explicit **Staff/Principal advance bars**. Use this supplement after scoring ≥ 160 on the main checklist.

---

## Staff Engineer — ready to interview when

### Technical (all must be ≥ 4/5 self-score)

| # | Criterion | Evidence | Repo check |
|---|-----------|----------|------------|
| S1 | Whiteboard flagship HLD in 45 min with NFRs + deep dive | Payment or Chat without notes | [HLD Template](../../04_SystemDesign/00_Templates/HLD_Template/HLD_Template.md) |
| S2 | Explain Kafka vs REST trade-off for your domain | Draw sync/async failure modes | [§06](../../01_TechGuide/06_Microservices_Distributed_Systems.md) |
| S3 | Solve Tier3 hard pattern in 35 min | Segment tree, Dijkstra, or design LRU/LFU | [Tier3](../../02_DSA/Tier3_Differentiators.md) |
| S4 | JVM production debug story | GC log or flame graph | [§18](../../01_TechGuide/18_Performance_Engineering_JVM.md) |
| S5 | Platform or golden-path contribution | Template, IDP, shared lib | [§24](../../01_TechGuide/24_Platform_Engineering_IDP.md) |

### Influence (prepare 2 documented examples each)

| # | Criterion | Example |
|---|-----------|---------|
| S6 | RFC adopted by 3+ teams | Link or describe [Multi-Team Review](Multi_Team_Architecture_Review.md) style RFC |
| S7 | Blocked bad launch with data | Load test, security review |
| S8 | Mentored another tech lead | Career growth outcome |

### SelfAssessment mapping

- Sections 22–26 average ≥ **4.0/5**
- Section 21 (DSA hard) ≥ **3.5/5**
- Total score ≥ **165** (stricter than generic "interview-ready" 160)

---

## Principal / Architect — ready when

### Technical judgment

| # | Criterion | Evidence |
|---|-----------|----------|
| P1 | Domain architecture across 5+ capabilities | Checkout, catalog, payments, notify, analytics |
| P2 | Compliance-aware design for your industry | PCI/GDPR touchpoints — [§38](../../01_TechGuide/38_Compliance_and_Regulated_Systems.md) |
| P3 | AI product architecture at scale | RAG + cost + safety — [05_AI](../../05_AI/10_AI_System_Design.md) |
| P4 | 3-year migration narrative | Monolith → strangler without fantasy dates |

### Organizational

| # | Criterion | Evidence |
|---|-----------|----------|
| P5 | Influenced product roadmap with technical bet | Said no to feature; proposed alternative |
| P6 | Exec-readable one-pager | Non-jargon summary for director |
| P7 | Hiring bar contribution | Interview loop design or bar raiser |

### SelfAssessment mapping

- Sections 27 (leadership) + 19 (AI) ≥ **4.5/5**
- Total ≥ **175** with no section below 3 except polyglot (§36) if backend-only

---

## Scorecard template

```
Date: ___________  Target: [ ] Staff  [ ] Principal

Staff S1–S8:  ___/8 criteria met (need 7/8)
Principal P1–P7: ___/7 criteria met (need 6/7)

SelfAssessment total: ___ / 190
Weakest section: ___ → ROADMAP link: ___

Go / No-go for scheduling: ___
```

---

## If not ready

| Gap | Action |
|-----|--------|
| HLD depth | 2 HLDs/week from [04_SystemDesign](../../04_SystemDesign/README.md) |
| Tier3 DSA | [Tier3 12-week sprint](../../02_DSA/Tier3_Differentiators.md) |
| Influence stories | Draft RFC using [Multi-Team Review](Multi_Team_Architecture_Review.md) template on past work |
| Principal domain | [Principal Loop Guide](../Principal/Interview_Loop_Guide.md) week plan |
