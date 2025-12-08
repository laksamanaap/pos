package com.pos.view;

import com.pos.util.SalesManager;
import com.pos.util.SolidButton;
import com.pos.util.DataManager;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.SpinnerDateModel;
import javax.swing.JSpinner;
import java.text.DecimalFormat;
import java.util.Date;
import com.pos.util.Style;
import com.pos.util.UIUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class SalesReportFrame extends JFrame {
    private MainFrame mainFrame;
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtStartDate, txtEndDate;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    private List<String[]> allHistory = new ArrayList<>();

    public SalesReportFrame(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setTitle("Laporan Penjualan");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Style.BACKGROUND_COLOR);

        // Header (modern)
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
        headerPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        JLabel titleLabel = new JLabel("LAPORAN PENJUALAN");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.WEST);
        add(headerPanel, BorderLayout.NORTH);


        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        contentPanel.setBackground(Style.BACKGROUND_COLOR);

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 12)) {
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
        filterPanel.setOpaque(false);
        filterPanel.setBorder(new EmptyBorder(12, 12, 12, 12));

        filterPanel.add(new JLabel("Dari:"));
        SpinnerDateModel startModel = new SpinnerDateModel();
        JSpinner startSpinner = new JSpinner(startModel);
        startSpinner.setEditor(new JSpinner.DateEditor(startSpinner, "yyyy-MM-dd"));
        filterPanel.add(startSpinner);

        filterPanel.add(new JLabel("Sampai:"));
        SpinnerDateModel endModel = new SpinnerDateModel();
        JSpinner endSpinner = new JSpinner(endModel);
        endSpinner.setEditor(new JSpinner.DateEditor(endSpinner, "yyyy-MM-dd"));
        filterPanel.add(endSpinner);

        JButton btnFilter = new SolidButton("Terapkan", Style.PRIMARY_COLOR);
        JButton btnClear = new SolidButton("Reset", Color.LIGHT_GRAY);
        filterPanel.add(btnFilter);
        filterPanel.add(btnClear);

        txtStartDate = new JTextField(10);
        txtEndDate = new JTextField(10);
        txtStartDate.setVisible(false);
        txtEndDate.setVisible(false);
        contentPanel.add(filterPanel, BorderLayout.NORTH);

        String[] columns = { "No", "ID Transaksi", "Tanggal", "Total Penjualan (Rp)", "Laba (Rp)" };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        UIUtils.customizeTable(table);
        table.setRowHeight(28);
        JTableHeader th = table.getTableHeader();
        th.setFont(Style.BOLD_FONT);

        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(DefaultTableCellRenderer.RIGHT);
        table.getColumnModel().getColumn(3).setCellRenderer(rightRenderer);
        table.getColumnModel().getColumn(4).setCellRenderer(rightRenderer);

        JScrollPane scrollPane = new JScrollPane(table);
        contentPanel.add(scrollPane, BorderLayout.CENTER);

        add(contentPanel, BorderLayout.CENTER);

        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setBackground(Style.BACKGROUND_COLOR);

        JPanel leftActions = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leftActions.setBackground(Style.BACKGROUND_COLOR);
        JButton btnBack = new SolidButton("Kembali ke Menu Utama", Color.GRAY);
        btnBack.addActionListener(e -> {
            mainFrame.setVisible(true);
            dispose();
        });
        JButton btnExport = new SolidButton("Export ke Excel", new Color(0, 123, 255));
        leftActions.add(btnBack);
        leftActions.add(btnExport);

        JPanel totalsPanel = new JPanel();
        totalsPanel.setBackground(Style.BACKGROUND_COLOR);
        totalsPanel.setLayout(new BoxLayout(totalsPanel, BoxLayout.Y_AXIS));
        totalsPanel.setBorder(new EmptyBorder(6, 6, 6, 12));
        JLabel lblTotalAmount = new JLabel("Total Amount: Rp 0");
        lblTotalAmount.setFont(Style.BOLD_FONT);
        JLabel lblTotalProfit = new JLabel("Total Profit: Rp 0");
        lblTotalProfit.setFont(Style.BOLD_FONT);
        totalsPanel.add(lblTotalAmount);
        totalsPanel.add(lblTotalProfit);

        footerPanel.add(leftActions, BorderLayout.WEST);
        footerPanel.add(totalsPanel, BorderLayout.EAST);
        add(footerPanel, BorderLayout.SOUTH);

        loadData();

        btnFilter.addActionListener(e -> {
            Date sd = (Date) startSpinner.getValue();
            Date ed = (Date) endSpinner.getValue();
            SimpleDateFormat sdfLocal = new SimpleDateFormat("yyyy-MM-dd");
            txtStartDate.setText(sdfLocal.format(sd));
            txtEndDate.setText(sdfLocal.format(ed));
            applyFilter();
        });
        btnClear.addActionListener(e -> {
            txtStartDate.setText("");
            txtEndDate.setText("");
            loadData();
        });

        btnExport.addActionListener(e -> exportToCSV());

        table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    int row = table.getSelectedRow();
                    if (row != -1) {
                        String trxId = tableModel.getValueAt(row, 1).toString();
                        showTransactionDetail(trxId);
                    }
                }
            }
        });
    }

    private void loadData() {
        tableModel.setRowCount(0);
        allHistory = SalesManager.getSalesHistory();
        int no = 1;
        for (String[] row : allHistory) {
            double profit = computeProfitForTransaction(row[0]);
            tableModel.addRow(new Object[] {
                    no++, row[0], row[1], String.format("%.0f", Double.parseDouble(row[2])), String.format("%.0f", profit)
            });
        }
        updateTotals(allHistory);
    }

    private void applyFilter() {
        String start = txtStartDate.getText().trim();
        String end = txtEndDate.getText().trim();
        tableModel.setRowCount(0);
        int no = 1;
        for (String[] row : allHistory) {
            try {
                String dateStr = row[1].split(" ")[0]; // yyyy-MM-dd
                boolean afterStart = start.isEmpty() || !sdf.parse(dateStr).before(sdf.parse(start));
                boolean beforeEnd = end.isEmpty() || !sdf.parse(dateStr).after(sdf.parse(end));
                if (afterStart && beforeEnd) {
                    double profit = computeProfitForTransaction(row[0]);
                    tableModel.addRow(new Object[]{no++, row[0], row[1], String.format("%.0f", Double.parseDouble(row[2])), String.format("%.0f", profit)});
                }
            } catch (ParseException e) {
            }
        }
        updateTotals(getFilteredHistory());
    }

    private void updateTotals(List<String[]> rows) {
        double totalAmount = 0;
        double totalProfit = 0;
        for (String[] r : rows) {
            try { totalAmount += Double.parseDouble(r[2]); } catch (Exception e) {}
            totalProfit += computeProfitForTransaction(r[0]);
        }
        for (Component c : getContentPane().getComponents()) {
            if (c instanceof JPanel) {
                JPanel p = (JPanel) c;
                for (Component cc : p.getComponents()) {
                    if (cc instanceof JPanel) {
                        JPanel inner = (JPanel) cc;
                        for (Component lab : inner.getComponents()) {
                            if (lab instanceof JLabel) {
                                JLabel jl = (JLabel) lab;
                                if (jl.getText().startsWith("Total Amount")) {
                                    jl.setText("Total Amount: Rp " + String.format("%.0f", totalAmount));
                                } else if (jl.getText().startsWith("Total Profit")) {
                                    jl.setText("Total Profit: Rp " + String.format("%.0f", totalProfit));
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private List<String[]> getFilteredHistory() {
        String start = txtStartDate.getText().trim();
        String end = txtEndDate.getText().trim();
        List<String[]> result = new ArrayList<>();
        for (String[] row : allHistory) {
            try {
                String dateStr = row[1].split(" ")[0]; // yyyy-MM-dd
                boolean afterStart = start.isEmpty() || !sdf.parse(dateStr).before(sdf.parse(start));
                boolean beforeEnd = end.isEmpty() || !sdf.parse(dateStr).after(sdf.parse(end));
                if (afterStart && beforeEnd) {
                    result.add(row);
                }
            } catch (ParseException e) {
            }
        }
        return result;
    }

    private void exportToCSV() {
        List<String[]> filtered = getFilteredHistory();
        if (filtered.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tidak ada data untuk diekspor.", "Export", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new java.io.File("sales_report_export.csv"));
        int option = chooser.showSaveDialog(this);
        if (option != JFileChooser.APPROVE_OPTION) return;
        
        java.io.File file = chooser.getSelectedFile();
        double grandTotalAmount = 0;
        double grandTotalProfit = 0;
        
        try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"))) {
            bw.write('\ufeff');
            
            bw.write("Transaction ID;Date;Time;Customer;Cashier;Total Items;Total Amount;Profit;Item Name;Qty;Sell Price;Buy Price;Subtotal;Subtotal Profit");
            bw.newLine();
            
            for (String[] trx : filtered) {
                String trxId = trx[0];
                String dateTime = trx[1];
                String[] dtParts = dateTime.split(" ");
                String date = dtParts.length > 0 ? dtParts[0] : dateTime;
                String time = dtParts.length > 1 ? dtParts[1] : "";
                double totalAmount = 0;
                try {
                    totalAmount = Double.parseDouble(trx[2]);
                } catch (Exception ex) {}
                
                List<com.pos.model.SalesDetail> details = com.pos.util.SalesManager.getAllSalesDetails();
                int totalItems = 0;
                double profit = 0;
                List<String[]> itemRows = new ArrayList<>();
                
                for (com.pos.model.SalesDetail d : details) {
                    if (d.getTransactionId().equals(trxId)) {
                        totalItems += d.getQuantity();
                        double subtotal = d.getQuantity() * d.getSellingPrice();
                        double itemProfit = (d.getSellingPrice() - d.getPurchasePrice()) * d.getQuantity();
                        profit += itemProfit;
                        
                        com.pos.model.Item item = DataManager.getItemByCode(d.getItemCode());
                        String itemName = item != null ? item.getName() : d.getItemCode();
                        
                        String[] itemRow = new String[14];
                        itemRow[0] = "";
                        itemRow[1] = "";
                        itemRow[2] = "";
                        itemRow[3] = "";
                        itemRow[4] = "";
                        itemRow[5] = "";
                        itemRow[6] = ""; 
                        itemRow[7] = "";
                        itemRow[8] = escapeCSV(itemName);
                        itemRow[9] = String.valueOf(d.getQuantity());
                        itemRow[10] = String.format("%.0f", d.getSellingPrice());
                        itemRow[11] = String.format("%.0f", d.getPurchasePrice());
                        itemRow[12] = String.format("%.0f", subtotal);
                        itemRow[13] = String.format("%.0f", itemProfit);
                        
                        itemRows.add(itemRow);
                    }
                }
                
                grandTotalAmount += totalAmount;
                grandTotalProfit += profit;
                
                bw.write(escapeCSV(trxId) + ";");
                bw.write(date + ";");
                bw.write(time + ";");
                bw.write("Umum;");
                bw.write(";");
                bw.write(totalItems + ";");
                bw.write(String.format("%.0f", totalAmount) + ";");
                bw.write(String.format("%.0f", profit) + ";");
                bw.write(";;;;");
                bw.newLine();
                
                for (String[] ir : itemRows) {
                    for (int i = 0; i < ir.length; i++) {
                        bw.write(ir[i] != null ? ir[i] : "");
                        if (i < ir.length - 1) bw.write(";");
                    }
                    bw.newLine();
                }
                
                bw.newLine();
            }
            
            bw.newLine();
            bw.write(";;;;;;Total Amount:;" + String.format("%.0f", grandTotalAmount));
            bw.newLine();
            bw.write(";;;;;;Total Profit:;" + String.format("%.0f", grandTotalProfit));
            bw.newLine();
            
            bw.flush();
            JOptionPane.showMessageDialog(this, "Export selesai: " + file.getAbsolutePath(), "Export", JOptionPane.INFORMATION_MESSAGE);
            
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Gagal saat ekspor: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String escapeCSV(String value) {
        if (value == null) return "";
        if (value.contains(";") || value.contains("\"") || value.contains("\n")) {
            value = value.replace("\"", "\"\""); // Escape quotes
            return "\"" + value + "\"";
        }
        return value;
    }

    private double computeProfitForTransaction(String trxId) {
        double profit = 0.0;
        List<com.pos.model.SalesDetail> details = com.pos.util.SalesManager.getAllSalesDetails();
        for (com.pos.model.SalesDetail d : details) {
            if (d.getTransactionId().equals(trxId)) {
                profit += (d.getSellingPrice() - d.getPurchasePrice()) * d.getQuantity();
            }
        }
        return profit;
    }

    private void showTransactionDetail(String trxId) {
        List<com.pos.model.SalesDetail> details = com.pos.util.SalesManager.getAllSalesDetails();
        StringBuilder sb = new StringBuilder();
        sb.append("ID Transaksi: ").append(trxId).append("\n");
        sb.append("------------------------------------------\n");
        sb.append(String.format("%-10s %-6s %-10s %-10s %-10s\n", "Kode", "Qty", "Harga", "Subtotal", "Laba"));
        double total = 0;
        double totalProfit = 0;
        for (com.pos.model.SalesDetail d : details) {
            if (d.getTransactionId().equals(trxId)) {
                double subtotal = d.getQuantity() * d.getSellingPrice();
                double itemProfit = (d.getSellingPrice() - d.getPurchasePrice()) * d.getQuantity();
                sb.append(String.format("%-10s %-6d %-10.0f %-10.0f %-10.0f\n", d.getItemCode(), d.getQuantity(), d.getSellingPrice(), subtotal, itemProfit));
                total += subtotal;
                totalProfit += itemProfit;
            }
        }
        sb.append("------------------------------------------\n");
        sb.append("Total: Rp " + String.format("%.0f", total) + "\n");
        sb.append("Total Laba: Rp " + String.format("%.0f", totalProfit));
        JOptionPane.showMessageDialog(this, sb.toString(), "Detail Transaksi", JOptionPane.INFORMATION_MESSAGE);
    }
}
