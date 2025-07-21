# Parking Lot System - Low Level Design

## Problem Statement

Design a parking lot system that can handle multiple types of vehicles and parking spots efficiently. The system should be able to:

- **Support different vehicle types**: Motorcycle, Car, Truck
- **Handle different parking spot sizes**: Motorcycle, Compact, Large
- **Track availability** and assign spots optimally
- **Calculate parking fees** based on time and vehicle type
- **Support multiple levels** of parking
- **Handle edge cases** like spot unavailability and pricing strategies

## Requirements

### Functional Requirements
1. **Vehicle Management**: Support different vehicle types with appropriate spot assignments
2. **Spot Assignment**: Automatically assign the best available spot
3. **Fee Calculation**: Calculate parking fees based on duration and vehicle type
4. **Multi-level Support**: Handle multiple floors/levels of parking
5. **Real-time Availability**: Track and display current availability
6. **Ticket Management**: Generate and manage parking tickets

### Non-Functional Requirements
1. **Scalability**: Should handle thousands of vehicles
2. **Performance**: Quick spot assignment (O(n) in worst case)
3. **Extensibility**: Easy to add new vehicle types or pricing strategies
4. **Reliability**: Consistent state management
5. **Maintainability**: Clean, modular code structure

## Core Classes and Design Patterns

### Design Patterns Used

#### 1. **Singleton Pattern**
- **Class**: `ParkingLot`
- **Purpose**: Ensure only one instance of the parking lot system exists
- **Benefit**: Global access point and consistent state management

#### 2. **Factory Pattern**
- **Class**: `VehicleFactory`
- **Purpose**: Create different types of vehicles without exposing creation logic
- **Benefit**: Loose coupling and easy extensibility for new vehicle types

#### 3. **Strategy Pattern**
- **Interface**: `PricingStrategy`
- **Purpose**: Allow different pricing algorithms (hourly, daily, monthly)
- **Benefit**: Runtime strategy selection and easy addition of new pricing models

#### 4. **State Pattern** (Implicit)
- **Class**: `ParkingSpot` with `SpotStatus` enum
- **Purpose**: Manage different states of parking spots
- **Benefit**: Clear state transitions and behavior based on current state

## Class Diagram

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│    Vehicle      │    │  ParkingSpot    │    │  ParkingLevel   │
│   (Abstract)    │    │                 │    │                 │
├─────────────────┤    ├─────────────────┤    ├─────────────────┤
│- licensePlate   │    │- spotId         │    │- level          │
│- type           │    │- size           │    │- spots[]        │
│- requiredSpotSize│    │- status         │    │- availableSpots │
└─────────────────┘    │- parkedVehicle  │    └─────────────────┘
         △              │- level, row     │             △
         │              └─────────────────┘             │
    ┌────┴────┐                   △                     │
    │         │                   │                     │
┌───▼───┐ ┌──▼──┐ ┌──────▼──────┐ │        ┌─────────────▼─────────────┐
│  Car  │ │Truck│ │ Motorcycle  │ │        │      ParkingLot           │
└───────┘ └─────┘ └─────────────┘ │        │     (Singleton)           │
                                  │        ├───────────────────────────┤
         ┌────────────────────────┘        │- levels[]                 │
         │                                 │- activeTickets            │
    ┌────▼────────┐                        │- pricingStrategy          │
    │ParkingTicket│                        │+ parkVehicle()            │
    ├─────────────┤                        │+ exitVehicle()            │
    │- ticketId   │                        │+ displayAvailability()    │
    │- entryTime  │                        └───────────────────────────┘
    │- exitTime   │                                     △
    │- amount     │                                     │
    └─────────────┘                        ┌──────────────────────────┐
                                          │   PricingStrategy        │
                                          │    (Interface)           │
                                          ├──────────────────────────┤
                                          │+ calculatePrice()        │
                                          └──────────────────────────┘
                                                       △
                                                       │
                                          ┌──────────────────────────┐
                                          │ HourlyPricingStrategy    │
                                          ├──────────────────────────┤
                                          │- hourlyRates             │
                                          │+ calculatePrice()        │
                                          └──────────────────────────┘
```

## Detailed Implementation

### 1. Vehicle Hierarchy

```java
// Abstract base class for all vehicles
abstract class Vehicle {
    protected String licensePlate;
    protected VehicleType type;
    protected SpotSize requiredSpotSize;
}

