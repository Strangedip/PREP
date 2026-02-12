# Elevator System — Low-Level Design

## Problem Statement

Design an elevator system for a building with multiple floors and multiple elevators. The system should:

- **Handle external requests** (user on a floor presses UP or DOWN button)
- **Handle internal requests** (user inside an elevator presses a floor button)
- **Optimally assign elevators** to requests to minimize wait time
- **Support multiple scheduling algorithms** (FCFS, SCAN/Elevator, LOOK, Shortest Seek Time First)
- **Handle edge cases** like overweight, door obstruction, emergency, and maintenance mode

This problem tests your ability to model state machines, apply the Strategy pattern for algorithm selection, and handle concurrent access from multiple request sources.

---

## Requirements

### Functional Requirements

1. **External Request**: A user on floor N presses UP or DOWN. The system dispatches the nearest suitable elevator.
2. **Internal Request**: A user inside elevator E presses floor N. The elevator adds floor N to its destination list.
3. **Elevator Movement**: Elevators move in a direction, stop at requested floors, open/close doors, then continue.
4. **Display**: Each floor and each elevator has a display showing the current floor and direction of each elevator.
5. **Door Control**: Doors open when the elevator arrives at a requested floor. Doors close after a timeout or when the close button is pressed. Door sensors detect obstruction.
6. **Emergency**: Emergency button stops the elevator, opens doors, and alerts building management.
7. **Maintenance**: An elevator can be taken offline for maintenance.

### Non-Functional Requirements

1. **Fairness**: No request should starve indefinitely.
2. **Efficiency**: Minimize average wait time and travel time.
3. **Safety**: Weight limit enforcement, door obstruction detection.
4. **Extensibility**: Easy to swap scheduling algorithms or add new elevator types.

---

## Design Patterns Used

| Pattern | Where | Why |
|---------|-------|-----|
| **State** | `ElevatorState` (IDLE, MOVING_UP, MOVING_DOWN, DOOR_OPEN, MAINTENANCE, EMERGENCY) | Clean state transitions with behavior per state |
| **Strategy** | `ElevatorSchedulingStrategy` | Swap scheduling algorithms (SCAN, LOOK, SSTF) at runtime |
| **Observer** | `Display` updates | Floors and elevators observe position changes |
| **Singleton** | `ElevatorController` | Single point of coordination for all elevators |
| **Command** | `Request` objects | Encapsulate requests for queuing and processing |

---

## Class Diagram

```
┌─────────────────┐       ┌──────────────────────┐
│    Building      │       │  ElevatorController  │ (Singleton)
├─────────────────┤       ├──────────────────────┤
│- floors[]        │       │- elevators[]         │
│- elevators[]     │       │- strategy            │
│- controller      │       │+ handleExternalReq() │
└─────────────────┘       │+ handleInternalReq() │
                          │+ assignElevator()    │
                          └──────────────────────┘
                                    │ uses
                                    ▼
                      ┌──────────────────────────┐
                      │ ElevatorSchedulingStrategy│ (Interface)
                      ├──────────────────────────┤
                      │+ selectElevator()         │
                      └──────────────────────────┘
                                    △
                     ┌──────────────┼───────────────┐
                     │              │               │
               ┌─────▼────┐  ┌─────▼─────┐  ┌─────▼────┐
               │   SCAN   │  │   LOOK    │  │   SSTF   │
               │ Strategy │  │ Strategy  │  │ Strategy │
               └──────────┘  └───────────┘  └──────────┘

┌──────────────────┐       ┌──────────────────┐
│    Elevator       │       │     Floor         │
├──────────────────┤       ├──────────────────┤
│- id               │       │- floorNumber      │
│- currentFloor     │       │- upButton         │
│- direction        │       │- downButton       │
│- state            │       │- display          │
│- destinationFloors│       └──────────────────┘
│- currentWeight    │
│- maxWeight        │
│- door             │
│+ addDestination() │
│+ move()           │
│+ openDoor()       │
│+ closeDoor()      │
└──────────────────┘

┌──────────────────┐       ┌──────────────────┐
│    Request        │       │      Door         │
├──────────────────┤       ├──────────────────┤
│- sourceFloor      │       │- state            │  ← OPEN | CLOSED | OPENING | CLOSING
│- direction        │       │- obstructed       │
│- timestamp        │       │+ open()           │
│- type (INT/EXT)   │       │+ close()          │
└──────────────────┘       └──────────────────┘
```

---

## Complete Java Implementation

### Enums

