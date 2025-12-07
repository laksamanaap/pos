package com.pos.util;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;

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
}
