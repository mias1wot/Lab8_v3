package com.dreamteam.model;

import com.dreamteam.view.viewModels.*;

import com.dreamteam.view.ObservableProperties;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.*;
import java.util.stream.*;

public class FloorManager {
    private Building building;
    private int floor;
    private PassengerGenerator passengerGenerator;
    private List<Queue> queues;

    private PropertyChangeSupport support;

    public FloorManager(Building building, int floorNumber, int countOfQueues, int generationSpeed, PropertyChangeListener listener) {
        this.building = building;
        floor = floorNumber;
        passengerGenerator = new PassengerGenerator(building, this, generationSpeed);
        new Thread(passengerGenerator).start();//creates thread
        queues = Collections.synchronizedList(new ArrayList<>());
        for (var i = 0; i < countOfQueues; i++)
            queues.add(new Queue(building, i, floor));

        support = new PropertyChangeSupport(this);
        support.addPropertyChangeListener(listener);
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

//        if(queueToAdd.getLiftIndex() == 1)
//            System.out.println(queueToAdd.getLiftIndex());

        building.callLiftAt(queueToAdd.getLiftIndex(), floor);

        QueueViewModel queueViewModel = new QueueViewModel(queueToAdd.getCount(), queueToAdd.getLiftIndex(), floor);
        support.firePropertyChange(ObservableProperties.QUEUE_CHANGED.toString(), null, queueViewModel);
    }

    public void getPassengersOnBoard(Lift lift) {
        Queue curQueue = queues.get(lift.getNumber());
        curQueue.getPassengersOnBoard(lift);

        QueueViewModel queueViewModel = new QueueViewModel(curQueue.getCount(), curQueue.getLiftIndex(), floor);
        support.firePropertyChange(ObservableProperties.QUEUE_CHANGED.toString(), null, queueViewModel);
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