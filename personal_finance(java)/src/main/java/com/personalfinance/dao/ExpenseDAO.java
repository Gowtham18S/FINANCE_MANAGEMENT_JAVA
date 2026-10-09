package com.personalfinance.dao;

import com.personalfinance.config.DatabaseConnection;
import com.personalfinance.model.Expense;
import com.personalfinance.model.PaymentMethod;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Data Access Object for Expense entity.
 */
public class ExpenseDAO {

    public Expense save(Expense expense) throws SQLException {
        String sql = "INSERT INTO expenses (title, amount, expense_date, category_id, payment_method, description) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, expense.getTitle());
            stmt.setBigDecimal(2, expense.getAmount());
            stmt.setDate(3, Date.valueOf(expense.getExpenseDate()));
            stmt.setLong(4, expense.getCategoryId());
            stmt.setString(5, expense.getPaymentMethod().name());
            stmt.setString(6, expense.getDescription());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating expense record failed, no rows affected.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    expense.setId(generatedKeys.getLong(1));
                }
            }
            return expense;
        }
    }

    public List<Expense> findAll() throws SQLException {
        List<Expense> list = new ArrayList<>();
        String sql = """
            SELECT e.id, e.title, e.amount, e.expense_date, e.category_id, e.payment_method, e.description, e.created_at, c.name AS category_name
            FROM expenses e
            JOIN categories c ON e.category_id = c.id
            ORDER BY e.expense_date DESC, e.id DESC
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

    public Optional<Expense> findById(long id) throws SQLException {
        String sql = """
            SELECT e.id, e.title, e.amount, e.expense_date, e.category_id, e.payment_method, e.description, e.created_at, c.name AS category_name
            FROM expenses e
            JOIN categories c ON e.category_id = c.id
            WHERE e.id = ?
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

    public List<Expense> findByCategory(long categoryId) throws SQLException {
        List<Expense> list = new ArrayList<>();
        String sql = """
            SELECT e.id, e.title, e.amount, e.expense_date, e.category_id, e.payment_method, e.description, e.created_at, c.name AS category_name
            FROM expenses e
            JOIN categories c ON e.category_id = c.id
            WHERE e.category_id = ?
            ORDER BY e.expense_date DESC
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

    public List<Expense> findByDateRange(LocalDate startDate, LocalDate endDate) throws SQLException {
        List<Expense> list = new ArrayList<>();
        String sql = """
            SELECT e.id, e.title, e.amount, e.expense_date, e.category_id, e.payment_method, e.description, e.created_at, c.name AS category_name
            FROM expenses e
            JOIN categories c ON e.category_id = c.id
            WHERE e.expense_date BETWEEN ? AND ?
            ORDER BY e.expense_date DESC
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

    public boolean update(Expense expense) throws SQLException {
        String sql = "UPDATE expenses SET title = ?, amount = ?, expense_date = ?, category_id = ?, payment_method = ?, description = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, expense.getTitle());
            stmt.setBigDecimal(2, expense.getAmount());
            stmt.setDate(3, Date.valueOf(expense.getExpenseDate()));
            stmt.setLong(4, expense.getCategoryId());
            stmt.setString(5, expense.getPaymentMethod().name());
            stmt.setString(6, expense.getDescription());
            stmt.setLong(7, expense.getId());
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean delete(long id) throws SQLException {
        String sql = "DELETE FROM expenses WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    public BigDecimal getTotalExpensesOverall() throws SQLException {
        String sql = "SELECT COALESCE(SUM(amount), 0) FROM expenses";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getBigDecimal(1);
            }
        }
        return BigDecimal.ZERO;
    }

    public BigDecimal getTotalExpensesByMonth(int month, int year) throws SQLException {
        String sql = "SELECT COALESCE(SUM(amount), 0) FROM expenses WHERE MONTH(expense_date) = ? AND YEAR(expense_date) = ?";
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

    public BigDecimal getTotalExpensesByYear(int year) throws SQLException {
        String sql = "SELECT COALESCE(SUM(amount), 0) FROM expenses WHERE YEAR(expense_date) = ?";
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

    public List<Expense> getHighestExpenses(int limit) throws SQLException {
        List<Expense> list = new ArrayList<>();
        String sql = """
            SELECT e.id, e.title, e.amount, e.expense_date, e.category_id, e.payment_method, e.description, e.created_at, c.name AS category_name
            FROM expenses e
            JOIN categories c ON e.category_id = c.id
            ORDER BY e.amount DESC
            LIMIT ?
            """;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, limit);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        }
        return list;
    }

    public Map<String, BigDecimal> getCategoryWiseExpenses() throws SQLException {
        Map<String, BigDecimal> map = new LinkedHashMap<>();
        String sql = """
            SELECT c.name AS category_name, COALESCE(SUM(e.amount), 0) AS total_amount
            FROM expenses e
            JOIN categories c ON e.category_id = c.id
            GROUP BY c.id, c.name
            ORDER BY total_amount DESC
            """;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                map.put(rs.getString("category_name"), rs.getBigDecimal("total_amount"));
            }
        }
        return map;
    }

    private Expense mapRow(ResultSet rs) throws SQLException {
        Expense expense = new Expense();
        expense.setId(rs.getLong("id"));
        expense.setTitle(rs.getString("title"));
        expense.setAmount(rs.getBigDecimal("amount"));
        expense.setExpenseDate(rs.getDate("expense_date").toLocalDate());
        expense.setCategoryId(rs.getLong("category_id"));
        expense.setCategoryName(rs.getString("category_name"));
        expense.setPaymentMethod(PaymentMethod.fromString(rs.getString("payment_method")));
        expense.setDescription(rs.getString("description"));
        expense.setCreatedAt(rs.getTimestamp("created_at"));
        return expense;
    }
}
