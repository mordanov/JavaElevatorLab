package classes;

import classes.elements.Outputter;
import classes.elements.eDirection;
import classes.elements.elevatorFillStrategy;
import classes.elements.passIncomeStrategy;

import java.util.ArrayList;
import java.util.List;

// каждый лифт - в своем потоке
public class Elevator extends Outputter implements Runnable {

    private final int capacity;

    private List<Passenger> passengers = new ArrayList<>();   // пассажиры внутри лифта
    private Floor position;                                   // текущий этаж
    private final int id;
    protected final Building b;
    private boolean active;
    private int movedPassCount = 0;
    private int maxPassCount = 0;

    private ElevatorMovingStrategy strategyMoving;            // тактика поведения, когда внутри есть пассажиры
    private ElevatorGettingStrategy strategyGetting;          // тактика поведения, когда внутри нет пассажиров
    private passIncomeStrategy estrategy;
    private elevatorFillStrategy fstrategy;
    protected eDirection direction;

    public Elevator(int number, Floor position, int id, int capacity, Building building) {
        this.position = position;
        this.id = id;
        this.capacity = capacity;
        this.b = building;
        switch (number%3) {
            case 0:
                this.estrategy = passIncomeStrategy.MAX_STRATEGY;
                break;
            case 1:
                this.estrategy = passIncomeStrategy.MIN_STRATEGY;
                break;
            default:
                this.estrategy = passIncomeStrategy.RANDOM_STRATEGY;
                break;
        }
        switch (number%2) {
            case 0:
                this.fstrategy = elevatorFillStrategy.GET_ONLY;
            case 1:
            default:
                this.fstrategy = elevatorFillStrategy.GET_AND_RELEASE;
        }

        this.strategyGetting = new ElevatorGettingStrategy(this.fstrategy);
        this.strategyMoving = new ElevatorMovingStrategy(this.estrategy);
        this.setDirection(eDirection.OFFLINE);

        Thread t = new Thread(this, "Лифт " + getElevatorId());
        this.active = true;
        t.start();
    }

    public passIncomeStrategy getStrategyMoving() {
        return estrategy;
    }

    public elevatorFillStrategy getStrategyGetting() {
        return fstrategy;
    }

    //переместиться на другой этаж
    public Elevator move2position(Floor f) throws InterruptedException {
        if(f!=null) {
            if (!position.equals(f)) {
                outprintf("Лифт %d переместился с этажа %d на этаж %d\n", this.getElevatorId(), this.position.getNumber(), f.getNumber());
                moveInfo(this.position.getNumber(), f.getNumber());    // сообщить куда едем
                this.position = f;
                Thread.sleep(1000);
                outPassengers(f); // выгрузить пассажиров
                if ((strategyGetting.canGetPassengers(this) && position.getPassengers().size() > 0))
                    inPassengers(position);
            } else {
                inPassengers(position); // загрузить пассажиров
            }
            elevInfo();              // сообщить о себе
        }
        return this;
    }

    protected void elevInfo() {};                             // сообщение после окончания выгрузки/загрузки пассажиров
    protected void moveInfo(int source, int destination) {};  // сообщение перед движением на другой этаж
    protected void finishInfo(Floor position) {};             // сообщение перед окончанием работы

    // выгрузить пассажиров
    protected Elevator outPassengers(Floor f) {
        synchronized (f) {
            for (Passenger p : new ArrayList<>(getPassengers())) {
                if (p.getDestination().equals(f)) {
                    p.setOutTime(b.getTimer());
                    drawPassengerOutTime(p);
                    passengers.remove(p);
                    f.incDestination_pass_count();
                    f.getOutpassengers().add(p);
                    f.drawFloorOutPassenger(f.getOutpassengers().size());
                    b.getNames().insertInfo(p.getFirstname(), p.getSecondname(), p.getSource().getNumber(), p.getDestination().getNumber(),
                            p.getCreateTime() / 1000.0 , p.getInTime() / 1000.0, p.getOutTime() / 1000.0,
                            this.strategyMoving.toString());
                    outprintf("Пассажир id=%d доставлен на этаж id=%d лифтом %d\n", p.getId(), f.getNumber(), this.getElevatorId());
                }
            }
        }
        return this;
    }

    protected void drawPassengerOutTime(Passenger p) {}   // для fx - нарисовать пассажира на выходе
    protected void drawPassengerInTime(Passenger p) {}    // для fx - нарисовать пассажира на входе

    //загрузить пассажиров
    protected Elevator inPassengers(Floor f) {
        synchronized (f) {
            for (Passenger p : new ArrayList<>(f.getPassengers())) {
                if ((p.getSource() == f) && (passengers.size()<capacity)) {
                    p.setInTime(b.getTimer());
                    drawPassengerInTime(p);
                    passengers.add(p);
                    movedPassCount++;
                    f.removePassenger(p);
                    outprintf("Пассажир id=%d сел в лифт id=%d на этаже %d\n", p.getId(), this.getElevatorId(), f.getNumber());
                }
            }
            if(passengers.size()>maxPassCount)
                maxPassCount = passengers.size();
        }
        return this;
    }

    public List<Passenger> getPassengers() {
        return passengers;
    }

    public int getElevatorId() {
        return id;
    }

    public int getMovedPassCount() {
        return movedPassCount;
    }

    private int getPassCount() {
        return passengers.size();
    }

    public int getMaxPassCount() {
        return maxPassCount;
    }

    public boolean isActive() {
        return active;
    }

    @Override
    public void run() {

        // если не активен генератор пассажиров - стоять на месте
        while(!b.isPassGeneratorActive()) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        try {
            // пока есть пассажиры на этажах и в лифте и если активен генератор пассажиров - работать!
            while (((b.getPasscount() + this.getPassCount() > 0) && active) || b.isPassGeneratorActive()) {
                Floor f = null;
                while (f == null && b.getPasscount() > 0)
                    f = b.getFloorWithPassengers(this);
                active = f!=null;
                if (this.getPassCount() == 0) {
                    setDirection(eDirection.GET_PASSENGERS);
                    move2position(f);
                }
                else {
                    setDirection(eDirection.PUT_PASSENGERS);
                    move2position(strategyMoving.getNextPosition(passengers, position));
                }
            }
        }
        catch (InterruptedException ex) {
            outprintf("Лифт %d - работа прервана\n", getElevatorId());
        }
        active = false;
        outprintf("Лифт %d завершил работу\n", getElevatorId());
        b.checkActiveElevators(this);
        finishInfo(position);
    }

    public void setInActive() {
        active = false;
    }

    protected void setDirection(eDirection direction) {
        this.direction = direction;
    }

    @Override
    public boolean equals(Object o) {
        if(!(o instanceof Elevator)) {
            return false;
        }
        Elevator e = (Elevator)o;
        return (e.id==this.id);
    }

    @Override
    public void outprintln(String message) {
        System.out.println(message);
    }

    @Override
    public void outprintf(String format, Object... args) {
        System.out.printf(format, args);
    }

    public int getCapacity() {
        return capacity;
    }
}
