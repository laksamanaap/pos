package com.pos.util;

import java.io.*;
import java.sql.*;
import java.util.Properties;

/**
 * SettingsManager mengelola pengaturan global aplikasi menggunakan PostgreSQL.
 * Memerlukan file db.properties dengan konfigurasi database.
 */
public class SettingsManager {
    private static String dbUrl;
    private static String dbUser;
    private static String dbPassword;

    static {
        loadConfig();
        try {
            Class.forName("org.postgresql.Driver");
            initializeDatabase();
        } catch (ClassNotFoundException e) {
            System.err.println("ERROR: PostgreSQL JDBC driver not found. Please add postgresql JAR to classpath.");
            System.exit(1);
        } catch (SQLException e) {
            System.err.println("ERROR: Failed to initialize settings database: " + e.getMessage());
            System.exit(1);
        }
    }

    private static void loadConfig() {
        File cfg = new File("db.properties");
        Properties p = new Properties();
        
        if (!cfg.exists()) {
            System.err.println("ERROR: db.properties not found. Please create it with database configuration.");
            System.exit(1);
        }
        
        try (FileInputStream fis = new FileInputStream(cfg)) {
            p.load(fis);
            dbUrl = p.getProperty("url");
            dbUser = p.getProperty("user");
            dbPassword = p.getProperty("password");
            
            if (dbUrl == null || dbUser == null || dbPassword == null) {
                System.err.println("ERROR: db.properties incomplete. Please set url, user, and password.");
                System.exit(1);
            }
        } catch (IOException e) {
            System.err.println("ERROR: Failed to load db.properties: " + e.getMessage());
            System.exit(1);
        }
    }

    private static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(dbUrl, dbUser, dbPassword);
    }

    /**
     * Initialize database table for settings
     */
    private static void initializeDatabase() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS profit_margin_settings (" +
                "id SERIAL PRIMARY KEY, " +
                "setting_name VARCHAR(255) UNIQUE, " +
                "default_margin DOUBLE PRECISION DEFAULT 20, " +
                "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ")";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("✓ Settings table initialized successfully");
        }
    }

    /**
     * Get default profit margin setting from database
     */
    public static double getDefaultProfitMargin() {
        String sql = "SELECT default_margin FROM profit_margin_settings WHERE setting_name='default_profit_margin' LIMIT 1";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getDouble("default_margin");
            }
        } catch (SQLException e) {
            System.err.println("ERROR: Failed to load profit margin from database: " + e.getMessage());
        }
        return 20.0; // Default fallback
    }

    /**
     * Set default profit margin in database
     */
    public static void setDefaultProfitMargin(double margin) {
        try (Connection conn = getConnection()) {
            // Try to update first
            String updateSql = "UPDATE profit_margin_settings SET default_margin=?, updated_at=CURRENT_TIMESTAMP WHERE setting_name='default_profit_margin'";
            try (PreparedStatement ps = conn.prepareStatement(updateSql)) {
                ps.setDouble(1, margin);
                int updated = ps.executeUpdate();
                
                // If no row was updated, insert new one
                if (updated == 0) {
                    String insertSql = "INSERT INTO profit_margin_settings (setting_name, default_margin) VALUES ('default_profit_margin', ?)";
                    try (PreparedStatement insertPs = conn.prepareStatement(insertSql)) {
                        insertPs.setDouble(1, margin);
                        insertPs.executeUpdate();
                    }
                }
                System.out.println("✓ Profit margin updated to: " + margin + "%");
            }
        } catch (SQLException e) {
            System.err.println("ERROR: Failed to save profit margin to database: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
