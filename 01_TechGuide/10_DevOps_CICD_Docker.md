# Section 10: DevOps, CI/CD, Docker & Containerization

---

## 10.1 Docker: Containerization for Java/Angular Applications

---

### The "Why" & The Problem

"It works on my machine" is the bane of software development. Without containerization:
- **Environment inconsistency**: Developer A has Java 17, Developer B has Java 21. The CI server has Java 17 but a different glibc version. Production has Java 21 with a specific JVM flag. Each environment behaves subtly differently.
- **Dependency hell**: Your app depends on PostgreSQL 16, Redis 7, and Kafka 3.5. Getting all developers to install exactly these versions on their laptops is a support nightmare.
- **Deployment unpredictability**: A deployment works in staging but fails in production because staging has a different OS version, different filesystem permissions, or a different system library.

Docker solves this by packaging your application, its runtime, and its dependencies into a **container image**. The same image runs identically on a developer's laptop, in CI, in staging, and in production.

A company pays you to know this because **containerization is a prerequisite for modern deployment**. Every cloud-native application runs in containers, typically orchestrated by Kubernetes.

---

### Interviewer Expectations

- **Multi-stage builds**: Know how to create production-optimized Docker images for Java and Angular. Use multi-stage builds to separate the build environment from the runtime.
- **Image optimization**: Know that a `FROM openjdk:21` image is 700MB+. Use `eclipse-temurin:21-jre-alpine` (~200MB) or distroless (~100MB). Explain layer caching and why dependency layers should come before code layers.
- **Docker Compose**: Know how to set up a local development environment with your app, PostgreSQL, Redis, and Kafka using Docker Compose.
- **Keywords**: "Multi-stage build", "layer caching", "distroless image", "slim JRE", "Docker Compose", "health check", "non-root user", "build context", ".dockerignore".

---

### The Deep Dive & Solution

#### Production-Optimized Dockerfile for Spring Boot

```dockerfile
# ============================================================
# Stage 1: Build the application
# ============================================================
FROM eclipse-temurin:21-jdk AS builder

WORKDIR /app

# Copy dependency files first (layer caching optimization)
# These layers change rarely, so Docker caches them
COPY pom.xml mvnw ./
COPY .mvn .mvn

# Download dependencies (cached unless pom.xml changes)
RUN ./mvnw dependency:go-offline -B

# Copy source code (changes frequently — this layer is NOT cached)
COPY src ./src

# Build the application (skip tests — they run in CI separately)
RUN ./mvnw package -DskipTests -B

# Extract Spring Boot layers for optimized Docker layering
RUN java -Djarmode=layertools -jar target/*.jar extract --destination extracted

# ============================================================
# Stage 2: Create the runtime image (minimal — no JDK, no Maven, no source code)
# ============================================================
FROM eclipse-temurin:21-jre-alpine

# Security: Run as non-root user
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

WORKDIR /app

# Copy Spring Boot layers in order of change frequency (least → most)
# This maximizes Docker layer caching
COPY --from=builder /app/extracted/dependencies/ ./
COPY --from=builder /app/extracted/spring-boot-loader/ ./
COPY --from=builder /app/extracted/snapshot-dependencies/ ./
COPY --from=builder /app/extracted/application/ ./

# Set ownership
RUN chown -R appuser:appgroup /app

USER appuser

# JVM configuration for containers
ENV JAVA_OPTS="-XX:+UseZGC \
               -XX:+ZGenerational \
               -XX:MaxRAMPercentage=75.0 \
               -XX:+ExitOnOutOfMemoryError \
               -Djava.security.egd=file:/dev/./urandom"

EXPOSE 8080 8081

# Health check
HEALTHCHECK --interval=30s --timeout=3s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8081/actuator/health/liveness || exit 1

ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} org.springframework.boot.loader.launch.JarLauncher"]
```

**Key optimizations explained**:

| Optimization | Why |
|-------------|-----|
| **Multi-stage build** | Build stage has JDK + Maven (~800MB). Runtime stage has only JRE (~200MB). Final image is 60% smaller. |
| **Layer ordering** | Dependencies change rarely (cached), source code changes frequently (rebuilt). This means `docker build` only rebuilds the source layer, not dependencies — builds in 10s instead of 5min. |
| **Spring Boot layertools** | Splits the fat JAR into 4 layers (dependencies, spring-boot-loader, snapshot-deps, application). Only the `application` layer changes on every code change. |
| **`eclipse-temurin:21-jre-alpine`** | Alpine-based JRE image (~200MB vs. ~700MB for full JDK). No compilation tools in production. |
| **Non-root user** | Security best practice. If the container is compromised, the attacker has limited privileges. |
| **`MaxRAMPercentage=75.0`** | Tells the JVM to use 75% of the container's memory limit, leaving 25% for OS overhead, Metaspace, and thread stacks. |
| **`ExitOnOutOfMemoryError`** | JVM exits immediately on OOM so Kubernetes can restart the pod, instead of running in a degraded state. |

#### Production-Optimized Dockerfile for Angular

```dockerfile
# ============================================================
# Stage 1: Build the Angular application
# ============================================================
FROM node:20-alpine AS builder

WORKDIR /app

# Copy dependency files first (layer caching)
COPY package.json package-lock.json ./

# Install dependencies (cached unless package.json changes)
RUN npm ci --no-audit

# Copy source code
COPY . .

# Build production bundle
RUN npm run build -- --configuration=production
# Output: /app/dist/my-angular-app/browser/

# ============================================================
# Stage 2: Serve with nginx (no Node.js in production!)
# ============================================================
FROM nginx:1.25-alpine

# Custom nginx config for SPA routing
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Copy built assets from builder stage
COPY --from=builder /app/dist/my-angular-app/browser/ /usr/share/nginx/html/

# Security: Run as non-root
RUN chown -R nginx:nginx /usr/share/nginx/html && \
    chmod -R 755 /usr/share/nginx/html

EXPOSE 80

HEALTHCHECK --interval=30s --timeout=3s \
  CMD wget --no-verbose --tries=1 --spider http://localhost:80/health || exit 1

CMD ["nginx", "-g", "daemon off;"]
```

