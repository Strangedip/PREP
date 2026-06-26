# Section 32: Operating Systems & Linux for Engineers

> **Level**: ALL (basics) to SR+ (performance tuning, debugging production)
> **Why This Matters**: Every backend runs on Linux in production. Interviews ask about processes, memory, file descriptors, and how you debug a slow server.

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

**Must-say keywords**: context switch, file descriptor, OOM, epoll, cgroup, namespace, virtual memory, page fault, ulimit.
