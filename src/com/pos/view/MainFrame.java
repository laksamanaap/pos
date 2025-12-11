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

        // Header with subtle gradient
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
        headerPanel.setBorder(new EmptyBorder(30, 40, 30, 40));
        headerPanel.setOpaque(false);

        JLabel titleLabel = new JLabel("Toko Medan Agam City", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        titleLabel.setForeground(Color.WHITE);

        JLabel subtitleLabel = new JLabel("Sistem Kasir Modern", SwingConstants.CENTER);
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitleLabel.setForeground(new Color(235, 240, 242));

        JPanel titleContainer = new JPanel(new GridLayout(2, 1, 0, 6));
        titleContainer.setBackground(null);
        titleContainer.setOpaque(false);
        titleContainer.add(titleLabel);
        titleContainer.add(subtitleLabel);

        headerPanel.add(titleContainer, BorderLayout.NORTH);

        // Info Bar - minimal style
        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.setBackground(Style.SURFACE_COLOR);
        infoPanel.setBorder(new EmptyBorder(14, 40, 14, 40));

        SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy");
        JLabel dateLabel = new JLabel("Tanggal: " + sdf.format(new Date()));
        dateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        dateLabel.setForeground(Style.TEXT_SECONDARY);

        JLabel userLabel = new JLabel("Kasir: Admin");
        userLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        userLabel.setForeground(Style.TEXT_SECONDARY);

        infoPanel.add(dateLabel, BorderLayout.WEST);
        infoPanel.add(userLabel, BorderLayout.EAST);

        headerPanel.add(infoPanel, BorderLayout.SOUTH);
        add(headerPanel, BorderLayout.NORTH);

        // Center Panel (Stats + Menu)
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(Style.BACKGROUND_COLOR);

        // Stats Panel - cleaner spacing
        JPanel statsPanel = new JPanel(new GridLayout(1, 3, 24, 0));
        statsPanel.setBackground(Style.BACKGROUND_COLOR);
        statsPanel.setBorder(new EmptyBorder(28, 60, 28, 60));

        statsPanel.add(createStatCard("Total Transaksi", "lblTotalTransactions"));
        statsPanel.add(createStatCard("Total Keuntungan", "lblTotalProfit"));
        statsPanel.add(createTopProductsCard());

        centerPanel.add(statsPanel, BorderLayout.NORTH);

        // Menu Grid - better spacing and sizing
        JPanel menuPanel = new JPanel(new GridLayout(2, 2, 24, 24));
        menuPanel.setBackground(Style.BACKGROUND_COLOR);
        menuPanel.setBorder(new EmptyBorder(20, 60, 20, 60));

        GradientButton btnSales = createMenuButton("Transaksi Penjualan", "cart");
        GradientButton btnItems = createMenuButton("Data Barang", "box");
        GradientButton btnReport = createMenuButton("Laporan Penjualan", "report");
        GradientButton btnSettings = createMenuButton("Pengaturan", "user");

        // Add Action Listeners
        btnSales.addActionListener(e -> openSalesTransaction());
        btnItems.addActionListener(e -> openItemData());
        btnReport.addActionListener(e -> openSalesReport());
        btnSettings.addActionListener(e -> openSettings());

        menuPanel.add(btnSales);
        menuPanel.add(btnItems);
        menuPanel.add(btnReport);
        menuPanel.add(btnSettings);

        centerPanel.add(menuPanel, BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);

        // Footer - minimal style
        JPanel footerPanel = new JPanel();
        footerPanel.setBackground(Style.SURFACE_COLOR);
        footerPanel.setBorder(new EmptyBorder(16, 20, 16, 20));

        JButton btnExit = new SolidButton("Keluar", Style.DANGER_COLOR);
        btnExit.setPreferredSize(new Dimension(160, 40));
        btnExit.setFont(new Font("Segoe UI", Font.PLAIN, 13));
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
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                // Subtle shadow
                g2.setColor(new Color(0, 0, 0, 8));
                g2.fillRoundRect(2, 2, getWidth()-2, getHeight()-2, 14, 14);
                g2.setColor(Style.BORDER_COLOR);
                g2.setStroke(new BasicStroke(0.8f));

                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(24, 20, 24, 20));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblTitle.setForeground(Style.TEXT_SECONDARY);

        JLabel lblValue = new JLabel("0");
        lblValue.setFont(new Font("Segoe UI", Font.BOLD, 32));
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

    private void openSettings() {
        new SettingsFrame(this).setVisible(true);
        this.setVisible(false);
    }
}
