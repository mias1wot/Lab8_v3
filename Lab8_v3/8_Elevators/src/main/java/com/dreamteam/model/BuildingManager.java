package com.dreamteam.model;

import java.util.*;

class BuildingManager
{
    private Building building;

    public BuildingManager(){
        building = new Building();
    }

    public Building getBuilding(){
        return this.building;
    }

    public void buildFloors(int countFloors, List<Integer> passengerGenerationSpeedForEachFloor)
    {
        for(int i = 0; i < countFloors; ++i)
        {
            this.building.addFloor(
                    new FloorManager(i, countFloors, passengerGenerationSpeedForEachFloor.get(i))//int floorNumber, int countOfQueues, int generationSpeed
            );
        }
    }


    public void buildLifts(int countLift,
                           List<LiftStrategy> strategies,
                           List<Integer> speedForEachLift,
                           List<Double> weightCapacityForEachLift,
                           List<Integer> passengersCapacityForEachLift)
    {
        for(int i = 0; i < countLift; ++i)
        {
            Lift lift = new Lift(
                    building,
                    i,
                    passengersCapacityForEachLift.get(i),
                    weightCapacityForEachLift.get(i),
                    speedForEachLift.get(i)
            );

            LiftManager liftManager = new LiftManager(this.building, lift, strategies.get(i));
            this.building.addLift(
                liftManager 
            );

            new Thread(liftManager).start();//creates Thread
        }

    }
}