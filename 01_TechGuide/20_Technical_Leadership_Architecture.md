# Section 20: Technical Leadership & Software Architecture

> **Level**: LEAD (this section is specifically for the Lead Engineer role)
> **Why This Matters**: The Lead Engineer interview isn't just about coding — it's about demonstrating that you can own technical decisions, mentor teams, drive architecture, and communicate with stakeholders. This section covers everything beyond the code.

> **You are here**: Tech Lead — Soft Skills
> **Roadmap**: [Developer Master Roadmap](../ROADMAP.md) | **Prerequisites**: [19_Event_Driven_Architecture.md](19_Event_Driven_Architecture.md) | **Next**: [21_GraphQL_and_Alternative_APIs.md](21_GraphQL_and_Alternative_APIs.md)

---

## 20.1 Architecture Decision Records (ADRs)

### The "Why" & The Problem

Architecture decisions are invisible — they live in Slack threads, meetings, and people's heads. When someone asks "why did we choose Kafka over RabbitMQ?", nobody remembers. ADRs solve this.

### The Deep Dive & Solution

```markdown
# ADR-001: Use Event-Driven Architecture for Order Processing

## Status
Accepted (2026-01-15)

## Context
The current order processing system uses synchronous REST calls between 5 microservices.
During peak traffic (sales events), the system experiences:
- Cascading failures when Payment Service is slow
- 30-second response times due to sequential calls
- Complete outage when any downstream service goes down

We need a more resilient architecture that can handle 10x current peak traffic (50K orders/min).

## Decision
We will adopt event-driven architecture using Apache Kafka for order processing:
1. Order Service publishes "OrderCreated" events to Kafka
2. Payment, Inventory, Notification, and Analytics services consume events independently
3. Use the Saga pattern (choreography) for distributed transaction management
4. Implement Dead Letter Topics for failed event processing

## Consequences
### Positive
- Services are decoupled — failure of one doesn't cascade
- Horizontal scalability via Kafka partitions
- Event replay capability for debugging and recovery
- New consumers can be added without changing producers

### Negative
- Increased complexity (debugging distributed events is harder)
- Eventual consistency (customers may see stale data briefly)
- Team needs Kafka expertise (training investment)
- Need Schema Registry for event schema evolution

### Risks
- Kafka cluster becomes a single point of failure → mitigate with multi-AZ deployment
- Event ordering is per-partition only → use customer ID as partition key

## Alternatives Considered
1. **RabbitMQ**: Lower latency but lacks event replay and high throughput
2. **Synchronous + Circuit Breaker**: Simpler but still has temporal coupling
3. **AWS SNS/SQS**: Managed service but vendor lock-in and less control

## Decision Makers
- Sandip Gupta (Lead Engineer)
- Priya Sharma (Architect)
- Team consensus after RFC discussion
```

### When to Write ADRs

```
ALWAYS write an ADR for:
- Technology/framework selections (Kafka vs RabbitMQ)
- Architecture pattern choices (monolith vs microservices)
- Database decisions (SQL vs NoSQL, which vendor)
- API design changes (REST vs gRPC, versioning strategy)
- Security model changes (auth strategy, encryption)
- Infrastructure decisions (cloud provider, container orchestration)

DON'T write an ADR for:
- Minor library upgrades
- Bug fixes
- Routine configuration changes
- Obvious best practices
```

---

## 20.2 Technical Estimation & Planning

### The Deep Dive & Solution

#### Estimation Techniques

