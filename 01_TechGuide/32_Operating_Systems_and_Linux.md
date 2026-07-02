# Section 32: Operating Systems & Linux for Engineers

> **Level**: ALL (basics) to SR+ (performance tuning, debugging production)
> **Why This Matters**: Every backend runs on Linux in production. Interviews ask about processes, memory, file descriptors, and how you debug a slow server.

> **You are here**: Fresher — Technical Skills
> **Roadmap**: [Developer Master Roadmap](../ROADMAP.md) | **Prerequisites**: [31_Cloud_Computing_AWS_GCP_Azure.md](31_Cloud_Computing_AWS_GCP_Azure.md) | **Next**: [33_Git_Version_Control_Workflow.md](33_Git_Version_Control_Workflow.md)

---

## 32.1 Process vs Thread

| | Process | Thread |
|---|---------|--------|
| Memory | Own address space | Shared within process |
| Creation cost | High (fork) | Low |
| Communication | IPC (pipes, sockets) | Shared memory |
| Crash isolation | Isolated | One thread crash can kill process |
| Java mapping | JVM process | Platform/virtual threads |

**Context switch**: CPU saves state of one process/thread, loads another — expensive at high rates.

---

## 32.2 Linux Process States

```
Running → Waiting (I/O) → Runnable → Running
                ↓
            Stopped / Zombie
```

| State | Meaning |
|-------|---------|
| **R** | Running or runnable |
| **S** | Sleeping (interruptible — waiting for I/O) |
| **D** | Uninterruptible sleep (usually disk I/O) |
| **Z** | Zombie — child exited but parent didn't reap |
| **T** | Stopped (debugger, SIGSTOP) |

---

## 32.3 Essential Linux Commands (Production Debugging)

| Command | Purpose |
|---------|---------|
| `top` / `htop` | CPU, memory per process |
| `ps aux` | Process list |
| `free -h` | Memory usage (available vs used) |
| `df -h` | Disk space |
| `du -sh *` | Directory sizes |
| `lsof -p PID` | Open files by process |
| `ss -tlnp` / `netstat` | Listening ports |
| `strace -p PID` | System calls (debug hangs) |
| `tcpdump` | Network packet capture |
| `journalctl -u service` | Systemd service logs |
| `dmesg` | Kernel messages (OOM kills) |

### Finding What's Using CPU
```bash
top -H -p <java_pid>    # per-thread CPU in Java process
jstack <pid>            # Java thread dump
```

---

## 32.4 Memory Management

| Concept | Detail |
|---------|--------|
| **Virtual memory** | Each process sees own address space |
| **Page** | Typically 4 KB — unit of memory management |
| **Page fault** | Access page not in RAM → load from disk (slow) |
| **Swap** | Disk used as overflow when RAM full — kills performance |
| **OOM Killer** | Kernel kills process when memory exhausted |

**Java on Linux**: JVM heap + metaspace + thread stacks + direct memory + native libs — can exceed `-Xmx`.

---

## 32.5 File Descriptors & ulimit

Each open connection, file, socket = one FD. Default limit often 1024 — **too low for servers**.

```bash
ulimit -n          # current limit
# /etc/security/limits.conf
* soft nofile 65535
* hard nofile 65535
```

**"Too many open files"** in Java → increase ulimit + check connection leaks.

---

## 32.6 I/O Models

| Model | Behavior | Use |
|-------|----------|-----|
| **Blocking I/O** | Thread waits until data ready | Simple, thread-per-connection |
| **Non-blocking I/O** | Returns immediately, poll later | NIO, selectors |
| **Async I/O (io_uring)** | Kernel notifies on completion | High-performance servers |
| **epoll** | Linux efficient event notification | Netty, Nginx, Node.js |

**C10K problem**: 10,000 concurrent connections — blocking threads don't scale; use event loops or virtual threads.

---

## 32.7 Containers & Linux Namespaces

Docker containers use Linux features:
- **Namespaces** — PID, network, mount isolation
- **cgroups** — CPU/memory limits per container
- **Union filesystem** — layered images

Container is **not a VM** — shares host kernel.

---

## 32.8 Interview Quick Reference

