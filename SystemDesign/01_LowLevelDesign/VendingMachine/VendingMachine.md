# Vending Machine — Low-Level Design

## Problem Statement

Design a vending machine that supports:

- **Product selection** from a display of items with prices
- **Coin/note insertion** with running total displayed
- **Product dispensing** when sufficient money is inserted
- **Change return** calculation using greedy coin algorithm
- **Refund** if user cancels before dispensing
- **Inventory management** with out-of-stock handling
- **Maintenance mode** for restocking and collecting cash

This is a classic **State Machine** problem. The vending machine transitions between well-defined states, and behavior varies per state.

---

## Design Patterns Used

| Pattern | Where | Why |
|---------|-------|-----|
| **State** | `VendingMachineState` interface | Machine behavior changes based on current state (Idle, HasMoney, Dispensing, Maintenance) |
| **Strategy** | `ChangeCalculationStrategy` | Different algorithms for making change (Greedy, Exact) |
| **Observer** | Admin alerts | Notify when inventory is low or cash box is full |

---

## State Machine Diagram

```
                    insertCoin()
    ┌─────────┐  ──────────────▶  ┌──────────┐
    │  IDLE   │                   │ HAS_MONEY│
    │         │  ◀──────────────  │          │
    └─────────┘     refund()      └──────────┘
         │                             │
         │ selectProduct()             │ selectProduct()
         │ (free item?!)               │ (enough money)
         ▼                             ▼
    ┌──────────┐                 ┌──────────────┐
    │DISPENSING│ ───────────────▶│    IDLE      │
    │          │  dispenseComplete│(return change)│
    └──────────┘                 └──────────────┘

    Any State ──── enterMaintenance() ────▶ ┌─────────────┐
                                            │ MAINTENANCE  │
    ┌─────────────┐ ◀─── exitMaintenance() │             │
    │    IDLE     │                         └─────────────┘
    └─────────────┘
```

---

## Complete Java Implementation

### Product and Inventory

```java
public record Product(String code, String name, double price) {
    public Product {
        if (price < 0) throw new IllegalArgumentException("Price cannot be negative");
        if (code == null || code.isBlank()) throw new IllegalArgumentException("Code required");
    }
}

public class Slot {
    private final String slotId;    // e.g., "A1", "B3"
    private final Product product;
    private int quantity;
    private final int maxCapacity;

    public Slot(String slotId, Product product, int quantity, int maxCapacity) {
        this.slotId = slotId;
        this.product = product;
        this.quantity = quantity;
        this.maxCapacity = maxCapacity;
    }

    public boolean isAvailable() {
        return quantity > 0;
    }

    public void dispense() {
        if (quantity <= 0) throw new IllegalStateException("Slot is empty");
        quantity--;
    }

    public void restock(int amount) {
        quantity = Math.min(quantity + amount, maxCapacity);
    }

    // Getters
    public String getSlotId() { return slotId; }
    public Product getProduct() { return product; }
    public int getQuantity() { return quantity; }
    public int getMaxCapacity() { return maxCapacity; }
}
```

### Coin/Note Types

```java
public enum Denomination {
    PENNY(0.01),
    NICKEL(0.05),
    DIME(0.10),
    QUARTER(0.25),
    DOLLAR(1.00),
    FIVE_DOLLAR(5.00),
    TEN_DOLLAR(10.00),
    TWENTY_DOLLAR(20.00);

    private final double value;

    Denomination(double value) {
        this.value = value;
    }

    public double getValue() { return value; }
}
```

### Change Calculation Strategy

