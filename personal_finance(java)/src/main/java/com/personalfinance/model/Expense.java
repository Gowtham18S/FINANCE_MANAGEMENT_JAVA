package com.personalfinance.model;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Model class representing an expense record.
 */
public class Expense {
    private Long id;
    private String title;
    private BigDecimal amount;
    private LocalDate expenseDate;
    private Long categoryId;
    private String categoryName; // Helper for display joins
    private PaymentMethod paymentMethod;
    private String description;
    private Timestamp createdAt;

    public Expense() {}

    public Expense(String title, BigDecimal amount, LocalDate expenseDate, Long categoryId, PaymentMethod paymentMethod, String description) {
        this.title = title;
        this.amount = amount;
        this.expenseDate = expenseDate;
        this.categoryId = categoryId;
        this.paymentMethod = paymentMethod;
        this.description = description;
    }

    public Expense(Long id, String title, BigDecimal amount, LocalDate expenseDate, Long categoryId, PaymentMethod paymentMethod, String description) {
        this.id = id;
        this.title = title;
        this.amount = amount;
        this.expenseDate = expenseDate;
        this.categoryId = categoryId;
        this.paymentMethod = paymentMethod;
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDate getExpenseDate() {
        return expenseDate;
    }

    public void setExpenseDate(LocalDate expenseDate) {
        this.expenseDate = expenseDate;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Expense expense = (Expense) o;
        return Objects.equals(id, expense.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("ID: %-5d | Title: %-20s | Amount: $%-10.2f | Date: %s | Category: %-15s | Mode: %-12s | Desc: %s",
                id, title, amount, expenseDate, (categoryName != null ? categoryName : "Cat#" + categoryId),
                paymentMethod, (description != null ? description : "N/A"));
    }
}
