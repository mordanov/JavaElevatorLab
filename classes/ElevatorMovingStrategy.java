package classes;

import classes.elements.*;

import java.util.List;
import java.util.Random;

public class ElevatorMovingStrategy {

    private passIncomeStrategy strategy;

    public ElevatorMovingStrategy(passIncomeStrategy strategy) {
        this.strategy = strategy;
    }

    public Floor getNextPosition(List<Passenger> passengers, Floor position) {
        switch (strategy) {
            case MIN_STRATEGY:
                return execMinStrategy(passengers, position);
            case MAX_STRATEGY:
                return execMaxStrategy(passengers, position);
            default:
            case RANDOM_STRATEGY:
                return execRandomStrategy(passengers, position);
        }
    }

    private Floor execMinStrategy(List<Passenger> passengers, Floor position) {
        for(Passenger p: passengers) {
            if(p.getDestination().getNumber()<=position.getNumber()) {
                return p.getDestination();
            }
        }
        for(Passenger p: passengers) {
            if(p.getDestination().getNumber()>position.getNumber()) {
                return p.getDestination();
            }
        }
        return position;
    }

    private Floor execMaxStrategy(List<Passenger> passengers, Floor position) {
        for(Passenger p: passengers) {
            if(p.getDestination().getNumber()>position.getNumber()) {
                return p.getDestination();
            }
        }
        for(Passenger p: passengers) {
            if(p.getDestination().getNumber()<=position.getNumber()) {
                return p.getDestination();
            }
        }
        return position;
    }

    private Floor execRandomStrategy(List<Passenger> passengers, Floor position) {
        if(passengers.size()>0) {
            final Random r = new Random();
            int nextfloor = r.nextInt(passengers.size());
            return passengers.get(nextfloor).getDestination();
        }
        else
            return position;
    }

    @Override
    public String toString() {
        return strategy.name();
    }

}
