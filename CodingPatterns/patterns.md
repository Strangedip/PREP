# **Complete List of Design Patterns Explained So Far**

Here is the **updated** list of all design patterns I have explained, along with **why and when to use them**:

---

### **1. Singleton Pattern**
✅ **Ensures only one instance of a class exists globally.**  
📌 **Use When:** You need a single shared instance, like database connections, logging, or configuration management.

---

### **2. Factory Pattern**
✅ **Encapsulates object creation logic and provides a flexible way to instantiate objects.**  
📌 **Use When:** You need dynamic object creation, avoiding `new` keyword usage in multiple places (e.g., DAO creation, service instantiation).

---

### **3. Abstract Factory Pattern**
✅ **Creates families of related objects without specifying their concrete classes.**  
📌 **Use When:** You need to produce multiple types of related objects dynamically (e.g., UI themes with buttons, menus, and text fields).

---

### **4. Builder Pattern**
✅ **Simplifies object creation for complex objects with many parameters.**  
📌 **Use When:** You need to create immutable objects with multiple optional parameters (e.g., configuring an HTTP request or building a report).

---

### **5. Prototype Pattern**
✅ **Allows cloning of existing objects instead of creating new instances.**  
📌 **Use When:** Object creation is expensive and you want to reuse an existing instance (e.g., object caching, game character cloning).

---

### **6. Adapter Pattern**
✅ **Converts one interface into another compatible interface.**  
📌 **Use When:** You need to integrate an old system with a new API or connect incompatible interfaces (e.g., legacy code adaptation).

---

### **7. Bridge Pattern**
✅ **Decouples abstraction from implementation to allow independent variations.**  
📌 **Use When:** You need to extend both functionality and implementation separately (e.g., different payment methods and gateways).

---

### **8. Composite Pattern**
✅ **Manages hierarchical structures where objects can be treated uniformly.**  
📌 **Use When:** You need to handle individual and group objects in the same way (e.g., file systems, menus, product categories).

---

### **9. Decorator Pattern**
✅ **Dynamically adds new behavior to objects without modifying their structure.**  
📌 **Use When:** You need to extend object functionality without modifying the original class (e.g., logging, security, caching enhancements).

---

### **10. Facade Pattern**
✅ **Provides a simplified, unified interface to a complex system.**  
📌 **Use When:** You need to simplify interactions with multiple subsystems (e.g., order processing in e-commerce).

---

### **11. Flyweight Pattern**
✅ **Minimizes memory usage by sharing common objects instead of creating new ones.**  
📌 **Use When:** Your application creates a large number of similar objects (e.g., caching, UI rendering optimization).

---

### **12. Proxy Pattern**
✅ **Provides a surrogate for another object to control access.**  
📌 **Use When:** You need to add security, lazy loading, or remote access to an object (e.g., database access control, API rate limiting).

---

### **13. Chain of Responsibility Pattern**
✅ **Passes a request through a chain of handlers until one processes it.**  
📌 **Use When:** You need a flexible way to handle requests (e.g., logging, authentication, middleware filters).

---

### **14. Command Pattern**
✅ **Encapsulates requests as objects, allowing execution, queuing, and undo functionality.**  
📌 **Use When:** You need to support undo/redo operations or queue tasks (e.g., transaction rollback, task scheduling).

---

### **15. Interpreter Pattern**
✅ **Defines a language grammar and provides an interpreter for processing expressions.**  
📌 **Use When:** You need to process user-defined queries, expressions, or DSL (e.g., search filtering, mathematical expression evaluation).

---

### **16. Iterator Pattern**
✅ **Provides a way to access elements of a collection without exposing its implementation.**  
📌 **Use When:** You need a standard way to iterate through different types of collections (e.g., database result sets, tree structures).

---

### **17. Mediator Pattern**
✅ **Centralizes communication between multiple objects to reduce dependencies.**  
📌 **Use When:** You need to decouple components that communicate frequently (e.g., chat systems, microservices communication).

---

### **18. Memento Pattern**
✅ **Saves an object’s state to restore it later (undo functionality).**  
📌 **Use When:** You need rollback or undo features (e.g., form input recovery, document editing history).

---

### **19. Observer Pattern**
✅ **Defines a dependency between objects, so when one changes, others are notified.**  
📌 **Use When:** You need event-driven systems (e.g., notification systems, real-time stock price updates).

---

### **20. State Pattern**
✅ **Allows an object to change its behavior dynamically based on its state.**  
📌 **Use When:** You need to manage state transitions (e.g., order processing, authentication states).

---

### **21. Strategy Pattern**
✅ **Encapsulates algorithms inside separate classes for flexible swapping.**  
📌 **Use When:** You need to switch between different behaviors dynamically (e.g., payment processing, sorting algorithms).

---

### **22. Template Method Pattern**
✅ **Defines a skeleton of an algorithm, allowing subclasses to customize steps.**  
📌 **Use When:** You need to enforce a standard process with customizable steps (e.g., authentication workflows, report generation).

---

### **23. Visitor Pattern**
✅ **Adds new behaviors to objects without modifying them.**  
📌 **Use When:** You need to process different object types in a structured way (e.g., report generation, AST processing).

---

### **24. Null Object Pattern**
✅ **Provides a default "do-nothing" behavior for missing objects instead of returning `null`.**  
📌 **Use When:** You need to handle missing data gracefully and avoid `NullPointerException` (e.g., missing users, logging stubs).

---

### **25. Servant Pattern**
✅ **Encapsulates common functionality in a helper (servant) class shared across multiple unrelated objects.**  
📌 **Use When:** You need to apply shared behavior without modifying classes (e.g., validation, logging, object conversions).

---

## **Summary Table**
| Pattern | Purpose | Common Use Cases |
|---------|---------|-----------------|
| **Singleton** | One shared instance | Logging, database connections |
| **Factory** | Centralized object creation | DAO creation, dependency injection |
| **Abstract Factory** | Create related objects | UI components, theming |
| **Builder** | Simplifies object construction | HTTP requests, reports |
| **Prototype** | Clones objects | Caching, game development |
| **Adapter** | Converts incompatible interfaces | Legacy system integration |
| **Bridge** | Separates abstraction & implementation | Payment gateways, UI elements |
| **Composite** | Hierarchical structures | File systems, menus |
| **Decorator** | Dynamically adds behavior | Logging, caching, security |
| **Facade** | Simplifies complex systems | E-commerce order processing |
| **Flyweight** | Shares common objects | Caching, UI optimization |
| **Proxy** | Controls access | Security, API gateways |
| **Chain of Responsibility** | Passes requests through handlers | Logging, authentication |
| **Command** | Encapsulates requests as objects | Undo, task queues |
| **Interpreter** | Parses custom expressions | Search queries, DSLs |
| **Iterator** | Standardizes collection traversal | Database results, trees |
| **Mediator** | Centralizes communication | Chat systems, microservices |
| **Memento** | Saves object state | Undo, version control |
| **Observer** | Notifies dependent objects | Event-driven applications |
| **State** | Manages state transitions | Order processing, authentication |
| **Strategy** | Selects behavior dynamically | Sorting, payment processing |
| **Template Method** | Defines process skeleton | Authentication, workflows |
| **Visitor** | Adds behaviors without modification | Reporting, data processing |
| **Null Object** | Avoids null checks | Default object behavior |
| **Servant** | Encapsulates shared logic | Validation, logging |

---

This is the **updated complete list** of design patterns I have explained. Would you like me to explain **another design pattern** or **Spring Boot feature**?

## Java & Spring Boot Fact: Dependency Injection in Spring Boot

### **What is Dependency Injection?**
Dependency Injection (DI) is a design pattern used in Spring Boot that allows objects to be injected into a class rather than the class creating them. It enables loose coupling between components, improving maintainability and testability.

---

### **How Does Dependency Injection Work?**
Spring Boot manages the lifecycle of beans (components) and injects dependencies automatically. This is achieved using annotations like:
- `@Component` - Marks a class as a Spring-managed component.
- `@Service` - Specialized for service-layer components.
- `@Repository` - Specialized for DAO (Data Access Objects).
- `@Controller` - Specialized for MVC controllers.
- `@Autowired` - Injects dependencies automatically.

**Example Code:**
```java
import org.springframework.stereotype.Service;

@Service
public class GreetingService {
    public String getGreeting() {
        return "Hello, Spring Boot!";
    }
}
```

```java
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class GreetingController {

    private final GreetingService greetingService;

    @Autowired
    public GreetingController(GreetingService greetingService) {
        this.greetingService = greetingService;
    }

    public void printGreeting() {
        System.out.println(greetingService.getGreeting());
    }
}
```

---

### **Why Use Dependency Injection?**
1. **Loose Coupling:** Components do not depend on specific implementations, making changes easier.
2. **Easier Testing:** Mock dependencies can be injected during testing.
3. **Improved Maintainability:** Code is structured and modular.
4. **Inversion of Control (IoC):** Spring handles object creation, reducing boilerplate code.

---

### **Where is Dependency Injection Used?**
- **Service Classes:** To handle business logic.
- **Repository Layer:** For database access.
- **Controllers:** To handle HTTP requests.
- **Configuration Classes:** To define beans dynamically.

---

### **Additional Tips & Tricks**
- **Field Injection vs Constructor Injection:**
  - Constructor injection (`@Autowired` on constructor) is preferred for mandatory dependencies.
  - Field injection (`@Autowired` on fields) is not recommended as it makes testing harder.

- **Using `@Qualifier`:** When multiple beans of the same type exist, use `@Qualifier` to specify which one should be injected.
  ```java
  @Autowired
  @Qualifier("beanName")
  private MyService myService;
  ```

---

**Conclusion:**  
Dependency Injection is a core feature of Spring Boot that simplifies development, enhances flexibility, and makes applications easier to manage. Using DI properly ensures a well-structured and maintainable application.
user
Next
ChatGPT
## Java & Spring Boot Fact: **Spring Boot Profiles**

### **What are Spring Boot Profiles?**
Spring Boot profiles allow developers to configure different settings for different environments (e.g., development, testing, production). This feature helps in managing multiple configurations efficiently without modifying the core application code.

---

### **How Do Spring Boot Profiles Work?**
Spring Boot enables profile-based configuration using:
1. **`application.properties` or `application.yml` files**
2. **`@Profile` annotation**
3. **Spring Boot's `spring.profiles.active` property**

---

### **Using `application.properties` for Profiles**
You can create separate property files for each profile. For example:
- `application-dev.properties` (for development)
- `application-prod.properties` (for production)

**Example: `application-dev.properties`**
```properties
server.port=8081
spring.datasource.url=jdbc:mysql://localhost:3306/devdb
spring.datasource.username=devuser
spring.datasource.password=devpass
```

**Example: `application-prod.properties`**
```properties
server.port=8082
spring.datasource.url=jdbc:mysql://prod-db-server:3306/proddb
spring.datasource.username=produser
spring.datasource.password=prodpass
```

**Activating a Profile in `application.properties`**
```properties
spring.profiles.active=dev
```
Alternatively, you can set the active profile via the command line:
```bash
java -jar myapp.jar --spring.profiles.active=prod
```

---

### **Using `@Profile` Annotation**
You can use the `@Profile` annotation to activate a specific bean only for a given profile.

```java
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("dev")
public class DevDataService implements DataService {
    public String getData() {
        return "Development Data";
    }
}
```

```java
@Service
@Profile("prod")
public class ProdDataService implements DataService {
    public String getData() {
        return "Production Data";
    }
}
```

---

### **Why Use Spring Boot Profiles?**
1. **Environment-Specific Configuration:** Helps separate configuration settings for `dev`, `test`, and `prod`.
2. **Better Maintainability:** No need to change properties manually when switching environments.
3. **Enhanced Security:** Sensitive production credentials stay separate from development configurations.
4. **Easy Testing:** You can set up test profiles to isolate configurations for unit and integration testing.

---

### **Where Are Spring Boot Profiles Used?**
- Database configurations (`dev` vs. `prod` databases)
- API keys and security credentials
- Logging levels (`DEBUG` for `dev`, `INFO` for `prod`)
- Feature toggling for experimental features

---

### **Additional Tips & Tricks**
- Use **`application.yml`** instead of multiple `application.properties` for better readability.
- Use **Spring Cloud Config** for centralized profile management across multiple microservices.
- Ensure **secure storage of production credentials** by using Spring Boot's support for externalized configuration (e.g., environment variables, Vault, AWS Secrets Manager).

---

### **Conclusion**
Spring Boot profiles simplify environment-specific configuration management, making applications more flexible and maintainable. Leveraging profiles correctly ensures smooth deployments and easier debugging in different environments.
user

# **Java & Spring Boot Fact: The Singleton Design Pattern in Spring Boot**

## **What is the Singleton Pattern?**
The Singleton Pattern is a design pattern that restricts a class to have only **one instance** throughout the application's lifecycle. This is particularly useful when only one object is needed to manage application-wide configurations, shared resources, or services.

In **Spring Boot**, Singleton is the **default scope** for Spring beans. This means that, unless explicitly defined otherwise, Spring ensures only **one instance** of a bean is created and shared across the application.

---

## **How Does the Singleton Pattern Work?**
The Singleton pattern is implemented using:
1. **Private constructor** - Prevents external instantiation.
2. **Static instance variable** - Holds the single instance.
3. **Public method to access instance** - Provides global access to the instance.

---

## **Why Use the Singleton Pattern?**
1. **Memory efficiency** – Reduces unnecessary object creation.
2. **Consistency** – Ensures the same instance is used across different parts of the application.
3. **Global access point** – Allows shared access to common resources.
4. **Thread safety** – Proper implementation prevents issues in multi-threaded environments.

---

## **Where is the Singleton Pattern Used?**
- **Database connections** – Single `DataSource` instance for all DB operations.
- **Logging** – A single logger instance shared across the application.
- **Caching** – Centralized storage for frequently accessed data.
- **Configuration management** – Application-wide settings stored in a single object.
- **Thread pools** – Ensures controlled management of threads.

---

## **Implementing Singleton in Java**
There are different ways to implement Singleton in Java:

### **1. Classic Singleton (Not Thread-Safe)**
```java
public class ClassicSingleton {
    private static ClassicSingleton instance;

    private ClassicSingleton() {
        // Private constructor
    }

    public static ClassicSingleton getInstance() {
        if (instance == null) {
            instance = new ClassicSingleton();
        }
        return instance;
    }
}
```
**Issue:** This is **not thread-safe**—two threads can create multiple instances.

---

### **2. Thread-Safe Singleton (Synchronized Method)**
```java
public class ThreadSafeSingleton {
    private static ThreadSafeSingleton instance;

    private ThreadSafeSingleton() {}

    public static synchronized ThreadSafeSingleton getInstance() {
        if (instance == null) {
            instance = new ThreadSafeSingleton();
        }
        return instance;
    }
}
```
**Drawback:** The `synchronized` method can be slow because every thread must wait for others to finish before accessing.

---

### **3. Double-Checked Locking Singleton**
```java
public class DoubleCheckedLockingSingleton {
    private static volatile DoubleCheckedLockingSingleton instance;

    private DoubleCheckedLockingSingleton() {}

    public static DoubleCheckedLockingSingleton getInstance() {
        if (instance == null) {
            synchronized (DoubleCheckedLockingSingleton.class) {
                if (instance == null) {
                    instance = new DoubleCheckedLockingSingleton();
                }
            }
        }
        return instance;
    }
}
```
**Benefit:** Reduces unnecessary synchronization, improving performance.

---

### **4. Singleton with Enum (Best Practice)**
```java
public enum EnumSingleton {
    INSTANCE;

    public void showMessage() {
        System.out.println("Singleton using Enum!");
    }
}
```
**Advantage:** Ensures safety against reflection and serialization attacks.

---

## **Using Singleton in Spring Boot**
Spring Boot **automatically manages Singleton Beans**. Here’s how:

### **1. Singleton Service in Spring Boot**
```java
import org.springframework.stereotype.Service;

@Service
public class SingletonService {
    public String getServiceMessage() {
        return "This is a Singleton Service!";
    }
}
```
### **2. Injecting Singleton Bean in a Controller**
```java
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/singleton")
public class SingletonController {

    private final SingletonService singletonService;

    @Autowired
    public SingletonController(SingletonService singletonService) {
        this.singletonService = singletonService;
    }

    @GetMapping
    public String getSingletonMessage() {
        return singletonService.getServiceMessage();
    }
}
```
**Output:**  
When accessing `http://localhost:8080/singleton`, it returns:
```
This is a Singleton Service!
```

---

## **Bean Scope in Spring Boot**
Spring Boot beans are **Singleton by default** but can have other scopes:

| Scope      | Description |
|------------|-------------|
| `singleton` | Default scope, one instance per application context |
| `prototype` | A new instance is created every time it is requested |
| `request` | One instance per HTTP request (used in web applications) |
| `session` | One instance per user session |
| `application` | One instance per entire application |

To define a prototype bean:
```java
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope("prototype")
public class PrototypeService {
    public String getMessage() {
        return "This is a Prototype Bean!";
    }
}
```

---

## **Benefits of Using Singleton Pattern in Spring Boot**
### ✅ **Improved Performance**
- Reduces memory consumption by reusing instances.
- Eliminates unnecessary object creation.

### ✅ **Better Maintainability**
- Centralized resource management simplifies debugging.

### ✅ **Thread Safety**
- When implemented correctly (e.g., using `@Service` or `@Bean`), Singleton beans in Spring Boot are **thread-safe**.

### ✅ **Consistency**
- Ensures that only one instance of a class handles business logic, reducing inconsistencies.

---

## **Potential Pitfalls of Singleton Pattern**
1. **Stateful Beans Can Cause Issues**
  - Singleton beans are **shared across requests**. If they store **state**, this might lead to data inconsistency.
  - **Solution:** Avoid stateful beans or use `prototype` scope.

2. **Multi-Threading Issues**
  - A poorly implemented Singleton may cause thread-safety issues.
  - **Solution:** Use **synchronized**, **volatile**, or **enum-based Singleton**.

3. **Difficult to Unit Test**
  - Testing a Singleton can be challenging because the instance is static.
  - **Solution:** Use Dependency Injection to mock Singleton beans.

---

## **Conclusion**
The **Singleton Pattern** is a crucial design pattern in **Spring Boot and Java**, ensuring efficient memory usage and consistency across applications.
- Spring Boot **automatically manages Singleton beans**.
- If you need a different scope (e.g., per request), you can explicitly set it.
- The **best approach for Singleton in Java** is using **Enum** or **Spring-managed Beans**.

**Next Step:**
- Try creating a Singleton-based logging service.
- Experiment with different bean scopes (`singleton`, `prototype`, `request`).
- Optimize Singleton usage in high-performance applications.

---

### **Interview Question**
**Q:** Why is the Singleton pattern the default in Spring Boot?  
**A:** Spring Boot follows the Singleton pattern to optimize performance by creating **only one instance of each bean** by default. This minimizes memory usage and ensures consistent behavior throughout the application.

---


# **Java & Spring Boot Fact: Factory Design Pattern in Spring Boot**

## **What is the Factory Design Pattern?**
The **Factory Pattern** is a **creational design pattern** that provides an interface for creating objects without exposing the instantiation logic to the client. Instead of calling a class constructor directly, the Factory Pattern delegates object creation to a dedicated factory method.

---

## **How Does the Factory Pattern Work?**
The Factory Pattern follows these principles:
1. **Encapsulation of Object Creation** – Object creation logic is placed inside a separate factory class.
2. **Decoupling of Code** – The client code depends only on an interface, not on the concrete implementation.
3. **Flexibility** – New object types can be introduced without modifying existing code.

---

## **Why Use the Factory Pattern?**
1. **Loose Coupling** – The client doesn’t need to know the concrete implementation.
2. **Code Reusability** – Centralized object creation ensures reusable logic.
3. **Simplifies Maintenance** – Changes to object creation don’t affect the client code.
4. **Better Testability** – Easier to mock objects for unit testing.

---

## **Where is the Factory Pattern Used?**
- **Logging Systems** – Different loggers (Console, File, Database).
- **Notification Systems** – Email, SMS, Push notifications.
- **Database Connections** – MySQL, PostgreSQL, MongoDB.
- **Spring Boot Beans** – Custom object instantiation with `@Bean`.

---

## **Basic Factory Pattern Implementation in Java**
### **Step 1: Create an Interface**
```java
public interface Notification {
    void notifyUser();
}
```

### **Step 2: Implement Concrete Classes**
```java
public class EmailNotification implements Notification {
    @Override
    public void notifyUser() {
        System.out.println("Sending an Email Notification");
    }
}
```

```java
public class SMSNotification implements Notification {
    @Override
    public void notifyUser() {
        System.out.println("Sending an SMS Notification");
    }
}
```

### **Step 3: Create a Factory Class**
```java
public class NotificationFactory {
    public static Notification createNotification(String type) {
        if (type.equalsIgnoreCase("EMAIL")) {
            return new EmailNotification();
        } else if (type.equalsIgnoreCase("SMS")) {
            return new SMSNotification();
        }
        return null;
    }
}
```

### **Step 4: Use the Factory in a Client Class**
```java
public class FactoryPatternDemo {
    public static void main(String[] args) {
        Notification email = NotificationFactory.createNotification("EMAIL");
        email.notifyUser(); // Output: Sending an Email Notification

        Notification sms = NotificationFactory.createNotification("SMS");
        sms.notifyUser(); // Output: Sending an SMS Notification
    }
}
```

---

## **Factory Pattern in Spring Boot**
Spring Boot encourages using the **Factory Pattern** in various scenarios, such as **bean instantiation**, **conditional creation**, and **dynamic object management**.

### **1. Factory Method in Spring Boot Using @Bean**
Spring Boot allows factory-based object creation using `@Bean`.

```java
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class NotificationConfig {
    
    @Bean
    public Notification emailNotification() {
        return new EmailNotification();
    }

    @Bean
    public Notification smsNotification() {
        return new SMSNotification();
    }
}
```

**Injecting Factory Beans:**
```java
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class NotificationController {

    private final Notification emailNotification;
    private final Notification smsNotification;

    public NotificationController(@Qualifier("emailNotification") Notification emailNotification,
                                  @Qualifier("smsNotification") Notification smsNotification) {
        this.emailNotification = emailNotification;
        this.smsNotification = smsNotification;
    }

    @GetMapping("/notify")
    public String sendNotification(@RequestParam String type) {
        if (type.equalsIgnoreCase("EMAIL")) {
            emailNotification.notifyUser();
            return "Email sent!";
        } else if (type.equalsIgnoreCase("SMS")) {
            smsNotification.notifyUser();
            return "SMS sent!";
        }
        return "Invalid notification type!";
    }
}
```

### **Output:**
- `/notify?type=EMAIL` → **"Sending an Email Notification"**
- `/notify?type=SMS` → **"Sending an SMS Notification"**

---

## **Factory Pattern with Spring Boot & Dependency Injection**
Instead of using static factory methods, we can create a factory bean that Spring Boot manages.

### **1. Factory Interface**
```java
public interface NotificationFactory {
    Notification createNotification(String type);
}
```

### **2. Factory Implementation**
```java
import org.springframework.stereotype.Component;

@Component
public class NotificationFactoryImpl implements NotificationFactory {
    @Override
    public Notification createNotification(String type) {
        switch (type.toUpperCase()) {
            case "EMAIL":
                return new EmailNotification();
            case "SMS":
                return new SMSNotification();
            default:
                throw new IllegalArgumentException("Invalid notification type");
        }
    }
}
```

### **3. Injecting the Factory into a Controller**
```java
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/notify")
public class NotificationController {

    private final NotificationFactory notificationFactory;

    public NotificationController(NotificationFactory notificationFactory) {
        this.notificationFactory = notificationFactory;
    }

    @GetMapping("/{type}")
    public String sendNotification(@PathVariable String type) {
        Notification notification = notificationFactory.createNotification(type);
        notification.notifyUser();
        return "Notification sent: " + type;
    }
}
```

### **Output:**
- `/notify/EMAIL` → **"Sending an Email Notification"**
- `/notify/SMS` → **"Sending an SMS Notification"**

---

