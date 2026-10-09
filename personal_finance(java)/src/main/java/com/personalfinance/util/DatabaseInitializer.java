package com.personalfinance.util;

import com.personalfinance.config.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Utility responsible for auto-initializing database, tables, and default seed data on startup.
 */
public class DatabaseInitializer {

    public static boolean initializeDatabase() {
        System.out.println("[INFO] Initializing Personal Finance Management System database...");
        
        // Step 1: Ensure Database exists
        try (Connection serverConn = DatabaseConnection.getServerConnection();
             Statement stmt = serverConn.createStatement()) {
            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS personal_finance_db");
        } catch (SQLException e) {
            System.err.println("[ERROR] Failed to verify/create database 'personal_finance_db': " + e.getMessage());
            return false;
        }

        // Step 2: Ensure Tables exist and seed default categories
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            // Create Categories table
            String createCategoriesTable = """
                CREATE TABLE IF NOT EXISTS categories (
                    id BIGINT PRIMARY KEY AUTO_INCREMENT,
                    name VARCHAR(100) NOT NULL,
                    type VARCHAR(20) NOT NULL,
                    description VARCHAR(255),
                    CONSTRAINT uk_category_name_type UNIQUE (name, type),
                    CONSTRAINT chk_category_type CHECK (type IN ('INCOME', 'EXPENSE'))
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
                """;
            stmt.executeUpdate(createCategoriesTable);

            // Create Income table
            String createIncomeTable = """
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
                """;
            stmt.executeUpdate(createIncomeTable);

            // Create Expenses table
            String createExpensesTable = """
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
                """;
            stmt.executeUpdate(createExpensesTable);

            // Create Budgets table
            String createBudgetsTable = """
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
                """;
            stmt.executeUpdate(createBudgetsTable);

            // Seed default categories safely
            seedDefaultCategories(conn);

            System.out.println("[INFO] Database schema and seed data initialized successfully.");
            return true;
        } catch (SQLException e) {
            System.err.println("[ERROR] Database initialization failed: " + e.getMessage());
            return false;
        }
    }

    private static void seedDefaultCategories(Connection conn) throws SQLException {
        String checkQuery = "SELECT COUNT(*) FROM categories WHERE name = ? AND type = ?";
        String insertQuery = "INSERT INTO categories (name, type, description) VALUES (?, ?, ?)";

        String[][] defaultCategories = {
            // Income
            {"Salary", "INCOME", "Monthly job salary or wage compensation"},
            {"Freelancing", "INCOME", "Earnings from contract and freelance projects"},
            {"Business", "INCOME", "Profits from business operations"},
            {"Investment", "INCOME", "Returns, dividends, and interest from investments"},
            {"Other Income", "INCOME", "Gifts, refunds, and miscellaneous income"},
            // Expenses
            {"Food", "EXPENSE", "Groceries, restaurants, and food delivery"},
            {"Travel", "EXPENSE", "Commute, fuel, public transit, and travel expenses"},
            {"Education", "EXPENSE", "Tuition fees, books, courses, and educational material"},
            {"Shopping", "EXPENSE", "Clothing, electronics, and lifestyle shopping"},
            {"Rent", "EXPENSE", "Residential or commercial rent payments"},
            {"Bills", "EXPENSE", "Electricity, water, internet, and utility bills"},
            {"Healthcare", "EXPENSE", "Medical consultations, medicines, and insurance"},
            {"Entertainment", "EXPENSE", "Movies, gaming, subscriptions, and events"},
            {"Other Expenses", "EXPENSE", "Uncategorized and emergency expenses"}
        };

        try (PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
             PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {

            for (String[] cat : defaultCategories) {
                checkStmt.setString(1, cat[0]);
                checkStmt.setString(2, cat[1]);
                try (ResultSet rs = checkStmt.executeQuery()) {
                    if (rs.next() && rs.getInt(1) == 0) {
                        insertStmt.setString(1, cat[0]);
                        insertStmt.setString(2, cat[1]);
                        insertStmt.setString(3, cat[2]);
                        insertStmt.executeUpdate();
                    }
                }
            }
        }
    }
}
