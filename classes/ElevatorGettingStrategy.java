package classes;

import classes.elements.*;

public class ElevatorGettingStrategy {

    private elevatorFillStrategy strategy;

    public ElevatorGettingStrategy(elevatorFillStrategy strategy) {
        this.strategy = strategy;
    }

    public boolean canGetPassengers(Elevator elevator) {
        if(elevator.getStrategyGetting()==elevatorFillStrategy.GET_ONLY)
            return false;
        if(elevator.getStrategyGetting()==elevatorFillStrategy.GET_AND_RELEASE && elevator.getPassengers().size() < elevator.getCapacity())
            return true;
        return false;
    }
}
