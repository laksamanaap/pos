package com.pos.view;

import com.pos.model.Item;
import com.pos.model.SalesDetail;
import com.pos.util.DataManager;
import com.pos.util.SalesManager;
import com.pos.util.SolidButton;
import com.pos.util.Style;
import com.pos.util.UIUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SalesTransactionFrame extends JFrame {
    private MainFrame mainFrame;
    private JTextField txtCode, txtPrice, txtQty, txtPay, txtChange;
    private JComboBox<Item> cmbItems;
    private JTable table;
    private DefaultTableModel tableModel;
    private JLabel lblTotal, lblTotalItems;
    private double totalAmount = 0;

    public SalesTransactionFrame(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setTitle("Transaksi Penjualan");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        
        // Inherit fullscreen state and bounds from MainFrame
        setExtendedState(mainFrame.getExtendedState());
        if ((mainFrame.getExtendedState() & JFrame.MAXIMIZED_BOTH) == JFrame.MAXIMIZED_BOTH) {
            // If parent is fullscreen, match its bounds
            setBounds(mainFrame.getBounds());
        } else {
            setLocationRelativeTo(null);
        }
        
        setLayout(new BorderLayout());
        getContentPane().setBackground(Style.BACKGROUND_COLOR);

        // Header
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
        headerPanel.setOpaque(false);
        headerPanel.setBorder(new EmptyBorder(24, 40, 24, 40));

        JLabel titleLabel = new JLabel("Transaksi Penjualan");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);

        SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy");
        JLabel dateLabel = new JLabel("Tanggal: " + sdf.format(new Date()));
        dateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        dateLabel.setForeground(new Color(235, 240, 242));

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(dateLabel, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // Content
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        contentPanel.setBackground(Style.BACKGROUND_COLOR);;

        // Input Panel
        JPanel inputPanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Style.SURFACE_COLOR);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.setColor(Style.BORDER_COLOR);
                g2.setStroke(new BasicStroke(1.0f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
            }
        };
        inputPanel.setOpaque(false);
        inputPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEmptyBorder(), "Input Barang",
                javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                javax.swing.border.TitledBorder.DEFAULT_POSITION,
                Style.BOLD_FONT, Style.TEXT_COLOR));
        inputPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Row 1
        gbc.gridx = 0;
        gbc.gridy = 0;
        inputPanel.add(new JLabel("Kode Barang"), gbc);
        gbc.gridx = 1;
        inputPanel.add(new JLabel("Nama Barang"), gbc);
        gbc.gridx = 2;
        inputPanel.add(new JLabel("Harga"), gbc);
        gbc.gridx = 3;
        inputPanel.add(new JLabel("Jumlah"), gbc);

        // Row 2
        gbc.gridx = 0;
        gbc.gridy = 1;
        txtCode = new JTextField(10);
        inputPanel.add(txtCode, gbc);

        gbc.gridx = 1;
        cmbItems = new JComboBox<>();
        cmbItems.addItem(new Item("", "-- Pilih Barang --", 0, 0, 0));
        for (Item item : DataManager.getAllItems()) {
            cmbItems.addItem(item);
        }
        inputPanel.add(cmbItems, gbc);

        gbc.gridx = 2;
        txtPrice = new JTextField(10);
        txtPrice.setEditable(false);
        inputPanel.add(txtPrice, gbc);

        gbc.gridx = 3;
        txtQty = new JTextField("1", 5);
        inputPanel.add(txtQty, gbc);

        // Add Button
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 4;
        JButton btnAdd = new SolidButton("Tambah ke Keranjang", Style.SUCCESS_COLOR);
        inputPanel.add(btnAdd, gbc);

        contentPanel.add(inputPanel);
        contentPanel.add(Box.createVerticalStrut(10));

        // Table
        String[] columns = { "No", "Kode", "Nama Barang", "Harga", "Jumlah", "Subtotal" };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        UIUtils.customizeTable(table);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(800, 200));
        contentPanel.add(scrollPane);
        contentPanel.add(Box.createVerticalStrut(10));

        // Payment Panel
        JPanel paymentPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        paymentPanel.setBackground(Style.BACKGROUND_COLOR);

        // Left: Payment Input
        JPanel payInputPanel = new JPanel(new GridLayout(4, 2, 10, 10)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Style.SURFACE_COLOR);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.setColor(Style.BORDER_COLOR);
                g2.setStroke(new BasicStroke(1.0f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
            }
        };
        payInputPanel.setOpaque(false);
        payInputPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEmptyBorder(), "Pembayaran",
                javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                javax.swing.border.TitledBorder.DEFAULT_POSITION,
                Style.BOLD_FONT, Style.TEXT_COLOR));
        payInputPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        payInputPanel.add(new JLabel("Jumlah Bayar (Rp):"));
        txtPay = new JTextField();
        txtPay.setBackground(Style.SURFACE_COLOR);
        payInputPanel.add(txtPay);

        payInputPanel.add(new JLabel("Kembalian:"));
        txtChange = new JTextField();
        txtChange.setEditable(false);
        txtChange.setForeground(Style.SUCCESS_COLOR);
        txtChange.setFont(Style.BOLD_FONT);
        txtChange.setBackground(Style.SURFACE_COLOR);
        payInputPanel.add(txtChange);

        // Right: Summary
        JPanel summaryPanel = new JPanel(new GridLayout(4, 2, 10, 10)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Style.SURFACE_COLOR);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.setColor(Style.BORDER_COLOR);
                g2.setStroke(new BasicStroke(1.0f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
            }
        };
        summaryPanel.setOpaque(false);
        summaryPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEmptyBorder(), "Ringkasan",
                javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                javax.swing.border.TitledBorder.DEFAULT_POSITION,
                Style.BOLD_FONT, Style.TEXT_COLOR));
        summaryPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        summaryPanel.add(new JLabel("Jumlah Item:"));
        lblTotalItems = new JLabel("0 Item");
        summaryPanel.add(lblTotalItems);

        summaryPanel.add(new JLabel("TOTAL BAYAR:"));
        lblTotal = new JLabel("Rp 0");
        lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTotal.setForeground(Style.PRIMARY_COLOR);
        summaryPanel.add(lblTotal);

        paymentPanel.add(payInputPanel);
        paymentPanel.add(summaryPanel);

        contentPanel.add(paymentPanel);

        add(contentPanel, BorderLayout.CENTER);

        // Footer
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footerPanel.setBackground(Style.BACKGROUND_COLOR);

        JButton btnProcess = new SolidButton("Proses Pembayaran", Style.PRIMARY_COLOR);
        btnProcess.setPreferredSize(new Dimension(200, 40));

        JButton btnCancel = new SolidButton("Batal", Style.DANGER_COLOR);

        JButton btnBack = new SolidButton("Kembali", Color.GRAY);
        btnBack.addActionListener(e -> {
            mainFrame.setVisible(true);
            // Restore parent's fullscreen state if it was fullscreen
            if ((getExtendedState() & JFrame.MAXIMIZED_BOTH) == JFrame.MAXIMIZED_BOTH) {
                mainFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
            }
            dispose();
        });

        footerPanel.add(btnProcess);
        footerPanel.add(btnCancel);
        footerPanel.add(btnBack);

        add(footerPanel, BorderLayout.SOUTH);

        // Listeners
        cmbItems.addActionListener(e -> {
            Item item = (Item) cmbItems.getSelectedItem();
            if (item != null && !item.getCode().isEmpty()) {
                txtCode.setText(item.getCode());
                txtPrice.setText(String.valueOf(item.getSellingPrice()));
            }
        });

        txtCode.addActionListener(e -> {
            String code = txtCode.getText();
            Item item = DataManager.getItemByCode(code);
            if (item != null) {
                cmbItems.setSelectedItem(item);
                txtQty.requestFocus();
            } else {
                UIUtils.showInfo(this, "Barang tidak ditemukan", "Barang tidak ditemukan!");
            }
        });

        btnAdd.addActionListener(e -> addToCart());

        txtPay.addActionListener(e -> calculateChange());

        btnProcess.addActionListener(e -> processPayment());

        btnCancel.addActionListener(e -> {
            tableModel.setRowCount(0);
            updateSummary();
            txtPay.setText("");
            txtChange.setText("");
        });
    }

    private void addToCart() {
        try {
            Item item = (Item) cmbItems.getSelectedItem();
            if (item == null || item.getCode().isEmpty()) {
                UIUtils.showInfo(this, "Peringatan", "Pilih barang terlebih dahulu!");
                return;
            }

            int qty = Integer.parseInt(txtQty.getText());
            if (qty <= 0) {
                UIUtils.showWarning(this, "Peringatan", "Jumlah harus lebih dari 0!");
                return;
            }

            if (qty > item.getStock()) {
                UIUtils.showWarning(this, "Stok", "Stok tidak mencukupi! Stok: " + item.getStock());
                return;
            }

            double subtotal = item.getSellingPrice() * qty;

            tableModel.addRow(new Object[] {
                    tableModel.getRowCount() + 1,
                    item.getCode(),
                    item.getName(),
                    item.getSellingPrice(),
                    qty,
                    subtotal
            });

            updateSummary();
            txtCode.setText("");
            txtQty.setText("1");
            cmbItems.setSelectedIndex(0);
            txtPrice.setText("");

        } catch (NumberFormatException e) {
            UIUtils.showWarning(this, "Input Tidak Valid", "Input jumlah tidak valid!");
        }
    }

    private void updateSummary() {
        double total = 0;
        int items = 0;
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            total += (double) tableModel.getValueAt(i, 5);
            items += (int) tableModel.getValueAt(i, 4);
        }
        totalAmount = total;
        lblTotal.setText("Rp " + NumberFormat.getNumberInstance(Locale.US).format(total));
        lblTotalItems.setText(items + " Item");
    }

    private void calculateChange() {
        try {
            double pay = Double.parseDouble(txtPay.getText());
            double change = pay - totalAmount;
            txtChange.setText("Rp " + NumberFormat.getNumberInstance(Locale.US).format(change));
        } catch (NumberFormatException e) {
            // Ignore
        }
    }

    private void processPayment() {
        calculateChange();
        try {
            double pay = Double.parseDouble(txtPay.getText());
            if (pay < totalAmount) {
                UIUtils.showWarning(this, "Pembayaran", "Uang pembayaran kurang!");
                return;
            }

            // Save Transaction
            String transactionId = "TRX-" + System.currentTimeMillis();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); // For date column
            String dateStr = sdf.format(new Date());

            List<SalesDetail> salesDetails = new ArrayList<>();

            // Reduce stock and collect details
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                String code = (String) tableModel.getValueAt(i, 1);
                int qty = (int) tableModel.getValueAt(i, 4);
                Item item = DataManager.getItemByCode(code);
                if (item != null) {
                    item.setStock(item.getStock() - qty);
                    DataManager.updateItem(code, item);

                    // Add details
                    salesDetails.add(new SalesDetail(
                            transactionId,
                            item.getCode(),
                            qty,
                            item.getPurchasePrice(),
                            item.getSellingPrice(),
                            dateStr));
                }
            }

            SalesManager.saveTransaction(transactionId, totalAmount);
            SalesManager.saveTransactionDetails(salesDetails);

            // Generate Receipt
            StringBuilder receipt = new StringBuilder();
            SimpleDateFormat sdfReceipt = new SimpleDateFormat("dd MMMM yyyy HH:mm:ss");
            String dateReceiptStr = sdfReceipt.format(new Date());

            // Header
            receipt.append("             TOKO MEDAN LOUIS AGAM\n");
            receipt.append("               Jl. Soehat No. 19\n");
            receipt.append("           Malang, Jawa Timur 65144\n");
            receipt.append("             Telp: (0341) 123-4567\n");
            receipt.append("------------------------------------------------\n");

            // Transaction Details
            receipt.append("No. Transaksi: " + transactionId + "\n");
            receipt.append("Tanggal      : " + dateReceiptStr + "\n");
            receipt.append("Kasir        : Admin\n");
            receipt.append("------------------------------------------------\n");

            // Items Header
            receipt.append(String.format("%-18s %-5s %-10s %-10s\n", "Nama Barang", "Qty", "Harga", "Subtotal"));
            receipt.append("\n");

            // Items
            int totalQty = 0;
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                String name = (String) tableModel.getValueAt(i, 2);
                if (name.length() > 18)
                    name = name.substring(0, 18); // Truncate if too long
                int qty = (int) tableModel.getValueAt(i, 4);
                double price = (double) tableModel.getValueAt(i, 3);
                double sub = (double) tableModel.getValueAt(i, 5);
                totalQty += qty;

                receipt.append(String.format("%-18s %-5d %-10s %-10s\n",
                        name, qty,
                        NumberFormat.getNumberInstance(Locale.US).format(price),
                        NumberFormat.getNumberInstance(Locale.US).format(sub)));
            }
            receipt.append("------------------------------------------------\n");

            // Summary
            receipt.append(String.format("Total Item:       %d Item (%d Pcs)\n", tableModel.getRowCount(), totalQty));
            receipt.append("\n");
            receipt.append(String.format("TOTAL:            Rp %s\n",
                    NumberFormat.getNumberInstance(Locale.US).format(totalAmount)));
            receipt.append(
                    String.format("Bayar:            Rp %s\n", NumberFormat.getNumberInstance(Locale.US).format(pay)));
            receipt.append(String.format("Kembalian:        Rp %s\n",
                    NumberFormat.getNumberInstance(Locale.US).format(pay - totalAmount)));
            receipt.append("------------------------------------------------\n");

            // Footer
            // Footer
            receipt.append(" Barang yang sudah dibeli tidak dapat ditukar/dikembalikan\n");
            receipt.append("   Simpan struk ini sebagai bukti pembayaran yang sah\n");
            receipt.append("================================================\n");
            receipt.append("                  TERIMA KASIH\n");
            receipt.append("              ATAS KUNJUNGAN ANDA\n");
            receipt.append("================================================\n");

            new ReceiptDialog(this, receipt.toString(), transactionId).setVisible(true);

            // Reset
            tableModel.setRowCount(0);
            updateSummary();
            txtPay.setText("");
            txtChange.setText("");

        } catch (NumberFormatException e) {
            UIUtils.showWarning(this, "Input Tidak Valid", "Input pembayaran tidak valid!");
        }
    }
}
