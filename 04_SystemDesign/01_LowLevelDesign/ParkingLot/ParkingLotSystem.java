/**
 * Parking Lot System - Low Level Design
 * 
 * Problem: Design a parking lot system that can:
 * - Support different vehicle types (Car, Truck, Motorcycle)
 * - Handle multiple parking spot sizes (Compact, Large, Motorcycle)
 * - Track availability and assign spots efficiently
 * - Calculate parking fees based on time
 * - Support multiple levels
 * 
 * Design Patterns Used:
 * - Strategy Pattern (for pricing)
 * - Factory Pattern (for vehicle creation)
 * - Singleton Pattern (for parking lot management)
 * - State Pattern (for parking spot status)
 * 
 * Time Complexity: O(n) for finding spots, O(1) for most operations
 * Space Complexity: O(n) where n is total number of parking spots
 */

import java.time.LocalDateTime;
import java.time.Duration;
import java.util.*;

// Enums for different types
enum VehicleType {
    MOTORCYCLE, CAR, TRUCK
}

enum SpotSize {
    MOTORCYCLE, COMPACT, LARGE
}

enum SpotStatus {
    AVAILABLE, OCCUPIED, RESERVED, OUT_OF_ORDER
}

// Abstract Vehicle class
abstract class Vehicle {
    protected String licensePlate;
    protected VehicleType type;
    protected SpotSize requiredSpotSize;
    
    public Vehicle(String licensePlate, VehicleType type, SpotSize requiredSpotSize) {
        this.licensePlate = licensePlate;
        this.type = type;
        this.requiredSpotSize = requiredSpotSize;
    }
    
    public String getLicensePlate() { return licensePlate; }
    public VehicleType getType() { return type; }
    public SpotSize getRequiredSpotSize() { return requiredSpotSize; }
}

// Concrete Vehicle classes
class Motorcycle extends Vehicle {
    public Motorcycle(String licensePlate) {
        super(licensePlate, VehicleType.MOTORCYCLE, SpotSize.MOTORCYCLE);
    }
}

class Car extends Vehicle {
    public Car(String licensePlate) {
        super(licensePlate, VehicleType.CAR, SpotSize.COMPACT);
    }
}

class Truck extends Vehicle {
    public Truck(String licensePlate) {
        super(licensePlate, VehicleType.TRUCK, SpotSize.LARGE);
    }
}

// Vehicle Factory
class VehicleFactory {
    public static Vehicle createVehicle(VehicleType type, String licensePlate) {
        switch (type) {
            case MOTORCYCLE:
                return new Motorcycle(licensePlate);
            case CAR:
                return new Car(licensePlate);
            case TRUCK:
                return new Truck(licensePlate);
            default:
                throw new IllegalArgumentException("Invalid vehicle type");
        }
    }
}

// Parking Spot class
class ParkingSpot {
    private String spotId;
    private SpotSize size;
    private SpotStatus status;
    private Vehicle parkedVehicle;
    private int level;
    private int row;
    private int spotNumber;
    
    public ParkingSpot(String spotId, SpotSize size, int level, int row, int spotNumber) {
        this.spotId = spotId;
        this.size = size;
        this.status = SpotStatus.AVAILABLE;
        this.level = level;
        this.row = row;
        this.spotNumber = spotNumber;
    }
    
    public boolean isAvailable() {
        return status == SpotStatus.AVAILABLE;
    }
    
    public boolean canFit(Vehicle vehicle) {
        return isAvailable() && 
               (size == vehicle.getRequiredSpotSize() || 
                (size == SpotSize.LARGE && vehicle.getRequiredSpotSize() == SpotSize.COMPACT) ||
                (size == SpotSize.COMPACT && vehicle.getRequiredSpotSize() == SpotSize.MOTORCYCLE));
    }
    
    public void parkVehicle(Vehicle vehicle) {
        if (!canFit(vehicle)) {
            throw new IllegalStateException("Cannot park vehicle in this spot");
        }
        this.parkedVehicle = vehicle;
        this.status = SpotStatus.OCCUPIED;
    }
    