## **Advantages of Using Factory Pattern in Spring Boot**
### ✅ **Better Code Maintainability**
- Decouples object creation logic from business logic.

### ✅ **Scalability**
- Easy to add new notification types without modifying existing code.

### ✅ **Flexibility**
- Allows switching between different implementations dynamically.

### ✅ **Reduces Complexity**
- Avoids complex `if-else` conditions in multiple places.

---

## **Disadvantages & When Not to Use Factory Pattern**
1. **Overhead for Simple Cases**
  - If object creation is straightforward, a factory might be unnecessary.

2. **More Classes to Manage**
  - Increases code complexity by introducing multiple interfaces and factory classes.

3. **Performance Issues with Reflection-Based Factories**
  - Using Java reflection in factories can slow down object creation.

---

## **Factory Pattern vs Singleton Pattern**
| Feature | Factory Pattern | Singleton Pattern |
|---------|----------------|-------------------|
| **Purpose** | Creates different instances | Ensures a single instance |
| **Flexibility** | Supports multiple object types | Restricts to one instance |
| **Usage** | Used for object creation logic | Used for global shared objects |
| **Example** | Notification Factory | Database Connection |

---

## **Conclusion**
The **Factory Pattern** is a powerful and flexible design pattern that helps **simplify object creation**, **reduce dependencies**, and **improve maintainability**. Spring Boot encourages its use in **dynamic object creation** scenarios, such as **notifications, database connections, and business logic services**.

### **Next Steps:**
- Try implementing a **PaymentFactory** that creates `PayPalPayment`, `StripePayment`, or `BankTransferPayment`.
- Combine the **Factory Pattern with Singleton Pattern** to create a **centralized factory instance**.

---

### **Interview Question**
**Q:** How does Spring Boot support the Factory Pattern?  
**A:** Spring Boot allows factory-based object creation using `@Bean`, `@Configuration`, and `@Component`. Additionally, Dependency Injection (DI) enables dynamic object selection, making it a natural fit for the Factory Pattern.

---


# **Java & Spring Boot Fact: Builder Pattern in Spring Boot**

## **What is the Builder Pattern?**
The **Builder Pattern** is a **creational design pattern** used to construct complex objects **step by step**. It provides a way to build immutable objects while keeping the creation logic separate from the main business logic.

Instead of using **constructors with multiple parameters**, the Builder Pattern allows object creation in a **more readable and maintainable** way.

---

## **How Does the Builder Pattern Work?**
1. **Encapsulation of Object Construction** – The builder class takes responsibility for object creation.
2. **Chained Method Calls** – The builder allows method chaining for setting properties.
3. **Immutable Objects** – The pattern helps create immutable objects by setting all values at the time of construction.

---

## **Why Use the Builder Pattern?**
### ✅ **Better Readability**
- Eliminates the problem of **long constructors** (`new User("John", "Doe", "NY", 25, "Engineer")`).
- Instead, allows easy-to-read code like:
  ```java
  User user = new UserBuilder().firstName("John").lastName("Doe").age(25).build();
  ```

### ✅ **Immutable Objects**
- Since fields are **final**, the object is **thread-safe** and cannot be modified after creation.

### ✅ **Flexible Object Creation**
- Allows setting only **needed fields** without requiring unnecessary parameters.

### ✅ **Encapsulation of Complexity**
- If object creation requires multiple steps, it’s managed within the builder.

---

## **Where is the Builder Pattern Used?**
- **Creating DTOs (Data Transfer Objects)** – When passing objects in APIs.
- **Configuring HTTP Requests** – Builders are used in frameworks like `OkHttpClient` and `RestTemplate`.
- **Database Query Builders** – Hibernate and JPA use builders for queries.
- **Spring Boot Configuration Objects** – Configuring Beans dynamically.

---

## **Basic Builder Pattern Implementation in Java**
### **1. Without Builder Pattern (Long Constructor Issue)**
```java
public class User {
    private String firstName;
    private String lastName;
    private int age;
    private String city;

    public User(String firstName, String lastName, int age, String city) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
        this.city = city;
    }
}
```
Using the constructor:
```java
User user = new User("John", "Doe", 30, "New York");
```
**Issue:**
- Hard to read with multiple parameters.
- Must always pass all parameters, even if not required.

---

### **2. Builder Pattern Implementation**
```java
public class User {
    private final String firstName;
    private final String lastName;
    private final int age;
    private final String city;

    private User(UserBuilder builder) {
        this.firstName = builder.firstName;
        this.lastName = builder.lastName;
        this.age = builder.age;
        this.city = builder.city;
    }

    public static class UserBuilder {
        private String firstName;
        private String lastName;
        private int age;
        private String city;

        public UserBuilder firstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public UserBuilder lastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public UserBuilder age(int age) {
            this.age = age;
            return this;
        }

        public UserBuilder city(String city) {
            this.city = city;
            return this;
        }

        public User build() {
            return new User(this);
        }
    }
}
```

**Usage:**
```java
User user = new User.UserBuilder()
        .firstName("John")
        .lastName("Doe")
        .age(30)
        .city("New York")
        .build();
```

### **Benefits:**
✔ No need to pass unnecessary parameters.  
✔ Readable and self-explanatory.  
✔ Creates **immutable** objects.

---

## **Builder Pattern in Spring Boot**
Spring Boot provides built-in support for the Builder Pattern with frameworks like **Lombok** and **Jackson**.

### **1. Using Lombok’s `@Builder` Annotation**
Lombok eliminates boilerplate code and simplifies builder creation.

```java
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class User {
    private String firstName;
    private String lastName;
    private int age;
    private String city;
}
```

**Usage:**
```java
User user = User.builder()
        .firstName("John")
        .lastName("Doe")
        .age(30)
        .city("New York")
        .build();
```
✔ No need to manually define the builder class.  
✔ Increases code simplicity.

---

### **2. Using Builder Pattern with REST APIs in Spring Boot**
Spring Boot uses the **Builder Pattern** extensively in APIs and response objects.

#### **Example: Building API Responses with Builder Pattern**
```java
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ApiResponse {
    private int status;
    private String message;
    private Object data;
}
```
#### **Controller:**
```java
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    @GetMapping("/{id}")
    public ApiResponse getUser(@PathVariable int id) {
        User user = User.builder()
                        .firstName("John")
                        .lastName("Doe")
                        .age(30)
                        .city("New York")
                        .build();

        return ApiResponse.builder()
                .status(200)
                .message("User retrieved successfully")
                .data(user)
                .build();
    }
}
```

**Output (JSON Response):**
```json
{
    "status": 200,
    "message": "User retrieved successfully",
    "data": {
        "firstName": "John",
        "lastName": "Doe",
        "age": 30,
        "city": "New York"
    }
}
```

✔ Ensures **clean, structured, and readable code**.  
✔ **Decouples object creation from logic**.

---

### **3. Builder Pattern in Query Construction (Spring Data JPA)**
Spring Data JPA uses the Builder Pattern internally in its **Criteria API** for dynamic queries.

```java
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import org.springframework.stereotype.Repository;

@Repository
public class UserSpecification {

    public static Specification<User> withAge(int age) {
        return (root, query, criteriaBuilder) -> 
                criteriaBuilder.equal(root.get("age"), age);
    }
}
```

**Usage in Repository:**
```java
List<User> users = userRepository.findAll(UserSpecification.withAge(30));
```

✔ Helps build **dynamic queries efficiently**.  
✔ Avoids complex `if-else` conditions for queries.

---

## **Advantages of Using the Builder Pattern in Spring Boot**
| Feature | Benefit |
|---------|---------|
| **Improves Readability** | Makes object creation self-explanatory. |
| **Handles Complex Objects** | Useful when dealing with multiple fields. |
| **Encapsulates Object Creation** | Keeps the main logic clean. |
| **Supports Optional Parameters** | Only set needed values. |
| **Creates Immutable Objects** | Ensures thread-safety. |

---

## **When NOT to Use the Builder Pattern**
❌ **For Simple Objects** – If an object has only **2-3 fields**, a constructor is enough.  
❌ **Performance Overhead** – Extra memory is used in creating builder instances.  
❌ **Unnecessary Complexity** – In simple applications, it may add unnecessary boilerplate code.

---

## **Builder Pattern vs Factory Pattern**
| Feature | Builder Pattern | Factory Pattern |
|---------|---------------|---------------|
| **Purpose** | Constructs complex objects step by step | Centralized object creation logic |
| **Flexibility** | Allows optional parameters | Creates different object types |
| **Usage** | Used when an object has many fields | Used when an object has multiple variations |
| **Example** | User.builder().firstName("John").build() | NotificationFactory.create("EMAIL") |

---

## **Conclusion**
The **Builder Pattern** is a **powerful tool** in Spring Boot that helps create **clean, readable, and maintainable** code. It simplifies object creation for **DTOs, API responses, and configurations**, ensuring better maintainability.

### **Next Steps**
- Try implementing a **ProductBuilder** for an e-commerce system.
- Use **Lombok’s `@Builder` annotation** to reduce boilerplate.
- Apply the **Builder Pattern in Spring Data JPA Queries**.

---

### **Interview Question**
**Q:** How does Spring Boot benefit from the Builder Pattern?  
**A:** Spring Boot extensively uses the Builder Pattern in APIs, DTOs, and configuration objects to improve **readability, flexibility, and immutability**.

---


# **Java & Spring Boot Fact: Observer Design Pattern in Spring Boot**

## **What is the Observer Pattern?**
The **Observer Pattern** is a **behavioral design pattern** that establishes a **one-to-many dependency** between objects. When one object (the **Subject**) changes state, all its dependent objects (the **Observers**) are automatically notified and updated.

This pattern is commonly used in **event-driven systems**, such as **Spring Boot's event listeners**, messaging systems, and UI frameworks.

---

## **How Does the Observer Pattern Work?**
1. **Subject (Publisher)** – Maintains a list of observers and notifies them of state changes.
2. **Observers (Subscribers)** – Register with the subject and react to updates.
3. **Loose Coupling** – The subject doesn't need to know details about observers, making it easy to add/remove observers dynamically.

---

## **Why Use the Observer Pattern?**
✅ **Decoupled Code** – Publishers don’t need to know details about subscribers.  
✅ **Scalability** – Supports multiple observers reacting to a single event.  
✅ **Flexibility** – New observers can be added dynamically without modifying the subject.  
✅ **Event-Driven Programming** – Used in real-world event systems, such as Spring Boot's event handling.

---

## **Where is the Observer Pattern Used?**
- **Event Listeners in Spring Boot** – `@EventListener` annotation.
- **Real-time Updates in UI Frameworks** – ReactJS, Angular, etc.
- **Messaging Systems** – Kafka, RabbitMQ, WebSockets.
- **Stock Market Applications** – Stocks updating in real time.
- **Chat Applications** – New messages triggering updates in UI.

---

## **Implementing Observer Pattern in Java**
### **1. Define the Observer Interface**
```java
public interface Observer {
    void update(String message);
}
```

### **2. Create Concrete Observers**
```java
public class EmailSubscriber implements Observer {
    private String name;

    public EmailSubscriber(String name) {
        this.name = name;
    }

    @Override
    public void update(String message) {
        System.out.println(name + " received an email: " + message);
    }
}
```
```java
public class SMSSubscriber implements Observer {
    private String phoneNumber;

    public SMSSubscriber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Override
    public void update(String message) {
        System.out.println("SMS to " + phoneNumber + ": " + message);
    }
}
```

### **3. Create the Subject Interface**
```java
import java.util.ArrayList;
import java.util.List;

public class NotificationService {
    private List<Observer> observers = new ArrayList<>();

    public void addObserver(Observer observer) {
        observers.add(observer);
    }

    public void removeObserver(Observer observer) {
        observers.remove(observer);
    }

    public void notifyObservers(String message) {
        for (Observer observer : observers) {
            observer.update(message);
        }
    }
}
```

### **4. Using the Observer Pattern**
```java
public class ObserverPatternDemo {
    public static void main(String[] args) {
        NotificationService notificationService = new NotificationService();

        Observer emailUser = new EmailSubscriber("John Doe");
        Observer smsUser = new SMSSubscriber("+123456789");

        notificationService.addObserver(emailUser);
        notificationService.addObserver(smsUser);

        // Send notification
        notificationService.notifyObservers("New Product Launch!");

        // Output:
        // John Doe received an email: New Product Launch!
        // SMS to +123456789: New Product Launch!
    }
}
```

---

## **Observer Pattern in Spring Boot**
Spring Boot provides a built-in mechanism for implementing the **Observer Pattern** using **Spring Events**.

### **1. Define a Custom Event**
```java
import org.springframework.context.ApplicationEvent;

public class OrderPlacedEvent extends ApplicationEvent {
    private String orderId;

    public OrderPlacedEvent(Object source, String orderId) {
        super(source);
        this.orderId = orderId;
    }

    public String getOrderId() {
        return orderId;
    }
}
```

### **2. Create an Event Publisher**
```java
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
public class OrderService {
    private final ApplicationEventPublisher eventPublisher;

    public OrderService(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    public void placeOrder(String orderId) {
        System.out.println("Order placed: " + orderId);
        eventPublisher.publishEvent(new OrderPlacedEvent(this, orderId));
    }
}
```

### **3. Create an Event Listener (Observer)**
```java
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class EmailNotificationListener {

    @EventListener
    public void handleOrderPlacedEvent(OrderPlacedEvent event) {
        System.out.println("Sending email for order: " + event.getOrderId());
    }
}
```

### **4. Calling the Service**
```java
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class AppRunner implements CommandLineRunner {
    private final OrderService orderService;

    public AppRunner(OrderService orderService) {
        this.orderService = orderService;
    }

    @Override
    public void run(String... args) throws Exception {
        orderService.placeOrder("ORD123");
    }
}
```

### **Output:**
```
Order placed: ORD123
Sending email for order: ORD123
```

✔ **Decoupled Components** – The order service does not directly call the email service.  
✔ **Easier Maintenance** – Adding a new listener (e.g., SMS notifications) does not change existing code.  
✔ **Asynchronous Execution** – Spring events can be executed asynchronously using `@Async`.

---

## **Observer Pattern in WebSockets (Real-Time Communication)**
Spring Boot supports real-time updates using **WebSockets**. The Observer Pattern is used in **chat applications, stock updates, and real-time dashboards**.

### **1. WebSocket Configuration**
```java
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new WebSocketMessageHandler(), "/ws");
    }
}
```

### **2. WebSocket Message Handler**
```java
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class WebSocketMessageHandler extends TextWebSocketHandler {
    private List<WebSocketSession> sessions = new ArrayList<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        sessions.add(session);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
        for (WebSocketSession s : sessions) {
            s.sendMessage(new TextMessage("Update: " + message.getPayload()));
        }
    }
}
```

✔ **Multiple Users Receive Updates** – Follows the Observer Pattern.  
✔ **Used in Chat Apps, Stocks, IoT Dashboards** – Sends live data to all connected users.

---

## **Advantages of the Observer Pattern in Spring Boot**
| Feature | Benefit |
|---------|---------|
| **Decoupled Code** | Observers don’t depend on publishers directly. |
| **Flexible** | New observers can be added without modifying existing code. |
| **Event-Driven Architecture** | Works well with Spring Boot’s event system. |
| **Real-Time Updates** | Used in WebSockets, messaging, and notifications. |
| **Supports Asynchronous Execution** | With `@Async`, observers can run in the background. |

---

## **When NOT to Use the Observer Pattern**
❌ **If Only One Object Needs the Data** – No need for multiple observers.  
❌ **Performance Overhead in Large Systems** – Too many observers may slow down the application.  
❌ **Complex Event Chains** – If events trigger other events, debugging can be difficult.

---

## **Conclusion**
The **Observer Pattern** is widely used in **Spring Boot's event system, messaging, and WebSockets**. It **reduces coupling, increases flexibility, and simplifies event-driven architectures**.

### **Next Steps**
- Implement **SMS notifications** using another `@EventListener`.
- Use **RabbitMQ or Kafka** for event-driven messaging.
- Explore **Spring WebSockets** for real-time applications.

---

### **Interview Question**
**Q:** How does Spring Boot implement the Observer Pattern?  
**A:** Spring Boot uses the Observer Pattern in its event system (`ApplicationEvent`, `@EventListener`), WebSockets, and messaging platforms like RabbitMQ.

---


# **Java & Spring Boot Fact: Strategy Pattern in Spring Boot**

## **What is the Strategy Pattern?**
The **Strategy Pattern** is a **behavioral design pattern** that allows a class to choose a specific implementation of a behavior **at runtime**. Instead of using multiple `if-else` or `switch` statements, the Strategy Pattern allows for a **clean, modular, and extensible approach**.

In **Spring Boot**, this pattern is widely used for **algorithm selection, payment processing, authentication strategies, and service implementations**.

---

## **How Does the Strategy Pattern Work?**
1. **Define a Strategy Interface** – Defines the common method(s) to be implemented.
2. **Implement Multiple Concrete Strategies** – Each strategy has its own implementation.
3. **Use a Context Class** – The context class decides which strategy to use at runtime.
4. **Dependency Injection in Spring Boot** – Spring can automatically inject the appropriate strategy based on configuration.

---

## **Why Use the Strategy Pattern?**
✅ **Eliminates Complex `if-else` Statements** – Avoids nested conditions in business logic.  
✅ **Improves Maintainability** – New strategies can be added without modifying existing code.  
✅ **Enhances Testability** – Each strategy can be tested independently.  
✅ **Enables Runtime Behavior Changes** – Allows dynamic selection of strategies based on conditions.  
✅ **Supports Dependency Injection** – Works seamlessly with Spring Boot’s `@Autowired` and `@Component` features.

---

## **Where is the Strategy Pattern Used?**
- **Payment Processing** – Credit Card, PayPal, Bitcoin, etc.
- **Authentication Strategies** – OAuth, JWT, LDAP, etc.
- **Sorting Algorithms** – Bubble Sort, Quick Sort, Merge Sort, etc.
- **Discount Calculation** – Fixed discount, percentage discount, seasonal discounts.
- **Shipping Methods** – Air, sea, road transportation.

---

## **Implementing Strategy Pattern in Java**
### **1. Define the Strategy Interface**
```java
public interface PaymentStrategy {
    void pay(int amount);
}
```

### **2. Implement Different Payment Strategies**
```java
public class CreditCardPayment implements PaymentStrategy {
    @Override
    public void pay(int amount) {
        System.out.println("Paid " + amount + " using Credit Card.");
    }
}
```

```java
public class PayPalPayment implements PaymentStrategy {
    @Override
    public void pay(int amount) {
        System.out.println("Paid " + amount + " using PayPal.");
    }
}
```

### **3. Create the Context Class**
```java
public class PaymentContext {
    private PaymentStrategy paymentStrategy;

    public void setPaymentStrategy(PaymentStrategy paymentStrategy) {
        this.paymentStrategy = paymentStrategy;
    }

    public void processPayment(int amount) {
        if (paymentStrategy == null) {
            throw new IllegalStateException("Payment strategy is not set");
        }
        paymentStrategy.pay(amount);
    }
}
```

### **4. Using the Strategy Pattern**
```java
public class StrategyPatternDemo {
    public static void main(String[] args) {
        PaymentContext paymentContext = new PaymentContext();

        paymentContext.setPaymentStrategy(new CreditCardPayment());
        paymentContext.processPayment(100); // Output: Paid 100 using Credit Card.

        paymentContext.setPaymentStrategy(new PayPalPayment());
        paymentContext.processPayment(200); // Output: Paid 200 using PayPal.
    }
}
```

✔ **Flexible & Extendable** – New payment methods can be added without modifying existing code.  
✔ **Decoupled Code** – The client only depends on the `PaymentStrategy` interface, not specific implementations.

---

## **Strategy Pattern in Spring Boot**
Spring Boot allows for an even **cleaner** implementation of the **Strategy Pattern** using `@Component` and `@Autowired`.

### **1. Define the Payment Strategy Interface**
```java
public interface PaymentStrategy {
    String pay(int amount);
}
```

### **2. Implement Different Payment Strategies**
```java
import org.springframework.stereotype.Component;

@Component("creditCard")
public class CreditCardPayment implements PaymentStrategy {
    @Override
    public String pay(int amount) {
        return "Paid " + amount + " using Credit Card.";
    }
}
```

```java
import org.springframework.stereotype.Component;

@Component("paypal")
public class PayPalPayment implements PaymentStrategy {
    @Override
    public String pay(int amount) {
        return "Paid " + amount + " using PayPal.";
    }
}
```

### **3. Use a Strategy Factory to Select Payment Method**
```java
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Map;

@Service
public class PaymentService {
    private final Map<String, PaymentStrategy> paymentStrategies;

    @Autowired
    public PaymentService(Map<String, PaymentStrategy> paymentStrategies) {
        this.paymentStrategies = paymentStrategies;
    }

    public String processPayment(String type, int amount) {
        PaymentStrategy strategy = paymentStrategies.get(type);
        if (strategy == null) {
            throw new IllegalArgumentException("Invalid payment type: " + type);
        }
        return strategy.pay(amount);
    }
}
```

### **4. Create a REST Controller to Accept Payment Requests**
```java
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payment")
public class PaymentController {
    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @GetMapping("/{type}/{amount}")
    public String pay(@PathVariable String type, @PathVariable int amount) {
        return paymentService.processPayment(type, amount);
    }
}
```

### **5. Test the API**
#### **Request:**
```bash
curl http://localhost:8080/payment/creditCard/100
```
#### **Response:**
```json
"Paid 100 using Credit Card."
```

#### **Request:**
```bash
curl http://localhost:8080/payment/paypal/200
```
#### **Response:**
```json
"Paid 200 using PayPal."
```

✔ **Loose Coupling** – New payment strategies can be added **without modifying controllers or services**.  
✔ **Spring Boot’s `@Component`** – Automatically detects and injects all available strategies.  
✔ **Scalability** – Easily supports **new payment methods** in the future.

---

## **Real-World Use Cases for Strategy Pattern**
| Use Case | Example |
|----------|---------|
| **Payment Processing** | Credit Card, PayPal, Bitcoin, Bank Transfer |
| **Authentication** | OAuth, LDAP, JWT, Basic Auth |
| **Sorting Algorithms** | QuickSort, MergeSort, BubbleSort |
| **Discount Strategies** | Fixed Discount, Percentage Discount, Seasonal Discount |
| **Shipping Methods** | Air Freight, Sea Cargo, Road Transport |

---

## **Advantages of Using Strategy Pattern in Spring Boot**
| Feature | Benefit |
|---------|---------|
| **Eliminates `if-else` Logic** | Improves readability and maintainability. |
| **Runtime Behavior Selection** | Allows dynamic switching of algorithms. |
| **Encapsulation** | Keeps business logic separate from decision logic. |
| **Extensibility** | New strategies can be added **without modifying existing code**. |
| **Dependency Injection (DI) Support** | Automatically selects strategies using Spring Boot’s DI. |

---

## **When NOT to Use the Strategy Pattern**
❌ **If Behavior Does Not Change** – Unnecessary complexity for simple, static behavior.  
❌ **Too Many Strategies** – Managing too many strategy classes may increase maintenance overhead.  
❌ **Performance Overhead** – Object creation and dependency injection may slightly impact performance in high-load systems.

---

## **Strategy Pattern vs Factory Pattern**
| Feature | Strategy Pattern | Factory Pattern |
|---------|-----------------|----------------|
| **Purpose** | Allows dynamic selection of algorithms | Centralized object creation |
| **Flexibility** | Behaviors can be changed at runtime | Creates different object types |
| **Usage** | Sorting, payment methods, authentication | Notification factory, object creation |

---

## **Conclusion**
The **Strategy Pattern** is a powerful way to **decouple business logic from decision logic**, allowing **dynamic behavior selection** in **Spring Boot applications**. It is particularly useful for **payment processing, authentication mechanisms, sorting, and discount calculations**.

### **Next Steps**
- Implement a **discount calculation service** using the Strategy Pattern.
- Add **multiple authentication strategies (JWT, OAuth, LDAP)** in a Spring Security project.
- Experiment with **Spring Boot’s `@Primary` annotation** to set a default strategy.

---

