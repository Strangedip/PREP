# AI in Frontend — Building AI-Powered Angular UIs

> **Goal**: Build production-quality AI-powered user interfaces with Angular 17+, including streaming chat, markdown rendering, and real-time AI interactions.
> **Level**: Mid-Level through Lead

> **You are here**: SDE2 — AI / ML
> **Roadmap**: [Developer Master Roadmap](../ROADMAP.md) | **Prerequisites**: [05_Spring_AI.md](05_Spring_AI.md) | **Next**: [09_MLOps_AI_in_Production.md](09_MLOps_AI_in_Production.md)

---

## 1. AI UI Patterns in 2026

### 1.1 Common AI Interface Patterns

| Pattern | Description | Examples |
|---------|-------------|---------|
| **Chat Interface** | Conversational UI with messages | ChatGPT, Customer support bots |
| **Inline Assist** | AI suggestions within existing UI | Gmail Smart Compose, Notion AI |
| **Copilot Panel** | Side panel with AI assistance | Microsoft 365 Copilot, GitHub Copilot Chat |
| **Search + AI Answer** | Search with AI-generated summary | Google AI Overviews, Perplexity |
| **AI Form Fill** | Auto-populate forms from natural language | "Create an order for 5 widgets at $10 each" |
| **Real-time Analysis** | AI analyzing user input as they type | Grammarly, code editors |
| **AI Dashboard** | AI-generated insights on dashboards | Analytics platforms |

---

## 2. Streaming Chat Interface with Angular

### 2.1 The Service Layer (Consuming SSE from Spring Boot)

