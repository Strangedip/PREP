# Section 38: Compliance & Regulated Systems

> **Level**: SR+ / LEAD — Finance, healthcare, payments, and enterprise B2B
> **Complements**: [12_Security_OWASP_Cloud.md](./12_Security_OWASP_Cloud.md), [PaymentSystem HLD](../04_SystemDesign/02_HighLevelDesign/PaymentSystem/PaymentSystem.md)

> **You are here**: Senior SDE — Technical Skills
> **Roadmap**: [Developer Master Roadmap](../ROADMAP.md) | **Prerequisites**: [37_TypeScript_and_Frontend_Landscape.md](37_TypeScript_and_Frontend_Landscape.md)

---

## 38.1 Why This Matters in Interviews

Goldman Sachs, Stripe, healthcare startups, and banks ask:
- How do you handle **PII** and **audit trails**?
- What is **PCI-DSS** scope for your payment flow?
- How do you design for **GDPR** deletion and data residency?

---

## 38.2 Common Regulations (Summary)

| Regulation | Domain | Key Requirements |
|------------|--------|------------------|
| **GDPR** | EU personal data | Consent, right to erasure, data minimization, DPA |
| **PCI-DSS** | Card payments | No store PAN in logs; tokenization; network segmentation |
| **HIPAA** | US healthcare | PHI encryption, access controls, audit logs |
| **SOC 2** | SaaS trust | Security, availability, confidentiality controls |
| **SOX** | Public companies | Financial data integrity, change management |

---

## 38.3 PII Handling in Architecture

```
┌─────────────┐     ┌──────────────┐     ┌─────────────────┐
│  App Layer  │────▶│ Tokenization │────▶│ Encrypted Store │
│ (no raw PII │     │ Service      │     │ (AES-256, KMS)  │
│  in logs)   │     └──────────────┘     └─────────────────┘
└─────────────┘
```

**Practices**:
- **Field-level encryption** for sensitive columns (email, phone)
- **Tokenization** for payment — store token, not card number
- **Data classification**: Public / Internal / Confidential / Restricted
- **Retention policies**: Auto-delete after legal retention period
- **Audit log**: Who accessed what, when — immutable (append-only store)

```java
// Never log PII
log.info("User login success userId={}", userId);  // OK
log.info("Login email={}", email);                 // BAD
```

---

## 38.4 GDPR — Technical Implementation

| Right | Technical Response |
|-------|-------------------|
| **Access** | Export API — all user data in JSON |
| **Erasure** | Cascade delete + anonymize in analytics |
| **Portability** | Standard export format |
| **Restriction** | Flag account; stop processing except storage |
| **Consent** | Consent store with timestamp and version |

**Data residency**: EU users' data in EU region (S3 bucket, RDS region, no cross-border replication without legal basis).

---

## 38.5 PCI-DSS for Engineers

**Scope reduction**:
- Use **Stripe/PayPal/Adyen** — card data never touches your servers (SAQ A)
- If you handle cards: isolate **CDE** (Cardholder Data Environment), network segmentation, quarterly scans

**Never**:
- Log full card numbers, CVV, magnetic stripe data
- Store CVV after authorization

**Idempotency + reconciliation**: Required for payment correctness — see Payment System HLD.

---

## 38.6 Audit & Compliance Logging

| Log Type | Purpose | Retention |
|----------|---------|-----------|
| **Security audit** | Auth, permission changes | 1-7 years |
| **Application audit** | Business actions (who approved loan) | Per regulation |
| **Access log** | API access to sensitive resources | 90 days - 1 year |

**Properties**: Timestamp, actor, action, resource, outcome, correlation ID. **Immutable** — no UPDATE on audit tables.

---

## 38.7 Change Management (SOX / Enterprise)

- **Separation of duties**: Developer cannot deploy to prod alone
- **PR reviews** + **CI gates** + **approval workflows**
- **Immutable artifacts**: Deploy from tagged builds, not local builds
- **Rollback plan** documented in every production change

---

## 38.8 Interview Quick Reference

