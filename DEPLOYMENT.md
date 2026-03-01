# 🚀 AutoRoot Deployment Guide

This guide outlines how to deploy the AutoRoot platform using **free-tier** services.

---

## 🛠 Target Architecture

| Component | Recommended Provider | Why? |
|-----------|----------------------|------|
| **PostgreSQL** | [Neon.tech](https://neon.tech) | Excellent serverless Postgres with a generous free tier. |
| **Kafka** | [Upstash](https://upstash.com) | Serverless Kafka with zero management and a solid free tier. |
| **Backend Services** | [Railway](https://railway.app) | Easiest for Docker-based microservices. |
| **Frontend** | [Vercel](https://vercel.com) | Native Next.js support, lightning fast. |
| **Object Storage** | [Supabase Storage](https://supabase.com) | S3-compatible and very easy to set up. |

---

## 1. 🏗 Infrastructure Setup (Free Tier)

### 🐘 Database (Neon)
1. Create a project on [Neon](https://neon.tech).
2. Copy the **Connection String** (Postgres URL).
3. Ensure you have the `tenant_id` and other tables initialized (Railway/Docker will do this via JPA DDL-Auto).

### 🎡 Message Queue (Upstash Kafka)
1. Create a Kafka cluster on [Upstash](https://upstash.com).
2. Create a topic named `autoroot.logs`.
3. Copy the `BOOTSTRAP_SERVER`, `SASL_USERNAME`, and `SASL_PASSWORD`.

---

## 2. 🔌 Backend Deployment (Railway)

Railway allows you to deploy directly from your GitHub repository using the `Dockerfile` in each module (or a root `docker-compose.yml`).

### Step-by-Step:
1. Push your code to a **GitHub Repository**.
2. Connect your repo to Railway.
3. For each service (Gateway, Log Processor, etc.), set the following **Environment Variables**:

| Variable | Value |
|----------|-------|
| `SPRING_PROFILES_ACTIVE` | `prod` |
| `POSTGRES_HOST` | `<your-neon-host>` |
| `POSTGRES_USER` | `<your-neon-user>` |
| `POSTGRES_PASSWORD` | `<your-neon-password>` |
| `KAFKA_BOOTSTRAP_SERVERS` | `<your-upstash-server>` |
| `OPENAI_API_KEY` | `sk-xxxxx` |

---

## 3. 🎨 Frontend Deployment (Vercel)

1. Import your GitHub repo into Vercel.
2. Set the following **Environment Variables**:

| Variable | Value |
|----------|-------|
| `NEXT_PUBLIC_API_URL` | `https://your-railway-gateway-url.up.railway.app` |
| `NEXT_PUBLIC_WS_URL` | `wss://your-railway-gateway-url.up.railway.app/ws` |
| `NEXT_PUBLIC_DEFAULT_TENANT_ID` | `00000000-0000-0000-0000-000000000001` |

3. Click **Deploy**.

---

## 🐳 Production Docker Compose (Alternative)

If you have a free VPS (like **Oracle Cloud Free Tier**), use this optimized production compose file:

```yaml
version: '3.8'

services:
  # Using managed DB/Kafka is recommended for free tiers to save VPS memory
  autoroot-gateway:
    image: autoroot/gateway:latest
    ports:
      - "8080:8080"
    environment:
      - SPRING_DATASOURCE_URL=jdbc:postgresql://<db-host>:5432/autoroot
      - KAFKA_BOOTSTRAP_SERVERS=<upstash-kafka-host>:9092
```

---

## 🚨 Critical Checklists

1. **CORS Policy**: Ensure the Gateway's `WebConfig` allows your Vercel URL.
2. **Kafka Security**: In production (Upstash), you **must** use SASL_SSL. Update your `application.yml` to include:
   ```yaml
   spring:
     kafka:
       properties:
         security.protocol: SASL_SSL
         sasl.mechanism: SCRAM-SHA-256
         sasl.jaas.config: "org.apache.kafka.common.security.scram.ScramLoginModule required username='...' password='...';"
   ```
3. **Health Checks**: Railway will automatically use the `/actuator/health` endpoint if enabled.

---

**AutoRoot is now ready for the world!** 🌍