### **Interview Question**
**Q:** How does Spring Boot support the Strategy Pattern?  
**A:** Spring Boot leverages **Dependency Injection (DI)** to automatically inject the correct strategy. By using `@Component` and `@Autowired Map<String, Strategy>`, Spring can dynamically select the appropriate strategy at runtime.

---


# **Java & Spring Boot Fact: Adapter Pattern in Spring Boot**

## **What is the Adapter Pattern?**
The **Adapter Pattern** is a **structural design pattern** that allows incompatible interfaces to work together. It acts as a **bridge** between two different systems or classes by converting one interface into another.

This pattern is especially useful when integrating **third-party libraries, legacy code, or external APIs** that don’t match the expected interface in a system.

---

## **How Does the Adapter Pattern Work?**
1. **Target Interface** – Defines the expected behavior in the application.
2. **Adaptee** – Represents an existing class with an incompatible interface.
3. **Adapter** – A wrapper that **translates requests** from the Target to the Adaptee.

---

## **Why Use the Adapter Pattern?**
✅ **Integrate Legacy Code** – Connect old systems with new ones without modifying existing code.  
✅ **Enable Third-Party APIs** – Standardize external API interactions.  
✅ **Enhance Code Reusability** – Use different implementations interchangeably.  
✅ **Improve Maintainability** – Keep the application loosely coupled and extensible.

---

## **Where is the Adapter Pattern Used?**
- **Converting Third-Party Libraries** – Adapting APIs like Google Maps, PayPal, etc.
- **Integrating External Payment Gateways** – Different providers (Stripe, Razorpay, PayPal).
- **Working with Different Data Formats** – XML to JSON conversion.
- **Legacy System Integration** – Modern applications interacting with old services.
- **Database Adaptation** – Switching from MySQL to PostgreSQL with a uniform interface.

---

## **Implementing Adapter Pattern in Java**
### **1. Define the Target Interface (Expected Interface)**
```java
public interface PaymentProcessor {
    void processPayment(double amount);
}
```

### **2. Create an Existing (Incompatible) Third-Party Class**
```java
public class PayPalPayment {
    public void sendPayment(double amount) {
        System.out.println("Processing PayPal payment of $" + amount);
    }
}
```

### **3. Implement the Adapter Class**
```java
public class PayPalAdapter implements PaymentProcessor {
    private PayPalPayment payPalPayment;

    public PayPalAdapter(PayPalPayment payPalPayment) {
        this.payPalPayment = payPalPayment;
    }

    @Override
    public void processPayment(double amount) {
        payPalPayment.sendPayment(amount); // Converts Target to Adaptee
    }
}
```

### **4. Using the Adapter**
```java
public class AdapterPatternDemo {
    public static void main(String[] args) {
        PayPalPayment payPalPayment = new PayPalPayment();
        PaymentProcessor processor = new PayPalAdapter(payPalPayment);

        processor.processPayment(100); // Output: Processing PayPal payment of $100
    }
}
```

✔ **Bridges Two Incompatible Interfaces** – Converts `processPayment()` to `sendPayment()`.  
✔ **Keeps Code Clean and Maintainable** – No changes needed in `PayPalPayment`.

---

## **Adapter Pattern in Spring Boot**
Spring Boot uses the **Adapter Pattern** in various frameworks like **Spring Security, RestTemplate, and Data Converters**.

### **1. Implementing Adapter Pattern for Payment Processing**
#### **1.1 Define the Target Interface**
```java
public interface PaymentProcessor {
    String processPayment(double amount);
}
```

#### **1.2 Implement Multiple Adaptees (Third-Party APIs)**
```java
import org.springframework.stereotype.Component;

@Component
public class StripePayment {
    public String makeStripePayment(double amount) {
        return "Stripe payment of $" + amount + " processed.";
    }
}
```

```java
import org.springframework.stereotype.Component;

@Component
public class RazorpayPayment {
    public String executeRazorpayTransaction(double amount) {
        return "Razorpay payment of $" + amount + " completed.";
    }
}
```

#### **1.3 Create Adapter Classes**
```java
import org.springframework.stereotype.Component;

@Component("stripeAdapter")
public class StripeAdapter implements PaymentProcessor {
    private final StripePayment stripePayment;

    public StripeAdapter(StripePayment stripePayment) {
        this.stripePayment = stripePayment;
    }

    @Override
    public String processPayment(double amount) {
        return stripePayment.makeStripePayment(amount);
    }
}
```

```java
import org.springframework.stereotype.Component;

@Component("razorpayAdapter")
public class RazorpayAdapter implements PaymentProcessor {
    private final RazorpayPayment razorpayPayment;

    public RazorpayAdapter(RazorpayPayment razorpayPayment) {
        this.razorpayPayment = razorpayPayment;
    }

    @Override
    public String processPayment(double amount) {
        return razorpayPayment.executeRazorpayTransaction(amount);
    }
}
```

#### **1.4 Use Dependency Injection to Select Payment Method**
```java
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class PaymentService {
    private final Map<String, PaymentProcessor> paymentProcessors;

    @Autowired
    public PaymentService(Map<String, PaymentProcessor> paymentProcessors) {
        this.paymentProcessors = paymentProcessors;
    }

    public String makePayment(String type, double amount) {
        PaymentProcessor processor = paymentProcessors.get(type);
        if (processor == null) {
            throw new IllegalArgumentException("Invalid payment type: " + type);
        }
        return processor.processPayment(amount);
    }
}
```

#### **1.5 Expose a REST API for Payment Processing**
```java
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payment")
public class PaymentController {
    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @GetMapping("/{type}/{amount}")
    public String processPayment(@PathVariable String type, @PathVariable double amount) {
        return paymentService.makePayment(type, amount);
    }
}
```

### **2. Testing the API**
#### **Request:**
```bash
curl http://localhost:8080/payment/stripeAdapter/150
```
#### **Response:**
```json
"Stripe payment of $150 processed."
```

#### **Request:**
```bash
curl http://localhost:8080/payment/razorpayAdapter/200
```
#### **Response:**
```json
"Razorpay payment of $200 completed."
```

✔ **Encapsulates Third-Party Integration** – Different APIs are handled seamlessly.  
✔ **Spring Boot Automatically Injects Adapters** – Uses `@Component` and `@Autowired`.  
✔ **Easily Extendable** – Adding **PayPal, Square, or Bitcoin** requires just a new adapter.

---

## **Real-World Use Cases for Adapter Pattern**
| Use Case | Example |
|----------|---------|
| **Third-Party API Integration** | Payment gateways, cloud storage APIs |
| **Legacy System Integration** | Connecting old Java applications with new microservices |
| **Database Adaptation** | Switching between different databases (MySQL, PostgreSQL, MongoDB) |
| **File Format Conversion** | Adapting JSON to XML responses |
| **Logging Frameworks** | Integrating Log4j, SLF4J, or Java Util Logging |

---

## **Advantages of Using the Adapter Pattern in Spring Boot**
| Feature | Benefit |
|---------|---------|
| **Loose Coupling** | Separates business logic from third-party dependencies. |
| **Encapsulation** | Hides complex API integrations inside adapter classes. |
| **Scalability** | New APIs can be added without modifying existing code. |
| **Improves Code Readability** | No direct dependency on external APIs. |
| **Spring Boot Dependency Injection** | Automatically manages adapters at runtime. |

---

## **When NOT to Use the Adapter Pattern**
❌ **If Direct Integration is Sufficient** – When an API fits directly into your application.  
❌ **If There Are Too Many Adapters** – May add unnecessary complexity.  
❌ **Performance Overhead** – Each adapter introduces an extra layer of method calls.

---

## **Adapter Pattern vs Strategy Pattern**
| Feature | Adapter Pattern | Strategy Pattern |
|---------|---------------|----------------|
| **Purpose** | Converts one interface to another | Dynamically selects an algorithm |
| **Focus** | Bridging different interfaces | Behavior switching at runtime |
| **Example** | Adapting third-party APIs | Choosing payment method dynamically |

---

## **Conclusion**
The **Adapter Pattern** is an essential tool in **Spring Boot** for integrating **external APIs, legacy systems, and different interfaces** while maintaining a **clean, modular architecture**.

### **Next Steps**
- Implement an **Email Service Adapter** for **SendGrid, Amazon SES, and SMTP**.
- Use the Adapter Pattern to **convert XML APIs to JSON responses**.
- Extend the **payment service** to support **Bitcoin payments**.

---

### **Interview Question**
**Q:** How does Spring Boot use the Adapter Pattern internally?  
**A:** Spring Boot applies the Adapter Pattern in **Spring Security (Authentication Providers), RestTemplate, and Data Format Converters** to integrate different interfaces seamlessly.

---


# **Java & Spring Boot Fact: Proxy Pattern in Spring Boot**

## **What is the Proxy Pattern?**
The **Proxy Pattern** is a **structural design pattern** that acts as a substitute or intermediary for another object. A proxy controls access to the actual object, adding functionalities like **caching, security, lazy initialization, or logging** before delegating the request to the real object.

In **Spring Boot**, the Proxy Pattern is widely used in **AOP (Aspect-Oriented Programming), transaction management, security proxies, and lazy-loaded beans.**

---

## **How Does the Proxy Pattern Work?**
1. **Subject (Interface)** – Defines the operations that both the real object and proxy must implement.
2. **Real Object (Service Class)** – Contains the actual implementation of business logic.
3. **Proxy (Interceptor)** – Acts as an intermediary, adding extra behavior before forwarding requests to the real object.

---

## **Why Use the Proxy Pattern?**
✅ **Lazy Initialization** – Loads heavy objects only when needed.  
✅ **Security** – Controls access to sensitive resources.  
✅ **Logging & Auditing** – Captures method calls before executing the real logic.  
✅ **Performance Optimization** – Uses caching to improve response times.  
✅ **Transaction Management** – Automatically wraps method calls with transaction handling.

---

## **Where is the Proxy Pattern Used?**
- **Spring AOP (Aspect-Oriented Programming)** – Logging, security, and transaction management.
- **Database Caching Proxies** – Improves query performance.
- **Security Proxies** – Restricts access based on roles.
- **REST API Rate Limiting** – Prevents excessive calls from a single user.
- **Lazy-Loading in Hibernate** – Loads data only when required.

---

## **Implementing Proxy Pattern in Java**
### **1. Define the Subject Interface**
```java
public interface PaymentService {
    void processPayment(double amount);
}
```

### **2. Create the Real Object (Actual Service)**
```java
public class RealPaymentService implements PaymentService {
    @Override
    public void processPayment(double amount) {
        System.out.println("Processing payment of $" + amount);
    }
}
```

### **3. Implement a Proxy Class**
```java
public class PaymentServiceProxy implements PaymentService {
    private RealPaymentService realPaymentService;
    
    public PaymentServiceProxy(RealPaymentService realPaymentService) {
        this.realPaymentService = realPaymentService;
    }

    @Override
    public void processPayment(double amount) {
        System.out.println("Logging: Attempting to process payment of $" + amount);
        realPaymentService.processPayment(amount);
        System.out.println("Logging: Payment of $" + amount + " processed successfully.");
    }
}
```

### **4. Using the Proxy Pattern**
```java
public class ProxyPatternDemo {
    public static void main(String[] args) {
        PaymentService paymentService = new PaymentServiceProxy(new RealPaymentService());
        paymentService.processPayment(100);
    }
}
```

**Output:**
```
Logging: Attempting to process payment of $100
Processing payment of $100
Logging: Payment of $100 processed successfully.
```

✔ **Adds Logging Without Modifying Business Logic**  
✔ **Decouples Logging from Core Service Implementation**

---

## **Proxy Pattern in Spring Boot**
Spring Boot **automatically applies proxies** for **transaction management, AOP logging, security, and lazy loading.**

### **1. Proxy Pattern in AOP (Aspect-Oriented Programming)**
Spring AOP uses proxies to wrap methods **before** or **after** execution.

#### **1.1 Define the Service**
```java
import org.springframework.stereotype.Service;

@Service
public class PaymentService {
    public void processPayment(double amount) {
        System.out.println("Processing payment of $" + amount);
    }
}
```

#### **1.2 Create an Aspect for Logging (Proxy)**
```java
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.After;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {
    
    @Before("execution(* com.example.demo.PaymentService.processPayment(..))")
    public void beforePayment() {
        System.out.println("Logging: Payment processing started.");
    }

    @After("execution(* com.example.demo.PaymentService.processPayment(..))")
    public void afterPayment() {
        System.out.println("Logging: Payment processing completed.");
    }
}
```

#### **1.3 Call the Service**
```java
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class AppRunner implements CommandLineRunner {
    private final PaymentService paymentService;

    public AppRunner(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @Override
    public void run(String... args) throws Exception {
        paymentService.processPayment(150);
    }
}
```

**Output:**
```
Logging: Payment processing started.
Processing payment of $150
Logging: Payment processing completed.
```

✔ **No Changes in Business Logic** – AOP handles logging transparently.  
✔ **Proxy is Applied Dynamically at Runtime** – Uses `@Aspect` annotation.

---

### **2. Proxy Pattern in Spring Transactions**
Spring Boot **automatically applies a proxy** for transaction management.

#### **2.1 Service with `@Transactional`**
```java
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {
    
    @Transactional
    public void placeOrder(String orderId) {
        System.out.println("Transaction started for order: " + orderId);
        // Database operations...
        System.out.println("Transaction committed for order: " + orderId);
    }
}
```

#### **2.2 Call the Service**
```java
@Component
public class OrderRunner implements CommandLineRunner {
    private final OrderService orderService;

    public OrderRunner(OrderService orderService) {
        this.orderService = orderService;
    }

    @Override
    public void run(String... args) throws Exception {
        orderService.placeOrder("ORD001");
    }
}
```

**Output:**
```
Transaction started for order: ORD001
Transaction committed for order: ORD001
```

✔ **Spring Automatically Wraps Method Calls in Transactions**  
✔ **Ensures Database Consistency**

---

### **3. Proxy Pattern in Lazy Loading (Hibernate)**
Spring Boot uses **proxies to delay database queries** until required.

#### **3.1 Entity Class**
```java
import javax.persistence.*;

@Entity
public class Customer {
    @Id @GeneratedValue
    private Long id;
    private String name;

    @OneToOne(fetch = FetchType.LAZY)
    private Address address;
}
```

#### **3.2 Lazy Loading in Action**
```java
Customer customer = customerRepository.findById(1L).get(); 
System.out.println(customer.getName()); // No DB query for address
System.out.println(customer.getAddress()); // Triggers DB query
```

✔ **Delays Database Queries Until Needed**  
✔ **Improves Performance by Avoiding Unnecessary Joins**

---

## **Real-World Use Cases for Proxy Pattern**
| Use Case | Example |
|----------|---------|
| **AOP Logging & Auditing** | Logs method calls without modifying code |
| **Security Proxies** | Restricts access based on user roles |
| **Transaction Management** | Automatically commits or rolls back transactions |
| **Caching Proxies** | Stores API responses to reduce load |
| **Lazy Loading in Hibernate** | Delays fetching large datasets |

---

## **Advantages of Using the Proxy Pattern in Spring Boot**
| Feature | Benefit |
|---------|---------|
| **Encapsulation** | Keeps additional behavior separate from core logic |
| **Performance Optimization** | Uses caching and lazy loading |
| **Security Control** | Prevents unauthorized access |
| **Automatic Transaction Management** | Ensures DB consistency with `@Transactional` |
| **Flexible & Scalable** | New proxy behaviors can be added without modifying the real object |

---

## **When NOT to Use the Proxy Pattern**
❌ **If Performance is Critical** – Proxies add an extra method call.  
❌ **If No Additional Behavior is Needed** – Unnecessary overhead.  
❌ **If Direct Object Access is Required** – Proxies may interfere with debugging.

---

## **Proxy Pattern vs Decorator Pattern**
| Feature | Proxy Pattern | Decorator Pattern |
|---------|--------------|-------------------|
| **Purpose** | Controls access to an object | Adds functionality to an object |
| **Focus** | Security, logging, caching | Enhancing object behavior |
| **Example** | Spring AOP, Hibernate Lazy Loading | Adding encryption to file operations |

---

## **Conclusion**
The **Proxy Pattern** is widely used in **Spring Boot** to enable **logging, security, transactions, caching, and lazy loading**. It enhances **performance, maintainability, and flexibility** while keeping business logic **clean and reusable**.

### **Next Steps**
- Implement a **security proxy** that checks user roles before method execution.
- Extend **transaction management** to support rollback scenarios.
- Use **caching proxies** to reduce database load.

---


# **Java & Spring Boot Fact: Chain of Responsibility Pattern in Spring Boot**

## **What is the Chain of Responsibility Pattern?**
The **Chain of Responsibility Pattern** is a **behavioral design pattern** that allows multiple handlers to process a request **sequentially**. Instead of a single method handling all logic, requests **pass through a chain of handlers** where each handler decides whether to process the request or pass it to the next handler.

In **Spring Boot**, this pattern is commonly used in **request processing pipelines, logging systems, authentication filters, and middleware processing.**

---

## **How Does the Chain of Responsibility Pattern Work?**
1. **Handler Interface** – Defines the contract for processing requests.
2. **Concrete Handlers** – Implementations that process the request or pass it to the next handler.
3. **Chain Setup** – Handlers are linked in a sequence to process requests in order.
4. **Client Request Processing** – The first handler receives the request and starts the chain.

---

## **Why Use the Chain of Responsibility Pattern?**
✅ **Decouples Request Processing** – Each handler processes only what it needs to.  
✅ **Flexible & Extendable** – New handlers can be added without modifying existing ones.  
✅ **Improves Maintainability** – Reduces large `if-else` conditions.  
✅ **Supports Dynamic Request Handling** – Allows runtime selection of handlers.  
✅ **Enhances Logging, Security, and Filtering** – Common in API processing and authentication.

---

## **Where is the Chain of Responsibility Pattern Used?**
- **Spring Boot Filter Chains** – Used in authentication, security, and API request processing.
- **Exception Handling Middleware** – Centralized exception handling in APIs.
- **Logging Frameworks** – Multiple loggers (file, database, console) processing logs.
- **Transaction Processing** – Sequential validation and execution of business rules.
- **Validation Pipelines** – Input validation in REST APIs.

---

## **Implementing Chain of Responsibility Pattern in Java**
### **1. Define the Handler Interface**
```java
public interface RequestHandler {
    void handle(Request request);
}
```

### **2. Create a Request Class**
```java
public class Request {
    private String message;
    
    public Request(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
```

### **3. Implement Concrete Handlers**
```java
public class AuthenticationHandler implements RequestHandler {
    private RequestHandler nextHandler;

    public AuthenticationHandler(RequestHandler nextHandler) {
        this.nextHandler = nextHandler;
    }

    @Override
    public void handle(Request request) {
        System.out.println("Authentication check for request: " + request.getMessage());
        if (nextHandler != null) {
            nextHandler.handle(request);
        }
    }
}
```
```java
public class LoggingHandler implements RequestHandler {
    private RequestHandler nextHandler;

    public LoggingHandler(RequestHandler nextHandler) {
        this.nextHandler = nextHandler;
    }

    @Override
    public void handle(Request request) {
        System.out.println("Logging request: " + request.getMessage());
        if (nextHandler != null) {
            nextHandler.handle(request);
        }
    }
}
```
```java
public class DataValidationHandler implements RequestHandler {
    private RequestHandler nextHandler;

    public DataValidationHandler(RequestHandler nextHandler) {
        this.nextHandler = nextHandler;
    }

    @Override
    public void handle(Request request) {
        System.out.println("Validating request: " + request.getMessage());
        if (nextHandler != null) {
            nextHandler.handle(request);
        }
    }
}
```

### **4. Using the Chain of Responsibility**
```java
public class ChainOfResponsibilityDemo {
    public static void main(String[] args) {
        RequestHandler chain = new AuthenticationHandler(
                                    new LoggingHandler(
                                        new DataValidationHandler(null)
                                    ));

        Request request = new Request("User Login");
        chain.handle(request);
    }
}
```

**Output:**
```
Authentication check for request: User Login
Logging request: User Login
Validating request: User Login
```

✔ **Handlers Work Independently** – New handlers can be added without changing existing code.  
✔ **Encapsulates Processing Logic** – Business logic remains modular and separate.

---

## **Chain of Responsibility in Spring Boot**
Spring Boot **automatically uses this pattern** in **Spring Security, request filters, and middleware processing.**

---

### **1. Implementing Chain of Responsibility for API Requests**
#### **1.1 Define the Handler Interface**
```java
public interface ApiRequestHandler {
    void handle(ApiRequest request);
}
```

#### **1.2 Create an API Request Class**
```java
public class ApiRequest {
    private String requestType;

    public ApiRequest(String requestType) {
        this.requestType = requestType;
    }

    public String getRequestType() {
        return requestType;
    }
}
```

#### **1.3 Implement Handlers**
```java
import org.springframework.stereotype.Component;

@Component
public class AuthenticationApiHandler implements ApiRequestHandler {
    private final LoggingApiHandler nextHandler;

    public AuthenticationApiHandler(LoggingApiHandler nextHandler) {
        this.nextHandler = nextHandler;
    }

    @Override
    public void handle(ApiRequest request) {
        System.out.println("Authenticating API request: " + request.getRequestType());
        nextHandler.handle(request);
    }
}
```
```java
import org.springframework.stereotype.Component;

@Component
public class LoggingApiHandler implements ApiRequestHandler {
    private final ValidationApiHandler nextHandler;

    public LoggingApiHandler(ValidationApiHandler nextHandler) {
        this.nextHandler = nextHandler;
    }

    @Override
    public void handle(ApiRequest request) {
        System.out.println("Logging API request: " + request.getRequestType());
        nextHandler.handle(request);
    }
}
```
```java
import org.springframework.stereotype.Component;

@Component
public class ValidationApiHandler implements ApiRequestHandler {
    @Override
    public void handle(ApiRequest request) {
        System.out.println("Validating API request: " + request.getRequestType());
    }
}
```

#### **1.4 Expose a REST API**
```java
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ApiController {
    private final AuthenticationApiHandler apiHandler;

    public ApiController(AuthenticationApiHandler apiHandler) {
        this.apiHandler = apiHandler;
    }

    @GetMapping("/{requestType}")
    public String handleApiRequest(@PathVariable String requestType) {
        ApiRequest request = new ApiRequest(requestType);
        apiHandler.handle(request);
        return "Request processed.";
    }
}
```

### **2. Testing the API**
#### **Request:**
```bash
curl http://localhost:8080/api/login
```
#### **Response:**
```
Authenticating API request: login
Logging API request: login
Validating API request: login
```

✔ **Spring Boot Uses Dependency Injection to Manage Handlers**  
✔ **Easily Extendable** – Adding a new handler requires no change in existing handlers.  
✔ **Modular Processing** – Authentication, logging, and validation remain separate.

---

## **Real-World Use Cases for Chain of Responsibility Pattern**
| Use Case | Example |
|----------|---------|
| **Spring Security Filter Chain** | Authentication & authorization checks |
| **Logging Frameworks** | Logs passing through different handlers (console, file, database) |
| **Middleware Processing** | Custom API request interceptors |
| **Validation Pipelines** | Multi-step request validation |
| **Transaction Processing** | Multi-layered approval systems |

---

## **Advantages of Using the Chain of Responsibility Pattern in Spring Boot**
| Feature | Benefit |
|---------|---------|
| **Loose Coupling** | Handlers work independently |
| **Extensibility** | New handlers can be added without modifying existing ones |
| **Encapsulation** | Business logic is separate from processing logic |
| **Dynamic Request Handling** | Requests can be handled differently based on runtime conditions |
| **Spring Boot Integration** | Works seamlessly with filters, interceptors, and event-driven systems |

---

## **When NOT to Use the Chain of Responsibility Pattern**
❌ **If Only One Handler is Needed** – Unnecessary complexity.  
❌ **If Order of Execution is Not Important** – Direct method calls may be better.  
❌ **Performance Concerns** – Passing requests through multiple handlers adds overhead.

---

## **Chain of Responsibility vs Decorator Pattern**
| Feature | Chain of Responsibility | Decorator Pattern |
|---------|-----------------|----------------|
| **Purpose** | Processes requests sequentially | Adds behavior to an object |
| **Flow** | Each handler may process or forward the request | Each decorator wraps an object |
| **Example** | API filter chains, logging frameworks | Adding encryption to file storage |

