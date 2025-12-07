package com.pos.view;

import com.pos.util.SolidButton;
import com.pos.util.Style;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class ReceiptDialog extends JDialog {
    private JTextArea txtReceipt;

    public ReceiptDialog(JFrame parent, String receiptText) {
        super(parent, "Struk Pembelian", true);
        setSize(600, 700);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        // Main Panel with Blue Border (Padding)
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(100, 100, 255)); // Blue-ish
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Inner White Panel with Dashed Border
        JPanel receiptPanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // White background
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);

                // Dashed Border
                float[] dash = { 5.0f };
                g2.setStroke(new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash, 0.0f));
                g2.setColor(new Color(100, 100, 255));
                g2.drawRoundRect(10, 10, getWidth() - 20, getHeight() - 20, 15, 15);

                g2.dispose();
            }
        };
        receiptPanel.setOpaque(false);
        receiptPanel.setBorder(new EmptyBorder(25, 25, 25, 25));

        txtReceipt = new JTextArea(receiptText);
        txtReceipt.setFont(new Font("Monospaced", Font.PLAIN, 12));
        txtReceipt.setEditable(false);
        txtReceipt.setOpaque(false); // Transparent to show white panel

        // Fix for ScrollPane
        JScrollPane scrollPane = new JScrollPane(txtReceipt);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);

        receiptPanel.add(scrollPane, new GridBagConstraints());
        mainPanel.add(receiptPanel, BorderLayout.CENTER);

        add(mainPanel, BorderLayout.CENTER);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBackground(Color.WHITE);

        JButton btnPrint = new SolidButton("🖨️ Cetak Nota", new Color(100, 149, 237)); // Cornflower Blue
        JButton btnSave = new SolidButton("💾 Simpan PDF", new Color(46, 204, 113)); // Emerald Green
        JButton btnClose = new SolidButton("⬅ Kembali", new Color(149, 165, 166)); // Gray

        btnPrint.setPreferredSize(new Dimension(130, 40));
        btnSave.setPreferredSize(new Dimension(130, 40));
        btnClose.setPreferredSize(new Dimension(130, 40));

        buttonPanel.add(btnPrint);
        buttonPanel.add(btnSave);
        buttonPanel.add(btnClose);

        add(buttonPanel, BorderLayout.SOUTH);

        // Listeners
        btnPrint.addActionListener(e -> {
            try {
                txtReceipt.print();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Gagal mencetak: " + ex.getMessage());
            }
        });

        btnSave.addActionListener(e -> {
            try {
                String fileName = "Struk_" + System.currentTimeMillis() + ".txt";
                BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
                writer.write(txtReceipt.getText());
                writer.close();
                JOptionPane.showMessageDialog(this,
                        "Struk berhasil disimpan ke " + fileName + "\n(PDF generation requires external libraries)");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Gagal menyimpan: " + ex.getMessage());
            }
        });

        btnClose.addActionListener(e -> dispose());
    }
}