```nginx
# nginx.conf — SPA routing + security headers + caching
server {
    listen 80;
    server_name _;
    root /usr/share/nginx/html;
    index index.html;

    # SPA routing — all routes serve index.html (Angular handles routing client-side)
    location / {
        try_files $uri $uri/ /index.html;
    }

    # Cache static assets aggressively (they have hash-based filenames)
    location ~* \.(js|css|png|jpg|jpeg|gif|ico|svg|woff|woff2)$ {
        expires 1y;
        add_header Cache-Control "public, immutable";
    }

    # Don't cache index.html (it references the hashed JS/CSS files)
    location = /index.html {
        expires -1;
        add_header Cache-Control "no-cache, no-store, must-revalidate";
    }

    # Security headers
    add_header X-Frame-Options "SAMEORIGIN" always;
    add_header X-Content-Type-Options "nosniff" always;
    add_header X-XSS-Protection "1; mode=block" always;
    add_header Referrer-Policy "strict-origin-when-cross-origin" always;
    add_header Content-Security-Policy "default-src 'self'; script-src 'self'; style-src 'self' 'unsafe-inline';" always;

    # Health check endpoint
    location /health {
        access_log off;
        return 200 'OK';
        add_header Content-Type text/plain;
    }

    # Gzip compression
    gzip on;
    gzip_types text/plain text/css application/json application/javascript text/xml application/xml;
    gzip_min_length 1000;
}
```

#### Docker Compose for Local Development

```yaml
# docker-compose.yml — Full local development environment
version: '3.8'

services:
  # PostgreSQL Database
  postgres:
    image: postgres:16-alpine
    environment:
      POSTGRES_DB: orderdb
      POSTGRES_USER: appuser
      POSTGRES_PASSWORD: localpassword
    ports:
      - "5432:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data
      - ./init-scripts:/docker-entrypoint-initdb.d  # Auto-run SQL scripts on first start
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U appuser -d orderdb"]
      interval: 10s
      timeout: 5s
      retries: 5

  # Redis Cache
  redis:
    image: redis:7-alpine
    ports:
      - "6379:6379"
    command: redis-server --maxmemory 256mb --maxmemory-policy allkeys-lru
    healthcheck:
      test: ["CMD", "redis-cli", "ping"]
      interval: 10s
      timeout: 5s
      retries: 5

  # Apache Kafka + Zookeeper
  zookeeper:
    image: confluentinc/cp-zookeeper:7.5.0
    environment:
      ZOOKEEPER_CLIENT_PORT: 2181
    ports:
      - "2181:2181"

  kafka:
    image: confluentinc/cp-kafka:7.5.0
    depends_on:
      - zookeeper
    ports:
      - "9092:9092"
    environment:
      KAFKA_BROKER_ID: 1
      KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181
      KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://localhost:9092
      KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 1
      KAFKA_AUTO_CREATE_TOPICS_ENABLE: "true"

  # Spring Boot Application
  order-service:
    build:
      context: ./order-service
      dockerfile: Dockerfile
    ports:
      - "8080:8080"
      - "8081:8081"  # Management port
    environment:
      SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/orderdb
      SPRING_DATASOURCE_USERNAME: appuser
      SPRING_DATASOURCE_PASSWORD: localpassword
      SPRING_REDIS_HOST: redis
      SPRING_KAFKA_BOOTSTRAP_SERVERS: kafka:9092
      SPRING_PROFILES_ACTIVE: local
    depends_on:
      postgres:
        condition: service_healthy
      redis:
        condition: service_healthy
      kafka:
        condition: service_started

  # Angular Frontend
  frontend:
    build:
      context: ./frontend
      dockerfile: Dockerfile
    ports:
      - "4200:80"
    depends_on:
      - order-service

volumes:
  postgres_data:
```

---

## 10.2 CI/CD Pipelines: From Commit to Production

---

### The "Why" & The Problem

Without CI/CD:
- Deployments are manual, error-prone, and take hours. Someone SSHs into a server and runs scripts.
- Bugs are discovered days or weeks after the code was written (long feedback loops).
- Releases are rare, large, and terrifying "big bang" events.

With CI/CD:
- Every commit is automatically built, tested, and verified in minutes.
- Deployments are automated, repeatable, and can happen multiple times per day.
- Small, frequent releases reduce risk and make rollbacks trivial.

---

### Interviewer Expectations

- **CI/CD stages**: Build → Unit Test → Static Analysis → Integration Test → Container Build → Deploy to Staging → E2E Test → Deploy to Production.
- **Branching strategy**: Trunk-based development vs. GitFlow. Why trunk-based is preferred for CI/CD.
- **Deployment strategies**: Blue-green, canary, rolling update. When to use each.
- **Keywords**: "Trunk-based development", "feature flags", "blue-green deployment", "canary release", "immutable infrastructure", "GitOps", "artifact registry", "pipeline as code".

---

### The Deep Dive & Solution

#### CI/CD Pipeline Stages

```
Developer pushes code
        │
        ▼
┌──────────────────────────────────────────────────────┐
│  CI PIPELINE (runs on every commit/PR)                │
│                                                        │
│  1. ✅ Compile & Build                                │
│     └── mvn compile / npm run build                   │
│                                                        │
│  2. ✅ Unit Tests + Code Coverage                     │
│     └── mvn test (target: >80% coverage)              │
│     └── npm test (Angular unit tests)                  │
│                                                        │
│  3. ✅ Static Analysis / Linting                      │
│     └── SonarQube scan (code quality, security vulns) │
│     └── ESLint + Angular compiler strict mode          │
│                                                        │
│  4. ✅ Integration Tests                              │
│     └── Testcontainers (real DB, Redis, Kafka)        │
│                                                        │
│  5. ✅ Build & Push Container Image                   │
│     └── docker build → push to container registry      │
│     └── Tag: git-sha + semantic version                │
│                                                        │
│  6. ✅ Security Scan                                  │
│     └── Trivy / Snyk scan container image for CVEs    │
└──────────────────────────────────────────────────────┘
        │
        ▼  (merge to main)
┌──────────────────────────────────────────────────────┐
│  CD PIPELINE (runs on merge to main)                  │
│                                                        │
│  7. ✅ Deploy to Staging                              │
│     └── kubectl apply to staging namespace             │
│     └── Wait for readiness probes to pass              │
│                                                        │
│  8. ✅ E2E / Smoke Tests in Staging                   │
│     └── Playwright/Cypress tests against staging       │
│                                                        │
│  9. ✅ Deploy to Production (Canary)                  │
│     └── Route 5% traffic to new version               │
│     └── Monitor error rate and latency for 10 min     │
│                                                        │
│  10. ✅ Full Production Rollout                       │
│      └── Route 100% traffic to new version            │
│      └── Keep old version for instant rollback         │
└──────────────────────────────────────────────────────┘
```

