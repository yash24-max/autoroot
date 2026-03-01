# 🚀 AutoRoot - AI-Powered API Observability Platform

> **Intelligent log correlation + auto-fix suggestions for microservices**

## Quick Start

```bash
# 1. Start infrastructure
docker-compose up -d

# 2. Start backend
cd backend && mvn spring-boot:run

# 3. Start frontend
cd frontend && npm run dev

# 4. Open dashboard
open http://localhost:3000
```

## What is AutoRoot?

AutoRoot transforms microservice debugging from hours of log-hunting into **10-minute incident resolution** using AI.

### The Problem
- 🔥 Incidents cascade across services
- 🔍 Engineers waste hours correlating logs
- 🤔 Root cause is buried in noise
- 🛠 Fix suggestions require domain expertise

### The Solution
- **Smart Correlation**: Groups logs by trace_id across services
- **AI Explanations**: "Auth DB timeout → Order failures → Payment failures"
- **Fix Suggestions**: "Add DB index on user_id" (rule-based, not guessing)
- **Risk Analysis**: Pre-deployment risk scoring

## Architecture

```
Microservices → Java Agent → Kafka → Correlation Engine → AI Analysis → Dashboard
```

## Core Features

- 🧠 **Incident Explainer**: Human-readable incident summaries
- 🔗 **Cascade Detection**: Find the first failing service
- 🛠 **Fix Suggestions**: Actionable remediation steps  
- 📊 **Risk Analysis**: Pre-deployment safety checks
- ⚡ **Real-time**: WebSocket updates, sub-second latency

## Tech Stack

- **Backend**: Spring Boot 3.2, PostgreSQL, Kafka, Redis
- **Frontend**: Next.js 14, TypeScript, Tailwind CSS
- **AI**: OpenAI API for embeddings + explanations
- **Storage**: MinIO (S3-compatible) for log archives
- **Deployment**: Docker Compose → Kubernetes

## Documentation

- 📋 [PROJECT.md](./PROJECT.md) - Complete specifications
- 🏗 [Architecture](./docs/architecture.md)
- 🔧 [Development Setup](./docs/development.md)
- 📚 [API Documentation](./docs/api.md)

## Status

🟡 **Phase 1**: Foundation setup (In Progress)

See [PROJECT.md](./PROJECT.md) for detailed status and roadmap.

---

**Built for SaaS teams with 5-30 engineers who need SRE-level observability without dedicated SRE teams.**