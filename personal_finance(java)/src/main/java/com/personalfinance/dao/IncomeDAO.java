package com.personalfinance.dao;

import com.personalfinance.config.DatabaseConnection;
import com.personalfinance.model.Income;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object for Income entity.
 */
public class IncomeDAO {

    public Income save(Income income) throws SQLException {
        String sql = "INSERT INTO income (source, amount, income_date, category_id, description) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, income.getSource());
            stmt.setBigDecimal(2, income.getAmount());
            stmt.setDate(3, Date.valueOf(income.getIncomeDate()));
            stmt.setLong(4, income.getCategoryId());
            stmt.setString(5, income.getDescription());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating income record failed, no rows affected.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    income.setId(generatedKeys.getLong(1));
                }
            }
            return income;
        }
    }

    public List<Income> findAll() throws SQLException {
        List<Income> list = new ArrayList<>();
        String sql = """
            SELECT i.id, i.source, i.amount, i.income_date, i.category_id, i.description, i.created_at, c.name AS category_name
            FROM income i
            JOIN categories c ON i.category_id = c.id
            ORDER BY i.income_date DESC, i.id DESC
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

    public Optional<Income> findById(long id) throws SQLException {
        String sql = """
            SELECT i.id, i.source, i.amount, i.income_date, i.category_id, i.description, i.created_at, c.name AS category_name
            FROM income i
            JOIN categories c ON i.category_id = c.id
            WHERE i.id = ?
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

    public List<Income> findByCategory(long categoryId) throws SQLException {
        List<Income> list = new ArrayList<>();
        String sql = """
            SELECT i.id, i.source, i.amount, i.income_date, i.category_id, i.description, i.created_at, c.name AS category_name
            FROM income i
            JOIN categories c ON i.category_id = c.id
            WHERE i.category_id = ?
            ORDER BY i.income_date DESC
            """;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, categoryId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        }
        return list;
    }

    public List<Income> findByDateRange(LocalDate startDate, LocalDate endDate) throws SQLException {
        List<Income> list = new ArrayList<>();
        String sql = """
            SELECT i.id, i.source, i.amount, i.income_date, i.category_id, i.description, i.created_at, c.name AS category_name
            FROM income i
            JOIN categories c ON i.category_id = c.id
            WHERE i.income_date BETWEEN ? AND ?
            ORDER BY i.income_date DESC
            """;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDate(1, Date.valueOf(startDate));
            stmt.setDate(2, Date.valueOf(endDate));
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        }
        return list;
    }

    public boolean update(Income income) throws SQLException {
        String sql = "UPDATE income SET source = ?, amount = ?, income_date = ?, category_id = ?, description = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, income.getSource());
            stmt.setBigDecimal(2, income.getAmount());
            stmt.setDate(3, Date.valueOf(income.getIncomeDate()));
            stmt.setLong(4, income.getCategoryId());
            stmt.setString(5, income.getDescription());
            stmt.setLong(6, income.getId());
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean delete(long id) throws SQLException {
        String sql = "DELETE FROM income WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    public BigDecimal getTotalIncomeOverall() throws SQLException {
        String sql = "SELECT COALESCE(SUM(amount), 0) FROM income";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getBigDecimal(1);
            }
        }
        return BigDecimal.ZERO;
    }

    public BigDecimal getTotalIncomeByMonth(int month, int year) throws SQLException {
        String sql = "SELECT COALESCE(SUM(amount), 0) FROM income WHERE MONTH(income_date) = ? AND YEAR(income_date) = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, month);
            stmt.setInt(2, year);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getBigDecimal(1);
                }
            }
        }
        return BigDecimal.ZERO;
    }

    public BigDecimal getTotalIncomeByYear(int year) throws SQLException {
        String sql = "SELECT COALESCE(SUM(amount), 0) FROM income WHERE YEAR(income_date) = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, year);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getBigDecimal(1);
                }
            }
        }
        return BigDecimal.ZERO;
    }

    private Income mapRow(ResultSet rs) throws SQLException {
        Income income = new Income();
        income.setId(rs.getLong("id"));
        income.setSource(rs.getString("source"));
        income.setAmount(rs.getBigDecimal("amount"));
        income.setIncomeDate(rs.getDate("income_date").toLocalDate());
        income.setCategoryId(rs.getLong("category_id"));
        income.setCategoryName(rs.getString("category_name"));
        income.setDescription(rs.getString("description"));
        income.setCreatedAt(rs.getTimestamp("created_at"));
        return income;
    }
}
