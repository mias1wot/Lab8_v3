package com.dreamteam.model;

import com.dreamteam.view.ObservableProperties;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.*;

public class LiftManager implements Runnable {
    Building building;
    Lift lift;
    LiftStrategy liftStrategy;
    Logger logger;
    List<Integer> floorsWithWaitingPassengersQueue;
    ThreadState state;

    private PropertyChangeSupport support;//not needed

    public LiftManager(Building building, Lift lift, PropertyChangeListener listener){
        this.building = building;
        this.lift = lift;
        logger = Logger.getInstance();
        floorsWithWaitingPassengersQueue = new ArrayList<Integer> (building.getCountFloor());
        state = ThreadState.Paused;

        support = new PropertyChangeSupport(this);
        support.addPropertyChangeListener(listener);
    } 

    //getters
    public int getLiftNumber(){
        return lift.getNumber();
    } 
    public Lift getLift(){
        return lift;
    }
    public List<Integer> getFloorsWithWaitingPassengersQueue(){ return floorsWithWaitingPassengersQueue; }

    //setters
    public void setSpeed(int speed){
        lift.setSpeed(speed);
    }
    public void setLiftStategy(LiftStrategy liftStrategy){
        this.liftStrategy = liftStrategy;
    }

    //methods
    public void run(){
//        System.out.println("lift manager: " + lift.getNumber() + ", state: " + state);
        int initFloor;
        int initPassengers;
        int remainingPassengers;
        while(state != ThreadState.Terminated){
            if(state == ThreadState.Paused){
                synchronized(building){
                    while((state == ThreadState.Paused)) {
                        try {
                            building.wait();
                        }
                        catch (Exception e) {}
                    }
                }
            }


            while (state == ThreadState.Working && (lift.getCurPassengersCount() != 0 || !floorsWithWaitingPassengersQueue.isEmpty())){
                initPassengers = lift.getCurPassengersCount();
                lift.freePassengers();
                remainingPassengers = lift.getCurPassengersCount();
                if(initPassengers != remainingPassengers)
                    logger.log(lift.getNumber(), lift.getFloor(), (initPassengers - remainingPassengers) + " passengers left the lift. " + remainingPassengers + " passengers remain in the lift.");

                initPassengers = remainingPassengers;
                liftStrategy.pickPassengers();
                remainingPassengers = lift.getCurPassengersCount();
                if(initPassengers != remainingPassengers)
                    logger.log(lift.getNumber(), lift.getFloor(), (remainingPassengers - initPassengers) + " passengers entered the lift. " + remainingPassengers + " passengers remain in the lift.");

                initFloor = lift.getFloor();
                liftStrategy.checkAndSetFloorToMove();

                if (lift.getFloor() != lift.getFloorToMove()){
                    logger.log(lift.getNumber(), initFloor, "Lift going to move to floor " + lift.getFloorToMove() + ".");

                    lift.move();

                    logger.log(lift.getNumber(), lift.getFloor(), "Lift arrived at floor " + lift.getFloor() + " from floor " + initFloor + ".");
                }
            }
            //if you want, you can leave sleep() here
        }
//        System.out.println(lift.getNumber() + " ended their work");
    }

    public synchronized void addFloorCall(int floor){
        if(!floorsWithWaitingPassengersQueue.contains(floor)){
            floorsWithWaitingPassengersQueue.add(floor);

            int initFloorToMove = lift.getFloorToMove();
            liftStrategy.checkAndSetFloorToMove();

            if(lift.getFloorToMove() != initFloorToMove)
                logger.log(lift.getNumber(), lift.getFloor(), "Lift got call from floor " + lift.getFloorToMove() + ". Going to move there.");
            
        }
    }
    
    public void pause(){
        state = ThreadState.Paused;
        lift.pause();
    }

    public void resume(){
        state = ThreadState.Working;
        lift.resume();
    }

    public void terminate(){
        state = ThreadState.Terminated;
        lift.terminate();
    }
}