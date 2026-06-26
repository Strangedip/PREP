# Section 33: Git, Version Control & Engineering Workflow

> **Level**: ALL — every engineer uses Git daily; interviews ask about workflows and conflict resolution

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
