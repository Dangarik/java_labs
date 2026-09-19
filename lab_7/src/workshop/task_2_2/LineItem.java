package workshop.task_2_2;

public class LineItem {
    private String name;
    private final double value;

    public LineItem(String name, double value) {
        this.name = name;
        this.value = value;
    }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public double getValue() { return value; }
    @Override public String toString() { return name + " $ " + value; }
}
