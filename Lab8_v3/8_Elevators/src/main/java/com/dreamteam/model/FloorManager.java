package com.dreamteam.model;

import java.util.*;
import java.util.stream.*;

public class FloorManager {
    private Building building;
    private int floor;
    private PassengerGenerator passengerGenerator;
    private List<Queue> queues;

    public FloorManager(int floorNumber, int countOfQueues, int generationSpeed) {
        floor = floorNumber;
        passengerGenerator = new PassengerGenerator(building, this, generationSpeed);
        new Thread(passengerGenerator).start();//creates thread
        queues = new ArrayList<>();
        for (var i = 0; i < countOfQueues; i++)
            queues.add(new Queue(i));
    }

    public int getFloor(){
        return floor;
    }

    public void addToQueue(Passenger passenger) {
        Queue queueToAdd = queues
                .stream()
                .min((item1, item2) -> item1.getCount() > item2.getCount() ? 1 : -1)
                .get();

        queueToAdd.addToQueue(passenger);

        building.callLiftAt(queueToAdd.getLiftIndex(), floor);
    }

    public void getPassengersOnBoard(Lift lift) {
        queues
                .get(lift.getNumber())
                .getPassengersOnBoard(lift);
    }

    public List<Integer> getCountOfPassengers() {
        return queues
                .stream()
                .map(item->item.getCount())
                .collect(Collectors.toList());
    }

    public void setPassengerGenerationSpeed(int newSpeed) {
        passengerGenerator.setSpeed(newSpeed);
    }

    public void pause(){
        passengerGenerator.pause();
    }

    public void resume(){
        passengerGenerator.resume();
    }

    public void terminate(){
        passengerGenerator.terminate();
    }
}