```
1. T-SHIRT SIZING (for roadmap-level estimates)
   XS: < 1 sprint (1-2 days for 1 person)
   S:  1 sprint (1-2 weeks)
   M:  2-3 sprints (1-2 months)
   L:  1 quarter (3 months)
   XL: Multi-quarter (6+ months)

2. STORY POINTS (for sprint planning)
   1: Trivial (config change, text update)
   2: Small (simple CRUD endpoint)
   3: Medium (endpoint with business logic)
   5: Complex (cross-service feature)
   8: Very complex (new integration, significant refactoring)
   13: Epic-sized (should be broken down)

3. THREE-POINT ESTIMATION (for technical designs)
   O = Optimistic (everything goes right)
   M = Most Likely (realistic estimate)
   P = Pessimistic (everything goes wrong)
   
   Expected = (O + 4M + P) / 6
   Standard deviation = (P - O) / 6
   
   Example: Database migration
   O = 2 weeks, M = 4 weeks, P = 10 weeks
   Expected = (2 + 16 + 10) / 6 = 4.67 weeks
   SD = (10 - 2) / 6 = 1.33 weeks
   → "4-6 weeks, with risk of 8+ if we hit data quality issues"
```

#### Technical Design Document Template

```markdown
# Technical Design: [Feature Name]

## Problem Statement
What problem are we solving? Why now? What happens if we don't solve it?

## Requirements
### Functional
- FR1: System shall...
- FR2: Users can...

### Non-Functional
- Performance: p99 latency < 200ms
- Availability: 99.9%
- Scalability: Handle 10K concurrent users
- Security: All data encrypted at rest and in transit

## Proposed Solution
### Architecture Diagram
[Include C4 model: Context, Container, Component diagrams]

### Data Model
[Entity-relationship diagram, event schemas]

### API Design
[OpenAPI spec, gRPC proto, event contracts]

### Key Design Decisions
1. Decision: Why chosen, what alternatives were rejected

## Alternatives Considered
| Option | Pros | Cons | Effort |
|--------|------|------|--------|

## Implementation Plan
### Phase 1 (Sprint 1-2): Foundation
- Task 1: Set up Kafka topics and schemas
- Task 2: Implement producer in Order Service

### Phase 2 (Sprint 3-4): Core Logic
- ...

### Phase 3 (Sprint 5): Rollout & Monitoring
- ...

## Risks & Mitigations
| Risk | Impact | Probability | Mitigation |
|------|--------|-------------|------------|

## Rollout Strategy
- Feature flag: `order-event-processing-v2`
- Canary deployment: 5% → 25% → 50% → 100%
- Rollback plan: Disable feature flag, revert to REST calls

## Observability
- New metrics: [list]
- New dashboards: [list]
- Alerts: [list]

## Success Metrics
- Reduce order processing p99 from 30s to 500ms
- Zero cascading failures during downstream outages
- 100% event delivery within 30s
```

---

## 20.3 Code Review Leadership

### The Deep Dive & Solution

```
Code Review as a Lead — Your Responsibilities:

1. ARCHITECTURE ALIGNMENT
   - Does this change align with our architectural decisions?
   - Is the right pattern being used? (e.g., event-driven where we agreed)
   - Are cross-cutting concerns handled? (logging, metrics, error handling)

2. DESIGN QUALITY
   - SOLID principles followed?
   - Appropriate abstraction level?
   - Proper separation of concerns?
   - Does it introduce technical debt? If so, is there a follow-up ticket?

3. PRODUCTION READINESS
   - Error handling: What happens when external service is down?
   - Logging: Can we debug production issues with these logs?
   - Metrics: Are we tracking key business/technical metrics?
   - Configuration: Are magic numbers externalized?
   - Security: Any SQL injection, XSS, or authentication gaps?

4. TESTING COMPLETENESS
   - Unit tests for business logic?
   - Integration tests for external dependencies?
   - Edge cases covered? (null, empty, boundary values)
   - Performance-sensitive paths benchmarked?

5. MAINTAINABILITY
   - Would a new team member understand this code?
   - Are complex algorithms documented?
   - Variable/method names are clear?
   - No unnecessary cleverness?
```

#### Code Review Communication Guidelines

