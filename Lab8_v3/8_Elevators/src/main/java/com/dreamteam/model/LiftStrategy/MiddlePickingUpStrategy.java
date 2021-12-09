package com.dreamteam.model;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

public class MiddlePickingUpStrategy implements LiftStrategy {
    private LiftManager liftManager;
    public MiddlePickingUpStrategy(LiftManager liftManager) {
        this.liftManager = liftManager;
    }

    public void pickPassengers() {
        Lift lift = liftManager.getLift();
        liftManager.building.getPassangersOnBoard(lift);//picks people
    }
    
    public void checkAndSetFloorToMove() {
        Lift lift = liftManager.getLift();
        int curFloor = lift.getFloor();
        LiftDirection direction = lift.getDirection();
        List<Passenger> passengers = lift.getPassengers();

        int floorToMove = curFloor;
        //finds nearest Up
        Optional<Integer> nearestUpFloor = passengers.stream().map(passenger -> passenger.getNeededFloor()).filter(floor -> floor > curFloor)
            .findAny();

        //finds nearest Down
        Optional<Integer> nearestDownFloor = passengers.stream().map(passenger -> passenger.getNeededFloor()).filter(floor -> floor < curFloor)
        .findAny();

        if(direction == LiftDirection.Up){
            if(nearestUpFloor.isPresent())
                floorToMove = curFloor + 1;
            else if(nearestDownFloor.isPresent()){
                floorToMove = curFloor - 1;
                lift.setDeriction(LiftDirection.Down);
            }
        }
        if(direction == LiftDirection.Down){
            if(nearestDownFloor.isPresent())
                floorToMove = curFloor - 1;
            else if(nearestUpFloor.isPresent()){
                floorToMove = curFloor + 1;
                lift.setDeriction(LiftDirection.Up);
            }
        }

        lift.setFloorToMove(floorToMove);
    }
}