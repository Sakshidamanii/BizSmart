-- =============================================================================
-- BizSmart Database Schema DDL & Seed Data (SME Indian Retail Edition)
-- Compatible with H2 (PostgreSQL/MySQL modes), PostgreSQL, and MySQL
-- =============================================================================

DROP TABLE IF EXISTS audit_logs CASCADE;
DROP TABLE IF EXISTS demand_forecasts CASCADE;
DROP TABLE IF EXISTS bill_items CASCADE;
DROP TABLE IF EXISTS bills CASCADE;
DROP TABLE IF EXISTS purchase_order_items CASCADE;
DROP TABLE IF EXISTS purchase_orders CASCADE;
DROP TABLE IF EXISTS expenses CASCADE;
DROP TABLE IF EXISTS order_items CASCADE;
DROP TABLE IF EXISTS orders CASCADE;
DROP TABLE IF EXISTS products CASCADE;
DROP TABLE IF EXISTS suppliers CASCADE;
DROP TABLE IF EXISTS categories CASCADE;
DROP TABLE IF EXISTS customers CASCADE;
DROP TABLE IF EXISTS user_roles CASCADE;
DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS roles CASCADE;

-- -----------------------------------------------------------------------------
-- 1. Roles Table (Matching 4 Specified Roles)
-- -----------------------------------------------------------------------------
CREATE TABLE roles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

INSERT INTO roles (id, name) VALUES (1, 'ROLE_BUSINESS_OWNER');
INSERT INTO roles (id, name) VALUES (2, 'ROLE_EMPLOYEE');
INSERT INTO roles (id, name) VALUES (3, 'ROLE_SUPPLIER');
INSERT INTO roles (id, name) VALUES (4, 'ROLE_PLATFORM_ADMIN');

-- -----------------------------------------------------------------------------
-- 2. Users Table
-- -----------------------------------------------------------------------------
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(100),
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO users (id, username, email, password, full_name, active) VALUES
(1, 'owner', 'owner@bizsmart.in', '$2a$10$3zGqMsmB4dE7yX1oO8HzeOXK95oO6V3lq80sWzYqYFw0U12B6fTxe', 'Rajesh Sharma (Owner)', true),
(2, 'employee', 'cashier@bizsmart.in', '$2a$10$3zGqMsmB4dE7yX1oO8HzeOXK95oO6V3lq80sWzYqYFw0U12B6fTxe', 'Amit Verma (Cashier/Staff)', true),
(3, 'supplier', 'supplier@itc-distributors.in', '$2a$10$3zGqMsmB4dE7yX1oO8HzeOXK95oO6V3lq80sWzYqYFw0U12B6fTxe', 'Sunil Kumar (ITC FMCG Distributor)', true),
(4, 'admin', 'admin@bizsmart.in', '$2a$10$3zGqMsmB4dE7yX1oO8HzeOXK95oO6V3lq80sWzYqYFw0U12B6fTxe', 'Platform Super Admin', true);

INSERT INTO user_roles (user_id, role_id) VALUES (1, 1);
INSERT INTO user_roles (user_id, role_id) VALUES (2, 2);
INSERT INTO user_roles (user_id, role_id) VALUES (3, 3);
INSERT INTO user_roles (user_id, role_id) VALUES (4, 4);

-- -----------------------------------------------------------------------------
-- 3. Suppliers Table (Solves "Supplier payment issues")
-- -----------------------------------------------------------------------------
CREATE TABLE suppliers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    contact_person VARCHAR(100),
    phone VARCHAR(20),
    email VARCHAR(100),
    address VARCHAR(255),
    pending_dues DECIMAL(12, 2) DEFAULT 0.00,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO suppliers (id, name, contact_person, phone, email, address, pending_dues) VALUES
