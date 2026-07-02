# AI-Powered Development Tools — The 2026 Developer Toolkit

> **Goal**: Master the AI tools that 10x your productivity as a developer. Know what exists, how to use them effectively, and what interviewers ask about them.
> **Level**: ALL (Associate through Lead)

> **You are here**: SDE1 — AI / ML
> **Roadmap**: [Developer Master Roadmap](../ROADMAP.md) | **Prerequisites**: [01_AI_Fundamentals.md](01_AI_Fundamentals.md) | **Next**: [08_AI_in_Frontend.md](08_AI_in_Frontend.md)

---

## 1. The AI-Powered Development Landscape

### 1.1 Categories of AI Dev Tools (2026)

| Category | Tools | What They Do |
|----------|-------|-------------|
| **AI Code Completion** | GitHub Copilot, Cursor Tab, Codeium, Tabnine, Amazon CodeWhisperer | Real-time code suggestions as you type |
| **AI IDE / Code Editor** | Cursor, Windsurf (Codeium), JetBrains AI | Full IDE with AI deeply integrated (chat, edit, multi-file) |
| **AI Chat (Code)** | ChatGPT, Claude, Gemini | Conversational coding assistance (not inline) |
| **AI Code Review** | CodeRabbit, Codium PR-Agent, Qodo | Automated PR reviews, bug detection |
| **AI Testing** | Diffblue Cover, Codium, CodiumAI | Automated test generation |
| **AI Documentation** | Mintlify, Swimm, Stenography | Auto-generate docs from code |
| **AI Debugging** | Cursor (chat), Claude, Sentry AI | Root cause analysis, fix suggestions |
| **AI DevOps** | Kubecost AI, Datadog AI, PagerDuty AI | Incident diagnosis, cost optimization |
| **AI Database** | AI2SQL, DataGrip AI, Vanna | Natural language to SQL |
| **AI API Design** | Speakeasy, Apidog AI | Generate SDKs, API docs, tests from specs |

### 1.2 Tool Adoption by Team Size

| Team Size | Recommended Stack | Investment |
|-----------|-------------------|------------|
| **Solo/Startup** | Cursor (Pro) + ChatGPT/Claude | ~$40/month |
| **Small Team (2-10)** | Cursor Business + GitHub Copilot Business | ~$40-60/seat/month |
| **Enterprise (10+)** | GitHub Copilot Enterprise + JetBrains AI + CodeRabbit | ~$50-100/seat/month |

---

## 2. GitHub Copilot — The Industry Standard

### 2.1 What GitHub Copilot Does

| Feature | Description | Shortcut |
|---------|-------------|----------|
| **Inline Completion** | Suggests code as you type (ghost text) | Tab to accept |
| **Copilot Chat** | Conversational AI in the IDE | Ctrl+I (inline), Ctrl+Shift+I (panel) |
| **Code Explanation** | Explain selected code | Select → "Explain this" |
| **Fix Suggestion** | Suggest fixes for errors | Click lightbulb on error |
| **Test Generation** | Generate tests for selected code | /tests command |
| **Documentation** | Generate docs/comments | /doc command |
| **Commit Messages** | Auto-generate commit messages | Copilot icon in Git panel |
| **PR Summaries** | Summarize PR changes | GitHub PR page |
| **CLI Suggestions** | Terminal command suggestions | `gh copilot suggest` |

### 2.2 Copilot Chat Commands

```
/explain    — Explain selected code
/fix        — Suggest a fix for selected code
/tests      — Generate tests for selected code
/doc        — Generate documentation
/optimize   — Suggest performance optimizations
/clear      — Clear chat history
@workspace  — Reference the entire workspace context
@vscode     — VS Code-specific questions
@terminal   — Terminal/CLI context
#file       — Reference a specific file
#selection  — Reference selected code
```

### 2.3 Effective Copilot Prompting for Code

**The Context Pyramid (What Copilot Sees):**
```
Priority 1: Current file content (especially code near cursor)
Priority 2: Open tabs (other files in your editor)
Priority 3: File names and structure in your workspace
Priority 4: Language and framework (from file extensions, imports)
```