```java
public enum Direction {
    UP, DOWN, IDLE;
}

public enum ElevatorState {
    IDLE,           // Stationary, no pending requests
    MOVING_UP,      // Moving upward
    MOVING_DOWN,    // Moving downward
    DOOR_OPEN,      // Stopped at a floor with doors open
    MAINTENANCE,    // Taken offline
    EMERGENCY;      // Emergency stop activated
}

public enum DoorState {
    OPEN, CLOSED, OPENING, CLOSING;
}

public enum RequestType {
    EXTERNAL,  // From a floor button
    INTERNAL;  // From inside the elevator
}
```

### Request (Command Pattern)

```java
public class Request {
    private final int floor;
    private final Direction direction; // Only relevant for EXTERNAL requests
    private final RequestType type;
    private final long timestamp;

    // External request: user on floor 5 pressed UP
    public static Request external(int floor, Direction direction) {
        return new Request(floor, direction, RequestType.EXTERNAL, System.currentTimeMillis());
    }

    // Internal request: user in elevator pressed floor 8
    public static Request internal(int floor) {
        return new Request(floor, Direction.IDLE, RequestType.INTERNAL, System.currentTimeMillis());
    }

    private Request(int floor, Direction direction, RequestType type, long timestamp) {
        this.floor = floor;
        this.direction = direction;
        this.type = type;
        this.timestamp = timestamp;
    }

    public int getFloor() { return floor; }
    public Direction getDirection() { return direction; }
    public RequestType getType() { return type; }
    public long getTimestamp() { return timestamp; }

    @Override
    public String toString() {
        return String.format("Request{floor=%d, direction=%s, type=%s}", floor, direction, type);
    }
}
```

### Door

```java
public class Door {
    private DoorState state;
    private boolean obstructed;

    public Door() {
        this.state = DoorState.CLOSED;
        this.obstructed = false;
    }

    public synchronized void open() {
        if (state == DoorState.CLOSED || state == DoorState.CLOSING) {
            state = DoorState.OPENING;
            // Simulate door opening time
            state = DoorState.OPEN;
            System.out.println("  Door opened");
        }
    }

    public synchronized void close() {
        if (state == DoorState.OPEN) {
            if (obstructed) {
                System.out.println("  Door obstructed! Reopening...");
                open();
                return;
            }
            state = DoorState.CLOSING;
            state = DoorState.CLOSED;
            System.out.println("  Door closed");
        }
    }

    public void setObstructed(boolean obstructed) {
        this.obstructed = obstructed;
    }

    public DoorState getState() { return state; }
    public boolean isObstructed() { return obstructed; }
}
```

### Elevator

