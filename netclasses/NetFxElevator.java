package netclasses;

import classes.Building;
import classes.Floor;
import fxclasses.FxElevator;
import fxclasses.search.SearchEngine;
import javafx.scene.layout.AnchorPane;

public class NetFxElevator extends FxElevator {
    public NetFxElevator(int number, Floor position, int id, int capacity, Building b, int width, int height, AnchorPane group, int floorcount, int elevcount, SearchEngine search) {
        super(number, position, id, capacity, b, width, height, group, floorcount, elevcount, search);
    }

    @Override
    public void outprintln(String message) {
        b.outprintln(message);
    }

    @Override
    public void outprintf(String format, Object... args) {
        b.outprintf(format, args);
    }

    @Override
    public void elevInfo() {
        super.elevInfo();
        b.outprintf("#stt#:elevt:" + String.valueOf(getElevatorId()) + ":" + String.valueOf(getPassengers().size()));
    }
}