(1, 'ITC Consumer Goods Distribution', 'Sunil Kumar', '+91-98200-11223', 'itc.delhi@distributor.in', 'Warehouse 14, Okhla Phase III, New Delhi', 45000.00),
(2, 'Tata Consumer Products Hub', 'Ramesh Patel', '+91-98211-44556', 'tata.supply@tataconsumer.in', 'Sector 18, Gurugram', 18500.00),
(3, 'Amul Dairy Federation Depot', 'Dinesh Rawat', '+91-98100-99887', 'amul.depot@amul.coop', 'Patparganj Industrial Area, Delhi', 12000.00),
(4, 'Adani Wilmar Edible Oils Supply', 'Vikas Gupta', '+91-98999-33221', 'sales@adaniwilmar.in', 'Transport Nagar, Delhi', 28000.00);

-- -----------------------------------------------------------------------------
-- 4. Categories Table
-- -----------------------------------------------------------------------------
CREATE TABLE categories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description TEXT
);

INSERT INTO categories (id, name, description) VALUES
(1, 'Staples & Grains', 'Atta, rice, pulses, and whole grains'),
(2, 'Edible Oils & Ghee', 'Cooking oils, mustard oil, refined oil, and pure ghee'),
(3, 'Dairy & Breakfast', 'Milk, butter, cheese, bread, and breakfast items'),
(4, 'FMCG & Packaged Foods', 'Biscuits, noodles, spices, and snacks'),
(5, 'Personal Care & Hygiene', 'Soaps, shampoos, detergents, and household cleaners');

-- -----------------------------------------------------------------------------
-- 5. Products Table (With Attributes from Spec Section 5)
-- -----------------------------------------------------------------------------
CREATE TABLE products (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    sku VARCHAR(60) NOT NULL UNIQUE,
    name VARCHAR(150) NOT NULL,
    category_id BIGINT,
    supplier_id BIGINT,
    purchase_price DECIMAL(10, 2) NOT NULL,
    selling_price DECIMAL(10, 2) NOT NULL,
    quantity INT NOT NULL DEFAULT 0,
    min_stock INT NOT NULL DEFAULT 10,
    reorder_quantity INT NOT NULL DEFAULT 50,
    expiry_date DATE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_prod_cat FOREIGN KEY (category_id) REFERENCES categories (id),
    CONSTRAINT fk_prod_supp FOREIGN KEY (supplier_id) REFERENCES suppliers (id)
);

-- Note: Aashirvaad Atta 5kg matches exact example in Section 5 (Purchase ₹240, Selling ₹280, Quantity 8, Min Stock 10 -> ⚠️ LOW STOCK)
INSERT INTO products (id, sku, name, category_id, supplier_id, purchase_price, selling_price, quantity, min_stock, reorder_quantity, expiry_date) VALUES
(1, 'GROC-ATT-001', 'Aashirvaad Atta 5kg', 1, 1, 240.00, 280.00, 8, 10, 50, DATEADD('MONTH', 6, CURRENT_DATE)), -- ⚠️ LOW STOCK
(2, 'GROC-OIL-002', 'Fortune Sunlite Sunflower Oil 1L', 2, 4, 135.00, 160.00, 14, 15, 60, DATEADD('MONTH', 9, CURRENT_DATE)), -- ⚠️ LOW STOCK
(3, 'GROC-SLT-003', 'Tata Salt Vacuum Evaporated 1kg', 1, 2, 22.00, 28.00, 45, 20, 80, DATEADD('MONTH', 18, CURRENT_DATE)),
(4, 'DAIR-BUT-004', 'Amul Pasteurised Butter 500g', 3, 3, 245.00, 275.00, 6, 12, 40, DATEADD('DAY', 45, CURRENT_DATE)), -- ⚠️ LOW STOCK
(5, 'DAIR-MLK-005', 'Amul Taaza Homogenised Toned Milk 1L', 3, 3, 62.00, 72.00, 32, 15, 50, DATEADD('MONTH', 3, CURRENT_DATE)),
(6, 'FMCG-MAG-006', 'Maggi 2-Minute Masala Noodles 70g Pack of 4', 4, 1, 52.00, 60.00, 60, 25, 100, DATEADD('MONTH', 8, CURRENT_DATE)),
(7, 'PERS-SOAP-007', 'Dettol Original Germ Protection Soap 125g (Pack of 4)', 5, 1, 155.00, 185.00, 18, 10, 40, DATEADD('MONTH', 24, CURRENT_DATE)),
(8, 'GROC-RC-008', 'India Gate Basmati Rice Feast Rozzana 5kg', 1, 1, 380.00, 440.00, 5, 10, 30, DATEADD('MONTH', 12, CURRENT_DATE)); -- ⚠️ LOW STOCK

