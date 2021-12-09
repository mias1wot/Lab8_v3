package com.dreamteam.model;

import com.dreamteam.view.ObservableProperties;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.*;
import com.dreamteam.view.viewModels.*;



import static java.lang.Thread.sleep;

public class Lift {
    private Building building;
    private final int number;
    private int floorToMove;
    private final int passengersCapacity;
    private final double weightCapacity;
    private List <Passenger> passengers;
    private int speed;
    private int curFloor;
    private int curPassengersCount;
    private double curWeight;
    private boolean doorsAreOpen;
    private ThreadState state;
    private LiftDirection direction;

    private PropertyChangeSupport support;


    public Lift(Building building, int number, int passengersCapacity, double weightCapacity, int speed, PropertyChangeListener listener){
        this.building = building;
        this.number = number;
        this.passengersCapacity = passengersCapacity;
        this.weightCapacity = weightCapacity;
        this.speed = speed;
        direction = LiftDirection.Up;
        passengers = Collections.synchronizedList(new ArrayList<>());

        support = new PropertyChangeSupport(this);
        support.addPropertyChangeListener(listener);
    }

    //getters
    public synchronized int getNumber(){
        return this.number;
    }
    public synchronized int getCurPassengersCount(){
        return this.curPassengersCount;
    }
    public synchronized int getFloor(){
        return this.curFloor;
    }
    public synchronized int getFloorToMove(){
        return this.floorToMove;
    }
    public synchronized List<Passenger> getPassengers(){return this.passengers;}
    public synchronized int getSpeed(){
        return speed;
    }
    public synchronized LiftDirection getDirection(){
        return direction;
    }

    //setters
    public synchronized void setSpeed(int newSpeed){
        this.speed = newSpeed;
    }
    public synchronized void setFloorToMove(int floorToMove){
        this.floorToMove = floorToMove;
    }
    public synchronized void setDeriction(LiftDirection direction){
        this.direction = direction;
    }

    //methods

    //can be used later
//    public boolean openDoors(){return false;}
//    public boolean closeDoors(){return false;}

    public void move(){
        //moving to floor floorToMove with UI
        while(state != ThreadState.Terminated && curFloor != floorToMove){
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
            
            try{
                Thread.sleep(speed);
            }
            catch(Exception e){
                System.out.println(e);
            }


            //moving lift
            LiftViewModel liftViewModel = new LiftViewModel(number, curFloor, curPassengersCount);
            support.firePropertyChange(ObservableProperties.FLOOR_CHANGED.toString(), null, liftViewModel);

            switch (direction) {//moves by 1 floor as floor can change as a new passanger comes on nearer floor than the destination and when we can pick them in the middle
                case Up:
                    curFloor++;
                    break;
                case Down:
                    curFloor--;
                    break;
            }
        }
    }

    public boolean addPassenger (Passenger passenger){
        if(curPassengersCount + 1 > passengersCapacity)
            return false;
        if(curWeight + passenger.getWeight() > weightCapacity)
            return false;

        passengers.add(passenger);
        curPassengersCount++;
        curWeight += passenger.getWeight();
        return true;
    }

    public void freePassengers(){
        Passenger passenger;
        for(Iterator<Passenger> passengerIt = passengers.iterator(); passengerIt.hasNext();){
            passenger = passengerIt.next();
            if(passenger.leaveLift(curFloor)){//clears their data from lift
                passengerIt.remove();
                curPassengersCount--;
                curWeight -= passenger.getWeight();
            }
        }
    }

    public void pause(){
        state = ThreadState.Paused;
    }

    public void resume(){
        state = ThreadState.Working;
    }

    public void terminate(){
        state = ThreadState.Terminated;
    }
}
