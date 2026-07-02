# AI Agents & Agentic Workflows — The 2025-2026 Frontier

> **Goal**: Understand AI agents, tool use, the Model Context Protocol (MCP), and how to build agentic systems with Java/Spring Boot.
> **Level**: Senior through Lead — This is the cutting edge.

> **You are here**: Tech Lead — AI / ML
> **Roadmap**: [Developer Master Roadmap](../ROADMAP.md) | **Prerequisites**: [05_Spring_AI.md](05_Spring_AI.md) | **Next**: [07_AI_Powered_Dev_Tools.md](07_AI_Powered_Dev_Tools.md)

---

## 1. What is an AI Agent?

An AI agent is an AI system that can:
1. **Perceive** — Receive input from users, APIs, or the environment
2. **Reason** — Analyze the situation and form a plan
3. **Act** — Execute actions using tools (APIs, databases, code execution)
4. **Observe** — See the results of its actions
5. **Iterate** — Decide if more actions are needed, and repeat

**Key difference from basic LLM calls**: A simple LLM call is input → output. An agent is input → plan → action → observe → re-plan → action → ... → final output.

### 1.1 Agent vs. Chatbot vs. RAG

| Feature | Basic Chatbot | RAG System | AI Agent |
|---------|--------------|------------|----------|
| **Input** | User message | User message | User message + environment state |
| **Knowledge** | Model training only | Retrieved documents | Tools + APIs + documents |
| **Actions** | Generate text | Generate text from context | Call APIs, write files, execute code |
| **Planning** | None | None | Multi-step reasoning |
| **Autonomy** | None | None | Can decide what to do next |
| **Loops** | Single turn | Single turn | Multi-turn (reason → act → observe) |
| **Example** | "What is Java?" | "Search docs about Java features" | "Find the bug, read the logs, write a fix, create a PR" |

### 1.2 The ReAct Pattern (Reasoning + Acting)

The foundational pattern for AI agents. The model alternates between thinking and acting.

```
USER: "Our checkout service has been throwing 500 errors since this morning."

THOUGHT 1: I need to check the error logs for the checkout service to understand the errors.
ACTION 1: search_logs(service="checkout", level="ERROR", since="8 hours ago")
OBSERVATION 1: Found 1,247 errors: "PaymentGatewayException: Connection timeout to payment.provider.com:443"

THOUGHT 2: The checkout service can't reach the payment provider. This could be a network issue,
a DNS issue, or the payment provider is down. Let me check from our side first.
ACTION 2: check_health(service="payment-gateway-proxy")
OBSERVATION 2: Service is UP, latency to payment.provider.com: 15,234ms (normal: <200ms)

THOUGHT 3: The latency is extremely high. Let me check if there's a network change or provider issue.
ACTION 3: get_recent_changes(category="network", since="24 hours ago")
OBSERVATION 3: Network change at 6:00 AM: "Updated firewall rules for PCI compliance - ticket NET-4521"

THOUGHT 4: The firewall change is likely blocking or throttling connections to the payment provider.
ACTION 4: get_firewall_rule(ticket="NET-4521")
OBSERVATION 4: New rule: Block all outbound traffic on port 443 except whitelisted IPs. payment.provider.com IP not in whitelist.

ANSWER: The checkout 500 errors are caused by a firewall change (NET-4521) deployed at 6 AM.
The new PCI compliance rules block outbound HTTPS traffic, and the payment provider's IP
(payment.provider.com) was not added to the whitelist. To fix:
1. Add payment.provider.com to the outbound whitelist in NET-4521
2. Verify connectivity after the change
3. Monitor error rates for 30 minutes
```

---

## 2. Building Agents with Spring AI

### 2.1 Basic Agent with Function Calling

