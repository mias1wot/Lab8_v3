package com.dreamteam;

import com.dreamteam.model2.Main;
import com.dreamteam.view.*;
import com.dreamteam.view.viewModels.ElevatorViewModel;
import com.dreamteam.view.viewModels.UserQueueViewModel;

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
            var elevator = (ElevatorViewModel)evt.getNewValue();

            ChangeCellColor(table.getColumnModel(), elevator.getNumber(), elevator.getCurrentFloor());

            for(int i = 0; i < Main.floorAmount; i++) {
                table.setValueAt("", i,elevator.getNumber() * 2);

                if(i == Main.floorAmount - elevator.getCurrentFloor()) {
                    table.setValueAt(elevator.getCurrentActiveUserAmount(), i - 1, elevator.getNumber() * 2);
                }
            }
        }

        if(evt.getPropertyName().equals(ObservableProperties.QUEUE_CHANGED.toString())) {
            var userQueue = (UserQueueViewModel)evt.getNewValue();
            var unicodeEmojis = new String[] {
                    "\uD83D\uDC68", // man \ud83d\udc68
                    "\uD83D\uDC68", // woman
            };

            StringBuilder cellText = new StringBuilder();

            for(int i = 0; i < userQueue.getUsersInQueue(); i++) {
                var emoji = unicodeEmojis[random.nextInt(unicodeEmojis.length)];
                cellText.append(emoji);
            }

            var QueueRenderer = new QueueCellRenderer();
            QueueRenderer.setHorizontalAlignment(JLabel.RIGHT);
            table.getColumnModel().getColumn(userQueue.getElevatorNumber() * 2 - 1).setCellRenderer(QueueRenderer);

            table.setValueAt(cellText.toString(),
                    Main.floorAmount - userQueue.getCurrentFloor() - 1,
                    userQueue.getElevatorNumber() * 2 - 1);
        }

        table.repaint();
    }

    public static void ChangeCellColor(TableColumnModel model, int elevatorIndex, int floorIndex)
    {
        var ElevatorRenderer = new ElevatorRenderer(Main.floorAmount - floorIndex - 1);
        ElevatorRenderer.setHorizontalAlignment(JLabel.CENTER);
        model.getColumn(elevatorIndex * 2).setCellRenderer(ElevatorRenderer);
    }
}