// Concrete implementations
class Car extends Vehicle {
    // Requires COMPACT spot but can use LARGE
}

class Motorcycle extends Vehicle {
    // Requires MOTORCYCLE spot but can use COMPACT or LARGE
}

class Truck extends Vehicle {
    // Requires LARGE spot only
}
```

**Key Design Decisions:**
- **Inheritance**: Common properties in base class
- **Composition**: Vehicle has a required spot size
- **Flexibility**: Smaller vehicles can use larger spots

### 2. Parking Spot Management

```java
class ParkingSpot {
    private String spotId;      // Unique identifier: "L1-R2-S5"
    private SpotSize size;      // MOTORCYCLE, COMPACT, LARGE
    private SpotStatus status;  // AVAILABLE, OCCUPIED, RESERVED, OUT_OF_ORDER
    private Vehicle parkedVehicle;
    
    public boolean canFit(Vehicle vehicle) {
        // Logic for spot compatibility
    }
}
```

**Key Features:**
- **Flexible Assignment**: Larger spots can accommodate smaller vehicles
- **Status Management**: Clear state transitions
- **Unique Identification**: Easy spot location

### 3. Multi-Level Architecture

```java
class ParkingLevel {
    private int level;
    private List<ParkingSpot> spots;
    private Map<SpotSize, Integer> availableSpots; // Quick availability lookup
    
    // 20% motorcycle, 60% compact, 20% large spots
    private void initializeSpots(int totalSpots) {
        // Distribution logic
    }
}
```

**Optimization Features:**
- **Quick Availability Check**: O(1) lookup for available spots by size
- **Efficient Spot Distribution**: Realistic parking lot layout
- **Level-wise Organization**: Scalable to multiple floors

### 4. Pricing Strategy Implementation

```java
interface PricingStrategy {
    double calculatePrice(ParkingTicket ticket);
}

class HourlyPricingStrategy implements PricingStrategy {
    private Map<VehicleType, Double> hourlyRates;
    
    public double calculatePrice(ParkingTicket ticket) {
        // Calculate based on vehicle type and duration
    }
}
```

**Extensibility Benefits:**
- **Easy Strategy Changes**: Runtime strategy switching
- **New Pricing Models**: Daily, monthly, peak-hour pricing
- **Vehicle-Specific Rates**: Different rates for different vehicle types

## Algorithms and Complexity

### Spot Assignment Algorithm

```java
public ParkingSpot findAvailableSpot(Vehicle vehicle) {
    SpotSize requiredSize = vehicle.getRequiredSpotSize();
    
    // 1. Try exact size match first (optimal)
    for (ParkingSpot spot : spots) {
        if (spot.getSize() == requiredSize && spot.canFit(vehicle)) {
            return spot;
        }
    }
    
    // 2. Try larger spots if no exact match
    // This ensures efficient space utilization
    if (requiredSize == SpotSize.MOTORCYCLE) {
        // Can use COMPACT or LARGE
    } else if (requiredSize == SpotSize.COMPACT) {
        // Can use LARGE
    }
    
    return null;
}
```

**Time Complexity**: O(n) where n is number of spots per level
**Space Complexity**: O(1) for the search operation
**Optimization**: Could use separate lists for each spot size for O(1) lookup

### Fee Calculation Algorithm

```java
public double calculatePrice(ParkingTicket ticket) {
    long minutes = ticket.getParkingDurationInMinutes();
    double hours = Math.ceil(minutes / 60.0); // Round up to next hour
    
    VehicleType vehicleType = ticket.getAssignedSpot().getParkedVehicle().getType();
    return hours * hourlyRates.get(vehicleType);
}
```

**Time Complexity**: O(1)
**Space Complexity**: O(1)

## Advanced Features and Extensions

### 1. Reservation System
```java
class ParkingReservation {
    private String reservationId;
    private String licensePlate;
    private LocalDateTime reservationTime;
    private LocalDateTime expiryTime;
    private ParkingSpot reservedSpot;
}
```

### 2. Payment Integration
```java
interface PaymentProcessor {
    PaymentResult processPayment(double amount, PaymentMethod method);
}

