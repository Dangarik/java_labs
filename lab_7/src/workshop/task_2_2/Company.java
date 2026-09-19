package workshop.task_2_2;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Company {
    private final String name;
    private final List<Customer> customers = new ArrayList<>();
    private Supplier[] suppliers = new Supplier[0];

    public Company(String name) { this.name = name; }
    public String getName() { return name; }
    public void addCustomer(Customer customer) { customers.add(customer); }
    public List<Customer> getCustomers() { return customers; }
    public List<Order> getOrders() {
        return customers.stream()
                .flatMap(customer -> customer.getOrders().stream())
                .collect(Collectors.toList());
    }
    public Customer getMostRecentCustomer() {
        return customers.get(customers.size() - 1);
    }
    public Customer getCustomerNamed(String name) {
        return customers.stream()
                .filter(customer -> customer.getName().equals(name))
                .findFirst().orElse(null);
    }
    public void addSupplier(Supplier supplier) {
        suppliers = Arrays.copyOf(suppliers, suppliers.length + 1);
        suppliers[suppliers.length - 1] = supplier;
    }
    public Supplier[] getSuppliers() { return suppliers.clone(); }
}
