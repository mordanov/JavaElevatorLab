package classes;

import classes.elements.*;
import sql.NameGenerator;
import java.util.Calendar;

import java.util.*;

public class Building extends Outputter implements Runnable {
    protected List<Floor> floors = new ArrayList<>();           // этажи
    protected List<Elevator> elevators = new ArrayList<>();     // лифты
    protected final int passcount;                              // сколько создать пассажиров в здании
    protected final int capacity;                               // вместимость лифтов
    private final int source;                                   // на каком этаже создаются пассажиры (0 - на случайном)
    private final int destination;                               // на каком этаже выгружаются пассажиры (0 - на случайном)
    protected long passid = 1000;                               // id пассажиров
    protected int elevid = 200;                                 // id лифтов

    private Thread t;
    protected int floorcount;
    protected int elevcount;
    private boolean passGeneratorActive = false;

    private final long buildingCreateTime = Calendar.getInstance().getTimeInMillis();

    private NameGenerator names;

    public boolean isPassGeneratorActive() {
        return passGeneratorActive;
    }

    public Building(int floorcount, int passcount, int elevcount, int capacity, int source, int destination) {
        this.floorcount = floorcount;
        this.elevcount = elevcount;
        this.passcount = passcount;
        this.capacity = capacity;
        this.source = source;
        this.destination = destination;
        names = new NameGenerator(elevcount, floorcount, passcount, capacity);
    }

    public void start() {
        for(int i=0;i<floorcount;i++) {
            addFloor(i + 1);
            outprintf("Добавлен новый этаж: %d\n", i+1);
        }
        t = new Thread(this, "Здание с лифтами");
        for(int i=0;i<elevcount;i++)
            addElevator(i + 1);
        t.start();
    }

    protected Floor floorGenerator(int number) {
        return new Floor(number, this);
    }

    public Building addFloor(int number) {
        floors.add(floorGenerator(number));
        return this;
    }

    public int getFloorcount() {
        return floorcount;
    }

    public int getElevcount() {
        return elevcount;
    }

    protected Elevator elevatorGenerator(int number) {
        return new Elevator(number, floors.get(0), ++elevid, capacity,this);
    }

    public Building addElevator(int number) {
        elevators.add(elevatorGenerator(number));
        outprintf("Появился новый лифт %d! Всего лифтов: %d\n", elevid, elevators.size());
        return this;
    }

    private int getRandomFloor() {
        return (int)(Math.random()*floors.size());
    }

    public Passenger generatePassenger() {
        Floor f0, f1;

        if(source==0) {
            f0 = floors.get(getRandomFloor()); // откуда едет
            if(destination==0) {
                f1 = floors.get(getRandomFloor()); // куда едет
                while (f1.equals(f0))                     // не едет ли на этаж, с которого стартовал
                    f1 = floors.get(getRandomFloor());
            }
            else {
                f1 = floors.get(destination - 1);
                while(f1.equals(f0)) {
                    f0 = floors.get(getRandomFloor());
                }
            }
        }
        else {
            f0 = floors.get(source - 1);
            if(destination==0) {
                f1 = floors.get(getRandomFloor());        // куда едет
                while (f1.equals(f0))                     // не едет ли на этаж, с которого стартовал
                    f1 = floors.get(getRandomFloor());
            }
            else
                f1 = floors.get(destination - 1);
        }

        Passenger p = f0.addPassenger(f1, ++passid, names);
        drawPassenger(p);
        return p;
    }

    protected void drawPassenger(Passenger p) {};

    public int getPasscount() {
        int r=0;
        for(Floor f:floors) {
            r+=f.getPassengers().size();
        }
        return r;
    }

    public boolean checkActiveElevators(Elevator elevator) {
        boolean active = true;
        boolean allstopped = false;
        for(Elevator e: elevators) {
            active &= e.isActive();
            allstopped |= !e.isActive();
        }
        if(allstopped)
            stopService();
        return active;
    }

    protected void stopService() {};

    public void killService() {
        for(Elevator e: elevators) {
            e.setInActive();
        }
        passGeneratorActive = false;
        stopService();
    }

    public NameGenerator getNames() {
        return names;
    }

    @Override
    public void run() {
        Date d1 = new Date();
        long d2 = d1.getTime()+2000;

        while(d2>d1.getTime()) {
            d1 = new Date();
        }
        try {
            int i=0;
            passGeneratorActive = true;
            while((i<passcount) && passGeneratorActive) {
                generatePassenger();
                Thread.sleep(100);
                i++;
            }
        }
        catch (InterruptedException ex) {
            outprintln("Генератор пассажиров прерван");
        }
        finally {
            passGeneratorActive = false;
            names.close();
        }
        outprintln("Генератор пассажиров завершил работу");
    }

    // находит первый этаж, на котором есть пассажиры
    public Floor getFloorWithPassengers(Elevator elevator) {
        switch (elevator.getStrategyMoving()) {
            case MIN_STRATEGY:
                for(Floor f:floors) {
                    if(f.getPassengers().size()>0)
                        return f;
                }
                break;
            case MAX_STRATEGY:
                for(int i=floorcount-1;i>=0;i--) {
                    if(floors.get(i).getPassengers().size()>0)
                        return floors.get(i);
                }
                break;
            case RANDOM_STRATEGY:
                Random r = new Random();
                int next;
                int maxrandom = floorcount*2;
                do {
                    next = r.nextInt(floorcount);
                    maxrandom--;
                } while (floors.get(next).getPassengers().size()==0 && maxrandom>0);
                if(maxrandom>0)
                    return floors.get(next);
                else
                    return null;
        }
        return null;
    }

    @Override
    public void outprintln(String message) {
        System.out.println(message);
    }

    @Override
    public void outprintf(String format, Object... args) {
        if(!format.substring(0,1).equals("#"))
            System.out.printf(format, args);
    }

    public long getTimer() {
        return Calendar.getInstance().getTimeInMillis() - buildingCreateTime;
    }

}
