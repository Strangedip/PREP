# Design a Payment System

## Problem Statement

Design a payment system like Stripe or the payment backend for an e-commerce platform that can:
- Process payments (credit card, debit card, UPI, wallets)
- Handle refunds
- Ensure exactly-once payment processing (no double charges)
- Maintain financial accuracy (zero data loss)
- Support multiple currencies and payment methods
- Provide transaction history and reconciliation

---

## Step 1: Requirements

### Functional Requirements
1. **Pay-in**: Accept payments from customers (card, UPI, wallet, bank transfer)
2. **Pay-out**: Send money to merchants (settlement)
3. **Refunds**: Full and partial refunds
4. **Idempotency**: Guarantee exactly-once processing (retry-safe)
5. **Transaction history**: Complete audit trail for every transaction
6. **Reconciliation**: Match internal records with payment gateway records
7. **Multi-currency**: Support USD, INR, EUR with exchange rate handling

### Non-Functional Requirements
- **Consistency**: STRONG consistency — financial data cannot be eventual
- **Reliability**: Zero data loss, 99.999% durability
- **Availability**: 99.99% uptime
- **Latency**: Payment processing < 2 seconds
- **Security**: PCI DSS compliance, encryption at rest and in transit
- **Scale**: 1M transactions/day, peak 50 TPS

### Capacity Estimation

```
Transactions/day: 1M → ~12 TPS, peak ~50 TPS
Transaction record: ~2 KB → 1M × 2 KB = 2 GB/day → ~730 GB/year
Ledger entries: 2 per transaction (debit + credit) → 2M entries/day
Total financial records (5 years): ~3.6 TB
```

---

## Step 2: High-Level Architecture

```
┌──────────────┐     ┌──────────────────┐
│   Client     │────▶│   API Gateway    │
│ (Web/Mobile) │     │ (TLS, Rate Limit)│
└──────────────┘     └────────┬─────────┘
                               │
          ┌────────────────────┼────────────────────┐
          │                    │                     │
    ┌─────▼─────┐       ┌─────▼─────┐        ┌─────▼─────┐
    │  Payment  │       │  Ledger   │        │  Account  │
    │  Service  │       │  Service  │        │  Service  │
    └─────┬─────┘       └─────┬─────┘        └─────┬─────┘
          │                   │                     │
    ┌─────▼─────┐       ┌─────▼─────┐        ┌─────▼─────┐
    │  Payment  │       │  Ledger   │        │  Account  │
    │  DB       │       │  DB       │        │  DB       │
    │(PostgreSQL│       │(PostgreSQL│        │(PostgreSQL│
    │ + WAL)    │       │ append-   │        │           │
    └─────┬─────┘       │ only)     │        └───────────┘
          │             └───────────┘
    ┌─────▼──────────────────────────────────┐
    │  Payment Service Provider (PSP)        │
    │  Gateway (Stripe / Razorpay / Adyen)   │
    └─────┬──────────────────────────────────┘
          │
    ┌─────▼─────┐  ┌──────────────┐  ┌──────────────┐
    │ Card      │  │   UPI        │  │   Wallet     │
    │ Network   │  │   Network    │  │   Provider   │
    │(Visa/MC)  │  │   (NPCI)     │  │              │
    └───────────┘  └──────────────┘  └──────────────┘
```

---

## Step 3: Detailed Component Design

### 3.1 Payment Service — The Core

**Payment Flow (Happy Path)**:

```
1. Client sends payment request with idempotency_key
2. Payment Service checks idempotency_key in DB
   - If exists and completed → return cached result (exactly-once)
   - If exists and processing → return "in progress"
   - If not exists → create new payment record with status CREATED
3. Validate payment details (amount, currency, payment method)
4. Call PSP (Stripe/Razorpay) to process the payment
5. PSP communicates with card network / UPI network
6. PSP returns success/failure
7. Update payment record to COMPLETED or FAILED
8. Record ledger entries (double-entry bookkeeping)
9. Return result to client
```