```java
import java.util.*;
import java.util.concurrent.ConcurrentSkipListSet;

public class Elevator {
    private final int id;
    private int currentFloor;
    private Direction direction;
    private ElevatorState state;
    private final Door door;
    private final int maxWeight; // in kg
    private int currentWeight;

    // Destinations sorted for efficient SCAN/LOOK processing
    private final TreeSet<Integer> upDestinations;   // Floors to visit going UP
    private final TreeSet<Integer> downDestinations;  // Floors to visit going DOWN

    private final int minFloor;
    private final int maxFloor;

    public Elevator(int id, int minFloor, int maxFloor, int maxWeight) {
        this.id = id;
        this.currentFloor = minFloor; // Start at ground floor
        this.direction = Direction.IDLE;
        this.state = ElevatorState.IDLE;
        this.door = new Door();
        this.maxWeight = maxWeight;
        this.currentWeight = 0;
        this.upDestinations = new TreeSet<>();
        this.downDestinations = new TreeSet<>();
        this.minFloor = minFloor;
        this.maxFloor = maxFloor;
    }

    /**
     * Add a destination floor. Determines which direction set to add to
     * based on the floor relative to current position and current direction.
     */
    public synchronized void addDestination(int floor) {
        if (floor < minFloor || floor > maxFloor) {
            throw new IllegalArgumentException("Floor " + floor + " out of range");
        }
        if (floor == currentFloor) return; // Already here

        if (floor > currentFloor) {
            upDestinations.add(floor);
        } else {
            downDestinations.add(floor);
        }

        // If idle, start moving
        if (state == ElevatorState.IDLE) {
            if (floor > currentFloor) {
                direction = Direction.UP;
                state = ElevatorState.MOVING_UP;
            } else {
                direction = Direction.DOWN;
                state = ElevatorState.MOVING_DOWN;
            }
        }
    }

    /**
     * Process one step of movement. Called by the controller's event loop.
     * The elevator moves one floor at a time.
     */
    public synchronized void step() {
        if (state == ElevatorState.MAINTENANCE || state == ElevatorState.EMERGENCY) {
            return;
        }

        if (state == ElevatorState.IDLE) {
            // Check if there are pending destinations
            if (!upDestinations.isEmpty()) {
                direction = Direction.UP;
                state = ElevatorState.MOVING_UP;
            } else if (!downDestinations.isEmpty()) {
                direction = Direction.DOWN;
                state = ElevatorState.MOVING_DOWN;
            }
            return;
        }

        if (state == ElevatorState.DOOR_OPEN) {
            door.close();
            // After closing, determine next action
            if (direction == Direction.UP && !upDestinations.isEmpty()) {
                state = ElevatorState.MOVING_UP;
            } else if (direction == Direction.DOWN && !downDestinations.isEmpty()) {
                state = ElevatorState.MOVING_DOWN;
            } else if (!upDestinations.isEmpty()) {
                direction = Direction.UP;
                state = ElevatorState.MOVING_UP;
            } else if (!downDestinations.isEmpty()) {
                direction = Direction.DOWN;
                state = ElevatorState.MOVING_DOWN;
            } else {
                direction = Direction.IDLE;
                state = ElevatorState.IDLE;
            }
            return;
        }

        // Move one floor
        if (state == ElevatorState.MOVING_UP) {
            currentFloor++;
            System.out.printf("Elevator %d → Floor %d (UP)%n", id, currentFloor);

            if (upDestinations.contains(currentFloor)) {
                upDestinations.remove(currentFloor);
                arriveAtFloor();
            } else if (upDestinations.isEmpty()) {
                // Reverse direction if no more up destinations
                if (!downDestinations.isEmpty()) {
                    direction = Direction.DOWN;
                    state = ElevatorState.MOVING_DOWN;
                } else {
                    direction = Direction.IDLE;
                    state = ElevatorState.IDLE;
                }
            }
        } else if (state == ElevatorState.MOVING_DOWN) {
            currentFloor--;
            System.out.printf("Elevator %d → Floor %d (DOWN)%n", id, currentFloor);

            if (downDestinations.contains(currentFloor)) {
                downDestinations.remove(currentFloor);
                arriveAtFloor();
            } else if (downDestinations.isEmpty()) {
                if (!upDestinations.isEmpty()) {
                    direction = Direction.UP;
                    state = ElevatorState.MOVING_UP;
                } else {
                    direction = Direction.IDLE;
                    state = ElevatorState.IDLE;
                }
            }
        }
    }

    private void arriveAtFloor() {
        System.out.printf("Elevator %d stopped at Floor %d%n", id, currentFloor);
        state = ElevatorState.DOOR_OPEN;
        door.open();
    }

    public void emergencyStop() {
        state = ElevatorState.EMERGENCY;
        door.open();
        upDestinations.clear();
        downDestinations.clear();
        System.out.printf("EMERGENCY: Elevator %d stopped at Floor %d%n", id, currentFloor);
    }

    public void setMaintenance(boolean maintenance) {
        if (maintenance) {
            state = ElevatorState.MAINTENANCE;
            upDestinations.clear();
            downDestinations.clear();
        } else {
            state = ElevatorState.IDLE;
            direction = Direction.IDLE;
        }
    }

    public boolean isAvailable() {
        return state != ElevatorState.MAINTENANCE && state != ElevatorState.EMERGENCY;
    }

    public int getTotalPendingStops() {
        return upDestinations.size() + downDestinations.size();
    }

    // Getters
    public int getId() { return id; }
    public int getCurrentFloor() { return currentFloor; }
    public Direction getDirection() { return direction; }
    public ElevatorState getState() { return state; }
    public Door getDoor() { return door; }
    public int getMaxWeight() { return maxWeight; }
    public int getCurrentWeight() { return currentWeight; }
    public void setCurrentWeight(int weight) { this.currentWeight = weight; }
    public TreeSet<Integer> getUpDestinations() { return upDestinations; }
    public TreeSet<Integer> getDownDestinations() { return downDestinations; }
}
```

### Scheduling Strategies (Strategy Pattern)