```java
@Service
public class DevOpsAgent {

    private final ChatClient chatClient;

    public DevOpsAgent(ChatClient.Builder builder) {
        this.chatClient = builder
            .defaultSystem("""
                You are a DevOps agent that helps diagnose production issues.
                You have access to monitoring tools. Use them to investigate issues.
                Always explain your reasoning before taking an action.
                After gathering enough information, provide a clear diagnosis and recommended fix.
                """)
            .defaultFunctions(
                "searchLogs",
                "getMetrics",
                "checkServiceHealth",
                "getRecentDeployments",
                "getRecentConfigChanges",
                "getAlerts"
            )
            .build();
    }

    public String diagnoseIssue(String issueDescription) {
        return chatClient.prompt()
            .user(issueDescription)
            .call()
            .content();
    }

    // Function definitions
    @Bean
    @Description("Search application logs by service name, log level, and time range")
    public Function<LogSearchRequest, List<LogEntry>> searchLogs(LogService logService) {
        return req -> logService.search(req.service(), req.level(), req.since());
    }

    @Bean
    @Description("Get metrics (CPU, memory, error rate, latency) for a service")
    public Function<MetricsRequest, ServiceMetrics> getMetrics(MetricsService metricsService) {
        return req -> metricsService.getMetrics(req.service(), req.metric(), req.timeRange());
    }

    @Bean
    @Description("Check the health status and connectivity of a service")
    public Function<HealthCheckRequest, HealthStatus> checkServiceHealth(HealthService healthService) {
        return req -> healthService.checkHealth(req.service());
    }

    @Bean
    @Description("Get recent deployments to a service within a time range")
    public Function<DeploymentRequest, List<Deployment>> getRecentDeployments(
            DeploymentService deploymentService) {
        return req -> deploymentService.getRecent(req.service(), req.since());
    }
}
```

### 2.2 Multi-Step Agent with Memory

```java
@Service
public class ResearchAgent {

    private final ChatClient chatClient;
    private final ChatMemory chatMemory;

    public ResearchAgent(ChatClient.Builder builder) {
        this.chatMemory = new InMemoryChatMemory();
        this.chatClient = builder
            .defaultSystem("""
                You are a research agent. Given a topic, you:
                1. Break it into sub-questions
                2. Research each sub-question using available tools
                3. Synthesize findings into a comprehensive report

                Think step by step. After each tool call, assess whether you have enough
                information or need to dig deeper.
                """)
            .defaultAdvisors(
                new MessageChatMemoryAdvisor(chatMemory, "research-session", 50)
            )
            .defaultFunctions("searchWeb", "searchDocumentation", "analyzeData")
            .build();
    }

    public String research(String topic) {
        // Phase 1: Plan
        String plan = chatClient.prompt()
            .user("""
                Research topic: %s

                First, create a research plan:
                1. What are the key questions to answer?
                2. What sources should be consulted?
                3. What is the expected structure of the final report?
                """.formatted(topic))
            .call()
            .content();

        // Phase 2: Execute (the agent will use tools autonomously)
        String research = chatClient.prompt()
            .user("""
                Now execute the research plan you created. Use the available tools to
                gather information for each question. Be thorough.
                """)
            .call()
            .content();

        // Phase 3: Synthesize
        return chatClient.prompt()
            .user("""
                Based on all the research gathered, create a comprehensive report with:
                - Executive Summary
                - Key Findings (with evidence)
                - Recommendations
                - Areas that need further investigation
                """)
            .call()
            .content();
    }
}
```

---

## 3. Model Context Protocol (MCP) — The Universal Tool Standard

### 3.1 What is MCP?

MCP (Model Context Protocol) is an open standard (created by Anthropic in late 2024) that defines how AI models connect to external tools and data sources. Think of it as **"USB for AI"** — a universal plug that any AI model can use to connect to any tool.

### 3.2 MCP Architecture

```
┌────────────────────┐     ┌─────────────────┐     ┌──────────────────┐
│  MCP Client        │     │  MCP Server      │     │  External System │
│  (AI Application)  │◄───►│  (Tool Provider)  │◄───►│  (Database, API) │
│                    │JSON │                   │     │                  │
│  - Spring AI       │RPC  │  - Exposes tools  │     │  - PostgreSQL    │
│  - Cursor IDE      │     │  - Handles calls  │     │  - JIRA API      │
│  - Claude Desktop  │     │  - Returns results│     │  - GitHub API    │
└────────────────────┘     └─────────────────┘     └──────────────────┘
```

**MCP defines three types of capabilities:**

| Capability | Description | Example |
|-----------|-------------|---------|
| **Tools** | Functions the AI can call | `search_database`, `send_email`, `create_ticket` |
| **Resources** | Data the AI can read | Configuration files, database schemas, documentation |
| **Prompts** | Pre-built prompt templates | "Summarize this PR", "Review this code" |

### 3.3 Building an MCP Server with Java