**Tip 1: Write clear comments before code**
```java
// Create a REST endpoint that accepts a JSON body with "email" and "password",
// validates the input, authenticates against the database using BCrypt,
// generates a JWT token with 1-hour expiry, and returns it in the response header.
@PostMapping("/login")
// Copilot will now generate the full implementation with high accuracy
```

**Tip 2: Write function signatures with descriptive names and types**
```java
/**
 * Finds all orders placed in the last N days that have not been shipped,
 * groups them by warehouse, and returns the count per warehouse sorted descending.
 */
public Map<String, Long> getUnshippedOrderCountByWarehouse(int days) {
    // Copilot will generate the implementation
}
```

**Tip 3: Provide examples in tests**
```java
@Test
void shouldParseCronExpression() {
    // "0 30 9 * * MON-FRI" = 9:30 AM every weekday
    assertThat(CronParser.parse("0 30 9 * * MON-FRI").getNextExecution())
        .isEqualTo(/* next weekday at 9:30 */);

    // "0 0 0 1 * *" = midnight on the first of every month
    assertThat(CronParser.parse("0 0 0 1 * *").getNextExecution())
        .isEqualTo(/* first of next month at midnight */);
    // Copilot learns the pattern from your examples
}
```

### 2.4 Copilot Best Practices

| Do | Do Not |
|----|--------|
| Write clear comments describing what you want | Accept suggestions blindly without reading |
| Use descriptive variable and function names | Rely on Copilot for complex business logic |
| Review generated code for correctness | Paste sensitive data (API keys, passwords) |
| Use for boilerplate, patterns, and repetitive code | Trust Copilot for security-critical code |
| Keep relevant files open in tabs for context | Assume generated code is tested |
| Use /tests to generate test scaffolding | Use Copilot as a replacement for understanding |

---

## 3. Cursor — The AI-First IDE

### 3.1 What Makes Cursor Different

Cursor is a fork of VS Code rebuilt around AI. Key differences from VS Code + Copilot:

| Feature | VS Code + Copilot | Cursor |
|---------|-------------------|--------|
| **Inline completion** | Yes | Yes (Cursor Tab — smarter, predicts edits) |
| **Chat** | Side panel only | Inline (Ctrl+K), panel, and composer |
| **Multi-file edit** | No | Yes (Composer mode — edits multiple files) |
| **Codebase awareness** | Limited (open tabs) | Full codebase indexing (semantic search) |
| **@ references** | @workspace, #file | @file, @folder, @codebase, @docs, @web, @git |
| **Agent mode** | No | Yes (Composer Agent — plans and executes) |
| **Custom rules** | Limited | .cursorrules file for project-specific context |
| **Model choice** | GPT-4o only | GPT-4o, Claude 3.5 Sonnet, Gemini, custom |
| **MCP support** | No | Yes (connect to external tools) |
| **Terminal AI** | Basic | Full terminal integration with AI |

### 3.2 Cursor Key Features

**Cursor Tab (Smart Autocomplete):**
- Predicts your next edit, not just the next line
- Can suggest multi-line changes
- Understands your editing patterns (if you renamed one variable, it suggests renaming similar ones)

**Cmd+K (Inline Edit):**
```
// Select code → Cmd+K → "Add error handling for null input and empty list"
// Cursor modifies the selected code in-place with AI
```

**Composer (Multi-File Agent):**
```
// Open Composer → Type: "Add a new REST endpoint for user registration
// that validates the input, checks for duplicate emails, saves to the database,
// sends a welcome email, and returns the created user."
//
// Cursor will:
// 1. Create/modify the Controller
// 2. Create/modify the Service
// 3. Create/modify the Repository
// 4. Create the DTO
// 5. Add email sending logic
// All in one operation, across multiple files
```

**@Codebase (Semantic Search):**
```
// In chat: "@codebase how is authentication implemented in this project?"
// Cursor searches your entire codebase semantically and provides a comprehensive answer
```

