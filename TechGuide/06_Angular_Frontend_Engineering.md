# Section 6: Angular & Frontend Engineering (Full-Stack Lead Critical)

---

## 6.1 Angular Architecture: Modules, Standalone Components, Signals, and Change Detection

---

### The "Why" & The Problem

As a Full Stack Java/Angular Lead Engineer, you own the **entire vertical slice** — from the database to the browser. An interviewer hiring for a Lead Full Stack role will probe your Angular knowledge as deeply as your Java knowledge. They are not looking for someone who "can write components" — they are looking for someone who can **architect a maintainable, performant, scalable Angular application** that a team of 5-10 developers can work on without stepping on each other's toes.

The core production problems this knowledge solves:
- **Performance degradation**: A poorly architected Angular app can trigger thousands of unnecessary change detection cycles per second, causing visible jank (dropped frames, slow scrolling, laggy input).
- **Bundle size explosion**: Without proper lazy loading and module boundaries, the initial bundle grows to 5MB+, and mobile users on 3G networks wait 15+ seconds to see anything.
- **Developer productivity collapse**: Without clear component architecture, state management strategy, and folder structure, a team of 5 developers will constantly create merge conflicts, duplicate code, and ship inconsistent UI patterns.
- **Unmanageable state**: Without a state management strategy, components pass data through 7 levels of @Input/@Output, or worse, mutate shared objects directly, leading to bugs that are impossible to reproduce.

A company pays you to know this because **frontend code is the first thing users see**. A beautiful, fast frontend on top of a mediocre backend feels better to users than a perfect backend with a slow, buggy frontend.

---

### Interviewer Expectations

- **Angular 17+/18+ features**: Standalone components (no NgModules required), Signals for reactive state, `@defer` blocks for lazy loading, new control flow syntax (`@if`, `@for`, `@switch`). If you only know the old `*ngIf`/`*ngFor` directives, you sound outdated.
- **Change Detection**: Explain Default vs. OnPush strategy. Why OnPush dramatically improves performance. How Signals change the game by making change detection granular.
- **Component Architecture**: Smart (container) vs. Dumb (presentational) components. How to structure a large-scale application. Feature modules, core module, shared module.
- **RxJS**: Understand Observables, Subjects, operators (map, switchMap, mergeMap, exhaustMap, debounceTime, distinctUntilChanged, combineLatest). Know when to use Signals vs. RxJS.
- **Keywords**: "Standalone components", "Signal-based reactivity", "OnPush change detection", "Zone.js vs. Zoneless", "lazy loading with loadComponent", "smart vs. dumb components", "unidirectional data flow", "tree-shakable providers", "deferred views", "SSR with Angular Universal/Hydration".

---

### The Deep Dive & Solution

#### Angular Application Architecture (2025-2026 Best Practices)

Angular has undergone a massive evolution. Angular 17+ introduced **standalone components** as the default, **Signals** for reactive state management, a new **control flow syntax**, and **deferred views**. Modern Angular is significantly simpler and more performant than Angular 14 and below.

**Project Structure for a Large-Scale Application**:

```
src/
├── app/
│   ├── core/                          # Singleton services, guards, interceptors
│   │   ├── auth/
│   │   │   ├── auth.service.ts
│   │   │   ├── auth.guard.ts
│   │   │   └── auth.interceptor.ts
│   │   ├── api/
│   │   │   ├── api.service.ts         # Base HTTP service
│   │   │   └── error-handler.interceptor.ts
│   │   └── layout/
│   │       ├── header.component.ts
│   │       ├── sidebar.component.ts
│   │       └── layout.component.ts
│   │
│   ├── shared/                        # Reusable components, pipes, directives
│   │   ├── components/
│   │   │   ├── data-table/
│   │   │   ├── modal/
│   │   │   ├── loading-spinner/
│   │   │   └── pagination/
│   │   ├── directives/
│   │   │   └── click-outside.directive.ts
│   │   ├── pipes/
│   │   │   ├── date-format.pipe.ts
│   │   │   └── currency-format.pipe.ts
│   │   └── models/
│   │       ├── api-response.model.ts
│   │       └── pagination.model.ts
│   │
│   ├── features/                      # Feature areas (lazy-loaded)
│   │   ├── orders/
│   │   │   ├── order-list/
│   │   │   │   ├── order-list.component.ts       # Smart component
│   │   │   │   └── order-list.component.html
│   │   │   ├── order-detail/
│   │   │   │   ├── order-detail.component.ts     # Smart component
│   │   │   │   └── order-detail.component.html
│   │   │   ├── order-card/
│   │   │   │   ├── order-card.component.ts       # Dumb component
│   │   │   │   └── order-card.component.html
│   │   │   ├── services/
│   │   │   │   └── order.service.ts
│   │   │   ├── models/
│   │   │   │   └── order.model.ts
│   │   │   └── order.routes.ts                   # Feature routes
│   │   │
│   │   ├── dashboard/
│   │   │   ├── dashboard.component.ts
│   │   │   ├── widgets/
│   │   │   └── dashboard.routes.ts
│   │   │
│   │   └── settings/
│   │       ├── profile/
│   │       ├── preferences/
│   │       └── settings.routes.ts
│   │
│   ├── app.component.ts
│   ├── app.config.ts                  # Application-level providers
│   └── app.routes.ts                  # Root routing with lazy loading
│
├── environments/
│   ├── environment.ts
│   └── environment.prod.ts
└── styles/
    ├── _variables.scss
    ├── _mixins.scss
    └── global.scss
```

#### Standalone Components — The Modern Way

Before Angular 14, every component had to belong to an NgModule. This created a complex web of module imports and declarations. Standalone components simplify this dramatically — each component declares its own dependencies.

```typescript
// Old way (Angular < 14): Components belong to modules
@NgModule({
  declarations: [OrderListComponent, OrderCardComponent],
  imports: [CommonModule, FormsModule, SharedModule],
  exports: [OrderListComponent]
})
export class OrderModule { }

// New way (Angular 17+): Standalone components — no modules needed
@Component({
  selector: 'app-order-card',
  standalone: true,
  imports: [DatePipe, CurrencyPipe, RouterLink],  // Declare dependencies directly
  template: `
    <div class="order-card" [class.urgent]="order().status === 'PENDING'">
      <h3>Order #{{ order().id }}</h3>
      <p>{{ order().totalAmount | currency }}</p>
      <p>{{ order().orderDate | date:'mediumDate' }}</p>
      <span class="badge" [class]="'badge-' + order().status.toLowerCase()">
        {{ order().status }}
      </span>
      <a [routerLink]="['/orders', order().id]">View Details</a>
    </div>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush  // ALWAYS use OnPush
})
export class OrderCardComponent {
  // Signal-based input (Angular 17.1+)
  order = input.required<Order>();
  