```java
// An MCP server that exposes your application's APIs as tools
@SpringBootApplication
public class MyMCPServer {

    public static void main(String[] args) {
        SpringApplication.run(MyMCPServer.class, args);
    }
}

@Configuration
public class MCPToolsConfig {

    // Register tools that AI models can call
    @Bean
    public McpServer mcpServer(
            OrderService orderService,
            CustomerService customerService,
            InventoryService inventoryService) {

        return McpServer.builder()
            .name("ecommerce-tools")
            .version("1.0.0")

            // Tool 1: Look up order details
            .tool(Tool.builder()
                .name("get_order")
                .description("Get order details by order ID. Returns order status, items, and shipping info.")
                .inputSchema("""
                    {
                        "type": "object",
                        "properties": {
                            "order_id": { "type": "string", "description": "The order ID (e.g., ORD-12345)" }
                        },
                        "required": ["order_id"]
                    }
                    """)
                .handler(args -> {
                    String orderId = (String) args.get("order_id");
                    OrderDTO order = orderService.getOrder(orderId);
                    return objectMapper.writeValueAsString(order);
                })
                .build())

            // Tool 2: Search customers
            .tool(Tool.builder()
                .name("search_customers")
                .description("Search customers by email, name, or phone number.")
                .inputSchema("""
                    {
                        "type": "object",
                        "properties": {
                            "query": { "type": "string", "description": "Search query (email, name, or phone)" },
                            "limit": { "type": "integer", "description": "Max results (default 10)", "default": 10 }
                        },
                        "required": ["query"]
                    }
                    """)
                .handler(args -> {
                    String query = (String) args.get("query");
                    int limit = (int) args.getOrDefault("limit", 10);
                    List<CustomerDTO> customers = customerService.search(query, limit);
                    return objectMapper.writeValueAsString(customers);
                })
                .build())

            // Tool 3: Check inventory
            .tool(Tool.builder()
                .name("check_inventory")
                .description("Check current inventory level for a product SKU.")
                .inputSchema("""
                    {
                        "type": "object",
                        "properties": {
                            "sku": { "type": "string", "description": "Product SKU" },
                            "warehouse": { "type": "string", "description": "Warehouse code (optional)" }
                        },
                        "required": ["sku"]
                    }
                    """)
                .handler(args -> {
                    String sku = (String) args.get("sku");
                    String warehouse = (String) args.get("warehouse");
                    InventoryDTO inventory = inventoryService.check(sku, warehouse);
                    return objectMapper.writeValueAsString(inventory);
                })
                .build())

            // Resource: Expose product catalog schema
            .resource(Resource.builder()
                .uri("schema://product-catalog")
                .name("Product Catalog Schema")
                .description("The database schema for the product catalog")
                .mimeType("application/json")
                .handler(() -> productCatalogSchema)
                .build())

            .build();
    }
}
```

### 3.4 Consuming MCP Tools from Spring AI

```java
@Configuration
public class MCPClientConfig {

    @Bean
    public McpClient ecommerceTools() {
        return McpClient.builder()
            .transport(new StdioTransport("java", "-jar", "ecommerce-mcp-server.jar"))
            .build();
    }
}

@Service
public class AIAssistantWithMCP {

    private final ChatClient chatClient;

    public AIAssistantWithMCP(ChatClient.Builder builder, McpClient mcpClient) {
        // MCP tools are automatically registered as Spring AI functions
        this.chatClient = builder
            .defaultSystem("You are a customer service assistant with access to our e-commerce system.")
            .defaultTools(mcpClient.tools()) // All MCP tools available
            .build();
    }

    public String handleCustomerQuery(String query) {
        return chatClient.prompt()
            .user(query)
            .call()
            .content();
    }
}
```

---

## 4. Agent Patterns

### 4.1 The Router Agent

Routes requests to specialized sub-agents.

