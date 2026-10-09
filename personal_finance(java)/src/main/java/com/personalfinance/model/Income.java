package com.personalfinance.model;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Model class representing an income record.
 */
public class Income {
    private Long id;
    private String source;
    private BigDecimal amount;
    private LocalDate incomeDate;
    private Long categoryId;
    private String categoryName; // Helper for display joins
    private String description;
    private Timestamp createdAt;

    public Income() {}

    public Income(String source, BigDecimal amount, LocalDate incomeDate, Long categoryId, String description) {
        this.source = source;
        this.amount = amount;
        this.incomeDate = incomeDate;
        this.categoryId = categoryId;
        this.description = description;
    }

    public Income(Long id, String source, BigDecimal amount, LocalDate incomeDate, Long categoryId, String description) {
        this.id = id;
        this.source = source;
        this.amount = amount;
        this.incomeDate = incomeDate;
        this.categoryId = categoryId;
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDate getIncomeDate() {
        return incomeDate;
    }

    public void setIncomeDate(LocalDate incomeDate) {
        this.incomeDate = incomeDate;
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
        Income income = (Income) o;
        return Objects.equals(id, income.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("ID: %-5d | Source: %-20s | Amount: $%-10.2f | Date: %s | Category: %-15s | Desc: %s",
                id, source, amount, incomeDate, (categoryName != null ? categoryName : "Cat#" + categoryId),
                (description != null ? description : "N/A"));
    }
}