```java
public interface ChangeCalculationStrategy {
    /**
     * Calculate the coins/notes to return as change.
     * Returns a map of denomination → count.
     * Throws exception if exact change cannot be made.
     */
    Map<Denomination, Integer> calculateChange(
        double changeAmount, Map<Denomination, Integer> availableCoins
    );
}

public class GreedyChangeStrategy implements ChangeCalculationStrategy {
    @Override
    public Map<Denomination, Integer> calculateChange(
            double changeAmount, Map<Denomination, Integer> availableCoins) {

        Map<Denomination, Integer> change = new LinkedHashMap<>();
        double remaining = Math.round(changeAmount * 100.0) / 100.0; // Avoid floating point issues

        // Sort denominations in descending order
        Denomination[] sorted = Denomination.values();
        Arrays.sort(sorted, (a, b) -> Double.compare(b.getValue(), a.getValue()));

        for (Denomination denom : sorted) {
            if (remaining <= 0) break;

            int available = availableCoins.getOrDefault(denom, 0);
            int needed = (int) (remaining / denom.getValue());
            int toUse = Math.min(needed, available);

            if (toUse > 0) {
                change.put(denom, toUse);
                remaining = Math.round((remaining - toUse * denom.getValue()) * 100.0) / 100.0;
            }
        }

        if (remaining > 0.001) {
            throw new InsufficientChangeException(
                "Cannot make exact change. Short by $" + String.format("%.2f", remaining)
            );
        }

        return change;
    }
}
```

### State Interface and Implementations

```java
public interface VendingMachineState {
    void insertMoney(VendingMachine machine, Denomination denomination);
    void selectProduct(VendingMachine machine, String slotId);
    void refund(VendingMachine machine);
    void enterMaintenance(VendingMachine machine);
    void exitMaintenance(VendingMachine machine);
}

// ─── IDLE State: Waiting for user ───
public class IdleState implements VendingMachineState {
    @Override
    public void insertMoney(VendingMachine machine, Denomination denomination) {
        machine.addToCurrentBalance(denomination.getValue());
        machine.addToAvailableCoins(denomination);
        System.out.printf("Inserted $%.2f. Balance: $%.2f%n",
            denomination.getValue(), machine.getCurrentBalance());
        machine.setState(new HasMoneyState());
    }

    @Override
    public void selectProduct(VendingMachine machine, String slotId) {
        System.out.println("Please insert money first.");
    }

    @Override
    public void refund(VendingMachine machine) {
        System.out.println("No money to refund.");
    }

    @Override
    public void enterMaintenance(VendingMachine machine) {
        machine.setState(new MaintenanceState());
        System.out.println("Entered maintenance mode.");
    }

    @Override
    public void exitMaintenance(VendingMachine machine) {
        System.out.println("Not in maintenance mode.");
    }
}

// ─── HAS_MONEY State: User has inserted money ───
public class HasMoneyState implements VendingMachineState {
    @Override
    public void insertMoney(VendingMachine machine, Denomination denomination) {
        machine.addToCurrentBalance(denomination.getValue());
        machine.addToAvailableCoins(denomination);
        System.out.printf("Inserted $%.2f. Balance: $%.2f%n",
            denomination.getValue(), machine.getCurrentBalance());
    }

    @Override
    public void selectProduct(VendingMachine machine, String slotId) {
        Slot slot = machine.getSlot(slotId);
        if (slot == null) {
            System.out.println("Invalid slot: " + slotId);
            return;
        }

        if (!slot.isAvailable()) {
            System.out.println("Product '" + slot.getProduct().name() + "' is out of stock.");
            return;
        }

        double price = slot.getProduct().price();
        if (machine.getCurrentBalance() < price) {
            System.out.printf("Insufficient funds. Price: $%.2f, Balance: $%.2f. Insert $%.2f more.%n",
                price, machine.getCurrentBalance(), price - machine.getCurrentBalance());
            return;
        }

        // Sufficient funds — transition to dispensing
        machine.setState(new DispensingState());
        machine.dispenseProduct(slot);
    }

    @Override
    public void refund(VendingMachine machine) {
        double balance = machine.getCurrentBalance();
        machine.returnChange(balance);
        machine.resetCurrentBalance();
        machine.setState(new IdleState());
        System.out.printf("Refunded $%.2f%n", balance);
    }

    @Override
    public void enterMaintenance(VendingMachine machine) {
        // Refund first, then enter maintenance
        refund(machine);
        machine.setState(new MaintenanceState());
        System.out.println("Entered maintenance mode.");
    }

    @Override
    public void exitMaintenance(VendingMachine machine) {
        System.out.println("Not in maintenance mode.");
    }
}

// ─── DISPENSING State: Product being dispensed ───
public class DispensingState implements VendingMachineState {
    @Override
    public void insertMoney(VendingMachine machine, Denomination denomination) {
        System.out.println("Please wait, dispensing in progress...");
    }

    @Override
    public void selectProduct(VendingMachine machine, String slotId) {
        System.out.println("Please wait, dispensing in progress...");
    }

    @Override
    public void refund(VendingMachine machine) {
        System.out.println("Cannot refund during dispensing.");
    }

    @Override
    public void enterMaintenance(VendingMachine machine) {
        System.out.println("Cannot enter maintenance during dispensing.");
    }

    @Override
    public void exitMaintenance(VendingMachine machine) {
        System.out.println("Not in maintenance mode.");
    }
}

// ─── MAINTENANCE State: Restocking or cash collection ───
public class MaintenanceState implements VendingMachineState {
    @Override
    public void insertMoney(VendingMachine machine, Denomination denomination) {
        System.out.println("Machine is in maintenance mode.");
    }

    @Override
    public void selectProduct(VendingMachine machine, String slotId) {
        System.out.println("Machine is in maintenance mode.");
    }

    @Override
    public void refund(VendingMachine machine) {
        System.out.println("Machine is in maintenance mode.");
    }

    @Override
    public void enterMaintenance(VendingMachine machine) {
        System.out.println("Already in maintenance mode.");
    }

    @Override
    public void exitMaintenance(VendingMachine machine) {
        machine.setState(new IdleState());
        System.out.println("Exited maintenance mode. Machine is ready.");
    }
}
```