#### Deployment Strategies

**Blue-Green Deployment**:
Two identical production environments: Blue (current) and Green (new). Deploy to Green, test it, then switch the load balancer to point to Green. If something goes wrong, switch back to Blue instantly.

```
Before:  LB → [Blue v1.0 ✓]    [Green (idle)]
Deploy:  LB → [Blue v1.0 ✓]    [Green v1.1 🔨 deploying...]
Test:    LB → [Blue v1.0 ✓]    [Green v1.1 ✓ tested]
Switch:  LB → [Green v1.1 ✓]   [Blue v1.0 (standby)]
Rollback: LB → [Blue v1.0 ✓]   [Green v1.1 ✗]  ← instant switch back
```

**Pros**: Instant rollback (just switch the LB). Zero downtime.
**Cons**: Requires 2x infrastructure. Database migrations must be backward-compatible.

**Canary Deployment**:
Deploy the new version to a small subset of servers/pods. Route a small percentage of real traffic to it. Monitor. Gradually increase traffic.

```
Step 1:  [v1.0] [v1.0] [v1.0] [v1.0] [v1.1 ← canary, 5% traffic]
Step 2:  [v1.0] [v1.0] [v1.0] [v1.1] [v1.1 ← 20% traffic]
Step 3:  [v1.0] [v1.1] [v1.1] [v1.1] [v1.1 ← 80% traffic]
Step 4:  [v1.1] [v1.1] [v1.1] [v1.1] [v1.1 ← 100% traffic]
```

**Pros**: Real users test the new version. Issues are caught with minimal blast radius.
**Cons**: Requires traffic splitting (Kubernetes + Istio or weighted Ingress routing). Must support two versions simultaneously.

**Rolling Update** (Kubernetes default):
Gradually replace old pods with new pods. At no point are all pods simultaneously unavailable.

**Pros**: Built into Kubernetes. No extra infrastructure.
**Cons**: During the rollout, both old and new versions serve traffic simultaneously. Must handle backward compatibility.

#### Trunk-Based Development vs. GitFlow

| Aspect | Trunk-Based | GitFlow |
|--------|-------------|---------|
| **Branching** | Short-lived feature branches (< 1 day), merge to main frequently | Long-lived feature/develop/release branches |
| **Integration** | Continuous — multiple merges per day | Periodic — merge at the end of a sprint |
| **CI/CD** | Natural fit — main is always deployable | Requires release branches, more manual process |
| **Feature management** | Feature flags to hide incomplete features | Feature branches to isolate incomplete work |
| **Risk** | Small, frequent merges = small conflicts | Large, infrequent merges = large conflicts |
| **Best for** | Teams practicing CI/CD, fast iteration | Teams with long release cycles, regulatory constraints |

**Recommendation for a Lead**: Advocate for **trunk-based development with feature flags**. It enables continuous delivery, reduces merge conflicts, and forces small, incremental changes.

#### GitHub Actions Pipeline — Full CI/CD for Spring Boot

