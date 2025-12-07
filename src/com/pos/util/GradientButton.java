package com.pos.util;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

public class GradientButton extends JButton {
    private Color color1;
    private Color color2;
    private boolean isHovered = false;

    public GradientButton(String text, Icon icon) {
        super(text);
        if (icon != null)
            setIcon(icon);
        init();
    }

    public GradientButton(String text) {
        super(text);
        init();
    }

    private void init() {
        this.color1 = Style.GRADIENT_START;
        this.color2 = Style.GRADIENT_END;
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setForeground(Color.WHITE);
        setFont(Style.BOLD_FONT);
        setCursor(new Cursor(Cursor.HAND_CURSOR));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                isHovered = true;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                isHovered = false;
                repaint();
            }
        });
    }

    public void setColors(Color c1, Color c2) {
        this.color1 = c1;
        this.color2 = c2;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();

        Color c1 = isHovered ? color1.brighter() : color1;
        Color c2 = isHovered ? color2.brighter() : color2;

        GradientPaint gp = new GradientPaint(0, 0, c1, width, height, c2);
        g2.setPaint(gp);
        g2.fill(new RoundRectangle2D.Double(0, 0, width, height, 15, 15));

        g2.dispose();
        super.paintComponent(g);
    }
}
