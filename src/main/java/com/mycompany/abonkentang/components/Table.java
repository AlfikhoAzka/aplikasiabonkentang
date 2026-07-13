/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.abonkentang.components;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.border.EmptyBorder;

/**
 *
 * @author Alfikho Azka Dinova - 10125107
 */
public class Table extends JTable {

    public Table() {
        init();
    }

    private void init() {

        Color bgTable = new Color(45, 45, 45);
        Color bgHeader = new Color(60, 60, 60);
        Color bgSelect = new Color(70, 71, 174);

        setRowHeight(36);
        setShowGrid(false);
        setIntercellSpacing(new Dimension(0, 0));
        setShowHorizontalLines(false);
        setShowVerticalLines(false);
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        setBackground(bgTable);
        setForeground(new Color(230,230,230));

        setSelectionBackground(bgSelect);
        setSelectionForeground(Color.WHITE);

        setFont(new Font("Segoe UI", Font.PLAIN, 13));

        JTableHeader header = getTableHeader();
        header.setReorderingAllowed(false);
        header.setResizingAllowed(true);
        header.setPreferredSize(new Dimension(0, 38));

        header.setBackground(new Color(70,71,174));
        header.setForeground(Color.BLACK);
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setReorderingAllowed(false);
        header.setResizingAllowed(true);
        header.setPreferredSize(new Dimension(0,38));

        setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(
                    JTable table, Object value,
                    boolean isSelected, boolean hasFocus,
                    int row, int column) {

                super.getTableCellRendererComponent(
                        table, value,
                        isSelected, hasFocus,
                        row, column);

                if (isSelected) {
                    setBackground(bgSelect);
                    setForeground(Color.WHITE);
                } else {
                    if (row % 2 == 0) {
                        setBackground(new Color(45,45,45));
                    } else {
                        setBackground(new Color(55,55,55));
                    }
                    setForeground(new Color(230,230,230));
                }
                setBorder(new EmptyBorder(0,10,0,10));
                return this;
            }
        });
    }
}