```yaml
# .github/workflows/ci-cd.yml
name: CI/CD Pipeline

on:
  push:
    branches: [main]
  pull_request:
    branches: [main]

env:
  REGISTRY: ghcr.io
  IMAGE_NAME: ${{ github.repository }}/order-service
  JAVA_VERSION: '21'

jobs:
  # ============================================================
  # Job 1: Build, Test & Analyze
  # ============================================================
  build-and-test:
    runs-on: ubuntu-latest
    permissions:
      contents: read
      checks: write          # For test report publishing
      pull-requests: write   # For PR comments

    services:
      postgres:
        image: postgres:16-alpine
        env:
          POSTGRES_DB: testdb
          POSTGRES_USER: testuser
          POSTGRES_PASSWORD: testpass
        ports: ['5432:5432']
        options: >-
          --health-cmd "pg_isready -U testuser"
          --health-interval 10s
          --health-timeout 5s
          --health-retries 5

      redis:
        image: redis:7-alpine
        ports: ['6379:6379']
        options: >-
          --health-cmd "redis-cli ping"
          --health-interval 10s
          --health-timeout 5s
          --health-retries 5

    steps:
      - name: Checkout code
        uses: actions/checkout@v4

      - name: Set up JDK ${{ env.JAVA_VERSION }}
        uses: actions/setup-java@v4
        with:
          java-version: ${{ env.JAVA_VERSION }}
          distribution: 'temurin'
          cache: 'maven'

      - name: Build & Run Unit Tests
        run: mvn verify -B -Dspring.profiles.active=test
        env:
          SPRING_DATASOURCE_URL: jdbc:postgresql://localhost:5432/testdb
          SPRING_DATASOURCE_USERNAME: testuser
          SPRING_DATASOURCE_PASSWORD: testpass
          SPRING_REDIS_HOST: localhost

      - name: Publish Test Results
        uses: dorny/test-reporter@v1
        if: always()
        with:
          name: JUnit Test Results
          path: target/surefire-reports/TEST-*.xml
          reporter: java-junit

      - name: Upload Coverage to Codecov
        uses: codecov/codecov-action@v4
        with:
          files: target/site/jacoco/jacoco.xml

      - name: SonarQube Scan
        if: github.event_name == 'push'
        run: mvn sonar:sonar -Dsonar.host.url=${{ secrets.SONAR_HOST_URL }} -Dsonar.token=${{ secrets.SONAR_TOKEN }}

      - name: Upload build artifact
        uses: actions/upload-artifact@v4
        with:
          name: app-jar
          path: target/*.jar

  # ============================================================
  # Job 2: Build & Push Container Image
  # ============================================================
  build-image:
    needs: build-and-test
    if: github.ref == 'refs/heads/main'
    runs-on: ubuntu-latest
    permissions:
      contents: read
      packages: write

    outputs:
      image-tag: ${{ steps.meta.outputs.tags }}
      image-digest: ${{ steps.build.outputs.digest }}

    steps:
      - name: Checkout code
        uses: actions/checkout@v4

      - name: Set up Docker Buildx
        uses: docker/setup-buildx-action@v3

      - name: Log in to Container Registry
        uses: docker/login-action@v3
        with:
          registry: ${{ env.REGISTRY }}
          username: ${{ github.actor }}
          password: ${{ secrets.GITHUB_TOKEN }}

      - name: Extract metadata
        id: meta
        uses: docker/metadata-action@v5
        with:
          images: ${{ env.REGISTRY }}/${{ env.IMAGE_NAME }}
          tags: |
            type=sha,prefix=
            type=semver,pattern={{version}}
            type=raw,value=latest,enable={{is_default_branch}}

      - name: Build and push Docker image
        id: build
        uses: docker/build-push-action@v5
        with:
          context: .
          push: true
          tags: ${{ steps.meta.outputs.tags }}
          labels: ${{ steps.meta.outputs.labels }}
          cache-from: type=gha
          cache-to: type=gha,mode=max

      - name: Scan image for vulnerabilities
        uses: aquasecurity/trivy-action@master
        with:
          image-ref: ${{ env.REGISTRY }}/${{ env.IMAGE_NAME }}:latest
          format: 'sarif'
          output: 'trivy-results.sarif'
          severity: 'CRITICAL,HIGH'

  # ============================================================
  # Job 3: Deploy to Staging
  # ============================================================
  deploy-staging:
    needs: build-image
    runs-on: ubuntu-latest
    environment: staging

    steps:
      - name: Checkout code
        uses: actions/checkout@v4

      - name: Configure kubectl
        uses: azure/setup-kubectl@v3

      - name: Set Kubernetes context
        uses: azure/k8s-set-context@v3
        with:
          kubeconfig: ${{ secrets.KUBE_CONFIG_STAGING }}

      - name: Deploy to staging
        run: |
          kubectl set image deployment/order-service \
            order-service=${{ env.REGISTRY }}/${{ env.IMAGE_NAME }}@${{ needs.build-image.outputs.image-digest }} \
            -n staging
          kubectl rollout status deployment/order-service -n staging --timeout=300s

      - name: Run smoke tests
        run: |
          # Wait for service to be ready
          sleep 30
          # Run basic health check
          curl -f https://staging.example.com/actuator/health || exit 1
          # Run E2E smoke tests
          npm run test:e2e -- --config baseUrl=https://staging.example.com

  # ============================================================
  # Job 4: Deploy to Production (with manual approval)
  # ============================================================
  deploy-production:
    needs: deploy-staging
    runs-on: ubuntu-latest
    environment: production   # Requires manual approval in GitHub settings

    steps:
      - name: Checkout code
        uses: actions/checkout@v4

      - name: Set Kubernetes context
        uses: azure/k8s-set-context@v3
        with:
          kubeconfig: ${{ secrets.KUBE_CONFIG_PRODUCTION }}

      - name: Deploy canary (10% traffic)
        run: |
          kubectl set image deployment/order-service-canary \
            order-service=${{ env.REGISTRY }}/${{ env.IMAGE_NAME }}@${{ needs.build-image.outputs.image-digest }} \
            -n production
          kubectl rollout status deployment/order-service-canary -n production --timeout=300s

      - name: Monitor canary (5 minutes)
        run: |
          echo "Monitoring canary deployment for 5 minutes..."
          sleep 300
          # Check error rate from Prometheus
          ERROR_RATE=$(curl -s "http://prometheus:9090/api/v1/query?query=rate(http_server_requests_seconds_count{status=~'5..', app='order-service-canary'}[5m])" | jq '.data.result[0].value[1] // "0"')
          if (( $(echo "$ERROR_RATE > 0.01" | bc -l) )); then
            echo "Error rate too high ($ERROR_RATE). Rolling back canary."
            kubectl rollout undo deployment/order-service-canary -n production
            exit 1
          fi
          echo "Canary is healthy. Proceeding with full rollout."

      - name: Full production rollout
        run: |
          kubectl set image deployment/order-service \
            order-service=${{ env.REGISTRY }}/${{ env.IMAGE_NAME }}@${{ needs.build-image.outputs.image-digest }} \
            -n production
          kubectl rollout status deployment/order-service -n production --timeout=600s
```

#### GitLab CI/CD Pipeline — Alternative Example

```yaml
# .gitlab-ci.yml
stages:
  - build
  - test
  - analyze
  - package
  - deploy-staging
  - deploy-production

variables:
  MAVEN_OPTS: "-Dmaven.repo.local=.m2/repository"
  IMAGE_NAME: "$CI_REGISTRY_IMAGE/order-service"

# Cache Maven dependencies across jobs
cache:
  key: "${CI_COMMIT_REF_SLUG}"
  paths:
    - .m2/repository/

build:
  stage: build
  image: eclipse-temurin:21-jdk
  script:
    - ./mvnw compile -B
  artifacts:
    paths:
      - target/

test:
  stage: test
  image: eclipse-temurin:21-jdk
  services:
    - postgres:16-alpine
    - redis:7-alpine
  variables:
    POSTGRES_DB: testdb
    POSTGRES_USER: testuser
    POSTGRES_PASSWORD: testpass
    SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/testdb
    SPRING_REDIS_HOST: redis
  script:
    - ./mvnw verify -B
  artifacts:
    reports:
      junit: target/surefire-reports/TEST-*.xml
    paths:
      - target/site/jacoco/

sonarqube:
  stage: analyze
  image: eclipse-temurin:21-jdk
  script:
    - ./mvnw sonar:sonar -Dsonar.host.url=$SONAR_HOST_URL -Dsonar.token=$SONAR_TOKEN
  only:
    - main
    - merge_requests

package:
  stage: package
  image: docker:24
  services:
    - docker:24-dind
  script:
    - docker login -u $CI_REGISTRY_USER -p $CI_REGISTRY_PASSWORD $CI_REGISTRY
    - docker build -t $IMAGE_NAME:$CI_COMMIT_SHORT_SHA -t $IMAGE_NAME:latest .
    - docker push $IMAGE_NAME:$CI_COMMIT_SHORT_SHA
    - docker push $IMAGE_NAME:latest
  only:
    - main

deploy-staging:
  stage: deploy-staging
  image: bitnami/kubectl:latest
  script:
    - kubectl config use-context staging
    - kubectl set image deployment/order-service order-service=$IMAGE_NAME:$CI_COMMIT_SHORT_SHA -n staging
    - kubectl rollout status deployment/order-service -n staging --timeout=300s
  environment:
    name: staging
    url: https://staging.example.com
  only:
    - main

deploy-production:
  stage: deploy-production
  image: bitnami/kubectl:latest
  script:
    - kubectl config use-context production
    - kubectl set image deployment/order-service order-service=$IMAGE_NAME:$CI_COMMIT_SHORT_SHA -n production
    - kubectl rollout status deployment/order-service -n production --timeout=600s
  environment:
    name: production
    url: https://app.example.com
  when: manual   # Requires manual click to deploy to production
  only:
    - main
```

