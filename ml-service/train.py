"""
BizSmart Demand Prediction Model Trainer
Generates representative historical sales dataset and trains a
RandomForestRegressor for retail product demand forecasting.
"""

import os
import joblib
import numpy as np
import pandas as pd
from sklearn.ensemble import RandomForestRegressor
from sklearn.model_selection import train_test_split
from sklearn.metrics import mean_absolute_error, r2_score

def generate_synthetic_sales_data(num_samples: int = 5000, random_state: int = 42) -> pd.DataFrame:
    np.random.seed(random_state)
    
    # 4 Product Categories: 0: Electronics, 1: Apparel, 2: Home & Kitchen, 3: Office Supplies
    categories = np.random.choice([0, 1, 2, 3], size=num_samples, p=[0.35, 0.25, 0.25, 0.15])
    
    # Base price depends on category
    category_base_price = {0: 120.0, 1: 45.0, 2: 60.0, 3: 30.0}
    prices = np.array([category_base_price[c] * np.random.uniform(0.6, 2.2) for c in categories])
    
    # Discounts between 0% and 40%
    discounts = np.random.choice([0.0, 0.05, 0.10, 0.15, 0.20, 0.30], size=num_samples, p=[0.4, 0.2, 0.15, 0.1, 0.1, 0.05])
    
    # Seasonal factors (Month 1-12)
    months = np.random.randint(1, 13, size=num_samples)
    
    # Recent sales velocities
    past_7_days_sales = np.random.poisson(lam=np.clip(100.0 / (prices * 0.15 + 1.0) + 15, 5, 80), size=num_samples)
    past_30_days_sales = past_7_days_sales * np.random.uniform(3.8, 4.4, size=num_samples)
    
    # Supplier lead times in days (3 to 21 days)
    lead_time_days = np.random.randint(3, 22, size=num_samples)
    
    # Stock on hand
    stock_quantity = np.random.randint(5, 200, size=num_samples)
    
    # Target: Demand in the next 7 days
    # Real-world dynamics:
    # - Strongly correlated with past_7_days_sales
    # - Boosted by discounts (elasticity ~ 1.5)
    # - Holiday surge in Q4 (months 10, 11, 12)
    # - Lower demand if price is unusually high for category
    seasonal_boost = np.where(np.isin(months, [11, 12]), 1.35, np.where(np.isin(months, [7, 8]), 1.15, 1.0))
    discount_multiplier = 1.0 + (discounts * 1.6)
    
    noise = np.random.normal(0, 2.5, size=num_samples)
    next_7_days_demand = (past_7_days_sales * 1.02 * seasonal_boost * discount_multiplier) + noise
    next_7_days_demand = np.clip(np.round(next_7_days_demand), 0, None).astype(int)
    
    df = pd.DataFrame({
        "category": categories,
        "price": np.round(prices, 2),
        "discount_percent": np.round(discounts * 100, 1),
        "past_7_days_sales": past_7_days_sales,
        "past_30_days_sales": np.round(past_30_days_sales, 1),
        "lead_time_days": lead_time_days,
        "stock_quantity": stock_quantity,
        "month": months,
        "target_demand_7d": next_7_days_demand
    })
    
    return df

def train_and_export_model():
    print("Generating synthetic retail inventory dataset...")
    df = generate_synthetic_sales_data(num_samples=6000)
    
    features = [
        "category",
        "price",
        "discount_percent",
        "past_7_days_sales",
        "past_30_days_sales",
        "lead_time_days",
        "stock_quantity",
        "month"
    ]
    
    X = df[features]
    y = df["target_demand_7d"]
    
    X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.2, random_state=42)
    
    print(f"Training RandomForestRegressor on {len(X_train)} samples...")
    model = RandomForestRegressor(
        n_estimators=100,
        max_depth=12,
        min_samples_split=4,
        random_state=42,
        n_jobs=-1
    )
    model.fit(X_train, y_train)
    
    y_pred = model.predict(X_test)
    mae = mean_absolute_error(y_test, y_pred)
    r2 = r2_score(y_test, y_pred)
    
    print(f"Model Performance:")
    print(f" - Mean Absolute Error (MAE): {mae:.2f} units")
    print(f" - R² Score: {r2:.4f}")
    
    model_payload = {
        "model": model,
        "features": features,
        "metrics": {"mae": round(mae, 2), "r2": round(r2, 4)},
        "version": "1.0.0"
    }
    
    script_dir = os.path.dirname(os.path.abspath(__file__))
    model_path = os.path.join(script_dir, "demand_model.pkl")
    
    joblib.dump(model_payload, model_path)
    print(f"Saved trained model to: {model_path}")

if __name__ == "__main__":
    train_and_export_model()