```java
@Service
public class RouterAgent {

    private final Map<String, ChatClient> specializedAgents;

    public RouterAgent(ChatClient.Builder builder,
                       VectorStore techDocsStore,
                       VectorStore hrDocsStore) {
        // Create specialized agents
        this.specializedAgents = Map.of(
            "technical", builder.clone()
                .defaultSystem("You are a technical support agent.")
                .defaultAdvisors(new QuestionAnswerAdvisor(techDocsStore))
                .defaultFunctions("searchLogs", "checkHealth")
                .build(),
            "hr", builder.clone()
                .defaultSystem("You are an HR policy assistant.")
                .defaultAdvisors(new QuestionAnswerAdvisor(hrDocsStore))
                .build(),
            "billing", builder.clone()
                .defaultSystem("You are a billing support agent.")
                .defaultFunctions("getInvoice", "processRefund")
                .build()
        );
    }

    public String route(String query) {
        // Step 1: Classify the query
        String category = classifyQuery(query);

        // Step 2: Route to the appropriate agent
        ChatClient agent = specializedAgents.getOrDefault(category,
            specializedAgents.get("technical")); // Default to technical

        return agent.prompt()
            .user(query)
            .call()
            .content();
    }

    private String classifyQuery(String query) {
        return chatClient.prompt()
            .system("""
                Classify this query into one of: technical, hr, billing.
                Respond with ONLY the category name.
                """)
            .user(query)
            .call()
            .content()
            .trim()
            .toLowerCase();
    }
}
```

### 4.2 The Planner Agent (Plan and Execute)

```java
@Service
public class PlannerAgent {

    private final ChatClient planner;
    private final ChatClient executor;

    public String execute(String task) {
        // Phase 1: Create a plan
        Plan plan = planner.prompt()
            .system("""
                You are a task planner. Break down the task into ordered steps.
                Each step should be atomic and actionable.
                Identify which tools are needed for each step.
                """)
            .user(task)
            .call()
            .entity(Plan.class);

        // Phase 2: Execute each step
        StringBuilder results = new StringBuilder();
        for (Step step : plan.steps()) {
            String stepResult = executor.prompt()
                .system("Execute this step. Use tools as needed. Report what you did and what you found.")
                .user("""
                    Task: %s
                    Current step: %s
                    Previous results: %s
                    """.formatted(task, step.description(), results.toString()))
                .call()
                .content();

            results.append("\nStep ").append(step.order())
                   .append(": ").append(stepResult);
        }

        // Phase 3: Synthesize results
        return planner.prompt()
            .user("""
                Original task: %s
                Execution results: %s
                Provide a final summary and answer.
                """.formatted(task, results.toString()))
            .call()
            .content();
    }

    record Plan(List<Step> steps) {}
    record Step(int order, String description, List<String> tools) {}
}
```

### 4.3 The Critic Agent (Self-Reflection)

```java
@Service
public class CriticAgent {

    private final ChatClient generator;
    private final ChatClient critic;
    private static final int MAX_ITERATIONS = 3;

    public String generateWithQualityControl(String task) {
        String currentOutput = null;

        for (int i = 0; i < MAX_ITERATIONS; i++) {
            // Generate (or improve)
            if (currentOutput == null) {
                currentOutput = generator.prompt()
                    .user(task)
                    .call()
                    .content();
            } else {
                currentOutput = generator.prompt()
                    .user("""
                        Original task: %s
                        Previous output: %s
                        Feedback: %s
                        Improve the output based on the feedback.
                        """.formatted(task, currentOutput, feedback))
                    .call()
                    .content();
            }

            // Critique
            CritiqueResult critique = critic.prompt()
                .system("""
                    You are a quality reviewer. Evaluate the output for:
                    1. Correctness
                    2. Completeness
                    3. Clarity
                    Score each 1-10. If ALL scores are >= 8, set approved = true.
                    """)
                .user("Task: " + task + "\nOutput: " + currentOutput)
                .call()
                .entity(CritiqueResult.class);

            if (critique.approved()) {
                return currentOutput; // Quality is good
            }

            String feedback = critique.feedback();
            log.info("Iteration {}: Not approved. Feedback: {}", i + 1, feedback);
        }

        return currentOutput; // Return best effort after max iterations
    }

    record CritiqueResult(boolean approved, int correctness, int completeness,
                          int clarity, String feedback) {}
}
```

---

## 5. Multi-Agent Systems

### 5.1 Conversational Multi-Agent (Debate Pattern)

