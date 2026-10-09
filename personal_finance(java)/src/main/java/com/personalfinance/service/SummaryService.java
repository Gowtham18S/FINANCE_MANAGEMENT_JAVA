package com.personalfinance.service;

import com.personalfinance.dao.BudgetDAO;
import com.personalfinance.dao.ExpenseDAO;
import com.personalfinance.dao.IncomeDAO;
import com.personalfinance.model.BudgetSummary;
import com.personalfinance.model.Expense;
import com.personalfinance.model.FinancialSummary;
import com.personalfinance.model.Income;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * Service class for computing high-level financial summaries and reports.
 */
public class SummaryService {

    private final IncomeDAO incomeDAO;
    private final ExpenseDAO expenseDAO;
    private final BudgetDAO budgetDAO;
    private final BudgetService budgetService;

    public SummaryService() {
        this.incomeDAO = new IncomeDAO();
        this.expenseDAO = new ExpenseDAO();
        this.budgetDAO = new BudgetDAO();
        this.budgetService = new BudgetService(this.budgetDAO, new com.personalfinance.dao.CategoryDAO());
    }

    public SummaryService(IncomeDAO incomeDAO, ExpenseDAO expenseDAO, BudgetDAO budgetDAO, BudgetService budgetService) {
        this.incomeDAO = incomeDAO;
        this.expenseDAO = expenseDAO;
        this.budgetDAO = budgetDAO;
        this.budgetService = budgetService;
    }

    public FinancialSummary getOverallSummary() throws SQLException {
        BigDecimal totalIncome = incomeDAO.getTotalIncomeOverall();
        BigDecimal totalExpenses = expenseDAO.getTotalExpensesOverall();
        return new FinancialSummary(totalIncome, totalExpenses);
    }

    public FinancialSummary getMonthlySummary(int month, int year) throws SQLException {
        validateMonthAndYear(month, year);
        BigDecimal monthlyIncome = incomeDAO.getTotalIncomeByMonth(month, year);
        BigDecimal monthlyExpenses = expenseDAO.getTotalExpensesByMonth(month, year);
        return new FinancialSummary(monthlyIncome, monthlyExpenses, month, year);
    }

    public FinancialSummary getAnnualSummary(int year) throws SQLException {
        validateYear(year);
        BigDecimal annualIncome = incomeDAO.getTotalIncomeByYear(year);
        BigDecimal annualExpenses = expenseDAO.getTotalExpensesByYear(year);
        return new FinancialSummary(annualIncome, annualExpenses, null, year);
    }

    public Map<String, BigDecimal> getCategoryWiseExpenses() throws SQLException {
        return expenseDAO.getCategoryWiseExpenses();
    }

    public List<Expense> getHighestExpenses(int limit) throws SQLException {
        if (limit <= 0) limit = 5;
        return expenseDAO.getHighestExpenses(limit);
    }

    public List<BudgetSummary> getMonthlyBudgetUtilization(int month, int year) throws SQLException {
        return budgetService.getBudgetsForMonthAndYear(month, year);
    }

    /**
     * Helper to retrieve recent transactions (combined Income and Expense items) sorted by date descending.
     */
    public List<RecentTransaction> getRecentTransactions(int limit) throws SQLException {
        List<Income> incomes = incomeDAO.findAll();
        List<Expense> expenses = expenseDAO.findAll();

        List<RecentTransaction> all = new ArrayList<>();
        for (Income inc : incomes) {
            all.add(new RecentTransaction("INCOME", inc.getId(), inc.getSource(), inc.getAmount(), inc.getIncomeDate(), inc.getCategoryName()));
        }
        for (Expense exp : expenses) {
            all.add(new RecentTransaction("EXPENSE", exp.getId(), exp.getTitle(), exp.getAmount(), exp.getExpenseDate(), exp.getCategoryName()));
        }

        all.sort(Comparator.comparing(RecentTransaction::getDate).reversed());

        if (all.size() > limit) {
            return all.subList(0, limit);
        }
        return all;
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

    /**
     * DTO for combined recent transaction listing.
     */
    public static class RecentTransaction {
        private final String type; // INCOME or EXPENSE
        private final Long id;
        private final String titleOrSource;
        private final BigDecimal amount;
        private final java.time.LocalDate date;
        private final String categoryName;

        public RecentTransaction(String type, Long id, String titleOrSource, BigDecimal amount, java.time.LocalDate date, String categoryName) {
            this.type = type;
            this.id = id;
            this.titleOrSource = titleOrSource;
            this.amount = amount;
            this.date = date;
            this.categoryName = categoryName;
        }

        public String getType() { return type; }
        public Long getId() { return id; }
        public String getTitleOrSource() { return titleOrSource; }
        public BigDecimal getAmount() { return amount; }
        public java.time.LocalDate getDate() { return date; }
        public String getCategoryName() { return categoryName; }

        @Override
        public String toString() {
            return String.format("[%s] ID: %-4d | Date: %s | Item: %-20s | Cat: %-15s | Amount: $%.2f",
                    type, id, date, titleOrSource, (categoryName != null ? categoryName : "N/A"), amount);
        }
    }
}