| Question | Answer |
|----------|--------|
| GDPR right to delete? | Delete/anonymize across DB, caches, backups policy, analytics. |
| Reduce PCI scope? | Use hosted payment fields / tokenization provider. |
| PII in logs? | Never — mask or use user ID only. |
| SOC 2 vs ISO 27001? | SOC 2: US SaaS audit report. ISO 27001: international ISMS certification. |
| Data residency? | Store and process data in required geographic region. |
| Audit trail design? | Append-only, actor + action + timestamp, tamper-evident. |

**Related**: [11_AI_Ethics_Safety_Governance.md](../05_AI/11_AI_Ethics_Safety_Governance.md) for AI-specific compliance.

---

## §38.10 Production & Interview Depth — India Regulations (RBI, DPDP, PCI)

Global compliance basics (GDPR, PCI) are table stakes; **India-specific** depth wins Senior+ rounds at Paytm, banks, NBFCs, and healthtech (Practo, pharma SaaS). Interviewers ask how **Spring Boot services in ap-south-1** implement localization, consent, and audit without blocking product velocity.

### India Regulatory Map for Engineers

| Framework | Who It Hits | Engineering Obligation |
|-----------|-------------|------------------------|
| **RBI data localization** | Payment aggregators, lenders storing payment data | Store **full payment data** in India; explicit board-approved policy |
| **DPDP Act 2023** | Any significant data fiduciary | Consent manager, purpose limitation, erasure, grievance officer |
| **PCI-DSS** | Card flows (even via gateway) | Scope reduction, no PAN in logs — §38.5 |
| **ABDM / health** | PHR, clinic apps | Consent artifacts, FHIR audit — stricter access logging |
| **SEBI / SOX-adjacent** | Brokerages, listed SaaS | Immutable audit, change control — [33_Git_Version_Control_Workflow.md](./33_Git_Version_Control_Workflow.md) |

Cloud placement ties to [31_Cloud_Computing_AWS_GCP_Azure.md](./31_Cloud_Computing_AWS_GCP_Azure.md) Mumbai region defaults.

### Consent + Erasure in Spring Boot 3

```java
@Entity
@Table(name = "user_consent")
public class UserConsent {
    @Id private UUID id;
    private UUID userId;
    private String purpose;           // "marketing", "kyc", "credit_check"
    private String policyVersion;     // "dpdp-v2026-03"
    private Instant grantedAt;
    private Instant withdrawnAt;      // nullable
}

@Service
@Transactional
public class DataErasureService {
    public void eraseUser(UUID userId) {
        auditLog.append("ERASURE_REQUESTED", userId, actor());
        userRepository.anonymizePii(userId);      // hash email, strip phone
        orderRepository.retainLegalFieldsOnly(userId); // GST invoices 7yr
        cacheEvictor.evictAllForUser(userId);     // Redis — [28_Redis_Distributed_Caching.md](./28_Redis_Distributed_Caching.md)
        searchIndexer.deleteByUserId(userId);     // ES — [34_Search_Engines_Elasticsearch.md](./34_Search_Engines_Elasticsearch.md)
        auditLog.append("ERASURE_COMPLETED", userId, actor());
    }
}
```

### Build vs Buy for Compliance Primitives

| Capability | Buy (Typical) | Build | Trade-off |
|------------|---------------|-------|-----------|
| **KYC / VKYC** | Hyperverge, Onfido, IDfy | In-house OCR | Buy until volume justifies ML team |
| **Payments** | Razorpay, PayU, Cashfree | Own PA license | Build is years + RBI — [PaymentSystem HLD](../04_SystemDesign/02_HighLevelDesign/PaymentSystem/PaymentSystem.md) |
| **Consent UI** | OneTrust, custom CMP | Widget + API | DPDP needs vernacular + withdraw flow |
| **Audit store** | immudb, CloudTrail + SIEM | Append-only Postgres partition | Tamper-evidence vs query latency |

### Interview STAR Prompt

*"We classified data in Postgres ([26_PostgreSQL_Relational_DB_Deep_Dive.md](./26_PostgreSQL_Relational_DB_Deep_Dive.md)), encrypted PII with KMS, routed payment webhooks through a PCI-scoped VPC, and proved **right to erasure** via playbook that included backup retention legal hold — not delete tax invoices."*

**Must-say keywords**: data fiduciary, purpose limitation, localization ap-south-1, tokenization, immutable audit, legal hold vs erasure, DPDP consent version.