**@Docs (Documentation Reference):**
```
// In chat: "@docs spring-ai how do I configure pgvector?"
// Cursor searches the Spring AI documentation and provides the answer
```

### 3.3 .cursorrules File

Create a `.cursorrules` file in your project root to give Cursor persistent context.

```
# .cursorrules

## Project Context
This is a Java 21 Spring Boot 3.3 application with Angular 17 frontend.
We use PostgreSQL with pgvector for vector search.
The project follows Clean Architecture with DDD patterns.

## Coding Standards
- Use Java records for DTOs and value objects
- Use constructor injection (no @Autowired on fields)
- All REST endpoints return ResponseEntity
- Use problem details (RFC 7807) for error responses
- All services must have corresponding unit tests
- Use Testcontainers for integration tests

## Architecture Rules
- Domain layer has NO Spring dependencies
- Application services orchestrate domain objects
- Infrastructure layer implements repository interfaces
- Controllers are thin — delegate to services immediately

## Naming Conventions
- Entities: User, Order, Product (no suffix)
- DTOs: UserDTO, CreateOrderRequest, OrderResponse
- Services: UserService, OrderService
- Repositories: UserRepository (Spring Data JPA)
- Controllers: UserController

## Testing
- Unit tests: JUnit 5 + Mockito
- Integration tests: @SpringBootTest + Testcontainers
- API tests: MockMvc with JSON assertions

## Dependencies
- Spring Boot 3.3
- Spring AI 1.0
- Spring Security with OAuth2
- Flyway for migrations
- MapStruct for DTO mapping
```

---

## 4. AI Code Review Tools

### 4.1 CodeRabbit

Automated AI code review on every PR. Integrates with GitHub/GitLab.

**What it reviews:**
- Code quality and best practices
- Potential bugs and edge cases
- Security vulnerabilities
- Performance issues
- Test coverage gaps
- Documentation completeness

**Configuration (.coderabbit.yaml):**
```yaml
reviews:
  auto_review:
    enabled: true
    language: en
  review_comment:
    style: concise
  path_filters:
    exclude:
      - "**/*.generated.*"
      - "**/test/**"
  profile: assertive  # Options: chill, assertive, nitpicky
```

### 4.2 AI-Assisted Testing

**Diffblue Cover (Java-specific):**
- Automatically generates JUnit tests for Java code
- Achieves 60-80% code coverage automatically
- Integrates with CI/CD pipelines
- Understands Spring Boot, Hibernate, and standard Java patterns

```bash
# Generate tests for a specific class
dcover create --class com.example.service.OrderService

# Generate tests for entire package
dcover create --package com.example.service

# Generate tests with specific coverage target
dcover create --class com.example.service.OrderService --coverage 80
```

---

## 5. AI in the Software Development Lifecycle

### 5.1 AI at Every Stage

```
┌──────────────────────────────────────────────────────────────────┐
│                    SOFTWARE DEVELOPMENT LIFECYCLE                 │
│                                                                  │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐       │
│  │  PLAN    │→ │  CODE    │→ │  TEST    │→ │  DEPLOY  │→ ...  │
│  │          │  │          │  │          │  │          │       │
│  │ AI:      │  │ AI:      │  │ AI:      │  │ AI:      │       │
│  │ ○ Story  │  │ ○ Copilot│  │ ○ Test   │  │ ○ PR     │       │
│  │   writing│  │ ○ Cursor │  │   gen    │  │   summary│       │
│  │ ○ Task   │  │ ○ Code   │  │ ○ Test   │  │ ○ Release│       │
│  │   breakdn│  │   review │  │   review │  │   notes  │       │
│  │ ○ Estimtn│  │ ○ Refactr│  │ ○ Bug    │  │ ○ Incident│      │
│  │ ○ Arch   │  │ ○ Debug  │  │   detect │  │   diagns │       │
│  │   design │  │          │  │          │  │          │       │
│  └──────────┘  └──────────┘  └──────────┘  └──────────┘       │
│                                                                  │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐                      │
│  │ MONITOR  │→ │ MAINTAIN │→ │ LEARN    │                      │
│  │          │  │          │  │          │                      │
│  │ AI:      │  │ AI:      │  │ AI:      │                      │
│  │ ○ Anomaly│  │ ○ Dep    │  │ ○ Code   │                      │
│  │   detect │  │   updates│  │   explain │                      │
│  │ ○ Root   │  │ ○ Tech   │  │ ○ Docs   │                      │
│  │   cause  │  │   debt   │  │   gen    │                      │
│  │ ○ Alert  │  │   detect │  │ ○ Onboard│                      │
│  │   mgmt   │  │          │  │   assist │                      │
│  └──────────┘  └──────────┘  └──────────┘                      │
└──────────────────────────────────────────────────────────────────┘
```

