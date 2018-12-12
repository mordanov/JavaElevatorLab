package classes.elements;

// класс для стандартного вывода
public abstract class Outputter {

    public abstract void outprintln(String message);

    public abstract void outprintf(String format, Object ... args);
}
