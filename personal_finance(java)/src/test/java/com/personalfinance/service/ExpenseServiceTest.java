package com.personalfinance.service;

import com.personalfinance.dao.CategoryDAO;
import com.personalfinance.dao.ExpenseDAO;
import com.personalfinance.model.Category;
import com.personalfinance.model.CategoryType;
import com.personalfinance.model.Expense;
import com.personalfinance.model.PaymentMethod;
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
class ExpenseServiceTest {

    @Mock
    private ExpenseDAO expenseDAO;

    @Mock
    private CategoryDAO categoryDAO;

    private ExpenseService expenseService;

    @BeforeEach
    void setUp() {
        expenseService = new ExpenseService(expenseDAO, categoryDAO);
    }

    @Test
    @DisplayName("Should successfully add valid expense record")
    void testAddValidExpense() throws SQLException {
        Category category = new Category(2L, "Food", CategoryType.EXPENSE, "Food & Dining");
        when(categoryDAO.findById(2L)).thenReturn(Optional.of(category));

        when(expenseDAO.save(any(Expense.class))).thenAnswer(invocation -> {
            Expense exp = invocation.getArgument(0);
            exp.setId(200L);
            return exp;
        });

        Expense expense = expenseService.addExpense("Grocery Shopping", new BigDecimal("150.75"), LocalDate.now(), 2L, PaymentMethod.CREDIT_CARD, "Weekly supplies");
        assertNotNull(expense);
        assertEquals(200L, expense.getId());
        assertEquals("Grocery Shopping", expense.getTitle());
        assertEquals(new BigDecimal("150.75"), expense.getAmount());
        assertEquals(PaymentMethod.CREDIT_CARD, expense.getPaymentMethod());
    }

    @Test
    @DisplayName("Should reject expense with invalid amount or missing title")
    void testAddExpenseValidation() {
        assertThrows(IllegalArgumentException.class, () ->
                expenseService.addExpense("", new BigDecimal("50.00"), LocalDate.now(), 2L, PaymentMethod.CASH, "Empty title"));

        assertThrows(IllegalArgumentException.class, () ->
                expenseService.addExpense("Dinner", new BigDecimal("0.00"), LocalDate.now(), 2L, PaymentMethod.CASH, "Zero amount"));
    }

    @Test
    @DisplayName("Should reject expense assigned to INCOME category")
    void testAddExpenseWithIncomeCategory() throws SQLException {
        Category incomeCat = new Category(1L, "Salary", CategoryType.INCOME, "Job salary");
        when(categoryDAO.findById(1L)).thenReturn(Optional.of(incomeCat));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                expenseService.addExpense("Lunch", new BigDecimal("20.00"), LocalDate.now(), 1L, PaymentMethod.UPI, "Wrong cat"));

        assertTrue(ex.getMessage().contains("Only EXPENSE categories can be assigned"));
    }
}
