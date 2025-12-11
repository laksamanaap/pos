package com.pos.view;

import com.pos.model.Item;
import com.pos.util.DataManager;
import com.pos.util.SettingsManager;
import com.pos.util.SolidButton;
import com.pos.util.Style;
import com.pos.util.UIUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
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
        headerPanel.setBorder(new EmptyBorder(24, 40, 24, 40));
        JLabel titleLabel = new JLabel("Manajemen Data Barang");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel);
        add(headerPanel, BorderLayout.NORTH);

        // Content Panel
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Style.BACKGROUND_COLOR);
        contentPanel.setBorder(new EmptyBorder(24, 40, 24, 40));

        // Form Panel with modern design (centered card)
        JPanel formPanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Style.SURFACE_COLOR);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                // Subtle shadow
                g2.setColor(new Color(0, 0, 0, 6));
                g2.fillRoundRect(2, 2, getWidth()-2, getHeight()-2, 14, 14);
                g2.setColor(Style.BORDER_COLOR);
                g2.setStroke(new BasicStroke(0.8f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 14, 14);
            }
        };
        formPanel.setOpaque(false);
        formPanel.setBorder(new EmptyBorder(24, 24, 24, 24));
        // limit width and center
        formPanel.setPreferredSize(new Dimension(680, 340));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 12, 12, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Row 1 - labels aligned right, inputs larger
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel lbl1 = new JLabel("Kode Barang");
        lbl1.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lbl1.setForeground(Style.TEXT_SECONDARY);
        lbl1.setHorizontalAlignment(SwingConstants.RIGHT);
        lbl1.setPreferredSize(new Dimension(140, 18));
        formPanel.add(lbl1, gbc);

        gbc.gridx = 1;
        JLabel lbl2 = new JLabel("Nama Barang");
        lbl2.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lbl2.setForeground(Style.TEXT_SECONDARY);
        lbl2.setHorizontalAlignment(SwingConstants.RIGHT);
        lbl2.setPreferredSize(new Dimension(140, 18));
        formPanel.add(lbl2, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        txtCode = new JTextField();
        txtCode.setPreferredSize(new Dimension(260, 34));
        UIUtils.modernizeTextField(txtCode);
        formPanel.add(txtCode, gbc);
        gbc.gridx = 1;
        txtName = new JTextField();
        txtName.setPreferredSize(new Dimension(360, 34));
        UIUtils.modernizeTextField(txtName);
        formPanel.add(txtName, gbc);

        // Row 2
        gbc.gridx = 0;
        gbc.gridy = 2;
        JLabel lbl3 = new JLabel("Harga Beli (Rp)");
        lbl3.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lbl3.setForeground(Style.TEXT_SECONDARY);
        formPanel.add(lbl3, gbc);
        gbc.gridx = 1;
        JLabel lbl4 = new JLabel("Harga Jual (Rp)");
        lbl4.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lbl4.setForeground(Style.TEXT_SECONDARY);
        formPanel.add(lbl4, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        txtPurchasePrice = new JTextField();
        txtPurchasePrice.setPreferredSize(new Dimension(260, 34));
        UIUtils.modernizeTextField(txtPurchasePrice);
        // Add DocumentListener for auto-calculation of selling price
        txtPurchasePrice.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                calculateSellingPrice();
            }
            @Override
            public void removeUpdate(DocumentEvent e) {
                calculateSellingPrice();
            }
            @Override
            public void changedUpdate(DocumentEvent e) {
                calculateSellingPrice();
            }
        });
        formPanel.add(txtPurchasePrice, gbc);
        gbc.gridx = 1;
        txtSellingPrice = new JTextField();
        txtSellingPrice.setPreferredSize(new Dimension(360, 34));
        UIUtils.modernizeTextField(txtSellingPrice);
        formPanel.add(txtSellingPrice, gbc);

        // Row 3
        gbc.gridx = 0;
        gbc.gridy = 4;
        JLabel lbl5 = new JLabel("Stok");
        lbl5.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lbl5.setForeground(Style.TEXT_SECONDARY);
        formPanel.add(lbl5, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        txtStock = new JTextField();
        txtStock.setPreferredSize(new Dimension(120, 34));
        UIUtils.modernizeTextField(txtStock);
        formPanel.add(txtStock, gbc);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 8));
        buttonPanel.setOpaque(false);

        JButton btnAdd = createButton("Tambah", Style.PRIMARY_COLOR);
        JButton btnUpdate = createButton("Ubah", Style.WARNING_COLOR);
        JButton btnDelete = createButton("Hapus", Style.DANGER_COLOR);
        JButton btnReset = createButton("Reset", Style.BORDER_COLOR);

        btnAdd.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btnUpdate.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btnDelete.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btnReset.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        Dimension btnSize = new Dimension(140, 40);
        btnAdd.setPreferredSize(btnSize);
        btnUpdate.setPreferredSize(btnSize);
        btnDelete.setPreferredSize(btnSize);
        btnReset.setPreferredSize(new Dimension(100, 40));

        // Add small icons to buttons
        btnAdd.setIcon(UIUtils.createModernIcon("add", Style.SURFACE_COLOR, 20));
        btnUpdate.setIcon(UIUtils.createModernIcon("edit", Style.SURFACE_COLOR, 20));
        btnDelete.setIcon(UIUtils.createModernIcon("delete", Style.SURFACE_COLOR, 20));
        btnReset.setIcon(UIUtils.createModernIcon("x", Style.SURFACE_COLOR, 20));
        btnAdd.setIconTextGap(10);
        btnUpdate.setIconTextGap(10);
        btnDelete.setIconTextGap(10);
        btnReset.setIconTextGap(8);

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnReset);

        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        formPanel.add(buttonPanel, gbc);

        // center the form card horizontally
        JPanel centerWrap = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        centerWrap.setOpaque(false);
        centerWrap.add(formPanel);
        contentPanel.add(centerWrap);
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
            // Restore parent's fullscreen state if it was fullscreen
            if ((getExtendedState() & JFrame.MAXIMIZED_BOTH) == JFrame.MAXIMIZED_BOTH) {
                mainFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
            }
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

    private void calculateSellingPrice() {
        try {
            String purchasePriceStr = txtPurchasePrice.getText().trim();
            if (purchasePriceStr.isEmpty()) {
                txtSellingPrice.setText("");
                return;
            }
            
            double purchasePrice = Double.parseDouble(purchasePriceStr);
            double profitMargin = SettingsManager.getDefaultProfitMargin();
            
            // Calculate selling price: purchasePrice + (purchasePrice * profitMargin / 100)
            double sellingPrice = purchasePrice * (1 + profitMargin / 100);
            
            txtSellingPrice.setText(String.format("%.0f", sellingPrice));
        } catch (NumberFormatException e) {
            // Silently ignore invalid input while user is typing
        }
    }
}