| # | Question | One-Line Answer |
|---|----------|-----------------|
| 1 | Process vs thread? | Process: isolated memory. Thread: shared memory within process. |
| 2 | Context switch cost? | CPU state save/restore — avoid excessive threading on CPU-bound work. |
| 3 | Zombie process? | Child exited, parent didn't wait() — consumes PID entry. |
| 4 | OOM Killer? | Kernel kills process when system runs out of memory. |
| 5 | ulimit nofile? | Max open file descriptors — raise for high-connection servers. |
| 6 | Swap impact? | Disk as RAM — severe latency; avoid on production DB/app servers. |
| 7 | epoll vs select? | epoll: O(1) for many connections. select: O(n) — scales poorly. |
| 8 | Container vs VM? | Container: shared kernel, lightweight. VM: full OS isolation. |
| 9 | Debug high CPU Java? | `top -H`, `jstack`, async-profiler flame graph. |
| 10 | Load average? | Average runnable processes — > CPU count means queueing. |

**Must-say keywords**: context switch, file descriptor, OOM, cgroup, namespace, virtual memory, page fault, ulimit.

---

## §32.10 Production & Interview Depth — Debugging Spring Boot on Linux

When a Razorpay-scale API or e-commerce cart service "gets slow at 9 PM IST," you SSH (or `kubectl exec`) into Linux, not IntelliJ. Indian on-call rotations expect **systematic triage**: CPU vs GC vs I/O vs connection exhaustion — often on **t3/c6i** instances or container limits from [30_Kubernetes_Deep_Dive.md](./30_Kubernetes_Deep_Dive.md).

### On-Call Triage Flow

```
1. kubectl top pod / top -H -p <pid>     → CPU thread?
2. free -h && cat /proc/<pid>/status     → RSS vs cgroup limit?
3. ss -s && lsof -p <pid> | wc -l        → FD leak?
4. dmesg | grep -i oom                   → kernel killed container?
5. jcmd <pid> GC.heap_info / async-profiler → JVM-specific
```

### cgroup Memory vs JVM Heap (Classic Interview Trap)

Container `limits.memory: 1Gi` with `-Xmx1024m` **OOMKills** the pod — native memory, metaspace, thread stacks, and direct buffers sit outside heap.

| Symptom | Likely Cause | Fix |
|---------|--------------|-----|
| Pod restart every ~30 min | Liveness probe + slow GC | Separate liveness from deep checks; tune G1 |
| `OOMKilled` in `kubectl describe` | Heap + native > cgroup limit | `-XX:MaxRAMPercentage=70`; raise limit or reduce threads |
| Load avg >> CPU count, low CPU% | Disk I/O wait (`wa` in top) | RDS latency, EBS throughput — [05_Database_Performance_Tuning.md](./05_Database_Performance_Tuning.md) |
| `Too many open files` | ulimit 1024 on old AMIs | Raise `nofile`; fix HttpClient leak in Spring |

```bash
# Inside pod — map Java threads to OS threads
top -H -p 1 -b -n 1 | head -20
jstack 1 | grep -A5 "pool-.*-thread"

# cgroup v2 memory current (EKS AL2 / containerd)
cat /sys/fs/cgroup/memory.current
cat /sys/fs/cgroup/memory.max
```

### epoll and Spring Boot 3 (Virtual Threads)

Spring Boot 3.2+ with **virtual threads** (`spring.threads.virtual.enabled=true`) maps many request tasks onto few carrier threads — Linux **epoll** backs NIO. Interview angle: you get C10K-friendly blocking style without thread-per-request explosion; still watch **pinned carriers** on `synchronized` blocks — see [15_Java_Collections_Concurrency_DeepDive.md](./15_Java_Collections_Concurrency_DeepDive.md).

### India Product Context

Peak traffic aligns with **salary day, IPL, festival sales** — load average spikes may be normal if queue depth is healthy. Say: *"I correlate OS metrics with Micrometer dashboards ([11_Observability.md](./11_Observability.md)) and trace IDs before restarting pods."*

**Must-say keywords**: cgroup OOM, RSS vs heap, `top -H`, jstack, epoll, ulimit nofile, I/O wait, virtual thread pinning.
