package com.pos.util;

import com.pos.model.Item;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * DataManager supports two modes:
 * - CSV mode (default): uses items.csv for persistence (backwards compatible)
 * - DB mode: when `db.properties` contains `useDb=true`, uses PostgreSQL via JDBC.
 *
 * To enable DB mode, create `db.properties` in the project root with:
 * useDb=true
 * url=jdbc:postgresql://localhost:5432/yourdb
 * user=youruser
 * password=yourpassword
 *
 * NOTE: When running in DB mode you must include the PostgreSQL JDBC driver on the classpath.
 */
public class DataManager {
    private static final String FILE_NAME = "items.csv";
    private static List<Item> items = new ArrayList<>();

    // DB configuration
    private static boolean useDb = false;
    private static String dbUrl;
    private static String dbUser;
    private static String dbPassword;

    static {
        loadConfig();
        if (useDb) {
            try {
                // Load PostgreSQL JDBC driver
                Class.forName("org.postgresql.Driver");
            } catch (ClassNotFoundException e) {
                System.err.println("WARNING: PostgreSQL JDBC driver not found on classpath. Falling back to CSV mode.");
                System.err.println("To use database mode, download postgresql-XX.X.X.jar and add to classpath.");
                useDb = false;
            }
            
            if (useDb) {
                try {
                    initializeDatabase();
                    // load items from DB into memory for compatibility with existing code
                    items = loadItemsFromDb();
                } catch (Exception e) {
                    e.printStackTrace();
                    System.err.println("Failed to connect to database. Falling back to CSV mode.");
                    useDb = false;
                    loadItemsFromCsv();
                }
            } else {
                loadItemsFromCsv();
            }
        } else {
            loadItemsFromCsv();
            if (items.isEmpty()) {
                // Dummy data
                items.add(new Item("BRG001", "Indomie Goreng", 2500, 3500, 100));
                items.add(new Item("BRG002", "Aqua 600ml", 3000, 4000, 50));
                items.add(new Item("BRG003", "Teh Botol Sosro", 4000, 5500, 75));
                items.add(new Item("BRG004", "Biskuit Roma Kelapa", 4500, 6000, 60));
                items.add(new Item("BRG005", "Susu Ultra Milk Coklat", 6000, 8500, 40));
                saveItems();
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
                dbUrl = p.getProperty("url");
                dbUser = p.getProperty("user");
                dbPassword = p.getProperty("password");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            // allow environment variables as alternative
            String envUse = System.getenv("POS_USE_DB");
            if (envUse != null && envUse.equalsIgnoreCase("true")) {
                useDb = true;
                dbUrl = System.getenv("POS_DB_URL");
                dbUser = System.getenv("POS_DB_USER");
                dbPassword = System.getenv("POS_DB_PASSWORD");
            }
        }
    }

    private static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(dbUrl, dbUser, dbPassword);
    }

    private static void initializeDatabase() throws SQLException {
        // Create table if not exists
        String sql = "CREATE TABLE IF NOT EXISTS items (" +
                "code VARCHAR(64) PRIMARY KEY, " +
                "name TEXT, " +
                "purchase_price DOUBLE PRECISION, " +
                "selling_price DOUBLE PRECISION, " +
                "stock INTEGER" +
                ")";
        try (Connection c = getConnection(); Statement st = c.createStatement()) {
            st.execute(sql);
        }
    }

    public static List<Item> getAllItems() {
        if (useDb) {
            try {
                return loadItemsFromDb();
            } catch (SQLException e) {
                e.printStackTrace();
                return new ArrayList<>();
            }
        }
        return items;
    }

    public static void addItem(Item item) {
        if (useDb) {
            try (Connection c = getConnection()) {
                String sql = "INSERT INTO items(code,name,purchase_price,selling_price,stock) VALUES(?,?,?,?,?)";
                try (PreparedStatement ps = c.prepareStatement(sql)) {
                    ps.setString(1, item.getCode());
                    ps.setString(2, item.getName());
                    ps.setDouble(3, item.getPurchasePrice());
                    ps.setDouble(4, item.getSellingPrice());
                    ps.setInt(5, item.getStock());
                    ps.executeUpdate();
                }
                // keep memory list in sync
                items = loadItemsFromDb();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return;
        }
        items.add(item);
        saveItems();
    }

    public static void updateItem(String code, Item newItem) {
        if (useDb) {
            try (Connection c = getConnection()) {
                String sql = "UPDATE items SET name=?, purchase_price=?, selling_price=?, stock=? WHERE code=?";
                try (PreparedStatement ps = c.prepareStatement(sql)) {
                    ps.setString(1, newItem.getName());
                    ps.setDouble(2, newItem.getPurchasePrice());
                    ps.setDouble(3, newItem.getSellingPrice());
                    ps.setInt(4, newItem.getStock());
                    ps.setString(5, code);
                    ps.executeUpdate();
                }
                items = loadItemsFromDb();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return;
        }
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).getCode().equals(code)) {
                items.set(i, newItem);
                break;
            }
        }
        saveItems();
    }

    public static void deleteItem(String code) {
        if (useDb) {
            try (Connection c = getConnection()) {
                String sql = "DELETE FROM items WHERE code=?";
                try (PreparedStatement ps = c.prepareStatement(sql)) {
                    ps.setString(1, code);
                    ps.executeUpdate();
                }
                items = loadItemsFromDb();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return;
        }
        items.removeIf(item -> item.getCode().equals(code));
        saveItems();
    }

    public static Item getItemByCode(String code) {
        if (useDb) {
            try (Connection c = getConnection()) {
                String sql = "SELECT code,name,purchase_price,selling_price,stock FROM items WHERE lower(code)=lower(?)";
                try (PreparedStatement ps = c.prepareStatement(sql)) {
                    ps.setString(1, code);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            return new Item(rs.getString("code"), rs.getString("name"), rs.getDouble("purchase_price"), rs.getDouble("selling_price"), rs.getInt("stock"));
                        }
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        }
        for (Item item : items) {
            if (item.getCode().equalsIgnoreCase(code)) {
                return item;
            }
        }
        return null;
    }

    private static List<Item> loadItemsFromDb() throws SQLException {
        List<Item> out = new ArrayList<>();
        String sql = "SELECT code,name,purchase_price,selling_price,stock FROM items ORDER BY code";
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                out.add(new Item(rs.getString("code"), rs.getString("name"), rs.getDouble("purchase_price"), rs.getDouble("selling_price"), rs.getInt("stock")));
            }
        }
        return out;
    }

    private static void loadItemsFromCsv() {
        File file = new File(FILE_NAME);
        if (!file.exists())
            return;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            items.clear();
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 5) {
                    items.add(new Item(parts[0], parts[1], Double.parseDouble(parts[2]), Double.parseDouble(parts[3]),
                            Integer.parseInt(parts[4])));
                } else if (parts.length == 4) {
                    // Backward compatibility: assume purchasePrice is 80% of sellingPrice
                    double sellingPrice = Double.parseDouble(parts[2]);
                    items.add(
                            new Item(parts[0], parts[1], sellingPrice * 0.8, sellingPrice, Integer.parseInt(parts[3])));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void saveItems() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_NAME))) {
            for (Item item : items) {
                bw.write(item.getCode() + "," + item.getName() + "," + item.getPurchasePrice() + ","
                        + item.getSellingPrice() + "," + item.getStock());
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
