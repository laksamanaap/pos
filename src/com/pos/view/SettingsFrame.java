package com.pos.view;

import com.pos.util.SettingsManager;
import com.pos.util.SolidButton;
import com.pos.util.Style;
import com.pos.util.UIUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Settings Frame untuk mengatur persentase laba global
 */
public class SettingsFrame extends JFrame {
    private MainFrame mainFrame;
    private JTextField txtProfitMargin;

    public SettingsFrame(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setTitle("Pengaturan");
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
        JLabel titleLabel = new JLabel("Pengaturan Aplikasi");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel);
        add(headerPanel, BorderLayout.NORTH);

        // Content Panel
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Style.BACKGROUND_COLOR);
        contentPanel.setBorder(new EmptyBorder(40, 60, 40, 60));

        // Settings Card
        JPanel settingsCard = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Style.SURFACE_COLOR);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                g2.setColor(new Color(0, 0, 0, 6));
                g2.fillRoundRect(2, 2, getWidth()-2, getHeight()-2, 14, 14);
                g2.setColor(Style.BORDER_COLOR);
                g2.setStroke(new BasicStroke(0.8f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 14, 14);
            }
        };
        settingsCard.setOpaque(false);
        settingsCard.setBorder(new EmptyBorder(40, 40, 40, 40));
        settingsCard.setMaximumSize(new Dimension(600, 300));
        settingsCard.setAlignmentX(Component.LEFT_ALIGNMENT);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        JLabel settingsTitle = new JLabel("Persentase Laba Default");
        settingsTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        settingsTitle.setForeground(Style.TEXT_COLOR);
        settingsCard.add(settingsTitle, gbc);

        // Description
        gbc.gridy = 1;
        JLabel description = new JLabel("Atur persentase laba default yang akan digunakan untuk menghitung harga jual semua barang");
        description.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        description.setForeground(Style.TEXT_SECONDARY);
        settingsCard.add(description, gbc);

        // Label
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        JLabel lblMargin = new JLabel("Persentase Laba (%):");
        lblMargin.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblMargin.setForeground(Style.TEXT_COLOR);
        lblMargin.setHorizontalAlignment(SwingConstants.RIGHT);
        lblMargin.setPreferredSize(new Dimension(200, 30));
        settingsCard.add(lblMargin, gbc);

        // Input Field
        gbc.gridx = 1;
        gbc.weightx = 1;
        txtProfitMargin = new JTextField();
        txtProfitMargin.setPreferredSize(new Dimension(200, 36));
        txtProfitMargin.setText(String.valueOf((int) SettingsManager.getDefaultProfitMargin()));
        UIUtils.modernizeTextField(txtProfitMargin);
        settingsCard.add(txtProfitMargin, gbc);

        // Example
        gbc.gridy = 3;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        JLabel example = new JLabel("Contoh: Harga Beli 10.000 + Laba 25% = Harga Jual 12.500");
        example.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        example.setForeground(new Color(100, 100, 100));
        settingsCard.add(example, gbc);

        // Buttons
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 15, 15, 15);
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        buttonPanel.setOpaque(false);

        JButton btnSave = new SolidButton("Simpan", Style.PRIMARY_COLOR);
        JButton btnReset = new SolidButton("Reset ke Default", Style.BORDER_COLOR);
        btnSave.setPreferredSize(new Dimension(140, 40));
        btnReset.setPreferredSize(new Dimension(160, 40));

        btnSave.addActionListener(e -> saveProfitMargin());
        btnReset.addActionListener(e -> resetToDefault());

        buttonPanel.add(btnSave);
        buttonPanel.add(btnReset);
        settingsCard.add(buttonPanel, gbc);

        contentPanel.add(settingsCard);
        contentPanel.add(Box.createVerticalGlue());

        add(contentPanel, BorderLayout.CENTER);

        // Footer
        JPanel footerPanel = new JPanel();
        footerPanel.setBackground(Style.BACKGROUND_COLOR);
        JButton btnBack = new SolidButton("Kembali ke Menu Utama", Color.GRAY);
        btnBack.addActionListener(e -> {
            mainFrame.setVisible(true);
            if ((getExtendedState() & JFrame.MAXIMIZED_BOTH) == JFrame.MAXIMIZED_BOTH) {
                mainFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
            }
            dispose();
        });
        footerPanel.add(btnBack);
        add(footerPanel, BorderLayout.SOUTH);
    }

    private void saveProfitMargin() {
        try {
            String input = txtProfitMargin.getText().trim();
            if (input.isEmpty()) {
                UIUtils.showWarning(this, "Input Kosong", "Masukkan persentase laba!");
                return;
            }

            double margin = Double.parseDouble(input);
            if (margin < 0 || margin > 500) {
                UIUtils.showWarning(this, "Input Invalid", "Persentase laba harus antara 0-500%");
                return;
            }

            SettingsManager.setDefaultProfitMargin(margin);
            UIUtils.showInfo(this, "Sukses", "Persentase laba default berhasil disimpan!");
        } catch (NumberFormatException e) {
            UIUtils.showWarning(this, "Input Invalid", "Masukkan angka yang valid!");
        }
    }

    private void resetToDefault() {
        boolean confirm = UIUtils.showConfirm(this, "Konfirmasi", "Reset ke persentase laba default (20%)?");
        if (confirm) {
            SettingsManager.setDefaultProfitMargin(20.0);
            txtProfitMargin.setText("20");
            UIUtils.showInfo(this, "Sukses", "Persentase laba berhasil direset ke 20%");
        }
    }
}