  // Signal-based output
  onSelect = output<string>();
  
  selectOrder() {
    this.onSelect.emit(this.order().id);
  }
}
```

#### Signals — Angular's New Reactive Primitive

Signals are Angular's answer to fine-grained reactivity (inspired by SolidJS, Preact Signals, and Vue's ref system). They replace many uses of RxJS Observables for synchronous, component-level state.

**What is a Signal?** A Signal is a wrapper around a value that notifies consumers when the value changes. Unlike RxJS Observables, Signals are:
- **Synchronous**: The value is always available immediately (no subscription needed).
- **Glitch-free**: Computed signals update atomically — consumers see a consistent state.
- **Simpler**: No subscribe/unsubscribe boilerplate, no memory leak risk from forgotten subscriptions.

```typescript
// Signals — the basics
import { signal, computed, effect } from '@angular/core';

@Component({
  selector: 'app-cart',
  standalone: true,
  imports: [CurrencyPipe],
  template: `
    <h2>Shopping Cart</h2>
    <p>Items: {{ itemCount() }}</p>
    <p>Total: {{ totalPrice() | currency }}</p>
    <p>Discount Applied: {{ hasDiscount() ? 'Yes' : 'No' }}</p>
    
    @for (item of items(); track item.id) {
      <div class="cart-item">
        <span>{{ item.name }} — {{ item.price | currency }} x {{ item.quantity }}</span>
        <button (click)="removeItem(item.id)">Remove</button>
      </div>
    } @empty {
      <p>Your cart is empty.</p>
    }
    
    <button (click)="addItem()">Add Sample Item</button>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class CartComponent {
  // Writable signal — holds mutable state
  items = signal<CartItem[]>([]);
  
  // Computed signal — derived from other signals, recalculates automatically
  itemCount = computed(() => this.items().length);
  
  totalPrice = computed(() => 
    this.items().reduce((sum, item) => sum + item.price * item.quantity, 0)
  );
  
  // Computed signals can depend on other computed signals
  hasDiscount = computed(() => this.totalPrice() > 100);
  
  discountedPrice = computed(() => 
    this.hasDiscount() ? this.totalPrice() * 0.9 : this.totalPrice()
  );
  
  constructor() {
    // Effect — runs side effects when signals change
    effect(() => {
      console.log(`Cart updated: ${this.itemCount()} items, $${this.totalPrice()}`);
      // This runs every time items(), itemCount(), or totalPrice() changes
    });
  }
  
  addItem() {
    // Update a signal — use .update() for transformations, .set() for replacement
    this.items.update(current => [
      ...current,
      { id: crypto.randomUUID(), name: 'New Item', price: 29.99, quantity: 1 }
    ]);
  }
  
  removeItem(id: string) {
    this.items.update(current => current.filter(item => item.id !== id));
  }
}
```

**Signals vs. RxJS — When to Use Each**:

| Use Case | Signals | RxJS |
|----------|---------|------|
| Component local state | Yes — simpler, synchronous | Overkill |
| Derived/computed values | `computed()` | `combineLatest` + `map` (more complex) |
| HTTP calls | No (async) | Yes — `HttpClient` returns Observables |
| Event streams (WebSocket, real-time) | No | Yes — RxJS is built for streams |
| Debouncing user input | No | Yes — `debounceTime`, `distinctUntilChanged` |
| Complex async orchestration | No | Yes — `switchMap`, `mergeMap`, `forkJoin` |
| State that crosses component boundaries | Yes (with service-level signals) | Yes (with BehaviorSubject) |

**Rule of thumb**: Use Signals for synchronous state and UI bindings. Use RxJS for asynchronous streams and complex event processing. They interop: `toSignal()` converts an Observable to a Signal, `toObservable()` converts a Signal to an Observable.

```typescript
// Interop between Signals and RxJS
import { toSignal, toObservable } from '@angular/core/rxjs-interop';

@Component({ /* ... */ })
export class SearchComponent {
  
  private http = inject(HttpClient);
  private destroyRef = inject(DestroyRef);
  
  // Signal for the search query
  searchQuery = signal('');
  
  // Convert signal to observable for RxJS operators (debounce, switchMap)
  private searchQuery$ = toObservable(this.searchQuery);
  
  // RxJS pipeline: debounce → switchMap to HTTP → convert back to Signal
  searchResults = toSignal(
    this.searchQuery$.pipe(
      debounceTime(300),
      distinctUntilChanged(),
      filter(query => query.length >= 2),
      switchMap(query => this.http.get<SearchResult[]>(`/api/search?q=${query}`)),
      catchError(err => {
        console.error('Search failed', err);
        return of([]);
      })
    ),
    { initialValue: [] }  // Initial value for the signal
  );
}
```

#### Change Detection — OnPush and Beyond

**Default Change Detection**: Angular checks EVERY component in the component tree on EVERY browser event (click, keystroke, HTTP response, timer). For a large app with 500 components, this means 500 components are re-evaluated on every mouse move. This is slow.

**OnPush Change Detection**: Angular only checks a component when:
1. An `@Input()` reference changes (not mutation — a NEW object reference).
2. An event handler in the component or its children fires.
3. An Observable bound via `async` pipe emits.
4. A Signal that the template reads changes.
5. `ChangeDetectorRef.markForCheck()` is called explicitly.

```typescript
// OnPush — the template only re-renders when inputs change by REFERENCE
@Component({
  selector: 'app-order-list',
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,  // ALWAYS set this
  template: `
    @for (order of orders(); track order.id) {
      <app-order-card [order]="order" (onSelect)="selectOrder($event)" />
    }
  `
})
export class OrderListComponent {
  orders = input.required<Order[]>();
  
  selectOrder(orderId: string) {
    // Event handler → triggers change detection for this component
  }
}
```

**Common OnPush mistake**: Mutating objects instead of creating new ones.

```typescript
// BAD: Mutating the array — OnPush won't detect this change
this.orders.push(newOrder);  // Same array reference → no change detection

// GOOD: Creating a new array — new reference → change detection triggers
this.orders = [...this.orders, newOrder];

// With Signals, this is handled automatically:
this.orders.update(current => [...current, newOrder]);  // Signal notifies consumers
```

**Zoneless Angular (Angular 18+ experimental, stable in 19+)**: Traditionally, Angular uses Zone.js to automatically detect when async operations complete (setTimeout, Promises, HTTP calls) and trigger change detection. Signals enable **zoneless** change detection — Angular knows exactly which components to update because Signals track their consumers. This eliminates the overhead of Zone.js monkey-patching every async API.

```typescript
// app.config.ts — Enabling zoneless change detection
export const appConfig: ApplicationConfig = {
  providers: [
    provideZoneChangeDetection({ eventCoalescing: true }),
    // OR for zoneless (experimental in Angular 18, stable in 19):
    // provideExperimentalZonelessChangeDetection(),
    provideRouter(routes),
    provideHttpClient(withInterceptors([authInterceptor, errorInterceptor])),
    provideAnimationsAsync(),
  ]
};
```

#### New Control Flow Syntax (Angular 17+)

Angular 17 introduced a built-in control flow that replaces `*ngIf`, `*ngFor`, and `*ngSwitch` with `@if`, `@for`, and `@switch`. The new syntax is:
- **More performant**: Optimized at compile time.
- **More readable**: Clearer syntax, especially for `else` blocks.
- **Supports `@defer`**: Built-in lazy loading of template sections.

```html
<!-- Old syntax (still works but deprecated) -->
<div *ngIf="isLoading; else content">Loading...</div>
<ng-template #content>
  <div *ngFor="let order of orders; trackBy: trackById">
    <div [ngSwitch]="order.status">
      <span *ngSwitchCase="'PENDING'">⏳ Pending</span>
      <span *ngSwitchCase="'SHIPPED'">🚚 Shipped</span>
      <span *ngSwitchDefault>Unknown</span>
    </div>
  </div>
</ng-template>

<!-- New syntax (Angular 17+ — use this) -->
@if (isLoading()) {
  <div class="spinner">Loading...</div>
} @else if (error()) {
  <div class="error">{{ error() }}</div>
} @else {
  @for (order of orders(); track order.id) {
    @switch (order.status) {
      @case ('PENDING') { <span>⏳ Pending</span> }
      @case ('SHIPPED') { <span>🚚 Shipped</span> }
      @default { <span>Unknown</span> }
    }
  } @empty {
    <p>No orders found.</p>
  }
}
```

#### Deferred Views (`@defer`) — Built-in Lazy Loading

`@defer` allows you to lazy-load parts of a template. The deferred content is only loaded when a trigger condition is met (viewport visibility, interaction, timer, idle).

```html
<!-- The heavy chart component is only loaded when it enters the viewport -->
@defer (on viewport) {
  <app-analytics-chart [data]="analyticsData()" />
} @placeholder {
  <div class="chart-placeholder">📊 Chart will load when you scroll here</div>
} @loading (minimum 500ms) {
  <app-loading-spinner />
} @error {
  <p>Failed to load chart component.</p>
}

<!-- Load a component on user interaction -->
@defer (on interaction) {
  <app-advanced-filters [categories]="categories()" />
} @placeholder {
  <button>Click to show advanced filters</button>
}

<!-- Load after a timer (prefetch after 2 seconds) -->
@defer (on timer(2000)) {
  <app-recommendations [userId]="userId()" />
} @placeholder {
  <div>Recommendations loading soon...</div>
}

<!-- Combine triggers: load when idle AND prefetch on viewport -->
@defer (on idle; prefetch on viewport) {
  <app-footer-content />
}
```

---

## 6.2 State Management: Service-based State, NgRx (Redux), and Signal Store

---

### The "Why" & The Problem

As an application grows beyond 20-30 components, managing state becomes the #1 source of bugs. State is any data that changes over time: the current user, the list of orders, whether a modal is open, the current search filter. Without a state management strategy:

- **Data flows become spaghetti**: Component A passes data to Component B via @Input, which passes it to Component C, which emits events back up through @Output chains 5 levels deep.
- **Stale data**: Two components show different versions of the same order because they fetched it independently at different times.
- **Race conditions**: User clicks "Save" twice. The second save overwrites the first. Without proper state management, both succeed and the UI shows the wrong data.
- **Debugging nightmare**: When a bug is reported ("the order total is wrong"), you have no idea which component modified the state, when, and why.

---

### Interviewer Expectations

- **Service-based state**: For small/medium apps, simple Injectable services with Signals or BehaviorSubject are sufficient. Explain when you DON'T need NgRx.
- **NgRx (Redux pattern)**: For large enterprise apps, explain the Redux pattern (Store, Actions, Reducers, Effects, Selectors). Know the trade-offs (boilerplate vs. predictability).
- **Signal Store**: NgRx Signal Store (the modern, simplified NgRx). Explain why it's recommended over the classic NgRx Store for new projects.
- **Keywords**: "Single source of truth", "unidirectional data flow", "immutability", "pure functions (reducers)", "side effects (effects)", "selector memoization", "normalized state", "action/reducer pattern".

---

### The Deep Dive & Solution

#### Approach 1: Service-based State with Signals (Recommended for Small-Medium Apps)

```typescript
// order-state.service.ts — A reactive state service using Signals
@Injectable({ providedIn: 'root' })
export class OrderStateService {
  
  private http = inject(HttpClient);
  
  // Private writable signals — only this service can modify state
  private _orders = signal<Order[]>([]);
  private _selectedOrder = signal<Order | null>(null);
  private _loading = signal(false);
  private _error = signal<string | null>(null);
  private _filters = signal<OrderFilters>({ status: 'ALL', dateRange: 'LAST_30_DAYS' });
  
  // Public read-only signals — components can only read, not write
  readonly orders = this._orders.asReadonly();
  readonly selectedOrder = this._selectedOrder.asReadonly();
  readonly loading = this._loading.asReadonly();
  readonly error = this._error.asReadonly();
  readonly filters = this._filters.asReadonly();
  
  // Computed (derived) state
  readonly filteredOrders = computed(() => {
    const orders = this._orders();
    const filters = this._filters();
    
    let result = orders;
    if (filters.status !== 'ALL') {
      result = result.filter(o => o.status === filters.status);
    }
    return result;
  });
  
  readonly orderCount = computed(() => this.filteredOrders().length);
  readonly totalRevenue = computed(() => 
    this.filteredOrders().reduce((sum, o) => sum + o.totalAmount, 0)
  );
  
  // Actions — methods that modify state
  loadOrders() {
    this._loading.set(true);
    this._error.set(null);
    
    this.http.get<Order[]>('/api/orders').pipe(
      finalize(() => this._loading.set(false))
    ).subscribe({
      next: (orders) => this._orders.set(orders),
      error: (err) => this._error.set(err.message)
    });
  }
  
  selectOrder(orderId: string) {
    const order = this._orders().find(o => o.id === orderId) ?? null;
    this._selectedOrder.set(order);
  }
  
  updateFilters(filters: Partial<OrderFilters>) {
    this._filters.update(current => ({ ...current, ...filters }));
  }
  
  // Optimistic update — update UI immediately, rollback on failure
  updateOrderStatus(orderId: string, newStatus: OrderStatus) {
    const previousOrders = this._orders();  // Save current state for rollback
    
    // Optimistic update — UI shows the change immediately
    this._orders.update(orders => 
      orders.map(o => o.id === orderId ? { ...o, status: newStatus } : o)
    );
    
    // API call — if it fails, rollback
    this.http.patch(`/api/orders/${orderId}`, { status: newStatus }).subscribe({
      error: (err) => {
        this._orders.set(previousOrders);  // Rollback
        this._error.set('Failed to update order status. Please try again.');
      }
    });
  }
}

// Usage in a component — clean and simple
@Component({
  selector: 'app-order-list',
  standalone: true,
  imports: [OrderCardComponent, LoadingSpinnerComponent, CurrencyPipe],
  template: `
    @if (state.loading()) {
      <app-loading-spinner />
    } @else if (state.error()) {
      <div class="error-banner">
        {{ state.error() }}
        <button (click)="state.loadOrders()">Retry</button>
      </div>
    } @else {
      <div class="stats">
        <span>{{ state.orderCount() }} orders</span>
        <span>Total: {{ state.totalRevenue() | currency }}</span>
      </div>
      
      @for (order of state.filteredOrders(); track order.id) {
        <app-order-card 
          [order]="order" 
          (onSelect)="state.selectOrder($event)" />
      } @empty {
        <p>No orders match your filters.</p>
      }
    }
  `,
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class OrderListComponent implements OnInit {
  state = inject(OrderStateService);
  
  ngOnInit() {
    this.state.loadOrders();
  }
}
```

#### Approach 2: NgRx Signal Store (Recommended for Large Enterprise Apps — Angular 17+)

NgRx Signal Store is the modern, simplified version of NgRx that uses Signals instead of RxJS for state management. It retains the benefits of NgRx (predictable state, dev tools, middleware) while dramatically reducing boilerplate.

```typescript
// order.store.ts — NgRx Signal Store
import { signalStore, withState, withComputed, withMethods, patchState } from '@ngrx/signals';
import { rxMethod } from '@ngrx/signals/rxjs-interop';

// State interface
interface OrderState {
  orders: Order[];
  selectedOrderId: string | null;
  loading: boolean;
  error: string | null;
  filters: OrderFilters;
}

// Initial state
const initialState: OrderState = {
  orders: [],
  selectedOrderId: null,
  loading: false,
  error: null,
  filters: { status: 'ALL', dateRange: 'LAST_30_DAYS' }
};

// The store — single source of truth for order state
export const OrderStore = signalStore(
  { providedIn: 'root' },
  
  // Define state shape
  withState(initialState),
  
  // Computed properties (derived from state)
  withComputed((store) => ({
    filteredOrders: computed(() => {
      const orders = store.orders();
      const filters = store.filters();
      if (filters.status === 'ALL') return orders;
      return orders.filter(o => o.status === filters.status);
    }),
    selectedOrder: computed(() => 
      store.orders().find(o => o.id === store.selectedOrderId()) ?? null
    ),
    orderCount: computed(() => store.orders().length),
  })),
  
  // Methods (actions that modify state)
  withMethods((store, http = inject(HttpClient)) => ({
    
    // Synchronous state update
    selectOrder(orderId: string) {
      patchState(store, { selectedOrderId: orderId });
    },
    
    updateFilters(filters: Partial<OrderFilters>) {
      patchState(store, (state) => ({
        filters: { ...state.filters, ...filters }
      }));
    },
    
    // Async method using rxMethod for RxJS integration
    loadOrders: rxMethod<void>(
      pipe(
        tap(() => patchState(store, { loading: true, error: null })),
        switchMap(() => http.get<Order[]>('/api/orders').pipe(
          tapResponse({
            next: (orders) => patchState(store, { orders, loading: false }),
            error: (err: HttpErrorResponse) => patchState(store, { 
              error: err.message, 
              loading: false 
            })
          })
        ))
      )
    ),
    
    // Optimistic update
    updateOrderStatus(orderId: string, newStatus: OrderStatus) {
      const previousOrders = store.orders();
      
      // Optimistic update
      patchState(store, (state) => ({
        orders: state.orders.map(o => 
          o.id === orderId ? { ...o, status: newStatus } : o
        )
      }));
      
      http.patch(`/api/orders/${orderId}`, { status: newStatus }).subscribe({
        error: () => {
          patchState(store, { orders: previousOrders, error: 'Update failed' });
        }
      });
    }
  }))
);

// Usage in component — inject the store directly
@Component({
  selector: 'app-order-dashboard',
  standalone: true,
  template: `
    <h1>Orders ({{ store.orderCount() }})</h1>
    
    @if (store.loading()) {
      <app-loading-spinner />
    } @else {
      @for (order of store.filteredOrders(); track order.id) {
        <app-order-card [order]="order" (onSelect)="store.selectOrder($event)" />
      }
    }
  `,
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class OrderDashboardComponent implements OnInit {
  store = inject(OrderStore);
  
  ngOnInit() {
    this.store.loadOrders();
  }
}
```

**When to use which approach**:

| Approach | When to Use |
|----------|-------------|
| **Service + Signals** | Small-medium apps (< 50 components), simple state, small team |
| **NgRx Signal Store** | Large enterprise apps, complex state, large team (5+), need devtools and middleware |
| **Classic NgRx (Actions/Reducers)** | Legacy codebases already using it; new projects should prefer Signal Store |

---

## 6.3 RxJS Mastery: Operators Every Lead Must Know

---

### The "Why" & The Problem

RxJS (Reactive Extensions for JavaScript) is the backbone of Angular's async operations. Every HTTP call, every form value change, every WebSocket message, and every router event is an Observable. A Lead who cannot fluently compose RxJS operators will write inefficient, buggy, and unmaintainable async code.

The most common production bugs caused by poor RxJS knowledge:
- **Memory leaks**: Subscribing in `ngOnInit` but never unsubscribing in `ngOnDestroy`. Over time, the app accumulates hundreds of active subscriptions that continue firing callbacks to destroyed components.
- **Race conditions**: User types "cat", then "car". The search for "cat" takes longer than the search for "car". Without `switchMap`, the "cat" results arrive AFTER the "car" results and overwrite them. The user searched for "car" but sees "cat" results.
- **Duplicate API calls**: User clicks "Submit" 5 times. Without `exhaustMap`, 5 identical API calls are made.

---

### Interviewer Expectations

- **Key operators**: `switchMap`, `mergeMap`, `concatMap`, `exhaustMap` — know the difference and when to use each. This is the #1 RxJS interview question.
- **Combination operators**: `combineLatest`, `forkJoin`, `withLatestFrom`, `merge`.
- **Filtering operators**: `debounceTime`, `distinctUntilChanged`, `filter`, `take`, `takeUntil`.
- **Memory management**: `takeUntilDestroyed()`, `async` pipe, `DestroyRef`.
- **Keywords**: "Hot vs. cold observable", "higher-order observable", "backpressure", "marble diagrams", "operator composition", "subscription leak prevention".

---

### The Deep Dive & Solution

#### The Four "Map" Operators — The Most Important RxJS Concept

All four operators take an outer Observable and map each emission to an inner Observable. They differ in how they handle **concurrent inner Observables**.

```typescript
// The scenario: User types in a search box. Each keystroke triggers an API call.
// User types: "a" → "ab" → "abc"

// 1. switchMap — CANCEL previous, only keep latest (MOST COMMON)
// Use for: Search/typeahead, navigation, any case where only the latest matters
this.searchControl.valueChanges.pipe(
  debounceTime(300),
  distinctUntilChanged(),
  switchMap(query => this.http.get(`/api/search?q=${query}`))
  // When "abc" is typed, the in-flight request for "ab" is CANCELLED
  // Only the "abc" request runs to completion
).subscribe(results => this.results.set(results));

// 2. mergeMap (flatMap) — Run ALL concurrently, no cancellation
// Use for: Independent operations where order doesn't matter (batch processing)
this.selectedUserIds$.pipe(
  mergeMap(userId => this.http.get(`/api/users/${userId}`))
  // All requests run simultaneously. Results arrive in completion order (not input order).
).subscribe(user => this.users.update(list => [...list, user]));

// 3. concatMap — Run ONE at a time, in order, wait for previous to complete
// Use for: Sequential operations where order matters (form submissions, file uploads)
this.filesToUpload$.pipe(
  concatMap(file => this.http.post('/api/upload', file))
  // File 1 must finish uploading before File 2 starts
  // Preserves order: results arrive in the same order as inputs
).subscribe(response => console.log('Uploaded:', response.filename));

// 4. exhaustMap — IGNORE new emissions while current is in progress
// Use for: Submit buttons (prevent duplicate submissions)
this.submitButton$.pipe(
  exhaustMap(() => this.http.post('/api/orders', this.orderForm.value))
  // User clicks Submit 5 times rapidly → only the FIRST click triggers an API call
  // Clicks 2-5 are silently ignored until the first call completes
).subscribe(order => this.router.navigate(['/orders', order.id]));
```

**Visual comparison (marble diagrams)**:

```
Input:        --a--------b--------c--|

switchMap:    --[a-http]--X
              -----------[b-http]--X
              --------------------[c-http]-->  (only c's result emitted)

mergeMap:     --[a-http]------>
              -----------[b-http]------>
              --------------------[c-http]-->  (all three results emitted)

concatMap:    --[a-http]------>[b-http]------>[c-http]-->  (sequential)

exhaustMap:   --[a-http]------>(b ignored)(c runs)-->  (b dropped while a was running)
```

#### Combination Operators

```typescript
// combineLatest — Emit when ANY source emits (after all have emitted at least once)
// Use for: Combining multiple filters/inputs that independently change
const filters$ = combineLatest([
  this.statusFilter$,     // Signal/Observable for status filter
  this.dateFilter$,       // Signal/Observable for date filter  
  this.searchQuery$       // Signal/Observable for search query
]).pipe(
  debounceTime(300),  // Wait for rapid filter changes to settle
  switchMap(([status, date, query]) => 
    this.http.get('/api/orders', { params: { status, date, query } })
  )
);

// forkJoin — Wait for ALL Observables to complete, emit once
// Use for: Parallel API calls where you need all results before proceeding
// Like Promise.all() for Observables
const dashboardData$ = forkJoin({
  orders: this.http.get<Order[]>('/api/orders'),
  stats: this.http.get<Stats>('/api/stats'),
  notifications: this.http.get<Notification[]>('/api/notifications')
}).subscribe(({ orders, stats, notifications }) => {
  // All three responses are available here
  this.orders.set(orders);
  this.stats.set(stats);
  this.notifications.set(notifications);
});

// withLatestFrom — When source emits, grab the latest value from other Observables
// Use for: "When this happens, also use the latest value of that"
this.saveButton$.pipe(
  withLatestFrom(this.formValue$, this.userId$),
  // Only emits when saveButton$ emits; grabs latest form value and userId
  switchMap(([_, formValue, userId]) => 
    this.http.put(`/api/users/${userId}`, formValue)
  )
);
```

#### Preventing Memory Leaks — The Modern Way

```typescript
// Method 1: takeUntilDestroyed() — Angular 16+ (RECOMMENDED)
@Component({ /* ... */ })
export class OrderListComponent {
  private destroyRef = inject(DestroyRef);
  
  ngOnInit() {
    this.orderService.orders$.pipe(
      takeUntilDestroyed(this.destroyRef)
      // Automatically unsubscribes when component is destroyed
    ).subscribe(orders => this.orders.set(orders));
  }
}

// Method 2: async pipe in template — auto-unsubscribes (BEST for template bindings)
@Component({
  template: `
    @if (orders$ | async; as orders) {
      @for (order of orders; track order.id) {
        <app-order-card [order]="order" />
      }
    }
  `
})
export class OrderListComponent {
  orders$ = this.http.get<Order[]>('/api/orders');
  // No subscribe(), no unsubscribe() — the async pipe handles everything
}

// Method 3: toSignal() — Convert Observable to Signal (Angular 16+)
@Component({
  template: `
    @for (order of orders(); track order.id) {
      <app-order-card [order]="order" />
    }
  `
})
export class OrderListComponent {
  private http = inject(HttpClient);
  
  orders = toSignal(
    this.http.get<Order[]>('/api/orders'),
    { initialValue: [] }
  );
  // No subscribe needed. Signal auto-updates when Observable emits.
  // Auto-unsubscribes on component destroy.
}
```

---

## 6.4 Routing, Lazy Loading, and Guards

---

### The "Why" & The Problem

In a single-page application (SPA), routing determines which component is displayed for a given URL. Proper routing architecture is critical for:
- **Bundle size**: Without lazy loading, ALL feature modules are included in the initial bundle. Users download JavaScript for features they may never visit.
- **Security**: Without route guards, users can access admin pages by manually typing the URL.
- **User experience**: Proper routing enables deep linking (bookmarkable URLs), browser back/forward navigation, and loading indicators during navigation.

---

### Interviewer Expectations

- **Lazy loading**: Know how `loadComponent` and `loadChildren` defer the loading of feature modules until the user navigates to them. This reduces the initial bundle size by 50-70%.
- **Route Guards**: `canActivate`, `canDeactivate`, `canMatch`, `resolve`. Functional guards (Angular 15+) vs. class-based guards.
- **Route resolvers**: Pre-fetching data before a component loads, so the component never shows an empty state.
- **Keywords**: "Lazy loading with loadComponent", "functional guard", "route resolver", "preloading strategy", "code splitting", "initial bundle size".

---

### The Deep Dive & Solution

```typescript
// app.routes.ts — Root routes with lazy loading
export const routes: Routes = [
  {
    path: '',
    component: LayoutComponent,
    children: [
      {
        path: '',
        redirectTo: 'dashboard',
        pathMatch: 'full'
      },
      {
        path: 'dashboard',
        loadComponent: () => import('./features/dashboard/dashboard.component')
          .then(m => m.DashboardComponent),
        // Dashboard component is loaded ONLY when user navigates to /dashboard
        // It's in a separate JavaScript chunk, not in the initial bundle
      },
      {
        path: 'orders',
        loadChildren: () => import('./features/orders/order.routes')
          .then(m => m.ORDER_ROUTES),
        // Entire orders feature (list, detail, create) loaded as one chunk
        canActivate: [authGuard],  // Functional guard — must be logged in
      },
      {
        path: 'admin',
        loadChildren: () => import('./features/admin/admin.routes')
          .then(m => m.ADMIN_ROUTES),
        canActivate: [authGuard, roleGuard('ADMIN')],  // Must be admin
      }
    ]
  },
  {
    path: 'login',
    loadComponent: () => import('./features/auth/login.component')
      .then(m => m.LoginComponent)
  },
  {
    path: '**',
    loadComponent: () => import('./shared/components/not-found.component')
      .then(m => m.NotFoundComponent)
  }
];

// order.routes.ts — Feature routes
export const ORDER_ROUTES: Routes = [
  {
    path: '',
    component: OrderListComponent,
    title: 'Orders'  // Sets the browser tab title
  },
  {
    path: 'create',
    loadComponent: () => import('./order-create/order-create.component')
      .then(m => m.OrderCreateComponent),
    canDeactivate: [unsavedChangesGuard],  // Warn before leaving with unsaved changes
    title: 'Create Order'
  },
  {
    path: ':id',
    loadComponent: () => import('./order-detail/order-detail.component')
      .then(m => m.OrderDetailComponent),
    resolve: { order: orderResolver },  // Pre-fetch order data before component loads
    title: (route) => `Order #${route.paramMap.get('id')}`
  }
];

