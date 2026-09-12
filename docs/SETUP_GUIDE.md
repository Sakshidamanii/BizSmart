# BizSmart Setup & Running Guide

Follow these steps to run each microservice locally.

---

## Prerequisites
- **Java**: JDK 17 or higher (`java -version`)
- **Python**: Python 3.10 or higher (`python --version`)
- **Node.js**: Node 18 or higher with `npm` (`node -v`, `npm -v`)

---

## 1. Machine Learning Microservice (`ml-service/`)

1. Open a terminal in `BizSmart/ml-service`:
   ```bash
   cd BizSmart/ml-service
   ```
2. Install Python dependencies:
   ```bash
   pip install -r requirements.txt
   ```
3. (Optional) Re-train model:
   ```bash
   python train.py
   ```
   *(Note: The trained model `demand_model.pkl` is already generated and included!)*
4. Start the FastAPI microservice:
   ```bash
   python -m uvicorn app:app --port 8000 --reload
   ```
   - API runs at: `http://localhost:8000`
   - Interactive Swagger docs: `http://localhost:8000/docs`

---

## 2. Spring Boot Backend (`backend/`)

1. Open a terminal in `BizSmart/backend`:
   ```bash
   cd BizSmart/backend
   ```
2. Run using Maven (or Maven wrapper):
   ```bash
   mvn spring-boot:run
   ```
3. The backend will start on `http://localhost:8080`.
   - Seed data is loaded automatically on startup.
   - H2 Console available at: `http://localhost:8080/h2-console`
     - JDBC URL: `jdbc:h2:mem:bizsmartdb`
     - User: `sa`
     - Password: *(blank)*

---

## 3. Frontend Dashboard (`frontend/`)

1. Open a terminal in `BizSmart/frontend`:
   ```bash
   cd BizSmart/frontend
   ```
2. Install dependencies:
   ```bash
   npm install
   ```
3. Start Vite development server:
   ```bash
   npm run dev
   ```
4. Access the dashboard in your browser at:
   ```
   http://localhost:5173
   ```

---

## Default Credentials
| Role | Username | Password | Permissions |
|---|---|---|---|
| Admin | `admin` | `password123` | Full access (Product CRUD, Security, Forecasts, Orders) |
| Manager | `manager` | `password123` | Inventory, Stock Adjustments, Forecasts, Orders |
| Staff | `staff` | `password123` | Stock Updates, Order Processing |