-- -----------------------------------------------------------------------------
-- 6. Customers Table (Solves "Unpaid customer balances / Udhaar")
-- -----------------------------------------------------------------------------
CREATE TABLE customers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(120) NOT NULL,
    email VARCHAR(120) UNIQUE,
    phone VARCHAR(20) NOT NULL,
    address VARCHAR(255),
    city VARCHAR(80) DEFAULT 'Delhi NCR',
    outstanding_balance DECIMAL(12, 2) DEFAULT 0.00, -- Credit / Khata balance
    credit_limit DECIMAL(12, 2) DEFAULT 5000.00,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO customers (id, name, email, phone, address, city, outstanding_balance, credit_limit) VALUES
(1, 'Rakesh Gupta (Shop Regular)', 'rakesh.gupta@gmail.com', '+91-98111-22334', 'Flat 302, Palm Heights, Sector 12', 'Dwarka, New Delhi', 1850.00, 5000.00),
(2, 'Priya Sundaram', 'priya.s@yahoo.com', '+91-98777-66554', 'B-14, Mayur Vihar Phase 1', 'East Delhi', 0.00, 3000.00),
(3, 'Anand Mehra (Catering)', 'anand.mehra@caterers.in', '+91-98990-11223', 'Shop 4, Main Market, Lajpat Nagar', 'South Delhi', 4200.00, 15000.00),
(4, 'Sunita Devi', 'sunita.devi@outlook.com', '+91-98666-33445', 'House 45, Gali 3, Krishna Nagar', 'Delhi', 750.00, 2500.00);

-- -----------------------------------------------------------------------------
-- 7. Bills / Invoices Table (Solves "Paper bills" problem)
-- -----------------------------------------------------------------------------
CREATE TABLE bills (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    bill_number VARCHAR(50) NOT NULL UNIQUE,
    customer_id BIGINT,
    subtotal DECIMAL(12, 2) NOT NULL,
    tax_amount DECIMAL(10, 2) DEFAULT 0.00,
    discount_amount DECIMAL(10, 2) DEFAULT 0.00,
    total_amount DECIMAL(12, 2) NOT NULL,
    payment_mode VARCHAR(20) NOT NULL, -- CASH, UPI, CREDIT (UDHAAR)
    payment_status VARCHAR(20) NOT NULL DEFAULT 'PAID', -- PAID, PENDING
    biller_name VARCHAR(100) DEFAULT 'Amit Verma (Cashier)',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_bills_cust FOREIGN KEY (customer_id) REFERENCES customers (id)
);

CREATE TABLE bill_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    bill_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity INT NOT NULL,
    selling_price DECIMAL(10, 2) NOT NULL,
    subtotal DECIMAL(12, 2) NOT NULL,
    CONSTRAINT fk_bill_items_bill FOREIGN KEY (bill_id) REFERENCES bills (id) ON DELETE CASCADE,
    CONSTRAINT fk_bill_items_prod FOREIGN KEY (product_id) REFERENCES products (id)
);

INSERT INTO bills (id, bill_number, customer_id, subtotal, tax_amount, discount_amount, total_amount, payment_mode, payment_status, created_at) VALUES
(1, 'BILL-2026-001', 1, 840.00, 0.00, 40.00, 800.00, 'UPI', 'PAID', CURRENT_TIMESTAMP),
(2, 'BILL-2026-002', 3, 2200.00, 0.00, 100.00, 2100.00, 'CREDIT', 'PENDING', CURRENT_TIMESTAMP),
(3, 'BILL-2026-003', 2, 440.00, 0.00, 0.00, 440.00, 'CASH', 'PAID', CURRENT_TIMESTAMP);

