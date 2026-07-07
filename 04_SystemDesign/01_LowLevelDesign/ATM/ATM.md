# ATM Machine — Low Level Design

> **You are here**: SDE1 — System Design (LLD)
> **Roadmap**: [Developer Master Roadmap](../../../ROADMAP.md) | **Prerequisites**: [LLD Template](../../00_Templates/LLD_Template/LLD_Template.md), [GoF Patterns](../../../03_CodingPatterns/01_Patterns.md), [Parking Lot](../ParkingLot/ParkingLot.md) | **Next**: [Vending Machine](../VendingMachine/VendingMachine.md), [BookMyShow](../BookMyShow/BookMyShow.md)

## Problem Statement

Design an ATM system that:

- **Authenticates** users via card + PIN (max 3 attempts)
- **Supports operations**: balance inquiry, cash withdrawal, deposit (simplified)
- **Manages cash inventory** in the machine with denomination-aware dispensing
- **Records transactions** with an append-only audit trail
- **Handles one active session** per physical ATM (mutex on session)

---

## Requirements

### Functional Requirements

1. **Card insertion** — Read card, validate format, prompt PIN
2. **Balance inquiry** — Show available balance from bank service
3. **Withdrawal** — Debit account and dispense cash if balance and denominations allow
4. **Deposit** — Accept cash envelope (simplified: immediate credit after count)
5. **Session** — Timeout after inactivity; eject card on end or lockout
6. **Receipt** — Optional transaction record per operation

### Non-Functional Requirements

1. **Security** — PIN verified server-side; never log raw PIN
2. **Consistency** — No double withdrawal; atomic debit + dispense or rollback
3. **Auditability** — Append-only transaction log for reconciliation
4. **Extensibility** — New transaction types via Strategy without changing state machine

### Out of scope (mention in interview)

- Multi-ATM fleet routing, key exchange with HSM, chip-card EMV protocol

---

## Design Patterns

| Pattern | Use | Alternative rejected |
|---------|-----|----------------------|
| **State** | ATM session states (Idle → HasCard → Authenticated → Processing) | Giant if-else — hard to test, easy to break invariants |
| **Strategy** | Withdraw / Deposit / Balance operations | Switch on enum — violates Open/Closed |
| **Singleton** | One `ATMController` per physical machine | Spring `@Scope` prototype — overkill for LLD |
| **Chain of Responsibility** | PIN → balance → denomination check before dispense | Single method — interviewers ask for separation |

---

## Trade-off Tables

### State pattern vs single controller method

| Approach | Pros | Cons | When to choose |
|----------|------|------|----------------|
| **State pattern** | Invalid transitions impossible; each state testable | More classes | ATM, vending machine, any multi-step FSM |
| **Single method + enum** | Fewer files | Easy to call `dispense()` from Idle | Only trivial demos |

### Cash denomination algorithm

| Approach | Pros | Cons | When to choose |
|----------|------|------|----------------|
| **Greedy (largest first)** | O(denominations), simple | Fails for some coin sets (e.g. {1,3,4} amount 6) | Standard ATM bills {100, 50, 20, 10} — greedy works |
| **DP (unbounded knapsack)** | Optimal minimum note count always | O(amount × denominations) | Unusual denomination sets |
| **Reject non-standard amounts** | Simplest UX | User frustration | Production ATMs often only dispense multiples of $20 |

**Interview answer**: Use greedy for standard bill sets; mention DP if interviewer gives a trick denomination set.

### Bank communication

| Approach | Pros | Cons |
|----------|------|------|
| **Synchronous RPC per transaction** | Strong consistency | Latency, availability coupling |
| **Authorize + async settle** | Faster UX | Reconciliation complexity |
| **Local cache of balance** | Offline resilience | Stale balance risk — never for withdrawal without auth |

**LLD default**: synchronous `BankService` interface; mention idempotent `transactionId` for retries.

---

## Class Diagram

```
┌─────────────┐       ┌──────────────┐       ┌─────────────────┐
│    Card     │       │  BankService │       │ CashDispenser   │
├─────────────┤       │  (interface) │       ├─────────────────┤
│ cardNumber  │       ├──────────────┤       │ Map<Denom,Int>  │
│ accountId   │       │ validatePin  │       │ canDispense()   │
└─────────────┘       │ getBalance   │       │ dispense()      │
       │              │ debit/credit │       └─────────────────┘
       │              └──────┬───────┘
       ▼                     │
┌─────────────┐              │       ┌─────────────────┐
│ ATMController│◀─────────────┘       │ TransactionLog  │
│ (Singleton)  │                      │ (append-only)   │
├─────────────┤       ┌──────────────┐└─────────────────┘
│ ATMState    │       │ Transaction  │
│ cardReader  │       │ Strategy     │
│ pinAttempts │       │ (interface)  │
└─────────────┘       └──────────────┘
```

---

## State Machine

```
Idle ──insert card──▶ HasCard ──valid PIN──▶ Authenticated ──select op──▶ Processing
  ▲                        │                        │                         │
  │                   invalid PIN                  │                         │
  │                   (3 strikes)                  │                         │
  │                        ▼                        │                         │
  │                   Eject + Lock                   │                         │
  │                                                 ▼                         ▼
  └──────────── eject / timeout / complete ───────────────────────────────────┘
```

**Invalid transitions to reject in code**: `withdraw()` from `Idle`; `enterPin()` from `Authenticated` without logout.

---

## Core APIs