```typescript
// ai-chat.service.ts
import { Injectable, signal, computed } from '@angular/core';
import { HttpClient } from '@angular/common/http';

export interface ChatMessage {
  id: string;
  role: 'user' | 'assistant' | 'system';
  content: string;
  timestamp: Date;
  isStreaming: boolean;
  sources?: Source[];
}

export interface Source {
  name: string;
  section: string;
  page?: string;
}

@Injectable({ providedIn: 'root' })
export class AiChatService {
  private readonly apiUrl = '/api/ai';

  // State management with signals
  readonly messages = signal<ChatMessage[]>([]);
  readonly isLoading = signal(false);
  readonly error = signal<string | null>(null);
  readonly currentStreamingContent = signal('');

  // Computed signals
  readonly messageCount = computed(() => this.messages().length);
  readonly hasMessages = computed(() => this.messages().length > 0);
  readonly lastMessage = computed(() => {
    const msgs = this.messages();
    return msgs.length > 0 ? msgs[msgs.length - 1] : null;
  });

  constructor(private http: HttpClient) {}

  // Method 1: Streaming with EventSource (Server-Sent Events)
  sendMessageStreaming(userMessage: string): void {
    this.error.set(null);
    this.isLoading.set(true);
    this.currentStreamingContent.set('');

    // Add user message
    const userMsg: ChatMessage = {
      id: crypto.randomUUID(),
      role: 'user',
      content: userMessage,
      timestamp: new Date(),
      isStreaming: false,
    };
    this.messages.update((msgs) => [...msgs, userMsg]);

    // Add placeholder for assistant response
    const assistantMsg: ChatMessage = {
      id: crypto.randomUUID(),
      role: 'assistant',
      content: '',
      timestamp: new Date(),
      isStreaming: true,
    };
    this.messages.update((msgs) => [...msgs, assistantMsg]);

    // Create EventSource for SSE streaming
    const encodedMessage = encodeURIComponent(userMessage);
    const eventSource = new EventSource(
      `${this.apiUrl}/chat/stream?message=${encodedMessage}`
    );

    let fullContent = '';

    eventSource.onmessage = (event) => {
      if (event.data === '[DONE]') {
        // Stream complete
        eventSource.close();
        this.finalizeStream(assistantMsg.id, fullContent);
        return;
      }

      fullContent += event.data;
      this.currentStreamingContent.set(fullContent);

      // Update the assistant message in real-time
      this.messages.update((msgs) =>
        msgs.map((msg) =>
          msg.id === assistantMsg.id
            ? { ...msg, content: fullContent }
            : msg
        )
      );
    };

    eventSource.addEventListener('complete', () => {
      eventSource.close();
      this.finalizeStream(assistantMsg.id, fullContent);
    });

    eventSource.onerror = (error) => {
      console.error('SSE Error:', error);
      eventSource.close();
      this.error.set('Connection lost. Please try again.');
      this.isLoading.set(false);

      // Update message to show error state
      this.messages.update((msgs) =>
        msgs.map((msg) =>
          msg.id === assistantMsg.id
            ? { ...msg, isStreaming: false, content: fullContent || 'Error: Failed to get response.' }
            : msg
        )
      );
    };
  }

  // Method 2: Streaming with fetch() API (more control)
  async sendMessageWithFetch(userMessage: string): Promise<void> {
    this.error.set(null);
    this.isLoading.set(true);

    // Add user message
    const userMsg: ChatMessage = {
      id: crypto.randomUUID(),
      role: 'user',
      content: userMessage,
      timestamp: new Date(),
      isStreaming: false,
    };
    this.messages.update((msgs) => [...msgs, userMsg]);

    // Add placeholder
    const assistantId = crypto.randomUUID();
    const assistantMsg: ChatMessage = {
      id: assistantId,
      role: 'assistant',
      content: '',
      timestamp: new Date(),
      isStreaming: true,
    };
    this.messages.update((msgs) => [...msgs, assistantMsg]);

    try {
      const response = await fetch(`${this.apiUrl}/chat`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ message: userMessage }),
      });

      if (!response.ok) throw new Error(`HTTP ${response.status}`);
      if (!response.body) throw new Error('No response body');

      const reader = response.body.getReader();
      const decoder = new TextDecoder();
      let fullContent = '';

      while (true) {
        const { done, value } = await reader.read();
        if (done) break;

        const chunk = decoder.decode(value, { stream: true });
        fullContent += chunk;

        this.messages.update((msgs) =>
          msgs.map((msg) =>
            msg.id === assistantId
              ? { ...msg, content: fullContent }
              : msg
          )
        );
      }

      this.finalizeStream(assistantId, fullContent);
    } catch (err: any) {
      this.error.set(err.message || 'Failed to send message');
      this.isLoading.set(false);
    }
  }

  // Method 3: Non-streaming (simple HTTP POST)
  sendMessage(userMessage: string): void {
    this.error.set(null);
    this.isLoading.set(true);

    const userMsg: ChatMessage = {
      id: crypto.randomUUID(),
      role: 'user',
      content: userMessage,
      timestamp: new Date(),
      isStreaming: false,
    };
    this.messages.update((msgs) => [...msgs, userMsg]);

    this.http
      .post<{ answer: string; sources: Source[] }>(`${this.apiUrl}/chat`, {
        message: userMessage,
      })
      .subscribe({
        next: (response) => {
          const assistantMsg: ChatMessage = {
            id: crypto.randomUUID(),
            role: 'assistant',
            content: response.answer,
            timestamp: new Date(),
            isStreaming: false,
            sources: response.sources,
          };
          this.messages.update((msgs) => [...msgs, assistantMsg]);
          this.isLoading.set(false);
        },
        error: (err) => {
          this.error.set('Failed to get response. Please try again.');
          this.isLoading.set(false);
        },
      });
  }

  private finalizeStream(messageId: string, content: string): void {
    this.messages.update((msgs) =>
      msgs.map((msg) =>
        msg.id === messageId
          ? { ...msg, isStreaming: false, content }
          : msg
      )
    );
    this.isLoading.set(false);
    this.currentStreamingContent.set('');
  }

  clearChat(): void {
    this.messages.set([]);
    this.error.set(null);
  }
}
```

### 2.2 The Chat Component