### 3.2 Idempotency — Preventing Double Charges

This is the **most critical design concern** in a payment system.

**Problem**: Network failures cause retries. If the first request succeeded but the response was lost, the retry must NOT charge the customer again.

**Solution**: Idempotency key — a unique identifier for each payment intention.

```sql
CREATE TABLE payments (
    payment_id       UUID PRIMARY KEY,
    idempotency_key  VARCHAR(64) UNIQUE NOT NULL,  -- Client-provided
    buyer_id         BIGINT NOT NULL,
    seller_id        BIGINT NOT NULL,
    amount           DECIMAL(15, 2) NOT NULL,
    currency         VARCHAR(3) NOT NULL,
    status           VARCHAR(20) NOT NULL,  -- CREATED, PROCESSING, COMPLETED, FAILED
    payment_method   VARCHAR(30),
    psp_reference    VARCHAR(100),          -- Reference from Stripe/Razorpay
    created_at       TIMESTAMP NOT NULL,
    updated_at       TIMESTAMP NOT NULL,
    version          INT DEFAULT 0,         -- Optimistic locking
    INDEX idx_idempotency (idempotency_key),
    INDEX idx_buyer (buyer_id, created_at DESC),
    INDEX idx_seller (seller_id, created_at DESC)
);
```

**Idempotency Implementation**:

```java
@Transactional
public PaymentResult processPayment(PaymentRequest request) {
    // Step 1: Check for existing payment with same idempotency key
    Optional<Payment> existing = paymentRepo.findByIdempotencyKey(request.getIdempotencyKey());

    if (existing.isPresent()) {
        Payment payment = existing.get();
        if (payment.getStatus() == PaymentStatus.COMPLETED) {
            return PaymentResult.success(payment); // Return cached result
        }
        if (payment.getStatus() == PaymentStatus.PROCESSING) {
            return PaymentResult.inProgress(payment);
        }
        // If FAILED, allow retry by continuing below
    }

    // Step 2: Create or update payment record
    Payment payment = existing.orElse(new Payment());
    payment.setIdempotencyKey(request.getIdempotencyKey());
    payment.setAmount(request.getAmount());
    payment.setCurrency(request.getCurrency());
    payment.setStatus(PaymentStatus.PROCESSING);
    paymentRepo.save(payment);

    // Step 3: Call Payment Service Provider
    try {
        PSPResponse pspResponse = pspClient.charge(
            request.getPaymentMethod(),
            request.getAmount(),
            request.getCurrency()
        );

        // Step 4: Update status based on PSP response
        payment.setStatus(PaymentStatus.COMPLETED);
        payment.setPspReference(pspResponse.getReferenceId());
        paymentRepo.save(payment);

        // Step 5: Record ledger entries
        ledgerService.recordPayment(payment);

        return PaymentResult.success(payment);

    } catch (PSPException e) {
        payment.setStatus(PaymentStatus.FAILED);
        payment.setFailureReason(e.getMessage());
        paymentRepo.save(payment);
        return PaymentResult.failed(payment, e.getMessage());
    }
}
```

### 3.3 Double-Entry Ledger — Financial Accuracy

Every financial transaction creates TWO ledger entries: a debit and a credit. This ensures the books always balance (total debits = total credits).

```sql
CREATE TABLE ledger_entries (
    entry_id         BIGSERIAL PRIMARY KEY,
    payment_id       UUID NOT NULL REFERENCES payments(payment_id),
    account_id       BIGINT NOT NULL,
    entry_type       VARCHAR(10) NOT NULL,  -- DEBIT or CREDIT
    amount           DECIMAL(15, 2) NOT NULL,
    currency         VARCHAR(3) NOT NULL,
    balance_after    DECIMAL(15, 2) NOT NULL,
    created_at       TIMESTAMP NOT NULL,
    INDEX idx_account (account_id, created_at DESC)
);

-- This table is APPEND-ONLY. No updates, no deletes. Ever.
-- To reverse a transaction, create new REVERSE entries.
```

