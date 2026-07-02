# Principal / Architect Interview Failure Modes

> **You are here**: Principal / Architect — Interview Prep
> **Roadmap**: [Developer Master Roadmap](../../ROADMAP.md#principal-architect) | **Prerequisites**: [Principal Interview Loop](../Principal/Interview_Loop_Guide.md), [Staff Failure Modes](Staff_Failure_Modes.md) | **Next**: [Executive Communication](../Principal/Executive_Communication_Board_Narrative.md)

Principal loops fail candidates who are **excellent Staff ICs** but cannot operate at **org-wide strategy**: multi-year vision, executive communication, vendor/build-buy at scale, and regulated enterprise constraints.

---

## Failure modes by round

### Domain / enterprise architecture (60 min)

| Failure mode | Symptom | Fix |
|--------------|---------|-----|
| **Feature-level answer** | Designs one API, not portfolio | [Multi-Year Vision](../Principal/Multi_Year_Vision_Build_vs_Buy.md) |
| **No EA vocabulary** | Cannot map capabilities to contexts | [Enterprise Architecture Frameworks](../Principal/Enterprise_Architecture_Frameworks.md) — TOGAF, Zachman, strategic DDD |
| **No regulatory lens** | Ignores PCI/GDPR in fintech design | [§38 Compliance](../../01_TechGuide/38_Compliance_and_Regulated_Systems.md) |
| **Integration spaghetti** | Point-to-point without event mesh strategy | [Enterprise Integration](../Principal/Enterprise_Integration_ESB_iPaaS_Event_Mesh.md) |
| **DR hand-wave** | "We'll add a backup region" | [Multi-Region DR](../Principal/Multi_Region_Active_Active_DR.md) — RTO/RPO numbers |
| **Org blind** | Architecture fights team structure | [Org Design](../Principal/Organization_Design_Conway_Team_Topologies.md) |

### Executive / board narrative (30–45 min)

| Failure mode | Fix |
|--------------|-----|
| Jargon to non-technical audience | [Executive Communication](../Principal/Executive_Communication_Board_Narrative.md) — SCQA |
| No business metric translation | "3× deploy frequency" → revenue/risk impact |
| Cannot defend 3-year bet | Vision pillars with kill criteria |
| Vendor decision emotional | [Vendor Evaluation Rubrics](../Principal/Vendor_Evaluation_Rubrics.md) |

### Cross-functional leadership

| Failure mode | Fix |
|--------------|-----|
| Cannot align conflicting VPs | Stakeholder map + phased roadmap |
| No "disagree and commit" at exec level | Real example with outcome |
| Principal as ivory tower | Enablement metrics: teams unblocked |

---

## Principal vs Staff — interview delta

| Dimension | Staff | Principal |
|-----------|-------|-----------|
| **Time horizon** | 6–18 months | **2–5 years** |
| **Scope** | Platform / 2–4 teams | **Business unit / org** |
| **Build vs buy** | Recommends | **Owns** with TCO |
| **Executive comms** | Optional | **Core round** |
| **Coding** | Often yes | Rare; architecture depth instead |

---

## India context (GCC + global remote)

| Setting | Principal trap | Prep |
|---------|----------------|------|
| **US HQ GCC in India** | Local Principal scope vs global standards | Influence upward; align with global reference architecture |
| **Indian unicorn** | Hyper-growth debt | Vision + phased modernization |
| **Remote Principal for US firm** | Async exec communication | Written strategy docs, recorded walkthroughs |
| **Consulting / SI Principal** | Client politics | Vendor rubric + stakeholder management |

---

## Recovery checklist

- [ ] [Staff/Principal Advance Criteria](Staff_Principal_Advance_Criteria.md) — Principal band checked
- [ ] 3-min board narrative on a real or hypothetical platform bet
- [ ] Build-vs-buy matrix for one vendor decision you've made or simulated
- [ ] [Interview Loop Guide](../Principal/Interview_Loop_Guide.md) — full mock loop

**Note**: Formal TOGAF/Zachman frameworks are optional for most product-company Principal loops in India; vision + org design + DR docs in this repo cover interview depth. Enterprise SI roles may expect framework vocabulary — add certifications only if targeting that segment.