```
AS A LEAD, how you give feedback matters as much as what you say:

✅ GOOD Feedback Patterns:
- "What do you think about using [alternative]? It might be simpler because..."
- "I learned something from [reference]. It might apply here: [link]"
- "Nice approach! One suggestion: consider [X] for [specific benefit]"
- "This works, but I'm concerned about [edge case]. What if we...?"
- "nit: [minor style/naming suggestion]" — prefix nits to show they're optional

❌ BAD Feedback Patterns:
- "This is wrong" — no explanation
- "We don't do it this way" — no reason why
- "Can you rewrite this?" — no direction
- "I wouldn't do it like this" — subjective without justification
- Blocking PR for stylistic preferences the linter should handle

Categories to prefix your comments:
- [blocker]: Must fix before merge (security, correctness, data loss)
- [suggestion]: Recommended improvement (not blocking)
- [nit]: Minor style/naming preference (optional)
- [question]: Need clarification to understand intent
- [praise]: Acknowledging good work (important for morale!)
```

---

## 20.4 Team Mentorship & Technical Growth

### The Deep Dive & Solution

```
Lead Engineer Mentorship Framework:

1. TECHNICAL MENTORSHIP
   - Pair programming on complex problems
   - Architecture design sessions (whiteboard together)
   - Code review as a teaching moment (explain WHY, not just WHAT)
   - Book/article recommendations based on individual growth areas
   - Conference talk attendance and knowledge sharing

2. CAREER DEVELOPMENT
   - Help team members define growth areas
   - Assign stretch projects that align with career goals
   - Provide specific, actionable feedback (not just "good job")
   - Advocate for their promotions and visibility

3. KNOWLEDGE SHARING
   - Weekly tech talks (30 min, rotated among team)
   - Architecture Decision Records (everyone reviews)
   - Post-mortems as learning opportunities (blameless)
   - Internal documentation and runbooks

4. CREATING PSYCHOLOGICAL SAFETY
   - "There are no stupid questions"
   - Admit your own mistakes publicly ("I made this production error...")
   - Celebrate learning from failures, not just successes
   - Protect team from unreasonable deadlines/scope creep
```

---

## 20.5 Incident Management & Post-Mortems

### The Deep Dive & Solution

```
Incident Severity Levels:

| Level | Description                                | Response Time | Who's Involved          |
|-------|--------------------------------------------|---------------|-------------------------|
| P0    | Service completely down, data loss          | < 15 min      | On-call + Lead + Manager|
| P1    | Major feature broken, significant user impact| < 30 min     | On-call + Lead          |
| P2    | Minor feature broken, workaround exists    | < 4 hours     | On-call                 |
| P3    | Minor issue, no user impact                | Next sprint   | Team                    |
```

#### Post-Mortem Template

```markdown
# Post-Mortem: [Incident Title]
Date: 2026-02-10
Duration: 2 hours 15 minutes
Severity: P1
Author: Sandip Gupta

## Summary
Order processing was down for 2h15m due to a Kafka consumer rebalancing storm
caused by a misconfigured consumer group setting during deployment.

## Timeline (all times IST)
- 14:00 — Deployment started (new order-processor service version)
- 14:05 — Kafka consumer group rebalancing triggered
- 14:07 — PagerDuty alert: order processing lag > 5 minutes
- 14:10 — On-call engineer (Sandip) acknowledged, began investigation
- 14:15 — Identified consumer rebalancing loop in Kafka logs
- 14:30 — Root cause identified: max.poll.interval.ms too low (30s vs 300s)
- 14:45 — Config fix applied, canary deployment started
- 15:00 — Canary stable, full rollout started
- 15:15 — All consumers stable, lag cleared
- 16:15 — Monitoring confirmed normal operation for 1 hour — incident resolved

## Root Cause
The new deployment changed `max.poll.interval.ms` from 300000 (5 min) to 30000 (30s)
due to a typo in the Helm chart. Processing a batch of orders sometimes takes >30s,
causing the consumer to be evicted from the group, triggering a rebalancing storm.

## Impact
- ~5,000 orders delayed by up to 2 hours
- No data loss (events still in Kafka, processed after recovery)
- 47 customer complaints via support channel

## Contributing Factors
1. No automated validation of Kafka consumer configuration changes
2. No unit test for Helm chart values
3. Consumer lag alert threshold was 5 minutes (should have been 1 minute)

## Action Items
| # | Action | Owner | Priority | Due Date | Status |
|---|--------|-------|----------|----------|--------|
| 1 | Add Helm chart value validation CI step | Sandip | P1 | Feb 15 | TODO |
| 2 | Reduce consumer lag alert threshold to 1 min | Priya | P1 | Feb 12 | Done |
| 3 | Add integration test for consumer config | Rahul | P2 | Feb 20 | TODO |
| 4 | Document Kafka consumer tuning guidelines | Sandip | P2 | Feb 25 | TODO |
| 5 | Add circuit breaker for slow batch processing | Team | P3 | Mar 01 | TODO |

## Lessons Learned
1. Configuration changes should be reviewed as carefully as code changes
2. Consumer rebalancing behavior should be part of team's Kafka knowledge
3. Our alerting caught the issue in 2 minutes — good signal-to-noise ratio

## What Went Well
- On-call responded within 5 minutes
- Root cause identified within 20 minutes
- No data loss thanks to Kafka's durability
- Team communication in #incident-order-processing was clear

## What Could Be Better
- Config validation should have caught the typo
- We should have a canary consumer before full rollout
- Runbook for "Kafka rebalancing storm" didn't exist (now created)
```

