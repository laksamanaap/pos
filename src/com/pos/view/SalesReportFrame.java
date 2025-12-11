package com.pos.view;

import com.pos.util.SalesManager;
import com.pos.util.SolidButton;
import com.pos.util.DataManager;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.SpinnerDateModel;
import javax.swing.JSpinner;
import java.util.Date;
import com.pos.util.Style;
import com.pos.util.UIUtils;

// Import Apache POI untuk Excel
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFFont;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.io.FileOutputStream;

public class SalesReportFrame extends JFrame {
    private MainFrame mainFrame;
    private JTable table;
    private DefaultTableModel tableModel;
    private JComboBox<String> cboMode;
    private JTextField txtStartDate, txtEndDate;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    private List<String[]> allHistory = new ArrayList<>();

    public SalesReportFrame(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setTitle("Laporan Penjualan");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        
        // Inherit fullscreen state and bounds from MainFrame
        setExtendedState(mainFrame.getExtendedState());
        if ((mainFrame.getExtendedState() & JFrame.MAXIMIZED_BOTH) == JFrame.MAXIMIZED_BOTH) {
            setBounds(mainFrame.getBounds());
        } else {
            setLocationRelativeTo(null);
        }
        
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
        titleLabel.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 20));
        titleLabel.setForeground(java.awt.Color.WHITE);
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
        JButton btnClear = new SolidButton("Reset", java.awt.Color.LIGHT_GRAY);
        filterPanel.add(btnFilter);
        filterPanel.add(btnClear);

        filterPanel.add(new JLabel("Mode:"));
        cboMode = new JComboBox<>(new String[]{"Per Transaksi","Per Hari","Per Bulan","Per Tahun"});
        cboMode.setSelectedIndex(0);
        filterPanel.add(cboMode);

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
        JButton btnBack = new SolidButton("Kembali ke Menu Utama", java.awt.Color.GRAY);
        btnBack.addActionListener(e -> {
            mainFrame.setVisible(true);
            if ((getExtendedState() & JFrame.MAXIMIZED_BOTH) == JFrame.MAXIMIZED_BOTH) {
                mainFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
            }
            dispose();
        });
        JButton btnExport = new SolidButton("Export ke Excel", new java.awt.Color(0, 123, 255));
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

        btnExport.addActionListener(e -> exportToExcel());

        table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    int row = table.getSelectedRow();
                    if (row != -1) {
                        String mode = cboMode.getSelectedItem().toString();
                        if (mode.equals("Per Transaksi")) {
                            String trxId = tableModel.getValueAt(row, 1).toString();
                            showTransactionDetail(trxId);
                        } else {
                            String period = tableModel.getValueAt(row, 1).toString();
                            showAggregateDetail(period, mode);
                        }
                    }
                }
            }
        });

        cboMode.addActionListener(e -> {
            loadData();
        });
    }

    private void loadData() {
        String mode = cboMode.getSelectedItem().toString();
        allHistory = SalesManager.getSalesHistory();
        if (mode.equals("Per Transaksi")) {
            setTransactionColumns();
            tableModel.setRowCount(0);
            int no = 1;
            for (String[] row : allHistory) {
                double profit = computeProfitForTransaction(row[0]);
                tableModel.addRow(new Object[] { no++, row[0], row[1], String.format("%.0f", Double.parseDouble(row[2])), String.format("%.0f", profit) });
            }
            updateTotals(allHistory);
        } else {
            setAggregateColumns();
            java.util.Map<String, AggregateRow> map = new java.util.HashMap<>();
            SimpleDateFormat sdfLocal = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            for (String[] row : allHistory) {
                String dateStr = row[1].split(" ")[0];
                String key = dateStr;
                if (mode.equals("Per Bulan")) key = dateStr.substring(0, 7);
                if (mode.equals("Per Tahun")) key = dateStr.substring(0, 4);

                AggregateRow ar = map.get(key);
                if (ar == null) {
                    ar = new AggregateRow(key);
                    map.put(key, ar);
                }
                double amount = 0;
                try { amount = Double.parseDouble(row[2]); } catch (Exception ex) {}
                ar.totalAmount += amount;
                double profit = computeProfitForTransaction(row[0]);
                ar.totalProfit += profit;
                ar.transactionIds.add(row[0]);
            }

            java.util.List<AggregateRow> list = new java.util.ArrayList<>(map.values());
            list.sort((a, b) -> b.key.compareTo(a.key));

            tableModel.setRowCount(0);
            int no = 1;
            for (AggregateRow a : list) {
                tableModel.addRow(new Object[] { no++, a.getDisplayLabel(mode), String.format("%.0f", a.totalAmount), String.format("%.0f", a.totalProfit) });
            }
            updateTotalsAggregated(list);
        }
    }

    private void setTransactionColumns() {
        String[] columns = { "No", "ID Transaksi", "Tanggal", "Total Penjualan (Rp)", "Laba (Rp)" };
        tableModel.setColumnIdentifiers(columns);
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(DefaultTableCellRenderer.RIGHT);
        table.getColumnModel().getColumn(3).setCellRenderer(rightRenderer);
        table.getColumnModel().getColumn(4).setCellRenderer(rightRenderer);
    }

    private void setAggregateColumns() {
        String[] columns = { "No", "Periode", "Total Penjualan (Rp)", "Laba (Rp)" };
        tableModel.setColumnIdentifiers(columns);
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(DefaultTableCellRenderer.RIGHT);
        table.getColumnModel().getColumn(2).setCellRenderer(rightRenderer);
        table.getColumnModel().getColumn(3).setCellRenderer(rightRenderer);
    }

    private void updateTotalsAggregated(java.util.List<AggregateRow> list) {
        double totalAmount = 0;
        double totalProfit = 0;
        for (AggregateRow a : list) {
            totalAmount += a.totalAmount;
            totalProfit += a.totalProfit;
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

    private void applyFilter() {
        String start = txtStartDate.getText().trim();
        String end = txtEndDate.getText().trim();
        String mode = cboMode.getSelectedItem().toString();
        tableModel.setRowCount(0);
        if (mode.equals("Per Transaksi")) {
            int no = 1;
            for (String[] row : allHistory) {
                try {
                    String dateStr = row[1].split(" ")[0];
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
        } else {
            java.util.Map<String, AggregateRow> map = new java.util.HashMap<>();
            for (String[] row : allHistory) {
                try {
                    String dateStr = row[1].split(" ")[0];
                    boolean afterStart = start.isEmpty() || !sdf.parse(dateStr).before(sdf.parse(start));
                    boolean beforeEnd = end.isEmpty() || !sdf.parse(dateStr).after(sdf.parse(end));
                    if (!(afterStart && beforeEnd)) continue;
                    String key = dateStr;
                    if (mode.equals("Per Bulan")) key = dateStr.substring(0, 7);
                    if (mode.equals("Per Tahun")) key = dateStr.substring(0, 4);

                    AggregateRow ar = map.get(key);
                    if (ar == null) { ar = new AggregateRow(key); map.put(key, ar); }
                    double amount = 0;
                    try { amount = Double.parseDouble(row[2]); } catch (Exception ex) {}
                    ar.totalAmount += amount;
                    double profit = computeProfitForTransaction(row[0]);
                    ar.totalProfit += profit;
                    ar.transactionIds.add(row[0]);
                } catch (Exception e) {}
            }

            java.util.List<AggregateRow> list = new java.util.ArrayList<>(map.values());
            list.sort((a, b) -> b.key.compareTo(a.key));
            int no = 1;
            for (AggregateRow a : list) {
                tableModel.addRow(new Object[] { no++, a.getDisplayLabel(mode), String.format("%.0f", a.totalAmount), String.format("%.0f", a.totalProfit) });
            }
            updateTotalsAggregated(list);
        }
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
                String dateStr = row[1].split(" ")[0];
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

    private void exportToExcel() {
        String mode = cboMode.getSelectedItem().toString();
        
        // Check if there's data to export based on current view
        if (tableModel.getRowCount() == 0) {
            UIUtils.showInfo(this, "Export", "Tidak ada data untuk diekspor.");
            return;
        }
        
        // Generate filename based on mode and timestamp
        SimpleDateFormat filenameSdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String timestamp = filenameSdf.format(new Date());
        String defaultFilename = "";
        
        switch (mode) {
            case "Per Transaksi":
                defaultFilename = "laporan_transaksi_" + timestamp + ".xlsx";
                break;
            case "Per Hari":
                defaultFilename = "laporan_harian_" + timestamp + ".xlsx";
                break;
            case "Per Bulan":
                defaultFilename = "laporan_bulanan_" + timestamp + ".xlsx";
                break;
            case "Per Tahun":
                defaultFilename = "laporan_tahunan_" + timestamp + ".xlsx";
                break;
        }
        
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new java.io.File(defaultFilename));
        int option = chooser.showSaveDialog(this);
        if (option != JFileChooser.APPROVE_OPTION) return;
        
        java.io.File file = chooser.getSelectedFile();
        if (!file.getName().endsWith(".xlsx")) {
            file = new java.io.File(file.getAbsolutePath() + ".xlsx");
        }
        
        try {
            XSSFWorkbook workbook = new XSSFWorkbook();
            
            if (mode.equals("Per Transaksi")) {
                exportDetailedTransactions(workbook);
            } else {
                exportAggregatedData(workbook, mode);
            }
            
            // Write to file
            FileOutputStream outputStream = new FileOutputStream(file);
            workbook.write(outputStream);
            workbook.close();
            outputStream.close();
            
            UIUtils.showInfo(this, "Export", "Export selesai: " + file.getAbsolutePath());
            
        } catch (Exception e) {
            e.printStackTrace();
            UIUtils.showError(this, "Error", "Gagal saat ekspor: " + e.getMessage());
        }
    }

    private void exportDetailedTransactions(XSSFWorkbook workbook) {
        XSSFSheet sheet = workbook.createSheet("Laporan Transaksi Detail");
        
        // Create styles
        CellStyle headerStyle = workbook.createCellStyle();
        XSSFFont headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setFontHeightInPoints((short) 11);
        headerStyle.setFont(headerFont);
        headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setBorderBottom(BorderStyle.THIN);
        headerStyle.setBorderTop(BorderStyle.THIN);
        headerStyle.setBorderRight(BorderStyle.THIN);
        headerStyle.setBorderLeft(BorderStyle.THIN);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        
        CellStyle dataStyle = workbook.createCellStyle();
        dataStyle.setBorderBottom(BorderStyle.THIN);
        dataStyle.setBorderTop(BorderStyle.THIN);
        dataStyle.setBorderRight(BorderStyle.THIN);
        dataStyle.setBorderLeft(BorderStyle.THIN);
        
        CellStyle numberStyle = workbook.createCellStyle();
        numberStyle.cloneStyleFrom(dataStyle);
        numberStyle.setDataFormat(workbook.createDataFormat().getFormat("#,##0"));
        
        CellStyle totalStyle = workbook.createCellStyle();
        XSSFFont totalFont = workbook.createFont();
        totalFont.setBold(true);
        totalStyle.setFont(totalFont);
        totalStyle.setDataFormat(workbook.createDataFormat().getFormat("#,##0"));
        
        // Create header row
        Row headerRow = sheet.createRow(0);
        String[] headers = {"No", "Transaction ID", "Date", "Time", "Total Items", 
                        "Total Amount", "Profit", "Item Name", "Qty", "Sell Price", 
                        "Buy Price", "Subtotal", "Subtotal Profit"};
        
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }
        
        int rowNum = 1;
        double grandTotalAmount = 0;
        double grandTotalProfit = 0;
        
        // Get filtered transactions from current table view
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            String trxId = tableModel.getValueAt(i, 1).toString();
            String dateTime = tableModel.getValueAt(i, 2).toString();
            String[] dtParts = dateTime.split(" ");
            String date = dtParts.length > 0 ? dtParts[0] : dateTime;
            String time = dtParts.length > 1 ? dtParts[1] : "";
            
            double totalAmount = Double.parseDouble(tableModel.getValueAt(i, 3).toString().replace(",", ""));
            double profit = Double.parseDouble(tableModel.getValueAt(i, 4).toString().replace(",", ""));
            
            grandTotalAmount += totalAmount;
            grandTotalProfit += profit;
            
            // Get transaction details
            List<com.pos.model.SalesDetail> details = com.pos.util.SalesManager.getAllSalesDetails();
            int totalItems = 0;
            List<DetailRow> itemRows = new ArrayList<>();
            
            for (com.pos.model.SalesDetail d : details) {
                if (d.getTransactionId().equals(trxId)) {
                    totalItems += d.getQuantity();
                    double subtotal = d.getQuantity() * d.getSellingPrice();
                    double itemProfit = (d.getSellingPrice() - d.getPurchasePrice()) * d.getQuantity();
                    
                    com.pos.model.Item item = DataManager.getItemByCode(d.getItemCode());
                    String itemName = item != null ? item.getName() : d.getItemCode();
                    
                    itemRows.add(new DetailRow(itemName, d.getQuantity(), d.getSellingPrice(), 
                                            d.getPurchasePrice(), subtotal, itemProfit));
                }
            }
            
            // Write transaction header row
            Row row = sheet.createRow(rowNum++);
            Cell noCell = row.createCell(0);
            noCell.setCellValue(i + 1);
            noCell.setCellStyle(dataStyle);
            
            Cell trxIdCell = row.createCell(1);
            trxIdCell.setCellValue(trxId);
            trxIdCell.setCellStyle(dataStyle);
            
            Cell dateCell = row.createCell(2);
            dateCell.setCellValue(date);
            dateCell.setCellStyle(dataStyle);
            
            Cell timeCell = row.createCell(3);
            timeCell.setCellValue(time);
            timeCell.setCellStyle(dataStyle);
            
            Cell totalItemsCell = row.createCell(4);
            totalItemsCell.setCellValue(totalItems);
            totalItemsCell.setCellStyle(numberStyle);
            
            Cell totalAmountCell = row.createCell(5);
            totalAmountCell.setCellValue(totalAmount);
            totalAmountCell.setCellStyle(numberStyle);
            
            Cell profitCell = row.createCell(6);
            profitCell.setCellValue(profit);
            profitCell.setCellStyle(numberStyle);
            
            // Write item detail rows
            for (DetailRow dr : itemRows) {
                Row itemRow = sheet.createRow(rowNum++);
                
                Cell itemNameCell = itemRow.createCell(7);
                itemNameCell.setCellValue(dr.itemName);
                itemNameCell.setCellStyle(dataStyle);
                
                Cell qtyCell = itemRow.createCell(8);
                qtyCell.setCellValue(dr.qty);
                qtyCell.setCellStyle(numberStyle);
                
                Cell sellPriceCell = itemRow.createCell(9);
                sellPriceCell.setCellValue(dr.sellPrice);
                sellPriceCell.setCellStyle(numberStyle);
                
                Cell buyPriceCell = itemRow.createCell(10);
                buyPriceCell.setCellValue(dr.buyPrice);
                buyPriceCell.setCellStyle(numberStyle);
                
                Cell subtotalCell = itemRow.createCell(11);
                subtotalCell.setCellValue(dr.subtotal);
                subtotalCell.setCellStyle(numberStyle);
                
                Cell subtotalProfitCell = itemRow.createCell(12);
                subtotalProfitCell.setCellValue(dr.subtotalProfit);
                subtotalProfitCell.setCellStyle(numberStyle);
            }
            
            // Add empty row between transactions
            rowNum++;
        }
        
        // Add total rows
        rowNum++;
        Row totalAmountRow = sheet.createRow(rowNum++);
        Cell totalAmountLabelCell = totalAmountRow.createCell(5);
        totalAmountLabelCell.setCellValue("TOTAL AMOUNT:");
        totalAmountLabelCell.setCellStyle(totalStyle);
        
        Cell totalAmountValueCell = totalAmountRow.createCell(6);
        totalAmountValueCell.setCellValue(grandTotalAmount);
        totalAmountValueCell.setCellStyle(totalStyle);
        
        Row totalProfitRow = sheet.createRow(rowNum++);
        Cell totalProfitLabelCell = totalProfitRow.createCell(5);
        totalProfitLabelCell.setCellValue("TOTAL PROFIT:");
        totalProfitLabelCell.setCellStyle(totalStyle);
        
        Cell totalProfitValueCell = totalProfitRow.createCell(6);
        totalProfitValueCell.setCellValue(grandTotalProfit);
        totalProfitValueCell.setCellStyle(totalStyle);
        
        // Auto-size columns
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    private void exportAggregatedData(XSSFWorkbook workbook, String mode) {
        String sheetName = "";
        switch (mode) {
            case "Per Hari":
                sheetName = "Laporan Harian";
                break;
            case "Per Bulan":
                sheetName = "Laporan Bulanan";
                break;
            case "Per Tahun":
                sheetName = "Laporan Tahunan";
                break;
        }
        
        XSSFSheet sheet = workbook.createSheet(sheetName);
        
        // Create styles
        CellStyle headerStyle = workbook.createCellStyle();
        XSSFFont headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setFontHeightInPoints((short) 11);
        headerStyle.setFont(headerFont);
        headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setBorderBottom(BorderStyle.THIN);
        headerStyle.setBorderTop(BorderStyle.THIN);
        headerStyle.setBorderRight(BorderStyle.THIN);
        headerStyle.setBorderLeft(BorderStyle.THIN);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        
        CellStyle dataStyle = workbook.createCellStyle();
        dataStyle.setBorderBottom(BorderStyle.THIN);
        dataStyle.setBorderTop(BorderStyle.THIN);
        dataStyle.setBorderRight(BorderStyle.THIN);
        dataStyle.setBorderLeft(BorderStyle.THIN);
        
        CellStyle numberStyle = workbook.createCellStyle();
        numberStyle.cloneStyleFrom(dataStyle);
        numberStyle.setDataFormat(workbook.createDataFormat().getFormat("#,##0"));
        
        CellStyle totalStyle = workbook.createCellStyle();
        XSSFFont totalFont = workbook.createFont();
        totalFont.setBold(true);
        totalStyle.setFont(totalFont);
        totalStyle.setDataFormat(workbook.createDataFormat().getFormat("#,##0"));
        
        // Create header row
        Row headerRow = sheet.createRow(0);
        String[] headers = {"No", "Periode", "Jumlah Transaksi", "Total Penjualan", "Total Laba"};
        
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }
        
        int rowNum = 1;
        double grandTotalAmount = 0;
        double grandTotalProfit = 0;
        int totalTransactions = 0;
        
        // Get data from current table view
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            String period = tableModel.getValueAt(i, 1).toString();
            double totalAmount = Double.parseDouble(tableModel.getValueAt(i, 2).toString().replace(",", ""));
            double profit = Double.parseDouble(tableModel.getValueAt(i, 3).toString().replace(",", ""));
            
            // Count transactions in this period
            int transactionCount = 0;
            String periodKey = period.split(" ")[0]; // Get the date part before parentheses
            
            for (String[] row : allHistory) {
                String dateStr = row[1].split(" ")[0];
                String key = dateStr;
                if (mode.equals("Per Bulan")) key = dateStr.substring(0, 7);
                if (mode.equals("Per Tahun")) key = dateStr.substring(0, 4);
                
                if (periodKey.equals(key)) {
                    transactionCount++;
                }
            }
            
            grandTotalAmount += totalAmount;
            grandTotalProfit += profit;
            totalTransactions += transactionCount;
            
            // Write data row
            Row row = sheet.createRow(rowNum++);
            
            Cell noCell = row.createCell(0);
            noCell.setCellValue(i + 1);
            noCell.setCellStyle(dataStyle);
            
            Cell periodCell = row.createCell(1);
            periodCell.setCellValue(period);
            periodCell.setCellStyle(dataStyle);
            
            Cell countCell = row.createCell(2);
            countCell.setCellValue(transactionCount);
            countCell.setCellStyle(numberStyle);
            
            Cell totalAmountCell = row.createCell(3);
            totalAmountCell.setCellValue(totalAmount);
            totalAmountCell.setCellStyle(numberStyle);
            
            Cell profitCell = row.createCell(4);
            profitCell.setCellValue(profit);
            profitCell.setCellStyle(numberStyle);
        }
        
        // Add total rows
        rowNum++;
        Row totalRow = sheet.createRow(rowNum++);
        
        Cell totalLabelCell = totalRow.createCell(1);
        totalLabelCell.setCellValue("TOTAL");
        totalLabelCell.setCellStyle(totalStyle);
        
        Cell totalTransactionsCell = totalRow.createCell(2);
        totalTransactionsCell.setCellValue(totalTransactions);
        totalTransactionsCell.setCellStyle(totalStyle);
        
        Cell totalAmountCell = totalRow.createCell(3);
        totalAmountCell.setCellValue(grandTotalAmount);
        totalAmountCell.setCellStyle(totalStyle);
        
        Cell totalProfitCell = totalRow.createCell(4);
        totalProfitCell.setCellValue(grandTotalProfit);
        totalProfitCell.setCellStyle(totalStyle);
        
        // Auto-size columns
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
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

        String[] cols = { "No", "Kode", "Nama", "Qty", "Harga (Rp)", "Subtotal (Rp)", "Laba (Rp)" };
        DefaultTableModel dm = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        int no = 1;
        double total = 0;
        double totalProfit = 0;
        for (com.pos.model.SalesDetail d : details) {
            if (d.getTransactionId().equals(trxId)) {
                double subtotal = d.getQuantity() * d.getSellingPrice();
                double itemProfit = (d.getSellingPrice() - d.getPurchasePrice()) * d.getQuantity();
                com.pos.model.Item item = DataManager.getItemByCode(d.getItemCode());
                String itemName = item != null ? item.getName() : d.getItemCode();
                dm.addRow(new Object[] {
                        no++, d.getItemCode(), itemName, d.getQuantity(), String.format("%.0f", d.getSellingPrice()),
                        String.format("%.0f", subtotal), String.format("%.0f", itemProfit)
                });
                total += subtotal;
                totalProfit += itemProfit;
            }
        }

        JTable detailTable = new JTable(dm);
        UIUtils.customizeTable(detailTable);
        detailTable.setRowHeight(28);

        DefaultTableCellRenderer right = new DefaultTableCellRenderer();
        right.setHorizontalAlignment(DefaultTableCellRenderer.RIGHT);
        detailTable.getColumnModel().getColumn(3).setCellRenderer(right);
        detailTable.getColumnModel().getColumn(4).setCellRenderer(right);
        detailTable.getColumnModel().getColumn(5).setCellRenderer(right);
        detailTable.getColumnModel().getColumn(6).setCellRenderer(right);

        JScrollPane sp = new JScrollPane(detailTable);
        sp.setPreferredSize(new Dimension(720, 320));

        JPanel content = new JPanel(new BorderLayout(12, 12));
        content.setBackground(Style.SURFACE_COLOR);
        content.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JLabel lblTitle = new JLabel("ID Transaksi: " + trxId);
        lblTitle.setFont(Style.BOLD_FONT);
        lblTitle.setBorder(new EmptyBorder(6, 6, 6, 6));

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(lblTitle, BorderLayout.WEST);

        JPanel totals = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        totals.setOpaque(false);
        JLabel lblTotals = new JLabel("Total: Rp " + String.format("%.0f", total) + "   " + "Total Laba: Rp " + String.format("%.0f", totalProfit));
        lblTotals.setFont(Style.BOLD_FONT);
        totals.add(lblTotals);

        content.add(top, BorderLayout.NORTH);
        content.add(sp, BorderLayout.CENTER);
        content.add(totals, BorderLayout.SOUTH);

        JDialog dialog = new JDialog(this, "Detail Transaksi", true);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.getContentPane().setBackground(Style.BACKGROUND_COLOR);
        dialog.setLayout(new BorderLayout());
        dialog.add(content, BorderLayout.CENTER);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actions.setBackground(Style.SURFACE_COLOR);
        SolidButton btnClose = new SolidButton("Tutup", Style.BORDER_COLOR);
        btnClose.setPreferredSize(new Dimension(120, 40));
        btnClose.addActionListener(e -> dialog.dispose());
        actions.add(btnClose);
        dialog.add(actions, BorderLayout.SOUTH);

        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void showAggregateDetail(String period, String mode) {
        java.util.List<String[]> txs = new java.util.ArrayList<>();
        for (String[] row : allHistory) {
            String dateStr = row[1].split(" ")[0];
            String key = dateStr;
            if (mode.equals("Per Bulan")) key = dateStr.substring(0, 7);
            if (mode.equals("Per Tahun")) key = dateStr.substring(0, 4);
            if (period.equals(key) || period.equals(key)) {
                txs.add(row);
            } else if (period.equals(key) == false && period.equals(key) == false) {
                if (period.startsWith(key)) txs.add(row);
            }
        }

        String[] cols = { "No", "ID Transaksi", "Tanggal", "Total Penjualan (Rp)", "Laba (Rp)" };
        DefaultTableModel dm = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };

        int no = 1;
        for (String[] r : txs) {
            double profit = computeProfitForTransaction(r[0]);
            dm.addRow(new Object[] { no++, r[0], r[1], String.format("%.0f", Double.parseDouble(r[2])), String.format("%.0f", profit) });
        }

        JTable t = new JTable(dm);
        UIUtils.customizeTable(t);
        t.setRowHeight(28);
        DefaultTableCellRenderer right = new DefaultTableCellRenderer();
        right.setHorizontalAlignment(DefaultTableCellRenderer.RIGHT);
        t.getColumnModel().getColumn(3).setCellRenderer(right);
        t.getColumnModel().getColumn(4).setCellRenderer(right);

        t.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    int row = t.getSelectedRow();
                    if (row != -1) {
                        String trxId = dm.getValueAt(row, 1).toString();
                        showTransactionDetail(trxId);
                    }
                }
            }
        });

        JScrollPane sp = new JScrollPane(t);
        sp.setPreferredSize(new Dimension(760, 340));

        JPanel content = new JPanel(new BorderLayout(12, 12));
        content.setBackground(Style.SURFACE_COLOR);
        content.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JLabel lblTitle = new JLabel("Periode: " + period + " (" + mode + ")");
        lblTitle.setFont(Style.BOLD_FONT);
        lblTitle.setBorder(new EmptyBorder(6, 6, 6, 6));

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(lblTitle, BorderLayout.WEST);

        content.add(top, BorderLayout.NORTH);
        content.add(sp, BorderLayout.CENTER);

        JDialog dialog = new JDialog(this, "Detail Periode", true);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.getContentPane().setBackground(Style.BACKGROUND_COLOR);
        dialog.setLayout(new BorderLayout());
        dialog.add(content, BorderLayout.CENTER);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actions.setBackground(Style.SURFACE_COLOR);
        SolidButton btnClose = new SolidButton("Tutup", Style.BORDER_COLOR);
        btnClose.setPreferredSize(new Dimension(120, 40));
        btnClose.addActionListener(e -> dialog.dispose());
        actions.add(btnClose);
        dialog.add(actions, BorderLayout.SOUTH);

        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private static class AggregateRow {
        String key;
        double totalAmount = 0;
        double totalProfit = 0;
        java.util.List<String> transactionIds = new java.util.ArrayList<>();
        AggregateRow(String key) { this.key = key; }
        String getDisplayLabel(String mode) {
            if (mode.equals("Per Bulan")) {
                try {
                    java.text.SimpleDateFormat in = new java.text.SimpleDateFormat("yyyy-MM");
                    java.util.Date d = in.parse(key);
                    java.text.SimpleDateFormat out = new java.text.SimpleDateFormat("MMMM yyyy");
                    return key + " (" + out.format(d) + ")";
                } catch (Exception e) {
                    return key;
                }
            }
            return key;
        }
    }
    
    private static class DetailRow {
        String itemName;
        int qty;
        double sellPrice;
        double buyPrice;
        double subtotal;
        double subtotalProfit;
        
        DetailRow(String itemName, int qty, double sellPrice, double buyPrice, double subtotal, double subtotalProfit) {
            this.itemName = itemName;
            this.qty = qty;
            this.sellPrice = sellPrice;
            this.buyPrice = buyPrice;
            this.subtotal = subtotal;
            this.subtotalProfit = subtotalProfit;
        }
    }
}