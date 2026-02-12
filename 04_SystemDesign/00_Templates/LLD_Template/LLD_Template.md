# Low-Level Design Interview Template

## 📋 **Step-by-Step Approach**

Use this systematic approach for any LLD interview problem:

### **Phase 1: Problem Understanding (5-10 minutes)**

#### 1.1 **Clarify Requirements**
```
Ask questions like:
- What are the core functionalities needed?
- Who are the main actors/users of the system?
- What are the constraints and assumptions?
- Are there any specific technologies to use/avoid?
- What is the expected scale/load?
```

#### 1.2 **Define Scope**
```
✅ What to include (MVP features)
❌ What to exclude (nice-to-have features)
🎯 Primary focus areas
📏 Scale and constraints
```

#### 1.3 **Identify Key Actors**
```
Primary Actors: Direct users of the system
Secondary Actors: Admin, maintenance systems
External Systems: APIs, third-party services
```

### **Phase 2: High-Level Design (5-10 minutes)**

#### 2.1 **Use Case Diagram**
```
┌─────────────┐     ┌─────────────────┐     ┌─────────────┐
│   Actor 1   │────▶│   Use Case 1    │◄────│   Actor 2   │
└─────────────┘     └─────────────────┘     └─────────────┘
                              │
                    ┌─────────────────┐
                    │   Use Case 2    │
                    └─────────────────┘
```

#### 2.2 **Core Components Identification**
```
List main components:
1. [Component Name] - [Responsibility]
2. [Component Name] - [Responsibility]
3. [Component Name] - [Responsibility]
```

### **Phase 3: Detailed Class Design (20-25 minutes)**

#### 3.1 **Class Diagram Structure**
```java
// Abstract classes and interfaces first
abstract class BaseClass {
    // Common properties and methods
}

interface ServiceInterface {
    // Contract definitions
}

// Enums for constants
enum StatusType {
    ACTIVE, INACTIVE, PENDING
}

// Main entity classes
class MainEntity {
    // Properties with appropriate visibility
    // Constructor, getters, setters
    // Business logic methods
}
```

#### 3.2 **Apply Design Patterns**
```
Consider these patterns:
✅ Singleton - For global objects
✅ Factory - For object creation
✅ Strategy - For algorithm variations
✅ Observer - For event notifications
✅ State - For state management
✅ Builder - For complex object construction
```

#### 3.3 **Relationship Definition**
```
Relationships to consider:
- Inheritance (IS-A)
- Composition (HAS-A)
- Aggregation (USES-A)
- Association (RELATED-TO)
- Dependency (DEPENDS-ON)
```

### **Phase 4: Implementation (10-15 minutes)**

#### 4.1 **Write Key Methods**
```java
// Focus on core business logic
public class CoreService {
    
    public Result performCoreOperation(Input input) {
        // 1. Validate input
        if (!isValid(input)) {
            throw new InvalidInputException();
        }
        
        // 2. Process business logic
        ProcessedData data = processInput(input);
        
        // 3. Apply business rules
        Result result = applyBusinessRules(data);
        
        // 4. Handle side effects
        notifyObservers(result);
        
        return result;
    }
}
```

#### 4.2 **Error Handling**
```java
// Define custom exceptions
public class BusinessLogicException extends Exception {
    private ErrorCode errorCode;
    private String message;
    
    // Constructor and methods
}

// Handle gracefully in methods
try {
    return performOperation();
} catch (BusinessLogicException e) {
    logger.error("Business logic error", e);
    return handleError(e);
}
```

### **Phase 5: Discussion (5-10 minutes)**

#### 5.1 **Design Justification**
```
Explain:
- Why specific patterns were chosen
- How SOLID principles are applied
- Trade-offs made during design
- Alternative approaches considered
```

#### 5.2 **Extensibility**
```
Discuss:
- How to add new features
- How to modify existing functionality
- Plugin architecture possibilities
- Configuration management
```

---

## 🎯 **Common LLD Problem Categories**

### **1. Management Systems**
```
Examples: Library Management, Hotel Booking, Hospital Management
Key Focus:
- Domain modeling
- CRUD operations
- Business rule enforcement
- User role management
```

### **2. Game Design**
```
Examples: Chess, TicTacToe, Snake and Ladder
Key Focus:
- State management
- Game rules implementation
- Player interaction
- Turn management
```

