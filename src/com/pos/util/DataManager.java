package com.pos.util;

import com.pos.model.Item;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class DataManager {
    private static final String FILE_NAME = "items.csv";
    private static List<Item> items = new ArrayList<>();

    static {
        loadItems();
        if (items.isEmpty()) {
            // Dummy data
            items.add(new Item("BRG001", "Indomie Goreng", 3500, 100));
            items.add(new Item("BRG002", "Aqua 600ml", 4000, 50));
            items.add(new Item("BRG003", "Teh Botol Sosro", 5500, 75));
            items.add(new Item("BRG004", "Biskuit Roma Kelapa", 6000, 60));
            items.add(new Item("BRG005", "Susu Ultra Milk Coklat", 8500, 40));
            saveItems();
        }
    }

    public static List<Item> getAllItems() {
        return items;
    }

    public static void addItem(Item item) {
        items.add(item);
        saveItems();
    }

    public static void updateItem(String code, Item newItem) {
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).getCode().equals(code)) {
                items.set(i, newItem);
                break;
            }
        }
        saveItems();
    }

    public static void deleteItem(String code) {
        items.removeIf(item -> item.getCode().equals(code));
        saveItems();
    }

    public static Item getItemByCode(String code) {
        for (Item item : items) {
            if (item.getCode().equalsIgnoreCase(code)) {
                return item;
            }
        }
        return null;
    }

    private static void loadItems() {
        File file = new File(FILE_NAME);
        if (!file.exists())
            return;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            items.clear();
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 4) {
                    items.add(new Item(parts[0], parts[1], Double.parseDouble(parts[2]), Integer.parseInt(parts[3])));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void saveItems() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_NAME))) {
            for (Item item : items) {
                bw.write(item.getCode() + "," + item.getName() + "," + item.getPrice() + "," + item.getStock());
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
