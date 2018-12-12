package fxclasses;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;

// класс для отображения в таблице fx
public class FxPerson {
    private final SimpleLongProperty id;
    private final SimpleStringProperty firstname;
    private final SimpleStringProperty secondname;
    private final SimpleIntegerProperty source;
    private final SimpleIntegerProperty destination;
    private final SimpleLongProperty generateTime;

    private SimpleLongProperty inTime;
    private SimpleLongProperty outTime;
    private SimpleLongProperty inoutTime;

    public FxPerson(long id, String firstname, String secondname, int source, int destination, long generateTime,
                    long inTime, long outTime, long inoutTime) {
        this.id = new SimpleLongProperty(id);
        this.firstname = new SimpleStringProperty(firstname);
        this.secondname = new SimpleStringProperty(secondname);
        this.source = new SimpleIntegerProperty(source);
        this.destination = new SimpleIntegerProperty(destination);
        this.generateTime = new SimpleLongProperty(generateTime);
        this.inTime = new SimpleLongProperty(inTime);
        this.outTime = new SimpleLongProperty(outTime);
        this.inoutTime = new SimpleLongProperty(inoutTime);
    }

    public FxPerson setInTime(long inTime) {
        this.inTime = new SimpleLongProperty(inTime);
        return this;
    }

    public void setOutTime(long outTime) {
        this.outTime = new SimpleLongProperty(outTime);
    }

    public void setInoutTime(long inoutTime) {
        this.inoutTime = new SimpleLongProperty(inoutTime);
    }

    public long getId() {
        return id.getValue();
    }

    public String getFirstname() { return firstname.getValue(); }

    public String getSecondname() {return secondname.getValue(); }

    public int getSource() {return source.getValue(); }

    public int getDestination() {return destination.getValue(); }

    public Double getGenerateTime() {return generateTime.getValue()/1000.0; }

    public Double getInTime() {return inTime.getValue()/1000.0; }

    public Double getOutTime() {return outTime.getValue()/1000.0; }

    public Double getInoutTime() {
        return inoutTime.getValue()/1000.0;
    }

}
