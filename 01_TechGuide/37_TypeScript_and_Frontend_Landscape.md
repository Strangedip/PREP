# Section 37: TypeScript & Frontend Landscape (2026)

> **Level**: ALL — Complements [08_Angular_Frontend_Engineering.md](./08_Angular_Frontend_Engineering.md)
> **Purpose**: Speak confidently about modern frontend even when your primary role is backend/Java

> **You are here**: Fresher — Technical Skills
> **Roadmap**: [Developer Master Roadmap](../ROADMAP.md) | **Prerequisites**: [36_Polyglot_Interview_Python_and_Go.md](36_Polyglot_Interview_Python_and_Go.md) | **Next**: [38_Compliance_and_Regulated_Systems.md](38_Compliance_and_Regulated_Systems.md)

---

## 37.1 Frontend Stack Landscape

| Framework | Maintainer | Model | Typical Use |
|-----------|------------|-------|-------------|
| **Angular** | Google | Full framework, TypeScript-first | Enterprise, large teams |
| **React** | Meta | Library + ecosystem | Startups, SPAs, React Native |
| **Vue** | Community | Progressive framework | Mid-size apps, fast onboarding |
| **Svelte** | Community | Compile-time | Performance-sensitive UIs |

**This repo's depth**: Angular in §08. This section gives **breadth** for cross-team and full-stack interviews.

---

## 37.2 TypeScript Essentials

```typescript
// Types
interface User {
  id: string;
  name: string;
  role?: 'admin' | 'user';  // optional + union
}

type ApiResponse<T> = { data: T } | { error: string };

// Generics
function first<T>(arr: T[]): T | undefined {
  return arr[0];
}

// Utility types
type PartialUser = Partial<User>;
type UserKeys = keyof User;

// async/await (same mental model as Java)
async function fetchUser(id: string): Promise<User> {
  const res = await fetch(`/api/users/${id}`);
  if (!res.ok) throw new Error('Not found');
  return res.json();
}
```

| Concept | TypeScript | Java |
|---------|------------|------|
| Structural typing | Interface match by shape | Nominal (class name) |
| Null safety | `strictNullChecks` | Optional, `@Nullable` |
| Union types | `string \| number` | Rare (sealed classes) |
| `any` | Escape hatch — avoid in prod | — |

---

## 37.3 React vs Angular (Interview Comparison)

| Aspect | Angular | React |
|--------|---------|-------|
| **Learning curve** | Steeper (RxJS, modules, DI) | Gentler entry |
| **State** | Signals, NgRx, services | useState, Redux, Zustand |
| **Templates** | HTML + directives | JSX |
| **Routing** | Built-in | React Router |
| **Forms** | Reactive Forms | Form libraries |
| **Enterprise fit** | Strong (structure, CLI) | Strong (flexibility, hiring pool) |

**When interviewer asks "Why Angular?"**: TypeScript-native, opinionated structure, built-in DI, enterprise patterns, long-term Google backing.

---

## 37.4 Modern Frontend Concerns (All Frameworks)

| Concern | What to Know |
|---------|--------------|
| **SSR / SSG** | Next.js (React), Angular Universal — SEO, first paint |
| **Bundling** | Vite, Webpack, esbuild — tree shaking, code splitting |
| **CSS** | Tailwind, CSS Modules, component-scoped styles |
| **API layer** | REST, GraphQL (Apollo), tRPC |
| **Auth** | OAuth2 PKCE in SPA, httpOnly cookies vs JWT in memory |
| **Performance** | Lazy loading, virtual scroll, memoization, Lighthouse |
| **Testing** | Jest/Vitest, Cypress/Playwright E2E |

---

## 37.5 Browser Security (Frontend Angle)

- **XSS**: Sanitize output, CSP, avoid `innerHTML` with user data
- **CSRF**: SameSite cookies, CSRF tokens for cookie-based auth
- **CORS**: Server must allow origin — not a "frontend bug"
- **Token storage**: Prefer httpOnly cookie or short-lived token in memory; avoid localStorage for sensitive JWT

Deep dive: [12_Security_OWASP_Cloud.md](./12_Security_OWASP_Cloud.md).

---

## 37.6 Interview Quick Reference

| Question | Answer |
|----------|--------|
| TypeScript benefit? | Static types, better IDE support, catch errors at compile time. |
| Angular Signals? | Fine-grained reactivity without RxJS for simple state. |
| Virtual DOM (React)? | Diff against previous tree, batch DOM updates. |
| SSR vs CSR? | SSR: server renders HTML (SEO, fast FCP). CSR: client renders after JS loads. |
| Monorepo frontend? | Nx, Turborepo — shared libs, one CI pipeline. |
| Micro-frontends? | Module Federation, single-spa — team-owned deployable UIs. |

---

## §37.10 Production & Interview Depth — Backend Engineer Frontend Literacy

Indian product companies (Freshworks, Postman, enterprise B2B) expect backend/Java hires to **review Angular or React PRs**, debug CORS in staging, and design BFF APIs. You are not becoming a frontend lead — you are passing **cross-functional** rounds and shipping safer contracts to [08_Angular_Frontend_Engineering.md](./08_Angular_Frontend_Engineering.md) teams.

### BFF Pattern for Spring Boot 3 + SPA

```
Browser → CloudFront → Angular/React SPA
              ↓ API calls (JSON)
         Spring Boot BFF (/api/v1/*)
              ↓ internal
    Microservices (orders, catalog, payments)
```

| Approach | Pros | Cons |
|----------|------|------|
| **SPA talks direct to microservices** | Fewer hops | CORS chaos, multiple auth flows, DTO leakage |
| **BFF per client** (web vs mobile) | Tailored payloads, hides domain | BFF obesity without ownership |
| **GraphQL gateway** | Flexible queries | N+1 resolvers, cache complexity — [21_GraphQL_and_Alternative_APIs.md](./21_GraphQL_and_Alternative_APIs.md) |
| **Server-driven UI** (niche) | Thin client | Not default in 2026 product shops |

### TypeScript API Contract (What Backend Should Publish)

```typescript
// Generated from OpenAPI — keep in sync with Springdoc
export interface OrderSummary {
  orderId: string;
  status: 'CREATED' | 'PAID' | 'SHIPPED' | 'DELIVERED';
  amountPaise: number;  // India: store money as integer paise in JSON
  placedAt: string;     // ISO-8601 UTC; UI formats to IST
}

export type ApiResult<T> =
  | { ok: true; data: T }
  | { ok: false; error: { code: string; message: string } };
```

Spring side: consistent error envelope in [04_API_Design_REST.md](./04_API_Design_REST.md); never return stack traces to the browser — [12_Security_OWASP_Cloud.md](./12_Security_OWASP_Cloud.md).

### Auth in Indian Consumer Apps

- **OTP login** + short-lived access token in memory; refresh via httpOnly cookie
- **UPI / payment redirects**: frontend handles return URL; backend verifies signature — see [PaymentSystem HLD](../04_SystemDesign/02_HighLevelDesign/PaymentSystem/PaymentSystem.md)
- **CORS**: explicit allowed origins for `*.yourcompany.in` staging vs prod

### Interview One-Liner

*"I'm backend-first but I read TypeScript interfaces, validate OpenAPI breaking changes in CI, and pair with frontend on pagination and error shapes — Angular Signals vs RxJS is their call, I own the API contract."*

**Must-say keywords**: BFF, OpenAPI codegen, paise integer money, httpOnly refresh, CORS preflight, CSP for XSS.

**Next**: Deep Angular → [08_Angular_Frontend_Engineering.md](./08_Angular_Frontend_Engineering.md).
