package workshop.task_2_2;
import java.util.ArrayList;
import java.util.List;

public class Order {
    private static int nextOrderNumber = 1;
    private final int number = nextOrderNumber++;
    private final List<LineItem> lineItems = new ArrayList<>();
    private boolean delivered;

    public static void resetNextOrderNumber() { nextOrderNumber = 1; }
    public void deliver() { delivered = true; }
    public boolean isDelivered() { return delivered; }
    public void addLineItem(LineItem item) { lineItems.add(item); }
    public List<LineItem> getLineItems() { return lineItems; }
    public double getValue() {
        return lineItems.stream().mapToDouble(LineItem::getValue).sum();
    }
    public double getMostExpensiveItemValue() {
        return lineItems.stream().mapToDouble(LineItem::getValue).max().orElse(0);
    }
    @Override public String toString() {
        return "order " + number + " items: " + lineItems.size();
    }
}
