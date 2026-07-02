# High-Level Design Interview Template

> **You are here**: SDE2 — System Design
> **Roadmap**: [Developer Master Roadmap](../../../ROADMAP.md) | **Prerequisites**: [01_Patterns.md](../../../03_CodingPatterns/01_Patterns.md)

## 📋 **Step-by-Step Approach**

Use this systematic methodology for any HLD interview problem:

### **Phase 1: Requirements Gathering (5-10 minutes)**

#### 1.1 **Functional Requirements**
```
Ask and clarify:
✅ What are the core features?
✅ Who are the users and what do they do?
✅ What are the key user journeys?
✅ Are there any specific business rules?
✅ Integration with external systems?
```

#### 1.2 **Non-Functional Requirements**
```
Clarify:
📊 Scale: How many users? QPS? Data volume?
⚡ Performance: Latency requirements? Throughput?
🔄 Availability: Uptime requirements? Disaster recovery?
🌍 Geography: Global? Regional? Multi-datacenter?
🔒 Security: Authentication? Authorization? Compliance?
💰 Cost: Budget constraints? Cost optimization needs?
```

#### 1.3 **Capacity Estimation**
```
Calculate:
👥 Daily Active Users (DAU)
📈 Queries Per Second (QPS) - Read/Write ratio
💾 Storage requirements (5 years projection)
🌐 Bandwidth requirements (incoming/outgoing)
🧠 Memory requirements (caching)
```

### **Phase 2: High-Level Architecture (10-15 minutes)**

#### 2.1 **System Overview**
```
Draw basic architecture:
┌─────────────┐    ┌─────────────┐    ┌─────────────┐
│   Client    │───▶│ Load Balancer│───▶│ Web Servers │
│ (Mobile/Web)│    │ (Layer 7)   │    │  (Stateless)│
└─────────────┘    └─────────────┘    └─────────────┘
                                              │
                           ┌──────────────────┼──────────────────┐
                           │                  │                  │
                    ┌──────▼──────┐  ┌───────▼───────┐  ┌──────▼──────┐
                    │   Cache     │  │   Database    │  │   Message   │
                    │  (Redis)    │  │  (Primary)    │  │   Queue     │
                    └─────────────┘  └───────────────┘  └─────────────┘
```

#### 2.2 **Core Components**
```
Identify and list:
1. Client Layer (Web, Mobile, API consumers)
2. Load Balancer (Traffic distribution)
3. Application Layer (Business logic)
4. Caching Layer (Performance optimization)
5. Database Layer (Data persistence)
6. Message Queue (Async processing)
7. External Services (Third-party integrations)
```

### **Phase 3: Deep Dive Design (15-20 minutes)**

#### 3.1 **Database Design**
```
Choose database type:
SQL (ACID compliance, complex queries)
- MySQL, PostgreSQL
- Good for: Financial transactions, user data

NoSQL (Scalability, flexibility)
- MongoDB (Document), Cassandra (Column), Redis (Key-Value)
- Good for: Social media, IoT data, caching

Design schema:
- Normalize for consistency vs Denormalize for performance
- Indexing strategy
- Partitioning/Sharding strategy
```

#### 3.2 **API Design**
```
RESTful API endpoints:
GET    /api/v1/users/{id}          - Retrieve user
POST   /api/v1/users               - Create user
PUT    /api/v1/users/{id}          - Update user
DELETE /api/v1/users/{id}          - Delete user

GraphQL (if applicable):
query GetUser($id: ID!) {
    user(id: $id) {
        id
        name
        email
        posts {
            title
            content
        }
    }
}
```

#### 3.3 **Caching Strategy**
```
Cache Layers:
L1: Application Cache (In-memory, fast)
L2: Distributed Cache (Redis, shared)
L3: CDN (Static content, global)

Cache Patterns:
- Cache-Aside (Lazy loading)
- Write-Through (Immediate consistency)
- Write-Behind (Eventual consistency)

Cache Eviction:
- LRU (Least Recently Used)
- TTL (Time To Live)
- Manual invalidation
```

