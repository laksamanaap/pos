package com.pos.view;

import com.pos.model.Item;
import com.pos.util.DataManager;
import com.pos.util.SolidButton;
import com.pos.util.Style;
import com.pos.util.UIUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.stream.Collectors;

public class ItemDataFrame extends JFrame {
    private MainFrame mainFrame;
    private JTextField txtCode, txtName, txtPurchasePrice, txtSellingPrice, txtStock, txtSearch;
    private JTable table;
    private DefaultTableModel tableModel;

    public ItemDataFrame(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setTitle("Manajemen Data Barang");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 750);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Style.BACKGROUND_COLOR);

        // Header with Gradient
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT)) {
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
        JLabel titleLabel = new JLabel("MANAJEMEN DATA BARANG");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel);
        add(headerPanel, BorderLayout.NORTH);

        // Content Panel
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Style.BACKGROUND_COLOR);
        contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Form Panel with modern rounded design
        JPanel formPanel = new JPanel(new GridBagLayout()) {
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
        formPanel.setOpaque(false);
        formPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Row 1
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Kode Barang"), gbc);
        gbc.gridx = 1;
        formPanel.add(new JLabel("Nama Barang"), gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        txtCode = new JTextField(15);
        formPanel.add(txtCode, gbc);
        gbc.gridx = 1;
        txtName = new JTextField(20);
        formPanel.add(txtName, gbc);

        // Row 2
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Harga Beli (Rp)"), gbc);
        gbc.gridx = 1;
        formPanel.add(new JLabel("Harga Jual (Rp)"), gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        txtPurchasePrice = new JTextField(15);
        formPanel.add(txtPurchasePrice, gbc);
        gbc.gridx = 1;
        txtSellingPrice = new JTextField(15);
        formPanel.add(txtSellingPrice, gbc);

        // Row 3
        gbc.gridx = 0;
        gbc.gridy = 4;
        formPanel.add(new JLabel("Stok"), gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        txtStock = new JTextField(20);
        formPanel.add(txtStock, gbc);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setBackground(Color.WHITE);

        JButton btnAdd = createButton("Tambah", Style.PRIMARY_COLOR);
        JButton btnUpdate = createButton("Ubah", Style.WARNING_COLOR);
        JButton btnDelete = createButton("Hapus", Style.DANGER_COLOR);
        JButton btnReset = createButton("Reset", Color.GRAY);

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnReset);

        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        formPanel.add(buttonPanel, gbc);

        contentPanel.add(formPanel);
        contentPanel.add(Box.createVerticalStrut(20));

        // Search Panel
        JPanel searchPanel = new JPanel(new BorderLayout(10, 0));
        searchPanel.setBackground(Style.BACKGROUND_COLOR);
        searchPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        txtSearch = new JTextField();
        txtSearch.setBackground(Style.SURFACE_COLOR);
        txtSearch.setForeground(Style.TEXT_COLOR);
        txtSearch.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Style.BORDER_COLOR, 1),
                new EmptyBorder(8, 8, 8, 8)));
        
        JButton btnSearch = createButton("Cari", Style.PRIMARY_COLOR);

        searchPanel.add(new JLabel("Cari barang: "), BorderLayout.WEST);
        searchPanel.add(txtSearch, BorderLayout.CENTER);
        searchPanel.add(btnSearch, BorderLayout.EAST);

        contentPanel.add(searchPanel);
        contentPanel.add(Box.createVerticalStrut(10));

        // Table
        String[] columns = { "No", "Kode Barang", "Nama Barang", "Harga Beli", "Harga Jual", "Stok" };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        UIUtils.customizeTable(table);

        JScrollPane scrollPane = new JScrollPane(table);
        contentPanel.add(scrollPane);

        add(contentPanel, BorderLayout.CENTER);

        // Footer / Back Button
        JPanel footerPanel = new JPanel();
        footerPanel.setBackground(Style.BACKGROUND_COLOR);
        JButton btnBack = createButton("Kembali ke Menu Utama", Color.GRAY);
        btnBack.addActionListener(e -> {
            mainFrame.setVisible(true);
            dispose();
        });
        footerPanel.add(btnBack);
        add(footerPanel, BorderLayout.SOUTH);

        // Event Listeners
        btnAdd.addActionListener(e -> addItem());
        btnUpdate.addActionListener(e -> updateItem());
        btnDelete.addActionListener(e -> deleteItem());
        btnReset.addActionListener(e -> clearForm());
        btnSearch.addActionListener(e -> loadData(txtSearch.getText()));

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                if (row != -1) {
                    txtCode.setText(tableModel.getValueAt(row, 1).toString());
                    txtName.setText(tableModel.getValueAt(row, 2).toString());
                    txtPurchasePrice.setText(tableModel.getValueAt(row, 3).toString().replace(".0", ""));
                    txtSellingPrice.setText(tableModel.getValueAt(row, 4).toString().replace(".0", ""));
                    txtStock.setText(tableModel.getValueAt(row, 5).toString());
                    txtCode.setEditable(false); // Cannot change code when editing
                }
            }
        });

        loadData("");
    }

    private JButton createButton(String text, Color bg) {
        return new SolidButton(text, bg);
    }

    private void loadData(String query) {
        tableModel.setRowCount(0);
        List<Item> items = DataManager.getAllItems();
        if (!query.isEmpty()) {
            items = items.stream()
                    .filter(i -> i.getName().toLowerCase().contains(query.toLowerCase()) ||
                            i.getCode().toLowerCase().contains(query.toLowerCase()))
                    .collect(Collectors.toList());
        }

        int no = 1;
        for (Item item : items) {
            tableModel.addRow(new Object[] {
                    no++, item.getCode(), item.getName(), item.getPurchasePrice(), item.getSellingPrice(),
                    item.getStock()
            });
        }
    }

    private void addItem() {
        try {
            String code = txtCode.getText();
            String name = txtName.getText();
            double purchasePrice = Double.parseDouble(txtPurchasePrice.getText());
            double sellingPrice = Double.parseDouble(txtSellingPrice.getText());
            int stock = Integer.parseInt(txtStock.getText());

            if (DataManager.getItemByCode(code) != null) {
                UIUtils.showWarning(this, "Duplikasi Kode", "Kode barang sudah ada!");
                return;
            }

            DataManager.addItem(new Item(code, name, purchasePrice, sellingPrice, stock));
            loadData("");
            clearForm();
            UIUtils.showInfo(this, "Sukses", "Data berhasil ditambahkan!");
        } catch (NumberFormatException e) {
            UIUtils.showWarning(this, "Input Tidak Valid", "Input harga/stok tidak valid!");
        }
    }

    private void updateItem() {
        try {
            String code = txtCode.getText();
            String name = txtName.getText();
            double purchasePrice = Double.parseDouble(txtPurchasePrice.getText());
            double sellingPrice = Double.parseDouble(txtSellingPrice.getText());
            int stock = Integer.parseInt(txtStock.getText());

            DataManager.updateItem(code, new Item(code, name, purchasePrice, sellingPrice, stock));
            loadData("");
            clearForm();
            UIUtils.showInfo(this, "Sukses", "Data berhasil diubah!");
        } catch (NumberFormatException e) {
            UIUtils.showWarning(this, "Input Tidak Valid", "Input harga/stok tidak valid!");
        }
    }

    private void deleteItem() {
        String code = txtCode.getText();
        if (code.isEmpty()) {
            UIUtils.showInfo(this, "Peringatan", "Pilih barang yang akan dihapus!");
            return;
        }

        boolean confirm = UIUtils.showConfirm(this, "Konfirmasi", "Yakin ingin menghapus data ini?");
        if (confirm) {
            DataManager.deleteItem(code);
            loadData("");
            clearForm();
            UIUtils.showInfo(this, "Sukses", "Data berhasil dihapus!");
        }
    }

    private void clearForm() {
        txtCode.setText("");
        txtName.setText("");
        txtPurchasePrice.setText("");
        txtSellingPrice.setText("");
        txtStock.setText("");
        txtCode.setEditable(true);
        table.clearSelection();
    }
}