---

## **Conclusion**
The **Chain of Responsibility Pattern** is widely used in **Spring Boot’s request processing, security filters, and middleware pipelines**. It enables **modular, flexible, and scalable request handling** while **reducing code complexity**.

### **Next Steps**
- Implement **error-handling middleware** using this pattern.
- Extend the API to support **multiple request types dynamically**.
- Experiment with **Spring Security filter chains** for authentication.

---


# **Java & Spring Boot Fact: Template Method Pattern in Spring Boot**

## **What is the Template Method Pattern?**
The **Template Method Pattern** is a **behavioral design pattern** that defines the **skeleton** of an algorithm in a **base class** and allows **subclasses** to override specific steps without changing the overall structure.

In **Spring Boot**, this pattern is commonly used in **batch processing, authentication flows, database interactions, and caching mechanisms**.

---

## **How Does the Template Method Pattern Work?**
1. **Abstract Class (Template)** – Defines the algorithm structure with some steps **left for subclasses to implement**.
2. **Concrete Subclasses** – Provide the specific implementation of the undefined steps.
3. **Final Method Execution** – The base class **controls the execution order** to ensure a consistent flow.

---

## **Why Use the Template Method Pattern?**
✅ **Code Reusability** – Reduces code duplication by defining a **common algorithm** in one place.  
✅ **Encapsulation** – Keeps algorithm structure hidden from subclasses.  
✅ **Enforces Consistency** – Ensures a **standardized** workflow across different implementations.  
✅ **Open-Closed Principle** – Allows adding new behaviors without modifying the base class.  
✅ **Simplifies Complex Algorithms** – Breaks down logic into **manageable steps**.

---

## **Where is the Template Method Pattern Used?**
- **Spring JDBC Template** – Provides a template for database operations.
- **Spring Security Authentication Providers** – Handles different authentication mechanisms.
- **Spring Boot Caching Abstraction** – Unifies different caching strategies.
- **Batch Processing Workflows** – Standardizes job execution flows.
- **Algorithm Frameworks** – Sorting, filtering, and report generation templates.

---

## **Implementing Template Method Pattern in Java**

### **1. Define the Abstract Class (Template)**
```java
public abstract class PaymentProcessor {
    
    // Template method defining the steps of payment processing
    public final void processPayment(double amount) {
        validatePayment();
        debitAmount(amount);
        sendNotification();
    }

    // Steps to be implemented by subclasses
    protected abstract void validatePayment();
    protected abstract void debitAmount(double amount);

    // Common method for all payments
    private void sendNotification() {
        System.out.println("Sending payment notification...");
    }
}
```

### **2. Create Concrete Implementations**
```java
public class CreditCardPayment extends PaymentProcessor {
    @Override
    protected void validatePayment() {
        System.out.println("Validating credit card details...");
    }

    @Override
    protected void debitAmount(double amount) {
        System.out.println("Debiting $" + amount + " from credit card.");
    }
}
```
```java
public class PayPalPayment extends PaymentProcessor {
    @Override
    protected void validatePayment() {
        System.out.println("Validating PayPal account...");
    }

    @Override
    protected void debitAmount(double amount) {
        System.out.println("Debiting $" + amount + " from PayPal account.");
    }
}
```

### **3. Using the Template Method Pattern**
```java
public class TemplateMethodDemo {
    public static void main(String[] args) {
        PaymentProcessor creditCardPayment = new CreditCardPayment();
        creditCardPayment.processPayment(100);

        System.out.println();

        PaymentProcessor paypalPayment = new PayPalPayment();
        paypalPayment.processPayment(200);
    }
}
```

### **Output:**
```
Validating credit card details...
Debiting $100 from credit card.
Sending payment notification...

Validating PayPal account...
Debiting $200 from PayPal account.
Sending payment notification...
```

✔ **Defines a Standard Payment Flow** – Common logic is in `PaymentProcessor`.  
✔ **Flexible & Extendable** – New payment methods can be added without modifying existing code.

---

## **Template Method Pattern in Spring Boot**
Spring Boot **heavily relies on this pattern** in **database operations, security, caching, and job processing**.

---

### **1. Template Method in Spring Boot with `JdbcTemplate`**
Spring Boot provides `JdbcTemplate` to **standardize database interactions** using the **Template Method Pattern**.

#### **1.1 Define an Abstract DAO Class (Template)**
```java
import org.springframework.jdbc.core.JdbcTemplate;
import java.util.List;

public abstract class AbstractUserDao {
    protected final JdbcTemplate jdbcTemplate;

    public AbstractUserDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public final List<String> getUsers() {
        String sql = getQuery();
        return jdbcTemplate.queryForList(sql, String.class);
    }

    // Subclasses define the query
    protected abstract String getQuery();
}
```

#### **1.2 Implement Specific DAO Subclasses**
```java
import org.springframework.stereotype.Repository;

@Repository
public class ActiveUserDao extends AbstractUserDao {

    public ActiveUserDao(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    @Override
    protected String getQuery() {
        return "SELECT name FROM users WHERE status = 'ACTIVE'";
    }
}
```
```java
@Repository
public class InactiveUserDao extends AbstractUserDao {

    public InactiveUserDao(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    @Override
    protected String getQuery() {
        return "SELECT name FROM users WHERE status = 'INACTIVE'";
    }
}
```

#### **1.3 Expose the API**
```java
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    private final ActiveUserDao activeUserDao;
    private final InactiveUserDao inactiveUserDao;

    public UserController(ActiveUserDao activeUserDao, InactiveUserDao inactiveUserDao) {
        this.activeUserDao = activeUserDao;
        this.inactiveUserDao = inactiveUserDao;
    }

    @GetMapping("/active")
    public List<String> getActiveUsers() {
        return activeUserDao.getUsers();
    }

    @GetMapping("/inactive")
    public List<String> getInactiveUsers() {
        return inactiveUserDao.getUsers();
    }
}
```

✔ **Reduces Code Duplication** – Common logic is in `AbstractUserDao`.  
✔ **Easier to Extend** – New user categories (e.g., `PremiumUserDao`) can be added without modifying existing code.

---

## **Real-World Use Cases for Template Method Pattern**
| Use Case | Example |
|----------|---------|
| **Spring JDBC Template** | Standardizes database queries |
| **Spring Security Authentication** | Different authentication providers (LDAP, OAuth, JWT) |
| **Caching Mechanisms** | Abstracts different cache implementations (Redis, EhCache) |
| **Job Processing Frameworks** | Defines standard job execution workflows |
| **Report Generation Systems** | Enforces structure for reports with different data sources |

---

## **Advantages of Using the Template Method Pattern in Spring Boot**
| Feature | Benefit |
|---------|---------|
| **Encapsulation of Workflow** | Ensures a structured process without exposing implementation details |
| **Code Reusability** | Common logic is centralized, reducing redundancy |
| **Extensibility** | New behaviors can be added without modifying the base class |
| **Consistency** | Enforces uniform behavior across different implementations |
| **Spring Boot Integration** | Works well with `JdbcTemplate`, security, and batch processing |

---

## **When NOT to Use the Template Method Pattern**
❌ **If Steps Do Not Follow a Strict Sequence** – The Strategy Pattern may be better.  
❌ **If Too Many Subclasses Exist** – May lead to a **large inheritance hierarchy**.  
❌ **If Runtime Flexibility is Needed** – The Factory Pattern or Dependency Injection is more suitable.

---

## **Template Method Pattern vs Strategy Pattern**
| Feature | Template Method Pattern | Strategy Pattern |
|---------|------------------------|-----------------|
| **Purpose** | Defines a **fixed algorithm structure** with customizable steps | Allows selection of **different algorithms** dynamically |
| **Hierarchy** | Uses **inheritance** (abstract classes) | Uses **composition** (interfaces) |
| **Example** | `JdbcTemplate` enforcing database query flow | Choosing between **JWT and OAuth authentication** |

---

## **Conclusion**
The **Template Method Pattern** is a **powerful tool** in **Spring Boot**, helping to enforce **standardized workflows** in **database queries, authentication, caching, and job processing**. By centralizing common logic, it ensures **maintainable, consistent, and reusable** code.

### **Next Steps**
- Implement **batch job processing using this pattern**.
- Extend the **payment processor to support new payment methods dynamically**.
- Explore **Spring Security authentication providers using this approach**.

---


# **Java & Spring Boot Fact: Observer Pattern in Event-Driven Systems**

## **What is the Observer Pattern?**
The **Observer Pattern** is a **behavioral design pattern** that allows one object (**Subject**) to notify multiple other objects (**Observers**) about state changes automatically. It follows a **one-to-many dependency model**, meaning when the subject changes, all subscribed observers are notified.

In **Spring Boot**, this pattern is heavily used in **event-driven programming, messaging systems, and real-time updates**.

---

## **How Does the Observer Pattern Work?**
1. **Subject (Publisher)** – Maintains a list of observers and notifies them when an event occurs.
2. **Observers (Subscribers)** – Register with the subject and react to events.
3. **Loose Coupling** – The subject doesn’t know about observers' implementations, making the system flexible.

---

## **Why Use the Observer Pattern?**
✅ **Decouples Code** – The publisher does not depend on the observer’s implementation.  
✅ **Enhances Scalability** – New observers can be added without modifying the subject.  
✅ **Real-Time Notifications** – Useful for event-driven systems.  
✅ **Asynchronous Processing** – Observers can process events without blocking the main thread.  
✅ **Improves Maintainability** – Separates concerns, making it easier to test and debug.

---

## **Where is the Observer Pattern Used?**
- **Spring Boot Event Listeners (`@EventListener`)** – Observing application events.
- **Spring Messaging (RabbitMQ, Kafka, WebSockets)** – Handling distributed events.
- **Logging & Auditing** – Notifying multiple listeners when actions occur.
- **User Notification Systems** – Sending email/SMS notifications on user actions.
- **Stock Market Applications** – Real-time stock price updates.

---

## **Implementing Observer Pattern in Java**
### **1. Define the Observer Interface**
```java
public interface Observer {
    void update(String message);
}
```

### **2. Create Concrete Observers**
```java
public class EmailSubscriber implements Observer {
    private String name;

    public EmailSubscriber(String name) {
        this.name = name;
    }

    @Override
    public void update(String message) {
        System.out.println(name + " received an email: " + message);
    }
}
```
```java
public class SMSSubscriber implements Observer {
    private String phoneNumber;

    public SMSSubscriber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Override
    public void update(String message) {
        System.out.println("SMS to " + phoneNumber + ": " + message);
    }
}
```

### **3. Create the Subject (Publisher)**
```java
import java.util.ArrayList;
import java.util.List;

public class NotificationService {
    private List<Observer> observers = new ArrayList<>();

    public void addObserver(Observer observer) {
        observers.add(observer);
    }

    public void removeObserver(Observer observer) {
        observers.remove(observer);
    }

    public void notifyObservers(String message) {
        for (Observer observer : observers) {
            observer.update(message);
        }
    }
}
```

### **4. Using the Observer Pattern**
```java
public class ObserverPatternDemo {
    public static void main(String[] args) {
        NotificationService notificationService = new NotificationService();

        Observer emailUser = new EmailSubscriber("John Doe");
        Observer smsUser = new SMSSubscriber("+123456789");

        notificationService.addObserver(emailUser);
        notificationService.addObserver(smsUser);

        notificationService.notifyObservers("New Product Launched!");
    }
}
```

**Output:**
```
John Doe received an email: New Product Launched!
SMS to +123456789: New Product Launched!
```

✔ **Observers Work Independently** – New notification methods can be added without modifying `NotificationService`.  
✔ **Loose Coupling** – The subject doesn't need to know how notifications are handled.

---

## **Observer Pattern in Spring Boot with `@EventListener`**
Spring Boot provides a **built-in mechanism** for handling the Observer Pattern using **Application Events**.

### **1. Define a Custom Event**
```java
import org.springframework.context.ApplicationEvent;

public class OrderPlacedEvent extends ApplicationEvent {
    private String orderId;

    public OrderPlacedEvent(Object source, String orderId) {
        super(source);
        this.orderId = orderId;
    }

    public String getOrderId() {
        return orderId;
    }
}
```

### **2. Create an Event Publisher**
```java
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
public class OrderService {
    private final ApplicationEventPublisher eventPublisher;

    public OrderService(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    public void placeOrder(String orderId) {
        System.out.println("Order placed: " + orderId);
        eventPublisher.publishEvent(new OrderPlacedEvent(this, orderId));
    }
}
```

### **3. Create Event Listeners (Observers)**
```java
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class EmailNotificationListener {
    @EventListener
    public void handleOrderPlacedEvent(OrderPlacedEvent event) {
        System.out.println("Sending email for order: " + event.getOrderId());
    }
}
```
```java
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class SMSNotificationListener {
    @EventListener
    public void handleOrderPlacedEvent(OrderPlacedEvent event) {
        System.out.println("Sending SMS for order: " + event.getOrderId());
    }
}
```

### **4. Simulating the Order Processing**
```java
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class AppRunner implements CommandLineRunner {
    private final OrderService orderService;

    public AppRunner(OrderService orderService) {
        this.orderService = orderService;
    }

    @Override
    public void run(String... args) throws Exception {
        orderService.placeOrder("ORD123");
    }
}
```

### **Output:**
```
Order placed: ORD123
Sending email for order: ORD123
Sending SMS for order: ORD123
```

✔ **Automatic Event Handling** – Listeners react to events without modifying `OrderService`.  
✔ **Spring Boot Manages Event Publishing and Subscribing**.  
✔ **Easily Extendable** – New observers (e.g., WebSocket updates) can be added without touching existing code.

---

## **Real-World Use Cases for Observer Pattern**
| Use Case | Example |
|----------|---------|
| **Spring Event Listeners** | Handling events across microservices |
| **Notification Systems** | Sending email, SMS, push notifications |
| **Stock Market Applications** | Real-time stock updates |
| **Chat Applications** | New messages triggering UI updates |
| **Logging & Auditing** | Recording application events dynamically |

---

## **Advantages of Using the Observer Pattern in Spring Boot**
| Feature | Benefit |
|---------|---------|
| **Loose Coupling** | Publishers don’t depend on observers |
| **Scalability** | New observers can be added dynamically |
| **Event-Driven** | Improves responsiveness and asynchronous handling |
| **Better Maintainability** | Separation of concerns makes the code cleaner |
| **Built-in Spring Support** | Simplifies implementation with `@EventListener` |

---

## **When NOT to Use the Observer Pattern**
❌ **If Only One Component Needs to Listen** – Direct method calls may be better.  
❌ **If Performance is a Concern** – Too many observers can slow down the system.  
❌ **Complex Event Chains** – If events trigger other events, debugging becomes difficult.

---

## **Observer Pattern vs Publisher-Subscriber Pattern**
| Feature | Observer Pattern | Pub-Sub Pattern |
|---------|----------------|----------------|
| **Communication Type** | Direct notification | Message broker (e.g., Kafka, RabbitMQ) |
| **Observers Know the Subject?** | Yes, they register directly | No, they listen to topics |
| **Use Case** | UI updates, application events | Distributed systems, logging |

---

## **Conclusion**
The **Observer Pattern** is **a core design pattern** in **Spring Boot’s event-driven architecture**. It enables **real-time updates, modular notification systems, and seamless event handling**.

### **Next Steps**
- Implement **WebSocket event broadcasting** using this pattern.
- Use **RabbitMQ or Kafka** for distributed event-driven messaging.
- Explore **Spring Security event listeners** to handle login attempts.

---

but don't repeat you already explained
# **Java & Spring Boot Fact: Facade Pattern in Spring Boot**

## **What is the Facade Pattern?**
The **Facade Pattern** is a **structural design pattern** that provides a **simplified, unified interface** to a complex system. Instead of exposing multiple dependencies and classes to the client, a **single facade class** hides the complexity and provides a clean, easy-to-use API.

In **Spring Boot**, the Facade Pattern is commonly used to **simplify service calls, integrate multiple subsystems, and reduce dependencies in controllers**.

---

## **How Does the Facade Pattern Work?**
1. **Subsystem Classes** – Contain complex logic, interacting with databases, APIs, and other services.
2. **Facade Class** – Provides a simplified API by internally managing multiple subsystem classes.
3. **Client (Controller or Service)** – Calls only the facade, avoiding direct interaction with subsystems.

---

## **Why Use the Facade Pattern?**
✅ **Reduces Complexity** – Hides intricate details of multiple services and APIs.  
✅ **Decouples Controllers from Business Logic** – Controllers interact with a single facade instead of multiple services.  
✅ **Enhances Maintainability** – Centralizes complex interactions in one class.  
✅ **Improves Testability** – Easier to test a single facade than multiple services individually.  
✅ **Better Performance** – Can combine multiple service calls into a single optimized request.

---

## **Where is the Facade Pattern Used?**
- **Microservices Communication** – A single facade can aggregate data from multiple microservices.
- **E-commerce Checkout Process** – Handles order processing, payments, and notifications via one interface.
- **Security & Authentication** – Simplifies token validation, user roles, and permission checking.
- **Service Aggregation** – Combines results from different APIs into a single response.
- **Logging & Monitoring** – Provides a unified entry point for tracking system behavior.

---

## **Implementing the Facade Pattern in Java**

### **1. Create Subsystem Classes**
```java
public class PaymentService {
    public void processPayment(String orderId) {
        System.out.println("Processing payment for order: " + orderId);
    }
}
```
```java
public class InventoryService {
    public void updateStock(String orderId) {
        System.out.println("Updating stock for order: " + orderId);
    }
}
```
```java
public class NotificationService {
    public void sendOrderConfirmation(String orderId) {
        System.out.println("Sending confirmation email for order: " + orderId);
    }
}
```

### **2. Create the Facade Class**
```java
public class OrderFacade {
    private final PaymentService paymentService;
    private final InventoryService inventoryService;
    private final NotificationService notificationService;

    public OrderFacade() {
        this.paymentService = new PaymentService();
        this.inventoryService = new InventoryService();
        this.notificationService = new NotificationService();
    }

    public void placeOrder(String orderId) {
        paymentService.processPayment(orderId);
        inventoryService.updateStock(orderId);
        notificationService.sendOrderConfirmation(orderId);
    }
}
```

### **3. Using the Facade**
```java
public class FacadePatternDemo {
    public static void main(String[] args) {
        OrderFacade orderFacade = new OrderFacade();
        orderFacade.placeOrder("ORD123");
    }
}
```

### **Output:**
```
Processing payment for order: ORD123
Updating stock for order: ORD123
Sending confirmation email for order: ORD123
```

✔ **Encapsulates Complex Interactions** – `OrderFacade` handles all internal logic.  
✔ **Single Entry Point** – Clients only interact with `OrderFacade`.

---

## **Facade Pattern in Spring Boot**
Spring Boot uses this pattern **to aggregate multiple service calls and simplify controller logic**.

---

### **1. Define the Services (Subsystems)**
```java
import org.springframework.stereotype.Service;

@Service
public class PaymentService {
    public void processPayment(String orderId) {
        System.out.println("Processing payment for order: " + orderId);
    }
}
```
```java
import org.springframework.stereotype.Service;

@Service
public class InventoryService {
    public void updateStock(String orderId) {
        System.out.println("Updating stock for order: " + orderId);
    }
}
```
```java
import org.springframework.stereotype.Service;

@Service
public class NotificationService {
    public void sendOrderConfirmation(String orderId) {
        System.out.println("Sending confirmation email for order: " + orderId);
    }
}
```

---

### **2. Implement the Facade**
```java
import org.springframework.stereotype.Service;

@Service
public class OrderFacade {
    private final PaymentService paymentService;
    private final InventoryService inventoryService;
    private final NotificationService notificationService;

    public OrderFacade(PaymentService paymentService, InventoryService inventoryService, NotificationService notificationService) {
        this.paymentService = paymentService;
        this.inventoryService = inventoryService;
        this.notificationService = notificationService;
    }

    public void placeOrder(String orderId) {
        paymentService.processPayment(orderId);
        inventoryService.updateStock(orderId);
        notificationService.sendOrderConfirmation(orderId);
    }
}
```

---

### **3. Use the Facade in a Controller**
```java
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
public class OrderController {
    private final OrderFacade orderFacade;

    public OrderController(OrderFacade orderFacade) {
        this.orderFacade = orderFacade;
    }

    @PostMapping("/{orderId}")
    public String placeOrder(@PathVariable String orderId) {
        orderFacade.placeOrder(orderId);
        return "Order placed successfully!";
    }
}
```

---

### **4. Testing the API**
#### **Request:**
```bash
curl -X POST http://localhost:8080/orders/ORD123
```
#### **Response:**
```
Processing payment for order: ORD123
Updating stock for order: ORD123
Sending confirmation email for order: ORD123
```

✔ **Controllers are Simplified** – No direct dependencies on multiple services.  
✔ **Loose Coupling** – Services can change without affecting the controller.  
✔ **Scalability** – New services (e.g., discount service) can be added without modifying existing code.

---

## **Real-World Use Cases for Facade Pattern**
| Use Case | Example |
|----------|---------|
| **Microservices Aggregation** | A single facade fetches data from multiple services |
| **E-commerce Checkout** | Manages payments, inventory, and order confirmations |
| **Authentication** | Facade handles OAuth, JWT, and database authentication |
| **Logging & Monitoring** | Provides a unified interface for tracking requests |
| **Payment Processing** | Combines multiple payment methods under one interface |

---

## **Advantages of Using the Facade Pattern in Spring Boot**
| Feature | Benefit |
|---------|---------|
| **Reduces Complexity** | Controllers interact with a single facade instead of multiple services |
| **Encapsulation** | Hides complex workflows behind a simple API |
| **Improves Maintainability** | Changes in subsystems don’t affect the facade’s clients |
| **Better Testability** | Easier to mock a single facade than multiple service classes |
| **Boosts Performance** | Can combine multiple service calls into a single optimized request |

---

## **When NOT to Use the Facade Pattern**
❌ **If Services Are Simple** – Adding a facade might be unnecessary overhead.  
❌ **If Performance is Critical** – Facade introduces an extra layer of method calls.  
❌ **If Loose Coupling is Not Required** – When controllers can safely use services directly.

---

## **Facade Pattern vs Adapter Pattern**
| Feature | Facade Pattern | Adapter Pattern |
|---------|--------------|----------------|
| **Purpose** | Simplifies access to complex subsystems | Converts one interface into another |
| **Focus** | Hiding complexity | Ensuring compatibility |
| **Example** | Checkout process facade | Wrapping PayPal API into a standard payment interface |

---

## **Conclusion**
The **Facade Pattern** is an essential design pattern in **Spring Boot**, helping to **simplify complex business logic, improve maintainability, and reduce dependencies** in controllers. It’s widely used in **microservices, e-commerce platforms, authentication systems, and API aggregators**.

### **Next Steps**
- Implement **a facade for third-party API integrations**.
- Extend the order facade to **handle discounts and shipping calculations**.
- Use **Spring Boot’s caching mechanisms** inside a facade for better performance.

---


# **Java & Spring Boot Fact: Bridge Pattern in Spring Boot**

## **What is the Bridge Pattern?**
The **Bridge Pattern** is a **structural design pattern** that **decouples abstraction from implementation**, allowing them to vary independently. It helps avoid **rigid inheritance hierarchies** and promotes **composition over inheritance**.

In **Spring Boot**, the Bridge Pattern is useful for **decoupling business logic from underlying implementations**, such as **database interactions, messaging systems, and external API integrations**.

---

## **How Does the Bridge Pattern Work?**
1. **Abstraction (Interface or Abstract Class)** – Represents the **high-level** part of the system.
2. **Implementation (Bridge Interface)** – Defines **multiple concrete implementations** that can be swapped at runtime.
3. **Decoupled Variants** – New implementations can be added without modifying the abstraction.

---

## **Why Use the Bridge Pattern?**
✅ **Reduces Code Duplication** – Avoids deep inheritance trees by using composition.  
✅ **Flexible & Scalable** – New implementations can be added without modifying existing code.  
✅ **Improves Maintainability** – Business logic and implementation details are **separated**.  
✅ **Enhances Testability** – Each abstraction and implementation can be tested independently.  
✅ **Runtime Implementation Switching** – Allows dynamic selection of different implementations.