    public void removeVehicle() {
        this.parkedVehicle = null;
        this.status = SpotStatus.AVAILABLE;
    }
    
    // Getters
    public String getSpotId() { return spotId; }
    public SpotSize getSize() { return size; }
    public SpotStatus getStatus() { return status; }
    public Vehicle getParkedVehicle() { return parkedVehicle; }
    public int getLevel() { return level; }
    public int getRow() { return row; }
    public int getSpotNumber() { return spotNumber; }
}

// Parking Level class
class ParkingLevel {
    private int level;
    private List<ParkingSpot> spots;
    private Map<SpotSize, Integer> availableSpots;
    
    public ParkingLevel(int level, int totalSpots) {
        this.level = level;
        this.spots = new ArrayList<>();
        this.availableSpots = new HashMap<>();
        
        // Initialize spot counts
        for (SpotSize size : SpotSize.values()) {
            availableSpots.put(size, 0);
        }
        
        initializeSpots(totalSpots);
    }
    
    private void initializeSpots(int totalSpots) {
        // 20% motorcycle spots, 60% compact spots, 20% large spots
        int motorcycleSpots = (int) (totalSpots * 0.2);
        int compactSpots = (int) (totalSpots * 0.6);
        int largeSpots = totalSpots - motorcycleSpots - compactSpots;
        
        int spotNumber = 1;
        int row = 1;
        
        // Add motorcycle spots
        for (int i = 0; i < motorcycleSpots; i++) {
            String spotId = String.format("L%d-R%d-S%d", level, row, spotNumber);
            spots.add(new ParkingSpot(spotId, SpotSize.MOTORCYCLE, level, row, spotNumber));
            availableSpots.put(SpotSize.MOTORCYCLE, availableSpots.get(SpotSize.MOTORCYCLE) + 1);
            spotNumber++;
            if (spotNumber > 10) { // 10 spots per row
                row++;
                spotNumber = 1;
            }
        }
        
        // Add compact spots
        for (int i = 0; i < compactSpots; i++) {
            String spotId = String.format("L%d-R%d-S%d", level, row, spotNumber);
            spots.add(new ParkingSpot(spotId, SpotSize.COMPACT, level, row, spotNumber));
            availableSpots.put(SpotSize.COMPACT, availableSpots.get(SpotSize.COMPACT) + 1);
            spotNumber++;
            if (spotNumber > 10) {
                row++;
                spotNumber = 1;
            }
        }
        
        // Add large spots
        for (int i = 0; i < largeSpots; i++) {
            String spotId = String.format("L%d-R%d-S%d", level, row, spotNumber);
            spots.add(new ParkingSpot(spotId, SpotSize.LARGE, level, row, spotNumber));
            availableSpots.put(SpotSize.LARGE, availableSpots.get(SpotSize.LARGE) + 1);
            spotNumber++;
            if (spotNumber > 10) {
                row++;
                spotNumber = 1;
            }
        }
    }
    
    public ParkingSpot findAvailableSpot(Vehicle vehicle) {
        SpotSize requiredSize = vehicle.getRequiredSpotSize();
        
        // Try to find exact size match first
        for (ParkingSpot spot : spots) {
            if (spot.getSize() == requiredSize && spot.canFit(vehicle)) {
                return spot;
            }
        }
        
        // If no exact match, try larger spots (but not smaller)
        if (requiredSize == SpotSize.MOTORCYCLE) {
            for (ParkingSpot spot : spots) {
                if ((spot.getSize() == SpotSize.COMPACT || spot.getSize() == SpotSize.LARGE) 
                    && spot.canFit(vehicle)) {
                    return spot;
                }
            }
        } else if (requiredSize == SpotSize.COMPACT) {
            for (ParkingSpot spot : spots) {
                if (spot.getSize() == SpotSize.LARGE && spot.canFit(vehicle)) {
                    return spot;
                }
            }
        }
        
        return null; // No available spot
    }
    
    public boolean parkVehicle(Vehicle vehicle) {
        ParkingSpot spot = findAvailableSpot(vehicle);
        if (spot != null) {
            spot.parkVehicle(vehicle);
            availableSpots.put(spot.getSize(), availableSpots.get(spot.getSize()) - 1);
            return true;
        }
        return false;
    }
    
