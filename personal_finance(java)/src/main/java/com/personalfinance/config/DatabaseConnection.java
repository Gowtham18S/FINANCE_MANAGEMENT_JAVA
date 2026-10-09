package com.personalfinance.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Utility class responsible for managing JDBC database connections to MySQL.
 */
public class DatabaseConnection {

    private static final String DEFAULT_HOST = "localhost";
    private static final String DEFAULT_PORT = "3306";
    private static final String DEFAULT_DB_NAME = "personal_finance_db";
    private static final String DEFAULT_USER = "root";
    private static final String DEFAULT_PASS = "Gow@2008";

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("CRITICAL: MySQL JDBC Driver not found on classpath!");
            e.printStackTrace();
        }
    }

    /**
     * Obtains a connection to the specific personal_finance_db database.
     * Reads credentials from environment variables if set, otherwise uses defaults.
     */
    public static Connection getConnection() throws SQLException {
        String dbUrl = System.getenv("DB_URL");
        if (dbUrl == null || dbUrl.trim().isEmpty()) {
            dbUrl = String.format("jdbc:mysql://%s:%s/%s?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC",
                    DEFAULT_HOST, DEFAULT_PORT, DEFAULT_DB_NAME);
        }

        String user = System.getenv("DB_USER");
        if (user == null || user.trim().isEmpty()) {
            user = DEFAULT_USER;
        }

        String password = System.getenv("DB_PASSWORD");
        if (password == null || password.trim().isEmpty()) {
            password = DEFAULT_PASS;
        }

        return DriverManager.getConnection(dbUrl, user, password);
    }

    /**
     * Obtains a connection to the root MySQL server (without specifying database name).
     * Useful for database creation / initialization scripts.
     */
    public static Connection getServerConnection() throws SQLException {
        String user = System.getenv("DB_USER");
        if (user == null || user.trim().isEmpty()) {
            user = DEFAULT_USER;
        }

        String password = System.getenv("DB_PASSWORD");
        if (password == null || password.trim().isEmpty()) {
            password = DEFAULT_PASS;
        }

        String serverUrl = String.format("jdbc:mysql://%s:%s/?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC",
                DEFAULT_HOST, DEFAULT_PORT);

        return DriverManager.getConnection(serverUrl, user, password);
    }

    /**
     * Utility method to test database connection and print diagnostic information.
     */
    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            System.err.println("\n==================================================");
            System.err.println("          DATABASE CONNECTION FAILED              ");
            System.err.println("==================================================");
            System.err.println("Error Code: " + e.getErrorCode());
            System.err.println("SQL State:  " + e.getSQLState());
            System.err.println("Message:    " + e.getMessage());
            System.err.println("--------------------------------------------------");
            System.err.println("Please check:");
            System.err.println("1. Is MySQL Server running on localhost:3306?");
            System.err.println("2. Are DB_USER and DB_PASSWORD environment variables correct?");
            System.err.println("3. Has the database 'personal_finance_db' been created?");
            System.err.println("==================================================\n");
            return false;
        }
    }
}