---

## 10.3 GitOps: Infrastructure as Code, Declarative Deployments

---

### The "Why" & The Problem

Traditional CI/CD pushes changes to the cluster. The CI server has cluster credentials and runs `kubectl apply`. Problems:
- **Security risk**: CI server has god-mode access to the production cluster. If the CI server is compromised, the attacker can deploy anything.
- **Drift**: Someone manually runs `kubectl edit` on the cluster. Now the cluster state doesn't match what's in Git. There's no single source of truth.
- **Auditability**: Who deployed what, when, and why? With push-based CD, you have to dig through CI logs.

GitOps solves this by **pulling changes from Git**. An agent running inside the cluster (ArgoCD, Flux) watches a Git repository. When Git changes, the agent reconciles the cluster to match Git. Git is the single source of truth.

---

### Interviewer Expectations

- **Pull-based vs. Push-based CD**: Know the difference. GitOps is pull-based.
- **ArgoCD concepts**: Application, Sync, Health, Rollback.
- **Separation of concerns**: Application code in one repo, Kubernetes manifests in another repo. CI updates the image tag in the manifest repo.
- **Keywords**: "GitOps", "ArgoCD", "declarative", "reconciliation loop", "single source of truth", "pull-based CD", "drift detection".

---

### The Deep Dive & Solution

#### GitOps Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│  DEVELOPER WORKFLOW                                              │
│                                                                   │
│  1. Developer pushes code to app-repo (main branch)              │
│  2. CI pipeline builds, tests, and pushes Docker image           │
│  3. CI pipeline updates image tag in config-repo                 │
│     (via automated PR or direct commit)                          │
└─────────────────────────────────────────────────────────────────┘
                               │
                               ▼
┌───────────────────┐    ┌──────────────────┐    ┌──────────────┐
│   app-repo        │    │  config-repo     │    │  Kubernetes  │
│   (source code)   │    │  (k8s manifests) │    │  Cluster     │
│                   │    │                  │    │              │
│  src/             │    │  base/           │    │  ArgoCD      │
│  Dockerfile       │──▶ │  overlays/       │◀──│  Agent       │
│  pom.xml         │ CI │    staging/       │    │  (watches    │
│                   │    │    production/    │    │   config-    │
│                   │    │  values.yaml     │    │   repo)      │
└───────────────────┘    └──────────────────┘    └──────────────┘
                                                         │
                              ┌───────────────────────────┘
                              │  Reconciliation Loop:
                              │  - Detects desired state (Git) ≠ actual state (cluster)
                              │  - Applies the diff to the cluster
                              │  - Reports sync status back
                              ▼
                     ┌──────────────────┐
                     │  Deployed Pods   │
                     │  (synced with    │
                     │   Git state)     │
                     └──────────────────┘
```

#### ArgoCD Application Manifest

```yaml
# argocd-application.yaml
apiVersion: argoproj.io/v1alpha1
kind: Application
metadata:
  name: order-service
  namespace: argocd
spec:
  project: default

  source:
    repoURL: https://github.com/myorg/k8s-config.git
    targetRevision: main
    path: overlays/production/order-service

  destination:
    server: https://kubernetes.default.svc
    namespace: production

  syncPolicy:
    automated:
      prune: true          # Delete resources removed from Git
      selfHeal: true       # Revert manual changes to match Git
    syncOptions:
      - CreateNamespace=true
    retry:
      limit: 5
      backoff:
        duration: 5s
        factor: 2
        maxDuration: 3m
```

#### Kustomize Overlay for Environment-Specific Config

```yaml
# config-repo/base/deployment.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: order-service
spec:
  replicas: 2
  template:
    spec:
      containers:
        - name: order-service
          image: ghcr.io/myorg/order-service:IMAGE_TAG_PLACEHOLDER
          ports:
            - containerPort: 8080
```

```yaml
# config-repo/overlays/production/kustomization.yaml
apiVersion: kustomize.config.k8s.io/v1beta1
kind: Kustomization

resources:
  - ../../base

replicas:
  - name: order-service
    count: 5  # Production needs more replicas

patches:
  - target:
      kind: Deployment
      name: order-service
    patch: |-
      - op: replace
        path: /spec/template/spec/containers/0/resources/requests/cpu
        value: "1000m"
      - op: replace
        path: /spec/template/spec/containers/0/resources/requests/memory
        value: "1Gi"

images:
  - name: ghcr.io/myorg/order-service
    newTag: abc123def  # Updated by CI pipeline
```

---

## 10.4 Feature Flags: Decoupling Deployment from Release

---

### The "Why" & The Problem

Without feature flags, **deployment = release**. Every time you deploy, the new code is immediately visible to all users. This creates problems:
- You can't deploy incomplete features without exposing them.
- Rollback means redeploying the old version (minutes to hours of downtime).
- A/B testing requires separate deployments.

Feature flags decouple **deployment** (putting code in production) from **release** (making features visible to users). You deploy code that's hidden behind a flag, then flip the flag when ready.

---

### Interviewer Expectations

- **Types of flags**: Release flags (temporary, removed after rollout), Operational flags (kill switches), Experiment flags (A/B tests), Permission flags (entitlements).
- **Flag lifecycle**: Create → Enable for internal users → Canary rollout (10%) → Full rollout (100%) → Remove the flag and dead code.
- **Keywords**: "Feature flag", "trunk-based development", "dark launch", "canary release", "kill switch", "flag lifecycle", "technical debt cleanup".

---

### The Deep Dive & Solution

#### Feature Flag Implementation with Spring Boot

```java
// FeatureFlagService.java — Centralized feature flag management
@Service
public class FeatureFlagService {

