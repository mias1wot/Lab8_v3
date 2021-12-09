package com.dreamteam.model;

import java.util.*;

class Building
{
    //fields
    private List<LiftManager> liftManagers;
    private List<FloorManager> floorManagers;
    private int countLift = 0;
    private int countFloor = 0;

    //constructor
    public Building() {
        liftManagers = new ArrayList<>();
        floorManagers = new ArrayList<>();
    }

    //getters
    public int getCountLift(){
        return this.countLift;
    }

    public int getCountFloor(){
        return this.countFloor;
    }

    //logic
    public void addLift(LiftManager liftManager) {
        liftManagers.add(liftManager);
        countLift++;
    }

    public void addFloor(FloorManager floorManager) {
        floorManagers.add(floorManager);
        countFloor++;
    }

    public void setLiftSpeed(int liftIndex, int newSpeed) {
        liftManagers.get(liftIndex).getLift().setSpeed(newSpeed);
    }

    public void setPassengerGenerationSpeed(int floorNumber, int newSpeed) {
        floorManagers.get(floorNumber).setPassengerGenerationSpeed(newSpeed);
    }

    public void setLiftStrategyAt(int liftNumber, LiftStrategy strategy) {
        liftManagers.get(liftNumber).setLiftStategy(strategy);
    }

    public void getPassangersOnBoard(Lift lift) {
        floorManagers.get(lift.getFloor()).getPassengersOnBoard(lift);
    }

    public void callLiftAt(int liftNumber, int floor){
        liftManagers.get(liftNumber).addFloorCall(floor);
    }

    public void pause(){
        for (var liftManager : liftManagers)
        {
            liftManager.pause();
        }
        for (var floorManager : floorManagers)
        {
            floorManager.pause();
        }
    }

    public void resume(){
        for (var liftManager : liftManagers)
        {
            liftManager.resume();
        }
        for (var floorManager : floorManagers)
        {
            floorManager.resume();
        }
    }

    public void terminate(){
        for (var liftManager : liftManagers)
        {
            liftManager.terminate();
        }
        for (var floorManager : floorManagers)
        {
            floorManager.terminate();
        }
    }
}