---

## **Where is the Bridge Pattern Used?**
- **Spring Boot Data Repositories** – Abstracting database interactions (MySQL, PostgreSQL, MongoDB).
- **Messaging Systems** – Supporting multiple messaging platforms (Kafka, RabbitMQ, ActiveMQ).
- **Payment Processing** – Switching between different payment gateways (Stripe, PayPal, Razorpay).
- **Logging Abstractions** – Logging to console, file, or external monitoring services.
- **Notification Systems** – Supporting multiple notification channels (Email, SMS, Push).

---

## **Implementing Bridge Pattern in Java**

### **1. Define the Bridge Interface (Implementation Layer)**
```java
public interface MessageSender {
    void sendMessage(String message);
}
```

### **2. Create Concrete Implementations**
```java
public class EmailSender implements MessageSender {
    @Override
    public void sendMessage(String message) {
        System.out.println("Sending Email: " + message);
    }
}
```
```java
public class SMSSender implements MessageSender {
    @Override
    public void sendMessage(String message) {
        System.out.println("Sending SMS: " + message);
    }
}
```

### **3. Define the Abstraction Layer**
```java
public abstract class Notification {
    protected MessageSender messageSender;

    public Notification(MessageSender messageSender) {
        this.messageSender = messageSender;
    }

    public abstract void notifyUser(String message);
}
```

### **4. Create Refined Abstractions**
```java
public class UrgentNotification extends Notification {
    public UrgentNotification(MessageSender messageSender) {
        super(messageSender);
    }

    @Override
    public void notifyUser(String message) {
        System.out.println("[Urgent Notification]");
        messageSender.sendMessage(message);
    }
}
```
```java
public class RegularNotification extends Notification {
    public RegularNotification(MessageSender messageSender) {
        super(messageSender);
    }

    @Override
    public void notifyUser(String message) {
        System.out.println("[Regular Notification]");
        messageSender.sendMessage(message);
    }
}
```

### **5. Using the Bridge Pattern**
```java
public class BridgePatternDemo {
    public static void main(String[] args) {
        MessageSender emailSender = new EmailSender();
        MessageSender smsSender = new SMSSender();

        Notification urgentEmail = new UrgentNotification(emailSender);
        Notification regularSMS = new RegularNotification(smsSender);

        urgentEmail.notifyUser("Server is down!");
        regularSMS.notifyUser("Your order has been shipped.");
    }
}
```

### **Output:**
```
[Urgent Notification]
Sending Email: Server is down!

[Regular Notification]
Sending SMS: Your order has been shipped.
```

✔ **Separates Notification Logic from Message Sending Logic**.  
✔ **New Message Senders Can Be Added Without Changing Notification Classes**.

---

## **Bridge Pattern in Spring Boot**
Spring Boot uses the **Bridge Pattern** extensively in **service layers, repositories, and messaging integrations**.

---

### **1. Implementing Bridge Pattern for Payment Processing**
#### **1.1 Define Payment Processor Interface (Bridge)**
```java
public interface PaymentProcessor {
    void processPayment(double amount);
}
```

#### **1.2 Implement Different Payment Gateways**
```java
import org.springframework.stereotype.Component;

@Component("stripeProcessor")
public class StripePaymentProcessor implements PaymentProcessor {
    @Override
    public void processPayment(double amount) {
        System.out.println("Processing Stripe payment of $" + amount);
    }
}
```
```java
import org.springframework.stereotype.Component;

@Component("paypalProcessor")
public class PayPalPaymentProcessor implements PaymentProcessor {
    @Override
    public void processPayment(double amount) {
        System.out.println("Processing PayPal payment of $" + amount);
    }
}
```

#### **1.3 Define the Abstraction Layer**
```java
public abstract class Payment {
    protected PaymentProcessor paymentProcessor;

    public Payment(PaymentProcessor paymentProcessor) {
        this.paymentProcessor = paymentProcessor;
    }

    public abstract void pay(double amount);
}
```

#### **1.4 Create Payment Types**
```java
import org.springframework.stereotype.Component;

@Component
public class OnlinePayment extends Payment {
    public OnlinePayment(PaymentProcessor paymentProcessor) {
        super(paymentProcessor);
    }

    @Override
    public void pay(double amount) {
        System.out.println("[Online Payment]");
        paymentProcessor.processPayment(amount);
    }
}
```
```java
import org.springframework.stereotype.Component;

@Component
public class InStorePayment extends Payment {
    public InStorePayment(PaymentProcessor paymentProcessor) {
        super(paymentProcessor);
    }

    @Override
    public void pay(double amount) {
        System.out.println("[In-Store Payment]");
        paymentProcessor.processPayment(amount);
    }
}
```

#### **1.5 Inject Payment Types in Service**
```java
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {
    private final OnlinePayment onlinePayment;
    private final InStorePayment inStorePayment;

    @Autowired
    public PaymentService(@Autowired @Component("stripeProcessor") PaymentProcessor stripeProcessor,
                          @Autowired @Component("paypalProcessor") PaymentProcessor paypalProcessor) {
        this.onlinePayment = new OnlinePayment(stripeProcessor);
        this.inStorePayment = new InStorePayment(paypalProcessor);
    }

    public void processOnlinePayment(double amount) {
        onlinePayment.pay(amount);
    }

    public void processInStorePayment(double amount) {
        inStorePayment.pay(amount);
    }
}
```

#### **1.6 Expose an API**
```java
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payment")
public class PaymentController {
    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @GetMapping("/online/{amount}")
    public String onlinePayment(@PathVariable double amount) {
        paymentService.processOnlinePayment(amount);
        return "Online payment processed!";
    }

    @GetMapping("/instore/{amount}")
    public String inStorePayment(@PathVariable double amount) {
        paymentService.processInStorePayment(amount);
        return "In-store payment processed!";
    }
}
```

---

### **2. Testing the API**
#### **Request:**
```bash
curl http://localhost:8080/payment/online/150
```
#### **Response:**
```
[Online Payment]
Processing Stripe payment of $150
```

#### **Request:**
```bash
curl http://localhost:8080/payment/instore/200
```
#### **Response:**
```
[In-Store Payment]
Processing PayPal payment of $200
```

✔ **Separates Payment Logic from Payment Gateways**.  
✔ **New Gateways Can Be Added Without Changing Business Logic**.  
✔ **Encapsulates Variations, Keeping Code Clean and Maintainable**.

---

## **Advantages of Using the Bridge Pattern in Spring Boot**
| Feature | Benefit |
|---------|---------|
| **Reduces Inheritance Complexity** | Uses composition instead of deep inheritance trees |
| **Enhances Maintainability** | Separates business logic from specific implementations |
| **Supports Multiple Implementations** | Easily swap services (e.g., Stripe, PayPal) |
| **Enables Runtime Switching** | Can dynamically select implementations based on conditions |
| **Spring Boot Integration** | Works well with dependency injection and service-based architecture |

---

## **Conclusion**
The **Bridge Pattern** is an essential tool for **decoupling abstraction from implementation**, enabling **flexible, scalable, and maintainable systems** in **Spring Boot**.

### **Next Steps**
- Implement **logging with multiple backends (console, file, database)** using this pattern.
- Extend the **payment service to support new providers dynamically**.
- Use **Spring Boot’s conditional beans** to select bridge implementations at runtime.


# **Java & Spring Boot Fact: Flyweight Pattern in Spring Boot**

## **What is the Flyweight Pattern?**
The **Flyweight Pattern** is a **structural design pattern** that minimizes memory usage by **sharing common objects** instead of creating new instances each time. It is particularly useful in applications that handle **large numbers of similar objects** by storing reusable objects in a shared pool.

In **Spring Boot**, this pattern is commonly used in **caching, object pooling, and optimizing high-memory usage applications**.

---

## **How Does the Flyweight Pattern Work?**
1. **Intrinsic State (Shared Data)** – Immutable data that can be **reused** across multiple objects.
2. **Extrinsic State (Unique Data)** – Data that is passed dynamically and not stored in the flyweight object.
3. **Flyweight Factory** – Manages a pool of shared objects to prevent redundant object creation.

---

## **Why Use the Flyweight Pattern?**
✅ **Reduces Memory Usage** – Prevents duplicate objects by sharing instances.  
✅ **Improves Performance** – Faster object retrieval compared to creating new instances.  
✅ **Enhances Scalability** – Useful when managing **millions of similar objects**.  
✅ **Encourages Reusability** – Prevents unnecessary instantiation of identical objects.  
✅ **Works Well with Caching** – Commonly used in **Spring Boot caching frameworks**.

---

## **Where is the Flyweight Pattern Used?**
- **Spring Boot Caching Mechanisms** – To store reusable objects in memory.
- **Database Connection Pools** – Managing shared database connections efficiently.
- **Thread Pools** – Reusing worker threads instead of creating new ones.
- **Font Rendering in UI** – Caching text styles and colors to save memory.
- **Large-scale Games (e.g., Chess, Maps)** – Storing frequently used object properties.

---

## **Implementing Flyweight Pattern in Java**

### **1. Define the Flyweight Interface**
```java
public interface Vehicle {
    void assignColor(String color);
    void display();
}
```

### **2. Create the Flyweight Concrete Class**
```java
public class Car implements Vehicle {
    private final String brand; // Intrinsic (Shared)
    private final String model; // Intrinsic (Shared)
    private String color;        // Extrinsic (Unique)

    public Car(String brand, String model) {
        this.brand = brand;
        this.model = model;
    }

    @Override
    public void assignColor(String color) {
        this.color = color;
    }

    @Override
    public void display() {
        System.out.println("Car: " + brand + " " + model + " | Color: " + color);
    }
}
```

### **3. Implement the Flyweight Factory**
```java
import java.util.HashMap;
import java.util.Map;

public class CarFactory {
    private static final Map<String, Car> carPool = new HashMap<>();

    public static Car getCar(String brand, String model) {
        String key = brand + "-" + model;
        if (!carPool.containsKey(key)) {
            carPool.put(key, new Car(brand, model));
            System.out.println("Creating new Car object for: " + brand + " " + model);
        }
        return carPool.get(key);
    }
}
```

### **4. Using the Flyweight Pattern**
```java
public class FlyweightPatternDemo {
    public static void main(String[] args) {
        Car car1 = CarFactory.getCar("Tesla", "Model S");
        car1.assignColor("Red");
        car1.display();

        Car car2 = CarFactory.getCar("Tesla", "Model S");
        car2.assignColor("Blue");
        car2.display();

        Car car3 = CarFactory.getCar("BMW", "X5");
        car3.assignColor("Black");
        car3.display();

        Car car4 = CarFactory.getCar("Tesla", "Model S");
        car4.assignColor("White");
        car4.display();
    }
}
```

### **Output:**
```
Creating new Car object for: Tesla Model S
Car: Tesla Model S | Color: Red
Car: Tesla Model S | Color: Blue
Creating new Car object for: BMW X5
Car: BMW X5 | Color: Black
Car: Tesla Model S | Color: White
```

✔ **Reuses Objects Instead of Creating New Ones**.  
✔ **Saves Memory by Caching Shared Data**.  
✔ **Efficient for Applications Handling Large Object Collections**.

---

## **Flyweight Pattern in Spring Boot (Caching & Object Pooling)**

Spring Boot uses the **Flyweight Pattern** in **caching**, **database connection pools**, and **thread management**.

---

### **1. Using Flyweight Pattern in Spring Boot Caching**
Spring Boot provides **built-in caching** using **Redis, EhCache, or Caffeine**.

#### **1.1 Enable Caching in Spring Boot**
```java
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class CacheConfig {
}
```

#### **1.2 Implement a Cached Service**
```java
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class ProductService {
    
    @Cacheable("products")
    public String getProductDetails(String productId) {
        System.out.println("Fetching product details from database...");
        return "Product " + productId + " details";
    }
}
```

#### **1.3 Expose an API for Product Details**
```java
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/products")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/{productId}")
    public String getProduct(@PathVariable String productId) {
        return productService.getProductDetails(productId);
    }
}
```

---

### **2. Testing Caching in Spring Boot**
#### **Request:**
```bash
curl http://localhost:8080/products/101
```
#### **Response:**
```
Fetching product details from database...
Product 101 details
```

#### **Request Again:**
```bash
curl http://localhost:8080/products/101
```
#### **Response (Now Cached):**
```
Product 101 details
```

✔ **Data is Cached to Prevent Redundant Database Calls**.  
✔ **Only Fetches from Database on First Request**.

---

## **Real-World Use Cases for Flyweight Pattern**
| Use Case | Example |
|----------|---------|
| **Spring Boot Caching** | Stores reusable objects in memory (Redis, EhCache) |
| **Database Connection Pooling** | Manages shared connections efficiently |
| **Thread Pools** | Reuses worker threads instead of creating new ones |
| **Font & Icon Caching** | Stores reusable assets in UI applications |
| **Game Object Optimization** | Avoids duplicate character or object instances |

---

## **Advantages of Using the Flyweight Pattern in Spring Boot**
| Feature | Benefit |
|---------|---------|
| **Reduces Memory Footprint** | Prevents excessive object creation |
| **Boosts Performance** | Faster object retrieval via caching |
| **Improves Scalability** | Supports handling of large datasets efficiently |
| **Encourages Reusability** | Objects are shared instead of recreated |
| **Works Well with Caching** | Complements Spring Boot’s caching mechanisms |

---

## **When NOT to Use the Flyweight Pattern**
❌ **If Objects Have Unique State** – When most objects have different intrinsic data, the pattern is ineffective.  
❌ **If Object Creation is Not a Bottleneck** – If memory consumption is not an issue, caching may be unnecessary.  
❌ **If Objects Are Frequently Modified** – Shared instances should remain immutable; frequent updates may cause issues.

---

## **Flyweight Pattern vs Singleton Pattern**
| Feature | Flyweight Pattern | Singleton Pattern |
|---------|-----------------|----------------|
| **Purpose** | Reuses multiple shared objects | Ensures a single instance globally |
| **Usage** | Caching, object pooling | Global configuration, logging |
| **Example** | Database connections, font caching | Database connection manager |

---

## **Conclusion**
The **Flyweight Pattern** is a **powerful optimization technique** in **Spring Boot**, allowing **efficient caching, object reuse, and memory management**. It is particularly useful for applications that handle **large volumes of similar objects**.

### **Next Steps**
- Implement **Flyweight Pattern for database connection pooling**.
- Extend **Spring Boot’s caching system with Redis for distributed caching**.
- Optimize **heavyweight object instantiation in UI applications** using Flyweight.


# **Java & Spring Boot Fact: Mediator Pattern in Spring Boot**

## **What is the Mediator Pattern?**
The **Mediator Pattern** is a **behavioral design pattern** that **reduces direct dependencies** between components by introducing a **mediator object** to handle communication. Instead of **objects communicating directly**, they interact with the **mediator**, which **coordinates actions**.

In **Spring Boot**, this pattern is useful for **decoupling services, managing messaging systems (RabbitMQ, Kafka), and handling complex workflows**.

---

## **How Does the Mediator Pattern Work?**
1. **Mediator Interface** – Defines a central method for communication between components.
2. **Concrete Mediator** – Implements the mediator interface and orchestrates interactions.
3. **Colleagues (Components)** – Register with the mediator and send messages through it.

---

## **Why Use the Mediator Pattern?**
✅ **Reduces Coupling** – Components don’t directly depend on each other, improving modularity.  
✅ **Simplifies Communication** – Avoids complex **many-to-many** interactions.  
✅ **Improves Maintainability** – Changes in one component don’t affect others.  
✅ **Enhances Testability** – Easier to mock and test individual components.  
✅ **Works Well with Event-Driven Architectures** – Fits naturally with **Spring Events, Kafka, and RabbitMQ**.

---

## **Where is the Mediator Pattern Used?**
- **Spring Boot Event Handling (`@EventListener`)** – Centralized communication between services.
- **Message Brokers (Kafka, RabbitMQ, ActiveMQ)** – Decoupling microservices.
- **Workflow Management Systems** – Automating business processes.
- **Microservices Orchestration** – Coordinating service interactions.
- **Chat Applications** – Managing real-time message exchanges.

---

## **Implementing Mediator Pattern in Java**

### **1. Define the Mediator Interface**
```java
public interface ChatMediator {
    void sendMessage(String message, User user);
    void addUser(User user);
}
```

### **2. Create a Concrete Mediator**
```java
import java.util.ArrayList;
import java.util.List;

public class ChatRoom implements ChatMediator {
    private List<User> users = new ArrayList<>();

    @Override
    public void addUser(User user) {
        users.add(user);
    }

    @Override
    public void sendMessage(String message, User sender) {
        for (User user : users) {
            if (user != sender) {
                user.receiveMessage(message);
            }
        }
    }
}
```

### **3. Define Colleague Components (Users)**
```java
public abstract class User {
    protected ChatMediator mediator;
    protected String name;

    public User(ChatMediator mediator, String name) {
        this.mediator = mediator;
        this.name = name;
    }

    public abstract void sendMessage(String message);
    public abstract void receiveMessage(String message);
}
```
```java
public class ChatUser extends User {
    public ChatUser(ChatMediator mediator, String name) {
        super(mediator, name);
    }

    @Override
    public void sendMessage(String message) {
        System.out.println(name + " sends: " + message);
        mediator.sendMessage(message, this);
    }

    @Override
    public void receiveMessage(String message) {
        System.out.println(name + " received: " + message);
    }
}
```

### **4. Using the Mediator Pattern**
```java
public class MediatorPatternDemo {
    public static void main(String[] args) {
        ChatMediator chatRoom = new ChatRoom();

        User user1 = new ChatUser(chatRoom, "Alice");
        User user2 = new ChatUser(chatRoom, "Bob");
        User user3 = new ChatUser(chatRoom, "Charlie");

        chatRoom.addUser(user1);
        chatRoom.addUser(user2);
        chatRoom.addUser(user3);

        user1.sendMessage("Hello, everyone!");
    }
}
```

### **Output:**
```
Alice sends: Hello, everyone!
Bob received: Hello, everyone!
Charlie received: Hello, everyone!
```

✔ **Decouples User Classes from Direct Interaction**.  
✔ **Allows Adding More Users Without Changing Existing Code**.

---

## **Mediator Pattern in Spring Boot (Event-Driven Communication)**

Spring Boot supports **event-driven programming** using **Application Events (`@EventListener`)**, following the **Mediator Pattern**.

---

### **1. Define a Custom Event**
```java
import org.springframework.context.ApplicationEvent;

public class OrderCreatedEvent extends ApplicationEvent {
    private String orderId;

    public OrderCreatedEvent(Object source, String orderId) {
        super(source);
        this.orderId = orderId;
    }

    public String getOrderId() {
        return orderId;
    }
}
```

---

### **2. Implement the Mediator (Spring Event Publisher)**
```java
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
public class OrderService {
    private final ApplicationEventPublisher eventPublisher;

    public OrderService(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    public void createOrder(String orderId) {
        System.out.println("Order created: " + orderId);
        eventPublisher.publishEvent(new OrderCreatedEvent(this, orderId));
    }
}
```

---

### **3. Implement Colleagues (Observers)**
```java
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class EmailService {
    @EventListener
    public void onOrderCreated(OrderCreatedEvent event) {
        System.out.println("Sending order confirmation email for: " + event.getOrderId());
    }
}
```
```java
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class InventoryService {
    @EventListener
    public void onOrderCreated(OrderCreatedEvent event) {
        System.out.println("Updating stock for order: " + event.getOrderId());
    }
}
```

---

### **4. Expose an API to Trigger the Event**
```java
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/{orderId}")
    public String createOrder(@PathVariable String orderId) {
        orderService.createOrder(orderId);
        return "Order placed successfully!";
    }
}
```

---

### **5. Testing the API**
#### **Request:**
```bash
curl -X POST http://localhost:8080/orders/ORD123
```
#### **Response:**
```
Order created: ORD123
Sending order confirmation email for: ORD123
Updating stock for order: ORD123
```

✔ **Decouples Components Using Event Publishing**.  
✔ **Easily Extendable – New Listeners Can Be Added Without Modifying Existing Code**.

---

## **Real-World Use Cases for Mediator Pattern**
| Use Case | Example |
|----------|---------|
| **Spring Event Listeners** | Handles events asynchronously across services |
| **Message Brokers (Kafka, RabbitMQ)** | Decouples microservices communication |
| **Workflow Automation** | Orchestrates multi-step processes (e.g., order fulfillment) |
| **Microservices Coordination** | Mediator helps coordinate transactions across services |
| **Chat Systems** | Manages user message exchanges without direct coupling |

---

## **Advantages of Using the Mediator Pattern in Spring Boot**
| Feature | Benefit |
|---------|---------|
| **Reduces Direct Dependencies** | Services communicate through a mediator, reducing coupling |
| **Improves Scalability** | New components can be added without modifying existing ones |
| **Event-Driven** | Works well with asynchronous messaging systems |
| **Enhances Maintainability** | Easier to debug and extend business logic |
| **Spring Boot Integration** | Works seamlessly with `@EventListener`, Kafka, and RabbitMQ |

---

## **When NOT to Use the Mediator Pattern**
❌ **If Communication is Simple** – Direct method calls may be more efficient.  
❌ **If Performance is Critical** – Using event-driven architecture introduces **latency**.  
❌ **If Debugging Complexity is a Concern** – Indirect communication can make debugging harder.

---

## **Mediator Pattern vs Observer Pattern**
| Feature | Mediator Pattern | Observer Pattern |
|---------|-----------------|-----------------|
| **Purpose** | Manages multiple objects’ interactions | Notifies multiple observers of a state change |
| **Focus** | Centralized communication | Decentralized event handling |
| **Example** | Chat rooms, microservices orchestration | Event-driven email notifications |

---

## **Conclusion**
The **Mediator Pattern** is an essential design pattern in **Spring Boot’s event-driven architecture, microservices orchestration, and messaging systems**. It helps **decouple services, improve scalability, and centralize communication**.

### **Next Steps**
- Implement **Kafka-based event-driven communication** using the Mediator Pattern.
- Extend **Spring Events with custom logging and monitoring listeners**.
- Explore **RabbitMQ message queues** as an alternative to Spring’s built-in events.


# **Java & Spring Boot Fact: State Pattern in Spring Boot**

## **What is the State Pattern?**
The **State Pattern** is a **behavioral design pattern** that allows an object to **change its behavior dynamically** when its internal state changes. Instead of using large `if-else` or `switch` statements, the pattern encapsulates state-specific logic inside **state classes**.

In **Spring Boot**, the **State Pattern** is useful in **workflow automation, order processing, stateful services, and finite-state machines (FSMs).**

---

## **How Does the State Pattern Work?**
1. **State Interface** – Defines common behavior for all states.
2. **Concrete State Classes** – Implement different behaviors based on the current state.
3. **Context (Main Class)** – Holds a reference to the current state and delegates actions to it.
4. **State Transitions** – Changes the state dynamically based on conditions.

---

## **Why Use the State Pattern?**
✅ **Eliminates Complex `if-else` Logic** – Reduces clutter and improves readability.  
✅ **Encapsulates State-Specific Behavior** – Each state has its own logic, making it easy to manage.  
✅ **Enhances Maintainability** – Adding new states doesn't require modifying existing code.  
✅ **Enables Dynamic Behavior Changes** – The object can change behavior at runtime.  
✅ **Supports Workflow Automation** – Ideal for processes like **order fulfillment, authentication, and stateful services**.

---

## **Where is the State Pattern Used?**
- **Order Processing Systems** – Orders transition from `New` → `Processing` → `Shipped` → `Delivered`.
- **Workflow Management** – Approval processes with `Pending`, `Approved`, and `Rejected` states.
- **Authentication Systems** – User states like `LoggedOut`, `LoggedIn`, `Suspended`.
- **ATM Machines** – States like `Idle`, `Processing`, `Dispensing Cash`.
- **Game Development** – Character states like `Walking`, `Jumping`, `Attacking`.

---

## **Implementing State Pattern in Java**

