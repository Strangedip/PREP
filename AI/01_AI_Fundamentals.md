# AI Fundamentals for Software Engineers

> **Goal**: Understand the core concepts of AI, Machine Learning, Deep Learning, and Large Language Models — not as a data scientist, but as a software engineer who builds AI-powered products.
> **Level**: ALL (Associate through Lead)

---

## 1. The AI Landscape — What a Developer Must Know

### 1.1 AI vs ML vs DL vs GenAI — The Hierarchy

```
Artificial Intelligence (AI)
└── Machine Learning (ML)
    ├── Supervised Learning (Classification, Regression)
    ├── Unsupervised Learning (Clustering, Dimensionality Reduction)
    ├── Reinforcement Learning (Reward-based learning)
    └── Deep Learning (DL)
        ├── Convolutional Neural Networks (CNN) — Images
        ├── Recurrent Neural Networks (RNN/LSTM) — Sequences
        └── Transformers — THE architecture behind modern AI
            ├── Encoder-only (BERT) — Understanding/Classification
            ├── Decoder-only (GPT, LLaMA, Claude) — Generation
            └── Encoder-Decoder (T5, BART) — Translation, Summarization
                └── Generative AI (GenAI)
                    ├── Large Language Models (LLMs) — Text
                    ├── Diffusion Models — Images (Stable Diffusion, DALL-E)
                    ├── Multi-modal Models — Text + Image + Audio (GPT-4o, Gemini)
                    └── Code Models — Code generation (Codex, StarCoder, DeepSeek)
```

### 1.2 What Developers Actually Use

As a software engineer, you do NOT need to train models from scratch. You need to know:

| What You Do | What You Need to Know |
|-------------|----------------------|
| **Call an LLM API** | HTTP APIs, token limits, pricing, streaming, structured output |
| **Build RAG systems** | Embeddings, vector databases, chunking, retrieval, re-ranking |
| **Add AI features to apps** | Spring AI, prompt engineering, guardrails, error handling |
| **Build AI-powered UIs** | Streaming responses, chat interfaces, markdown rendering |
| **Design AI systems** | Architecture patterns, caching, cost management, evaluation |
| **Deploy AI features** | MLOps, monitoring, A/B testing, fallback strategies |

You do NOT need to know: gradient descent math, backpropagation derivations, model architecture details, or CUDA programming. Leave that to ML engineers.

---

## 2. The Transformer Architecture — The Foundation of Modern AI

### 2.1 Why Transformers Matter

Every major AI model in 2026 is based on the Transformer architecture (introduced in "Attention Is All You Need", 2017). Understanding it at a high level is essential because:

- It explains **why context windows exist** (limited attention span)
- It explains **why tokens matter** (not words — subword units)
- It explains **why AI "hallucinates"** (generates plausible but false text)
- It explains **why prompt engineering works** (attention focuses on relevant context)
- It explains **why RAG helps** (provides relevant context within the attention window)

### 2.2 How Transformers Work (Developer-Level Understanding)

**Input Processing:**
1. Text is split into **tokens** (subword units, not words). "unhappiness" → ["un", "happiness"]. "ChatGPT" → ["Chat", "G", "PT"]
2. Each token is converted to a **vector** (a list of numbers) called an **embedding** — typically 768 to 4096 dimensions
3. **Positional encoding** is added so the model knows word order (transformers process all tokens in parallel, unlike RNNs)

**The Self-Attention Mechanism (The Core Innovation):**
4. For each token, the model computes: "How much should I pay attention to every other token?"
5. This is done via three matrices: **Query (Q)**, **Key (K)**, **Value (V)**
   - Q = "What am I looking for?"
   - K = "What do I contain?"
   - V = "What information do I provide?"
6. Attention Score = softmax(Q × K^T / √d_k) × V
7. **Multi-head attention**: This is done multiple times in parallel (e.g., 32 heads), so the model can attend to different aspects simultaneously (syntax in one head, semantics in another)

**Feed-Forward + Output:**
8. The attention output goes through a **feed-forward neural network** (per-token transformation)
9. This is repeated through many **layers** (GPT-4 has ~120 layers, each with multi-head attention + feed-forward)
10. The final layer produces a **probability distribution** over all tokens in the vocabulary
11. The next token is selected based on this distribution (using temperature, top-p, top-k sampling)