    private final FeatureFlagRepository flagRepository;
    private final CacheManager cacheManager;

    /**
     * Check if a feature is enabled for a specific user.
     * Supports: global flags, percentage rollouts, user targeting, and A/B groups.
     */
    @Cacheable(value = "feature-flags", key = "#flagName + ':' + #userId")
    public boolean isEnabled(String flagName, String userId) {
        FeatureFlag flag = flagRepository.findByName(flagName)
            .orElseReturn(FeatureFlag.disabled());

        if (!flag.isGloballyEnabled()) {
            return false;
        }

        // Check user-specific targeting (e.g., internal employees)
        if (flag.getTargetedUserIds().contains(userId)) {
            return true;
        }

        // Percentage rollout using consistent hashing
        // Same user always gets the same result (deterministic)
        if (flag.getRolloutPercentage() > 0) {
            int hash = Math.abs((flagName + userId).hashCode() % 100);
            return hash < flag.getRolloutPercentage();
        }

        return flag.isDefaultEnabled();
    }
}

// Using the flag in a controller
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired private FeatureFlagService featureFlags;
    @Autowired private OrderService orderService;
    @Autowired private OrderServiceV2 orderServiceV2;  // New implementation

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(
            @RequestBody CreateOrderRequest request,
            @AuthenticationPrincipal UserDetails user) {

        // Feature flag: dark launch new order processing
        if (featureFlags.isEnabled("new-order-processing", user.getUsername())) {
            return ResponseEntity.ok(orderServiceV2.createOrder(request));
        }

        // Fallback to old implementation
        return ResponseEntity.ok(orderService.createOrder(request));
    }
}
```

#### Feature Flag in Angular

```typescript
// feature-flag.service.ts
@Injectable({ providedIn: 'root' })
export class FeatureFlagService {
  private flags$ = new BehaviorSubject<Map<string, boolean>>(new Map());

  constructor(private http: HttpClient) {
    // Load flags on app startup and refresh periodically
    this.loadFlags();
    interval(60_000).pipe(
      switchMap(() => this.fetchFlags())
    ).subscribe(flags => this.flags$.next(flags));
  }

  isEnabled(flagName: string): Observable<boolean> {
    return this.flags$.pipe(
      map(flags => flags.get(flagName) ?? false),
      distinctUntilChanged()
    );
  }

  private fetchFlags(): Observable<Map<string, boolean>> {
    return this.http.get<Record<string, boolean>>('/api/feature-flags')
      .pipe(map(obj => new Map(Object.entries(obj))));
  }

  private loadFlags(): void {
    this.fetchFlags().subscribe(flags => this.flags$.next(flags));
  }
}

// feature-flag.directive.ts — Structural directive for templates
@Directive({ selector: '[appFeatureFlag]' })
export class FeatureFlagDirective implements OnInit, OnDestroy {
  @Input() appFeatureFlag: string = '';
  private subscription?: Subscription;

  constructor(
    private templateRef: TemplateRef<any>,
    private viewContainer: ViewContainerRef,
    private featureFlagService: FeatureFlagService
  ) {}

  ngOnInit(): void {
    this.subscription = this.featureFlagService.isEnabled(this.appFeatureFlag)
      .subscribe(enabled => {
        this.viewContainer.clear();
        if (enabled) {
          this.viewContainer.createEmbeddedView(this.templateRef);
        }
      });
  }

  ngOnDestroy(): void {
    this.subscription?.unsubscribe();
  }
}
```

```html
<!-- Usage in Angular template -->
<div *appFeatureFlag="'new-checkout-flow'">
  <app-new-checkout></app-new-checkout>
</div>

<div *appFeatureFlag="'!new-checkout-flow'">
  <app-legacy-checkout></app-legacy-checkout>
</div>
```

---

## 10.5 Infrastructure as Code (IaC): Terraform & Kubernetes

---

### The "Why" & The Problem

Manually provisioning cloud infrastructure (clicking through the AWS console) leads to:
- **Snowflake servers**: Each environment is slightly different. Nobody knows exactly what's running or how it was configured.
- **No audit trail**: Who created that S3 bucket? When? With what permissions?
- **Unreproducible environments**: "Creating a new staging environment" takes 3 days of manual work.

Infrastructure as Code (IaC) treats infrastructure like application code: version-controlled, reviewed, tested, and reproducible. Terraform is the most popular tool.

---

### Interviewer Expectations

- **Terraform basics**: Providers, resources, state, plan, apply.
- **State management**: Why state is critical, remote state (S3 + DynamoDB for locking), state locking.
- **Modules**: Reusable Terraform modules for common patterns (VPC, EKS cluster, RDS database).
- **Keywords**: "Infrastructure as Code", "Terraform", "idempotent", "desired state", "state file", "remote backend", "modules", "plan before apply".

---

### The Deep Dive & Solution

#### Terraform for AWS EKS + RDS + ElastiCache

```hcl
# main.tf — Production infrastructure for a microservices platform

terraform {
  required_version = ">= 1.6"

  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.0"
    }
  }

  # Remote state stored in S3 with DynamoDB locking
  backend "s3" {
    bucket         = "mycompany-terraform-state"
    key            = "production/infrastructure.tfstate"
    region         = "us-east-1"
    dynamodb_table = "terraform-locks"
    encrypt        = true
  }
}

provider "aws" {
  region = var.aws_region

  default_tags {
    tags = {
      Environment = var.environment
      ManagedBy   = "terraform"
      Team        = "platform"
    }
  }
}

# ============================================================
# VPC — Network isolation
# ============================================================
module "vpc" {
  source  = "terraform-aws-modules/vpc/aws"
  version = "~> 5.0"

  name = "${var.project}-${var.environment}-vpc"
  cidr = "10.0.0.0/16"