```java
public interface ElevatorSchedulingStrategy {
    /**
     * Select the best elevator to handle an external request.
     *
     * @param elevators All elevators in the building
     * @param request   The external request (floor + direction)
     * @return The selected elevator, or null if none available
     */
    Elevator selectElevator(List<Elevator> elevators, Request request);
}

/**
 * FCFS (First Come First Served) — Simplest strategy.
 * Assigns request to the elevator with the fewest pending stops.
 */
public class FCFSStrategy implements ElevatorSchedulingStrategy {
    @Override
    public Elevator selectElevator(List<Elevator> elevators, Request request) {
        return elevators.stream()
                .filter(Elevator::isAvailable)
                .min(Comparator.comparingInt(Elevator::getTotalPendingStops))
                .orElse(null);
    }
}

/**
 * LOOK Strategy — The most commonly used elevator algorithm.
 *
 * Prefers an elevator that:
 * 1. Is already moving in the same direction as the request
 *    AND has not yet passed the request floor
 * 2. Is idle
 * 3. Is closest to the request floor
 *
 * This is the algorithm most real elevators use.
 */
public class LOOKStrategy implements ElevatorSchedulingStrategy {
    @Override
    public Elevator selectElevator(List<Elevator> elevators, Request request) {
        Elevator best = null;
        int bestScore = Integer.MAX_VALUE;

        for (Elevator elevator : elevators) {
            if (!elevator.isAvailable()) continue;

            int distance = Math.abs(elevator.getCurrentFloor() - request.getFloor());
            int score;

            if (elevator.getState() == ElevatorState.IDLE) {
                // Idle elevator: score is just the distance
                score = distance;
            } else if (elevator.getDirection() == request.getDirection()) {
                // Same direction: check if we haven't passed the floor yet
                boolean notPassed = (request.getDirection() == Direction.UP &&
                        elevator.getCurrentFloor() <= request.getFloor()) ||
                        (request.getDirection() == Direction.DOWN &&
                                elevator.getCurrentFloor() >= request.getFloor());

                if (notPassed) {
                    // Best case: elevator is heading our way and hasn't passed us
                    score = distance;
                } else {
                    // Has passed us — will need to reverse and come back
                    score = distance + 1000; // High penalty
                }
            } else {
                // Opposite direction: will need to finish current run first
                score = distance + 2000; // Even higher penalty
            }

            if (score < bestScore) {
                bestScore = score;
                best = elevator;
            }
        }

        return best;
    }
}

/**
 * SSTF (Shortest Seek Time First) — Minimizes immediate wait time.
 * Always picks the elevator closest to the request floor.
 * Warning: Can cause starvation for distant floors.
 */
public class SSTFStrategy implements ElevatorSchedulingStrategy {
    @Override
    public Elevator selectElevator(List<Elevator> elevators, Request request) {
        return elevators.stream()
                .filter(Elevator::isAvailable)
                .min(Comparator.comparingInt(
                    e -> Math.abs(e.getCurrentFloor() - request.getFloor())
                ))
                .orElse(null);
    }
}
```

### Floor

```java
public class Floor {
    private final int floorNumber;
    private final boolean hasUpButton;
    private final boolean hasDownButton;

    public Floor(int floorNumber, int minFloor, int maxFloor) {
        this.floorNumber = floorNumber;
        this.hasUpButton = (floorNumber < maxFloor);
        this.hasDownButton = (floorNumber > minFloor);
    }

    public Request pressUp() {
        if (!hasUpButton) throw new IllegalStateException("No UP button on top floor");
        return Request.external(floorNumber, Direction.UP);
    }

    public Request pressDown() {
        if (!hasDownButton) throw new IllegalStateException("No DOWN button on ground floor");
        return Request.external(floorNumber, Direction.DOWN);
    }

    public int getFloorNumber() { return floorNumber; }
}
```

### Elevator Controller (Singleton, Orchestrator)