### 2.3 Key Concepts for Developers

**Tokens:**
- Tokens are NOT words. They are subword units based on Byte-Pair Encoding (BPE)
- Average: 1 token ≈ 0.75 words (or ~4 characters in English)
- "Hello, how are you?" = 6 tokens
- Code is tokenized differently — a function name might be 3-5 tokens
- Token count determines: API cost, context window usage, response length
- Tools: tiktoken (OpenAI), tokenizers (Hugging Face)

**Context Window:**
- The maximum number of tokens a model can process in a single request (input + output combined)
- GPT-4o: 128K tokens (~96K words, ~300 pages)
- Claude 3.5 Sonnet: 200K tokens (~150K words, ~500 pages)
- Gemini 1.5 Pro: 2M tokens (~1.5M words)
- Longer context ≠ better — attention degrades for very long contexts ("lost in the middle" problem)
- This is why RAG is important even with large context windows — focused retrieval beats dumping everything

**Temperature:**
- Controls randomness of output. Range: 0.0 to 2.0 (typically 0.0 to 1.0)
- Temperature = 0.0: Deterministic, always picks the most likely token. Use for: code generation, factual Q&A, classification
- Temperature = 0.3-0.7: Balanced. Use for: most general tasks, chatbots
- Temperature = 0.8-1.0: Creative. Use for: creative writing, brainstorming
- Temperature > 1.0: Very random, often incoherent. Rarely useful.

**Top-p (Nucleus Sampling):**
- Instead of temperature, limits output to tokens whose cumulative probability ≥ p
- Top-p = 0.1: Very focused (only most likely tokens)
- Top-p = 0.9: Wide range of tokens considered
- Typically use EITHER temperature OR top-p, not both

**Top-k:**
- Limits output to the top K most likely tokens
- Top-k = 1: Greedy decoding (always most likely)
- Top-k = 50: Consider top 50 tokens

**Stop Sequences:**
- Tokens/strings that tell the model to stop generating
- Example: "\n\nHuman:" to prevent the model from generating more conversation
- Essential for controlling output format

---

## 3. Large Language Models (LLMs) — The Models You Will Use

### 3.1 The Major LLM Providers in 2026

| Provider | Models | Strengths | API |
|----------|--------|-----------|-----|
| **OpenAI** | GPT-4o, GPT-4o-mini, o1, o3 | Best general-purpose, strong coding, structured output | api.openai.com |
| **Anthropic** | Claude 3.5 Sonnet, Claude 3.5 Haiku, Claude 3 Opus | Best for long context, safety, instruction following | api.anthropic.com |
| **Google** | Gemini 1.5 Pro, Gemini 1.5 Flash, Gemini 2.0 | Multi-modal, largest context (2M tokens), Google integration | ai.google.dev |
| **Meta** | LLaMA 3.1 (8B, 70B, 405B) | Open-source, self-hostable, no API cost | huggingface.co |
| **Mistral** | Mistral Large, Mixtral, Codestral | Strong coding, open-weight, EU-based | api.mistral.ai |
| **DeepSeek** | DeepSeek-V3, DeepSeek-Coder-V2 | Excellent coding, cost-effective, open-source | api.deepseek.com |
| **Cohere** | Command R+, Embed v3 | Enterprise RAG, strong embeddings | api.cohere.com |

### 3.2 Model Selection Guide for Developers

| Use Case | Recommended Model | Why |
|----------|-------------------|-----|
| **General chat / Q&A** | GPT-4o-mini or Claude 3.5 Haiku | Fast, cheap, good enough |
| **Complex reasoning** | o1 / o3 or Claude 3 Opus | Multi-step reasoning, math, logic |
| **Code generation** | GPT-4o or DeepSeek-Coder-V2 | Best code quality |
| **Long document analysis** | Claude 3.5 Sonnet or Gemini 1.5 Pro | Large context windows |
| **Embeddings** | text-embedding-3-small (OpenAI) or Cohere Embed v3 | Best price/performance for RAG |
| **Self-hosted (no data leaves)** | LLaMA 3.1 70B or Mistral Large | Open-source, full data control |
| **Structured output (JSON)** | GPT-4o (native JSON mode) | Guaranteed valid JSON |
| **Multi-modal (image + text)** | GPT-4o or Gemini 2.0 | Process images, diagrams, screenshots |

