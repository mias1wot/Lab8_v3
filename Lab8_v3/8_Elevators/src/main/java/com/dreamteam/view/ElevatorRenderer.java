package com.dreamteam.view;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class ElevatorRenderer extends DefaultTableCellRenderer {
    private int _floor;

    public ElevatorRenderer(int floor)
    {
        this._floor = floor;
    }

    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
    {
        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        c.setBackground(Color.WHITE);
        c.setForeground(Color.WHITE);

        if (row == _floor ) {
            c.setBackground(new Color(77,128,173));
        }

        return c;
    }
}