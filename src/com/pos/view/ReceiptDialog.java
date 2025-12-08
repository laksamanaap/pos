package com.pos.view;

import com.pos.util.SolidButton;
import com.pos.util.Style;
import com.pos.util.UIUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.print.*;

public class ReceiptDialog extends JDialog {
    private JTextArea txtReceipt;
    private JPanel receiptPanel;
    private String transactionId;

    public ReceiptDialog(JFrame parent, String receiptText, String transactionId) {
        super(parent, "Struk Pembelian", true);
        this.transactionId = transactionId;
        setSize(600, 750);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        // Main Panel with Blue Border (Padding)
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(100, 100, 255)); // Blue-ish
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Inner White Panel with Dashed Border
        receiptPanel = new JPanel(new GridBagLayout()) {
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

        // GridBagConstraints for centering
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        receiptPanel.add(scrollPane, gbc);

        mainPanel.add(receiptPanel, BorderLayout.CENTER);

        add(mainPanel, BorderLayout.CENTER);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBackground(Color.WHITE);

        JButton btnPrint = new SolidButton("Cetak Nota", new Color(100, 149, 237)); // Cornflower Blue
        JButton btnSave = new SolidButton("Simpan PDF", new Color(46, 204, 113)); // Emerald Green
        JButton btnClose = new SolidButton("Kembali", new Color(149, 165, 166)); // Gray

        btnPrint.setPreferredSize(new Dimension(130, 40));
        btnSave.setPreferredSize(new Dimension(130, 40));
        btnClose.setPreferredSize(new Dimension(130, 40));

        buttonPanel.add(btnPrint);
        buttonPanel.add(btnSave);
        buttonPanel.add(btnClose);

        add(buttonPanel, BorderLayout.SOUTH);

        // Listeners
        btnPrint.addActionListener(e -> printPanel(false)); // False = just print

        btnSave.addActionListener(e -> {
            // Copy Transaction ID to Clipboard
            java.awt.datatransfer.StringSelection selection = new java.awt.datatransfer.StringSelection(transactionId);
            java.awt.Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, selection);

                UIUtils.showInfo(this, "Siap Simpan PDF",
                    "Nama file (" + transactionId + ") telah disalin ke Clipboard!\n" +
                        "Silakan 'Paste' (Ctrl+V) di kolom File Name saat menyimpan.\n\n" +
                        "Pilih 'Microsoft Print to PDF' atau 'Save as PDF'.");
            printPanel(true);
        });

        btnClose.addActionListener(e -> dispose());
    }

    private void printPanel(boolean isPdfMode) {
        PrinterJob job = PrinterJob.getPrinterJob();
        job.setJobName(transactionId);

        job.setPrintable(new Printable() {
            @Override
            public int print(Graphics pg, PageFormat pf, int pageNum) {
                if (pageNum > 0) {
                    return Printable.NO_SUCH_PAGE;
                }

                Graphics2D g2 = (Graphics2D) pg;
                g2.translate(pf.getImageableX(), pf.getImageableY());

                // Scale to fit page
                double scaleX = pf.getImageableWidth() / receiptPanel.getWidth();
                double scaleY = pf.getImageableHeight() / receiptPanel.getHeight();
                double scale = Math.min(scaleX, scaleY); // Maintain aspect ratio

                // Don't upscale, just downscale if larger than page
                if (scale > 1)
                    scale = 1;

                g2.scale(scale, scale);

                // Print the receipt panel
                receiptPanel.paint(g2);

                return Printable.PAGE_EXISTS;
            }
        });

        if (job.printDialog()) {
            try {
                job.print();
            } catch (PrinterException ex) {
                UIUtils.showError(this, "Gagal Mencetak", "Gagal mencetak: " + ex.getMessage());
            }
        }
    }
}
