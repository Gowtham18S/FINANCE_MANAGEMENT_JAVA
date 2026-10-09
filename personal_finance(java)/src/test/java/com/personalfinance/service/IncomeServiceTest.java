package com.personalfinance.service;

import com.personalfinance.dao.CategoryDAO;
import com.personalfinance.dao.IncomeDAO;
import com.personalfinance.model.Category;
import com.personalfinance.model.CategoryType;
import com.personalfinance.model.Income;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IncomeServiceTest {

    @Mock
    private IncomeDAO incomeDAO;

    @Mock
    private CategoryDAO categoryDAO;

    private IncomeService incomeService;

    @BeforeEach
    void setUp() {
        incomeService = new IncomeService(incomeDAO, categoryDAO);
    }

    @Test
    @DisplayName("Should successfully add valid income record")
    void testAddValidIncome() throws SQLException {
        Category category = new Category(1L, "Salary", CategoryType.INCOME, "Monthly Salary");
        when(categoryDAO.findById(1L)).thenReturn(Optional.of(category));

        when(incomeDAO.save(any(Income.class))).thenAnswer(invocation -> {
            Income inc = invocation.getArgument(0);
            inc.setId(100L);
            return inc;
        });

        Income income = incomeService.addIncome("Tech Corp", new BigDecimal("5000.00"), LocalDate.now(), 1L, "Monthly paycheck");
        assertNotNull(income);
        assertEquals(100L, income.getId());
        assertEquals("Tech Corp", income.getSource());
        assertEquals(new BigDecimal("5000.00"), income.getAmount());
    }

    @Test
    @DisplayName("Should reject income with zero or negative amount")
    void testAddIncomeInvalidAmount() {
        assertThrows(IllegalArgumentException.class, () ->
                incomeService.addIncome("Tech Corp", BigDecimal.ZERO, LocalDate.now(), 1L, "Zero test"));
        assertThrows(IllegalArgumentException.class, () ->
                incomeService.addIncome("Tech Corp", new BigDecimal("-100"), LocalDate.now(), 1L, "Negative test"));
    }

    @Test
    @DisplayName("Should reject income if category type is EXPENSE")
    void testAddIncomeWithExpenseCategory() throws SQLException {
        Category expenseCat = new Category(2L, "Food", CategoryType.EXPENSE, "Food category");
        when(categoryDAO.findById(2L)).thenReturn(Optional.of(expenseCat));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                incomeService.addIncome("Tech Corp", new BigDecimal("1000.00"), LocalDate.now(), 2L, "Wrong category type"));

        assertTrue(ex.getMessage().contains("Only INCOME categories can be assigned"));
    }
}