class CreditCardProcessor implements PaymentProcessor {
    // Integration with payment gateway
}
```

### 3. Real-time Notifications
```java
interface NotificationService {
    void sendSpotAvailableNotification(String userId);
    void sendReservationExpiredNotification(String userId);
}
```

### 4. Analytics and Reporting
```java
class ParkingAnalytics {
    public double getAverageOccupancyRate();
    public Map<VehicleType, Integer> getVehicleTypeDistribution();
    public double getRevenueForPeriod(LocalDate start, LocalDate end);
}
```

## Edge Cases and Error Handling

### 1. No Available Spots
```java
public ParkingTicket parkVehicle(VehicleType vehicleType, String licensePlate) {
    // Try all levels
    for (ParkingLevel level : levels) {
        if (level.parkVehicle(vehicle)) {
            return createTicket(vehicle, assignedSpot);
        }
    }
    
    // No spots available
    System.out.println("No available parking spot for vehicle " + licensePlate);
    return null;
}
```

### 2. Invalid Vehicle Exit
```java
public double exitVehicle(String licensePlate) {
    ParkingTicket ticket = activeTickets.get(licensePlate);
    if (ticket == null) {
        throw new IllegalArgumentException("No active ticket found for license plate: " + licensePlate);
    }
    // Process exit
}
```

### 3. Spot State Consistency
```java
public void parkVehicle(Vehicle vehicle) {
    if (!canFit(vehicle)) {
        throw new IllegalStateException("Cannot park vehicle in this spot");
    }
    // Update state atomically
    this.parkedVehicle = vehicle;
    this.status = SpotStatus.OCCUPIED;
}
```

## Performance Optimizations

### 1. Quick Availability Lookup
- **HashMap for spot counts**: O(1) availability check by spot size
- **Level-wise distribution**: Parallel processing for large parking lots

### 2. Caching Strategies
- **Cache frequently accessed data**: Available spot counts
- **Cache pricing calculations**: For common durations

### 3. Database Optimization
- **Indexing**: On license plate, ticket ID, spot ID
- **Partitioning**: By level or date for large datasets

## Testing Strategy

### Unit Tests
```java
@Test
public void testVehicleCanFitInSpot() {
    ParkingSpot compactSpot = new ParkingSpot("test", SpotSize.COMPACT, 1, 1, 1);
    Car car = new Car("ABC-123");
    assertTrue(compactSpot.canFit(car));
}

@Test
public void testPricingCalculation() {
    // Test hourly pricing with different vehicle types
}
```

### Integration Tests
```java
@Test
public void testFullParkingAndExitFlow() {
    ParkingLot parkingLot = ParkingLot.getInstance();
    ParkingTicket ticket = parkingLot.parkVehicle(VehicleType.CAR, "TEST-123");
    assertNotNull(ticket);
    
    double amount = parkingLot.exitVehicle("TEST-123");
    assertTrue(amount > 0);
}
```

### Load Testing
- **Concurrent parking**: Multiple vehicles parking simultaneously
- **High availability scenarios**: System behavior when nearly full
- **Performance benchmarks**: Response times under load

## Interview Discussion Points

### 1. **Design Decisions**
- **Why use Singleton for ParkingLot?** Global state management and consistency
- **Why Strategy pattern for pricing?** Runtime flexibility and extensibility
- **Why separate ParkingLevel class?** Scalability and organization

### 2. **Scalability Considerations**
- **Database design**: How to persist parking data
- **Horizontal scaling**: Multiple parking lot locations
- **Performance optimization**: Caching and indexing strategies

### 3. **Real-world Extensions**
- **Mobile app integration**: QR codes, mobile payments
- **IoT integration**: Sensor-based availability detection
- **Machine learning**: Predictive analytics for parking patterns

### 4. **Alternative Approaches**
- **Event-driven architecture**: For real-time updates
- **Microservices**: Separate services for different functionalities
- **NoSQL databases**: For high-scale, distributed scenarios

## Conclusion

This parking lot design demonstrates:
- **Solid OOP principles**: Inheritance, encapsulation, polymorphism
- **Design pattern application**: Singleton, Factory, Strategy, State
- **Scalable architecture**: Multi-level support with efficient algorithms
- **Extensibility**: Easy to add new features and pricing strategies
- **Real-world applicability**: Handles edge cases and performance concerns

The design balances **simplicity with functionality**, making it suitable for both interview discussions and actual implementation in production systems. 