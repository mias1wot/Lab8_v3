package com.dreamteam.model;

import com.dreamteam.model.LiftStrategy;

import java.beans.PropertyChangeListener;
import java.util.*;
import com.dreamteam.Observer;

class BuildingManager
{
    private Building building;

    public BuildingManager(){
        building = new Building();
    }

    public Building getBuilding(){
        return this.building;
    }

    public void buildFloors(int countFloors, int countLift, List<Integer> passengerGenerationSpeedForEachFloor, PropertyChangeListener listener)
    {
        for(int i = 0; i < countFloors; ++i)
        {
            this.building.addFloor(
                    new FloorManager(this.building, i, countLift, passengerGenerationSpeedForEachFloor.get(i), listener)//int floorNumber, int countOfQueues, int generationSpeed
            );
        }
    }


    public void buildLifts(int countLift,
                           List<Integer> strategiesNumbers,
                           List<Integer> speedForEachLift,
                           List<Double> weightCapacityForEachLift,
                           List<Integer> passengersCapacityForEachLift,
                           Observer observer)
    {
        ArrayList <Thread> threads = new ArrayList<>();
        LiftStrategy strategy = null;
        for(int i = 0; i < countLift; ++i)
        {
            Lift lift = new Lift(
                    this.building,
                    i,
                    passengersCapacityForEachLift.get(i),
                    weightCapacityForEachLift.get(i),
                    speedForEachLift.get(i),
                    observer
            );

            LiftManager liftManager = new LiftManager(this.building, lift, observer);

            switch(strategiesNumbers.get(i)) {
                case 0:
                    strategy = new MiddlePickingUpStrategy(liftManager);
                    break;
                case 1:
                    strategy = new IgnoringMiddlePickingUpStrategy(liftManager);
                    break;
            }

            liftManager.setLiftStategy(strategy);
            this.building.addLift(
                liftManager 
            );

            //new Thread(liftManager).start()
            threads.add(new Thread(liftManager));//creates Thread
        }
        for(Thread thread: threads)
            thread.start();

    }
}