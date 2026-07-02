# Design Uber / Ride-Sharing Platform

> **You are here**: Senior SDE — System Design (HLD)
> **Roadmap**: [Developer Master Roadmap](../../../ROADMAP.md) | **Prerequisites**: [HLD_Template.md](../../00_Templates/HLD_Template/HLD_Template.md)

## Problem Statement

Design a ride-sharing platform like Uber where:
- Riders can request rides from point A to point B
- Drivers can accept ride requests
- The system matches the nearest available driver to a rider
- Real-time tracking of driver location
- Fare estimation and payment processing
- Trip history for both riders and drivers

---

## Step 1: Requirements

### Functional Requirements
1. **Rider requests a ride** — Specify pickup and dropoff locations
2. **Driver matching** — Find and assign the nearest available driver
3. **Real-time location tracking** — Track driver's location during the trip
4. **Fare estimation** — Calculate estimated fare before ride confirmation
5. **Fare calculation** — Calculate actual fare after trip completion (distance + time + surge)
6. **Trip management** — Start, track, complete, cancel trips
7. **Driver availability** — Drivers toggle online/offline status

### Non-Functional Requirements
- **Scale**: 100M riders, 5M drivers, 20M rides/day
- **Latency**: Matching within 5 seconds, location updates every 3-5 seconds
- **Availability**: 99.99% (critical infrastructure)
- **Consistency**: Strong consistency for ride state (no double-booking), eventual consistency for location

### Capacity Estimation

```
Rides/day: 20M → ~230 rides/sec, peak ~700/sec
Active drivers: 2M at any time (sending location updates)
Location updates: 2M drivers × 1 update/4 sec = 500K updates/sec

Storage:
  Trip record: ~2 KB → 20M × 2 KB = 40 GB/day
  Location history: 500K/sec × 100 bytes × 86400 sec = ~4.3 TB/day (use TTL, purge old data)
```

---

## Step 2: High-Level Architecture

```
┌──────────────┐     ┌──────────────┐     ┌──────────────────┐
│  Rider App   │     │  Driver App  │     │   API Gateway    │
│  (Mobile)    │────▶│  (Mobile)    │────▶│  + Load Balancer │
└──────────────┘     └──────────────┘     └────────┬─────────┘
                                                    │
          ┌─────────────────────────────────────────┼─────────────┐
          │                 │               │       │             │
    ┌─────▼─────┐    ┌─────▼─────┐   ┌─────▼─────┐ │   ┌─────────▼─────┐
    │  Ride     │    │  Location │   │  Matching │ │   │  Payment      │
    │  Service  │    │  Service  │   │  Service  │ │   │  Service      │
    └─────┬─────┘    └─────┬─────┘   └─────┬─────┘ │   └───────┬───────┘
          │                │               │        │           │
    ┌─────▼─────┐    ┌─────▼─────┐   ┌─────▼─────┐ │   ┌───────▼───────┐
    │  Ride DB  │    │  Location │   │  Geo Index │ │   │  Payment      │
    │  (MySQL/  │    │  Store    │   │ (QuadTree/ │ │   │  Gateway      │
    │  Postgres)│    │  (Redis)  │   │  Geohash)  │ │   │  (Stripe)     │
    └───────────┘    └───────────┘   └────────────┘ │   └───────────────┘
                                                     │
    ┌─────────────┐  ┌─────────────┐   ┌─────────────▼─┐
    │  ETA        │  │ Pricing /   │   │ Notification  │
    │  Service    │  │ Surge       │   │ Service       │
    └─────────────┘  │ Service     │   └───────────────┘
                     └─────────────┘
```

---

## Step 3: Detailed Component Design

### 3.1 Location Service — The Core Technical Challenge

Drivers send GPS coordinates every 3-5 seconds. The system must:
1. **Ingest** 500K location updates per second.
2. **Store** the latest location for quick lookup.
3. **Index** locations for spatial queries (find drivers near a point).

**Location Update Flow**:
```
Driver App → WebSocket connection → Location Service → Redis (current location)
                                                     → Kafka (location history for analytics)
                                                     → Geo Index update (QuadTree/Geohash)
```

**Current Location Store (Redis)**:
```
Key: driver:location:{driver_id}
Value: { "lat": 12.9716, "lng": 77.5946, "timestamp": 1707832800, "status": "available" }
TTL: 60 seconds (if no update, consider driver offline)
```

### 3.2 Geospatial Indexing — Finding Nearby Drivers

This is the critical data structure choice. Three options:

#### Option A: Geohash + Redis

Divide the world into grid cells using Geohash encoding. Each cell is a string prefix.

```
Geohash "tdr1w" covers a ~5km × 5km area
Redis: GEOADD drivers 77.5946 12.9716 "driver_123"
Query: GEORADIUS drivers 77.5946 12.9716 5 km WITHDIST COUNT 20 ASC
```

