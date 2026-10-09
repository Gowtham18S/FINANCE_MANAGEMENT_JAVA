package com.personalfinance.model;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Model class representing a monthly expense budget.
 */
public class Budget {
    private Long id;
    private Long categoryId;
    private String categoryName; // Helper for display joins
    private BigDecimal budgetAmount;
    private int month;
    private int year;

    public Budget() {}

    public Budget(Long categoryId, BigDecimal budgetAmount, int month, int year) {
        this.categoryId = categoryId;
        this.budgetAmount = budgetAmount;
        this.month = month;
        this.year = year;
    }

    public Budget(Long id, Long categoryId, BigDecimal budgetAmount, int month, int year) {
        this.id = id;
        this.categoryId = categoryId;
        this.budgetAmount = budgetAmount;
        this.month = month;
        this.year = year;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public BigDecimal getBudgetAmount() {
        return budgetAmount;
    }

    public void setBudgetAmount(BigDecimal budgetAmount) {
        this.budgetAmount = budgetAmount;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Budget budget = (Budget) o;
        return Objects.equals(id, budget.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("ID: %-5d | Category: %-18s | Budget Amount: $%-10.2f | Month/Year: %02d/%d",
                id, (categoryName != null ? categoryName : "Cat#" + categoryId), budgetAmount, month, year);
    }
}