**Example**: Customer pays merchant $100:

| Entry | Account | Type | Amount |
|-------|---------|------|--------|
| 1 | Customer account | DEBIT | -$100 |
| 2 | Merchant account | CREDIT | +$100 |

**Invariant**: `SUM(all entries) = 0` (debits and credits always balance).

### 3.4 Payment State Machine

```
                    ┌──────────┐
                    │ CREATED  │
                    └────┬─────┘
                         │ validate
                    ┌────▼─────┐
              ┌─────│PROCESSING│─────┐
              │     └──────────┘     │
        success│                     │failure
              │                      │
        ┌─────▼─────┐         ┌──────▼─────┐
        │ COMPLETED │         │  FAILED    │
        └─────┬─────┘         └────────────┘
              │ refund request
        ┌─────▼─────┐
        │ REFUNDING │
        └─────┬─────┘
              │
        ┌─────▼─────┐
        │ REFUNDED  │
        └───────────┘
```

**Each transition**:
1. Must be recorded in an audit log.
2. Must update the ledger (for COMPLETED and REFUNDED).
3. Must use optimistic locking to prevent concurrent state transitions.

### 3.5 Reconciliation Service

**Why**: PSP records and our records might diverge (network failures, partial processing). Reconciliation ensures they match.

**Process** (runs daily):
1. Download settlement file from PSP (all transactions for the day).
2. Compare each PSP transaction with our internal records.
3. Flag discrepancies:
   - **Missing in our DB**: PSP processed it, but we have no record → investigate.
   - **Missing in PSP**: We recorded it, but PSP has no record → might need to void.
   - **Amount mismatch**: Different amounts → investigate and correct.
4. Generate reconciliation report.

### 3.6 Refund Processing

```java
@Transactional
public RefundResult processRefund(String paymentId, BigDecimal refundAmount) {
    Payment payment = paymentRepo.findById(paymentId)
        .orElseThrow(() -> new PaymentNotFoundException(paymentId));

    if (payment.getStatus() != PaymentStatus.COMPLETED) {
        throw new InvalidRefundException("Can only refund completed payments");
    }

    BigDecimal totalRefunded = refundRepo.getTotalRefundedForPayment(paymentId);
    if (totalRefunded.add(refundAmount).compareTo(payment.getAmount()) > 0) {
        throw new InvalidRefundException("Refund exceeds original payment amount");
    }

    // Create refund record
    Refund refund = new Refund(paymentId, refundAmount, RefundStatus.PROCESSING);
    refundRepo.save(refund);

    // Call PSP to process refund
    PSPResponse response = pspClient.refund(payment.getPspReference(), refundAmount);

    refund.setStatus(RefundStatus.COMPLETED);
    refundRepo.save(refund);

    // Record reverse ledger entries
    ledgerService.recordRefund(payment, refundAmount);

    return RefundResult.success(refund);
}
```

---

## Step 4: API Design

```
POST   /api/v1/payments
  Headers: Idempotency-Key: "order_12345_payment_1"
  Body: {
    "amount": 9999,           // Amount in smallest currency unit (cents/paise)
    "currency": "USD",
    "payment_method": "card_pm_123",
    "seller_id": "merchant_456",
    "metadata": { "order_id": "order_12345" }
  }
  Response: 201 {
    "payment_id": "pay_abc123",
    "status": "COMPLETED",
    "amount": 9999,
    "currency": "USD"
  }

GET    /api/v1/payments/{payment_id}
POST   /api/v1/payments/{payment_id}/refund
  Body: { "amount": 5000, "reason": "customer_request" }

GET    /api/v1/payments?buyer_id=123&from=2026-01-01&to=2026-02-01&cursor=...
```

---

## Step 5: Security

### PCI DSS Compliance

