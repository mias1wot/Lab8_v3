package com.dreamteam.model;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

public class IgnoringMiddlePickingUpStrategy implements LiftStrategy {
    private LiftManager liftManager;
    public IgnoringMiddlePickingUpStrategy(LiftManager liftManager) {
        this.liftManager = liftManager;
    }

    public void pickPassengers() {
        Lift lift = liftManager.getLift();
        int curPassengersCount = lift.getCurPassengersCount();
        if(curPassengersCount == 0){//picks people
            liftManager.building.getPassangersOnBoard(lift);
        }
    }
    
    public void checkAndSetFloorToMove() {
        Lift lift = liftManager.getLift();
        int curFloor = lift.getFloor();
        LiftDirection direction = lift.getDirection();
        List<Passenger> passengers = lift.getPassengers();

        int floorToMove = curFloor;
        //finds nearest Up
        Optional<Integer> nearestUpFloor = passengers.stream().map(passenger -> passenger.getNeededFloor()).filter(floor -> floor > curFloor)
            .min((floor1, floor2) -> floor1 - floor2);

        //finds nearest Down
        Optional<Integer> nearestDownFloor = passengers.stream().map(passenger -> passenger.getNeededFloor()).filter(floor -> floor < curFloor)
        .max((floor1, floor2) -> floor1 - floor2);

        if(direction == LiftDirection.Up){
            if(nearestUpFloor.isPresent())
                floorToMove = nearestUpFloor.get();
            else if(nearestDownFloor.isPresent()){
                floorToMove = nearestDownFloor.get();
                lift.setDeriction(LiftDirection.Down);
            }
        }
        if(direction == LiftDirection.Down){
            if(nearestDownFloor.isPresent())
                floorToMove = nearestDownFloor.get();
            else if(nearestUpFloor.isPresent()){
                floorToMove = nearestUpFloor.get();
                lift.setDeriction(LiftDirection.Up);
            }
        }

        lift.setFloorToMove(floorToMove);
    }
}