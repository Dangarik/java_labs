package workshop.task_2_2;
import java.util.ArrayList;
import java.util.List;

public class Customer {
    private final String name;
    private final String city;
    private final List<Order> orders = new ArrayList<>();

    public Customer(String name, String city) {
        this.name = name;
        this.city = city;
    }
    public String getName() { return name; }
    public String getCity() { return city; }
    public void addOrder(Order order) { orders.add(order); }
    public List<Order> getOrders() { return orders; }
    public double getTotalOrderValue() {
        return orders.stream().mapToDouble(Order::getValue).sum();
    }
    public double getMostExpensiveItemValue() {
        return orders.stream().mapToDouble(Order::getMostExpensiveItemValue).max().orElse(0);
    }
}