---

## 20.6 System Design Interview Framework

### How to Approach Any System Design Question

```
THE 4-STEP FRAMEWORK (40-50 minutes):

STEP 1: REQUIREMENTS (5 minutes)
  Functional Requirements:
  - What features does the system need?
  - Who are the users? How many?
  - What are the core use cases? (list 3-5)
  
  Non-Functional Requirements:
  - Latency: "What's the acceptable response time?"
  - Throughput: "How many requests per second?"
  - Availability: "What's the uptime requirement?"
  - Consistency: "Is eventual consistency acceptable?"
  - Scale: "How many users/data?"
  
  Constraints:
  - Budget? Cloud provider?
  - Existing technology stack?
  - Regulatory requirements?

STEP 2: HIGH-LEVEL DESIGN (10 minutes)
  - Draw the main components (boxes and arrows)
  - Client → Load Balancer → API Gateway → Services → Database
  - Identify data flow
  - Don't go into detail yet — show the big picture
  - Call out key design choices

STEP 3: DEEP DIVE (20 minutes)
  - Pick the most critical/complex components
  - Database schema design
  - API design (REST endpoints)
  - Data partitioning / sharding strategy
  - Caching strategy
  - Message queue / event design
  - Show trade-offs and alternatives

STEP 4: WRAP-UP (5 minutes)
  - Address bottlenecks and single points of failure
  - Discuss monitoring, alerting, and observability
  - Mention future improvements
  - Show awareness of trade-offs made
```

#### Back-of-the-Envelope Calculations

```
NUMBERS EVERY ENGINEER SHOULD KNOW:

Time:
  L1 cache reference:                0.5 ns
  L2 cache reference:                7 ns
  Main memory reference:             100 ns
  SSD random read:                   150 μs
  HDD seek:                          10 ms
  Network round trip (same DC):      0.5 ms
  Network round trip (cross-region): 50-150 ms

Throughput:
  SSD sequential read:               1 GB/s
  HDD sequential read:               200 MB/s
  Network bandwidth (1 Gbps):        125 MB/s
  Kafka throughput per partition:     ~10 MB/s write, ~30 MB/s read

Scale:
  1 million seconds ≈ 11.5 days
  1 billion seconds ≈ 31.7 years
  
  Daily active users (DAU):
    Small app: 10K-100K
    Medium app: 1M-10M  
    Large app: 100M+ (Facebook, YouTube)
  
  Storage estimation:
    1 Tweet (280 chars + metadata): ~1 KB
    1 Photo (compressed): ~200 KB
    1 Minute of video (720p): ~5 MB
    
  QPS estimation:
    100M DAU, each makes 10 requests/day
    = 1 billion requests/day
    = ~12K QPS average
    = ~36K QPS peak (3x average)
```

---

## 20.7 Stakeholder Communication

### The Deep Dive & Solution