### **1. Define the State Interface**
```java
public interface OrderState {
    void processOrder(OrderContext context);
}
```

### **2. Create Concrete State Classes**
```java
public class NewOrderState implements OrderState {
    @Override
    public void processOrder(OrderContext context) {
        System.out.println("Order is NEW. Moving to PROCESSING.");
        context.setState(new ProcessingOrderState());
    }
}
```
```java
public class ProcessingOrderState implements OrderState {
    @Override
    public void processOrder(OrderContext context) {
        System.out.println("Order is PROCESSING. Moving to SHIPPED.");
        context.setState(new ShippedOrderState());
    }
}
```
```java
public class ShippedOrderState implements OrderState {
    @Override
    public void processOrder(OrderContext context) {
        System.out.println("Order is SHIPPED. Moving to DELIVERED.");
        context.setState(new DeliveredOrderState());
    }
}
```
```java
public class DeliveredOrderState implements OrderState {
    @Override
    public void processOrder(OrderContext context) {
        System.out.println("Order is DELIVERED. No further processing.");
    }
}
```

### **3. Implement the Context Class**
```java
public class OrderContext {
    private OrderState currentState;

    public OrderContext() {
        this.currentState = new NewOrderState(); // Initial state
    }

    public void setState(OrderState state) {
        this.currentState = state;
    }

    public void processOrder() {
        currentState.processOrder(this);
    }
}
```

### **4. Using the State Pattern**
```java
public class StatePatternDemo {
    public static void main(String[] args) {
        OrderContext order = new OrderContext();
        order.processOrder();
        order.processOrder();
        order.processOrder();
        order.processOrder();
    }
}
```

### **Output:**
```
Order is NEW. Moving to PROCESSING.
Order is PROCESSING. Moving to SHIPPED.
Order is SHIPPED. Moving to DELIVERED.
Order is DELIVERED. No further processing.
```

✔ **Encapsulates Each Order State as a Separate Class**.  
✔ **Dynamically Transitions Between States** Without Modifying Order Logic.

---

## **State Pattern in Spring Boot (Order Management System)**

Spring Boot applications often require **stateful workflows** like **order processing, user authentication, and approvals**.

---

### **1. Define the State Interface**
```java
public interface OrderState {
    void next(Order order);
}
```

---

### **2. Create Concrete State Implementations**
```java
import org.springframework.stereotype.Component;

@Component
public class NewOrder implements OrderState {
    @Override
    public void next(Order order) {
        System.out.println("Order is NEW. Moving to PROCESSING.");
        order.setState(new ProcessingOrder());
    }
}
```
```java
import org.springframework.stereotype.Component;

@Component
public class ProcessingOrder implements OrderState {
    @Override
    public void next(Order order) {
        System.out.println("Order is PROCESSING. Moving to SHIPPED.");
        order.setState(new ShippedOrder());
    }
}
```
```java
import org.springframework.stereotype.Component;

@Component
public class ShippedOrder implements OrderState {
    @Override
    public void next(Order order) {
        System.out.println("Order is SHIPPED. Moving to DELIVERED.");
        order.setState(new DeliveredOrder());
    }
}
```
```java
import org.springframework.stereotype.Component;

@Component
public class DeliveredOrder implements OrderState {
    @Override
    public void next(Order order) {
        System.out.println("Order is DELIVERED. No further changes.");
    }
}
```

---

### **3. Implement the Order Entity (Context)**
```java
import org.springframework.stereotype.Component;

@Component
public class Order {
    private OrderState state;

    public Order() {
        this.state = new NewOrder(); // Initial state
    }

    public void setState(OrderState state) {
        this.state = state;
    }

    public void processNextState() {
        state.next(this);
    }
}
```

---

### **4. Create a REST Controller to Process Orders**
```java
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
public class OrderController {
    private final Order order;

    public OrderController(Order order) {
        this.order = order;
    }

    @PostMapping("/next")
    public String nextOrderState() {
        order.processNextState();
        return "Order state updated!";
    }
}
```

---

### **5. Testing the API**
#### **Request:**
```bash
curl -X POST http://localhost:8080/orders/next
```
#### **Response:**
```
Order is NEW. Moving to PROCESSING.
```

#### **Request Again:**
```bash
curl -X POST http://localhost:8080/orders/next
```
#### **Response:**
```
Order is PROCESSING. Moving to SHIPPED.
```

✔ **Spring Boot Manages Stateful Transitions Dynamically**.  
✔ **Controllers Remain Clean Without `if-else` Logic**.

---

## **Real-World Use Cases for State Pattern**
| Use Case | Example |
|----------|---------|
| **Order Processing Systems** | Order states: `New` → `Processing` → `Shipped` → `Delivered` |
| **Workflow Management** | Document states: `Draft` → `Under Review` → `Approved` |
| **Authentication Systems** | User states: `LoggedOut` → `LoggedIn` → `Suspended` |
| **Traffic Light System** | States: `Red` → `Green` → `Yellow` |
| **Game Development** | Character states: `Walking`, `Jumping`, `Attacking` |

---

## **Advantages of Using the State Pattern in Spring Boot**
| Feature | Benefit |
|---------|---------|
| **Eliminates `if-else` Chains** | Each state encapsulates its behavior independently |
| **Encapsulation** | State-specific logic is **self-contained** |
| **Easy to Extend** | New states can be added **without modifying existing states** |
| **Enhances Maintainability** | Reduces **code complexity and dependencies** |
| **Spring Boot Integration** | Works **seamlessly** with DI and REST APIs |

---

## **When NOT to Use the State Pattern**
❌ **If the Number of States is Small** – `if-else` may be sufficient.  
❌ **If Transitions are Simple** – No need for complex state management.  
❌ **If Performance is a Concern** – Switching state objects may introduce slight overhead.

---

## **Conclusion**
The **State Pattern** is widely used in **Spring Boot for handling workflows, authentication, and process automation**. It **eliminates complex logic, improves maintainability, and allows dynamic behavior changes**.

### **Next Steps**
- Implement **Spring Security authentication state management**.
- Extend the order processing system to **support order cancellations**.
- Use **Spring Boot state machines (Spring StateMachine)** for **complex workflows**.


# **Java & Spring Boot Fact: Composite Pattern in Spring Boot**

## **What is the Composite Pattern?**
The **Composite Pattern** is a **structural design pattern** that lets you **compose objects into tree structures** to represent **part-whole hierarchies**. This pattern allows **clients to treat individual objects and groups of objects uniformly**.

In **Spring Boot**, the **Composite Pattern** is useful for handling **hierarchical structures like menus, organization trees, product catalogs, and file systems**.

---

## **How Does the Composite Pattern Work?**
1. **Component Interface** – Defines common behavior for individual and composite objects.
2. **Leaf (Individual Objects)** – Represents **single elements** (e.g., a file, employee).
3. **Composite (Group Objects)** – Contains **multiple leaf objects** or other composite objects.
4. **Client Code** – Works with all objects **uniformly** without worrying about whether it's a leaf or a composite.

---

## **Why Use the Composite Pattern?**
✅ **Simplifies Hierarchical Structures** – Treats individual objects and groups the same way.  
✅ **Encapsulates Complex Trees** – Makes recursive structures easier to manage.  
✅ **Improves Code Maintainability** – Adding new elements doesn’t require modifying existing code.  
✅ **Enhances Scalability** – Supports large-scale systems with nested elements.  
✅ **Works Well with Recursive Processing** – Useful for handling nested data structures like trees.

---

## **Where is the Composite Pattern Used?**
- **Menu Systems** – Menu items and submenus (e.g., in a UI).
- **Organization Structures** – Departments, teams, and employees.
- **Product Categories** – Parent and subcategories in an e-commerce store.
- **File System Representation** – Files and folders in a hierarchical structure.
- **Graph-Based Data Structures** – Social networks, family trees, decision trees.

---

## **Implementing Composite Pattern in Java**

### **1. Define the Component Interface**
```java
public interface FileComponent {
    void showDetails();
}
```

---

### **2. Implement the Leaf Class (File)**
```java
public class File implements FileComponent {
    private String name;

    public File(String name) {
        this.name = name;
    }

    @Override
    public void showDetails() {
        System.out.println("File: " + name);
    }
}
```

---

### **3. Implement the Composite Class (Folder)**
```java
import java.util.ArrayList;
import java.util.List;

public class Folder implements FileComponent {
    private String name;
    private List<FileComponent> components = new ArrayList<>();

    public Folder(String name) {
        this.name = name;
    }

    public void add(FileComponent component) {
        components.add(component);
    }

    public void remove(FileComponent component) {
        components.remove(component);
    }

    @Override
    public void showDetails() {
        System.out.println("Folder: " + name);
        for (FileComponent component : components) {
            component.showDetails();
        }
    }
}
```

---

### **4. Using the Composite Pattern**
```java
public class CompositePatternDemo {
    public static void main(String[] args) {
        FileComponent file1 = new File("document.txt");
        FileComponent file2 = new File("photo.jpg");

        Folder folder = new Folder("My Documents");
        folder.add(file1);
        folder.add(file2);

        Folder root = new Folder("Root Folder");
        root.add(folder);
        root.add(new File("README.md"));

        root.showDetails();
    }
}
```

---

### **Output:**
```
Folder: Root Folder
Folder: My Documents
File: document.txt
File: photo.jpg
File: README.md
```

✔ **Files and Folders Are Treated Uniformly**.  
✔ **Supports Nested Structures**.

---

## **Composite Pattern in Spring Boot (Managing Categories in an E-Commerce Application)**

Spring Boot applications often handle **hierarchical data**, such as **product categories** in e-commerce.

---

### **1. Define the Component Interface**
```java
public interface Category {
    void showDetails();
}
```

---

### **2. Implement the Leaf Class (Single Category)**
```java
import org.springframework.stereotype.Component;

@Component
public class SingleCategory implements Category {
    private String name;

    public SingleCategory(String name) {
        this.name = name;
    }

    @Override
    public void showDetails() {
        System.out.println("Category: " + name);
    }
}
```

---

### **3. Implement the Composite Class (Category Group)**
```java
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;

@Component
public class CategoryGroup implements Category {
    private String name;
    private List<Category> subCategories = new ArrayList<>();

    public CategoryGroup(String name) {
        this.name = name;
    }

    public void addCategory(Category category) {
        subCategories.add(category);
    }

    public void removeCategory(Category category) {
        subCategories.remove(category);
    }

    @Override
    public void showDetails() {
        System.out.println("Category Group: " + name);
        for (Category category : subCategories) {
            category.showDetails();
        }
    }
}
```

---

### **4. Create a Service to Manage Categories**
```java
import org.springframework.stereotype.Service;

@Service
public class CategoryService {
    public Category createCategoryTree() {
        SingleCategory electronics = new SingleCategory("Electronics");
        SingleCategory laptops = new SingleCategory("Laptops");
        SingleCategory phones = new SingleCategory("Phones");

        CategoryGroup techCategory = new CategoryGroup("Tech");
        techCategory.addCategory(electronics);
        techCategory.addCategory(laptops);
        techCategory.addCategory(phones);

        SingleCategory clothing = new SingleCategory("Clothing");
        SingleCategory shoes = new SingleCategory("Shoes");

        CategoryGroup fashionCategory = new CategoryGroup("Fashion");
        fashionCategory.addCategory(clothing);
        fashionCategory.addCategory(shoes);

        CategoryGroup rootCategory = new CategoryGroup("All Categories");
        rootCategory.addCategory(techCategory);
        rootCategory.addCategory(fashionCategory);

        return rootCategory;
    }
}
```

---

### **5. Expose the Composite Structure via REST API**
```java
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/categories")
public class CategoryController {
    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public String showCategoryTree() {
        Category rootCategory = categoryService.createCategoryTree();
        rootCategory.showDetails();
        return "Category structure printed in logs!";
    }
}
```

---

### **6. Testing the API**
#### **Request:**
```bash
curl http://localhost:8080/categories
```
#### **Response (Printed in Logs):**
```
Category Group: All Categories
Category Group: Tech
Category: Electronics
Category: Laptops
Category: Phones
Category Group: Fashion
Category: Clothing
Category: Shoes
```

✔ **Spring Boot Handles Nested Categories with Ease**.  
✔ **Categories Can Be Extended Without Modifying Existing Code**.

---

## **Real-World Use Cases for Composite Pattern**
| Use Case | Example |
|----------|---------|
| **Menu Systems** | UI menus with nested submenus |
| **Organization Trees** | Employees, teams, and departments |
| **File Systems** | Files and directories in a hierarchy |
| **E-commerce Categories** | Product categories with subcategories |
| **Workflow Systems** | Nested tasks in process automation |

---

## **Advantages of Using the Composite Pattern in Spring Boot**
| Feature | Benefit |
|---------|---------|
| **Unified API** | Treats individual and composite objects uniformly |
| **Scalability** | Easily supports large hierarchical structures |
| **Encapsulation** | Hides complex tree management logic |
| **Recursive Processing** | Supports nested operations efficiently |
| **Spring Boot Integration** | Works well with REST APIs and database structures |

---

## **When NOT to Use the Composite Pattern**
❌ **If the Object Structure is Simple** – No need for a tree-based model.  
❌ **If Objects Are Unrelated** – No clear parent-child relationship exists.  
❌ **If Performance is Critical** – Recursive tree traversal may introduce overhead.

---

## **Conclusion**
The **Composite Pattern** is widely used in **Spring Boot for managing hierarchical structures** like **file systems, menus, and product categories**. It **simplifies tree-based object management** and **supports recursive processing**.

### **Next Steps**
- Implement **database storage for hierarchical categories**.
- Extend the category system to support **CRUD operations**.
- Use **GraphQL in Spring Boot** for querying complex hierarchical data.


# **Java & Spring Boot Fact: Command Pattern in Spring Boot**

## **What is the Command Pattern?**
The **Command Pattern** is a **behavioral design pattern** that **encapsulates a request as an object**, allowing parameterization, queuing, logging, and undoable operations. It separates the **sender** of a request from the **executor**, making systems more **flexible, maintainable, and extensible**.

In **Spring Boot**, the **Command Pattern** is useful in **task execution, undo/redo operations, scheduling, and microservices communication**.

---

## **How Does the Command Pattern Work?**
1. **Command Interface** – Defines a standard method for execution.
2. **Concrete Commands** – Implement different commands.
3. **Invoker (Executor)** – Stores and executes commands.
4. **Receiver (Service or Business Logic)** – Performs the actual operation.

---

## **Why Use the Command Pattern?**
✅ **Encapsulates Requests** – Commands are objects that can be passed, stored, or queued.  
✅ **Reduces Coupling** – The client does not interact directly with the business logic.  
✅ **Supports Undo/Redo** – Commands can be stored and reversed.  
✅ **Enhances Maintainability** – New commands can be added without modifying existing code.  
✅ **Improves Asynchronous Processing** – Commands can be scheduled for execution later.

---

## **Where is the Command Pattern Used?**
- **Task Scheduling** – Delayed or batch processing.
- **Undo/Redo Operations** – Document editing, transaction rollbacks.
- **Microservices Communication** – Command-based event-driven processing.
- **Logging & Auditing** – Storing user actions for replay.
- **Queue-based Job Processing** – Handling background jobs in systems like RabbitMQ or Kafka.

---

## **Implementing Command Pattern in Java**

### **1. Define the Command Interface**
```java
public interface Command {
    void execute();
}
```

---

### **2. Create Concrete Commands**
```java
public class TurnOnLightCommand implements Command {
    private Light light;

    public TurnOnLightCommand(Light light) {
        this.light = light;
    }

    @Override
    public void execute() {
        light.turnOn();
    }
}
```
```java
public class TurnOffLightCommand implements Command {
    private Light light;

    public TurnOffLightCommand(Light light) {
        this.light = light;
    }

    @Override
    public void execute() {
        light.turnOff();
    }
}
```

---

### **3. Implement the Receiver**
```java
public class Light {
    public void turnOn() {
        System.out.println("Light is ON");
    }

    public void turnOff() {
        System.out.println("Light is OFF");
    }
}
```

---

### **4. Implement the Invoker**
```java
public class RemoteControl {
    private Command command;

    public void setCommand(Command command) {
        this.command = command;
    }

    public void pressButton() {
        command.execute();
    }
}
```

---

### **5. Using the Command Pattern**
```java
public class CommandPatternDemo {
    public static void main(String[] args) {
        Light light = new Light();

        Command turnOnCommand = new TurnOnLightCommand(light);
        Command turnOffCommand = new TurnOffLightCommand(light);

        RemoteControl remote = new RemoteControl();

        remote.setCommand(turnOnCommand);
        remote.pressButton(); // Output: Light is ON

        remote.setCommand(turnOffCommand);
        remote.pressButton(); // Output: Light is OFF
    }
}
```

### **Output:**
```
Light is ON
Light is OFF
```

✔ **Decouples Command Execution from Command Logic**.  
✔ **Allows Queuing and Scheduling of Commands**.

---

## **Command Pattern in Spring Boot (Task Execution & Undo Feature)**

Spring Boot applications often require **task execution**, **scheduling**, and **undoable operations**.

---

### **1. Define the Command Interface**
```java
public interface OrderCommand {
    void execute();
    void undo();
}
```

---

### **2. Implement Concrete Commands**
```java
import org.springframework.stereotype.Component;

@Component
public class CreateOrderCommand implements OrderCommand {
    private String orderId;

    public CreateOrderCommand(String orderId) {
        this.orderId = orderId;
    }

    @Override
    public void execute() {
        System.out.println("Creating order: " + orderId);
    }

    @Override
    public void undo() {
        System.out.println("Cancelling order: " + orderId);
    }
}
```
```java
import org.springframework.stereotype.Component;

@Component
public class ShipOrderCommand implements OrderCommand {
    private String orderId;

    public ShipOrderCommand(String orderId) {
        this.orderId = orderId;
    }

    @Override
    public void execute() {
        System.out.println("Shipping order: " + orderId);
    }

    @Override
    public void undo() {
        System.out.println("Reverting shipping for order: " + orderId);
    }
}
```

---

### **3. Implement the Invoker (Command Executor)**
```java
import org.springframework.stereotype.Service;
import java.util.Stack;

@Service
public class OrderCommandExecutor {
    private Stack<OrderCommand> commandHistory = new Stack<>();

    public void executeCommand(OrderCommand command) {
        command.execute();
        commandHistory.push(command);
    }

    public void undoLastCommand() {
        if (!commandHistory.isEmpty()) {
            OrderCommand lastCommand = commandHistory.pop();
            lastCommand.undo();
        } else {
            System.out.println("No commands to undo.");
        }
    }
}
```

---

### **4. Expose Command Execution via REST API**
```java
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
public class OrderController {
    private final OrderCommandExecutor executor;

    public OrderController(OrderCommandExecutor executor) {
        this.executor = executor;
    }

    @PostMapping("/create/{orderId}")
    public String createOrder(@PathVariable String orderId) {
        executor.executeCommand(new CreateOrderCommand(orderId));
        return "Order Created!";
    }

    @PostMapping("/ship/{orderId}")
    public String shipOrder(@PathVariable String orderId) {
        executor.executeCommand(new ShipOrderCommand(orderId));
        return "Order Shipped!";
    }

    @PostMapping("/undo")
    public String undoLastOrder() {
        executor.undoLastCommand();
        return "Last operation undone!";
    }
}
```

---

### **5. Testing the API**
#### **Create Order:**
```bash
curl -X POST http://localhost:8080/orders/create/ORD123
```
#### **Response:**
```
Creating order: ORD123
```

#### **Ship Order:**
```bash
curl -X POST http://localhost:8080/orders/ship/ORD123
```
#### **Response:**
```
Shipping order: ORD123
```

#### **Undo Last Operation:**
```bash
curl -X POST http://localhost:8080/orders/undo
```
#### **Response:**
```
Reverting shipping for order: ORD123
```

✔ **Spring Boot Manages Command Execution & Undo**.  
✔ **New Commands Can Be Added Without Modifying Existing Logic**.

---

## **Real-World Use Cases for Command Pattern**
| Use Case | Example |
|----------|---------|
| **Task Scheduling** | Scheduling commands to execute later |
| **Undo/Redo Operations** | Reverting transactions, document editing |
| **Microservices Event Handling** | Queuing commands for asynchronous execution |
| **Logging & Auditing** | Storing user actions for replay |
| **Database Transactions** | Implementing rollback mechanisms |

---

## **Advantages of Using the Command Pattern in Spring Boot**
| Feature | Benefit |
|---------|---------|
| **Encapsulates Requests** | Commands are objects that can be stored, queued, and logged |
| **Reduces Coupling** | Invokers don’t depend on specific operations |
| **Enhances Maintainability** | New commands can be added without modifying existing code |
| **Supports Undo/Redo** | Command history enables rollback features |
| **Works Well with Spring Boot Services** | Integrates with REST APIs, event-driven systems, and job queues |

---

## **When NOT to Use the Command Pattern**
❌ **If Operations are Simple** – Direct method calls may be more efficient.  
❌ **If Undo/Redo is Not Needed** – Adds unnecessary complexity.  
❌ **If Execution is Immediate** – No need for command queuing.

---

## **Conclusion**
The **Command Pattern** is essential in **Spring Boot for handling task execution, undoable operations, and event-driven processing**. It **decouples execution logic from requests, supports undo, and enables flexible microservices orchestration**.

### **Next Steps**
- Implement **asynchronous command execution with RabbitMQ**.
- Extend the order system to **persist command history in a database**.
- Use **Spring Batch for command-driven job execution**.


# **Java & Spring Boot Fact: Visitor Pattern in Spring Boot**

## **What is the Visitor Pattern?**
The **Visitor Pattern** is a **behavioral design pattern** that **separates an algorithm from the objects it operates on**. It allows you to add new operations **without modifying existing classes**, making it useful when working with **complex object hierarchies**.

In **Spring Boot**, the **Visitor Pattern** is useful in **report generation, serialization, compiler design, data transformation, and AST (Abstract Syntax Tree) processing**.

---

## **How Does the Visitor Pattern Work?**
1. **Element Interface** – Defines an `accept(Visitor visitor)` method that allows a visitor to interact with elements.
2. **Concrete Elements** – Implement `accept()` to allow visitors to operate on them.
3. **Visitor Interface** – Defines operations that can be performed on elements.
4. **Concrete Visitors** – Implement different behaviors for each element type.

---

## **Why Use the Visitor Pattern?**
✅ **Open-Closed Principle** – New operations can be added **without modifying element classes**.  
✅ **Encapsulates Complex Operations** – Keeps element classes **clean and focused** on their primary behavior.  
✅ **Enhances Maintainability** – Changes to operations **don’t affect** the element structure.  
✅ **Supports Multiple Behaviors** – Different visitors can be created **for different use cases**.  
✅ **Works Well with Tree Structures** – Commonly used for **parsing and hierarchical data processing**.

---

## **Where is the Visitor Pattern Used?**
- **Report Generation** – Collecting and formatting different types of data objects.
- **Compilers & AST Processing** – Parsing programming languages and performing optimizations.
- **Serialization & Transformation** – Converting objects to different formats (JSON, XML, etc.).
- **File System Processing** – Applying operations on different file types (folders, text files, images).
- **E-commerce Discount Calculations** – Applying different discounts based on product types.

---

## **Implementing Visitor Pattern in Java**

### **1. Define the Element Interface**
```java
public interface ItemElement {
    void accept(ShoppingCartVisitor visitor);
}
```

---

### **2. Implement Concrete Elements**
```java
public class Book implements ItemElement {
    private String title;
    private double price;

    public Book(String title, double price) {
        this.title = title;
        this.price = price;
    }

    public double getPrice() {
        return price;
    }

    @Override
    public void accept(ShoppingCartVisitor visitor) {
        visitor.visit(this);
    }
}
```
```java
public class Electronics implements ItemElement {
    private String name;
    private double price;

    public Electronics(String name, double price) {
        this.name = name;
        this.price = price;
    }

    public double getPrice() {
        return price;
    }

    @Override
    public void accept(ShoppingCartVisitor visitor) {
        visitor.visit(this);
    }
}
```

---

### **3. Define the Visitor Interface**
```java
public interface ShoppingCartVisitor {
    void visit(Book book);
    void visit(Electronics electronics);
}
```

