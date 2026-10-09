package com.personalfinance.integration;

import com.personalfinance.config.DatabaseConnection;
import com.personalfinance.dao.BudgetDAO;
import com.personalfinance.dao.CategoryDAO;
import com.personalfinance.dao.ExpenseDAO;
import com.personalfinance.dao.IncomeDAO;
import com.personalfinance.model.Budget;
import com.personalfinance.model.Category;
import com.personalfinance.model.CategoryType;
import com.personalfinance.model.Expense;
import com.personalfinance.model.Income;
import com.personalfinance.model.PaymentMethod;

import com.personalfinance.util.DatabaseInitializer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

class MySQLDatabaseIntegrationTest {

    private static CategoryDAO categoryDAO;
    private static IncomeDAO incomeDAO;
    private static ExpenseDAO expenseDAO;
    private static BudgetDAO budgetDAO;

    @BeforeAll
    static void init() {
        assumeTrue(DatabaseConnection.testConnection(), "Skipping MySQL integration test: MySQL connection unavailable");

        assertTrue(DatabaseInitializer.initializeDatabase(), "Database initialization must succeed");

        categoryDAO = new CategoryDAO();
        incomeDAO = new IncomeDAO();
        expenseDAO = new ExpenseDAO();
        budgetDAO = new BudgetDAO();
    }

    @Test
    @DisplayName("Verify real JDBC connection to MySQL")
    void testConnection() throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            assertNotNull(conn);
            assertFalse(conn.isClosed());
        }
    }

    @Test
    @DisplayName("Verify seed categories exist in MySQL database")
    void testSeedCategoriesExist() throws SQLException {
        List<Category> categories = categoryDAO.findAll();
        assertFalse(categories.isEmpty(), "Categories should contain seed data");

        Optional<Category> salaryCat = categoryDAO.findByNameAndType("Salary", CategoryType.INCOME);
        assertTrue(salaryCat.isPresent(), "Salary category should exist");

        Optional<Category> foodCat = categoryDAO.findByNameAndType("Food", CategoryType.EXPENSE);
        assertTrue(foodCat.isPresent(), "Food category should exist");
    }

    @Test
    @DisplayName("Verify complete Income, Expense, and Budget lifecycle in MySQL")
    void testFullFinancialLifecycle() throws SQLException {
        // 1. Fetch Income Category
        Category salaryCat = categoryDAO.findByNameAndType("Salary", CategoryType.INCOME).orElseThrow();
        Category foodCat = categoryDAO.findByNameAndType("Food", CategoryType.EXPENSE).orElseThrow();

        // 2. Insert Income
        Income inc = new Income("Software Engineer Job", new BigDecimal("7500.00"), LocalDate.of(2026, 3, 1), salaryCat.getId(), "March salary");
        Income savedInc = incomeDAO.save(inc);
        assertNotNull(savedInc.getId());

        // 3. Insert Expense
        Expense exp = new Expense("Supermarket Groceries", new BigDecimal("320.50"), LocalDate.of(2026, 3, 5), foodCat.getId(), PaymentMethod.UPI, "March groceries");
        Expense savedExp = expenseDAO.save(exp);
        assertNotNull(savedExp.getId());

        // 4. Set Budget
        Budget b = new Budget(foodCat.getId(), new BigDecimal("600.00"), 3, 2026);
        Optional<Budget> existingB = budgetDAO.findByCategoryMonthYear(foodCat.getId(), 3, 2026);
        if (existingB.isPresent()) {
            budgetDAO.delete(existingB.get().getId());
        }
        Budget savedB = budgetDAO.save(b);
        assertNotNull(savedB.getId());

        // 5. Verify calculations from DB
        BigDecimal actualSpent = budgetDAO.getActualExpenseForCategory(foodCat.getId(), 3, 2026);
        assertTrue(actualSpent.compareTo(new BigDecimal("320.50")) >= 0, "Actual spent must include inserted expense");

        BigDecimal totalIncome = incomeDAO.getTotalIncomeByMonth(3, 2026);
        assertTrue(totalIncome.compareTo(new BigDecimal("7500.00")) >= 0);

        BigDecimal totalExpense = expenseDAO.getTotalExpensesByMonth(3, 2026);
        assertTrue(totalExpense.compareTo(new BigDecimal("320.50")) >= 0);
    }
}