```java
public class ElevatorController {
    private final List<Elevator> elevators;
    private final List<Floor> floors;
    private ElevatorSchedulingStrategy strategy;

    public ElevatorController(int numFloors, int numElevators,
                              int minFloor, int maxWeight,
                              ElevatorSchedulingStrategy strategy) {
        this.strategy = strategy;
        this.elevators = new ArrayList<>();
        this.floors = new ArrayList<>();

        int maxFloor = minFloor + numFloors - 1;

        for (int i = 0; i < numElevators; i++) {
            elevators.add(new Elevator(i + 1, minFloor, maxFloor, maxWeight));
        }

        for (int f = minFloor; f <= maxFloor; f++) {
            floors.add(new Floor(f, minFloor, maxFloor));
        }
    }

    public void setStrategy(ElevatorSchedulingStrategy strategy) {
        this.strategy = strategy;
    }

    /**
     * Handle an external request (user on a floor).
     * The strategy decides which elevator to dispatch.
     */
    public void handleExternalRequest(Request request) {
        System.out.printf("External request: Floor %d, Direction: %s%n",
            request.getFloor(), request.getDirection());

        Elevator selected = strategy.selectElevator(elevators, request);
        if (selected == null) {
            System.out.println("No available elevator!");
            return;
        }

        System.out.printf("Assigned to Elevator %d (currently at Floor %d)%n",
            selected.getId(), selected.getCurrentFloor());
        selected.addDestination(request.getFloor());
    }

    /**
     * Handle an internal request (user inside an elevator).
     */
    public void handleInternalRequest(int elevatorId, int destinationFloor) {
        Elevator elevator = elevators.stream()
                .filter(e -> e.getId() == elevatorId)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Elevator not found: " + elevatorId));

        System.out.printf("Internal request: Elevator %d → Floor %d%n", elevatorId, destinationFloor);
        elevator.addDestination(destinationFloor);
    }

    /**
     * Advance all elevators by one step. Called periodically by the simulation loop.
     */
    public void stepAll() {
        for (Elevator elevator : elevators) {
            elevator.step();
        }
    }

    public void displayStatus() {
        for (Elevator elevator : elevators) {
            System.out.printf("  Elevator %d: Floor %d, State: %s, Direction: %s, Stops: %d%n",
                elevator.getId(), elevator.getCurrentFloor(),
                elevator.getState(), elevator.getDirection(),
                elevator.getTotalPendingStops());
        }
    }

    public List<Elevator> getElevators() { return Collections.unmodifiableList(elevators); }
    public List<Floor> getFloors() { return Collections.unmodifiableList(floors); }
}
```

### Main — Simulation

```java
public class ElevatorSimulation {
    public static void main(String[] args) throws InterruptedException {
        // Building: 10 floors (1-10), 3 elevators, 1000kg max weight
        ElevatorController controller = new ElevatorController(
            10, 3, 1, 1000, new LOOKStrategy()
        );

        // Scenario: Multiple requests
        controller.handleExternalRequest(Request.external(5, Direction.UP));
        controller.handleExternalRequest(Request.external(3, Direction.DOWN));
        controller.handleExternalRequest(Request.external(8, Direction.DOWN));

        // Simulate 20 steps
        for (int step = 0; step < 20; step++) {
            System.out.printf("%n--- Step %d ---%n", step + 1);
            controller.stepAll();
            controller.displayStatus();

            // After elevator 1 reaches floor 5, user inside presses 8
            Elevator e1 = controller.getElevators().get(0);
            if (e1.getCurrentFloor() == 5 && e1.getState() == ElevatorState.DOOR_OPEN) {
                controller.handleInternalRequest(e1.getId(), 8);
            }
        }
    }
}
```

---

## Scheduling Algorithm Comparison

| Algorithm | How It Works | Pros | Cons |
|-----------|-------------|------|------|
| **FCFS** | Serve requests in arrival order | Simple, fair | High average wait time |
| **SCAN (Elevator)** | Move in one direction until the end, then reverse | No starvation | Goes to the building extremes unnecessarily |
| **LOOK** | Like SCAN but reverses when no more requests in current direction | Efficient, no starvation | Slightly more complex |
| **SSTF** | Always serve the closest request | Low average seek time | Can starve distant requests |

---

## Interview Discussion Points

1. **Why use the Strategy pattern for scheduling?**
   - Different buildings have different needs. A hospital elevator might use FCFS for fairness, while a skyscraper uses LOOK for efficiency. The Strategy pattern allows runtime swapping without modifying the controller.

2. **How would you handle overweight?**
   - The `Elevator` has `currentWeight` and `maxWeight`. When a weight sensor detects overweight, the door stays open, an alarm sounds, and the elevator does not move until weight is reduced. This is a safety invariant enforced in the `step()` method.

3. **How would you scale to multiple buildings?**
   - Each building has its own `ElevatorController`. A `BuildingManagement` system could aggregate controllers and provide centralized monitoring.

4. **How would you make this thread-safe in production?**
   - Use a single event loop (like an actor model) for each elevator controller, or use `synchronized` blocks on the elevator's destination sets. The current implementation uses `synchronized` methods on the `Elevator` class.

5. **Real-world consideration: What about express elevators?**
   - Add an `ElevatorType` enum (LOCAL, EXPRESS, FREIGHT). Express elevators skip intermediate floors and only stop at specific zones (e.g., floors 1, 20, 40). The scheduling strategy would respect elevator type constraints.

