# AI & Machine Learning for Software Engineers — 2026 Edition

> **Who this is for**: Java/Angular Full-Stack developers (Associate → Lead) who need to understand, integrate, and architect AI-powered features in production systems.
> **This is NOT** a data science course. This is AI knowledge from the **software engineer's perspective** — building, deploying, scaling, and designing AI-powered applications.

---

## Why Every Developer Needs AI Knowledge in 2026

The software industry has fundamentally shifted. AI is no longer a niche specialty — it is embedded in every product, every workflow, and every interview. Here is what has changed:

| Year | What Changed | Impact on Developers |
|------|-------------|---------------------|
| 2022 | ChatGPT launched | Awareness — "AI can write code" |
| 2023 | GPT-4, GitHub Copilot, LangChain | Adoption — Developers start using AI tools daily |
| 2024 | RAG becomes standard, AI Agents emerge, Cursor/Codeium mature | Integration — AI features in every product |
| 2025 | Agentic workflows, MCP, function calling, Spring AI 1.0, AI-native UIs | Architecture — AI is a system design concern |
| 2026 | AI-first development, multi-modal, AI agents in production, AI coding is table stakes | **Expectation — If you can't build with AI, you can't get hired** |

### What Interviewers Ask in 2026

| Level | AI Questions You Will Face |
|-------|---------------------------|
| **Associate** | "Have you used Copilot/Cursor? How do you prompt effectively? What is an LLM?" |
| **Mid-Level** | "How would you add AI features to this app? What is RAG? How do embeddings work?" |
| **Senior** | "Design a RAG pipeline. How would you handle hallucinations? What vector DB would you choose and why?" |
| **Lead** | "Architect an AI-powered system for 10M users. How do you evaluate LLM output quality? What is your AI governance strategy? How do you handle PII in prompts?" |

---

## Directory Structure

```
05_AI/
├── README.md                           ← YOU ARE HERE
├── 01_AI_Fundamentals.md               — Core ML/DL/LLM concepts every developer must know
├── 02_LLM_and_Prompt_Engineering.md    — Working with LLMs, prompt techniques, token economics
├── 03_RAG_Architecture.md              — Complete RAG deep-dive with Java/Spring Boot implementation
├── 04_Vector_Databases_Embeddings.md   — Embeddings, vector DBs, similarity search, pgvector
├── 05_Spring_AI.md                     — Spring AI framework: chat, embedding, function calling, streaming
├── 06_AI_Agents_and_Workflows.md       — Agentic AI, tool use, MCP, orchestration patterns
├── 07_AI_Powered_Dev_Tools.md          — GitHub Copilot, Cursor, Codeium, AI in the SDLC
├── 08_AI_in_Frontend.md                — AI-powered Angular UIs, streaming responses, chat interfaces
├── 09_MLOps_AI_in_Production.md        — Deploying, monitoring, scaling, cost-controlling AI features
├── 10_AI_System_Design.md              — AI system design interview problems with solutions
└── 11_AI_Ethics_Safety_Governance.md   — Responsible AI, bias, compliance, PII handling
```

---

## Study Path by Career Level

### Associate / Junior — 2 weeks

| Week | Topic | File |
|------|-------|------|
| 1 | AI Fundamentals (what is ML, DL, LLM, transformer) | [01_AI_Fundamentals.md](01_AI_Fundamentals.md) |
| 1 | AI Dev Tools (Copilot, Cursor — use them daily) | [07_AI_Powered_Dev_Tools.md](07_AI_Powered_Dev_Tools.md) |
| 2 | Prompt Engineering basics | [02_LLM_and_Prompt_Engineering.md](02_LLM_and_Prompt_Engineering.md) |
| 2 | What is RAG (high level) | [03_RAG_Architecture.md](03_RAG_Architecture.md) (first 2 sections) |

### Mid-Level SDE — 4 weeks

