# BizSmart REST API Documentation

Base URL: `http://localhost:8080/api`

---

## 1. Authentication Endpoints

### 1.1 Sign In
- **URL**: `POST /api/auth/signin`
- **Request Body**:
```json
{
  "username": "admin",
  "password": "password123"
}
```
- **Response `200 OK`**:
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer",
  "id": 1,
  "username": "admin",
  "email": "admin@bizsmart.io",
  "fullName": "System Administrator",
  "roles": ["ROLE_ADMIN", "ROLE_MANAGER"]
}
```

### 1.2 Sign Up
- **URL**: `POST /api/auth/signup`
- **Request Body**:
```json
{
  "username": "johndoe",
  "email": "john@example.com",
  "password": "password123",
  "fullName": "John Doe",
  "roles": ["manager"]
}
```

---

## 2. Product & Inventory Endpoints

### 2.1 List All Products
- **URL**: `GET /api/products`
- **Query Params**: `?search=headphones` (optional)
- **Response `200 OK`**:
```json
[
  {
    "id": 1,
    "sku": "ELEC-PRO-001",
    "name": "Ultra Wireless ANC Headphones",
    "price": 149.99,
    "costPrice": 85.00,
    "stockQuantity": 42,
    "safetyStock": 15,
    "reorderQuantity": 60,
    "category": { "id": 1, "name": "Electronics" }
  }
]
```

### 2.2 Low Stock Products Alert
- **URL**: `GET /api/products/low-stock`
- **Description**: Returns all products where `stockQuantity <= safetyStock`.

### 2.3 Quick Stock Adjustment
- **URL**: `PATCH /api/products/{id}/stock?delta=10`
- **Security**: Requires `ROLE_ADMIN`, `ROLE_MANAGER`, or `ROLE_STAFF`.

---

## 3. Order Management Endpoints

### 3.1 Create Order
- **URL**: `POST /api/orders`
- **Request Body**:
```json
{
  "customerId": 1,
  "items": [
    { "productId": 1, "quantity": 2 },
    { "productId": 3, "quantity": 1 }
  ]
}
```
- **Behavior**: Automatically verifies inventory sufficiency, deducts stock, calculates line totals and grand total, and returns created order.

### 3.2 Update Order Status
- **URL**: `PATCH /api/orders/{id}/status?status=SHIPPED`
- **Status Options**: `PENDING`, `PROCESSING`, `SHIPPED`, `DELIVERED`, `CANCELLED`

---

## 4. Analytics & AI Forecasting Endpoints

### 4.1 Dashboard Overview Metrics
- **URL**: `GET /api/analytics/dashboard`
- **Response**: Aggregated revenue, active product count, low-stock count, customer count, recent orders list, and demand trends.

### 4.2 Run AI Demand Forecast
- **URL**: `POST /api/analytics/forecast/{productId}?discountPercent=10.0&leadTimeDays=7`
- **Response `200 OK`**:
```json
{
  "product_id": 2,
  "sku": "ELEC-PRO-002",
  "forecast_period_days": 7,
  "predicted_demand": 34,
  "current_stock": 12,
  "net_stock_after_period": -22,
  "reorder_recommended": true,
  "suggested_reorder_qty": 48,
  "confidence_score": 0.91,
  "risk_level": "CRITICAL",
  "daily_breakdown": [
    { "day": 1, "day_name": "Mon", "expected_units": 4 },
    { "day": 2, "day_name": "Tue", "expected_units": 5 },
    { "day": 3, "day_name": "Wed", "expected_units": 5 },
    { "day": 4, "day_name": "Thu", "expected_units": 4 },
    { "day": 5, "day_name": "Fri", "expected_units": 7 },
    { "day": 6, "day_name": "Sat", "expected_units": 6 },
    { "day": 7, "day_name": "Sun", "expected_units": 3 }
  ],
  "created_at": "2026-09-12T13:50:00Z"
}
```

---

## 5. Python ML Service Microservice Endpoints

Direct ML Service running on `http://localhost:8000`:

- `GET /health`: Health status, model version, features, and MAE/R² metrics.
- `POST /predict`: Direct Scikit-Learn inference given input features.
- `POST /bulk-predict`: Batch inference for array of products.
- Interactive OpenAPI Swagger UI: `http://localhost:8000/docs`