### **Phase 4: Scaling and Optimization (10-15 minutes)**

#### 4.1 **Horizontal Scaling**
```
Scaling Strategies:
📊 Load Balancing: Round-robin, Weighted, Least connections
🗄️ Database Sharding: Horizontal partitioning
📱 Microservices: Service decomposition
🌍 Geographic Distribution: Multi-region deployment
```

#### 4.2 **Performance Optimization**
```
Optimization Techniques:
⚡ Caching: Multi-layer caching strategy
📊 Database Optimization: Indexing, query optimization
🔄 Async Processing: Message queues, event-driven
📈 Connection Pooling: Database connections
🗜️ Compression: Data and response compression
```

#### 4.3 **Fault Tolerance**
```
Reliability Measures:
🔄 Replication: Master-slave, Master-master
🛡️ Circuit Breaker: Prevent cascade failures
↩️ Retry Logic: Exponential backoff
📊 Health Checks: Monitoring and alerting
🔧 Graceful Degradation: Fallback mechanisms
```

### **Phase 5: Advanced Topics (5-10 minutes)**

#### 5.1 **Security**
```
Security Considerations:
🔐 Authentication: JWT, OAuth, SAML
🛡️ Authorization: RBAC, ACL
🔒 Data Encryption: At rest and in transit
🚫 Rate Limiting: Prevent abuse
🛡️ Input Validation: SQL injection, XSS prevention
```

#### 5.2 **Monitoring**
```
Observability:
📊 Metrics: System and business metrics
📝 Logging: Centralized logging (ELK stack)
🔍 Tracing: Distributed tracing (Jaeger)
🚨 Alerting: Proactive monitoring
📈 Dashboards: Real-time visibility
```

---

## 📊 **Capacity Estimation Framework**

### **Traffic Estimation**
```
Example: Social Media Platform

Users:
- Total Users: 1 Billion
- Daily Active Users: 200 Million (20%)
- Average session: 30 minutes
- Posts per user per day: 2

Calculations:
- Posts per day: 200M × 2 = 400M posts/day
- Posts per second: 400M / (24 × 3600) = 4,630 posts/sec
- Read to Write ratio: 100:1
- Read QPS: 4,630 × 100 = 463,000 reads/sec
```

### **Storage Estimation**
```
Data per post:
- Text: 140 characters × 2 bytes = 280 bytes
- Metadata: 100 bytes (timestamp, user_id, etc.)
- Media: 1MB (average for photos/videos)
- Total per post: ~1MB

Storage calculation:
- Daily storage: 400M posts × 1MB = 400TB/day
- 5-year storage: 400TB × 365 × 5 = 730PB
- With replication (3x): 730PB × 3 = 2.2EB
```

### **Bandwidth Estimation**
```
Incoming:
- Posts: 4,630 posts/sec × 1MB = 4.6GB/s
- Peak traffic (2x): 9.2GB/s

Outgoing:
- Reads: 463,000 reads/sec × 1MB = 463GB/s
- Peak traffic (2x): 926GB/s
```

---

## 🏗️ **Common Architecture Patterns**

### **1. Microservices Architecture**
```
┌─────────────┐  ┌─────────────┐  ┌─────────────┐
│   User      │  │   Content   │  │ Notification│
│  Service    │  │  Service    │  │  Service    │
└─────────────┘  └─────────────┘  └─────────────┘
       │                 │                 │
       └─────────────────┼─────────────────┘
                         │
                ┌────────▼────────┐
                │  Message Queue  │
                │   (Kafka)       │
                └─────────────────┘
```

### **2. Event-Driven Architecture**
```
Event Producer → Event Store → Event Consumers
    │               │              │
    │               │              ├─ Analytics Service
    │               │              ├─ Notification Service
    │               │              └─ Audit Service
```

