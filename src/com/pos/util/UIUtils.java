package com.pos.util;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;

public class UIUtils {

    public static void customizeTable(JTable table) {
        table.setRowHeight(30);
        table.setShowVerticalLines(false);
        table.setGridColor(new Color(230, 230, 230));
        table.setSelectionBackground(new Color(232, 240, 254));
        table.setSelectionForeground(Style.TEXT_COLOR);
        table.setFont(Style.REGULAR_FONT);

        JTableHeader header = table.getTableHeader();
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row,
                        column);
                label.setBackground(Style.PRIMARY_COLOR);
                label.setForeground(Color.WHITE);
                label.setFont(Style.BOLD_FONT);
                label.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
                label.setOpaque(true);
                return label;
            }
        });
        header.setPreferredSize(new Dimension(header.getWidth(), 40));
    }

    /**
     * Create modern flat icons with different types
     * @param iconType Type of icon (cart, box, report, search, add, edit, delete, export, calendar, user, back, check, x)
     * @param bgColor Background color of the icon
     * @param size Size of the icon in pixels
     * @return ImageIcon with modern design
     */
    public static ImageIcon createModernIcon(String iconType, Color bgColor, int size) {
        BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw background rounded rectangle
        int padding = size / 8;
        int iconSize = size - padding * 2;
        g2.setColor(bgColor);
        g2.fillRoundRect(padding, padding, iconSize, iconSize, 8, 8);

        // Draw icon based on type
        g2.setColor(Style.PRIMARY_COLOR);
        g2.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        switch (iconType.toLowerCase()) {
            case "cart":
                drawCart(g2, padding, size);
                break;
            case "box":
                drawBox(g2, padding, size);
                break;
            case "report":
                drawReport(g2, padding, size);
                break;
            case "search":
                drawSearch(g2, padding, size);
                break;
            case "add":
                drawAdd(g2, padding, size);
                break;
            case "edit":
                drawEdit(g2, padding, size);
                break;
            case "delete":
                drawDelete(g2, padding, size);
                break;
            case "export":
                drawExport(g2, padding, size);
                break;
            case "calendar":
                drawCalendar(g2, padding, size);
                break;
            case "user":
                drawUser(g2, padding, size);
                break;
            case "back":
                drawBack(g2, padding, size);
                break;
            case "info":
                drawInfo(g2, padding, size);
                break;
            case "check":
                drawCheck(g2, padding, size);
                break;
            case "x":
                drawX(g2, padding, size);
                break;
            default:
                drawDefault(g2, padding, size);
        }

        g2.dispose();
        return new ImageIcon(img);
    }

    private static void drawCart(Graphics2D g, int padding, int size) {
        int cx = size / 2;
        int cy = size / 2;
        int w = size - padding * 2;
        int h = w * 3 / 4;
        g.drawRect(padding + 4, padding + h / 3, w - 8, h / 2);
        g.drawLine(padding + w / 4, padding + h / 3, padding + w / 4, padding + h / 3 - 4);
        g.drawLine(padding + 3 * w / 4, padding + h / 3, padding + 3 * w / 4, padding + h / 3 - 4);
    }

    private static void drawBox(Graphics2D g, int padding, int size) {
        int w = size - padding * 2;
        g.drawRect(padding + 2, padding + 2, w - 4, w - 4);
        g.drawLine(padding + 2, padding + 2, padding + w - 2, padding + w - 2);
    }

    private static void drawReport(Graphics2D g, int padding, int size) {
        int w = size - padding * 2;
        g.drawRect(padding + 4, padding + 2, w - 8, w - 4);
        int barH = w / 3;
        for (int i = 0; i < 3; i++) {
            g.drawLine(padding + 5 + i * (w / 4), padding + w - 2, padding + 5 + i * (w / 4), padding + w - 2 - (i + 1) * barH / 2);
        }
    }

    private static void drawSearch(Graphics2D g, int padding, int size) {
        int cx = padding + (size - padding * 2) / 2;
        int cy = padding + (size - padding * 2) / 2;
        int r = (size - padding * 2) / 3;
        g.drawOval(cx - r, cy - r, r * 2, r * 2);
        g.drawLine(cx + r / 2, cy + r / 2, cx + r, cy + r);
    }

    private static void drawAdd(Graphics2D g, int padding, int size) {
        int cx = size / 2;
        int cy = size / 2;
        int len = (size - padding * 2) / 3;
        g.drawLine(cx - len, cy, cx + len, cy);
        g.drawLine(cx, cy - len, cx, cy + len);
    }

    private static void drawEdit(Graphics2D g, int padding, int size) {
        int w = size - padding * 2;
        int x = padding + 2;
        int y = padding + w / 2;
        g.drawLine(x, y + 4, x + w - 6, y - w / 2 + 4);
        g.fillRect(x + w - 6, y - 6, 6, 6);
    }

    private static void drawDelete(Graphics2D g, int padding, int size) {
        int w = size - padding * 2;
        int h = w * 3 / 4;
        g.drawRect(padding + 3, padding + 3, w - 6, h);
        g.drawLine(padding + 4, padding + 3, padding + w - 4, padding + 3);
        g.drawLine(padding + w / 3, padding + 2, padding + w / 3, padding + 4);
        g.drawLine(padding + 2 * w / 3, padding + 2, padding + 2 * w / 3, padding + 4);
    }

    private static void drawExport(Graphics2D g, int padding, int size) {
        int w = size - padding * 2;
        int cx = padding + w / 2;
        int cy = padding + w / 2;
        g.drawLine(cx, padding + 2, cx, cy + 2);
        g.drawLine(cx - 3, cy, cx, cy - 3);
        g.drawLine(cx + 3, cy, cx, cy - 3);
        g.drawLine(padding + 2, cy + w / 3, padding + w - 2, cy + w / 3);
    }

    private static void drawCalendar(Graphics2D g, int padding, int size) {
        int w = size - padding * 2;
        g.drawRect(padding + 2, padding + 3, w - 4, w - 5);
        g.drawLine(padding + 5, padding + 5, padding + w - 5, padding + 5);
        for (int i = 0; i < 3; i++) {
            g.drawLine(padding + 4 + i * 4, padding + 3, padding + 4 + i * 4, padding + 6);
        }
    }

    private static void drawUser(Graphics2D g, int padding, int size) {
        int w = size - padding * 2;
        int cx = padding + w / 2;
        int r = w / 4;
        g.drawOval(cx - r, padding + 2, r * 2, r * 2);
        g.drawArc(cx - w / 3, padding + w / 2, w * 2 / 3, w / 2, 0, 180);
    }

    private static void drawBack(Graphics2D g, int padding, int size) {
        int w = size - padding * 2;
        int cx = padding + w / 2;
        g.drawLine(cx + 2, padding + 3, cx - 3, padding + w / 2);
        g.drawLine(cx - 3, padding + w / 2, cx + 2, padding + w - 3);
    }

    private static void drawCheck(Graphics2D g, int padding, int size) {
        int w = size - padding * 2;
        int x1 = padding + w / 4;
        int y1 = padding + w / 2;
        int x2 = padding + w / 2;
        int y2 = padding + 3 * w / 4;
        int x3 = padding + 3 * w / 4;
        int y3 = padding + w / 4;
        g.drawLine(x1, y1, x2, y2);
        g.drawLine(x2, y2, x3, y3);
    }

    private static void drawX(Graphics2D g, int padding, int size) {
        int w = size - padding * 2;
        g.drawLine(padding + 3, padding + 3, padding + w - 3, padding + w - 3);
        g.drawLine(padding + w - 3, padding + 3, padding + 3, padding + w - 3);
    }

    private static void drawInfo(Graphics2D g, int padding, int size) {
        int w = size - padding * 2;
        int cx = padding + w / 2;
        int cy = padding + w / 2;
        int r = Math.max(4, w / 4);
        // Circle
        g.drawOval(cx - r, cy - r, r * 2, r * 2);
        // vertical bar of 'i'
        g.fillRect(cx - 1, cy - r/2, 2, r);
        // dot
        g.fillOval(cx - 1, cy - r - 4, 4, 4);
    }

    private static void drawDefault(Graphics2D g, int padding, int size) {
        // Default: just the rounded background is drawn
    }

    // ------------------ Modern Dialog Helpers ------------------
    public static void showInfo(Component parent, String title, String message) {
        ModernDialog d = new ModernDialog(SwingUtilities.getWindowAncestor(parent), title, message, "info");
        d.showDialog();
    }

    public static void showWarning(Component parent, String title, String message) {
        ModernDialog d = new ModernDialog(SwingUtilities.getWindowAncestor(parent), title, message, "warning");
        d.showDialog();
    }

    public static void showError(Component parent, String title, String message) {
        ModernDialog d = new ModernDialog(SwingUtilities.getWindowAncestor(parent), title, message, "error");
        d.showDialog();
    }

    /**
     * Show a confirm dialog. Returns true if user pressed OK/Yes.
     */
    public static boolean showConfirm(Component parent, String title, String message) {
        ModernDialog d = new ModernDialog(SwingUtilities.getWindowAncestor(parent), title, message, "confirm");
        return d.showDialog();
    }

    private static class ModernDialog extends JDialog {
        private boolean result = false;

        public ModernDialog(Window owner, String title, String message, String type) {
            super(owner, ModalityType.APPLICATION_MODAL);
            setUndecorated(true);
            setLayout(new BorderLayout());
            // Larger dialog size for better readability
            setSize(600, 300);
            setLocationRelativeTo(owner);

            // Header
            JPanel header = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    GradientPaint gp = new GradientPaint(0, 0, Style.GRADIENT_START, getWidth(), 0, Style.GRADIENT_END);
                    g2.setPaint(gp);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight() + 8, 16, 16);
                }
            };
            header.setPreferredSize(new Dimension(600, 64));
            header.setLayout(new BorderLayout(12, 0));
            header.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));

            JLabel lblTitle = new JLabel(title);
            lblTitle.setForeground(Color.WHITE);
            lblTitle.setFont(Style.BOLD_FONT.deriveFont(16f));

            // Icon (bigger)
            JLabel iconLabel = new JLabel();
            ImageIcon ic;
            switch (type) {
                case "warning": ic = createModernIcon("x", Style.ACCENT_COLOR, 44); break;
                case "error": ic = createModernIcon("x", Style.DANGER_COLOR, 44); break;
                case "confirm": ic = createModernIcon("check", Style.PRIMARY_COLOR, 44); break;
                default: ic = createModernIcon("info", Style.PRIMARY_COLOR, 44); break;
            }
            iconLabel.setIcon(ic);
            iconLabel.setBorder(BorderFactory.createEmptyBorder(4, 0, 0, 8));

            header.add(iconLabel, BorderLayout.WEST);
            header.add(lblTitle, BorderLayout.CENTER);

            // Content
            JPanel content = new JPanel(new BorderLayout());
            content.setBackground(Style.SURFACE_COLOR);
            content.setBorder(BorderFactory.createEmptyBorder(20, 20, 16, 20));

            JTextArea txt = new JTextArea(message);
            txt.setEditable(false);
            txt.setOpaque(false);
            txt.setFont(Style.REGULAR_FONT.deriveFont(14f));
            txt.setLineWrap(true);
            txt.setWrapStyleWord(true);

            content.add(txt, BorderLayout.CENTER);

            // Buttons
            JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 6));
            actions.setOpaque(false);

            SolidButton ok = new SolidButton("OK", Style.PRIMARY_COLOR);
            ok.setPreferredSize(new Dimension(140, 44));
            ok.setFont(Style.BOLD_FONT.deriveFont(14f));
            ok.addActionListener(e -> {
                result = true;
                dispose();
            });

            SolidButton cancel = new SolidButton("Batal", Style.BORDER_COLOR);
            cancel.setPreferredSize(new Dimension(140, 44));
            cancel.setFont(Style.REGULAR_FONT.deriveFont(13f));
            cancel.addActionListener(e -> {
                result = false;
                dispose();
            });

            if ("confirm".equals(type)) {
                actions.add(cancel);
                actions.add(ok);
            } else {
                actions.add(ok);
            }

            JPanel bottom = new JPanel(new BorderLayout());
            bottom.setBackground(Style.SURFACE_COLOR);
            bottom.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
            bottom.add(actions, BorderLayout.EAST);

            // Compose
            add(header, BorderLayout.NORTH);
            add(content, BorderLayout.CENTER);
            add(bottom, BorderLayout.SOUTH);

            // Round window shape
            setShape(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 16, 16));
        }

        public boolean showDialog() {
            setVisible(true);
            return result;
        }
    }
}
