# Section 37: TypeScript & Frontend Landscape (2026)

> **Level**: ALL — Complements [08_Angular_Frontend_Engineering.md](./08_Angular_Frontend_Engineering.md)
> **Purpose**: Speak confidently about modern frontend even when your primary role is backend/Java

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

**Next**: Deep Angular → [08_Angular_Frontend_Engineering.md](./08_Angular_Frontend_Engineering.md).