// Functional guards (Angular 15+) — simple functions, no classes
export const authGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);
  
  if (authService.isAuthenticated()) {
    return true;
  }
  
  // Redirect to login with return URL
  return router.createUrlTree(['/login'], {
    queryParams: { returnUrl: state.url }
  });
};

// Higher-order guard — factory function that returns a guard
export function roleGuard(requiredRole: string): CanActivateFn {
  return (route, state) => {
    const authService = inject(AuthService);
    return authService.hasRole(requiredRole);
  };
}

// Unsaved changes guard
export const unsavedChangesGuard: CanDeactivateFn<{ hasUnsavedChanges: () => boolean }> = 
  (component) => {
    if (component.hasUnsavedChanges()) {
      return confirm('You have unsaved changes. Are you sure you want to leave?');
    }
    return true;
  };

// Route resolver — pre-fetch data
export const orderResolver: ResolveFn<Order> = (route) => {
  const orderService = inject(OrderService);
  const orderId = route.paramMap.get('id')!;
  return orderService.getOrder(orderId);
  // The component receives the resolved data via ActivatedRoute:
  // this.route.data.subscribe(data => data['order'])
};
```

---

## 6.5 Angular HTTP Client, Interceptors, and Error Handling

---

### The "Why" & The Problem

Every Angular application communicates with a backend API via HTTP. Without proper HTTP architecture, you end up with duplicated error handling code in every component, no centralized authentication token management, and no way to retry failed requests or show loading indicators globally.

---

### Interviewer Expectations

- **Functional interceptors** (Angular 15+): Know the new `HttpInterceptorFn` pattern. Explain how to chain interceptors for auth, error handling, logging, and caching.
- **Error handling strategy**: Global error handler vs. per-request error handling. HTTP error interceptor that shows toast notifications.
- **Keywords**: "Functional interceptor", "withInterceptors", "retry strategy", "error boundary", "global error handler", "optimistic update".

---

### The Deep Dive & Solution

```typescript
// auth.interceptor.ts — Adds JWT token to every request
export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);
  const token = authService.getAccessToken();
  
  if (token && !req.url.includes('/auth/login')) {
    const clonedReq = req.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`
      }
    });
    return next(clonedReq);
  }
  
  return next(req);
};

// error-handler.interceptor.ts — Global HTTP error handling
export const errorHandlerInterceptor: HttpInterceptorFn = (req, next) => {
  const router = inject(Router);
  const toastService = inject(ToastService);
  const authService = inject(AuthService);
  
  return next(req).pipe(
    retry({
      count: 2,
      delay: (error, retryCount) => {
        // Only retry on 5xx errors and network errors
        if (error.status >= 500 || error.status === 0) {
          return timer(1000 * retryCount);  // Exponential backoff: 1s, 2s
        }
        return throwError(() => error);  // Don't retry 4xx errors
      }
    }),
    catchError((error: HttpErrorResponse) => {
      switch (error.status) {
        case 401:
          // Token expired — try to refresh
          return authService.refreshToken().pipe(
            switchMap(newToken => {
              const retryReq = req.clone({
                setHeaders: { Authorization: `Bearer ${newToken}` }
              });
              return next(retryReq);
            }),
            catchError(() => {
              authService.logout();
              router.navigate(['/login']);
              return throwError(() => error);
            })
          );
        
        case 403:
          toastService.error('You do not have permission to perform this action.');
          break;
        
        case 404:
          // Don't show toast for 404s — let the component handle it
          break;
        
        case 429:
          toastService.warning('Too many requests. Please wait a moment.');
          break;
        
        case 0:
          toastService.error('Network error. Please check your connection.');
          break;
        
        default:
          if (error.status >= 500) {
            toastService.error('Server error. Our team has been notified.');
          }
      }
      
      return throwError(() => error);
    })
  );
};

// logging.interceptor.ts — Log all HTTP requests for debugging
export const loggingInterceptor: HttpInterceptorFn = (req, next) => {
  const startTime = Date.now();
  
  return next(req).pipe(
    tap({
      next: (event) => {
        if (event instanceof HttpResponse) {
          const duration = Date.now() - startTime;
          console.log(`[HTTP] ${req.method} ${req.url} → ${event.status} (${duration}ms)`);
        }
      },
      error: (error) => {
        const duration = Date.now() - startTime;
        console.error(`[HTTP] ${req.method} ${req.url} → ${error.status} (${duration}ms)`, error);
      }
    })
  );
};

// Register interceptors in app.config.ts
export const appConfig: ApplicationConfig = {
  providers: [
    provideHttpClient(
      withInterceptors([
        loggingInterceptor,        // Outermost — logs everything
        authInterceptor,           // Adds auth token
        errorHandlerInterceptor,   // Handles errors, retries
      ])
    ),
    provideRouter(routes, withPreloading(PreloadAllModules)),
    provideAnimationsAsync(),
  ]
};
```

---

## 6.6 Angular Performance Optimization

---

### The "Why" & The Problem

A performant Angular application should:
- **Load fast**: First Contentful Paint (FCP) under 1.5 seconds on 4G.
- **Feel responsive**: No visible jank when scrolling, no lag on input.
- **Handle large data sets**: Render 10,000 rows without freezing the browser.

---

### Interviewer Expectations

- **Bundle optimization**: Tree shaking, lazy loading, preloading strategies.
- **Runtime performance**: OnPush change detection, `trackBy` in `@for`, virtual scrolling for long lists.
- **Network performance**: HTTP caching, compression, CDN.
- **Keywords**: "Tree shaking", "code splitting", "virtual scroll (CDK)", "OnPush", "lazy loading", "web vitals (LCP, FID, CLS)", "preloading strategy".

---

### The Deep Dive & Solution

```typescript
// Virtual Scrolling — render only visible items from a list of 10,000+
import { CdkVirtualScrollViewport, CdkFixedSizeVirtualScroll, CdkVirtualForOf } from '@angular/cdk/scrolling';

@Component({
  selector: 'app-large-order-list',
  standalone: true,
  imports: [CdkVirtualScrollViewport, CdkFixedSizeVirtualScroll, CdkVirtualForOf, OrderCardComponent],
  template: `
    <cdk-virtual-scroll-viewport itemSize="80" class="order-viewport">
      <app-order-card 
        *cdkVirtualFor="let order of orders(); trackBy: trackById"
        [order]="order" />
    </cdk-virtual-scroll-viewport>
  `,
  styles: [`
    .order-viewport {
      height: 600px;  /* Fixed height container */
      width: 100%;
    }
  `],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class LargeOrderListComponent {
  orders = input.required<Order[]>();  // Can be 10,000+ items
  
  trackById(index: number, order: Order): string {
    return order.id;  // Helps Angular reuse DOM elements
  }
  // Only ~10-15 items are actually rendered in the DOM at any time
  // As the user scrolls, items are recycled — massively faster than rendering all 10,000
}

// Preloading strategy — load lazy routes in the background after initial load
// This gives you the best of both worlds: fast initial load + instant navigation later
export const appConfig: ApplicationConfig = {
  providers: [
    provideRouter(
      routes,
      withPreloading(PreloadAllModules)  // Preload all lazy routes after initial load
      // Or use a custom strategy:
      // withPreloading(CustomPreloadingStrategy) — only preload critical routes
    )
  ]
};
```

---

## 6.7 Reactive Forms, Validation, and Type-Safe Forms

---

### The "Why" & The Problem

Forms are the primary way users input data. In an enterprise application (CRM, ERP, order management), forms can be extremely complex: 50+ fields, dynamic sections that appear/disappear based on selections, cross-field validation, async validation (check if email already exists), and multi-step wizards. Without proper form architecture, these become unmaintainable.

---

### Interviewer Expectations

- **Reactive Forms**: Know `FormGroup`, `FormControl`, `FormArray`, `FormBuilder`. Explain why Reactive Forms are preferred over Template-Driven forms for complex forms.
- **Typed Forms** (Angular 14+): Strongly typed `FormGroup<T>` where TypeScript enforces the form structure at compile time.
- **Custom validators**: Both synchronous and asynchronous (e.g., check username availability via API).
- **Keywords**: "Reactive forms", "FormGroup", "typed forms", "custom validator", "async validator", "cross-field validation", "dynamic form", "form array".

---

### The Deep Dive & Solution

```typescript
// Typed Reactive Form — Angular 14+
interface OrderForm {
  customerId: FormControl<string>;
  items: FormArray<FormGroup<OrderItemForm>>;
  shippingAddress: FormGroup<AddressForm>;
  notes: FormControl<string | null>;
  expedited: FormControl<boolean>;
}

interface OrderItemForm {
  productId: FormControl<string>;
  quantity: FormControl<number>;
  price: FormControl<number>;
}

interface AddressForm {
  street: FormControl<string>;
  city: FormControl<string>;
  state: FormControl<string>;
  zip: FormControl<string>;
}

@Component({
  selector: 'app-create-order',
  standalone: true,
  imports: [ReactiveFormsModule],
  template: `
    <form [formGroup]="orderForm" (ngSubmit)="onSubmit()">
      <div formGroupName="shippingAddress">
        <label>Street</label>
        <input formControlName="street" />
        @if (orderForm.controls.shippingAddress.controls.street.errors?.['required'] 
             && orderForm.controls.shippingAddress.controls.street.touched) {
          <span class="error">Street is required</span>
        }
        
        <label>City</label>
        <input formControlName="city" />
        
        <label>Zip</label>
        <input formControlName="zip" />
        @if (orderForm.controls.shippingAddress.controls.zip.errors?.['pattern']) {
          <span class="error">Invalid ZIP code format</span>
        }
      </div>
      
      <h3>Order Items</h3>
      @for (item of orderForm.controls.items.controls; track $index) {
        <div [formGroupName]="$index" class="item-row">
          <input formControlName="productId" placeholder="Product ID" />
          <input formControlName="quantity" type="number" min="1" />
          <input formControlName="price" type="number" step="0.01" />
          <button type="button" (click)="removeItem($index)">Remove</button>
        </div>
      }
      <button type="button" (click)="addItem()">Add Item</button>
      
      <div>
        <label>
          <input type="checkbox" formControlName="expedited" />
          Expedited Shipping
        </label>
      </div>
      
      <textarea formControlName="notes" placeholder="Order notes (optional)"></textarea>
      
      <div class="summary">
        <p>Total: {{ calculateTotal() | currency }}</p>
      </div>
      
      <button type="submit" [disabled]="orderForm.invalid || submitting()">
        {{ submitting() ? 'Submitting...' : 'Create Order' }}
      </button>
    </form>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class CreateOrderComponent {
  private fb = inject(NonNullableFormBuilder);
  private orderService = inject(OrderService);
  
  submitting = signal(false);
  
  // Fully typed form — TypeScript knows the exact shape
  orderForm = this.fb.group<OrderForm>({
    customerId: this.fb.control('', [Validators.required]),
    items: this.fb.array<FormGroup<OrderItemForm>>([this.createItemGroup()]),
    shippingAddress: this.fb.group<AddressForm>({
      street: this.fb.control('', [Validators.required]),
      city: this.fb.control('', [Validators.required]),
      state: this.fb.control('', [Validators.required]),
      zip: this.fb.control('', [Validators.required, Validators.pattern(/^\d{5}(-\d{4})?$/)]),
    }),
    notes: this.fb.control(null),
    expedited: this.fb.control(false),
  });
  
  createItemGroup(): FormGroup<OrderItemForm> {
    return this.fb.group({
      productId: this.fb.control('', [Validators.required]),
      quantity: this.fb.control(1, [Validators.required, Validators.min(1)]),
      price: this.fb.control(0, [Validators.required, Validators.min(0.01)]),
    });
  }
  
  addItem() {
    this.orderForm.controls.items.push(this.createItemGroup());
  }
  
  removeItem(index: number) {
    this.orderForm.controls.items.removeAt(index);
  }
  
  calculateTotal(): number {
    return this.orderForm.controls.items.controls.reduce((total, item) => {
      return total + (item.controls.quantity.value * item.controls.price.value);
    }, 0);
  }
  
  onSubmit() {
    if (this.orderForm.valid) {
      this.submitting.set(true);
      const formValue = this.orderForm.getRawValue();  // Fully typed!
      
      this.orderService.createOrder(formValue).subscribe({
        next: (order) => {
          // Navigate to order detail
        },
        error: (err) => {
          this.submitting.set(false);
        }
      });
    } else {
      // Mark all fields as touched to show validation errors
      this.orderForm.markAllAsTouched();
    }
  }
  
  // Check if form has unsaved changes (for CanDeactivate guard)
  hasUnsavedChanges(): boolean {
    return this.orderForm.dirty;
  }
}

// Custom async validator — check if customer exists
export function customerExistsValidator(
  customerService: CustomerService
): AsyncValidatorFn {
  return (control: AbstractControl): Observable<ValidationErrors | null> => {
    if (!control.value) return of(null);
    
    return customerService.checkExists(control.value).pipe(
      map(exists => exists ? null : { customerNotFound: true }),
      catchError(() => of(null))  // Don't block form on API errors
    );
  };
}
```

---

## 6.8 Angular Testing: Unit Tests and Component Tests

---

### The "Why" & The Problem

A Lead Engineer is responsible for the quality strategy of the frontend codebase. Untested Angular components are ticking time bombs — every refactoring is a gamble, every merge is a prayer. But Angular testing is also notorious for being verbose and flaky if done poorly.

---

### Interviewer Expectations

- **Component testing**: Know how to use `TestBed`, `ComponentFixture`, and `ComponentHarness`. Test component behavior (what the user sees and does), not internal implementation details.
- **Service testing**: Test services with mocked `HttpClient` using `HttpTestingController`.
- **Keywords**: "TestBed", "ComponentFixture", "HttpTestingController", "component harness", "testing library (Angular)", "arrange-act-assert", "mock vs. spy".

---

### The Deep Dive & Solution

```typescript
// Testing a component with signals and services
describe('OrderListComponent', () => {
  let component: OrderListComponent;
  let fixture: ComponentFixture<OrderListComponent>;
  let orderService: jasmine.SpyObj<OrderStateService>;
  
  beforeEach(async () => {
    // Create mock service with Signals
    const mockOrderService = jasmine.createSpyObj('OrderStateService', 
      ['loadOrders', 'selectOrder'],
      {
        orders: signal<Order[]>([]),
        loading: signal(false),
        error: signal<string | null>(null),
        filteredOrders: signal<Order[]>([]),
        orderCount: signal(0),
      }
    );
    
    await TestBed.configureTestingModule({
      imports: [OrderListComponent],  // Standalone component
      providers: [
        { provide: OrderStateService, useValue: mockOrderService }
      ]
    }).compileComponents();
    
    fixture = TestBed.createComponent(OrderListComponent);
    component = fixture.componentInstance;
    orderService = TestBed.inject(OrderStateService) as jasmine.SpyObj<OrderStateService>;
  });
  
  it('should show loading spinner when loading', () => {
    (orderService.loading as any).set(true);
    fixture.detectChanges();
    
    const spinner = fixture.nativeElement.querySelector('app-loading-spinner');
    expect(spinner).toBeTruthy();
  });
  
  it('should display orders when loaded', () => {
    const mockOrders: Order[] = [
      { id: '1', status: 'PENDING', totalAmount: 99.99, orderDate: new Date() },
      { id: '2', status: 'SHIPPED', totalAmount: 149.99, orderDate: new Date() },
    ];
    
    (orderService.filteredOrders as any).set(mockOrders);
    (orderService.orderCount as any).set(2);
    fixture.detectChanges();
    
    const orderCards = fixture.nativeElement.querySelectorAll('app-order-card');
    expect(orderCards.length).toBe(2);
  });
  
  it('should show empty state when no orders', () => {
    (orderService.filteredOrders as any).set([]);
    fixture.detectChanges();
    
    const emptyMessage = fixture.nativeElement.querySelector('p');
    expect(emptyMessage.textContent).toContain('No orders');
  });
  
  it('should call loadOrders on init', () => {
    fixture.detectChanges();  // Triggers ngOnInit
    expect(orderService.loadOrders).toHaveBeenCalled();
  });
});

// Testing an HTTP service
describe('OrderService', () => {
  let service: OrderService;
  let httpMock: HttpTestingController;
  
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        OrderService,
        provideHttpClient(),
        provideHttpClientTesting()
      ]
    });
    
    service = TestBed.inject(OrderService);
    httpMock = TestBed.inject(HttpTestingController);
  });
  
  afterEach(() => {
    httpMock.verify();  // Ensure no unexpected HTTP calls
  });
  
  it('should fetch orders', () => {
    const mockOrders: Order[] = [
      { id: '1', status: 'PENDING', totalAmount: 99.99, orderDate: new Date() }
    ];
    
    service.getOrders().subscribe(orders => {
      expect(orders.length).toBe(1);
      expect(orders[0].id).toBe('1');
    });
    
    const req = httpMock.expectOne('/api/orders');
    expect(req.request.method).toBe('GET');
    req.flush(mockOrders);  // Respond with mock data
  });
  
  it('should handle errors', () => {
    service.getOrders().subscribe({
      error: (err) => {
        expect(err.status).toBe(500);
      }
    });
    
    const req = httpMock.expectOne('/api/orders');
    req.flush('Server Error', { status: 500, statusText: 'Internal Server Error' });
  });
});
```


