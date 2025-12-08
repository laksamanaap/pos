package com.pos.util;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class SolidButton extends JButton {
    private Color normalColor;
    private Color hoverColor;
    private Color pressedColor;
    private boolean isPressed = false;

    public SolidButton(String text, Color color) {
        super(text);
        this.normalColor = color;
        this.hoverColor = new Color(
            Math.min(255, color.getRed() + 20),
            Math.min(255, color.getGreen() + 20),
            Math.min(255, color.getBlue() + 20)
        );
        this.pressedColor = color.darker();

        setBackground(normalColor);
        setForeground(Color.WHITE);
        setFocusPainted(false);
        setFont(Style.BOLD_FONT);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setPreferredSize(new Dimension(120, 40));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                setBackground(hoverColor);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                setBackground(normalColor);
                isPressed = false;
            }

            @Override
            public void mousePressed(MouseEvent e) {
                isPressed = true;
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                isPressed = false;
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        int cornerRadius = 8;
        
        // Draw shadow when not pressed
        if (!isPressed) {
            g2.setColor(new Color(0, 0, 0, 25));
            g2.fillRoundRect(2, 3, getWidth() - 2, getHeight() - 2, cornerRadius, cornerRadius);
        }

        // Draw button background
        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, getWidth() - 2, getHeight() - 2, cornerRadius, cornerRadius);

        // Draw border highlight for better definition
        g2.setColor(new Color(255, 255, 255, 50));
        g2.setStroke(new BasicStroke(1));
        g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, cornerRadius, cornerRadius);

        g2.dispose();
        super.paintComponent(g);
    }
}