| Requirement | Implementation |
|-------------|---------------|
| **Never store full card numbers** | Tokenize via PSP; store only last 4 digits |
| **Encrypt data at rest** | AES-256 encryption for sensitive fields |
| **Encrypt in transit** | TLS 1.3 for all communication |
| **Access control** | Role-based access; only Payment Service talks to PSP |
| **Audit logging** | Log every access to payment data with timestamp and user |
| **Network segmentation** | Payment Service in isolated network segment |

### Fraud Detection

```
For each payment:
1. Velocity checks: > 5 payments in 1 minute from same card? → Flag
2. Amount checks: Amount significantly higher than user's average? → Flag
3. Geolocation: Payment from unusual location? → Flag
4. Device fingerprinting: New device? → Require additional verification
5. ML model: Score transaction risk (0-100). Above threshold → manual review.
```

---

## Step 6: Scaling and Reliability

### Database Choices

| Component | Database | Why |
|-----------|----------|-----|
| Payments | PostgreSQL (primary) + read replicas | ACID, strong consistency, reliable |
| Ledger | PostgreSQL (append-only, no updates) | Audit trail, financial accuracy |
| Accounts/Balances | PostgreSQL with row-level locking | Balance updates need ACID |
| Event Store | Kafka | Async processing, event sourcing |
| Cache | Redis | Idempotency key dedup, rate limiting |

### Handling Failures

| Failure Point | Strategy |
|--------------|----------|
| PSP timeout | Retry with same idempotency key (safe due to idempotency) |
| Our DB write fails after PSP success | Reconciliation catches it; also use transactional outbox |
| PSP returns ambiguous response | Query PSP for payment status before retrying |
| Partial system failure | Saga pattern: compensate (refund) if downstream fails |
| Network partition | Queue payment for later processing; prefer consistency over availability |

### Transactional Outbox Pattern

To reliably publish events after a payment is processed:

```
1. Within a DB transaction:
   - Update payment status to COMPLETED
   - Insert event into "outbox" table

2. Separate process (Debezium CDC or polling):
   - Read events from outbox table
   - Publish to Kafka
   - Mark event as published
```

This ensures that the DB update and the event publication are atomic — either both happen or neither happens.

---

## Step 7: Monitoring

### Key Metrics

| Metric | Target | Alert |
|--------|--------|-------|
| Payment success rate | > 98% | < 95% |
| Payment latency (p99) | < 2s | > 5s |
| Refund processing time | < 5 days | > 7 days |
| Reconciliation match rate | > 99.99% | < 99.9% |
| Fraud detection rate | > 95% | N/A |
| False positive rate | < 5% | > 10% |

---

## Interview Discussion Points

1. **Why idempotency is critical** — Network retries are inevitable. Without idempotency keys, a retry after a timeout could charge the customer twice. The idempotency key guarantees exactly-once semantics.
2. **Why double-entry ledger?** — It is the only way to guarantee financial accuracy. Every debit has a corresponding credit. If books do not balance, there is a bug. This is not a nice-to-have — it is required by financial regulations.
3. **Why PostgreSQL over NoSQL?** — Financial data requires ACID transactions, strong consistency, and complex queries (reconciliation, reporting). NoSQL databases sacrifice consistency, which is unacceptable for money.
4. **How to handle PSP outages?** — Queue payments, fail gracefully, retry. For time-sensitive payments, have a secondary PSP as fallback.
5. **Consistency vs Availability trade-off** — Payment systems choose CP (consistency + partition tolerance). We would rather reject a payment than process it incorrectly. This is the opposite of most social media systems (which choose AP).
6. **Why append-only ledger?** — Audit requirements. You never delete or modify a ledger entry. To correct an error, create a new reversing entry. This provides a complete, tamper-evident history.

---

**Difficulty**: Hard
**Frequency**: High — common at fintech companies, Amazon, Google, Stripe
**Key Patterns**: Idempotency, Double-Entry Ledger, State Machine, Transactional Outbox, Reconciliation, PCI DSS

