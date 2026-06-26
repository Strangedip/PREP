# Section 31: Cloud Computing — AWS, GCP & Azure

> **Level**: MID+ (core services) to LEAD (multi-cloud, cost optimization, Well-Architected)
> **Complements**: [12_Security_OWASP_Cloud.md](./12_Security_OWASP_Cloud.md) Section 12.4

---

## 31.1 Cloud Service Models

| Model | You Manage | Provider Manages | Examples |
|-------|------------|------------------|----------|
| **IaaS** | App, runtime, OS | Hardware, network | EC2, GCE, Azure VMs |
| **PaaS** | App code | Runtime, OS, infra | Elastic Beanstalk, App Engine |
| **SaaS** | Configuration | Everything | Salesforce, Gmail |
| **FaaS** | Function code | Everything else | Lambda, Cloud Functions |

---

## 31.2 AWS Core Services Map

| Category | AWS | Use Case |
|----------|-----|----------|
| **Compute** | EC2, Lambda, ECS, EKS | VMs, serverless, containers |
| **Storage** | S3, EBS, EFS | Object, block, shared file |
| **Database** | RDS, Aurora, DynamoDB, ElastiCache | SQL, NoSQL, cache |
| **Networking** | VPC, ALB, CloudFront, Route 53 | Network, LB, CDN, DNS |
| **Messaging** | SQS, SNS, Kinesis, MSK | Queue, pub/sub, streaming |
| **Identity** | IAM, Cognito, STS | Access control, user auth |
| **Observability** | CloudWatch, X-Ray | Metrics, logs, traces |
| **Secrets** | Secrets Manager, Parameter Store | Credential management |

---

## 31.3 GCP & Azure Equivalents

| Capability | AWS | GCP | Azure |
|------------|-----|-----|-------|
| VMs | EC2 | Compute Engine | Virtual Machines |
| K8s | EKS | GKE | AKS |
| Object storage | S3 | Cloud Storage | Blob Storage |
| SQL DB | RDS/Aurora | Cloud SQL | Azure SQL |
| NoSQL | DynamoDB | Firestore/Bigtable | Cosmos DB |
| Serverless | Lambda | Cloud Functions | Azure Functions |
| CDN | CloudFront | Cloud CDN | Azure CDN |
| Queue | SQS | Pub/Sub | Service Bus |
| IAM | IAM | Cloud IAM | Azure AD/RBAC |

---

## 31.4 IAM — Least Privilege

```
User/Role → Policy (JSON) → Allow/Deny actions on resources

Example: EC2 read-only
{
  "Effect": "Allow",
  "Action": ["ec2:Describe*"],
  "Resource": "*"
}
```

**Best practices**:
- No root account for daily ops
- Roles for services (EC2 instance role, Lambda execution role)
- **Permission boundaries** for delegated admin
- **MFA** on human accounts

---

## 31.5 S3 Deep Dive

| Feature | Use |
|---------|-----|
| **Storage classes** | Standard, IA, Glacier — cost vs access frequency |
| **Versioning** | Protect against accidental delete |
| **Lifecycle rules** | Auto-transition to Glacier after 90 days |
| **Presigned URLs** | Time-limited upload/download without AWS creds |
| **Event notifications** | S3 → Lambda/SQS on object create |
| **Strong consistency** | Read-after-write consistent (since 2020) |

---

## 31.6 Serverless — Lambda Patterns

| Pattern | Flow |
|---------|------|
| **API** | API Gateway → Lambda → DynamoDB |
| **Event-driven** | S3 upload → Lambda → process → SQS |
| **Stream** | Kinesis/DynamoDB Streams → Lambda |

**Limits**: 15 min timeout, cold starts, package size — not for long-running or high-throughput steady load without tuning.

**Provisioned concurrency** — eliminate cold starts for latency-sensitive paths.

---

## 31.7 Cost Optimization

| Strategy | Example |
|----------|---------|
| **Right-sizing** | Don't run m5.2xlarge if m5.large suffices |
| **Reserved / Savings Plans** | 1-3 year commit for steady workloads |
| **Spot Instances** | 70-90% discount — fault-tolerant batch jobs |
| **Auto-scaling** | Scale down nights/weekends |
| **S3 lifecycle** | Glacier for archives |
| **Tagging** | Cost allocation per team/product |

---

## 31.8 Well-Architected Framework (AWS)

| Pillar | Focus |
|--------|-------|
| **Operational Excellence** | Run and monitor systems |
| **Security** | Protect data and systems |
| **Reliability** | Recover from failures, meet demand |
| **Performance Efficiency** | Use resources efficiently |
| **Cost Optimization** | Avoid unnecessary costs |
| **Sustainability** | Minimize environmental impact |

---

## 31.9 Interview Quick Reference

| # | Question | One-Line Answer |
|---|----------|-----------------|
| 1 | S3 vs EBS? | S3: object storage, unlimited, HTTP API. EBS: block storage attached to EC2. |
| 2 | SQS vs SNS? | SQS: queue, one consumer per message. SNS: pub/sub fan-out to many. |
| 3 | IAM Role vs User? | Role: temporary creds for services/users — no long-lived keys. |
| 4 | Multi-AZ vs Multi-Region? | Multi-AZ: same region HA. Multi-Region: DR, global latency. |
| 5 | Lambda cold start? | Init runtime on first invoke — fix: provisioned concurrency, smaller packages. |
| 6 | VPC peering vs Transit Gateway? | Peering: 1:1. TGW: hub for many VPCs — transitive routing. |
| 7 | Aurora vs RDS? | Aurora: AWS-proprietary, faster failover, auto-scaling storage. |
| 8 | CloudFront? | CDN — cache at edge, reduce origin load and latency. |
| 9 | Spot instances risk? | Can be reclaimed with 2-min notice — use for fault-tolerant workloads. |
| 10 | Shared responsibility model? | Provider: security OF cloud. Customer: security IN cloud. |

**Must-say keywords**: IAM least privilege, Multi-AZ, presigned URL, SQS/SNS, EKS, Well-Architected, cold start, lifecycle policy, shared responsibility.
