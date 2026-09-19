import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ForkJoinPool;

public class Main {
    public static void main(String[] args) {
        int[] numbers = new int[1_000_000];
        Random random = new Random();
        for (int i = 0; i < numbers.length; i++) {
            numbers[i] = random.nextInt(101);
        }

        System.out.println("Array size: " + numbers.length);
        System.out.println("Value range: 0..100");
        System.out.println("First 20 values: "
                + Arrays.toString(Arrays.copyOf(numbers, 20)));
        System.out.println("Direct sum when part size < 20");

        ForkJoinPool pool = new ForkJoinPool();
        long parallelSum;
        long parallelTime;
        try {
            long startTime = System.nanoTime();
            parallelSum = pool.invoke(new SumTask(numbers, 0, numbers.length));
            parallelTime = System.nanoTime() - startTime;
        } finally {
            pool.shutdown();
        }

        long startTime = System.nanoTime();
        long sequentialSum = 0;
        for (int number : numbers) {
            sequentialSum += number;
        }
        long sequentialTime = System.nanoTime() - startTime;

        System.out.println("ForkJoin sum: " + parallelSum);
        System.out.println("Sequential sum: " + sequentialSum);
        System.out.println("Results match: " + (parallelSum == sequentialSum));
        System.out.printf("ForkJoin time: %.3f ms%n", parallelTime / 1_000_000.0);
        System.out.printf("Sequential time: %.3f ms%n", sequentialTime / 1_000_000.0);
    }
}
