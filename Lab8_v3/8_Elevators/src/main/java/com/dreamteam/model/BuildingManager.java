package com.dreamteam.model;

import com.dreamteam.model.LiftStrategy;

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
                           List<Integer> strategiesNumbers,
                           List<Integer> speedForEachLift,
                           List<Double> weightCapacityForEachLift,
                           List<Integer> passengersCapacityForEachLift)
    {
        LiftStrategy strategy = null;
        for(int i = 0; i < countLift; ++i)
        {
            Lift lift = new Lift(
                    building,
                    i,
                    passengersCapacityForEachLift.get(i),
                    weightCapacityForEachLift.get(i),
                    speedForEachLift.get(i)
            );

            LiftManager liftManager = new LiftManager(this.building, lift);

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

            new Thread(liftManager).start();//creates Thread
        }

    }
}