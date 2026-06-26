# File Storage System (Dropbox / Google Drive) — High-Level Design

## Problem Statement

Design a cloud file storage and sync system that:

- **Upload/download files** of any size (up to 50 GB per file)
- **Sync across devices** — changes on one device appear on others
- **Share files/folders** with other users (read/write permissions)
- **Version history** — restore previous versions
- **Handle concurrent edits** on the same file from multiple clients

---

## Requirements

### Functional Requirements

1. **Upload/Download**: Store and retrieve files; support resumable uploads.
2. **Sync**: Detect local changes, push to cloud, pull remote changes.
3. **Sharing**: Share by link or user with permission levels (view, edit).
4. **Versioning**: Keep N versions per file; allow restore.
5. **Metadata**: File name, size, checksum, parent folder, timestamps.

### Non-Functional Requirements

1. **Durability**: 99.999999999% (11 nines) — no data loss.
2. **Availability**: 99.9% for metadata; 99.99% for blob storage.
3. **Scale**: 500M users, 1B files/day uploaded, 100 PB total storage.
4. **Latency**: Metadata ops < 200ms; download starts within 1s.

---

## Capacity Estimation

```
Users: 500M, 20% daily active = 100M DAU
Uploads/day: 1B files, average 2 MB → 2 PB/day raw (dedup reduces significantly)
Average file: 2 MB
Metadata QPS: 100M users × 10 ops/day / 86400 ≈ 12K QPS (metadata)
Blob read QPS: ~500K peak (downloads dominate)

Storage after dedup (assume 3:1 ratio): ~700 TB/day new unique data
```

---

## High-Level Architecture

```
┌──────────┐     ┌─────────────┐     ┌────────────────────────────────────┐
│  Client  │────►│ API Gateway │────►│         Metadata Service           │
│ (sync    │     │             │     │  (file tree, permissions, versions)│
│  agent)  │     └─────────────┘     └──────────────┬─────────────────────┘
└────┬─────┘                                        │
     │                                              ▼
     │                              ┌───────────────────────────────┐
     │                              │  Metadata DB (sharded SQL)     │
     │                              │  files, folders, shares, ACL  │
     │                              └───────────────────────────────┘
     │
     │ Direct upload/download
     ▼
┌─────────────────────────────────────────────────────────────────────┐
│                     Blob Storage (S3 / Object Store)               │
│  Content-addressed chunks (SHA-256 hash as key)                     │
└─────────────────────────────────────────────────────────────────────┘
     │
     ▼
┌─────────────────┐     ┌──────────────────┐
│ Block Cache     │     │ CDN (for popular   │
│ (hot chunks)    │     │  shared files)     │
└─────────────────┘     └──────────────────┘
```

---

## Key Design: Chunking & Content-Addressable Storage

Large files split into **chunks** (4-64 MB):

```
File "report.pdf" (100 MB)
  → Chunk 1: hash abc123...
  → Chunk 2: hash def456...
  → Chunk 3: hash ghi789...

Metadata stores: file_id → [abc123, def456, ghi789]
Blob store: chunks keyed by hash (content-addressable)
```

**Benefits**:
- **Deduplication**: Same chunk uploaded twice → store once (hash match).
- **Resumable upload**: Retry failed chunks only.
- **Delta sync**: Client uploads only changed chunks.
- **Parallel upload**: Multiple chunks in parallel.

---

## Sync Protocol

### Client Sync Agent

```
1. Watch local filesystem (inotify / FSEvents)
2. On change: compute chunk hashes
3. Compare with server manifest (list of chunk hashes for file)
4. Upload only missing chunks
5. Update metadata with new chunk list
6. Poll / long-poll / WebSocket for remote changes
7. Download missing chunks, reconstruct file locally
```

### Conflict Resolution

| Strategy | When |
|----------|------|
| **Last-write-wins** | Simple, default for many files |
| **Keep both** | Create "report (conflicted copy).pdf" |
| **Operational Transform** | Real-time collaborative docs (Google Docs style) |

---

## Database Schema (Metadata)

```sql
users (id, email, storage_quota_bytes)

folders (id, user_id, parent_id, name, created_at)

files (id, folder_id, user_id, name, size, current_version_id, created_at, updated_at)

file_versions (id, file_id, chunk_list_json, size, checksum, created_at)

chunks (hash PK, size, storage_path, ref_count)  -- ref_count for garbage collection

shares (id, resource_id, resource_type, shared_with_user_id, permission, link_token)

sync_devices (id, user_id, device_name, last_sync_at, cursor)
```

**Sharding**: By `user_id` — all user metadata on same shard.

---

## API Design

```
POST   /v1/files/upload/init     → { upload_id, chunk_urls[] }
PUT    /v1/chunks/{hash}         → upload chunk (idempotent)
POST   /v1/files/upload/complete → finalize with chunk list

GET    /v1/files/{id}/download   → redirect to blob URL or chunk manifest
GET    /v1/files/{id}/versions   → version history
POST   /v1/shares               → create share link
GET    /v1/sync/changes?cursor=X → delta since cursor (sync API)
```

---

## Scaling Strategy

| Component | Strategy |
|-----------|----------|
| Metadata DB | Shard by user_id, read replicas for sync reads |
| Blob storage | S3 with cross-region replication |
| Hot files | CDN + edge caching for popular shared links |
| Upload | Direct-to-S3 presigned URLs — bypass API servers for bytes |
| Garbage collection | Background job: chunks with ref_count=0 → delete after grace period |

---

## Security

- **Encryption at rest**: SSE-S3 or client-side encryption (E2EE for enterprise)
- **Encryption in transit**: TLS 1.3
- **ACL on every metadata request**: User owns file OR has share permission
- **Presigned URLs**: Time-limited, scoped to specific chunk
- **Audit log**: Who accessed/shared which file

---

## Interview Discussion Points

1. **Why content-addressable storage?** Dedup, integrity (hash verifies content), idempotent uploads.
2. **How is sync different from upload?** Sync compares manifests; upload is raw transfer.
3. **Metadata vs blob separation?** Metadata is small, transactional, SQL. Blobs are large, immutable, object store.
4. **Handling 50 GB file?** Chunked upload with parallel streams; never single HTTP body.
5. **Cross-device consistency?** Eventual consistency with sync cursor; conflicts handled explicitly.

---

## Trade-offs

| Choice | Pros | Cons |
|--------|------|------|
| Chunk-level dedup | Massive storage savings | Hash computation on client |
| Last-write-wins | Simple | User may lose edits |
| SQL for metadata | ACID, joins for permissions | Shard carefully at scale |
| Direct S3 upload | Scales bandwidth | More complex client logic |
