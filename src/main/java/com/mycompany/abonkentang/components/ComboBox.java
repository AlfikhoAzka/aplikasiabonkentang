/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.abonkentang.components;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicComboBoxUI;

/**
 *
 * @author Alfikho Azka Dinova - 10125107
 */
public class ComboBox<E> extends JComboBox<E> {

    private static final Color WARNA_BORDER = new Color(70, 71, 174);
    private static final Color WARNA_BORDER_FOKUS = new Color(100, 101, 200);
    private static final Color WARNA_TEKS = new Color(40, 40, 40);

    public ComboBox() {
        setFont(new Font("Segoe UI", Font.PLAIN, 13));
        setForeground(WARNA_TEKS);
        setBackground(Color.WHITE);
        setOpaque(false);
        setFocusable(false);
        setBorder(new EmptyBorder(2, 10, 2, 4));
        setUI(new ModernComboUI());
    }

    @Override
    public java.awt.Dimension getPreferredSize() {
        java.awt.Dimension ukuran = super.getPreferredSize();
        int lebar = Math.max(ukuran.width, 130);
        return new java.awt.Dimension(lebar, 30);
    }
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(Color.WHITE);
        g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 14, 14);
        g2.setColor(hasFocus() ? WARNA_BORDER_FOKUS : WARNA_BORDER);
        g2.setStroke(new BasicStroke(1.4f));
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 14, 14);
        g2.dispose();
        super.paintComponent(g);
    }

    private class ModernComboUI extends BasicComboBoxUI {
        @Override
        protected JButton createArrowButton() {
            JButton tombolPanah = new JButton() {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(WARNA_BORDER);
                    int cx = getWidth() / 2;
                    int cy = getHeight() / 2;
                    int[] xPoints = {cx - 5, cx + 5, cx};
                    int[] yPoints = {cy - 3, cy - 3, cy + 4};
                    g2.fillPolygon(xPoints, yPoints, 3);
                    g2.dispose();
                }
            };
            tombolPanah.setContentAreaFilled(false);
            tombolPanah.setBorderPainted(false);
            tombolPanah.setFocusPainted(false);
            tombolPanah.setOpaque(false);
            return tombolPanah;
        }

        @Override
        public void paintCurrentValueBackground(Graphics g, Rectangle bounds, boolean hasFocus) {
            // dikosongkan biar nggak ada kotak abu-abu default di belakang teks
        }
    }
}
