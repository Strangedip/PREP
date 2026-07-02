# Section 31: Cloud Computing — AWS, GCP & Azure

> **Level**: MID+ (core services) to LEAD (multi-cloud, cost optimization, Well-Architected)
> **Complements**: [12_Security_OWASP_Cloud.md](./12_Security_OWASP_Cloud.md) Section 12.4

> **You are here**: SDE2 — Technical Skills
> **Roadmap**: [Developer Master Roadmap](../ROADMAP.md) | **Prerequisites**: [30_Kubernetes_Deep_Dive.md](30_Kubernetes_Deep_Dive.md) | **Next**: [32_Operating_Systems_and_Linux.md](32_Operating_Systems_and_Linux.md)

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

---

## §31.10 Production & Interview Depth — Cloud Choices for Indian Product Teams

Most India HQ product companies standardize on **one primary cloud** (AWS dominant; GCP strong in data/ML; Azure in enterprise B2B) with **Mumbai / Hyderabad regions** for latency and data-residency conversations. Senior interviews probe **cost vs reliability** and **vendor lock-in**, not service name trivia.

### Regional & Residency Decisions

| Requirement | Typical Pattern | Pitfall |
|-------------|-----------------|---------|
| **RBI / DPDP data localization** | RDS + S3 in `ap-south-1`; no cross-region replication of PII | Analytics pipeline copying EU/US buckets — compliance gap |
| **UPI / payments HA** | Multi-AZ RDS Aurora + 3 AZ EKS node groups | Single-AZ cost savings that fail AZ outage drills |
| **DR for BFSI** | Warm standby in second region (chennai/hyderabad) | Untested failover — "we have Multi-AZ" ≠ DR |
| **CDN for Bharat users** | CloudFront / Cloud CDN with edge in India | Origin in `us-east-1` — TTFB kills mobile 4G UX |

Deep compliance framing: [38_Compliance_and_Regulated_Systems.md](./38_Compliance_and_Regulated_Systems.md).

### Spring Boot 3 on AWS — Concrete Integration Pattern

```java
// application.yml — use IAM role on EKS, never static keys
@Configuration
public class AwsConfig {
    @Bean
    S3Client s3Client() {
        return S3Client.builder()
            .region(Region.AP_SOUTH_1)
            .credentialsProvider(DefaultCredentialsProvider.create())
            .build();
    }
}

// Presigned URL for KYC document upload (common in Indian fintech)
public URL presignUpload(String bucket, String key, Duration ttl) {
    return s3Presigner.presignPutObject(b -> b
        .putObjectRequest(r -> r.bucket(bucket).key(key))
        .signatureDuration(ttl)).url();
}
```

**SQS + Spring** for async order fulfillment: `@SqsListener` with idempotent consumers — ties to [06_Microservices_Distributed_Systems.md](./06_Microservices_Distributed_Systems.md).

### Build vs Buy on Cloud Managed Services

| Layer | Buy (Managed) | Build (Self-Managed on K8s) | India Org Bias |
|-------|---------------|-----------------------------|----------------|
| **Postgres** | RDS / Cloud SQL | StatefulSet + Patroni | Buy until >$50k/mo or exotic extensions — see [26_PostgreSQL_Relational_DB_Deep_Dive.md](./26_PostgreSQL_Relational_DB_Deep_Dive.md) |
| **Kafka** | MSK / Confluent Cloud | Strimzi on EKS | Buy for speed; self-host when egress costs bite |
| **Search** | OpenSearch Service | Self-hosted ES | Managed wins for ops headcount — [34_Search_Engines_Elasticsearch.md](./34_Search_Engines_Elasticsearch.md) |
| **Secrets** | Secrets Manager + External Secrets | Vault cluster | Managed + rotation Lambdas typical at Series B+ |

### Cost Governance Interview Answer

*"We tagged every resource with `team`, `env`, `cost-center` in Terraform ([10_DevOps_CICD_Docker.md](./10_DevOps_CICD_Docker.md)), used **Savings Plans** for baseline EKS nodes, **Spot** for batch/reporting, S3 lifecycle to Glacier for 7-year audit archives, and FinOps monthly reviews — caught orphaned EBS from autoscaling events."*

**Must-say keywords**: ap-south-1, IAM instance role, presigned URL, shared responsibility, Multi-AZ vs DR, FinOps tagging, Well-Architected reliability pillar.
