# Content Style Guide

> **Purpose**: Keep this repo complete, clean, and non-generic as it grows.
> **Audience**: Anyone adding or editing markdown in PREP.

---

## Voice

| Do | Don't |
|----|-------|
| Precise, interview-and-production oriented | Motivational fluff, "crush the interview" |
| India + global-hiring-into-India context when relevant | Generic FAANG-only advice |
| Tables for comparisons, checklists for readiness | Walls of prose without structure |
| Concrete metrics, trade-offs, failure modes | Buzzword lists without judgment |
| Link to the single canonical file for a topic | Duplicate the same topic in two homes |

**Tone**: Professional second person ("you"). No emoji bullets in new content. No first-person "patterns I have explained."

---

## Required header (every topic file)

```markdown
> **You are here**: [Level] — [Topic]
> **Roadmap**: [Developer Master Roadmap](../ROADMAP.md) | **Prerequisites**: [...] | **Next**: [...]
```

Adjust relative paths by folder depth. End with a **Related** section linking ROADMAP / hub / sibling guides.

---

## Structure by content type

### TechGuide (`01_TechGuide/`)

1. **The "Why" & The Problem** — production pain this solves
2. **Interviewer Expectations** — keywords and depth by level
3. **The Deep Dive & Solution** — code, diagrams, trade-offs
4. **Failure modes / common traps** (short table)
5. **Related** links

Target depth: **400–1200 lines** for core chapters; fundamentals may be shorter if complete.

### Principal guides (`00_Interview_Prep/Principal/`)

1. Definitions / decision frame
2. **Worked narrative** (one fictional but realistic India product/fintech case)
3. Templates execs or ARFs actually use
4. Interview prompts + failure modes
5. Related

Target depth: **250–450 lines** — judgment and narrative, not encyclopedia padding.

### DSA problems (`02_DSA/`)

1. Problem statement (concise)
2. Pattern link to `03_CodingPatterns/02_AlgorithmicPatterns.md`
3. At least **two** approaches (brute → optimal)
4. Complexity
5. Java implementation
6. Interview follow-ups (2–4) + optional production tie-in

### System design (`04_SystemDesign/`)

Follow existing LLD/HLD templates. Prefer capacity math, NFRs, failure modes, and discussion points over decorative diagrams.

### On-the-job (`06_On_The_Job/`)

Practical playbooks: checklists, scripts, RACI tables. Link interview STAR stories where the skill maps back.

---

## Tags in ROADMAP

| Tag | Meaning |
|-----|---------|
| `[Core]` | Required for that level's "ready to move on" |
| `[Recommended]` | Strong differentiator; do after Core |
| `[Optional]` | Deeper dive or alternate path |

Every new file must appear in the relevant ROADMAP level section. **No orphan files.**

---

## Canonical homes (no split topics)

| Topic | Home |
|-------|------|
| Kafka depth | `01_TechGuide/19_...` (or §06 intro — follow ROADMAP) |
| Patterns theory | `01_TechGuide/03_...` + catalog in `03_CodingPatterns/` |
| Career fresher | `00_Interview_Prep/Career/` |
| Mid-career switch / negotiation depth | `Career/Mid_Career_...` + `Levels/Comp_and_Scope.md` |
| Migration playbook | `01_TechGuide/39_...` |

Cross-link; do not copy.

---

## Diagrams

- Prefer ASCII or Mermaid that **matches the logic** being explained
- Delete or fix diagrams that contradict the code
- One diagram per major idea — not decoration

---

## Code

- Java 17–21 / Spring Boot 3.x as default
- Snippets must compile conceptually (imports optional if noted)
- Label language in fences: `java`, `sql`, `yaml`, `bash`
- Polyglot (Python/Go) only in §36 or explicitly marked alternate tracks

---

## What not to add

- Parallel ROADMAPs or second folder hierarchies
- Verbatim LeetCode dumps without approach/pattern value
- 500-line soft-skill essays — prefer scripts + tables + links
- Generic purple-gradient "AI career" marketing pages

---

## Checklist before merging a new file

- [ ] Breadcrumb header present
- [ ] Linked from ROADMAP with Core/Recommended/Optional
- [ ] Listed in MASTER_INDEX (and section README if applicable)
- [ ] Prerequisites / Next point to real files
- [ ] No orphan duplicate of an existing canonical topic
- [ ] Matches voice rules above
