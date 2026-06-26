# Section 35: SQL Fundamentals & Query Mastery

> **Level**: ALL — Associate to Lead (everyone must know SQL; depth increases with level)
> **Complements**: [05_Database_Performance_Tuning.md](./05_Database_Performance_Tuning.md) (optimization), [26_PostgreSQL_Relational_DB_Deep_Dive.md](./26_PostgreSQL_Relational_DB_Deep_Dive.md) (internals)

---

## 35.1 Core SQL Operations

| Category | Commands |
|----------|----------|
| **DDL** | CREATE, ALTER, DROP, TRUNCATE |
| **DML** | SELECT, INSERT, UPDATE, DELETE |
| **DCL** | GRANT, REVOKE |
| **TCL** | COMMIT, ROLLBACK, BEGIN |

---

## 35.2 SELECT Essentials

```sql
SELECT u.name, COUNT(o.id) AS order_count
FROM users u
INNER JOIN orders o ON u.id = o.user_id
WHERE u.created_at >= '2026-01-01'
GROUP BY u.id, u.name
HAVING COUNT(o.id) > 5
ORDER BY order_count DESC
LIMIT 10;
```

**Execution order** (not written order):
`FROM → JOIN → WHERE → GROUP BY → HAVING → SELECT → ORDER BY → LIMIT`

---

## 35.3 JOIN Types

| Join | Returns |
|------|---------|
| **INNER** | Rows matching both tables |
| **LEFT** | All left rows + matching right (NULL if no match) |
| **RIGHT** | All right rows + matching left |
| **FULL OUTER** | All rows from both — NULL where no match |
| **CROSS** | Cartesian product — every combination |
| **SELF** | Table joined to itself |

```sql
-- Find users with NO orders
SELECT u.* FROM users u
LEFT JOIN orders o ON u.id = o.user_id
WHERE o.id IS NULL;
```

---

## 35.4 Subqueries vs JOINs

```sql
-- Subquery (often slower — runs per row or as separate step)
SELECT name FROM users
WHERE id IN (SELECT user_id FROM orders WHERE total > 1000);

-- JOIN (usually preferred — optimizer can plan better)
SELECT DISTINCT u.name
FROM users u
INNER JOIN orders o ON u.id = o.user_id
WHERE o.total > 1000;
```

**EXISTS** for existence checks:
```sql
SELECT * FROM users u
WHERE EXISTS (SELECT 1 FROM orders o WHERE o.user_id = u.id AND o.status = 'PENDING');
```

---

## 35.5 Window Functions (Senior-Level Must-Know)

```sql
-- Running total
SELECT order_date, amount,
       SUM(amount) OVER (ORDER BY order_date) AS running_total
FROM orders;

-- Rank employees by salary per department
SELECT name, department, salary,
       RANK() OVER (PARTITION BY department ORDER BY salary DESC) AS rank
FROM employees;

-- Compare to previous row
SELECT date, revenue,
       revenue - LAG(revenue) OVER (ORDER BY date) AS daily_change
FROM daily_sales;
```

| Function | Purpose |
|----------|---------|
| `ROW_NUMBER()` | Unique sequential number per partition |
| `RANK()` | Rank with gaps for ties |
| `DENSE_RANK()` | Rank without gaps |
| `LAG/LEAD` | Previous/next row value |
| `SUM() OVER` | Running/cumulative aggregates |

---

## 35.6 Common Table Expressions (CTE)

```sql
WITH monthly_sales AS (
    SELECT DATE_TRUNC('month', created_at) AS month,
           SUM(total) AS revenue
    FROM orders
    GROUP BY 1
),
growth AS (
    SELECT month, revenue,
           LAG(revenue) OVER (ORDER BY month) AS prev_revenue
    FROM monthly_sales
)
SELECT month, revenue,
       ROUND((revenue - prev_revenue) / prev_revenue * 100, 2) AS growth_pct
FROM growth
WHERE prev_revenue IS NOT NULL;
```

**Recursive CTE** — org hierarchies, bill of materials:
```sql
WITH RECURSIVE subordinates AS (
    SELECT id, name, manager_id, 1 AS depth
    FROM employees WHERE id = 1
    UNION ALL
    SELECT e.id, e.name, e.manager_id, s.depth + 1
    FROM employees e
    INNER JOIN subordinates s ON e.manager_id = s.id
)
SELECT * FROM subordinates;
```

---

## 35.7 Indexes — What Juniors Must Know

```sql
CREATE INDEX idx_orders_user_date ON orders (user_id, created_at DESC);
```

- Index speeds **WHERE**, **JOIN**, **ORDER BY** on indexed columns
- **Leftmost prefix rule** for composite indexes
- Don't index every column — slows writes

---

## 35.8 Normalization vs Denormalization

| Normal Form | Rule |
|-------------|------|
| **1NF** | No repeating groups; atomic values |
| **2NF** | No partial dependency on composite key |
| **3NF** | No transitive dependency |

**Denormalize** for read performance — duplicate data to avoid joins (reporting tables, caches).

---

## 35.9 Interview Quick Reference

| # | Question | One-Line Answer |
|---|----------|-----------------|
| 1 | INNER vs LEFT JOIN? | INNER: only matches. LEFT: all left rows, NULL for non-matching right. |
| 2 | WHERE vs HAVING? | WHERE filters rows before grouping. HAVING filters after GROUP BY. |
| 3 | UNION vs UNION ALL? | UNION removes duplicates (sort). UNION ALL keeps all rows — faster. |
| 4 | Window function vs GROUP BY? | Window: keeps all rows, adds aggregate column. GROUP BY: collapses rows. |
| 5 | Correlated subquery? | Subquery references outer query — runs per outer row, often slow. |
| 6 | N+1 in SQL context? | App runs 1 query + N queries for related data — fix with JOIN or batch. |
| 7 | DELETE vs TRUNCATE? | DELETE: row-by-row, can rollback, fires triggers. TRUNCATE: fast, resets table. |
| 8 | ACID? | Atomicity, Consistency, Isolation, Durability — transaction guarantees. |
| 9 | Primary vs unique key? | PK: not null, one per table, clustered index. Unique: allows one NULL. |
| 10 | Covering index? | Index contains all columns needed — index-only scan, no table fetch. |

**Must-say keywords**: JOIN types, window functions, CTE, execution order, HAVING, covering index, normalization, correlated subquery.
