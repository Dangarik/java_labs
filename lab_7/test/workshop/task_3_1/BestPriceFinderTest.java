package workshop.task_3_1;

import org.junit.Test;
import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

// Test cases from the assigned sirotae/lambdaworkshop repository.
public class BestPriceFinderTest {

    private final List<Shop> shops = Arrays.asList(new Shop("BestPrice"),
            new Shop("LetsSaveBig"),
            new Shop("MyFavoriteShop"),
            new Shop("BuyItAll"),
            new Shop("ShopEasy"));

    @Test
    public void findPricesSequential() {
        long start = System.nanoTime();
        List<ProductPrice> res = BestPriceFinder.findPricesSequential(shops, "myPhone27S");
        long duration = (System.nanoTime() - start) / 1_000_000;
        System.out.println("Sequential done in " + duration + " msecs");
        res.forEach(System.out::println);
        assertEquals(shops.size(), res.size());
        for (int i = 0; i < shops.size(); i++) {
            assertEquals(shops.get(i).getName(), res.get(i).getShopName());
            assertTrue(Double.isFinite(res.get(i).getFinalPrice()));
            assertTrue(res.get(i).getFinalPrice() > 0);
        }
    }

    @Test
    public void findPricesParallel() {
        long start = System.nanoTime();
        List<ProductPrice> res = BestPriceFinder.findPricesParallel(shops, "myPhone27S");
        long duration = (System.nanoTime() - start) / 1_000_000;
        System.out.println("Parallel done in " + duration + " msecs");
        res.forEach(System.out::println);
        assertEquals(shops.size(), res.size());
        for (int i = 0; i < shops.size(); i++) {
            assertEquals(shops.get(i).getName(), res.get(i).getShopName());
            assertTrue(Double.isFinite(res.get(i).getFinalPrice()));
            assertTrue(res.get(i).getFinalPrice() > 0);
        }
    }

    @Test
    public void findPricesFuture() {
        long start = System.nanoTime();
        List<ProductPrice> res = BestPriceFinder.findPricesFuture(shops, "myPhone27S");
        long duration = (System.nanoTime() - start) / 1_000_000;
        System.out.println("Future done in " + duration + " msecs");
        res.forEach(System.out::println);
        assertEquals(shops.size(), res.size());
        for (int i = 0; i < shops.size(); i++) {
            assertEquals(shops.get(i).getName(), res.get(i).getShopName());
            assertTrue(Double.isFinite(res.get(i).getFinalPrice()));
            assertTrue(res.get(i).getFinalPrice() > 0);
        }
    }

    @Test
    public void findPricesFutureAsync() {
       long start = System.nanoTime();
       List<ProductPrice> res = BestPriceFinder.findPricesFutureAsync(shops, "myPhone27S");
        long duration = (System.nanoTime() - start) / 1_000_000;
       System.out.println("Future done in " + duration + " msecs");
       res.forEach(System.out::println);
        assertEquals(shops.size(), res.size());
        for (int i = 0; i < shops.size(); i++) {
            assertEquals(shops.get(i).getName(), res.get(i).getShopName());
            assertTrue(Double.isFinite(res.get(i).getFinalPrice()));
            assertTrue(res.get(i).getFinalPrice() > 0);
        }
   }
}
