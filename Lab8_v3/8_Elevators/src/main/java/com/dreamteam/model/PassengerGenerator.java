package com.dreamteam.model;

import java.util.*;
import java.lang.*;

// class PassengerGenerator extends Thread {
class PassengerGenerator implements Runnable {
    private Building building;
    private FloorManager floorManager;
    private int generationSpeed;
    private ThreadState state;

    public PassengerGenerator(Building building, FloorManager floorManager, Integer generationSpeed) {
        this.building = building;
        this.floorManager = floorManager;
        this.generationSpeed = generationSpeed;
        state = ThreadState.Paused;
    }

    private Passenger generatePassenger() {
        int neededFloor;
        while(true) {
            neededFloor = (int) ((Math.random() * building.getCountFloor()));
            if (neededFloor != floorManager.getFloor()) {
                break;
            }
        }
        int weight = (int) ((Math.random() * (Passenger.WEIGHT_MAX - Passenger.WEIGHT_MIN)) + Passenger.WEIGHT_MIN);
        return new Passenger(neededFloor, weight);
    }

    public void run() {
        while(state != ThreadState.Terminated) {
            if(state == ThreadState.Paused){
                synchronized(building){
                    while((state == ThreadState.Paused)){
                        try{
                            building.wait();
                        }
                        catch(Exception e){
                            System.out.println(e);
                        }
                    }
                }
            }

            while (state == ThreadState.Working){
                floorManager.addToQueue(this.generatePassenger());
                try{
                    Thread.sleep(this.generationSpeed);
                }
                catch(Exception e){
                    System.out.println(e);
                }
            }
        }
    }

    public void setSpeed(Integer generationSpeed) {
        this.generationSpeed = generationSpeed;
    }

    public void pause() {
        state = ThreadState.Paused;
    }

    public void resume() {
        state = ThreadState.Working;
    }

    public void terminate() {
        state = ThreadState.Terminated;
    }
}