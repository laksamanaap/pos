package com.pos.view;

import com.pos.model.Item;
import com.pos.model.SalesDetail;
import com.pos.util.DataManager;
import com.pos.util.GradientButton;
import com.pos.util.SalesManager;
import com.pos.util.SolidButton;
import com.pos.util.Style;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

public class MainFrame extends JFrame {
    private JLabel lblTotalTransactions;
    private JLabel lblTotalProfit;
    private JTextArea txtTopProducts;

    public MainFrame() {
        setTitle("Aplikasi Kasir Sederhana");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700); // Increased width for stats
        setLocationRelativeTo(null);
        getContentPane().setBackground(Color.WHITE);
        setLayout(new BorderLayout());

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(new EmptyBorder(20, 20, 10, 20));

        JLabel titleLabel = new JLabel("APLIKASI KASIR SEDERHANA", SwingConstants.CENTER);
        titleLabel.setFont(Style.HEADER_FONT);
        titleLabel.setForeground(Style.TEXT_COLOR);

        JLabel subtitleLabel = new JLabel("Sistem Kasir Berbasis Java Swing", SwingConstants.CENTER);
        subtitleLabel.setFont(Style.REGULAR_FONT);
        subtitleLabel.setForeground(Color.GRAY);

        JPanel titleContainer = new JPanel(new GridLayout(2, 1));
        titleContainer.setBackground(Color.WHITE);
        titleContainer.add(titleLabel);
        titleContainer.add(subtitleLabel);

        headerPanel.add(titleContainer, BorderLayout.NORTH);

        // Info Bar
        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.setBackground(new Color(248, 249, 250));
        infoPanel.setBorder(new EmptyBorder(10, 20, 10, 20));

        SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy");
        JLabel dateLabel = new JLabel("📅 Tanggal: " + sdf.format(new Date()));
        dateLabel.setFont(Style.REGULAR_FONT);

        JLabel userLabel = new JLabel("👤 Kasir: Admin");
        userLabel.setFont(Style.REGULAR_FONT);

        infoPanel.add(dateLabel, BorderLayout.WEST);
        infoPanel.add(userLabel, BorderLayout.EAST);

        headerPanel.add(infoPanel, BorderLayout.SOUTH);

        add(headerPanel, BorderLayout.NORTH);

        // Center Panel (Stats + Menu)
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(Color.WHITE);

        // Stats Panel
        JPanel statsPanel = new JPanel(new GridLayout(1, 3, 20, 0));
        statsPanel.setBackground(Color.WHITE);
        statsPanel.setBorder(new EmptyBorder(10, 50, 10, 50));

        statsPanel.add(createStatCard("Total Transaksi", "lblTotalTransactions"));
        statsPanel.add(createStatCard("Total Keuntungan", "lblTotalProfit"));
        statsPanel.add(createTopProductsCard());

        centerPanel.add(statsPanel, BorderLayout.NORTH);

        // Menu Grid
        JPanel menuPanel = new JPanel(new GridLayout(2, 2, 20, 20));
        menuPanel.setBackground(Color.WHITE);
        menuPanel.setBorder(new EmptyBorder(30, 50, 30, 50));

        GradientButton btnSales = createMenuButton("Transaksi Penjualan", "🛍️");
        GradientButton btnItems = createMenuButton("Data Barang", "📦");
        GradientButton btnReport = createMenuButton("Laporan Penjualan", "📊");
        GradientButton btnSettings = createMenuButton("Pengaturan", "⚙️");

        // Add Action Listeners
        btnSales.addActionListener(e -> openSalesTransaction());
        btnItems.addActionListener(e -> openItemData());
        btnReport.addActionListener(e -> openSalesReport());
        btnSettings.addActionListener(e -> JOptionPane.showMessageDialog(this, "Fitur Pengaturan belum tersedia."));

        menuPanel.add(btnSales);
        menuPanel.add(btnItems);
        menuPanel.add(btnReport);
        menuPanel.add(btnSettings);

        centerPanel.add(menuPanel, BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);