### Vending Machine (Context)

```java
import java.util.*;

public class VendingMachine {
    private final Map<String, Slot> slots;
    private final Map<Denomination, Integer> availableCoins;
    private final ChangeCalculationStrategy changeStrategy;
    private VendingMachineState currentState;
    private double currentBalance;

    public VendingMachine(ChangeCalculationStrategy changeStrategy) {
        this.slots = new LinkedHashMap<>();
        this.availableCoins = new EnumMap<>(Denomination.class);
        this.changeStrategy = changeStrategy;
        this.currentState = new IdleState();
        this.currentBalance = 0;

        // Initialize with some coins for change
        for (Denomination d : Denomination.values()) {
            availableCoins.put(d, 10);
        }
    }

    // ─── Public operations (delegated to current state) ───

    public void insertMoney(Denomination denomination) {
        currentState.insertMoney(this, denomination);
    }

    public void selectProduct(String slotId) {
        currentState.selectProduct(this, slotId);
    }

    public void refund() {
        currentState.refund(this);
    }

    public void enterMaintenance() {
        currentState.enterMaintenance(this);
    }

    public void exitMaintenance() {
        currentState.exitMaintenance(this);
    }

    // ─── Admin operations (only in maintenance mode) ───

    public void addSlot(String slotId, Product product, int quantity, int maxCapacity) {
        slots.put(slotId, new Slot(slotId, product, quantity, maxCapacity));
    }

    public void restockSlot(String slotId, int amount) {
        Slot slot = slots.get(slotId);
        if (slot == null) throw new IllegalArgumentException("Slot not found: " + slotId);
        slot.restock(amount);
        System.out.printf("Restocked %s with %d items. Total: %d%n",
            slotId, amount, slot.getQuantity());
    }

    // ─── Internal methods (called by states) ───

    void dispenseProduct(Slot slot) {
        double price = slot.getProduct().price();
        double change = Math.round((currentBalance - price) * 100.0) / 100.0;

        slot.dispense();
        System.out.printf("🎉 Dispensed: %s ($%.2f)%n", slot.getProduct().name(), price);

        if (change > 0) {
            returnChange(change);
        }

        resetCurrentBalance();
        setState(new IdleState());
    }

    void returnChange(double amount) {
        try {
            Map<Denomination, Integer> change = changeStrategy.calculateChange(amount, availableCoins);
            System.out.printf("Returning change: $%.2f → %s%n", amount, formatChange(change));

            // Deduct coins from available pool
            for (Map.Entry<Denomination, Integer> entry : change.entrySet()) {
                availableCoins.merge(entry.getKey(), -entry.getValue(), Integer::sum);
            }
        } catch (InsufficientChangeException e) {
            System.out.println("Warning: " + e.getMessage() + ". Returning full refund.");
            // In production, this would trigger an admin alert
        }
    }

    private String formatChange(Map<Denomination, Integer> change) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<Denomination, Integer> entry : change.entrySet()) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(entry.getValue()).append("x ").append(entry.getKey().name());
        }
        return sb.toString();
    }

    // ─── State management ───

    void setState(VendingMachineState state) {
        this.currentState = state;
    }

    void addToCurrentBalance(double amount) {
        this.currentBalance = Math.round((currentBalance + amount) * 100.0) / 100.0;
    }

    void resetCurrentBalance() {
        this.currentBalance = 0;
    }

    void addToAvailableCoins(Denomination denomination) {
        availableCoins.merge(denomination, 1, Integer::sum);
    }

    // Getters
    public Slot getSlot(String slotId) { return slots.get(slotId); }
    public double getCurrentBalance() { return currentBalance; }
    public Map<String, Slot> getSlots() { return Collections.unmodifiableMap(slots); }

    public void displayProducts() {
        System.out.println("╔══════════════════════════════════════╗");
        System.out.println("║        VENDING MACHINE               ║");
        System.out.println("╠══════════════════════════════════════╣");
        for (Slot slot : slots.values()) {
            String availability = slot.isAvailable() ? "✓" : "✗ SOLD OUT";
            System.out.printf("║  [%s] %-15s $%.2f  %s%n",
                slot.getSlotId(), slot.getProduct().name(),
                slot.getProduct().price(), availability);
        }
        System.out.println("╚══════════════════════════════════════╝");
    }
}

public class InsufficientChangeException extends RuntimeException {
    public InsufficientChangeException(String message) {
        super(message);
    }
}
```

