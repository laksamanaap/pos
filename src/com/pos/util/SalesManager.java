package com.pos.util;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SalesManager {
    private static final String SALES_FILE = "sales.csv";

    public static void saveTransaction(String transactionId, double totalAmount) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(SALES_FILE, true))) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String date = sdf.format(new Date());
            bw.write(transactionId + "," + date + "," + totalAmount);
            bw.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<String[]> getSalesHistory() {
        List<String[]> history = new ArrayList<>();
        File file = new File(SALES_FILE);
        if (!file.exists())
            return history;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 3) {
                    history.add(parts);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return history;
    }
}