### 3.3 LLM API Pricing — What Developers Must Know

Pricing is per **token** (not per request). Costs are split into **input tokens** (your prompt) and **output tokens** (the model's response). Output tokens typically cost 2-4x more than input tokens.

| Model | Input (per 1M tokens) | Output (per 1M tokens) | Context Window |
|-------|----------------------|------------------------|----------------|
| GPT-4o | $2.50 | $10.00 | 128K |
| GPT-4o-mini | $0.15 | $0.60 | 128K |
| Claude 3.5 Sonnet | $3.00 | $15.00 | 200K |
| Claude 3.5 Haiku | $0.25 | $1.25 | 200K |
| Gemini 1.5 Flash | $0.075 | $0.30 | 1M |
| Gemini 1.5 Pro | $1.25 | $5.00 | 2M |
| DeepSeek-V3 | $0.27 | $1.10 | 64K |

**Cost Example:**
- A typical RAG request: ~2000 input tokens + ~500 output tokens
- With GPT-4o-mini: (2000/1M × $0.15) + (500/1M × $0.60) = $0.0003 + $0.0003 = $0.0006 per request
- At 1M requests/day: $600/day = $18,000/month
- At 1M requests/day with GPT-4o: $5,500/day = $165,000/month
- **Model selection is a cost-critical architectural decision**

### 3.4 How to Call an LLM API (Raw HTTP)

Every LLM API follows a similar pattern. Understanding the raw API helps you debug and optimize.

```java
// OpenAI Chat Completions API — Raw HTTP call
HttpClient client = HttpClient.newHttpClient();

String requestBody = """
    {
        "model": "gpt-4o-mini",
        "messages": [
            {
                "role": "system",
                "content": "You are a helpful assistant that answers questions about Java programming."
            },
            {
                "role": "user",
                "content": "What is the difference between HashMap and ConcurrentHashMap?"
            }
        ],
        "temperature": 0.3,
        "max_tokens": 1000,
        "response_format": { "type": "json_object" }
    }
    """;

HttpRequest request = HttpRequest.newBuilder()
    .uri(URI.create("https://api.openai.com/v1/chat/completions"))
    .header("Authorization", "Bearer " + apiKey)
    .header("Content-Type", "application/json")
    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
    .build();

HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
// Parse response.body() as JSON to get the model's response
```

**The Message Structure (Common to ALL LLM APIs):**

| Role | Purpose | Example |
|------|---------|---------|
| **system** | Sets the AI's behavior, personality, constraints | "You are a Java expert. Always provide code examples. Never use deprecated APIs." |
| **user** | The human's message | "How do I implement a thread-safe singleton?" |
| **assistant** | Previous AI responses (for multi-turn conversation) | "Here is the double-checked locking pattern..." |
| **tool** | Results from function/tool calls | `{"function": "getWeather", "result": {"temp": 72}}` |

---

## 4. Embeddings — The Bridge Between Text and Math

### 4.1 What Are Embeddings?

An embedding is a **vector** (array of floating-point numbers) that represents the **meaning** of a piece of text. Similar meanings produce similar vectors.

```
"Java programming" → [0.12, -0.45, 0.78, 0.23, ..., -0.91]  // 1536 dimensions
"Python coding"    → [0.10, -0.42, 0.75, 0.25, ..., -0.88]  // Very similar vector!
"Italian cooking"  → [-0.56, 0.33, -0.12, 0.89, ..., 0.44]  // Very different vector
```

### 4.2 How Embeddings Work

1. A text string goes into an **embedding model** (not a generative model — a separate, specialized model)
2. The model outputs a fixed-size vector (e.g., 1536 dimensions for OpenAI's text-embedding-3-small)
3. The vector captures semantic meaning — synonyms are close, unrelated concepts are far apart
4. You store these vectors in a **vector database** for later retrieval

### 4.3 Similarity Metrics

To find "similar" text, you compare embedding vectors using distance metrics:

**Cosine Similarity (Most Common):**
```
similarity(A, B) = (A · B) / (||A|| × ||B||)
```
- Range: -1 to 1 (1 = identical, 0 = unrelated, -1 = opposite)
- Measures the angle between vectors, not magnitude
- **This is the default for most vector databases**

**Euclidean Distance (L2):**
```
distance(A, B) = √(Σ(Ai - Bi)²)
```
- Range: 0 to ∞ (0 = identical, higher = more different)
- Sensitive to vector magnitude

**Dot Product:**
```
similarity(A, B) = Σ(Ai × Bi)
```
- Combines similarity and magnitude
- Faster to compute than cosine similarity
- Used when vectors are normalized

### 4.4 Embedding Models Comparison

| Model | Dimensions | Max Tokens | Price (per 1M tokens) | Quality |
|-------|-----------|------------|----------------------|---------|
| text-embedding-3-small (OpenAI) | 1536 | 8191 | $0.02 | Good |
| text-embedding-3-large (OpenAI) | 3072 | 8191 | $0.13 | Best |
| embed-english-v3.0 (Cohere) | 1024 | 512 | $0.10 | Excellent for RAG |
| all-MiniLM-L6-v2 (open-source) | 384 | 256 | Free (self-hosted) | Good for basic use |
| BGE-large-en-v1.5 (BAAI, open) | 1024 | 512 | Free (self-hosted) | Very good |
| Nomic Embed 1.5 (open-source) | 768 | 8192 | Free (self-hosted) | Long context |

```java
// Creating an embedding with OpenAI API
String text = "What is the Java Memory Model?";
String requestBody = """
    {
        "model": "text-embedding-3-small",
        "input": "%s"
    }
    """.formatted(text);

// Response contains: { "data": [{ "embedding": [0.12, -0.45, ...] }] }
// Store this 1536-dimension vector in your vector database
```

---

## 5. How LLMs Generate Text — Autoregressive Generation

### 5.1 The Generation Process

LLMs generate text **one token at a time**, left to right. Each token is selected based on the probability distribution from the previous tokens. This is called **autoregressive generation**.

```
Input:  "The capital of France is"
Step 1: P(next_token) → "Paris" (0.95), "Lyon" (0.02), "a" (0.01), ...
Step 2: "The capital of France is Paris" → P(next_token) → "." (0.80), "," (0.15), ...
Step 3: "The capital of France is Paris." → <EOS> (end of sequence)
```

### 5.2 Why LLMs Hallucinate

Hallucination occurs when the model generates text that is **fluent and confident but factually incorrect**. This happens because:

1. **LLMs are probabilistic**: They pick the most likely next token, not the most factually correct one
2. **Training data cutoff**: The model has no knowledge after its training date
3. **No real-time access**: Without tools/RAG, the model cannot look up current information
4. **Confidence calibration**: LLMs express low-confidence answers with the same fluency as high-confidence answers
5. **Rare/ambiguous topics**: Less training data → more hallucination

**Developer Solutions to Hallucination:**

| Solution | How It Works | Effectiveness |
|----------|-------------|---------------|
| **RAG** | Provide relevant context in the prompt | HIGH — grounds the model in real data |
| **Structured Output** | Force JSON schema validation | MEDIUM — prevents format hallucination |
| **Temperature = 0** | Reduce randomness | LOW-MEDIUM — reduces creative hallucination |
| **Citation requirement** | Ask the model to cite sources from the context | MEDIUM — makes hallucination detectable |
| **Guardrails** | Post-processing validation of output | HIGH — catches factual errors |
| **Multi-model verification** | Ask two models and compare | HIGH — expensive but reliable |
| **Fine-tuning** | Train on domain-specific data | HIGH — expensive, needs data |

---

## 6. Types of AI Tasks for Developers

### 6.1 Text Tasks

| Task | Description | Example | Model Choice |
|------|-------------|---------|-------------|
| **Text Generation** | Create new text from a prompt | Product descriptions, emails | GPT-4o-mini, Claude Haiku |
| **Summarization** | Condense long text | Meeting notes, document summaries | Claude Sonnet (long context) |
| **Classification** | Categorize text into labels | Sentiment analysis, ticket routing | GPT-4o-mini (cheapest) |
| **Extraction** | Pull structured data from unstructured text | Parse invoice → JSON | GPT-4o (structured output) |
| **Translation** | Convert between languages | Localization | GPT-4o, Gemini |
| **Q&A** | Answer questions from given context | Customer support, docs search | RAG + any model |
| **Code Generation** | Write code from description | Feature implementation | GPT-4o, DeepSeek-Coder |
| **Code Explanation** | Explain existing code | Code review, onboarding | Claude Sonnet |
| **Conversation** | Multi-turn chat | Chatbots, assistants | Any chat model |

### 6.2 Multi-Modal Tasks (2025-2026 Trend)

| Task | Input | Output | Model |
|------|-------|--------|-------|
| **Image Understanding** | Image + Text prompt | Text description/analysis | GPT-4o, Gemini 2.0 |
| **Image Generation** | Text prompt | Image | DALL-E 3, Stable Diffusion, Midjourney |
| **Audio Transcription** | Audio file | Text transcript | Whisper (OpenAI) |
| **Text-to-Speech** | Text | Audio | OpenAI TTS, ElevenLabs |
| **Video Understanding** | Video + prompt | Text analysis | Gemini 2.0 |
| **Document Understanding** | PDF/scan + prompt | Structured data | GPT-4o (vision), Gemini |

---

## 7. Key Concepts Cheat Sheet

| Concept | What It Means | Why It Matters to You |
|---------|---------------|----------------------|
| **Token** | Subword unit (~0.75 words) | Determines cost and context limit |
| **Context Window** | Max tokens per request | Limits how much info you can send |
| **Temperature** | Randomness control (0-2) | Low for code/facts, high for creativity |
| **Embedding** | Text → number vector | Enables semantic search (RAG) |
| **Fine-tuning** | Further training a model on your data | Expensive, use only when needed |
| **RAG** | Retrieve relevant docs, add to prompt | The #1 pattern for adding knowledge to AI |
| **Hallucination** | Confident but incorrect output | Your biggest reliability challenge |
| **Prompt Engineering** | Crafting inputs to control output | Free performance improvement |
| **Function Calling** | Model requests to call your code | Connects AI to your APIs/databases |
| **Streaming** | Token-by-token response delivery | Better UX (user sees output immediately) |
| **Guardrails** | Input/output validation and filtering | Safety, compliance, quality control |
| **Structured Output** | Force JSON/schema compliance | Reliable integration with your code |
| **Multi-modal** | Processing text + images + audio | Document understanding, rich AI features |
| **Agent** | AI that plans and uses tools autonomously | The 2025-2026 frontier |

---

## 8. AI Terminology Glossary

| Term | Definition |
|------|-----------|
| **Attention** | Mechanism allowing the model to focus on relevant parts of the input |
| **BERT** | Bidirectional Encoder Representations from Transformers — used for understanding/classification |
| **Chunking** | Splitting documents into smaller pieces for embedding and retrieval |
| **Completion** | The model's generated output text |
| **Decoder** | The part of a transformer that generates output tokens one at a time |
| **Diffusion Model** | AI model that generates images by iteratively denoising random noise |
| **Encoder** | The part of a transformer that processes and understands input |
| **Few-shot** | Providing a few examples in the prompt to guide the model |
| **Fine-tuning** | Additional training of a pre-trained model on domain-specific data |
| **Foundation Model** | A large pre-trained model (GPT-4, Claude, LLaMA) that can be adapted |
| **GAN** | Generative Adversarial Network — older image generation approach |
| **GPT** | Generative Pre-trained Transformer (OpenAI's model family) |
| **Grounding** | Connecting model output to verified information (via RAG or tools) |
| **Inference** | Running a trained model to produce output (as opposed to training) |
| **LoRA** | Low-Rank Adaptation — efficient fine-tuning technique |
| **MCP** | Model Context Protocol — standard for connecting AI models to external tools |
| **RLHF** | Reinforcement Learning from Human Feedback — how models are aligned |
| **Semantic Search** | Search by meaning (using embeddings) rather than keywords |
| **Tokenizer** | Algorithm that splits text into tokens |
| **Transfer Learning** | Using a model trained on one task for a different task |
| **Vector** | An array of numbers representing data in high-dimensional space |
| **Zero-shot** | Asking the model to perform a task without any examples |

---

**Next**: [02_LLM_and_Prompt_Engineering.md](02_LLM_and_Prompt_Engineering.md) — Learn how to effectively communicate with LLMs to get the output you need.