        // Footer
        JPanel footerPanel = new JPanel();
        footerPanel.setBackground(Color.WHITE);
        footerPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JButton btnExit = new SolidButton("❌ Keluar Aplikasi", Style.DANGER_COLOR);
        btnExit.setPreferredSize(new Dimension(200, 40));
        btnExit.addActionListener(e -> System.exit(0));

        footerPanel.add(btnExit);
        add(footerPanel, BorderLayout.SOUTH);

        refreshStats();
    }

    private JPanel createStatCard(String title, String labelName) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(new Color(240, 248, 255)); // Light Alice Blue
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                new EmptyBorder(15, 15, 15, 15)));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTitle.setForeground(Color.GRAY);

        JLabel lblValue = new JLabel("0");
        lblValue.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblValue.setForeground(Style.PRIMARY_COLOR);

        if (labelName.equals("lblTotalTransactions"))
            lblTotalTransactions = lblValue;
        if (labelName.equals("lblTotalProfit"))
            lblTotalProfit = lblValue;

        card.add(lblTitle, BorderLayout.NORTH);
        card.add(lblValue, BorderLayout.CENTER);

        return card;
    }

    private JPanel createTopProductsCard() {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(new Color(255, 250, 240)); // Floral White
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                new EmptyBorder(15, 15, 15, 15)));

        JLabel lblTitle = new JLabel("Produk Terlaris");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTitle.setForeground(Color.GRAY);

        txtTopProducts = new JTextArea();
        txtTopProducts.setEditable(false);
        txtTopProducts.setOpaque(false);
        txtTopProducts.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtTopProducts.setLineWrap(true);

        card.add(lblTitle, BorderLayout.NORTH);
        card.add(txtTopProducts, BorderLayout.CENTER);

        return card;
    }

    @Override
    public void setVisible(boolean b) {
        if (b)
            refreshStats();
        super.setVisible(b);
    }

    private void refreshStats() {
        List<SalesDetail> details = SalesManager.getAllSalesDetails();

        // 1. Total Transactions
        long totalTrans = SalesManager.getSalesHistory().size(); // Use history for transaction count integrity
        lblTotalTransactions.setText(String.valueOf(totalTrans));

        // 2. Total Profit
        double totalProfit = 0;
        for (SalesDetail d : details) {
            double profitPerItem = (d.getSellingPrice() - d.getPurchasePrice()) * d.getQuantity();
            totalProfit += profitPerItem;
        }
        lblTotalProfit.setText("Rp " + NumberFormat.getNumberInstance(Locale.US).format(totalProfit));

        // 3. Most Sold Products
        Map<String, Integer> productSales = new HashMap<>();
        for (SalesDetail d : details) {
            productSales.put(d.getItemCode(), productSales.getOrDefault(d.getItemCode(), 0) + d.getQuantity());
        }

        // Sort by value descending
        List<Map.Entry<String, Integer>> sortedSales = new ArrayList<>(productSales.entrySet());
        sortedSales.sort((e1, e2) -> e2.getValue().compareTo(e1.getValue()));

        StringBuilder top = new StringBuilder();
        int count = 0;
        for (Map.Entry<String, Integer> entry : sortedSales) {
            if (count >= 3)
                break;
            Item item = DataManager.getItemByCode(entry.getKey());
            String name = (item != null) ? item.getName() : entry.getKey();
            top.append((count + 1) + ". " + name + " (" + entry.getValue() + ")\n");
            count++;
        }

        if (top.length() == 0)
            txtTopProducts.setText("Belum ada data");
        else
            txtTopProducts.setText(top.toString());
    }

    private GradientButton createMenuButton(String text, String icon) {
        GradientButton btn = new GradientButton(
                "<html><center><font size='6'>" + icon + "</font><br><br>" + text + "</center></html>");
        btn.setFont(Style.SUBHEADER_FONT);
        return btn;
    }

    private void openItemData() {
        new ItemDataFrame(this).setVisible(true);
        this.setVisible(false);
    }

    private void openSalesTransaction() {
        new SalesTransactionFrame(this).setVisible(true);
        this.setVisible(false);
    }

    private void openSalesReport() {
        new SalesReportFrame(this).setVisible(true);
        this.setVisible(false);
    }
}
