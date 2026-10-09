-- ===================================================
-- PERSONAL FINANCE MANAGEMENT SYSTEM - DATABASE SCHEMA
-- Database Engine: MySQL 8.0+
-- ===================================================

CREATE DATABASE IF NOT EXISTS personal_finance_db;
USE personal_finance_db;

-- ---------------------------------------------------
-- 1. Table: categories
-- ---------------------------------------------------
CREATE TABLE IF NOT EXISTS categories (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    type VARCHAR(20) NOT NULL,
    description VARCHAR(255),
    CONSTRAINT uk_category_name_type UNIQUE (name, type),
    CONSTRAINT chk_category_type CHECK (type IN ('INCOME', 'EXPENSE'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ---------------------------------------------------
-- 2. Table: income
-- ---------------------------------------------------
CREATE TABLE IF NOT EXISTS income (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    source VARCHAR(150) NOT NULL,
    amount DECIMAL(15,2) NOT NULL,
    income_date DATE NOT NULL,
    category_id BIGINT NOT NULL,
    description VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_income_category FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE RESTRICT,
    CONSTRAINT chk_income_amount CHECK (amount > 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ---------------------------------------------------
-- 3. Table: expenses
-- ---------------------------------------------------
CREATE TABLE IF NOT EXISTS expenses (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(150) NOT NULL,
    amount DECIMAL(15,2) NOT NULL,
    expense_date DATE NOT NULL,
    category_id BIGINT NOT NULL,
    payment_method VARCHAR(30) NOT NULL,
    description VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_expenses_category FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE RESTRICT,
    CONSTRAINT chk_expenses_amount CHECK (amount > 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ---------------------------------------------------
-- 4. Table: budgets
-- ---------------------------------------------------
CREATE TABLE IF NOT EXISTS budgets (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    category_id BIGINT NOT NULL,
    budget_amount DECIMAL(15,2) NOT NULL,
    month INT NOT NULL,
    year INT NOT NULL,
    CONSTRAINT fk_budgets_category FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE RESTRICT,
    CONSTRAINT uk_budget_category_month_year UNIQUE (category_id, month, year),
    CONSTRAINT chk_budget_month CHECK (month BETWEEN 1 AND 12),
    CONSTRAINT chk_budget_amount CHECK (budget_amount > 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ===================================================
-- DEFAULT CATEGORIES SEED DATA (SAFE INSERTION)
-- ===================================================

-- Income Categories
INSERT IGNORE INTO categories (name, type, description) VALUES
('Salary', 'INCOME', 'Monthly job salary or wage compensation'),
('Freelancing', 'INCOME', 'Earnings from contract and freelance projects'),
('Business', 'INCOME', 'Profits from business operations'),
('Investment', 'INCOME', 'Returns, dividends, and interest from investments'),
('Other Income', 'INCOME', 'Gifts, refunds, and miscellaneous income');

-- Expense Categories
INSERT IGNORE INTO categories (name, type, description) VALUES
('Food', 'EXPENSE', 'Groceries, restaurants, and food delivery'),
('Travel', 'EXPENSE', 'Commute, fuel, public transit, and travel expenses'),
('Education', 'EXPENSE', 'Tuition fees, books, courses, and educational material'),
('Shopping', 'EXPENSE', 'Clothing, electronics, and lifestyle shopping'),
('Rent', 'EXPENSE', 'Residential or commercial rent payments'),
('Bills', 'EXPENSE', 'Electricity, water, internet, and utility bills'),
('Healthcare', 'EXPENSE', 'Medical consultations, medicines, and insurance'),
('Entertainment', 'EXPENSE', 'Movies, gaming, subscriptions, and events'),
('Other Expenses', 'EXPENSE', 'Uncategorized and emergency expenses');
