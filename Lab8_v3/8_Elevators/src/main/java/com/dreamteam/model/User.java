package com.dreamteam.model;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.*;
import java.util.Timer;

import com.dreamteam.Observer;
import com.dreamteam.view.MainForm;
import com.dreamteam.view.ObservableProperties;
import com.dreamteam.view.viewModels.LiftViewModel;
import lombok.SneakyThrows;



import com.dreamteam.Observer;
import com.dreamteam.view.FloorRenderer;
import com.dreamteam.view.MainForm;
import lombok.SneakyThrows;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.*;
import java.util.List;
import java.util.Timer;

import javax.swing.*;

import static java.awt.Frame.MAXIMIZED_BOTH;

public class User{
    private static Building building;

    private static Observer observer;
    private static boolean working = false;


    public User(){
        createAndShowGUI();
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame();
        var form  = new MainForm();

        Insets scnMax = Toolkit.getDefaultToolkit().getScreenInsets(frame.getGraphicsConfiguration());
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int taskBarSize = scnMax.bottom;
        frame.setSize(screenSize.width, screenSize.height - taskBarSize);
        frame.setExtendedState(MAXIMIZED_BOTH);

        form.getStartButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                initOrResumeEmulation(form);
            }
        });

        form.getStopButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                pauseEmulation();
            }
        });

        form.getLiftSpeedSlider().addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                if(building != null);
                int newSpeed = calculateSleepForLiftSpeed(form.getLiftSpeedSlider().getValue());
                for(int i = 0; i < building.getCountLift(); i++) {
                    building.setLiftSpeed(i, newSpeed);
                }
            }
        });
        form.getGenerationSpeedSlider().addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                if(building != null);
                int newSpeed = calculateSleepForGenerationSpeed(form.getGenerationSpeedSlider().getValue());
                for(int i = 0; i < building.getCountFloor(); i++) {
                    building.setPassengerGenerationSpeed(i, newSpeed);
                }
            }
        });

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent we) {
                terminateEmulation();
                System.exit(0);
            }
        });


        form.getSpinnerFloorAmount().setModel(new SpinnerNumberModel(30, 3, 50, 1));
        form.getSpinnerElevatorAmount().setModel(new SpinnerNumberModel(5, 1, 10, 1));

        frame.setContentPane(form.getRootPanel());
        frame.setVisible(true);

        observer = new Observer(form.getTable1());
    }

    public static int calculateSleepForLiftSpeed(int speed){
        final int maxSpeed = 101;
//        final int multiplier = 10;
        final int multiplier = 5;
        return (maxSpeed - speed) * multiplier;
    }
    public static int calculateSleepForGenerationSpeed(int speed){
        final int maxSpeed = 101;
        final int multiplier = 20;
        return (maxSpeed - speed) * multiplier;
//        return 1000;
    }

    private static void initOrResumeEmulation(MainForm form) {
        if (working)
            resumeEmulation();
        else
            initializeEmulation(form);
    }

    public static void initializeEmulation(MainForm form){
        //reads user input data
        int countLift = (int)form.getSpinnerElevatorAmount().getValue();
        int countFloors = (int)form.getSpinnerFloorAmount().getValue();
        int liftSpeed = calculateSleepForLiftSpeed((int)form.getLiftSpeedSlider().getValue());// the more the speed, the more is sleeps
        int generationSpeed = calculateSleepForGenerationSpeed((int)form.getGenerationSpeedSlider().getValue());

        ArrayList<Integer> speedForEachLift = new ArrayList<>(countLift);
        ArrayList<Integer> passengerGenerationSpeedForEachFloor = new ArrayList<>(countFloors);

        ArrayList<Double> weightCapacityForEachLift = new ArrayList<>(countLift);
        ArrayList<Integer> passengersCapacityForEachLift = new ArrayList<>(countLift);

        for (int i = 0; i < countLift; ++i) {
            speedForEachLift.add(generationSpeed);
            weightCapacityForEachLift.add(Double.MAX_VALUE);
            passengersCapacityForEachLift.add(10);
        }
        for (int i = 0; i < countFloors; ++i) {
            passengerGenerationSpeedForEachFloor.add(generationSpeed);
        }


        clearTable(form);
        createTable(form, countLift, countFloors);

        String strategy = Objects.requireNonNull(form.getComboBoxStrategy().getSelectedItem()).toString();


        List<Integer> strategiesNumbers = new ArrayList<>(countFloors * countLift);


        //sets strategies
        if(strategy.equals("Strategy A")){
            for (int i = 0; i < countLift; ++i) {
                strategiesNumbers.add(0);
            }
        }

        if(strategy.equals("Strategy B")){
            for (int i = 0; i < countLift; ++i) {
                strategiesNumbers.add(1);
            }
        }


        BuildingManager buildingManager = new BuildingManager();
        buildingManager.buildFloors(countFloors, countLift, passengerGenerationSpeedForEachFloor, observer);
        buildingManager.buildLifts(countLift, strategiesNumbers, speedForEachLift,
                weightCapacityForEachLift, passengersCapacityForEachLift, observer);
        building = buildingManager.getBuilding();// in the end of initialize emulation



        //makes all threads to run
        resumeEmulation();

        working = true;
    }


    private static void clearTable(MainForm form){
        DefaultTableModel model = (DefaultTableModel)form.getTable1().getModel();
        model.setRowCount(0);
        model.setColumnCount(1);
    }

    private static void createTable(MainForm form, int countLift, int countFloors) {
        DefaultTableModel model = (DefaultTableModel)form.getTable1().getModel();

//        getNumbers(form);
        var table = form.getTable1();
        model.addColumn("Floor");

        for(int i = 0; i < countLift * 2-1; i++) {
            model.addColumn("Elevator #" + i);
        }

        for(int i = 0; i < countFloors; i++) {
            model.addRow(new Object[countLift]);
            model.setValueAt(countFloors - i, i, 0);
        }

        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.getColumnModel().getColumn(0).setMaxWidth(50);
        table.setRowHeight(form.getTable1().getHeight() / countFloors);

        FloorRenderer FR = new FloorRenderer();
        FR.setHorizontalAlignment( JLabel.CENTER );
        FR.setBackground(new Color(173,232,244));
        table.getColumnModel().getColumn(0).setCellRenderer(FR);
    }


    public static void pauseEmulation(){
        building.pause();
    }


    public static void resumeEmulation(){
        building.resume();

        synchronized(building) {
            building.notifyAll();
        }
    }

    public static void terminateEmulation(){
        if(building != null)
            building.terminate();
        Logger.getInstance().close();
    }


    public static Building getBuilding(){
        return building;
    }


//    public void mock(){
//        You need to read this from the form //if you're going to change smth, it's a good mock
//        countFloors = 5;
//        countLift = 2;
//        ArrayList<Integer> passengerGenerationSpeedForEachFloor = new ArrayList<Integer>(Arrays.asList(600, 600, 600, 600, 600));//passenger generation speed
//        ArrayList<Integer>  speedForEachLift = new ArrayList<Integer>(Arrays.asList(400, 400));//speed for each lift
//        ArrayList<Double> weightCapacityForEachLift = new ArrayList<Double>(Arrays.asList(Double.MAX_VALUE, Double.MAX_VALUE)); //weight capacity for each list
//        ArrayList<Integer> passengersCapacityForEachLift = new ArrayList<Integer>(Arrays.asList(6, 4)); //passenger capacity of lifts
//
//    }

}