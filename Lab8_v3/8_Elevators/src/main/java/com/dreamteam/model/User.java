package com.dreamteam.model;

import java.util.*;

class User
{
    private Building building;

    public User(
            int countFloors,
            List<Integer> passengerGenerationSpeedForEachFloor,
            int countLift,
            List<Integer> strategiesNumbers,
            List<Integer> speedForEachLift,
            List<Double> weightCapacityForEachLift,
            List<Integer> passengersCapacityForEachLift
    ){
        BuildingManager buildingManager = new BuildingManager();
        buildingManager.buildFloors(countFloors, passengerGenerationSpeedForEachFloor);
        buildingManager.buildLifts(countLift, strategiesNumbers, speedForEachLift,
                weightCapacityForEachLift, passengersCapacityForEachLift);

        this.building = buildingManager.getBuilding();

    }

    public Building initializeEmulation(){
        return this.building;
    }

    public void pauseEmulation(){
        this.building.pause();
    }


    public void resumeEmulation(){
        this.building.resume();

        synchronized(building) {
            notifyAll();
        }
    }

    public void terminateEmulation(){
        this.building.terminate();
        Logger.getInstance().close();
    }

}