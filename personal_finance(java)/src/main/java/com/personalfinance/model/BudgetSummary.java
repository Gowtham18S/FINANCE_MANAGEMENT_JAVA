package com.personalfinance.model;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Helper class for rendering budget utilization summaries.
 */
public class BudgetSummary {
    private Budget budget;
    private BigDecimal actualExpenses;
    private BigDecimal remainingBudget;
    private BigDecimal usagePercentage;
    private String status; // "WITHIN LIMIT" or "EXCEEDED"

    public BudgetSummary(Budget budget, BigDecimal actualExpenses) {
        this.budget = budget;
        this.actualExpenses = (actualExpenses != null) ? actualExpenses : BigDecimal.ZERO;
        
        BigDecimal budgetAmount = budget.getBudgetAmount();
        this.remainingBudget = budgetAmount.subtract(this.actualExpenses);
        
        if (budgetAmount.compareTo(BigDecimal.ZERO) > 0) {
            this.usagePercentage = this.actualExpenses
                    .multiply(new BigDecimal("100"))
                    .divide(budgetAmount, 2, RoundingMode.HALF_UP);
        } else {
            this.usagePercentage = BigDecimal.ZERO;
        }

        if (this.actualExpenses.compareTo(budgetAmount) > 0) {
            this.status = "EXCEEDED";
        } else {
            this.status = "WITHIN LIMIT";
        }
    }

    public Budget getBudget() {
        return budget;
    }

    public BigDecimal getActualExpenses() {
        return actualExpenses;
    }

    public BigDecimal getRemainingBudget() {
        return remainingBudget;
    }

    public BigDecimal getUsagePercentage() {
        return usagePercentage;
    }

    public String getStatus() {
        return status;
    }
}
