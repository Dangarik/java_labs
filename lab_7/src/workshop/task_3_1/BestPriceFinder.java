package workshop.task_3_1;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class BestPriceFinder {
    private static ProductPrice getPrice(Shop shop, String product) {
        return new ProductPrice(shop.getName(), shop.getPrice(product), 0.08, 0.05);
    }

    private static ProductPrice calculateFinal(ProductPrice price) {
        price.calculateFinalPrice();
        return price;
    }

    public static List<ProductPrice> findPricesSequential(List<Shop> shops, String product) {
        return shops.stream()
                .map(shop -> getPrice(shop, product))
                .map(BestPriceFinder::calculateFinal)
                .collect(Collectors.toList());
    }

    public static List<ProductPrice> findPricesParallel(List<Shop> shops, String product) {
        return shops.parallelStream()
                .map(shop -> getPrice(shop, product))
                .map(BestPriceFinder::calculateFinal)
                .collect(Collectors.toList());
    }

    public static List<ProductPrice> findPricesFuture(List<Shop> shops, String product) {
        if (shops.isEmpty()) {
            return Collections.emptyList();
        }
        ExecutorService executor = Executors.newFixedThreadPool(shops.size());
        try {
            List<CompletableFuture<ProductPrice>> futures = shops.stream()
                    .map(shop -> CompletableFuture
                            .supplyAsync(() -> getPrice(shop, product), executor)
                            .thenApply(BestPriceFinder::calculateFinal))
                    .collect(Collectors.toList());
            return futures.stream()
                    .map(CompletableFuture::join)
                    .collect(Collectors.toList());
        } finally {
            executor.shutdown();
        }
    }

    public static List<ProductPrice> findPricesFutureAsync(List<Shop> shops, String product) {
        if (shops.isEmpty()) {
            return Collections.emptyList();
        }
        ExecutorService executorPrice = Executors.newFixedThreadPool(shops.size());
        ExecutorService executorFinal = Executors.newFixedThreadPool(shops.size());
        try {
            List<CompletableFuture<ProductPrice>> futures = shops.stream()
                    .map(shop -> CompletableFuture
                            .supplyAsync(() -> getPrice(shop, product), executorPrice)
                            .thenApplyAsync(BestPriceFinder::calculateFinal, executorFinal))
                    .collect(Collectors.toList());
            return futures.stream()
                    .map(CompletableFuture::join)
                    .collect(Collectors.toList());
        } finally {
            executorPrice.shutdown();
            executorFinal.shutdown();
        }
    }
}
