package netclasses;

import fxclasses.FxFloor;
import javafx.scene.layout.AnchorPane;

public class NetFxFloor extends FxFloor {
    public NetFxFloor(int number, NetFxBuilding building, int width, int height, AnchorPane group, int floorcount, int elevcount) {
        super(number, building, width, height, group, floorcount, elevcount);

    }

    @Override
    public void floorInfo() {
        super.floorInfo();
        building.outprintf("#stt#:floor:" + String.valueOf(getNumber()) + ":" + String.valueOf(getOutpassengers().size()));
    }

    @Override
    public void outprintln(String message) {
        building.outprintln(message);
    }

    @Override
    public void outprintf(String format, Object... args) {
        building.outprintf(format, args);
    }

}