### 5.2 AI-Assisted Code Review Workflow

```
Developer pushes PR
    │
    ▼
┌─────────────────────────┐
│ Automated Checks        │
│ ○ CI/CD pipeline        │
│ ○ CodeRabbit AI review  │
│ ○ Copilot PR summary    │
│ ○ Security scan (Snyk)  │
└──────────┬──────────────┘
           │
           ▼
┌─────────────────────────┐
│ Human Review (Enriched) │
│ ○ AI-flagged issues     │
│ ○ AI-generated summary  │
│ ○ AI test suggestions   │
│ ○ Focus on architecture │
│   and business logic    │
└──────────┬──────────────┘
           │
           ▼
    Merge + Deploy
```

---

## 6. Measuring AI Tool Effectiveness

### 6.1 Metrics for Engineering Teams

| Metric | How to Measure | Good Target |
|--------|---------------|-------------|
| **Acceptance Rate** | % of AI suggestions accepted | 25-40% (higher with good context) |
| **Time to First PR** | Time from task start to first PR | 20-30% reduction |
| **Code Review Time** | Time spent on code reviews | 30-50% reduction |
| **Bug Rate** | Bugs per 1000 LOC after AI adoption | Should not increase |
| **Test Coverage** | % test coverage after AI test gen | +10-20% improvement |
| **Developer Satisfaction** | Survey scores (1-10) | 7+/10 |
| **Boilerplate Ratio** | % of code that is boilerplate/repetitive | Should decrease significantly |

### 6.2 The AI Skill Maturity Model for Developers

| Level | Description | Indicators |
|-------|------------|------------|
| **Level 1: Aware** | Knows AI tools exist | Has tried ChatGPT/Copilot once |
| **Level 2: Using** | Uses AI tools daily for code completion | Accepts inline suggestions regularly |
| **Level 3: Proficient** | Uses chat, multi-file edit, @codebase effectively | Can prompt engineer for complex tasks |
| **Level 4: Advanced** | Customizes rules, builds AI features, uses MCP | Creates .cursorrules, uses function calling |
| **Level 5: Expert** | Designs AI-powered systems, evaluates models, leads AI adoption | Architects RAG systems, trains teams on AI tools |

**For a Lead Engineer interview in 2026, you should be at Level 4-5.**

---

## 7. Interview Questions — AI Dev Tools

| Question | Expected Answer |
|----------|----------------|
| "How do you use AI in your daily development?" | Describe specific tools (Copilot, Cursor), workflows, acceptance rate, and impact |
| "Do you trust AI-generated code?" | "Trust but verify — I review all AI output, especially for security and business logic" |
| "How do you handle AI hallucinations in code?" | "Run tests, verify API signatures, check docs, use type systems to catch errors" |
| "What is the biggest risk of AI coding tools?" | Security (leaked secrets, vulnerable patterns), over-reliance, reduced understanding |
| "How would you roll out AI tools to a team?" | Training, guidelines (.cursorrules), metrics, gradual adoption, measure impact |
| "When should you NOT use AI for coding?" | Security-critical code, complex business logic, novel algorithms, compliance-sensitive areas |

---

**Next**: [08_AI_in_Frontend.md](08_AI_in_Frontend.md) — Building AI-powered Angular UIs with streaming and chat interfaces.

