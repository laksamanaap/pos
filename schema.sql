-- PostgreSQL Database Schema for POS Application
-- Database: posdb
-- User: postgres (no password)

-- Create database (run as superuser or postgres)
CREATE DATABASE posdb;

-- Connect to posdb and create tables
\c posdb;

-- Items table
CREATE TABLE IF NOT EXISTS items (
    code VARCHAR(64) PRIMARY KEY,
    name TEXT NOT NULL,
    purchase_price DOUBLE PRECISION NOT NULL,
    selling_price DOUBLE PRECISION NOT NULL,
    stock INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Sales transactions table
CREATE TABLE IF NOT EXISTS sales (
    transaction_id VARCHAR(64) PRIMARY KEY,
    transaction_date TIMESTAMP NOT NULL,
    total_amount DOUBLE PRECISION NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Sales details table (line items for each transaction)
CREATE TABLE IF NOT EXISTS sales_details (
    id SERIAL PRIMARY KEY,
    transaction_id VARCHAR(64) NOT NULL,
    item_code VARCHAR(64) NOT NULL,
    quantity INTEGER NOT NULL,
    purchase_price DOUBLE PRECISION NOT NULL,
    selling_price DOUBLE PRECISION NOT NULL,
    sales_date VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (transaction_id) REFERENCES sales(transaction_id) ON DELETE CASCADE,
    FOREIGN KEY (item_code) REFERENCES items(code) ON DELETE RESTRICT
);

-- Create indexes for better query performance
CREATE INDEX idx_sales_date ON sales(transaction_date);
CREATE INDEX idx_sales_details_transaction ON sales_details(transaction_id);
CREATE INDEX idx_sales_details_item ON sales_details(item_code);

-- Sample data (optional)
INSERT INTO items (code, name, purchase_price, selling_price, stock) VALUES
('BRG001', 'Indomie Goreng', 2500, 3500, 100),
('BRG002', 'Aqua 600ml', 3000, 4000, 50),
('BRG003', 'Teh Botol Sosro', 4000, 5500, 75),
('BRG004', 'Biskuit Roma Kelapa', 4500, 6000, 60),
('BRG005', 'Susu Ultra Milk Coklat', 6000, 8500, 40)
ON CONFLICT (code) DO NOTHING;
