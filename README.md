# ⚔️ Coding Arena Backend

> **A standalone software system designed to automate the management and evaluation of programming competitions.**
> 
> *Created by team **Gladiator Radiator** for GK Software Czech Republic s.r.o.*

## 📖 Overview
The **Coding Arena** is a platform built specifically as a core engine for hosting time-limited coding challenges. It provides the underlying logic and execution infrastructure utilized during events such as technical workshops or promotional recruitment activities (PvP competitive programming).
The system supports two primary operational modes:
* **🔴 Live Contest Mode:** Multiple teams compete simultaneously within a fixed time window.
* **📈 Ladder Mode:** Individual teams can start challenges at any time while competing on a shared, persistent leaderboard.

## ✨ Core Features
* **Dynamic Task Assignment:** Automatically selects and assigns unsolved tasks based on difficulty tiers (100, 200, 400 points).
* **Robust Sandbox Evaluation:** Interfaces securely with the **Judge0 API** to execute participant source code in an isolated Docker container.
* **Automated Scoring & Penalties:** Awards points on `Accepted` verdicts and applies a `-50` point penalty when a task is `Surrendered`.
* **Real-time Leaderboard:** Aggregates participant data for real-time rankings based on total points and submission speed (Tie-breaker).
* **Data Liability Compliance:** Evaluated code is strictly *Transient*—it is executed in memory and immediately discarded to ensure data security and compliance.
* **Role-based Admin API:** Allows organizers to dynamically inject new tasks and test cases on the fly.

## 🛠️ Technology Stack
* **Java 17+** & **Spring Boot 3.x** (Web, Data JPA)
* **PostgreSQL 15** (Relational Data Storage)
* **Judge0 CE** (Remote Code Execution API)
* **Docker & Docker Compose** (Container Orchestration)
* **Maven** (Dependency Management)

## 🚀 Quick Start (Docker Run)

The entire architecture (Backend API, PostgreSQL DB, Redis, and Judge0 Server + Workers) is fully containerized.
1. **Clone the repository:**
   ```bash
   git clone https://github.com/gladiator-radiator/coding-arena.git
   cd coding-arena
   ```
2. **Build and spin up the environment:**
   ```bash
   sudo docker compose up -d --build
   ```
3. The API will be available at `http://localhost:8080`.
*(Database is seeded automatically via `data.sql` with a test contest, users, and preliminary tasks).*

## 🔌 API Endpoints
All API endpoints require the unique HTTP header `X-User-Id` to authenticate the participant requesting the action.
**Participants:**
* `POST /api/tasks/request?tier={tier}&contestId={id}`: Locks a random unsolved task for the team.
* `POST /api/submissions`: Submits source code (JSON payload) to Judge0.
* `POST /api/tasks/surrender`: Gives up the locked task (applies penalty).
* `GET /api/leaderboard?contestId={id}`: Fetches the real-time calculated leaderboard.

**Administrators (Role: ADMIN required):**
* `POST /api/admin/tasks`: Create new coding challenges.
* `POST /api/admin/tasks/{taskId}/testcases`: Add hidden I/O test cases to existing challenges.

## 🧪 Testing
A complete Postman Collection is available for testing the core lifecycle scenarios (Task request -> Submission -> Penalties -> Leaderboard). 
Import the collection directly into Postman to demonstrate the business logic and Edge-cases (such as proper tie-breaker sorting based on `submitted_at` indexes).