```
Lead Engineers bridge the gap between technical teams and business stakeholders.

COMMUNICATING TECHNICAL DECISIONS TO NON-TECHNICAL STAKEHOLDERS:

1. START WITH THE BUSINESS IMPACT
   ❌ "We need to migrate from monolith to microservices using Kafka event streaming"
   ✅ "We can reduce checkout failures by 90% and support 10x more concurrent orders.
       This requires a 3-month architecture change."

2. USE ANALOGIES
   ❌ "Horizontal scaling with stateless services behind a load balancer"
   ✅ "Instead of one big cash register that breaks under load, we're adding 
       more registers that share the customer queue"

3. QUANTIFY TRADE-OFFS
   ❌ "This will take more time but be more robust"
   ✅ "Option A: 3 weeks, handles 10K users. Option B: 6 weeks, handles 100K users.
       Given our growth rate, we'll hit 10K users in Q3."

4. USE THE RFC FORMAT FOR MAJOR DECISIONS
   - Problem: What business problem are we solving?
   - Proposal: What do we want to do? (high-level)
   - Cost: Time, money, resources needed
   - Risk: What could go wrong?
   - Alternatives: What else did we consider?
   - Decision deadline: When do we need to decide?
```

---

## 20.8 Technical Debt Management

### The Deep Dive & Solution

```
Technical Debt Quadrant (Martin Fowler):

                    Deliberate                  Inadvertent
  ┌──────────────────────────────┬──────────────────────────────┐
  │ "We know this is a hack but  │ "What's dependency           │
  │  we need to ship by Friday"  │  injection?"                 │
  │                              │                              │
  │  PRUDENT & DELIBERATE        │  RECKLESS & INADVERTENT      │
  │  (acceptable with payback    │  (needs education/training)  │
  │   plan)                      │                              │
  ├──────────────────────────────┼──────────────────────────────┤
  │ "We must ship now and deal   │ "Now we know how we should   │
  │  with consequences"          │  have done it"               │
  │                              │                              │
  │  RECKLESS & DELIBERATE       │  PRUDENT & INADVERTENT       │
  │  (dangerous, avoid this)     │  (natural learning, manage   │
  │                              │   proactively)               │
  └──────────────────────────────┴──────────────────────────────┘

Lead Engineer's Approach to Tech Debt:

1. TRACK IT
   - Tag tech debt in Jira/Linear (label: tech-debt)
   - Categorize: Critical (blocks features), High (slows team), Medium, Low
   - Include "interest rate" — how much extra time does this cost per sprint?

2. COMMUNICATE IT
   - "Every sprint, we spend 20% extra time working around [debt item]"
   - "If we fix [debt item], feature delivery speed increases by X%"
   - Use business language, not technical jargon

3. PAY IT DOWN
   - 20% rule: Reserve ~20% of sprint capacity for tech debt
   - Opportunistic: Fix debt when you're already changing that area
   - Strategic: Plan larger debt payoff as dedicated sprints/quarters
   - Boy Scout Rule: Leave code better than you found it

4. PREVENT IT
   - Architecture reviews for major features
   - Code review standards
   - Automated quality gates (SonarQube, linter rules)
   - Adequate time estimates (don't create debt through rushed deadlines)
```

---

## 20.9 Behavioral Interview — STAR Stories for Leads

### Prepared STAR Stories You Need

