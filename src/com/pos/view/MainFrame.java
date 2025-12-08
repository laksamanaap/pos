package com.pos.view;

import com.pos.model.Item;
import com.pos.model.SalesDetail;
import com.pos.util.DataManager;
import com.pos.util.GradientButton;
import com.pos.util.SalesManager;
import com.pos.util.SolidButton;
import com.pos.util.Style;
import com.pos.util.UIUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

public class MainFrame extends JFrame {
    private JLabel lblTotalTransactions;
    private JLabel lblTotalProfit;
    private JTextArea txtTopProducts;

    public MainFrame() {
        setTitle("Toko Medan Agam City Oi Oi");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);
        getContentPane().setBackground(Style.BACKGROUND_COLOR);
        setLayout(new BorderLayout());

        // Header with Gradient Background
        JPanel headerPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gradient = new GradientPaint(0, 0, Style.GRADIENT_START, getWidth(), 0, Style.GRADIENT_END);
                g2.setPaint(gradient);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        headerPanel.setBorder(new EmptyBorder(25, 20, 20, 20));
        headerPanel.setOpaque(false);

        JLabel titleLabel = new JLabel("Toko Medan Agam City Oi Oi", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);

        JLabel subtitleLabel = new JLabel("Sistem Kasir Berbasis Java Swing", SwingConstants.CENTER);
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(220, 220, 220));

        JPanel titleContainer = new JPanel(new GridLayout(2, 1));
        titleContainer.setBackground(null);
        titleContainer.setOpaque(false);
        titleContainer.add(titleLabel);
        titleContainer.add(subtitleLabel);

        headerPanel.add(titleContainer, BorderLayout.NORTH);

        // Info Bar with Fresh Background
        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.setBackground(new Color(255, 255, 255));
        infoPanel.setBorder(new EmptyBorder(12, 20, 12, 20));

        SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy");
        JLabel dateLabel = new JLabel("Tanggal: " + sdf.format(new Date()));
        dateLabel.setFont(Style.REGULAR_FONT);
        dateLabel.setForeground(Style.TEXT_COLOR);

        JLabel userLabel = new JLabel("Kasir: Admin");
        userLabel.setFont(Style.REGULAR_FONT);
        userLabel.setForeground(Style.TEXT_COLOR);

        infoPanel.add(dateLabel, BorderLayout.WEST);
        infoPanel.add(userLabel, BorderLayout.EAST);

        headerPanel.add(infoPanel, BorderLayout.SOUTH);
        add(headerPanel, BorderLayout.NORTH);

        // Center Panel (Stats + Menu)
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(Style.BACKGROUND_COLOR);

        // Stats Panel
        JPanel statsPanel = new JPanel(new GridLayout(1, 3, 20, 0));
        statsPanel.setBackground(Style.BACKGROUND_COLOR);
        statsPanel.setBorder(new EmptyBorder(20, 50, 20, 50));

        statsPanel.add(createStatCard("Total Transaksi", "lblTotalTransactions"));
        statsPanel.add(createStatCard("Total Keuntungan", "lblTotalProfit"));
        statsPanel.add(createTopProductsCard());

        centerPanel.add(statsPanel, BorderLayout.NORTH);

        // Menu Grid
        JPanel menuPanel = new JPanel(new GridLayout(2, 2, 20, 20));
        menuPanel.setBackground(Style.BACKGROUND_COLOR);
        menuPanel.setBorder(new EmptyBorder(30, 50, 30, 50));

        GradientButton btnSales = createMenuButton("Transaksi Penjualan", "cart");
        GradientButton btnItems = createMenuButton("Data Barang", "box");
        GradientButton btnReport = createMenuButton("Laporan Penjualan", "report");
        GradientButton btnSettings = createMenuButton("Pengaturan", "user");

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
        JPanel card = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Style.SURFACE_COLOR);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.setColor(new Color(224, 224, 224, 100));
                g2.setStroke(new BasicStroke(1.0f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(20, 15, 20, 15));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblTitle.setForeground(Style.TEXT_SECONDARY);

        JLabel lblValue = new JLabel("0");
        lblValue.setFont(new Font("Segoe UI", Font.BOLD, 28));
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
        JPanel card = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Style.SURFACE_COLOR);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.setColor(new Color(224, 224, 224, 100));
                g2.setStroke(new BasicStroke(1.0f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(15, 15, 15, 15));

        JLabel lblTitle = new JLabel("Produk Terlaris");
        lblTitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblTitle.setForeground(Style.TEXT_SECONDARY);

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

    private GradientButton createMenuButton(String text, String iconType) {
        GradientButton btn = new GradientButton(text);
        btn.setFont(Style.SUBHEADER_FONT);
        btn.setIcon(UIUtils.createModernIcon(iconType, Style.BACKGROUND_COLOR, 32));
        btn.setIconTextGap(15);
        btn.setHorizontalAlignment(SwingConstants.CENTER);
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
