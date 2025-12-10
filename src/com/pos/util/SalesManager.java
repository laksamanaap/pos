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
 * SalesManager supports two modes:
 * - CSV mode (default): uses sales.csv and sales_details.csv for persistence
 * - DB mode: when `db.properties` contains `useDb=true`, uses PostgreSQL via JDBC
 */
public class SalesManager {
    private static final String SALES_FILE = "sales.csv";
    private static final String SALES_DETAILS_FILE = "sales_details.csv";

    // DB configuration
    private static boolean useDb = false;
    private static String dbUrl;
    private static String dbUser;
    private static String dbPassword;

    static {
        loadConfig();
        if (useDb) {
            try {
                Class.forName("org.postgresql.Driver");
            } catch (ClassNotFoundException e) {
                System.err.println("WARNING: PostgreSQL JDBC driver not found. Using CSV mode.");
                useDb = false;
            }
        }
    }

    private static void loadConfig() {
        File cfg = new File("db.properties");
        Properties p = new Properties();
        if (cfg.exists()) {
            try (FileInputStream fis = new FileInputStream(cfg)) {
                p.load(fis);
                useDb = Boolean.parseBoolean(p.getProperty("useDb", "false"));
                dbUrl = p.getProperty("url", "");
                dbUser = p.getProperty("user", "");
                dbPassword = p.getProperty("password", "");
            } catch (IOException e) {
                System.err.println("Failed to load db.properties: " + e.getMessage());
            }
        }
    }

    private static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(dbUrl, dbUser, dbPassword);
    }

    public static void saveTransaction(String transactionId, double totalAmount) {
        if (useDb) {
            saveTransactionToDb(transactionId, totalAmount);
        } else {
            saveTransactionToCsv(transactionId, totalAmount);
        }
    }

    private static void saveTransactionToDb(String transactionId, double totalAmount) {
        String sql = "INSERT INTO sales (transaction_id, transaction_date, total_amount) VALUES (?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, transactionId);
            stmt.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
            stmt.setDouble(3, totalAmount);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Failed to save transaction to DB: " + e.getMessage());
            // Fallback to CSV
            saveTransactionToCsv(transactionId, totalAmount);
        }
    }

    private static void saveTransactionToCsv(String transactionId, double totalAmount) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(SALES_FILE, true))) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String date = sdf.format(new Date());
            bw.write(transactionId + "," + date + "," + totalAmount);
            bw.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveTransactionDetails(List<SalesDetail> details) {
        if (useDb) {
            saveTransactionDetailsToDb(details);
        } else {
            saveTransactionDetailsToCsv(details);
        }
    }

    private static void saveTransactionDetailsToDb(List<SalesDetail> details) {
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
        } catch (SQLException e) {
            System.err.println("Failed to save transaction details to DB: " + e.getMessage());
            // Fallback to CSV
            saveTransactionDetailsToCsv(details);
        }
    }

    private static void saveTransactionDetailsToCsv(List<SalesDetail> details) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(SALES_DETAILS_FILE, true))) {
            for (SalesDetail detail : details) {
                bw.write(detail.getTransactionId() + "," +
                        detail.getItemCode() + "," +
                        detail.getQuantity() + "," +
                        detail.getPurchasePrice() + "," +
                        detail.getSellingPrice() + "," +
                        detail.getDate());
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<String[]> getSalesHistory() {
        if (useDb) {
            return getSalesHistoryFromDb();
        } else {
            return getSalesHistoryFromCsv();
        }
    }

    private static List<String[]> getSalesHistoryFromDb() {
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
            System.err.println("Failed to load sales history from DB: " + e.getMessage());
            return getSalesHistoryFromCsv();
        }
        return history;
    }

    private static List<String[]> getSalesHistoryFromCsv() {
        List<String[]> history = new ArrayList<>();
        File file = new File(SALES_FILE);
        if (!file.exists())
            return history;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",", 3);
                if (parts.length == 3) {
                    history.add(parts);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return history;
    }

    public static List<SalesDetail> getAllSalesDetails() {
        if (useDb) {
            return getAllSalesDetailsFromDb();
        } else {
            return getAllSalesDetailsFromCsv();
        }
    }

    private static List<SalesDetail> getAllSalesDetailsFromDb() {
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
            System.err.println("Failed to load sales details from DB: " + e.getMessage());
            return getAllSalesDetailsFromCsv();
        }
        return details;
    }

    private static List<SalesDetail> getAllSalesDetailsFromCsv() {
        List<SalesDetail> details = new ArrayList<>();
        File file = new File(SALES_DETAILS_FILE);
        if (!file.exists())
            return details;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 6) {
                    details.add(new SalesDetail(
                            parts[0],
                            parts[1],
                            Integer.parseInt(parts[2]),
                            Double.parseDouble(parts[3]),
                            Double.parseDouble(parts[4]),
                            parts[5]));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return details;
    }
}
