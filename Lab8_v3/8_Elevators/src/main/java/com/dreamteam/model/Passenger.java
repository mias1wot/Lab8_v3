package com.dreamteam.model;

public class Passenger {
    private final int neededFloor;
    private final double weight;
    public static int WEIGHT_MAX = 200;
    public static int WEIGHT_MIN = 30;

    public Passenger(int neededFloor, double weight) {
        this.neededFloor = neededFloor;
        this.weight = weight;
    }

    public boolean enterLift(Lift lift) {
        return lift.addPassenger(this);
    }

    public boolean leaveLift(int curFloor) {
        return neededFloor == curFloor;
    }

    public int getNeededFloor() {
        return neededFloor;
    }

    public double getWeight() {
        return weight;
    }

    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        if (!super.equals(object)) return false;

        Passenger passenger = (Passenger) object;

        if (neededFloor != passenger.neededFloor) return false;
        if (java.lang.Double.compare(passenger.weight, weight) != 0) return false;

        return true;
    }

    public int hashCode() {
        int result = super.hashCode();
        long temp;
        result = 31 * result + neededFloor;
        temp = Double.doubleToLongBits(weight);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }
}