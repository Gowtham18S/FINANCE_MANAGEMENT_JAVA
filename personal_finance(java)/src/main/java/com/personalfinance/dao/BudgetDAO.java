package com.personalfinance.dao;

import com.personalfinance.config.DatabaseConnection;
import com.personalfinance.model.Budget;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object for Budget entity.
 */
public class BudgetDAO {

    public Budget save(Budget budget) throws SQLException {
        String sql = "INSERT INTO budgets (category_id, budget_amount, month, year) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setLong(1, budget.getCategoryId());
            stmt.setBigDecimal(2, budget.getBudgetAmount());
            stmt.setInt(3, budget.getMonth());
            stmt.setInt(4, budget.getYear());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating budget failed, no rows affected.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    budget.setId(generatedKeys.getLong(1));
                }
            }
            return budget;
        }
    }

    public List<Budget> findAll() throws SQLException {
        List<Budget> list = new ArrayList<>();
        String sql = """
            SELECT b.id, b.category_id, b.budget_amount, b.month, b.year, c.name AS category_name
            FROM budgets b
            JOIN categories c ON b.category_id = c.id
            ORDER BY b.year DESC, b.month DESC, c.name ASC
            """;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        }
        return list;
    }

    public Optional<Budget> findById(long id) throws SQLException {
        String sql = """
            SELECT b.id, b.category_id, b.budget_amount, b.month, b.year, c.name AS category_name
            FROM budgets b
            JOIN categories c ON b.category_id = c.id
            WHERE b.id = ?
            """;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        }
        return Optional.empty();
    }

    public Optional<Budget> findByCategoryMonthYear(long categoryId, int month, int year) throws SQLException {
        String sql = """
            SELECT b.id, b.category_id, b.budget_amount, b.month, b.year, c.name AS category_name
            FROM budgets b
            JOIN categories c ON b.category_id = c.id
            WHERE b.category_id = ? AND b.month = ? AND b.year = ?
            """;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, categoryId);
            stmt.setInt(2, month);
            stmt.setInt(3, year);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        }
        return Optional.empty();
    }

    public List<Budget> findByMonthAndYear(int month, int year) throws SQLException {
        List<Budget> list = new ArrayList<>();
        String sql = """
            SELECT b.id, b.category_id, b.budget_amount, b.month, b.year, c.name AS category_name
            FROM budgets b
            JOIN categories c ON b.category_id = c.id
            WHERE b.month = ? AND b.year = ?
            ORDER BY c.name ASC
            """;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, month);
            stmt.setInt(2, year);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        }
        return list;
    }

    public boolean update(Budget budget) throws SQLException {
        String sql = "UPDATE budgets SET category_id = ?, budget_amount = ?, month = ?, year = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, budget.getCategoryId());
            stmt.setBigDecimal(2, budget.getBudgetAmount());
            stmt.setInt(3, budget.getMonth());
            stmt.setInt(4, budget.getYear());
            stmt.setLong(5, budget.getId());
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean delete(long id) throws SQLException {
        String sql = "DELETE FROM budgets WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    public BigDecimal getActualExpenseForCategory(long categoryId, int month, int year) throws SQLException {
        String sql = "SELECT COALESCE(SUM(amount), 0) FROM expenses WHERE category_id = ? AND MONTH(expense_date) = ? AND YEAR(expense_date) = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, categoryId);
            stmt.setInt(2, month);
            stmt.setInt(3, year);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getBigDecimal(1);
                }
            }
        }
        return BigDecimal.ZERO;
    }

    private Budget mapRow(ResultSet rs) throws SQLException {
        Budget budget = new Budget();
        budget.setId(rs.getLong("id"));
        budget.setCategoryId(rs.getLong("category_id"));
        budget.setCategoryName(rs.getString("category_name"));
        budget.setBudgetAmount(rs.getBigDecimal("budget_amount"));
        budget.setMonth(rs.getInt("month"));
        budget.setYear(rs.getInt("year"));
        return budget;
    }
}
