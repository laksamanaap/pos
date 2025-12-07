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
    private JTextField txtCode, txtName, txtPrice, txtStock, txtSearch;
    private JTable table;
    private DefaultTableModel tableModel;

    public ItemDataFrame(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setTitle("Manajemen Data Barang");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 700);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Style.BACKGROUND_COLOR);

        // Header
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(new EmptyBorder(15, 20, 15, 20));
        JLabel titleLabel = new JLabel("📦 MANAJEMEN DATA BARANG");
        titleLabel.setFont(Style.SUBHEADER_FONT);
        titleLabel.setForeground(Style.TEXT_COLOR);
        headerPanel.add(titleLabel);
        add(headerPanel, BorderLayout.NORTH);

        // Content Panel
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Style.BACKGROUND_COLOR);
        contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
                new EmptyBorder(20, 20, 20, 20)));

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
        formPanel.add(new JLabel("Harga Satuan (Rp)"), gbc);
        gbc.gridx = 1;
        formPanel.add(new JLabel("Stok"), gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        txtPrice = new JTextField(15);
        formPanel.add(txtPrice, gbc);
        gbc.gridx = 1;
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
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        formPanel.add(buttonPanel, gbc);

        contentPanel.add(formPanel);
        contentPanel.add(Box.createVerticalStrut(20));

        // Search Panel
        JPanel searchPanel = new JPanel(new BorderLayout(10, 0));
        searchPanel.setBackground(Style.BACKGROUND_COLOR);
        searchPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        txtSearch = new JTextField();
        JButton btnSearch = createButton("Cari", Style.PRIMARY_COLOR);

        searchPanel.add(new JLabel("Cari barang: "), BorderLayout.WEST);
        searchPanel.add(txtSearch, BorderLayout.CENTER);
        searchPanel.add(btnSearch, BorderLayout.EAST);

        contentPanel.add(searchPanel);
        contentPanel.add(Box.createVerticalStrut(10));

        // Table
        String[] columns = { "No", "Kode Barang", "Nama Barang", "Harga (Rp)", "Stok" };
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
                    txtPrice.setText(tableModel.getValueAt(row, 3).toString().replace(".0", ""));
                    txtStock.setText(tableModel.getValueAt(row, 4).toString());
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
                    no++, item.getCode(), item.getName(), item.getPrice(), item.getStock()
            });
        }
    }

    private void addItem() {
        try {
            String code = txtCode.getText();
            String name = txtName.getText();
            double price = Double.parseDouble(txtPrice.getText());
            int stock = Integer.parseInt(txtStock.getText());

            if (DataManager.getItemByCode(code) != null) {
                JOptionPane.showMessageDialog(this, "Kode barang sudah ada!");
                return;
            }

            DataManager.addItem(new Item(code, name, price, stock));
            loadData("");
            clearForm();
            JOptionPane.showMessageDialog(this, "Data berhasil ditambahkan!");
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Input harga/stok tidak valid!");
        }
    }

    private void updateItem() {
        try {
            String code = txtCode.getText();
            String name = txtName.getText();
            double price = Double.parseDouble(txtPrice.getText());
            int stock = Integer.parseInt(txtStock.getText());

            DataManager.updateItem(code, new Item(code, name, price, stock));
            loadData("");
            clearForm();
            JOptionPane.showMessageDialog(this, "Data berhasil diubah!");
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Input harga/stok tidak valid!");
        }
    }

    private void deleteItem() {
        String code = txtCode.getText();
        if (code.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Pilih barang yang akan dihapus!");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Yakin ingin menghapus data ini?", "Konfirmasi",
                JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            DataManager.deleteItem(code);
            loadData("");
            clearForm();
            JOptionPane.showMessageDialog(this, "Data berhasil dihapus!");
        }
    }

    private void clearForm() {
        txtCode.setText("");
        txtName.setText("");
        txtPrice.setText("");
        txtStock.setText("");
        txtCode.setEditable(true);
        table.clearSelection();
    }
}