  azs             = ["${var.aws_region}a", "${var.aws_region}b", "${var.aws_region}c"]
  private_subnets = ["10.0.1.0/24", "10.0.2.0/24", "10.0.3.0/24"]
  public_subnets  = ["10.0.101.0/24", "10.0.102.0/24", "10.0.103.0/24"]

  enable_nat_gateway   = true
  single_nat_gateway   = false  # One NAT per AZ for HA
  enable_dns_hostnames = true

  # Tags for EKS auto-discovery of subnets
  private_subnet_tags = {
    "kubernetes.io/role/internal-elb" = 1
  }
  public_subnet_tags = {
    "kubernetes.io/role/elb" = 1
  }
}

# ============================================================
# EKS Cluster — Kubernetes
# ============================================================
module "eks" {
  source  = "terraform-aws-modules/eks/aws"
  version = "~> 19.0"

  cluster_name    = "${var.project}-${var.environment}"
  cluster_version = "1.29"

  vpc_id     = module.vpc.vpc_id
  subnet_ids = module.vpc.private_subnets

  # Managed node groups
  eks_managed_node_groups = {
    general = {
      instance_types = ["m6i.xlarge"]
      min_size       = 3
      max_size       = 10
      desired_size   = 5

      labels = {
        workload-type = "general"
      }
    }

    memory-optimized = {
      instance_types = ["r6i.xlarge"]
      min_size       = 1
      max_size       = 5
      desired_size   = 2

      labels = {
        workload-type = "memory-intensive"
      }

      taints = [{
        key    = "workload-type"
        value  = "memory-intensive"
        effect = "NO_SCHEDULE"
      }]
    }
  }
}

# ============================================================
# RDS — PostgreSQL Database
# ============================================================
module "rds" {
  source  = "terraform-aws-modules/rds/aws"
  version = "~> 6.0"

  identifier = "${var.project}-${var.environment}-postgres"

  engine            = "postgres"
  engine_version    = "16.1"
  instance_class    = "db.r6g.xlarge"
  allocated_storage = 100
  storage_encrypted = true

  db_name  = "orderdb"
  username = "appuser"
  port     = 5432

  multi_az               = true  # HA with automatic failover
  db_subnet_group_name   = module.vpc.database_subnet_group_name
  vpc_security_group_ids = [aws_security_group.rds.id]

  # Backups
  backup_retention_period = 30
  backup_window          = "03:00-04:00"
  maintenance_window     = "Mon:04:00-Mon:05:00"

  # Performance monitoring
  performance_insights_enabled = true
  monitoring_interval         = 60
  monitoring_role_arn        = aws_iam_role.rds_monitoring.arn

  # Read replicas for scaling reads
  create_db_parameter_group = true
  parameters = [
    {
      name  = "shared_preload_libraries"
      value = "pg_stat_statements"
    }
  ]
}

# ============================================================
# ElastiCache — Redis Cluster
# ============================================================
resource "aws_elasticache_replication_group" "redis" {
  replication_group_id       = "${var.project}-${var.environment}-redis"
  description                = "Redis cluster for caching and sessions"

  engine         = "redis"
  engine_version = "7.0"
  node_type      = "cache.r6g.large"

  num_cache_clusters = 3  # 1 primary + 2 replicas
  automatic_failover_enabled = true
  multi_az_enabled           = true

  subnet_group_name  = aws_elasticache_subnet_group.redis.name
  security_group_ids = [aws_security_group.redis.id]

  at_rest_encryption_enabled = true
  transit_encryption_enabled = true

  # Maintenance
  snapshot_retention_limit = 7
  snapshot_window         = "03:00-04:00"
  maintenance_window      = "mon:04:00-mon:05:00"
}
```

```hcl
# variables.tf
variable "project" {
  description = "Project name"
  type        = string
  default     = "order-platform"
}

variable "environment" {
  description = "Environment (staging, production)"
  type        = string
}

variable "aws_region" {
  description = "AWS region"
  type        = string
  default     = "us-east-1"
}
```

---

## 10.6 Security in the Pipeline: DevSecOps

---

### The "Why" & The Problem

Security can't be an afterthought. If you find a vulnerability in production, the cost of fixing it is 100x more than finding it during development. DevSecOps integrates security checks into every stage of the CI/CD pipeline.

---

### Interviewer Expectations

- **Shift-left security**: Move security testing earlier in the pipeline (developers find issues, not just the security team).
- **SAST/DAST/SCA**: Know the difference between Static Application Security Testing, Dynamic Application Security Testing, and Software Composition Analysis.
- **Container security**: Image scanning, non-root users, read-only filesystems, no secrets in images.
- **Keywords**: "Shift-left security", "SAST", "DAST", "SCA", "supply chain security", "image scanning", "secrets management", "OWASP Top 10".

---

### The Deep Dive & Solution

#### Security Scanning Integration Points

```
┌────────────────────────────────────────────────────────────┐
│  CODE COMMIT                                                │
│  ├── Pre-commit hooks: secrets scanning (gitleaks)         │
│  └── IDE plugins: SonarLint (real-time code analysis)      │
│                                                              │
│  PULL REQUEST                                                │
│  ├── SAST: SonarQube / Semgrep (finds code vulnerabilities)│
│  ├── SCA: Snyk / Dependabot (finds vulnerable dependencies)│
│  └── License scanning: FOSSA (ensures OSS license compliance)│
│                                                              │
│  BUILD                                                       │
│  ├── Container image scan: Trivy / Snyk Container          │
│  ├── Dockerfile linting: Hadolint                          │
│  └── SBOM generation: Syft (Software Bill of Materials)    │
│                                                              │
│  DEPLOYMENT                                                  │
│  ├── DAST: OWASP ZAP (runtime vulnerability scanning)      │
│  ├── Infrastructure scan: Checkov (Terraform/K8s misconfig)│
│  └── Runtime security: Falco (container behavior monitoring)│
│                                                              │
│  PRODUCTION                                                  │
│  ├── Vulnerability monitoring: Snyk Monitor (continuous)    │
│  ├── WAF: AWS WAF / Cloudflare (web application firewall)  │
│  └── Audit logging: all access logged and alertable        │
└────────────────────────────────────────────────────────────┘
```

#### SAST vs. DAST vs. SCA

| Type | What it does | When it runs | Example tools |
|------|-------------|-------------|---------------|
| **SAST** (Static Application Security Testing) | Analyzes source code for vulnerabilities without running it | During CI, on every PR | SonarQube, Semgrep, CodeQL |
| **DAST** (Dynamic Application Security Testing) | Attacks the running application to find vulnerabilities | After deployment to staging | OWASP ZAP, Burp Suite |
| **SCA** (Software Composition Analysis) | Scans dependencies (Maven, npm) for known CVEs | During CI, on every PR | Snyk, Dependabot, OWASP Dependency-Check |

#### Secrets Management

**Never store secrets in**:
- Source code (even if the repo is private)
- Docker images
- Environment variables in Dockerfiles
- CI/CD pipeline YAML files (use encrypted secrets)

**Where to store secrets**:

```
Application → Kubernetes Secrets (encrypted at rest via KMS)
                    or
