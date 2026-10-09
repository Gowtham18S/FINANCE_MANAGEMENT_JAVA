package com.personalfinance.service;

import com.personalfinance.dao.BudgetDAO;
import com.personalfinance.dao.CategoryDAO;
import com.personalfinance.model.Budget;
import com.personalfinance.model.BudgetSummary;
import com.personalfinance.model.Category;
import com.personalfinance.model.CategoryType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BudgetServiceTest {

    @Mock
    private BudgetDAO budgetDAO;

    @Mock
    private CategoryDAO categoryDAO;

    private BudgetService budgetService;

    @BeforeEach
    void setUp() {
        budgetService = new BudgetService(budgetDAO, categoryDAO);
    }

    @Test
    @DisplayName("Should set monthly budget correctly")
    void testSetBudgetSuccess() throws SQLException {
        Category category = new Category(2L, "Food", CategoryType.EXPENSE, "Food");
        when(categoryDAO.findById(2L)).thenReturn(Optional.of(category));
        when(budgetDAO.findByCategoryMonthYear(2L, 3, 2026)).thenReturn(Optional.empty());

        when(budgetDAO.save(any(Budget.class))).thenAnswer(invocation -> {
            Budget b = invocation.getArgument(0);
            b.setId(300L);
            return b;
        });

        Budget budget = budgetService.setBudget(2L, new BigDecimal("500.00"), 3, 2026);
        assertNotNull(budget);
        assertEquals(300L, budget.getId());
        assertEquals(new BigDecimal("500.00"), budget.getBudgetAmount());
    }

    @Test
    @DisplayName("Should prevent setting duplicate monthly budget for same category and period")
    void testSetDuplicateBudget() throws SQLException {
        Category category = new Category(2L, "Food", CategoryType.EXPENSE, "Food");
        when(categoryDAO.findById(2L)).thenReturn(Optional.of(category));

        Budget existing = new Budget(300L, 2L, new BigDecimal("500.00"), 3, 2026);
        when(budgetDAO.findByCategoryMonthYear(2L, 3, 2026)).thenReturn(Optional.of(existing));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                budgetService.setBudget(2L, new BigDecimal("600.00"), 3, 2026));

        assertTrue(ex.getMessage().contains("already been set"));
    }

    @Test
    @DisplayName("Should calculate budget summary metrics correctly when WITHIN LIMIT")
    void testBudgetSummaryWithinLimit() throws SQLException {
        Budget budget = new Budget(300L, 2L, new BigDecimal("500.00"), 3, 2026);
        budget.setCategoryName("Food");
        when(budgetDAO.findById(300L)).thenReturn(Optional.of(budget));
        when(budgetDAO.getActualExpenseForCategory(2L, 3, 2026)).thenReturn(new BigDecimal("350.00"));

        BudgetSummary summary = budgetService.getBudgetStatus(300L);
        assertEquals(new BigDecimal("350.00"), summary.getActualExpenses());
        assertEquals(new BigDecimal("150.00"), summary.getRemainingBudget());
        assertEquals(new BigDecimal("70.00"), summary.getUsagePercentage());
        assertEquals("WITHIN LIMIT", summary.getStatus());
    }

    @Test
    @DisplayName("Should calculate budget summary metrics correctly when EXCEEDED")
    void testBudgetSummaryExceeded() throws SQLException {
        Budget budget = new Budget(300L, 2L, new BigDecimal("500.00"), 3, 2026);
        budget.setCategoryName("Food");
        when(budgetDAO.findById(300L)).thenReturn(Optional.of(budget));
        when(budgetDAO.getActualExpenseForCategory(2L, 3, 2026)).thenReturn(new BigDecimal("600.00"));

        BudgetSummary summary = budgetService.getBudgetStatus(300L);
        assertEquals(new BigDecimal("600.00"), summary.getActualExpenses());
        assertEquals(new BigDecimal("-100.00"), summary.getRemainingBudget());
        assertEquals(new BigDecimal("120.00"), summary.getUsagePercentage());
        assertEquals("EXCEEDED", summary.getStatus());
    }
}
