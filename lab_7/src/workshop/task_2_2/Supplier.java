package workshop.task_2_2;

public class Supplier {
    private final String name;
    private final String[] itemNames;

    public Supplier(String name, String[] itemNames) {
        this.name = name;
        this.itemNames = itemNames.clone();
    }
    public String getName() { return name; }
    public String[] getItemNames() { return itemNames.clone(); }
}