| Week | Topic | File |
|------|-------|------|
| 1 | AI Fundamentals deep dive | [01_AI_Fundamentals.md](01_AI_Fundamentals.md) |
| 2 | Prompt Engineering + LLM APIs | [02_LLM_and_Prompt_Engineering.md](02_LLM_and_Prompt_Engineering.md) |
| 3 | RAG Architecture + Vector Databases | [03_RAG_Architecture.md](03_RAG_Architecture.md), [04_Vector_Databases_Embeddings.md](04_Vector_Databases_Embeddings.md) |
| 4 | Spring AI + AI in Frontend | [05_Spring_AI.md](05_Spring_AI.md), [08_AI_in_Frontend.md](08_AI_in_Frontend.md) |

### Senior SDE — 6 weeks

All of the above, plus:

| Week | Topic | File |
|------|-------|------|
| 5 | AI Agents + Agentic Workflows | [06_AI_Agents_and_Workflows.md](06_AI_Agents_and_Workflows.md) |
| 6 | MLOps + AI in Production | [09_MLOps_AI_in_Production.md](09_MLOps_AI_in_Production.md) |

### Lead Engineer — 8 weeks

All of the above, plus:

| Week | Topic | File |
|------|-------|------|
| 7 | AI System Design Problems | [10_AI_System_Design.md](10_AI_System_Design.md) |
| 8 | AI Ethics, Safety, Governance | [11_AI_Ethics_Safety_Governance.md](11_AI_Ethics_Safety_Governance.md) |

---

## Quick Reference: AI Keywords for Interviews

| Domain | Must-Know Terms |
|--------|----------------|
| **LLM Basics** | Transformer, Attention, Token, Context Window, Temperature, Top-p, Inference, Fine-tuning |
| **Prompt Engineering** | System Prompt, Few-Shot, Chain-of-Thought, ReAct, Structured Output, Guardrails |
| **RAG** | Retrieval-Augmented Generation, Chunking, Embedding, Vector Search, Re-ranking, Hybrid Search |
| **Vector DB** | pgvector, Pinecone, Weaviate, Milvus, HNSW, Cosine Similarity, ANN (Approximate Nearest Neighbor) |
| **Spring AI** | ChatClient, EmbeddingModel, VectorStore, FunctionCallback, Advisors, StreamingChatModel |
| **AI Agents** | Tool Calling, Function Calling, MCP (Model Context Protocol), ReAct, Planning, Multi-Agent |
| **Dev Tools** | GitHub Copilot, Cursor, Codeium, AI Code Review, AI Testing, AI Pair Programming |
| **Production** | Token Cost, Latency Budget, Caching, Guardrails, Fallback, A/B Testing, Evaluation Metrics |
| **Governance** | Hallucination, Bias, PII Redaction, Prompt Injection, Content Filtering, Audit Trail |

---

## How AI Connects to Your Existing Knowledge

| Existing Skill | AI Extension |
|---------------|-------------|
| **Spring Boot REST APIs** | → Spring AI ChatClient, streaming endpoints, function calling |
| **Angular Components** | → AI chat interfaces, streaming response rendering, markdown rendering |
| **Database Design** | → Vector databases, hybrid search (SQL + vector), pgvector extension |
| **Microservices** | → AI as a microservice, API gateway for LLM routing, cost management |
| **Caching (Redis)** | → Semantic caching for LLM responses, embedding cache |
| **Message Queues (Kafka)** | → Async AI processing, batch embedding pipelines |
| **System Design** | → AI system design problems, RAG at scale, multi-tenant AI |
| **DevOps / K8s** | → GPU node pools, model serving, auto-scaling AI workloads |
| **Testing** | → LLM output evaluation, prompt regression testing, AI-assisted testing |
| **Security** | → Prompt injection prevention, PII redaction, AI governance |

---

**Start with Section 01 and work through sequentially. Each section builds on the previous one.**

