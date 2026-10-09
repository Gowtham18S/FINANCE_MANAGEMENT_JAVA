package com.personalfinance.model;

import java.math.BigDecimal;

/**
 * Value object holding calculated financial summaries (Overall, Monthly, or Annual).
 */
public class FinancialSummary {
    private BigDecimal totalIncome;
    private BigDecimal totalExpenses;
    private BigDecimal netBalanceOrSavings;
    private Integer month;
    private Integer year;

    public FinancialSummary(BigDecimal totalIncome, BigDecimal totalExpenses) {
        this.totalIncome = (totalIncome != null) ? totalIncome : BigDecimal.ZERO;
        this.totalExpenses = (totalExpenses != null) ? totalExpenses : BigDecimal.ZERO;
        this.netBalanceOrSavings = this.totalIncome.subtract(this.totalExpenses);
    }

    public FinancialSummary(BigDecimal totalIncome, BigDecimal totalExpenses, Integer month, Integer year) {
        this(totalIncome, totalExpenses);
        this.month = month;
        this.year = year;
    }

    public BigDecimal getTotalIncome() {
        return totalIncome;
    }

    public BigDecimal getTotalExpenses() {
        return totalExpenses;
    }

    public BigDecimal getNetBalanceOrSavings() {
        return netBalanceOrSavings;
    }

    public Integer getMonth() {
        return month;
    }

    public Integer getYear() {
        return year;
    }
}
