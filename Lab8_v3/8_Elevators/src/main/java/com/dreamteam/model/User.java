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
                System.out.println("Slider2: " + form.getLiftSpeedSlider().getValue());
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
        frame.pack();
        frame.setVisible(true);

        observer = new Observer(form.getTable1());
    }

    private static void initOrResumeEmulation(MainForm form) {
//        int liftSpeed = (int)form.getLiftSpeed().getValue();
//        int generationSpeed = (int)form.getGenerationSpeed().getValue();
//        System.out.println(liftSpeed);
//        System.out.println(generationSpeed);
        if (working)
            resumeEmulation();
        else
            initializeEmulation(form);
    }

    public static void initializeEmulation(MainForm form){
        final int maxSpeed = 101;
        final int multiplier = 50;
        //reads user input data
        int countLift = (int)form.getSpinnerElevatorAmount().getValue();
        int countFloors = (int)form.getSpinnerFloorAmount().getValue();
        int liftSpeed = (maxSpeed - (int)form.getLiftSpeedSlider().getValue()) * multiplier;// the more the speed, the more is sleeps
        int generationSpeed =(maxSpeed - (int)form.getGenerationSpeedSlider().getValue()) * multiplier;
        System.out.println(liftSpeed);
        System.out.println(generationSpeed);

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

        //You need to read this from the form //todo
//        countFloors = 5;
//        countLift = 2;
//        ArrayList<Integer> passengerGenerationSpeedForEachFloor = new ArrayList<Integer>(Arrays.asList(600, 600, 600, 600, 600));//passenger generation speed
//        ArrayList<Integer>  speedForEachLift = new ArrayList<Integer>(Arrays.asList(400, 400));//speed for each lift
//        ArrayList<Double> weightCapacityForEachLift = new ArrayList<Double>(Arrays.asList(Double.MAX_VALUE, Double.MAX_VALUE)); //weight capacity for each list
//        ArrayList<Integer> passengersCapacityForEachLift = new ArrayList<Integer>(Arrays.asList(6, 4)); //passenger capacity of lifts
//


//        countFloors = 10;
//                ArrayList<Integer> passengerGenerationSpeedForEachFloor =        new ArrayList<Integer>(Arrays.asList(5, 1, 1, 2, 3, 4, 4, 2, 2, 1));//passenger generation speed
//                countLift = 5;
//                ArrayList<Integer> strategiesNumbers =         new ArrayList<Integer>(Arrays.asList(1, 0, 1, 0, 1));//strategies num
//                ArrayList<Integer>  speedForEachLift =        new ArrayList<Integer>(Arrays.asList(1000, 400, 500, 400, 500));//speed for each lift
//                ArrayList<Double> weightCapacityForEachLift  =         new ArrayList<Double>(Arrays.asList(Double.MAX_VALUE, Double.MAX_VALUE, 1500.0, 1000.0, 1200.0)); //weight capacity for each list
//                ArrayList<Integer> passengersCapacityForEachLift = new ArrayList<Integer>(Arrays.asList(9, 7, 4, 7, 5)); //passenger capacity of lifts




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

}