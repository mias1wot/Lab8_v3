package com.dreamteam.model;

import java.util.*;

public class Queue {
    private int liftIndex;
    private List<Passenger> passengers;

    public Queue(int index){
        liftIndex = index;
        passengers = new ArrayList<>();
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

    public void addToQueue(Passenger passenger) {
        passengers.add(passenger);
    }

    public void getPassengersOnBoard(Lift lift) {
        for (Passenger passenger : passengers) {
            if (lift.addPassenger(passenger)) {
                passengers.remove(passenger);
            }
        }
    }
}