package com.pos.util;

import com.pos.model.Item;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * DataManager menggunakan PostgreSQL untuk penyimpanan data item.
 * Memerlukan file db.properties dengan konfigurasi database:
 * 
 * url=jdbc:postgresql://localhost:5432/posdb
 * user=postgres
 * password=yourpassword
 */
public class DataManager {
    private static String dbUrl;
    private static String dbUser;
    private static String dbPassword;

    static {
        loadConfig();
        try {
            // Load PostgreSQL JDBC driver
            Class.forName("org.postgresql.Driver");
            initializeDatabase();
        } catch (ClassNotFoundException e) {
            System.err.println("ERROR: PostgreSQL JDBC driver not found. Please add postgresql JAR to classpath.");
            System.exit(1);
        } catch (SQLException e) {
            System.err.println("ERROR: Failed to initialize database: " + e.getMessage());
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
        try (Connection c = getConnection(); Statement st = c.createStatement()) {
            // Create items table if not exists
            String createTableSql = "CREATE TABLE IF NOT EXISTS items (" +
                    "code VARCHAR(64) PRIMARY KEY, " +
                    "name TEXT NOT NULL, " +
                    "purchase_price DOUBLE PRECISION NOT NULL, " +
                    "selling_price DOUBLE PRECISION NOT NULL, " +
                    "profit_margin DOUBLE PRECISION DEFAULT 20, " +
                    "stock INTEGER NOT NULL DEFAULT 0, " +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                    "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                    ")";
            st.execute(createTableSql);
            
            // Add profit_margin column if it doesn't exist (for existing tables)
            try {
                st.execute("ALTER TABLE items ADD COLUMN profit_margin DOUBLE PRECISION DEFAULT 20");
                System.out.println("✓ Added profit_margin column to existing items table");
            } catch (SQLException e) {
                // Column already exists, ignore
            }
            
            System.out.println("✓ Database initialized successfully");
        }
    }

    public static List<Item> getAllItems() {
        List<Item> items = new ArrayList<>();
        String sql = "SELECT code,name,purchase_price,selling_price,profit_margin,stock FROM items ORDER BY code";
        try (Connection c = getConnection(); 
             PreparedStatement ps = c.prepareStatement(sql); 
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                items.add(new Item(
                    rs.getString("code"), 
                    rs.getString("name"), 
                    rs.getDouble("purchase_price"), 
                    rs.getDouble("selling_price"), 
                    rs.getDouble("profit_margin"), 
                    rs.getInt("stock")
                ));
            }
        } catch (SQLException e) {
            System.err.println("ERROR: Failed to load items from database: " + e.getMessage());
            e.printStackTrace();
        }
        return items;
    }

    public static void addItem(Item item) {
        String sql = "INSERT INTO items(code,name,purchase_price,selling_price,profit_margin,stock) VALUES(?,?,?,?,?,?)";
        try (Connection c = getConnection(); 
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, item.getCode());
            ps.setString(2, item.getName());
            ps.setDouble(3, item.getPurchasePrice());
            ps.setDouble(4, item.getSellingPrice());
            ps.setDouble(5, item.getProfitMargin());
            ps.setInt(6, item.getStock());
            ps.executeUpdate();
            System.out.println("✓ Item added: " + item.getCode());
        } catch (SQLException e) {
            System.err.println("ERROR: Failed to add item: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void updateItem(String code, Item newItem) {
        String sql = "UPDATE items SET name=?, purchase_price=?, selling_price=?, profit_margin=?, stock=?, updated_at=CURRENT_TIMESTAMP WHERE code=?";
        try (Connection c = getConnection(); 
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, newItem.getName());
            ps.setDouble(2, newItem.getPurchasePrice());
            ps.setDouble(3, newItem.getSellingPrice());
            ps.setDouble(4, newItem.getProfitMargin());
            ps.setInt(5, newItem.getStock());
            ps.setString(6, code);
            ps.executeUpdate();
            System.out.println("✓ Item updated: " + code);
        } catch (SQLException e) {
            System.err.println("ERROR: Failed to update item: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void deleteItem(String code) {
        String sql = "DELETE FROM items WHERE code=?";
        try (Connection c = getConnection(); 
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, code);
            ps.executeUpdate();
            System.out.println("✓ Item deleted: " + code);
        } catch (SQLException e) {
            System.err.println("ERROR: Failed to delete item: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static Item getItemByCode(String code) {
        String sql = "SELECT code,name,purchase_price,selling_price,profit_margin,stock FROM items WHERE lower(code)=lower(?)";
        try (Connection c = getConnection(); 
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, code);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Item(
                        rs.getString("code"), 
                        rs.getString("name"), 
                        rs.getDouble("purchase_price"), 
                        rs.getDouble("selling_price"), 
                        rs.getDouble("profit_margin"), 
                        rs.getInt("stock")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("ERROR: Failed to get item: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
}
