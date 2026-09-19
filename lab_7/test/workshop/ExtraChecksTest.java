package workshop;

import org.junit.Test;
import static org.junit.Assert.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import workshop.task_1_2.Factorial;
import workshop.task_2_1.Developer;
import workshop.task_2_1.DeveloperService;
import workshop.task_3_1.*;

public class ExtraChecksTest {
    @Test public void zeroFactorial() { assertEquals(1, Factorial.factorial(0)); }
    @Test(expected = IllegalArgumentException.class)
    public void negativeFactorial() { Factorial.factorial(-1); }
    @Test(expected = IllegalArgumentException.class)
    public void factorialOverflow() { Factorial.factorial(21); }
    @Test public void duplicateLanguages() {
        Developer first = new Developer("First");
        Developer second = new Developer("Second");
        first.add("java"); second.add("java");
        assertEquals(Arrays.asList("java"), DeveloperService.getLanguages(Arrays.asList(first, second)));
    }
    @Test public void emptyFutureInput() {
        assertTrue(BestPriceFinder.findPricesFuture(Collections.emptyList(), "phone").isEmpty());
        assertTrue(BestPriceFinder.findPricesFutureAsync(Collections.emptyList(), "phone").isEmpty());
    }
    @Test public void allStrategiesCalculateSamePrice() {
        // Deterministic shops avoid depending on random numbers or timing.
        List<Shop> shops = Arrays.asList(new Shop("FixedShop") {
            @Override public double getPrice(String product) { return 100; }
        });
        check(BestPriceFinder.findPricesSequential(shops, "phone"));
        check(BestPriceFinder.findPricesParallel(shops, "phone"));
        check(BestPriceFinder.findPricesFuture(shops, "phone"));
        check(BestPriceFinder.findPricesFutureAsync(shops, "phone"));
    }
    private void check(List<ProductPrice> prices) {
        assertEquals(1, prices.size());
        assertEquals("FixedShop", prices.get(0).getShopName());
        // Formula in the original workshop: 100 - 8 + 5 = 97.
        assertEquals(97, prices.get(0).getFinalPrice(), 0.000001);
    }
}
