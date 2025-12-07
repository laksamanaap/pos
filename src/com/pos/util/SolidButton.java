package com.pos.util;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class SolidButton extends JButton {
    private Color normalColor;
    private Color hoverColor;

    public SolidButton(String text, Color color) {
        super(text);
        this.normalColor = color;
        this.hoverColor = color.darker();

        setBackground(normalColor);
        setForeground(Color.WHITE);
        setFocusPainted(false);
        setFont(Style.BOLD_FONT);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                setBackground(hoverColor);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                setBackground(normalColor);
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);

        g2.dispose();
        super.paintComponent(g);
    }
}
