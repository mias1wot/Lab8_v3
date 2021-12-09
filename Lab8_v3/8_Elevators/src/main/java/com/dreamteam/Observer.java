package com.dreamteam;

import com.dreamteam.view.*;
import com.dreamteam.view.viewModels.*;
import com.dreamteam.model.*;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Random;

public class Observer implements PropertyChangeListener {
    public JTable table;
    private static Random random = new Random();

    public Observer(JTable table) {
        this.table = table;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if(evt.getPropertyName().equals(ObservableProperties.FLOOR_CHANGED.toString())) {
            var liftManager = (LiftViewModel)evt.getNewValue();
            int countFloor = User.getBuilding().getCountFloor();

            ChangeCellColor(table.getColumnModel(), liftManager.getLiftNumber(), liftManager.getFloor());

            for(int i = 0; i < countFloor; i++) {
                table.setValueAt("", i,(liftManager.getLiftNumber() + 1) * 2);//2 4 6
            }
            table.setValueAt(liftManager.getCurPassengersCount(), countFloor - liftManager.getFloor() - 1, (liftManager.getLiftNumber() + 1) * 2);//2 4 6
        }

        if(evt.getPropertyName().equals(ObservableProperties.QUEUE_CHANGED.toString())) {
            var queueViewModel = (QueueViewModel)evt.getNewValue();
            int countFloor = User.getBuilding().getCountFloor();
            var unicodeEmojis = new String[] {
                    "\uD83D\uDC68", // man \ud83d\udc68
                    "\uD83D\uDC68", // woman
            };

            StringBuilder cellText = new StringBuilder();

            for(int i = 0; i < queueViewModel.getCount(); i++) {
                var emoji = unicodeEmojis[random.nextInt(unicodeEmojis.length)];
                cellText.append(emoji);
            }

            var QueueRenderer = new QueueCellRenderer();
            QueueRenderer.setHorizontalAlignment(JLabel.RIGHT);
            table.getColumnModel().getColumn(queueViewModel.getLiftIndex() * 2 + 1).setCellRenderer(QueueRenderer);//1 3 5

            //System.out.println(queueViewModel.getLiftIndex());
            table.setValueAt(cellText.toString(),
                    countFloor - queueViewModel.getFloor() - 1,
                    queueViewModel.getLiftIndex()  * 2 + 1);//1 3 5
        }

        table.repaint();
    }

    public static void ChangeCellColor(TableColumnModel model, int liftNumber, int floorIndex)
    {
        int countFloor = User.getBuilding().getCountFloor();
        var ElevatorRenderer = new ElevatorRenderer(countFloor - floorIndex - 1);
        ElevatorRenderer.setHorizontalAlignment(JLabel.CENTER);
        model.getColumn((liftNumber + 1) * 2).setCellRenderer(ElevatorRenderer);//1 3 5
    }
}
