# Section 33: Git, Version Control & Engineering Workflow

> **Level**: ALL — every engineer uses Git daily; interviews ask about workflows and conflict resolution

> **You are here**: Fresher — Engineering Practices
> **Roadmap**: [Developer Master Roadmap](../ROADMAP.md) | **Prerequisites**: [32_Operating_Systems_and_Linux.md](32_Operating_Systems_and_Linux.md) | **Next**: [34_Search_Engines_Elasticsearch.md](34_Search_Engines_Elasticsearch.md)

---

## 33.1 Git Fundamentals

```
Working Directory → git add → Staging Area → git commit → Local Repository
                                                              ↓
                                                    git push → Remote (GitHub)
```

| Command | Purpose |
|---------|---------|
| `git clone` | Copy remote repo |
| `git status` | Changed files |
| `git diff` | Line-by-line changes |
| `git log --oneline --graph` | Commit history |
| `git branch` / `git checkout -b` | Branch management |
| `git merge` / `git rebase` | Integrate branches |
| `git stash` | Temporarily save uncommitted work |
| `git cherry-pick` | Apply specific commit to current branch |
| `git revert` | Safe undo — new commit reversing changes |
| `git reset` | Move HEAD — `--soft`, `--mixed`, `--hard` |

---

## 33.2 Merge vs Rebase

| | Merge | Rebase |
|---|-------|--------|
| History | Preserves branch topology | Linear history |
| Commit graph | Merge commit | Rewrites commits |
| Safety | Non-destructive | Don't rebase public/shared branches |
| Use | Feature → main integration | Clean up local feature branch before PR |

**Golden rule**: Never rebase commits others have pulled.

---

## 33.3 Branching Strategies

### Trunk-Based Development (2026 default at many companies)
- Short-lived branches (< 1 day)
- Frequent merges to `main`
- Feature flags hide incomplete work
- CI runs on every commit

### Git Flow (legacy for release-heavy products)
- `main`, `develop`, `feature/*`, `release/*`, `hotfix/*`
- More ceremony — suited for versioned releases (mobile, desktop)

### GitHub Flow
- `main` + feature branches + PR
- Deploy from `main` after merge

---

## 33.4 Pull Request Best Practices

| Practice | Why |
|----------|-----|
| Small PRs (< 400 lines) | Faster review, easier rollback |
| Descriptive title + context | Reviewer understands intent |
| Link ticket/issue | Traceability |
| Self-review first | Catch obvious issues |
| CI green before review | Don't waste reviewer time |
| One logical change per PR | Easier revert |

---

## 33.5 Resolving Conflicts

```bash
git fetch origin
git merge origin/main
# CONFLICT in file.java
git status                    # see conflicted files
# Edit files — remove <<<<<<< ======= >>>>>>> markers
git add resolved-file.java
git commit
```

**Prevention**: Rebase feature branch on main frequently; communicate on shared files.

---

## 33.6 Git Internals (Interview Depth)

| Object | Content |
|--------|---------|
| **Blob** | File content (SHA-1 hash as ID) |
| **Tree** | Directory listing (pointers to blobs/trees) |
| **Commit** | Tree + parent commit + author + message |
| **Ref** | Branch/tag pointer to commit |

`.git/objects/` stores content-addressable storage — deduplication by hash.

---

## 33.7 Monorepo vs Polyrepo

| | Monorepo | Polyrepo |
|---|----------|----------|
| Structure | All services in one repo | One repo per service |
| Tools | Bazel, Nx, Turborepo | Independent CI per repo |
| Pros | Shared code, atomic changes | Clear boundaries, independent deploy |
| Examples | Google, Meta | Most startups |

---

## 33.8 Interview Quick Reference

| # | Question | One-Line Answer |
|---|----------|-----------------|
| 1 | git merge vs rebase? | Merge: preserve history with merge commit. Rebase: linear history, rewrites commits. |
| 2 | When not to rebase? | Public/shared branches — others may have based work on those commits. |
| 3 | git revert vs reset? | Revert: safe, new commit. Reset: moves HEAD — can lose commits. |
| 4 | Trunk-based development? | Short branches, frequent main merges, feature flags. |
| 5 | Cherry-pick use? | Apply one commit from another branch without full merge. |
| 6 | Squash commits? | Combine PR commits into one on merge — clean main history. |
| 7 | git stash? | Save uncommitted changes temporarily without committing. |
| 8 | Conflict resolution? | Edit conflict markers, stage, commit — communicate for shared files. |
| 9 | Monorepo benefits? | Atomic cross-service changes, shared libraries, unified CI. |
| 10 | What is a detached HEAD? | HEAD points to commit not on any branch — checkout branch to fix. |

**Must-say keywords**: trunk-based, feature flag, rebase golden rule, cherry-pick, revert, squash, PR size, monorepo.

---

## §33.10 Production & Interview Depth — Release Workflow in Indian Product Orgs

Flipkart, Swiggy, Zerodha, and post-Series-B SaaS shops converged on **trunk-based development + feature flags** (LaunchDarkly, in-house, or Unleash) rather than long-lived `release/2026-Q2` branches. Interviews at SDE2+ ask how you ship **10–50 deploys/day** without breaking UPI settlements or B2B SLAs.

### Trunk-Based vs Git Flow in Practice

| Pattern | Indian Product Fit | When It Breaks |
|---------|-------------------|----------------|
| **Trunk + flags** | Web/mobile backends, microservices | Flag debt — 200 stale flags nobody owns |
| **Git Flow** | Regulated releases, app store coupling | Slow hotfix path for payment bugs |
| **Release branches** | Enterprise on-prem with quarterly cadence | Merge hell before "freeze week" |
| **Monorepo** (Bazel/Nx) | Large platform teams (PhonePe-style) | CI time without remote cache |

Aligns with CI/CD depth in [10_DevOps_CICD_Docker.md](./10_DevOps_CICD_Docker.md) and change controls in [38_Compliance_and_Regulated_Systems.md](./38_Compliance_and_Regulated_Systems.md).

### Hotfix Pattern (Interview Favorite)

```bash
# Production bug on main — prefer revert-forward over force-push
git fetch origin
git checkout main && git pull
git revert <bad-commit-sha> -m 1    # safe, auditable
git push origin main                # CI deploys rollback

# Parallel: fix forward on branch
git checkout -b hotfix/payment-timeout
# ... minimal fix, tests ...
git push -u origin hotfix/payment-timeout
# PR with 2 approvals + expedited CI → merge → delete branch
```

**Never** `git push --force` to `main` — SOX/BFSI auditors and [38_Compliance_and_Regulated_Systems.md](./38_Compliance_and_Regulated_Systems.md) change-management stories fail instantly.

### PR Hygiene for Java/Spring Monorepos

```text
PR title: fix(checkout): idempotent refund handler for duplicate webhook
Size: <400 lines | Ticket: PAY-4821 | Risk: medium
Rollback: revert commit / disable flag checkout.refund-v2
```

Squash-merge keeps `main` readable; **cherry-pick** to LTS branch only when supporting on-prem customers — explain trade-off: clean history vs traceability to original commits.

### Conflict Resolution Under Pressure

During monsoon-sale prep, two teams touch `OrderService.java`. Say: *"I rebased daily, split PR into API contract vs implementation, and used module ownership in CODEOWNERS so reviews route to platform team."* Link to microservice boundaries: [06_Microservices_Distributed_Systems.md](./06_Microservices_Distributed_Systems.md).

**Must-say keywords**: revert not reset, squash on merge, feature flag rollback, CODEOWNERS, immutable artifact tag, separation of duties for prod deploy.