    public boolean removeVehicle(Vehicle vehicle) {
        for (ParkingSpot spot : spots) {
            if (spot.getParkedVehicle() != null && 
                spot.getParkedVehicle().getLicensePlate().equals(vehicle.getLicensePlate())) {
                spot.removeVehicle();
                availableSpots.put(spot.getSize(), availableSpots.get(spot.getSize()) + 1);
                return true;
            }
        }
        return false;
    }
    
    public int getAvailableSpots(SpotSize size) {
        return availableSpots.get(size);
    }
    
    public int getLevel() { return level; }
    public List<ParkingSpot> getSpots() { return spots; }
}

// Parking Ticket class
class ParkingTicket {
    private String ticketId;
    private String licensePlate;
    private ParkingSpot assignedSpot;
    private LocalDateTime entryTime;
    private LocalDateTime exitTime;
    private double amount;
    
    public ParkingTicket(String ticketId, String licensePlate, ParkingSpot assignedSpot) {
        this.ticketId = ticketId;
        this.licensePlate = licensePlate;
        this.assignedSpot = assignedSpot;
        this.entryTime = LocalDateTime.now();
        this.amount = 0.0;
    }
    
    public void setExitTime(LocalDateTime exitTime) {
        this.exitTime = exitTime;
    }
    
    public void setAmount(double amount) {
        this.amount = amount;
    }
    
    public long getParkingDurationInMinutes() {
        if (exitTime == null) {
            return Duration.between(entryTime, LocalDateTime.now()).toMinutes();
        }
        return Duration.between(entryTime, exitTime).toMinutes();
    }
    
    // Getters
    public String getTicketId() { return ticketId; }
    public String getLicensePlate() { return licensePlate; }
    public ParkingSpot getAssignedSpot() { return assignedSpot; }
    public LocalDateTime getEntryTime() { return entryTime; }
    public LocalDateTime getExitTime() { return exitTime; }
    public double getAmount() { return amount; }
}

// Pricing Strategy Interface
interface PricingStrategy {
    double calculatePrice(ParkingTicket ticket);
}

// Hourly Pricing Strategy
class HourlyPricingStrategy implements PricingStrategy {
    private Map<VehicleType, Double> hourlyRates;
    
    public HourlyPricingStrategy() {
        hourlyRates = new HashMap<>();
        hourlyRates.put(VehicleType.MOTORCYCLE, 2.0);
        hourlyRates.put(VehicleType.CAR, 5.0);
        hourlyRates.put(VehicleType.TRUCK, 10.0);
    }
    
    @Override
    public double calculatePrice(ParkingTicket ticket) {
        long minutes = ticket.getParkingDurationInMinutes();
        double hours = Math.ceil(minutes / 60.0); // Round up to next hour
        
        VehicleType vehicleType = ticket.getAssignedSpot().getParkedVehicle().getType();
        return hours * hourlyRates.get(vehicleType);
    }
}

// Main Parking Lot class (Singleton)
class ParkingLot {
    private static ParkingLot instance;
    private List<ParkingLevel> levels;
    private Map<String, ParkingTicket> activeTickets;
    private PricingStrategy pricingStrategy;
    private int ticketCounter;
    
    private ParkingLot() {
        this.levels = new ArrayList<>();
        this.activeTickets = new HashMap<>();
        this.pricingStrategy = new HourlyPricingStrategy();
        this.ticketCounter = 1;
        
        // Initialize with 3 levels, 100 spots each
        for (int i = 1; i <= 3; i++) {
            levels.add(new ParkingLevel(i, 100));
        }
    }
    
    public static synchronized ParkingLot getInstance() {
        if (instance == null) {
            instance = new ParkingLot();
        }
        return instance;
    }
    