INSERT INTO bill_items (bill_id, product_id, quantity, selling_price, subtotal) VALUES
(1, 1, 2, 280.00, 560.00), -- 2 Aashirvaad Atta
(1, 2, 1, 160.00, 160.00), -- 1 Fortune Oil
(2, 8, 4, 440.00, 1760.00), -- 4 Basmati Rice
(2, 4, 2, 275.00, 550.00), -- 2 Amul Butter
(3, 1, 1, 280.00, 280.00),
(3, 2, 1, 160.00, 160.00);

-- -----------------------------------------------------------------------------
-- 8. Expenses Table (Solves "Manual expense calculations")
-- -----------------------------------------------------------------------------
CREATE TABLE expenses (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(150) NOT NULL,
    category VARCHAR(50) NOT NULL, -- RENT, ELECTRICITY, SALARY, LOGISTICS, MISC
    amount DECIMAL(12, 2) NOT NULL,
    expense_date DATE NOT NULL,
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO expenses (title, category, amount, expense_date, notes) VALUES
('Shop Floor Monthly Rent', 'RENT', 85000.00, CURRENT_DATE, 'Main market commercial space'),
('Electricity & Commercial Power Bill', 'ELECTRICITY', 24500.00, CURRENT_DATE, 'Cooling, deep freezers, and lighting'),
('Staff Salaries (3 Employees)', 'SALARY', 65000.00, CURRENT_DATE, 'Cashier and stock helpers'),
('Stock Delivery & Transport Logistics', 'LOGISTICS', 18000.00, CURRENT_DATE, 'Wholesale transport charges'),
('Packaging Materials & Carry Bags', 'MISC', 7500.00, CURRENT_DATE, 'Paper bags and thermal receipt rolls'),
('Store Maintenance & Pest Control', 'MISC', 10000.00, CURRENT_DATE, 'Quarterly sanitation');

-- -----------------------------------------------------------------------------
-- 9. Purchase Orders Table (Supplier Portal Integration)
-- -----------------------------------------------------------------------------
CREATE TABLE purchase_orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    po_number VARCHAR(50) NOT NULL UNIQUE,
    supplier_id BIGINT NOT NULL,
    total_amount DECIMAL(12, 2) NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'PENDING', -- PENDING, CONFIRMED, SHIPPED, DELIVERED
    payment_status VARCHAR(30) NOT NULL DEFAULT 'UNPAID', -- PAID, PARTIAL, UNPAID
    expected_delivery_date DATE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_po_supplier FOREIGN KEY (supplier_id) REFERENCES suppliers (id)
);

INSERT INTO purchase_orders (id, po_number, supplier_id, total_amount, status, payment_status, expected_delivery_date) VALUES
(1, 'PO-2026-ITC-01', 1, 28000.00, 'CONFIRMED', 'PARTIAL', DATEADD('DAY', 3, CURRENT_DATE)),
(2, 'PO-2026-AMUL-02', 3, 14500.00, 'PENDING', 'UNPAID', DATEADD('DAY', 2, CURRENT_DATE)),
(3, 'PO-2026-TATA-03', 2, 18500.00, 'SHIPPED', 'PAID', DATEADD('DAY', 1, CURRENT_DATE));

-- -----------------------------------------------------------------------------
-- 10. AI Demand Forecasts Table (Matching Section 6)
-- -----------------------------------------------------------------------------
CREATE TABLE demand_forecasts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id BIGINT NOT NULL,
    forecast_month VARCHAR(20) NOT NULL,
    past_sales_json TEXT, -- Historical monthly data (Jan, Feb, Mar, Apr, May)
    predicted_units INT NOT NULL,
    confidence_score DECIMAL(5, 4),
    reorder_recommended BOOLEAN DEFAULT FALSE,
    recommendation_text TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_df_prod FOREIGN KEY (product_id) REFERENCES products (id)
);

INSERT INTO demand_forecasts (product_id, forecast_month, past_sales_json, predicted_units, confidence_score, reorder_recommended, recommendation_text) VALUES
(1, 'June 2026', '{"Jan":100,"Feb":120,"Mar":135,"Apr":150,"May":165}', 182, 0.9450, true, 'Urgent Reorder Required: Current stock is 8, Minimum stock is 10. Predicted June consumption is 182 units. Order 180 units from ITC Distribution immediately.');