### **3. Real-time Systems**
```
Examples: Chat Application, Stock Trading, Live Streaming
Key Focus:
- Observer pattern
- Event handling
- Concurrency management
- State synchronization
```

### **4. Utility Systems**
```
Examples: Parking Lot, Elevator, Vending Machine
Key Focus:
- State machines
- Resource allocation
- Scheduling algorithms
- Hardware abstraction
```

---

## 💡 **Best Practices Checklist**

### **Code Quality**
- [ ] Meaningful class and method names
- [ ] Proper encapsulation (private fields, public methods)
- [ ] Single Responsibility Principle
- [ ] Open/Closed Principle
- [ ] Interface segregation

### **Error Handling**
- [ ] Input validation
- [ ] Custom exception classes
- [ ] Graceful error recovery
- [ ] Proper logging

### **Design Patterns**
- [ ] At least 2-3 patterns applied correctly
- [ ] Justify pattern choices
- [ ] Show understanding of pattern benefits

### **Extensibility**
- [ ] Easy to add new features
- [ ] Configuration-driven behavior
- [ ] Plugin architecture where applicable
- [ ] Separation of concerns

---

## 🗣️ **Communication Tips**

### **What to Say**
```
✅ "Let me clarify the requirements first..."
✅ "I'm going to use the Factory pattern here because..."
✅ "This design follows the Single Responsibility Principle by..."
✅ "For extensibility, we could add..."
✅ "An alternative approach would be..."
```

### **What to Avoid**
```
❌ Starting to code immediately
❌ Not asking clarifying questions
❌ Over-engineering for the given scope
❌ Not explaining design decisions
❌ Ignoring edge cases
```

### **Structure Your Response**
```
1. "Based on the requirements, I understand that..."
2. "The main actors in this system are..."
3. "I'll design this using [patterns] because..."
4. "Let me start with the core classes..."
5. "For extensibility, we could consider..."
```

---

## 📊 **Time Management**

### **45-Minute Interview Timeline**
```
Minutes 0-5:   Requirements clarification
Minutes 5-10:  High-level component design
Minutes 10-30: Detailed class design and relationships
Minutes 30-40: Code key methods and demonstrate patterns
Minutes 40-45: Discuss extensions and alternatives
```

### **If Running Short on Time**
```
Priority 1: Core classes with relationships
Priority 2: Key methods implementation
Priority 3: Design pattern application
Priority 4: Extension discussion
```

---

## 🧪 **Sample Problem Walkthrough**

### **Problem: Design a Coffee Machine**

#### **Step 1: Requirements**
```
- Support multiple drink types (Coffee, Tea, Hot Chocolate)
- Different sizes (Small, Medium, Large)
- Payment processing
- Ingredient management
- Maintenance mode
```

#### **Step 2: Core Classes**
```java
// Enums
enum DrinkType { COFFEE, TEA, HOT_CHOCOLATE }
enum Size { SMALL, MEDIUM, LARGE }
enum MachineState { READY, BREWING, MAINTENANCE, OUT_OF_ORDER }

// Strategy Pattern for Pricing
interface PricingStrategy {
    double calculatePrice(DrinkType type, Size size);
}

// Factory Pattern for Drinks
interface DrinkFactory {
    Drink createDrink(DrinkType type, Size size);
}

// State Pattern for Machine
interface MachineState {
    void insertMoney(CoffeeMachine machine, double amount);
    void selectDrink(CoffeeMachine machine, DrinkType type, Size size);
    void dispenseDrink(CoffeeMachine machine);
}

// Main Classes
class CoffeeMachine {
    private MachineState currentState;
    private IngredientManager ingredientManager;
    private PaymentProcessor paymentProcessor;
    private DrinkFactory drinkFactory;
    
    public void insertMoney(double amount) {
        currentState.insertMoney(this, amount);
    }
}
```

#### **Step 3: Key Patterns Applied**
```
✅ State Pattern: Machine states (Ready, Brewing, Maintenance)
✅ Strategy Pattern: Different pricing strategies
✅ Factory Pattern: Drink creation
✅ Observer Pattern: Notify when ingredients low
✅ Singleton Pattern: CoffeeMachine instance
```

This template provides a systematic approach to tackle any LLD problem with confidence and clarity. 