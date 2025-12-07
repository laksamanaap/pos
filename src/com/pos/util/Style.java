package com.pos.util;

import java.awt.Color;
import java.awt.Font;

public class Style {
    // Colors
    public static final Color PRIMARY_COLOR = new Color(100, 100, 255); // Light Blue-ish
    public static final Color SECONDARY_COLOR = new Color(120, 80, 200); // Purple-ish
    public static final Color BACKGROUND_COLOR = new Color(245, 245, 250);
    public static final Color TEXT_COLOR = new Color(50, 50, 50);
    public static final Color DANGER_COLOR = new Color(220, 53, 69);
    public static final Color SUCCESS_COLOR = new Color(40, 167, 69);
    public static final Color WARNING_COLOR = new Color(255, 193, 7);

    // Gradients (Start and End)
    public static final Color GRADIENT_START = new Color(110, 130, 240);
    public static final Color GRADIENT_END = new Color(140, 90, 210);

    // Fonts
    public static final Font HEADER_FONT = new Font("Segoe UI", Font.BOLD, 24);
    public static final Font SUBHEADER_FONT = new Font("Segoe UI", Font.BOLD, 18);
    public static final Font REGULAR_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    // Use Segoe UI Emoji for buttons to support icons
    public static final Font BOLD_FONT = new Font("Segoe UI Emoji", Font.BOLD, 14);
    public static final Font MONO_FONT = new Font("Consolas", Font.PLAIN, 12);
}
