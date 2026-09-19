package workshop.task_3_1;
import java.util.Random;

public class Shop {
    private final String name;
    private final Random random;

    public Shop(String name) {
        this.name = name;
        // Same seed allows comparison with the workshop examples.
        random = new Random(name.charAt(0) * name.charAt(1) * name.charAt(2));
    }
    public String getName() { return name; }
    public double getPrice(String product) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Price request was interrupted", exception);
        }
        return random.nextDouble() * product.charAt(0) + product.charAt(1);
    }
}
