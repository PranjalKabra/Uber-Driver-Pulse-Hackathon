# 🚕 DriverOS — Uber Driver Pulse

> A real-time driver wellness and operations platform built for the Uber She++ Hackathon 2026.

---

## 🌐 Live Deployment

| Service | URL |
|---------|-----|
| **Frontend (Live App)** | https://uber-driver-pulse-hackathon.vercel.app |
| **Backend API** | https://uber-driver-pulse-hackathon.onrender.com |
| **Demo Video** | *(see demo_video.mp4 in repo root)* |

---

## 📌 Problem Statement

Uber drivers face significant stress during their shifts — from harsh road conditions to difficult passengers — but there's no system to monitor or respond to these stress signals in real time. **DriverOS** bridges this gap by tracking driver stress through simulated sensors and giving both drivers and Uber admins actionable insights.

---

## 💡 Solution

DriverOS is a full-stack platform with **two perspectives**:

- 🚗 **Driver POV** — Drivers start shifts, accept/reject rides, track earnings, and monitor their own stress levels in real time with full trip summaries after each ride.
- 🛡️ **Admin POV** — Uber admins monitor all rides platform-wide, review flagged stress moments, and download raw CSV logs for analysis.

---

## ✨ Features

### Driver Dashboard
- Register and start a shift with a personal earning goal
- Generate and accept/reject ride requests
- Real-time earning velocity tracker — shows if you're on pace to hit your goal
- End-of-ride Trip Summary with stress timeline, flagged moments, cause analysis and overall verdict
- End-of-shift report

### Stress Detection Engine
- **Audio Sensor** — detects sustained high decibel levels (arguments, shouting, cabin noise)
- **Motion Sensor** — detects harsh braking and aggressive acceleration
- **Combined Score** — weighted combination: 40% audio + 60% motion
- **3 Stress Strategies** — Average, Peak, Weighted (switchable per ride)
- **Explainability** — every flagged moment includes a plain-English explanation

### Admin Dashboard
- Platform-wide live stats (total rides, ongoing, flags, revenue)
- Full ride table with driver, route, fare, stress rating, and flag count
- Flagged moments table from live CSV logs
- One-click CSV download for all 4 sensor/log files
- Auto-refreshes every 15 seconds

---

## 🏗️ Tech Stack

| Layer | Technology |
|-------|------------|
| Backend | Java 17 + Spring Boot |
| Frontend | React.js |
| Deployment (Backend) | Render (Docker) |
| Deployment (Frontend) | Vercel |
| Data Storage | In-memory repositories + CSV logging |
| Build | Maven |

---

## ⚖️ Design Trade-offs

### Rule-Based vs Machine Learning
We chose a **rule-based system** with configurable thresholds over ML for the following reasons:
- **Explainability** — Every flagged moment can be explained to the driver in plain English. ML black-box decisions would undermine driver trust.
- **No training data needed** — We simulate sensors rather than using historical data, making ML impractical.
- **Lightweight** — Rule-based logic runs instantly with no inference latency.
- **Debuggable** — Thresholds can be tuned by a product manager without retraining a model.

### Three Stress Strategies (Strategy Pattern)
Rather than hard-coding one stress aggregation method, we implemented three switchable strategies:
- **Average** — smooths out spikes, good for long rides
- **Peak** — captures worst moment, good for safety monitoring
- **Weighted** — recent events weigh more, good for real-time alerts

This lets Uber tune the system per use case without changing core logic.

### In-Memory Storage vs Database
We used in-memory HashMaps + CSV logging instead of a database because:
- Zero setup time for a hackathon prototype
- CSV logs provide transparent, auditable output exactly as required
- Easily replaceable with PostgreSQL in production

### Sensor Simulation
Real phone sensors were outside hackathon scope, so we built a `SensorSimulator` that generates realistic audio and motion data with configurable randomness, spike probability, and ride-phase variation.

---

## 📂 Project Structure

```
DriverPulse/
├── backend/                          # Spring Boot Backend
│   ├── src/main/java/com/uber/
│   │   ├── controller/               # REST API endpoints
│   │   ├── models/                   # Driver, Ride, Shift, etc.
│   │   ├── service/                  # Business logic + sensor simulation
│   │   ├── repository/               # In-memory data store
│   │   ├── strategy/                 # Stress rating strategies
│   │   └── enums/                    # Status enums
│   ├── log/                          # CSV logs (auto-generated at runtime)
│   │   ├── flagged_moments.csv
│   │   ├── ride_summary_log.csv
│   │   ├── audio_sensor_log.csv
│   │   └── motion_sensor_log.csv
│   └── Dockerfile
│
└── frontend/driver-dashboard/        # React Frontend
    └── src/
        ├── components/
        │   ├── LandingPage.js        # Splash + role selection + login
        │   ├── Sidebar.js            # Navigation sidebar
        │   ├── Dashboard.js          # Driver dashboard
        │   ├── AvailableRides.js     # Ride request list
        │   ├── StressMonitor.js      # Real-time stress charts
        │   ├── TripSummary.js        # Post-ride summary modal
        │   ├── Report.js             # Driver report
        │   ├── AdminDashboard.js     # Admin operations view
        │   ├── RegisterModal.js      # Driver registration
        │   └── UI.js                 # Shared UI components
        ├── hooks/useToast.js
        ├── api.js                    # API helper (points to Render backend)
        └── App.js
```

---

## 🚀 Local Setup

### Prerequisites
- Java 17+, Maven 3.6+, Node.js 18+

### Backend
```bash
cd backend
mvn spring-boot:run
# Runs on http://localhost:8080
```

### Frontend
```bash
cd frontend/driver-dashboard
npm install
npm start
# Runs on http://localhost:3000
```

> For local development, change `api.js` BASE to `'/api'` and add a proxy in `package.json`.

---

## 🔌 API Endpoints

### Driver
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/driver/register` | Register driver + start shift |
| POST | `/api/rides/generate` | Generate ride requests |
| GET | `/api/rides/available` | List available rides |
| POST | `/api/rides/accept` | Accept a ride |
| POST | `/api/rides/reject` | Reject a ride |
| POST | `/api/rides/{id}/complete` | Complete a ride |
| GET | `/api/rides/{id}/stress` | Get stress snapshots |
| POST | `/api/rides/{id}/strategy` | Switch stress strategy |
| GET | `/api/driver/{id}/report` | Driver shift report |
| POST | `/api/shift/end` | End shift |

### Admin
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/admin/dashboard` | Platform summary stats |
| GET | `/api/admin/rides` | All rides |
| GET | `/api/admin/flagged-moments` | All flagged stress moments |
| GET | `/api/admin/csv/flagged-moments` | Download CSV |
| GET | `/api/admin/csv/ride-summary` | Download CSV |
| GET | `/api/admin/csv/audio-log` | Download CSV |
| GET | `/api/admin/csv/motion-log` | Download CSV |

---

## 🔐 Login Credentials

| Role | Password |
|------|----------|
| Driver | `driver123` |
| Admin | `admin123` |

---

## 👥 Team

Built with ❤️ for the **Uber She++ Hackathon 2026**
