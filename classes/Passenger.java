package classes;

public final class Passenger {
    private final Floor source;
    private final Floor destination;
    private final long id;

    private final long createTime;
    private long inTime;
    private long outTime;

    private final String firstname;
    private final String secondname;

    public Passenger(Floor source, Floor destination, long id, long createTime, String firstname, String secondname) {
        this.source = source;
        this.destination = destination;
        this.id = id;
        this.createTime = createTime;
        this.firstname = firstname;
        this.secondname = secondname;
    }

    @Override
    public boolean equals(Object o) {
        if(!(o instanceof Passenger)) {
            return false;
        }
        Passenger p = (Passenger)o;
        return (p.id==this.id);
    }

    @Override
    public String toString() {
        return "{id: " + String.valueOf(this.id) + ", source=" + this.getSource().getNumber() + ", destination=" +
                this.getDestination().getNumber() + "}";
    }

    @Override
    public Passenger clone() {
        Passenger p = new Passenger(this.source, this.destination, this.id, this.createTime, this.firstname, this.secondname);
        p.setInTime(this.inTime);
        p.setOutTime(this.outTime);
        return p;
    }

    public void setInTime(long inTime) {
        this.inTime = inTime;
    }

    public void setOutTime(long outTime) {
        this.outTime = outTime;
    }

    public long getCreateTime() {
        return createTime;
    }

    public long getInTime() {
        return inTime;
    }

    public long getOutTime() {
        return outTime;
    }

    public long getInOutTime() {
        return outTime - inTime;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getSecondname() {
        return secondname;
    }

    public Floor getSource() {
        return source;
    }

    public Floor getDestination() {
        return destination;
    }

    public long getId() {
        return id;
    }
}
