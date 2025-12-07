package com.pos.view;

import com.pos.util.GradientButton;
import com.pos.util.SolidButton;
import com.pos.util.Style;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainFrame extends JFrame {

    public MainFrame() {
        setTitle("Aplikasi Kasir Sederhana");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
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

        add(menuPanel, BorderLayout.CENTER);

        // Footer
        JPanel footerPanel = new JPanel();
        footerPanel.setBackground(Color.WHITE);
        footerPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JButton btnExit = new SolidButton("❌ Keluar Aplikasi", Style.DANGER_COLOR);
        btnExit.setPreferredSize(new Dimension(200, 40));
        btnExit.addActionListener(e -> System.exit(0));

        footerPanel.add(btnExit);
        add(footerPanel, BorderLayout.SOUTH);
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