```java
@Service
public class DebateAgent {

    private final ChatClient proponent;
    private final ChatClient opponent;
    private final ChatClient judge;

    // Two agents debate, a judge picks the winner
    public DebateResult debate(String topic) {
        String proArgument = proponent.prompt()
            .system("You are arguing IN FAVOR of the following position. Be persuasive and cite evidence.")
            .user(topic)
            .call()
            .content();

        String conArgument = opponent.prompt()
            .system("You are arguing AGAINST the following position. Be persuasive and cite evidence.")
            .user(topic + "\n\nThe opposing argument is:\n" + proArgument)
            .call()
            .content();

        // Rebuttal round
        String proRebuttal = proponent.prompt()
            .user("Rebut this argument:\n" + conArgument)
            .call()
            .content();

        String conRebuttal = opponent.prompt()
            .user("Rebut this argument:\n" + proRebuttal)
            .call()
            .content();

        // Judge synthesizes
        return judge.prompt()
            .system("""
                You are an impartial judge. Evaluate both sides of the debate.
                Consider the strength of arguments, evidence cited, and logical consistency.
                Provide a balanced conclusion.
                """)
            .user("""
                Topic: %s
                For: %s
                Rebuttal: %s
                Against: %s
                Rebuttal: %s
                """.formatted(topic, proArgument, proRebuttal, conArgument, conRebuttal))
            .call()
            .entity(DebateResult.class);
    }

    record DebateResult(String winner, String reasoning, String conclusion,
                        List<String> keyInsights) {}
}
```

---

## 6. Agent Safety and Control

### 6.1 The Principle of Least Privilege

```java
@Service
public class SafeAgent {

    // RULE 1: Agents should only have the tools they need
    public ChatClient createAgentWithScope(String scope) {
        return switch (scope) {
            case "read-only" -> chatClientBuilder.build()
                .mutate()
                .defaultFunctions("searchLogs", "getMetrics", "getConfig") // Read-only tools
                .build();
            case "operator" -> chatClientBuilder.build()
                .mutate()
                .defaultFunctions("searchLogs", "getMetrics", "restartService", "scaleService")
                .build();
            case "admin" -> chatClientBuilder.build()
                .mutate()
                .defaultFunctions("searchLogs", "getMetrics", "restartService",
                    "scaleService", "deployService", "modifyConfig")
                .build();
            default -> throw new IllegalArgumentException("Unknown scope: " + scope);
        };
    }

    // RULE 2: Human-in-the-loop for destructive actions
    public AgentAction proposeAction(String diagnosis) {
        AgentAction action = chatClient.prompt()
            .user("Based on this diagnosis, what action should we take?\n" + diagnosis)
            .call()
            .entity(AgentAction.class);

        if (action.isDestructive()) {
            // Don't auto-execute — return for human approval
            action.setStatus("PENDING_APPROVAL");
            actionRepository.save(action);
            notificationService.notifyOncall(
                "AI Agent proposes: " + action.description() + ". Approve?"
            );
        } else {
            // Safe to auto-execute
            executeAction(action);
            action.setStatus("EXECUTED");
        }

        return action;
    }

    // RULE 3: Rate limiting and budget controls
    private final RateLimiter agentRateLimiter = RateLimiter.create(10); // 10 tool calls per second
    private final AtomicInteger dailyToolCalls = new AtomicInteger(0);
    private static final int DAILY_LIMIT = 1000;

    public void executeToolCall(String tool, Map<String, Object> args) {
        if (!agentRateLimiter.tryAcquire()) {
            throw new RateLimitExceededException("Agent rate limit exceeded");
        }
        if (dailyToolCalls.incrementAndGet() > DAILY_LIMIT) {
            throw new BudgetExceededException("Daily tool call limit reached");
        }
        // Execute the tool call
    }
}
```

---

## 7. Interview Questions — AI Agents

| Question | Key Points |
|----------|-----------|
| "What is an AI agent?" | AI system that can reason, plan, and use tools autonomously in a loop |
| "How does ReAct work?" | Thought → Action → Observation loop until the task is complete |
| "What is MCP?" | Model Context Protocol — universal standard for AI tool connections |
| "How do you prevent an agent from going rogue?" | Least privilege, rate limits, budget caps, human-in-the-loop for destructive actions |
| "Multi-agent vs. single agent?" | Multi-agent for complex tasks with different expertise areas, single for focused tasks |
| "How do you test an agent?" | Mock tool calls, predefined scenarios, deterministic seeds, evaluation metrics |
| "What about agent hallucinations?" | Ground in tool results, validate outputs, use structured output, add guardrails |

---

**Next**: [07_AI_Powered_Dev_Tools.md](07_AI_Powered_Dev_Tools.md) — The tools that make you 10x more productive as a developer.