---

### **4. Implement a Concrete Visitor**
```java
public class PriceCalculatorVisitor implements ShoppingCartVisitor {
    private double totalPrice = 0;

    @Override
    public void visit(Book book) {
        totalPrice += book.getPrice();
        System.out.println("Added book: $" + book.getPrice());
    }

    @Override
    public void visit(Electronics electronics) {
        totalPrice += electronics.getPrice();
        System.out.println("Added electronics: $" + electronics.getPrice());
    }

    public double getTotalPrice() {
        return totalPrice;
    }
}
```

---

### **5. Using the Visitor Pattern**
```java
import java.util.Arrays;
import java.util.List;

public class VisitorPatternDemo {
    public static void main(String[] args) {
        List<ItemElement> items = Arrays.asList(
            new Book("Spring Boot Guide", 30.0),
            new Electronics("Laptop", 500.0)
        );

        PriceCalculatorVisitor priceVisitor = new PriceCalculatorVisitor();
        
        for (ItemElement item : items) {
            item.accept(priceVisitor);
        }

        System.out.println("Total Cart Price: $" + priceVisitor.getTotalPrice());
    }
}
```

---

### **Output:**
```
Added book: $30.0
Added electronics: $500.0
Total Cart Price: $530.0
```

✔ **Supports Multiple Item Types Without Changing Their Implementation**.  
✔ **Adding More Visitors (e.g., Discount Calculation) is Easy**.

---

## **Visitor Pattern in Spring Boot (Report Generation for Orders)**

Spring Boot applications often need to **generate reports** and **apply different operations on hierarchical data**.

---

### **1. Define the Element Interface**
```java
public interface OrderItem {
    void accept(OrderReportVisitor visitor);
}
```

---

### **2. Implement Concrete Order Items**
```java
import org.springframework.stereotype.Component;

@Component
public class Product implements OrderItem {
    private String name;
    private double price;

    public Product(String name, double price) {
        this.name = name;
        this.price = price;
    }

    public double getPrice() {
        return price;
    }

    @Override
    public void accept(OrderReportVisitor visitor) {
        visitor.visit(this);
    }
}
```
```java
import org.springframework.stereotype.Component;

@Component
public class Service implements OrderItem {
    private String description;
    private double cost;

    public Service(String description, double cost) {
        this.description = description;
        this.cost = cost;
    }

    public double getCost() {
        return cost;
    }

    @Override
    public void accept(OrderReportVisitor visitor) {
        visitor.visit(this);
    }
}
```

---

### **3. Define the Visitor Interface**
```java
public interface OrderReportVisitor {
    void visit(Product product);
    void visit(Service service);
}
```

---

### **4. Implement Concrete Visitors**
#### **4.1 Sales Report Generator**
```java
import org.springframework.stereotype.Component;

@Component
public class SalesReportVisitor implements OrderReportVisitor {
    private double totalRevenue = 0;

    @Override
    public void visit(Product product) {
        totalRevenue += product.getPrice();
        System.out.println("Product sold: $" + product.getPrice());
    }

    @Override
    public void visit(Service service) {
        totalRevenue += service.getCost();
        System.out.println("Service provided: $" + service.getCost());
    }

    public double getTotalRevenue() {
        return totalRevenue;
    }
}
```

#### **4.2 Discount Calculator Visitor**
```java
import org.springframework.stereotype.Component;

@Component
public class DiscountCalculatorVisitor implements OrderReportVisitor {
    private double discountAmount = 0;

    @Override
    public void visit(Product product) {
        discountAmount += product.getPrice() * 0.10; // 10% discount
        System.out.println("Discount applied on product: $" + (product.getPrice() * 0.10));
    }

    @Override
    public void visit(Service service) {
        discountAmount += service.getCost() * 0.05; // 5% discount
        System.out.println("Discount applied on service: $" + (service.getCost() * 0.05));
    }

    public double getTotalDiscount() {
        return discountAmount;
    }
}
```

---

### **5. Create an Order Service to Process Orders**
```java
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class OrderService {
    public void generateReports(List<OrderItem> orderItems, OrderReportVisitor visitor) {
        for (OrderItem item : orderItems) {
            item.accept(visitor);
        }
    }
}
```

---

### **6. Expose the API for Order Reports**
```java
import org.springframework.web.bind.annotation.*;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {
    private final OrderService orderService;
    private final SalesReportVisitor salesVisitor;
    private final DiscountCalculatorVisitor discountVisitor;

    public OrderController(OrderService orderService, SalesReportVisitor salesVisitor, DiscountCalculatorVisitor discountVisitor) {
        this.orderService = orderService;
        this.salesVisitor = salesVisitor;
        this.discountVisitor = discountVisitor;
    }

    @GetMapping("/report")
    public String generateReport() {
        List<OrderItem> items = Arrays.asList(new Product("Laptop", 800), new Service("Repair", 50));

        orderService.generateReports(items, salesVisitor);
        return "Total Revenue: $" + salesVisitor.getTotalRevenue();
    }

    @GetMapping("/discount")
    public String calculateDiscount() {
        List<OrderItem> items = Arrays.asList(new Product("Laptop", 800), new Service("Repair", 50));

        orderService.generateReports(items, discountVisitor);
        return "Total Discount: $" + discountVisitor.getTotalDiscount();
    }
}
```

✔ **Supports Multiple Reports Without Changing OrderItem Classes**.  
✔ **New Visitors Can Be Added Without Modifying Existing Logic**.

---


# **Java & Spring Boot Fact: Memento Pattern in Spring Boot**

## **What is the Memento Pattern?**
The **Memento Pattern** is a **behavioral design pattern** that allows an object to **save and restore its previous state** without exposing its internal details. It is useful for **undo/redo functionality, state history management, and checkpointing.**

In **Spring Boot**, the **Memento Pattern** is useful in **version control, undo/redo operations, stateful web applications, and rollback mechanisms**.

---

## **How Does the Memento Pattern Work?**
1. **Originator** – The object whose state needs to be saved and restored.
2. **Memento** – Stores the state of the originator without exposing internal details.
3. **Caretaker** – Manages the history of mementos, allowing undo and redo operations.

---

## **Why Use the Memento Pattern?**
✅ **Enables Undo/Redo Functionality** – Useful for user-driven applications.  
✅ **Protects Object Encapsulation** – Saves state without exposing object internals.  
✅ **Simplifies Rollbacks** – Restores previous states easily.  
✅ **Supports Versioning** – Useful in document editors and workflow systems.  
✅ **Improves Debugging** – Allows stepping back to previous states.

---

## **Where is the Memento Pattern Used?**
- **Document Editing** – Undo/redo for text changes.
- **Workflow Systems** – Tracking process history.
- **Game Development** – Saving checkpoints in gameplay.
- **Database Transactions** – Implementing rollback mechanisms.
- **Stateful Web Applications** – Restoring previous form inputs.

---

## **Implementing Memento Pattern in Java**

### **1. Define the Memento Class**
```java
public class EditorMemento {
    private final String content;

    public EditorMemento(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }
}
```

---

### **2. Implement the Originator (Editor)**
```java
public class TextEditor {
    private String content;

    public void write(String newContent) {
        content = newContent;
    }

    public EditorMemento save() {
        return new EditorMemento(content);
    }

    public void restore(EditorMemento memento) {
        content = memento.getContent();
    }

    public String getContent() {
        return content;
    }
}
```

---

### **3. Implement the Caretaker (Undo Manager)**
```java
import java.util.Stack;

public class UndoManager {
    private Stack<EditorMemento> history = new Stack<>();

    public void save(TextEditor editor) {
        history.push(editor.save());
    }

    public void undo(TextEditor editor) {
        if (!history.isEmpty()) {
            editor.restore(history.pop());
        }
    }
}
```

---

### **4. Using the Memento Pattern**
```java
public class MementoPatternDemo {
    public static void main(String[] args) {
        TextEditor editor = new TextEditor();
        UndoManager undoManager = new UndoManager();

        editor.write("Hello, world!");
        undoManager.save(editor);
        System.out.println("Content: " + editor.getContent());

        editor.write("Hello, Spring Boot!");
        undoManager.save(editor);
        System.out.println("Updated Content: " + editor.getContent());

        undoManager.undo(editor);
        System.out.println("After Undo: " + editor.getContent());

        undoManager.undo(editor);
        System.out.println("After Second Undo: " + editor.getContent());
    }
}
```

---

### **Output:**
```
Content: Hello, world!
Updated Content: Hello, Spring Boot!
After Undo: Hello, world!
After Second Undo: Hello, world!
```

✔ **Provides Undo Functionality Without Breaking Encapsulation**.  
✔ **Keeps Track of Previous States** for easy restoration.

---

## **Memento Pattern in Spring Boot (Undo for User Actions)**

Spring Boot applications often require **undo functionality**, **version control**, and **rollback mechanisms**.

---

### **1. Define the Memento Class**
```java
public class UserMemento {
    private final String name;
    private final String email;

    public UserMemento(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }
}
```

---

### **2. Implement the Originator (User Profile)**
```java
import org.springframework.stereotype.Component;

@Component
public class UserProfile {
    private String name;
    private String email;

    public void updateProfile(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public UserMemento save() {
        return new UserMemento(name, email);
    }

    public void restore(UserMemento memento) {
        this.name = memento.getName();
        this.email = memento.getEmail();
    }

    public String getProfile() {
        return "Name: " + name + ", Email: " + email;
    }
}
```

---

### **3. Implement the Caretaker (Undo Service)**
```java
import org.springframework.stereotype.Service;
import java.util.Stack;

@Service
public class UndoService {
    private Stack<UserMemento> history = new Stack<>();

    public void saveState(UserProfile user) {
        history.push(user.save());
    }

    public void undo(UserProfile user) {
        if (!history.isEmpty()) {
            user.restore(history.pop());
        }
    }
}
```

---

### **4. Expose an API for User Updates with Undo Support**
```java
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {
    private final UserProfile userProfile;
    private final UndoService undoService;

    public UserController(UserProfile userProfile, UndoService undoService) {
        this.userProfile = userProfile;
        this.undoService = undoService;
    }

    @PostMapping("/update")
    public String updateProfile(@RequestParam String name, @RequestParam String email) {
        undoService.saveState(userProfile);
        userProfile.updateProfile(name, email);
        return "Profile Updated: " + userProfile.getProfile();
    }

    @PostMapping("/undo")
    public String undoProfileUpdate() {
        undoService.undo(userProfile);
        return "Profile Restored: " + userProfile.getProfile();
    }
}
```

---

### **5. Testing the API**
#### **Update User Profile**
```bash
curl -X POST "http://localhost:8080/user/update?name=Alice&email=alice@example.com"
```
#### **Response:**
```
Profile Updated: Name: Alice, Email: alice@example.com
```

#### **Undo Last Profile Update**
```bash
curl -X POST http://localhost:8080/user/undo
```
#### **Response:**
```
Profile Restored: Name: (Previous Name), Email: (Previous Email)
```

✔ **Spring Boot Manages User Profile State & Undo Operations**.  
✔ **New Changes Can Be Tracked Without Losing Past Data**.

---

## **Real-World Use Cases for Memento Pattern**
| Use Case | Example |
|----------|---------|
| **Document Editing** | Undo/redo changes in text editors |
| **Stateful Web Applications** | Save and restore form inputs |
| **Workflow Systems** | Reverting approval steps |
| **Game Development** | Saving game progress at checkpoints |
| **Database Transactions** | Implementing rollback functionality |

---

## **Advantages of Using the Memento Pattern in Spring Boot**
| Feature | Benefit |
|---------|---------|
| **Encapsulates State** | Saves state without exposing object internals |
| **Supports Undo/Redo** | Easily revert previous operations |
| **Enhances Debugging** | Step back to previous states |
| **Works with Spring Boot Services** | Integrates with REST APIs for history tracking |
| **Simplifies Rollback Mechanisms** | Useful in transactions and workflows |

---

## **When NOT to Use the Memento Pattern**
❌ **If State Changes are Rare** – No need for tracking history.  
❌ **If Memory Usage is a Concern** – Saving too many states may cause performance issues.  
❌ **If Direct State Management is Preferred** – The State Pattern may be a better choice.

---

## **Memento Pattern vs State Pattern**
| Feature | Memento Pattern | State Pattern |
|---------|----------------|--------------|
| **Purpose** | Saves and restores state | Changes behavior based on state |
| **Focus** | Keeping snapshots of past states | Defining different behaviors for each state |
| **Example** | Undo feature in a text editor | Order processing states (New, Processing, Shipped) |

---

## **Conclusion**
The **Memento Pattern** is widely used in **Spring Boot for handling undo operations, rollback mechanisms, and version control**. It **provides encapsulated state management and simplifies tracking changes**.

### **Next Steps**
- Implement **version control for document editing**.
- Extend undo functionality to **track multiple changes**.
- Use **Spring Boot with databases to store memento history**.


# **Java & Spring Boot Fact: Prototype Pattern in Spring Boot**

## **What is the Prototype Pattern?**
The **Prototype Pattern** is a **creational design pattern** that allows objects to be **cloned** rather than created from scratch. Instead of instantiating new objects, the pattern uses an existing object as a prototype and **copies its properties** to generate new instances.

In **Spring Boot**, the **Prototype Pattern** is useful for **object caching, reducing expensive object creation, and handling dynamic configurations.**

---

## **How Does the Prototype Pattern Work?**
1. **Prototype Interface** – Declares a `clone()` method for copying objects.
2. **Concrete Prototype** – Implements `clone()` to duplicate objects.
3. **Client** – Uses the prototype to create new instances dynamically.

---

## **Why Use the Prototype Pattern?**
✅ **Reduces Object Creation Cost** – Cloning is faster than creating new objects from scratch.  
✅ **Encapsulates Object Creation Logic** – The client does not worry about object creation complexity.  
✅ **Supports Dynamic Object Configuration** – Each clone can be modified without affecting the original object.  
✅ **Works Well with Dependency Injection** – Helps create multiple bean instances in **Spring Boot**.  
✅ **Efficient for Caching & Performance Optimization** – Reduces redundant initialization processes.

---

## **Where is the Prototype Pattern Used?**
- **Spring Boot Bean Scope (`@Scope("prototype")`)** – Creates new bean instances instead of reusing the same instance.
- **Object Caching** – Reusing expensive-to-create objects.
- **Database Entity Cloning** – Duplicating database objects for transactional operations.
- **Configuration Management** – Creating different configurations based on templates.
- **Game Development** – Cloning enemy characters, weapons, or maps.

---

## **Implementing Prototype Pattern in Java**

### **1. Define the Prototype Interface**
```java
public interface Prototype {
    Prototype clone();
}
```

---

### **2. Implement the Concrete Prototype Class**
```java
public class Product implements Prototype {
    private String name;
    private double price;

    public Product(String name, double price) {
        this.name = name;
        this.price = price;
    }

    @Override
    public Prototype clone() {
        return new Product(name, price);
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void showDetails() {
        System.out.println("Product: " + name + " | Price: $" + price);
    }
}
```

---

### **3. Using the Prototype Pattern**
```java
public class PrototypePatternDemo {
    public static void main(String[] args) {
        Product original = new Product("Laptop", 1000.0);
        original.showDetails();

        Product cloned = (Product) original.clone();
        cloned.setPrice(900.0);
        cloned.showDetails();
    }
}
```

---

### **Output:**
```
Product: Laptop | Price: $1000.0
Product: Laptop | Price: $900.0
```

✔ **Cloning Avoids Expensive Object Creation**.  
✔ **Each Clone Can Be Modified Independently**.

---

## **Prototype Pattern in Spring Boot (Prototype Scoped Beans)**

In **Spring Boot**, the Prototype Pattern is used for **creating multiple bean instances dynamically** instead of reusing a single instance.

---

### **1. Define a Prototype Bean**
```java
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype") // Each request gets a new instance
public class Report {
    private String reportName;

    public Report() {
        this.reportName = "Monthly Sales Report";
        System.out.println("New Report Object Created!");
    }

    public void setReportName(String reportName) {
        this.reportName = reportName;
    }

    public String getReportName() {
        return reportName;
    }
}
```

---

### **2. Create a Service to Generate Reports**
```java
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

@Service
public class ReportService {
    private final ApplicationContext applicationContext;

    public ReportService(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public Report generateReport() {
        return applicationContext.getBean(Report.class); // Retrieves a new instance
    }
}
```

---

### **3. Expose an API to Generate Reports**
```java
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/report")
public class ReportController {
    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/generate")
    public String generateReport() {
        Report report = reportService.generateReport();
        return "Generated: " + report.getReportName();
    }
}
```

---

### **4. Testing the API**
#### **Generate Report:**
```bash
curl http://localhost:8080/report/generate
```
#### **Response:**
```
New Report Object Created!
Generated: Monthly Sales Report
```

✔ **Each API Call Creates a New Report Object**.  
✔ **Prototype Beans Work Independently Without Affecting Others**.

---

## **Real-World Use Cases for Prototype Pattern**
| Use Case | Example |
|----------|---------|
| **Spring Prototype Scoped Beans** | Creating new bean instances dynamically |
| **Object Caching** | Reusing pre-created objects instead of expensive instantiation |
| **Game Development** | Cloning characters, items, or maps dynamically |
| **Document Templates** | Generating new documents from predefined templates |
| **Database Record Cloning** | Duplicating entities for transactional safety |

---

## **Advantages of Using the Prototype Pattern in Spring Boot**
| Feature | Benefit |
|---------|---------|
| **Reduces Object Creation Cost** | Cloning is faster than creating new instances from scratch |
| **Encapsulates Object Creation** | Clients don’t need to know how objects are created |
| **Supports Dynamic Configuration** | Each clone can be customized without affecting others |
| **Spring Boot Integration** | Works seamlessly with `@Scope("prototype")` |
| **Improves Performance** | Reduces redundant initialization of complex objects |

---

## **When NOT to Use the Prototype Pattern**
❌ **If Objects Don’t Require Cloning** – Using `new` may be simpler.  
❌ **If Object Identity is Important** – Cloned objects don’t share references with the original.  
❌ **If Object Initialization is Cheap** – Cloning may introduce unnecessary overhead.

---

## **Prototype Pattern vs Singleton Pattern**
| Feature | Prototype Pattern | Singleton Pattern |
|---------|----------------|----------------|
| **Purpose** | Creates multiple instances from a prototype | Ensures only one instance globally |
| **Scope** | New instance per request | Shared instance for entire application |
| **Example** | Report generation, object caching | Configuration management, logging |

---

## **Conclusion**
The **Prototype Pattern** is widely used in **Spring Boot for handling dynamic object creation, prototype-scoped beans, and performance optimization**. It **reduces object creation cost and improves flexibility**.

### **Next Steps**
- Implement **deep cloning for nested objects**.
- Extend the prototype pattern to **handle database entity duplication**.
- Use **Spring Boot with caching frameworks to optimize object reuse**.


# **Java & Spring Boot Fact: Decorator Pattern in Spring Boot**

## **What is the Decorator Pattern?**
The **Decorator Pattern** is a **structural design pattern** that allows you to **dynamically add new behavior** to an object **without modifying its original structure**. Instead of using **inheritance**, it relies on **composition** to wrap objects with additional functionality.

In **Spring Boot**, the **Decorator Pattern** is useful for **logging, security enhancements, caching, request/response modification, and dynamically modifying service behavior.**

---

## **How Does the Decorator Pattern Work?**
1. **Component Interface** – Defines the common behavior for objects that can be decorated.
2. **Concrete Component** – The base class whose behavior can be extended dynamically.
3. **Decorator Class** – Wraps the concrete component and adds extra functionality.
4. **Concrete Decorators** – Implement additional behavior dynamically at runtime.

---

## **Why Use the Decorator Pattern?**
✅ **Extends Functionality Without Modifying Existing Code** – Ideal for **Open-Closed Principle**.  
✅ **Supports Dynamic Behavior** – You can **add/remove behaviors at runtime**.  
✅ **Avoids Class Explosion** – Reduces the need for multiple subclasses.  
✅ **Follows Composition Over Inheritance** – More flexible than deep inheritance trees.  
✅ **Works Well in Spring Boot** – Ideal for **middleware, interceptors, and proxy-based enhancements**.

---

## **Where is the Decorator Pattern Used?**
- **Logging in Spring Services** – Wrapping services to log method calls.
- **Spring Boot Security Enhancements** – Adding authentication layers dynamically.
- **Caching Mechanisms** – Decorating services with cache lookups.
- **HTTP Request & Response Modification** – Enhancing controllers dynamically.
- **Data Encryption & Compression** – Wrapping streams to apply security or optimization.

---

## **Implementing Decorator Pattern in Java**

### **1. Define the Component Interface**
```java
public interface Notifier {
    void send(String message);
}
```

---

### **2. Implement the Concrete Component**
```java
public class EmailNotifier implements Notifier {
    @Override
    public void send(String message) {
        System.out.println("Sending Email: " + message);
    }
}
```

---

### **3. Implement the Decorator Class**
```java
public abstract class NotifierDecorator implements Notifier {
    protected Notifier wrappedNotifier;

    public NotifierDecorator(Notifier notifier) {
        this.wrappedNotifier = notifier;
    }

    @Override
    public void send(String message) {
        wrappedNotifier.send(message);
    }
}
```

---

### **4. Implement Concrete Decorators**
```java
public class SMSDecorator extends NotifierDecorator {
    public SMSDecorator(Notifier notifier) {
        super(notifier);
    }

    @Override
    public void send(String message) {
        super.send(message);
        System.out.println("Sending SMS: " + message);
    }
}
```
```java
public class PushNotificationDecorator extends NotifierDecorator {
    public PushNotificationDecorator(Notifier notifier) {
        super(notifier);
    }

    @Override
    public void send(String message) {
        super.send(message);
        System.out.println("Sending Push Notification: " + message);
    }
}
```

---

### **5. Using the Decorator Pattern**
```java
public class DecoratorPatternDemo {
    public static void main(String[] args) {
        Notifier emailNotifier = new EmailNotifier();
        Notifier smsNotifier = new SMSDecorator(emailNotifier);
        Notifier pushNotifier = new PushNotificationDecorator(smsNotifier);

        System.out.println("Basic Email Notification:");
        emailNotifier.send("Hello, user!");

        System.out.println("\nEmail + SMS Notification:");
        smsNotifier.send("Hello, user!");

        System.out.println("\nEmail + SMS + Push Notification:");
        pushNotifier.send("Hello, user!");
    }
}
```

---

### **Output:**
```
Basic Email Notification:
Sending Email: Hello, user!

Email + SMS Notification:
Sending Email: Hello, user!
Sending SMS: Hello, user!

Email + SMS + Push Notification:
Sending Email: Hello, user!
Sending SMS: Hello, user!
Sending Push Notification: Hello, user!
```

✔ **Adds Multiple Behaviors Without Modifying the Base Class**.  
✔ **Dynamically Combines Features at Runtime**.

---

## **Decorator Pattern in Spring Boot (Logging & Caching Services)**

Spring Boot applications often need **logging, caching, and security enhancements** dynamically.

---

### **1. Define the Service Interface**
```java
public interface PaymentService {
    void processPayment(double amount);
}
```

---

### **2. Implement the Concrete Service**
```java
import org.springframework.stereotype.Service;

@Service
public class BasicPaymentService implements PaymentService {
    @Override
    public void processPayment(double amount) {
        System.out.println("Processing payment of $" + amount);
    }
}
```

---

### **3. Implement the Decorator Base Class**
```java
public abstract class PaymentDecorator implements PaymentService {
    protected PaymentService wrappedService;

    public PaymentDecorator(PaymentService service) {
        this.wrappedService = service;
    }

    @Override
    public void processPayment(double amount) {
        wrappedService.processPayment(amount);
    }
}
```

---

### **4. Implement Concrete Decorators**
#### **4.1 Logging Decorator**
```java
import org.springframework.stereotype.Service;

@Service
public class LoggingPaymentDecorator extends PaymentDecorator {
    public LoggingPaymentDecorator(PaymentService service) {
        super(service);
    }

    @Override
    public void processPayment(double amount) {
        System.out.println("[LOG] Payment initiated: $" + amount);
        super.processPayment(amount);
        System.out.println("[LOG] Payment completed: $" + amount);
    }
}
```

