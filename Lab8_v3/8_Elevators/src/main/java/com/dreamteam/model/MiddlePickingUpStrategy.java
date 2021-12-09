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
        liftManager.building.getPassengersOnBoard(lift);//picks people
    }
    
    public void checkAndSetFloorToMove() {
        Lift lift = liftManager.getLift();
        int curFloor = lift.getFloor();
        LiftDirection direction = lift.getDirection();
        List<Passenger> passengers = lift.getPassengers();
        List<Integer> floorsWithWaitingPassengersQueue = liftManager.getFloorsWithWaitingPassengersQueue();

        int floorToMove = curFloor;


        int nearestUpFloor = -1;
        int nearestDownFloor = -1;

        if(!passengers.isEmpty()) {
            //finds nearest Up
            Optional<Integer>  nearestUpFloorOptional = passengers.stream().map(passenger -> passenger.getNeededFloor()).filter(floor -> floor > curFloor)
                    .findAny();

            if(nearestUpFloorOptional.isPresent())
                nearestUpFloor = nearestUpFloorOptional.get();

            //finds nearest Down
            Optional<Integer> nearestDownFloorOptional = passengers.stream().map(passenger -> passenger.getNeededFloor()).filter(floor -> floor < curFloor)
                    .findAny();

            if(nearestDownFloorOptional.isPresent())
                nearestDownFloor = nearestDownFloorOptional.get();
        }
        else if(!floorsWithWaitingPassengersQueue.isEmpty()){//choses among calling floors

            //finds nearest Up
            Optional<Integer>  nearestUpFloorOptional = floorsWithWaitingPassengersQueue.stream().filter(floor -> floor > curFloor).
                    min(Integer::compare);

            if(nearestUpFloorOptional.isPresent())
                nearestUpFloor = nearestUpFloorOptional.get();

            //finds nearest Down
            Optional<Integer> nearestDownFloorOptional = floorsWithWaitingPassengersQueue.stream().filter(floor -> floor < curFloor)
                    .max(Integer::compare);

            if(nearestDownFloorOptional.isPresent())
                nearestDownFloor = nearestDownFloorOptional.get();
        }


        if(direction == LiftDirection.Up){
            if(nearestUpFloor != -1)
                floorToMove = curFloor + 1;
            else if(nearestDownFloor != -1){
                floorToMove = curFloor - 1;
                lift.setDeriction(LiftDirection.Down);
            }
        }
        if(direction == LiftDirection.Down){
            if(nearestDownFloor != -1)
                floorToMove = curFloor - 1;
            else if(nearestUpFloor != -1){
                floorToMove = curFloor + 1;
                lift.setDeriction(LiftDirection.Up);
            }
        }

        lift.setFloorToMove(floorToMove);

//        System.out.println(curFloor + " -> " + floorToMove);
    }
}