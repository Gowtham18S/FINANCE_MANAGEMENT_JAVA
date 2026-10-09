package com.personalfinance.service;

import com.personalfinance.dao.BudgetDAO;
import com.personalfinance.dao.ExpenseDAO;
import com.personalfinance.dao.IncomeDAO;
import com.personalfinance.model.FinancialSummary;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SummaryServiceTest {

    @Mock
    private IncomeDAO incomeDAO;

    @Mock
    private ExpenseDAO expenseDAO;

    @Mock
    private BudgetDAO budgetDAO;

    @Mock
    private BudgetService budgetService;

    private SummaryService summaryService;

    @BeforeEach
    void setUp() {
        summaryService = new SummaryService(incomeDAO, expenseDAO, budgetDAO, budgetService);
    }

    @Test
    @DisplayName("Should calculate overall balance correctly: Balance = Total Income - Total Expense")
    void testOverallSummary() throws SQLException {
        when(incomeDAO.getTotalIncomeOverall()).thenReturn(new BigDecimal("10000.00"));
        when(expenseDAO.getTotalExpensesOverall()).thenReturn(new BigDecimal("4200.50"));

        FinancialSummary summary = summaryService.getOverallSummary();
        assertEquals(new BigDecimal("10000.00"), summary.getTotalIncome());
        assertEquals(new BigDecimal("4200.50"), summary.getTotalExpenses());
        assertEquals(new BigDecimal("5799.50"), summary.getNetBalanceOrSavings());
    }

    @Test
    @DisplayName("Should calculate monthly savings correctly: Savings = Monthly Income - Monthly Expenses")
    void testMonthlySummary() throws SQLException {
        when(incomeDAO.getTotalIncomeByMonth(3, 2026)).thenReturn(new BigDecimal("5000.00"));
        when(expenseDAO.getTotalExpensesByMonth(3, 2026)).thenReturn(new BigDecimal("2100.00"));

        FinancialSummary summary = summaryService.getMonthlySummary(3, 2026);
        assertEquals(new BigDecimal("5000.00"), summary.getTotalIncome());
        assertEquals(new BigDecimal("2100.00"), summary.getTotalExpenses());
        assertEquals(new BigDecimal("2900.00"), summary.getNetBalanceOrSavings());
    }
}
