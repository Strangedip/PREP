# ATM Machine — Low Level Design

## Problem Statement

Design an ATM system that:

- **Authenticates** users via card + PIN
- **Supports operations**: balance inquiry, cash withdrawal, deposit (simplified)
- **Manages cash inventory** in the machine
- **Records transactions** with audit trail
- **Handles concurrent access** to one ATM (single user at a time)

---

## Requirements

### Functional Requirements

1. **Card insertion** — Read card, validate, prompt PIN (max 3 attempts)
2. **Balance inquiry** — Show available balance
3. **Withdrawal** — Dispense cash if sufficient balance and ATM cash
4. **Deposit** — Accept cash (simplified: immediate credit)
5. **Session** — Timeout after inactivity; eject card on end
6. **Receipt** — Optional transaction record

### Non-Functional Requirements

1. **Security** — PIN never stored on card; encrypted communication with bank
2. **Consistency** — No double withdrawal; atomic balance updates
3. **Extensibility** — New transaction types via strategy pattern

---

## Design Patterns

| Pattern | Use |
|---------|-----|
| **State** | ATM states: Idle, HasCard, Authenticated, Transaction |
| **Strategy** | Different transaction types (Withdraw, Deposit, Balance) |
| **Singleton** | ATM controller (one session per machine) |
| **Chain of Responsibility** | PIN validation → balance check → dispense |

---

## Class Diagram

```
┌─────────────┐       ┌──────────────┐       ┌─────────────────┐
│    Card     │       │  BankService │       │ CashDispenser   │
├─────────────┤       │  (interface) │       ├─────────────────┤
│ cardNumber  │       ├──────────────┤       │ denominations   │
│ pinHash     │       │ validatePin  │       │ dispense(amount)│
└─────────────┘       │ getBalance   │       └─────────────────┘
       │              │ withdraw     │
       │              │ deposit      │       ┌─────────────────┐
       ▼              └──────┬───────┘       │ Transaction     │
┌─────────────┐              │               ├─────────────────┤
│ ATM         │◀─────────────┘               │ id, type, amount│
│ (State)     │                              │ timestamp       │
├─────────────┤       ┌──────────────┐       └─────────────────┘
│ currentState│       │ Transaction  │
│ cardReader  │       │ Strategy     │
│ keypad      │       │ (interface)  │
└─────────────┘       └──────────────┘
```

---

## Core APIs

```java
public interface ATMState {
    void insertCard(Card card);
    void enterPin(String pin);
    void selectTransaction(TransactionType type);
    void enterAmount(long cents);
    void cancel();
}

public interface TransactionStrategy {
    TransactionResult execute(Account account, long amount);
}

public class WithdrawStrategy implements TransactionStrategy {
  public TransactionResult execute(Account account, long amount) {
    if (account.getBalance() < amount) return TransactionResult.insufficientFunds();
    if (!cashDispenser.canDispense(amount)) return TransactionResult.noCash();
  account.debit(amount);
    cashDispenser.dispense(amount);
    return TransactionResult.success();
  }
}
```

---

## State Machine

```
Idle ──insert card──▶ HasCard ──valid PIN──▶ Authenticated ──select op──▶ Processing
  ▲                        │                        │                         │
  │                        │ invalid PIN x3         │                         │
  │                        ▼                        │                         │
  │                   Eject Card                    │                         │
  │                                                 ▼                         ▼
  └──────────── eject / timeout / complete ───────────────────────────────────┘
```

---

## Edge Cases

| Case | Handling |
|------|----------|
| Wrong PIN 3 times | Block card, eject, log security event |
| Withdraw > balance | Reject before debit |
| Withdraw > ATM cash | Reject; optional partial if denominations allow |
| Power failure mid-withdraw | Idempotent transaction ID; reconcile on restart |
| Concurrent sessions | One active session per ATM (mutex on ATM instance) |
| Odd withdrawal amount | Only multiples of smallest denomination (e.g., $20) |

---

## Interview Discussion Points

1. **State pattern** vs giant if-else for ATM flow
2. **Distributed ATM** — Each ATM talks to central bank service; not LLD scope but mention
3. **PIN security** — Hash on server; 3 attempts lockout
4. **Cash dispenser** — Greedy algorithm for denominations (200 → 100+100 vs 50×4)
5. **Transaction log** — Append-only for audit (regulated systems → [§38 Compliance](../../01_TechGuide/38_Compliance_and_Regulated_Systems.md))

**Patterns reference**: [ParkingLot](../ParkingLot/ParkingLot.md), [VendingMachine](../VendingMachine/VendingMachine.md)
