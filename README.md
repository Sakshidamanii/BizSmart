# BizSmart 🚀

> **Intelligent Business & Inventory Management Platform with Demand Prediction Engine**

[![Deploy with Vercel](https://vercel.com/button)]
**Live Demo on Vercel**: [https://biz-smart-store.vercel.app/(https://biz-smart-store.vercel.app/)

BizSmart is an end-to-end full-stack retail and inventory management application designed to eliminate stockouts and optimize purchasing decisions through real-time Machine Learning demand forecasting.

---

## 📂 Project Structure

```
BizSmart/
│── frontend/              # Modern React + Vite + Tailwind CSS UI
│── backend/               # Spring Boot 3 enterprise backend
│   ├── src/main/java/com/bizsmart/
│   │   ├── controllers/   # REST endpoints
│   │   ├── services/      # Business logic & ML client integration
│   │   ├── models/        # Entities (Product, Customer, Order, DemandForecast, etc.)
│   │   ├── repositories/  # JPA Repositories
│   │   └── security/      # JWT + Role-based access control
│── ml-service/            # Python demand prediction microservice
│   ├── app.py             # FastAPI entry (:8000)
│   ├── train.py           # Model training pipeline
│   ├── demand_model.pkl   # Trained Scikit-Learn RandomForest model
│   └── requirements.txt   # Dependencies
│── database/              # SQL scripts
│   └── schema.sql         # Relational database schema with initial seed data
│── docs/                  # System documentation
│   ├── ARCHITECTURE.md    # Architecture & Data Flow
│   ├── API.md             # REST API specifications
│   └── SETUP_GUIDE.md     # Installation & Execution Guide
```

---

## ✨ Features

- **Automated AI Demand Forecasting**: Scikit-Learn `RandomForestRegressor` predicts product consumption over a 7-day rolling window considering historical sales, discounts, lead times, and seasonal factors.
- **Stockout & Reorder Alerts**: Real-time identification of products breaching safety thresholds with automated purchase order quantity suggestions.
- **Spring Boot 3 REST API**: Robust backend with Spring Security, stateless JWT authentication, and fine-grained role-based access control (`ROLE_ADMIN`, `ROLE_MANAGER`, `ROLE_STAFF`).
- **Interactive React Dashboard**: Modern UI built with Vite, Tailwind CSS, Lucide icons, responsive weekly charts, quick stock adjustment toggles, and simulation sliders.
- **Transactional Order Processing**: Deducts inventory automatically on order creation and replenishes on order cancellation.
- **High Availability & Fault Tolerance**: If the ML service is temporarily unreachable, the backend provides an intelligent heuristic fallback without throwing exceptions.

---

## 🚀 Quick Start

### 1. Python ML Service
```bash
cd ml-service
pip install -r requirements.txt
python -m uvicorn app:app --port 8000
```
API running on `http://localhost:8000` (Docs: `http://localhost:8000/docs`).

### 2. Spring Boot Backend
```bash
cd backend
mvn spring-boot:run
```
Backend running on `http://localhost:8080` (H2 Console: `http://localhost:8080/h2-console`).

### 3. Frontend Dashboard
```bash
cd frontend
npm install
npm run dev
```
Dashboard available at `http://localhost:5173`.

---

## 📖 Documentation
- [System Architecture](docs/ARCHITECTURE.md)
- [REST API Reference](docs/API.md)
- [Setup & Deployment Guide](docs/SETUP_GUIDE.md)