### Main

```java
public class VendingMachineDemo {
    public static void main(String[] args) {
        VendingMachine machine = new VendingMachine(new GreedyChangeStrategy());

        // Add products
        machine.addSlot("A1", new Product("COLA", "Coca Cola", 1.50), 10, 20);
        machine.addSlot("A2", new Product("PEPSI", "Pepsi", 1.50), 10, 20);
        machine.addSlot("B1", new Product("WATER", "Water Bottle", 1.00), 5, 20);
        machine.addSlot("B2", new Product("CHIPS", "Lays Chips", 2.00), 8, 15);
        machine.addSlot("C1", new Product("SNKRS", "Snickers", 1.75), 12, 20);

        machine.displayProducts();

        // User interaction
        System.out.println("\n--- User buys Coca Cola with $2 ---");
        machine.insertMoney(Denomination.DOLLAR);
        machine.insertMoney(Denomination.DOLLAR);
        machine.selectProduct("A1");

        System.out.println("\n--- User tries to buy without money ---");
        machine.selectProduct("B1");

        System.out.println("\n--- User inserts money then cancels ---");
        machine.insertMoney(Denomination.FIVE_DOLLAR);
        machine.refund();

        System.out.println("\n--- Maintenance: restock ---");
        machine.enterMaintenance();
        machine.restockSlot("B1", 15);
        machine.exitMaintenance();
    }
}
```

---

## Interview Discussion Points

1. **Why use the State pattern instead of if/else chains?**
   - Without the State pattern, every method would have a switch/if-else on `currentState`. Adding a new state (e.g., `OUT_OF_ORDER`) would require modifying every method — violating the **Open/Closed Principle**. With the State pattern, you simply add a new `VendingMachineState` implementation.

2. **How do you handle floating-point precision?**
   - We use `Math.round(value * 100.0) / 100.0` for every arithmetic operation. In production, you'd use `BigDecimal` instead of `double` for all money calculations.

3. **How would you make this thread-safe?**
   - Use `synchronized` on the `VendingMachine` methods, or better, use a `ReentrantLock`. In a real vending machine, only one user interacts at a time, so a single lock suffices.

4. **What if the machine runs out of change?**
   - The `ChangeCalculationStrategy` throws `InsufficientChangeException`. The machine would display "EXACT CHANGE ONLY" and the admin would be notified via the Observer pattern.

