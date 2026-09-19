package workshop.task_1_2;

import java.util.stream.LongStream;

public class Factorial {


    public static long factorial(long i) {
        if (i < 0 || i > 20) {
            throw new IllegalArgumentException("Factorial requires a number from 0 to 20");
        }
        return LongStream.rangeClosed(1, i)
                .reduce(1, (result, number) -> result * number);

    }
}
