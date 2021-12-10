package com.dreamteam.model;

import java.util.*;

public class Queue {
    private Building building;
    private int floor;
    private int liftIndex;
    private List<Passenger> passengers;

    public Queue(Building building, int liftIndex, int floor){
        this.building = building;
        this.floor = floor;
        this.liftIndex = liftIndex;
//        passengers = new ArrayList<>();
        passengers = Collections.synchronizedList(new ArrayList<>());
    }

    public synchronized int getLiftIndex() {
        return liftIndex;
    }//lift index is the same as queue index

    public synchronized List<Passenger> getPassengers() {
        return passengers;
    }

    public synchronized int getCount() {
        return passengers.size();
    }

    public synchronized int getFloor(){return floor;}

    public synchronized void addToQueue(Passenger passenger) {
        synchronized (building) {
            passengers.add(passenger);
        }
    }

    public synchronized void getPassengersOnBoard(Lift lift) {
        Passenger passenger;
        synchronized (building) {
            for(Iterator<Passenger> passengerIt = passengers.iterator(); passengerIt.hasNext();){
                passenger = passengerIt.next();
                if (lift.addPassenger(passenger)){
                    passengerIt.remove();
                }
            }
        }
    }
}