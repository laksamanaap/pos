package com.pos.view;

import com.pos.util.SalesManager;
import com.pos.util.SolidButton;
import com.pos.util.Style;
import com.pos.util.UIUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class SalesReportFrame extends JFrame {
    private MainFrame mainFrame;
    private JTable table;
    private DefaultTableModel tableModel;

    public SalesReportFrame(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setTitle("Laporan Penjualan");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Style.BACKGROUND_COLOR);

        // Header
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(new EmptyBorder(15, 20, 15, 20));
        JLabel titleLabel = new JLabel("📊 LAPORAN PENJUALAN");
        titleLabel.setFont(Style.SUBHEADER_FONT);
        titleLabel.setForeground(Style.TEXT_COLOR);
        headerPanel.add(titleLabel);
        add(headerPanel, BorderLayout.NORTH);

        // Content
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        contentPanel.setBackground(Style.BACKGROUND_COLOR);

        String[] columns = { "No", "ID Transaksi", "Tanggal", "Total Penjualan (Rp)" };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        UIUtils.customizeTable(table);

        JScrollPane scrollPane = new JScrollPane(table);
        contentPanel.add(scrollPane, BorderLayout.CENTER);

        add(contentPanel, BorderLayout.CENTER);

        // Footer
        JPanel footerPanel = new JPanel();
        footerPanel.setBackground(Style.BACKGROUND_COLOR);
        JButton btnBack = new SolidButton("Kembali ke Menu Utama", Color.GRAY);
        btnBack.addActionListener(e -> {
            mainFrame.setVisible(true);
            dispose();
        });
        footerPanel.add(btnBack);
        add(footerPanel, BorderLayout.SOUTH);

        loadData();
    }

    private void loadData() {
        tableModel.setRowCount(0);
        List<String[]> history = SalesManager.getSalesHistory();
        int no = 1;
        for (String[] row : history) {
            tableModel.addRow(new Object[] {
                    no++, row[0], row[1], row[2]
            });
        }
    }
}
