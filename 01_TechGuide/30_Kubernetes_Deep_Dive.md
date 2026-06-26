# Section 30: Kubernetes Deep Dive

> **Level**: SR+ (workloads, networking, scaling) to LEAD (operators, multi-cluster, security)
> **Complements**: [10_DevOps_CICD_Docker.md](./10_DevOps_CICD_Docker.md) — Section 10 covers Docker and IaC basics

---

## 30.1 Kubernetes Architecture

```
Control Plane (master):
  API Server  — front door for all operations
  etcd        — cluster state (strongly consistent)
  Scheduler   — assigns pods to nodes
  Controller Manager — reconciliation loops (Deployments, etc.)

Worker Nodes:
  kubelet     — runs pods, reports health
  kube-proxy  — Service networking (iptables/IPVS)
  Container Runtime — containerd, CRI-O
```

**Desired state reconciliation**: You declare desired state (3 replicas); controllers continuously reconcile actual → desired.

---

## 30.2 Core Objects

| Object | Purpose |
|--------|---------|
| **Pod** | One or more containers sharing network/storage — smallest deploy unit |
| **Deployment** | Manages ReplicaSets — rolling updates, rollbacks |
| **StatefulSet** | Stable identity, ordered deploy — databases, Kafka |
| **DaemonSet** | One pod per node — log collectors, monitoring agents |
| **Job / CronJob** | Run-to-completion workloads |
| **ConfigMap** | Non-sensitive config |
| **Secret** | Sensitive config (base64 — encrypt at rest with KMS) |
| **Service** | Stable network endpoint for pods |
| **Ingress** | HTTP routing into cluster |
| **HPA** | Horizontal Pod Autoscaler — scale on CPU/memory/custom metrics |
| **PVC / PV** | Persistent storage |

---

## 30.3 Service Types & Networking

| Type | Behavior |
|------|----------|
| **ClusterIP** | Internal only — default |
| **NodePort** | Exposes on each node's IP at static port |
| **LoadBalancer** | Provisions cloud LB (ALB, NLB) |
| **ExternalName** | CNAME to external DNS |

### Pod Networking
- Each pod gets unique IP in pod network CIDR
- Containers in pod share network namespace (localhost between containers)

### Ingress + Ingress Controller
```
Internet → ALB/Ingress Controller → Ingress rules → Service → Pods
```
Controllers: **nginx-ingress**, **AWS Load Balancer Controller**, **Traefik**.

### Network Policies
Firewall rules at pod level — default allow all; restrict with `NetworkPolicy`:
```yaml
# Only allow traffic from frontend namespace to backend pods on port 8080
```

---

## 30.4 Health Probes

| Probe | Purpose | Failure Action |
|-------|---------|----------------|
| **Liveness** | Is container alive? | Restart container |
| **Readiness** | Ready to serve traffic? | Remove from Service endpoints |
| **Startup** | Slow-starting apps | Disable liveness until success |

```yaml
livenessProbe:
  httpGet:
    path: /actuator/health/liveness
    port: 8080
  initialDelaySeconds: 30
  periodSeconds: 10
readinessProbe:
  httpGet:
    path: /actuator/health/readiness
    port: 8080
  periodSeconds: 5
```

**Spring Boot 3**: `/actuator/health/liveness` and `/readiness` separate endpoints.

---

## 30.5 Resource Management

```yaml
resources:
  requests:
    memory: "512Mi"
    cpu: "250m"      # 0.25 CPU
  limits:
    memory: "1Gi"
    cpu: "1000m"
```

| Concept | Meaning |
|---------|---------|
| **requests** | Guaranteed resources — used for scheduling |
| **limits** | Max allowed — CPU throttled, memory OOMKilled |
| **QoS classes** | Guaranteed (requests=limits), Burstable, BestEffort |

**HPA**:
```yaml
minReplicas: 3
maxReplicas: 50
metrics:
  - type: Resource
    resource:
      name: cpu
      targetAverageUtilization: 70
```

---

## 30.6 Deployment Strategies

| Strategy | How | Risk |
|----------|-----|------|
| **Rolling Update** | Gradually replace pods | Default — brief mixed versions |
| **Recreate** | Kill all, deploy new | Downtime |
| **Blue-Green** | Two full environments, switch traffic | Double resources |
| **Canary** | 5% traffic to new version | Needs traffic splitting (mesh/ALB) |

```yaml
strategy:
  type: RollingUpdate
  rollingUpdate:
    maxSurge: 1
    maxUnavailable: 0   # Zero-downtime deploy
```

---

## 30.7 Security Best Practices

| Practice | Detail |
|----------|--------|
| **RBAC** | Least privilege ServiceAccounts per workload |
| **Pod Security** | No root, read-only root filesystem, drop capabilities |
| **Network Policies** | Default deny, explicit allow |
| **Secrets management** | External Secrets Operator + Vault/AWS SM |
| **Image scanning** | Trivy in CI, admission controller blocks vulnerable images |
| **mTLS** | Service mesh or cert-manager for internal TLS |

---

## 30.8 Interview Quick Reference

| # | Question | One-Line Answer |
|---|----------|-----------------|
| 1 | Pod vs Container? | Pod wraps one+ containers; shared network/IP; unit of scheduling. |
| 2 | Deployment vs StatefulSet? | Deployment: stateless, random names. StatefulSet: stable identity, ordered. |
| 3 | Liveness vs readiness? | Liveness: restart if dead. Readiness: remove from load balancer if not ready. |
| 4 | Service ClusterIP? | Internal stable IP/DNS for pod group — kube-proxy routes. |
| 5 | Ingress vs Service? | Service: L4 cluster networking. Ingress: L7 HTTP routing rules. |
| 6 | HPA? | Auto-scale pod count based on CPU/memory/custom metrics. |
| 7 | etcd role? | Distributed key-value store — entire cluster state. |
| 8 | requests vs limits? | Requests: scheduling guarantee. Limits: max — OOM if memory exceeded. |
| 9 | Rolling update maxUnavailable 0? | Always maintain desired replica count during deploy. |
| 10 | Why operators? | Automate complex stateful app lifecycle (CRDs + custom controllers). |

**Must-say keywords**: reconciliation loop, kubelet, etcd, HPA, readiness probe, NetworkPolicy, Ingress, StatefulSet, rolling update, QoS.