```java
public enum ATMStateType { IDLE, HAS_CARD, AUTHENTICATED, PROCESSING }

public interface ATMState {
    void insertCard(Card card);
    void enterPin(String pin);
    void selectTransaction(TransactionType type);
    void enterAmount(long cents);
    void cancel();
    ATMStateType type();
}

public interface BankService {
    boolean validatePin(String accountId, String pin);
    long getBalance(String accountId);
    boolean debit(String accountId, long cents, String transactionId);
    boolean credit(String accountId, long cents, String transactionId);
}

public interface TransactionStrategy {
    TransactionResult execute(AccountSession session, long amountCents);
}

public class WithdrawStrategy implements TransactionStrategy {
    private final BankService bank;
    private final CashDispenser dispenser;
    private final TransactionLog log;

    @Override
    public TransactionResult execute(AccountSession session, long amountCents) {
        String txId = session.newTransactionId();
        if (amountCents % 2000 != 0) { // e.g. $20 minimum bill
            return TransactionResult.invalidAmount();
        }
        if (!dispenser.canDispense(amountCents)) {
            return TransactionResult.insufficientCash();
        }
        if (!bank.debit(session.accountId(), amountCents, txId)) {
            return TransactionResult.insufficientFunds();
        }
        try {
            dispenser.dispense(amountCents);
            log.append(txId, TransactionType.WITHDRAW, amountCents);
            return TransactionResult.success(txId);
        } catch (Exception e) {
            bank.credit(session.accountId(), amountCents, txId + "-REVERSAL");
            throw e;
        }
    }
}
```

### Denomination greedy dispense

```java
public class CashDispenser {
    private static final int[] BILLS_CENTS = {10000, 5000, 2000, 1000}; // $100..$10
    private final Map<Integer, Integer> inventory = new HashMap<>();

    public boolean canDispense(long amountCents) {
        return computeGreedy(amountCents, inventory) != null;
    }

    public void dispense(long amountCents) {
        Map<Integer, Integer> plan = computeGreedy(amountCents, inventory);
        if (plan == null) throw new IllegalStateException("Cannot dispense");
        plan.forEach((denom, count) -> inventory.merge(denom, -count, Integer::sum));
    }

    /** Returns bill counts per denomination, or null if impossible. */
    static Map<Integer, Integer> computeGreedy(long remaining, Map<Integer, Integer> stock) {
        Map<Integer, Integer> plan = new HashMap<>();
        for (int denom : BILLS_CENTS) {
            int available = stock.getOrDefault(denom, 0);
            int need = (int) Math.min(available, remaining / denom);
            if (need > 0) {
                plan.put(denom, need);
                remaining -= (long) need * denom;
            }
        }
        return remaining == 0 ? plan : null;
    }
}
```

---

## Edge Cases

| Case | Handling |
|------|----------|
| Wrong PIN 3 times | Increment `pinAttempts`; on 3 → `BankService.lockCard()`, eject, security log |
| Withdraw > balance | `BankService.debit` returns false before dispense |
| Withdraw > ATM cash | `canDispense` false; do not debit |
| Power failure after debit, before dispense | Idempotent `transactionId`; reconciliation job completes or reverses |
| Concurrent users | `synchronized` on `ATMController` or `ReentrantLock` — one session |
| Odd withdrawal ($15) | Reject unless machine supports $5 bills |
| Deposit envelope | Simplified: operator count → `credit`; production uses escrow state |

---

## Interview Discussion Points

1. **Why State over enum + switch?** — Illegal transitions become compile-time / unit-test failures
2. **Idempotency** — Same `transactionId` on retry must not double-debit ([Payment System HLD](../../02_HighLevelDesign/PaymentSystem/PaymentSystem.md) connection)
3. **Audit log** — Append-only for PCI/SOX ([§38 Compliance](../../../01_TechGuide/38_Compliance_and_Regulated_Systems.md))
4. **Greedy vs DP** — Walk through `{1,3,4}` counterexample if asked
5. **Extension** — Transfer funds = new `TransactionStrategy`; PIN change = new authenticated sub-flow

**Related designs**: [VendingMachine](../VendingMachine/VendingMachine.md) (FSM), [ParkingLot](../ParkingLot/ParkingLot.md) (Strategy), [BookMyShow](../BookMyShow/BookMyShow.md) (concurrency + holds)

---

## Sequence diagram: withdrawal happy path

```
User          ATMController    BankService    CashDispenser    TransactionLog
  │                 │               │              │                │
  │──insert card───▶│               │              │                │
  │◀──prompt PIN───│               │              │                │
  │──enter PIN─────▶│──validatePin─▶│              │                │
  │◀──menu─────────│◀──ok──────────│              │                │
  │──withdraw $100─▶│               │              │                │
  │                 │──canDispense?──────────────▶│                │
  │                 │◀──yes────────────────────────│                │
  │                 │──debit───────────────────────▶│                │
  │                 │◀──ok──────────────────────────│                │
  │                 │──dispense────────────────────▶│                │
  │◀──cash─────────│               │              │                │
  │                 │──append log──────────────────────────────────▶│
  │──eject card────▶│               │              │                │
```

---

## REST API mapping (if interviewer asks web/mobile ATM)

| Method | Endpoint | Maps to state |
|--------|----------|---------------|
| POST | `/atm/sessions` | insertCard → HasCard |
| POST | `/atm/sessions/{id}/pin` | enterPin → Authenticated |
| POST | `/atm/sessions/{id}/withdraw` | WithdrawStrategy |
| GET | `/atm/sessions/{id}/balance` | BalanceStrategy |
| DELETE | `/atm/sessions/{id}` | eject / timeout → Idle |

---

## Interview walkthrough (40 min)

1. **State machine** — draw states first (interviewers love FSM)
2. **Strategy pattern** — withdraw vs balance vs deposit
3. **Concurrency** — one session per machine (`synchronized`)
4. **Debit + dispense atomicity** — compensate on dispense failure
5. **Greedy denominations** — walk through $180 with $100/$50/$20 bills
6. **Extensions** — transfer, PIN change as new strategies
