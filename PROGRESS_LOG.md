# 📋 Progress Log — DriverOS (Uber Driver Pulse)

Chronological development history, major iterations, and technical pivots.

---

## Day 1 — Project Setup & Backend Foundation

**Goals:** Understand problem statement, set up project, build core data models.

### Completed
- Read and analyzed the Uber Driver Pulse problem statement
- Set up Java Spring Boot project with Maven
- Defined core data models: `Driver`, `Ride`, `RideRequest`, `Shift`, `SensorReading`
- Created enums: `RideStatus`, `ShiftStatus`, `StressRating`, `AudioRating`, `MotionRating`
- Built in-memory repositories using HashMaps (`DriverRepository`, `RideRepository`)
- Implemented basic `DriverController` with register and shift endpoints

### Technical Decisions
- Chose **in-memory storage** over a database for rapid prototyping
- Used **HashMap** instead of a List for O(1) ride lookups by ID

---

## Day 1 (cont.) — Sensor Simulation & Stress Engine

**Goals:** Build the core stress detection system.

### Completed
- Built `SensorSimulator` — generates realistic audio + motion readings per ride snapshot
  - Audio: baseline + random spikes simulating cabin noise and arguments
  - Motion: baseline + harsh braking/acceleration events
- Implemented `StressScoreService` — computes combined score (40% audio, 60% motion)
- Built `StressRatingService` — maps numeric score to LOW / MEDIUM / HIGH / CRITICAL
- Implemented **Strategy Pattern** for stress aggregation:
  - `AverageStressStrategy` — mean across all snapshots
  - `PeakStressStrategy` — worst single moment
  - `WeightedStressStrategy` — recent events weighted higher

### Technical Pivot
- **Initially:** Planned equal 50/50 audio-motion weighting
- **Changed to:** 60% motion / 40% audio after reasoning that motion events (harsh braking, crashes) are more objectively measurable and safety-critical than audio spikes which can be false positives (music, radio)

---

## Day 1 (cont.) — Earnings Velocity

**Goals:** Build the earnings tracking and goal prediction system.

### Completed
- Built `EarningVelocityService` — computes:
  - `currentVelocity` = earnings so far / hours worked
  - `targetVelocity` = remaining goal / hours left
  - `paceStatus` = AHEAD / BEHIND / ON_TRACK
- Integrated velocity into driver report endpoint
- Added `EarningGoal` model with shift start/end times
- Built `CsvLogger` — writes to 4 structured CSV files after each ride

### CSV Output Files
| File | Contents |
|------|----------|
| `flagged_moments.csv` | Stress-flagged events with scores and explanations |
| `ride_summary_log.csv` | Per-ride summary with fare, stress rating, flag counts |
| `audio_sensor_log.csv` | Raw audio readings per snapshot |
| `motion_sensor_log.csv` | Raw motion readings per snapshot |

---

## Day 2 — React Frontend

**Goals:** Build a clean, production-quality driver-facing interface.

### Completed
- Set up React project with custom CSS design system (CSS variables for colors, fonts, spacing)
- Built core components: `Dashboard`, `AvailableRides`, `StressMonitor`, `Report`
- Implemented `Sidebar` navigation
- Built `RegisterModal` for driver onboarding (name, earning goal, shift hours)
- Built `apiGet` / `apiPost` helpers with error handling
- Added `useToast` hook for non-intrusive notifications
- Implemented **Earning Velocity Card** with live gauge bar (actual vs target pace)
- Added real-time auto-polling for velocity updates every 10 seconds during active rides

### Design Decisions
- Used **CSS variables** throughout for a consistent dark theme
- Lime (`#c8f135`) for driver actions, Cyan (`#35d4f1`) for data/info, Coral for warnings
- Monospace font for all data/metrics to feel like a real operations tool

---

## Day 2 (cont.) — Trip Summary & Admin Dashboard

**Goals:** Add post-ride explainability and admin monitoring view.

### Completed
- Built `TripSummary` modal — full post-ride report card including:
  - Stress timeline chart (Audio / Motion / Combined via Recharts AreaChart)
  - Earning velocity vs target LineChart
  - Flagged moments list with plain-English explanations
  - Cause analysis split by audio and motion
  - Overall verdict with emoji mood indicator
- Built `AdminDashboard` with:
  - 6 live stat cards (rides, flags, revenue)
  - All Rides table with stress ratings and flag counts
  - Flagged Moments table parsed from live CSV logs
  - CSV download buttons for all 4 log files
  - Auto-refresh every 15 seconds

### Technical Pivot
- **Initially:** Admin was just a tab inside the driver dashboard
- **Changed to:** Separate role-based login with splash screen and role selection cards
- **Reason:** Judges/demo audiences can clearly see the two distinct user perspectives

---

## Day 2 (cont.) — Auth, Deployment & Polish

**Goals:** Add login system, deploy to cloud, finalize submission structure.

### Completed
- Added role-based login: Driver (name + password) and Admin (password only)
- Driver name from login pre-fills the shift registration form
- Animated splash screen on app load
- Deployed backend to **Render** using Docker (Java not natively supported)
- Deployed frontend to **Vercel** (auto-detects React)
- Connected frontend `api.js` to Render backend URL
- Resolved Git merge conflicts from team's upstream changes
- Renamed project structure to match submission requirements (`uber` → `backend`)
- Added `Dockerfile` for containerized Spring Boot deployment
- Updated `README.md` with live links, trade-offs, and setup instructions

---

## Summary of Major Technical Pivots

| Decision | Original Plan | Final Approach | Reason |
|----------|--------------|----------------|--------|
| Stress weighting | 50/50 audio-motion | 60% motion, 40% audio | Motion more safety-critical |
| Admin access | Tab in driver UI | Separate role with login | Clearer demo, better UX |
| Storage | PostgreSQL | In-memory + CSV | Speed for hackathon |
| Stress aggregation | Single algorithm | 3 switchable strategies | Flexibility for Uber to tune |
| Backend deployment | Heroku | Render (Docker) | Heroku removed free tier |
| ML vs rules | Considered lightweight ML | Rule-based thresholds | Explainability, no training data |
