package com.personalfinance.service;

import com.personalfinance.dao.CategoryDAO;
import com.personalfinance.dao.IncomeDAO;
import com.personalfinance.model.Category;
import com.personalfinance.model.CategoryType;
import com.personalfinance.model.Income;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/**
 * Service class handling business logic for income management.
 */
public class IncomeService {

    private final IncomeDAO incomeDAO;
    private final CategoryDAO categoryDAO;

    public IncomeService() {
        this.incomeDAO = new IncomeDAO();
        this.categoryDAO = new CategoryDAO();
    }

    public IncomeService(IncomeDAO incomeDAO, CategoryDAO categoryDAO) {
        this.incomeDAO = incomeDAO;
        this.categoryDAO = categoryDAO;
    }

    public Income addIncome(String source, BigDecimal amount, LocalDate incomeDate, long categoryId, String description) throws SQLException {
        validateIncomeInputs(source, amount, incomeDate, categoryId);

        Income income = new Income(source.trim(), amount, incomeDate, categoryId, description != null ? description.trim() : "");
        return incomeDAO.save(income);
    }

    public List<Income> getAllIncome() throws SQLException {
        return incomeDAO.findAll();
    }

    public Income getIncomeById(long id) throws SQLException {
        return incomeDAO.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Income record not found with ID: " + id));
    }

    public boolean updateIncome(long id, String source, BigDecimal amount, LocalDate incomeDate, long categoryId, String description) throws SQLException {
        Income existing = getIncomeById(id);
        validateIncomeInputs(source, amount, incomeDate, categoryId);

        existing.setSource(source.trim());
        existing.setAmount(amount);
        existing.setIncomeDate(incomeDate);
        existing.setCategoryId(categoryId);
        existing.setDescription(description != null ? description.trim() : "");

        return incomeDAO.update(existing);
    }

    public boolean deleteIncome(long id) throws SQLException {
        getIncomeById(id); // Ensure exists
        return incomeDAO.delete(id);
    }

    public List<Income> getIncomeByCategory(long categoryId) throws SQLException {
        categoryDAO.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Category not found with ID: " + categoryId));
        return incomeDAO.findByCategory(categoryId);
    }

    public List<Income> getIncomeByDateRange(LocalDate startDate, LocalDate endDate) throws SQLException {
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("Start date and end date must not be null.");
        }
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date cannot be after end date.");
        }
        return incomeDAO.findByDateRange(startDate, endDate);
    }

    public BigDecimal getTotalIncomeByMonth(int month, int year) throws SQLException {
        validateMonthAndYear(month, year);
        return incomeDAO.getTotalIncomeByMonth(month, year);
    }

    public BigDecimal getTotalIncomeByYear(int year) throws SQLException {
        validateYear(year);
        return incomeDAO.getTotalIncomeByYear(year);
    }

    private void validateIncomeInputs(String source, BigDecimal amount, LocalDate incomeDate, long categoryId) throws SQLException {
        if (source == null || source.trim().isEmpty()) {
            throw new IllegalArgumentException("Income source cannot be empty.");
        }
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Income amount must be greater than zero.");
        }
        if (incomeDate == null) {
            throw new IllegalArgumentException("Income date cannot be null.");
        }

        Category category = categoryDAO.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Category not found with ID: " + categoryId));

        if (category.getType() != CategoryType.INCOME) {
            throw new IllegalArgumentException("Category '" + category.getName() + "' (ID: " + categoryId + ") is of type " + category.getType() + ". Only INCOME categories can be assigned to income records.");
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