    public ParkingTicket parkVehicle(VehicleType vehicleType, String licensePlate) {
        Vehicle vehicle = VehicleFactory.createVehicle(vehicleType, licensePlate);
        
        // Try to park on each level
        for (ParkingLevel level : levels) {
            if (level.parkVehicle(vehicle)) {
                // Find the spot where vehicle was parked
                ParkingSpot assignedSpot = null;
                for (ParkingSpot spot : level.getSpots()) {
                    if (spot.getParkedVehicle() != null && 
                        spot.getParkedVehicle().getLicensePlate().equals(licensePlate)) {
                        assignedSpot = spot;
                        break;
                    }
                }
                
                String ticketId = "TICKET-" + (ticketCounter++);
                ParkingTicket ticket = new ParkingTicket(ticketId, licensePlate, assignedSpot);
                activeTickets.put(licensePlate, ticket);
                
                System.out.println("Vehicle " + licensePlate + " parked at " + assignedSpot.getSpotId());
                return ticket;
            }
        }
        
        System.out.println("No available parking spot for vehicle " + licensePlate);
        return null;
    }
    
    public double exitVehicle(String licensePlate) {
        ParkingTicket ticket = activeTickets.get(licensePlate);
        if (ticket == null) {
            throw new IllegalArgumentException("No active ticket found for license plate: " + licensePlate);
        }
        
        ticket.setExitTime(LocalDateTime.now());
        double amount = pricingStrategy.calculatePrice(ticket);
        ticket.setAmount(amount);
        
        // Remove vehicle from spot
        Vehicle vehicle = VehicleFactory.createVehicle(
            ticket.getAssignedSpot().getParkedVehicle().getType(), 
            licensePlate
        );
        
        for (ParkingLevel level : levels) {
            if (level.removeVehicle(vehicle)) {
                break;
            }
        }
        
        activeTickets.remove(licensePlate);
        
        System.out.println("Vehicle " + licensePlate + " exited. Amount: $" + amount);
        return amount;
    }
    
    public void displayAvailability() {
        System.out.println("\n=== Parking Lot Availability ===");
        for (ParkingLevel level : levels) {
            System.out.println("Level " + level.getLevel() + ":");
            System.out.println("  Motorcycle spots: " + level.getAvailableSpots(SpotSize.MOTORCYCLE));
            System.out.println("  Compact spots: " + level.getAvailableSpots(SpotSize.COMPACT));
            System.out.println("  Large spots: " + level.getAvailableSpots(SpotSize.LARGE));
        }
        System.out.println("================================\n");
    }
    
    public boolean isSpotAvailable(SpotSize size) {
        for (ParkingLevel level : levels) {
            if (level.getAvailableSpots(size) > 0) {
                return true;
            }
        }
        return false;
    }
    
    public void setPricingStrategy(PricingStrategy strategy) {
        this.pricingStrategy = strategy;
    }
}

// Main class for testing
public class ParkingLotSystem {
    public static void main(String[] args) {
        ParkingLot parkingLot = ParkingLot.getInstance();
        
        System.out.println("=== Parking Lot System Demo ===\n");
        
        // Display initial availability
        parkingLot.displayAvailability();
        
        // Park some vehicles
        ParkingTicket ticket1 = parkingLot.parkVehicle(VehicleType.CAR, "ABC-123");
        ParkingTicket ticket2 = parkingLot.parkVehicle(VehicleType.MOTORCYCLE, "BIKE-456");
        ParkingTicket ticket3 = parkingLot.parkVehicle(VehicleType.TRUCK, "TRUCK-789");
        
        // Display availability after parking
        parkingLot.displayAvailability();
        
        // Simulate some time passing (for demo, we'll just calculate immediately)
        try {
            Thread.sleep(2000); // 2 seconds for demo
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // Exit vehicles
        if (ticket1 != null) {
            double amount1 = parkingLot.exitVehicle("ABC-123");
        }
        
        if (ticket2 != null) {
            double amount2 = parkingLot.exitVehicle("BIKE-456");
        }
        
        // Display final availability
        parkingLot.displayAvailability();
        
        // Test parking when truck is still parked
        ParkingTicket ticket4 = parkingLot.parkVehicle(VehicleType.CAR, "NEW-CAR");
        
        parkingLot.displayAvailability();
    }
} 