package com.personalfinance.service;

import com.personalfinance.dao.CategoryDAO;
import com.personalfinance.dao.ExpenseDAO;
import com.personalfinance.model.Category;
import com.personalfinance.model.CategoryType;
import com.personalfinance.model.Expense;
import com.personalfinance.model.PaymentMethod;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Service class handling business logic for expense management.
 */
public class ExpenseService {

    private final ExpenseDAO expenseDAO;
    private final CategoryDAO categoryDAO;

    public ExpenseService() {
        this.expenseDAO = new ExpenseDAO();
        this.categoryDAO = new CategoryDAO();
    }

    public ExpenseService(ExpenseDAO expenseDAO, CategoryDAO categoryDAO) {
        this.expenseDAO = expenseDAO;
        this.categoryDAO = categoryDAO;
    }

    public Expense addExpense(String title, BigDecimal amount, LocalDate expenseDate, long categoryId, PaymentMethod paymentMethod, String description) throws SQLException {
        validateExpenseInputs(title, amount, expenseDate, categoryId, paymentMethod);

        Expense expense = new Expense(title.trim(), amount, expenseDate, categoryId, paymentMethod, description != null ? description.trim() : "");
        return expenseDAO.save(expense);
    }

    public List<Expense> getAllExpenses() throws SQLException {
        return expenseDAO.findAll();
    }

    public Expense getExpenseById(long id) throws SQLException {
        return expenseDAO.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Expense record not found with ID: " + id));
    }

    public boolean updateExpense(long id, String title, BigDecimal amount, LocalDate expenseDate, long categoryId, PaymentMethod paymentMethod, String description) throws SQLException {
        Expense existing = getExpenseById(id);
        validateExpenseInputs(title, amount, expenseDate, categoryId, paymentMethod);

        existing.setTitle(title.trim());
        existing.setAmount(amount);
        existing.setExpenseDate(expenseDate);
        existing.setCategoryId(categoryId);
        existing.setPaymentMethod(paymentMethod);
        existing.setDescription(description != null ? description.trim() : "");

        return expenseDAO.update(existing);
    }

    public boolean deleteExpense(long id) throws SQLException {
        getExpenseById(id); // Ensure exists
        return expenseDAO.delete(id);
    }

    public List<Expense> getExpensesByCategory(long categoryId) throws SQLException {
        categoryDAO.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Category not found with ID: " + categoryId));
        return expenseDAO.findByCategory(categoryId);
    }

    public List<Expense> getExpensesByDateRange(LocalDate startDate, LocalDate endDate) throws SQLException {
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("Start date and end date must not be null.");
        }
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date cannot be after end date.");
        }
        return expenseDAO.findByDateRange(startDate, endDate);
    }

    public BigDecimal getTotalExpensesByMonth(int month, int year) throws SQLException {
        validateMonthAndYear(month, year);
        return expenseDAO.getTotalExpensesByMonth(month, year);
    }

    public BigDecimal getTotalExpensesByYear(int year) throws SQLException {
        validateYear(year);
        return expenseDAO.getTotalExpensesByYear(year);
    }

    public List<Expense> getHighestExpenses(int limit) throws SQLException {
        if (limit <= 0) {
            throw new IllegalArgumentException("Limit must be greater than zero.");
        }
        return expenseDAO.getHighestExpenses(limit);
    }

    public Map<String, BigDecimal> getCategoryWiseExpenses() throws SQLException {
        return expenseDAO.getCategoryWiseExpenses();
    }

    private void validateExpenseInputs(String title, BigDecimal amount, LocalDate expenseDate, long categoryId, PaymentMethod paymentMethod) throws SQLException {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Expense title cannot be empty.");
        }
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Expense amount must be greater than zero.");
        }
        if (expenseDate == null) {
            throw new IllegalArgumentException("Expense date cannot be null.");
        }
        if (paymentMethod == null) {
            throw new IllegalArgumentException("Payment method must be specified.");
        }

        Category category = categoryDAO.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Category not found with ID: " + categoryId));

        if (category.getType() != CategoryType.EXPENSE) {
            throw new IllegalArgumentException("Category '" + category.getName() + "' (ID: " + categoryId + ") is of type " + category.getType() + ". Only EXPENSE categories can be assigned to expense records.");
        }
    }

    private void validateMonthAndYear(int month, int year) {
        if (month < 1 || month > 12) {
            throw new IllegalArgumentException("Month must be between 1 and 12.");
        }
        validateYear(year);
    }

    private void validateYear(int year) {
        if (year < 1900 || year > 2100) {
            throw new IllegalArgumentException("Year must be a valid 4-digit year (1900-2100).");
        }
    }
}
