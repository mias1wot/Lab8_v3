package com.dreamteam.model;

import java.util.*;

public class Lift {
    Building building;
    final int number;
    int floorToMove;
    final int passengersCapacity;
    final double weightCapacity;
    List <Passenger> passengers;
    int speed;
    int curFloor;
    int curPassengersCount;
    double curWeight;
    boolean doorsAreOpen;
    ThreadState state;
    LiftDirection direction;


    public Lift(Building building, int number, int passengersCapacity, double weightCapacity, int speed){
        this.building = building;
        this.number = number;
        this.passengersCapacity = passengersCapacity;
        this.weightCapacity = weightCapacity;
        this.speed = speed;
    }

    //getters
    public int getNumber(){
        return this.number;
    }
    public int getCurPassengersCount(){
        return this.curPassengersCount;
    }
    public int getFloor(){
        return this.curFloor;
    }
    public int getFloorToMove(){
        return this.floorToMove;
    }
    public List<Passenger> getPassengers(){return this.passengers;}
    public int getSpeed(){
        return speed;
    }
    public LiftDirection getDirection(){
        return direction;
    }

    //setters
    public void setSpeed(int newSpeed){
        this.speed = newSpeed;
    }
    public void setFloorToMove(int floorToMove){
        this.floorToMove = floorToMove;
    }
    public void setDeriction(LiftDirection direction){
        this.direction = direction;
    }

    //methods

    //can be used later
//    public boolean openDoors(){return false;}
//    public boolean closeDoors(){return false;}

    public void move(){
        //moving to floor floorToMove with UI
        while(state != ThreadState.Terminated && curFloor != floorToMove){
            if(state == ThreadState.Paused){
                synchronized(building){
                    while((state == ThreadState.Paused)){
                        try{
                            wait();
                        }
                        catch(Exception e){
                            System.out.println(e);
                        }
                    }
                }
            }
            
            try{
                Thread.sleep(0);
            }
            catch(Exception e){
                System.out.println(e);
            }

            //you need to use speed;
            // UI.moveNextFloor(floorToMove - curFloor);//moves by 1 floor as floor can change as a new passanger comes on nearer floor than the destination and when we can pick them in the middle
        }
    }

    public boolean addPassenger (Passenger passenger){
        if(curPassengersCount + 1 > passengersCapacity)
            return false;
        if(curWeight + passenger.getWeight() > weightCapacity)
            return false;

        curPassengersCount++;
        curWeight += passenger.getWeight();
        return true;
    }

    public void freePassengers(){
        for(Passenger passenger: passengers){
            if(passenger.leaveLift(curFloor)){//clears their data from lift
                passengers.remove(passenger);
                curPassengersCount--;
                curWeight -= passenger.getWeight();
            }
        }
    }

    public void pause(){
        state = ThreadState.Paused;
    }

    public void resume(){
        state = ThreadState.Working;
    }

    public void terminate(){
        state = ThreadState.Terminated;
    }
}