### **3. CQRS (Command Query Responsibility Segregation)**
```
Commands (Write)        Queries (Read)
      │                      │
      ▼                      ▼
┌─────────────┐      ┌─────────────┐
│ Write Model │      │ Read Model  │
│ (Normalized)│      │(Denormalized)│
└─────────────┘      └─────────────┘
```

---

## 🗄️ **Database Design Patterns**

### **1. Database Sharding**
```
Shard by User ID:
Shard 1: user_id % 3 = 0
Shard 2: user_id % 3 = 1  
Shard 3: user_id % 3 = 2

Shard by Geography:
Shard 1: US users
Shard 2: EU users
Shard 3: APAC users
```

### **2. Master-Slave Replication**
```
┌─────────────┐    ┌─────────────┐    ┌─────────────┐
│   Master    │───▶│  Slave 1    │    │   Slave 2   │
│  (Writes)   │    │  (Reads)    │    │  (Reads)    │
└─────────────┘    └─────────────┘    └─────────────┘
```

### **3. Database Federation**
```
Users DB ────┐
             ├─── Application Layer
Posts DB ────┤
             └─── Load Balancer
Analytics DB ─┘
```

---

## 🚨 **Common Bottlenecks and Solutions**

### **1. Database Bottlenecks**
```
Problem: Single database overload
Solutions:
✅ Read replicas for read scaling
✅ Database sharding for write scaling  
✅ Caching frequently accessed data
✅ Database optimization (indexing, query tuning)
```

### **2. Application Server Bottlenecks**
```
Problem: Server overload
Solutions:
✅ Horizontal scaling (more servers)
✅ Load balancing
✅ Caching at application level
✅ Async processing for heavy operations
```

### **3. Network Bottlenecks**
```
Problem: Network latency/bandwidth
Solutions:
✅ CDN for static content
✅ Data compression
✅ Geographic distribution
✅ Connection pooling
```

---

## 🎯 **Company-Specific Focus Areas**

### **FAANG Companies**
```
Google: 
- Emphasize scalability and performance
- Discuss Google technologies (BigTable, MapReduce)
- Focus on search and indexing

Amazon:
- Cost optimization and AWS services
- Microservices and serverless architecture
- Operational excellence

Meta:
- Social networking challenges
- Real-time features and notifications
- Graph databases and relationships

Apple:
- Privacy and security focus
- Mobile-first architecture
- Hardware-software integration

Netflix:
- Content delivery and streaming
- Recommendation systems
- Global scale and availability
```

---

## 💬 **Communication Best Practices**

### **What to Say**
```
✅ "Let me start by understanding the requirements..."
✅ "Based on the scale, I would suggest..."
✅ "The trade-off here is between consistency and availability..."
✅ "For this scale, we need to consider..."
✅ "An alternative approach would be..."
```

### **How to Structure**
```
1. Requirements clarification
2. Capacity estimation with numbers
3. High-level architecture drawing
4. Component deep-dive with justification
5. Scaling discussion with trade-offs
6. Monitoring and operational concerns
```

### **Time Management**
```
45-60 minute interview:
- Requirements: 5-10 minutes
- High-level design: 10-15 minutes  
- Deep dive: 15-20 minutes
- Scaling: 10-15 minutes
- Wrap-up: 5 minutes
```

---

## 🔧 **Quick Reference Cheat Sheet**

### **Load Balancer Types**
- **Layer 4**: IP + Port (faster, less flexible)
- **Layer 7**: HTTP content (slower, more features)

### **Database Types**
- **ACID**: Consistency over availability
- **BASE**: Availability over consistency

### **Caching**
- **TTL**: Time-based expiration
- **LRU**: Usage-based expiration
- **Write-through**: Immediate consistency
- **Write-behind**: Eventual consistency

### **Message Queues**
- **Kafka**: High throughput, persistent
- **RabbitMQ**: Feature-rich, reliable
- **SQS**: Managed, scalable

This template provides a comprehensive framework for approaching any high-level design interview with confidence and systematic thinking. 