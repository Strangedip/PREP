# **Complete Design Patterns Learning Guide**  

Here is a **comprehensive collection** of design patterns with practical explanations and **real-world applications**:  

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
✅ **Saves an object's state to restore it later (undo functionality).**  
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

## **Learning Summary**
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

This **comprehensive guide** covers essential design patterns for building maintainable, scalable, and robust software systems. Each pattern addresses specific design challenges and provides proven solutions for common software development problems.

## Java & Spring Boot Learning: Dependency Injection in Spring Boot

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

## Java & Spring Boot Learning: **Spring Boot Profiles**

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

---

[Content continues with detailed pattern explanations and Spring Boot concepts...]