**Pros**: Simple to implement, Redis handles it natively, O(n) in the cell.
**Cons**: Edge cases at cell boundaries (driver 1 km away might be in a different cell).

#### Option B: QuadTree (In-Memory)

Recursively partition 2D space into four quadrants. Each leaf node holds drivers in that region.

```
World → 4 quadrants → each subdivided → ... → leaf with list of drivers

Query: Find all drivers within radius R of point P
  1. Start at root
  2. Recurse into children that overlap with the search circle
  3. Collect drivers from overlapping leaf nodes
  4. Filter by exact distance
```

**Pros**: Efficient for non-uniform distribution, adaptive resolution.
**Cons**: More complex to implement, need to handle dynamic updates.

#### Option C: S2 Geometry (What Uber Actually Uses)

Google's S2 library maps Earth's surface onto a unit sphere and uses a Hilbert curve to convert 2D coordinates to 1D cell IDs. This allows range queries on a single index dimension.

**Decision**: For interviews, Geohash + Redis is the simplest to explain. QuadTree shows deeper knowledge. Mention S2 as a production-grade alternative.

### 3.3 Matching Service — Driver-Rider Assignment

**Matching Algorithm**:
1. Rider requests a ride at location (lat, lng).
2. Query Geo Index for available drivers within expanding radius (start 2 km, expand to 5 km, then 10 km).
3. Filter: only drivers with status = "available", matching vehicle type.
4. Rank by: distance, ETA, driver rating, acceptance rate.
5. Send request to top-ranked driver.
6. If driver does not accept within 15 seconds, move to the next driver.
7. If no driver accepts after 3 attempts, notify rider "no drivers available."

```java
// Pseudocode for matching
public MatchResult matchDriver(RideRequest request) {
    double[] radiuses = {2.0, 5.0, 10.0}; // km

    for (double radius : radiuses) {
        List<Driver> nearby = geoIndex.findDrivers(
            request.getPickupLat(),
            request.getPickupLng(),
            radius,
            limit = 20
        );

        List<Driver> eligible = nearby.stream()
            .filter(d -> d.getStatus() == AVAILABLE)
            .filter(d -> d.getVehicleType() == request.getVehicleType())
            .sorted(Comparator.comparingDouble(d -> calculateScore(d, request)))
            .collect(Collectors.toList());

        if (!eligible.isEmpty()) {
            return new MatchResult(eligible.get(0), estimateETA(eligible.get(0), request));
        }
    }

    return MatchResult.NO_DRIVERS_AVAILABLE;
}
```

### 3.4 Ride Service — Trip State Machine

A ride goes through these states:

```
REQUESTED → DRIVER_ASSIGNED → DRIVER_EN_ROUTE → DRIVER_ARRIVED
    ↓                                              ↓
CANCELLED                                    TRIP_STARTED
                                                  ↓
                                            TRIP_COMPLETED
                                                  ↓
                                              PAYMENT_PROCESSED
```

**Database Schema**:
```sql
CREATE TABLE rides (
    ride_id          UUID PRIMARY KEY,
    rider_id         BIGINT NOT NULL,
    driver_id        BIGINT,
    status           VARCHAR(30) NOT NULL,
    pickup_lat       DECIMAL(10, 7),
    pickup_lng       DECIMAL(10, 7),
    dropoff_lat      DECIMAL(10, 7),
    dropoff_lng      DECIMAL(10, 7),
    vehicle_type     VARCHAR(20),
    estimated_fare   DECIMAL(10, 2),
    actual_fare      DECIMAL(10, 2),
    distance_km      DECIMAL(8, 2),
    duration_minutes INT,
    surge_multiplier DECIMAL(3, 2) DEFAULT 1.00,
    requested_at     TIMESTAMP,
    started_at       TIMESTAMP,
    completed_at     TIMESTAMP,
    INDEX idx_rider (rider_id, requested_at DESC),
    INDEX idx_driver (driver_id, requested_at DESC)
);
```

### 3.5 Pricing / Surge Service

**Base Fare Calculation**:
```
fare = base_fare + (per_km_rate × distance_km) + (per_min_rate × duration_min) + booking_fee
```

**Surge Pricing**:
When demand exceeds supply in a geographic area, apply a surge multiplier.

```
surge_multiplier = demand_in_area / supply_in_area

If surge > 1.0:
  final_fare = base_fare × surge_multiplier
```

**How to calculate demand/supply per area**:
- Divide the city into hexagonal cells (H3 index) or Geohash cells.
- Count ride requests in each cell in the last 5 minutes (demand).
- Count available drivers in each cell (supply).
- Calculate surge multiplier per cell.
- Update every 30-60 seconds.

### 3.6 ETA Service

