package com.personalfinance.service;

import com.personalfinance.dao.BudgetDAO;
import com.personalfinance.dao.CategoryDAO;
import com.personalfinance.model.Budget;
import com.personalfinance.model.BudgetSummary;
import com.personalfinance.model.Category;
import com.personalfinance.model.CategoryType;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Service class handling business logic for budget management.
 */
public class BudgetService {

    private final BudgetDAO budgetDAO;
    private final CategoryDAO categoryDAO;

    public BudgetService() {
        this.budgetDAO = new BudgetDAO();
        this.categoryDAO = new CategoryDAO();
    }

    public BudgetService(BudgetDAO budgetDAO, CategoryDAO categoryDAO) {
        this.budgetDAO = budgetDAO;
        this.categoryDAO = categoryDAO;
    }

    public Budget setBudget(long categoryId, BigDecimal budgetAmount, int month, int year) throws SQLException {
        validateBudgetInputs(categoryId, budgetAmount, month, year);

        Optional<Budget> existing = budgetDAO.findByCategoryMonthYear(categoryId, month, year);
        if (existing.isPresent()) {
            throw new IllegalArgumentException("A budget has already been set for this expense category for " + String.format("%02d/%d", month, year) + ". Use update option instead.");
        }

        Budget budget = new Budget(categoryId, budgetAmount, month, year);
        return budgetDAO.save(budget);
    }

    public List<Budget> getAllBudgets() throws SQLException {
        return budgetDAO.findAll();
    }

    public Budget getBudgetById(long id) throws SQLException {
        return budgetDAO.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Budget record not found with ID: " + id));
    }

    public boolean updateBudget(long id, long categoryId, BigDecimal budgetAmount, int month, int year) throws SQLException {
        Budget existing = getBudgetById(id);
        validateBudgetInputs(categoryId, budgetAmount, month, year);

        // If category, month, or year changed, check for duplicate collision
        if (existing.getCategoryId() != categoryId || existing.getMonth() != month || existing.getYear() != year) {
            Optional<Budget> duplicate = budgetDAO.findByCategoryMonthYear(categoryId, month, year);
            if (duplicate.isPresent() && !duplicate.get().getId().equals(id)) {
                throw new IllegalArgumentException("A budget already exists for that category, month, and year.");
            }
        }

        existing.setCategoryId(categoryId);
        existing.setBudgetAmount(budgetAmount);
        existing.setMonth(month);
        existing.setYear(year);

        return budgetDAO.update(existing);
    }

    public boolean deleteBudget(long id) throws SQLException {
        getBudgetById(id); // Ensure exists
        return budgetDAO.delete(id);
    }

    public List<BudgetSummary> getBudgetsForMonthAndYear(int month, int year) throws SQLException {
        validateMonthAndYear(month, year);
        List<Budget> budgets = budgetDAO.findByMonthAndYear(month, year);
        List<BudgetSummary> summaries = new ArrayList<>();

        for (Budget b : budgets) {
            BigDecimal actualExpenses = budgetDAO.getActualExpenseForCategory(b.getCategoryId(), month, year);
            summaries.add(new BudgetSummary(b, actualExpenses));
        }
        return summaries;
    }

    public BudgetSummary getBudgetStatus(long budgetId) throws SQLException {
        Budget budget = getBudgetById(budgetId);
        BigDecimal actualExpenses = budgetDAO.getActualExpenseForCategory(budget.getCategoryId(), budget.getMonth(), budget.getYear());
        return new BudgetSummary(budget, actualExpenses);
    }

    private void validateBudgetInputs(long categoryId, BigDecimal budgetAmount, int month, int year) throws SQLException {
        if (budgetAmount == null || budgetAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Budget amount must be strictly greater than zero.");
        }
        validateMonthAndYear(month, year);

        Category category = categoryDAO.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Category not found with ID: " + categoryId));

        if (category.getType() != CategoryType.EXPENSE) {
            throw new IllegalArgumentException("Category '" + category.getName() + "' (ID: " + categoryId + ") is of type " + category.getType() + ". Budgets can only be assigned to EXPENSE categories.");
        }
    }

    private void validateMonthAndYear(int month, int year) {
        if (month < 1 || month > 12) {
            throw new IllegalArgumentException("Month must be between 1 and 12.");
        }
        if (year < 1900 || year > 2100) {
            throw new IllegalArgumentException("Year must be a valid 4-digit year (1900-2100).");
        }
    }
}