#### **4.2 Caching Decorator**
```java
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;

@Service
public class CachingPaymentDecorator extends PaymentDecorator {
    private final Map<Double, String> cache = new HashMap<>();

    public CachingPaymentDecorator(PaymentService service) {
        super(service);
    }

    @Override
    public void processPayment(double amount) {
        if (cache.containsKey(amount)) {
            System.out.println("[CACHE] Returning cached result for $" + amount);
        } else {
            super.processPayment(amount);
            cache.put(amount, "Processed");
            System.out.println("[CACHE] Stored result for $" + amount);
        }
    }
}
```

---

### **5. Configure Dependency Injection**
```java
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PaymentConfig {
    @Bean
    public PaymentService paymentService() {
        return new CachingPaymentDecorator(new LoggingPaymentDecorator(new BasicPaymentService()));
    }
}
```

---

### **6. Expose an API to Test Decorators**
```java
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payment")
public class PaymentController {
    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @GetMapping("/{amount}")
    public String process(@PathVariable double amount) {
        paymentService.processPayment(amount);
        return "Payment Processed!";
    }
}
```

---

### **7. Testing the API**
#### **First Payment Request**
```bash
curl http://localhost:8080/payment/100
```
#### **Response:**
```
[LOG] Payment initiated: $100
Processing payment of $100
[LOG] Payment completed: $100
[CACHE] Stored result for $100
```

#### **Second Payment Request**
```bash
curl http://localhost:8080/payment/100
```
#### **Response:**
```
[CACHE] Returning cached result for $100
```

✔ **Logging & Caching Applied Dynamically Without Changing `BasicPaymentService`**.  
✔ **New Decorators Can Be Added Without Modifying Existing Code**.

---

## **Advantages of Using the Decorator Pattern in Spring Boot**
| Feature | Benefit |
|---------|---------|
| **Extends Behavior Without Modifying Code** | Follows **Open-Closed Principle** |
| **Dynamic Feature Addition** | Can enable/disable features at runtime |
| **Reduces Class Explosion** | Avoids deep inheritance hierarchies |
| **Works Well with Spring Boot** | Easily integrates with services and APIs |

---

## **Conclusion**
The **Decorator Pattern** is a **powerful way to extend Spring Boot services dynamically**, commonly used for **logging, security, caching, and middleware enhancements**.

### **Next Steps**
- Implement **Spring Boot request logging using a decorator**.
- Use **decorators to add authentication to services dynamically**.
- Extend caching to support **Redis or EhCache**.


# **Java & Spring Boot Fact: Interpreter Pattern in Spring Boot**

## **What is the Interpreter Pattern?**
The **Interpreter Pattern** is a **behavioral design pattern** used to define a **grammar for a language** and provide an **interpreter** that can process expressions based on that grammar. It is commonly used in **parsing, evaluating mathematical expressions, rule engines, and query language processing**.

In **Spring Boot**, the **Interpreter Pattern** is useful for **custom query processing, expression evaluation, DSL (Domain-Specific Language) parsing, and workflow rule engines**.

---

## **How Does the Interpreter Pattern Work?**
1. **Abstract Expression Interface** – Defines the method to interpret an expression.
2. **Terminal Expression** – Represents constants or basic operations.
3. **Non-Terminal Expression** – Represents complex expressions composed of other expressions.
4. **Context** – Stores data that the interpreter uses.
5. **Client** – Calls the interpreter to process expressions.

---

## **Why Use the Interpreter Pattern?**
✅ **Parses and Evaluates Expressions Dynamically** – Handles user-defined queries or calculations.  
✅ **Encapsulates Grammar Rules** – Separates parsing logic from business logic.  
✅ **Works Well with Recursion** – Supports tree-like expressions (e.g., mathematical expressions).  
✅ **Improves Maintainability** – Easy to extend by adding new expressions.  
✅ **Useful for DSLs and Rule Engines** – Great for processing business rules.

---

## **Where is the Interpreter Pattern Used?**
- **Mathematical Expression Evaluators** – Evaluating complex expressions dynamically.
- **Custom Query Engines** – Parsing user-defined queries.
- **Business Rule Engines** – Defining rules in a structured manner.
- **Expression-Based Configuration** – Handling **Spring Expression Language (SpEL)**.
- **Search Filtering** – Parsing dynamic search filters in applications.

---

## **Implementing Interpreter Pattern in Java**

### **1. Define the Expression Interface**
```java
public interface Expression {
    int interpret();
}
```

---

### **2. Implement Terminal Expressions (Numbers)**
```java
public class NumberExpression implements Expression {
    private int number;

    public NumberExpression(int number) {
        this.number = number;
    }

    @Override
    public int interpret() {
        return number;
    }
}
```

---

### **3. Implement Non-Terminal Expressions (Addition & Subtraction)**
```java
public class AdditionExpression implements Expression {
    private Expression left, right;

    public AdditionExpression(Expression left, Expression right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public int interpret() {
        return left.interpret() + right.interpret();
    }
}
```
```java
public class SubtractionExpression implements Expression {
    private Expression left, right;

    public SubtractionExpression(Expression left, Expression right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public int interpret() {
        return left.interpret() - right.interpret();
    }
}
```

---

### **4. Using the Interpreter Pattern**
```java
public class InterpreterPatternDemo {
    public static void main(String[] args) {
        Expression num1 = new NumberExpression(10);
        Expression num2 = new NumberExpression(5);
        Expression addition = new AdditionExpression(num1, num2);
        Expression subtraction = new SubtractionExpression(addition, new NumberExpression(2));

        System.out.println("10 + 5 = " + addition.interpret());
        System.out.println("(10 + 5) - 2 = " + subtraction.interpret());
    }
}
```

---

### **Output:**
```
10 + 5 = 15
(10 + 5) - 2 = 13
```

✔ **Processes Mathematical Expressions Recursively**.  
✔ **Easily Extensible by Adding New Operations**.

---

## **Interpreter Pattern in Spring Boot (Search Query Engine)**

Spring Boot applications often require **dynamic query processing**, such as **filtering search results based on user input**.

---

### **1. Define the Expression Interface**
```java
public interface QueryExpression {
    boolean interpret(String context);
}
```

---

### **2. Implement Terminal Expressions**
```java
public class ContainsExpression implements QueryExpression {
    private String word;

    public ContainsExpression(String word) {
        this.word = word;
    }

    @Override
    public boolean interpret(String context) {
        return context.contains(word);
    }
}
```
```java
public class EqualsExpression implements QueryExpression {
    private String word;

    public EqualsExpression(String word) {
        this.word = word;
    }

    @Override
    public boolean interpret(String context) {
        return context.equalsIgnoreCase(word);
    }
}
```

---

### **3. Implement Non-Terminal Expressions**
```java
public class AndExpression implements QueryExpression {
    private QueryExpression expr1, expr2;

    public AndExpression(QueryExpression expr1, QueryExpression expr2) {
        this.expr1 = expr1;
        this.expr2 = expr2;
    }

    @Override
    public boolean interpret(String context) {
        return expr1.interpret(context) && expr2.interpret(context);
    }
}
```
```java
public class OrExpression implements QueryExpression {
    private QueryExpression expr1, expr2;

    public OrExpression(QueryExpression expr1, QueryExpression expr2) {
        this.expr1 = expr1;
        this.expr2 = expr2;
    }

    @Override
    public boolean interpret(String context) {
        return expr1.interpret(context) || expr2.interpret(context);
    }
}
```

---

### **4. Implement the Search Service**
```java
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductSearchService {
    private List<String> products = List.of("Laptop", "Smartphone", "Tablet", "Smartwatch");

    public List<String> search(QueryExpression expression) {
        return products.stream()
                .filter(expression::interpret)
                .collect(Collectors.toList());
    }
}
```

---

### **5. Expose an API for Dynamic Search Queries**
```java
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/search")
public class SearchController {
    private final ProductSearchService searchService;

    public SearchController(ProductSearchService searchService) {
        this.searchService = searchService;
    }

    @GetMapping("/contains/{keyword}")
    public List<String> searchContains(@PathVariable String keyword) {
        QueryExpression containsExpression = new ContainsExpression(keyword);
        return searchService.search(containsExpression);
    }

    @GetMapping("/equals/{keyword}")
    public List<String> searchEquals(@PathVariable String keyword) {
        QueryExpression equalsExpression = new EqualsExpression(keyword);
        return searchService.search(equalsExpression);
    }

    @GetMapping("/and/{keyword1}/{keyword2}")
    public List<String> searchAnd(@PathVariable String keyword1, @PathVariable String keyword2) {
        QueryExpression andExpression = new AndExpression(new ContainsExpression(keyword1), new ContainsExpression(keyword2));
        return searchService.search(andExpression);
    }

    @GetMapping("/or/{keyword1}/{keyword2}")
    public List<String> searchOr(@PathVariable String keyword1, @PathVariable String keyword2) {
        QueryExpression orExpression = new OrExpression(new ContainsExpression(keyword1), new ContainsExpression(keyword2));
        return searchService.search(orExpression);
    }
}
```

---

### **6. Testing the API**
#### **Search Products Containing "Smart"**
```bash
curl http://localhost:8080/search/contains/Smart
```
#### **Response:**
```
["Smartphone", "Smartwatch"]
```

#### **Search Products Matching "Laptop"**
```bash
curl http://localhost:8080/search/equals/Laptop
```
#### **Response:**
```
["Laptop"]
```

#### **Search Products Containing Both "Smart" and "watch"**
```bash
curl http://localhost:8080/search/and/Smart/watch
```
#### **Response:**
```
["Smartwatch"]
```

✔ **Processes Dynamic Search Queries Without Hardcoded Conditions**.  
✔ **New Query Expressions Can Be Added Without Modifying Existing Code**.

---

## **Advantages of Using the Interpreter Pattern in Spring Boot**
| Feature | Benefit |
|---------|---------|
| **Encapsulates Expression Logic** | Separates query logic from execution |
| **Supports Dynamic Queries** | Allows runtime parsing of search expressions |
| **Works Well with Tree Structures** | Supports nested expressions |
| **Enhances Maintainability** | Easy to add new expressions |

---

## **Conclusion**
The **Interpreter Pattern** is ideal for **query processing, rule engines, and DSL interpretation** in **Spring Boot**.

### **Next Steps**
- Extend the search engine to support **complex query expressions**.
- Integrate **Spring Expression Language (SpEL)** for **advanced expression evaluation**.
- Use **ANTLR or JavaCC** to build a full-fledged **DSL interpreter**.


# **Java & Spring Boot Fact: Null Object Pattern**

## **What is the Null Object Pattern?**
The **Null Object Pattern** is a **behavioral design pattern** that **provides a default "do-nothing" behavior for missing objects instead of returning `null`**. Instead of checking for `null` everywhere, a **special null object** implements the expected behavior without causing `NullPointerException`.

In **Spring Boot**, the **Null Object Pattern** is useful for **handling missing data, avoiding null checks, preventing exceptions, and improving code readability.**

---

## **How Does the Null Object Pattern Work?**
1. **Abstract Base Class or Interface** – Defines the common behavior.
2. **Concrete Implementation** – Represents real objects that perform operations.
3. **Null Object Class** – Implements the same interface but provides **default or empty behavior**.
4. **Client Code** – Uses objects without checking for `null`.

---

## **Why Use the Null Object Pattern?**
✅ **Avoids NullPointerExceptions** – Prevents runtime errors caused by `null` values.  
✅ **Eliminates Redundant Null Checks** – Reduces boilerplate code.  
✅ **Provides Default Behavior** – Ensures predictable application behavior.  
✅ **Improves Readability** – Code flows naturally without unnecessary conditionals.  
✅ **Enhances Maintainability** – Easy to update behavior without affecting existing code.

---

## **Where is the Null Object Pattern Used?**
- **Database Query Results** – Returning a default object instead of `null` when no record is found.
- **Logging Systems** – A default logger that does nothing if logging is disabled.
- **Configuration Management** – Returning a default configuration instead of `null`.
- **Spring Boot Service Layers** – Avoiding null responses from services.
- **Optional UI Elements** – Rendering a default UI component when data is missing.

---

## **Implementing Null Object Pattern in Java**

### **1. Define the Base Interface**
```java
public interface Customer {
    String getName();
    boolean isNull();
}
```

---

### **2. Implement the Real Object Class**
```java
public class RealCustomer implements Customer {
    private String name;

    public RealCustomer(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isNull() {
        return false;
    }
}
```

---

### **3. Implement the Null Object Class**
```java
public class NullCustomer implements Customer {
    @Override
    public String getName() {
        return "No Customer Available";
    }

    @Override
    public boolean isNull() {
        return true;
    }
}
```

---

### **4. Implement a Factory to Return Real or Null Object**
```java
import java.util.Arrays;
import java.util.List;

public class CustomerFactory {
    private static final List<String> existingCustomers = Arrays.asList("Alice", "Bob", "Charlie");

    public static Customer getCustomer(String name) {
        if (existingCustomers.contains(name)) {
            return new RealCustomer(name);
        } else {
            return new NullCustomer();
        }
    }
}
```

---

### **5. Using the Null Object Pattern**
```java
public class NullObjectPatternDemo {
    public static void main(String[] args) {
        Customer customer1 = CustomerFactory.getCustomer("Alice");
        Customer customer2 = CustomerFactory.getCustomer("Unknown");

        System.out.println("Customer 1: " + customer1.getName());
        System.out.println("Customer 2: " + customer2.getName());
    }
}
```

---

### **Output:**
```
Customer 1: Alice
Customer 2: No Customer Available
```

✔ **No Need for `null` Checks**  
✔ **Avoids `NullPointerException`**

---

## **Null Object Pattern in Spring Boot (Service Layer Example)**

In **Spring Boot applications**, service layers often return `null` when an entity is not found, which leads to **NullPointerException**. Using the **Null Object Pattern**, we can return a **default object instead**.

---

### **1. Define the Customer Entity**
```java
public class Customer {
    private String name;
    private String email;

    public Customer(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }
}
```

---

### **2. Implement the Null Object for Customer**
```java
public class NullCustomer extends Customer {
    public NullCustomer() {
        super("No Customer", "N/A");
    }
}
```

---

### **3. Create the Customer Repository**
```java
import org.springframework.stereotype.Repository;
import java.util.HashMap;
import java.util.Map;

@Repository
public class CustomerRepository {
    private Map<String, Customer> customers = new HashMap<>();

    public CustomerRepository() {
        customers.put("Alice", new Customer("Alice", "alice@example.com"));
        customers.put("Bob", new Customer("Bob", "bob@example.com"));
    }

    public Customer findByName(String name) {
        return customers.getOrDefault(name, new NullCustomer());
    }
}
```

---

### **4. Implement the Customer Service**
```java
import org.springframework.stereotype.Service;

@Service
public class CustomerService {
    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public Customer getCustomerByName(String name) {
        return customerRepository.findByName(name);
    }
}
```

---

### **5. Expose the API Endpoint**
```java
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/customers")
public class CustomerController {
    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping("/{name}")
    public Customer getCustomer(@PathVariable String name) {
        return customerService.getCustomerByName(name);
    }
}
```

---

### **6. Testing the API**
#### **Request: Valid Customer**
```bash
curl http://localhost:8080/customers/Alice
```
#### **Response:**
```json
{
    "name": "Alice",
    "email": "alice@example.com"
}
```

#### **Request: Non-Existent Customer**
```bash
curl http://localhost:8080/customers/Unknown
```
#### **Response:**
```json
{
    "name": "No Customer",
    "email": "N/A"
}
```

✔ **Avoids `NullPointerException` Without Additional `if` Checks**  
✔ **Provides a Meaningful Default Response**

---

## **Advantages of Using the Null Object Pattern in Spring Boot**
| Feature | Benefit |
|---------|---------|
| **Avoids NullPointerException** | Prevents crashes due to missing objects |
| **Reduces Boilerplate Code** | Eliminates repetitive null checks |
| **Provides Default Behavior** | Ensures consistent application behavior |
| **Improves Code Readability** | Simplifies service and repository logic |
| **Works Well with Spring Boot** | Seamlessly integrates with service layers |

---

## **When NOT to Use the Null Object Pattern**
❌ **If `null` is a Valid Response** – Sometimes, returning `null` is meaningful (e.g., database queries).  
❌ **If Default Behavior is Unnecessary** – If missing data should trigger an error instead of default values.  
❌ **If Memory Usage is a Concern** – Creating many default objects could increase memory footprint.

---

## **Null Object Pattern vs Optional**
| Feature | Null Object Pattern | `Optional<T>` |
|---------|-----------------|--------------|
| **Purpose** | Provides a default object | Encapsulates optional values |
| **Handling** | Implements default behavior | Requires explicit checks (`isPresent()`) |
| **Use Case** | Service layers, caching | Wrapping optional return values |

---

## **Conclusion**
The **Null Object Pattern** is a powerful design pattern in **Spring Boot for handling missing data gracefully**. It prevents **NullPointerException**, **eliminates redundant null checks**, and **improves code readability**.

### **Next Steps**
- Implement the pattern for **logging systems** to prevent null logger issues.
- Extend the pattern for **Spring Boot security roles (guest user pattern)**.
- Use it in **database repositories to handle missing records**.

new
# **Java & Spring Boot Fact: Servant Pattern**

## **What is the Servant Pattern?**
The **Servant Pattern** is a **behavioral design pattern** that allows a **helper (servant) class** to perform **common actions for multiple unrelated classes** without modifying their structure. It helps avoid **code duplication** by centralizing reusable behavior.

In **Spring Boot**, the **Servant Pattern** is useful for **utility services, shared business logic, object transformations, and cross-cutting concerns (e.g., logging, validation, security, and auditing).**

---

## **How Does the Servant Pattern Work?**
1. **Client Classes** – Objects that need certain functionalities but **do not define them internally**.
2. **Servant Class** – A helper class that **performs common operations** for multiple unrelated classes.
3. **Loose Coupling** – The client classes do **not depend on each other** but can still share functionality via the servant.

---

## **Why Use the Servant Pattern?**
✅ **Reduces Code Duplication** – Common logic is extracted into a single class.  
✅ **Avoids Large Inheritance Trees** – Functionality is **added without subclassing**.  
✅ **Enhances Maintainability** – Fixing or updating behavior happens in one place.  
✅ **Improves Readability** – Keeps client classes **focused on their core logic**.  
✅ **Works Well with Dependency Injection** – Helps separate **business logic from implementation details**.

---

## **Where is the Servant Pattern Used?**
- **Utility Services** – Shared helper methods (e.g., formatting, encryption, validation).
- **File & Data Processing** – Handling CSV, JSON, and XML transformations.
- **Logging & Auditing** – A centralized way to log system activity.
- **Security & Access Control** – Managing permissions and authentication logic.
- **Object Conversions** – Transforming DTOs into entities and vice versa.

---

## **Implementing Servant Pattern in Java**

### **1. Define the Client Classes (Objects Requiring a Common Behavior)**
```java
public class Document {
    private String name;
    
    public Document(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
```
```java
public class Image {
    private String fileName;
    
    public Image(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }
}
```

---

### **2. Implement the Servant Class (Common Behavior for Unrelated Objects)**
```java
public class FileProcessor {
    public void printFileDetails(Document doc) {
        System.out.println("Processing document: " + doc.getName());
    }

    public void printFileDetails(Image img) {
        System.out.println("Processing image: " + img.getFileName());
    }
}
```

---

### **3. Using the Servant Pattern**
```java
public class ServantPatternDemo {
    public static void main(String[] args) {
        Document doc = new Document("Report.pdf");
        Image img = new Image("Profile.png");

        FileProcessor processor = new FileProcessor();
        processor.printFileDetails(doc);
        processor.printFileDetails(img);
    }
}
```

---

### **Output:**
```
Processing document: Report.pdf
Processing image: Profile.png
```

✔ **Same Functionality Shared by Different Object Types Without Inheritance**.  
✔ **Code Duplication Avoided by Centralizing Logic in `FileProcessor`**.

---

## **Servant Pattern in Spring Boot (Utility Service for Data Validation & Logging)**

Spring Boot applications often need **common business logic across multiple services**, such as **validating data** or **logging actions**.

---

### **1. Define the Client Classes (Entities Requiring Common Logic)**
```java
public class User {
    private String name;
    private String email;

    public User(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }
}
```
```java
public class Order {
    private String orderId;
    private double amount;

    public Order(String orderId, double amount) {
        this.orderId = orderId;
        this.amount = amount;
    }

    public String getOrderId() {
        return orderId;
    }

    public double getAmount() {
        return amount;
    }
}
```

---

### **2. Implement the Servant Class (Utility Service for Validation & Logging)**
```java
import org.springframework.stereotype.Service;

@Service
public class ValidationService {
    
    public boolean validateUser(User user) {
        if (user.getName() == null || user.getName().isEmpty()) {
            System.out.println("[ERROR] Invalid User: Name is required");
            return false;
        }
        if (user.getEmail() == null || !user.getEmail().contains("@")) {
            System.out.println("[ERROR] Invalid User: Email is incorrect");
            return false;
        }
        System.out.println("[INFO] User is valid: " + user.getName());
        return true;
    }

    public boolean validateOrder(Order order) {
        if (order.getAmount() <= 0) {
            System.out.println("[ERROR] Invalid Order: Amount must be positive");
            return false;
        }
        System.out.println("[INFO] Order is valid: " + order.getOrderId());
        return true;
    }
}
```

---

### **3. Create Services That Use the Servant (Shared Validation Service)**
```java
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final ValidationService validationService;

    public UserService(ValidationService validationService) {
        this.validationService = validationService;
    }

    public String registerUser(User user) {
        if (validationService.validateUser(user)) {
            return "User registered: " + user.getName();
        }
        return "User registration failed.";
    }
}
```
```java
import org.springframework.stereotype.Service;

@Service
public class OrderService {
    private final ValidationService validationService;

    public OrderService(ValidationService validationService) {
        this.validationService = validationService;
    }

    public String placeOrder(Order order) {
        if (validationService.validateOrder(order)) {
            return "Order placed: " + order.getOrderId();
        }
        return "Order failed.";
    }
}
```

---

### **4. Expose an API to Test the Servant Pattern**
```java
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class AppController {
    private final UserService userService;
    private final OrderService orderService;

    public AppController(UserService userService, OrderService orderService) {
        this.userService = userService;
        this.orderService = orderService;
    }

    @PostMapping("/user")
    public String registerUser(@RequestParam String name, @RequestParam String email) {
        return userService.registerUser(new User(name, email));
    }

    @PostMapping("/order")
    public String placeOrder(@RequestParam String orderId, @RequestParam double amount) {
        return orderService.placeOrder(new Order(orderId, amount));
    }
}
```

---

### **5. Testing the API**
#### **Register Valid User**
```bash
curl -X POST "http://localhost:8080/api/user?name=Alice&email=alice@example.com"
```
#### **Response:**
```
[INFO] User is valid: Alice
User registered: Alice
```

#### **Register Invalid User**
```bash
curl -X POST "http://localhost:8080/api/user?name=Alice&email=invalidEmail"
```
#### **Response:**
```
[ERROR] Invalid User: Email is incorrect
User registration failed.
```

✔ **Validation Logic Centralized in `ValidationService`**.  
✔ **Code Duplication Avoided Across `UserService` and `OrderService`**.

---

## **Advantages of Using the Servant Pattern in Spring Boot**
| Feature | Benefit |
|---------|---------|
| **Reduces Code Duplication** | Shared functionality in one class |
| **Encourages Code Reuse** | Multiple classes benefit from a single servant |
| **Improves Maintainability** | Updating logic happens in one place |
| **Works Well with Spring Boot** | Ideal for utility services and cross-cutting concerns |

---

## **Conclusion**
The **Servant Pattern** is a great approach to **centralize reusable logic** in **Spring Boot applications**, making code **cleaner, more reusable, and easier to maintain**.

### **Next Steps**
- Extend the pattern for **file processing services**.
- Implement a **security servant for authentication checks**.
- Use it in **Spring Boot request validation mechanisms**.  