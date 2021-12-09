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

    public int getLiftIndex() {
        return liftIndex;
    }

    public List<Passenger> getPassengers() {
        return passengers;
    }

    public int getCount() {
        return passengers.size();
    }

    public int getFloor(){return floor;}

    public void addToQueue(Passenger passenger) {
        synchronized (building) {
            passengers.add(passenger);
        }
    }

    public void getPassengersOnBoard(Lift lift) {
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