```typescript
// ai-chat.component.ts
import {
  Component,
  ViewChild,
  ElementRef,
  AfterViewChecked,
  signal,
} from '@angular/core';
import { FormsModule } from '@angular/forms';
import { AiChatService, ChatMessage } from './ai-chat.service';
import { ChatMessageComponent } from './chat-message.component';
import { MarkdownPipe } from './markdown.pipe';

@Component({
  selector: 'app-ai-chat',
  standalone: true,
  imports: [FormsModule, ChatMessageComponent],
  template: `
    <div class="chat-container">
      <!-- Header -->
      <div class="chat-header">
        <h2>AI Assistant</h2>
        <button (click)="chatService.clearChat()" class="clear-btn"
                [disabled]="!chatService.hasMessages()">
          Clear Chat
        </button>
      </div>

      <!-- Messages -->
      <div class="chat-messages" #messagesContainer>
        @if (!chatService.hasMessages()) {
          <div class="empty-state">
            <div class="empty-icon">💬</div>
            <h3>How can I help you?</h3>
            <div class="suggestions">
              @for (suggestion of suggestions; track suggestion) {
                <button class="suggestion-chip" (click)="sendSuggestion(suggestion)">
                  {{ suggestion }}
                </button>
              }
            </div>
          </div>
        }

        @for (message of chatService.messages(); track message.id) {
          <app-chat-message [message]="message" />
        }

        @if (chatService.isLoading() && !chatService.currentStreamingContent()) {
          <div class="typing-indicator">
            <span></span><span></span><span></span>
          </div>
        }
      </div>

      <!-- Error Banner -->
      @if (chatService.error()) {
        <div class="error-banner">
          {{ chatService.error() }}
          <button (click)="chatService.error.set(null)">Dismiss</button>
        </div>
      }

      <!-- Input -->
      <div class="chat-input">
        <textarea
          #inputField
          [(ngModel)]="userInput"
          (keydown.enter)="onEnter($event)"
          placeholder="Ask me anything..."
          [disabled]="chatService.isLoading()"
          rows="1"
          (input)="autoResize($event)"
        ></textarea>
        <button
          (click)="send()"
          [disabled]="!userInput().trim() || chatService.isLoading()"
          class="send-btn"
        >
          @if (chatService.isLoading()) {
            <span class="spinner"></span>
          } @else {
            Send
          }
        </button>
      </div>
    </div>
  `,
  styles: [`
    .chat-container {
      display: flex;
      flex-direction: column;
      height: 100vh;
      max-width: 800px;
      margin: 0 auto;
      background: #ffffff;
      border-radius: 12px;
      box-shadow: 0 2px 20px rgba(0, 0, 0, 0.08);
      overflow: hidden;
    }

    .chat-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 16px 24px;
      border-bottom: 1px solid #e5e7eb;
      background: #f9fafb;
    }

    .chat-messages {
      flex: 1;
      overflow-y: auto;
      padding: 24px;
      scroll-behavior: smooth;
    }

    .empty-state {
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      height: 100%;
      color: #6b7280;
    }

    .suggestions {
      display: flex;
      flex-wrap: wrap;
      gap: 8px;
      margin-top: 16px;
      justify-content: center;
    }

    .suggestion-chip {
      padding: 8px 16px;
      border: 1px solid #d1d5db;
      border-radius: 20px;
      background: white;
      cursor: pointer;
      transition: all 0.2s;
    }

    .suggestion-chip:hover {
      background: #f3f4f6;
      border-color: #6366f1;
      color: #6366f1;
    }

    .typing-indicator {
      display: flex;
      gap: 4px;
      padding: 12px 16px;
    }

    .typing-indicator span {
      width: 8px;
      height: 8px;
      border-radius: 50%;
      background: #d1d5db;
      animation: bounce 1.4s infinite ease-in-out;
    }

    .typing-indicator span:nth-child(2) { animation-delay: 0.2s; }
    .typing-indicator span:nth-child(3) { animation-delay: 0.4s; }

    @keyframes bounce {
      0%, 80%, 100% { transform: scale(0); }
      40% { transform: scale(1); }
    }

    .chat-input {
      display: flex;
      gap: 12px;
      padding: 16px 24px;
      border-top: 1px solid #e5e7eb;
      background: #f9fafb;
    }

    .chat-input textarea {
      flex: 1;
      padding: 12px 16px;
      border: 1px solid #d1d5db;
      border-radius: 12px;
      resize: none;
      font-size: 14px;
      line-height: 1.5;
      max-height: 120px;
      overflow-y: auto;
    }

    .send-btn {
      padding: 12px 24px;
      background: #6366f1;
      color: white;
      border: none;
      border-radius: 12px;
      cursor: pointer;
      font-weight: 600;
      transition: background 0.2s;
    }

    .send-btn:hover:not(:disabled) { background: #4f46e5; }
    .send-btn:disabled { background: #d1d5db; cursor: not-allowed; }

    .error-banner {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 8px 24px;
      background: #fef2f2;
      color: #dc2626;
      font-size: 14px;
    }
  `],
})
export class AiChatComponent implements AfterViewChecked {
  @ViewChild('messagesContainer') messagesContainer!: ElementRef;
  @ViewChild('inputField') inputField!: ElementRef;

  chatService = inject(AiChatService);
  userInput = signal('');

  suggestions = [
    'Explain virtual threads in Java 21',
    'How do I implement RAG with Spring AI?',
    'Review my database schema for scalability',
    'What are the best practices for Angular signals?',
  ];

  ngAfterViewChecked(): void {
    this.scrollToBottom();
  }

  send(): void {
    const message = this.userInput().trim();
    if (!message) return;

    this.chatService.sendMessageStreaming(message);
    this.userInput.set('');

    // Reset textarea height
    if (this.inputField) {
      this.inputField.nativeElement.style.height = 'auto';
    }
  }

  sendSuggestion(suggestion: string): void {
    this.userInput.set(suggestion);
    this.send();
  }

  onEnter(event: KeyboardEvent): void {
    if (!event.shiftKey) {
      event.preventDefault();
      this.send();
    }
    // Shift+Enter = new line (default behavior)
  }

  autoResize(event: Event): void {
    const textarea = event.target as HTMLTextAreaElement;
    textarea.style.height = 'auto';
    textarea.style.height = Math.min(textarea.scrollHeight, 120) + 'px';
  }

  private scrollToBottom(): void {
    try {
      this.messagesContainer.nativeElement.scrollTop =
        this.messagesContainer.nativeElement.scrollHeight;
    } catch (err) {}
  }
}
```

### 2.3 The Message Component with Markdown Rendering

```typescript
// chat-message.component.ts
import { Component, input, computed } from '@angular/core';
import { ChatMessage } from './ai-chat.service';
import { MarkdownPipe } from './markdown.pipe';

@Component({
  selector: 'app-chat-message',
  standalone: true,
  imports: [MarkdownPipe],
  template: `
    <div class="message" [class]="message().role">
      <div class="message-avatar">
        {{ message().role === 'user' ? '👤' : '🤖' }}
      </div>
      <div class="message-body">
        <div class="message-content" [innerHTML]="message().content | markdown"></div>

        @if (message().isStreaming) {
          <span class="cursor-blink">▊</span>
        }

        @if (message().sources?.length) {
          <div class="sources">
            <span class="sources-label">Sources:</span>
            @for (source of message().sources; track source.name) {
              <span class="source-chip">
                📄 {{ source.name }}
                @if (source.section) { · {{ source.section }} }
              </span>
            }
          </div>
        }

        <div class="message-meta">
          <span class="timestamp">{{ message().timestamp | date:'shortTime' }}</span>
          @if (message().role === 'assistant' && !message().isStreaming) {
            <div class="actions">
              <button (click)="copyToClipboard()" title="Copy">📋</button>
              <button (click)="thumbsUp()" title="Helpful">👍</button>
              <button (click)="thumbsDown()" title="Not helpful">👎</button>
            </div>
          }
        </div>
      </div>
    </div>
  `,
  styles: [`
    .message {
      display: flex;
      gap: 12px;
      margin-bottom: 24px;
      animation: fadeIn 0.3s ease;
    }

    @keyframes fadeIn {
      from { opacity: 0; transform: translateY(8px); }
      to { opacity: 1; transform: translateY(0); }
    }

    .message.user { flex-direction: row-reverse; }

    .message-avatar {
      width: 36px;
      height: 36px;
      border-radius: 50%;
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 18px;
      flex-shrink: 0;
    }

    .message.user .message-body {
      background: #6366f1;
      color: white;
      border-radius: 16px 16px 4px 16px;
    }

    .message.assistant .message-body {
      background: #f3f4f6;
      border-radius: 16px 16px 16px 4px;
    }

    .message-body {
      padding: 12px 16px;
      max-width: 80%;
      line-height: 1.6;
    }

    .cursor-blink {
      animation: blink 1s infinite;
    }

    @keyframes blink {
      0%, 50% { opacity: 1; }
      51%, 100% { opacity: 0; }
    }

    .sources {
      margin-top: 12px;
      padding-top: 8px;
      border-top: 1px solid #e5e7eb;
      display: flex;
      flex-wrap: wrap;
      gap: 6px;
      align-items: center;
    }

    .source-chip {
      padding: 2px 8px;
      background: #e0e7ff;
      border-radius: 12px;
      font-size: 12px;
      color: #4338ca;
    }

    .message-meta {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-top: 8px;
      font-size: 12px;
      color: #9ca3af;
    }

    .actions {
      display: flex;
      gap: 4px;
    }

    .actions button {
      background: none;
      border: none;
      cursor: pointer;
      font-size: 14px;
      opacity: 0.5;
      transition: opacity 0.2s;
    }

    .actions button:hover { opacity: 1; }
  `],
})
export class ChatMessageComponent {
  message = input.required<ChatMessage>();

  copyToClipboard(): void {
    navigator.clipboard.writeText(this.message().content);
  }

  thumbsUp(): void {
    // Send feedback to backend for LLM evaluation
    console.log('Positive feedback for message:', this.message().id);
  }

  thumbsDown(): void {
    console.log('Negative feedback for message:', this.message().id);
  }
}
```

### 2.4 Markdown Pipe for Rendering AI Responses

```typescript
// markdown.pipe.ts
import { Pipe, PipeTransform } from '@angular/core';
import { DomSanitizer, SafeHtml } from '@angular/platform-browser';

// For production, use a library like 'marked' + 'highlight.js'
// npm install marked highlight.js
// npm install @types/marked

import { marked } from 'marked';
import hljs from 'highlight.js';

@Pipe({
  name: 'markdown',
  standalone: true,
})
export class MarkdownPipe implements PipeTransform {
  constructor(private sanitizer: DomSanitizer) {
    // Configure marked with syntax highlighting
    marked.setOptions({
      highlight: (code: string, lang: string) => {
        if (lang && hljs.getLanguage(lang)) {
          return hljs.highlight(code, { language: lang }).value;
        }
        return hljs.highlightAuto(code).value;
      },
      breaks: true,
      gfm: true, // GitHub Flavored Markdown
    });
  }

  transform(markdown: string): SafeHtml {
    if (!markdown) return '';
    const html = marked.parse(markdown) as string;
    return this.sanitizer.bypassSecurityTrustHtml(html);
  }
}
```

---

## 3. AI-Powered Search with Angular

### 3.1 Search-with-AI-Answer Pattern

```typescript
// ai-search.service.ts
@Injectable({ providedIn: 'root' })
export class AiSearchService {
  private readonly apiUrl = '/api/search';

  readonly query = signal('');
  readonly results = signal<SearchResult[]>([]);
  readonly aiAnswer = signal('');
  readonly isSearching = signal(false);
  readonly isAnswering = signal(false);

  private searchSubject = new Subject<string>();

  constructor(private http: HttpClient) {
    // Debounced search with AI answer
    this.searchSubject
      .pipe(
        debounceTime(300),
        distinctUntilChanged(),
        filter((q) => q.length >= 3),
        switchMap((query) => this.executeSearch(query))
      )
      .subscribe();
  }

  search(query: string): void {
    this.query.set(query);
    this.searchSubject.next(query);
  }

  private executeSearch(query: string) {
    this.isSearching.set(true);
    this.isAnswering.set(true);
    this.aiAnswer.set('');

    // 1. Get traditional search results
    const searchResults$ = this.http
      .get<SearchResult[]>(`${this.apiUrl}/results`, { params: { q: query } })
      .pipe(
        tap((results) => {
          this.results.set(results);
          this.isSearching.set(false);
        })
      );

    // 2. Stream AI answer simultaneously
    const aiAnswer$ = new Observable<string>((observer) => {
      const eventSource = new EventSource(
        `${this.apiUrl}/ai-answer?q=${encodeURIComponent(query)}`
      );

      let fullAnswer = '';
      eventSource.onmessage = (event) => {
        if (event.data === '[DONE]') {
          eventSource.close();
          observer.complete();
          this.isAnswering.set(false);
          return;
        }
        fullAnswer += event.data;
        this.aiAnswer.set(fullAnswer);
      };

      eventSource.onerror = () => {
        eventSource.close();
        observer.complete();
        this.isAnswering.set(false);
      };
    });

    return merge(searchResults$, aiAnswer$);
  }
}
```

---

## 4. Accessibility and UX Best Practices for AI UIs

### 4.1 AI UX Principles

| Principle | Implementation |
|-----------|---------------|
| **Show thinking state** | Typing indicator, progress messages during long AI operations |
| **Stream responses** | Show text as it generates (do not make user wait for full response) |
| **Cite sources** | Show where the AI got its information (builds trust) |
| **Allow feedback** | Thumbs up/down on AI responses (improves quality over time) |
| **Show confidence** | Indicate when the AI is uncertain ("I'm not sure about this, but...") |
| **Provide escape hatch** | Always allow user to speak to a human or skip AI |
| **Handle errors gracefully** | Show user-friendly error messages, offer retry |
| **Respect privacy** | Indicate what data is sent to AI, offer opt-out |
| **Make AI transparent** | Label AI-generated content, do not pretend to be human |
| **Support keyboard** | Full keyboard navigation (Enter to send, Shift+Enter for newline) |

### 4.2 Loading States for AI

```typescript
// AI operations can take 2-30 seconds — much longer than typical API calls
// Users need clear feedback throughout

@Component({
  template: `
    <!-- Phase 1: Acknowledging (0-0.5s) -->
    @if (phase() === 'acknowledging') {
      <div class="status">Processing your request...</div>
    }

    <!-- Phase 2: Searching (0.5-3s) — Show what the AI is doing -->
    @if (phase() === 'searching') {
      <div class="status">
        <span class="spinner"></span>
        Searching through {{ documentCount }} documents...
      </div>
    }

    <!-- Phase 3: Generating (3-15s) — Stream the response -->
    @if (phase() === 'generating') {
      <div class="response" [innerHTML]="partialResponse() | markdown"></div>
      <span class="cursor-blink">▊</span>
    }

    <!-- Phase 4: Complete -->
    @if (phase() === 'complete') {
      <div class="response" [innerHTML]="fullResponse() | markdown"></div>
      <div class="actions">
        <button (click)="copyResponse()">Copy</button>
        <button (click)="regenerate()">Regenerate</button>
      </div>
    }

    <!-- Error state -->
    @if (phase() === 'error') {
      <div class="error">
        <p>Sorry, I encountered an issue. This might help:</p>
        <ul>
          <li>Try rephrasing your question</li>
          <li>Make your question more specific</li>
          <li>Check your internet connection</li>
        </ul>
        <button (click)="retry()">Try Again</button>
      </div>
    }
  `,
})
export class AiResponseComponent {
  phase = signal<'idle' | 'acknowledging' | 'searching' | 'generating' | 'complete' | 'error'>('idle');
  partialResponse = signal('');
  fullResponse = signal('');
}
```

---

## 5. Interview Questions — AI in Frontend

| Question | Key Points |
|----------|-----------|
| "How do you stream AI responses in Angular?" | EventSource (SSE) or fetch with ReadableStream, update signals per chunk |
| "How do you render markdown from AI?" | marked + highlight.js, sanitize HTML, DomSanitizer |
| "What UX considerations for AI features?" | Streaming, typing indicators, source citations, feedback, error states |
| "How do you handle slow AI responses?" | Progressive loading states, stream immediately, show what's happening |
| "How do you manage chat state in Angular?" | Signals for reactive state, persist to localStorage or backend |
| "How do you handle AI errors in the UI?" | Graceful degradation, retry, user-friendly messages, fallback to non-AI |

---

**Next**: [09_MLOps_AI_in_Production.md](09_MLOps_AI_in_Production.md) — Deploying, monitoring, and scaling AI features in production.