```
Prepare 8-10 STAR stories covering these themes:

1. TECHNICAL LEADERSHIP
   S: Our team was assigned to redesign the payment system
   T: I needed to lead the architecture and guide 4 engineers
   A: Created ADR, hosted design reviews, broke into phases, pair-programmed critical paths
   R: Delivered in 3 months, reduced payment failures by 95%

2. CONFLICT RESOLUTION
   S: Two senior engineers disagreed on database choice (Postgres vs MongoDB)
   T: I needed to facilitate a decision without creating resentment
   A: Set objective criteria, ran benchmarks, presented data, let the team vote
   R: Team aligned on Postgres; the "losing" engineer appreciated the fair process

3. MENTORSHIP
   S: A junior developer was struggling with code review feedback
   T: Help them grow without demotivating them
   A: Pair-programmed weekly, gave specific feedback, assigned stretch tasks
   R: Within 6 months, they were independently delivering complex features

4. HANDLING FAILURE
   S: My architectural decision caused a production outage
   T: Fix the issue and prevent recurrence
   A: Led the incident response, wrote honest post-mortem, implemented 5 action items
   R: Team learned from my transparency; we implemented better testing practices

5. DRIVING CHANGE
   S: Team was not doing code reviews consistently
   T: Establish a code review culture
   A: Set clear expectations, created review guidelines, led by example, recognized good reviews
   R: Code quality improved measurably (SonarQube scores up 40%)

6. DEALING WITH AMBIGUITY
   S: Product team had vague requirements for a new feature
   T: Deliver something useful despite incomplete requirements
   A: Built a technical spike, created prototypes, facilitated requirements workshop
   R: Clarified scope, delivered MVP in 4 weeks, iterated based on user feedback

7. PRIORITIZATION UNDER PRESSURE
   S: Three critical bugs + a deadline-driven feature request all at once
   T: Deliver maximum impact with limited resources
   A: Triaged by business impact, assigned based on expertise, communicated trade-offs to PM
   R: Fixed P0 bug in 2 hours, delivered feature with reduced scope, deferred P3 bugs

8. CROSS-TEAM COLLABORATION
   S: Our service change impacted 3 other teams
   T: Coordinate the change without breaking anyone
   A: Created RFC, hosted cross-team sync, staged rollout, created runbook
   R: Zero-downtime migration with buy-in from all teams
```

---

## 20.10 Interview Quick Reference — Leadership

### Top Questions and One-Line Answers

| Question | Answer |
|----------|--------|
| Tell me about a time you led a technical initiative | [Use STAR: Technical Leadership story] |
| How do you handle disagreements on technical decisions? | Objective criteria + data + team discussion + ADR to document. |
| How do you manage technical debt? | Track, communicate in business terms, allocate 20% sprint capacity, prevent with standards. |
| How do you mentor junior engineers? | Pair programming, stretch assignments, specific feedback, psychological safety. |
| What's your approach to system design? | 4-step: Requirements → High-level → Deep-dive → Wrap-up. |
| How do you handle a production incident? | Mitigate first → communicate → RCA → blameless post-mortem → action items. |
| How do you estimate projects? | Three-point (optimistic/likely/pessimistic) + buffer for unknowns + track accuracy. |
| How do you communicate technical decisions to non-technical people? | Start with business impact, use analogies, quantify trade-offs. |
| What makes a good code review? | Architecture alignment, design quality, production readiness, teaching opportunity. |
| How do you handle scope creep? | Document requirements upfront, push back with data, propose phased delivery. |

### Amazon Leadership Principles (Most Asked)

```
The 6 most relevant for Lead Engineer:

1. OWNERSHIP: "Leaders are owners. They think long-term and don't sacrifice long-term
   value for short-term results."
   → Prepare a story about taking responsibility beyond your defined role

2. DIVE DEEP: "Leaders operate at all levels, stay connected to the details, and audit
   frequently. No task is beneath them."
   → Prepare a story about debugging a complex production issue

3. BIAS FOR ACTION: "Speed matters in business. Many decisions are reversible and do
   not need extensive study."
   → Prepare a story about making a decision with incomplete information

4. EARN TRUST: "Leaders listen attentively, speak candidly, and treat others respectfully.
   They are vocally self-critical."
   → Prepare a story about admitting a mistake and learning from it

5. HAVE BACKBONE; DISAGREE AND COMMIT: "Leaders respectfully challenge decisions when
   they disagree, even when doing so is uncomfortable."
   → Prepare a story about pushing back on a bad technical decision

6. DELIVER RESULTS: "Leaders focus on the key inputs and deliver them with the right
   quality and in a timely fashion."
   → Prepare a story about delivering under tight deadlines with quality
```