Application → HashiCorp Vault → Dynamic secrets (short-lived, auto-rotated)
                    or
Application → AWS Secrets Manager → Automatic rotation via Lambda
```

```java
// Spring Boot integration with Vault
// bootstrap.yml
spring:
  cloud:
    vault:
      uri: https://vault.internal.example.com
      authentication: KUBERNETES
      kubernetes:
        role: order-service
        service-account-token-file: /var/run/secrets/kubernetes.io/serviceaccount/token
      kv:
        enabled: true
        backend: secret
        default-context: order-service

// In application code — secrets are injected like regular properties
@Value("${database.password}")
private String dbPassword;  // Comes from Vault, not application.yml
```

---

## 10.7 Docker Best Practices Checklist for Lead Engineers

---

### Production Docker Checklist

| # | Practice | Why |
|---|---------|-----|
| 1 | **Use multi-stage builds** | Build image has JDK + Maven (~800MB). Runtime image has only JRE (~200MB). |
| 2 | **Use specific image tags** | `FROM eclipse-temurin:21.0.2_13-jre-alpine` — not `latest`. Reproducible builds. |
| 3 | **Run as non-root** | Limits blast radius if container is compromised. |
| 4 | **Use `.dockerignore`** | Exclude `.git`, `target/`, `node_modules/`, `.env` from build context. Faster builds, no secrets in images. |
| 5 | **Order Dockerfile for caching** | Copy dependency files → install dependencies → copy source code. Dependencies are cached across builds. |
| 6 | **Set resource limits** | In Kubernetes: `resources.requests` and `resources.limits`. In Docker: `--memory` and `--cpus`. Prevents noisy neighbor problem. |
| 7 | **Health checks** | `HEALTHCHECK` in Dockerfile or readiness/liveness probes in Kubernetes. Restart unhealthy containers automatically. |
| 8 | **No secrets in images** | Use Kubernetes Secrets, Vault, or AWS Secrets Manager. Never `ENV DB_PASSWORD=...` in Dockerfile. |
| 9 | **Scan images for CVEs** | Trivy or Snyk in CI pipeline. Block deployments with CRITICAL CVEs. |
| 10 | **Use read-only root filesystem** | `securityContext.readOnlyRootFilesystem: true` in Kubernetes. Write to `emptyDir` volumes. |
| 11 | **Pin base image digests** for critical apps | `FROM eclipse-temurin:21-jre-alpine@sha256:abc123...` — immune to upstream tag changes. |
| 12 | **Label images** | `LABEL version="1.2.3" maintainer="team@example.com" git-commit="abc123"`. Traceability. |

#### `.dockerignore` Example

```
# .dockerignore — Keep build context small and secure
.git
.gitignore
.github
.vscode
.idea
*.md
LICENSE

# Build artifacts (we build inside the container)
target/
build/
dist/
node_modules/

# Environment and secret files
.env
.env.*
*.pem
*.key

# Test files (not needed in production image)
src/test/
**/*test*
**/*spec*
```

---

## 10.8 Interview Quick Reference: DevOps & CI/CD

---

### Common Interview Questions & Lead-Level Answers

**Q: "How would you design a CI/CD pipeline for a microservices application?"**

A: "I'd implement a **trunk-based development** workflow with **feature flags** for incomplete features. The CI pipeline runs on every commit: compile, unit tests, SonarQube SAST analysis, integration tests with Testcontainers, then builds and pushes a Docker image tagged with the git SHA. For CD, I'd use **GitOps with ArgoCD** — the CI pipeline updates the image tag in a config repository, and ArgoCD automatically syncs the cluster. Production deployments use a **canary strategy**: 5% traffic to the new version, monitored for 10 minutes via Prometheus metrics. If error rate stays below threshold, full rollout proceeds. For security, I integrate **Trivy** for image scanning, **Snyk** for dependency scanning, and **gitleaks** for secrets detection — all as pipeline gates that block deployment on critical findings."

**Q: "Explain the difference between blue-green and canary deployments."**

A: "**Blue-green** maintains two identical environments and switches traffic atomically via the load balancer. It offers instant rollback (just switch back) and zero mixed-version traffic, but requires 2x infrastructure and careful database migration planning. **Canary** deploys the new version to a subset of instances and gradually shifts traffic (e.g., 5% → 25% → 100%). It tests with real production traffic and catches issues with minimal blast radius, but requires traffic splitting infrastructure (like Istio or weighted ingress) and must handle two versions serving simultaneously. I prefer **canary for most microservices** because it catches subtle production issues that staging can't replicate, while blue-green is better for **database-heavy changes** where you want atomic cutover."

**Q: "How do you handle secrets in a Kubernetes environment?"**

A: "I use a **layered approach**. Base secrets are stored in **HashiCorp Vault** with dynamic secret generation — database credentials are ephemeral, auto-rotated every 24 hours. Applications access Vault via the **Vault Agent Injector** sidecar, which injects secrets as files into the pod. For Kubernetes-native secrets (like TLS certificates), I use **Sealed Secrets** or **External Secrets Operator** that syncs secrets from Vault/AWS Secrets Manager into Kubernetes Secret objects. Critical rules: never store secrets in Git (even encrypted, except Sealed Secrets which are designed for this), never bake secrets into Docker images, and always encrypt secrets at rest using KMS. All secret access is audit-logged."


