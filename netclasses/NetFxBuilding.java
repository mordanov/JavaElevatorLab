package netclasses;

import fxclasses.FxBuilding;
import fxclasses.search.SearchEngine;
import javafx.scene.layout.AnchorPane;
import netclasses.server.NetServer;

import java.io.IOException;

public class NetFxBuilding extends FxBuilding {

    private NetServer netserver;

    public NetFxBuilding(int floorcount, int passcount, int elevcount, int capacity, int source, int destination,
                         int width, int height, AnchorPane passPane, AnchorPane statPane, SearchEngine search) {
        super(floorcount, passcount, elevcount, capacity, source, destination, width, height, passPane, statPane, search);
        try {
            netserver = new NetServer(18943);
            new Thread(netserver).start();
        }
        catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    @Override
    public NetFxElevator elevatorGenerator(int number) {
        return new NetFxElevator(number, floors.get(0), ++elevid, capacity,this, this.width, this.height, this.passPane, this.floorcount, this.elevcount, search);
    }

    @Override
    public NetFxFloor floorGenerator(int number) {
        return new NetFxFloor(number, this, this.width, this.height, this.passPane, this.floorcount, this.elevcount);
    }

    @Override
    public void stopService() {
        super.stopService();
        netserver.stop();
    }

    @Override
    public void outprintln(String message) {
        super.outprintln(message);
        netserver.sendMessage(message);
    }

    @Override
    public void outprintf(String format, Object... args) {
        super.outprintf(format, args);
        netserver.sendMessage(String.format(format, args));
    }

}
