package classes;

import classes.elements.Outputter;
import sql.NameGenerator;

import java.util.ArrayList;
import java.util.List;

public class Floor extends Outputter {
    private final int number;                                 //номер этажа
    private List<Passenger> passengers = new ArrayList<>();   //пассажиры, ждущие лифт
    private List<Passenger> outpassengers = new ArrayList<>();
    protected Building building;

    private int source_pass_count = 0;
    private int destination_pass_count = 0;

    protected final int maxvisiblepass = 10;

    public Floor(int number, Building building) {
        this.number = number;
        this.building = building;
    }

    public int getNumber() {
        return number;
    }

    public int getIndex() {
        return number - 1;
    }

    public List<Passenger> getOutpassengers() {
        return outpassengers;
    }

    protected void drawFloorInPassenger(int passcount) {};
    protected void drawFloorOutPassenger(int passcount) {}
    protected void floorInfo() {};

    @Override
    public boolean equals(Object o) {
        if(!(o instanceof Floor)) {
            return false;
        }
        Floor f = (Floor)o;
        return f.number == this.number;
    }

    //появился пассажир (функцию вызывает здание)
    public Passenger addPassenger(Floor destination, long id, NameGenerator namegen) {
        Passenger p;
        synchronized (this) {
            namegen.generate();
            p = new Passenger(this, destination, id, building.getTimer(), namegen.getFirst(), namegen.getSecond());
            passengers.add(p);
            source_pass_count++;
            drawFloorInPassenger(passengers.size());
            outprintf("На этаж %d добавлен пассажир (" + p.getFirstname() + " " + p.getSecondname() + "): на этаж %d, id=%d\n", this.getNumber(), destination.getNumber(), id);
        }
        floorInfo();
        return p;
    }

    //пассажир сел в лифт (функция вызывается лифтом)
    public Floor removePassenger(Passenger passenger) {
        synchronized (this) {
            for (Passenger p : passengers) {
                if (p.equals(passenger)) {
                    passengers.remove(passenger);
                    drawFloorInPassenger(passengers.size());
                    return this;
                }
            }
        }
        floorInfo();
        return passenger.getDestination();
    }

    public List<Passenger> getPassengers() {
        synchronized (this) {
            return passengers;
        }
    }

    public int getSource_pass_count() {
        return source_pass_count;
    }

    public int getDestination_pass_count() {
        return destination_pass_count;
    }

    public void incDestination_pass_count() {
        destination_pass_count++;
    }

    @Override
    public void outprintln(String message) {
        System.out.println(message);
    }

    @Override
    public void outprintf(String format, Object... args) {
        System.out.printf(format, args);
    }
}
