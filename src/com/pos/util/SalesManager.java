package com.pos.util;

import com.pos.model.SalesDetail;
import java.io.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

/**
 * SalesManager menggunakan PostgreSQL untuk penyimpanan data transaksi penjualan.
 * Memerlukan file db.properties dengan konfigurasi database.
 */
public class SalesManager {
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
            System.err.println("ERROR: Failed to initialize sales database: " + e.getMessage());
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

    private static void initializeDatabase() throws SQLException {
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            // Create sales table
            stmt.execute("CREATE TABLE IF NOT EXISTS sales (" +
                    "transaction_id VARCHAR(64) PRIMARY KEY, " +
                    "transaction_date TIMESTAMP NOT NULL, " +
                    "total_amount DOUBLE PRECISION NOT NULL, " +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                    ")");
            
            // Create sales_details table
            stmt.execute("CREATE TABLE IF NOT EXISTS sales_details (" +
                    "id SERIAL PRIMARY KEY, " +
                    "transaction_id VARCHAR(64) NOT NULL, " +
                    "item_code VARCHAR(64) NOT NULL, " +
                    "quantity INTEGER NOT NULL, " +
                    "purchase_price DOUBLE PRECISION NOT NULL, " +
                    "selling_price DOUBLE PRECISION NOT NULL, " +
                    "sales_date VARCHAR(50), " +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                    "FOREIGN KEY (transaction_id) REFERENCES sales(transaction_id) ON DELETE CASCADE, " +
                    "FOREIGN KEY (item_code) REFERENCES items(code) ON DELETE RESTRICT" +
                    ")");
            
            System.out.println("✓ Sales tables initialized successfully");
        }
    }

    public static void saveTransaction(String transactionId, double totalAmount) {
        String sql = "INSERT INTO sales (transaction_id, transaction_date, total_amount) VALUES (?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, transactionId);
            stmt.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
            stmt.setDouble(3, totalAmount);
            stmt.executeUpdate();
            System.out.println("✓ Transaction saved: " + transactionId);
        } catch (SQLException e) {
            System.err.println("ERROR: Failed to save transaction: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void saveTransactionDetails(List<SalesDetail> details) {
        String sql = "INSERT INTO sales_details (transaction_id, item_code, quantity, purchase_price, selling_price, sales_date) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (SalesDetail detail : details) {
                stmt.setString(1, detail.getTransactionId());
                stmt.setString(2, detail.getItemCode());
                stmt.setInt(3, detail.getQuantity());
                stmt.setDouble(4, detail.getPurchasePrice());
                stmt.setDouble(5, detail.getSellingPrice());
                stmt.setString(6, detail.getDate());
                stmt.addBatch();
            }
            stmt.executeBatch();
            System.out.println("✓ " + details.size() + " transaction details saved");
        } catch (SQLException e) {
            System.err.println("ERROR: Failed to save transaction details: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static List<String[]> getSalesHistory() {
        List<String[]> history = new ArrayList<>();
        String sql = "SELECT transaction_id, transaction_date, total_amount FROM sales ORDER BY transaction_date DESC";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String[] row = new String[3];
                row[0] = rs.getString("transaction_id");
                row[1] = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(rs.getTimestamp("transaction_date"));
                row[2] = String.valueOf(rs.getDouble("total_amount"));
                history.add(row);
            }
        } catch (SQLException e) {
            System.err.println("ERROR: Failed to load sales history: " + e.getMessage());
            e.printStackTrace();
        }
        return history;
    }

    public static List<SalesDetail> getAllSalesDetails() {
        List<SalesDetail> details = new ArrayList<>();
        String sql = "SELECT transaction_id, item_code, quantity, purchase_price, selling_price, sales_date FROM sales_details";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                details.add(new SalesDetail(
                        rs.getString("transaction_id"),
                        rs.getString("item_code"),
                        rs.getInt("quantity"),
                        rs.getDouble("purchase_price"),
                        rs.getDouble("selling_price"),
                        rs.getString("sales_date")));
            }
        } catch (SQLException e) {
            System.err.println("ERROR: Failed to load sales details: " + e.getMessage());
            e.printStackTrace();
        }
        return details;
    }
}