Estimated Time of Arrival uses:
1. **Straight-line distance** for rough estimation.
2. **Route-based distance** using a routing engine (OSRM, Google Maps API).
3. **Historical traffic data** to adjust for time of day and day of week.
4. **Real-time traffic** from driver location updates (aggregate speed data from active drivers).

---

## Step 4: API Design

```
POST   /api/v1/rides/estimate
  Body: { "pickup": {"lat": 12.97, "lng": 77.59}, "dropoff": {"lat": 12.93, "lng": 77.61}, "vehicle_type": "standard" }
  Response: { "estimated_fare": 250.00, "surge": 1.5, "eta_minutes": 5 }

POST   /api/v1/rides
  Body: { "pickup": {...}, "dropoff": {...}, "vehicle_type": "standard", "payment_method": "card_123" }
  Response: 201 { "ride_id": "...", "status": "REQUESTED" }

PATCH  /api/v1/rides/{ride_id}
  Body: { "status": "TRIP_STARTED" }

GET    /api/v1/rides/{ride_id}
  Response: { "ride_id": "...", "status": "...", "driver": {...}, "eta": 3 }

WebSocket /ws/v1/rides/{ride_id}/track
  → Streams driver location updates to rider in real-time

POST   /api/v1/drivers/{driver_id}/location
  Body: { "lat": 12.97, "lng": 77.59, "heading": 180, "speed": 35 }

PATCH  /api/v1/drivers/{driver_id}/status
  Body: { "status": "available" | "busy" | "offline" }
```

---

## Step 5: Scaling Strategy

### Location Ingestion at Scale

```
500K updates/sec → Kafka (partitioned by city or geohash prefix)
  → Consumer groups update Redis (current location)
  → Consumer groups update Geo Index
  → Consumer groups write to time-series DB (location history for analytics)
```

### Database Scaling

| Component | Strategy |
|-----------|----------|
| Rides DB | Shard by ride_id (or city_id + ride_id), read replicas |
| Location Store | Redis Cluster, shard by driver_id |
| Geo Index | Partition by city/region, each region has its own index |
| Payment | Separate DB with strong consistency (ACID) |

### City-Based Partitioning

Uber operates independently in each city. This is a natural partitioning boundary:
- Each city has its own Matching Service, Geo Index, and Location Store.
- Cross-city rides are rare and handled specially.
- This dramatically reduces the scope of each component.

---

## Step 6: Real-Time Communication

### WebSocket for Ride Tracking

```
Rider opens WebSocket → /ws/rides/{ride_id}/track

Driver sends location update → Location Service → publishes to Redis Pub/Sub channel "ride:{ride_id}"

WebSocket server subscribes to "ride:{ride_id}" → forwards to rider's WebSocket

Result: Rider sees driver moving on the map in real-time (3-5 second updates)
```

### Push Notifications

| Event | Recipient | Channel |
|-------|-----------|---------|
| Driver assigned | Rider | Push notification + in-app |
| Driver arrived | Rider | Push notification |
| Ride request | Driver | Push notification + sound alert |
| Trip completed | Both | Push notification + receipt email |
| Payment processed | Rider | Push notification |

---

## Step 7: Fault Tolerance

| Failure | Impact | Mitigation |
|---------|--------|-----------|
| Matching Service down | Cannot create new rides | Multiple instances, circuit breaker, queue ride requests |
| Location Service down | Cannot track drivers | Cache last-known location, graceful degradation |
| Payment Service down | Cannot process payment | Queue payment, complete ride first, charge later |
| Redis cluster down | Geo queries fail | Fall back to database-backed geo queries (slower) |
| Driver app loses connectivity | Location updates stop | TTL on driver location (60s), mark offline |

---

## Interview Discussion Points

1. **Why Geohash over QuadTree?** — Geohash is simpler, maps to Redis GEORADIUS natively, and handles most use cases. QuadTree is better for non-uniform distribution. S2 is production-grade (Uber uses it).
2. **How to handle surge pricing?** — Divide city into cells, count demand vs supply per cell every 30-60 seconds, apply multiplier. Use exponential smoothing to avoid rapid fluctuations.
3. **Consistency for ride matching** — Use optimistic locking or distributed lock (Redis SETNX) to prevent double-booking a driver. Only one ride request can lock a driver at a time.
4. **How to handle driver going offline during a ride?** — Detect via missing location updates (TTL). Alert rider, attempt to reassign, and handle payment for partial trip.
5. **Why city-based partitioning?** — Natural boundary, reduces cross-partition queries to near-zero, simplifies scaling.

---

**Difficulty**: Hard
**Frequency**: Very High — top 5 most asked HLD problem
**Key Patterns**: Geospatial Indexing (Geohash/QuadTree/S2), WebSocket, State Machine, Surge Pricing, City-based Partitioning

